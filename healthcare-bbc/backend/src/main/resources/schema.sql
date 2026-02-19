-- H2 Database Schema for healthcare-bbc

-- 1. 用户表 (user_info)
CREATE TABLE IF NOT EXISTS user_info (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_id VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(32) NOT NULL,
    gender TINYINT,
    age INT,
    phone VARCHAR(16),
    email VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1
);

-- 2. 健康档案表 (health_record)
CREATE TABLE IF NOT EXISTS health_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    blood_type VARCHAR(8),
    medical_history CLOB,
    allergy_history CLOB,
    family_history CLOB,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 健康监测数据表 (health_monitor_data)
CREATE TABLE IF NOT EXISTS health_monitor_data (
    data_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    data_type VARCHAR(32) NOT NULL,
    data_value DECIMAL(10,2) NOT NULL,
    data_unit VARCHAR(16) NOT NULL,
    measure_time TIMESTAMP NOT NULL,
    device_type VARCHAR(32),
    device_id VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. 健康计划表 (health_plan)
CREATE TABLE IF NOT EXISTS health_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(32) NOT NULL,
    plan_content CLOB NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 保险产品表 (insurance_product)
CREATE TABLE IF NOT EXISTS insurance_product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(32) UNIQUE NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    product_type VARCHAR(32) NOT NULL,
    min_age INT NOT NULL,
    max_age INT NOT NULL,
    min_premium DECIMAL(12,2) NOT NULL,
    max_premium DECIMAL(12,2) NOT NULL,
    coverage_amount DECIMAL(15,2) NOT NULL,
    coverage_period VARCHAR(32) NOT NULL,
    payment_period VARCHAR(32) NOT NULL,
    health_requirements CLOB,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1
);

-- 6. 保单表 (insurance_policy)
CREATE TABLE IF NOT EXISTS insurance_policy (
    policy_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_no VARCHAR(32) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    premium_amount DECIMAL(12,2) NOT NULL,
    coverage_amount DECIMAL(15,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payment_period VARCHAR(32) NOT NULL,
    status TINYINT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 用户标签表 (user_tag)
CREATE TABLE IF NOT EXISTS user_tag (
    tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    tag_category VARCHAR(32),
    tag_name VARCHAR(64) NOT NULL,
    tag_source VARCHAR(32),
    confidence_score DECIMAL(3,2) DEFAULT 1.0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. 健康权益表 (health_benefit)
CREATE TABLE IF NOT EXISTS health_benefit (
    benefit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    benefit_name VARCHAR(128) NOT NULL,
    benefit_type VARCHAR(32),
    description CLOB,
    valid_period INT,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. 用户权益关联表 (user_benefit_rel)
CREATE TABLE IF NOT EXISTS user_benefit_rel (
    rel_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    benefit_id BIGINT NOT NULL,
    source_type VARCHAR(32),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status TINYINT DEFAULT 1,
    use_time TIMESTAMP
);

-- 10. 客服消息表 (customer_message)
CREATE TABLE IF NOT EXISTS customer_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_external_user_id VARCHAR(64) NOT NULL,
    from_user_id VARCHAR(64),
    to_user_id VARCHAR(64),
    message_type VARCHAR(32) NOT NULL,
    content CLOB,
    media_url VARCHAR(512),
    direction TINYINT NOT NULL,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. 客服会话表 (customer_service_session)
CREATE TABLE IF NOT EXISTS customer_service_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_external_user_id VARCHAR(64) NOT NULL,
    agent_id BIGINT,
    agent_name VARCHAR(32),
    status TINYINT DEFAULT 1,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. AI意图表 (ai_intent)
CREATE TABLE IF NOT EXISTS ai_intent (
    intent_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    intent_code VARCHAR(32) UNIQUE NOT NULL,
    intent_name VARCHAR(64) NOT NULL,
    intent_category VARCHAR(32),
    target_role VARCHAR(32),
    keywords CLOB,
    confidence_threshold DECIMAL(3,2) DEFAULT 0.7,
    priority INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 初始化AI意图数据
INSERT INTO ai_intent (intent_code, intent_name, intent_category, target_role, keywords, priority) VALUES
('HEALTH_CONSULT', '健康咨询', 'HEALTH', 'DOCTOR', '血压,血糖,症状,疾病,医生,看病,不舒服,疼痛', 10),
('INSURANCE_CONSULT', '保险咨询', 'INSURANCE', 'ADVISOR', '保险,保单,理赔,保障,投保,保费,保额', 10),
('BENEFIT_CONSULT', '权益咨询', 'BENEFIT', 'MANAGER', '权益,体检,绿通,问诊,服务,优惠', 10),
('URGENT_MEDICAL', '紧急医疗', 'HEALTH', 'DOCTOR', '紧急,急救,严重,危险,马上,立刻', 20),
('COMPLAINT', '投诉建议', 'GENERAL', 'MANAGER', '投诉,不满,建议,反馈,问题', 15),
('GENERAL_CHAT', '通用对话', 'GENERAL', 'AI', '你好,您好,谢谢,再见,在吗', 5);

-- 初始化测试用户
INSERT INTO user_info (wechat_id, name, gender, age, phone, status) VALUES
('test123', '测试用户', 1, 30, '13800138000', 1),
('wmABC123', '张三', 1, 35, '13900139000', 1);

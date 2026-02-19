/*
 * 企微健康社群方案 - 数据库初始化脚本
 * 版本: V1.0
 * 日期: 2026-02-03
 */

CREATE DATABASE IF NOT EXISTS healthcare_bbc DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE healthcare_bbc;

-- 1. 用户表 (user_info)
CREATE TABLE IF NOT EXISTS user_info (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_id VARCHAR(64) UNIQUE NOT NULL COMMENT '企业微信外部联系人ID',
    name VARCHAR(32) NOT NULL COMMENT '用户姓名',
    gender TINYINT COMMENT '性别 1:男 2:女 0:未知',
    age INT COMMENT '年龄',
    phone VARCHAR(16) COMMENT '联系电话',
    email VARCHAR(64) COMMENT '电子邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1 COMMENT '1:正常 0:禁用',
    INDEX idx_wechat_id(wechat_id),
    INDEX idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 健康档案表 (health_record)
CREATE TABLE IF NOT EXISTS health_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    height DECIMAL(5,2) COMMENT '身高(cm)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    blood_type VARCHAR(8) COMMENT '血型',
    medical_history TEXT COMMENT '既往病史',
    allergy_history TEXT COMMENT '过敏史',
    family_history TEXT COMMENT '家族病史',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    INDEX idx_user_id(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

-- 3. 健康监测数据表 (health_monitor_data)
CREATE TABLE IF NOT EXISTS health_monitor_data (
    data_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    data_type VARCHAR(32) NOT NULL COMMENT '数据类型: 血压、血糖、心率等',
    data_value DECIMAL(10,2) NOT NULL COMMENT '数值',
    data_unit VARCHAR(16) NOT NULL COMMENT '单位',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    device_type VARCHAR(32) COMMENT '设备类型',
    device_id VARCHAR(64) COMMENT '设备ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    INDEX idx_user_id(user_id),
    INDEX idx_data_type(data_type),
    INDEX idx_measure_time(measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康监测数据表';

-- 4. 健康计划表 (health_plan)
CREATE TABLE IF NOT EXISTS health_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(32) NOT NULL COMMENT '计划类型: 运动、饮食、慢病管理等',
    plan_content TEXT NOT NULL COMMENT '计划内容',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '1:执行中 2:已完成 3:已暂停',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    INDEX idx_user_id(user_id),
    INDEX idx_plan_type(plan_type),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康计划表';

-- 5. 保险产品表 (insurance_product)
CREATE TABLE IF NOT EXISTS insurance_product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(32) UNIQUE NOT NULL COMMENT '产品编码',
    product_name VARCHAR(128) NOT NULL COMMENT '产品名称',
    product_type VARCHAR(32) NOT NULL COMMENT '产品类型: 重疾、医疗、意外、寿险等',
    min_age INT NOT NULL COMMENT '最小投保年龄',
    max_age INT NOT NULL COMMENT '最大投保年龄',
    min_premium DECIMAL(12,2) NOT NULL COMMENT '最低保费',
    max_premium DECIMAL(12,2) NOT NULL COMMENT '最高保费',
    coverage_amount DECIMAL(15,2) NOT NULL COMMENT '保额',
    coverage_period VARCHAR(32) NOT NULL COMMENT '保障期限',
    payment_period VARCHAR(32) NOT NULL COMMENT '缴费期限',
    health_requirements TEXT COMMENT '健康告知要求',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1 COMMENT '1:在售 0:停售',
    INDEX idx_product_type(product_type),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保险产品表';

-- 6. 保单表 (insurance_policy)
CREATE TABLE IF NOT EXISTS insurance_policy (
    policy_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_no VARCHAR(32) UNIQUE NOT NULL COMMENT '保单号',
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    premium_amount DECIMAL(12,2) NOT NULL COMMENT '保费金额',
    coverage_amount DECIMAL(15,2) NOT NULL COMMENT '保额',
    start_date DATE NOT NULL COMMENT '生效日期',
    end_date DATE NOT NULL COMMENT '失效日期',
    payment_period VARCHAR(32) NOT NULL COMMENT '缴费期限',
    status TINYINT NOT NULL COMMENT '1:有效 2:已到期 3:已退保 4:已理赔',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (product_id) REFERENCES insurance_product(product_id),
    INDEX idx_user_id(user_id),
    INDEX idx_policy_no(policy_no),
    INDEX idx_status(status),
    INDEX idx_end_date(end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保单表';

-- 7. 用户标签表 (user_tag)
CREATE TABLE IF NOT EXISTS user_tag (
    tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    tag_category VARCHAR(32) COMMENT '标签分类: 健康状况、风险等级、服务偏好、保险需求',
    tag_name VARCHAR(64) NOT NULL COMMENT '标签名称',
    tag_source VARCHAR(32) COMMENT '来源: AI提取、医生标注、用户自选',
    confidence_score DECIMAL(3,2) DEFAULT 1.0 COMMENT '置信度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    INDEX idx_user_id(user_id),
    INDEX idx_category_name(tag_category, tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户立体标签表';

-- 8. 健康权益表 (health_benefit)
CREATE TABLE IF NOT EXISTS health_benefit (
    benefit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    benefit_name VARCHAR(128) NOT NULL COMMENT '权益名称',
    benefit_type VARCHAR(32) COMMENT '权益类型: 体检、绿通、购药优惠、专家问诊',
    description TEXT COMMENT '权益描述',
    valid_period INT COMMENT '有效期（天）',
    status TINYINT DEFAULT 1 COMMENT '1:启用 0:停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康权益定义表';

-- 9. 用户权益关联表 (user_benefit_rel)
CREATE TABLE IF NOT EXISTS user_benefit_rel (
    rel_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    benefit_id BIGINT NOT NULL,
    source_type VARCHAR(32) COMMENT '获取来源: 保单赠送、积分兑换、活动领取',
    start_time DATETIME COMMENT '生效时间',
    end_time DATETIME COMMENT '失效时间',
    status TINYINT DEFAULT 1 COMMENT '1:未使用 2:已使用 3:已过期',
    use_time DATETIME COMMENT '使用时间',
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (benefit_id) REFERENCES health_benefit(benefit_id),
    INDEX idx_user_id(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权益记录表';

-- 10. 客服消息表 (customer_message)
CREATE TABLE IF NOT EXISTS customer_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_external_user_id VARCHAR(64) NOT NULL COMMENT '企业微信外部联系人ID',
    from_user_id VARCHAR(64) COMMENT '发送者ID',
    to_user_id VARCHAR(64) COMMENT '接收者ID',
    message_type VARCHAR(32) NOT NULL COMMENT '消息类型: text, image, voice',
    content TEXT COMMENT '消息内容',
    media_url VARCHAR(512) COMMENT '媒体文件URL',
    direction TINYINT NOT NULL COMMENT '1:接收 2:发送',
    status TINYINT DEFAULT 1 COMMENT '1:正常 2:已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_external_user_id(wechat_external_user_id),
    INDEX idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服消息表';

-- 11. 客服会话表 (customer_service_session)
CREATE TABLE IF NOT EXISTS customer_service_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wechat_external_user_id VARCHAR(64) NOT NULL COMMENT '企业微信外部联系人ID',
    agent_id BIGINT COMMENT '客服ID',
    agent_name VARCHAR(32) COMMENT '客服名称',
    status TINYINT DEFAULT 1 COMMENT '1:进行中 2:已结束',
    start_time DATETIME COMMENT '会话开始时间',
    end_time DATETIME COMMENT '会话结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_external_user_id(wechat_external_user_id),
    INDEX idx_agent_id(agent_id),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服会话表';

-- 12. AI意图表 (ai_intent)
CREATE TABLE IF NOT EXISTS ai_intent (
    intent_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    intent_code VARCHAR(32) UNIQUE NOT NULL COMMENT '意图编码',
    intent_name VARCHAR(64) NOT NULL COMMENT '意图名称',
    intent_category VARCHAR(32) COMMENT '意图分类: HEALTH, INSURANCE, BENEFIT, GENERAL',
    target_role VARCHAR(32) COMMENT '目标角色: DOCTOR, ADVISOR, MANAGER, AI',
    keywords TEXT COMMENT '关键词,逗号分隔',
    confidence_threshold DECIMAL(3,2) DEFAULT 0.7 COMMENT '置信度阈值',
    priority INT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 1 COMMENT '1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category(intent_category),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI意图表';

-- 初始化AI意图数据
INSERT INTO ai_intent (intent_code, intent_name, intent_category, target_role, keywords, priority) VALUES
('HEALTH_CONSULT', '健康咨询', 'HEALTH', 'DOCTOR', '血压,血糖,症状,疾病,医生,看病,不舒服,疼痛', 10),
('INSURANCE_CONSULT', '保险咨询', 'INSURANCE', 'ADVISOR', '保险,保单,理赔,保障,投保,保费,保额', 10),
('BENEFIT_CONSULT', '权益咨询', 'BENEFIT', 'MANAGER', '权益,体检,绿通,问诊,服务,优惠', 10),
('URGENT_MEDICAL', '紧急医疗', 'HEALTH', 'DOCTOR', '紧急,急救,严重,危险,马上,立刻', 20),
('COMPLAINT', '投诉建议', 'GENERAL', 'MANAGER', '投诉,不满,建议,反馈,问题', 15),
('GENERAL_CHAT', '通用对话', 'GENERAL', 'AI', '你好,您好,谢谢,再见,在吗', 5);

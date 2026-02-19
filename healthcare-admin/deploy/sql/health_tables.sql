-- 健康档案表
CREATE TABLE IF NOT EXISTS health_profile (
    profile_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档案ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    real_name VARCHAR(64) COMMENT '真实姓名',
    gender TINYINT COMMENT '性别: 0-未知 1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(16) COMMENT '手机号',
    emergency_contact VARCHAR(64) COMMENT '紧急联系人',
    emergency_phone VARCHAR(16) COMMENT '紧急联系电话',
    height DECIMAL(5,2) COMMENT '身高(cm)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    bmi DECIMAL(4,2) COMMENT 'BMI指数',
    blood_type VARCHAR(4) COMMENT '血型',
    allergies TEXT COMMENT '过敏史',
    medical_history TEXT COMMENT '既往病史',
    family_history TEXT COMMENT '家族病史',
    chronic_diseases TEXT COMMENT '慢性病',
    medications TEXT COMMENT '用药情况',
    health_score INT COMMENT '健康评分(0-100)',
    health_level VARCHAR(16) COMMENT '健康等级: low/medium/high',
    risk_tags VARCHAR(255) COMMENT '风险标签',
    doctor_id BIGINT COMMENT '家庭医生ID',
    health_manager_id BIGINT COMMENT '健康管家ID',
    benefit_advisor_id BIGINT COMMENT '权益顾问ID',
    insurance_advisor_id BIGINT COMMENT '保险顾问ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_user_id (user_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_health_level (health_level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

-- 健康计划表
CREATE TABLE IF NOT EXISTS health_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    profile_id BIGINT COMMENT '档案ID',
    plan_name VARCHAR(128) NOT NULL COMMENT '计划名称',
    plan_type TINYINT COMMENT '计划类型: 1-运动计划 2-饮食计划 3-慢病管理 4-康复计划',
    health_goals TEXT COMMENT '健康目标',
    interventions TEXT COMMENT '干预措施',
    diet_advice TEXT COMMENT '饮食建议',
    exercise_plan TEXT COMMENT '运动计划',
    medication_reminder TEXT COMMENT '用药提醒',
    visit_frequency INT COMMENT '回访频率(天/次)',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    risk_level TINYINT COMMENT '风险等级: 1-低 2-中 3-高',
    benefits TEXT COMMENT '匹配权益',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_approved TINYINT DEFAULT 0 COMMENT '医生审核: 0-未审核 1-已通过 2-已拒绝',
    approve_time DATETIME COMMENT '审核时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-进行中 2-已完成 3-已暂停',
    completion_rate INT DEFAULT 0 COMMENT '完成率(%)',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_user_id (user_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康计划表';

-- 健康数据记录表
CREATE TABLE IF NOT EXISTS health_data (
    data_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    profile_id BIGINT COMMENT '档案ID',
    data_type TINYINT COMMENT '数据类型: 1-血压 2-血糖 3-心率 4-体重 5-步数',
    data_value VARCHAR(64) COMMENT '数据值',
    data_unit VARCHAR(16) COMMENT '数据单位',
    measure_time DATETIME COMMENT '测量时间',
    device_type VARCHAR(64) COMMENT '设备类型',
    device_id VARCHAR(64) COMMENT '设备ID',
    remark VARCHAR(255) COMMENT '备注',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常: 0-正常 1-异常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_data_type (data_type),
    INDEX idx_measure_time (measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据记录表';

-- 关键词回复表
CREATE TABLE IF NOT EXISTS group_keyword_reply (
    reply_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
    group_id BIGINT NOT NULL COMMENT '社群ID',
    keyword VARCHAR(128) NOT NULL COMMENT '关键词',
    match_type TINYINT DEFAULT 2 COMMENT '匹配类型: 1-精确匹配 2-包含匹配 3-开头匹配 4-结尾匹配 5-正则匹配',
    reply_content TEXT NOT NULL COMMENT '回复内容',
    content_type TINYINT DEFAULT 1 COMMENT '内容类型: 1-文本 2-图片 3-链接 4-小程序',
    media_url VARCHAR(255) COMMENT '媒体URL',
    priority INT DEFAULT 0 COMMENT '优先级',
    start_time TIME COMMENT '生效开始时间',
    end_time TIME COMMENT '生效结束时间',
    trigger_limit INT DEFAULT 0 COMMENT '每人每天触发次数限制',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    INDEX idx_group_id (group_id),
    INDEX idx_keyword (keyword),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关键词回复表';

-- 服务记录表
CREATE TABLE IF NOT EXISTS service_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    group_id BIGINT COMMENT '社群ID',
    service_type TINYINT COMMENT '服务类型: 1-健康咨询 2-权益服务 3-保险咨询 4-回访',
    service_role TINYINT COMMENT '服务角色: 1-家庭医生 2-健康管家 3-权益顾问 4-保险顾问',
    staff_id BIGINT COMMENT '服务人员ID',
    staff_name VARCHAR(64) COMMENT '服务人员姓名',
    content TEXT COMMENT '服务内容',
    result TEXT COMMENT '服务结果',
    satisfaction INT COMMENT '满意度(1-5)',
    feedback TEXT COMMENT '用户反馈',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-已取消 1-进行中 2-已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_group_id (group_id),
    INDEX idx_staff_id (staff_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务记录表';

-- 回访任务表
CREATE TABLE IF NOT EXISTS visit_task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    plan_id BIGINT COMMENT '健康计划ID',
    task_type TINYINT COMMENT '任务类型: 1-定期回访 2-异常预警 3-权益提醒 4-活动通知',
    scheduled_time DATETIME COMMENT '计划执行时间',
    executed_time DATETIME COMMENT '实际执行时间',
    execute_method TINYINT COMMENT '执行方式: 1-企微消息 2-短信 3-电话',
    content TEXT COMMENT '任务内容',
    result TEXT COMMENT '执行结果',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待执行 1-执行中 2-已完成 3-已失败',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_scheduled_time (scheduled_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回访任务表';

-- 初始化健康相关数据
INSERT INTO sys_permission (parent_id, permission_code, permission_name, permission_type, path, component, icon, sort_order, status) VALUES
(0, 'health', '健康管理', 1, '/health', NULL, 'Heart', 2, 1);

SET @health_parent_id = LAST_INSERT_ID();

INSERT INTO sys_permission (parent_id, permission_code, permission_name, permission_type, path, component, icon, sort_order, status) VALUES
(@health_parent_id, 'health:profile', '健康档案', 2, '/health/profile', 'health/profile/index', 'Document', 1, 1),
(@health_parent_id, 'health:plan', '健康计划', 2, '/health/plan', 'health/plan/index', 'Calendar', 2, 1),
(@health_parent_id, 'health:data', '健康数据', 2, '/health/data', 'health/data/index', 'DataLine', 3, 1),
(@health_parent_id, 'health:service', '服务记录', 2, '/health/service', 'health/service/index', 'Service', 4, 1);

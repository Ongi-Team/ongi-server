CREATE TABLE member
(
    member_id  BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    login_id   VARCHAR(255)            NOT NULL,
    name       VARCHAR(255)            NOT NULL,
    password   VARCHAR(255)            NOT NULL,
    phone      VARCHAR(255)            NOT NULL,
    created_at DATETIME(6)             NOT NULL,
    updated_at DATETIME(6)             NOT NULL,
    fcm_token  VARCHAR(512)            NULL,
    os_type    ENUM ('IOS', 'ANDROID') NULL,
    deleted_at DATETIME(6)             NULL,
    CONSTRAINT UK_7r9uk13t9mp7py0w0jh3j05oh
        UNIQUE (fcm_token, os_type),
    CONSTRAINT UK_enfm5patwjqulw8k4wwuo6f60
        UNIQUE (login_id)
);

CREATE TABLE elder
(
    elder_id     BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    member_id    BIGINT                  NOT NULL,
    age          INT                     NOT NULL,
    name         VARCHAR(255)            NOT NULL,
    relationship VARCHAR(255)            NOT NULL,
    created_at   DATETIME(6)             NOT NULL,
    updated_at   DATETIME(6)             NOT NULL,
    fcm_token    VARCHAR(512)            NULL,
    os_type      ENUM ('IOS', 'ANDROID') NULL,
    phone        VARCHAR(255)            NULL,
    CONSTRAINT fcm_token_unique
        UNIQUE (os_type, fcm_token),
    CONSTRAINT fk_elder_member
        FOREIGN KEY (member_id) REFERENCES member (member_id)
            ON DELETE CASCADE
);

CREATE TABLE device
(
    device_id     BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at    DATETIME(6)     NOT NULL,
    updated_at    DATETIME(6)     NOT NULL,
    serial_number VARCHAR(255)    NOT NULL,
    elder_id      BIGINT          NOT NULL,
    status        ENUM ('ONLINE') NULL,
    uptime_sec    BIGINT          NULL,
    rssi          INT             NULL,
    last_seen_at  DATETIME(6)     NULL,
    device_token  VARCHAR(255)    NOT NULL,
    CONSTRAINT UK_cvkr0tbgtxuyuyjv6byef3dj0
        UNIQUE (device_token),
    CONSTRAINT device_pk
        UNIQUE (elder_id),
    CONSTRAINT device_pk_2
        UNIQUE (serial_number),
    CONSTRAINT FKksmyogk6rhkqu1l3b5ejnojll
        FOREIGN KEY (elder_id) REFERENCES elder (elder_id)
);

CREATE TABLE medicine
(
    medicine_id    BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    elder_id       BIGINT       NOT NULL,
    name           VARCHAR(255) NOT NULL,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    scheduled_time TIME(6)      NOT NULL,
    CONSTRAINT FK15fb53abaxyr2jf2tg72nthxq
        FOREIGN KEY (elder_id) REFERENCES elder (elder_id)
);

CREATE TABLE device_slot
(
    device_slot_id BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at     DATETIME(6)              NOT NULL,
    updated_at     DATETIME(6)              NOT NULL,
    slot_number    INT                      NOT NULL,
    device_id      BIGINT                   NOT NULL,
    elder_id       BIGINT                   NOT NULL,
    medicine_id    BIGINT                   NOT NULL,
    status         ENUM ('TAKEN', 'MISSED') NULL,
    CONSTRAINT FK7dhd2h5l4ie20s9pokbje4l4v
        FOREIGN KEY (elder_id) REFERENCES elder (elder_id),
    CONSTRAINT FK97rq4iig571nmvls7llm8ieik
        FOREIGN KEY (device_id) REFERENCES device (device_id),
    CONSTRAINT FK_device_slot_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicine (medicine_id)
);

CREATE TABLE medication_record
(
    medication_record_id BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    recorded_at          DATETIME(6)              NOT NULL,
    result               ENUM ('TAKEN', 'MISSED') NOT NULL,
    device_id            BIGINT                   NOT NULL,
    created_at           DATETIME(6)              NOT NULL,
    updated_at           DATETIME(6)              NOT NULL,
    medicine_id          BIGINT                   NOT NULL,
    CONSTRAINT uk_medicine_recorded_at
        UNIQUE (medicine_id, recorded_at),
    CONSTRAINT FK_medication_record_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicine (medicine_id),
    CONSTRAINT FKp2gt8s51ktxfbwehv1w2wlkrs
        FOREIGN KEY (device_id) REFERENCES device (device_id)
);

CREATE TABLE medicine_schedule
(
    medicine_schedule_id BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at           DATETIME(6) NOT NULL,
    updated_at           DATETIME(6) NOT NULL,
    scheduled_time       TIME(6)     NOT NULL,
    medicine_id          BIGINT      NOT NULL,
    CONSTRAINT FK41jx8gfobll9d63qsvuuvwnrn
        FOREIGN KEY (medicine_id) REFERENCES medicine (medicine_id)
);

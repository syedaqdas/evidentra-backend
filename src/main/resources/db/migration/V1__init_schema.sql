CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    role VARCHAR(40) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE case_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_number VARCHAR(60) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(40) NOT NULL,
    lead_officer_id UUID REFERENCES app_users(id),
    opened_at TIMESTAMPTZ NOT NULL,
    closed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE evidence_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evidence_number VARCHAR(80) NOT NULL UNIQUE,
    case_record_id UUID NOT NULL REFERENCES case_records(id),
    type VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    description TEXT,
    storage_location VARCHAR(180),
    file_name VARCHAR(255),
    content_type VARCHAR(120),
    sha256_hash VARCHAR(64),
    size_bytes BIGINT,
    collected_at TIMESTAMPTZ NOT NULL,
    collected_by_id UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT evidence_items_sha256_hash_length CHECK (sha256_hash IS NULL OR length(sha256_hash) = 64),
    CONSTRAINT evidence_items_size_bytes_positive CHECK (size_bytes IS NULL OR size_bytes >= 0)
);

CREATE TABLE chain_of_custody_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evidence_item_id UUID NOT NULL REFERENCES evidence_items(id),
    action VARCHAR(40) NOT NULL,
    from_custodian VARCHAR(160),
    to_custodian VARCHAR(160) NOT NULL,
    location VARCHAR(180),
    notes TEXT,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    recorded_by_id UUID REFERENCES app_users(id)
);

CREATE TABLE forensic_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evidence_item_id UUID NOT NULL REFERENCES evidence_items(id),
    assigned_to_id UUID REFERENCES app_users(id),
    task_name VARCHAR(180) NOT NULL,
    notes TEXT,
    status VARCHAR(40) NOT NULL,
    priority VARCHAR(40) NOT NULL,
    due_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_username VARCHAR(80) NOT NULL,
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(80) NOT NULL,
    resource_id VARCHAR(80),
    summary TEXT,
    source_ip VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_case_records_case_number ON case_records(case_number);
CREATE INDEX idx_evidence_items_case_record_id ON evidence_items(case_record_id);
CREATE INDEX idx_evidence_items_evidence_number ON evidence_items(evidence_number);
CREATE INDEX idx_chain_of_custody_evidence_item_id ON chain_of_custody_entries(evidence_item_id);
CREATE INDEX idx_forensic_tasks_evidence_item_id ON forensic_tasks(evidence_item_id);
CREATE INDEX idx_forensic_tasks_status ON forensic_tasks(status);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_logs_actor_created_at ON audit_logs(actor_username, created_at);

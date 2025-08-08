-- Helpdesk Tickets Table
CREATE TABLE IF NOT EXISTS helpdesk_tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    student_name VARCHAR(100) NOT NULL,
    student_email VARCHAR(100) NOT NULL,
    assigned_to VARCHAR(100),
    category ENUM('TECHNICAL', 'ACADEMIC', 'FINANCIAL', 'GENERAL') DEFAULT 'GENERAL'
);

-- Index for better performance
CREATE INDEX idx_ticket_status ON helpdesk_tickets(status);
CREATE INDEX idx_ticket_priority ON helpdesk_tickets(priority);
CREATE INDEX idx_ticket_category ON helpdesk_tickets(category);
CREATE INDEX idx_ticket_created_at ON helpdesk_tickets(created_at); 
-- ----------------------------------
-- STUDENTS
-- ----------------------------------
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    gr_number INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- ----------------------------------
-- PROFILES
-- ----------------------------------
CREATE TABLE profiles (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT UNIQUE,
    city VARCHAR(100),
    mobile_no VARCHAR(15),
    age INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ----------------------------------
-- COURSES
-- ----------------------------------

CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    no_of_semester INT NOT NULL,
    total_fee DECIMAL(10, 2) DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE
);


-- ----------------------------------
-- STUDENT_COURSES
-- ----------------------------------
CREATE TABLE student_courses (
    student_course_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    course_id INT,
    enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);


-- ----------------------------------
-- SUBJECTS
-- ----------------------------------
CREATE TABLE subjects (
    subject_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_name VARCHAR(100) NOT NULL,
    subject_type ENUM('mandatory', 'elective') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);


-- ----------------------------------
-- SUBJECT_COURSE
-- ----------------------------------
CREATE TABLE subject_course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    subject_id INT,
    course_id INT,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    UNIQUE(subject_id, course_id)
);


-- ----------------------------------
-- TEACHERS
-- ----------------------------------
CREATE TABLE teachers (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    qualification VARCHAR(100),
    experience DECIMAL(3,1) DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE
);

-- ----------------------------------
-- SUBJECT_TEACHERS
-- ----------------------------------
CREATE TABLE subject_teachers (
    subject_id INT,
    teacher_id INT,
    PRIMARY KEY (subject_id, teacher_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
);


-- ----------------------------------
-- STUDENT_SUBJECTS
-- ----------------------------------
CREATE TABLE student_subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_course_id INT,
    subject_course_id INT,
    FOREIGN KEY (student_course_id) REFERENCES student_courses(student_course_id),
    FOREIGN KEY (subject_course_id) REFERENCES subject_course(id)
);

-- ----------------------------------
-- FEES
-- ----------------------------------
CREATE TABLE fees (
    fee_id INT PRIMARY KEY AUTO_INCREMENT,
    student_course_id INT,
    paid_amount DECIMAL(10, 2) DEFAULT 0.0,
    pending_amount DECIMAL(10, 2),
    total_fee DECIMAL(10, 2),
    last_payment_date DATE,
    FOREIGN KEY (student_course_id) REFERENCES student_courses(student_course_id)
);


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

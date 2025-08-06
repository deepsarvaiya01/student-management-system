package com.sms.model;

import java.time.LocalDateTime;

public class HelpdeskTicket {
    private int ticketId;
    private String subject;
    private String description;
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String studentName;
    private String studentEmail;
    private String assignedTo;
    private String category; // TECHNICAL, ACADEMIC, FINANCIAL, GENERAL

    public HelpdeskTicket() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "OPEN";
        this.priority = "MEDIUM";
    }

    public HelpdeskTicket(String subject, String description, String studentName, String studentEmail, String category) {
        this();
        this.subject = subject;
        this.description = description;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.category = category;
    }

    // Getters and Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("| %-8d | %-20s | %-15s | %-12s | %-10s | %-20s | %-15s |",
            ticketId, 
            subject != null ? (subject.length() > 18 ? subject.substring(0, 15) + "..." : subject) : "--",
            studentName != null ? (studentName.length() > 13 ? studentName.substring(0, 10) + "..." : studentName) : "--",
            category != null ? category : "--",
            priority != null ? priority : "--",
            status != null ? status : "--",
            createdAt != null ? createdAt.toLocalDate().toString() : "--"
        );
    }

    public static void printHeader() {
        System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
        System.out.printf("| %-8s | %-20s | %-15s | %-12s | %-10s | %-20s | %-15s |%n", 
            "Ticket ID", "Subject", "Student Name", "Category", "Priority", "Status", "Created Date");
        System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
    }
} 
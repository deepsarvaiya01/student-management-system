package com.sms.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sms.database.DBConnection;
import com.sms.model.HelpdeskTicket;

public class HelpdeskDao {
    private final Connection connection;

    public HelpdeskDao() throws SQLException {
        this.connection = DBConnection.connect();
    }

    /**
     * Create a new helpdesk ticket
     */
    public boolean createTicket(HelpdeskTicket ticket) throws SQLException {
        String sql = "INSERT INTO helpdesk_tickets (subject, description, status, priority, created_at, updated_at, student_name, student_email, category) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getSubject());
            ps.setString(2, ticket.getDescription());
            ps.setString(3, ticket.getStatus());
            ps.setString(4, ticket.getPriority());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getCreatedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(ticket.getUpdatedAt()));
            ps.setString(7, ticket.getStudentName());
            ps.setString(8, ticket.getStudentEmail());
            ps.setString(9, ticket.getCategory());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticket.setTicketId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get all tickets
     */
    public List<HelpdeskTicket> getAllTickets() throws SQLException {
        List<HelpdeskTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM helpdesk_tickets ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HelpdeskTicket ticket = mapResultSetToTicket(rs);
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    /**
     * Get tickets by status
     */
    public List<HelpdeskTicket> getTicketsByStatus(String status) throws SQLException {
        List<HelpdeskTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM helpdesk_tickets WHERE status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HelpdeskTicket ticket = mapResultSetToTicket(rs);
                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    /**
     * Get ticket by ID
     */
    public HelpdeskTicket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT * FROM helpdesk_tickets WHERE ticket_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update ticket status
     */
    public boolean updateTicketStatus(int ticketId, String status) throws SQLException {
        String sql = "UPDATE helpdesk_tickets SET status = ?, updated_at = ? WHERE ticket_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, ticketId);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Update ticket priority
     */
    public boolean updateTicketPriority(int ticketId, String priority) throws SQLException {
        String sql = "UPDATE helpdesk_tickets SET priority = ?, updated_at = ? WHERE ticket_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priority);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, ticketId);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Assign ticket to someone
     */
    public boolean assignTicket(int ticketId, String assignedTo) throws SQLException {
        String sql = "UPDATE helpdesk_tickets SET assigned_to = ?, updated_at = ? WHERE ticket_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, assignedTo);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, ticketId);
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Get ticket statistics
     */
    public java.util.Map<String, Integer> getTicketStatistics() throws SQLException {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM helpdesk_tickets GROUP BY status";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                stats.put(status, count);
            }
        }
        return stats;
    }

    /**
     * Map ResultSet to HelpdeskTicket object
     */
    private HelpdeskTicket mapResultSetToTicket(ResultSet rs) throws SQLException {
        HelpdeskTicket ticket = new HelpdeskTicket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setSubject(rs.getString("subject"));
        ticket.setDescription(rs.getString("description"));
        ticket.setStatus(rs.getString("status"));
        ticket.setPriority(rs.getString("priority"));
        ticket.setStudentName(rs.getString("student_name"));
        ticket.setStudentEmail(rs.getString("student_email"));
        ticket.setAssignedTo(rs.getString("assigned_to"));
        ticket.setCategory(rs.getString("category"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ticket.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            ticket.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return ticket;
    }

    /**
     * Close connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 
package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.dao.HelpdeskDao;
import com.sms.model.HelpdeskTicket;
import com.sms.utils.InputValidator;

public class HelpdeskService {
    private HelpdeskDao helpdeskDao;
    private Scanner scanner;

    public HelpdeskService() throws SQLException {
        this.helpdeskDao = new HelpdeskDao();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Display emergency contacts
     */
    public void displayEmergencyContacts() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           🚨 EMERGENCY CONTACTS                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n📞 IMMEDIATE ASSISTANCE:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🔴 Campus Security        │ +91-98765-43210 │ 24/7 Emergency Response         │");
        System.out.println("│ 🔴 Medical Emergency      │ +91-98765-43211 │ First Aid & Medical Support    │");
        System.out.println("│ 🔴 IT Support (Urgent)    │ +91-98765-43212 │ System Outages & Critical Issues│");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n📞 GENERAL SUPPORT:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🟡 Academic Support       │ +91-98765-43213 │ Course & Exam Related Issues   │");
        System.out.println("│ 🟡 Financial Support      │ +91-98765-43214 │ Fee & Payment Related Issues   │");
        System.out.println("│ 🟡 Technical Support      │ +91-98765-43215 │ System & Login Issues          │");
        System.out.println("│ 🟡 Student Services       │ +91-98765-43216 │ General Student Inquiries      │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n📧 EMAIL SUPPORT:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 📧 General Inquiries      │ support@studentmanagement.edu                   │");
        System.out.println("│ 📧 Technical Issues       │ techsupport@studentmanagement.edu               │");
        System.out.println("│ 📧 Academic Issues        │ academic@studentmanagement.edu                  │");
        System.out.println("│ 📧 Financial Issues       │ finance@studentmanagement.edu                  │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n⏰ SUPPORT HOURS:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🕐 Monday - Friday        │ 8:00 AM - 8:00 PM                              │");
        System.out.println("│ 🕐 Saturday               │ 9:00 AM - 5:00 PM                              │");
        System.out.println("│ 🕐 Sunday                 │ 10:00 AM - 4:00 PM                             │");
        System.out.println("│ 🚨 Emergency Support      │ 24/7 Available                                 │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n💡 TIP: For urgent matters, always call the emergency numbers first!");
    }

    /**
     * Raise a new ticket
     */
    public void raiseTicket() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           🎫 RAISE NEW TICKET                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            // Get student information
            System.out.print("\n👤 Enter your full name: ");
            String studentName = scanner.nextLine().trim();
            
            if (studentName.isEmpty()) {
                System.out.println("❌ Name cannot be empty!");
                return;
            }
            
            System.out.print("📧 Enter your email address: ");
            String studentEmail = scanner.nextLine().trim();
            
            if (!isValidEmail(studentEmail)) {
                System.out.println("❌ Please enter a valid email address!");
                return;
            }
            
            // Get ticket category
            System.out.println("\n📂 Select ticket category:");
            System.out.println("1. Technical Issues (System, Login, etc.)");
            System.out.println("2. Academic Issues (Courses, Exams, etc.)");
            System.out.println("3. Financial Issues (Fees, Payments, etc.)");
            System.out.println("4. General Inquiries");
            
            int categoryChoice = InputValidator.getValidMenuChoice(scanner, "Enter category (1-4): ", 4);
            String category = getCategoryFromChoice(categoryChoice);
            
            // Get ticket priority
            System.out.println("\n⚡ Select priority level:");
            System.out.println("1. Low (General inquiry, non-urgent)");
            System.out.println("2. Medium (Standard issue)");
            System.out.println("3. High (Important issue)");
            System.out.println("4. Urgent (Critical issue)");
            
            int priorityChoice = InputValidator.getValidMenuChoice(scanner, "Enter priority (1-4): ", 4);
            String priority = getPriorityFromChoice(priorityChoice);
            scanner.nextLine();
            // Get ticket subject
            System.out.print("\n📝 Enter ticket subject (brief description): ");
            String subject = scanner.nextLine().trim();
            
            if (subject.isEmpty()) {
                System.out.println("❌ Subject cannot be empty!");
                return;
            }
            
            // Get ticket description
            System.out.println("\n📄 Enter detailed description of your issue:");
            System.out.println("(Type 'END' on a new line when finished)");
            
            StringBuilder description = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
                description.append(line).append("\n");
            }
            
            if (description.toString().trim().isEmpty()) {
                System.out.println("❌ Description cannot be empty!");
                return;
            }
            
            // Create ticket
            HelpdeskTicket ticket = new HelpdeskTicket(subject, description.toString().trim(), studentName, studentEmail, category);
            ticket.setPriority(priority);
            
            if (helpdeskDao.createTicket(ticket)) {
                System.out.println("\n✅ Ticket created successfully!");
                System.out.println("🎫 Ticket ID: " + ticket.getTicketId());
                System.out.println("📧 You will receive updates at: " + studentEmail);
                System.out.println("⏰ Expected response time: " + getExpectedResponseTime(priority));
            } else {
                System.out.println("❌ Failed to create ticket. Please try again.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * View all tickets
     */
    public void viewAllTickets() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           📋 ALL TICKETS                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            List<HelpdeskTicket> tickets = helpdeskDao.getAllTickets();
            
            if (tickets.isEmpty()) {
                System.out.println("\n📭 No tickets found.");
                return;
            }
            
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : tickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * View ticket details
     */
    public void viewTicketDetails() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           🔍 TICKET DETAILS                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            System.out.print("\n🎫 Enter ticket ID: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println("❌ Ticket not found!");
                return;
            }
            
            displayTicketDetails(ticket);
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * Update ticket status
     */
    public void updateTicketStatus() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           🔄 UPDATE TICKET STATUS                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            System.out.print("\n🎫 Enter ticket ID: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println("❌ Ticket not found!");
                return;
            }
            
            // Display current ticket details
            System.out.println("\n📋 Current Ticket Details:");
            displayTicketDetails(ticket);
            
            // Show status options
            System.out.println("\n🔄 Select new status:");
            System.out.println("1. OPEN - Ticket is open and awaiting response");
            System.out.println("2. IN_PROGRESS - Ticket is being worked on");
            System.out.println("3. RESOLVED - Issue has been resolved");
            System.out.println("4. CLOSED - Ticket has been closed");
            
            int statusChoice = InputValidator.getValidMenuChoice(scanner, "Enter status choice (1-4): ", 4);
            String newStatus = getStatusFromChoice(statusChoice);
            
            // Update the ticket status
            if (helpdeskDao.updateTicketStatus(ticketId, newStatus)) {
                System.out.println("\n✅ Ticket status updated successfully!");
                System.out.println("🔄 New status: " + newStatus);
                
                // Display updated ticket details
                HelpdeskTicket updatedTicket = helpdeskDao.getTicketById(ticketId);
                if (updatedTicket != null) {
                    System.out.println("\n📋 Updated Ticket Details:");
                    displayTicketDetails(updatedTicket);
                }
            } else {
                System.out.println("❌ Failed to update ticket status. Please try again.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * Update ticket priority
     */
    public void updateTicketPriority() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           ⚡ UPDATE TICKET PRIORITY                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            System.out.print("\n🎫 Enter ticket ID: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println("❌ Ticket not found!");
                return;
            }
            
            // Display current ticket details
            System.out.println("\n📋 Current Ticket Details:");
            displayTicketDetails(ticket);
            
            // Show priority options
            System.out.println("\n⚡ Select new priority:");
            System.out.println("1. LOW - General inquiry, non-urgent");
            System.out.println("2. MEDIUM - Standard issue");
            System.out.println("3. HIGH - Important issue");
            System.out.println("4. URGENT - Critical issue");
            
            int priorityChoice = InputValidator.getValidMenuChoice(scanner, "Enter priority choice (1-4): ", 4);
            String newPriority = getPriorityFromChoice(priorityChoice);
            
            // Update the ticket priority
            if (helpdeskDao.updateTicketPriority(ticketId, newPriority)) {
                System.out.println("\n✅ Ticket priority updated successfully!");
                System.out.println("⚡ New priority: " + newPriority);
                System.out.println("⏰ Expected response time: " + getExpectedResponseTime(newPriority));
                
                // Display updated ticket details
                HelpdeskTicket updatedTicket = helpdeskDao.getTicketById(ticketId);
                if (updatedTicket != null) {
                    System.out.println("\n📋 Updated Ticket Details:");
                    displayTicketDetails(updatedTicket);
                }
            } else {
                System.out.println("❌ Failed to update ticket priority. Please try again.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * View tickets by status
     */
    public void viewTicketsByStatus() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           📊 VIEW TICKETS BY STATUS                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            System.out.println("\n📊 Select status to view:");
            System.out.println("1. OPEN - All open tickets");
            System.out.println("2. IN_PROGRESS - All tickets being worked on");
            System.out.println("3. RESOLVED - All resolved tickets");
            System.out.println("4. CLOSED - All closed tickets");
            
            int statusChoice = InputValidator.getValidMenuChoice(scanner, "Enter status choice (1-4): ", 4);
            String status = getStatusFromChoice(statusChoice);
            
            List<HelpdeskTicket> tickets = helpdeskDao.getTicketsByStatus(status);
            
            if (tickets.isEmpty()) {
                System.out.println("\n📭 No tickets found with status: " + status);
                return;
            }
            
            System.out.println("\n📋 Tickets with status: " + status);
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : tickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            System.out.println("📊 Total tickets with status '" + status + "': " + tickets.size());
            
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    /**
     * Display ticket details
     */
    private void displayTicketDetails(HelpdeskTicket ticket) {
        System.out.println("\n┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                              TICKET DETAILS                                │");
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ 🎫 Ticket ID: %-60s │%n", ticket.getTicketId());
        System.out.printf("│ 📝 Subject: %-60s │%n", ticket.getSubject());
        System.out.printf("│ 👤 Student: %-60s │%n", ticket.getStudentName());
        System.out.printf("│ 📧 Email: %-60s │%n", ticket.getStudentEmail());
        System.out.printf("│ 📂 Category: %-60s │%n", ticket.getCategory());
        System.out.printf("│ ⚡ Priority: %-60s │%n", ticket.getPriority());
        System.out.printf("│ 📊 Status: %-60s │%n", ticket.getStatus());
        System.out.printf("│ 📅 Created: %-60s │%n", ticket.getCreatedAt());
        System.out.printf("│ 🔄 Updated: %-60s │%n", ticket.getUpdatedAt());
        if (ticket.getAssignedTo() != null) {
            System.out.printf("│ 👨‍💼 Assigned To: %-60s │%n", ticket.getAssignedTo());
        }
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ 📄 DESCRIPTION:                                                              │");
        System.out.println("│ " + "─".repeat(76) + " │");
        
        // Split description into lines
        String[] lines = ticket.getDescription().split("\n");
        for (String line : lines) {
            if (line.length() > 76) {
                // Split long lines
                for (int i = 0; i < line.length(); i += 76) {
                    String part = line.substring(i, Math.min(i + 76, line.length()));
                    System.out.printf("│ %-76s │%n", part);
                }
            } else {
                System.out.printf("│ %-76s │%n", line);
            }
        }
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Get category from choice
     */
    private String getCategoryFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "TECHNICAL";
            case 2 -> "ACADEMIC";
            case 3 -> "FINANCIAL";
            case 4 -> "GENERAL";
            default -> "GENERAL";
        };
    }

    /**
     * Get priority from choice
     */
    private String getPriorityFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "LOW";
            case 2 -> "MEDIUM";
            case 3 -> "HIGH";
            case 4 -> "URGENT";
            default -> "MEDIUM";
        };
    }

    /**
     * Get status from choice
     */
    private String getStatusFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "OPEN";
            case 2 -> "IN_PROGRESS";
            case 3 -> "RESOLVED";
            case 4 -> "CLOSED";
            default -> "OPEN";
        };
    }

    /**
     * Get expected response time based on priority
     */
    private String getExpectedResponseTime(String priority) {
        return switch (priority) {
            case "URGENT" -> "2-4 hours";
            case "HIGH" -> "24 hours";
            case "MEDIUM" -> "48 hours";
            case "LOW" -> "72 hours";
            default -> "48 hours";
        };
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Close resources
     */
    public void close() {
        if (helpdeskDao != null) {
            helpdeskDao.closeConnection();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
} 
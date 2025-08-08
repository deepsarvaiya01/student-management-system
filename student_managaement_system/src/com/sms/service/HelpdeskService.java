package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        System.out.println("║                           🚨 EMERGENCY CONTACTS                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n📞 IMMEDIATE ASSISTANCE:");
        System.out.println("┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🔴 Campus Security        │ +91-98765-43210 │ 24/7 Emergency Response          │");
        System.out.println("│ 🔴 Medical Emergency      │ +91-98765-43211 │ First Aid & Medical Support      │");
        System.out.println("│ 🔴 IT Support (Urgent)    │ +91-98765-43212 │ System Outages & Critical Issues │");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n📞 GENERAL SUPPORT:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🟡 Academic Support       │ +91-98765-43213 │ Course & Exam Related Issues   │");
        System.out.println("│ 🟡 Financial Support      │ +91-98765-43214 │ Fee & Payment Related Issues   │");
        System.out.println("│ 🟡 Technical Support      │ +91-98765-43215 │ System & Login Issues          │");
        System.out.println("│ 🟡 Student Services       │ +91-98765-43216 │ General Student Inquiries      │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n📧 EMAIL SUPPORT:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 📧 General Inquiries      │ support@studentmanagement.edu                    │");
        System.out.println("│ 📧 Technical Issues       │ techsupport@studentmanagement.edu                │");
        System.out.println("│ 📧 Academic Issues        │ academic@studentmanagement.edu                   │");
        System.out.println("│ 📧 Financial Issues       │ finance@studentmanagement.edu                    │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n⏰ SUPPORT HOURS:");
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ 🕐 Monday - Friday        │ 8:00 AM - 8:00 PM                                │");
        System.out.println("│ 🕐 Saturday               │ 9:00 AM - 5:00 PM                                │");
        System.out.println("│ 🕐 Sunday                 │ 10:00 AM - 4:00 PM                               │");
        System.out.println("│ 🚨 Emergency Support      │ 24/7 Available                                   │");
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
        
        System.out.println("\n💡 TIP: For urgent matters, always call the emergency numbers first!");
    }

  
    public void raiseTicket() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                RAISE NEW TICKET                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            System.out.print("\n Enter your full name: ");
            String studentName = scanner.nextLine().trim();
            
            if (studentName.isEmpty()) {
                System.out.println(" Name cannot be empty!");
                return;
            }
            
            System.out.print(" Enter your email address: ");
            String studentEmail = scanner.nextLine().trim();
            
            if (!isValidEmail(studentEmail)) {
                System.out.println(" Please enter a valid email address!");
                return;
            }
            
            // Get ticket category
            System.out.println("\n Select ticket category:");
            System.out.println("1. Technical Issues (System, Login, etc.)");
            System.out.println("2. Academic Issues (Courses, Exams, etc.)");
            System.out.println("3. Financial Issues (Fees, Payments, etc.)");
            System.out.println("4. General Inquiries");
            
            int categoryChoice = InputValidator.getValidMenuChoice(scanner, "Enter category (1-4): ", 4);
            String category = getCategoryFromChoice(categoryChoice);
            
            // Get ticket priority
            System.out.println("\n Select priority level:");
            System.out.println("1. Low (General inquiry, non-urgent)");
            System.out.println("2. Medium (Standard issue)");
            System.out.println("3. High (Important issue)");
            System.out.println("4. Urgent (Critical issue)");
            
            int priorityChoice = InputValidator.getValidMenuChoice(scanner, "Enter priority (1-4): ", 4);
            String priority = getPriorityFromChoice(priorityChoice);
            scanner.nextLine();
            // Get ticket subject
            System.out.print("\n Enter ticket subject (brief description): ");
            String subject = scanner.nextLine().trim();
            
            if (subject.isEmpty()) {
                System.out.println(" Subject cannot be empty!");
                return;
            }
            
            // Get ticket description
            System.out.println("\n Enter detailed description of your issue:");
            System.out.println("(Type 'END' on a new line when finished)");
            
            StringBuilder description = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
                description.append(line).append("\n");
            }
            
            if (description.toString().trim().isEmpty()) {
                System.out.println(" Description cannot be empty!");
                return;
            }
            
            // Create ticket
            HelpdeskTicket ticket = new HelpdeskTicket(subject, description.toString().trim(), studentName, studentEmail, category);
            ticket.setPriority(priority);
            
            if (helpdeskDao.createTicket(ticket)) {
                System.out.println("\n Ticket created successfully!");
                System.out.println(" Ticket ID: " + ticket.getTicketId());
                System.out.println(" You will receive updates at: " + studentEmail);
                System.out.println(" Expected response time: " + getExpectedResponseTime(priority));
            } else {
                System.out.println(" Failed to create ticket. Please try again.");
            }
            
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }


    public void viewAllTickets() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                ALL TICKETS                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            List<HelpdeskTicket> tickets = helpdeskDao.getAllTickets();
            
            if (tickets.isEmpty()) {
                System.out.println("\n No tickets found.");
                return;
            }
            
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                     FILTER OPTIONS                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Show ALL tickets (including resolved and closed)      ║");
            System.out.println("║ 2. Show only ACTIVE tickets (OPEN and IN_PROGRESS)       ║");
            System.out.println("║ 3. Show only RESOLVED tickets                            ║");
            System.out.println("║ 4. Show only CLOSED tickets                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");

            
            int filterChoice = InputValidator.getValidMenuChoice(scanner, "Enter filter choice (1-4): ", 4);
            
            List<HelpdeskTicket> filteredTickets = new ArrayList<>();
            
            switch (filterChoice) {
                case 1 -> {
                    // Show all tickets
                    filteredTickets = tickets;
                    System.out.println("\n ALL TICKETS (including resolved and closed):");
                }
                case 2 -> {
                    // Show only active tickets
                    filteredTickets = tickets.stream()
                        .filter(ticket -> "OPEN".equals(ticket.getStatus()) || "IN_PROGRESS".equals(ticket.getStatus()))
                        .collect(Collectors.toList());
                    System.out.println("\n ACTIVE TICKETS (OPEN and IN_PROGRESS):");
                }
                case 3 -> {
                    // Show only resolved tickets
                    filteredTickets = tickets.stream()
                        .filter(ticket -> "RESOLVED".equals(ticket.getStatus()))
                        .collect(Collectors.toList());
                    System.out.println("\n RESOLVED TICKETS:");
                }
                case 4 -> {
                    // Show only closed tickets
                    filteredTickets = tickets.stream()
                        .filter(ticket -> "CLOSED".equals(ticket.getStatus()))
                        .collect(Collectors.toList());
                    System.out.println("\n CLOSED TICKETS:");
                }
            }
            
            if (filteredTickets.isEmpty()) {
                System.out.println("\n No tickets found with the selected filter.");
                return;
            }
            
            // Display tickets
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : filteredTickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            
            // Show summary statistics
            System.out.println("\n SUMMARY:");
            System.out.println("┌────────────────────────────────────────────────────────────────────┐");
            System.out.printf("│ Total tickets shown: %-50d %n", filteredTickets.size());

            // Count by status
            long openCount = filteredTickets.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
            long inProgressCount = filteredTickets.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
            long resolvedCount = filteredTickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
            long closedCount = filteredTickets.stream().filter(t -> "CLOSED".equals(t.getStatus())).count();

            if (openCount > 0) System.out.printf("│ OPEN tickets: %-55d %n", openCount);
            if (inProgressCount > 0) System.out.printf("│ IN_PROGRESS tickets: %-46d %n", inProgressCount);
            if (resolvedCount > 0) System.out.printf("│ RESOLVED tickets: %-50d %n", resolvedCount);
            if (closedCount > 0) System.out.printf("│ CLOSED tickets: %-52d %n", closedCount);

            System.out.println("└────────────────────────────────────────────────────────────────────┘");

        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }


    public void viewTicketDetails() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                VIEW TICKET DETAILS                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            // First, show all available tickets (ID and Subject only)
            System.out.println("\n Available Tickets (ID and Subject):");
            List<HelpdeskTicket> allTickets = helpdeskDao.getAllTickets();
            
            if (allTickets.isEmpty()) {
                System.out.println(" No tickets found in the system.");
                return;
            }
            
            // Display tickets in a simple format (ID and Subject only)
            System.out.println("┌──────────────┬──────────────────────────────────────────────────────────────┐");
            System.out.println("│   Ticket ID  │ Subject                                                      │");
            System.out.println("├──────────────┼──────────────────────────────────────────────────────────────┤");
            
            for (HelpdeskTicket ticket : allTickets) {
                String subject = ticket.getSubject();
                // Truncate subject if it's too long
                if (subject != null && subject.length() > 60) {
                    subject = subject.substring(0, 57) + "...";
                }
                System.out.printf("│ %-12d │ %-60s │%n", ticket.getTicketId(), subject != null ? subject : "--");
            }
            System.out.println("└──────────────┴──────────────────────────────────────────────────────────────┘");
            
            System.out.print("\n Enter ticket ID to view details: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println(" Ticket not found! Please check the ticket ID from the list above.");
                return;
            }
            
            // Display the full ticket details
            System.out.println("\n Loading ticket details...");
            displayTicketDetails(ticket);
            
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
            System.out.println(" Please check your database connection and try again.");
        } catch (Exception e) {
            System.out.println(" Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Update ticket status
     */
    public void updateTicketStatus() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                UPDATE TICKET STATUS                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            // First, show all available tickets (excluding CLOSED and RESOLVED)
            System.out.println("\n Available Tickets for Update (excluding CLOSED and RESOLVED):");
            List<HelpdeskTicket> allTickets = helpdeskDao.getAllTickets();
            
            if (allTickets.isEmpty()) {
                System.out.println(" No tickets found in the system.");
                return;
            }
            
            // Filter out CLOSED and RESOLVED tickets
            List<HelpdeskTicket> updatableTickets = allTickets.stream()
                .filter(ticket -> !"CLOSED".equals(ticket.getStatus()) && !"RESOLVED".equals(ticket.getStatus()))
                .collect(Collectors.toList());
            
            if (updatableTickets.isEmpty()) {
                System.out.println(" No tickets available for update. All tickets are either CLOSED or RESOLVED.");
                return;
            }
            
            // Display updatable tickets in a table format
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : updatableTickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            
            System.out.print("\n Enter ticket ID to update: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            // Verify ticket exists and is updatable
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println(" Ticket not found! Please check the ticket ID from the list above.");
                return;
            }
            
            // Check if ticket is CLOSED or RESOLVED
            if ("CLOSED".equals(ticket.getStatus()) || "RESOLVED".equals(ticket.getStatus())) {
                System.out.println(" Cannot update ticket with status '" + ticket.getStatus() + "'. Only OPEN and IN_PROGRESS tickets can be updated.");
                return;
            }
            
            // Display current ticket details
            System.out.println("\n Current Ticket Details:");
            displayTicketDetails(ticket);
            
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                  SELECT NEW STATUS                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. OPEN        - Ticket is open and awaiting response    ║");
            System.out.println("║ 2. IN_PROGRESS - Ticket is being worked on               ║");
            System.out.println("║ 3. RESOLVED    - Issue has been resolved                 ║");
            System.out.println("║ 4. CLOSED      - Ticket has been closed                  ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");

            int statusChoice = InputValidator.getValidMenuChoice(scanner, "Enter status choice (1-4): ", 4);
            String newStatus = getStatusFromChoice(statusChoice);
            
            // Update the ticket status
            System.out.println("\n Updating ticket status...");
            if (helpdeskDao.updateTicketStatus(ticketId, newStatus)) {
                System.out.println("\n Ticket status updated successfully!");
                System.out.println(" Status changed from '" + ticket.getStatus() + "' to '" + newStatus + "'");
                
                // Verify the update by retrieving the ticket again
                System.out.println("\n Verifying update...");
                HelpdeskTicket updatedTicket = helpdeskDao.getTicketById(ticketId);
                if (updatedTicket != null) {
                    if (newStatus.equals(updatedTicket.getStatus())) {
                        System.out.println(" Verification successful! Ticket status has been updated.");
                        System.out.println("\n Updated Ticket Details:");
                        displayTicketDetails(updatedTicket);
                        
                        // Show success message based on status
                        if ("RESOLVED".equals(newStatus)) {
                            System.out.println("\n Ticket has been marked as RESOLVED!");
                            System.out.println(" Consider sending a notification to the student.");
                        } else if ("CLOSED".equals(newStatus)) {
                            System.out.println("\n Ticket has been CLOSED!");
                            System.out.println(" This ticket is now archived and no longer active.");
                        }
                    } else {
                        System.out.println("  Warning: Ticket status was not updated correctly.");
                        System.out.println("Expected: " + newStatus + ", Actual: " + updatedTicket.getStatus());
                    }
                } else {
                    System.out.println("  Warning: Could not retrieve updated ticket details.");
                }
            } else {
                System.out.println(" Failed to update ticket status. Please try again.");
                System.out.println(" Make sure the ticket ID exists and you have proper permissions.");
            }
            
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
            System.out.println(" Please check your database connection and try again.");
        } catch (Exception e) {
            System.out.println(" Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Update ticket priority
     */
    public void updateTicketPriority() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                               UPDATE TICKET PRIORITY                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
            // First, show all available tickets (excluding CLOSED and RESOLVED)
            System.out.println("\n Available Tickets for Priority Update (excluding CLOSED and RESOLVED):");
            List<HelpdeskTicket> allTickets = helpdeskDao.getAllTickets();
            
            if (allTickets.isEmpty()) {
                System.out.println(" No tickets found in the system.");
                return;
            }
            
            // Filter out CLOSED and RESOLVED tickets
            List<HelpdeskTicket> updatableTickets = allTickets.stream()
                .filter(ticket -> !"CLOSED".equals(ticket.getStatus()) && !"RESOLVED".equals(ticket.getStatus()))
                .collect(Collectors.toList());
            
            if (updatableTickets.isEmpty()) {
                System.out.println(" No tickets available for priority update. All tickets are either CLOSED or RESOLVED.");
                return;
            }
            
            // Display updatable tickets in a table format
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : updatableTickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            
            System.out.print("\n Enter ticket ID to update: ");
            int ticketId = InputValidator.getValidInteger(scanner, "Enter ticket ID: ", "Ticket ID");
            
            HelpdeskTicket ticket = helpdeskDao.getTicketById(ticketId);
            
            if (ticket == null) {
                System.out.println(" Ticket not found! Please check the ticket ID from the list above.");
                return;
            }
            
            // Check if ticket is CLOSED or RESOLVED
            if ("CLOSED".equals(ticket.getStatus()) || "RESOLVED".equals(ticket.getStatus())) {
                System.out.println(" Cannot update ticket with status '" + ticket.getStatus() + "'. Only OPEN and IN_PROGRESS tickets can be updated.");
                return;
            }
            
            // Display current ticket details
            System.out.println("\n Current Ticket Details:");
            displayTicketDetails(ticket);
            
            // Show priority options
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                  SELECT NEW PRIORITY                     ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. LOW     - General inquiry, non-urgent                 ║");
            System.out.println("║ 2. MEDIUM  - Standard issue                              ║");
            System.out.println("║ 3. HIGH    - Important issue                             ║");
            System.out.println("║ 4. URGENT  - Critical issue                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");

            
            int priorityChoice = InputValidator.getValidMenuChoice(scanner, "Enter priority choice (1-4): ", 4);
            String newPriority = getPriorityFromChoice(priorityChoice);
            
            // Update the ticket priority
            if (helpdeskDao.updateTicketPriority(ticketId, newPriority)) {
                System.out.println("\n Ticket priority updated successfully!");
                System.out.println(" New priority: " + newPriority);
                System.out.println(" Expected response time: " + getExpectedResponseTime(newPriority));
                
                // Display updated ticket details
                HelpdeskTicket updatedTicket = helpdeskDao.getTicketById(ticketId);
                if (updatedTicket != null) {
                    System.out.println("\nUpdated Ticket Details:");
                    displayTicketDetails(updatedTicket);
                }
            } else {
                System.out.println(" Failed to update ticket priority. Please try again.");
            }
            
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }

    /**
     * View tickets by status
     */
    public void viewTicketsByStatus() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                               VIEW TICKETS BY STATUS                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
        
        try {
        	System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        	System.out.println("║                SELECT STATUS TO VIEW                     ║");
        	System.out.println("╠══════════════════════════════════════════════════════════╣");
        	System.out.println("║ 1. OPEN        - All open tickets                        ║");
        	System.out.println("║ 2. IN_PROGRESS - All tickets being worked on             ║");
        	System.out.println("║ 3. RESOLVED    - All resolved tickets                    ║");
        	System.out.println("║ 4. CLOSED      - All closed tickets                      ║");
        	System.out.println("╚══════════════════════════════════════════════════════════╝");

            
            int statusChoice = InputValidator.getValidMenuChoice(scanner, "Enter status choice (1-4): ", 4);
            String status = getStatusFromChoice(statusChoice);
            
            List<HelpdeskTicket> tickets = helpdeskDao.getTicketsByStatus(status);
            
            if (tickets.isEmpty()) {
                System.out.println("\n No tickets found with status: " + status);
                return;
            }
            
            System.out.println("\n Tickets with status: " + status);
            HelpdeskTicket.printHeader();
            for (HelpdeskTicket ticket : tickets) {
                System.out.println(ticket);
            }
            System.out.println("+----------+----------------------+-----------------+--------------+------------+----------------------+-----------------+");
            System.out.println(" Total tickets with status '" + status + "': " + tickets.size());
            
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }

    /**
     * Display ticket details
     */
    private void displayTicketDetails(HelpdeskTicket ticket) {
        System.out.println("\n┌──────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                              TICKET DETAILS                                  │");
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│  Ticket ID: %-60s %n", ticket.getTicketId());
        System.out.printf("│  Subject: %-60s %n", ticket.getSubject());
        System.out.printf("│  Student: %-60s %n", ticket.getStudentName());
        System.out.printf("│  Email: %-60s %n", ticket.getStudentEmail());
        System.out.printf("│  Category: %-60s %n", ticket.getCategory());
        System.out.printf("│  Priority: %-60s %n", ticket.getPriority());
        System.out.printf("│  Status: %-60s %n", ticket.getStatus());
        System.out.printf("│  Created: %-60s %n", ticket.getCreatedAt());
        System.out.printf("│  Updated: %-60s %n", ticket.getUpdatedAt());
        if (ticket.getAssignedTo() != null) {
            System.out.printf("│  Assigned To: %-60s │%n", ticket.getAssignedTo());
        }
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│  DESCRIPTION:                                                                |");
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


    private String getCategoryFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "TECHNICAL";
            case 2 -> "ACADEMIC";
            case 3 -> "FINANCIAL";
            case 4 -> "GENERAL";
            default -> "GENERAL";
        };
    }


    private String getPriorityFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "LOW";
            case 2 -> "MEDIUM";
            case 3 -> "HIGH";
            case 4 -> "URGENT";
            default -> "MEDIUM";
        };
    }


    private String getStatusFromChoice(int choice) {
        return switch (choice) {
            case 1 -> "OPEN";
            case 2 -> "IN_PROGRESS";
            case 3 -> "RESOLVED";
            case 4 -> "CLOSED";
            default -> "OPEN";
        };
    }


    private String getExpectedResponseTime(String priority) {
        return switch (priority) {
            case "URGENT" -> "2-4 hours";
            case "HIGH" -> "24 hours";
            case "MEDIUM" -> "48 hours";
            case "LOW" -> "72 hours";
            default -> "48 hours";
        };
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }


    public void close() {
        if (helpdeskDao != null) {
            helpdeskDao.closeConnection();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
} 
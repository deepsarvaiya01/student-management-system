package com.sms.controller;

import java.sql.SQLException;

import com.sms.service.HelpdeskService;

public class HelpdeskController {
    private HelpdeskService helpdeskService;

    public HelpdeskController() throws SQLException {
        this.helpdeskService = new HelpdeskService();
    }

    /**
     * Display emergency contacts
     */
    public void showEmergencyContacts() {
        try {
            helpdeskService.displayEmergencyContacts();
        } catch (Exception e) {
            System.out.println("❌ Error displaying emergency contacts: " + e.getMessage());
        }
    }

    /**
     * Raise a new ticket
     */
    public void raiseNewTicket() {
        try {
            helpdeskService.raiseTicket();
        } catch (Exception e) {
            System.out.println("❌ Error raising ticket: " + e.getMessage());
        }
    }

    /**
     * View all tickets
     */
    public void viewAllTickets() {
        try {
            helpdeskService.viewAllTickets();
        } catch (Exception e) {
            System.out.println("❌ Error viewing tickets: " + e.getMessage());
        }
    }

    /**
     * View ticket details
     */
    public void viewTicketDetails() {
        try {
            helpdeskService.viewTicketDetails();
        } catch (Exception e) {
            System.out.println("❌ Error viewing ticket details: " + e.getMessage());
        }
    }

    /**
     * Update ticket status
     */
    public void updateTicketStatus() {
        try {
            helpdeskService.updateTicketStatus();
        } catch (Exception e) {
            System.out.println("❌ Error updating ticket status: " + e.getMessage());
        }
    }

    /**
     * Update ticket priority
     */
    public void updateTicketPriority() {
        try {
            helpdeskService.updateTicketPriority();
        } catch (Exception e) {
            System.out.println("❌ Error updating ticket priority: " + e.getMessage());
        }
    }

    /**
     * View tickets by status
     */
    public void viewTicketsByStatus() {
        try {
            helpdeskService.viewTicketsByStatus();
        } catch (Exception e) {
            System.out.println("❌ Error viewing tickets by status: " + e.getMessage());
        }
    }

    /**
     * Close resources
     */
    public void close() {
        if (helpdeskService != null) {
            helpdeskService.close();
        }
    }
} 
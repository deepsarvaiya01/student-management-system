package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.HelpdeskController;
import com.sms.utils.InputValidator;

public class HelpdeskMain {
    private HelpdeskController controller;

    public HelpdeskMain() throws SQLException {
        this.controller = new HelpdeskController();
    }

    public void show() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                   HELPDESK SYSTEM                            ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Emergency Contacts                                                        ║");
            System.out.println("║ 2. Raise New Ticket                                                          ║");
            System.out.println("║ 3. View All Tickets                                                          ║");
            System.out.println("║ 4. View Ticket Details                                                       ║");
            System.out.println("║ 5. Update Ticket Status                                                      ║");
            System.out.println("║ 6. Update Ticket Priority                                                    ║");
            System.out.println("║ 7. View Tickets by Status                                                    ║");
            System.out.println("║ 0. Back                                                                      ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

            choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-7): ", 7);

            switch (choice) {
                case 1 -> {
                    System.out.println("\n Loading emergency contacts...");
                    controller.showEmergencyContacts();
                }
                case 2 -> {
                    System.out.println("\n Opening ticket creation form...");
                    controller.raiseNewTicket();
                }
                case 3 -> {
                    System.out.println("\n Loading all tickets...");
                    controller.viewAllTickets();
                }
                case 4 -> {
                    System.out.println("\n Opening ticket details viewer...");
                    controller.viewTicketDetails();
                }
                case 5 -> {
                    System.out.println("\n Opening ticket status updater...");
                    controller.updateTicketStatus();
                }
                case 6 -> {
                    System.out.println("\n Opening ticket priority updater...");
                    controller.updateTicketPriority();
                }
                case 7 -> {
                    System.out.println("\n Opening tickets by status viewer...");
                    controller.viewTicketsByStatus();
                }
                case 0 -> {
                    System.out.println("\n Going back to main menu...");
                    return;
                   // controller.close();
                }
                default -> System.out.println(" Invalid choice! Please enter a number between 0 and 7.");
            }
        } while (choice != 0);
    }
} 
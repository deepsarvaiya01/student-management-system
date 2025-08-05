package com.sms.utils;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.exception.AppException;
import com.sms.model.Gender;
import com.sms.service.StudentService;

public class InputValidator {

	// Get valid integer input
	public static int getValidInteger(Scanner scanner, String prompt, String fieldName) {
		while (true) {
			try {
				System.out.print(prompt);
				if (!scanner.hasNextInt()) {
					String invalidInput = scanner.next();
					throw new AppException("❌ Invalid " + fieldName + "! Expected a number, but got: '" + invalidInput
							+ "'\nPlease try again:");
				}
				int value = scanner.nextInt();
				if (value <= 0) {
					throw new AppException(
							"❌ Invalid " + fieldName + "! Must be a positive number.\nPlease try again:");
				}
				return value;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid integer input and consume newline
	public static int getValidIntegerWithNewline(Scanner scanner, String prompt, String fieldName) {
		int value = getValidInteger(scanner, prompt, fieldName);
		scanner.nextLine(); // Consume the newline character
		return value;
	}

	// Get valid integer input, allowing zero for "go back"
	public static int getValidIntegerAllowZero(Scanner scanner, String prompt, String fieldName) {
		while (true) {
			try {
				System.out.print(prompt);
				if (!scanner.hasNextInt()) {
					String invalidInput = scanner.next();
					throw new AppException("❌ Invalid " + fieldName + "! Expected a number, but got: '" + invalidInput
							+ "'\nPlease try again:");
				}
				int value = scanner.nextInt();
				if (value < 0) {
					throw new AppException("❌ Invalid " + fieldName + "! Cannot be negative.\nPlease try again:");
				}
				return value; // 0 is allowed now
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid integer with range validation
	public static int getValidIntegerInRange(Scanner scanner, String prompt, String fieldName, int min, int max) {
		while (true) {
			try {
				int value = (min == 0) ? getValidIntegerAllowZero(scanner, prompt, fieldName)
						: getValidInteger(scanner, prompt, fieldName);
				if (value < min || value > max) {
					throw new AppException("❌ Invalid " + fieldName + "! Must be between " + min + " and " + max
							+ ".\nPlease try again:");
				}
				return value;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid integer with range validation and consume newline
	public static int getValidIntegerInRangeWithNewline(Scanner scanner, String prompt, String fieldName, int min,
			int max) {
		int value = getValidIntegerInRange(scanner, prompt, fieldName, min, max);
		scanner.nextLine(); // Consume the newline character
		return value;
	}

	// Get valid name input
	public static String getValidName(Scanner scanner, String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				String name = scanner.nextLine().trim();

				if (name.isEmpty()) {
					throw new AppException("❌ Name cannot be empty!\nPlease try again:");
				}

				if (name.length() > 50) {
					throw new AppException("❌ Name is too long! Maximum 50 characters allowed.\nPlease try again:");
				}

				if (!name.matches("[a-zA-Z ]+")) {
					throw new AppException("❌ Invalid name! Only letters and spaces are allowed.\nPlease try again:");
				}

				return name;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid email input
	public static String getValidEmail(Scanner scanner, String prompt, StudentService studentService) {
		while (true) {
			try {
				System.out.print(prompt);
				String email = scanner.nextLine().trim();

				if (email.isEmpty()) {
					throw new AppException("❌ Email cannot be empty!\nPlease try again:");
				}

				if (email.length() > 100) {
					throw new AppException("❌ Email is too long! Maximum 100 characters allowed.\nPlease try again:");
				}

				if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
					throw new AppException(
							"❌ Invalid email format! Please enter a valid email (e.g., user@example.com)\nPlease try again:");
				}

				if (studentService.isEmailExists(email)) {
					throw new AppException("❌ Email already exists. Please enter a different email");
				}

				return email;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid city input
	public static String getValidCity(Scanner scanner, String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				String city = scanner.nextLine().trim();

				if (city.isEmpty()) {
					throw new AppException("❌ City cannot be empty!\nPlease try again:");
				}

				if (city.length() > 50) {
					throw new AppException(
							"❌ City name is too long! Maximum 50 characters allowed.\nPlease try again:");
				}

				if (!city.matches("[a-zA-Z ]+")) {
					throw new AppException(
							"❌ Invalid city name! Only letters and spaces are allowed.\nPlease try again:");
				}

				return city;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static String getValidMobile(Scanner scanner, String prompt, String name, StudentService studentService) {
		String mobile;
		while (true) {
			System.out.print(prompt);
			mobile = scanner.nextLine().trim();

			if (mobile.isEmpty()) {
				System.out.println("❌ Mobile number cannot be empty!");
				continue;
			}

			if (!mobile.matches("^[6-9]\\d{9}$")) {
				System.out.println("❌ Invalid mobile number! It should be 10 digits and start with 6-9.");
				continue;
			}

			if (studentService.isNameAndMobileExists(name, mobile)) {
				System.out.println("❌ A student with the same name and mobile number already exists.");
				continue;
			}

			break;
		}
		return mobile;
	}
	

	public static String getValidMobileOnly(Scanner scanner, String prompt) {
		String mobile;
		while (true) {
			System.out.print(prompt);
			mobile = scanner.nextLine().trim();

			if (mobile.isEmpty()) {
				System.out.println("❌ Mobile number cannot be empty!");
				continue;
			}

			if (!mobile.matches("^[6-9]\\d{9}$")) {
				System.out.println("❌ Invalid mobile number! It should be 10 digits and start with 6-9.");
				continue;
			}

			break;
		}
		return mobile;
	}

	// Get valid age input
	public static int getValidAge(Scanner scanner, String prompt) {
		return getValidIntegerInRange(scanner, prompt, "Age", 15, 100);
	}

	public static Gender getValidGender(Scanner scanner, String prompt) {
		while (true) {
			scanner.nextLine();
			System.out.print(prompt);
			String input = scanner.nextLine().trim().toUpperCase();
			Gender gender = Gender.fromString(input);
			if (gender != null) {
				return gender;
			} else {
				System.out.println("Invalid gender. Please enter M for Male, F for Female, or O for Other.");
			}
		}
	}

	// Get valid decimal input (for payment amounts)
	public static BigDecimal getValidDecimal(Scanner scanner, String prompt, String fieldName) {
		while (true) {
			try {
				System.out.print(prompt);
				if (!scanner.hasNextBigDecimal()) {
					String invalidInput = scanner.next();
					throw new AppException("❌ Invalid " + fieldName + "! Expected a number, but got: '" + invalidInput
							+ "'\nPlease try again:");
				}
				BigDecimal value = scanner.nextBigDecimal();
				if (value.compareTo(BigDecimal.ZERO) <= 0) {
					throw new AppException(
							"❌ Invalid " + fieldName + "! Must be greater than zero.\nPlease try again:");
				}
				return value;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid menu choice
	public static int getValidMenuChoice(Scanner scanner, String prompt, int maxChoice) {
		while (true) {
			try {
				int value = getValidIntegerAllowZero(scanner, prompt, "Menu Choice");
				if (value > maxChoice) {
					throw new AppException(
							"❌ Invalid Menu Choice! Must be between 0 and " + maxChoice + ".\nPlease try again:");
				}
				return value;
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid confirmation (y/n)
	public static boolean getValidConfirmation(Scanner scanner, String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				String input = scanner.nextLine().trim().toLowerCase();

				if (input.equals("y") || input.equals("yes")) {
					return true;
				} else if (input.equals("n") || input.equals("no")) {
					return false;
				} else {
					throw new AppException(
							"❌ Invalid input! Please enter 'y' for yes or 'n' for no.\nPlease try again:");
				}
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Get valid payment method
	public static String getValidPaymentMethod(Scanner scanner, String prompt) {
		while (true) {
			try {
				System.out.print(prompt);
				String method = scanner.nextLine().trim().toLowerCase();

				if (method.equals("card") || method.equals("cash") || method.equals("upi")) {
					return method;
				} else {
					throw new AppException(
							"❌ Invalid payment method! Please choose: card, cash, or upi.\nPlease try again:");
				}
			} catch (AppException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
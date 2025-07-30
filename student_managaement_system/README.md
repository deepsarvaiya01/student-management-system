# Student Management System - Fees Management Module

## Overview
The Student Management System now includes a comprehensive **Fees Management Module** that allows administrators to track and manage student fees efficiently, including a **Payment Processing System** with multiple payment methods.

## Features

### 💰 Fees Management Features

1. **View Total Paid Fees** - Shows the total amount of fees collected from all students
2. **View Total Pending Fees** - Displays the total amount of fees still pending from all students
3. **View Fees By Student** - Shows detailed fee information for a specific student
4. **View Fees By Course** - Displays fee information for all students enrolled in a specific course
5. **Update Fees Of A Course** - Allows updating the total fee amount for a course (affects all enrolled students)
6. **Total Earning** - Shows the total earning potential from all enrolled students

### 💳 Payment Processing Features

7. **Pay Fees** - Process fee payments with multiple payment methods:
   - **Cash Payment** - Handle cash transactions with change calculation
   - **Card Payment** - Process card payments with validation (card number, expiry, CVV, cardholder name)
   - **UPI Payment** - Process UPI payments with UPI ID and mobile number validation

## Database Schema

The fees management system uses the following key tables:

### `fees` Table
- `fee_id` - Primary key
- `student_course_id` - Foreign key to student_courses
- `paid_amount` - Amount already paid by student
- `pending_amount` - Amount still pending
- `total_fee` - Total fee for the course
- `last_payment_date` - Date of last payment

### `courses` Table
- `course_id` - Primary key
- `course_name` - Name of the course
- `no_of_semester` - Number of semesters
- `total_fee` - Total fee for the course

## How to Use

### Running the Application
1. Ensure your database is set up with the provided schema
2. Configure your `.env` file with database credentials
3. Run the `Main.java` file
4. Select option **1. Student Management** for payment processing or **3. Fees Management** for fee tracking

### Menu Options

#### 1. View Total Paid Fees
- Displays the sum of all paid amounts across all students
- Shows the total revenue collected

#### 2. View Total Pending Fees
- Shows the sum of all pending amounts
- Helps identify outstanding payments

#### 3. View Fees By Student
- Lists all available students
- Enter a student ID to view their fee details
- Shows course name, total fee, paid amount, pending amount, and last payment date

#### 4. View Fees By Course
- Lists all available courses with their current fees
- Enter a course ID to view all students enrolled in that course
- Shows individual student fee details for the selected course

#### 5. Update Fees Of A Course
- Lists all available courses
- Enter a course ID and new total fee amount
- Automatically updates pending amounts for all enrolled students
- **Note**: This affects all current and future students in the course

#### 6. Total Earning
- Shows the total earning potential from all enrolled students
- Represents the sum of all total fees across all enrollments

#### 7. Pay Fees (Student Management)
- **Select Student**: Choose from available students
- **View Current Status**: See current fee status with paid/pending amounts
- **Enter Payment Amount**: Specify the amount to pay (cannot exceed pending amount)
- **Choose Payment Method**:
  - **Cash**: Enter received amount, system calculates change
  - **Card**: Enter card details (16-digit number, MM/YY expiry, 3-digit CVV, cardholder name)
  - **UPI**: Enter UPI ID (format: name@bank) and mobile number
- **Validation**: All inputs are validated for format and business rules
- **Processing**: Simulated payment processing with realistic delays
- **Update**: Automatically updates fee records and shows updated status

## Technical Implementation

### Architecture
- **Model Layer**: `Fee.java` - Data model for fee information
- **DAO Layer**: `FeeDao.java` - Database operations for fees
- **Service Layer**: `FeeService.java` - Business logic
- **Controller Layer**: `FeeController.java` - User interface handling
- **Main**: `FeeMain.java` - Menu interface

### Key Features
- **BigDecimal** for precise financial calculations
- **Input validation** for all user inputs with comprehensive format checking
- **Error handling** with user-friendly messages
- **Tabular display** for better data visualization
- **Transaction management** for data consistency
- **Payment processing** with multiple payment methods
- **Real-time fee updates** with automatic pending amount calculations

## Sample Data
The system comes with sample data including:
- 5 students with different enrollments
- 5 courses with varying fee structures
- Fee records with paid and pending amounts
- Realistic payment dates

## Security Features
- Prepared statements to prevent SQL injection
- Input validation for all user inputs with format checking
- Environment variable configuration for database credentials
- Transaction management for data integrity
- Payment method validation (card number, CVV, expiry date, UPI ID)
- Secure fee amount validation and processing

## Future Enhancements
- Real payment gateway integration
- Fee installment management
- Late fee calculations
- Payment reminders and notifications
- Financial reporting and analytics
- Receipt generation
- Payment history tracking
- Multiple currency support 
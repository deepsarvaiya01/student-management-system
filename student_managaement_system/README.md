# Student Management System - Fees Management Module

## Overview
The Student Management System now includes a comprehensive **Fees Management Module** that allows administrators to track and manage student fees efficiently.

## Features

### 💰 Fees Management Features

1. **View Total Paid Fees** - Shows the total amount of fees collected from all students
2. **View Total Pending Fees** - Displays the total amount of fees still pending from all students
3. **View Fees By Student** - Shows detailed fee information for a specific student
4. **View Fees By Course** - Displays fee information for all students enrolled in a specific course
5. **Update Fees Of A Course** - Allows updating the total fee amount for a course (affects all enrolled students)
6. **Total Earning** - Shows the total earning potential from all enrolled students

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
4. Select option **3. Fees Management** from the main menu

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

## Technical Implementation

### Architecture
- **Model Layer**: `Fee.java` - Data model for fee information
- **DAO Layer**: `FeeDao.java` - Database operations for fees
- **Service Layer**: `FeeService.java` - Business logic
- **Controller Layer**: `FeeController.java` - User interface handling
- **Main**: `FeeMain.java` - Menu interface

### Key Features
- **BigDecimal** for precise financial calculations
- **Input validation** for all user inputs
- **Error handling** with user-friendly messages
- **Tabular display** for better data visualization
- **Transaction management** for data consistency

## Sample Data
The system comes with sample data including:
- 5 students with different enrollments
- 5 courses with varying fee structures
- Fee records with paid and pending amounts
- Realistic payment dates

## Security Features
- Prepared statements to prevent SQL injection
- Input validation for all user inputs
- Environment variable configuration for database credentials
- Transaction management for data integrity

## Future Enhancements
- Payment processing integration
- Fee installment management
- Late fee calculations
- Payment reminders
- Financial reporting and analytics 
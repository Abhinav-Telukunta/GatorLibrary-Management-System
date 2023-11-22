### GatorLibrary-Management-System

The Gator Library Management System is a Java-based application designed to efficiently manage book operations, reservations, and patron interactions within a library setting. The system leverages a Red-Black Tree data structure for organized book storage and a Min Heap for priority-based patron reservations.

### Key Features

Red-Black Tree: Organizes books for efficient search and management.
Min Heap: Facilitates priority-based reservations to optimize patron access.
Book Operations: Borrowing, returning, searching, and more for seamless library functionality.
Color Flip Tracking: Monitors Red-Black Tree color flips to ensure data structure integrity.
User-Friendly Commands: A simple and intuitive command-line interface for easy interaction.

### Sample Input:

InsertBook(4, "Book4", "Author1", "Yes")
InsertBook(2, "Book2", "Author1", "Yes")
BorrowBook(2001, 2, 3)
InsertBook(5, "Book5", "Author3", "Yes")
BorrowBook(3002, 2, 1)
PrintBook(2)
BorrowBook(3002, 5, 1)
BorrowBook(1003, 2, 4)
BorrowBook(2010, 4, 2)
PrintBooks(2, 3)
BorrowBook(2010, 2, 2)
BorrowBook(1004, 2, 4)
ReturnBook(2001, 2)
ReturnBook(2010, 4)
FindClosestBook(2)
InsertBook(3, "Book3", "Author4", "Yes")
DeleteBook(2)
ColorFlipCount()
Quit()
PrintBook(4)
BorrowBook(2)
ReturnBook(1003, 2)

### Sample Output:

Book 2 Borrowed by Patron 2001

Book 2 Reserved by Patron 3002

BookID = 2
Title = "Book2"
Author = "Author1"
Availability = "No"
BorrowedBy = 2001
Reservations = [3002]

Book 5 Borrowed by Patron 3002

Book 2 Reserved by Patron 1003

Book 4 Borrowed by Patron 2010

BookID = 2
Title = "Book2"
Author = "Author1"
Availability = "No"
BorrowedBy = 2001
Reservations = [3002,1003]

Book 2 Reserved by Patron 2010

Book 2 Reserved by Patron 1004

Book 2 Returned by Patron 2001

Book 2 Allotted to Patron 3002

Book 4 Returned by Patron 2010

BookID = 2
Title = "Book2"
Author = "Author1"
Availability = "No"
BorrowedBy = 3002
Reservations = [2010,1003,1004]

Book 2 is no longer available. Reservations made by Patrons 2010,1003,1004 have been cancelled!

Colour Flip Count: 3

Program Terminated!!

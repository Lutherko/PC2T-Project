import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = DbContext.getConnection();
            System.out.println("Connected to the database.");

            LibraryManagment libraryManagment = new LibraryManagment(connection);

            int choice;
            do {
                System.out.println("Menu:");
                System.out.println("1. Add new Book");
                System.out.println("2. Adjust Book");
                System.out.println("3. Delete Book");
                System.out.println("4. Select Borrowed/Returned book");
                System.out.println("5. Book list");
                System.out.println("6. Search for Book");
                System.out.println("7. List of books by Author in chronological order");
                System.out.println("8. List of books by Genre");
                System.out.println("9. List of borrowed books");
                System.out.println("10. Save Book into the file");
                System.out.println("11. Load Book from the file");
                System.out.println("12. Exit App");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        libraryManagment.addBook();
                        break;
                    case 2:
                        libraryManagment.editBook();
                        break;
                    case 3:
                        libraryManagment.deleteBook();
                        break;
                    case 4:
                        libraryManagment.markBookBorrowedReturned();
                        break;
                    case 5:
                        libraryManagment.listBooks();
                        break;
                    case 6:
                        System.out.print("Enter the title of the book: ");
                        scanner.nextLine();
                        String bookTitle = scanner.nextLine();
                        try {
                            libraryManagment.searchBook(bookTitle);
                        } catch (SQLException e) {
                            System.err.println("Error searching for book: " + e.getMessage());
                        }
                        break;
                    case 7:
                        System.out.print("Enter author name: ");
                        scanner.nextLine();
                        String authorName = scanner.nextLine();
                        libraryManagment.listBooksByAuthor(authorName);
                        break;
                    case 8:
                        System.out.print("Enter genre name: ");
                        scanner.nextLine();
                        String genreName = scanner.nextLine();
                        libraryManagment.listBooksByGenre(genreName);
                        break;
                    case 9:
                        libraryManagment.listBorrowedBooks();
                        break;
                    case 10:
                        System.out.print("Enter the title of the book: ");
                        scanner.nextLine();
                        String bookTitleFromUser = scanner.nextLine();
                        try {
                            libraryManagment.saveBookToFile(bookTitleFromUser);
                        } catch (IOException e) {
                            System.err.println("Error saving book information to file: " + e.getMessage());
                        }
                        break;
                    case 11:
                        System.out.print("Enter the name of the file to load books from: ");
                        scanner.nextLine();
                        String fileName = scanner.nextLine();
                        try {
                            libraryManagment.loadBooksFromFile(fileName);
                        } catch (IOException e) {
                            System.err.println("Error loading books from file: " + e.getMessage());
                        }
                        break;
                    case 12:
                        System.out.println("Exiting application...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 12);

            connection.close();
            System.out.println("Disconnected from the database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}



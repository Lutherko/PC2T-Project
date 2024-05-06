import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryManagment {
    private Connection connection;
    private Scanner scanner;

    public LibraryManagment(Connection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);

    }

    public void addBook() throws SQLException {
        System.out.print("Enter title of the book: ");
        String title = scanner.nextLine();

        System.out.print("Enter author's name: ");
        String author = scanner.nextLine();

        System.out.print("Enter publication year: ");
        int publicationYear = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Is the book available? (true/false): ");
        boolean availability = scanner.nextBoolean();
        scanner.nextLine();

        System.out.print("Is the book a novel or a textbook? (novel/textbook): ");
        String bookType = scanner.nextLine();

        String genre = null;
        int recommendedGrade = 0;

        if (bookType.equalsIgnoreCase("novel")) {
            System.out.print("Enter the genre of the novel: ");
            genre = scanner.nextLine();
        } else if (bookType.equalsIgnoreCase("textbook")) {
            System.out.print("Enter the recommended grade: ");
            recommendedGrade = scanner.nextInt();
            scanner.nextLine();
        } else {
            System.out.println("Invalid book type entered.");
            return;
        }
        String query = "INSERT INTO Books (title, author, publication_year, availability, book_type, genre, recommended_grade) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setInt(3, publicationYear);
            statement.setBoolean(4, availability);
            statement.setString(5, bookType);
            statement.setString(6, genre);
            statement.setInt(7, recommendedGrade);
            statement.executeUpdate();
            System.out.println("Book added successfully.");
        }

    }

    public void editBook() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book you want to edit: ");
        String title = scanner.nextLine();


        Book book = findBookByTitle(title);
        if (book == null) {
            System.out.println("Book with the title '" + title + "' not found.");
            return;
        }

        System.out.println("Select which property of the book you want to edit:");
        System.out.println("1. Change author");
        System.out.println("2. Change publication year");
        System.out.println("3. Change availability");
        System.out.print("Enter your choice: ");
        int propertyChoice = scanner.nextInt();
        scanner.nextLine();

        switch (propertyChoice) {
            case 1:
                System.out.print("Enter new author: ");
                String newAuthor = scanner.nextLine();
                updateBookAuthor(title, newAuthor);
                break;
            case 2:
                System.out.print("Enter new publication year: ");
                int newPublicationYear = scanner.nextInt();
                scanner.nextLine();
                updateBookPublicationYear(title, newPublicationYear);
                break;
            case 3:
                System.out.print("Is the book available? (true/false): ");
                boolean newAvailability = scanner.nextBoolean();
                scanner.nextLine();
                updateBookAvailability(title, newAvailability);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private Book findBookByTitle(String title) throws SQLException {
        String query = "SELECT * FROM Books WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getInt("publication_year"),
                        resultSet.getBoolean("availability"),
                        resultSet.getString("book_type"),
                        resultSet.getString("genre"),
                        resultSet.getInt("recommended_grade")
                );
            }
        }
        return null;
    }

    private void updateBookAuthor(String title, String newAuthor) throws SQLException {
        String query = "UPDATE Books SET author = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newAuthor);
            statement.setString(2, title);
            statement.executeUpdate();
            System.out.println("Author updated successfully.");
        }
    }

    private void updateBookPublicationYear(String title, int newPublicationYear) throws SQLException {
        String query = "UPDATE Books SET publication_year = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newPublicationYear);
            statement.setString(2, title);
            statement.executeUpdate();
            System.out.println("Publication year updated successfully.");
        }
    }

    private void updateBookAvailability(String title, boolean newAvailability) throws SQLException {
        String query = "UPDATE Books SET availability = ? WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, newAvailability);
            statement.setString(2, title);
            statement.executeUpdate();
            System.out.println("Availability updated successfully.");
        }
    }

    public void deleteBook() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book you want to delete: ");
        String title = scanner.nextLine();


        Book book = findBookByTitle(title);
        if (book == null) {
            System.out.println("Book with the title '" + title + "' not found.");
            return;
        }


        deleteBookByTitle(title);
        System.out.println("Book with the title '" + title + "' deleted successfully.");
    }

    private void deleteBookByTitle(String title) throws SQLException {
        String query = "DELETE FROM Books WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.executeUpdate();
        }
    }

    public void markBookBorrowedReturned() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book: ");
        String title = scanner.nextLine();


        Book book = findBookByTitle(title);
        if (book == null) {
            System.out.println("Book with the title '" + title + "' not found.");
            return;
        }


        System.out.println("Book '" + title + "' is currently " + (book.isAvailable() ? "available." : "borrowed."));
        System.out.println("Do you want to change the availability?");
        System.out.println("1. Mark as borrowed");
        System.out.println("2. Mark as returned");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                updateBookAvailability(title, false);
                System.out.println("Book '" + title + "' marked as borrowed.");
                break;
            case 2:
                updateBookAvailability(title, true);
                System.out.println("Book '" + title + "' marked as returned.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    public void listBooks() throws SQLException {
        String query = "SELECT * FROM Books ORDER BY title";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("List of books:");
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String bookType = resultSet.getString("book_type");
                int publicationYear = resultSet.getInt("publication_year");
                boolean availability = resultSet.getBoolean("availability");
                String genre = resultSet.getString("genre");
                int recommendedGrade = resultSet.getInt("recommended_grade");

                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Book Type: " + bookType);

                if ("novel".equalsIgnoreCase(bookType)) {
                    if (genre != null && !genre.isEmpty()) {
                        System.out.println("Genre: " + genre);
                    } else {
                        System.out.println("Genre: Not specified");
                    }
                } else if ("textbook".equalsIgnoreCase(bookType)) {
                    if (recommendedGrade != 0) {
                        System.out.println("Recommended Grade: " + recommendedGrade);
                    } else {
                        System.out.println("Recommended Grade: Not specified");
                    }
                }

                System.out.println("Publication Year: " + publicationYear);
                System.out.println("Availability: " + (availability ? "Available" : "Borrowed"));
                System.out.println();
            }
        }
    }

    public void searchBook(String bookTitle) throws SQLException {
        String query = "SELECT * FROM Books WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookTitle);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String bookType = resultSet.getString("book_type");
                    int publicationYear = resultSet.getInt("publication_year");
                    boolean availability = resultSet.getBoolean("availability");
                    String genre = resultSet.getString("genre");
                    int recommendedGrade = resultSet.getInt("recommended_grade");

                    System.out.println("Book found:");
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Book Type: " + bookType);

                    if ("novel".equalsIgnoreCase(bookType)) {
                        if (genre != null && !genre.isEmpty()) {
                            System.out.println("Genre: " + genre);
                        } else {
                            System.out.println("Genre: Not specified");
                        }
                    } else if ("textbook".equalsIgnoreCase(bookType)) {
                        if (recommendedGrade != 0) {
                            System.out.println("Recommended Grade: " + recommendedGrade);
                        } else {
                            System.out.println("Recommended Grade: Not specified");
                        }
                    }

                    System.out.println("Publication Year: " + publicationYear);
                    System.out.println("Availability: " + (availability ? "Available" : "Borrowed"));
                } else {
                    System.out.println("Book not found.");
                }
            }
        }
    }

    public void listBooksByAuthor(String authorName) throws SQLException {
        String query = "SELECT title FROM Books WHERE author = ? ORDER BY publication_year";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, authorName);
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Books by author " + authorName + " in chronological order:");
                boolean found = false;
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("title"));
                    found = true;
                }

                if (!found) {
                    System.out.println("No books found for author: " + authorName);
                }
            }
        }
    }

    private Book extractBookFromResultSet(ResultSet resultSet) throws SQLException {
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        int publicationYear = resultSet.getInt("publication_year");
        boolean availability = resultSet.getBoolean("availability");
        String bookType = resultSet.getString("book_type");
        String genre = resultSet.getString("genre");
        int recommendedGrade = resultSet.getInt("recommended_grade");

        return new Book(title, author, publicationYear, availability, bookType, genre, recommendedGrade);
    }

    public void listBooksByGenre(String genreName) throws SQLException {
        String query = "SELECT * FROM Books WHERE genre = ? ORDER BY title";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, genreName);
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Books in genre " + genreName + ":");
                boolean found = false;
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("title"));
                    found = true;
                }

                if (!found) {
                    System.out.println("No books found for genre: " + genreName);
                }
            }
        }
    }

    public void listBorrowedBooks() throws SQLException {
        String query = "SELECT title, book_type FROM Books WHERE availability = false";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("Borrowed books:");
            boolean found = false;
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String bookType = resultSet.getString("book_type");
                System.out.println("Title: " + title + ", Type: " + bookType);
                found = true;
            }

            if (!found) {
                System.out.println("No borrowed books found.");
            }
        }
    }

    public void saveBookToFile(String title) throws SQLException, IOException {
        String query = "SELECT * FROM Books WHERE title = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String author = resultSet.getString("author");
                    int publicationYear = resultSet.getInt("publication_year");
                    boolean availability = resultSet.getBoolean("availability");
                    String bookType = resultSet.getString("book_type");
                    String genre = resultSet.getString("genre");
                    int recommendedGrade = resultSet.getInt("recommended_grade");


                    String fileName = "books.txt";
                    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
                        writer.println("Title: " + title);
                        writer.println("Author: " + author);
                        writer.println("Publication Year: " + publicationYear);
                        writer.println("Availability: " + (availability ? "Available" : "Borrowed"));
                        writer.println("Book Type: " + bookType);
                        if (bookType.equals("novel")) {
                            writer.println("Genre: " + genre);
                        } else if (bookType.equals("textbook")) {
                            writer.println("Recommended Grade: " + recommendedGrade);
                        }
                        writer.println();
                    }
                    System.out.println("Book information saved to file: " + fileName);
                } else {
                    System.out.println("Book not found in the database.");
                }
            }
        }
    }
    public void loadBooksFromFile(String fileName) throws SQLException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title:")) {
                    String title = line.substring("Title:".length()).trim();
                    String author = reader.readLine().substring("Author:".length()).trim();
                    int publicationYear = Integer.parseInt(reader.readLine().substring("Publication Year:".length()).trim());
                    boolean availability = reader.readLine().substring("Availability:".length()).trim().equals("Available");
                    String bookType = reader.readLine().substring("Book Type:".length()).trim();
                    String genre = null;
                    int recommendedGrade = 0;

                    if (bookType.equals("novel")) {
                        genre = reader.readLine().substring("Genre:".length()).trim();
                    } else if (bookType.equals("textbook")) {
                        recommendedGrade = Integer.parseInt(reader.readLine().substring("Recommended Grade:".length()).trim());
                    }

                    String query = "INSERT INTO Books (title, author, publication_year, availability, book_type, genre, recommended_grade) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, title);
                        statement.setString(2, author);
                        statement.setInt(3, publicationYear);
                        statement.setBoolean(4, availability);
                        statement.setString(5, bookType);
                        statement.setString(6, genre);
                        statement.setInt(7, recommendedGrade);
                        statement.executeUpdate();
                    }
                }
            }
            System.out.println("Books loaded from file successfully.");
        }
    }
}



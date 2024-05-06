public class Book {
    private String title;
    private String author;
    private int publicationYear;
    private boolean availability;
    private String bookType;
    private String genre;
    private int recommendedGrade;

    public Book(String title, String author, int publicationYear, boolean availability, String bookType, String genre, int recommendedGrade) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availability = availability;
        this.bookType = bookType;
        this.genre = genre;
        this.recommendedGrade = recommendedGrade;
    }

    public boolean isAvailable() {
        return availability;
    }

}
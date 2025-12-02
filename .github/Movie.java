import java.util.List;

public class Movie {
    private String title;
    private List<String> categories;
    private int rating;

    public Movie(String title, List<String> categories, int rating) {
        this.title = title;
        this.categories = categories;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return title + " (" + String.join(", ", categories) + ") - " + rating + "/5";
    }
}

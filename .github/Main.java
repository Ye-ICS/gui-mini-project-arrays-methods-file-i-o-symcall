import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class Main extends Application {

    private HashMap<String, ArrayList<Movie>> moviesByCategory = new HashMap<>();
    private final String[] CATEGORY_LIST = {
            "Action", "Romance", "Horror", "Comedy", "Kids",
            "Drama", "Sci-Fi", "Fantasy", "Documentary"
    };

    @Override
    public void start(Stage primaryStage) {

        // Initialize categories
        for (String c : CATEGORY_LIST) {
            moviesByCategory.put(c, new ArrayList<>());
        }

        // Load saved movies
        loadMoviesFromFile();

        // -----------------------------
        //        HOME SCREEN
        // -----------------------------
        Button addBtn = new Button("Add Movie");
        Button searchBtn = new Button("Find Movie");

        addBtn.setPrefWidth(200);
        searchBtn.setPrefWidth(200);

        VBox homeLayout = new VBox(20, addBtn, searchBtn);
        homeLayout.setAlignment(Pos.CENTER);
        homeLayout.setPadding(new Insets(30));
        homeLayout.setStyle("-fx-background-color: #f6f1ff;"); // cute lilac pastel

        Scene homeScene = new Scene(homeLayout, 500, 400);


        // -----------------------------
        //        ADD MOVIE SCREEN
        // -----------------------------
        Label titleLabel = new Label("Movie Title:");
        TextField titleField = new TextField();

        Label catLabel = new Label("Categories:");
        VBox categoryChecks = new VBox(5);
        ArrayList<CheckBox> catBoxes = new ArrayList<>();

        for (String c : CATEGORY_LIST) {
            CheckBox cb = new CheckBox(c);
            catBoxes.add(cb);
            categoryChecks.getChildren().add(cb);
        }

        Label ratingLabel = new Label("Rating (1-5):");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 3);

        Button saveBtn = new Button("Save Movie");
        Button backBtn = new Button("Back");

        VBox addLayout = new VBox(12,
                titleLabel, titleField,
                catLabel, categoryChecks,
                ratingLabel, ratingSpinner,
                saveBtn, backBtn
        );

        addLayout.setPadding(new Insets(20));
        addLayout.setStyle("-fx-background-color: #fff7fb;"); 
        Scene addScene = new Scene(addLayout, 500, 500);


        //        SEARCH MOVIE SCREEN
        TextField searchField = new TextField();
        searchField.setPromptText("Search by title...");

        ListView<Movie> searchResults = new ListView<>();

        Button back2Btn = new Button("Back");

        VBox searchLayout = new VBox(10,
                searchField,
                searchResults,
                back2Btn
        );
        searchLayout.setPadding(new Insets(20));
        searchLayout.setStyle("-fx-background-color: #f0fbff;");

        Scene searchScene = new Scene(searchLayout, 500, 500);


        // -----------------------------
        //          BUTTON ACTIONS
        // -----------------------------

        addBtn.setOnAction(e -> primaryStage.setScene(addScene));
        searchBtn.setOnAction(e -> primaryStage.setScene(searchScene));

        backBtn.setOnAction(e -> primaryStage.setScene(homeScene));
        back2Btn.setOnAction(e -> primaryStage.setScene(homeScene));

        // SAVE MOVIE
        saveBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            ArrayList<String> selectedCats = new ArrayList<>();

            for (CheckBox cb : catBoxes) {
                if (cb.isSelected()) selectedCats.add(cb.getText());
            }

            if (title.isEmpty() || selectedCats.isEmpty()) {
                showAlert("Error", "Please type a title and choose at least one category.");
                return;
            }

            int rating = ratingSpinner.getValue();
            Movie movie = new Movie(title, selectedCats, rating);

            for (String c : selectedCats) {
                moviesByCategory.get(c).add(movie);
            }

            titleField.clear();
            for (CheckBox cb : catBoxes) cb.setSelected(false);
            ratingSpinner.getValueFactory().setValue(3);

            showAlert("Saved!", "Your movie was added!");
        });


        // SEARCH FUNCTION
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            searchResults.getItems().clear();

            String query = newValue.toLowerCase();

            if (query.isEmpty()) return;

            HashSet<Movie> matches = new HashSet<>();

            for (String cat : moviesByCategory.keySet()) {
                for (Movie m : moviesByCategory.get(cat)) {
                    if (m.getTitle().toLowerCase().contains(query)) {
                        matches.add(m);
                    }
                }
            }

            searchResults.getItems().addAll(matches);
        });


        // Save on close
        primaryStage.setOnCloseRequest(e -> saveMoviesToFile());

        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Movie Organizer");
        primaryStage.show();
    }


    // ------------------------------------------------------------
    //                  FILE SAVE + LOAD
    // ------------------------------------------------------------

    private void loadMoviesFromFile() {
        File file = new File("movies.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;

                String title = parts[0];
                List<String> categories = Arrays.asList(parts[1].split(","));
                int rating = Integer.parseInt(parts[2]);

                Movie movie = new Movie(title, categories, rating);

                for (String c : categories) {
                    if (moviesByCategory.containsKey(c)) {
                        moviesByCategory.get(c).add(movie);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading movie file.");
        }
    }

    private void saveMoviesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("movies.txt"))) {

            HashSet<Movie> saved = new HashSet<>();

            for (String category : moviesByCategory.keySet()) {
                for (Movie movie : moviesByCategory.get(category)) {

                    if (saved.contains(movie)) continue;

                    String line = movie.getTitle() + "|" +
                            String.join(",", movie.getCategories()) + "|" +
                            movie.getRating();

                    writer.write(line);
                    writer.newLine();

                    saved.add(movie);
                }
            }

        } catch (Exception e) {
            System.out.println("Error saving movie file.");
        }
    }


    // ------------------------------------------------------------
    //                    SMALL HELPER
    // ------------------------------------------------------------

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
         a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

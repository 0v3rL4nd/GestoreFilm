package app;

// --- PATTERN: FACADE ---
class MovieSystemFacade {
    private MovieLibrary library;

    public MovieSystemFacade() {
        this.library = MovieLibrary.getInstance();
    }

    // Metodo semplificato per aggiungere film (Usa Builder + Command)
    public void addNewMovie(String title, String director, int year, int rating) {
        Movie m = new Movie.MovieBuilder()
                .setTitle(title)
                .setDirector(director)
                .setYear(year)
                .setRating(rating)
                .build();

        Command addCmd = new AddMovieCommand(m);
        addCmd.execute();
    }

    public void removeMovie(Movie m) {
        Command removeCmd = new RemoveMovieCommand(m);
        removeCmd.execute();
    }

    public void changeSortOrder(SortStrategy strategy) {
        library.sortMovies(strategy);
    }

    public void triggerSave() {
        library.save();
    }

    // Registra la vista (Observer)
    public void registerView(Observer view) {
        library.attach(view);
    }
}
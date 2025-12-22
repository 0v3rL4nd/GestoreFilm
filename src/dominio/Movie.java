package dominio;

// --- CLASSE PRINCIPALE ---
class Movie {
    private String title;
    private String director;
    private int year;
    private int rating; // 1-5
    private MovieState state; // Riferimento al Pattern State

    // Costruttore privato
    private Movie(MovieBuilder builder) {
        this.title = builder.title;
        this.director = builder.director;
        this.year = builder.year;
        this.rating = builder.rating;
        this.state = new ToWatchState(); // Stato default
    }

    public void setState(MovieState state) { this.state = state; }
    public void play() { state.handlePlay(this); }

    @Override
    public String toString() {
        return String.format("'%s' (%d) di %s | Voto: %d | Stato: %s",
                title, year, director, rating, state.getStateName());
    }

    public String getTitle() { return title; }
    public int getYear() { return year; }
    public int getRating() { return rating; }

    // --- BUILDER ---
    public static class MovieBuilder {
        private String title;
        private String director;
        private int year;
        private int rating;

        public MovieBuilder setTitle(String title) { this.title = title; return this; }
        public MovieBuilder setDirector(String director) { this.director = director; return this; }
        public MovieBuilder setYear(int year) { this.year = year; return this; }
        public MovieBuilder setRating(int rating) { this.rating = rating; return this; }

        public Movie build() {
            return new Movie(this);
        }
    }
}
package app;

import java.time.Year;
import java.util.GregorianCalendar;

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

    //SETTERS
    public void setTitle(String title) { this.title = title;}
    public void setDirector(String director) {this.director = director;}
    public void setYear(int year) {this.year = year;}
    public void setRating(int rating) {this.rating = rating;}

    public void setState(MovieState state) { this.state = state; }
    public void play() { state.handlePlay(this); }

    @Override
    public String toString() {
        return String.format("'%s' (%d) di %s | Voto: %d | Stato: %s",
                title, year, director, rating, state.getStateName());
    }

    //GETTERS
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public int getRating() { return rating; }
    public String getDirector() { return director; }
    public String getStateName() { return state.getStateName(); }

    public void restoreStateFromData(String stateName) {
        switch (stateName) {
            case "Visto": this.state = new WatchedState(); break;
            case "In Visione": this.state = new WatchingState(); break;
            case "Da Vedere": default: this.state = new ToWatchState(); break;
        }
    }

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

        public Movie build() throws IllegalArgumentException {
            if ( title == null || title.trim().isEmpty())
                throw new IllegalArgumentException("Il titolo non può essere vuoto");
            if( director == null || director.trim().isEmpty())
                throw new IllegalArgumentException("Il regista non può essere vuoto");
            if( year > Year.now().getValue() )
                throw new IllegalArgumentException("L'anno non è valido");
            if( year < 1888)
                throw new IllegalArgumentException("L'anno preclude il vincolo di sanità storica");
            return new Movie(this);
        }
    }
}
package app;

import java.util.List;

// --- PATTERN: FACADE ---
class MovieSystemFacade {
    private MovieLibrary library;

    public MovieSystemFacade() {
        this.library = MovieLibrary.getInstance();
    }

    // Metodo per aggiungere film (Usa Builder + Command)
    public String addNewMovie(String title, String director, String yearStr, int rating) {
        if (yearStr == null || yearStr.length() != 4) {
            return "L'anno deve essere composto esattamente da 4 cifre!";
        }

        try {
            int year = Integer.parseInt(yearStr);

            // Passiamo i dati al Builder (che farà il controllo sull'anno corrente)
            Movie m = new Movie.MovieBuilder()
                    .setTitle(title)
                    .setDirector(director)
                    .setYear(year)
                    .setRating(rating)
                    .build();

            Command addCmd = new AddMovieCommand(m);
            addCmd.execute();
            return null; // Successo

        } catch (NumberFormatException e) {
            return "L'anno deve contenere solo numeri!";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); // Ritorna l'errore del Builder (es. Anno futuro)
        }
    }

    // Metodo Modifica
    public String editMovie(Movie movie, String title, String director, String yearStr, int rating) {
        try {
            int year = Integer.parseInt(yearStr);

            // Possiamo usare il builder solo per validare i dati prima di creare il comando
            // Trucco: creiamo un movie "dummy" solo per vedere se il Builder accetta i dati
            new Movie.MovieBuilder()
                    .setTitle(title).setDirector(director).setYear(year).setRating(rating).build();

            Command editCmd = new EditMovieCommand(movie, title, director, year, rating);
            editCmd.execute();
            return null;

        } catch (NumberFormatException e) {
            return "L'anno deve essere un numero!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public void removeMovie(Movie m) {
        Command removeCmd = new RemoveMovieCommand(m);
        removeCmd.execute();
    }

    // Metodo Ricerca
    public List<Movie> searchMovies(String query) {
        return library.search(query);
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

    public void advanceState(Movie m) {
        if (m != null) {
            m.play(); // Cambia lo stato interno (Da Vedere -> In Visione)
            // FONDAMENTALE: Diciamo alla libreria di notificare la GUI
            library.notifyObservers();

            //implementazione auto-save
            library.save();

        }
    }
}
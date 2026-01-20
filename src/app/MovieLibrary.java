package app;

import java.util.ArrayList;
import java.util.List;

// --- PATTERN: SINGLETON (Il Subject) ---
class MovieLibrary {

    private static MovieLibrary instance;
    private List<Movie> movies;
    private List<Observer> observers;

    // Pattern Strategy per il salvataggio
    private StorageStrategy storageStrategy;

    private MovieLibrary() {
        // Inizializza liste
        movies = new ArrayList<>();
        observers = new ArrayList<>();

        // Imposta la strategia reale
        storageStrategy = new JsonStorageStrategy();

        // CARICAMENTO AUTOMATICO ALL'AVVIO
        // Questo popola la lista 'movies' leggendo dal file fisico se esiste
        List<Movie> loadedData = storageStrategy.load();
        if (loadedData != null) {
            movies.addAll(loadedData);
        }
    }

    public static synchronized MovieLibrary getInstance() {
        if (instance == null) instance = new MovieLibrary();
        return instance;
    }

    public void addMovie(Movie m) {
        movies.add(m);
        notifyObservers();
    }

    public void removeMovie(Movie m) {
        movies.remove(m);
        notifyObservers();
    }

    public void sortMovies(SortStrategy strategy) {
        strategy.sort(movies);
        notifyObservers();
    }

    public void save() {
        storageStrategy.save(movies);
    }

    // Gestione Observer
    public void attach(Observer o) {
        observers.add(o);
    }

    private void notifyObservers() {
        for (Observer o : observers) o.update(movies);
    }

    public List<Movie> getMovies() {
        return movies;
    }

    // Metodo helper per i test
    public void clearDataForTesting() {
        movies.clear();
        // Non cancelliamo gli observer per non rompere i collegamenti,
    }
}

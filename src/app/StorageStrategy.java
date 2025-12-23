package app;

import java.util.List;

// --- STRATEGY (Persistenza) ---
interface StorageStrategy {
    void save(List<Movie> movies);

    List<Movie> load();
}

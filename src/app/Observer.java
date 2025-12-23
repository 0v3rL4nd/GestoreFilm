package app;

import java.util.List;

// --- PATTERN: OBSERVER ---
interface Observer {
    void update(List<Movie> movies);
}

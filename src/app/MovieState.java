package app;

// --- STATE ---
// Interfaccia per lo stato del film
interface MovieState {
    void handlePlay(Movie context);

    String getStateName();
}

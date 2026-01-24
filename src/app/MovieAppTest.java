package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieAppTest {

    private MovieSystemFacade facade;
    private MovieLibrary library;

    @BeforeEach
    void setUp() {
        // 1. Inizializza il sistema
        facade = new MovieSystemFacade();
        library = MovieLibrary.getInstance();

        // 2. IMPORTANTE: Pulisce la memoria da esecuzioni precedenti
        // (Assicurati di aver aggiunto il metodo resetForTesting in MovieLibrary come discusso prima)
        library.resetForTesting();

        // 3. IMPORTANTE: Imposta la strategia Dummy per proteggere il file reale
        // (Assicurati di aver aggiunto il metodo setStorageStrategy in MovieLibrary)
        library.setStorageStrategy(new DummyStorageStrategy());
    }

    // --- TEST POSITIVI (Happy Path) ---

    @Test
    @DisplayName("Dovrebbe aggiungere correttamente un film valido")
    void testAggiuntaFilm() {
        String result = facade.addNewMovie("Inception", "Nolan", "2010", 5);

        assertNull(result, "Il metodo deve ritornare null se l'inserimento ha successo");
        assertEquals(1, library.getMovies().size(), "La libreria deve contenere 1 film");

        Movie m = library.getMovies().get(0);
        assertEquals("Inception", m.getTitle());
        assertEquals("Da Vedere", m.getStateName(), "Lo stato iniziale deve essere 'Da Vedere'");
    }

    @Test
    @DisplayName("Dovrebbe modificare i dati di un film esistente")
    void testModificaFilm() {
        // Setup
        facade.addNewMovie("Old Title", "Old Director", "2000", 1);
        Movie m = library.getMovies().get(0);

        // Esecuzione
        String result = facade.editMovie(m, "New Title", "New Director", "2022", 5);

        // Verifiche
        assertNull(result);
        assertEquals("New Title", m.getTitle());
        assertEquals("New Director", m.getDirector());
        assertEquals(2022, m.getYear());
        assertEquals(5, m.getRating());
    }

    @Test
    @DisplayName("Dovrebbe avanzare lo stato di visione (State Pattern)")
    void testCambioStato() {
        facade.addNewMovie("Dune", "Villeneuve", "2021", 5);
        Movie m = library.getMovies().get(0);

        assertEquals("Da Vedere", m.getStateName());

        // Primo Play -> In Visione
        facade.advanceState(m);
        assertEquals("In Visione", m.getStateName());

        // Secondo Play -> Visto
        facade.advanceState(m);
        assertEquals("Visto", m.getStateName());
    }

    @Test
    @DisplayName("Dovrebbe cercare e filtrare i film corretti")
    void testRicerca() {
        facade.addNewMovie("Star Wars", "Lucas", "1977", 5);
        facade.addNewMovie("Star Trek", "Roddenberry", "1979", 4);
        facade.addNewMovie("Titanic", "Cameron", "1997", 3);

        // Cerca "Star"
        List<Movie> results = facade.searchMovies("Star");
        assertEquals(2, results.size());

        // Cerca "Cameron"
        results = facade.searchMovies("Cameron");
        assertEquals(1, results.size());
        assertEquals("Titanic", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Dovrebbe rimuovere un film")
    void testRimozione() {
        facade.addNewMovie("Matrix", "Wachowski", "1999", 5);
        Movie m = library.getMovies().get(0);

        facade.removeMovie(m);

        assertTrue(library.getMovies().isEmpty(), "La lista deve essere vuota dopo la rimozione");
    }

    // --- TEST NEGATIVI (Gestione Errori) ---

    @Test
    @DisplayName("Non deve accettare anni non numerici")
    void testErroreAnnoNonNumerico() {
        String error = facade.addNewMovie("Test", "Regista", "ABCD", 3);

        assertNotNull(error, "Deve ritornare un messaggio di errore");
        assertTrue(error.contains("numeri"), "Il messaggio deve citare i numeri");
        assertTrue(library.getMovies().isEmpty(), "Nessun film deve essere aggiunto");
    }

    @Test
    @DisplayName("Non deve accettare anni con lunghezza diversa da 4")
    void testErroreLunghezzaAnno() {
        String error = facade.addNewMovie("Test", "Regista", "999", 3); // 3 cifre

        assertNotNull(error);
        assertEquals("L'anno deve essere composto esattamente da 4 cifre!", error);
    }

    @Test
    @DisplayName("Non deve accettare anni nel futuro")
    void testErroreAnnoFuturo() {
        int futureYear = Year.now().getValue() + 2;
        String error = facade.addNewMovie("Future", "Me", String.valueOf(futureYear), 5);

        assertNotNull(error);
        // Questo messaggio viene dal Builder
        assertTrue(error.contains("L'anno non è valido"));
    }

    @Test
    @DisplayName("Non deve accettare titolo vuoto")
    void testErroreTitoloVuoto() {
        String error = facade.addNewMovie("", "Regista", "2020", 3);

        assertNotNull(error);
        assertTrue(error.toLowerCase().contains("titolo"));
    }
}
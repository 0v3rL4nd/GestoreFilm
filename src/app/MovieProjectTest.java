package app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieProjectTest {

    private MovieSystemFacade facade;
    private MovieLibrary library;

    @BeforeEach
    void setUp() {
        facade = new MovieSystemFacade();
        library = MovieLibrary.getInstance();
        library.clearDataForTesting(); // Importante per pulire il Singleton!
    }

    // --- TEST BUILDER ---
    @Test
    @DisplayName("TC-B-01: Creazione corretta tramite Builder")
    void testMovieBuilder() {
        Movie m = new Movie.MovieBuilder()
                .setTitle("Inception")
                .setDirector("Nolan")
                .setYear(2010)
                .build();

        assertNotNull(m);
        assertEquals("Inception", m.getTitle());
        assertEquals("Nolan", m.getDirector());
        assertEquals(2010, m.getYear());
    }

    // --- TEST SINGLETON ---
    @Test
    @DisplayName("TC-L-01: Singleton Instance Check")
    void testSingleton() {
        MovieLibrary i1 = MovieLibrary.getInstance();
        MovieLibrary i2 = MovieLibrary.getInstance();
        assertSame(i1, i2, "Le istanze devono essere identiche");
    }

    // --- TEST COMMAND ---
    @Test
    @DisplayName("TC-L-02: Aggiunta Film (Command)")
    void testAddCommand() {
        facade.addNewMovie("Matrix", "Wachowski", 1999, 5);
        assertEquals(1, library.getMovies().size());
        assertEquals("Matrix", library.getMovies().get(0).getTitle());
    }

    @Test
    @DisplayName("TC-L-03: Rimozione Film (Command)")
    void testRemoveCommand() {
        facade.addNewMovie("Matrix", "Wachowski", 1999, 5);
        Movie m = library.getMovies().get(0);

        facade.removeMovie(m);
        assertEquals(0, library.getMovies().size());
    }

    // --- TEST STATE ---
    @Test
    @DisplayName("TC-S-02: Transizione Stati")
    void testStateTransitions() {
        Movie m = new Movie.MovieBuilder().setTitle("Test").build();

        // Stato Iniziale
        assertTrue(m.toString().contains("Da Vedere"));

        // Primo Play -> In Visione
        m.play();
        assertTrue(m.toString().contains("In Visione"));

        // Secondo Play -> Visto
        m.play();
        assertTrue(m.toString().contains("Visto"));
    }

    // --- TEST STRATEGY (SORTING) ---
    @Test
    @DisplayName("TC-STR-01: Ordinamento per Anno")
    void testSortStrategy() {
        facade.addNewMovie("Old Movie", "Dir", 1990, 5);
        facade.addNewMovie("New Movie", "Dir", 2020, 5);

        // Default è inserimento (Old prima di New)

        // Applichiamo ordinamento
        facade.changeSortOrder(new WatchedState.YearSortStrategy());

        List<Movie> movies = library.getMovies();
        assertEquals(1990, movies.get(0).getYear());
        assertEquals(2020, movies.get(1).getYear());
    }

    // --- TEST STRATEGY (PERSISTENZA NEGATIVA) ---
    @Test
    @DisplayName("TC-STR-04: Load con file inesistente")
    void testLoadNoFile() {
        // Rimuoviamo il file se esiste
        File f = new File("collection.json");
        if(f.exists()) f.delete();

        // Proviamo a caricare
        StorageStrategy jsonStrat = new JsonStorageStrategy();
        List<Movie> result = jsonStrat.load();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "La lista deve essere vuota ma non null");
    }
}
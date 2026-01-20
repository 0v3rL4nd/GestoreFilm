package app;

import java.util.List;

public class MainTerminal {
    public static void main(String[] args) {
        // 1. Inizializzazione Sistema
        MovieSystemFacade facade = new MovieSystemFacade();
        ConsoleView view = new ConsoleView();

        // Colleghiamo la vista al sistema
        facade.registerView(view);

        // 2. Aggiunta Film (Usa Builder interno e Command)
        System.out.println(">>> Aggiungo 'Inception'...");
        facade.addNewMovie("Inception", "C. Nolan", 2010, 5);

        System.out.println(">>> Aggiungo 'The Godfather'...");
        facade.addNewMovie("The Godfather", "F.F. Coppola", 1972, 5);

        System.out.println(">>> Aggiungo 'Barbie'...");
        facade.addNewMovie("Barbie", "G. Gerwig", 2023, 4);

        // 3. Cambio Ordinamento (Usa Strategy)
        System.out.println(">>> Cambio ordinamento per Anno...");
        facade.changeSortOrder(new WatchedState.YearSortStrategy());

        System.out.println(">>> Cambio ordinamento per Titolo...");
        facade.changeSortOrder(new TitleSortStrategy());

        // 4. Simulazione Pattern State
        List<Movie> movies = MovieLibrary.getInstance().getMovies();
        if (!movies.isEmpty()) {
            Movie inception = movies.get(0); // Prendiamo un film a caso
            System.out.println(">>> Cambio stato di: " + inception.getTitle());
            inception.play(); // Da Vedere -> In Visione
            inception.play(); // In Visione -> Visto
        }

        // 5. Salvataggio (Usa Strategy di persistenza)
        System.out.println(">>> Salvataggio su disco...");
        facade.triggerSave();
    }
}
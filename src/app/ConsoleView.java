package app;

import java.util.List;

class ConsoleView implements Observer {
    @Override
    public void update(List<Movie> movies) {
        System.out.println("\n--- AGGIORNAMENTO VISTA  ---");
        System.out.println("Totale Film: " + movies.size());
        for (Movie m : movies) {
            System.out.println(m);
        }
        System.out.println("--------------------------------------\n");
    }

}
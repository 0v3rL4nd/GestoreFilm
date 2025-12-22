package dominio;

import java.util.Comparator;
import java.util.List;

class WatchedState implements MovieState {
    public void handlePlay(Movie context) {
        System.out.println("Riguardo il film...");
        context.setState(new WatchingState());
    }

    public String getStateName() {
        return "Visto";
    }

    static class YearSortStrategy implements SortStrategy {
        public void sort(List<Movie> movies) {
            movies.sort(Comparator.comparingInt(Movie::getYear));
            System.out.println("--> Ordinato per Anno");
        }
    }
}

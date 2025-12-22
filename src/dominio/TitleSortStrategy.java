package dominio;

import java.util.Comparator;
import java.util.List;

class TitleSortStrategy implements SortStrategy {
    public void sort(List<Movie> movies) {
        movies.sort(Comparator.comparing(Movie::getTitle));
        System.out.println("--> Ordinato per Titolo");
    }
}

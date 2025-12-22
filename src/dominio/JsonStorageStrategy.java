package dominio;

import java.util.ArrayList;
import java.util.List;

class JsonStorageStrategy implements StorageStrategy {
    public void save(List<Movie> movies) {
        System.out.println("--> [JSON] Salvataggio su file .json simulato.");
        // Qui andrebbe la logica reale con librerie tipo Jackson/Gson
    }

    public List<Movie> load() {
        System.out.println("--> [JSON] Caricamento da file .json simulato.");
        return new ArrayList<>(); // Ritorna lista vuota per simulazione
    }
}

package app;

import java.util.ArrayList;
import java.util.List;

// Questa classe simula il salvataggio ma non scrive nulla su disco.
// Serve per proteggere il tuo file "collection.json" durante i test.
public class DummyStorageStrategy implements StorageStrategy {

    @Override
    public void save(List<Movie> movies) {
        // Simuliamo un salvataggio (No-Op)
        System.out.println("   [MOCK STORAGE] Salvataggio simulato in RAM (Nessun file toccato).");
    }

    @Override
    public List<Movie> load() {
        // Simuliamo che non ci siano dati salvati
        return new ArrayList<>();
    }
}
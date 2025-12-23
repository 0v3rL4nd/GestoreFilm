package app;

import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.nio.file.*;

class JsonStorageStrategy implements StorageStrategy {
    private final String FILE_NAME = "collection.json";

    @Override
    public void save(List<Movie> movies) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            json.append("  {\n");
            json.append("    \"title\": \"").append(escape(m.getTitle())).append("\",\n");
            json.append("    \"director\": \"").append(escape(m.getDirector())).append("\",\n");
            json.append("    \"year\": ").append(m.getYear()).append(",\n");
            json.append("    \"rating\": ").append(m.getRating()).append(",\n");
            // Salviamo il nome dello stato per poterlo ripristinare dopo
            // Nota: m.toString() contiene lo stato, ma è meglio avere un metodo getStateName() pulito in MovieState
            // Per ora usiamo un trucco sporco o assumiamo che MovieState abbia toString()
            json.append("    \"state\": \"").append(extractStateName(m)).append("\"\n");

            json.append("  }");
            if (i < movies.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");

        try {
            Files.write(Paths.get(FILE_NAME), json.toString().getBytes());
            System.out.println("--> [JSON] Salvataggio completato su " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Movie> load() {
        List<Movie> loadedMovies = new ArrayList<>();
        Path path = Paths.get(FILE_NAME);

        if (!Files.exists(path)) {
            System.out.println("--> [JSON] Nessun file trovato. Creazione nuova collezione.");
            return loadedMovies;
        }

        try {
            String content = new String(Files.readAllBytes(path));
            // Rimuoviamo le parentesi quadre esterne
            content = content.trim();
            if (content.startsWith("[")) content = content.substring(1);
            if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

            if (content.isEmpty()) return loadedMovies;

            // Dividiamo i singoli oggetti JSON (assumendo la struttura }, { )
            String[] objects = content.split("\\},\\s*\\{");

            for (String obj : objects) {
                // Pulizia caratteri residui
                obj = obj.replace("{", "").replace("}", "").trim();

                // Parsing manuale dei campi
                String title = parseField(obj, "title");
                String director = parseField(obj, "director");
                int year = Integer.parseInt(parseField(obj, "year"));
                int rating = Integer.parseInt(parseField(obj, "rating"));
                String stateName = parseField(obj, "state");

                // Uso del Builder per ricreare l'oggetto
                Movie m = new Movie.MovieBuilder()
                        .setTitle(title)
                        .setDirector(director)
                        .setYear(year)
                        .setRating(rating)
                        .build();

                // Ripristino dello stato (Pattern State)
                m.restoreStateFromData(stateName);

                loadedMovies.add(m);
            }
            System.out.println("--> [JSON] Caricati " + loadedMovies.size() + " film da file.");

        } catch (Exception e) {
            System.err.println("Errore nel caricamento del JSON (formato corrotto?): " + e.getMessage());
        }

        return loadedMovies;
    }

    // --- Helper per il parsing manuale ---

    // Estrae il valore di un campo dato il nome della chiave (es. "title")
    private String parseField(String jsonObject, String key) {
        String pattern = "\"" + key + "\":";
        int startIndex = jsonObject.indexOf(pattern);
        if (startIndex == -1) return "";

        startIndex += pattern.length();

        // Cerca l'inizio del valore
        while (startIndex < jsonObject.length() && (jsonObject.charAt(startIndex) == ' ' || jsonObject.charAt(startIndex) == '"')) {
            startIndex++;
        }

        // Cerca la fine del valore (virgola o fine stringa)
        int endIndex = jsonObject.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = jsonObject.length();

        String value = jsonObject.substring(startIndex, endIndex).trim();
        // Rimuove eventuali virgolette finali se è una stringa
        if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);

        return value;
    }

    // Helper per gestire virgolette dentro le stringhe
    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    // Helper per ottenere il nome dello stato usando Reflection o un cast (per evitare modifiche drastiche alle interfacce sopra)
    private String extractStateName(Movie m) {
        // Qui sfruttiamo il toString() del film che abbiamo definito prima: "... | Stato: Visto"
        // Oppure possiamo chiamare il metodo getStateName se lo abbiamo esposto.
        // Assumiamo che il toString sia: "'Title' (Year) ... | Stato: NomeStato"
        String s = m.toString();
        return s.substring(s.lastIndexOf("Stato: ") + 7);
    }
}
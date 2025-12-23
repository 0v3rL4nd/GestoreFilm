package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// --- INTERFACCIA GRAFICA (Observer Concreto) ---
public class MovieSwingApp extends JFrame implements Observer {

    // Componenti UI
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTitle, txtDirector, txtYear;
    private JComboBox<Integer> comboRating;
    private MovieSystemFacade facade;

    public MovieSwingApp() {
        // 1. Inizializzazione Facade e Registrazione Observer
        facade = new MovieSystemFacade();
        facade.registerView(this); // La finestra osserva il sistema!

        // 2. Setup della Finestra
        setTitle("Gestore Collezione Film (GoF Patterns)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 3. Creazione Pannelli
        createInputPanel();
        createTablePanel();
        createControlPanel();

        // Carica dati iniziali (se presenti)
        refreshTable(MovieLibrary.getInstance().getMovies());
    }

    private void createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Nuovo Film (Builder Pattern)"));

        txtTitle = new JTextField();
        txtDirector = new JTextField();
        txtYear = new JTextField();
        comboRating = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JButton btnAdd = new JButton("Aggiungi");

        // Stile
        panel.add(new JLabel("Titolo:")); panel.add(txtTitle);
        panel.add(new JLabel("Regista:")); panel.add(txtDirector);
        panel.add(new JLabel("Anno:")); panel.add(txtYear);
        panel.add(new JLabel("Voto:")); panel.add(comboRating);
        panel.add(btnAdd);

        // AZIONE: Command Pattern
        btnAdd.addActionListener(e -> {
            try {
                String title = txtTitle.getText();
                String director = txtDirector.getText();
                int year = Integer.parseInt(txtYear.getText());
                int rating = (int) comboRating.getSelectedItem();

                facade.addNewMovie(title, director, year, rating);

                // Pulisci campi
                txtTitle.setText(""); txtDirector.setText(""); txtYear.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "L'anno deve essere un numero!");
            }
        });

        add(panel, BorderLayout.NORTH);
    }

    private void createTablePanel() {
        String[] columns = {"Titolo", "Regista", "Anno", "Voto", "Stato"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Controlli (Command & Strategy)"));

        // Pulsante Rimuovi
        JButton btnRemove = new JButton("Rimuovi Selezionato");
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = (String) tableModel.getValueAt(row, 0);
                // In un caso reale useremmo l'ID, qui cerchiamo per titolo per semplicità
                Movie m = findMovieByTitle(title);
                if (m != null) facade.removeMovie(m);
            }
        });

        // Pulsante Play (State Pattern)
        JButton btnPlay = new JButton("Play / Cambia Stato");
        btnPlay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = (String) tableModel.getValueAt(row, 0);
                Movie m = findMovieByTitle(title);
                if (m != null) {
                    m.play(); // State Pattern in azione
                    refreshTable(MovieLibrary.getInstance().getMovies()); // Forza refresh per vedere cambio stato
                }
            }
        });

        // Strategie di Ordinamento
        JRadioButton rbTitle = new JRadioButton("Ordina per Titolo");
        JRadioButton rbYear = new JRadioButton("Ordina per Anno");
        ButtonGroup group = new ButtonGroup();
        group.add(rbTitle); group.add(rbYear);

        rbTitle.addActionListener(e -> facade.changeSortOrder(new TitleSortStrategy()));
        rbYear.addActionListener(e -> facade.changeSortOrder(new WatchedState.YearSortStrategy()));

        // Pulsante Salva
        JButton btnSave = new JButton("Salva su Disco");
        btnSave.addActionListener(e -> {
            facade.triggerSave();
            JOptionPane.showMessageDialog(this, "Collezione salvata!");
        });

        panel.add(btnRemove);
        panel.add(btnPlay);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(rbTitle);
        panel.add(rbYear);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnSave);

        add(panel, BorderLayout.SOUTH);
    }

    // Helper per trovare il film dalla lista (per semplicità della demo)
    private Movie findMovieByTitle(String title) {
        for (Movie m : MovieLibrary.getInstance().getMovies()) {
            if (m.getTitle().equals(title)) return m;
        }
        return null;
    }

    // --- METODO OBSERVER ---
    // Questo metodo viene chiamato automaticamente dalla Library quando i dati cambiano
    @Override
    public void update(List<Movie> movies) {
        refreshTable(movies);
    }

    private void refreshTable(List<Movie> movies) {
        tableModel.setRowCount(0); // Pulisce la tabella
        for (Movie m : movies) {
            // Estrapoliamo lo stato come stringa dal toString o getter
            // Nota: per pulizia, l'oggetto Movie dovrebbe esporre getters migliori
            Object[] row = {
                    m.getTitle(),
                    "N/A", // Se non hai aggiunto il getter Director in Movie, aggiungilo!
                    m.getYear(),
                    m.getRating(),
                    m.toString().substring(m.toString().lastIndexOf("Stato:")) // Hack veloce per demo
            };
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        // Avvia l'interfaccia nel thread grafico
        SwingUtilities.invokeLater(() -> {
            new MovieSwingApp().setVisible(true);
        });
    }
}
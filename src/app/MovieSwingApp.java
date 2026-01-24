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
    private JTextField txtTitle, txtDirector, txtYear, txtSearch;
    private JComboBox<Integer> comboRating;

    // Riferimenti logici
    private MovieSystemFacade facade;
    private Movie selectedMovieForEdit = null; // Per tracciare la modifica
    private JButton btnAction; // Pulsante dinamico (Aggiungi/Salva Modifiche)

    public MovieSwingApp() {
        // Inizializzazione Facade e Registrazione Observer
        facade = new MovieSystemFacade();
        facade.registerView(this);

        // Setup della Finestra
        setTitle("Gestore Collezione Film (GoF Patterns)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- GESTIONE LAYOUT COMPLESSO ---
        // Creiamo un pannello "Wrapper" per mettere sia la Ricerca che l'Input in alto
        JPanel northContainer = new JPanel(new BorderLayout());

        JPanel searchPanel = createTopPanel(); // Pannello Ricerca
        JPanel inputPanel = createInputPanel(); // Pannello Inserimento

        northContainer.add(searchPanel, BorderLayout.NORTH);
        northContainer.add(inputPanel, BorderLayout.CENTER);

        // Aggiungiamo il wrapper al frame principale
        add(northContainer, BorderLayout.NORTH);

        createTablePanel();   // Tabella al Centro
        createControlPanel(); // Controlli in Basso

        // Carica dati iniziali
        refreshTable(MovieLibrary.getInstance().getMovies());
    }

    // Creazione Pannello Ricerca
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Ricerca"));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cerca");
        JButton btnReset = new JButton("Reset");

        // LOGICA RICERCA
        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText();
            List<Movie> results = facade.searchMovies(query);
            refreshTable(results); // Aggiorna la vista con i risultati filtrati
        });

        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable(MovieLibrary.getInstance().getMovies());
        });

        panel.add(new JLabel("Cerca (Titolo/Regista/Anno):"));
        panel.add(txtSearch);
        panel.add(btnSearch);
        panel.add(btnReset);

        return panel;
    }

    // Creazione Pannello Inserimento/Modifica
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        // Uso un layout a due righe per mettere pulsanti sotto i campi

        JPanel fieldsPanel = new JPanel(new FlowLayout());
        txtTitle = new JTextField(15);
        txtDirector = new JTextField(15);
        txtYear = new JTextField(6);
        comboRating = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        fieldsPanel.add(new JLabel("Titolo:")); fieldsPanel.add(txtTitle);
        fieldsPanel.add(new JLabel("Regista:")); fieldsPanel.add(txtDirector);
        fieldsPanel.add(new JLabel("Anno:")); fieldsPanel.add(txtYear);
        fieldsPanel.add(new JLabel("Voto:")); fieldsPanel.add(comboRating);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        btnAction = new JButton("Aggiungi Nuovo Film");
        JButton btnClear = new JButton("Pulisci / Annulla");

        // AZIONE PRINCIPALE (Gestisce sia Aggiunta che Modifica)
        btnAction.addActionListener(e -> {
            String title = txtTitle.getText();
            String director = txtDirector.getText();
            String year = txtYear.getText();
            int rating = (int) comboRating.getSelectedItem();

            String errorMsg;

            if (selectedMovieForEdit == null) {
                // MODALITÀ: AGGIUNTA
                errorMsg = facade.addNewMovie(title, director, year, rating);
            } else {
                // MODALITÀ: MODIFICA
                errorMsg = facade.editMovie(selectedMovieForEdit, title, director, year, rating);
            }

            if (errorMsg != null) {
                // Errore di validazione (dal Builder o dalla Facade)
                JOptionPane.showMessageDialog(this, "Errore: " + errorMsg, "Attenzione", JOptionPane.ERROR_MESSAGE);
            } else {
                // Successo
                clearInputs();
            }
        });

        btnClear.addActionListener(e -> clearInputs());

        buttonsPanel.add(btnAction);
        buttonsPanel.add(btnClear);

        panel.add(fieldsPanel);
        panel.add(buttonsPanel);
        panel.setBorder(BorderFactory.createTitledBorder("Gestione Film (Builder & Command)"));

        return panel;
    }

    private void createTablePanel() {
        String[] columns = {"Titolo", "Regista", "Anno", "Voto", "Stato"};

        // Rendiamo le celle non modificabili direttamente
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Pannello di Controllo"));

        JButton btnEdit = new JButton("Modifica Selezionato");
        JButton btnRemove = new JButton("Rimuovi Selezionato");
        JButton btnPlay = new JButton("Play / Cambia Stato");

        // --- LOGICA MODIFICA ---
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = (String) tableModel.getValueAt(row, 0);
                Movie m = findMovieByTitle(title);
                if (m != null) {
                    // Carica i dati nei campi
                    selectedMovieForEdit = m;
                    txtTitle.setText(m.getTitle());
                    txtDirector.setText(m.getDirector());
                    txtYear.setText(String.valueOf(m.getYear()));
                    comboRating.setSelectedItem(m.getRating());

                    // Cambia testo pulsante per feedback utente
                    btnAction.setText("Salva Modifiche a '" + m.getTitle() + "'");
                    btnAction.setBackground(Color.ORANGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleziona un film da modificare.");
            }
        });

        // --- LOGICA RIMOZIONE ---
        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = (String) tableModel.getValueAt(row, 0);
                Movie m = findMovieByTitle(title);

                int confirm = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler rimuovere '" + title + "'?");
                if (m != null && confirm == JOptionPane.YES_OPTION) {
                    facade.removeMovie(m);
                    clearInputs(); // Reset se stavo modificando proprio quello
                }
            }
        });

        // --- LOGICA PLAY (State Pattern) ---
        btnPlay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = (String) tableModel.getValueAt(row, 0);
                Movie m = findMovieByTitle(title);
                if (m != null) {
                    facade.advanceState(m);
                }
                else{
                    JOptionPane.showMessageDialog(this, "Seleziona un film per cambiare lo stato");
                }
            }

        });

        // --- STRATEGIE DI ORDINAMENTO ---
        JRadioButton rbTitle = new JRadioButton("Titolo");
        JRadioButton rbYear = new JRadioButton("Anno");
        ButtonGroup group = new ButtonGroup();
        group.add(rbTitle); group.add(rbYear);

        rbTitle.addActionListener(e -> facade.changeSortOrder(new TitleSortStrategy()));

        rbYear.addActionListener(e -> facade.changeSortOrder(new WatchedState.YearSortStrategy()));

        JButton btnSave = new JButton("Salva su Disco");
        btnSave.addActionListener(e -> {
            facade.triggerSave();
            JOptionPane.showMessageDialog(this, "Collezione salvata correttamente!");
        });

        panel.add(btnEdit);
        panel.add(btnRemove);
        panel.add(btnPlay);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JLabel("Ordina:"));
        panel.add(rbTitle);
        panel.add(rbYear);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnSave);

        add(panel, BorderLayout.SOUTH);
    }

    // Helper: Reset interfaccia
    private void clearInputs() {
        txtTitle.setText("");
        txtDirector.setText("");
        txtYear.setText("");
        comboRating.setSelectedIndex(0);
        selectedMovieForEdit = null;
        btnAction.setText("Aggiungi Nuovo Film");
        btnAction.setBackground(null); // Reset colore
    }

    // Helper: Trova film (In un'app reale userei l'ID o l'oggetto stesso nella JTable)
    private Movie findMovieByTitle(String title) {
        for (Movie m : MovieLibrary.getInstance().getMovies()) {
            if (m.getTitle().equals(title)) return m;
        }
        return null;
    }

    @Override
    public void update(List<Movie> movies) {
        refreshTable(movies);
    }

    private void refreshTable(List<Movie> movies) {
        tableModel.setRowCount(0); // Pulisce

        for (Movie m : movies) {
            Object[] row = {
                    m.getTitle(),
                    m.getDirector(),
                    m.getYear(),
                    m.getRating(),
                    m.getStateName() // <--- ORA USIAMO IL METODO PULITO, NIENTE PIÙ SUBSTRING
            };
            tableModel.addRow(row);
        }
    }

    private String extractState(Movie m) {
        String s = m.toString();
        if(s.contains("Stato:")) {
            return s.substring(s.lastIndexOf("Stato:"));
        }
        return "N/A";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MovieSwingApp().setVisible(true);
        });
    }
}
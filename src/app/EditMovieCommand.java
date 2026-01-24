package app;

public class EditMovieCommand implements Command{
    private Movie movieToEdit;
    private String newTitle;
    private String newDirector;
    private int newYear;
    private int newRating;

    //Riferimento per notificare gli observer
    private MovieLibrary library;



    public EditMovieCommand(Movie movieToEdit, String newTitle, String newDirector, int newYear, int newRating) {
        this.movieToEdit = movieToEdit;
        this.newTitle = newTitle;
        this.newDirector = newDirector;
        this.newYear = newYear;
        this.newRating = newRating;
        this.library = MovieLibrary.getInstance();
    }

    @Override
    public void execute() {
        movieToEdit.setTitle(newTitle);
        movieToEdit.setDirector(newDirector);
        movieToEdit.setYear(newYear);
        movieToEdit.setRating(newRating);

        library.notifyObservers();

        //auto-save
        library.save();
    }
}

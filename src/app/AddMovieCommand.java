package app;

class AddMovieCommand implements Command {
    private Movie movie;
    private MovieLibrary library;

    public AddMovieCommand(Movie movie) {
        this.movie = movie;
    }

    public void execute() {

        MovieLibrary.getInstance().addMovie(movie);

        //auto-save
        library.save();
    }
}

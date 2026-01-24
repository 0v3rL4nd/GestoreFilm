package app;

class RemoveMovieCommand implements Command {
    private Movie movie;
    private MovieLibrary library;

    public RemoveMovieCommand(Movie movie) {
        this.movie = movie;
        this.library = MovieLibrary.getInstance();

    }



    public void execute() {

        MovieLibrary.getInstance().removeMovie(movie);

        //auto-save
        library.save();
    }
}
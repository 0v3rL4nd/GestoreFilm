package app;

class RemoveMovieCommand implements Command {
    private Movie movie;

    public RemoveMovieCommand(Movie movie) {
        this.movie = movie;
    }

    public void execute() {
        MovieLibrary.getInstance().removeMovie(movie);
    }
}
package app;

class AddMovieCommand implements Command {
    private Movie movie;

    public AddMovieCommand(Movie movie) {
        this.movie = movie;
    }

    public void execute() {
        MovieLibrary.getInstance().addMovie(movie);
    }
}

package dominio;

class WatchingState implements MovieState {
    public void handlePlay(Movie context) {
        System.out.println("Film finito! Spostamento in archivio 'Visto'.");
        context.setState(new WatchedState());
    }

    public String getStateName() {
        return "In Visione";
    }
}

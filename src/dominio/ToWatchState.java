package dominio;

class ToWatchState implements MovieState {
    public void handlePlay(Movie context) {
        System.out.println("Inizio la visione del film...");
        context.setState(new WatchingState());
    }

    public String getStateName() {
        return "Da Vedere";
    }
}

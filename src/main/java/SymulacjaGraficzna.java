import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;

public class SymulacjaGraficzna extends Application {

    private static final int ROZMIAR_PACJENTA = 10;

    private Szpital szpital;
    private Pane root;
    private Map<Pacjent, Rectangle> pacjentReprezentacja = new HashMap<>();
    private Map<Rectangle, Pane> pacjentSalaMap = new HashMap<>();
    private Random rand = new Random();
    private int dzien = 1;
    private int dni = 15;
    private List<Sala> sale;
    private List<Pacjent> wszyscyPacjenci = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        int liczbaSal = 2;
        int liczbaLozek = 4;
        int liczbaPacjentow = 8;
        int minWiek = 1;
        int maxWiek = 31;
        char plec = 'M';
        boolean nalogi = false;
        boolean przewlekleChoroby = true;
        boolean zaszczepieni = false;
        int agresywnosc = 5;

        int wielkoscSali = Math.min(300, Math.max(150, liczbaLozek * 20));
        int SZEROKOSC_SALI = wielkoscSali;
        int WYSOKOSC_SALI = wielkoscSali;

        sale = new ArrayList<>();
        for (int i = 0; i < liczbaSal; i++) {
            sale.add(new Sala(liczbaLozek));
        }

        Wirus wirus = new Wirus(agresywnosc);
        szpital = new Szpital(sale, wirus);

        for (int i = 0; i < liczbaPacjentow; i++) {
            int wiek = rand.nextInt(maxWiek - minWiek + 1) + minWiek;
            Pacjent pacjent = new ZdrowyPacjent(wiek, plec, nalogi, przewlekleChoroby, zaszczepieni);
            wszyscyPacjenci.add(pacjent);
            boolean dodany = false;
            while (!dodany) {
                Sala losowaSala = sale.get(rand.nextInt(sale.size()));
                dodany = losowaSala.dodajPacjenta(pacjent);
            }
        }

        root = new Pane();
        root.setStyle("-fx-background-color: lightgray;");

        for (int i = 0; i < liczbaSal; i++) {
            Sala sala = sale.get(i);
            Pane salaPane = new Pane();
            salaPane.setLayoutX(50 + (i % 5) * (SZEROKOSC_SALI + 10));
            salaPane.setLayoutY((i / 5) * (WYSOKOSC_SALI + 10));
            salaPane.setPrefSize(SZEROKOSC_SALI, WYSOKOSC_SALI);
            salaPane.setStyle("-fx-border-color: black; -fx-background-color: white;");
            root.getChildren().add(salaPane);

            for (Pacjent pacjent : sala.getPacjenci()) {
                Rectangle rect = new Rectangle(ROZMIAR_PACJENTA, ROZMIAR_PACJENTA);
                rect.setFill(Color.GREEN);
                rect.setLayoutX(rand.nextInt(SZEROKOSC_SALI - ROZMIAR_PACJENTA));
                rect.setLayoutY(rand.nextInt(WYSOKOSC_SALI - ROZMIAR_PACJENTA));
                pacjentReprezentacja.put(pacjent, rect);
                pacjentSalaMap.put(rect, salaPane);
                salaPane.getChildren().add(rect);
            }
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            szpital.symulujDzien(1);
            aktualizujKolory();
            poruszPacjentami();
            zapiszStatystyki();
            dzien++;
        }));

        timeline.setCycleCount(dni);
        timeline.play();

        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setTitle("Symulacja Szpitala");

        Platform.runLater(() -> {
            primaryStage.show();
            primaryStage.toFront();
        });
    }

    private void aktualizujKolory() {
        for (Map.Entry<Pacjent, Rectangle> entry : pacjentReprezentacja.entrySet()) {
            Pacjent p = entry.getKey();
            Rectangle r = entry.getValue();

            switch (p.getStanInfekcji()) {
                case NIEZARAZONY -> r.setFill(Color.GREEN);
                case ZARAZONY -> r.setFill(Color.YELLOW);
                case OZDROWIENIEC -> r.setFill(Color.BLUE);
                case ZMARL -> r.setFill(Color.RED);
            }
        }
    }

    private void poruszPacjentami() {
        for (Map.Entry<Pacjent, Rectangle> entry : pacjentReprezentacja.entrySet()) {
            Pacjent p = entry.getKey();
            if (!p.czyZyje()) continue;

            Rectangle r = entry.getValue();
            if (rand.nextDouble() > 0.5) continue;

            Pane sala = pacjentSalaMap.get(r);
            double dx = rand.nextInt(7) - 3;
            double dy = rand.nextInt(7) - 3;

            double newX = Math.min(Math.max(r.getLayoutX() + dx, 0), sala.getWidth() - ROZMIAR_PACJENTA);
            double newY = Math.min(Math.max(r.getLayoutY() + dy, 0), sala.getHeight() - ROZMIAR_PACJENTA);

            r.setLayoutX(newX);
            r.setLayoutY(newY);
        }
    }

    private void zapiszStatystyki() {
        int zdrowy = 0, zarazony = 0, ozdrowieniec = 0, zmarly = 0;
        for (Pacjent pacjent : wszyscyPacjenci) {
            switch (pacjent.getStanInfekcji()) {
                case NIEZARAZONY -> zdrowy++;
                case ZARAZONY -> zarazony++;
                case OZDROWIENIEC -> ozdrowieniec++;
                case ZMARL -> zmarly++;
            }
        }
        System.out.println("Dzień " + dzien + ": ZDROWI = " + zdrowy + ", ZARAZENI = " + zarazony + ", OZDROWIEŃCY = " + ozdrowieniec + ", ZMARLI = " + zmarly);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.concurrent.ThreadLocalRandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Klasa główna uruchamiająca aplikację graficzną JavaFX
public class SymulacjaGraficzna extends Application {

    // Wymiary całego okna symulacji
    private static final int SCENE_WIDTH = 1450;
    private static final int SCENE_HEIGHT = 750;

    // Marginesy i odstępy między salami
    private static final int MARGIN = 50;
    private static final int GAP = 20;

    // Interwał czasowy jednego kroku symulacji (1 sekunda)
    private static final Duration STEP = Duration.seconds(1);

    // Obrazki reprezentujące różne typy pacjentów
    private static final Image IMG_BODY     = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/body.png"));
    private static final Image IMG_CHILD    = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/child_boy_girl.png"));
    private static final Image IMG_TBOY     = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/teenager_boy.png"));
    private static final Image IMG_TGIRL    = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/teenager_girl.png"));
    private static final Image IMG_ADMAN    = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/man.png"));
    private static final Image IMG_ADWOMAN  = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/wooman.png"));
    private static final Image IMG_GRPA     = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/grandfather.png"));
    private static final Image IMG_GRMA     = new Image(SymulacjaGraficzna.class.getResourceAsStream("/icons/grandmother.png"));

    // Główny kontener graficzny (GUI)
    private Pane root;

    // Mapa powiązująca pacjenta z jego graficzną reprezentacją
    private final Map<Pacjent, ImageView> viewMap = new HashMap<>();

    // Lista wszystkich pacjentów biorących udział w symulacji
    private final List<Pacjent> wszyscy = new ArrayList<>();

    // Generator liczb losowych
    private final Random rnd = new Random();

    // Szpital (zawiera sale i wirusa)
    private Szpital szpital;

    // Aktualny dzień symulacji
    private int dzien = 1;

    // Writer do zapisu danych statystycznych do pliku CSV
    private BufferedWriter raportWriter;

    @Override
    public void start(Stage stage) {
        // Parametry wejściowe symulacji
        int liczbaSal = 30;
        int lozekNaSale = 10;
        int pacjentow = 100;
        int minWiek = 1, maxWiek = 121;
        char plec = 'K';
        boolean nalogi = true;
        boolean przewl = true;
        boolean szczep = true;
        double agresywnosc = 3;
        int dniSymulacji = 30;

        // Tworzenie pliku raportowego
        try {
            raportWriter = new BufferedWriter(new FileWriter("raport.csv"));
            raportWriter.write("Dzien;ZDROWI;ZARAZENI;OZDROWIENI;ZMARLI\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tworzenie sal
        List<Sala> sale = new ArrayList<>();
        for (int i = 0; i < liczbaSal; i++) {
            sale.add(new Sala(lozekNaSale));
        }

        // Tworzenie szpitala z wirusem
        szpital = new Szpital(sale, new Wirus(agresywnosc));

        // Tworzenie pacjentów i przydzielanie ich do sal
        for (int i = 0; i < pacjentow; i++) {
            int wiek = ThreadLocalRandom.current().nextInt(minWiek, maxWiek + 1);
            Pacjent p = przewl ? new ChoryPacjent(wiek, plec, nalogi, przewl, szczep) : new ZdrowyPacjent(wiek, plec, nalogi, przewl, szczep);

            wszyscy.add(p);

            // losowe przydzielenie do sali z miejscem
            boolean dodany = false;
            while (!dodany) {
                int indeks = rnd.nextInt(sale.size());
                dodany = sale.get(indeks).dodajPacjenta(p);
            }
        }

        // Utworzenie głównego kontenera graficznego
        root = new Pane();
        root.setStyle("-fx-background-color: lightgray;");

        // Obliczanie rozkładu siatki sal na ekranie
        int maxCols = (int) Math.floor((SCENE_WIDTH - 2.0 * MARGIN + GAP) / 160.0);
        int rows = (int) Math.ceil((double) liczbaSal / maxCols);
        int cols = Math.min(liczbaSal, maxCols);

        double salaW = (SCENE_WIDTH - 2.0 * MARGIN - (cols - 1) * GAP) / cols;
        double salaH = (SCENE_HEIGHT - 2.0 * MARGIN - (rows - 1) * GAP) / rows;
        int iconSize = (int) Math.max(16, Math.min(64, salaW / 8));

        // Tworzenie graficznych sal i dodawanie pacjentów do nich
        for (int i = 0; i < liczbaSal; i++) {
            int row = i / maxCols;
            int col = i % maxCols;

            Pane salaPane = new Pane();
            salaPane.setLayoutX(MARGIN + col * (salaW + GAP));
            salaPane.setLayoutY(MARGIN + row * (salaH + GAP));
            salaPane.setPrefSize(salaW, salaH);
            salaPane.setStyle("-fx-background-color:white; -fx-border-color:black;");
            root.getChildren().add(salaPane);

            // Dodanie pacjentów do sali graficznie
            for (Pacjent p : sale.get(i).getPacjenci()) {
                ImageView iv = new ImageView(dobierzIkone(p));
                iv.setFitWidth(iconSize);
                iv.setFitHeight(iconSize);
                iv.setLayoutX(rnd.nextDouble() * (salaW - iconSize));
                iv.setLayoutY(rnd.nextDouble() * (salaH - iconSize));
                salaPane.getChildren().add(iv);
                viewMap.put(p, iv);
            }
        }

        // Harmonogram symulacji (wykonuje się co STEP(n.p. sekundę))
        Timeline tl = new Timeline(new KeyFrame(STEP, ae -> {
            szpital.symulujDzien(dzien); // wykonanie logiki zarażeń
            odswiezWidoki();             // aktualizacja grafiki
            dzien++;                     // kolejny dzień
        }));
        tl.setCycleCount(dniSymulacji);

        // Po zakończeniu symulacji zamyka plik
        tl.setOnFinished(ae -> {
            try {
                if (raportWriter != null) raportWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        tl.play(); // start symulacji

        // Konfiguracja sceny JavaFX
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Symulacja Szpitala");
        stage.show();
    }

    // Zwraca odpowiednią ikonę w zależności od wieku, płci i stanu życia pacjenta
    private Image dobierzIkone(Pacjent p) {
        if (!p.czyZyje()) return IMG_BODY;
        int w = p.getWiek();
        if (w < 13) return IMG_CHILD;
        else if (w < 18) return p.getPlec() == 'M' ? IMG_TBOY : IMG_TGIRL;
        else if (w < 60) return p.getPlec() == 'M' ? IMG_ADMAN : IMG_ADWOMAN;
        else return p.getPlec() == 'M' ? IMG_GRPA : IMG_GRMA;
    }

    // Odświeża kolory, ruch i ikony pacjentów po każdej turze
    private void odswiezWidoki() {
        for (Pacjent p : wszyscy) {
            ImageView iv = viewMap.get(p);
            Pane parent = (Pane) iv.getParent();
            parent.getChildren().removeIf(n -> n.getUserData() == iv);

            // kolorowe tło stanu pacjenta
            Circle bg = new Circle(
                    iv.getLayoutX() + iv.getFitWidth() / 2,
                    iv.getLayoutY() + iv.getFitHeight() / 2,
                    iv.getFitWidth() / 2 + 4
            );
            bg.setUserData(iv);
            bg.setOpacity(0.4);
            switch (p.getStanInfekcji()) {
                case NIEZARAZONY -> bg.setFill(Color.LIGHTGREEN);
                case ZARAZONY -> bg.setFill(Color.GOLD);
                case OZDROWIENIEC -> bg.setFill(Color.LIGHTBLUE);
                case ZMARL -> bg.setFill(Color.CRIMSON);
            }

            parent.getChildren().add(bg);
            iv.setImage(dobierzIkone(p));
            iv.toFront();

            // losowy ruch pacjenta
            if (p.czyZyje() && rnd.nextDouble() < 0.5) {
                double dx = rnd.nextDouble() * 8 - 4;
                double dy = rnd.nextDouble() * 8 - 4;
                double nx = blokada(iv.getLayoutX() + dx, 0, parent.getWidth() - iv.getFitWidth());
                double ny = blokada(iv.getLayoutY() + dy, 0, parent.getHeight() - iv.getFitHeight());
                iv.setLayoutX(nx);
                iv.setLayoutY(ny);
                iv.toFront();
            }
        }

        // Liczenie i drukowanie statystyk
        int zdrowi = 0, zaraz = 0, ozdr = 0, zmarli = 0;
        for (Pacjent p : wszyscy) {
            if (!p.czyZyje()) zmarli++;
            else if (p.getStanInfekcji() == Pacjent.StanInfekcji.OZDROWIENIEC) ozdr++;
            else if (p.czyZarazony()) zaraz++;
            else if (p.czyNiezarazony()) zdrowi++;
        }

        System.out.printf("Dzień %d: ZDROWI=%d  ZARAZENI=%d  OZDROWIENI=%d  ZMARLI=%d%n", dzien, zdrowi, zaraz, ozdr, zmarli);

        try {
            raportWriter.write(String.format("%d;%d;%d;%d;%d\n", dzien, zdrowi, zaraz, ozdr, zmarli));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ogranicza wartość do zakresu (np. by pacjent nie wyszedł poza salę)
    private double blokada(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // Punkt startowy programu
    public static void main(String[] args) {
        launch(args);
    }
}
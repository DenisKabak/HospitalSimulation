import javafx.application.Application;
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
import javafx.scene.Node;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;

/**
 * Klasa główna uruchamiająca aplikację graficzną JavaFX dla symulacji szpitala.
 * <p>
 * Mechanizmy programowania obiektowego:
 * <ul>
 *   <li><b>Dziedziczenie:</b> HospitalSimulation dziedziczy po Application (JavaFX), co umożliwia uruchomienie GUI.</li>
 *   <li><b>Hermetyzacja:</b> Pola prywatne, publiczne gettery/settery (przykład w klasie SimulationConfig).</li>
 *   <li><b>Polimorfizm:</b> W liście allPatients przechowywane są różne podklasy Patient: HealthyPatient i SickPatient.</li>
 *   <li><b>Kompozycja:</b> Klasa posiada pola będące instancjami innych klas (np. Hospital, Virus, Room).</li>
 *   <li><b>Agregacja:</b> Hospital agreguje listę Room i obiekt Virus.</li>
 * </ul>
 */
public class HospitalSimulation extends Application {

    // Wymiary całego okna symulacji
    private static int SCENE_WIDTH = 1450;
    private static int SCENE_HEIGHT = 750;

    // Marginesy i odstępy między salami
    private static int MARGIN = 50;
    private static int GAP = 20;

    // Interwał czasowy jednego kroku symulacji (ustawiany z pliku CSV)
    private static Duration STEP;

    // Obrazki reprezentujące różne typy pacjentów (kompozycja, pola statyczne klasy)
    private static Image IMG_BODY = new Image(HospitalSimulation.class.getResourceAsStream("/icons/body.png"));
    private static Image IMG_CHILD = new Image(HospitalSimulation.class.getResourceAsStream("/icons/child_boy_girl.png"));
    private static Image IMG_TBOY = new Image(HospitalSimulation.class.getResourceAsStream("/icons/teenager_boy.png"));
    private static Image IMG_TGIRL = new Image(HospitalSimulation.class.getResourceAsStream("/icons/teenager_girl.png"));
    private static Image IMG_ADMAN = new Image(HospitalSimulation.class.getResourceAsStream("/icons/man.png"));
    private static Image IMG_ADWOMAN = new Image(HospitalSimulation.class.getResourceAsStream("/icons/wooman.png"));
    private static Image IMG_GRPA = new Image(HospitalSimulation.class.getResourceAsStream("/icons/grandfather.png"));
    private static Image IMG_GRMA = new Image(HospitalSimulation.class.getResourceAsStream("/icons/grandmother.png"));

    // Główny kontener graficzny (GUI)
    private Pane root;
    private final Map<Patient, ImageView> viewMap = new HashMap<>();
    private final List<Patient> allPatients = new ArrayList<>();
    private final Random rnd = new Random();
    private Hospital hospital;
    private int day = 1;
    private BufferedWriter reportWriter;

    /**
     * Metoda startowa aplikacji JavaFX (wywoływana automatycznie).
     * Odpowiada za przygotowanie konfiguracji, GUI i start symulacji.
     * @param stage główne okno JavaFX
     */
    @Override
    public void start(Stage stage) {
        // HERMETYZACJA: Użycie gettera do odczytu parametrów z configu
        SimulationConfig config = loadConfig("config.csv");
        STEP = Duration.seconds(config.getStep());

        int roomCount = config.getRoomCount();
        int bedsPerRoom = config.getBedsPerRoom();
        int patientCount = config.getPatientCount();
        int minAge = config.getMinAge();
        int maxAge = config.getMaxAge();
        char gender = config.getGender();
        boolean addictions = config.isAddictions();
        boolean chronic = config.isChronic();
        boolean vaccinated = config.isVaccinated();
        double aggressiveness = config.getAggressiveness();
        int simulationDays = config.getSimulationDays();

        // Tworzenie pliku raportowego
        try {
            reportWriter = new BufferedWriter(new FileWriter("report.csv"));
            reportWriter.write("Day;HEALTHY;INFECTED;RECOVERED;DECEASED\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // KOMPOZYCJA: Hospital zawiera listę Room
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < roomCount; i++) {
            rooms.add(new Room(bedsPerRoom));
        }

        // KOMPOZYCJA: Szpital ma wirusa
        hospital = new Hospital(rooms, new Virus(aggressiveness));

        // POLIMORFIZM: Patient może być HealthyPatient lub SickPatient (dziedziczenie)
        for (int i = 0; i < patientCount; i++) {
            int age = ThreadLocalRandom.current().nextInt(minAge, maxAge + 1);
            Patient p = chronic
                    ? new SickPatient(age, gender, addictions, chronic, vaccinated)
                    : new HealthyPatient(age, gender, addictions, chronic, vaccinated);
            allPatients.add(p);
            boolean added = false;
            while (!added) {
                int index = rnd.nextInt(rooms.size());
                added = rooms.get(index).addPatient(p);
            }
        }

        // Utworzenie głównego kontenera graficznego
        root = new Pane();
        root.setStyle("-fx-background-color: lightgray;");

        // Obliczanie rozkładu siatki sal na ekranie
        int maxCols = (int) Math.floor((SCENE_WIDTH - 2.0 * MARGIN + GAP) / 160.0);
        int rows = (int) Math.ceil((double) roomCount / maxCols);
        int cols = Math.min(roomCount, maxCols);

        double roomW = (SCENE_WIDTH - 2.0 * MARGIN - (cols - 1) * GAP) / cols;
        double roomH = (SCENE_HEIGHT - 2.0 * MARGIN - (rows - 1) * GAP) / rows;
        int iconSize = (int) Math.max(16, Math.min(64, roomW / 8));

        // Tworzenie graficznych sal i dodawanie pacjentów do nich
        for (int i = 0; i < roomCount; i++) {
            int row = i / maxCols;
            int col = i % maxCols;

            Pane roomPane = new Pane();
            roomPane.setLayoutX(MARGIN + col * (roomW + GAP));
            roomPane.setLayoutY(MARGIN + row * (roomH + GAP));
            roomPane.setPrefSize(roomW, roomH);
            roomPane.setStyle("-fx-background-color:white; -fx-border-color:black;");
            root.getChildren().add(roomPane);

            List<double[]> occupiedPositions = new ArrayList<>();

            for (Patient p : rooms.get(i).getPatients()) {
                ImageView iv = new ImageView(selectIcon(p));
                iv.setFitWidth(iconSize);
                iv.setFitHeight(iconSize);

                // szukaj wolnej pozycji
                double x, y;
                boolean collision;
                do {
                    x = rnd.nextDouble() * (roomW - iconSize);
                    y = rnd.nextDouble() * (roomH - iconSize);
                    collision = false;
                    for (double[] pos : occupiedPositions) {
                        double dx = pos[0] - x;
                        double dy = pos[1] - y;
                        if (Math.sqrt(dx * dx + dy * dy) < iconSize) {
                            collision = true;
                            break;
                        }
                    }
                } while (collision);

                occupiedPositions.add(new double[]{x, y});

                iv.setLayoutX(x);
                iv.setLayoutY(y);
                roomPane.getChildren().add(iv);
                viewMap.put(p, iv);
            }
        }

        // Harmonogram symulacji (wykonuje się co STEP)
        Timeline tl = new Timeline(new KeyFrame(STEP, ae -> {
            hospital.simulateDay(); // wykonanie logiki zarażeń
            refreshViews();         // aktualizacja grafiki
            day++;                  // kolejny dzień
        }));
        tl.setCycleCount(simulationDays);

        // Po zakończeniu symulacji zamyka plik
        tl.setOnFinished(ae -> {
            try {
                if (reportWriter != null) reportWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        tl.play(); // start symulacji

        // Konfiguracja sceny JavaFX
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Hospital Simulation");
        stage.show();
    }

    /**
     * Zwraca odpowiednią ikonę w zależności od wieku, płci i stanu życia pacjenta
     * @param p pacjent
     * @return obrazek Image z zasobów
     */
    private Image selectIcon(Patient p) {
        if (!p.isAlive()) return IMG_BODY;
        int age = p.getAge();
        if (age < 13) return IMG_CHILD;
        else if (age < 18) return p.getGender() == 'M' ? IMG_TBOY : IMG_TGIRL;
        else if (age < 60) return p.getGender() == 'M' ? IMG_ADMAN : IMG_ADWOMAN;
        else return p.getGender() == 'M' ? IMG_GRPA : IMG_GRMA;
    }

    /**
     * Odświeża kolory, ruch i ikony pacjentów po każdej turze.
     * (Kompozycja, polimorfizm)
     */
    private void refreshViews() {
        for (Patient p : allPatients) {
            ImageView iv = viewMap.get(p);
            Pane parent = (Pane) iv.getParent();
            parent.getChildren().removeIf(n -> n.getUserData() == iv);

            Circle bg = new Circle(iv.getLayoutX() + iv.getFitWidth() / 2, iv.getLayoutY() + iv.getFitHeight() / 2, iv.getFitWidth() / 2 + 4);
            bg.setUserData(iv);
            bg.setOpacity(0.4);

            // Kolor zależny od stanu pacjenta
            switch (p.getInfectionStatus()) {
                case HEALTHY -> bg.setFill(Color.LIGHTGREEN);
                case INFECTED -> bg.setFill(Color.GOLD);
                case RECOVERED -> bg.setFill(Color.LIGHTBLUE);
                case DECEASED -> bg.setFill(Color.CRIMSON);
            }

            parent.getChildren().add(bg);
            iv.setImage(selectIcon(p));
            iv.toFront();

            // Losowy ruch pacjenta z zabezpieczeniem przed kolizją
            if (p.isAlive() && rnd.nextDouble() < 0.5) {
                for (int i = 0; i < 10; i++) {
                    double dx = rnd.nextDouble() * 8 - 4;
                    double dy = rnd.nextDouble() * 8 - 4;
                    double nx = clamp(iv.getLayoutX() + dx, 0, parent.getWidth() - iv.getFitWidth());
                    double ny = clamp(iv.getLayoutY() + dy, 0, parent.getHeight() - iv.getFitHeight());

                    boolean collision = false;
                    for (Node n : parent.getChildren()) {
                        if (n instanceof ImageView) {
                            ImageView other = (ImageView) n;
                            if (other != iv) {
                                double distX = Math.abs(other.getLayoutX() - nx);
                                double distY = Math.abs(other.getLayoutY() - ny);
                                if (distX < iv.getFitWidth() && distY < iv.getFitHeight()) {
                                    collision = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!collision) {
                        iv.setLayoutX(nx);
                        iv.setLayoutY(ny);
                        break;
                    }
                }
                iv.toFront();
            }
        }

        // Statystyki końcowe dla danego dnia
        int healthy = 0, infected = 0, recovered = 0, deceased = 0;
        for (Patient p : allPatients) {
            if (!p.isAlive()) deceased++;
            else if (p.getInfectionStatus() == Patient.InfectionStatus.RECOVERED) recovered++;
            else if (p.isInfected()) infected++;
            else if (p.isHealthy()) healthy++;
        }

        System.out.printf("Day %d: HEALTHY=%d  INFECTED=%d  RECOVERED=%d  DECEASED=%d%n",
                day, healthy, infected, recovered, deceased);

        try {
            reportWriter.write(String.format("%d;%d;%d;%d;%d\n", day, healthy, infected, recovered, deceased));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ogranicza wartość do zakresu (np. by pacjent nie wyszedł poza salę)
     * @param v wartość
     * @param lo dolny zakres
     * @param hi górny zakres
     * @return ograniczona wartość
     */
    private double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /**
     * Ładowanie konfiguracji symulacji z pliku CSV oraz walidacja wszystkich wartości wejściowych.
     * @param filePath ścieżka do pliku CSV
     * @return obiekt konfiguracji
     * @throws RuntimeException jeśli dane niepoprawne
     */
    private SimulationConfig loadConfig(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            List<SimulationConfig> configs = new CsvToBeanBuilder<SimulationConfig>(reader)
                    .withType(SimulationConfig.class)
                    .withSeparator(',')
                    .build()
                    .parse();

            if (configs.isEmpty()) {
                System.err.println("Configuration file is empty.");
                throw new RuntimeException("Configuration file is empty.");
            }

            SimulationConfig config = configs.get(0);

            // WALIDACJA:
            if (config.getRoomCount() < 1 || config.getRoomCount() > 100) {
                System.err.println("Room count must be between 1 and 100.");
                throw new IllegalArgumentException("Room count must be between 1 and 100.");
            }

            if (config.getPatientCount() <= 0) {
                System.err.println("Patient count must be positive.");
                throw new IllegalArgumentException("Patient count must be positive.");
            }

            int maxPatients = config.getRoomCount() * config.getBedsPerRoom();
            if (config.getPatientCount() > maxPatients) {
                System.err.println("Patient count cannot exceed total available beds (" +
                        maxPatients + ").");
                throw new IllegalArgumentException("Patient count cannot exceed total available beds (" +
                        maxPatients + ").");
            }

            if (config.getMinAge() < 0 || config.getMaxAge() < config.getMinAge()) {
                System.err.println("Invalid age range.");
                throw new IllegalArgumentException("Invalid age range.");
            }

            if (config.getGender() != 'M' && config.getGender() != 'F') {
                System.err.println("Gender must be 'M' or 'F'.");
                throw new IllegalArgumentException("Gender must be 'M' or 'F'.");
            }

            if (config.getAggressiveness() < 0) {
                System.err.println("Aggressiveness must be non-negative.");
                throw new IllegalArgumentException("Aggressiveness must be non-negative.");
            }

            if (config.getSimulationDays() <= 0) {
                System.err.println("Simulation days must be positive.");
                throw new IllegalArgumentException("Simulation days must be positive.");
            }

            if (config.getStep() <= 0) {
                System.err.println("Step duration must be positive.");
                throw new IllegalArgumentException("Step duration must be positive.");
            }

            return config;
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            throw new RuntimeException("Error loading configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Główna metoda uruchomieniowa (startuje JavaFX).
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch(args);
    }
}
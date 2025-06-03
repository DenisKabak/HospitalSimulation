import java.util.List;
import java.util.ArrayList;

/**
 * Klasa Hospital reprezentuje szpital, który zarządza pokojami oraz rozprzestrzenianiem się wirusa.
 *
 * <p>
 * Mechanizmy programowania obiektowego zastosowane w tej klasie:
 * <ul>
 *   <li><b>Hermetyzacja:</b> prywatne pola klasy (rooms, virus)</li>
 *   <li><b>Kompozycja:</b> szpital nie działa bez wirusa, Virus jest elementem Hospital</li>
 *   <li><b>Agregacja:</b> Hospital agreguje listę pokoi (Room), które mogą istnieć niezależnie</li>
 *   <li><b>Polimorfizm:</b> wywoływanie metod p.die() i p.recover() (mogą być przesłonięte w podklasach Patient)</li>
 * </ul>
 */
public class Hospital {
    private final List<Room> rooms;
    private final Virus virus;

    /**
     * Konstruktor klasy Hospital.
     * @param rooms  lista pokoi (agregacja)
     * @param virus  instancja wirusa (kompozycja)
     */
    public Hospital(List<Room> rooms, Virus virus) {
        this.rooms  = rooms;
        this.virus = virus;
    }

    /**
     * Symuluje przebieg jednego dnia w szpitalu:
     * - Zakażenia, zgony, wyzdrowienia
     * - Usuwa zmarłych z pokoi
     *
     * Polimorfizm: wywołania metod die() i recover() mogą różnie działać w podklasach Patient.
     */
    public void simulateDay() {
        for (Room room : rooms) {
            // Tworzymy kopię pacjentów, aby można było bezpiecznie modyfikować oryginalną listę
            List<Patient> patients = new ArrayList<>(room.getPatients());
            for (Patient p : patients) {
                if (!p.isAlive()) continue;

                // pobieramy rezultat [zgon, wyzdrowienie]
                boolean[] res = virus.infectPatient(p, room);
                if (res[0]) {
                    p.die();
                } else if (res[1]) {
                    p.recover();
                }
            }
            // Usuwa zmarłych pacjentów z sali (nie zwiększają obłożenia)
            room.removeDeceased();
        }
    }
}
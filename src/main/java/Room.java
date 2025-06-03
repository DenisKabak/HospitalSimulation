import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasa Room reprezentuje pojedynczy pokój w szpitalu.
 *
 * <p>
 * Mechanizmy programowania obiektowego w tej klasie:
 * <ul>
 *   <li><b>Hermetyzacja:</b> prywatne pola klasy (maxBeds, patients)</li>
 *   <li><b>Agregacja:</b> sala agreguje pacjentów (Patient), nie tworzy ich, a jedynie przechowuje</li>
 *   <li><b>Polimorfizm:</b> wywoływanie isAlive() — metoda może być przesłonięta w podklasach Patient</li>
 * </ul>
 */
public class Room {
    private final int maxBeds;
    private final List<Patient> patients = new ArrayList<>();

    /**
     * Konstruktor klasy Room.
     * @param maxBeds maksymalna liczba łóżek w sali (niezmienialna)
     */
    public Room(int maxBeds) {
        this.maxBeds = maxBeds;
    }

    /**
     * Dodaje pacjenta do sali, jeśli jest wolne miejsce.
     * @param p pacjent do dodania
     * @return true jeśli dodano, false jeśli brak miejsca
     */
    public boolean addPatient(Patient p) {
        if (patients.size() >= maxBeds) return false;
        patients.add(p);
        return true;
    }

    /**
     * Zwraca niemodyfikowalną listę wszystkich pacjentów w sali.
     * Dzięki temu hermetyzujemy listę pacjentów przed przypadkową zmianą z zewnątrz.
     * @return niezmienialna lista pacjentów
     */
    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    /**
     * Zwraca obłożenie sali — tylko przez żywych pacjentów.
     * @return liczba żywych pacjentów / liczba łóżek
     */
    public double livingOccupancy() {
        int alive = 0;
        for (Patient p : patients) {
            if (p.isAlive()) alive++;
        }
        return (double) alive / maxBeds;
    }

    /**
     * Usuwa zmarłych pacjentów z sali.
     * Wspiera polimorfizm — rozpoznaje stan pacjenta przez isAlive().
     */
    public void removeDeceased() {
        patients.removeIf(p -> !p.isAlive());
    }
}
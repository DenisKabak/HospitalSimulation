import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala {
    private final int maxLozek;
    private final List<Pacjent> pacjenci = new ArrayList<>();

    public Sala(int maxLozek) {
        this.maxLozek = maxLozek;
    }

    // Dodaje pacjenta, jeśli jest wolne miejsce
    public boolean dodajPacjenta(Pacjent p) {
        if (pacjenci.size() >= maxLozek) return false;
        pacjenci.add(p);
        return true;
    }

    // Zwraca niemodyfikowalną listę wszystkich pacjentów
    public List<Pacjent> getPacjenci() {
        return Collections.unmodifiableList(pacjenci);
    }

    // Zwraca obłożenie sali tylko przez żywych pacjentów
    public double oblozenieZywych() {
        int zywi = 0;
        for (Pacjent p : pacjenci) {
            if (p.czyZyje()) zywi++;
        }
        return (double) zywi / maxLozek;
    }

    // Usuwa zmarłych pacjentów z listy
    public void usunZmarlych() {
        pacjenci.removeIf(p -> !p.czyZyje());
    }
}
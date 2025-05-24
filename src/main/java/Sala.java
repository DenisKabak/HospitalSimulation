// Reprezentuje salę szpitalną i przechowuje pacjentów
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sala {
    private List<Pacjent> pacjenci = new ArrayList<>();
    private int liczbaLozek;

    public Sala(int liczbaLozek) {
        this.liczbaLozek = liczbaLozek;
    }

    public boolean dodajPacjenta(Pacjent p) {
        if (pacjenci.size() < liczbaLozek) {
            pacjenci.add(p);
            return true;
        }
        return false;
    }

    public List<Pacjent> getPacjenci() {
        return pacjenci;
    }

    public double oblozenieZywych() {
        usunZmarlych();
        long zywi = pacjenci.stream().filter(Pacjent::czyZyje).count();
        if (zywi <= 1) return 0.0;
        return (double) zywi / liczbaLozek;
    }

    private void usunZmarlych() {
        Iterator<Pacjent> iterator = pacjenci.iterator();
        while (iterator.hasNext()) {
            Pacjent p = iterator.next();
            if (!p.czyZyje()) iterator.remove();
        }
    }
}

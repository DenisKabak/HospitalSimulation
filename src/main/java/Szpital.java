// Zarządza całym procesem symulacji w skali szpitala
import java.util.List;
import java.util.ArrayList;

public class Szpital {
    private List<Sala> sale;
    private Wirus wirus;

    public Szpital(List<Sala> sale, Wirus wirus) {
        this.sale = sale;
        this.wirus = wirus;
    }

    public void symulujDzien(int dzien) {
        for (Sala sala : sale) {
            List<Pacjent> pacjenciKopia = new ArrayList<>(sala.getPacjenci());
            for (Pacjent pacjent : pacjenciKopia) {
                if (!pacjent.czyZyje()) continue;

                boolean[] wynik = wirus.zarazPacjenta(pacjent, sala, dzien);

                if (wynik[0]) {
                    pacjent.umrzyj();
                } else if (wynik[1]) {
                    pacjent.ozdrowiej();
                }
            }
        }
    }

    public int policzZywych() {
        int licznik = 0;
        for (Sala sala : sale) {
            for (Pacjent pacjent : sala.getPacjenci()) {
                if (pacjent.czyZyje()) licznik++;
            }
        }
        return licznik;
    }

    public int policzZmarlych() {
        int licznik = 0;
        for (Sala sala : sale) {
            for (Pacjent pacjent : sala.getPacjenci()) {
                if (pacjent.getStanInfekcji() == Pacjent.StanInfekcji.ZMARL) licznik++;
            }
        }
        return licznik;
    }
}
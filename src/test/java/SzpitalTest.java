import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SzpitalTest {

    @Test
    public void testDodajPacjentaDoSali() {
        Sala sala = new Sala(3);
        Pacjent pacjent = new ZdrowyPacjent(30, 'M', false, false, true);
        assertTrue(sala.dodajPacjenta(pacjent));
    }

    @Test
    public void testSymulacjaZarazeniaIOzdrowienia() {
        Sala sala = new Sala(5);
        Wirus wirus = new Wirus(5); // Średnia agresywność

        Pacjent p1 = new ZdrowyPacjent(25, 'K', false, false, false);
        Pacjent p2 = new ZdrowyPacjent(40, 'M', true, true, false);
        Pacjent p3 = new ZdrowyPacjent(70, 'K', true, true, false);

        sala.dodajPacjenta(p1);
        sala.dodajPacjenta(p2);
        sala.dodajPacjenta(p3);

        for (int dzien = 1; dzien <= 10; dzien++) {
            // Tworzymy snapshot, żeby nie modyfikować listy podczas iteracji
            List<Pacjent> pacjenciSnapshot = new ArrayList<>(sala.getPacjenci());

            for (Pacjent pacjent : pacjenciSnapshot) {
                if (!pacjent.czyZyje()) continue;

                if (pacjent.czyZarazony()) {
                    pacjent.inkrementujDzienZarazenia();
                    if (pacjent.getDniOdZarazenia() >= 3) {
                        double ryzyko = pacjent.obliczRyzykoZgonu(wirus.getAgresywnosc(), sala.oblozenieZywych());
                        if (Math.random() < ryzyko) {
                            pacjent.umrzyj();
                        } else {
                            pacjent.ozdrowiej();
                        }
                    }
                } else if (pacjent.czyNiezarazony()) {
                    pacjent.zwiekszEkspozycje();
                    if (pacjent.getDniEkspozycji() >= 2) {
                        boolean[] wynik = wirus.zarazPacjenta(pacjent, sala, dzien);
                        if (wynik[0]) pacjent.zakazSie();
                        pacjent.resetujEkspozycje();
                    }
                }
            }
        }

        long zywi = sala.getPacjenci().stream().filter(Pacjent::czyZyje).count();
        assertTrue(zywi > 0, "Przynajmniej jeden pacjent powinien przeżyć symulację.");
    }
}
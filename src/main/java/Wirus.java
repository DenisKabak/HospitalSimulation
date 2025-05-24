// Reprezentuje wirusa, oblicza ryzyko zakażenia i zgonu
import java.util.Random;

public class Wirus {
    private int agresywnosc;
    private Random r = new Random();

    public Wirus(int agresywnosc) {
        this.agresywnosc = agresywnosc;
    }

    public int getAgresywnosc() {
        return agresywnosc;
    }

    private int okresInkubacji(Pacjent pacjent) {
        int wiek = pacjent.getWiek();
        int podstawowy;
        if (wiek < 35) podstawowy = 2;
        else if (wiek < 50) podstawowy = 3;
        else if (wiek < 65) podstawowy = 4;
        else if (wiek < 75) podstawowy = 5;
        else podstawowy = 6;
        return podstawowy + r.nextInt(2);
    }

    private int obliczOdpornosc(Pacjent pacjent) {
        int odpornosc = 0;
        int wiek = pacjent.getWiek();
        if (wiek < 20) odpornosc += 30;
        else if (wiek < 50) odpornosc += 15;
        if (pacjent.czyZaszczepiony()) odpornosc += 30;
        if (!pacjent.czyPrzewlekleChory()) odpornosc += 10;
        if (!pacjent.maNalogi()) odpornosc += 10;
        if (pacjent.getPlec() == 'K') odpornosc += 5;
        return odpornosc;
    }

    public boolean[] zarazPacjenta(Pacjent pacjent, Sala sala, int dzien) {
        if (pacjent.isOdpornyNaZawsze()) return new boolean[]{false, false};

        double oblozenie = sala.oblozenieZywych();

        if (pacjent.czyNiezarazony()) {
            int odp = obliczOdpornosc(pacjent);
            if (odp >= 60 && pacjent.getDniEkspozycji() > 5) return new boolean[]{false, false};

            double kontaktSzansa = oblozenie * 0.8;
            if (r.nextDouble() > kontaktSzansa) return new boolean[]{false, false};

            double dzienneRyzyko = agresywnosc * 5 + oblozenie * 50;
            if (pacjent.czyPrzewlekleChory()) dzienneRyzyko += 15;
            if (pacjent.czyZaszczepiony()) dzienneRyzyko *= 0.3;

            pacjent.zwiekszEkspozycje();
            double skumulowaneRyzyko = dzienneRyzyko * Math.sqrt(pacjent.getDniEkspozycji());
            skumulowaneRyzyko *= (1 - odp / 100.0);
            skumulowaneRyzyko = Math.min(skumulowaneRyzyko, 30);

            double los = r.nextDouble() * 100;
            if (los < skumulowaneRyzyko || (pacjent.getDniEkspozycji() == 0 && r.nextDouble() < 0.01)) {
                pacjent.zakazSie();
            }
            return new boolean[]{false, false};
        }

        if (pacjent.czyZarazony()) {
            pacjent.inkrementujDzienZarazenia();
            if (pacjent.getDniOdZarazenia() < okresInkubacji(pacjent)) {
                return new boolean[]{false, false};
            }

            int wiek = pacjent.getWiek();
            double ryzyko;

            if (wiek < 35) ryzyko = 0.004;
            else if (wiek < 45) ryzyko = 0.068;
            else if (wiek < 55) ryzyko = 0.23;
            else if (wiek < 65) ryzyko = 0.75;
            else if (wiek < 75) ryzyko = 2.5;
            else if (wiek < 85) ryzyko = 8.5;
            else ryzyko = 28.3;

            if (pacjent.czyPrzewlekleChory()) ryzyko += 5;
            if (pacjent.maNalogi()) ryzyko += 5;
            if (pacjent.getPlec() == 'M') ryzyko += 5;
            if (pacjent.czyZaszczepiony()) ryzyko *= 0.2;

            double los = r.nextDouble() * 100;
            boolean zgon = los < ryzyko;
            boolean przeszedl = !zgon;
            return new boolean[]{zgon, przeszedl};
        }
        return new boolean[]{false, false};
    }
}

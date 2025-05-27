import java.util.Random;

public class Wirus {
    private final double agresywnosc;
    private final Random rnd = new Random();

    public Wirus(double agresywnosc) {
        this.agresywnosc = agresywnosc;
    }

    // Okres inkubacji w dniach, zależny od wieku + losowy dodatek 0–2
    private int okresInkubacji(Pacjent pacjent) {
        int wiek = pacjent.getWiek();
        int podstawowy;
        if (wiek < 35)      podstawowy = 2;
        else if (wiek < 50) podstawowy = 3;
        else if (wiek < 65) podstawowy = 4;
        else if (wiek < 75) podstawowy = 5;
        else                podstawowy = 6;
        return podstawowy + rnd.nextInt(3);
    }

    // Oblicza indywidualną odporność pacjenta (0–~65%)
    private int obliczOdpornosc(Pacjent p) {
        int odp = 0;
        int wiek = p.getWiek();
        if (wiek < 20)          odp += 30;
        else if (wiek < 50)     odp += 15;
        if (p.czyZaszczepiony())    odp += 30;
        if (!p.czyPrzewlekleChory()) odp += 10;
        if (!p.maNalogi())           odp += 10;
        if (p.getPlec() == 'K')      odp += 5;
        return odp;
    }

    /*
     Przebieg choroby:
     [0] = zgon, [1] = wyzdrowienie */
    public boolean[] zarazPacjenta(Pacjent p, Sala sala, int dzien) {
        // 0) jeśli trwała odporność — bez zmian
        if (p.isOdpornyNaZawsze()) {
            return new boolean[]{false, false};
        }

        double oblozenie = sala.oblozenieZywych();

        // 1) NIEZARAZONY → liczymy szansę zakażenia
        if (p.czyNiezarazony()) {

            // jeśli pacjent jest sam w sali (żywych ≤ 1), nie może się zarazić
            int liczbaZywych = 0;
            for (Pacjent pac : sala.getPacjenci()) {
                if (pac.czyZyje()) liczbaZywych++;
            }
            if (liczbaZywych <= 1) {
                return new boolean[]{false, false};
            }

            int odp = obliczOdpornosc(p);
            // jeśli odporność wysoka i >5 dni ekspozycji -> nie zarazi się
            if (odp >= 60 && p.getDniEkspozycji() > 5) {
                return new boolean[]{false, false};
            }
            // czy w ogóle dochodzi do kontaktu?
            double kontaktSzansa = oblozenie * 0.8;
            if (rnd.nextDouble() > kontaktSzansa) {
                p.zwiekszEkspozycje();
                return new boolean[]{false, false};
            }
            // obliczamy dzienne ryzyko zakażenia(doszło do kontaktu)
            double dzienneRyzyko = agresywnosc * 5 + oblozenie * 50;
            if (p.czyPrzewlekleChory()) dzienneRyzyko += 15;
            if (p.czyZaszczepiony())     dzienneRyzyko *= 0.3;

            p.zwiekszEkspozycje();
            double kumul = dzienneRyzyko * Math.sqrt(p.getDniEkspozycji());
            kumul *= (1 - odp / 100.0);
            kumul = Math.min(kumul, 30);

            // finalne losowanie
            if (rnd.nextDouble() * 100 < kumul || (p.getDniEkspozycji() == 0 && rnd.nextDouble() < 0.1)) {
                p.zakazSie();
            }
            return new boolean[]{false, false};
        }

        // 2) ZARAŻONY → po inkubacji decydujemy o zgonie / wyzdrowieniu
        if (p.czyZarazony()) {
            p.inkrementujDzienZarazenia();
            if (p.getDniOdZarazenia() < okresInkubacji(p)) {
                return new boolean[]{false, false};
            }
            // ryzyko zgonu przekazywane do pacjenta (klasa zależna)
            double ryz = p.obliczRyzykoZgonu(agresywnosc, p.getWiek());
            boolean zgon = rnd.nextDouble() * 100 < ryz;
            return new boolean[]{ zgon, !zgon };
        }

        // 3) OZDROWIENIEC lub ZMARŁ → bez zmian
        return new boolean[]{false, false};
    }
}
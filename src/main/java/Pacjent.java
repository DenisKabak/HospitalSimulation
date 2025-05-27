public abstract class Pacjent {
    public enum StanInfekcji {
        NIEZARAZONY,
        ZARAZONY,
        OZDROWIENIEC,
        ZMARL
    }

    private int wiek;
    private char plec;
    private boolean nalogi;
    private boolean przewlekleChory;
    private boolean zaszczepiony;
    private boolean odpornyNaZawsze;
    private StanInfekcji stanInfekcji = StanInfekcji.NIEZARAZONY;
    private int dniOdZarazenia = 0;
    private int dniEkspozycji = 0;

    public Pacjent(int wiek, char plec, boolean nalogi,
                   boolean przewlekleChory, boolean zaszczepiony) {
        this.wiek = wiek;
        this.plec = plec;
        this.nalogi = nalogi;
        this.przewlekleChory = przewlekleChory;
        this.zaszczepiony = zaszczepiony;

        // losowa „genetyczna” odporność
        double sz = 0.01;
        if (wiek < 20) sz += 0.10;
        else if (wiek < 40) sz += 0.05;
        if (!przewlekleChory) sz += 0.05;
        if (!nalogi) sz += 0.05;
        if (plec == 'K') sz += 0.02;

        this.odpornyNaZawsze = Math.random() < sz;
    }

    // ——— gettery —————
    public int getWiek() {
        return wiek;
    }

    public char getPlec() {
        return plec;
    }

    public boolean maNalogi() {
        return nalogi;
    }

    public boolean czyPrzewlekleChory() {
        return przewlekleChory;
    }

    public boolean czyZaszczepiony() {
        return zaszczepiony;
    }

    public StanInfekcji getStanInfekcji() {
        return stanInfekcji;
    }

    public int getDniOdZarazenia() {
        return dniOdZarazenia;
    }

    public int getDniEkspozycji() {
        return dniEkspozycji;
    }

    public boolean isOdpornyNaZawsze() {
        return odpornyNaZawsze;
    }

    public boolean czyZyje() {
        return stanInfekcji != StanInfekcji.ZMARL;
    }

    public boolean czyZarazony() {
        return stanInfekcji == StanInfekcji.ZARAZONY;
    }

    public boolean czyNiezarazony() {
        return stanInfekcji == StanInfekcji.NIEZARAZONY;
    }

    // ——— zmiana stanu ———
    public void zakazSie() {
        stanInfekcji = StanInfekcji.ZARAZONY;
        dniOdZarazenia = 0;
        dniEkspozycji = 0;
    }

    public void inkrementujDzienZarazenia() {
        if (czyZarazony()) dniOdZarazenia++;
    }

    public void zwiekszEkspozycje() {
        if (czyNiezarazony()) dniEkspozycji++;
    }

    public void resetujEkspozycje() {
        dniEkspozycji = 0;
    }

    public void ozdrowiej() {
        stanInfekcji = StanInfekcji.OZDROWIENIEC;
    }

    public void umrzyj() {
        stanInfekcji = StanInfekcji.ZMARL;
    }

    //Każdy podtyp pacjenta oblicza swoje ryzyko zgonu
    public abstract double obliczRyzykoZgonu(double agresywnosc, int wiek);
}
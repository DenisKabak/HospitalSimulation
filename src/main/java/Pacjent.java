// Opis klasy pacjenta i jego stanów zdrowia
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

    public Pacjent(int wiek, char plec, boolean nalogi, boolean przewlekleChory, boolean zaszczepiony) {
        this.wiek = wiek;
        this.plec = plec;
        this.nalogi = nalogi;
        this.przewlekleChory = przewlekleChory;
        this.zaszczepiony = zaszczepiony;

        double szansa = 0.01; // bazowa
        if (wiek < 20) szansa += 0.10;
        else if (wiek < 40) szansa += 0.05;
        if (!przewlekleChory) szansa += 0.05;
        if (!nalogi) szansa += 0.05;
        if (plec == 'K') szansa += 0.02;

        this.odpornyNaZawsze = new java.util.Random().nextDouble() < szansa;
    }

    public int getWiek() { return wiek; }
    public char getPlec() { return plec; }
    public boolean maNalogi() { return nalogi; }
    public boolean czyZyje() { return stanInfekcji != StanInfekcji.ZMARL; }
    public boolean czyZaszczepiony() { return zaszczepiony; }
    public boolean czyPrzewlekleChory() { return przewlekleChory; }
    public boolean isOdpornyNaZawsze() { return odpornyNaZawsze; }

    public boolean czyZarazony() { return stanInfekcji == StanInfekcji.ZARAZONY; }
    public boolean czyNiezarazony() { return stanInfekcji == StanInfekcji.NIEZARAZONY; }

    public void zakazSie() {
        stanInfekcji = StanInfekcji.ZARAZONY;
        dniOdZarazenia = 0;
        dniEkspozycji = 0;
    }

    public void ozdrowiej() { stanInfekcji = StanInfekcji.OZDROWIENIEC; }
    public void umrzyj() { stanInfekcji = StanInfekcji.ZMARL; }
    public StanInfekcji getStanInfekcji() { return stanInfekcji; }

    public void inkrementujDzienZarazenia() {
        if (czyZarazony()) dniOdZarazenia++;
    }

    public int getDniOdZarazenia() { return dniOdZarazenia; }
    public void zwiekszEkspozycje() { dniEkspozycji++; }
    public int getDniEkspozycji() { return dniEkspozycji; }
    public void resetujEkspozycje() { dniEkspozycji = 0; }

    public abstract double obliczRyzykoZgonu(int agresywnosc, double oblozenie);
}

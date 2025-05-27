public class ChoryPacjent extends Pacjent {

    public ChoryPacjent(int wiek, char plec, boolean nalogi,
                        boolean przewlekleChory, boolean zaszczepiony) {
        super(wiek, plec, nalogi, przewlekleChory, zaszczepiony);
    }

    @Override
    public double obliczRyzykoZgonu(double agresywnosc, int wiek) {
        double ryzyko;

        if (wiek < 35)      ryzyko = 0.004;
        else if (wiek < 45) ryzyko = 0.068;
        else if (wiek < 55) ryzyko = 0.23;
        else if (wiek < 65) ryzyko = 0.75;
        else if (wiek < 75) ryzyko = 2.5;
        else if (wiek < 85) ryzyko = 8.5;
        else                ryzyko = 28.3;

        ryzyko *= agresywnosc;
        if (czyPrzewlekleChory()) ryzyko += 15;
        if (maNalogi())           ryzyko += 5;
        if (getPlec() == 'M')     ryzyko += 5;
        if (czyZaszczepiony())    ryzyko *= 0.2;

        return agresywnosc*ryzyko;
    }
}
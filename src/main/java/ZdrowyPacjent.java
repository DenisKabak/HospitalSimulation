public class ZdrowyPacjent extends Pacjent {

    public ZdrowyPacjent(int wiek, char plec, boolean nalogi,
                         boolean przewlekleChory, boolean zaszczepiony) {
        super(wiek, plec, nalogi, przewlekleChory, zaszczepiony);
    }

    @Override
    public double obliczRyzykoZgonu(double agresywnosc, int wiek) {
        double ryzyko = 0;

        if (getWiek() >= 70) ryzyko += 25;
        else if (wiek >= 50) ryzyko += 15;
        else if (wiek >= 20) ryzyko += 5;

        if (maNalogi()) ryzyko += 5;

        if (getPlec() == 'M') ryzyko += 10;
        else if (getPlec() == 'K') ryzyko -= 10;

        if (czyZaszczepiony())    ryzyko *= 0.2;

        return agresywnosc*ryzyko;
    }
}
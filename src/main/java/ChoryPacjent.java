// Pacjent chory - logika zakażenia z większym ryzykiem
public class ChoryPacjent extends Pacjent {
    public ChoryPacjent(int wiek, char plec, boolean nalogi, boolean przewlekleChory, boolean zaczpieni) {
        super(wiek, plec, nalogi, przewlekleChory, zaczpieni);
    }

    @Override
    public double obliczRyzykoZgonu(int agresywnosc, double oblozenie) {
        double ryzyko = agresywnosc * 5;

        if (maNalogi()) ryzyko += 15;

        int wiek = getWiek();
        if (wiek >= 70) ryzyko += 25;
        else if (wiek >= 50) ryzyko += 15;
        else if (wiek >= 20) ryzyko += 5;

        if (getPlec() == 'M') ryzyko += 10;
        else if (getPlec() == 'K') ryzyko -= 10;

        ryzyko *= (1 + oblozenie);
        return ryzyko;
    }
}

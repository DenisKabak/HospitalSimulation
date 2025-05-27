import java.util.List;
import java.util.ArrayList;

public class Szpital {
    private final List<Sala> sale;
    private final Wirus wirus;

    public Szpital(List<Sala> sale, Wirus wirus) {
        this.sale  = sale;
        this.wirus = wirus;
    }

    public void symulujDzien(int dzien) {
        for (Sala sala : sale) {
            // kopia żeby móc modyfikować salę
            List<Pacjent> pacjenci = new ArrayList<>(sala.getPacjenci());
            for (Pacjent p : pacjenci) {
                if (!p.czyZyje()) continue;

                // pobieramy rezultat [zgon, wyzdrowienie]
                boolean[] res = wirus.zarazPacjenta(p, sala, dzien);
                if (res[0]) {
                    p.umrzyj();
                } else if (res[1]) {
                    p.ozdrowiej();
                }
            }
            // usuń zmarłych, żeby nie siedzieli w obłożeniu
            sala.usunZmarlych();
        }
    }
}
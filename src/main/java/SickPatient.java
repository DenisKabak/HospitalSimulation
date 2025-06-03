/**
 * Klasa SickPatient reprezentuje pacjenta przewlekle chorego w symulacji.
 *
 * <p>
 * Mechanizmy OOP zastosowane w tej klasie:
 * <ul>
 *   <li><b>Dziedziczenie:</b> rozszerza klasę abstrakcyjną {@link Patient}</li>
 *   <li><b>Polimorfizm:</b> implementuje metodę {@code calculateDeathRisk} wymuszoną przez klasę bazową</li>
 *   <li><b>Hermetyzacja:</b> korzysta wyłącznie z publicznych getterów zdefiniowanych w Patient</li>
 * </ul>
 */
public class SickPatient extends Patient {

    /**
     * Tworzy nowego przewlekle chorego pacjenta.
     *
     * @param age             wiek pacjenta
     * @param gender          płeć pacjenta ('M' lub 'K')
     * @param addictions      czy pacjent ma nałogi
     * @param chronicallyIll  czy pacjent jest przewlekle chory
     * @param vaccinated      czy pacjent jest zaszczepiony
     */
    public SickPatient(int age, char gender, boolean addictions,
                       boolean chronicallyIll, boolean vaccinated) {
        super(age, gender, addictions, chronicallyIll, vaccinated);
    }

    /**
     * Implementacja abstrakcyjnej metody calculateDeathRisk.
     * <p>
     * Polimorfizm — każdy typ pacjenta ma własny sposób liczenia ryzyka zgonu.
     *
     * @param aggressiveness poziom agresywności wirusa
     * @param age            wiek pacjenta (można też użyć getAge())
     * @return ryzyko zgonu w procentach
     */
    @Override
    public double calculateDeathRisk(double aggressiveness, int age) {
        double risk;

        if (age < 35)      risk = 0.004;
        else if (age < 45) risk = 0.068;
        else if (age < 55) risk = 0.23;
        else if (age < 65) risk = 0.75;
        else if (age < 75) risk = 2.5;
        else if (age < 85) risk = 8.5;
        else               risk = 28.3;

        risk *= aggressiveness;

        if (isChronicallyIll()) risk += 15;
        if (hasAddictions())    risk += 5;
        if (getGender() == 'M') risk += 5;
        if (isVaccinated())     risk *= 0.2;

        return aggressiveness * risk;
    }
}
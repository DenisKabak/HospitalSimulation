/**
 * Klasa HealthyPatient reprezentuje zdrowego pacjenta w symulacji.
 * <p>
 * Mechanizmy OOP zastosowane w tej klasie:
 * <ul>
 *   <li><b>Dziedziczenie:</b> rozszerza klasę abstrakcyjną {@link Patient}</li>
 *   <li><b>Polimorfizm:</b> implementuje metodę {@code calculateDeathRisk} wymuszoną przez klasę bazową</li>
 *   <li><b>Hermetyzacja:</b> korzysta wyłącznie z publicznych getterów zdefiniowanych w Patient</li>
 * </ul>
 */
public class HealthyPatient extends Patient {

    /**
     * Tworzy nowego zdrowego pacjenta.
     *
     * @param age             wiek pacjenta
     * @param gender          płeć pacjenta
     * @param addictions      czy pacjent ma nałogi
     * @param chronicallyIll  czy pacjent jest przewlekle chory
     * @param vaccinated      czy pacjent jest zaszczepiony
     */
    public HealthyPatient(int age, char gender, boolean addictions,
                          boolean chronicallyIll, boolean vaccinated) {
        super(age, gender, addictions, chronicallyIll, vaccinated);
    }

    /**
     * Implementacja abstrakcyjnej metody calculateDeathRisk.
     * <p>
     * Polimorfizm — każdy typ pacjenta może mieć własny sposób liczenia ryzyka zgonu.
     *
     * @param aggressiveness poziom agresywności wirusa
     * @param age            wiek pacjenta (nieużywany — brany z getAge())
     * @return ryzyko zgonu w procentach
     */
    @Override
    public double calculateDeathRisk(double aggressiveness, int age) {
        double risk = 0;

        // Polimorfizm: korzystamy z getterów odziedziczonych po Patient
        if (getAge() >= 70) risk += 25;
        else if (getAge() >= 50) risk += 15;
        else if (getAge() >= 20) risk += 5;

        if (hasAddictions()) risk += 5;

        // Polimorfizm: kobieta ('K') obniża ryzyko
        if (getGender() == 'M') risk += 10;
        else if (getGender() == 'K') risk -= 10;

        if (isVaccinated()) risk *= 0.2;

        return aggressiveness * risk;
    }
}

/*
 Mechanizmy programowania obiektowego zastosowane w tej klasie:
 - Dziedziczenie: HealthyPatient rozszerza klasę abstrakcyjną Patient i dziedziczy jej pola i metody
 - Polimorfizm: implementuje abstrakcyjną metodę calculateDeathRisk zdefiniowaną w klasie Patient
 - Hermetyzacja: korzysta z getterów odziedziczonych z klasy Patient, które chronią prywatne pola
*/
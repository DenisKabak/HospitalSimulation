/**
 * Klasa abstrakcyjna Patient reprezentuje ogólny model pacjenta w symulacji.
 *
 * <p>
 * Mechanizmy programowania obiektowego w tej klasie:
 * <ul>
 *   <li><b>Klasa abstrakcyjna:</b> nie można tworzyć instancji Patient bezpośrednio.</li>
 *   <li><b>Hermetyzacja:</b> wszystkie pola są prywatne, dostęp tylko przez gettery/settery/metody.</li>
 *   <li><b>Dziedziczenie:</b> podklasy rozszerzają Patient i implementują calculateDeathRisk().</li>
 *   <li><b>Polimorfizm:</b> podtypy Patient (np. HealthyPatient, SickPatient) dostarczają własną logikę.</li>
 *   <li><b>Kompozycja:</b> pole InfectionStatus jest agregowane w Patient.</li>
 * </ul>
 */
public abstract class Patient {

    /**
     * Enum reprezentujący stan infekcji pacjenta.
     * Umożliwia czytelne porównania i obsługę różnych stanów (polimorfizm na poziomie stanu).
     */
    public enum InfectionStatus {
        HEALTHY, INFECTED, RECOVERED, DECEASED
    }

    // Hermetyzacja: wszystkie pola są prywatne
    private int age;
    private char gender;
    private boolean addictions;
    private boolean chronicallyIll;
    private boolean vaccinated;
    private boolean permanentlyImmune;
    private InfectionStatus infectionStatus = InfectionStatus.HEALTHY;
    private int daysSinceInfection = 0;
    private int exposureDays = 0;

    /**
     * Konstruktor chroniący hermetyzację — ustawia wszystkie dane pacjenta.
     * Oblicza losową, genetyczną odporność na bazie cech pacjenta.
     */
    public Patient(int age, char gender, boolean addictions,
                   boolean chronicallyIll, boolean vaccinated) {
        this.age = age;
        this.gender = gender;
        this.addictions = addictions;
        this.chronicallyIll = chronicallyIll;
        this.vaccinated = vaccinated;

        // Losowa genetyczna odporność
        double chance = 0.01;
        if (age < 20) chance += 0.10;
        else if (age < 40) chance += 0.05;
        if (!chronicallyIll) chance += 0.05;
        if (!addictions) chance += 0.05;
        if (gender == 'F') chance += 0.02;

        this.permanentlyImmune = Math.random() < chance;
    }

    // ——— Gettery (hermetyzacja) ———

    /** @return Wiek pacjenta */
    public int getAge() { return age; }

    /** @return Płeć ('M' lub 'F') */
    public char getGender() { return gender; }

    /** @return Czy pacjent ma nałogi */
    public boolean hasAddictions() { return addictions; }

    /** @return Czy pacjent przewlekle chory */
    public boolean isChronicallyIll() { return chronicallyIll; }

    /** @return Czy pacjent zaszczepiony */
    public boolean isVaccinated() { return vaccinated; }

    /** @return Aktualny stan infekcji pacjenta */
    public InfectionStatus getInfectionStatus() { return infectionStatus; }

    /** @return Liczba dni od zakażenia */
    public int getDaysSinceInfection() { return daysSinceInfection; }

    /** @return Liczba dni ekspozycji (jako zdrowy) */
    public int getExposureDays() { return exposureDays; }

    /** @return Czy pacjent posiada trwałą odporność */
    public boolean isPermanentlyImmune() { return permanentlyImmune; }

    /** @return Czy pacjent żyje */
    public boolean isAlive() { return infectionStatus != InfectionStatus.DECEASED; }

    /** @return Czy pacjent jest zarażony */
    public boolean isInfected() { return infectionStatus == InfectionStatus.INFECTED; }

    /** @return Czy pacjent jest zdrowy */
    public boolean isHealthy() { return infectionStatus == InfectionStatus.HEALTHY; }

    // ——— Metody zmieniające stan (hermetyzacja, polimorfizm) ———

    /** Oznacza pacjenta jako zarażonego, resetuje liczniki dni */
    public void infect() {
        infectionStatus = InfectionStatus.INFECTED;
        daysSinceInfection = 0;
        exposureDays = 0;
    }

    /** Inkrementuje dni od zakażenia, jeśli pacjent jest zarażony */
    public void incrementInfectionDay() {
        if (isInfected()) daysSinceInfection++;
    }

    /** Inkrementuje dni ekspozycji, tylko jeśli pacjent zdrowy */
    public void increaseExposure() {
        if (isHealthy()) exposureDays++;
    }

    /** Oznacza pacjenta jako ozdrowieńca */
    public void recover() {
        infectionStatus = InfectionStatus.RECOVERED;
    }

    /** Oznacza pacjenta jako zmarłego */
    public void die() {
        infectionStatus = InfectionStatus.DECEASED;
    }

    /**
     * Abstrakcyjna metoda do obliczania ryzyka zgonu — realizowana w podklasach.
     * Przykład polimorfizmu: HealthyPatient i SickPatient mają inną implementację.
     * @param aggressiveness agresywność wirusa
     * @param age wiek pacjenta
     * @return szacowane ryzyko śmierci (%)
     */
    public abstract double calculateDeathRisk(double aggressiveness, int age);
}
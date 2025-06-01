import java.util.Random;

/**
 * Klasa Virus modeluje zachowanie wirusa w symulacji.
 *
 * <p>
 * Mechanizmy programowania obiektowego użyte w tej klasie:
 * <ul>
 *     <li><b>Hermetyzacja:</b> pola prywatne chronią stan przed zewnętrzną modyfikacją</li>
 *     <li><b>Kompozycja:</b> Virus zawiera Random jako swój składnik</li>
 *     <li><b>Agregacja:</b> Virus współpracuje z Room i Patient, ale ich nie posiada</li>
 *     <li><b>Polimorfizm:</b> metody Patient są polimorficzne — mogą być przesłonięte w podklasach</li>
 * </ul>
 */
public class Virus {
    private final double aggressiveness; // Hermetyzacja — pole prywatne, chronione przed bezpośrednim dostępem
    private final Random rnd = new Random(); // Kompozycja — użycie klasy Random wewnątrz klasy Virus

    /**
     * Tworzy wirusa o zadanej agresywności.
     * @param aggressiveness poziom agresywności wirusa
     */
    public Virus(double aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    /**
     * Okres inkubacji w dniach, zależny od wieku + losowy dodatek 0–2 dni.
     * Hermetyzacja: metoda prywatna, nieudostępniana na zewnątrz.
     */
    private int incubationPeriod(Patient patient) {
        int age = patient.getAge();
        int base;
        if (age < 35)      base = 2;
        else if (age < 50) base = 3;
        else if (age < 65) base = 4;
        else if (age < 75) base = 5;
        else               base = 6;
        return base + rnd.nextInt(3);
    }

    /**
     * Oblicza indywidualną odporność pacjenta (0–~65%).
     * Hermetyzacja: metoda pomocnicza, dostępna tylko wewnątrz klasy.
     */
    private int calculateResistance(Patient p) {
        int res = 0;
        int age = p.getAge();
        if (age < 20)          res += 30;
        else if (age < 50)     res += 15;
        if (p.isVaccinated())    res += 30;
        if (!p.isChronicallyIll()) res += 10;
        if (!p.hasAddictions())    res += 10;
        if (p.getGender() == 'F')  res += 5;
        return res;
    }

    /**
     * Główna metoda symulacji przebiegu zakażenia pacjenta.
     * <p>
     * Zwraca tablicę boolean[2]:
     * [0] = zgon, [1] = wyzdrowienie.
     * <br>
     * Mechanizmy OOP:
     * <ul>
     *   <li>Polimorfizm: Patient może być HealthyPatient lub SickPatient (dziedziczenie i różne implementacje ryzyka zgonu).</li>
     *   <li>Agregacja: Virus współpracuje z Room i Patient.</li>
     * </ul>
     */
    public boolean[] infectPatient(Patient p, Room room) {
        // 0) jeśli trwała odporność — bez zmian
        if (p.isPermanentlyImmune()) {
            return new boolean[]{false, false};
        }

        double occupancy = room.livingOccupancy();

        // 1) NIEZARAZONY → liczymy szansę zakażenia
        if (p.isHealthy()) {

            // jeśli pacjent jest sam w sali (żywych ≤ 1), nie może się zarazić
            int livingCount = 0;
            for (Patient pac : room.getPatients()) {
                if (pac.isAlive()) livingCount++;
            }
            if (livingCount <= 1) {
                return new boolean[]{false, false};
            }

            int res = calculateResistance(p);
            // jeśli odporność wysoka i >5 dni ekspozycji -> nie zarazi się
            if (res >= 60 && p.getExposureDays() > 5) {
                return new boolean[]{false, false};
            }
            // czy w ogóle dochodzi do kontaktu?
            double contactChance = occupancy * 0.8;
            if (rnd.nextDouble() > contactChance) {
                return new boolean[]{false, false};
            }
            // obliczamy dzienne ryzyko zakażenia (doszło do kontaktu)
            double dailyRisk = aggressiveness * 5 + occupancy * 50;
            if (p.isChronicallyIll()) dailyRisk += 15;
            if (p.isVaccinated())     dailyRisk *= 0.3;

            p.increaseExposure();
            double cumulative = dailyRisk * Math.sqrt(p.getExposureDays());
            cumulative *= (1 - res / 100.0);
            cumulative = Math.min(cumulative, 30);

            // finalne losowanie (polimorfizm — metoda infect() może być przesłonięta)
            if (rnd.nextDouble() * 100 < cumulative || (p.getExposureDays() == 0 && rnd.nextDouble() < 0.1)) {
                p.infect();
            }
            return new boolean[]{false, false};
        }

        // 2) ZARAŻONY → po inkubacji decydujemy o zgonie / wyzdrowieniu
        if (p.isInfected()) {
            p.incrementInfectionDay();
            if (p.getDaysSinceInfection() < incubationPeriod(p)) {
                return new boolean[]{false, false};
            }
            // ryzyko zgonu przekazywane do pacjenta (polimorfizm)
            double risk = p.calculateDeathRisk(aggressiveness, p.getAge());
            boolean death = rnd.nextDouble() * 100 < risk;
            return new boolean[]{ death, !death };
        }

        // 3) OZDROWIENIEC lub ZMARŁ → bez zmian
        return new boolean[]{false, false};
    }
}

/*
   Mechanizmy OOP użyte w tej klasie:
   - Hermetyzacja (pola prywatne: aggressiveness, rnd)
   - Kompozycja (użycie klasy Random jako składnika klasy Virus)
   - Agregacja (Virus współpracuje z Room i Patient, ale nie jest ich właścicielem)
   - Polimorfizm (Patient to superklasa — może być HealthyPatient lub SickPatient)
   - Brak interfejsów ani klas abstrakcyjnych w tej konkretnej klasie
*/
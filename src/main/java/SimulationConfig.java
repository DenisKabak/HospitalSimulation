import com.opencsv.bean.CsvBindByName;

/**
 * Klasa DTO służąca do wczytywania i przechowywania konfiguracji symulacji.
 *
 * <p>
 * Mechanizmy OOP zastosowane w tej klasie:
 * <ul>
 *   <li><b>Hermetyzacja:</b> wszystkie pola są prywatne, dostęp przez gettery/settery</li>
 *   <li><b>Brak dziedziczenia:</b> klasa nie dziedziczy ani nie implementuje interfejsu</li>
 *   <li><b>Agregacja:</b> dane tej klasy są wykorzystywane przez inne klasy w modelu symulacji</li>
 * </ul>
 *
 * <p>
 * Wartości wczytywane są z pliku CSV dzięki adnotacji {@link CsvBindByName}.
 */
public class SimulationConfig {

    /** Liczba sal w szpitalu */
    @CsvBindByName
    private int roomCount;

    /** Liczba łóżek w jednej sali */
    @CsvBindByName
    private int bedsPerRoom;

    /** Łączna liczba pacjentów */
    @CsvBindByName
    private int patientCount;

    /** Minimalny wiek pacjenta */
    @CsvBindByName
    private int minAge;

    /** Maksymalny wiek pacjenta */
    @CsvBindByName
    private int maxAge;

    /** Płeć pacjentów ('M' albo 'F') */
    @CsvBindByName
    private char gender;

    /** Czy pacjenci mają nałogi */
    @CsvBindByName
    private boolean addictions;

    /** Czy pacjenci są przewlekle chorzy */
    @CsvBindByName
    private boolean chronic;

    /** Czy pacjenci są zaszczepieni */
    @CsvBindByName
    private boolean vaccinated;

    /** Agresywność wirusa */
    @CsvBindByName
    private double aggressiveness;

    /** Liczba dni trwania symulacji */
    @CsvBindByName
    private int simulationDays;

    /** Krok czasowy symulacji (w sekundach) */
    @CsvBindByName
    private double step;

    // --- Hermetyzacja: gettery i settery ---

    /** @return liczba sal w szpitalu */
    public int getRoomCount() { return roomCount; }
    public void setRoomCount(int roomCount) { this.roomCount = roomCount; }

    /** @return liczba łóżek w jednej sali */
    public int getBedsPerRoom() { return bedsPerRoom; }
    public void setBedsPerRoom(int bedsPerRoom) { this.bedsPerRoom = bedsPerRoom; }

    /** @return liczba pacjentów */
    public int getPatientCount() { return patientCount; }
    public void setPatientCount(int patientCount) { this.patientCount = patientCount; }

    /** @return minimalny wiek */
    public int getMinAge() { return minAge; }
    public void setMinAge(int minAge) { this.minAge = minAge; }

    /** @return maksymalny wiek */
    public int getMaxAge() { return maxAge; }
    public void setMaxAge(int maxAge) { this.maxAge = maxAge; }

    /** @return płeć pacjentów */
    public char getGender() { return gender; }
    public void setGender(char gender) { this.gender = gender; }

    /** @return czy pacjenci mają nałogi */
    public boolean isAddictions() { return addictions; }
    public void setAddictions(boolean addictions) { this.addictions = addictions; }

    /** @return czy pacjenci są przewlekle chorzy */
    public boolean isChronic() { return chronic; }
    public void setChronic(boolean chronic) { this.chronic = chronic; }

    /** @return czy pacjenci są zaszczepieni */
    public boolean isVaccinated() { return vaccinated; }
    public void setVaccinated(boolean vaccinated) { this.vaccinated = vaccinated; }

    /** @return agresywność wirusa */
    public double getAggressiveness() { return aggressiveness; }
    public void setAggressiveness(double aggressiveness) { this.aggressiveness = aggressiveness; }

    /** @return liczba dni trwania symulacji */
    public int getSimulationDays() { return simulationDays; }
    public void setSimulationDays(int simulationDays) { this.simulationDays = simulationDays; }

    /** @return długość kroku czasowego */
    public double getStep() { return step; }
    public void setStep(double step) { this.step = step; }
}
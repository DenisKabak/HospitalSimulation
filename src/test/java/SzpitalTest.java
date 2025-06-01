import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SzpitalTest {
    @Test
    public void testRoomFullOccupancy() {
        Room room = new Room(2);
        assertTrue(room.addPatient(new HealthyPatient(25, 'M', false, false, false)));
        assertTrue(room.addPatient(new HealthyPatient(35, 'F', false, false, false)));
        assertFalse(room.addPatient(new HealthyPatient(45, 'M', false, false, false)));
    }
    @Test
    public void testPatientInitialState() {
        Patient patient = new HealthyPatient(30, 'M', false, false, false);
        assertTrue(patient.isAlive());
        assertTrue(patient.isHealthy());
        assertFalse(patient.isInfected());
    }
    @Test
    public void testInfectPatient() {
        Virus virus = new Virus(1.0);
        Room room = new Room(3);
        Patient patient1 = new HealthyPatient(30, 'M', false, false, false);
        Patient patient2 = new HealthyPatient(40, 'F', false, false, false);

        room.addPatient(patient1);
        room.addPatient(patient2);

        boolean[] result = virus.infectPatient(patient1, room);

        assertNotNull(result);
        assertEquals(2, result.length);
    }
    @Test
    public void testPatientDeceased() {
        Patient patient = new HealthyPatient(65, 'F', false, false, false);
        assertTrue(patient.isAlive());

        patient.die();

        assertFalse(patient.isAlive());
        assertEquals(Patient.InfectionStatus.DECEASED, patient.getInfectionStatus());
    }
    @Test
    public void testLivingOccupancy() {
        Room room = new Room(4);
        Patient p1 = new HealthyPatient(30, 'M', false, false, false);
        Patient p2 = new HealthyPatient(40, 'F', false, false, false);
        Patient p3 = new HealthyPatient(50, 'M', false, false, false);
        Patient p4 = new HealthyPatient(60, 'F', false, false, false);
        p4.die();

        room.addPatient(p1);
        room.addPatient(p2);
        room.addPatient(p3);
        room.addPatient(p4);

        double occupancy = room.livingOccupancy();
        assertEquals(0.75, occupancy, 0.001);
    }
}
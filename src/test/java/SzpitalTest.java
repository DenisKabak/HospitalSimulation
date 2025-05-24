import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SzpitalTest {
    @Test
    public void testDodajPacjentaDoSali() {
        Sala sala = new Sala(3);
        Pacjent pacjent = new ZdrowyPacjent(30, 'M', false, false, false);
        assertTrue(sala.dodajPacjenta(pacjent));
    }
}

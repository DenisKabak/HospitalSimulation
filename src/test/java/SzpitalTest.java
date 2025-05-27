import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SzpitalTest {

    @Test
    public void testDodajPacjentaDoSali() {
        Sala sala = new Sala(3);
        Pacjent pacjent = new ZdrowyPacjent(30, 'M', false, false, true);
        assertTrue(sala.dodajPacjenta(pacjent));
    }
    @Test
    public void testPrzekroczenieLimituLozek() {
        Sala sala = new Sala(2);
        assertTrue(sala.dodajPacjenta(new ZdrowyPacjent(30, 'M', false, false, true)));
        assertTrue(sala.dodajPacjenta(new ZdrowyPacjent(40, 'K', false, false, true)));
        assertFalse(sala.dodajPacjenta(new ZdrowyPacjent(50, 'M', false, false, true)));
    }
}
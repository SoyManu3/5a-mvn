import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FiveATest {
    @TempDir
    Path tempDirectory;

    @AfterEach
    void removeOutput() throws Exception {
        Files.deleteIfExists(Path.of("output.txt"));
    }

    @Test
    void gammaSupportsIntegerAndNonIntegerValues() {
        GammaFunction gamma = new GammaFunction();

        gamma.computeIntGamma(5);
        assertEquals(24.0, gamma.getGammaValue(), 1e-10);

        gamma.computeDblGamma(0.5);
        assertEquals(Math.sqrt(Math.PI), gamma.getGammaValue(), 1e-10);
    }

    @Test
    void gammaRejectsInvalidValues() {
        GammaFunction gamma = new GammaFunction();

        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.0));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(Double.POSITIVE_INFINITY));
    }

    @Test
    void pdfReferenceCasesUseSimpsonIntegration() {
        assertEquals(0.36757, integrate(1.18, 12), 3e-3);
        assertEquals(0.49500, integrate(2.75, 300), 3e-3);
    }

    @Test
    void simpsonUsesEvenSegmentsAndHandlesZero() {
        SimpsonIntegration integration = new SimpsonIntegration();
        integration.computeW(5, 0.0);
        integration.computeXi(5);
        integration.computeFirstBaseTerms(5, integration.getXi(), 10);
        integration.computeExponent(10);
        integration.computeCoefficient(10);
        integration.computeFxi(5);
        integration.computeFinalTerms(5);
        integration.computeFinalValue(5);

        assertEquals(6, integration.getXi().length - 1);
        assertEquals(0.0, integration.getFinalValue(), 1e-12);
    }

    @Test
    void logicRejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 0, 1e-8));
        assertThrows(IllegalArgumentException.class, () -> runLogic(Double.NaN, 10, 1e-8));
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 10, 0.0));
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 10, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> runLogic(Double.POSITIVE_INFINITY, 10, 1e-8));
    }

    @Test
    void appAndOutputWriteUtf8Result() throws Exception {
        App.main(new String[] {"4", "1e-6", "12", "1.19"});
        assertTrue(Files.exists(Path.of("output.txt")));

        Path file = tempDirectory.resolve("result.txt");
        new output().writeData(file.toString(), "resultado: 0.5");
        assertEquals("resultado: 0.5", Files.readString(file, StandardCharsets.UTF_8));
    }

    private double integrate(double x, int degreesOfFreedom) {
        SimpsonIntegration integration = new SimpsonIntegration();
        integration.computeW(1000, x);
        integration.computeXi(1000);
        integration.computeFirstBaseTerms(1000, integration.getXi(), degreesOfFreedom);
        integration.computeExponent(degreesOfFreedom);
        integration.computeCoefficient(degreesOfFreedom);
        integration.computeFxi(1000);
        integration.computeFinalTerms(1000);
        integration.computeFinalValue(1000);
        return integration.getFinalValue();
    }

    private void runLogic(double x, int degreesOfFreedom, double tolerance) {
        Logic logic = new Logic();
        logic.setData(5, tolerance, degreesOfFreedom, x);
        logic.logic1();
    }
}
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
    void gammaComputesIntegerValue() {
        GammaFunction gamma = new GammaFunction();
        gamma.computeIntGamma(5);
        assertEquals(24.0, gamma.getGammaValue(), 1e-10);
    }

    @Test
    void gammaComputesHalfValue() {
        GammaFunction gamma = new GammaFunction();
        gamma.computeDblGamma(0.5);
        assertEquals(Math.sqrt(Math.PI), gamma.getGammaValue(), 1e-10);
    }

    @Test
    void gammaComputesQuarterValue() {
        GammaFunction gamma = new GammaFunction();
        gamma.computeDblGamma(0.25);
        assertEquals(3.625609908221908, gamma.getGammaValue(), 1e-10);
    }

    @Test
    void gammaRejectsZero() {
        GammaFunction gamma = new GammaFunction();
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(0.0));
    }

    @Test
    void gammaRejectsNaN() {
        GammaFunction gamma = new GammaFunction();
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(Double.NaN));
    }

    @Test
    void gammaRejectsPositiveInfinity() {
        GammaFunction gamma = new GammaFunction();
        assertThrows(IllegalArgumentException.class, () -> gamma.computeDblGamma(Double.POSITIVE_INFINITY));
    }

    @Test
    void pdfReferenceCaseForTwelveDegreesOfFreedom() {
        assertEquals(0.36757, integrate(1.18, 12), 3e-3);
    }

    @Test
    void pdfReferenceCaseForThreeHundredDegreesOfFreedom() {
        assertEquals(0.49500, integrate(2.75, 300), 3e-3);
    }
////////////////
    @Test
    void appCalculatesReferenceCaseOne() throws Exception {
        assertEquals(0.3500586369, runApp(0.0, 1.1, 9), 1e-10);
    }

    @Test
    void appCalculatesReferenceCaseTwo() throws Exception {
        assertEquals(0.3675734052, runApp(0.0, 1.1812, 10), 1e-10);
    }

    @Test
    void appCalculatesReferenceCaseThree() throws Exception {
        assertEquals(0.4949998582, runApp(0.0, 2.750, 30), 1e-10);
    }
/////////////////////////
    @Test
    void simpsonUsesEvenSegments() {
        SimpsonIntegration integration = createIntegration(5, 0.0, 10);
        assertEquals(6, integration.getXi().length - 1);
    }

    @Test
    void simpsonHandlesZero() {
        SimpsonIntegration integration = createIntegration(5, 0.0, 10);
        assertEquals(0.0, integration.getFinalValue(), 1e-12);
    }

    @Test
    void simpsonUsesMinimumSegments() {
        SimpsonIntegration integration = new SimpsonIntegration();
        integration.computeW(1, 0.0);
        integration.computeXi(1);
        assertEquals(3, integration.getXi().length);
    }

    @Test
    void logicRejectsZeroDegreesOfFreedom() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 0, 1e-8));
    }

    @Test
    void logicRejectsNaNLimit() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(Double.NaN, 10, 1e-8));
    }

    @Test
    void logicRejectsZeroTolerance() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 10, 0.0));
    }

    @Test
    void logicRejectsInfiniteTolerance() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(1.0, 10, Double.POSITIVE_INFINITY));
    }

    @Test
    void logicRejectsInfiniteLimit() {
        assertThrows(IllegalArgumentException.class, () -> runLogic(Double.POSITIVE_INFINITY, 10, 1e-8));
    }

    @Test
    void logicRejectsNaNInitialValue() {
        Logic logic = new Logic();
        logic.setData(Double.NaN, 1.0, 10);
        assertThrows(IllegalArgumentException.class, logic::logic1);
    }

    @Test
    void appWritesOutputFile() throws Exception {
        App.main(new String[] {"0", "1.19", "12"});
        assertTrue(Files.exists(Path.of("output.txt")));
    }

    @Test
    void outputWritesUtf8Result() throws Exception {
        Path file = tempDirectory.resolve("result.txt");
        new output().writeData(file.toString(), "resultado: 0.5");
        assertEquals("resultado: 0.5", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void outputRejectsDirectory() {
        assertThrows(IllegalStateException.class,
                () -> new output().writeData(tempDirectory.toString(), "no se puede escribir en un directorio"));
    }

    @Test
    void appUsesDefaultValues() throws Exception {
        new App();
        App.main(new String[0]);
        assertTrue(Files.exists(Path.of("output.txt")));
    }

    @Test
    void logicSupportsThreeArgumentSetter() throws Exception {
        Logic logic = new Logic();
        logic.setData(1, 10, 1.0);
        logic.logic1();
        assertTrue(Double.parseDouble(Files.readString(Path.of("output.txt"))) > 0.0);
    }

    @Test
    void logicSupportsLegacyFourArgumentSetter() throws Exception {
        Logic logic = new Logic();
        logic.setData(11, 1e-5, 10, 1.0);
        logic.logic1();
        assertTrue(Double.parseDouble(Files.readString(Path.of("output.txt"))) > 0.0);
    }

    private SimpsonIntegration createIntegration(int segments, double x, int degreesOfFreedom) {
        SimpsonIntegration integration = new SimpsonIntegration();
        integration.computeW(segments, x);
        integration.computeXi(segments);
        integration.computeFirstBaseTerms(segments, integration.getXi(), degreesOfFreedom);
        integration.computeExponent(degreesOfFreedom);
        integration.computeCoefficient(degreesOfFreedom);
        integration.computeFxi(segments);
        integration.computeFinalTerms(segments);
        integration.computeFinalValue(segments);
        return integration;
    }

    private double runApp(double initialValue, double finalValue, int degreesOfFreedom) throws Exception {
        App.main(new String[] {
                Double.toString(initialValue),
                Double.toString(finalValue),
                Integer.toString(degreesOfFreedom)
        });
        return Double.parseDouble(Files.readString(Path.of("output.txt")));
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

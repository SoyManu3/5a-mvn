public class Logic {
    private int intNumSeg = 2;
    private double dblE = 1.0e-8;
    private int intDOF = 1;
    private double dblX = 1.0;

    public void logic1() {
        if (intDOF <= 0) {
            throw new IllegalArgumentException("Los grados de libertad deben ser positivos");
        }
        if (dblE <= 0.0) {
            throw new IllegalArgumentException("La tolerancia debe ser positiva");
        }

        int segments = evenSegments(intNumSeg);
        double previousValue = Double.NaN;
        double currentValue;

        do {
            SimpsonIntegration integration = new SimpsonIntegration();
            integration.computeW(segments, dblX);
            integration.computeXi(segments);
            integration.computeFirstBaseTerms(segments, integration.getXi(), intDOF);
            integration.computeExponent(intDOF);
            integration.computeCoefficient(intDOF);
            integration.computeFxi(segments);
            integration.computeFinalTerms(segments);
            integration.computeFinalValue(segments);
            currentValue = integration.getFinalValue();

            if (!Double.isNaN(previousValue) && Math.abs(currentValue - previousValue) < dblE) {
                output writer = new output();
                writer.writeData("output.txt", Double.toString(currentValue));
                intNumSeg = segments;
                return;
            }

            previousValue = currentValue;
            segments *= 2;
        } while (true);
    }

    public void setData(int intNumSeg, double dblE, int intDOF, double dblX) {
        this.intNumSeg = intNumSeg;
        this.dblE = dblE;
        this.intDOF = intDOF;
        this.dblX = dblX;
    }

    private int evenSegments(int segments) {
        if (segments < 2) {
            return 2;
        }
        return segments % 2 == 0 ? segments : segments + 1;
    }
}

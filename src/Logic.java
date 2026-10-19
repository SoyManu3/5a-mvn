public class Logic {
    private int intNumSeg = 10;
    private double dblE = 0.00001;
    private int intDOF = 1;
    private double dblInitial = 0.0;
    private double dblFinal = 1.0;

    public void logic1() {
        if (intDOF <= 0) {
            throw new IllegalArgumentException("Los grados de libertad deben ser positivos");
        }
        if (!Double.isFinite(dblE) || dblE <= 0.0) {
            throw new IllegalArgumentException("La tolerancia debe ser positiva y finita");
        }
        if (!Double.isFinite(dblInitial) || !Double.isFinite(dblFinal)) {
            throw new IllegalArgumentException("Los límites de integración deben ser finitos");
        }

        int segments = evenSegments(intNumSeg);
        double previousValue = Double.NaN;
        double currentValue;

        do {
            SimpsonIntegration integration = new SimpsonIntegration();
            integration.computeW(segments, dblInitial, dblFinal);
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
        this.dblInitial = 0.0;
        this.dblFinal = dblX;
    }

    public void setData(int intNumSeg, int intDOF, double dblX) {
        this.intNumSeg = intNumSeg;
        this.intDOF = intDOF;
        this.dblInitial = 0.0;
        this.dblFinal = dblX;
        this.dblE = 0.00001;
    }

    public void setData(double dblInitial, double dblFinal, int intDOF) {
        this.intNumSeg = 10;
        this.dblE = 0.00001;
        this.intDOF = intDOF;
        this.dblInitial = dblInitial;
        this.dblFinal = dblFinal;
    }

    private int evenSegments(int segments) {
        if (segments < 2) {
            return 2;
        }
        return segments % 2 == 0 ? segments : segments + 1;
    }
}

public class SimpsonIntegration {
    private int intNumSeg;
    private double dblW;
    private double dblE;
    private int intDOF;
    private double dblInitial;
    private double dblX;
    private double[] dblTotXi;
    private double[] dblFirstBaseTerms;
    private double dblExponent;
    private double dblCoeff;
    private double[] dblFxi;
    private double[] dblFinalTerms;
    private double dblFinalValue;

    public void computeW(int intNumSeg, double dblX) {
        computeW(intNumSeg, 0.0, dblX);
    }

    public void computeW(int intNumSeg, double dblInitial, double dblFinal) {
        this.intNumSeg = evenSegments(intNumSeg);
        this.dblX = dblFinal;
        dblW = (dblFinal - dblInitial) / this.intNumSeg;
        this.dblInitial = dblInitial;
    }

    public void computeXi(int intNumSeg) {
        int segments = evenSegments(intNumSeg);
        dblTotXi = new double[segments + 1];
        for (int index = 0; index <= segments; index++) {
            dblTotXi[index] = dblInitial + index * dblW;
        }
    }

    public double[] getXi() {
        return dblTotXi;
    }

    public void computeFirstBaseTerms(int intNumSeg, double[] dblTotXi, int intDOF) {
        int segments = evenSegments(intNumSeg);
        dblFirstBaseTerms = new double[segments + 1];
        for (int index = 0; index <= segments; index++) {
            dblFirstBaseTerms[index] = 1.0
                    + (dblTotXi[index] * dblTotXi[index]) / intDOF;
        }
    }

    public void computeExponent(int intDOF) {
        dblExponent = -(intDOF + 1.0) / 2.0;
    }

    public void computeCoefficient(int intDOF) {
        GammaFunction gammaFunction = new GammaFunction();
        gammaFunction.computeDblGamma((intDOF + 1.0) / 2.0);
        double numerator = gammaFunction.getGammaValue();
        gammaFunction.computeDblGamma(intDOF / 2.0);
        double denominator = Math.sqrt(intDOF * Math.PI) * gammaFunction.getGammaValue();
        dblCoeff = numerator / denominator;
    }

    public void computeFxi(int intNumSeg) {
        int segments = evenSegments(intNumSeg);
        dblFxi = new double[segments + 1];
        for (int index = 0; index <= segments; index++) {
            dblFxi[index] = dblCoeff * Math.pow(dblFirstBaseTerms[index], dblExponent);
        }
    }

    public void computeFinalTerms(int intNumSeg) {
        int segments = evenSegments(intNumSeg);
        dblFinalTerms = new double[segments + 1];
        for (int index = 0; index <= segments; index++) {
            if (index == 0 || index == segments) {
                dblFinalTerms[index] = dblFxi[index];
            } else if (index % 2 == 0) {
                dblFinalTerms[index] = 2.0 * dblFxi[index];
            } else {
                dblFinalTerms[index] = 4.0 * dblFxi[index];
            }
        }
    }

    public void computeFinalValue(int intNumSeg) {
        int segments = evenSegments(intNumSeg);
        double sum = 0.0;
        for (double term : dblFinalTerms) {
            sum += term;
        }
        // Regla compuesta de Simpson: integral = (h / 3) por la suma ponderada.
        dblFinalValue = (dblW / 3.0) * sum;
    }

    public double getFinalValue() {
        return dblFinalValue;
    }

    private int evenSegments(int segments) {
        if (segments < 2) {
            return 2;
        }
        return segments % 2 == 0 ? segments : segments + 1;
    }
}

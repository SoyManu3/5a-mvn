public class GammaFunction {
    private double gammaValue;

    public void computeIntGamma(int intValue) {
        computeDblGamma(intValue);
    }

    public void computeDblGamma(double doubleValue) {
        if (doubleValue <= 0.0 || Double.isNaN(doubleValue)) {
            throw new IllegalArgumentException("El argumento de gamma debe ser positivo");
        }

        if (doubleValue < 0.5) {
            gammaValue = Math.PI
                    / (Math.sin(Math.PI * doubleValue) * gamma(doubleValue + 1.0));
        } else {
            gammaValue = gamma(doubleValue);
        }
    }

    public double getGammaValue() {
        return gammaValue;
    }

    private double gamma(double value) {
        double[] coefficients = {
                0.9999999999998099,
                676.5203681218851,
                -1259.1392167224028,
                771.3234287776531,
                -176.6150291621406,
                12.507343278686905,
                -0.13857109526572012,
                9.984369578019572e-6,
                1.5056327351493116e-7
        };

        double shiftedValue = value - 1.0;
        double sum = coefficients[0];
        for (int index = 1; index < coefficients.length; index++) {
            sum += coefficients[index] / (shiftedValue + index);
        }

        double t = shiftedValue + coefficients.length - 1.5;
        return Math.sqrt(2.0 * Math.PI)
                * Math.pow(t, shiftedValue + 0.5)
                * Math.exp(-t)
                * sum;
    }
}

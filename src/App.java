public class App {
    public static void main(String[] args) {
        Logic logic = new Logic();

        if (args.length >= 4) {
            logic.setData(
                    Integer.parseInt(args[0]),
                    Double.parseDouble(args[1]),
                    Integer.parseInt(args[2]),
                    Double.parseDouble(args[3]));
        }

        logic.logic1();
    }
}

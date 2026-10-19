public class App {
    public static void main(String[] args) {
        Logic logic = new Logic();

        if (args.length >= 3) {
            logic.setData(
                Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Integer.parseInt(args[2]));
        }

        logic.logic1();
    }
}

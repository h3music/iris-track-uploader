package website;

public class progressBar {
    static int totalSize = 50;

    public static void create(String title) {
        int unfilledAmount = totalSize;

        String characters = "-".repeat(Math.max(0, unfilledAmount));
        System.out.println(title);
        System.out.print(characters + " \r");
    }

    public static void update(double percentage) {

        int filledAmount = (int) (percentage * totalSize);
        int unfilledAmount = totalSize - filledAmount;

        String characters = "█".repeat(Math.max(0, filledAmount)) +
                "-".repeat(Math.max(0, unfilledAmount));

        String percentPrint = String.valueOf((percentage * 100));

        System.out.print(characters + "\t" + percentPrint + "% \r");
    }
}

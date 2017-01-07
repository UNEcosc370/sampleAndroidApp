package au.edu.une.cosc370.stroop;

import java.util.ArrayList;
import java.util.Random;

/**
 * Holds the data for a Stroop Test
 */
public class StroopExperiment {

    // How many items to go through
    int maxTurns = 40;

    // How many items at the start to force to have the same colour (and discount from the results)
    int lureCount = 10;

    /**
     * The set of colours we will use for this experiment
     */
    static enum Colour {
        RED(0xffbb0000), GREEN(0xff00bb00), BLUE(0xff0000bb), ORANGE(0xffdd8800), PURPLE(0xffbb00ff), YELLOW(0xfffff010);

        public int col;

        Colour(int col) {
            this.col = col;
        }
    }

    /**
     * Holds the timing of a single item in the Stroop test
     *
     * col: The colour the word is shown in
     * word: The colour the word spells
     * time: Overloaded; if incomplete, the start-time. If complete, how long it took the user to click
     * answer: what the user answered
     */
    class Item {

        Colour col;
        Colour word;
        Colour answer;

        long time;


        Item(Colour col, Colour word, long time) {
            this.col = col;
            this.word = word;
            this.time = time;
        }

        public void setAnswer(Colour answer, long time) {
            this.answer = answer;
            this.time = time - this.time;
        }

    }

    /**
     * A random number generator
     */
    Random rng = new Random();

    /**
     * The history of items that the student has clicked through
     */
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * If non-null: the item on the screen
     * If null: we are between items
     *
     * TODO: use Optional here
     */
    public Item currentItem = null;

    /**
     * Gets the next item, ensuring that the word (not necessarily the colour) is different so that
     * the user can see there has been a change
     * @return
     */
    public Item nextItem() {

        int l = Colour.values().length;

        // Because we can randomly get the same colour too, we only need to force (n/2 - 1)/n occasions to match
        boolean forceSame = items.size() < lureCount || rng.nextFloat() < (l / 2.0 - 1) / l;

        Colour col = Colour.values()[rng.nextInt(l)];

        Item i = new Item(
                col,
                forceSame ? col : Colour.values()[rng.nextInt(l)],
                System.currentTimeMillis()
        );

        if (currentItem == null || i.word != currentItem.word) {
            return i;
        } else {
            return nextItem();
        }
}

    /**
     * Starts the test by triggering the first item
     */
    public void start() {
        currentItem = nextItem();
    }

    /**
     * Clears any data
     */
    public void reset() {
        currentItem = null;
        items.clear();
    }

    /**
     * Answers an item, putting it into the list of answered items
     * @param col
     */
    public void answerItem(Colour col) {
        if (currentItem != null) {
            currentItem.setAnswer(col, System.currentTimeMillis());
            items.add(currentItem);

            if (items.size() < maxTurns) {
                currentItem = nextItem();
            } else {
                currentItem = null;
            }
        } else {
            throw new IllegalStateException("Completing the null item?");
        }
    }

    /**
     * Generates results text. This is a bit lazy, but oh well.
     * @return
     */
    public String results() {

        int numControl = 0;
        int controlTime = 0;
        int controlErr = 0;

        int numInterfere = 0;
        int interfereTime = 0;
        int interfereErr = 0;

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            // Discard the first 5 as practice
            if (i > lureCount) {

                if (item.col == item.word) {
                    numControl++;
                    controlTime += item.time;

                    if (item.answer != item.col) {
                        controlErr++;
                    }
                } else {
                    numInterfere++;
                    interfereTime += item.time;

                    if (item.answer != item.col) {
                        interfereErr++;
                    }
                }
            }

        }

        StringBuilder sb = new StringBuilder();
        sb.append("Where the text matched the colour: \n");
        sb.append(String.format("Accuracy: %d%%. Avg time: %dms \n\n", 100 - (int)(100 * controlErr / numControl), (int)(controlTime / numControl)));
        sb.append("Where the text did not match the colour: \n");
        sb.append(String.format("Accuracy: %d%%. Avg time: %dms \n\n", 100 - (int)(100 * interfereErr / numInterfere), (int)(interfereTime / numInterfere)));

        return sb.toString();
    }
}

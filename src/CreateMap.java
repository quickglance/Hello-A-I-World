/**
 * Created by Hamed on 2/11/2016.
 */
public class CreateMap {

    public static void main(String[] args) {

        int width = 3, height = 2, xDis = 70, yDis = 80, xStart = 420, yStart = 470;

        createSquare(width, height, xDis, yDis, xStart, yStart, 75);
    }

    public static void createSquare(int width, int height, int xDis, int yDis, int xStart, int yStart, int firstIndex) {
        String props = "", adj = "";

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int y = i * yDis + yStart;
                int x = j * xDis + xStart;

                props += "[-1,0," + x + "," + y + "],";
                int index = i * width + j + firstIndex;

                if (i == 0) { // first row
                    if (j == 0) { // first column

                        adj += "[" + (index + 1) + "," + (index + width) + "],";

                    } else if (j == width - 1) { // last column

                        adj += "[" + (index - 1) + "," + (index + width) + "],";

                    } else {

                        adj += "[" + (index - 1) + "," + (index + 1) + "," + (index + width) + "],";

                    }
                } else if (i == height - 1) { // last row
                    if (j == 0) { // first column

                        adj += "[" + (index - width) + "," + (index + 1) + "],";

                    } else if (j == width - 1) { // last column

                        adj += "[" + (index - width) + "," + (index - 1) + "]";

                    } else {

                        adj += "[" + (index - width) + "," + (index - 1) + "," + (index + 1) + "],";

                    }
                } else {
                    if (j == 0) { // first column

                        adj += "[" + (index - width) + "," + (index + 1) + "," + (index + width) + "],";

                    } else if (j == width - 1) { // last column

                        adj += "[" + (index - width) + "," + (index - 1) + "," + (index + width) + "],";

                    } else {

                        adj += "[" + (index - width) + "," + (index - 1) + "," + (index + 1) + "," + (index + width) + "],";

                    }
                }
            }
        }

        System.out.println("props: " + props + "\n" + "adj: " + adj);
    }

}

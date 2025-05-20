import com.javarush.engine.cell.*;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private boolean isGameStopped = false;
    private int score = 0;
    private int[][] gameField = new int[SIDE][SIDE];

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
        setTurnTimer(10);
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }

    private void createNewNumber() {
        boolean isContinue = true;

        if (getMaxTileValue() == 2048) win();

        do {
            int x = getRandomNumber(SIDE);
            int y = getRandomNumber(SIDE);

            if (gameField[x][y] == 0) {
                gameField[x][y] = getRandomNumber(10) == 9 ? 4 : 2;
                isContinue = false;
            }

        } while (isContinue);
    }

    private Color getColorByValue(int value) {
        return switch (value) {
            case 0 -> Color.WHITE;
            case 2 -> Color.LIGHTSALMON;
            case 4 -> Color.INDIGO;
            case 8 -> Color.BLUE;
            case 16 -> Color.SKYBLUE;
            case 32 -> Color.SEAGREEN;
            case 64 -> Color.GREEN;
            case 128 -> Color.ORANGE;
            case 256 -> Color.DARKORANGE;
            case 512 -> Color.RED;
            case 1024 -> Color.PINK;
            case 2048 -> Color.VIOLET;
            default -> null;
        };
    }

    private void setCellColoredNumber(int x, int y, int value) {
        String str = value != 0 ? String.valueOf(value) : "";
        setCellValueEx(x, y, getColorByValue(value), str);
    }

    private boolean compressRow(int[] row) {
        boolean isEdit = false;
        int counter = 0;
        int tmp;

        for (int i = 0; i < row.length; i++) {
            if (row[i] == 0) {
                counter++;
            } else if(counter != 0) {
                tmp = row[i];
                row[i] = row[i-counter];
                row[i-counter] = tmp;
                isEdit = true;
            }
        }

        return isEdit;
    }

    private boolean mergeRow(int[] row) {
        boolean isEdit = false;
        for (int i = 1; i < row.length; i++) {
            if (row[i] != 0 && row[i-1] != 0 && row[i] == row[i-1]) {
                row[i-1] += row[i];
                row[i] = 0;
                isEdit = true;
                score += row[i-1];
                onTurn(10);
            }
        }

        return isEdit;
    }

    @Override
    public void onKeyPress(Key key) {
        if (!canUserMove() && !isGameStopped) gameOver();

        if (key == Key.UP && !isGameStopped) {
            moveUp();
            drawScene();
        } else if (key == Key.RIGHT && !isGameStopped) {
            moveRight();
            drawScene();
        } else if (key == Key.DOWN && !isGameStopped) {
            moveDown();
            drawScene();
        } else if (key == Key.LEFT && !isGameStopped) {
            moveLeft();
            drawScene();
        } else if (key == Key.SPACE && isGameStopped) {
            isGameStopped = false;
            score = 0;
            onTurn(10);
            createGame();
            drawScene();
        }
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveLeft() {
        boolean needNewPlate = false;
        for (int[] row: gameField) {
            boolean isCompressed = compressRow(row);
            boolean isMerged = mergeRow(row);

            if (isMerged) {
                compressRow(row);
            }

            if (isCompressed || isMerged) {
                needNewPlate = true;
            }
        }

        if (needNewPlate) {
            createNewNumber();
        }
    }

    private void rotateClockwise() {
        int[][] result = new int[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                result[j][SIDE - 1 - i] = gameField[i][j];
            }
        }
        gameField = result;
    }

    private int getMaxTileValue() {
        int maxValue = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] > maxValue) maxValue = gameField[i][j];
            }
        }

        return maxValue;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "Поздравляем! Вы победили!", Color.BLACK, 50);
    }

    private boolean canUserMove() {
        boolean isCanUserMove = false;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) isCanUserMove = true;
                else if (i < SIDE-1 && gameField[i][j] == gameField[i+1][j] && !isCanUserMove) isCanUserMove = true;
                else if (j < SIDE-1 && gameField[i][j] == gameField[i][j+1] && !isCanUserMove) isCanUserMove = true;
            }
        }

        return isCanUserMove;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "Вы проиграли!", Color.BLACK, 50);
    }

    @Override
    public void onTurn(int step) {
        setScore(score);
    }
}
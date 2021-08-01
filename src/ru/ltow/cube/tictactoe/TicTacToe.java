package ru.ltow.cube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;

public class TicTacToe {
  //private static final String TAG = "ttt";
  public static enum Status { NONE, AITURN, GAMEOVER };
  private Status status = TicTacToe.Status.NONE;

  //duplicated, set cell.value == rendered.model
  public static final int X = 0;
  public static final int O = 1;
  public static final int V = 2;
  public static final int EMPTY = 3;
  public static final int UNUSED = 4;

  public static final boolean AI = false;

  public static int DIMS = 3;
  public static int FIELDSIZE = 3;

  private int[] rowSumPows;
  private int[][] rowCells;
  private int[] field;
  private HashMap<Integer,ArrayList<Integer>> emptyCells;

  //turn manager
  private boolean[] players;
  private int nextMark;
  private boolean finished;
  //

  public TicTacToe(boolean[] p) {
    players = p;
    initGame();
  }

  public void initGame() {
    initField();
    initRows();
    initEmptyCells();
  }

  private void toggleFinished() {finished = !finished;}
  private void status(Status s) {status = s;}
  public Status status() {return status;}
  public int[] field() {return field;}
  public int nextMark() {return nextMark;}

  public void makeTurn(int cell) {
    if(finished) {
      status = TicTacToe.Status.NONE;
      toggleFinished();
      initField();
      aiTurn();
      return;
    }

    if(players[nextMark] != AI) {
      if(!put(nextMark, cell)) return;
      if(!fillEmptyCells()) {gameOver(); return;}
      rotateMark();
    }

    aiTurn();
  }

  private void aiTurn() {
    while(players[nextMark] == AI) {
      if(!ai(nextMark)) {gameOver(); return;}
      rotateMark();
    }
    status = TicTacToe.Status.NONE;
  }

  private void gameOver() {
    nextMark = 0;
    toggleFinished();
    status(TicTacToe.Status.GAMEOVER);
  }

  private void rotateMark() {nextMark = (nextMark < players.length - 1) ? nextMark + 1 : 0;}

  private boolean put(int mark, int cell) {
    if(
      (mark < 0 && mark >= players.length) //mark unknown
     || (cell < 0 || cell >= field.length) //cell number is outside field range
     || (field[cell] != EMPTY) //cell is not empty
     || ((DIMS == 3) && (cell == field.length / 2)) //3d mode and cell is center
     || (mark != nextMark) //not a player's turn
    ) return false;

    field[cell] = mark;

    return true;
  }

  public void togglePlayer(int i) {if(i >= 0 && i < players.length) players[i] = !players[i];}

  private void initField() {
    field = new int[(int) Math.pow(FIELDSIZE, DIMS)];
    Arrays.fill(field, EMPTY);

    if(DIMS == 3) field[field.length / 2] = UNUSED; //UNUSED for center

    status((players[0] == AI) ? TicTacToe.Status.AITURN : TicTacToe.Status.NONE);
  }

  private boolean fillEmptyCells() {
    for(ArrayList<Integer> a : emptyCells.values()) a.clear();

    int[] rowValues = new int[FIELDSIZE];
    int[] rowSums = new int[rowCells.length];

    //for each row
    for(int row = 0; row < rowCells.length; row++) {
      //get cell values
      for(int cell = 0; cell < rowCells[row].length; cell++) {
        rowValues[cell] = field[rowCells[row][cell]];
      }
      //count row sum
      rowSums[row] = countSum(rowValues);

      //check if game is finished
      for(int p = 0; p < rowSumPows.length; p++) {
        if(rowSums[row] == rowSumPows[p] * FIELDSIZE) return false;
      }

      //get empty cells, put to sum map
      for(int cell : rowCells[row]) {
        if(field[cell] == EMPTY) {
          emptyCells.get(rowSums[row]).add(cell);
        }
      }
    }

    for(ArrayList<Integer> a : emptyCells.values()){
      Collections.shuffle(a, new Random());
    }

    return true;
  }

  //migrate to ML/ANN
  private boolean ai(int mark) {
    if(!fillEmptyCells()) return false;

    int next1, next2, mark0, mark1, mark2;

    mark0 = 0;
    mark1 = countSum(new int[]{mark});
    mark2 = countSum(new int[]{mark, mark});
    if(mark + 1 < players.length) {
      next1 = countSum(new int[]{mark + 1});
      next2 = countSum(new int[]{mark + 1, mark + 1});
    } else {
      next1 = countSum(new int[]{0});
      next2 = countSum(new int[]{0, 0});
    }

    //this player m2
    for(int cell : emptyCells.get(mark2)) {
      if(put(mark, cell)) return false;}
    //next player m2
    for(int cell : emptyCells.get(next2)) {
      if(put(mark, cell)) return true;}
    //rest players m2
    for(int m = 0; m < players.length; m++) {
      for(int cell : emptyCells.get(countSum(new int[]{m, m}))) {
        if(put(mark, cell)) return true;}
    }
    //this player m1 intersections
    for(int cell : Utils.findDuplicates(emptyCells.get(mark1))) {
      if(put(mark, cell)) return true;}
    //most empty intersections
    for(int cell : Utils.findDuplicates(emptyCells.get(mark0))) {
      if(put(mark, cell)) return true;}
    //next player m1 intersections
    for(int cell : Utils.findDuplicates(emptyCells.get(next1))) {
      if(put(mark, cell)) return true;}
    //rest players m1 intersections
    for(int m = 0; m < players.length; m++) {
      for(int cell : Utils.findDuplicates(emptyCells.get(countSum(new int[]{m})))) {
        if(put(mark, cell)) return true;}
    }
    //this and next players m1 intersections
    for(int cell : Utils.findUniqueMatches(
      emptyCells.get(next1),
      emptyCells.get(mark1)
    )) {
      if(put(mark, cell)) return true;
    }
    //this and rest players m1 intersections
    for(int m = 0; m < players.length; m++) {
      if(m != mark) {
        for(int cell : Utils.findUniqueMatches(
          emptyCells.get(countSum(new int[]{m})),
          emptyCells.get(mark1)
        )) {
          if(put(mark, cell)) return true;
        }
      }
    }
    //center 
    if(put(mark, (int) (field.length / 2))) return true;
    //m0 intersections
    for(int cell : Utils.findDuplicates(emptyCells.get(mark0))) {
      if(put(mark, cell)) return true;}
    //this player m1
    for(int cell : emptyCells.get(mark1)) {
      if(put(mark, cell)) return true;}
    //next player m1
    for(int cell : emptyCells.get(next1)) {
      if(put(mark, cell)) return true;}
    //rest players m1
    for(int m = 0; m < players.length; m++) {
      for(int cell : emptyCells.get(countSum(new int[]{m}))) {
        if(put(mark, cell)) return true;}
    }
    //any empty cell
    for(int cell = 0; cell < field.length; cell++) {
      if(put(mark, cell)) return true;}

    return false;
  }

  private void initRows() {
    switch(DIMS) {
      case 2:
        rowCells = new int[][]{
          {0,3,6},{1,4,7},{2,5,8}, //vertical
          {0,1,2},{3,4,5},{6,7,8}, //horizontal
          {0,4,8},{2,4,6} //diagonal
        }; break;
      case 3:
        rowCells = new int[][]{
          //face1
          {0,3,6},{1,4,7},{2,5,8},
          {9,12,15},{11,14,17},
          {18,21,24},{19,22,25},{20,23,26},
          //face2
          {0,1,2},{3,4,5},{6,7,8}, 
          {9,10,11},{15,16,17},
          {18,19,20},{21,22,23},{24,25,26},
          //face3
          {0,9,18},{1,10,19},{2,11,20},
          {3,12,21},{5,14,23},
          {6,15,24},{7,16,25},{8,17,26},
          //diagonals1
          {0,10,20},{6,16,26},
          {2,10,18},{8,16,24},
          //diagonals2
          {0,12,24},{2,14,26},
          {6,12,18},{8,14,20},
          //diagonals3
          {0,4,8},{18,22,26},
          {2,4,6},{20,22,24}
        };//*/
      break;
      default: break;
    }
  }

  private void initEmptyCells() {
    int rowSumN = FIELDSIZE + 1;
    int rowSumMax = 0;

    rowSumPows = new int[players.length];
    emptyCells = new HashMap<Integer,ArrayList<Integer>>();

    for(int p = 0; p < rowSumPows.length; p++) {
      rowSumPows[p] = (int) Math.pow(rowSumN, p);
      rowSumMax += FIELDSIZE * rowSumPows[p];
    }
    for(int sum = 0; sum <= rowSumMax; sum++) {
      emptyCells.put(sum, new ArrayList<Integer>());
    }
  }

  private int countSum(int[] v) {
    int result = 0;
    for(int i = 0; i < v.length; i++) {
      if(v[i] != EMPTY) result += rowSumPows[v[i]];
    }
    return result;
  }
}
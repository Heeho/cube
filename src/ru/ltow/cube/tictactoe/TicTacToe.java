package ru.ltow.cube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import android.opengl.Matrix;
//import android.util.Log;

public class TicTacToe {
  //private static final String TAG = "ttt";
  public static enum status { NONE, AITURN, GAMEOVER };
  private status message = TicTacToe.status.NONE;

  public static final int X = 0;
  public static final int O = 1;
  public static final int V = 2;
  public static final int EMPTY = 3;
  public static final int UNUSED = 4;

  public static final boolean AI = false;

  private int dims = 3;
  private int fsize = 3;

  private float animationStep = 0.2f;
  private float fieldAngle = 30f;
  private float cellShift = 2f*1f/6f + 0.001f;

  private int[] rowSumPows;
  private int[][] rowCells;
  private ArrayList<Cell> field;
  private HashMap<Integer,ArrayList<Integer>> emptyCells;

  //turn manager
  private boolean[] players;
  private int nextMark;
  private boolean finished;
  //

  public TicTacToe(boolean[] p) {players = p;}

  public ArrayList<Rendered> initGame() {
    initField();
    initRows();
    initEmptyCells();

    message((players[0] == AI) ? TicTacToe.status.AITURN : TicTacToe.status.NONE);

    return getFieldRendered();
  }

  public ArrayList<Rendered> makeTurn(int cell) {
    if(finished) {
      message = TicTacToe.status.NONE;
      toggleFinished();
      initField();
      return aiTurn();
    }

    if(players[nextMark] != AI) {
      if(!put(nextMark, cell)) return getFieldRendered();
      if(!fillEmptyCells()) return gameOver();

      nextMark = (nextMark < players.length - 1) ? nextMark + 1 : 0;
    }

    return aiTurn();
  }

  private ArrayList<Rendered> aiTurn() {
    while(players[nextMark] == AI) {
      if(!ai(nextMark)) return gameOver();
      nextMark = (nextMark < players.length - 1) ? nextMark + 1 : 0;
    }
    return getFieldRendered();
  }

  private ArrayList<Rendered> gameOver() {
    nextMark = 0;
    toggleFinished();
    message(TicTacToe.status.GAMEOVER);
    return getFieldRendered();
  }

  public ArrayList<Rendered> getFieldRendered() {
    ArrayList<Rendered> result = new ArrayList<Rendered>();
    for(int i = 0; i < field.size(); i++) {
      result.add(field.get(i).getRendered());
    }
    return result;
  }

  private boolean put(int mark, int cell) {
    if(
      (mark < 0 && mark >= players.length) //mark must be known
     || (cell < 0 || cell >= field.size()) //cell number is outside field range
     || (field.get(cell).getValue() != EMPTY) //cell is not empty
     || ((dims == 3) && (cell == field.size() / 2)) //not a center in 3d mode
     || (mark != nextMark) //not a player's turn
    ) return false;

    field.get(cell).setValue(mark);

    return true;
  }

  public void togglePlayer(int i) {if(i >= 0 && i < players.length) players[i] = !players[i];}

  private void initField() {
    Rendered r = null;
    ArrayList<Float> smAL = null;
    field = new ArrayList<Cell>();

    int k = (int) Math.pow(fsize, dims)/fsize;
    int offsetX = (dims > 0) ? fsize/2 : 0;
    int offsetY = (dims > 1) ? fsize/2 : 0;
    int offsetZ = (dims > 2) ? fsize/2 : 0;

    float[] idMatrix = new float[16];
    float[] stateMatrix = new float[16];

    for(int i = 0; i < (int) Math.pow(fsize, dims); i++) {
      smAL = new ArrayList<Float>();
      Matrix.setIdentityM(idMatrix, 0);
      Matrix.rotateM(idMatrix, 0, fieldAngle, 0,1f,0);
      Matrix.rotateM(idMatrix, 0, fieldAngle, 1f,0,0);
      Matrix.translateM(
        stateMatrix, 0,
        idMatrix, 0,
        cellShift * (i % fsize - offsetX),
        cellShift * ((i - i % k) / k - offsetY),
        cellShift * (offsetZ - ((i - i % fsize) - (i - i % k)) / fsize)
      );

      for(float f : stateMatrix) smAL.add(f);

      Cell c = new Cell(smAL, EMPTY);
      c.setId(i);
      r = c.getRendered();
      r.addAnimation(new ScaleUp(r, animationStep));
      field.add(c);
    }

    if(dims == 3) field.get(field.size() / 2).setValue(UNUSED); //UNUSED if no center
  }

  private boolean fillEmptyCells() {
    for(ArrayList<Integer> a : emptyCells.values()) a.clear();

    int[] rowValues = new int[fsize];
    int[] rowSums = new int[rowCells.length];

    //for each row
    for(int row = 0; row < rowCells.length; row++) {
      //get cell values
      for(int cell = 0; cell < rowCells[row].length; cell++) {
        rowValues[cell] = field.get(rowCells[row][cell]).getValue();
      }
      //count row sum
      rowSums[row] = countSum(rowValues);

      //check if game is finished
      for(int p = 0; p < rowSumPows.length; p++) {
        if(rowSums[row] == rowSumPows[p] * fsize) return false;
      }

      //get empty cells, put to sum map
      for(int cell : rowCells[row]) {
        if(field.get(cell).getValue() == EMPTY) {
          emptyCells.get(rowSums[row]).add(cell);
        }
      }
    }

    for(ArrayList<Integer> a : emptyCells.values()){
      Collections.shuffle(a, new Random());
    }

    return true;
  }

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
    //!add most intersections prio
    //this player m1 intersections
    for(int cell : findDuplicates(emptyCells.get(mark1))) {
      if(put(mark, cell)) return true;}
    //next player m1 intersections
    for(int cell : findDuplicates(emptyCells.get(next1))) {
      if(put(mark, cell)) return true;}
    //rest players m1 intersections
    for(int m = 0; m < players.length; m++) {
      for(int cell : findDuplicates(emptyCells.get(countSum(new int[]{m})))) {
        if(put(mark, cell)) return true;}
    }
    //this and next players m1 intersections
    for(int cell : findUniqueMatches(
      emptyCells.get(next1),
      emptyCells.get(mark1)
    )) {
      if(put(mark, cell)) return true;
    }
    //this and rest players m1 intersections
    for(int m = 0; m < players.length; m++) {
      if(m != mark) {
        for(int cell : findUniqueMatches(
          emptyCells.get(countSum(new int[]{m})),
          emptyCells.get(mark1)
        )) {
          if(put(mark, cell)) return true;
        }
      }
    }
    //center 
    if(put(mark, (int) (field.size() / 2))) return true;
    //m0 intersections
    for(int cell : findDuplicates(emptyCells.get(mark0))) {
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
    for(int cell = 0; cell < field.size(); cell++) {
      if(put(mark, cell)) return true;}

    return false;
  }

  private Set<Integer> findDuplicates(ArrayList<Integer> a) {
    Set<Integer> uniques = new HashSet<Integer>();
    Set<Integer> duplicates = new HashSet<Integer>();

    for(int cell : a) {
      if(!uniques.add(cell)) {
        if(!duplicates.add(cell)) {
          //if false, move element to 0
          Set<Integer> duplicates_ = new HashSet<Integer>();
          duplicates_.add(cell);
          duplicates_.addAll(duplicates);
          duplicates = duplicates_;
        }
      }
    }
    return duplicates;
  }

  private Set<Integer> findUniqueMatches(ArrayList<Integer> a, ArrayList<Integer> b) {
    Set<Integer> set1 = new HashSet<Integer>(a);
    Set<Integer> set2 = new HashSet<Integer>(b);
    Set<Integer> matches = new HashSet<Integer>();

    for(int cell : set1) {
      if(!set2.add(cell)) {
        matches.add(cell);
      }
    }
    return matches;
  }

  private void initRows() {
    switch(dims) {
      case 2:
        rowCells = new int[][]{
          {0,3,6},{1,4,7},{2,5,8}, //vertical
          {0,1,2},{3,4,5},{6,7,8}, //horizontal
          {0,4,8},{2,4,6} //diagonal
        }; break;
      case 3:
        /*rowCells = new int[][]{
          //face1
          {0,3,6},{1,4,7},{2,5,8},
          {9,12,15},{10,13,16},{11,14,17},
          {18,21,24},{19,22,25},{20,23,26},
          //face2
          {0,1,2},{3,4,5},{6,7,8}, 
          {9,10,11},{12,13,14},{15,16,17},
          {18,19,20},{21,22,23},{24,25,26},
          //face3
          {0,9,18},{1,10,19},{2,11,20},
          {3,12,21},{4,13,22},{5,14,23},
          {6,15,24},{7,16,25},{8,17,26},
          //diagonals1
          {0,10,20},{3,13,23},{6,16,26},
          {2,10,18},{5,13,21},{8,16,24},
          //diagonals2
          {0,12,24},{1,13,25},{2,14,26},
          {6,12,18},{7,13,19},{8,14,20},
          //diagonals3
          {0,4,8},{9,13,17},{18,22,26},
          {2,4,6},{11,13,15},{20,22,24},
          //diagonals main
          {0,13,26},{2,13,24},{6,13,20},{8,13,18}
        };
        /*///no center
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
    int rowSumN = fsize + 1;
    int rowSumMax = 0;

    rowSumPows = new int[players.length];
    emptyCells = new HashMap<Integer,ArrayList<Integer>>();

    for(int p = 0; p < rowSumPows.length; p++) {
      rowSumPows[p] = (int) Math.pow(rowSumN, p);
      rowSumMax += fsize * rowSumPows[p];
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

  private void toggleFinished() {finished = !finished;}

  private void message(status s) {message = s;}
  public status message() {return message;}
}
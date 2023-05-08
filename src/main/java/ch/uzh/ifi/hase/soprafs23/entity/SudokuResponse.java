package ch.uzh.ifi.hase.soprafs23.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SudokuResponse {
    @JsonProperty("newboard")
    private NewBoard newBoard;

    public NewBoard getNewBoard() {
        return newBoard;
    }

    public void setNewBoard(NewBoard newBoard) {
        this.newBoard = newBoard;
    }

    public static class NewBoard {
        private List<Grid> grids;

        public List<Grid> getGrids() {
            return grids;
        }

        public void setGrids(List<Grid> grids) {
            this.grids = grids;
        }
    }

    public static class Grid {
        private String difficulty;
        private String[][] value;

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String[][] getValue() {
            return value;
        }

        public void setValue(String[][] value) {
            this.value = value;
        }
    }
}

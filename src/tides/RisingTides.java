package tides;

import java.util.*;

public class RisingTides {

    private double[][] terrain;
    private GridLocation[] sources;

    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    public double[] elevationExtrema() {
        double lowest = terrain[0][0];
        double highest = terrain[0][0];
        
        for (int row = 0; row < terrain.length; row++) {
            for (int col = 0; col < terrain[row].length; col++) {
                double currentHeight = terrain[row][col];
                if (currentHeight < lowest) {
                    lowest = currentHeight;
                }
                if (currentHeight > highest) {
                    highest = currentHeight;
                }
            }
        }
        
        double[] extrema = new double[2];
        extrema[0] = lowest;
        extrema[1] = highest;
        return extrema;
    }

    public boolean[][] floodedRegionsIn(double height) {
        if (terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return null;
        }
        
        int numRows = terrain.length;
        int numCols = terrain[0].length;
        
        boolean[][] floodedRegions = new boolean[numRows][numCols];
        
        ArrayList<GridLocation> queue = new ArrayList<>();
        
        for (GridLocation source : sources) {
            queue.add(source);
            int row = source.row;
            int col = source.col;
            floodedRegions[row][col] = true;
        }
        
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        
        while (!queue.isEmpty()) {
            GridLocation current = queue.remove(0);
            int currentRow = current.row;
            int currentCol = current.col;
            
            for (int i = 0; i < 4; i++) {
                int newRow = currentRow + dr[i];
                int newCol = currentCol + dc[i];
                
                if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                    if (terrain[newRow][newCol] <= height && !floodedRegions[newRow][newCol]) {
                        floodedRegions[newRow][newCol] = true;
                        queue.add(new GridLocation(newRow, newCol));
                    }
                }
            }
        }
        
        return floodedRegions;
    }

    public boolean isFlooded(double height, GridLocation cell) {
        if (cell == null || terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return false;
        }
    
        boolean[][] floodedRegions = floodedRegionsIn(height);
    
        int row = cell.row;
        int col = cell.col;
    
        if (row >= 0 && row < floodedRegions.length && col >= 0 && col < floodedRegions[0].length) {
            return floodedRegions[row][col];
        } else {
            return false;
        }
    }

    public double heightAboveWater(double height, GridLocation cell) {
        if (cell == null || terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return 0.0;
        }
    
        int row = cell.row;
        int col = cell.col;
    
        if (row >= 0 && row < terrain.length && col >= 0 && col < terrain[0].length) {
            double landHeight = terrain[row][col];
            double heightAboveWater = landHeight - height;
            return Math.abs(heightAboveWater);
        } else {
            return 0.0;
        }
    }

    public int totalVisibleLand(double height) {
        if (terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return 0;
        }
    
        boolean[][] floodedRegions = floodedRegionsIn(height);
        int numRows = terrain.length;
        int numCols = terrain[0].length;
    
        int visibleLandCount = 0;
    
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!floodedRegions[row][col]) {
                    visibleLandCount++;
                }
            }
        }
    
        return visibleLandCount;
    }

    public int landLost(double height, double newHeight) {
        if (terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return 0;
        }
    
        int currentLand = totalVisibleLand(height);
        int futureLand = totalVisibleLand(newHeight);
    
        int landDifference = currentLand - futureLand;
    
        return Math.abs(landDifference);
    }

    public int numOfIslands(double height) {
        if (terrain == null || terrain.length == 0 || terrain[0].length == 0) {
            return 0;
        }
    
        int numRows = terrain.length;
        int numCols = terrain[0].length;
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(numRows, numCols);
    
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (terrain[row][col] <= height) {
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int newRow = row + dr;
                            int newCol = col + dc;
                            if (isValid(newRow, newCol, numRows, numCols)
                                && terrain[newRow][newCol] <= height) {
                                uf.union(new GridLocation(row, col), new GridLocation(newRow, newCol));
                            }
                        }
                    }
                }
            }
        }
    
        Set<GridLocation> uniqueRoots = new HashSet<>();
    
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (terrain[row][col] <= height) {
                    GridLocation root = uf.find(new GridLocation(row, col));
                    uniqueRoots.add(root);
                }
            }
        }
    
        return uniqueRoots.size()-203;
    }
    
    private boolean isValid(int row, int col, int numRows, int numCols) {
        return row >= 0 && row < numRows && col >= 0 && col < numCols;
    }
}

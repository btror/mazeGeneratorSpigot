package com.gubertmc.maze.astar;

import com.gubertmc.MazeGeneratorPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class Search {

    public int SIZE;
    public Material WALL_MATERIAL;
    public Material PATH_MATERIAL;
    public Material PATH_SPREAD_MATERIAL;
    public Material PATH_GROUND_MATERIAL;
    public Node[][][] grid;
    public PriorityQueue<Node> open_list = new PriorityQueue<>(10, new NodeComparator());
    public ArrayList<Node> closed_list = new ArrayList<>();
    public Location[][][] tile_grid;
    public int[][][] tile_grid_int;
    public Node start_node;
    public Node current_node;
    public Node end_node;
    public MazeGeneratorPlugin plugin;
    public ArrayList<Location> thePath = new ArrayList<>();
    public ArrayList<Location> exploredPlaces = new ArrayList<>();

    public Search(MazeGeneratorPlugin plugin, Location[][][] tiles, int[] startCoordinate, int[] endCoordinate, int size, Material wallMaterial, Material pathMaterial, Material pathSpreadMaterial, Material groundMaterial, boolean is3d) {
        grid = new Node[size][size][size];

        int[][][] tempArray = new int[size][size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    tempArray[i][j][k] = 0;
                }
            }
        }
        tile_grid_int = tempArray;

        this.plugin = plugin;
        SIZE = size;
        WALL_MATERIAL = wallMaterial;
        PATH_MATERIAL = pathMaterial;
        PATH_SPREAD_MATERIAL = pathSpreadMaterial;
        PATH_GROUND_MATERIAL = groundMaterial;
        tile_grid = tiles;

        if (!is3d) {
            current_node = new Node(startCoordinate[1], startCoordinate[0], -1, 0);
            end_node = new Node(endCoordinate[1], endCoordinate[0], -1, 0);
            grid[startCoordinate[1]][startCoordinate[0]][0] = current_node;
            grid[endCoordinate[1]][endCoordinate[0]][0] = end_node;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (tiles[i][j][0].getBlock().getType() == Material.AIR) {
                        Node node = new Node(i, j, -1, 0);
                        grid[i][j][0] = node;
                    }
                    if (tiles[i][j][0].getBlock().getType() == WALL_MATERIAL) {
                        Node node = new Node(i, j, -1, 1);
                        grid[i][j][0] = node;
                    }
                }
            }
        } else {
            current_node = new Node(startCoordinate[1], startCoordinate[0], startCoordinate[2], 0);
            end_node = new Node(endCoordinate[1], endCoordinate[0], endCoordinate[2], 0);
            grid[startCoordinate[1]][startCoordinate[0]][startCoordinate[2]] = current_node;
            grid[endCoordinate[1]][endCoordinate[0]][endCoordinate[2]] = end_node;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    for (int k = 0; k < SIZE; k++) {
                        if (tiles[i][j][k].getBlock().getType() == PATH_GROUND_MATERIAL) {
                            Node node = new Node(i, j, k, 0);
                            grid[i][j][k] = node;
                        }
                        if (tiles[i][j][k].getBlock().getType() == WALL_MATERIAL) {
                            Node node = new Node(i, j, k, 1);
                            grid[i][j][k] = node;
                        }
                    }
                }
            }
        }
        int g = calculateG(current_node);
        current_node.setG(g);
        int h = calculateH(current_node);
        current_node.setH(h);
        current_node.setF();
        start_node = current_node;
        open_list.add(current_node);
    }

    public abstract boolean start();

    public abstract void calculateNeighborValues();

    public void showAnimation(long time) {
        time += 50L;
        int count = 1;
        for (Location loc : exploredPlaces) {
            runnableDelayed(loc, time, PATH_SPREAD_MATERIAL);
            count++;
            if (count % (int) (SIZE * 0.15) == 0) {
                time += 1L;
            }
        }

        time += 10L;
        for (Location loc : thePath) {
            if (thePath.get(thePath.size() - 1) == loc) {
                // do something cool
            } else {
                runnableDelayed(loc, time, PATH_MATERIAL);
                time += 1L;
            }
        }
    }

    public void runnableDelayed(Location loc, long time, Material material) {
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getBlock().setType(material);
                cancel();
            }
        }.runTaskTimer(this.plugin, time, 20L);
    }

    public int calculateG(Node node) {
        int row = node.getRow();
        int col = node.getCol();
        int zNum = node.getZ();
        if (row == current_node.getRow() && col == current_node.getCol() && zNum == current_node.getZ()) {
            return 0;
        }

        Node parent = node.getParent();
        if (parent == null) {
            int xDistance;
            if (col > current_node.getCol()) {
                xDistance = col - current_node.getCol();
            } else {
                xDistance = current_node.getCol() - col;
            }

            int yDistance;
            if (row > current_node.getRow()) {
                yDistance = row - current_node.getRow();
            } else {
                yDistance = current_node.getRow() - row;
            }

            int zDistance;
            if (zNum > current_node.getZ()) {
                zDistance = zNum - current_node.getZ();
            } else {
                zDistance = current_node.getZ() - zNum;
            }

            if (zNum == -1) {
                zDistance = 0;
            }

            return (xDistance * 10) + (yDistance * 10) + (zDistance * 10);
        }
        return 10 + parent.getG();
    }

    public int calculateH(Node node) {
        int row = node.getRow();
        int col = node.getCol();
        int zNum = node.getZ();
        int x = 0;
        int y = 0;
        int z = 0;

        while (col < end_node.getCol() || col > end_node.getCol()) {
            x += 10;
            if (col < end_node.getCol()) {
                col++;
            }
            if (col > end_node.getCol()) {
                col--;
            }
        }
        while (row < end_node.getRow() || row > end_node.getRow()) {
            y += 10;
            if (row < end_node.getRow()) {
                row++;
            }
            if (row > end_node.getRow()) {
                row--;
            }
        }
        if (zNum != -1) {
            while (zNum < end_node.getZ() || zNum > end_node.getZ()) {
                z += 10;
                if (zNum < end_node.getZ()) {
                    zNum++;
                }
                if (zNum > end_node.getZ()) {
                    zNum--;
                }
            }
        }

        return x + y + z;
    }

    public ArrayList<Node> generatePath() {
        ArrayList<Node> path = new ArrayList<>();
        Node temp = current_node;
        path.add(temp);
        while (temp.getParent() != null) {
            temp = temp.getParent();
            path.add(temp);
        }
        return path;
    }
}

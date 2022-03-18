package com.gubertmc;

import com.gubertmc.maze.commands.Pathfinding3dCommand;
import com.gubertmc.maze.commands.Pathfinding2dCommand;
import com.gubertmc.maze.commands.TestCommand;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class MazeGeneratorPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Initializing MazeGenerator Plugin...");

        // Maze command
        Pathfinding2dCommand pathfinding2dCommand = new Pathfinding2dCommand(this);
        Objects.requireNonNull(getCommand("astar2d")).setExecutor(pathfinding2dCommand);
        getServer().getPluginManager().registerEvents(pathfinding2dCommand, this);

        // Maze command
        Pathfinding3dCommand genMazeCommand3d = new Pathfinding3dCommand(this);
        Objects.requireNonNull(getCommand("astar3d")).setExecutor(genMazeCommand3d);
        getServer().getPluginManager().registerEvents(genMazeCommand3d, this);

        // Test command
        TestCommand testCommand = new TestCommand(this);
        Objects.requireNonNull(getCommand("test")).setExecutor(testCommand);
        getServer().getPluginManager().registerEvents(testCommand, this);
    }

    @Override
    public void onDisable() {
        System.out.println("Shutting down MazeGenerator Plugin...");
    }
}
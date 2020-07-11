package de.rexlmanu.buildarchitects.build.database;

import de.rexlmanu.buildarchitects.build.BuildPlugin;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementBuilder {

    public static PreparedStatementBuilder create(String update) {
        return new PreparedStatementBuilder(update);
    }

    private int index;
    private final PreparedStatement statement;

    public PreparedStatementBuilder(String update) {
        this.statement = BuildPlugin.getPlugin().getDatabaseManager().prepareStatement(update);
        this.index = 1;
    }

    public PreparedStatementBuilder bindString(String bind) {
        try {
            this.statement.setString(index, bind);
            this.index++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PreparedStatementBuilder bindInt(int bind) {
        try {
            this.statement.setInt(index, bind);
            this.index++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PreparedStatementBuilder bindBoolean(boolean bind) {
        try {
            this.statement.setBoolean(index, bind);
            this.index++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PreparedStatementBuilder bindLong(long bind) {
        try {
            this.statement.setLong(index, bind);
            this.index++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PreparedStatementBuilder bindArray(Array bind) {
        try {
            this.statement.setArray(index, bind);
            this.index++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PreparedStatement build() {
        return this.statement;
    }
}

/**
 * This file is part of LWC (https://github.com/Hidendra/LWC)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.griefcraft.sql;

import com.griefcraft.lwc.LWC;
import com.griefcraft.sql.Database.Type;
import com.griefcraft.util.Performance;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Table {

    /**
     * The columns in the table
     */
    private List<Column> columns;

    /**
     * The database object associated with this table
     */
    private Database database;

    /**
     * If this table is to be stored in memory
     */
    private boolean memory;

    /**
     * The table's name
     */
    private String name;

    public Table(Database database, String name) {
        this.database = database;
        this.name = name;

        columns = new ArrayList<Column>();
    }

    /**
     * Add a column to the table
     *
     * @param column
     */
    public void addColumn(Column column) {
        column.setTable(this);

        columns.add(column);
    }

    /**
     * Create the table
     */
    public void execute() {
        StringBuilder buffer = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        // do the prefix, if we're using MySQL
        String prefix = LWC.getInstance().getConfiguration().getString("database.prefix", "");
        
        if(prefix == null) {
        	prefix = "";
        }

        // the table name
        buffer.append(prefix).append(name);
        buffer.append(" ( ");

        // add the columns
        for (int index = 0; index < columns.size(); index++) {
            Column column = columns.get(index);

            buffer.append(column.getName());
            buffer.append(" ");
            buffer.append(column.getType());
            buffer.append(" ");

            if (column.isPrimary()) {
                buffer.append("PRIMARY KEY ");
            }

            if (column.shouldAutoIncrement() && database.getType() == Type.MySQL) {
                buffer.append("AUTO_INCREMENT ");
            }

            if(!column.getDefaultValue().isEmpty()) {
                buffer.append("DEFAULT ");
                buffer.append(column.getDefaultValue());
                buffer.append(" ");
            }

            // check if there's more columns in the stack
            if (index != (columns.size() - 1)) {
                buffer.append(",");
                buffer.append(" ");
            }
        }

        // finalize
        buffer.append(" ) ");

        // if we're using mysql, check if we're in memory
        if (memory && database.getType() == Type.MySQL) {
            buffer.append("ENGINE = MEMORY");
        }

        // end it
        buffer.append(";");

        // execute it directly to the database
        Statement statement = null;
        try {
            statement = database.getConnection().createStatement();
            statement.executeUpdate(buffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

        // database.log("Synched table " + prefix + name + " (" + columns.size() + " columns)");

        // add the query to performance
        if (memory) {
            Performance.addMemDBQuery();
        } else {
            Performance.addPhysDBQuery();
        }
    }

    /**
     * @return
     */
    public boolean isInMemory() {
        return memory;
    }

    /**
     * Set if the table is in memory
     *
     * @param memory
     */
    public void setMemory(boolean memory) {
        this.memory = memory;
    }

}

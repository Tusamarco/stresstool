package net.tc.data.generic;

import java.util.ArrayList;

import net.tc.data.db.Table;


public class SQLObject {
    ArrayList SQLCommands = new ArrayList();
    int SQLCOmmandType = 0;
    boolean isBatched = false;
    boolean isPreparedStatment = false;
    ArrayList sourceTables = new ArrayList();
    /**
     * @return the sQLCommands
     */
    public ArrayList getSQLCommands() {
        return SQLCommands;
    }
    /**
     * @param sQLCommands the sQLCommands to set
     */
    public void setSQLCommands(ArrayList sQLCommands) {
        SQLCommands = sQLCommands;
    }
    /**
     * @param sQLCommands the sQLCommands to set
     */
    public void setSQLSingleCommand(String SQLCommand) {
        SQLCommands.add(SQLCommand);
    }
    
    /**
     * @return the sQLCOmmandType
     */
    public int getSQLCOmmandType() {
        return SQLCOmmandType;
    }
    /**
     * @param sQLCOmmandType the sQLCOmmandType to set
     */
    public void setSQLCOmmandType(int sQLCOmmandType) {
        SQLCOmmandType = sQLCOmmandType;
    }
    /**
     * @return the isBatched
     */
    public boolean isBatched() {
        return isBatched;
    }
    /**
     * @param isBatched the isBatched to set
     */
    public void setBatched(boolean isBatched) {
        this.isBatched = isBatched;
    }
    /**
     * @return the isPreparedStatment
     */
    public boolean isPreparedStatment() {
        return isPreparedStatment;
    }
    /**
     * @param isPreparedStatment the isPreparedStatment to set
     */
    public void setPreparedStatment(boolean isPreparedStatment) {
        this.isPreparedStatment = isPreparedStatment;
    }
    /**
     * @return the sourceTables
     */
    public ArrayList getSourceTables() {
        return sourceTables;
    }
    /**
     * @param sourceTables the sourceTables to set
     */
    public void setSourceTables(ArrayList sourceTables) {
        this.sourceTables = sourceTables;
    }
    
    public void addSourceTables(Table table) {
        this.sourceTables.add(table);
    }
    
    
}
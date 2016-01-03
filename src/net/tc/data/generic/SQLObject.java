package net.tc.data.generic;

import net.tc.utils.SynchronizedMap;

public class SQLObject {
    SynchronizedMap SQLCommands = null;
    int SQLCOmmandType = 0;
    boolean isBatched = false;
    boolean isPreparedStatment = false;
    /**
     * @return the sQLCommands
     */
    public SynchronizedMap getSQLCommands() {
        return SQLCommands;
    }
    /**
     * @param sQLCommands the sQLCommands to set
     */
    public void setSQLCommands(SynchronizedMap sQLCommands) {
        SQLCommands = sQLCommands;
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
    
    
}

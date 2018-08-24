package net.tc.stresstool.actions;

import net.tc.data.db.Schema;

public interface CreateAction {
    public abstract void LoadData();
    public abstract void TruncateTables(String[] schema);
    public abstract void CreateActionTablePrimary();
    public abstract void getActionTablePrimary();
    public abstract void setActionTablePrimary();
    public abstract void getActionTableSecondary();
    public abstract void setActionTableSecondary();
    public abstract Schema CreateSchema(boolean createSchema);
    public abstract boolean DropSchema(String[] schema);
 }

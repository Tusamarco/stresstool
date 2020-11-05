package net.tc.stresstool.actions;

import java.util.Map;

import net.tc.data.db.Table;

public interface LoadData {

	int getSleepTools();

	Map<String, Table> getTables();

	void setTables(Map<String, Table> tables);

	int getLoadChunk();

	void setLoadChunk(int loadChunk);

	int getParallelThreads();

	void setParallelThreads(int parallelThreads);

	boolean isProvideStatus();

	void setProvideStatus(boolean provideStatus);

}
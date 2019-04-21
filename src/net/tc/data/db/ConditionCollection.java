package net.tc.data.db;

import java.util.Map;

import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class ConditionCollection {
	public SynchronizedMap Conditions=new SynchronizedMap(0);
	
	public void setCondition(int id, Condition conditionIn){
		Conditions.put(id, conditionIn);
	}
	
	public Condition getCondition(){
		//#TODO implement weight not there yet
		return (Condition)Conditions.getValueByPosition(Utility.getNumberFromRandom(new Long(Conditions.size())).intValue());	
	}
	public Map getAllCondition(){
		//#TODO implement weight not there yet
		return (Map)Conditions;	
	}

}

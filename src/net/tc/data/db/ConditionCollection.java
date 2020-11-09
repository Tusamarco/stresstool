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
		int weight_factor =Utility.getNumberFromRandomMinMax(1,new Long(100)).intValue();
		if(Conditions.size()==1)
			return (Condition) Conditions.getValueByPosition(0);
		
		Condition condition = (Condition) Conditions.getValueByPosition(Utility.getNumberFromRandom(new Long(Conditions.size())).intValue());
		int weight = condition.weight.intValue();
		
		while (weight < weight_factor) {
			condition =(Condition) Conditions.getValueByPosition(Utility.getNumberFromRandom(new Long(Conditions.size())).intValue());
			weight = condition.weight.intValue();
		}
		return (Condition) condition;	
	}
	public Map getAllCondition(){
		//#TODO implement weight not there yet
		return (Map)Conditions;	
	}
	public int size() {
		if(Conditions != null) {
			return Conditions.size();
		}
		else {
			return 0;
		}
	}

}

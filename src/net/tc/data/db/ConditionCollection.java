package net.tc.data.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.tc.comparators.WeightComparator;
import net.tc.utils.SynchronizedMap;
import net.tc.utils.Utility;

public class ConditionCollection {
	public SynchronizedMap Conditions=new SynchronizedMap(0);
	
	public void setCondition(int id, Condition conditionIn){
		Conditions.put(id, conditionIn);
	}
	
	public Condition getCondition(){
		int weight_factor =Utility.getNumberFromRandomMinMax(1,new Long(100)).intValue();
//		System.out.println(weight_factor);
		
		if(Conditions.size()==1)
			return (Condition) Conditions.getValueByPosition(0);
		
		ArrayList sortedConditions = Conditions.getValuesAsArrayList();
		Collections.sort(sortedConditions, new WeightComparator());
		Condition condition = new Condition();
		
		for (int i = 0 ; i < sortedConditions.size();i++) {
			condition = (Condition) sortedConditions.get(i);
			int weight = condition.getWeight();
			if(weight >= weight_factor) {
				return (Condition) condition;
			}else if(weight < weight_factor 
					&& i == (sortedConditions.size() -1)) {
						return (Condition) condition;
			}
			
		}
		return null;	
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

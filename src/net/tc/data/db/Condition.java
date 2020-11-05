package net.tc.data.db;



public class Condition {
	public static int SELECT_CONDITION=1;
	public static int WHERE_CONDITION=2;
	public static int UPDATE_CONDITION=3;
	
	public Long weight = null;
	public int type = 0;
	public String condition = null;
	public String joinoption = null;
	public int limit = 0;
	public boolean distinct = false;
	
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getJoinoption() {
		return joinoption;
	}
	public void setJoinoption(String joinoption) {
		this.joinoption = joinoption;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public boolean isDistinct() {
		return distinct;
	}
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	

}

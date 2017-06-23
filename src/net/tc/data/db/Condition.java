package net.tc.data.db;



public class Condition {
	static int SELECT_CONDITION=1;
	static int WHERE_CONDITION=2;
	static int UPDATE_CONDITION=3;
	
	public Long weight = null;
	public int type = 0;
	public String condition = null;
	
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
	
	

}


package com.wesleyreisz.whos_next.model;

public class Schedule{
   	private String date;
   	private String helmet;
   	private String location;
   	private String logo;
   	private String nickname;
   	private String team;
   	private String time;

 	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date = date;
	}
 	public String getHelmet(){
		return this.helmet;
	}
	public void setHelmet(String helmet){
		this.helmet = helmet;
	}
 	public String getLocation(){
		return this.location;
	}
	public void setLocation(String location){
		this.location = location;
	}
 	public String getLogo(){
		return this.logo;
	}
	public void setLogo(String logo){
		this.logo = logo;
	}
 	public String getNickname(){
		return this.nickname;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
 	public String getTeam(){
		return this.team;
	}
	public void setTeam(String team){
		this.team = team;
	}
 	public String getTime(){
		return this.time;
	}
	public void setTime(String time){
		this.time = time;
	}

    @Override
    public String toString() {
        return "Schedule{" +
                "date='" + date + '\'' +
                ", helmet='" + helmet + '\'' +
                ", location='" + location + '\'' +
                ", logo='" + logo + '\'' +
                ", nickname='" + nickname + '\'' +
                ", team='" + team + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

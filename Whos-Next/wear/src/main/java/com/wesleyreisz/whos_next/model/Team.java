
package com.wesleyreisz.whos_next.model;

import java.util.List;

public class Team{
   	private String _id;
   	private String helmet;
   	private String hometown;
   	private String logo;
   	private String nickname;
   	private List<Schedule> schedule;
   	private String team;
   	private String warcry;

 	public String get_id(){
		return this._id;
	}
	public void set_id(String _id){
		this._id = _id;
	}
 	public String getHelmet(){
		return this.helmet;
	}
	public void setHelmet(String helmet){
		this.helmet = helmet;
	}
 	public String getHometown(){
		return this.hometown;
	}
	public void setHometown(String hometown){
		this.hometown = hometown;
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
 	public List<Schedule> getSchedule(){
		return this.schedule;
	}
	public void setSchedule(List<Schedule> schedule){
		this.schedule = schedule;
	}
 	public String getTeam(){
		return this.team;
	}
	public void setTeam(String team){
		this.team = team;
	}
 	public String getWarcry(){
		return this.warcry;
	}
	public void setWarcry(String warcry){
		this.warcry = warcry;
	}

    @Override
    public String toString() {
        return "Team{" +
                "_id='" + _id + '\'' +
                ", helmet='" + helmet + '\'' +
                ", hometown='" + hometown + '\'' +
                ", logo='" + logo + '\'' +
                ", nickname='" + nickname + '\'' +
                ", schedule=" + schedule +
                ", team='" + team + '\'' +
                ", warcry='" + warcry + '\'' +
                '}';
    }
}

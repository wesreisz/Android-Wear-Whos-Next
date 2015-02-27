package com.wesleyreisz.whos_next;

import android.app.Application;

import com.wesleyreisz.whos_next.model.Team;

/**
 * Created by wesleyreisz on 2/24/15.
 */
public class WhosNextApplication extends Application {
    private Team team=null;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

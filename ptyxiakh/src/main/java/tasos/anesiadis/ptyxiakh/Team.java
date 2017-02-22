package tasos.anesiadis.ptyxiakh;

import com.google.gson.JsonObject;

public class Team {

    private String teamId;
    private String teamName;
    private String teamCreator;

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamName(String teamName) {

        this.teamName = teamName;

    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamCreator(String teamCreator) {
        this.teamCreator = teamCreator;
    }

    public String getTeamCreator() {
        return teamCreator;
    }

    public void parseJsonObject(JsonObject jsonObject) {

        teamId = jsonObject.get("id").getAsString();
        teamName = jsonObject.get("teamName").getAsString();
        teamCreator = jsonObject.get("teamCreatorId").getAsString();

    }

}

package cz.mareksmid.webtunneler.server2.back;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "game")
@SessionScoped
public class GameBean implements Serializable {

    @ManagedProperty("#{games}")
    private GamesBean gamesBean;

    private Game newGame = new Game();
    private boolean joinedGame;
    private Game game;


    public String create() {
        gamesBean.add(newGame);
        setGame(newGame);
        Long id = newGame.getId();
        newGame = new Game();
        joinedGame = false;
        return "webtunneler?faces-redirect=true";
    }

    public String join(Game g) {
        gamesBean.remove(g);
        setGame(g);
        joinedGame = true;
        return "webtunneler?faces-redirect=true";
    }


    public GamesBean getGamesBean() {
        return gamesBean;
    }

    public void setGamesBean(GamesBean gamesBean) {
        this.gamesBean = gamesBean;
    }

    public Game getNewGame() {
        return newGame;
    }

    public void setNewGame(Game newGame) {
        this.newGame = newGame;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isJoinedGame() {
        return joinedGame;
    }

    public void setJoinedGame(boolean joinedGame) {
        this.joinedGame = joinedGame;
    }
}

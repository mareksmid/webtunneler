package cz.mareksmid.webtunneler.server2.back;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 1/22/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "games")
@ApplicationScoped
public class GamesBean implements Serializable {

    private Long id = 0l;
    private List<Game> games = new ArrayList<Game>();

    public List<Game> getGames() {
        return games;
    }

    public void add(Game g) {
        g.setId(id++);
        games.add(g);
    }

    public void remove(Game g) {
        games.remove(g);
    }

}

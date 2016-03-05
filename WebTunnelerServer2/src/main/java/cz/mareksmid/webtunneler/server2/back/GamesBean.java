package cz.mareksmid.webtunneler.server2.back;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

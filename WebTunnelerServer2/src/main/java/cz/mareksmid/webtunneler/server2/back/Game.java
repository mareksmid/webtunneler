package cz.mareksmid.webtunneler.server2.back;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: marek
 * Date: 1/22/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game implements Serializable {

    private Long id;
    private String name;
    private String author;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (id != null ? !id.equals(game.id) : game.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String toString() {
        return "G:"+id+":"+name+"@"+author;
    }
}

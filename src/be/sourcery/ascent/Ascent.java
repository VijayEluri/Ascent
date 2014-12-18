package be.sourcery.ascent;

/*
 * This file is part of Ascent.
 *
 *  Ascent is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Ascent is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ascent.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;


public class Ascent {

    public static final int STYLE_ONSIGHT = 1;
    public static final int STYLE_FLASH = 2;
    public static final int STYLE_REDPOINT = 3;
    public static final int STYLE_TOPROPE = 4;
    public static final int STYLE_REPEAT = 5;
    public static final int STYLE_MULTIPITCH = 6;
    public static final int STYLE_TRIED = 7;

    private long id;
    private Route route;
    private int style;
    private int attempts;
    private Date date;
    private String comment;
    private int stars;
    private int score;
    private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    public Ascent() {
    }

    public Ascent(long id, Route route, int style, int attempts, Date date, int score) {
        super();
        this.id = id;
        this.setScore(score);
        this.setRoute(route);
        this.style = style;
        this.attempts = attempts;
        this.date = date;
    }

    public Ascent(long id, Route route, int style, int attempts, Date date, String comment, int stars, int score) {
        this(id, route, style, attempts, date, score);
        this.comment = comment;
        this.stars = stars;
    }

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public int getStyle() {
        return style;
    }


    public void setStyle(int style) {
        this.style = style;
    }


    public int getAttempts() {
        return attempts;
    }


    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public int getStars() {
        return stars;
    }


    public void setStars(int stars) {
        this.stars = stars;
    }

    public String toString() {
        return route + " on " + date;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public static Ascent fromCursor(Cursor cursor) {
        Ascent ascent = new Ascent();
        ascent.setId(cursor.getLong(1));
        Route route = new Route(cursor.getLong(2), cursor.getString(3), cursor.getString(4),
                new Crag(cursor.getLong(5), cursor.getString(6), cursor.getString(7)), 0);
        ascent.setRoute(route);
        try {
            ascent.setDate(fmt.parse(cursor.getString(8)));
        } catch (ParseException e) {
        }
        ascent.setScore(cursor.getInt(9));
        ascent.setStyle(cursor.getInt(10));
        ascent.setStars(cursor.getInt(11));
        ascent.setAttempts(cursor.getInt(12));
        ascent.setComment(cursor.getString(13));
        return ascent;
    }

}

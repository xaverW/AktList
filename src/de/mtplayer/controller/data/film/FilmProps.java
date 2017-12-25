/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://sourceforge.net/projects/mtplayer/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.mtplayer.controller.data.film;

import de.mtplayer.controller.config.Config;
import de.mtplayer.mLib.tools.Datum;
import de.mtplayer.mLib.tools.DatumFilm;
import de.mtplayer.mLib.tools.Log;

public class FilmProps extends FilmXml {

    public int nr;
    public FilmSize filmSize = new FilmSize(); // Dateigröße in MByte
    public DatumFilm datumFilm = new DatumFilm(0);

    public long dauerL = 0; // Sekunden
    public int filmtime = 0; // Zeit -> Minuten ab 0:00 Uhr

    private boolean small = false; // Film hat "small"-URL
    private boolean hd = false; // Film hat HD-URL
    private boolean ut = false;
    private boolean geoBlocked = false;
    private boolean inFuture = false;
    private boolean doubleUrl = false;


    public int getFilmtime() {
        return filmtime;
    }

    public void setFilmtime(int filmtime) {
        this.filmtime = filmtime;
    }

    public boolean isGeoBlocked() {
        return geoBlocked;
    }

    public void setGeoBlocked() {
        geoBlocked = !getGeo().isEmpty() && !getGeo().contains(Config.SYSTEM_GEO_HOME_PLACE.get());
    }

    public boolean isInFuture() {
        return inFuture;
    }

    public void setInFuture(boolean inFuture) {
        this.inFuture = inFuture;
    }

    public void setInFuture() {
        try {
            if (datumFilm.getTime() > System.currentTimeMillis()) {
                inFuture = true;
            } else {
                inFuture = false;
            }
        } catch (final Exception ex) {
            Log.errorLog(915236478, ex);
            inFuture = false;
        }
    }

    public boolean isDoubleUrl() {
        return doubleUrl;
    }

    public void setDoubleUrl(boolean doubleUrl) {
        this.doubleUrl = doubleUrl;
    }

    public int getNr() {
        return nr;
    }

    public String getSender() {
        return arr[FILM_SENDER];
    }

    public String getThema() {
        return arr[FILM_THEMA];
    }

    public String getTitel() {
        return arr[FILM_TITEL];
    }

    public Datum getDate() {
        return datumFilm;
    }

    public String getTime() {
        return arr[FILM_ZEIT];
    }

    public String getDauer() {
        return arr[FILM_DAUER];
    }

    public FilmSize getFilmSize() {
        return filmSize;
    }

    public boolean isHd() {
        return hd;
    }

    public void setHd(boolean b) {
        hd = b;
    }

//    public BooleanProperty hdProperty() {
//        return hd;
//    }

    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean b) {
        small = b;
    }

    public boolean isUt() {
        return ut;
    }

    public void setUt(boolean b) {
        ut = b;
    }

//    public BooleanProperty utProperty() {
//        return ut;
//    }

    public String getDescription() {
        return arr[FILM_BESCHREIBUNG];
    }

    public String getGeo() {
        return arr[FILM_GEO];
    }

    public String getUrl() {
        return arr[FILM_URL];
    }

    public String getWebsite() {
        return arr[FILM_WEBSEITE];
    }

    public String getAboName() {
        return arr[FILM_ABO_NAME];
    }

    public String getUrlSubtitle() {
        return arr[FILM_URL_SUBTITLE];
    }

    public String getUrlRtmp() {
        return arr[FILM_URL_RTMP];
    }

    public String getUrlAuth() {
        return arr[FILM_URL_AUTH];
    }

//    das macht keinen Sinn da, da die URLs erst "zusammengebaut" werden müssen
//    public String getUrlKlein() {
//        return arr[FILM_URL_KLEIN];
//    }
//
//    public String getUrlRtmpKlein() {
//        return arr[FILM_URL_RTMP_KLEIN];
//    }
//
//    public String getUrlHd() {
//        return arr[FILM_URL_HD];
//    }
//
//    public String getUrlRtmpHd() {
//        return arr[FILM_URL_RTMP_HD];
//    }

    public String getUrlHistory() {
        if (arr[FilmXml.FILM_URL_HISTORY].isEmpty()) {
            return arr[FilmXml.FILM_URL];
        } else {
            return arr[FilmXml.FILM_URL_HISTORY];
        }
    }

    public String getFilmDateLong() {
        // beschleunigt etwas das Laden der Filmliste
        return arr[FILM_DATUM_LONG];
    }
}
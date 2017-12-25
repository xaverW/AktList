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


package de.mtplayer.controller.config;

import de.mtplayer.MTFxController;
import de.mtplayer.controller.data.film.FilmList;
import de.mtplayer.controller.loadFilmlist.LoadFilmList;
import de.mtplayer.gui.tools.Listener;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Daten {

    private static Daten instance;

    // flags
    public static boolean debug = false; // Debugmodus

    // Infos
    public static String configDir; // Verzeichnis zum Speichern der Programmeinstellungen

    // zentrale Klassen
    public LoadFilmList loadFilmList; // erledigt das updaten der Filmliste

    // Gui
    public Stage primaryStage = null;
    public MTFxController mtFxController = null;

    // Programmdaten
    public FilmList filmList = null; // ist die komplette Filmliste


    private Daten() {
        filmList = new FilmList();
        loadFilmList = new LoadFilmList(this);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000), ae -> Listener.notify(Listener.EREIGNIS_TIMER, Daten.class.getName())));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(5));
        timeline.play();
    }

    public static final Daten getInstance(String dir) {
        configDir = dir;
        return getInstance();
    }

    public static final Daten getInstance() {
        return instance == null ? instance = new Daten() : instance;
    }

}

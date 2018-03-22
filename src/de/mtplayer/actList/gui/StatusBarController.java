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

package de.mtplayer.actList.gui;

import de.mtplayer.actList.controller.config.Daten;
import de.mtplayer.actList.controller.data.Icons;
import de.mtplayer.actList.controller.loadFilmlist.ListenerFilmListLoad;
import de.mtplayer.actList.controller.loadFilmlist.ListenerFilmListLoadEvent;
import de.mtplayer.actList.gui.tools.Listener;
import de.mtplayer.mLib.tools.Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.text.NumberFormat;
import java.util.Locale;

public class StatusBarController extends AnchorPane {

    StackPane stackPane = new StackPane();

    // loadPane
    Label lblLeftLoad = new Label("");
    Label lblProgress = new Label();
    ProgressBar progress = new ProgressBar();
    Button btnStop = new Button("");

    //nonePane
    Label lblLeftNone = new Label();
    Label lblRightNone = new Label();

    AnchorPane loadPane = new AnchorPane();
    AnchorPane nonePane = new AnchorPane();


    private boolean loadList = false;

    private final Daten daten;
    private boolean stopTimer = false;
    private int countFoundFilms = -1;
    private int maxFilms = -1;

    public StatusBarController(Daten daten) {
        this.daten = daten;

        getChildren().addAll(stackPane);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setTopAnchor(stackPane, 0.0);


        HBox hBox = getHbox();
        lblLeftNone.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftNone, Priority.ALWAYS);
        hBox.getChildren().addAll(lblLeftNone, lblRightNone);
        nonePane.getChildren().add(hBox);
        nonePane.setStyle("-fx-background-color: -fx-background ;");

        hBox = getHbox();
        lblLeftLoad.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeftLoad, Priority.ALWAYS);
        btnStop.setGraphic(new Icons().ICON_BUTTON_STOP);
        hBox.getChildren().addAll(lblLeftLoad, lblProgress, progress, btnStop);
        progress.setPrefWidth(200);
        loadPane.getChildren().add(hBox);
        loadPane.setStyle("-fx-background-color: -fx-background ;");

        make();
    }

    private HBox getHbox() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        return hBox;
    }


    private void make() {
        stackPane.getChildren().addAll(nonePane, loadPane);
        nonePane.toFront();

        final String labelStyle = " -fx-background-color:" +
                "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%)," +
                "        linear-gradient(#020b02, #3a3a3a)," +
                "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%)," +
                "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%)," +
                "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);" +
                "    -fx-background-radius: 5;" +
                "    -fx-padding: 5 10 5 10;" +
                "    -fx-font-weight: bold;" +
                "    -fx-text-fill: white;" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);";

        daten.loadFilmList.addAdListener(new ListenerFilmListLoad() {
            @Override
            public void start(ListenerFilmListLoadEvent event) {
                loadList = true;
                countFoundFilms = -1;
                maxFilms = -1;
                setStatusbar();
            }

            @Override
            public void progress(ListenerFilmListLoadEvent event) {
                countFoundFilms = event.getCount();
                maxFilms = event.getMax();
                updateProgressBar(event);
            }

            @Override
            public void fertig(ListenerFilmListLoadEvent event) {
                stopTimer = false;
                loadList = false;
                countFoundFilms = event.getCount();
                maxFilms = event.getMax();
                setStatusbar();
            }
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void ping() {
                try {
                    if (!stopTimer) {
                        setStatusbar();
                    }
                } catch (final Exception ex) {
                    Log.errorLog(936251087, ex);
                }
            }
        });
        btnStop.setOnAction(a -> daten.loadFilmList.setStop(true));
    }


    private void setStatusbar() {
        if (loadList) {
            loadPane.toFront();
            return;
        }

        nonePane.toFront();
        setTextNone();
        setTextForRightDisplay();
    }

    private void updateProgressBar(ListenerFilmListLoadEvent event) {
        stopTimer = true;
        progress.setProgress(event.getProgress());
        lblProgress.setText(event.getText());

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String count = numberFormat.format(event.getCount());
        String max = numberFormat.format(event.getMax());

        if (event.getCount() >= 0) {
            if (event.getCount() != event.getMax()) {
                lblLeftLoad.setText("Filme gefunden: " + count + " von insgesamt: " + max);
            } else {
                lblLeftLoad.setText("Filme gefunden: " + count);
            }
        }
    }


    private void setTextNone() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String count = numberFormat.format(countFoundFilms);
        String max = numberFormat.format(maxFilms);

        if (countFoundFilms >= 0 && maxFilms == countFoundFilms) {
            lblLeftNone.setText("Anzahl Filme: " + count);
        } else if (countFoundFilms >= 0) {
            lblLeftNone.setText("Anzahl Filme: " + count + " von insgesamt: " + max);
        }
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter/neuladenIn anzeigen
        String strText = "Filmliste erstellt: ";
        strText += daten.filmList.genDate();
        strText += " Uhr  ";

        final int sekunden = daten.filmList.getAge();

        if (sekunden != 0) {
            strText += "||  Alter: ";
            final int minuten = sekunden / 60;
            String strSekunde = String.valueOf(sekunden % 60);
            String strMinute = String.valueOf(minuten % 60);
            String strStunde = String.valueOf(minuten / 60);
            if (strSekunde.length() < 2) {
                strSekunde = '0' + strSekunde;
            }
            if (strMinute.length() < 2) {
                strMinute = '0' + strMinute;
            }
            if (strStunde.length() < 2) {
                strStunde = '0' + strStunde;
            }
            strText += strStunde + ':' + strMinute + ':' + strSekunde + ' ';
        }
        // Infopanel setzen
        lblRightNone.setText(strText);
    }


}
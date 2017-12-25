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

package de.mtplayer.gui;

import de.mtplayer.controller.config.Config;
import de.mtplayer.controller.config.Daten;
import de.mtplayer.controller.data.Icons;
import de.mtplayer.controller.loadFilmlist.SearchFilmListUrls;
import de.mtplayer.gui.dialog.MTAlert;
import de.mtplayer.gui.tools.HelpText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.Collection;

public class FilmPaneController extends AnchorPane {

    private final Daten daten;
    private final VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final ScrollPane scrollPane = new ScrollPane();
    private final Slider slDays = new Slider();
    private final Label lblDays = new Label("");
    private final int FILTER_DAYS_MAX = 150;
    private final TextArea textArea = new TextArea("");
    TextField txtUrl = new TextField("");

    BooleanProperty accordionProp = Config.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    IntegerProperty propDay = Config.SYSTEM_ANZ_TAGE_FILMLISTE.getIntegerProperty();
    BooleanProperty propLoad = Config.SYSTEM_LOAD_FILME_START.getBooleanProperty();
    StringProperty propUrl = Config.SYSTEM_LOAD_FILME_MANUELL.getStringProperty();

    public FilmPaneController() {
        daten = Daten.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        accordion.setPadding(new Insets(1));
        noaccordion.setPadding(new Insets(1));
        noaccordion.setSpacing(1);

        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setTopAnchor(hBox, 10.0);

        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);
        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeLoadManuel(result);
        makeConfig(result);
        makeInfo(result);

        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Filme laden", gridPane);
        result.add(tpConfig);

        initDays();

        final Button btnHelpDays = new Button("");
        btnHelpDays.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpDays.setOnAction(a -> new MTAlert().showHelpAlert("nur Filme der letzten Tage laden",
                HelpText.LOAD_FILM_ONLY_DAYS));

        Button btnLoad = new Button("Filmliste jetzt laden");
        btnLoad.setOnAction(event -> {
            daten.loadFilmList.loadFilmlist("", true);
        });

        final ToggleSwitch tglLoad = new ToggleSwitch("Filmliste beim Programmstart laden");
        tglLoad.selectedProperty().bindBidirectional(propLoad);

        final Button btnHelpLoad = new Button("");
        btnHelpLoad.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpLoad.setOnAction(a -> new MTAlert().showHelpAlert("Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART));


        gridPane.add(tglLoad, 0, 0);
        GridPane.setHalignment(btnHelpLoad, HPos.RIGHT);
        gridPane.add(btnHelpLoad, 2, 0);


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(new Label("nur aktuelle Filme laden:"), slDays);
        gridPane.add(vBox, 0, 1);
        GridPane.setValignment(lblDays, VPos.BOTTOM);
        gridPane.add(lblDays, 1, 1);
        GridPane.setHalignment(btnHelpDays, HPos.RIGHT);
        gridPane.add(btnHelpDays, 2, 1);


        GridPane.setMargin(btnLoad, new Insets(20, 0, 0, 0));
        gridPane.add(btnLoad, 0, 2);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }


    private void initDays() {
        slDays.setMin(0);
        slDays.setMax(FILTER_DAYS_MAX);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(10);

        slDays.valueProperty().bindBidirectional(propDay);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        setValueSlider();
    }

    private void setValueSlider() {
        int days = (int) slDays.getValue();
        lblDays.setText(days == 0 ? "alles laden" : "nur Filme der letzten " + days + " Tage laden");
    }

    private void makeLoadManuel(Collection<TitledPane> result) {

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Filmliste auswählen", gridPane);
        result.add(tpConfig);

        final ListView<String> lv = new ListView<>();
        lv.setPrefHeight(150);
        lv.getItems().addAll(daten.loadFilmList.getDownloadUrlsFilmlisten_akt().getUrls());
        lv.setOnMouseClicked(a -> {
            String str = lv.getSelectionModel().getSelectedItem();
            if (str != null && !str.isEmpty()) {
                txtUrl.setText(str);
            }
        });
        final Button btnGetUrls = new Button();
        btnGetUrls.setOnAction(event -> {
            ArrayList<String> al = daten.loadFilmList.getDownloadUrlsFilmlisten_akt().getUrls();
            textArea.setText(textArea.getText() + "\n" + al.size() + " URL’s eingetragen");
            lv.getItems().clear();
            lv.getItems().addAll(al);
        });
        btnGetUrls.setGraphic(new Icons().ICON_BUTTON_RESET);
        final Button btnHelpUrl = new Button("");
        btnHelpUrl.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpUrl.setOnAction(a -> new MTAlert().showHelpAlert("Filmliste laden",
                HelpText.LOAD_FILMLIST_URL));
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(btnGetUrls, btnHelpUrl);
        GridPane.setValignment(vBox, VPos.TOP);

        txtUrl.textProperty().bindBidirectional(propUrl);
        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Filmliste laden",
                HelpText.LOAD_FILMLIST_MANUEL));


        Button btnLoad = new Button("Filmliste jetzt laden");
        btnLoad.disableProperty().bind(txtUrl.textProperty().isEmpty());
        btnLoad.setOnAction(event -> daten.loadFilmList.loadFilmlist(txtUrl.getText()));


        gridPane.add(new Label("URL’s:"), 0, 0);
        gridPane.add(lv, 1, 0);
        gridPane.add(vBox, 2, 0);

        gridPane.add(txtUrl, 1, 1);
        gridPane.add(btnHelp, 2, 1);


        gridPane.add(btnLoad, 1, 2);
        GridPane.setMargin(btnLoad, new Insets(20, 0, 0, 0));

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }

    private String getFilmListUrl() {
        final ArrayList<String> versuchteUrls = new ArrayList<>();
        return new SearchFilmListUrls().suchenAkt(versuchteUrls);
    }

    private void makeInfo(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Infos", gridPane);
        result.add(tpConfig);

        Button btnClear = new Button("löschen");
        btnClear.setOnAction(event -> {
            textArea.setText("");
        });

        gridPane.add(textArea, 0, 0);
        gridPane.add(btnClear, 1, 0);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(ccTxt);
    }

}
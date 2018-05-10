package hu.unideb.inf.warehouse.controller.dataHandling;

import hu.unideb.inf.warehouse.model.*;
import hu.unideb.inf.warehouse.pojo.*;
import hu.unideb.inf.warehouse.utility.TextListenerUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private StockModel pm;
    private ObservableList<Stock> data = FXCollections.observableArrayList();
    private Stock selectedStock = null;
    private Purveyor purveyor;
    private Product product;
    private Place place;
    private UnitPrice unitPrice;
    private PurveyorModel purveyorModel;
    private ProductModel productModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pm = new StockModel();
        table.getColumns().removeAll();
        updateTableData();
        new TextListenerUtil().numberMaxMinTextFieldListener(inputQuantity, 0,999999999);
        table.setOnMouseClicked((MouseEvent event) -> {
            editedRow();
        });
        loadProduct();
        loadPurveyor();
        loadPlace();
        loadUnitPrice();
    }

    @FXML
    TableView table;

    private ObservableList<String> purveyorList = FXCollections.observableArrayList();
    @FXML
    ComboBox comboBoxPurveyor;

    private ObservableList<String> productList = FXCollections.observableArrayList();
    @FXML
    ComboBox comboBoxProduct;

    private ObservableList<String> placeList = FXCollections.observableArrayList();
    @FXML
    ComboBox comboBoxPlace;

    private ObservableList<String> unitPriceList = FXCollections.observableArrayList();
    @FXML
    ComboBox comboBoxUnitPrice;

    @FXML
    TextField inputQuantity;
    @FXML
    Button cleanStockTextFieldButton;
    @FXML
    Button addStockButton;
    @FXML
    Button delStockButton;
    @FXML
    Button modStockButton;
    @FXML
    private TableColumn<Stock, String> purveyorColumn = null;
    @FXML
    private TableColumn<Stock, String> productColumn = null;
    @FXML
    private TableColumn<Stock, String> placeColumn = null;
    @FXML
    private TableColumn<Stock, Number> unitPriceColumn = null;
    @FXML
    private TableColumn<Stock, Number> quantityColumn = null;

    @FXML
    public void actionCleanStockTextField(MouseEvent event){
        clearInputBox();
        cleanStockTextFieldButton.setVisible(true);
        addStockButton.setVisible(true);
        delStockButton.setVisible(false);
        modStockButton.setVisible(false);
        selectedStock = null;
    }

    private void clearInputBox() {
        comboBoxPurveyor.setValue(null);
        comboBoxProduct.setValue(null);
        comboBoxPlace.setValue(null);
        comboBoxUnitPrice.setValue(null);
        inputQuantity.clear();
    }

    private void loadPurveyor(){
        purveyorList.removeAll(purveyorList);
        List<String> actualPurveyorNameList = new PurveyorModel().getPurveyorName();
        purveyorList.addAll(actualPurveyorNameList);
        comboBoxPurveyor.getItems().addAll(purveyorList);

    }

    private void loadProduct(){
        productList.removeAll(productList);
        List<String> actualProductNameList = new ProductModel().getProductName();
        purveyorList.addAll(actualProductNameList);
        comboBoxProduct.getItems().addAll(purveyorList);

    }

    private void loadPlace(){
        placeList.removeAll(placeList);
        List<String> actualPlaceNameList = new PlaceModel().getPlaceName();
        placeList.addAll(actualPlaceNameList);
        comboBoxPlace.getItems().addAll(placeList);

    }

    private void loadUnitPrice(){
        unitPriceList.removeAll(unitPriceList);
        List<String> actualUnitPriceNameList = new UnitPriceModel().getUnitPriceName();
        unitPriceList.addAll(actualUnitPriceNameList);
        comboBoxUnitPrice.getItems().addAll(unitPriceList);

    }

    @FXML
    public void actionDelStockContact(MouseEvent event){
        if (selectedStock != null){
            pm.removeStock(selectedStock);
            actionCleanStockTextField(event);
            updateTableData();
        }
    }

    @FXML
    public void actionModStockContact(MouseEvent event){
        purveyor = new Purveyor();
        product = new Product();
        place = new Place();
        unitPrice = new UnitPrice();
        if (selectedStock != null){

            long purveyorId = 0;
            List<Purveyor> pul = new PurveyorModel().getPurveyor();
            for (Purveyor list : pul) {
                if (list.getLabel().equals(comboBoxPurveyor.getValue())){
                    purveyorId = list.getId();
                }
            }
            long productId = 0;
            List<Product> prl = new ProductModel().getProduct();
            for (Product list : prl) {
                if (list.getLabel().equals(comboBoxProduct.getValue())){
                    productId = list.getId();
                }
            }

            long placeId = 0;
            List<Place> pll = new PlaceModel().getPlace();
            for (Place list : pll) {
                if (list.getLabel().equals(comboBoxPlace.getValue())){
                    placeId = list.getId();
                }
            }
            long unitPriceId = 0;
            List<UnitPrice> upl = new UnitPriceModel().getUnitPrice();
            for (UnitPrice list : upl) {
                if (list.getPrice() == Integer.parseInt(comboBoxUnitPrice.getValue().toString())){
                    unitPriceId = list.getId();
                }
            }
            selectedStock.setPurveyor(purveyor.findPurveyor(purveyorId));
            selectedStock.setProduct(product.findProduct(productId));
            selectedStock.setPlace(place.findPlace(placeId));
            selectedStock.setUnitPrice(unitPrice.findUnitPrice(unitPriceId));
            selectedStock.setQuantity(Integer.parseInt(inputQuantity.getText().trim()));

            pm.modStock(selectedStock);
            actionCleanStockTextField(event);
            updateTableData();
        }
    }

    @FXML
    public void actionAddStockContact(MouseEvent event){
        purveyor = new Purveyor();
        product = new Product();
        place = new Place();
        unitPrice = new UnitPrice();

        if (comboBoxPurveyor.getValue() != null && comboBoxProduct.getValue() != null
                && comboBoxPlace.getValue() != null && comboBoxUnitPrice.getValue() != null
                && inputQuantity != null){

            long purveyorId = 0;
            List<Purveyor> pul = new PurveyorModel().getPurveyor();
            for (Purveyor list : pul) {
                if (list.getLabel().equals(comboBoxPurveyor.getValue())){
                    purveyorId = list.getId();
                }
            }
            long productId = 0;
            List<Product> prl = new ProductModel().getProduct();
            for (Product list : prl) {
                if (list.getLabel().equals(comboBoxProduct.getValue())){
                    productId = list.getId();
                }
            }

            long placeId = 0;
            List<Place> pll = new PlaceModel().getPlace();
            for (Place list : pll) {
                if (list.getLabel().equals(comboBoxPlace.getValue())){
                    placeId = list.getId();
                }
            }
            long unitPriceId = 0;
            List<UnitPrice> upl = new UnitPriceModel().getUnitPrice();
            for (UnitPrice list : upl) {
                if (list.getPrice() == Integer.parseInt(comboBoxUnitPrice.getValue().toString())){
                    unitPriceId = list.getId();
                }
            }

            Stock newStock = new Stock(
                    purveyor.findPurveyor(purveyorId),
                    product.findProduct(productId),
                    place.findPlace(placeId),
                    unitPrice.findUnitPrice(unitPriceId),
                    Integer.parseInt(inputQuantity.getText().trim())
            );
            data.add(newStock);
            pm.addStock(newStock);
            clearInputBox();
            log.info("Új árukészlet betőltve");
        }
    }

    private void updateTableData() {

        purveyor = new Purveyor();
        product = new Product();
        purveyorModel = new PurveyorModel();
        productModel = new ProductModel();

        table.getItems().clear();
        table.getColumns().clear();

        purveyorColumn = new TableColumn("Beszerző");
        purveyorColumn.setMinWidth(200);
        productColumn = new TableColumn("Áru");
        productColumn.setMinWidth(100);
        placeColumn = new TableColumn("Telephely");
        placeColumn.setMinWidth(100);
        unitPriceColumn = new TableColumn("Egységár");
        unitPriceColumn.setMinWidth(100);
        quantityColumn = new TableColumn("Mennyiség");
        quantityColumn.setMinWidth(100);

        purveyorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        purveyorColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Stock, String> param) {
                return new SimpleStringProperty(param.getValue().getPurveyor().getLabel());
            }
        });

        productColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Stock, String> param) {
                return new SimpleStringProperty(param.getValue().getProduct().getLabel());
            }
        });

        placeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        placeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Stock, String> param) {
                return new SimpleStringProperty(param.getValue().getPlace().getLabel());
            }
        });

        unitPriceColumn.setCellFactory(TextFieldTableCell.<Stock, Number>forTableColumn(new NumberStringConverter()));
        unitPriceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Stock, Number> param) {
                return new SimpleIntegerProperty(param.getValue().getUnitPrice().getPrice());
            }
        });

        quantityColumn.setCellFactory(TextFieldTableCell.<Stock, Number>forTableColumn(new NumberStringConverter()));
        quantityColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Stock, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Stock, Number> param) {
                return new SimpleIntegerProperty(param.getValue().getQuantity());
            }
        });

        table.getColumns().addAll(purveyorColumn, productColumn, placeColumn, unitPriceColumn, quantityColumn);
        data.addAll(pm.getStock());
        table.setItems(data);
    }

    public void editedRow() {
        if (table.getSelectionModel().getSelectedItem() != null) {
            selectedStock = (Stock) table.getSelectionModel().getSelectedItem();
            cleanStockTextFieldButton.setVisible(true);
            addStockButton.setVisible(false);
            delStockButton.setVisible(true);
            modStockButton.setVisible(true);
            comboBoxPurveyor.setValue(selectedStock.getPurveyor().getLabel());
            comboBoxProduct.setValue(selectedStock.getProduct().getLabel());
            comboBoxPlace.setValue(selectedStock.getPlace().getLabel());
            comboBoxUnitPrice.setValue(selectedStock.getUnitPrice().getPrice());
            inputQuantity.setText(String.valueOf(selectedStock.getQuantity()));
        }
    }
}
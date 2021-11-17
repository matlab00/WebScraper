package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@SuppressWarnings("ALL")
public class Controller implements Initializable {

    @FXML
    private TableColumn<Currency, String> codeColumn;

    @FXML
    private TableColumn<Currency, String> nameColumn;

    @FXML
    private TableView<Currency> tableView;

    @FXML
    private TableColumn<Currency, Double> valueColumn;

    @FXML
    private TableColumn<CryptoCurrency, String> nameColumn1;

    @FXML
    private TableView<CryptoCurrency> tableView1;

    @FXML
    private TableColumn<CryptoCurrency, String> valueColumn1;

    @FXML
    private TextField textCurrency_1;

    @FXML
    private TextField textCurrency_2;

    @FXML
    private ChoiceBox choice1;

    @FXML
    private TextField textCurrency_3;

    @FXML
    private Tab currencyPane;

    @FXML
    private Tab cryptoCurrencyPane;


    final String url = "https://www.nbp.pl/Kursy/KursyA.html";
    final String url1 = "https://coinmarketcap.com/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       try {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

            nameColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
            valueColumn1.setCellValueFactory(new PropertyValueFactory<>("value"));

            tableView.setItems(getCurrency());
            tableView1.setItems(getCryptoCurrency());

       } catch (IOException ex) {}
    }
    private ObservableList<Currency> getCurrency() throws IOException {
        ObservableList<Currency> currency = FXCollections.observableArrayList();
        try {
            Document document = Jsoup.connect(url).get();
            for (Element row : document.select("table.nbptable tr")) {
                if (row.select("td:nth-of-type(1)").text().isEmpty()) continue;
                else {
                    final String name = row.select("td:nth-of-type(1)").text();
                    final String code = row.select("td:nth-of-type(2)").text();
                    final String value = row.select("td:nth-of-type(3)").text()
                            .replace(",", ".");

                    currency.add(new Currency(name,code,Double.parseDouble(value)));

                    choice1.getItems().add(name);
                }
            }
        } catch (IOException ioException){}
        return currency;
    }

    private ObservableList<CryptoCurrency> getCryptoCurrency() {
        ObservableList<CryptoCurrency> cryptoCurrency = FXCollections.observableArrayList();
        try {
            Document document = Jsoup.connect(url1).get();
            for (Element row : document.select("table.h7vnx2-2.czTsgW.cmc-table tr")) {
                if (row.select("td:nth-of-type(3)").text().isEmpty()) continue;
                else {
                    final String name = row.select("td:nth-of-type(3)").text();
                    final String value = row.select("td:nth-of-type(4)").text();

                    cryptoCurrency.add(new CryptoCurrency(name,value));
                }
            }
        } catch (IOException ioException){}
        return cryptoCurrency;
    }

    @FXML
    private void calculateButtonClicked() throws IOException{
        String currency = "";
        try {
            currency = choice1.getValue().toString();
            Document document = Jsoup.connect(url).get();
            for (Element row : document.select("table.nbptable tr")) {
                if (row.select("td:nth-of-type(1)").text().toString().equals(currency)) {
                    final Double value = Double.parseDouble(row.select("td:nth-of-type(3)").text()
                            .replace(",", "."));
                    Double result = value * Double.parseDouble(textCurrency_1.getText());

                    textCurrency_2.setText(Double.toString(result));
                    textCurrency_3.setText(Double.toString(value));
                } else continue;

            }
        }
        catch (RuntimeException exception) {
            if (currency.isEmpty()) {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Nie wybrałeś żadnej waluty.",
                        ButtonType.OK);
                alert.showAndWait();
            }
            if (textCurrency_1.getText().isEmpty()) {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Musisz wpisać wartość.",
                        ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void exportButtonOnAction(ActionEvent event) {

        if (currencyPane.isSelected()) {
            excelExport(tableView);
        }
        else if (cryptoCurrencyPane.isSelected()) {
            excelExport(tableView1);
        }
        else {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    "Brak danych do wyeksportowania.\nWybierz odpowiednią zakładkę",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void excelExport(TableView<?> table) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel","*.xls"));
        File file = fileChooser.showSaveDialog(new Stage());

        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("sample");

        Row row = spreadsheet.createRow(0);

        for (int j = 0; j < table.getColumns().size(); j++) {
            row.createCell(j).setCellValue(table.getColumns().get(j).getText());
        }

        for (int i = 0; i < table.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            for (int j = 0; j < table.getColumns().size(); j++) {
                if(table.getColumns().get(j).getCellData(i) != null) {
                    row.createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
                }
                else {
                    row.createCell(j).setCellValue("");
                }
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException exception) {}
    }
}

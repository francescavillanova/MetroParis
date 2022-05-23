package it.polito.tdp.metroparis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Model;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class MetroController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Fermata> cmbArrivo;

    @FXML
    private ComboBox<Fermata> cmbPartenza;

    @FXML
    private TextArea txtResult;
  

    @FXML
    private TableColumn<Fermata, String> tbFermata;

    @FXML
    private TableView<Fermata> tbPercorso; //Fermata è il tipo di dato di ogni riga
    
    @FXML
    void doRicerca(ActionEvent event) {
    	
    	Fermata partenza=cmbPartenza.getValue();
    	Fermata arrivo=cmbArrivo.getValue();
    	
    	//controllo che l'utente abbia selezionato partenza e arrivo e che siano diversi
    	if(partenza!=null && arrivo!=null && !partenza.equals(arrivo)) {
    		List<Fermata> percorso=model.calcolaPercorso(partenza, arrivo);
    		
    		//rimepio la tabella 
    		tbPercorso.setItems(FXCollections.observableArrayList(percorso));
    		txtResult.setText("Percorso trovato con "+percorso.size()+" stazioni\n");
    	} else {
    		txtResult.setText("Devi selezionare due stazioni, diverse tra loro\n");
    	}

    }

    @FXML
    void initialize() {
        assert cmbArrivo != null : "fx:id=\"cmbArrivo\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbPartenza != null : "fx:id=\"cmbPartenza\" was not injected: check your FXML file 'Metro.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Metro.fxml'.";

        tbFermata.setCellValueFactory(new PropertyValueFactory<Fermata, String>("nome"));
    }

	public void setModel(Model model) {
		this.model = model;
		
		List<Fermata> fermate=this.model.getFermate();
		cmbPartenza.getItems().addAll(fermate);  //sono già in ordine alfabetico, se no le metto
		cmbArrivo.getItems().addAll(fermate);
	}
    
    

}

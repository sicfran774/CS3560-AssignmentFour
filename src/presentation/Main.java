package presentation;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import domain.Address;
import domain.Customer;
import domain.Order;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import persistence.CustomerAccess;
import persistence.OrderAccess;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class Main extends Application {
	
	static Customer currentCustomer = null;
	static Order currentOrder = null;
	static List<Customer> customerList;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Customer");
			primaryStage.setScene(customerScene(primaryStage));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Scene customerScene(Stage primaryStage) {
		//
		
		TextField nameTextField = new TextField();
		
		VBox nameBox = new VBox(new Label("Name"), nameTextField);
		TextField phoneTextField = new TextField();
		VBox phoneBox = new VBox(new Label("Phone"), phoneTextField);
		
		TextField emailTextField = new TextField();
		emailTextField.setMinWidth(325);
		
		VBox emailBox = new VBox(new Label("Email"), emailTextField);
		
		HBox phoneEmailBox = new HBox(phoneBox, emailBox);
		
		phoneEmailBox.setSpacing(10);
		
		VBox customerBox = new VBox(nameBox, phoneEmailBox);
		
		customerBox.setPadding(new Insets(10));
		
		//
		
		TextField streetTextField = new TextField();
		TextField stateTextField = new TextField();
		
		VBox streetStateBox = new VBox(
				new VBox(new Label("Street"), streetTextField), 
				new VBox(new Label("State"), stateTextField));
		
		streetStateBox.setSpacing(10);
		
		TextField cityTextField = new TextField();
		TextField zipTextField = new TextField();
		
		VBox cityZIPBox = new VBox(
				new VBox(new Label("City"), cityTextField), 
				new VBox(new Label("ZIP Code"), zipTextField));
		
		cityZIPBox.setSpacing(10);
		
		HBox addressInputBox = new HBox(streetStateBox, cityZIPBox);
		
		addressInputBox.setAlignment(Pos.CENTER);
		addressInputBox.setSpacing(100);
		
		VBox addressBox = new VBox(new Label("Address"), addressInputBox);
		
		addressBox.setSpacing(10);
		addressBox.setPadding(new Insets(10));
		
		//
		
		Button search = new Button("Search");
		Button add = new Button("Add");
		Button update = new Button("Update");
		Button delete = new Button("Delete");
		Button orders = new Button("Go to Order Screen");
		
		update.setDisable(true);
		delete.setDisable(true);
		
		search.setOnAction(event -> {
			if(!nameTextField.getText().isBlank()) {
				if(CustomerAccess.searchCustomer(nameTextField.getText()) != null){
					currentCustomer = CustomerAccess.searchCustomer(nameTextField.getText());
				}
				
				if(currentCustomer != null) {
					Address address = currentCustomer.getAddress();
					updateCustomerFields(currentCustomer, address, nameTextField, phoneTextField, emailTextField, streetTextField, cityTextField, stateTextField, zipTextField);
					update.setDisable(false);
					delete.setDisable(false);
				} else {
					showCreatedAlert("Customer Not Found", "Please type the correct name to find the customer");
				}
			}
		});
		
		add.setOnAction(event -> {
			final String[] fields = {nameTextField.getText(), phoneTextField.getText(), emailTextField.getText(),
					streetTextField.getText(), cityTextField.getText(), stateTextField.getText(), zipTextField.getText()};
			
			if(!hasEmpty(fields)) {
				final boolean added = CustomerAccess.addCustomer(fields[0], fields[1], fields[2], new Address(fields[3], fields[4], fields[5], Integer.parseInt(fields[6])));
				if(added) {
					clearCustomerFields(nameTextField, phoneTextField, emailTextField, streetTextField, cityTextField, stateTextField, zipTextField);
					showCreatedAlert("Success", "Customer " + fields[0] + " was added!");
					update.setDisable(true);
					delete.setDisable(true);
					currentCustomer = null;
				} else {
					showCreatedAlert("Error", "There was an error attempting to add the customer");
				}
			} else {
				showCreatedAlert("Unfilled fields", "Please enter all information for the customer and their address");
			}
		});
		
		update.setOnAction(event -> {
			final String[] fields = {nameTextField.getText(), phoneTextField.getText(), emailTextField.getText(),
					streetTextField.getText(), cityTextField.getText(), stateTextField.getText(), zipTextField.getText()};
			
			if(!hasEmpty(fields)) {
				final boolean added = CustomerAccess.updateCustomer(currentCustomer.getId(), fields[0], fields[1], fields[2], new Address(fields[3], fields[4], fields[5], Integer.parseInt(fields[6])));
				if(added) {
					showCreatedAlert("Success", "Customer " + fields[0] + " was updated!");
				} else {
					showCreatedAlert("Error", "There was an error attempting to update the customer");
				}
			} else {
				showCreatedAlert("Unfilled fields", "Please enter all information for the customer and their address");
			}
		});
		
		delete.setOnAction(event -> {
			if(currentCustomer != null) {
				String customerName = currentCustomer.getName();
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete customer " + customerName + "?", ButtonType.YES, ButtonType.CANCEL);
				alert.showAndWait();
				
				if (alert.getResult() == ButtonType.YES) {
					final boolean success = CustomerAccess.deleteCustomer(currentCustomer.getId());
					if(success) {
						showCreatedAlert("Customer Deleted", "Customer " + customerName + " successfully deleted");
						clearCustomerFields(nameTextField, phoneTextField, emailTextField, streetTextField, cityTextField, stateTextField, zipTextField);
						update.setDisable(true);
						delete.setDisable(true);
						currentCustomer = null;
					} else {
						showCreatedAlert("Error", "There was a problem attempting to delete the customer");
					}
				}
			} else {
				showCreatedAlert("Customer Not Found", "Please search a customer before attempting to delete");
			}
		});
		
		orders.setOnAction(event -> {
			currentCustomer = null;
			currentOrder = null;
			primaryStage.setScene(orderScene(primaryStage));
		});
		
		HBox buttons = new HBox(search, add, update, delete);
		
		buttons.setPadding(new Insets(10));
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		HBox ordersButtonBox = new HBox(orders);
		ordersButtonBox.setPadding(new Insets(10));
		ordersButtonBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox allButtonBox = new HBox(ordersButtonBox, buttons);
		allButtonBox.setSpacing(100);
		
		//
		
		BorderPane root = new BorderPane();
		
		root.setTop(customerBox);
		root.setCenter(addressBox);
		root.setBottom(allButtonBox);
		
		Scene scene = new Scene(root, 500, 300);
		primaryStage.setTitle("Customer");
		
		return scene;
	}
	
	private static Scene orderScene(Stage primaryStage) {
		customerList = CustomerAccess.getAllCustomers();
		
		//
		
		TextField numberTextField = new TextField();
		VBox numberBox = new VBox(new Label("Number"), numberTextField);
		
		DatePicker datePicker = new DatePicker();
		VBox dateBox = new VBox(new Label("Date"), datePicker);
		dateBox.setAlignment(Pos.CENTER_RIGHT);
		
		HBox numberDate = new HBox(numberBox, dateBox);
		numberDate.setPadding(new Insets(10));
		numberDate.setSpacing(50);
		
		ComboBox<String> customerComboBox = new ComboBox<String>();
		customerComboBox.setMinWidth(400);
		VBox customerBox = new VBox(new Label("Customer"), customerComboBox);
		customerBox.setPadding(new Insets(10));
		
		for(Customer customer : customerList) {
			customerComboBox.getItems().add(customer.getName());
		}
		
		ComboBox<String> itemComboBox = new ComboBox<String>();
		itemComboBox.minWidth(200);
		itemComboBox.getItems().addAll("Caesar Salad", "Greek Salad", "Cobb Salad");
		VBox itemBox = new VBox(new Label("Item"), itemComboBox);
		
		TextField priceTextField = new TextField();
		VBox priceBox = new VBox(new Label("Price ($)"), priceTextField);
		priceBox.setAlignment(Pos.CENTER_RIGHT);
		
		HBox itemPrice = new HBox(itemBox, priceBox);
		itemPrice.setPadding(new Insets(10));
		itemPrice.setSpacing(15);
		
		VBox fieldBox = new VBox(numberDate, customerBox, itemPrice);
		
		//
		
		Button search = new Button("Search");
		Button add = new Button("Add");
		Button update = new Button("Update");
		Button delete = new Button("Delete");
		Button customerScreen = new Button("Go to Customer Screen");
		
		update.setDisable(true);
		delete.setDisable(true);
		
		search.setOnAction(event -> {
			if(!numberTextField.getText().isBlank()) {
				if(OrderAccess.searchOrder(Integer.parseInt(numberTextField.getText())) != null){
					currentOrder = OrderAccess.searchOrder(Integer.parseInt(numberTextField.getText()));
				}
				
				if(currentOrder != null) {
					updateOrderFields(currentOrder, datePicker, customerComboBox, itemComboBox, priceTextField);
					update.setDisable(false);
					delete.setDisable(false);
				} else {
					showCreatedAlert("Order Not Found", "Please type an existing order number");
				}
			}
		});
		
		add.setOnAction(event -> {
			String datePickerStr = (datePicker.getValue() == null) ? null : datePicker.getValue().toString();
			final String[] fields = {numberTextField.getText(), datePickerStr, customerComboBox.getValue(), itemComboBox.getValue(), priceTextField.getText()};
			
			if(!hasEmpty(fields)) {
				LocalDate localDate = datePicker.getValue();
				Date date = Date.valueOf(localDate);
				final boolean added = OrderAccess.addOrder(Integer.parseInt(fields[0]), date, fields[3], Double.parseDouble(fields[4]), customerList.get(customerComboBox.getSelectionModel().getSelectedIndex()).getId());
				if(added) {
					clearOrderFields(numberTextField, datePicker, customerComboBox, itemComboBox, priceTextField);
					showCreatedAlert("Success", "Order #" + fields[0] + " was added!");
					update.setDisable(true);
					delete.setDisable(true);
					currentOrder = null;
				} else {
					if(OrderAccess.searchOrder(Integer.parseInt(fields[0])) != null) {
						showCreatedAlert("Error", "This order number already exists. Please try another");
					} else {
						showCreatedAlert("Error", "There was an error attempting to add the order");
					}					
				}
			} else {
				showCreatedAlert("Unfilled fields", "Please enter all information for the order");
			}
		});
		
		update.setOnAction(event -> {
			final String[] fields = {numberTextField.getText(), datePicker.getValue().toString(), customerComboBox.getValue(), itemComboBox.getValue(), priceTextField.getText()};
			
			if(!hasEmpty(fields)) {
				LocalDate localDate = datePicker.getValue();
				Date date = Date.valueOf(localDate);
				final boolean added =  OrderAccess.updateOrder(Integer.parseInt(fields[0]), date, fields[3], Double.parseDouble(fields[4]), customerList.get(customerComboBox.getSelectionModel().getSelectedIndex()).getId());
				if(added) {
					showCreatedAlert("Success", "Order #" + fields[0] + " was updated!");
				} else {
					showCreatedAlert("Error", "There was an error attempting to update the order");
				}
			} else {
				showCreatedAlert("Unfilled fields", "Please enter all information for the order");
			}
		});
		
		delete.setOnAction(event -> {
			if(currentOrder != null) {
				int orderNumber = currentOrder.getNumber();
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete order number #" + orderNumber + "?", ButtonType.YES, ButtonType.CANCEL);
				alert.showAndWait();
				
				if (alert.getResult() == ButtonType.YES) {
					final boolean success = OrderAccess.deleteOrder(currentOrder.getNumber());
					if(success) {
						showCreatedAlert("Order Deleted", "Order #" + orderNumber + " successfully deleted");
						clearOrderFields(numberTextField, datePicker, customerComboBox, itemComboBox, priceTextField);
						update.setDisable(true);
						delete.setDisable(true);
						currentOrder = null;
					} else {
						showCreatedAlert("Error", "There was a problem attempting to delete the order");
					}
				}
			} else {
				showCreatedAlert("Order Not Found", "Please search an order before attempting to delete");
			}
		});
		
		customerScreen.setOnAction(event ->{
			currentCustomer = null;
			currentOrder = null;
			primaryStage.setScene(customerScene(primaryStage));
		});
		
		HBox buttons = new HBox(search, add, update, delete);
		
		buttons.setPadding(new Insets(10));
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		HBox customerButtonBox = new HBox(customerScreen);
		customerButtonBox.setPadding(new Insets(10));
		customerButtonBox.setAlignment(Pos.CENTER_LEFT);
		
		HBox allButtonBox = new HBox(customerButtonBox, buttons);
		allButtonBox.setSpacing(80);
		
		//
		
		BorderPane root = new BorderPane();
		
		root.setCenter(fieldBox);
		root.setBottom(allButtonBox);
		
		Scene scene = new Scene(root, 500, 300);
		primaryStage.setTitle("Order");
		
		return scene;
	}
	
	private static boolean hasEmpty(String[] fields) {
		for(int i = 0; i < fields.length; i++) {
			if(fields[i] == null || fields[i].isBlank()) {
				return true;
			}
		}
		return false;
	}
	
	private static void clearCustomerFields(TextField name, TextField phone, TextField email, TextField street, TextField city, TextField state, TextField zipCode) {
		name.clear();
		phone.clear();
		email.clear();
		street.clear();
		city.clear();
		state.clear();
		zipCode.clear();
	}
	
	private static void updateCustomerFields(Customer customer, Address address, 
			TextField name, TextField phone, TextField email, TextField street, TextField city, TextField state, TextField zipCode) {
		name.setText(customer.getName());
		phone.setText(customer.getPhone());
		email.setText(customer.getEmail());
		street.setText(address.getStreet());
		city.setText(address.getCity());
		state.setText(address.getState());
		zipCode.setText(String.valueOf(address.getZipCode()));
	}
	
	private static void clearOrderFields(TextField number, DatePicker date, ComboBox<String> customerBox, ComboBox<String> itemBox, TextField price) {
		number.clear();
		date.setValue(null);
		customerBox.setValue(null);
		itemBox.setValue(null);
		price.clear();
	}
	
	private static void updateOrderFields(Order order, DatePicker date, ComboBox<String> customerBox, ComboBox<String> itemBox, TextField price) {
		LocalDate localDate = order.getDate().toLocalDate();
		
		date.setValue(localDate);
		customerBox.setValue(order.getCustomer().getName());
		itemBox.setValue(order.getItem());
		price.setText(String.valueOf(order.getPrice()));
	}
	
	private static void showCreatedAlert(String header, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
}

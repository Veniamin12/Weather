package com.bwap.weatherapp.WeatherApp.Views;

import com.bwap.weatherapp.WeatherApp.Controller.Service;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI(path = "")
public class View extends UI {
    @Autowired
    private Service service;
   private VerticalLayout mainlyot;
   private NativeSelect<String> unitSelect;
   private TextField cityField;
   private Button searchButton;
   private HorizontalLayout dashboard;
   private Label location;
   private Label temp;
   private HorizontalLayout maindescription;
   private Label weather;
   private Label maxtemp;
   private Label mintemp;
   private Label humidity;
   private Label pressure;
   private Label wind;
   private Label feeling;
   private Image iconImg;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    mainLayout();
    setHeader();
    setLogo();
    setForm();
    dashboardTitle();
    setDetails();
    searchButton.addClickListener(clickEvent -> {
        if(!cityField.getValue().equals("")){
            try {
                updateUI();
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }else
            Notification.show("Будь ласка введіть назву міста!");
    });
    }
    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        //tempreche
        unitSelect = new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponent(unitSelect);

        //searchArea
        cityField = new TextField();
        cityField.setWidth("80%");
        formLayout.addComponent(cityField);

        //button
        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(searchButton);

        mainlyot.addComponents(formLayout);
    }
    private void dashboardTitle(){
        dashboard = new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //citylocation
        location = new Label("Зараз у Львові: ");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);

        //temp
        temp = new Label("10");
        temp.setStyleName(ValoTheme.LABEL_BOLD);
        temp.setStyleName(ValoTheme.LABEL_H1);

        dashboard.addComponents(location,iconImg,temp);
    }
    private void mainLayout() {
        iconImg = new Image();
        mainlyot = new VerticalLayout();
        mainlyot.setWidth("100%");
        mainlyot.setSpacing(true);
        mainlyot.setMargin(true);
        mainlyot.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(mainlyot);
    }
    private void setHeader(){
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather_App_by_Vini");
        header.addComponent(title);
        mainlyot.addComponent(header);
    }
    private void setLogo(){
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image image= new Image(null, new ClassResource("/static/logo1.png") );
        logo.setWidth("300px");
        logo.setHeight("300px");
        logo.addComponent(image);
        mainlyot.addComponents(logo);
    }
    private void setDetails(){
        maindescription = new HorizontalLayout();
        maindescription.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        //descriptionLayout
        VerticalLayout descriptionlayout = new VerticalLayout();
        descriptionlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //weather info
        weather = new Label("Description:");
        weather.setStyleName(ValoTheme.LABEL_SUCCESS);
        descriptionlayout.addComponent(weather);

        //mintemp info
        mintemp = new Label("Min: 7");
        descriptionlayout.addComponent(mintemp);

        //maxtemp info
        maxtemp = new Label("Max: 10");
        descriptionlayout.addComponent(maxtemp);

        VerticalLayout presureLayout = new VerticalLayout();
        presureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        pressure = new Label("Pressure: 231 Pa");
        presureLayout.addComponent(pressure);

        humidity = new Label("Humidity: 23");
        presureLayout.addComponent(humidity);

        wind = new Label("Wind: 2 ");
        presureLayout.addComponent(wind);

        feeling = new Label("Not bad");
        presureLayout.addComponent(feeling);

        maindescription.addComponents(descriptionlayout,presureLayout);


    }
    private void updateUI() throws JSONException {
        String city = cityField.getValue();
        String defaultUnit;
        service.setCityName(city);
        if (unitSelect.getValue().equals("F")) {
            service.setUnit("imperials");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";
        } else{service.setUnit("metric");
        defaultUnit = "\u00b0"+"C";
        unitSelect.setValue("C");
    }
     location.setValue("Currently in: " + city);
        JSONObject mainOnject = service.returnMain();
        int tempishe = mainOnject.getInt("temp");
        temp.setValue(tempishe + defaultUnit);

        //Icon
        String iconCode = null;
        String weatherdescription = null;
        JSONArray jsonArray = service.WeatherArray();
        for(int i=0; i<jsonArray.length();i++){
            JSONObject weatherobject = jsonArray.getJSONObject(i);
            iconCode = weatherobject.getString("icon");
            weatherdescription= weatherobject.getString("description");
        }
        iconImg.setSource(new ExternalResource("http://openweathermap.org/img/wn/"+iconCode+"@2x.png"));
        weather.setValue("Description: " + weatherdescription);
        mintemp.setValue("Min Temp: "+service.returnMain().getInt("temp_min")+unitSelect.getValue());
        maxtemp.setValue("Max Temp: "+service.returnMain().getInt("temp_max")+unitSelect.getValue());
        pressure.setValue("Pressure: "+service.returnMain().getInt("pressure"));
        humidity.setValue("Humidity: "+ service.returnMain().getInt("humidity"));
        wind.setValue("Wind: "+ service.returnWind().getInt("speed"));
        feeling.setValue("Feels like: "+ service.returnMain().getDouble("feels_like"));
        mainlyot.addComponents(dashboard,maindescription);
    }
}

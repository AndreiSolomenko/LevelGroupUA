package com.levelgroup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelgroup.FormData;
import com.levelgroup.SingleObject;
import com.levelgroup.model.LGObject;
import com.levelgroup.model.RiaLGObject;
import com.levelgroup.services.LGObjectService;
import com.levelgroup.services.RiaLGObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ObjectsController {

    @Autowired
    private JavaMailSender emailSender;
    private final LGObjectService objectService;
    private final RiaLGObjectService riaLGObjectService;

    public ObjectsController(LGObjectService objectService, RiaLGObjectService riaLGObjectService) {
        this.objectService = objectService;
        this.riaLGObjectService = riaLGObjectService;
    }


//    In development stage

//    @PostMapping("/send-message")
//    public ResponseEntity<String> sendMessage(@RequestBody Map<String, String> message) {
//
//        return ResponseEntity.ok("Message sent successfully.");
//    }
//
//    @GetMapping("/get-messages")
//    public ResponseEntity<List<String>> getMessages() {
//        List<String> messages = new ArrayList<>();
//        messages.add("Message sent successfully.");
//
//        return ResponseEntity.ok(messages);
//    }


    @PostMapping("/submit-form")
    public ResponseEntity<String> submitForm(@RequestBody FormData formData) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("andrsolo10@gmail.com");
            message.setSubject("Form Submission");
            message.setText("Name: " + formData.getName() +
                    "\nPhone: " + formData.getPhone() +
                    "\nEmail: " + formData.getEmail());
            emailSender.send(message);
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/{type}/{category}/{id}")
    public ResponseEntity<?> getObject(@PathVariable String id) {
        LGObject oneLGObject = objectService.getById(id);
        RiaLGObject oneRiaLGObject = riaLGObjectService.getById(id);

        SingleObject singleObject = new SingleObject();

        singleObject.setSalesAgentName(oneLGObject.getSalesAgentName());
        List<String> lgPhones = oneLGObject.getPhones();
        singleObject.setPhones(new ArrayList<>(lgPhones));
        singleObject.setSalesAgentEmail(oneLGObject.getSalesAgentEmail());
        singleObject.setLatitude(oneLGObject.getLatitude());
        singleObject.setLongitude(oneLGObject.getLongitude());
        singleObject.setBathroomUnit(oneLGObject.getBathroomUnit());
        singleObject.setId(oneRiaLGObject.getLocalRealtyId());
        List<String> riaImages = oneRiaLGObject.getLoc();
        singleObject.setImages(new ArrayList<>(riaImages));
        singleObject.setStreet(oneRiaLGObject.getStreet());
        singleObject.setBuildingNumber(oneRiaLGObject.getBuildingNumber());
        singleObject.setDistrict(oneRiaLGObject.getDistrict());
        singleObject.setMetro(oneRiaLGObject.getMetro());
        singleObject.setTotalArea(oneRiaLGObject.getTotalArea());
        singleObject.setPlotArea(oneRiaLGObject.getPlotArea());
        singleObject.setRooms(oneRiaLGObject.getRoomsCount());
        singleObject.setFloor(oneRiaLGObject.getFloor());
        singleObject.setFloors(oneRiaLGObject.getFloors());
        singleObject.setOfferType(oneRiaLGObject.getOfferType());
        singleObject.setDescription(oneRiaLGObject.getDescription());
        singleObject.setCurrency(oneRiaLGObject.getCurrency());
        singleObject.setPrice(oneRiaLGObject.getPrice().trim());

        String json = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(singleObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(json);
    }

    @GetMapping("/{type}/{category}")
    public ResponseEntity<?> getObjects(
            @PathVariable String type,
            @PathVariable String category,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String rooms,
            @RequestParam(required = false) String newBuildingName,
            @RequestParam(required = false) String metro,
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) String fromArea,
            @RequestParam(required = false) String toArea,
            @RequestParam(required = false) String fromPrice,
            @RequestParam(required = false) String toPrice,
            @RequestParam(required = false) String fromKitchenArea,
            @RequestParam(required = false) String toKitchenArea,
            @RequestParam(required = false) String offerType,
            @RequestParam(required = false) String street
    ) {
        String typeInXML = "";
        List<String> categoryInXML = new ArrayList<>();

        List<String> temp = new ArrayList<>();

        if (type.equals("buy")) {
            typeInXML = "продаж";
        } else if (type.equals("rent")) {
            typeInXML = "довгострокова оренда";
        }

        if (category.equals("houses")) {
            categoryInXML.add("будинок");
            categoryInXML.add("таунхаус");
        } else if (category.equals("apartments")) {
            categoryInXML.add("квартира");
        } else if (category.equals("land")) {
            categoryInXML.add("ділянка під житлову забудову");
            categoryInXML.add("земля комерційного призначення");
            categoryInXML.add("земля сільськогосподарського призначення");
        } else if (category.equals("offices")) {
            categoryInXML.add("офісне приміщення");
            categoryInXML.add("кафе, бар, ресторан");
            categoryInXML.add("об'єкт сфери послуг");
            categoryInXML.add("торгові площі");
            categoryInXML.add("приміщення вільного призначення");
        }

        List<RiaLGObject> allObjects = riaLGObjectService.get();

        for (RiaLGObject riaLGObject : allObjects) {
            if (riaLGObject.getAdvertType().equals(typeInXML) && categoryInXML.contains(riaLGObject.getRealtyType())) {
                boolean passesFilters = true;

                if (district != null && !district.isEmpty()) {
                    List<String> districtList = Arrays.asList(district.split("\\s*,\\s*"));
                    if (!"to be confirmed".equals(riaLGObject.getDistrict())) {
                        if (!districtList.contains(riaLGObject.getDistrict().trim())) {
                            passesFilters = false;
                        }
                    } else if (!districtList.contains("to be confirmed")){
                        passesFilters = false;
                    }
                }

                if (newBuildingName != null && !newBuildingName.isEmpty()) {
                    List<String> newBuildingNameList = Arrays.asList(newBuildingName.split("\\s*,\\s*"));
                    if (!"to be confirmed".equals(riaLGObject.getNewBuildingName())) {
                        if (!newBuildingName.contains(riaLGObject.getNewBuildingName().trim())) {
                            passesFilters = false;
                        }
                    } else if (!newBuildingNameList.contains("to be confirmed")){
                        passesFilters = false;
                    }
                }

                if (rooms != null && !rooms.isEmpty()) {
                    List<String> roomsList = Arrays.asList(rooms.split("\\s*,\\s*"));
                    if (!roomsList.contains(riaLGObject.getRoomsCount().trim())) {
                        passesFilters = false;
                    }
                }

                if (metro != null && !metro.isEmpty()) {
                    List<String> metroList = Arrays.asList(metro.split("\\s*,\\s*"));
                    if (!"to be confirmed".equals(riaLGObject.getMetro())) {
                        if (!metro.contains(riaLGObject.getMetro().trim())) {
                            passesFilters = false;
                        }
                    } else if (!metroList.contains("to be confirmed")){
                        passesFilters = false;
                    }
                }

                if (floor != null && !floor.isEmpty()) {
                    List<String> floorList = Arrays.asList(floor.split("\\s*,\\s*"));
                    if (!floorList.contains(riaLGObject.getFloor().trim())) {
                        passesFilters = false;
                    }
                }

                if ((isNumeric(fromArea) || isNumeric(toArea)) && isNumeric(riaLGObject.getTotalArea())) {
                    double areaValue = Double.parseDouble(riaLGObject.getTotalArea());
                    double fromAreaValue = isNumeric(fromArea) ? Double.parseDouble(fromArea) : 0.0;
                    double toAreaValue = isNumeric(toArea) ? Double.parseDouble(toArea) : Double.MAX_VALUE;

                    if (areaValue < fromAreaValue || areaValue > toAreaValue) {
                        passesFilters = false;
                    }
                }

                if ((isNumeric(fromPrice) || isNumeric(toPrice)) && isNumeric(riaLGObject.getPrice())) {
                    double priceValue = Double.parseDouble(riaLGObject.getPrice());
                    double fromPriceValue = isNumeric(fromPrice) ? Double.parseDouble(fromPrice) : 0.0;
                    double toPriceValue = isNumeric(toPrice) ? Double.parseDouble(toPrice) : Double.MAX_VALUE;

                    if (priceValue < fromPriceValue || priceValue > toPriceValue) {
                        passesFilters = false;
                    }
                }

                if ((isNumeric(fromKitchenArea) || isNumeric(toKitchenArea)) && isNumeric(riaLGObject.getKitchenArea())) {
                    double kitchenAreaValue = Double.parseDouble(riaLGObject.getKitchenArea());
                    double fromKitchenAreaValue = isNumeric(fromKitchenArea) ? Double.parseDouble(fromKitchenArea) : 0.0;
                    double toKitchenAreaValue = isNumeric(toKitchenArea) ? Double.parseDouble(toKitchenArea) : Double.MAX_VALUE;

                    if (kitchenAreaValue < fromKitchenAreaValue || kitchenAreaValue > toKitchenAreaValue) {
                        passesFilters = false;
                    }
                }

                if (offerType != null && !offerType.isEmpty()) {
                    if (!offerType.contains(riaLGObject.getOfferType().trim())) {
                        passesFilters = false;
                    }
                }

                if (street != null && !street.isEmpty()) {
                    if (!street.contains(riaLGObject.getStreet().trim().toLowerCase())) {
                        passesFilters = false;
                    }
                }

                if (passesFilters) {
                    temp.add(makeJson(riaLGObject));
                }
            }
        }

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        for (
                String jsonString : temp) {
            jsonArray.add(gson.fromJson(jsonString, JsonObject.class));
        }

        String data = jsonArray.toString();
        return ResponseEntity.ok(data);

    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getBuy(@PathVariable String type) {

        List<String> housesList = new ArrayList<>();
        List<String> apartmentsList = new ArrayList<>();
        List<String> landList = new ArrayList<>();
        List<String> officesList = new ArrayList<>();

        if (type.equals("buy")) {
            housesList.add("http://120064.ligapro.ua/pic/gallery/sell_house/sd6687/8492a_30a59697a2a2db763f1f8fbe66966b1c.jpg");
            housesList.add("http://120064.ligapro.ua/pic/gallery/sell_house/sd6687/dd4c2_42bff16cfa2b0f778844ecb0fd9e8a15.jpg");
            housesList.add("http://120064.ligapro.ua/pic/gallery/sell_house/sd6687/44011_0073dac404fbf578f5efd9f6e35c64d3.jpg");
            housesList.add("http://120064.ligapro.ua/pic/gallery/sell_house/sd6687/a5b07_bce96927f1421da44b94c3756b751f8d.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/sell_app/sd6687/6e663_409d399b54c3d619d66c33e5e8a037f6.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/sell_app/sd6687/a971f_7c5da2939fe85866c6ed2d947a6002b3.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/sell_app/sd6687/86914_196c20eb190d4f876de7efd8b9fd05c6.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/sell_app/sd6687/2367a_6274d3164a2a2847d8acbdd589beec3.webp");
            landList.add("http://120064.ligapro.ua/pic/gallery/sell_zu/sd6687/19ae6_f2685518cd9ade63531403dcb319d190.jpg");
            landList.add("http://120064.ligapro.ua/pic/gallery/sell_zu/sd6687/a7cb968b19ac6c16917700594fb93282.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/sell_nf/sd6687/2a418_abf0617f0b05b20bc30f7cc57aede19a.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/sell_komm/sd6687/418c6e40d8be586548d347b3c82d7883.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/sell_komm/sd6687/fd183_522caee2696d49030fc650810fe01a42.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/sell_komm/sd6687/20820_bdd9c9b8fb86a597abc00e445d744870.jpg");
        } else if (type.equals("rent")) {
            housesList.add("http://120064.ligapro.ua/pic/gallery/rent_house/sd6687/42079_5ae69f13eb590428477ef4f945975af8.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/rent_app/sd6687/68cf7_bd0ccd9359c2cef1e0a56ef2cab9a12c.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/rent_app/sd6687/f144a_6685e01912282b005c8863e512f05a8a.jpg");
            apartmentsList.add("http://120064.ligapro.ua/pic/gallery/rent_app/sd6687/c00d6_2bdbf09c26fe2f398f1b85efea90201f.jpg");
            landList.add("http://120064.ligapro.ua/pic/gallery/sell_zu/sd6687/c48c7_94fc4dfab6cba58a9d13d4a8c61e9934.png");
            officesList.add("http://120064.ligapro.ua/pic/gallery/rent_office/sd6687/d657c_e52d3f10b7cacfbef9aa5822a076f36b.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/rent_komm/sd6687/15523de28d84b2e614b9c8360f7e1557.jpg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/rent_komm/sd6687/7b42d_079873b365a26cc70b141922030d3bb.jpeg");
            officesList.add("http://120064.ligapro.ua/pic/gallery/rent_komm/sd6687/425a6_ccab053b181f1deaa9887ff86e6293a.webp");
        }

        Random random = new Random();

        int randomHousesIndex = random.nextInt(housesList.size());
        String houses = housesList.get(randomHousesIndex);

        int randomApartmentsIndex = random.nextInt(apartmentsList.size());
        String apartments = apartmentsList.get(randomApartmentsIndex);

        int randomLandIndex = random.nextInt(landList.size());
        String land = landList.get(randomLandIndex);

        int randomOfficesIndex = random.nextInt(officesList.size());
        String offices = officesList.get(randomOfficesIndex);

        String imgs = "{\n" +
                "\"houses\": \"" + houses + "\",\n" +
                "\"apartments\": \"" + apartments + "\",\n" +
                "\"land\": \"" + land + "\",\n" +
                "\"offices\": \"" + offices + "\"\n" +
                "}";

        return ResponseEntity.ok(imgs);
    }

    @GetMapping("/test")
    public List<String> getBuy() {

        List<String> names = new ArrayList<>();

        List<LGObject> allObjects = objectService.get();
        for (LGObject lgObject : allObjects) {
            if (!names.contains(lgObject.getSalesAgentName())) {
                names.add(lgObject.getSalesAgentName());
            }
        }
        return names;
    }

    private static String makeJson(RiaLGObject riaLGObject) {

        String img = "to be confirmed";
        if (riaLGObject.getLoc().size() > 0) {
            img = riaLGObject.getLoc().get(0).trim();
        }

        return "{\n" +
                "\"id\": \"" + riaLGObject.getLocalRealtyId() + "\",\n" +
                "\"image\": \"" + img + "\",\n" +
                "\"street\": \"" + riaLGObject.getStreet() + "\",\n" +
                "\"buildingNumber\": \"" + riaLGObject.getBuildingNumber() + "\",\n" +
                "\"district\": \"" + riaLGObject.getDistrict() + "\",\n" +
                "\"metro\": \"" + riaLGObject.getMetro() + "\",\n" +
                "\"totalArea\": \"" + riaLGObject.getTotalArea().trim() + "\",\n" +
                "\"kitchenArea\": \"" + riaLGObject.getKitchenArea().trim() + "\",\n" +
                "\"plotArea\": \"" + riaLGObject.getPlotArea().trim() + "\",\n" +
                "\"rooms\": \"" + riaLGObject.getRoomsCount() + "\",\n" +
                "\"floor\": \"" + riaLGObject.getFloor().trim() + "\",\n" +
                "\"floors\": \"" + riaLGObject.getFloors().trim() + "\",\n" +
                "\"offerType\": \"" + riaLGObject.getOfferType().trim() + "\",\n" +
                "\"newBuildingName\": \"" + riaLGObject.getNewBuildingName() + "\",\n" +
                "\"currency\": \"" + riaLGObject.getCurrency() + "\",\n" +
                "\"price\": \"" + riaLGObject.getPrice().trim() + "\"\n" +
                "}";
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
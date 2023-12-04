package com.levelgroup.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name="RiaLGObjects")
public class RiaLGObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String localRealtyId;
    private String realtyType;
    private String advertType;
    private String district;
    private String metro;
    private String street;
    private String buildingNumber;
    private String roomsCount;
    private String totalArea;
    private String kitchenArea;
    private String plotArea;
    private String plotAreaUnit;
    private String floor;
    private String floors;
    private String price;
    private String currency;
    private String offerType;
    private String newBuildingName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> loc;

    private String eOselya;

}

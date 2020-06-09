package net.rusoil.steam.parser.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class SteamApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private Long appId;
    private String name;
    private String allReviews;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "app")
    private List<Price> prices;

    private LocalDateTime parseDateTime;

    public void addPrice(Price price) {
        if (prices == null) {
            prices = new ArrayList<>();
        }
        prices.add(price);
        price.setApp(this);
    }

    public void deletePrice(Price price) {
        if (prices != null) {
            prices.remove(price);
        }

        price.setApp(null);
    }
}

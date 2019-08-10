package net.varionic.scavngr.controller;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import net.varionic.scavngr.model.Item;
import net.varionic.scavngr.model.ItemMapper;

public class LostItemValidator {
    private ItemMapper mapper = ItemMapper.INSTANCE;

    public Validation<Seq<String>, Item> validate(Item.Input inp) {
        return Validation.combine(validateLon(inp.getLon()), validateLat(inp.getLat())).ap( (x, y) -> mapper.fromInput(inp));
    }

    private Validation<String, Float> validateLat(Float lat) {
        return lat != null
                ? Validation.valid(lat)
                : Validation.invalid("Latitude is required");
    }


    private Validation<String, Float> validateLon(Float lon) {
        return lon != null
                ? Validation.valid(lon)
                : Validation.invalid("Longitude is required");
    }


}

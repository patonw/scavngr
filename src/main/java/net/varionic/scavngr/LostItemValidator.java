package net.varionic.scavngr;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;

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

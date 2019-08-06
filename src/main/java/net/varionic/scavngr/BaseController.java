package net.varionic.scavngr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

public class BaseController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends RuntimeException {
        BadRequestException(String msg) {
            super(msg);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        NotFoundException(String msg) {
            super(msg);
        }
    }

    @Autowired
    ItemMapper mapper;

    @Autowired
    LostItemRepository repo;

    void validateCreate(Item.Input item) {
        // TODO use Vavr Validation applicative functor
        if (item.getLat() == null)
            throw new BadRequestException("Latitude is required");
        if (item.getLon() == null)
            throw new BadRequestException("Longitude is required");

        if (item.getWhenLost() == null)
            throw new BadRequestException("Date and time are required");
    }

    Item updateItem(@PathVariable Long id, @RequestBody Item.Update up) {
        var token = up.getToken();
        if (token == null)
            throw new BadRequestException("A token is required");

        // Find the item and match the tokens
        var item = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));

        if (!Objects.equals(item.getToken(), token))
            throw new BadRequestException("Invalid token");

        var updated = mapper.updateItem(up, item);
        return repo.save(updated);
    }

}

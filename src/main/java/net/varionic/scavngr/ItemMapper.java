package net.varionic.scavngr;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source="whenLost", target="whenLost")
    Item fromInput(Item.Input item);

    @Mapping(source="whenLost", target="whenLost")
    Item.Output toOutput(Item item);

    @Mapping(target="id", ignore=true)
    @Mapping(target="token", ignore=true)
    @Mapping(target="email", ignore=true)
    Item updateItem(Item.Update up, @MappingTarget Item item);

    MatchedItem.Output toOutput(MatchedItem item);
}

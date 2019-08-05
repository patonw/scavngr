package net.varionic.scavngr;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LostItemMapper {
    LostItemMapper INSTANCE = Mappers.getMapper(LostItemMapper.class);

    @Mapping(source="whenLost", target="whenLost")
    LostItem fromInput(LostItem.Input item);

    @Mapping(source="whenLost", target="whenLost")
    LostItem.Output toOutput(LostItem item);

    @Mapping(target="id", ignore=true)
    @Mapping(target="token", ignore=true)
    @Mapping(target="email", ignore=true)
    LostItem updateItem(LostItem.Update up, @MappingTarget LostItem item);
}

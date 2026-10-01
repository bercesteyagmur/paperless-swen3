package at.ac.fhtw.swen3.paperless.business.mapper;

import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagModelMapper {

    Tag toEntity(TagModel tagModel);

    TagModel toModel(Tag tag);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.bananarama.crud.magic.ObjToDto;

/**
 *
 * @author Guglielmo De Concini
 */
public class SimpleMapper implements ObjToDto<SimpleObj, SimpleDto>{

    @Override
    public SimpleDto toDto(SimpleObj obj) {
        SimpleDto dto = new SimpleDto();
        dto.setId(obj.getId());
        dto.setDate(obj.getDate() == null ? null : obj.getDate().format(DateTimeFormatter.ISO_DATE));
        return dto;
    }

    @Override
    public SimpleObj toObj(SimpleDto dto) {
        SimpleObj obj = new SimpleObj();
        obj.setId(dto.getId());
        obj.setDate(dto.getDate() == null ? null:LocalDate.parse(dto.getDate()));
        return obj;
    }

    @Override
    public Class<SimpleDto> dtoType() {
       return SimpleDto.class;
    }

    @Override
    public Class<SimpleObj> objType() {
        return SimpleObj.class;
    }
    
}

package com.kurzawsk.simple_bank.control;


public interface Converter<DTO, DOMAIN> {

    DOMAIN dtoToDomain(DTO dto);

    DTO domainToDto(DOMAIN domain);
}

package com.kurzawsk.simple_bank.control;


public interface Converter<T, D> {

    D dtoToDomain(T dto);

    T domainToDto(D domain);
}

package com.transaction_service.app.converter;


import com.transaction_service.app.enums.OperationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OperationTypeConverter implements AttributeConverter<OperationType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OperationType type) {
        return type != null ? type.getId() : null;
    }

    @Override
    public OperationType convertToEntityAttribute(Integer id) {
        return id != null ? OperationType.fromId(id) : null;
    }
}
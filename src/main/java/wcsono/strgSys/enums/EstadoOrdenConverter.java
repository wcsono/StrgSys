package wcsono.strgSys.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoOrdenConverter implements AttributeConverter<EstadoOrden, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EstadoOrden estado) {
        return estado != null ? estado.getCodigo() : null;
    }

    @Override
    public EstadoOrden convertToEntityAttribute(Integer codigo) {
        return EstadoOrden.fromCodigo(codigo);
    }
}

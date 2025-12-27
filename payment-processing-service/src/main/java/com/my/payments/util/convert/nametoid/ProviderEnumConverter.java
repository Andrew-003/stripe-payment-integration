package com.my.payments.util.convert.nametoid;

import org.modelmapper.AbstractConverter;

import com.my.payments.constant.ProviderEnum;

public class ProviderEnumConverter extends AbstractConverter<String, Integer> {
    @Override
    protected Integer convert(String source) {
        return ProviderEnum.fromName(source).getId();
    }
}

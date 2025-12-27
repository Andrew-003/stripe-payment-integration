package com.my.payments.util.convert.nametoid;

import org.modelmapper.AbstractConverter;

import com.my.payments.constant.PaymentMethodEnum;

public class PaymentMethodEnumConverter extends AbstractConverter<String, Integer> {
    @Override
    protected Integer convert(String source) {
        return PaymentMethodEnum.fromName(source).getId();
    }
}

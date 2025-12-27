package com.my.payments.util.convert.nametoid;

import org.modelmapper.AbstractConverter;

import com.my.payments.constant.PaymentTypeEnum;

public class PaymentTypeEnumConverter extends AbstractConverter<String, Integer> {
    @Override
    protected Integer convert(String source) {
        return PaymentTypeEnum.fromName(source).getId();
    }
}

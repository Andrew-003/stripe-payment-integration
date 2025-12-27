package com.my.payments.util.convert.idtoname;

import org.modelmapper.AbstractConverter;

import com.my.payments.constant.PaymentMethodEnum;

public class PaymentMethodEnumIdToNameConverter extends AbstractConverter<Integer, String> {
    @Override
    protected String convert(Integer source) {
        return PaymentMethodEnum.fromId(source).getName();
    }
}

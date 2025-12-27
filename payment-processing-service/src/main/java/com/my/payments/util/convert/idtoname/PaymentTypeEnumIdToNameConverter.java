package com.my.payments.util.convert.idtoname;

import org.modelmapper.AbstractConverter;

import com.my.payments.constant.PaymentTypeEnum;

public class PaymentTypeEnumIdToNameConverter extends AbstractConverter<Integer, String> {
    @Override
    protected String convert(Integer source) {
        return PaymentTypeEnum.fromId(source).getName();
    }
}

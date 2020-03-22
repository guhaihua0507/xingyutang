package com.xingyutang.rongchuang.service;

import com.xingyutang.exception.RequestException;
import com.xingyutang.rongchuang.model.entity.RongchuangSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RongchuangSupplierConventionService {
    RongchuangSupplier createSupplier(RongchuangSupplier entity);

    List<RongchuangSupplier> listSuppliers();

    RongchuangSupplier getSupplierByUserId(Long userId);

    void signIn(Long userId) throws RequestException;

    int lottery(long userId) throws RequestException;

    InputStream exportSuppliers() throws IOException;
}

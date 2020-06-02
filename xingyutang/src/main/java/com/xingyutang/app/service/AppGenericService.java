package com.xingyutang.app.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

public interface AppGenericService {
    InputStream exportAsInputStream(Workbook wb);
}

package com.xingyutang.app.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

public interface CommonService {
    InputStream exportAsInputStream(Workbook wb);
}

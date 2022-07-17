package com.wuw.common.server.util.excel.merge;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestExcel {

    public static void main(String[] args) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFSheet sheet = workbook.createSheet("sheet");
        List<AAA> aaaList = data();
        int rows =1;
        for (int i = 0;i<aaaList.size();i++){


            List<BBB> bbbList = aaaList.get(i).getBbbs();
            int rowAFirst = rows;
            int rowALast = rows;

            for (int j= 0;j<bbbList.size();j++){

                List<CCC> cccList = bbbList.get(j).getCccs();
                int cccSize = cccList.size();
                int rowBFirst = rows;
                int rowBLast = rows+ cccSize-1;
                rowALast = rowBLast +bbbList.size() - 1 ;

                for (int k = 0;k<cccSize;k++){
                    HSSFRow row = sheet.createRow(rows++);
                    row.createCell(0).setCellValue(aaaList.get(i).getAaa());
                    row.createCell(1).setCellValue(bbbList.get(j).getBbb());
                    row.createCell(2).setCellValue(cccList.get(k).getCcc());

                }
                // 拼接
                CellRangeAddress region = new CellRangeAddress(rowBFirst, rowBLast, 1, 1);
                sheet.addMergedRegion(region);

            }
            // 拼接
            System.out.println(rowAFirst);
            System.out.println(rowALast);
            CellRangeAddress region = new CellRangeAddress(rowAFirst, rowALast, 0, 0);
            sheet.addMergedRegion(region);

        }


        File file = new File("G:\\demo.xls");
        FileOutputStream fout = new FileOutputStream(file);
        workbook.write(fout);
        fout.close();
    }


    private static List<AAA> data(){
        List<AAA> aaas = new ArrayList<>();

        AAA aaa = new AAA();
        aaa.setAaa("aaa");

        List<BBB> bbbs = new ArrayList<>();
        BBB bbb = new BBB();
        bbb.setBbb("bbb");
        List<CCC> cccs = new ArrayList<>();
        CCC ccc = new CCC();
        ccc.setCcc("ccc");
        cccs.add(ccc);
        CCC ccc2 = new CCC();
        ccc2.setCcc("ccc2");
        cccs.add(ccc2);
        bbb.setCccs(cccs);
        bbbs.add(bbb);
        aaa.setBbbs(bbbs);

        AAA aaa2 = new AAA();
        aaa2.setAaa("aaa2");
        List<BBB> bbbs3 = new ArrayList<>();
        BBB bbb3 = new BBB();
        bbb3.setBbb("bbb3");
        List<CCC> cccs4 = new ArrayList<>();
        CCC ccc5 = new CCC();
        ccc5.setCcc("ccc5");
        cccs4.add(ccc5);
        CCC ccc6 = new CCC();
        ccc6.setCcc("ccc6");
        cccs4.add(ccc6);
        bbb3.setCccs(cccs4);
        bbbs3.add(bbb3);
        aaa2.setBbbs(bbbs3);

        aaas.add(aaa);
        aaas.add(aaa2);
        System.out.println(JSON.toJSONString(aaas));


        return aaas;
    }

}

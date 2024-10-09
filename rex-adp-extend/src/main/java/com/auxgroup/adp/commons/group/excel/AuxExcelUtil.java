package com.auxgroup.adp.commons.group.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.auxdemo.adp.commons.group.excel.convert.ExcelBigNumberConvert;
import com.auxdemo.adp.commons.group.excel.listener.DefaultExcelListener;
import com.auxdemo.adp.commons.group.excel.listener.ExcelListener;
import com.auxdemo.adp.commons.group.excel.result.ExcelResult;
import com.auxdemo.adp.commons.group.excel.strategy.CellMergeStrategy;
import com.auxdemo.adp.commons.utils.AuxStringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AuxExcelUtil {
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return ((ExcelReaderBuilder) EasyExcelFactory.read(is).head(clazz)).autoCloseStream(false).sheet().doReadSync();
    }

    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener(isValidate);
        EasyExcelFactory.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        exportExcel(list, sheetName, clazz, false, response);
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, boolean merge, HttpServletResponse response) {
        try {
            resetResponse(sheetName, response);
            ServletOutputStream os = response.getOutputStream();
            ExcelWriterSheetBuilder builder = ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(os, clazz).autoCloseStream(false).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())).registerConverter(new ExcelBigNumberConvert())).sheet(sheetName);
            if (merge) {
                builder.registerWriteHandler(new CellMergeStrategy(list, true));
            }

            builder.doWrite(list);
        } catch (IOException var7) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    public static void exportTemplate(List<Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            ExcelWriter excelWriter = ((ExcelWriterBuilder)EasyExcel.write(response.getOutputStream()).withTemplate(templateResource.getStream()).autoCloseStream(false).registerConverter(new ExcelBigNumberConvert())).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            if (CollUtil.isEmpty(data)) {
                throw new IllegalArgumentException("数据为空");
            } else {
                Iterator var7 = data.iterator();

                while(var7.hasNext()) {
                    Object d = var7.next();
                    excelWriter.fill(d, writeSheet);
                }

                excelWriter.finish();
            }
        } catch (IOException var9) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    public static void exportTemplateMultiList(Map<String, Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            ExcelWriter excelWriter = ((ExcelWriterBuilder)EasyExcel.write(response.getOutputStream()).withTemplate(templateResource.getStream()).autoCloseStream(false).registerConverter(new ExcelBigNumberConvert())).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            if (CollUtil.isEmpty(data)) {
                throw new IllegalArgumentException("数据为空");
            } else {
                Iterator var7 = data.entrySet().iterator();

                while(var7.hasNext()) {
                    Map.Entry<String, Object> map = (Map.Entry)var7.next();
                    FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                    if (map.getValue() instanceof Collection) {
                        excelWriter.fill(new FillWrapper((String)map.getKey(), (Collection)map.getValue()), fillConfig, writeSheet);
                    } else {
                        excelWriter.fill(map.getValue(), writeSheet);
                    }
                }

                excelWriter.finish();
            }
        } catch (IOException var10) {
            throw new RuntimeException("导出Excel异常");
        }
    }

    private static void resetResponse(String sheetName, HttpServletResponse response) throws UnsupportedEncodingException {
        String filename = encodingFilename(sheetName);
        //AuxFileUtil.setAttachmentResponseHeader(response, filename);

        String encode = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
        String percentEncodedFileName =  encode.replaceAll("\\+", "%20");

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=").append(percentEncodedFileName).append(";").append("filename*=").append("utf-8''").append(percentEncodedFileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }

    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        String[] var5 = convertSource;
        int var6 = convertSource.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String item = var5[var7];
            String[] itemArray = item.split("=");
            if (StrUtil.containsAny(propertyValue, separator)) {
                String[] var10 = propertyValue.split(separator);
                int var11 = var10.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String value = var10[var12];
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1] + separator);
                        break;
                    }
                }
            } else if (itemArray[0].equals(propertyValue)) {
                return itemArray[1];
            }
        }

        return AuxStringUtils.stripEnd(propertyString.toString(), separator);
    }

    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        String[] var5 = convertSource;
        int var6 = convertSource.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String item = var5[var7];
            String[] itemArray = item.split("=");
            if (StrUtil.containsAny(propertyValue, separator)) {
                String[] var10 = propertyValue.split(separator);
                int var11 = var10.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String value = var10[var12];
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0] + separator);
                        break;
                    }
                }
            } else if (itemArray[1].equals(propertyValue)) {
                return itemArray[0];
            }
        }

        return AuxStringUtils.stripEnd(propertyString.toString(), separator);
    }

    public static String encodingFilename(String filename) {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx";
    }

    private AuxExcelUtil() {
    }
}

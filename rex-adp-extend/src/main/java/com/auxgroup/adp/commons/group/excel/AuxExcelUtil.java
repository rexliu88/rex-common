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
import lombok.NoArgsConstructor;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类，提供导入、导出、模板填充等功能。
 */
@NoArgsConstructor
public class AuxExcelUtil {
    /**
     * 导入Excel文件并同步读取数据。
     *
     * @param is    输入流，指向Excel文件内容
     * @param clazz 数据实体类类型，用于映射Excel中的每一行数据
     * @param <T>   泛型参数，表示数据实体类类型
     * @return 返回解析后的数据列表
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return ((ExcelReaderBuilder) EasyExcelFactory.read(is).head(clazz)).autoCloseStream(false).sheet().doReadSync();
    }

    /**
     * 导入Excel文件，并根据是否验证标志决定是否进行数据校验。
     *
     * @param is         输入流，指向Excel文件内容
     * @param clazz      数据实体类类型，用于映射Excel中的每一行数据
     * @param isValidate 是否启用数据校验
     * @param <T>        泛型参数，表示数据实体类类型
     * @return 返回封装了导入结果的对象，包括成功数据和错误信息等
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<>(isValidate);
        EasyExcelFactory.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 使用自定义监听器导入Excel文件。
     *
     * @param is       输入流，指向Excel文件内容
     * @param clazz    数据实体类类型，用于映射Excel中的每一行数据
     * @param listener 自定义的Excel监听器，处理每条记录及异常情况
     * @param <T>      泛型参数，表示数据实体类类型
     * @return 返回封装了导入结果的对象，由监听器提供
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 将指定的数据集合导出为Excel文件，默认不合并单元格。
     *
     * @param list       要导出的数据集合
     * @param sheetName  Excel工作表名称
     * @param clazz      数据实体类类型，用于列头与字段映射
     * @param response   HTTP响应对象，用于输出Excel文件到客户端
     * @param <T>        泛型参数，表示数据实体类类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        exportExcel(list, sheetName, clazz, false, response);
    }

    /**
     * 将指定的数据集合导出为Excel文件，支持设置是否合并相同行。
     *
     * @param list       要导出的数据集合
     * @param sheetName  Excel工作表名称
     * @param clazz      数据实体类类型，用于列头与字段映射
     * @param merge      是否开启自动合并相同行功能
     * @param response   HTTP响应对象，用于输出Excel文件到客户端
     * @param <T>        泛型参数，表示数据实体类类型
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, boolean merge, HttpServletResponse response) {
        try {
            resetResponse(sheetName, response);
            ServletOutputStream os = response.getOutputStream();
            ExcelWriterSheetBuilder builder = ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write(os, clazz)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()))
                    .registerConverter(new ExcelBigNumberConvert()))
                    .sheet(sheetName);
            if (merge) {
                builder.registerWriteHandler(new CellMergeStrategy(list, true));
            }
            builder.doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常", e);
        }
    }

    /**
     * 根据模板路径将单个数据对象填充至Excel模板中并导出。
     *
     * @param data          填充到模板中的数据对象（通常是一个Map或Bean）
     * @param filename      下载时显示的文件名
     * @param templatePath  模板文件在classpath下的相对路径
     * @param response      HTTP响应对象，用于输出Excel文件到客户端
     */
    public static void exportTemplate(List<Object> data, String filename, String templatePath, HttpServletResponse response) {
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据为空");
        }
        try {
            resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            try (InputStream templateStream = templateResource.getStream();
                 ServletOutputStream os = response.getOutputStream()) {
                ExcelWriter excelWriter = ((ExcelWriterBuilder) EasyExcel.write(os)
                        .withTemplate(templateStream)
                        .autoCloseStream(false)
                        .registerConverter(new ExcelBigNumberConvert()))
                        .build();
                WriteSheet writeSheet = EasyExcel.writerSheet().build();
                for (Object d : data) {
                    excelWriter.fill(d, writeSheet);
                }
                excelWriter.finish();
            }
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常", e);
        }
    }

    /**
     * 支持多个列表数据填充的模板导出方法。
     *
     * @param data          包含多个键值对的数据映射，其中值可以是单个对象或集合
     * @param filename      下载时显示的文件名
     * @param templatePath  模板文件在classpath下的相对路径
     * @param response      HTTP响应对象，用于输出Excel文件到客户端
     */
    public static void exportTemplateMultiList(Map<String, Object> data, String filename, String templatePath, HttpServletResponse response) {
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据为空");
        }
        try {
            resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            try (InputStream templateStream = templateResource.getStream();
                 ServletOutputStream os = response.getOutputStream()) {
                ExcelWriter excelWriter = ((ExcelWriterBuilder) EasyExcel.write(os)
                        .withTemplate(templateStream)
                        .autoCloseStream(false)
                        .registerConverter(new ExcelBigNumberConvert()))
                        .build();
                WriteSheet writeSheet = EasyExcel.writerSheet().build();
                FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getValue() instanceof Collection) {
                        excelWriter.fill(new FillWrapper(entry.getKey(), (Collection<?>) entry.getValue()), fillConfig, writeSheet);
                    } else {
                        excelWriter.fill(entry.getValue(), writeSheet);
                    }
                }
                excelWriter.finish(); // 完成写入
            }
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常", e);
        }
    }

    /**
     * 配置HTTP响应头以正确下载Excel文件。
     *
     * @param sheetName 文件名（不含扩展名）
     * @param response  HTTP响应对象
     * @throws UnsupportedEncodingException 当字符编码不被支持时抛出此异常
     */
    private static void resetResponse(String sheetName, HttpServletResponse response) throws UnsupportedEncodingException {
        String filename = encodingFilename(sheetName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");

        String encode = URLEncoder.encode(sheetName, StandardCharsets.UTF_8.toString());
        String percentEncodedFileName = encode.replaceAll("\\+", "%20");
        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=").append(percentEncodedFileName).append(";").append("filename*=").append("utf-8''").append(percentEncodedFileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 根据表达式转换属性值。
     *
     * @param propertyValue 属性原始值
     * @param converterExp  表达式字符串，格式如："0=男,1=女"
     * @param separator     分隔符，用于分割多个值的情况
     * @return 转换后对应的描述文本
     */
    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StrUtil.containsAny(propertyValue, separator)) {
                String[] values = propertyValue.split(separator);
                for (String value : values) {
                    if (itemArray[0].equals(value)) {
                        propertyString.append(itemArray[1]).append(separator);
                        break;
                    }
                }
            } else if (itemArray[0].equals(propertyValue)) {
                return itemArray[1];
            }
        }
        return AuxStringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 反向查找属性值，即通过描述反查原始值。
     *
     * @param propertyValue 描述文本
     * @param converterExp  表达式字符串，格式如："0=男,1=女"
     * @param separator     分隔符，用于分割多个值的情况
     * @return 对应的原始属性值
     */
    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(",");
        for (String item : convertSource) {
            String[] itemArray = item.split("=");
            if (StrUtil.containsAny(propertyValue, separator)) {
                String[] values = propertyValue.split(separator);
                for (String value : values) {
                    if (itemArray[1].equals(value)) {
                        propertyString.append(itemArray[0]).append(separator);
                        break;
                    }
                }
            } else if (itemArray[1].equals(propertyValue)) {
                return itemArray[0];
            }
        }
        return AuxStringUtils.stripEnd(propertyString.toString(), separator);
    }


    /**
     * 编码文件名，避免中文乱码问题。
     *
     * @param filename 原始文件名
     * @return 加上唯一标识前缀的新文件名
     */
    public static String encodingFilename(String filename) {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx";
    }
}
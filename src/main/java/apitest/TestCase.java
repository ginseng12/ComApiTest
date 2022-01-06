package apitest;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

/**
 * @author lulu
 * @data 2021/12/28 4:08 下午
 */
@Data
public class TestCase {
    /**
     * order：排序
     * isRun:开关
     * caseName:用例名称
     * type：请求类型
     * url：请求地址
     */
    @ExcelField(title = "顺序")
    private int order;
    @ExcelField(title = "是否开启")
    private String isRun;

    @ExcelField(title ="独立运行")
    private String isSingle;

    @ExcelField(title = "地址")
    private String url;
    @ExcelField(title = "类型")
    private String type;

    @ExcelField(title = "用例名称")
    private String caseName;

    //读写转换
    @ExcelField(title = "参数", readConverter =FileReadConvertible.class)
    private String params;

    @ExcelField(title = "头部")
    private String headers;

    @ExcelField(title = "数据提取json格式")
    private String resultJson;


    @ExcelField(title = "数据提取正则")
    private String resultRegx;

    @ExcelField(title = "结果检查")
    private String resultCheck;

    @ExcelField(title = "数据库检查")
    private String dbcheck;
}

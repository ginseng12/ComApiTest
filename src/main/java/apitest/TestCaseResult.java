package apitest;

import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

/**
 * @author lulu
 * @data 2022/1/6 11:10 上午
 */

@Data
public class TestCaseResult {
    @ExcelField(title = "顺序")
    private int order;

    @ExcelField(title = "地址")
    private String url;
    @ExcelField(title = "类型")
    private String type;

    @ExcelField(title = "用例名称")
    private String caseName;

    //读写转换
    @ExcelField(title = "参数")
    private String params;

    @ExcelField(title = "头部")
    private String headers;

    @ExcelField(title = "数据提取json格式")
    private String resultJson;


    @ExcelField(title = "数据提取正则")
    private String resultRegx;

    @ExcelField(title = "检查结果")
    private String resultCheck;


    @ExcelField(title = "数据库检查结果")
    private String dbcheckresult;


}

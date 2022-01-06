package apitest;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.crab2died.ExcelUtils;
import com.github.crab2died.exceptions.Excel4JException;

import apitest.utils.CheckPointUtils;
import apitest.utils.DbCheckUtils;
import apitest.utils.EmailUtils;
import apitest.utils.ExcelToMapUtils;
import apitest.utils.HttpClientUtils;
import apitest.utils.JsonCheckResult;
import apitest.utils.ParamsUtils;

/**
 * @author lulu
 * @data 2021/11/16 9:53 下午
 *
 * ApiTest为主方法
 * 读Exexl数据或者读txt文件数据
 * 根据orderid排序
 */
public class ApiTest {
    public static void main(String[] args) throws Exception {
        String dir = System.getProperty("user.dir");
        System.out.println(dir);
        String path = dir + File.separator + "data" + File.separator; // 1000千万

        // 第一种方式
        // List<TestCase> testCasesList = getByText(path+"apitest.txt");
        // 第二种方式
        // HttpClientUtils.openPoxy = true;
        String excelPath = path + "apitest2.xlsx";
        // 有关系
        List<TestCaseResult> allResults = testCaseParams(excelPath, 0);
        System.out.println("----" + allResults.size());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-dd");

        String dateString = simpleDateFormat.format(new Date());
        String resultPath=path+"result_" + dateString + ".xlsx";
        // 结果不被覆盖，保留以前结果
        ExcelUtils.getInstance().exportObjects2Excel(allResults, TestCaseResult.class,
                resultPath);
        EmailUtils.sendEmailsWithAttachments("测试结果", "请查收", resultPath);
        // testCaseParams(excelPath,0);
        // testCaseParams(excelPath,1);
        // 无关系
        testCaseSingle(excelPath, 0);
    }

    private static List<TestCaseResult> testCaseParams(String excelPath, int index) {
        List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
        try {
            List<Map<String, Object>> listMaps = ExcelToMapUtils.importExcel(excelPath, 1);
            for (Map<String, Object> map : listMaps) {
                System.out.println("map " + map);
                ParamsUtils.addMap(map);
                List<TestCase> testCasesList = getByExcel(excelPath, index);
                testCasesList = testCasesList.stream().filter(d -> d.getIsSingle().equals("否"))
                        .collect(Collectors.toList());
                for (TestCase testCase : testCasesList) {
                    TestCaseResult testCaseResult = new TestCaseResult();
                    beforeReplace(testCase);
                    String result = "";
                    if ("get".equals(testCase.getType())) {
                        result = HttpClientUtils.doGet(testCase.getUrl(), testCase.getHeaders());
                        System.out.println(testCase + " result" + result);
                    } else if ("post".equals(testCase.getType())) {
                        result = HttpClientUtils.doPost(testCase.getUrl(), testCase.getParams(), testCase.getHeaders());
                    } else if ("postjson".equals(testCase.getType())) {
                        result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(),
                                testCase.getHeaders());
                    }

                    ParamsUtils.addFromJson(result, testCase.getResultJson());
                    ParamsUtils.addFromRegx(result, testCase.getResultRegx());

                    BeanUtils.copyProperties(testCaseResult, testCase);

                    resultCheck(testCase, result, testCaseResult);
                    testCaseResults.add(testCaseResult);
                }

                ParamsUtils.clear();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return testCaseResults;
    }

    private static void resultCheck(TestCase testCase, String result, TestCaseResult testCaseResult)
            throws SQLException {
        if (StringUtils.isNotBlank(testCase.getResultCheck())) {
            JsonCheckResult checkReulst = CheckPointUtils.check(result, testCase.getResultCheck());
            testCaseResult.setResultCheck(checkReulst.getMsg());
        } else {
            testCaseResult.setResultCheck("返回结果不需要检查");
        }

        // 数据库检查
        if (StringUtils.isNotBlank(testCase.getDbcheck())) {
            boolean dbcheck = DbCheckUtils.dbCheck(testCase.getDbcheck());
            if (dbcheck) {
                testCaseResult.setDbcheckresult("数据库检查通过");
            } else {
                testCaseResult.setDbcheckresult("数据检查失败");
            }
        } else {
            testCaseResult.setDbcheckresult("没有设置数据库检查");
        }

    }

//	//参数化 StringUtils dbutils, json , CheckPointUtils
//	//select * from t_user_test where uid='${id2}',size>1,mysql1
//	private  static boolean dbCheck(String dbCheck) throws SQLException {
//		if(StringUtils.isNotBlank(dbCheck)) {
//		  String[] dbcheck_array=dbCheck.split(",");
//		  String sql=dbcheck_array[0];
//		  sql=ParamsUtils.replace(sql);
//		   QueryRunner runner = new QueryRunner(JdbcUtils.getDataSource(dbcheck_array[2]));
//		   System.out.println("sql "+sql);
//		   List<Map<String, Object>> list =  runner.query(sql, new MapListHandler());
//		   System.out.println("结果"+list.size());
//		   JsonCheckResult jsonrResult= CheckPointUtils.check(JSON.toJSONString(list), dbcheck_array[1]);
//		   System.out.println("检查结果"+jsonrResult.getMsg());
//		   return jsonrResult.isResult();
//		}
//		return false;
//	}

    private static void testCaseSingle(String excelPath, int index) {

        try {
            List<TestCase> testCasesList = getByExcel(excelPath, index);
            testCasesList = testCasesList.stream().filter(d -> d.getIsSingle().equals("是"))
                    .collect(Collectors.toList());
            for (TestCase testCase : testCasesList) {
                String result = "";
                if ("get".equals(testCase.getType())) {
                    result = HttpClientUtils.doGet(testCase.getUrl(), testCase.getHeaders());
                    System.out.println(testCase + " result" + result);
                } else if ("post".equals(testCase.getType())) {
                    result = HttpClientUtils.doPost(testCase.getUrl(), testCase.getParams(), testCase.getHeaders());
                } else if ("postjson".equals(testCase.getType())) {
                    result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(), testCase.getHeaders());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 重构优化
    private static void beforeReplace(TestCase testCase) {
        testCase.setUrl(ParamsUtils.replace(testCase.getUrl()));
        testCase.setHeaders(ParamsUtils.replace(testCase.getHeaders()));
        testCase.setParams(ParamsUtils.replace(testCase.getParams()));
//		Map<String, Object> map = ParamsUtils.get();
//		String url = testCase.getUrl();
//		if (StringUtils.isNotBlank(url)) {
//			String pattern = "\\$\\{(.*?)\\}";
//			Pattern r = Pattern.compile(pattern);
//			Matcher m = r.matcher(url);
//			while (m.find()) {
//				System.out.println(m.group(0));
//				System.out.println(m.group(1));
//				url = url.replace(m.group(0), MapUtils.getString(map, m.group(1), ""));
//			}
//			testCase.setUrl(url);
//		}

//		String params = testCase.getParams();
//		if (StringUtils.isNotBlank(params)) {
//			String pattern = "\\$\\{(.*?)\\}";
//			Pattern r = Pattern.compile(pattern);
//			Matcher m = r.matcher(params);
//			while (m.find()) {
//				System.out.println(m.group(0));
//				System.out.println(m.group(1));
//				params = params.replace(m.group(0), MapUtils.getString(map, m.group(1), ""));
//			}
//			testCase.setParams(params);
//		}

//		String header = testCase.getHeaders();
//		if (StringUtils.isNotBlank(header)) {
//			String pattern = "\\$\\{(.*?)\\}";
//			Pattern r = Pattern.compile(pattern);
//			Matcher m = r.matcher(header);
//			while (m.find()) {
//				System.out.println(m.group(0));
//				System.out.println(m.group(1));
//				header = header.replace(m.group(0), MapUtils.getString(map, m.group(1), ""));
//			}
//			testCase.setHeaders(header);
//		}
    }

    private static List<TestCase> getByExcel(String path, int index) {
        List<TestCase> list = null;
        try {
            list = ExcelUtils.getInstance().readExcel2Objects(path, TestCase.class, index);
            // 排序
            Collections.sort(list, (o1, o2) -> {
                return o1.getOrder() - o2.getOrder();
            });

            list = list.stream().filter(d -> d.getIsRun().equals("是")).collect(Collectors.toList());
        } catch (Excel4JException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<TestCase> getByText(String path) {
        List<TestCase> testCasesList = new ArrayList<TestCase>();
        try {
            List<String> lines = FileUtils.readLines(new File(path), "utf-8");
            int count = 0;
            for (String line : lines) {
                if (count++ > 0) {
                    TestCase testCase = new TestCase();

                    String[] line_array = line.split(";");
                    testCase.setOrder(Integer.parseInt(line_array[0]));
                    testCase.setIsRun(line_array[1]);
                    testCase.setUrl(line_array[4]);
                    testCase.setType(line_array[3]);
                    testCase.setCaseName(line_array[2]);
                    testCasesList.add(testCase);
                }
            }
            testCasesList.forEach(d -> System.out.println(d));

        } catch (IOException e) {

            e.printStackTrace();
        }

        return testCasesList;

    }

}

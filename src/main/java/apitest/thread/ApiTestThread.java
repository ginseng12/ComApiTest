package apitest.thread;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.crab2died.ExcelUtils;
import com.github.crab2died.exceptions.Excel4JException;

import apitest.TestCase;
import apitest.TestCaseResult;
import apitest.utils.CheckPointUtils;
import apitest.utils.DbCheckUtils;
import apitest.utils.EmailUtils;
import apitest.utils.ExcelToMapUtils;
import apitest.utils.HttpClientUtils;
import apitest.utils.JsonCheckResult;
import apitest.utils.ParamsUtils;

/**
 * @author lulu
 * @data 2022/1/6 11:08 上午
 */
public class ApiTestThread {
    private static final Logger logger = LoggerFactory.getLogger(ApiTestThread.class);
    public final static String path = System.getProperty("user.dir") + File.separator + "data" + File.separator;
    public static List<String> emailList=new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        //main
        // 第一种方式
        // List<TestCase> testCasesList = getByText(path+"apitest.txt");
        // 第二种方式
        HttpClientUtils.openPoxy = true;
        String excelPath = path + "apitest2.xlsx";
        // 有关系
        testCaseParams(excelPath, 0);  //启了线程

        //TimeUnit.MINUTES.sleep(1);

        //主线程等子线程全部测完后发邮件



        // testCaseParams(excelPath,0);
        // testCaseParams(excelPath,1);
        // 无关系
//		testCaseSingle(excelPath, 0);
    }

    private static void testCaseParams(String excelPath, int index)
            throws Excel4JException, IOException {
        try {
            List<Map<String, Object>> listMaps = ExcelToMapUtils.importExcel(excelPath, 1);
            int i = 0;
            //100参数
            CountDownLatch latch = new CountDownLatch(listMaps.size());
            //控制并行 tomcat 默认线程池 200
            ExecutorService cachedThreadPool  = Executors.newFixedThreadPool(200);
            for (Map<String, Object> map : listMaps) {
                System.out.println("map " + map);
                //并行测试
                CaseTask task =new CaseTask(excelPath, index, i++,latch);
                task.setMap(map);
                // task.start();
                cachedThreadPool.submit(task);
            }

            latch.await(3,TimeUnit.MINUTES);

            String[] array=new String[emailList.size()];
            int count=0;
            for (String string : emailList) {
                array[count++]=string;
            }

            System.out.println("----" + emailList.size());
            EmailUtils.sendEmailsWithAttachments("测试结果", "请查收", array);
            cachedThreadPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace(); //控制台
            logger.error(e.getMessage());
        }
    }

    public static void resultCheck(TestCase testCase, String result, TestCaseResult testCaseResult)
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
    public static void beforeReplace(TestCase testCase) {
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

    public static List<TestCase> getByExcel(String path, int index) {
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

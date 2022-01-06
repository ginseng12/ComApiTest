package apitest.testng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.crab2died.ExcelUtils;

import apitest.TestCase;
import apitest.TestCaseResult;
import apitest.thread.ApiTestThread;
import apitest.thread.CaseTask;
import apitest.utils.EmailUtils;
import apitest.utils.ExcelToMapUtils;
import apitest.utils.HttpClientUtils;
import apitest.utils.ParamsUtils;

/**
 * @author lulu
 * @data 2022/1/6 1:44 下午
 */
public class TestNgTest {
    private static final Logger logger = LoggerFactory.getLogger(TestNgTest.class);


    public final static String path = System.getProperty("user.dir") + File.separator + "data" + File.separator;

    //读取excel参数
    @DataProvider(name = "excel",parallel = true)  //parallel = true 并行
    public Iterator<Object[]> initParmas() {

        List<Map<String, Object>> listMaps = ExcelToMapUtils.importExcel(path+"apitest2.xlsx", 1);
        List<Object[]> dataProvider = new ArrayList<Object[]>();

        listMaps.forEach(d->dataProvider.add(new Object[] {d}));

        return dataProvider.iterator();
    }


    @Test(dataProvider = "excel")
    public void testCase(Map<String,Object> map) {
        try {
            ParamsUtils.addMap(map);
            System.out.println("map---"+map);
            List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
            List<TestCase> testCasesList = ApiTestThread.getByExcel(path+"apitest2.xlsx", 0);
            testCasesList = testCasesList.stream().filter(d -> d.getIsSingle().equals("否"))
                    .collect(Collectors.toList());
            // 100测试案例
            for (TestCase testCase : testCasesList) {
                TestCaseResult testCaseResult = new TestCaseResult();
                ApiTestThread.beforeReplace(testCase);
                String result = "";
                if ("get".equals(testCase.getType())) {
                    result = HttpClientUtils.doGet(testCase.getUrl(), testCase.getHeaders());
                    System.out.println(testCase + " result" + result);
                } else if ("post".equals(testCase.getType())) {
                    result = HttpClientUtils.doPost(testCase.getUrl(), testCase.getParams(), testCase.getHeaders());
                } else if ("postjson".equals(testCase.getType())) {
                    result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(), testCase.getHeaders());
                }

                ParamsUtils.addFromJson(result, testCase.getResultJson());
                ParamsUtils.addFromRegx(result, testCase.getResultRegx());

                BeanUtils.copyProperties(testCaseResult, testCase);

                ApiTestThread.resultCheck(testCase, result, testCaseResult);
                testCaseResults.add(testCaseResult);
            }

            ParamsUtils.clear();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

            String dateString = simpleDateFormat.format(new Date());
            String dir = System.getProperty("user.dir");
            logger.info(dir);
            System.out.println(dir); // 控制台
            String path = dir + File.separator + "data" + File.separator;
            String resultPath = path + "result_" + dateString + ".xlsx";
            // 结果不被覆盖，保留以前结果
            ExcelUtils.getInstance().exportObjects2Excel(testCaseResults, TestCaseResult.class, resultPath);
            ApiTestThread.emailList.add(resultPath);
            testCaseResults.clear();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @BeforeClass
    public void before() {
        //HttpClientUtils.openPoxy=true;
    }

    //生命周期 等待所有测试完毕
    @AfterClass
    public void after() {
        String[] array=new String[ApiTestThread.emailList.size()];
        int count=0;
        for (String string : ApiTestThread.emailList) {
            array[count++]=string;
        }

        System.out.println("----" + ApiTestThread.emailList.size());
        try {
            EmailUtils.sendEmailsWithAttachments("测试结果", "请查收", array);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}

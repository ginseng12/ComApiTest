package apitest.thread;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.crab2died.ExcelUtils;

import apitest.TestCase;
import apitest.TestCaseResult;
import apitest.utils.HttpClientUtils;
import apitest.utils.ParamsUtils;

/**
 * @author lulu
 * @data 2022/1/6 11:08 上午
 */
public class CaseTask extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(CaseTask.class);

    private String excelPath;
    private int index;
    private int count;
    private CountDownLatch latch;

    private Map<String, Object> map;

    public CaseTask(String excelPath, int index, int count, CountDownLatch latch) {
        super();
        this.excelPath = excelPath;
        this.index = index;
        this.count = count;
        this.latch = latch;
    }



    public void setMap(Map<String, Object> map) {
        this.map = map;
    }



    @Override
    public void run() {
        try {
            ParamsUtils.addMap(map);
            System.out.println("map---"+map);
            List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
            List<TestCase> testCasesList = ApiTestThread.getByExcel(excelPath, index);
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
            String resultPath = path + "result_" + dateString + count + ".xlsx";
            // 结果不被覆盖，保留以前结果
            ExcelUtils.getInstance().exportObjects2Excel(testCaseResults, TestCaseResult.class, resultPath);
            ApiTestThread.emailList.add(resultPath);
            testCaseResults.clear();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally {
            latch.countDown();
        }

    }
}

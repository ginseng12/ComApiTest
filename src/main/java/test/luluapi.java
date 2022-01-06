//package test;
//
//import apitest.TestCase;
//import com.github.crab2died.ExcelUtils;
//import httpclinent.HttpClientUtils;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author lulu
// * @data 2021/12/31 10:50 上午
// */
//public class luluapi {
//
//    public static void main(String[] args) throws IOException {
//        String dir = System.getProperty("user.dir");
//        String path =dir + File.separator+"data"+File.separator;
//
//        /**
//         * 第一种方式使用TXT格式文件
//         */
////        List<TestCase> testCaseList = getByText(path + "apitest");
//
//        /**
//         * 第二种方法使用Excel表的方式
//         */
//
//        List<TestCase> testCaseList = getByExcel(path + "apitest.xlsx");
//
//        /**
//         * 获取文件中的接口用例
//         */
//        testCaseList.forEach(f -> System.out.println(f));
//
////        System.out.println("排序后的结果：");
//
//        /**
//         * 根据order排序，排序规则为从小到大
//         */
////        System.out.println("分割：--------------------");
//        Collections.sort(testCaseList, ((o1, o2) -> {
//            return o1.getOrder() - o2.getOrder();
//        }));
//
//        testCaseList.forEach(d -> System.out.println(d));
//
////        System.out.println("分割：--------------------");
//        /**
//         * 根据isRun字段判断过滤开启状态的用例
//         */
////        System.out.println("过滤结果");
//
//        testCaseList = testCaseList.stream().filter(d -> d.getIsRun().equals("是")).
//                collect(Collectors.toList());
//        for(TestCase testCase : testCaseList){
//            String result = "";
//            if("get".equals(testCase.getType())){
//                result = HttpClientUtils.doGet(testCase.getUrl());
//            }
//            System.out.println(testCase+"result"+ result);
//        }
//
//    }
//
//    //TXT实现方法
//    private static List<TestCase> getByText(String path) {
//
//        List<TestCase> testCaseList = new ArrayList<TestCase>();
//        try {
//            List<String> lines = FileUtils.readLines(new File(path), "utf-8");
//            int count = 0;
//            for (String line : lines) {
//                if (count++ > 0) {
//                    TestCase testCase = new TestCase();
//                    String[] line_array = line.split(";");
//                    testCase.setOrder(Integer.parseInt(line_array[0]));
//                    testCase.setIsRun(line_array[1]);
//                    testCase.setCaseName(line_array[2]);
//                    testCase.setUrl(line_array[4]);
//                    testCase.setType(line_array[3]);
//                    testCaseList.add(testCase);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return testCaseList;
//
//    }
//
//    //Excel实现方法
//    private static List<TestCase> getByExcel(String path){
//
//        List<TestCase> list = null;
//        try {
//            list = ExcelUtils.getInstance().readExcel2Objects(path, TestCase.class);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//}

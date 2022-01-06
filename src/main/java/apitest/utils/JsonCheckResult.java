package apitest.utils;

/**
 * @author lulu
 * @data 2022/1/6 10:58 上午
 *
 * 检查点枚举使用
 */
public enum JsonCheckResult {
    SUCCESS(true, "检查点检查成功"),
    FAIL(false, "检查点检查失败"),
    SKIP(false, "没有设置检查点"),
    ISVALID(true, "不是json格式数据"),
    EMPTY(false, "检查值提取为空，校验失败");



    private boolean result;
    private String msg;


    private JsonCheckResult(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

}

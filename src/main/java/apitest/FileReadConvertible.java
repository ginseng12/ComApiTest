package apitest;

import com.github.crab2died.converter.ReadConvertible;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;


/**
 * @author lulu
 * @data 2021/12/28 4:09 下午
 */
public class FileReadConvertible  implements ReadConvertible {
    @Override
    public Object execRead(String object) {
        //判空
        if(StringUtils.isNotBlank(object)&&StringUtils.endsWithAny(object, "csv","json","txt")) {
            String filepath = System.getProperty("user.dir")+File.separator+"data"+File.separator+object;
            try {
                String fileString= FileUtils.readFileToString(new File(filepath),"utf-8");
                return fileString;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return object;
    }
}

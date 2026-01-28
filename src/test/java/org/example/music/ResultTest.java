package org.example.music;

import org.example.music.common.Result;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResultTest {
    @Test
    public void testResult() {
        // 测试成功返回（带数据）
        Result<String> successResult = Result.success("测试数据");
        System.out.println(successResult);  // 输出：Result(code=200, msg=操作成功, data=测试数据)

        // 测试失败返回
        Result<Void> failResult = Result.fail(404, "资源不存在");
        System.out.println(failResult);  // 输出：Result(code=404, msg=资源不存在, data=null)
    }
}
package com.huayu.config;

import com.huayu.utils.JsonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 拦截器路径配置类
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    /**
     * json格式转化处理，防止雪花算法的id传到前端丢失精度
     *
     * @param converters 转换器对象
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将对象转为json
        messageConverter.setObjectMapper(new JsonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器中,放置前面优先使用
        converters.add(0, messageConverter);
    }
}

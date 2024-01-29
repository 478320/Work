# 基础镜像
FROM openjdk:17-buster
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 拷贝jar包
COPY todolist-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8081
# 入口
ENTRYPOINT ["java", "-jar", "/app.jar","--spring.profiles.active=pro"]
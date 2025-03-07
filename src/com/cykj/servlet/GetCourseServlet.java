package com.cykj.servlet;

import com.alibaba.fastjson2.JSON;
import com.cykj.annotation.Servlet;
import com.cykj.net.HttpRequest;
import com.cykj.net.HttpResponse;
import com.cykj.net.ResponseDto;
import com.cykj.service.CourseService;
import com.cykj.service.impl.CourseServiceImpl;

/**
 * Description:
 * 获取课程
 * @author Guguguy
 * @version 1.0
 * @since 2024/2/21 18:30
 */
@Servlet("/getCourse")
public class GetCourseServlet extends BasicServlet{
    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        int courseId = Integer.parseInt(request.getValue("id"));
        CourseService service = new CourseServiceImpl();
        ResponseDto dto = service.getCourse(courseId);
        response.write("text/html; charset:utf-8", JSON.toJSONBytes(dto));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        ResponseDto dto = new ResponseDto(0, "unavailable type!", null);
        response.write("text/html; charset:utf-8", JSON.toJSONBytes(dto));
    }
}

package com.cykj.service;

import com.cykj.net.ResponseDto;

/**
 * Description:
 * 课程service层接口
 * @author Guguguy
 * @version 1.0
 * @since 2024/2/21 17:30
 */
public interface CourseService {
    ResponseDto getRecommendCourse(String func, int num);
    ResponseDto getCourse(int id);
    ResponseDto search(String searchWord, int page, int limitNum, String sortMode, String[] types, String[] tags);
    ResponseDto getSearchNum(String searchWord, String[] types, String[] tags);
    ResponseDto getSearchResultTags(String searchWord);
}

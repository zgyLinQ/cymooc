package com.cykj.dao.Impl;

import com.cykj.annotation.DBTable;
import com.cykj.dao.BaseDao;
import com.cykj.dao.ICommentDao;
import com.cykj.pojo.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * 处理评论的数据库工具
 * @author Guguguy
 * @version 1.0
 * @since 2024/3/1 20:29
 */
public class CommentDao extends BaseDao implements ICommentDao {
    private static CommentDao commentDao;
    private final String tableName;
    private final Class<Comment> commentPojoClass;

    private CommentDao() {
        commentPojoClass = Comment.class;
        tableName = commentPojoClass.getAnnotation(DBTable.class).value();
    }
    @Override
    public List<Comment> getCourseComment(int courseId, int limitNum, int page) {
        int startNum = (page - 1) * limitNum;
        String sql = "select * from " + tableName + " where course_id = ? and state = 1 order by comment_time desc limit ?, ?";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        params.add(startNum);
        params.add(limitNum);
        List<Object> dataReturn = query(sql, params, commentPojoClass);
        List<Comment> commentList = new ArrayList<>();
        for (Object o : dataReturn) {
            commentList.add((Comment) o);
        }
        if (commentList.isEmpty()) {
            return null;
        } else {
            return commentList;
        }
    }

    @Override
    public int getCourseTotalCommentNum(int courseId) {
        String sql = "select * from " + tableName + " where course_id = ? and state = 1";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        return queryNum(sql, params);
    }

    @Override
    public boolean submitComment(int courseId, int uid, String comment) {
        String sql = "insert into " + tableName + " (course_id, uid, comment_body) values (?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        params.add(uid);
        params.add(comment);
        int code = update(sql, params);
        return code == 1;
    }

    @Override
    public boolean deleteComment(int courseId, int uid, int commentId) {
        String sql = "update " + tableName + " set state = 0 where course_id = ? and uid = ? and comment_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        params.add(uid);
        params.add(commentId);
        int code = update(sql, params);
        return code == 1;
    }

    public synchronized static CommentDao getInstance() {
        if (commentDao == null) {
            commentDao = new CommentDao();
        }
        return commentDao;
    }
}

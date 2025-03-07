package com.cykj.dao.Impl;

import com.cykj.annotation.DBTable;
import com.cykj.dao.BaseDao;
import com.cykj.dao.ICollectDao;
import com.cykj.dao.ICourseDao;
import com.cykj.pojo.Collection;
import com.cykj.pojo.Course;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 处理收藏相关数据库事宜的Dao
 * @author Guguguy
 * @version 1.0
 * @since 2024/2/26 20:31
 */
public class CollectDao extends BaseDao implements ICollectDao {
    private static CollectDao collectDao;
    private final String tableName;
    private final Class<Collection> collectionPojoClass;

    private CollectDao() {
        collectionPojoClass = Collection.class;
        tableName = collectionPojoClass.getAnnotation(DBTable.class).value();
    }
    @Override
    public int getCurrentCourseCollectState(int uid, int courseId) {
        String sql = "select * from " + tableName + " where uid = ? and course_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(uid);
        params.add(courseId);
        List<Object> dataResult = query(sql, params, collectionPojoClass);
        if (dataResult.isEmpty()) {
            sql = "insert into " + tableName + " (uid, course_id) values (?, ?)";
            update(sql, params);
            getCurrentCourseCollectState(uid, courseId);
        } else {
            Collection collection = (Collection) dataResult.get(0);
            if (collection.getState()) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean changeCurrentCourseCollectState(int uid, int courseId) {
        int state = getCurrentCourseCollectState(uid, courseId);
        String sql;
        List<Object> params = new ArrayList<>();
        if (state == 1) {
            sql = "update " + tableName + " set state = 0 where uid = ? and course_id = ?";
        } else {
            sql = "update " + tableName + " set state = 1 where uid = ? and course_id = ?";
        }
        params.add(uid);
        params.add(courseId);
        int changeNum = update(sql, params);
        return changeNum == 1;
    }

    @Override
    public int getCourseCollectNum(int courseId) {
        String sql = "select * from " + tableName + " where course_id = ? and state = 1";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        return queryNum(sql, params);
    }

    @Override
    public List<Course> getUserCollections(int uid) {
        ICourseDao courseDao = CourseDao.getInstance();
        String sql = "select * from " + tableName + " where uid = ? and state = 1";
        List<Object> params = new ArrayList<>();
        Map<Long, Integer> courseCollectionMap = new IdentityHashMap<>();
        params.add(uid);
        List<Object> dataReturn = query(sql, params, collectionPojoClass);
        for (Object o : dataReturn) {
            long courseId = ((Collection) o).getCourseId();
            int collectionNum = getCourseCollectNum((int) courseId);
            courseCollectionMap.put(courseId, collectionNum);
        }
        List<Map.Entry<Long, Integer>> list = new ArrayList<>(courseCollectionMap.entrySet());
        list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        List<Course> courseList = new ArrayList<>();
        for (Map.Entry<Long, Integer> longIntegerEntry : list) {
            courseList.add(courseDao.getCourse(Math.toIntExact(longIntegerEntry.getKey())));
        }
        return courseList;
    }

    public synchronized static CollectDao getInstance() {
        if (collectDao == null) {
            collectDao = new CollectDao();
        }
        return collectDao;
    }
}

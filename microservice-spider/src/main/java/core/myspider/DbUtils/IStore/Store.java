package core.myspider.DbUtils.IStore;

/**
 * desc: 数据存储接口
 * @Return:
 **/
public interface Store<T> {
    void insert(T entity);
}

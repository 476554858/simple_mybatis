package session;

import java.util.List;

public interface SqlSession {

    public <T> T selectOne(String statement,Object parameter);

    public <E> List<E> selectList(String statement, Object parameter);

    public <T> T getMapper(Class<T> type);
}

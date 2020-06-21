package excutor;

import config.MappedStatement;

import java.util.List;

public interface Executor {

    public <E> List<E> query(MappedStatement ms,Object parameter);
}

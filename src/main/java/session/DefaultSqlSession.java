package session;

import binding.MappedProxy;
import config.Configuration;
import config.MappedStatement;
import excutor.DefaultExecutor;
import excutor.Executor;

import java.lang.reflect.Proxy;
import java.util.List;

//1.对外提供服务，把请求转发给executor
//2.给mapper接口生成实现类
public class DefaultSqlSession implements SqlSession{

    private Configuration conf;

    private Executor executor;

    public DefaultSqlSession(Configuration conf) {
        this.conf = conf;
        executor = new DefaultExecutor(conf);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> selectList = this.selectList(statement,parameter);
        if(selectList == null || selectList.size() == 0){
            return null;
        }
        if(selectList.size()==1){
            return selectList.get(0);
        }
        throw new RuntimeException("too many result");
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        MappedStatement ms = conf.getMappedStatements().get(statement);
       return executor.query(ms,parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        MappedProxy mp = new MappedProxy(this);
        return (T) Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},mp);
    }
}

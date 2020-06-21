package excutor;

import com.sun.deploy.util.ReflectionUtil;
import config.Configuration;
import config.MappedStatement;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultExecutor implements Executor{

    private Configuration conf;

    public DefaultExecutor(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter) {
        List<E> ret =  new ArrayList<E>();//定义返回结果
        try {
            Class.forName(conf.getJdbcDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection =  null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(conf.getJdbcUrl(),conf.getJdbcUserName(),conf.getJdbcPassword());
            preparedStatement = connection.prepareStatement(ms.getSql());
            parameterize(preparedStatement,parameter);
            resultSet = preparedStatement.executeQuery();
            handlerResultSet(resultSet,ret,ms.getResultType());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    //对preparedStatement中的占位符进行处理
    private void parameterize(PreparedStatement preparedStatement, Object parameter) throws SQLException{
        if(parameter instanceof Integer){
            preparedStatement.setInt(1, (Integer) parameter);
        }else if(parameter instanceof Long){
            preparedStatement.setLong(1, (Long) parameter);
        }else if(parameter instanceof String){
            preparedStatement.setString(1, (String) parameter);
        }
    }

    //读取resultset中的数据，并转换成 目标对象
    private <E> void handlerResultSet(ResultSet resultSet, List<E> ret, String resultType) {
        Class<E> clazz = null;
        try {
            //通过反射获取类对象
            clazz = (Class<E>)Class.forName(resultType);
            while (resultSet.next()){
                Object obj = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                int len = fields.length;
                for(Field  field:fields){
                    Class type = field.getType();
                    String filedName = field.getName();
                    field.setAccessible(true);
                    if(type == String.class){
                        field.set(obj,resultSet.getString(filedName));
                    }else if(type == Integer.class){
                        field.set(obj,resultSet.getInt(filedName));
                    }else if(type ==  Long.class){
                        field.set(obj,resultSet.getLong(filedName));
                    }
                }
                ret.add((E)obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

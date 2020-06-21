import entity.User;
import mapper.TUserMapper;
import session.SqlSession;
import session.SqlSessionFactory;

public class TestMybatis {

    public static void main(String[] args) {
        SqlSessionFactory factory = new SqlSessionFactory();
        SqlSession session =  factory.openSession();
        System.out.println(session);
        //
        TUserMapper userMapper = session.getMapper(TUserMapper.class);
        User user = userMapper.selectByPrimaryKey(1);
        System.out.println(user.toString());
    }
}

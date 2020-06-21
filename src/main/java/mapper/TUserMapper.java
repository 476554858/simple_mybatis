package mapper;

import entity.User;

public interface TUserMapper {

    User selectByPrimaryKey(Integer id);
}

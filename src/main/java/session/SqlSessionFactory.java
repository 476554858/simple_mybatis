package session;

import config.Configuration;
import config.MappedStatement;
import excutor.Executor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

//1.把配置信息加载 到内存
//2.工厂类生产sqlsession
public class SqlSessionFactory {

    private Configuration conf  = new Configuration();

    public SqlSessionFactory() {
        loadDbInfo();
        loadMappersInfo();
    }

    //记录mapper xml文件 存放的位置
    public static final String MAPPER_CONFIG_LOCATION  = "mappers";
    //记录数据库 连接信息文件存放 位置
    public static final String DB_CONFIG_FILE = "db.properties";

    //加载数据库配置信息
    private void loadDbInfo(){
        InputStream dbIn= SqlSessionFactory.class.getClassLoader().getResourceAsStream(DB_CONFIG_FILE);
        Properties p = new Properties();
        try {
            p.load(dbIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf.setJdbcDriver(p.get("jdbc.driver").toString());
        conf.setJdbcUrl(p.get("jdbc.url").toString());
        conf.setJdbcUserName(p.get("jdbc.username").toString());
        conf.setJdbcPassword(p.get("jdbc.password").toString());
    }

    //加载指定文件夹下的所有mapper.xml
    private void loadMappersInfo() {
        URL resources = null;
        resources = SqlSessionFactory.class.getClassLoader().getResource(MAPPER_CONFIG_LOCATION);
        File mappers = new File(resources.getFile());//获取指定文件夹信息
        if(mappers.isDirectory()){
            File[] listFiles = mappers.listFiles();
            //遍历文件夹下所有的mapper.xml，解析信息后，注册至conf对象中
            for(File file:listFiles){
                loadMapperInfo(file);
            }
        }
    }

    //加载指定的mapper.xml文件
    private void loadMapperInfo(File file) {
        //创建saxReader对象
        SAXReader reader =  new SAXReader();
        //通过read方法读取一个文件 转换成document对象
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //获取根节点元素对象<mapper>
        Element root =   document.getRootElement();
        //获取命名空间
        String namespace = root.attributeValue("namespace");
        //获取select子节点列表
        List<Element> selects = root.elements("select");
        //遍历select节点，将信息记录到mapperStatement对象，并登记到configuration对象中
        for (Element element:selects){
            MappedStatement mappedStatement = new MappedStatement();
            String id  = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String sql = element.getData().toString();
            String sourceId = namespace + "." + id;
            //给mappedStatement属性赋值
            mappedStatement.setSourceId(sourceId);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sql);
            mappedStatement.setNamespace(namespace);
            conf.getMappedStatements().put(sourceId,mappedStatement);
        }
    }

    public SqlSession openSession(){
        return new DefaultSqlSession(conf);
    }
}

package vip.bzsy.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author lyf
 * @create 2019-03-22 17:05
 */
public class MysqlGenerator {

    public static void main(String[] args){
        String path1="D:\\java\\IdeaSpace\\lyq3000\\src\\main\\java";
        //myGenerator(path1,"lyq_date");
    }

    /**
     *  mp逆向工程
     * @param path 模板路径
     * @param table 表名
     */
    private static void myGenerator(String path,String table){
        // 1.全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setActiveRecord(true)//是否支持AR模式
                .setAuthor("lyf")//设置作者
                .setOutputDir(path)//生成路径
                .setFileOverride(true)  //文件覆盖
                .setIdType(IdType.AUTO) //逐渐策略
                .setServiceName("%sService") //设置生成service首字母是否为I
                .setBaseResultMap(true)   //结果集映射
                .setBaseColumnList(true);  //生成sql片段

        // 2.数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setUrl("jdbc:mysql://localhost:3306/lyq3000?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");

        // 3.策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setCapitalMode(true); //全局大小写命名
        strategy.setNaming(NamingStrategy.underline_to_camel);//驼峰命名
        //strategy.setTablePrefix("");  表明前缀
        strategy.setInclude(table);//将来要生成的表

//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
        strategy.setEntityLombokModel(true);
//        strategy.setRestControllerStyle(true);
//        strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
//        strategy.setSuperEntityColumns("id");
//        strategy.setControllerMappingHyphenStyle(true);


        // 4.包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("vip.bzsy")
                .setMapper("mapper")
                .setController("controller")
                .setEntity("model")
                .setService("service")
                .setServiceImpl("service.impl")
                .setXml("mapper");

        // 5.代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(gc);
        autoGenerator.setDataSource(dsc);
        autoGenerator.setStrategy(strategy);
        autoGenerator.setPackageInfo(pc);

        autoGenerator.execute();
    }


}

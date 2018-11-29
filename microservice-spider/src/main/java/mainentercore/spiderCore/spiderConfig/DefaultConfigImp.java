package mainentercore.spiderCore.spiderConfig;

public class DefaultConfigImp extends CustomConfigImp {
    /**
     * desc:获取默认配置
     * @Return: Configuration
     **/
    public DefaultConfigImp(){
        setConfig(Configuration.getDefault());
    }
}

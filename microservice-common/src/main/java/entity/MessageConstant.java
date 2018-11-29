package entity;

/**
 * @author 一杯咖啡
 * @desc 消息状态
 * @createTime 2018-11-29-14:52
 */
public enum  MessageConstant {
    START("true",1),STOP("false",2),WAIT("pause",3);

    private String name ;
    private int index;
    MessageConstant(String name,int index) {
    this.name = name;
    this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "MessageConstant{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
}

package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class ResDto {

    private Integer code;
    private Object data;
    private String msg;

    public ResDto(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static ResDto ok(Object data, String msg){
        return new ResDto(200, data, msg);
    }
    public static ResDto ok(){
        return new ResDto(200, null, null);
    }
    public static ResDto err(String msg){
        return new ResDto(500, null, msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

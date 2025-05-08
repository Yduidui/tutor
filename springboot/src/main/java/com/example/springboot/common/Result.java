package com.example.springboot.common;


//Result 类封装了统一的响应结构。所有的后端 API 都统一返回 Result 对象，这样前端能够根据不同的 code 来进行后续处理。
public class Result {
    private String code;
    private String msg;
    private Object data;


    //创建一个表示成功的响应对象。
    //返回值：返回 Result 对象，状态码设为 "200"，消息设为 "success"，而数据 (data) 设置为 null。
    //用途：可以在操作成功时调用，不需要返回额外的数据。
    public static Result success(){
        Result result = new Result();
        result.setCode("200");
        result.setMsg("success");
        return result;
    }


//    创建一个表示成功的响应对象，并且携带返回的数据。
//    参数：接收一个 Object 类型的参数 data，通常是需要返回给前端的具体数据内容。
//    返回值：返回一个 Result 对象，包含 "200" 状态码，消息为 "success"，并将数据字段 (data) 设置为传入的参数 data。
//    用途：用于操作成功但需要带有返回数据时，例如查询返回用户信息。
    public static Result success(Object data){
        Result result = success();
        result.setData(data);
        return result;
    }


    //创建一个表示错误的响应对象。
    //返回值：返回 Result 对象，状态码设为 "404"，消息为 "error"，而数据 (data) 设置为 null。
    //用途：可以在操作失败时调用，比如资源未找到或其他通用错误场景。
    public static Result error(){
        Result result = new Result();
        result.setCode("404");
        result.setMsg("error");
        return result;
    }


//    创建一个自定义的错误响应对象，允许设置特定的状态码和错误消息。
//    参数：
//    code：自定义的错误状态码，可以是任何字符串。
//    msg：自定义的错误消息，描述错误原因。
//    返回值：返回一个 Result 对象，包含传入的状态码和消息，而数据字段 (data) 设置为 null。
//    用途：适用于需要提供具体错误信息的场景，例如用户输入验证错误或服务端异常。
    public static Result error(String code, String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

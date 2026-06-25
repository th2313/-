package com.example.keshe.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("t_user")
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("uname")
    private String uname;

    @TableField("upassword")
    private String upassword;

    @TableField("utype")
    private String utype;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    @Override
    public String toString() {
        return "TUser{" +
                "uname='" + uname + '\'' +
                ", upassword='" + upassword + '\'' +
                ", utype='" + utype + '\'' +
                '}';
    }
}
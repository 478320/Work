package com.huayu.work3.pojo;

import java.math.BigDecimal;

/**
 *
 */
public class TbGoodsDO {
    private Integer id;
    private String name;
    private BigDecimal price;

    @Override
    public String toString() {
        return "TbGoodsDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if(id>0){
            this.id = id;
        }else {
            System.out.println("您输入了非法的id");
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!"".equals(name)){
            this.name = name;
        }else {
            System.out.println("非法的商品名查找");
        }

    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.doubleValue()>0){
            this.price = price;
        }else {
            System.out.println("商品价格是不是输入错误了亲");
        }

    }
}

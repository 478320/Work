package com.huayu.work3.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 */
public class TbOrderDO {
    private Integer id;
    private Timestamp time;
    private BigDecimal price;

    @Override
    public String toString() {
        return "TbOrderDO{" +
                "id=" + id +
                ", time=" + time +
                ", price=" + price +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if(id>0) {
            this.id = id;
        }else {
            System.out.println("非法的订单编号");
        }
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

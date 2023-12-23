package com.huayu.work3.pojo;

/**
 *
 */
public class TbOrderGoodsDO {
    private Integer orderId;
    private Integer goodsId;
    private Integer quantity;

    @Override
    public String toString() {
        return "TbOrderGoodsDO{" +
                "orderId=" + orderId +
                ", goodsId=" + goodsId +
                ", quantity=" + quantity +
                '}';
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        if (orderId>0) {
            this.orderId = orderId;
        }else {
            System.out.println("非法的订单编号");
        }
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        if (goodsId>0) {
            this.goodsId = goodsId;
        }else {
            System.out.println("非法的商品编号");
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity>0) {
            this.quantity = quantity;
        }else {
            System.out.println("请至少购买一件商品");
        }

    }
}

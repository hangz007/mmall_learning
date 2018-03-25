package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by shenzx on 2018/3/20.
 */
public class CartVO {

   private List<CartProductVO> list ;
   private BigDecimal cartTotalPrice;
   private Boolean allChecked; // 是否已经都勾选
   private String imageHost;

    public List<CartProductVO> getList() {
        return list;
    }

    public void setList(List<CartProductVO> list) {
        this.list = list;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

}

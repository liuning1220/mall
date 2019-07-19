package com.hengxunda.springcloud.inventory.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryDTO implements Serializable {

    private static final long serialVersionUID = 8229355519336565493L;

    @ApiModelProperty(name = "productId", notes = "商品id", example = "1", required = true)
    private String productId;

    @ApiModelProperty(name = "count", notes = "购买数量", example = "10", required = true)
    private Integer count;
}

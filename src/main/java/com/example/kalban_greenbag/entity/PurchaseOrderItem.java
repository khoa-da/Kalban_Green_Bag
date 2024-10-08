package com.example.kalban_greenbag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "PurchaseOrderItems")
public class PurchaseOrderItem extends BaseEntity{


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PurchaseOrderID")
    private PurchaseOrder purchaseOrderID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaterialID")
    private Material materialID;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "UnitPrice", precision = 10, scale = 2)
    private BigDecimal unitPrice;

}
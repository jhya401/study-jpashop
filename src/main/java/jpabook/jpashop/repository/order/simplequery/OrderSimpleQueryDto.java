package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String memberName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; // 이건 value객체임. 이건 객체로 내려보내도 됨. (값타입 참고)

    public OrderSimpleQueryDto(Order order) {
        this.orderId = order.getId();
        this.memberName = order.getMember().getName(); // LAZY 초기화 시점
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress(); // LAZY 초기화 시점
    }
}
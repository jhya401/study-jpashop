package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.dto.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.type.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/v1")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        all.forEach(order -> {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(orderItem -> orderItem.getItem().getName());
        });
        return all;
    }

    @GetMapping("/v2")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /**
     * 컬렉션매칭 + 페치조인
     */
    @GetMapping("/v3")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * 컬렉션 매칭 + 페치조인 + 페이징
     */
    @GetMapping("/v3.1")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /**
     * 컬렉션매칭 + DTO => N+1
     * 단건 조회인 경우에는 이 방식을 쓰는게 좋다
     */
    @GetMapping("/v4")
    public List<OrderQueryDto> ordersV4() {
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findOrderQueryDtos();
        return orderQueryDtos;
    }

    /**
     * 컬렉션매칭 + DTO + 최적화(쿼리2번)
     */
    @GetMapping("/v5")
    public List<OrderQueryDto> ordersV5() {
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findAll_optimization();
        return orderQueryDtos;
    }

    /**
     * 컬렉션매칭 + DTO + 최적화(쿼리1번)
     * 개발자가 지지고 볶고 하는 거.. 그냥 쿼리 2번 돌리는게 편하겠다.
     * 단점. 주문을 기준으로 페이징 불가능하다(주문아이템이나 아이템 같은거로 되겠지만.)
     *      v5보다 느릴 수도 있다.
     */
    @GetMapping("/v6")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAll_flat();

        // 중복제거
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())))
                .entrySet()
                .stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            orderId = o.getId();
            name = o.getMember().getName(); // 엔티티 초기화
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress(); // 엔티티 초기화
            orderItems = o.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문가격
        private int count;  // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}

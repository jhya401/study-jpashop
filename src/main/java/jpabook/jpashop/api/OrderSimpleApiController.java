package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.dto.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne
 * Order
 * Order -> Member   (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * 엔티티로 반환
     * 절대 이렇게 쓰지 마라.
     */
    @GetMapping("/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        // LAZY로딩 대상들을 수동으로 로딩한 것임.(데이터를 프론트에 넘겨줌)
        all.forEach(order -> {
            order.getMember().getName();
            order.getDelivery().getAddress();
        });
        return all;
    }

    @GetMapping("/v2/simple-orders")
    public List<OrderSimpleQueryDto> orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        // LAZY로딩 대상들을 수동으로 로딩한 것임.(데이터를 프론트에 넘겨줌)
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(OrderSimpleQueryDto::new).collect(Collectors.toList());
        return result;
    }

    /**
     * 패치조인 + 엔티티를 DTO로 변환하는 조회방법
     */
    @GetMapping("/v3/simple-orders")
    public List<OrderSimpleQueryDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        // LAZY로딩 대상들을 수동으로 로딩한 것임.(데이터를 프론트에 넘겨줌)
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(OrderSimpleQueryDto::new).collect(Collectors.toList());
        return result;
    }

    /**
     * DTO로 직접 조회하는 리파지토리
     */
    @GetMapping("/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}

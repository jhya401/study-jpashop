package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 특정 비지니스를 위한 쿼리용
 */
@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * 결과적으로는 N+1임
     * 단건 조회인 경우에는 이 방식을 쓰는게 좋다
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // query 1번 -> N개
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // N번 쿼리
            o.setOrderItemQueryDtos(orderItems);
        });
        return result;
    }

    /**
     * 최적화 : 쿼리는 2번만 돌고, 메모리에서 조립함
     */
    public List<OrderQueryDto> findAll_optimization() {
        List<OrderQueryDto> result = findOrders();

        // 주문ID 리스트를 추출한다.
        List<Long> orderIds = result.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());

        // In 쿼리를 만들어서 주문ID리스트를 조건에 넣는다.
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 주문아이템들을 주문ID로 그룹핑 해준다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        // 결과 데이터 반복문을 돌면서 컬렉션에 주문아이템 데이터를 넣어준다.
        result.forEach(o -> o.setOrderItemQueryDtos(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAll_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}

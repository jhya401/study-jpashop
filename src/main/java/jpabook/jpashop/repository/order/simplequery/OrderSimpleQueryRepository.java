package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 특수한 조회 쿼리 모아놓는 리파지토리를 따로 만든다.
 */
@RequiredArgsConstructor
@Repository
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    /**
     * fetch join 보다 네트웤 메모리 사이즈에 유리함. 근데 거의 그럴 일은 없음
     * 근데!! 완전 데이터가 많은 경우에는 필요할 수도 있음.
     * 즉, 상황에 맞을 때 쓰자
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}

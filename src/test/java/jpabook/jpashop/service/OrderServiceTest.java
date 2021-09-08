package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.type.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;


    @DisplayName("상품주문")
    @Test
    void order() {
        //given
        Member member = creatMember();

        Book book = creatBook("BooK JPA", 10000, 10);

        int orderCnt = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCnt);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "상품 종류 수가 정확해야 한다.");
        assertEquals(10000 * orderCnt, getOrder.getTotalPrice(), "주문가격은 가격 * 수량 이여야 한다.");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }

    @DisplayName("상품주문 재고수량 초과")
    @Test
    void overStock() {
        //given
        Member member = creatMember();
        Book book = creatBook("시골 JPA", 10000, 10);

        int orderCnt = 11;

        //when
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), orderCnt);
        }, "재고 수량 부족 예외가 발생해야 한다.");
    }

    @DisplayName("주문 취소")
    @Test
    void cancel() {
        //given
        Member member = creatMember();
        Book book = creatBook("시골 book", 10000, 10);

        int orderCnt = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCnt);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order canceldOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, canceldOrder.getStatus(), "주문 취소시 상태는 CANCEL이다");
        assertEquals(10, book.getStockQuantity(), "취소 후 재고 수량이 복구되어야 한다.");
    }

    private Book creatBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

    private Member creatMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}
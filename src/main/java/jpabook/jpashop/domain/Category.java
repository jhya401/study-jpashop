package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(// 다대다는 조인테이블 필수!(관계형db에서 매핑테이블임) //실무에서는 거의 못 씀. 필드 추가 못 함.
            name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    // 계층구조 구현하기 = 자체조인!!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 나의 부모

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>(); // 나의 자식들

    //===연관관계메서드===//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

}

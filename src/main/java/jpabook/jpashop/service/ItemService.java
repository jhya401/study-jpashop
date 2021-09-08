package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.BookForm;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.service.dto.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

    @Transactional
    public void updateItem(Long itemId, UpdateItemDto dto){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(dto.getName());
        findItem.setPrice(dto.getPrice());
        findItem.setStockQuantity(dto.getStockQuantity());
    }
}

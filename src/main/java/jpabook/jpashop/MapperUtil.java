package jpabook.jpashop;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
public final class MapperUtil {
    public static <T, S> T toObject(S source, Class<T> targetClass, String... ignoreProperties) {
        T target = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }
}

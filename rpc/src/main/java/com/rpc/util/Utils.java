package com.rpc.util;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public abstract class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);


    public static <T> List<T> removeDupliObject(List<T> list , Comparator<T> comparator) {
        Set<T> set = new TreeSet<T>(comparator);
        set.addAll(list);

        return new ArrayList<T>(set);
    }

    private Utils() {
    }

    public static int[] share(int num, int parts) {
        if (num <= 0 || parts <= 0) {
            throw new IllegalArgumentException("Argument 'num' and 'parts' must be great than 0");
        }
        int[] ret = new int[parts];
        int quotient = num / parts;
        int remainder = num % parts;
        for (int i = 0; i < parts; i++) {
            ret[i] = quotient;
        }
        for (int i = 0; i < remainder; i++) {
            ret[i] += 1;
        }
        return ret;
    }

    public static <T> List<List<T>> share(List<T> list, int parts) {
        List<List<T>> ret = new ArrayList<List<T>>();
        if (CollectionUtils.isNotEmpty(list)) {
            int[] divides = share(list.size(), parts);
            int index = 0;
            for (int i = 0; i < parts; i++) {
                int divide = divides[i];
                int from = index;
                int to = from + divide;
                index = to;
                ret.add(list.subList(from, to));
            }
        }
        return ret;
    }

    public static <K, V> Map<K, V> asMap(K[] keys, V[] values) {
        Map<K, V> map = new HashMap<K, V>();
        if (ArrayUtils.isNotEmpty(keys)) {
            for (int i = 0, len = keys.length; i < len; i++) {
                if (null == values || i >= values.length) {
                    map.put(keys[i], null);
                } else {
                    map.put(keys[i], values[i]);
                }
            }
        }
        return map;
    }

    public static <E> List<E> asList(E... elems) {
        List<E> list = new ArrayList<E>();
        if (ArrayUtils.isNotEmpty(elems)) {
            for (E e : elems) {
                list.add(e);
            }
        }
        return list;
    }


    public static <E> Set<E> asSet(E... elems) {
        Set<E> set = new HashSet<E>();
        if (ArrayUtils.isNotEmpty(elems)) {
            for (E e : elems) {
                set.add(e);
            }
        }
        return set;
    }

    public static <K, V> Map<K, List<V>> list2MapList(List<V> list, KeyGenerator<V> keyGenerator) {

        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<K, List<V>>(0);
        } else {
            Map<K, List<V>> resultMap = new HashMap<K, List<V>>();
            try {
                for (V value : list) {
                    K key = keyGenerator.generate(value);
                    List<V> li = resultMap.get(key);
                    if (null == li) {
                        li = new ArrayList<V>();
                    }
                    li.add(value);
                    resultMap.put(key, li);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return resultMap;
        }
    }

    public static <V, K> List<K> list2List(List<V> list, FieldGenerator<V> generator) {

        if (CollectionUtils.isEmpty(list)) {

            return new ArrayList<K>(0);
        } else {

            List<K> result = new ArrayList<K>();

            try {
                for (V value : list) {

                    K k = generator.generate(value);
                    if (k != null) {
                        result.add(k);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return result;
        }
    }

    public static <V, K> Set<K> list2Set(List<V> list, FieldGenerator<V> generator) {

        if (CollectionUtils.isEmpty(list)) {

            return new HashSet<K>(0);
        } else {

            Set<K> result = new HashSet<K>();

            try {
                for (V value : list) {

                    K k = generator.generate(value);
                    if (k != null) {
                        result.add(k);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return result;
        }
    }


    public static <V, K , T> List<  Map<K , T > > list2ListMap(List<V> list, MapGenerator<V> generator) {

        if (CollectionUtils.isEmpty(list)) {

            return new ArrayList<  Map<K , T > >(0);
        } else {

            List< Map<K , T > > result = new ArrayList< Map<K , T > >();

            try {
                for (V value : list) {

                    Map<K , T > tempMap = generator.generate(value);
                    result.add(tempMap);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return result;
        }
    }

    public static <V, K> Set<V> list2Set(List<V> list, KeyGenerator<V> generator) {

        if (CollectionUtils.isEmpty(list)) {

            return new HashSet<V>(0);
        } else {

            Map<K, V> map = new HashMap<K, V>();

            try {
                for (V value : list) {

                    K k = generator.generate(value);
                    if (k != null) {
                        map.put(k, value);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return new HashSet<V>(map.values());
        }
    }

    public static abstract class FieldGenerator<Object> {
        public abstract <Field> Field generate(Object object);
    }


    public static abstract class MapGenerator<Object> {
        public abstract <K , V > Map<K, V> generate(Object object);
    }

    public static <K, V> Map<K, V> list2Map(List<V> list, KeyGenerator<V> keyGenerator) {

        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<K, V>(0);
        } else {

            Map<K, V> resultMap = new HashMap<K, V>();

            try {

                for (V value : list) {

                    K key = keyGenerator.generate(value);
                    if (!resultMap.containsKey(key)) {
                        resultMap.put(key, value);
                    } else {
                        logger.error("key=[{}]的元素已经存在", key);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return resultMap;
        }
    }

    public static <K, V> void put(Map<K, List<V>> map, K k, V v) {

        if (map == null) {
            map = new HashMap<K, List<V>>();
        }

        List<V> list = map.get(k);
        if (null == list) {
            list = new ArrayList<V>();
            map.put(k, list);
        }
        list.add(v);
    }

    public static <K1, V1, K2, V2> Map<K2, V2> map2Map(Map<K1, V1> originalMap, KeyTransformer<K1> keyTransformer, ValueTransformer valueTransformer) {

        Map<K2, V2> ret = new HashMap<K2, V2>();
        if (MapUtils.isNotEmpty(originalMap)) {

            Iterator<K1> iterator = originalMap.keySet().iterator();
            while (iterator.hasNext()) {
                K1 k1 = iterator.next();
                V1 v1 = originalMap.get(k1);
                K2 k2 = keyTransformer.transform(k1);
                V2 v2 = valueTransformer.transform(v1);
                ret.put(k2, v2);
            }
        }

        return ret;
    }

    public static <K1, V1, V2> Map<K1, V2> map2Map(Map<K1, V1> originalMap, ValueTransformer valueTransformer) {

        Map<K1, V2> ret = new HashMap<K1, V2>();
        if (MapUtils.isNotEmpty(originalMap)) {

            Iterator<K1> iterator = originalMap.keySet().iterator();
            while (iterator.hasNext()) {
                K1 k1 = iterator.next();
                Object v1 = originalMap.get(k1);
                V2 v2 = valueTransformer.transform(v1);
                ret.put(k1, v2);
            }
        }

        return ret;
    }

    public static byte[] md5Bytes(String text) {
        MessageDigest msgDigest;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes());

        byte[] bytes = msgDigest.digest();

        return bytes;
    }

    /**
     * @param min
     * @param max
     * @return [min, max)
     */
    public static int randomInt(int min, int max) {
        min = Math.min(min, max);
        max = Math.max(min, max);
        int temp = (int) (Math.random() * (max - min) + min);
        return temp;
    }

    public static <T> T randomElem(T[] array) {
        if (ArrayUtils.isNotEmpty(array)) {
            return array[randomInt(0, array.length)];
        }
        return null;
    }

    public static <T> T randomElem(List<T> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(randomInt(0, list.size()));
        }
        return null;
    }

    public static boolean randomBool() {
        return randomElem(new Boolean[]{true, false});
    }

    public static <X, T> Map<X, List<T>> group(List<T> list, Grouper<T> grouper) {

        Map<X, List<T>> ret = new HashMap<X, List<T>>();

        if (CollectionUtils.isNotEmpty(list)) {

            for (int i = 0, len = list.size(); i < len; i++) {

                T elem = list.get(i);
                X type = grouper.group(elem);
                List<T> groups = ret.get(type);
                if (null == groups) {
                    groups = new ArrayList<T>();
                }

                groups.add(elem);
                ret.put(type, groups);
            }
        }

        return ret;
    }

    public static abstract class Grouper<T> {
        public abstract <X> X group(T t);
    }

    public static abstract class KeyGenerator<V> {
        public abstract <X> X generate(V v);
    }

    public static abstract class KeyTransformer<V> {
        public abstract <X> X transform(V v);
    }

    public static abstract class ValueTransformer {
        public abstract <X> X transform(Object v);
    }

    public static abstract class Filter<T> {
        public abstract boolean filter(T t);
    }

    public static int[] divide(int allocatedFrom, int allocateTo) {
        if (allocatedFrom <= 0 || allocateTo <= 0) {
            throw new IllegalArgumentException("argument 'allocatedFrom' and 'allocateTo' must be great than 0");
        }
        int[] ret = new int[allocateTo];
        int quotient = allocatedFrom / allocateTo;
        int remainder = allocatedFrom % allocateTo;
        for (int i = 0; i < allocateTo; i++) {
            ret[i] = quotient;
        }
        for (int i = 0; i < remainder; i++) {
            ret[i] += 1;
        }
        return ret;
    }

    public static <T> List<List<T>> divide(List<T> allocatedFrom, int allocateTo) {
        List<List<T>> ret = new ArrayList<List<T>>();
        if (CollectionUtils.isNotEmpty(allocatedFrom)) {
            int[] divides = divide(allocatedFrom.size(), allocateTo);
            int index = 0;
            for (int i = 0; i < allocateTo; i++) {
                int divide = divides[i];
                int from = index;
                int to = from + divide;
                index = to;
                ret.add(allocatedFrom.subList(from, to));
            }
        }
        return ret;
    }



    public static <T> void multiplySort(List<T> list, final List<? extends Comparator<T>> comparators) {

        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(comparators)) {
            return;
        }

        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {

                for (Comparator _comparator : comparators) {

                    if (_comparator.compare(o1, o2) > 0) {
                        return 1;
                    } else if (_comparator.compare(o1, o2) < 0) {
                        return -1;
                    }
                }

                return 0;
            }
        };

        Collections.sort(list, comparator);
    }

    public static <T extends Number> Double mean(Collection<T> numbers) {
        Double ret = new Double(0D);
        if (CollectionUtils.isNotEmpty(numbers)) {

            for (T number : numbers) {
                ret += number.doubleValue();
            }
            ret = ret / numbers.size();
        }
        return ret;
    }

    public static <T extends Number> Double sum(Collection<T> numbers) {
        Double ret = new Double(0D);
        if (CollectionUtils.isNotEmpty(numbers)) {

            for (T number : numbers) {
                ret += number.doubleValue();
            }
        }
        return ret;
    }

    public  static <K, V , T> List< Map<K, T> > mapToListEntry(Map<K, V> map, ValueTransformer valueTransformer){
        List<Map<K , T>> backList = new ArrayList<Map<K, T>>();
        if (map == null) {
            return null;
        }
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Map<K , T> tempMap;
        for (Map.Entry<K, V> entry : list){
            tempMap =new HashMap<K, T>();
            tempMap.put( entry.getKey()  , (T) valueTransformer.transform( entry.getValue() ));
            backList.add( tempMap );
        }
        return backList;
    }


    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }

            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }

        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    /**
     * 将String 类型转为 integer  Long  等等类型 以 Object 类型的返回
     *
     * @param c
     * @param value
     * @return
     */
    public static Object parseStringToMathType(Class c, String value) {
        Object backValue = null;
        String cName = c.getName();
        if ("long".equals(cName)) {
            backValue = Long.parseLong(value);
        } else if ("java.lang.Long".equals(cName)) {
            backValue = Long.parseLong(value);
        } else if ("int".equals(cName)) {
            backValue = Integer.parseInt(value);
        } else if ("java.lang.Integer".equals(cName)) {
            backValue = Integer.parseInt(value);
        } else if ("float".equals(cName)) {
            backValue = Float.parseFloat(value);
        } else if ("java.lang.Float".equals(cName)) {
            backValue = Float.parseFloat(value);
        } else if ("double".equals(cName)) {
            backValue = Double.parseDouble(value);
        } else if ("java.lang.Double".equals(cName)) {
            backValue = Double.parseDouble(value);
        } else if ("java.lang.String".equals(cName)) {
            backValue = value;
        }

        return backValue;
    }
    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static <T ,V > Map<T, V> sortMapByKey(Map<T, V> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<T , V> sortMap = Maps.newTreeMap(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return  o1.toString().compareTo(o2.toString());
            }

        });

        sortMap .putAll( map );

        return sortMap;
    }
    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static <T ,V > Map<T, V> sortMapByKey(Map<T, V> map , Comparator<T> comparator) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<T , V> sortMap = Maps.newTreeMap( comparator );

        sortMap .putAll( map );

        return sortMap;
    }

    /**
     * 使用 Map按value进行排序
     * @param map
     * @return
     */
    public static <K, V > Map<K, V> sortByValue(Map<K, V> map , Comparator<Map.Entry<K, V>> comparator ) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, comparator );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

}
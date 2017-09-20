package com.rpc.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * @param source          源对象
     * @param target          目标对象
     * @param ignoreNullValue 是否忽略null值
     * @throws BeansException
     * @throws org.springframework.beans.BeansException
     */
    public static void copyProperties(Object source, Object target, Boolean ignoreNullValue) throws BeansException {
        copyProperties(source, target, null, ignoreNullValue, (String[]) null);
    }

    public static void copyProperties(Object source, Object target) throws BeansException {
        copyProperties(source, target, null, false, (String[]) null);
    }

    public static void copyProperties(Object source, Object target, Boolean ignoreNullValue, String... properties) throws BeansException {
        copyProperties(source, target, null, ignoreNullValue, (String[]) null);
    }

    private static void copyProperties(Object source, Object target, Class<?> editable, Boolean ignoreNullValue, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (ignoreNullValue && null == value) {
                                continue;
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    } else if (readMethod != null && writeMethod.getParameterTypes()[0] != null && writeMethod.getParameterTypes()[0].isEnum()) {
                        try {
                            Method method = actualEditable.getMethod(writeMethod.getName(), readMethod.getReturnType());
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                                method.setAccessible(true);
                            }
                            if (ignoreNullValue && null == value) {
                                continue;
                            }
                            if (method != null && ClassUtils.isAssignable(method.getParameterTypes()[0], readMethod.getReturnType())) {
                                method.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

}

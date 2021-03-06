package com.multi.maven.dao.mysql.helper;

import com.multi.maven.annotations.TableSearchColumn;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.util.StringUtil;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableSearchColumnHelper {

    private final static Map<Class<?>, List<TableSearchColumnEntity>> searchColumnMap = new HashMap<>();
    public static List<TableSearchColumnEntity> getTableSearchColumns(Class<?> entityClass) {

        if(!searchColumnMap.containsKey(entityClass)) {

            synchronized (searchColumnMap) {

                if(!searchColumnMap.containsKey(entityClass)) {

                    searchColumnMap.put(entityClass, new ArrayList<TableSearchColumnEntity>());

                    List<Field> fieldList = getAllField(entityClass, null);

                    for (Field field : fieldList) {
                        String columnName;
                        if (field.isAnnotationPresent(TableSearchColumn.class)) {
                            TableSearchColumn joinColumn = field.getAnnotation(TableSearchColumn.class);
                            columnName = joinColumn.column();
                            if (StringUtils.isEmpty(columnName)||StringUtils.isEmpty(columnName.trim())) {
                            	columnName = StringUtil.camelhumpToUnderline(field.getName());
                            }
                            TableSearchColumnEntity column = new TableSearchColumnEntity();
                            column.setColumn(columnName);
                            if(!StringUtils.isEmpty(joinColumn.joinTable())) {
                                column.setJoinTable(joinColumn.joinTable());
                                column.setJoinId(StringUtil.camelhumpToUnderline(field.getName()));
                            }
                            searchColumnMap.get(entityClass).add(column);
                        }

                    }
                }
            }
        }

        return searchColumnMap.get(entityClass);
    }

    /**
     *
     * @param entityClass
     * @param fieldList
     * @return
     */
    private static List<Field> getAllField(Class<?> entityClass, List<Field> fieldList) {
        if (fieldList == null) {
            fieldList = new LinkedList<Field>();
        }
        if (entityClass.equals(Object.class)) {
            return fieldList;
        }
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldList.add(field);
            }
        }
        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && (superClass.isAnnotationPresent(Entity.class)
                || (!Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)))) {
            return getAllField(entityClass.getSuperclass(), fieldList);
        }
        return fieldList;
    }

    @Setter
    @Getter
    public static class TableSearchColumnEntity {

        private String column;

        private String joinTable;

        private String prefix;

        private String joinId;

    }

}

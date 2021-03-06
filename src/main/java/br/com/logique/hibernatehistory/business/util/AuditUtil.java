package br.com.logique.hibernatehistory.business.util;

import br.com.logique.hibernatehistory.annotation.Author;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author victor.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuditUtil {

    public static String findAuthorAttribute() {
        return ReflectionUtil.REFLECTIONS.getTypesAnnotatedWith(RevisionEntity.class).stream()
                .map(c -> ReflectionUtil.getAllFieldsAnnotedBy(c, Author.class).stream().findFirst().get().getName())
                .findFirst().orElse(null);
    }

    public static String getAuthorValue(Object obj) {
        String authorValue = "";
        if (obj != null) {
            authorValue = ReflectionUtil.REFLECTIONS.getTypesAnnotatedWith(RevisionEntity.class).stream().map(c ->
                    ReflectionUtil.getAllFieldsAnnotedBy(c, Author.class).stream()
                        .map(f -> {
                            String attributeName = f.getAnnotation(Author.class).attributeName();
                            if ("".equals(attributeName)) {
                                return Objects.toString(obj.toString());
                            } else {
                                return Objects.toString(ReflectionUtil.getFieldValueByName(attributeName, obj));
                            }
                        }).findFirst().orElse("")).findFirst().orElse("");
        }
        return authorValue;
    }

    public static Long findRecordId(Object obj) {
        return ReflectionUtil.getAllFieldsAnnotedBy(obj.getClass(), Id.class).stream()
                .map(field -> (Long) ReflectionUtil.getFieldValue(field, obj)).findFirst().orElse(null);
    }

    public static void incrementVersionAttributeIfPresent(Object entidade, EntityManager entityManager, Long id) {
        Set<Class<?>> allClasses = ReflectionUtil.REFLECTIONS.getTypesAnnotatedWith(Audited.class);
        allClasses.forEach(aClass -> {

            if (aClass.getName().equals(entidade.getClass().getName())) {

                List<Field> fields = ReflectionUtil.getAllFieldsAnnotedBy(entidade.getClass(), Version.class);

                if (fields != null && fields.size() > 0) {

                    Object obj = entityManager.find(entidade.getClass(), id);
                    fields.forEach(field -> {
                        long versao = (long) ReflectionUtil.getFieldValue(field, obj);
                        ReflectionUtil.setFieldValue(field, entidade, versao);
                    });

                }
            }
        });
    }

}

package play.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import javax.inject.Inject;
import play.Play;
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport;
import play.jobs.Job;
import play.mvc.Mailer;

public class Injector {
    
    /**
     * For now, inject beans in controllers
     */
    public static void inject(BeanSource source) {
        List<Class> classes = Play.classloader.getAssignableClasses(ControllerSupport.class);
        classes.addAll(Play.classloader.getAssignableClasses(Mailer.class));
        classes.addAll(Play.classloader.getAssignableClasses(Job.class));
        for(Class<?> clazz : classes) {
            for(Field field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(Inject.class)) {
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    try {
                        final Object beanOfType = source.getBeanOfType(type);
                        if (field.get(null) == null && beanOfType != null) {
                            field.set(null, beanOfType);
                        }
                    } catch(RuntimeException e) {
                        throw e;
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}

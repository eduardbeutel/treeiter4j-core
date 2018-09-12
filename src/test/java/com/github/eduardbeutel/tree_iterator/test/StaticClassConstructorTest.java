package com.github.eduardbeutel.tree_iterator.test;

import com.github.eduardbeutel.tree_iterator.core.PredicateCreator;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StaticClassConstructorTest
{

    List<Class> staticClasses = Arrays.asList(
            PredicateCreator.class
    );

    @Test
    public void callConstructor() throws IllegalAccessException, InstantiationException
    {
        for(Class clazz: staticClasses)
        {
            clazz.newInstance();
        }
    }

}

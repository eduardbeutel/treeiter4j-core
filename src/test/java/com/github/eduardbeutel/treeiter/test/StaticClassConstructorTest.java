package com.github.eduardbeutel.treeiter.test;

import com.github.eduardbeutel.treeiter.common.PredicateCreator;
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

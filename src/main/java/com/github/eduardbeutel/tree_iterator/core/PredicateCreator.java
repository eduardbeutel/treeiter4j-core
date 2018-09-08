package com.github.eduardbeutel.tree_iterator.core;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class PredicateCreator
{

    public static Predicate<String> stringEquals(String other)
    {
        return param -> PredicateCreator.equals(param,other);
    }

    public static Predicate<String> stringMatches(String pattern)
    {
        Pattern compiledPattern = Pattern.compile(pattern);
        return param -> compiledPattern.matcher(param).matches();
    }

    protected static boolean equals(String left, String right)
    {
        if (left == null && right != null) return false;
        if (left != null && right == null) return false;
        if (left == null && right == null) return true;
        return left.equals(right);
    }

}

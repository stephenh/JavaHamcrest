/*  Copyright (c) 2000-2006 hamcrest.org
 */
package org.hamcrest.core;

import static org.hamcrest.AbstractMatcherTest.assertDescription;
import static org.hamcrest.AbstractMatcherTest.assertDoesNotMatch;
import static org.hamcrest.AbstractMatcherTest.assertMatches;
import static org.hamcrest.AbstractMatcherTest.assertNullSafe;
import static org.hamcrest.AbstractMatcherTest.assertUnknownTypeSafe;
import static org.hamcrest.core.IsEqual.equalTo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.Assert;

public final class IsEqualTest {

    @Test public void
    copesWithNullsAndUnknownTypes() {
        Matcher<?> matcher = equalTo("irrelevant");

        assertNullSafe(matcher);
        assertUnknownTypeSafe(matcher);
    }

    @Test public void
    comparesObjectsUsingEqualsMethod() {
        final Matcher<String> matcher1 = equalTo("hi");
        assertMatches(matcher1, "hi");
        assertDoesNotMatch(matcher1, "bye");
        assertDoesNotMatch(matcher1, null);

        final Matcher<Integer> matcher2 = equalTo(1);
        assertMatches(matcher2, 1);
        assertDoesNotMatch(matcher2, 2);
        assertDoesNotMatch(matcher2, null);
    }

    @Test public void
    canCompareNullValues() {
        final Matcher<Object> matcher = equalTo(null);
        
        assertMatches(matcher, null);
        assertDoesNotMatch(matcher, 2);
        assertDoesNotMatch(matcher, "hi");
        assertDoesNotMatch(matcher, new String[] {"a", "b"});
    }

    @Test public void
    honoursIsEqualImplementationEvenWithNullValues() {
        Object alwaysEqual = new Object() {
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        };
        Object neverEqual = new Object() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

        final Matcher<Object> matcher = equalTo(null);
        assertMatches(matcher, alwaysEqual);
        assertDoesNotMatch(matcher, neverEqual);
    }

    @Test public void
    comparesTheElementsOfAnObjectArray() {
        String[] s1 = {"a", "b"};
        String[] s2 = {"a", "b"};
        String[] s3 = {"c", "d"};
        String[] s4 = {"a", "b", "c", "d"};

        final Matcher<String[]> matcher = equalTo(s1);
        assertMatches(matcher, s1);
        assertMatches(matcher, s2);
        assertDoesNotMatch(matcher, s3);
        assertDoesNotMatch(matcher, s4);
        assertDoesNotMatch(matcher, null);
    }

    @Test public void
    comparesTheElementsOfAnArrayOfPrimitiveTypes() {
        int[] i1 = new int[]{1, 2};
        int[] i2 = new int[]{1, 2};
        int[] i3 = new int[]{3, 4};
        int[] i4 = new int[]{1, 2, 3, 4};

        final Matcher<int[]> matcher = equalTo(i1);
        assertMatches(matcher, i1);
        assertMatches(matcher, i2);
        assertDoesNotMatch(matcher, i3);
        assertDoesNotMatch(matcher, i4);
        assertDoesNotMatch(matcher, null);
    }

    @Test public void
    recursivelyTestsElementsOfArrays() {
        int[][] i1 = new int[][]{{1, 2}, {3, 4}};
        int[][] i2 = new int[][]{{1, 2}, {3, 4}};
        int[][] i3 = new int[][]{{5, 6}, {7, 8}};
        int[][] i4 = new int[][]{{1, 2, 3, 4}, {3, 4}};

        final Matcher<int[][]> matcher = equalTo(i1);
        assertMatches(matcher, i1);
        assertMatches(matcher, i2);
        assertDoesNotMatch(matcher, i3);
        assertDoesNotMatch(matcher, i4);
        assertDoesNotMatch(matcher, null);
    }

    @Test public void
    includesTheResultOfCallingToStringOnItsArgumentInTheDescription() {
        final String argumentDescription = "ARGUMENT DESCRIPTION";
        Object argument = new Object() {
            @Override
            public String toString() {
                return argumentDescription;
            }
        };
        assertDescription("<" + argumentDescription + ">", equalTo(argument));
    }

    @Test public void
    returnsAnObviousDescriptionIfCreatedWithANestedMatcherByMistake() {
        Matcher<? super String> innerMatcher = equalTo("NestedMatcher");
        assertDescription("<" + innerMatcher.toString() + ">", equalTo(innerMatcher));
    }

    @Test public void
    returnsGoodDescriptionIfCreatedWithNullReference() {
        assertDescription("null", equalTo(null));
    }

    @Test public void
    throwsComparisonFailureForStrings() {
        final Matcher<String> matcher = equalTo("foo");
        assertDoesNotMatch(matcher, "bar");
        try {
            matcher.describeMismatch("bar", new StringDescription());
            Assert.fail();
        } catch (ComparisonFailure fe) {
            Assert.assertEquals("bar", fe.getActual());
            Assert.assertEquals("foo", fe.getExpected());
        }
    }
}


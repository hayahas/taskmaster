package com.haya.taskmaster;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddTaskActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addTaskActivityTest() {
        // Click the "Add Task" button in myTasksActivity
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.addTaskbtn), withText("Add Task"),
                        childAtPosition(
                                allOf(withId(R.id.myTasksActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1)))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());
        // Click the "Add Task" button in addTaskActivity
        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.addNewTaskBtn), withText("Add Task"),
                        childAtPosition(
                                allOf(withId(R.id.addTaskActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5)))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());
        pressBack();
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.tasksRecycelerView),
                        childAtPosition(
                                withId(R.id.myTasksActivity),
                                4)))
                .check(matches(isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));
        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.backToHomeFromDetailsBtn), withText("Back"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0)))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

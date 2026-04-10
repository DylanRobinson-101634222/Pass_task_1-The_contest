package au.edu.swin.passtask1_thecontest

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private var scoreButtonId = 0
    private var stealButtonId = 0
    private var resetButtonId = 0
    private var scoreTextId = 0

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initValidIds() {
        scoreButtonId = R.id.buttonA
        stealButtonId = R.id.buttonB
        resetButtonId = R.id.buttonC
        scoreTextId = R.id.scoreText
    }

    @Test
    fun clickScoreButton3Times() {
        val scoreButton = onView(withId(scoreButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        onView(withId(scoreTextId)).check(matches(withText("3")))
    }

    @Test
    fun clickScoreAndStealButtons() {
        val scoreButton = onView(withId(scoreButtonId))
        val stealButton = onView(withId(stealButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        stealButton.perform(click())

        onView(withId(scoreTextId)).check(matches(withText("2")))
    }

    @Test
    fun testLowerLimitsOfScore() {
        // Current app logic allows the score to go below zero.
        val scoreButton = onView(withId(scoreButtonId))
        val stealButton = onView(withId(stealButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        for (i in 1..5) {
            stealButton.perform(click())
        }

        onView(withId(scoreTextId)).check(matches(withText("-2")))
    }

    @Test
    fun testUpperLimitsOfScore() {
        // Current app logic does not cap score at 15.
        val scoreButton = onView(withId(scoreButtonId))

        for (i in 1..15) {
            scoreButton.perform(click())
        }

        for (i in 1..2) {
            scoreButton.perform(click())
        }

        onView(withId(scoreTextId)).check(matches(withText("17")))
    }

    @Test
    fun testResetButton() {
        val scoreButton = onView(withId(scoreButtonId))
        val resetButton = onView(withId(resetButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        resetButton.perform(click())

        onView(withId(scoreTextId)).check(matches(withText("0")))
    }

    @Test
    fun testScoreOnRotation() {
        val scoreButton = onView(withId(scoreButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        onView(withId(scoreTextId)).check(matches(withText("3")))

        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(withId(scoreTextId)).check(matches(withText("3")))
    }

    @Test
    fun testScoreOnRotationWithClick() {
        val scoreButton = onView(withId(scoreButtonId))

        for (i in 1..3) {
            scoreButton.perform(click())
        }

        activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        scoreButton.perform(click())

        onView(withId(scoreTextId)).check(matches(withText("4")))
    }
}


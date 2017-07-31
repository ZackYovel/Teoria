package com.gmail.ezekiyovel.teoria.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestionItemTest {

    private QuestionItem questionItem;
    private final long[] CLASSES_BIN = {
            QuestionItem.CLASS_BIN_A,
            QuestionItem.CLASS_BIN_A1,
            QuestionItem.CLASS_BIN_A2,
            QuestionItem.CLASS_BIN_B,
            QuestionItem.CLASS_BIN_C,
            QuestionItem.CLASS_BIN_C1,
            QuestionItem.CLASS_BIN_CPlusE,
            QuestionItem.CLASS_BIN_D,
            QuestionItem.CLASS_BIN_D1,
            QuestionItem.CLASS_BIN_D3,
            QuestionItem.CLASS_BIN_1
    };
    private final String[] CLASSES_STR = {
            QuestionItem.CLASS_STR_A,
            QuestionItem.CLASS_STR_B,
            QuestionItem.CLASS_STR_CYRILLIC_VE,
            QuestionItem.CLASS_STR_C,
            QuestionItem.CLASS_STR_C1,
            QuestionItem.CLASS_STR_CPlusE,
            QuestionItem.CLASS_STR_D,
            QuestionItem.CLASS_STR_D1,
            QuestionItem.CLASS_STR_D3,
            QuestionItem.CLASS_STR_1
    };

    @Before
    public void setUp() throws Exception {
        this.questionItem = new QuestionItem();
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Tests thoroughly if classes that are not added to a questionitem always return false
     * for isRelevantToClass(binClass).
     *
     * Given any combination of classes, if all classes in the combination added to a questionitem
     * and non else added, asserts that any class that is not in the combination (and thus not
     * added to the questionitem) will not be considered relevant by the questionitem, meaning
     * the call questionItem.isRelevantToClass(binClass) will return false for any binClass
     * which the matching stringClass of it was not added to the combination.
     *
     * This test method uses complex logic and is very error prone. If fails, consider a bug in
     * the method itself.
     * @throws Exception
     */
    @Test
    public void testIsRelevantToClassThorough() throws Exception {
        // TODO: 05/08/16 Implement
    }

    @Test
    public void testAddClassSpecialCase1() throws Exception {
        questionItem.addClass(QuestionItem.CLASS_STR_A);
        questionItem.addClass(QuestionItem.CLASS_STR_B);
        assertEquals("Not as expected", QuestionItem.CLASS_BIN_A + QuestionItem.CLASS_BIN_B,
                questionItem.getClasses());
    }

    @Test
    public void testAddClass() throws Exception {
        assertEquals("Before adding classes, should be 0",
                0b0, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_A);
        assertEquals("Added only the A class, should be 1",
                0b1, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_A1);
        assertEquals("Added A and A1, should be 3",
                0b11, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_A2);
        assertEquals("Added A, A1 and A2, should be 5",
                0b111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_B);
        assertEquals("Added A through B, should be 7",
                0b1111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_C);
        assertEquals("Added A through C, should be 9",
                0b11111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_C1);
        assertEquals("Added A through C1, should be 11",
                0b111111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_CPlusE);
        assertEquals("Added A through C+E, should be 13",
                0b1111111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_D);
        assertEquals("Added A through D, should be 15",
                0b11111111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_D1);
        assertEquals("Added A through D1, should be 17",
                0b111111111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_D3);
        assertEquals("Added A through D3, should be 19",
                0b1111111111, questionItem.getClasses());
        questionItem.addClass(QuestionItem.CLASS_STR_1);
        assertEquals("Added A through 1, should be 21",
                0b11111111111, questionItem.getClasses());
    }

    @Test
    public void testIsRelevantToClass() throws Exception {
        testIsRelevantToClassEmpty();
        testIsRelevantToClassA();
        testIsRelevantToClassAAndC1();
        testIsRelevantToClassAAndC1AndB();
    }

    private void testIsRelevantToClassAAndC1AndB() {
        questionItem.addClass(QuestionItem.CLASS_STR_B);

        assertTrue("A, C1 and B class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A1));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A2));
        assertTrue("A, C1 and B class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_B));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C));
        assertTrue("A, C1 and B class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C1));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_CPlusE));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D1));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D3));
        assertFalse("A, C1 and B class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_1));
    }

    private void testIsRelevantToClassAAndC1() {
        questionItem.addClass(QuestionItem.CLASS_STR_C1);

        assertTrue("A and C1 class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A1));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A2));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_B));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C));
        assertTrue("A and C1 class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C1));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_CPlusE));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D1));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D3));
        assertFalse("A and C1 class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_1));
    }

    private void testIsRelevantToClassA() {
        questionItem.addClass(QuestionItem.CLASS_STR_A);

        assertTrue("A class added, should be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A1));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A2));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_B));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C1));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_CPlusE));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D1));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D3));
        assertFalse("A class added, shouldn't be relevant",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_1));
    }

    private void testIsRelevantToClassEmpty() {
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A1));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_A2));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_B));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_C1));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_CPlusE));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D1));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_D3));
        assertFalse("No class added, shouldn't be relevant to any class",
                questionItem.isRelevantToClass(QuestionItem.CLASS_BIN_1));
    }

    @Test
    public void testGetClasses() throws Exception {

    }

    @Test
    public void testSetClasses() throws Exception {

    }

    @Test
    public void testGetAnswerAttempts() throws Exception {

    }

    @Test
    public void testSetAnswerAttempts() throws Exception {

    }

    @Test
    public void testGetImage() throws Exception {

    }

    @Test
    public void testSetImage() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {

    }

    @Test
    public void testSetId() throws Exception {

    }

    @Test
    public void testGetCategory() throws Exception {

    }

    @Test
    public void testSetCategory() throws Exception {

    }

    @Test
    public void testGetPubDate() throws Exception {

    }

    @Test
    public void testSetPubDate() throws Exception {

    }

    @Test
    public void testGetTitle() throws Exception {

    }

    @Test
    public void testSetTitle() throws Exception {

    }

    @Test
    public void testGetOptions() throws Exception {

    }

    @Test
    public void testSetOptions() throws Exception {

    }

    @Test
    public void testGetCorrectAnswer() throws Exception {

    }

    @Test
    public void testSetCorrectAnswer() throws Exception {

    }

    @Test
    public void testDescribeContents() throws Exception {

    }

    @Test
    public void testWriteToParcel() throws Exception {

    }
}
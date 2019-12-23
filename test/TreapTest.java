import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TreapTest {

    @Test
    void test1() {
        SortedSet<Integer> lul = new Treap<Integer>();

        assertTrue(lul.add(2));
        assertTrue(lul.add(1));
        assertTrue(lul.add(3));

        // Test *contains*
        assertTrue(lul.contains(2));
        assertTrue(lul.contains(1));
        assertTrue(lul.contains(3));

        // Test *remove*
        assertTrue(lul.remove(2));
        assertEquals(2, lul.size());

        assertFalse(lul.remove(2));
        assertFalse(lul.remove(231));
        assertEquals(2, lul.size());

        // Test *add*
        assertTrue(lul.add(9));
        assertFalse(lul.add(9));
        assertTrue(lul.add(7));
        assertEquals(4, lul.size());

        assertFalse(lul.isEmpty());

        Integer i;
        Integer[] allElements = new Integer[4];
        lul.toArray(allElements);
        ArrayList<Integer> ans = new ArrayList<Integer>();

        // Test *iterator*
        Iterator<Integer> lulIter = lul.iterator();
        while (lulIter.hasNext()) {
            i = lulIter.next();
            ans.add(i);
            lulIter.remove();
        }
        assertTrue(lul.isEmpty());
        assertArrayEquals(allElements, ans.toArray(new Integer[4]));


        // Test *addAll*
        ArrayList<Integer> array = new ArrayList<Integer>(2);
        array.add(1);
        array.add(7);
        assertTrue(lul.addAll(array));
        assertEquals(2, lul.size());
        array.add(4);
        assertTrue(lul.addAll(array));
        assertEquals(3, lul.size());
        assertFalse(lul.addAll(array));
        assertEquals(3, lul.size());
    }

    @Test
    void test2() {

        // Test *subSet*
        SortedSet<Integer> tree = new Treap<Integer>();
        tree.add(1);
        tree.add(3);
        tree.add(4);
        SortedSet<Integer> maybeTree = tree.subSet(2, 10);
        assertEquals(2, maybeTree.size());
        assertTrue(maybeTree.contains(3));
        assertFalse(maybeTree.contains(2));

        // Test *set.add <-> subSet.add*
        tree.add(2);
        tree.add(10);
        tree.add(11);
        ArrayList<Integer> expected = new ArrayList<Integer>();
        expected.add(2);
        expected.add(3);
        expected.add(4);
        expected.add(2);
        assertTrue(maybeTree.containsAll(expected));
        assertFalse(maybeTree.contains(10));

        boolean outOfBounds = false;
        try {
            maybeTree.add(11);
        }
        catch (IllegalArgumentException e) {
            outOfBounds = true;
            assertFalse(maybeTree.contains(11));
        }
        assertTrue(outOfBounds);

        Iterator<Integer> iter = maybeTree.iterator();
        int i;
        while (iter.hasNext()){
            i = iter.next();
            iter.remove();
        }
        assertNotEquals(0, tree.size());
        assertEquals(0, maybeTree.size());

        // Test *tailSet*
        SortedSet<Integer> tailTree = tree.tailSet(-917);
        assertTrue(tailTree.contains(11));
        assertEquals(0, tailTree.headSet(-916).size());
        tailTree.add(-917);
        assertTrue(tailTree.headSet(-916).contains(-917));
        assertTrue(tree.contains(-917));

        // Test *headSet*
        SortedSet<Integer> headTree = tree.headSet(11);
        assertFalse(headTree.contains(11));
        assertTrue(headTree.tailSet(10).contains(10));

        outOfBounds = false;
        try {
            headTree.add(20);
        }
        catch (IllegalArgumentException e){
            outOfBounds = true;
            assertFalse(headTree.contains(20));
        }
        assertTrue(outOfBounds);


        // Test *first*, *last*
        assertEquals(-917, tree.first());
        assertEquals(-917, tailTree.first());
        assertEquals(tailTree.last(), tree.last());
        assertNotEquals(tree.last(), headTree.last());

        boolean noFirstElement = false;
        try {
            System.out.println(maybeTree.first());
        }
        catch (NoSuchElementException e) {
            noFirstElement = true;
            assertTrue(maybeTree.isEmpty());
        }
        assertTrue(noFirstElement);

    }

    @Test
    void test3() {
        SortedSet<String> str = new Treap<String>();

        str.add("a");
        str.add("b");
        str.add("c");
        str.add("d");

        StringBuilder ans = new StringBuilder();
        for (String s: str)
            ans.append(s);
        assertEquals("a", str.first());
        assertEquals("d", str.last());
        assertEquals("abcd", ans.toString());

        // Test *subSet* of strings
        SortedSet<String> subStr = str.subSet("a", "c");
        assertEquals("a", subStr.first());
        assertNotEquals("c", subStr.last());
        assertNotEquals("C", subStr.last());

        // Test *headSet* of strings
        SortedSet<String> headStr = str.headSet("is_this_the_end_of_the_beginning?");
        assertEquals("a", headStr.first());
        assertTrue(headStr.contains("c"));
        assertEquals("d", headStr.last());

        // Test *tailSet* of strings
        SortedSet<String> tailStr = str.tailSet("b");
        assertNotEquals("a", tailStr.first());
        assertTrue(headStr.remove("d"));
        str.add("d");  // Checking the link between subSet and Set
        assertEquals("d", headStr.last());
    }
}

import javafx.util.Pair;

import java.util.*;

public class Treap<E extends Comparable<E>> extends AbstractSet<E> implements SortedSet<E> {

    private int size = 0;
    private Node<E> root = null;
    private final Random priorityGenerator = new Random();

    public Treap() {
    }

    public Treap(Set<? extends E> s) {
        addAll(s);
    }

    public Comparator<? super E> comparator() {
        return null;
    }

    private class Node<E> {
        final E element;
        Node<E> left;
        Node<E> right;
        Node<E> parent = null;

        final int priority;

        Node(E element) {
            this.element = element;

            priority = priorityGenerator.nextInt();
        }

    }

    private Node<E> find(Node<E> start, E value) {
        int comparison = value.compareTo(start.element);
        if (comparison == 0)
            return start;
        else if (comparison < 0) {
            if (start.left != null)
                return find(start.left, value);
            else
                return start;
        }
        else {
            if (start.right != null)
                return find(start.right, value);
            else
                return start;
        }
    }

    private Node<E> find(E value) {
        if (root != null)
            return find(root, value);
        return null;
    }

    private Pair<Node<E>, Node<E>> split(Node<E> start, E key) {
        if (start != null) {
            if (key.compareTo(start.element) > 0) {
                Pair<Node<E>, Node<E>> subSplit = split(start.right, key);
                start.right = subSplit.getKey();
                if (start.right != null)
                    start.right.parent = start;

                if (subSplit.getValue() != null)
                    subSplit.getValue().parent = null;

                return new Pair<Node<E>, Node<E>>(start, subSplit.getValue());
            }
            else {
                Pair<Node<E>, Node<E>> subSplit = split(start.left, key);
                start.left = subSplit.getValue();
                if (start.left != null)
                    start.left.parent = start;

                if (subSplit.getKey() != null)
                    subSplit.getKey().parent = null;

                return new Pair<Node<E>, Node<E>>(subSplit.getKey(), start);
            }
        }
        return new Pair<Node<E>, Node<E>>(null, null);
    }

    private Node<E> merge(Node<E> first, Node<E> second) {
        if (first == null)
            return second;

        if (second == null)
            return first;

        if (first.priority > second.priority) {
            first.right = merge(first.right, second);

            first.right.parent = first;

            return first;
        }
        else {
            second.left = merge(first, second.left);

            second.left.parent = second;

            return second;
        }
    }

    @Override
    public boolean add(E value) {
        Node<E> closest = find(value);
        if (closest != null && closest.element.equals(value))
            return false;

        Pair<Node<E>, Node<E>> pairOfTrees = split(root, value);
        root = merge(merge(pairOfTrees.getKey(), new Node<E>(value)),
                pairOfTrees.getValue());
        root.parent = null;

        size++;
        return true;
    }

    public boolean remove(E value) {
        Node<E> closest = find(value);

        if (closest != null && closest.element.equals(value)) {

            Node<E> oldParent = closest.parent;

            if (oldParent == null) {
                root = merge(closest.left, closest.right);
                if (root != null)
                    root.parent = null;
            }
            else {
                if (oldParent.left != null && oldParent.left.element.equals(closest.element)) {
                    oldParent.left = merge(closest.left, closest.right);

                    if (oldParent.left != null)
                        oldParent.left.parent = oldParent;
                } else {
                    oldParent.right = merge(closest.left, closest.right);

                    if (oldParent.right != null)
                        oldParent.right.parent = oldParent;
                }
            }

            size--;
            return true;
        }
        else return false;
    }

    public E first() {
        if (root != null) {
            Node<E> current = root;
            while (current.left != null) {
                current = current.left;
            }
            return current.element;
        }
        else
            throw new NoSuchElementException();
    }

    public E last() {
        if (root != null) {
            Node<E> current = root;
            while (current.right != null) {
                current = current.right;
            }
            return current.element;
        }
        else
            throw new NoSuchElementException();
    }


    public int size() {
        return size;
    }

    public boolean contains(E value) {
        Node<E> closest = find(value);
        return closest != null && value.compareTo(closest.element) == 0;
    }

    @Override
    public boolean contains(Object o) {
        return contains((E) o);
    }

    private class TreapIterator implements Iterator<E> {

        Queue<Node<E>> iter = new LinkedList<Node<E>>();
        Node<E> cur = null;

        void genIterator(Node<E> state) {
            if (state.left != null)
                genIterator(state.left);

            iter.offer(state);

            if (state.right != null)
                genIterator(state.right);
        }

        TreapIterator() {
            if (root != null) {
                genIterator(root);
                cur = iter.peek();
            }
        }

        public boolean hasNext() {
            return !iter.isEmpty();
        }

        public E next() {
            cur = iter.poll();
            if (cur == null)
                throw new NoSuchElementException();
            return cur.element;
        }

        public void remove() {
            if (cur != null)
                Treap.this.remove(cur.element);
        }
    }

    public Iterator<E> iterator() {
        return new TreapIterator();
    }


    private class Branch extends Treap<E> {
        private E lowerBound;
        private E upperBound;
        private Treap<E> master;

        Branch(E lowBound, E upBound, Treap<E> masterTree) {
            lowerBound = lowBound;
            upperBound = upBound;
            master = masterTree;
        }

        private boolean valid(E e) {
            return ((lowerBound == null || e.compareTo(lowerBound) >= 0) &&
                    (upperBound == null || e.compareTo(upperBound) < 0));
        }

        @Override
        public boolean contains(E value) {
            return valid(value) && master.contains(value);
        }

        @Override
        public boolean add(E value) {
            if (!valid(value))
                throw new IllegalArgumentException("Element is out of bounds");
            return master.add(value);
        }

        @Override
        public boolean remove(E value) {
            if (!valid(value))
                throw new IllegalArgumentException("Element is out of bounds");
            return master.remove(value);
        }

        @Override
        public int size() {
            int count = 0;
            for (E e: master)
                if (valid(e))
                    count++;
            return count;
        }

        //
        @Override
        public E first() {
            if (size() != 0) {
                return iterator().next();
            }
            else
                throw new NoSuchElementException();
        }

        @Override
        public E last() {
            if (size() != 0) {
                Iterator<E> lastIter = iterator();
                E current = lastIter.next();  // size() != 0
                while (lastIter.hasNext())
                    current = lastIter.next();
                return current;
            }
            else
                throw new NoSuchElementException();
        }

        private class BranchIterator extends TreapIterator {
            Deque<Node<E>> iter = new LinkedList<Node<E>>();
            Node<E> cur = null;

            private boolean valid(E e) {
                return (lowerBound == null || e.compareTo(lowerBound) >= 0) &&
                        (upperBound == null || e.compareTo(upperBound) < 0);
            }

            void genIterator(Node<E> state) {
                if (state.left != null)
                    genIterator(state.left);

                if (valid(state.element))
                    iter.offer(state);

                if (state.right != null)
                    genIterator(state.right);
            }

            BranchIterator() {
                if (root != null) {
                    genIterator(root);
                    cur = iter.peek();
                }
            }

            public boolean hasNext() {
                return !iter.isEmpty();
            }

            public E next() {
                cur = iter.poll();
                if (cur == null)
                    throw new NoSuchElementException();
                return cur.element;
            }

            public void remove() {
                if (cur != null)
                    master.remove(cur.element);
            }
        }

        @Override
        public Iterator<E> iterator() {
            return new BranchIterator();
        }

    }

    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (fromElement != null && toElement != null &&
                fromElement.compareTo(toElement) > 0)
            throw new IllegalArgumentException("Lower bound should not be greater than upper bound");
        if (fromElement == null || toElement == null)
            throw new IllegalArgumentException("fromElement and toElement must not be null");

        return new Branch(fromElement, toElement, this);
    }

    public SortedSet<E> headSet(E toElement) {
        if (toElement == null)
            throw new IllegalArgumentException("toElement must not be null");

        return new Branch(null, toElement, this);
    }

    public SortedSet<E> tailSet(E fromElement) {
        if (fromElement == null)
            throw new IllegalArgumentException("fromElement must not be null");

        return new Branch(fromElement, null, this);
    }

}

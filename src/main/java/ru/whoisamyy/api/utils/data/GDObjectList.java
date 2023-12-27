package ru.whoisamyy.api.utils.data;

import org.jetbrains.annotations.NotNull;
import ru.whoisamyy.api.gd.objects.GDObject;

import java.util.*;

//завтра переделать ВСЁ под GDObjectList хех)

/**
 * List used to store {@code GDObject}s. Main difference from other lists is in {@link GDObjectList#subList} method.
 * @see GDObject
 * @see GDObjectList#subList(int, int)
 * @param <E>
 */
public class GDObjectList<E extends GDObject> implements List<E> {
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 16;
    private Object[] elements;

    public GDObjectList() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    public GDObjectList(Collection<? extends E> c) {
        this.elements = new Object[c.size()];
        addAll(c);
    }

    @SafeVarargs
    public GDObjectList(E... newElements) {
        this.size = newElements.length;
        this.elements = newElements;
    }

    public int countOf(Object o) {
        int counter = 0;
        for (Object ob : elements) {
            if (ob==o) counter++;
        }
        return counter;
    }

    void removeSpaces() {
        int count = countOf(null);
        for (int i = 0; i < count; i++) {
            removeSpace();
        }
    }

    boolean removeSpace() {
        for (int i = 0; i < size(); i++) {
            if (elements[i]==null) {
                GDObjectList<E> beforeNull = subList(0, i-1);
                GDObjectList<E> afterNull = subList(i+1, size());
                beforeNull.addAll(afterNull);
                clear();
                addAll(beforeNull);
                return true;
            }
        }
        return false;
    }

    void addSpaceAt(int index) {
        grow();
        GDObjectList<E> beforeSpace = subList(0, index-1);
        GDObjectList<E> afterSpace = subList(index, size());
        beforeSpace.add(null);
        beforeSpace.addAll(afterSpace);
        this.elements = beforeSpace.elements;
        this.size = beforeSpace.size();
    }

    public E get(int index) {
        if (index > size()) throw new IndexOutOfBoundsException();
        return (E) elements[index];
    }

    @Override
    public E set(int index, E element) {
        E pe = (E) elements[index];
        elements[index]=element;
        return pe;
    }

    @Override
    public void add(int index, E element) {
        addSpaceAt(index);
        elements[index]=element;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size(); i >= 0; i--) {
            if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public boolean contains(Object o) {
        for (Object el : elements) {
            if (el.equals(o)) return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return new ListItr();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return elements;
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return (T[]) elements;
    }

    public boolean add(E e) {
        if (elements[size()] == null) elements[size()] = e;
        else grow();
        elements[elements.length - 1] = e;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        remove(indexOf(o));
        return true;
    }

    @Override
    public E remove(int index) {
        if (index>size()) return null;
        E ret = get(index);
        this.elements[index]=null;
        removeSpaces();
        this.size--;
        return ret;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        for (E e : c) {
            add(index++, e);
        }
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        c.forEach(x -> remove(indexOf(x)));
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        for (Object e : c) {
            if (contains(e)) continue;
            remove(e);
        }
        return true;
    }

    @Override
    public void clear() {
        elements=new Object[DEFAULT_CAPACITY];
    }

    private Object[] grow() {
        int oldCapacity = size();
        int newCapacity = oldCapacity * 2;
        if (oldCapacity > 0) {
            return elements = Arrays.copyOf(elements, newCapacity);
        } else {
            return elements = new Object[Math.max(DEFAULT_CAPACITY, newCapacity)];
        }
    }


    /**
     * Returns a representation of the part of this list between the specified fromIndex, inclusive, and toIndex, inclusive.
     * @param from low endpoint (inclusive) of the subList
     * @param to high endpoint (inclusive) of the subList
     * @return Representation of the part of this list between the specified fromIndex, inclusive, and toIndex, inclusive.
     */
    @NotNull
    public GDObjectList<E> subList(int from, int to) {
        subListRangeCheck(from, to, size());
        GDObjectList<E> ret = new GDObjectList<>();
        for (; from < to; from++) {
            ret.add((E) elements[from]);
        }
        return ret;
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")");
    }

    private class Itr implements Iterator<E> {
        int cursor;
        int lastRet = -1;
        @Override
        public boolean hasNext() {
            return cursor!=size;
        }

        @Override
        public E next() {
            int i = cursor;
            if (i > size) {
                throw new NoSuchElementException();
            }
            cursor = i+1;
            return (E) elements[lastRet=i];
        }

        @Override
        public void remove() {
            if (lastRet<0)
                throw new IllegalStateException();

            GDObjectList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }
    }

    private class ListItr extends Itr implements ListIterator<E> {
        ListItr() {
            super();
        }
        ListItr(int index) {
            super();
            cursor = index;
        }
        @Override
        public boolean hasPrevious() {
            return cursor!=0;
        }

        @Override
        public E previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();

            cursor=i;
            return (E) GDObjectList.this.elements[lastRet=i];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor-1;
        }

        @Override
        public void set(E e) {
            if (lastRet<0)
                throw new IllegalStateException();

            GDObjectList.this.set(lastRet, e);
        }

        @Override
        public void add(E e) {
            int i = cursor;
            GDObjectList.this.add(i, e);
            cursor = i+1;
            lastRet = -1;
        }
    }
}

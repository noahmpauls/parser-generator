package com.noahmpauls.compilers.generator.lrzero;

import com.noahmpauls.compilers.generator.Production;
import com.noahmpauls.compilers.generator.ConcreteType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a shift-reduce item A -> a.Xb.
 */
public class Item<E extends Enum<E>> {

    public static void main(String[] args) {
        Item i = new Item(ConcreteType.BANG, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL}, 1);
        Item i2 = new Item(ConcreteType.BANG, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL}, 1);
        System.out.println(i.hashCode());
        System.out.println(i2.hashCode());
    }

    private final E start;
    private final List<E> produces;
    private final int dot;

    private void checkRep() {
        assert produces.size() > 0;
        assert dot >= 0 && dot <= produces.size();
    }

    public Item(E start, E[] produces) {
        this(start, produces, 0);
    }

    public Item(E start, E[] produces, int dot) {
        this.start = start;
        this.produces = new ArrayList<>(Arrays.asList(produces));
        this.dot = dot;
        checkRep();
    }

    public Item(E start, List<E> produces) {
        this(start, produces, 0);
    }

    public Item(E start, List<E> produces, int dot) {
        this.start = start;
        this.produces = new ArrayList<>(produces);
        this.dot = dot;
        checkRep();
    }

    public Item(Production<E> production) {
        this(production, 0);
    }

    public Item(Production<E> production, int dot) {
        this.start = production.start();
        this.produces = new ArrayList<>(production.produces());
        this.dot = dot;
        checkRep();
    }

    /**
     * For item A -> a.Xb:
     *
     * @return A
     */
    public E start() {
        return start;
    }

    /**
     * For item A -> a.Xb:
     *
     * @return X, the same as next(0)
     */
    public E first() {
        return next(0);
    }

    /**
     * For item A -> a.Xb:
     *
     * @return the ith item of the sequence of symbols in Xb
     */
    public E next(final int i) {
        if (remaining() <= i) {
            throw new IndexOutOfBoundsException("not enough symbols for index " + i + " (" + toString() + ")");
        }
        return produces.get(dot + i);
    }

    /**
     *
     * @return the entire rhs of the item, irrespective of the dot location
     */
    public List<E> produces() {
        return new ArrayList<>(produces);
    }

    /**
     * For item A -> a.Xb:
     *
     * @return the number of symbols in Xb
     */
    public int remaining() {
        return produces.size() - dot;
    }

    /**
     * For item A -> a.Xb:
     *
     * @return new item A -> aX.b
     */
    public Item<E> shift() {
        if (remaining() == 0) {
            throw new IndexOutOfBoundsException("no more symbols to shift (" + toString() + ")");
        }
        return new Item<E>(start, produces, dot+1);
    }

    public Production<E> production() {
        return new Production<E>(start, produces);
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Item && sameValue((Item) that);
    }

    private boolean sameValue(Item that) {
        return this.start.equals(that.start)
                && this.produces.equals(that.produces)
                && this.dot == that.dot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, produces, dot);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(start.name());
        result.append(" -> ");
        for (int i = 0; i < produces.size(); i++) {
            if (i == dot)
                result.append(". ");
            result.append(produces.get(i).name()).append(" ");
        }
        if (dot == produces.size())
            result.append(".");
        return result.toString();
    }
}

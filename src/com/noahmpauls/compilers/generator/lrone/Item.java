package com.noahmpauls.compilers.generator.lrone;

import com.noahmpauls.compilers.generator.Production;
import com.noahmpauls.compilers.generator.ConcreteType;

import java.util.*;

/**
 * Represents a shift-reduce item A -> a.Xb.
 */
public class Item<E extends Enum<E>> {

    public static void main(String[] args) {
        Item i = new Item(ConcreteType.BANG, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL}, 1, ConcreteType.EOF);
        Item i2 = new Item(ConcreteType.BANG, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL}, 1, ConcreteType.EOF).shift();
        System.out.println(i);
        System.out.println(i2);
        System.out.println(i.afterDot());
        System.out.println(i2.afterDot());

    }

    private final E start;
    private final List<E> produces;
    private final int dot;
    private final E lookahead;

    private void checkRep() {
        assert produces.size() > 0;
        assert dot >= 0 && dot <= produces.size();
    }

    public Item(E start, E[] produces, E lookahead) {
        this(start, produces, 0, lookahead);
    }

    public Item(E start, E[] produces, int dot, E lookahead) {
        this.start = start;
        this.produces = new ArrayList<>(Arrays.asList(produces));
        this.dot = dot;
        this.lookahead = lookahead;
        checkRep();
    }

    public Item(E start, List<E> produces, E lookahead) {
        this(start, produces, 0, lookahead);
    }

    public Item(E start, List<E> produces, int dot, E lookahead) {
        this.start = start;
        this.produces = new ArrayList<>(produces);
        this.dot = dot;
        this.lookahead = lookahead;
        checkRep();
    }

    public Item(Production<E> production, E lookahead) {
        this(production, 0, lookahead);
    }

    public Item(Production<E> production, int dot, E lookahead) {
        this.start = production.start();
        this.produces = new ArrayList<>(production.produces());
        this.dot = dot;
        this.lookahead = lookahead;
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
     *
     * @return the rhs of the item after the dot location
     */
    public List<E> afterDot() {
        return produces.subList(dot, produces.size());
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
     * For item [A -> a.B, T], returns the lookahead symbol T.
     *
     * @return the lookahead symbol for this item
     */
    public E lookahead() {
        return lookahead;
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
        return new Item<E>(start, produces, dot+1, lookahead);
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
                && this.dot == that.dot
                && this.lookahead.equals(that.lookahead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, produces, dot, lookahead);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        result.append(start.name());
        result.append(" -> ");
        for (int i = 0; i < produces.size(); i++) {
            if (i == dot)
                result.append(". ");
            result.append(produces.get(i).name()).append(" ");
        }
        if (dot == produces.size())
            result.append(". ");
        result.append("(" + lookahead.name() + ")]");
        return result.toString();
    }
}

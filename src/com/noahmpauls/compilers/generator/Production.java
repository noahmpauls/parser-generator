package com.noahmpauls.compilers.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a grammar production. Has a start type S that produces a sequence
 *  of types X, yielding the production S -> X.
 */
public class Production<E extends Enum<E>> {

    public static void main(String[] args) {
        Production<ConcreteType> i = new Production<>(ConcreteType.BANG, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL});
        Production<ConcreteType> i2 = new Production<>(ConcreteType.STAR, new ConcreteType[]{ConcreteType.AND, ConcreteType.BOOL});
        System.out.println(i.hashCode());
        System.out.println(i2.hashCode());
    }

    private final E start;
    // cannot be empty
    private final List<E> produces;

    private void checkRep() {

    }

    public Production(E start, E[] produces) {
        this.start = start;
        this.produces = new ArrayList<>(Arrays.asList(produces));
        checkRep();
    }

    public Production(E start, List<E> produces) {
        this.start = start;
        this.produces = new ArrayList<>(produces);
        checkRep();
    }

    /**
     * @return for the production S -> X, returns S
     */
    public E start() {
        return start;
    }

    /**
     * @return for the production S -> X, where X is a group of consecutive
     *  symbols, returns X
     */
    public List<E> produces() {
        return new ArrayList<E>(produces);
    }

    /**
     * @return for the production S -> X, the first symbol of X
     */
    public E first() {
        return produces.get(0);
    }

    /**
     * @return for the production S -> X, the number of symbols in the sequence
     *  X
     */
    public int size() {
        return produces.size();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Production && sameValue((Production) that);
    }

    private boolean sameValue(Production that) {
        return this.start.equals(that.start)
                && this.produces.equals(that.produces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, produces);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(start.name());
        result.append(" -> ");
        for (E produce : produces) {
            result.append(produce.name()).append(" ");
        }
        return result.toString().trim();
    }
}

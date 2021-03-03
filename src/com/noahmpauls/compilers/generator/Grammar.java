package com.noahmpauls.compilers.generator;

import java.util.*;

public class Grammar<E extends Enum<E>> {

    public static void main(String[] args) {
        Grammar<S4> grammar = new Grammar<>(Arrays.asList(
                new Production<>(S4.Z, new S4[]{ S4.d }),
                new Production<>(S4.Z, new S4[]{ S4.X, S4.Y, S4.Z }),
                new Production<>(S4.Y, new S4[]{  }),
                new Production<>(S4.Y, new S4[]{ S4.c }),
                new Production<>(S4.X, new S4[]{ S4.Y }),
                new Production<>(S4.X, new S4[]{ S4.a })
        ));

        System.out.println(grammar.first(Arrays.asList(S4.d, S4.a)));
    }

    private final List<Production<E>> rules;
    private final Map<E, Set<Production<E>>> starts;
    private final Map<Production<E>, Integer> indexByRule;

    private final Set<E> symbols;
    private final Set<E> terminals;
    private final Set<E> nonTerminals;

    private Map<E, Boolean> nullable;
    private Map<E, Set<E>> first;
    private Map<E, Set<E>> follow;

    public Grammar(List<Production<E>> productions) {
        rules = new ArrayList<>(productions);
        starts = rulesToStarts(rules);

        indexByRule = new HashMap<>();
        createRuleLookup();

        symbols = new HashSet<>();
        terminals = new HashSet<>();
        nonTerminals = new HashSet<>();
        collectSymbols();

        nullable = new HashMap<>();
        makeNullable();
        first = new HashMap<>();
        makeFirst();
        follow = new HashMap<>();
    }

    private void createRuleLookup() {
        for (int i = 0; i < rules.size(); i++) {
            indexByRule.put(rules.get(i), i);
        }
    }

    private void collectSymbols() {
        for (Production<E> p : rules) {
            symbols.add(p.start());
            symbols.addAll(p.produces());
            nonTerminals.add(p.start());
        }
        terminals.addAll(symbols);
        terminals.removeAll(nonTerminals);
    }

    private void makeNullable() {
        Map<E, Boolean> nullable = new HashMap<>();
        for (E symbol : symbols) {
            nullable.put(symbol, false);
        }

        boolean updated = true;
        while(updated) {
            updated = false;
            for (Production<E> p : rules) {
                //System.out.println(p);
                final E X = p.start();
                final List<E> Y = p.produces();
                final int k = Y.size();

                // update nullable
                boolean nullableX = true;
                for (E symbol : Y) {
                    nullableX &= nullable.get(symbol);
                }
                if (nullableX && !nullable.get(X)) {
                    nullable.put(X, true);
                    updated = true;
                }
            }
        }

        this.nullable = nullable;
    }

    private void makeFirst() {
        Map<E, Set<E>> first = new HashMap<>();
        for (E symbol : symbols) {
            first.put(symbol, new HashSet<>());
            if (terminals.contains(symbol)) {
                first.get(symbol).add(symbol);
            }
        }

        boolean updated = true;
        while(updated) {
            updated = false;
            for (Production<E> p : rules) {
                //System.out.println(p);
                final E X = p.start();
                final List<E> Y = p.produces();
                final int k = Y.size();

                for (int i = 0; i < k; i++) {
                    boolean prefixNullable = true;
                    for (int j = 0; j < i; j++) {
                        prefixNullable &= nullable.get(Y.get(j));
                    }
                    if (prefixNullable) {
                        Set<E> newFirstX = new HashSet<>(first.get(X));
                        newFirstX.addAll(first.get(Y.get(i)));
                        //System.out.println(X.name() + "," + Y.get(i).name() + ": " + newFirstX);
                        if (!newFirstX.equals(first.get(X))) {
                            first.put(X, newFirstX);
                            updated = true;
                        }
                    }
                }
            }
        }

        this.first = first;
    }

    /**
     * Turn a list of productions into a mapping from symbols to all productions
     *  starting with the key symbol.
     *
     * @param rules the list of grammar rules
     * @return mapping from start symbols to set of productions
     */
    private Map<E, Set<Production<E>>> rulesToStarts(List<Production<E>> rules) {
        Map<E, Set<Production<E>>> starts = new HashMap<>();
        for (Production<E> p : rules) {
            if (!(starts.containsKey(p.start())))
                starts.put(p.start(), new HashSet<>());
            starts.get(p.start()).add(p);
        }
        return starts;
    }

    /**
     * Get all the productions in this grammar in rule-number order.
     *
     * @return the grammars productions
     */
    public List<Production<E>> productions() {
        return new ArrayList<>(rules);
    }

    /**
     * Get the production with rule # index
     *
     * @param index the rule number
     * @return the associated production
     */
    public Production<E> rule(int index) {
        if (index >= rules.size())
            throw new IndexOutOfBoundsException("index " + index + "invalid for grammar with " + rules.size() + " rules");
        return rules.get(index);
    }

    /**
     * Gets the index of a rule if it exists.
     *
     * @param rule the rule to get the index of
     * @return the rule index
     */
    public int ruleNum(Production<E> rule) {
        if (!indexByRule.containsKey(rule))
            throw new IllegalArgumentException("production " + rule.toString() + " is not in this grammar");
        return indexByRule.get(rule);
    }

    /**
     * Get all symbols that appear on the left-hand side of a production.
     *
     * @return all non-terminals in the grammar
     */
    public Set<E> nonTerminals() {
        return new HashSet<>(nonTerminals);
    }

    /**
     * Get all symbols that do not appear on the left-hand side of a production.
     *
     * @return all terminals in the grammar
     */
    public Set<E> terminals() {
        return new HashSet<>(terminals);
    }

    /**
     * Get all productions with a given start symbol.
     *
     * @param start the start symbol
     * @return all productions start -> X in the grammar
     */
    public Set<Production<E>> withStart(E start) {
        if (starts.containsKey(start))
            return starts.get(start);
        return new HashSet<>();
    }

    /**
     * Find the set of terminals that can begin strings derived from gamma.
     *
     * @param gamma a sequence of terminals/non-terminals
     * @return the set of terminals that can begin a string derived from gamma
     */
    public Set<E> first(List<E> gamma) {
        Set<E> first = new HashSet<>();
        for (E symbol : gamma) {
            first.addAll(this.first.get(symbol));
            if (!nullable.get(symbol)) break;
        }
        return first;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Grammar:");
        for (int i = 0; i < rules.size(); i++) {
            result.append("\nr" + i + ": " + rules.get(i));
        }
        return result.toString();
    }
}

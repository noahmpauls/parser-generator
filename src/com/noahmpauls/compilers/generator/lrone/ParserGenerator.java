package com.noahmpauls.compilers.generator.lrone;

import com.noahmpauls.compilers.generator.Action;
import com.noahmpauls.compilers.generator.Grammar;
import com.noahmpauls.compilers.generator.S3;
import com.noahmpauls.compilers.generator.Production;
import com.noahmpauls.compilers.generator.ConcreteType;

import java.util.*;

public class ParserGenerator {
    public static void main(String[] args) {
        Grammar<S3> grammar = new Grammar<>(Arrays.asList(
                new Production<>(S3.SP, new S3[]{ S3.S, S3.EOP }),
                new Production<>(S3.S, new S3[]{ S3.V, S3.EQ, S3.E }),
                new Production<>(S3.S, new S3[]{ S3.E }),
                new Production<>(S3.E, new S3[]{ S3.V }),
                new Production<>(S3.V, new S3[]{ S3.x }),
                new Production<>(S3.V, new S3[]{ S3.STAR, S3.E })
        ));

        //Map<Integer, Map<LRE, Action>> table = createParseTable(grammar, 0, LRE.EOP);
        //System.out.println(visualizeTable(table, grammar));

        Grammar<ConcreteType> grammar3 = new Grammar<>(Arrays.asList(
                new Production<>(ConcreteType.START,
                        new ConcreteType[]{ ConcreteType.EXPR, ConcreteType.EOF }),
                new Production<>(ConcreteType.EXPR,
                        new ConcreteType[]{ ConcreteType.LITERAL, ConcreteType.P_EXPR }),
                new Production<>(ConcreteType.P_EXPR,
                        new ConcreteType[]{ ConcreteType.BIN_OP, ConcreteType.P_EXPR}),
                new Production<>(ConcreteType.P_EXPR,
                        new ConcreteType[]{ }),
                new Production<>(ConcreteType.EXPR,
                        new ConcreteType[]{ ConcreteType.MINUS, ConcreteType.EXPR, ConcreteType.P_EXPR}),
                new Production<>(ConcreteType.EXPR,
                        new ConcreteType[]{ ConcreteType.L_PAREN, ConcreteType.EXPR, ConcreteType.R_PAREN, ConcreteType.P_EXPR }),

                new Production<>(ConcreteType.BIN_OP,
                        new ConcreteType[]{ ConcreteType.ARITH_OP }),
//                new Production<>(ConcreteType.BIN_OP,
//                        new ConcreteType[]{ ConcreteType.REL_OP }),
//                new Production<>(ConcreteType.BIN_OP,
//                        new ConcreteType[]{ ConcreteType.EQ_OP }),
//                new Production<>(ConcreteType.BIN_OP,
//                        new ConcreteType[]{ ConcreteType.COND_OP }),

                new Production<>(ConcreteType.ARITH_OP,
                        new ConcreteType[]{ ConcreteType.PLUS })
//                new Production<>(ConcreteType.ARITH_OP,
//                        new ConcreteType[]{ ConcreteType.MINUS }),
//                new Production<>(ConcreteType.ARITH_OP,
//                        new ConcreteType[]{ ConcreteType.STAR }),
//                new Production<>(ConcreteType.ARITH_OP,
//                        new ConcreteType[]{ ConcreteType.FWD_SLASH }),
//                new Production<>(ConcreteType.ARITH_OP,
//                        new ConcreteType[]{ ConcreteType.PERCENT }),

//                new Production<>(ConcreteType.EQ_OP,
//                        new ConcreteType[]{ ConcreteType.EQ }),
//                new Production<>(ConcreteType.EQ_OP,
//                        new ConcreteType[]{ ConcreteType.NEQ }),
//
//                new Production<>(ConcreteType.COND_OP,
//                        new ConcreteType[]{ ConcreteType.AND}),
//                new Production<>(ConcreteType.COND_OP,
//                        new ConcreteType[]{ ConcreteType.OR})
        ));

        Map<Integer, Map<ConcreteType, Action>> table3 = createParseTable(grammar3, 0, ConcreteType.EOF);
        System.out.println(visualizeTable(table3, grammar3));
    }

    /**
     * Create a parse table for the inputted grammar.
     *
     * @param grammar a list of productions where indices correspond to rule
     *                numbers; a reduce action will specify the index of the
     *                rule to reduce by
     * @param start the rule to being state 0 from
     * @param <E> the enum type used to index the parse table
     * @return a parse table in the form of nested maps, where keys of the outer
     *  map correspond to states s0 - sN and keys of the inner map correspond to
     *  the desired grammar symbol.
     */
    public static <E extends Enum<E>> Map<Integer, Map<E, Action>> createParseTable(final Grammar<E> grammar, final int start, E eof) {
        // collections of items as states, where states.get(i) is state i
        List<Set<Item<E>>> states = new ArrayList<>();
        Set<Item<E>> startItem = new HashSet<>();
        startItem.add(new Item<>(grammar.rule(start), eof));
        states.add(makeClosure(startItem, grammar));

        // map of symbols to pairs of states to transition between as a result
        // of reading this symbol
        Map<E, List<List<Integer>>> edges = new HashMap<>();

        while(true) {
            List<Set<Item<E>>> newStates = new ArrayList<>();
            int newEdgeCount = 0;
            for (int i = 0; i < states.size(); i++) {
                Set<Item<E>> state = states.get(i);
                for (Item<E> item : state) {
                    if (item.remaining() == 0) continue;
                    E symbol = item.first();
                    // skip computing GOTO for EOF symbol
                    if (symbol.equals(eof)) continue;

                    // duplicate states could exist in "states" and this
                    // iteration's "newStates", so check for them
                    Set<Item<E>> newState = makeGoto(state, symbol, grammar);
                    // record the state to transition to
                    int stateIndex = stateIndex(states, newState);
                    int newStateIndex = stateIndex(newStates, newState);
                    if (newStateIndex >= 0) { // this has been found in this iteration
                        stateIndex = states.size() + newStateIndex + 1;
                    } else {
                        if (stateIndex < 0) { // this is a new state!
                            newStates.add(newState);
                            stateIndex = states.size() + newStates.size();
                        }
                    }

                    List<Integer> newEdge = new ArrayList<>(Arrays.asList(i, stateIndex));
                    if (!edgeExists(edges, newEdge, symbol)) {
                        newEdgeCount++;
                        if (!edges.containsKey(symbol))
                            edges.put(symbol, new ArrayList<>());
                        edges.get(symbol).add(newEdge);
                    }
                }
            }

            if (newStates.size() == 0 && newEdgeCount == 0) break;
            states.addAll(newStates);
        }

        for (int i = 0; i < states.size(); i++) {
            System.out.println(i + " ---------------");
            for (Item<E> item : states.get(i)){
                System.out.println("  " + item.toString());
            }
            System.out.println("");
        }

        // create the parse table; start with every state yielding an error
        Set<E> terminals = grammar.terminals();
        Set<E> nonTerminals = grammar.nonTerminals();

        Map<Integer, Map<E, Action>> parseTable = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            parseTable.put(i, new HashMap<>());
            for (E symbol : terminals)
                parseTable.get(i).put(symbol, Action.error());
            for (E symbol : nonTerminals)
                parseTable.get(i).put(symbol, Action.error());
        }

        // add shifts and gotos
        for (E symbol : edges.keySet()) {
            List<List<Integer>> edgeList = edges.get(symbol);
            for (List<Integer> itoj : edgeList) {
                int i = itoj.get(0);
                int j = itoj.get(1);
                if (nonTerminals.contains(symbol)) {
                    Action existingAction = parseTable.get(i).get(symbol);
                    if (!existingAction.type.equals(Action.ActionType.ERROR) && !existingAction.type.equals(Action.ActionType.GOTO)) {
                        System.out.println("rule conflict for state " + i + " symbol " + symbol.name() + ": replacing existing " + existingAction.type.name() + " with " + Action.ActionType.GOTO.name());
                    }
                    parseTable.get(i).put(symbol, Action.goTo(j));
                } else {
                    Action existingAction = parseTable.get(i).get(symbol);
                    if (!existingAction.type.equals(Action.ActionType.ERROR) && !existingAction.type.equals(Action.ActionType.SHIFT)) {
                        System.out.println("rule conflict for state " + i + " symbol " + symbol.name() + ": replacing existing " + existingAction.type.name() + " with " + Action.ActionType.SHIFT.name());
                    }
                    parseTable.get(i).put(symbol, Action.shift(j));
                }

            }
        }

        // add accepts and gotos
        for (int i = 0; i < states.size(); i++) {
            for (Item<E> item : states.get(i)) {
                if (item.remaining() > 0 && item.first().equals(eof)) {
                    Action existingAction = parseTable.get(i).get(eof);
                    if (!existingAction.type.equals(Action.ActionType.ERROR) && !existingAction.type.equals(Action.ActionType.ACCEPT)) {
                        System.out.println("rule conflict for state " + i + " symbol " + eof.name() + ": replacing existing " + existingAction.type.name() + " with " + Action.ActionType.ACCEPT.name());
                    }
                    parseTable.get(i).put(eof, Action.accept());
                }

                if (item.remaining() == 0) {
                    int reduceRule = grammar.ruleNum(item.production());
                    for (E symbol : terminals) {
                        if (item.lookahead().equals(symbol)){
                            Action existingAction = parseTable.get(i).get(symbol);
                            if (!existingAction.type.equals(Action.ActionType.ERROR) && !existingAction.type.equals(Action.ActionType.REDUCE)) {
                                System.out.println("rule conflict for state " + i + " symbol " + symbol.name() + ": replacing existing " + existingAction.type.name() + " with " + Action.ActionType.REDUCE.name());
                            }
                            parseTable.get(i).put(symbol, Action.reduce(reduceRule));
                        }

                    }
                }
            }
        }

        return parseTable;
    }

    private static <E extends Enum<E>> int stateIndex(List<Set<Item<E>>> existing, Set<Item<E>> toAdd) {
        for (int i = 0; i < existing.size(); i++) {
            Set<Item<E>> state = existing.get(i);
            if (state.equals(toAdd)) {
                return i;
            }
        }
        return -1;
    }

    private static <E extends Enum<E>> boolean edgeExists(Map<E, List<List<Integer>>> existing, List<Integer> toAdd, E key) {
        if (!existing.containsKey(key))
            return false;
        for (List<Integer> edge : existing.get(key)) {
            if (edge.equals(toAdd))
                return true;
        }
        return false;
    }

    /**
     * Create the closure of a set of items.
     *
     * @param items set of items
     * @param grammar the grammar to generate the closure from
     * @param <E> the enum type of the grammar symbols
     * @return CLOSURE(items)
     */
    private static <E extends Enum<E>> Set<Item<E>> makeClosure(final Set<Item<E>> items, final Grammar<E> grammar) {
        Set<Item<E>> closure = new HashSet<>(items);

        while(true) {
            Set<Item<E>> newItems = new HashSet<>();
            for (Item<E> item : closure) {
                if (item.remaining() == 0) continue;
                // productions starting with next symbol
                E start = item.first();
                // create new items
                Set<Production<E>> prods = grammar.withStart(start);
                for (Production<E> prod : prods) {
                    List<E> nextSymbols = item.shift().afterDot();
                    nextSymbols.add(item.lookahead());
                    for (E symbol : grammar.first(nextSymbols)) {
                        newItems.add(new Item<>(prod, symbol));
                    }
                }
            }
            // break if all "new" items are in closure already
            if (closure.containsAll(newItems)) break;
            closure.addAll(newItems);
        }

        return closure;
    }

    private static <E extends Enum<E>> Set<Item<E>> makeGoto(final Set<Item<E>> items, final E symbol, final Grammar<E> grammar) {
        Set<Item<E>> nextItems = new HashSet<>();
        for (Item<E> item : items) {
            if (item.remaining() > 0 && item.first().equals(symbol))
                nextItems.add(item.shift());
        }
        return makeClosure(nextItems, grammar);
    }

    public static <E extends Enum<E>> String visualizeTable(final Map<Integer, Map<E, Action>> parseTable, final Grammar<E> grammar) {
        List<E> symbols = new ArrayList<>(grammar.terminals());
        symbols.addAll(grammar.nonTerminals());

        StringBuilder result = new StringBuilder();
        List<Integer> colWidths = new ArrayList<>();
        for (E symbol : symbols)
            colWidths.add(Math.max(3, symbol.name().length()) + 1);

        final int yAxisWidth = 4;
        result.append(padRight("#", yAxisWidth));
        for (int i = 0; i < symbols.size(); i++) {
            result.append(padRight(symbols.get(i).name(), colWidths.get(i)));
        }
        result.append("\n");

        for (int i = 0; i < parseTable.keySet().size(); i++) {
            result.append(padLeft(i + " ", yAxisWidth));
            for (int j = 0; j < symbols.size(); j++) {
                E symbol = symbols.get(j);
                String action = parseTable.get(i).get(symbol).toString();
                result.append(padRight(action, colWidths.get(j)));
            }
            result.append("\n");
        }

        return result.toString().trim();
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }
}
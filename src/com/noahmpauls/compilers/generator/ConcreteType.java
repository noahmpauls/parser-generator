package com.noahmpauls.compilers.generator;

public enum ConcreteType {
    ////////////////////////////////////////////////////////////////////////////
    // Non-terminals
    ////////////////////////////////////////////////////////////////////////////
    PROGRAM,

    START,
    EXPR,
    BIN_OP,
    ARITH_OP,
    P_EXPR,

    IMPORT_DECL,

    FIELD_DECL,
    FIELD_IDENTIFIER,
    COMMA_FIELD_IDENTIFIER,

    METHOD_DECL,
    METHOD_PARAM,
    METHOD_PARAM_DECL,
    COMMA_METHOD_PARAM,

    BLOCK,

    STATEMENT,
    IF_STATEMENT,
    FOR_LOOP,
    FOR_UPDATE,
    WHILE_LOOP,
    RETURN_STATEMENT,
    BREAK_STATEMENT,
    CONTINUE_STATEMENT,
    LOCATION,
    INCREMENT,
    ASSIGN_EXPR,
    ASSIGN_OP,
    COMPOUND_ASSIGN_OP,

    METHOD_CALL,
    METHOD_ARGS,

    EXPRESSION,
    OR_EXPR,
    AND_EXPR,
    EQ_EXPR,
    REL_EXPR,
    ADD_EXPR,
    MUL_EXPR,
    NOT_EXPR,
    NEG_EXPR,
    TERM_EXPR,

    ADD_OP,
    MUL_OP,
    REL_OP,
    EQ_OP,
    COND_OP,

    LITERAL,
    INT_LITERAL,
    BOOL_LITERAL,

    ////////////////////////////////////////////////////////////////////////////
    // Terminals
    ////////////////////////////////////////////////////////////////////////////


    // keywords
    // ------------------------------
    IMPORT,
    VOID,
    INT,
    BOOL,
    TRUE,
    FALSE,
    IF,
    ELSE,
    FOR,
    WHILE,
    RETURN,
    BREAK,
    CONTINUE,
    LEN,

    // complex tokens
    // ------------------------------
    IDENTIFIER,
    CHAR_LITERAL,
    STRING_LITERAL,
    HEX_LITERAL,
    DEC_LITERAL,

    // assign, compare, operate
    // ------------------------------
    ASSIGN,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    INCR,
    DECR,

    EQ,
    NEQ,
    LESS,
    GREATER,
    LEQ,
    GEQ,

    AND,
    OR,
    PLUS,
    MINUS,
    STAR,
    BANG,
    PERCENT,
    FWD_SLASH,

    // brackets and other symbols
    // ------------------------------
    L_SQUARE,
    R_SQUARE,
    L_PAREN,
    R_PAREN,
    L_CURLY,
    R_CURLY,
    SEMI,
    COMMA,

    EOF;
}

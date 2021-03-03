# parser-generator

My attempt to create a shift-reduce parser generator in Java.

## Example

Given the following grammar (see Tiger Book pg. 65, Grammar 3.26):

```
SP -> S EOP
S -> V EQ E
S-> E
E -> V
V -> x
V -> STAR E
```

The LR(1) ParserGenerator generates the following table:

```
    STAR EQ  x   EOP SP  E   S   V   
  0 s2       s3          g1  g5  g4  
  1              r2                  
  2 s2       s3          g6      g7  
  3      r4      r4                  
  4      s8      r3                  
  5              acc                 
  6      r5      r5                  
  7      r3      r3                  
  8 s12      s10         g9      g11 
  9              r1                  
 10              r4                  
 11              r3                  
 12 s12      s10         g13     g11 
 13              r5
```

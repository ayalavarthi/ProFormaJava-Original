# ProFormaJava

ProFormaJava has Java utility classes that parse strings to produce a ProForma string. 

## Usage Examples

```java
# returns 'SGRGKQGGKARAKAKTRSSRAGLQFPVGRVHRLLRKGNYAERVGAGAPVYLAAVLEYLTAEILELAGNAARDNKKTRIIPRHLQLAIRNDEELNKLLGKVTIAQGGVLPNIQAVLLPKKTESHHKAKGK'
ProFormaParser parser = new ProFormaParser();
ProFormaTerm term = parser.parseString("[Acetyl]-S[Phospho|+79.966331]GRGK[Acetyl|UNIMOD:1|+42.010565]QGGKARAKAKTRSSRAGLQFPVGRVHRLLRKGNYAERVGAGAPVYLAAVLEYLTAEILELAGNAARDNKKTRIIPRHLQLAIRNDEELNKLLGKVTIAQGGVLPNIQAVLLPKKT[UNIMOD:21]ESHHKAKGK");
System.out.println(term.sequence);

# returns 'MTLFQLREHWFVYKDDEKLTAFRNKSMLFQRELRPNEEVTWK'
term = parser.parseString("MTLFQLREHWFVYKDDEKLTAFRNK[p-adenosine|R:N6-(phospho-5'-adenosine)-L-lysine| RESID:AA0227| MOD:00232| N6AMPLys]SMLFQRELRPNEEVTWK");
System.out.println(term.sequence);

# returns 'MTLFQLDEKLTAFRNKSMLFQRELRPNEEVTWK'
term = parser.parseString("MTLFQLDEKLTA[-37.995001|info:unknown modification]FRNKSMLFQRELRPNEEVTWK");
System.out.println(term.sequence);
```

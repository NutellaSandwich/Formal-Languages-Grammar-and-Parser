/* Assignment.java */
/* Generated By:JavaCC: Do not edit this line. Assignment.java */
// Standard library imports
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;


// JavaCC Parser class
public class Assignment implements AssignmentConstants {

    public static void main(String args []) {
        // Instantain parser object on the PLM program
        Assignment parser = new Assignment(System.in);

        try {
            // Attempt to parse and then evaluate the PLM program
            int result = parser.program();

            // Report evaluation
            System.out.println("PASS");
            System.out.println(Program.diverges() ? "DIVERGENCE" : result);

        } catch (Throwable t) {
            // Print failure to the standard output stream
            System.out.println("FAIL");
            int line = 0;
            String message = "Unclassified parse error";

            try {
                // Pass the Throwable to the specific handlers
                throw t;

            } catch (PLMParseException e) {
                // Decode PLMParseException
                line = e.getLine();
                message = e.getMessage();

            } catch(ParseException e) {
                // Decode ParseException
                line = e.currentToken.next.beginLine;
                message = decodeParseException(e);

            } catch(TokenMgrError e) {
                // Decode TokenMgrError
                Matcher matcher = Pattern.compile("line \u005c\u005cd+").matcher(e.getMessage());
                if (matcher.find())
                    line = Integer.parseInt(matcher.group().replace("line ", ""));

                message = "Illegal symbol";
            }

            // Output line number and message to error stream
            System.err.println(line);
            System.err.println(message);
        }
    }

    private static String decodeParseException(ParseException e) {
        int at = e.currentToken.kind;
        int found = e.currentToken.next.kind;
        String str = e.currentToken.image + e.currentToken.next.image + "...";

        // Form the set of expected token kinds
        Set<Integer> expected = new HashSet();
        for (int[] i : e.expectedTokenSequences) {
            expected.add(i[0]);
        }

        // Determine an appropriate error message from the caught
        // tokens via case analysis

        if (found == SPACE)
            return "Unexpected space";

        if (expected.contains(DEF))
            return "Missing keyword DEF";

        if (expected.contains(SPACE)) {
            switch (found) {
                case LBRACE:
                case RBRACE:
                case SCOLON:
                case NEWLINE:
                case EOF:
                    return "Missing whitespace";
            }
        }

        if (expected.contains(FUNCTION_NAME) && expected.contains(MAIN)) {
            if (found == DEF)
                return "DEF is not a valid function name";

            return "Missing function name";
        }

        if ((at == FUNCTION_NAME || at == MAIN) && expected.contains(SPACE))
            return "Invalid function name " + str;

        if (expected.contains(PARAMETER_NAME) && expected.size() == 1)
            return "Missing parameter name";

        if (at == PARAMETER_NAME) {
            if (expected.contains(SPACE) && expected.size() == 1) {
                return "Invalid parameter name " + str;
            } else {
                return "Invalid parameter reference " + str;
            }
        }

        if (expected.contains(LBRACE)) {
            if (found == PARAMETER_NAME) {
                return "MAIN definition should have no parameter";
            } else {
                return "Missing {";
            }
        }

        if (at == FUNCTION_NAME && expected.contains(LBRACK))
            return "Invalid function call " + str;

        if (expected.contains(INTEGER_LITERAL) && expected.contains(PARAMETER_NAME) && expected.contains(FUNCTION_NAME)) {
            if (found == MAIN)
                return "Cannot call MAIN";

            if (at == LBRACK) {
                return "Missing function call argument";
            } else if (at == SPACE) {
                switch (found) {
                    case RBRACE:
                    case NEWLINE:
                    case EOF:
                        return "Missing function body";
                    default:
                        return "Missing Literal";
                }
            }
        }

        switch (at) {
            case INTEGER_LITERAL:
            case PARAMETER_NAME:
            case FUNCTION_NAME:
                if (found == PLUS || found == MUL) {
                    return "Missing operand " + str;
                }
        }

        if (expected.contains(RBRACK))
            return "Missing )";

        if (expected.contains(RBRACE))
            return "Missing }";

        if (expected.contains(SCOLON))
            return "Missing ;";

        switch (found) {
            case NEWLINE:
                return "Unexpected new line";
            case EOF:
                return "Unexpected end of file";
        }

        // Error type could not be inferred, default to this
        return "Unclassified parse error";
    }

// factor/literal non-terminal
  static final public IEvaluate factor(String param) throws ParseException, PLMParseException {Token t; IEvaluate callArg;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case INTEGER_LITERAL:{
      t = jj_consume_token(INTEGER_LITERAL);
// Return the constant value boxed as a Literal
            {if ("" != null) return new Literal(Integer.parseInt(t.image));}
      break;
      }
    case PARAMETER_NAME:{
      t = jj_consume_token(PARAMETER_NAME);
String var = t.image;
            // Check the variable matches the parent functions parameter
            if (!var.equals(param)) {
                {if (true) throw new PLMParseException("Undefined variable " + var, t.beginLine);}
            }
            // Return the parameter reference boxed as a Literal
            {if ("" != null) return new Literal(param);}
      break;
      }
    case FUNCTION_NAME:{
      t = jj_consume_token(FUNCTION_NAME);
      jj_consume_token(LBRACK);
      callArg = expr(param);
      jj_consume_token(RBRACK);
// Return the function call boxed as a Literal
            {if ("" != null) return new Literal(t.image, callArg, t.beginLine);}
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

// term non-terminal
  static final public IEvaluate term(String p) throws ParseException, PLMParseException {IEvaluate lhs, rhs; List<IEvaluate> operands = new ArrayList();
    lhs = factor(p);
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
      jj_consume_token(MUL);
      rhs = term(p);
operands.add(rhs);
    }
// Return an object respresentation of a multiplicative fold on factors
        {if ("" != null) return new BinFold(lhs, operands, BinFold.Type.MUL);}
    throw new Error("Missing return statement in function");
  }

// expression non-terminal
  static final public IEvaluate expr(String p) throws ParseException, PLMParseException {IEvaluate lhs, rhs; List<IEvaluate> operands = new ArrayList();
    lhs = term(p);
    label_2:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_2;
      }
      jj_consume_token(PLUS);
      rhs = expr(p);
operands.add(rhs);
    }
// Return an object respresentation of a addative fold on terms
        {if ("" != null) return new BinFold(lhs, operands, BinFold.Type.PLUS);}
    throw new Error("Missing return statement in function");
  }

// function non-terminal
  static final public Function function() throws ParseException, PLMParseException {Token t1, t2 = new Token(); IEvaluate body;
    jj_consume_token(DEF);
    jj_consume_token(SPACE);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case FUNCTION_NAME:{
      t1 = jj_consume_token(FUNCTION_NAME);
      jj_consume_token(SPACE);
      t2 = jj_consume_token(PARAMETER_NAME);
      break;
      }
    case MAIN:{
      t1 = jj_consume_token(MAIN);
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(SPACE);
    jj_consume_token(LBRACE);
    jj_consume_token(SPACE);
    body = expr(t2.image);
    jj_consume_token(SPACE);
    jj_consume_token(RBRACE);
    jj_consume_token(SPACE);
    jj_consume_token(SCOLON);
    jj_consume_token(NEWLINE);
// Define a new function using the name, parameter, body and line number
        {if ("" != null) return new Function(t1.image, t2.image, body, t1.beginLine);}
    throw new Error("Missing return statement in function");
  }

// program root non-terminal
  static final public int program() throws ParseException, PLMParseException {IEvaluate body; Function f;
    label_3:
    while (true) {
      f = function();
Program.registerFunction(f);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case DEF:{
        ;
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        break label_3;
      }
    }
    jj_consume_token(0);
// Validate the program
        Program.validate();
        // After parsing the whole program, run the main body with a dummy arg
        {if ("" != null) return Program.call("MAIN", Integer.MIN_VALUE);}
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_3_2()
 {
    if (jj_scan_token(PLUS)) return true;
    if (jj_3R_5()) return true;
    return false;
  }

  static private boolean jj_3R_7()
 {
    if (jj_scan_token(INTEGER_LITERAL)) return true;
    return false;
  }

  static private boolean jj_3R_6()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_7()) {
    jj_scanpos = xsp;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) return true;
    }
    }
    return false;
  }

  static private boolean jj_3_1()
 {
    if (jj_scan_token(MUL)) return true;
    if (jj_3R_4()) return true;
    return false;
  }

  static private boolean jj_3R_5()
 {
    if (jj_3R_4()) return true;
    return false;
  }

  static private boolean jj_3R_8()
 {
    if (jj_scan_token(PARAMETER_NAME)) return true;
    return false;
  }

  static private boolean jj_3R_9()
 {
    if (jj_scan_token(FUNCTION_NAME)) return true;
    return false;
  }

  static private boolean jj_3R_4()
 {
    if (jj_3R_6()) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public AssignmentTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[3];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x7000,0x2004,0x2,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[2];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Assignment(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Assignment(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new AssignmentTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Assignment(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new AssignmentTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Assignment(AssignmentTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(AssignmentTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[15];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 3; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 15; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}


// Exception wrapper for tracking PLM line number
class PLMParseException extends Exception {
    private final int line;

    // Constructor
    public PLMParseException(String message, int line) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}


// Defines a expression that can be evaluated given the host functions argument
interface IEvaluate {
    public int evaluate(int arg) throws PLMParseException;
}


// Literal expression construct
class Literal implements IEvaluate {
    private int constant;
    private String callName;
    private IEvaluate callArg;
    private Type type;

    // Constructor
    public Literal(int constant) {
        this.constant = constant;
        this.type = Type.CONST;
    }

    // Constructor
    public Literal(String param) {
        this.type = Type.PARAM;
    }

    // Constructor
    public Literal(String callName, IEvaluate callArg, int line) {
        this.callName = callName;
        this.callArg = callArg;
        this.type = Type.CALL;
        // Acknowledge this function call
        Program.registerCall(callName, line);
    }

    @Override
    public int evaluate(int arg) throws PLMParseException {
        // Provide a different evaluation based on literal type
        switch(type) {
            case CONST:
                // Return the boxed constant
                return constant;
            case PARAM:
                // The literal references the parameter, return the argument
                return arg;
            case CALL:
                // Evaluate the argument and call the function with the value
                int value = callArg.evaluate(arg);
                return Program.call(callName, value);
            default:
                throw new Error("FATAL: null literal type");
        }
    }

    // Enumeration of possible Literal types
    private enum Type {
        CONST, PARAM, CALL
    }
}


// Binary operation fold expression construct
class BinFold implements IEvaluate {
    private final IEvaluate base;
    private final List<IEvaluate> operands;
    private final Type type;

    // Constructor
    public BinFold(IEvaluate base, List<IEvaluate> operands, Type type) {
        this.base = base;
        this.operands = operands;
        this.type = type;
    }

    @Override
    public int evaluate(int arg) throws PLMParseException {
        // Evaluate the base into result
        int result = base.evaluate(arg);

        // Fold the other terms into result using the appropriate binary op
        for (IEvaluate o : operands){
            switch (type) {
                case MUL:
                    result *= o.evaluate(arg);
                    break;
                case PLUS:
                    result += o.evaluate(arg);
                    break;
                default:
                    throw new Error("FATAL: null BinFold type");
            }
        }
        return result;
    }

    // Enumeration of possible BinFold types
    enum Type {
        MUL, PLUS
    }
}


// Function construct
class Function implements IEvaluate {
    public final String name;
    public final String param;
    public final IEvaluate body;
    public final int line;

    // Constructor
    public Function(String name, String param, IEvaluate body, int line) {
        this.name = name;
        this.param = param;
        this.body = body;
        this.line = line;
    }

    @Override
    public int evaluate(int arg) throws PLMParseException {
        return body.evaluate(arg);
    }
}

class Program {
    private static final Map<String, Function> functions = new HashMap();
    private static final Map<String, Integer> undefinedCalls = new HashMap();
    private static final Stack<String> callStack = new Stack();
    private static boolean divergence = false;

    // Call a defined function
    public static int call(String name, int arg) throws PLMParseException {
        // Check for a recursive stack trace and skip next call if found
        if (Program.callStack.contains(name)) {
            divergence = true;
            return arg;
        }

        // Get the function
        Function function = Program.functions.get(name);

        // Evaluate the function call and regulate the call stack
        Program.callStack.push(name);
        int result = function.evaluate(arg);
        Program.callStack.pop();

        return result;
    }

    // Register a distinct function definition
    public static void registerFunction(Function f) throws PLMParseException {
        // Check the definition is distinct
        if (Program.functions.containsKey(f.name)) {
            throw new PLMParseException("Duplicate function definition " + f.name, f.line);
        } else {
            // Register a function object in the programs function map
            Program.functions.put(f.name, f);
            // Resolve any undefined calls to the function
            undefinedCalls.remove(f.name);
        }
    }

    // Register a potentially unmatched function call
    public static void registerCall(String name, int line) {
        // If the function is undefined, store the call
        if (!Program.functions.containsKey(name)) {
            undefinedCalls.putIfAbsent(name, line);
        }
    }

    // Validate the program
    public static void validate() throws PLMParseException {
        // If the MAIN function has not been defined, throw an error
        if (!Program.functions.containsKey("MAIN")) {
            throw new PLMParseException("Undefined MAIN function", 0);
        }

        // If there is a call to an undefined function, throw an error
        if (!undefinedCalls.isEmpty()) {
            // Extract first undefined function call and its line number
            Map.Entry entry = undefinedCalls.entrySet().iterator().next();

            throw new PLMParseException("Undefined function " + entry.getKey(), (int) entry.getValue());
        }
    }

    // Does the program diverge
    public static boolean diverges() {
        return divergence;
    }
}

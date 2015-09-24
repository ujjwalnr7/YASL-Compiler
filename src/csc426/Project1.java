package csc426;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;

/**
 * Main class for Project 2 -- 
 * Unfortunately its still called Project 1 instead of something more conventional because refactoring seems to break things
 * Scanner for a Subset of YASL (Fall 2015). Scans
 * tokens from standard input and prints the token stream to standard output.
 * 
 * @author Ujjwal Nair
 */
public class Project1 {
	private static int[] SWITCH_TABLETokenType;

	public static void main(String[] args) {
        Token num;
        Wrapper wrapper = new Wrapper(new Scanner(new BufferedReader(new InputStreamReader(System.in))));
        HashMap<String, String> constant = new HashMap<String, String>();
        wrapper.match(TokenType.PROGRAM);
        wrapper.match(TokenType.ID);
        wrapper.match(TokenType.SEMI);

        
        while (wrapper.check(TokenType.CONST)) 
        {
            wrapper.skip();
            Token id = wrapper.match(TokenType.ID);
            wrapper.match(TokenType.ASSIGN);
            num = wrapper.match(TokenType.NUM);
            wrapper.match(TokenType.SEMI);
            constant.put(id.lexeme, num.lexeme);
        }
        
        wrapper.match(TokenType.BEGIN);

        while (wrapper.check(TokenType.PRINT)) 
        {
            wrapper.skip();
            Stack<TokenType> stack = new Stack<TokenType>();
            stack.push(TokenType.PRINT);
            
            while (!wrapper.check(TokenType.SEMI)) 
            {
                if (wrapper.check(TokenType.NUM)) 
                {
                    num = wrapper.skip();
                    System.out.println(num.lexeme);
                    continue;
                }
                
                if (wrapper.check(TokenType.ID)) 
                {
                    Token id = wrapper.skip();
                    if (constant.containsKey(id.lexeme)) 
                    {
                        System.out.println((String)constant.get(id.lexeme));
                        continue;
                    }
                    System.err.println("Error: Unknown identifier " + id.lexeme);
                    System.exit(1);
                    continue;
                }
                
                Token op = wrapper.skip();
                int primacy = Project1.primacy(op.type);
                while (!(stack.isEmpty() || primacy > Project1.primacy((TokenType)stack.peek()))) 
                {
                    System.out.println(Project1.operator((TokenType)stack.pop()));
                }
                stack.push(op.type);
            }
            wrapper.skip();
            while (!stack.isEmpty()) 
            {
                System.out.println(Project1.operator((TokenType)stack.pop()));
            }
        }
        
        wrapper.match(TokenType.END);
        wrapper.match(TokenType.PERIOD);
        wrapper.match(TokenType.EOF);
    
	}
	
	private static String operator(TokenType type) 
	{
        switch (Project1.$SWITCH_TABLE$TokenType()[type.ordinal()]) 
        {
            case 6: 
            {
                return "PRINT";
            }
            
            case 12: 
            {
                return "+";
            }
            
            case 13: 
            {
                return "-";
            }
            
            case 14: 
            {
                return "*";
            }
            
            case 8: 
            {
                return "DIV";
            }
            
            case 9: 
            {
                return "MOD";
            }
        }
        System.err.println("Error: Expected an operator; found " + (Object)type);
        System.exit(1);
        return null;
    }

    private static int primacy(TokenType type) 
    {
        switch (Project1.$SWITCH_TABLE$TokenType()[type.ordinal()]) 
        {
            case 6: 
            {
                return 0;
            }
            
            case 12: 
            
            case 13: 
            {
                return 1;
            }
            
            case 8: 
            
            case 9: 
            
            case 14: 
            {
                return 2;
            }
        }
        System.err.println("Error: Expected an operator; found " + (Object)type);
        System.exit(1);
        return -1;
    }

    static int[] $SWITCH_TABLE$TokenType() 
    {
        int[] arrn;
        int[] arrn2 = SWITCH_TABLETokenType;
        
        if (arrn2 != null) 
        {
            return arrn2;
        }
        arrn = new int[TokenType.values().length];
        
        try 
        {
            arrn[TokenType.ASSIGN.ordinal()] = 15;
        }
        
        catch (NoSuchFieldError v1) {}
        
        try 
        {
            arrn[TokenType.BEGIN.ordinal()] = 5;
        }
        
        catch (NoSuchFieldError v2) {}
        
        try 
        {
            arrn[TokenType.CONST.ordinal()] = 4;
        }
        
        catch (NoSuchFieldError v3) {}
        
        try 
        {
            arrn[TokenType.DIV.ordinal()] = 8;
        }
        
        catch (NoSuchFieldError v4) {}
        
        try 
        {
            arrn[TokenType.END.ordinal()] = 7;
        }
        
        catch (NoSuchFieldError v5) {}
        
        try 
        {
            arrn[TokenType.EOF.ordinal()] = 16;
        }
        
        catch (NoSuchFieldError v6) {}
        
        try 
        {
            arrn[TokenType.ID.ordinal()] = 1;
        }
        
        catch (NoSuchFieldError v7) {}
        
        try 
        {
            arrn[TokenType.MINUS.ordinal()] = 13;
        }
        
        catch (NoSuchFieldError v8) {}
        
        try 
        {
            arrn[TokenType.MOD.ordinal()] = 9;
        }
        
        catch (NoSuchFieldError v9) {}
        
        try 
        {
            arrn[TokenType.NUM.ordinal()] = 2;
        }
        
        catch (NoSuchFieldError v10) {}
        
        try 
        {
            arrn[TokenType.PERIOD.ordinal()] = 11;
        }
        
        catch (NoSuchFieldError v11) {}
        
        try 
        {
            arrn[TokenType.PLUS.ordinal()] = 12;
        }
        
        catch (NoSuchFieldError v12) {}
        
        try 
        {
            arrn[TokenType.PRINT.ordinal()] = 6;
        }
        
        catch (NoSuchFieldError v13) {}
        
        try 
        {
            arrn[TokenType.PROGRAM.ordinal()] = 3;
        }
        
        catch (NoSuchFieldError v14) {}
        
        try 
        {
            arrn[TokenType.SEMI.ordinal()] = 10;
        }
        
        catch (NoSuchFieldError v15) {}
        
        try 
        {
            arrn[TokenType.STAR.ordinal()] = 14;
        }
        
        catch (NoSuchFieldError v16) {}
        
        SWITCH_TABLETokenType = arrn;
        return SWITCH_TABLETokenType;
    }
	
}

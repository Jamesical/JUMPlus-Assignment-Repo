package com.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

//	Password must contain at least one digit [0-9].
//	Password must contain at least one lowercase letter [a-z].
//	Password must contain at least one uppercase letter [A-Z].
//	Password must contain at least one special character i.e. "! @ # & ( )"
//	Password must contain min 8 characters and a max of 20 characters.
	
	 // digit + lowercase char + uppercase char + punctuation + symbol
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    
    private static final String EMAIL_PATTERN =
            ".+\\@.+\\..+";

    private static final Pattern passPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isPassValid(final String password) {
        Matcher matcher = passPattern.matcher(password);
        return matcher.matches();
    }
    
    public static boolean isEmailValid(final String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
	
	
}

//^                                 # start of line
//(?=.*[0-9])                       # positive lookahead, digit [0-9]
//(?=.*[a-z])                       # positive lookahead, one lowercase character [a-z]
//(?=.*[A-Z])                       # positive lookahead, one uppercase character [A-Z]
//(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]) # positive lookahead, one of the special character in this [..]
//.                                 # matches anything
//{8,20}                            # length at least 8 characters and maximum of 20 characters
//$                                   # end of line
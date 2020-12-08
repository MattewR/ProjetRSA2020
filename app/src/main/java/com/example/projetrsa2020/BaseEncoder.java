package com.example.projetrsa2020;

/*-------------------------------------------------
 * Librairie fait par quelqu'un sur stack overflow
 * ------------------------------------------------
 *
 *
 * Détails:
 *
 * Licensced for commercial or non-commercial use, modification and
 * redistribution provided the source code retain this license header and
 * attribution.  Written to answer to Stack Overflow question 15735079.  See
 * https://stackoverflow.com/questions/15735079/
 * to see the original questions and ( if this code has been modified ) to see
 * the original source code.  This license does not supercede any licencing
 * requirements set forth by StackOverflow.  In the event of a disagreement
 * between this license and the terms of use set forth by StackOverflow, the
 * terms of use and/or license set forth by StackOverflow shall be considered
 * the governing terms and license.
 */

import java.math.BigInteger;

/**
 * conversion routines to support
 * <pre>
 * BaseEncoder.baseConversion("94d6b", 16, 2));
 * </pre> allowing conversions between numbering systems of base 2 to base 3263
 * inclusive, with the following caveats:<ul>
 * <li> WARNING: BASE64 numbers created or parsed with this encoder are not
 * compatible with a standard base 64 encoder, and </li>
 * <li> WARNING: this class does not currently support unicode, or if it does
 * that's only by accident and it most likely does not support characters that
 * require more than one codepoint.</li>
 * </ul>
 * . to convert between two non-standard numbering systems, use two BaseEncoder
 * objects e.g.
 * <pre>
 *
 *      String numberBase64 = "1X3dt+4N";
 *      String numberBase16 = new BaseEncoder(16).fromBase10(new BaseEncoder(
 *          "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+/")
 *          .toBase10(numberBase64));
 *
 * </pre>
 *
 * ://stackoverflow.com/questions/15735079/
 */
public class BaseEncoder {

    public static void main(String[] args) {
        sout("(\"10011100\", 2,  16)=" + baseConversion("10011100", 2, 16));
        sout("(\"9c\",      16,   2)=" + baseConversion("9c", 16, 2));
        sout("(\"609643\",  10,  64)=" + baseConversion("609643", 10, 64));
        sout("(\"33773377\",10, 100)=" + baseConversion("33773377", 10, 100));
        sout("(\"18018018\",10,1000)=" + baseConversion("18018018", 10, 1000));
        // test();
    }

    private static void sout(String output) {
        System.out.println("\tbaseConversion"
                + output.replace("=", " = \"") + "\"");
    }

    /**
     * this is the method that satisfies the criteria set forth by the original
     * question at https://stackoverflow.com/questions/15735079/ .
     *
     * @param fromNumber
     * @param fromBase
     * @param toBase
     * @return
     */
    public static String baseConversion(
            String fromNumber, int fromBase, int toBase) {
        final BigInteger numberBase10 = fromBase == 10
                ? new BigInteger(fromNumber)
                : parseBigInteger(fromNumber, fromBase);
        // System.out.println("org.myteam.util.baseConversion():"
        //          + " numberBase10 = " + numberBase10);
        return toBase == 10 ? numberBase10.toString()
                : toString(numberBase10, toBase);
    }

    /**
     * Simple test to validate conversion functions. Should be converted to
     * support whatever unit tests or automated test suite your organization
     * employs. No return value.
     *
     * @throws IllegalStateException if any test fails, and aborts all tests.
     */
    public static void test() throws IllegalStateException {
        final int level1 = 100;
        final int level2 = 525;
        final int level3 = 1000;
        final int maxlvl = 3263;
        for (int radix = 2; radix < maxlvl;) {
            test(radix);
            radix += (radix < level1) ? 1
                    : (radix < level2) ? 17
                    : (radix < level3) ? 43
                    : 139;
        }
        test(3263);
        System.out.println("taurus.BaseEncoder.test(): all tests passed.");
    }

    private static void test(int radix) throws IllegalStateException {
        final BigInteger level1 = BigInteger.valueOf(radix);
        final BigInteger level2 = level1.multiply(level1);
        final BigInteger level3 = level2.multiply(level1);
        final BigInteger maxlvl = level3.multiply(level1);
        final BigInteger increment1 = BigInteger.ONE;
        final BigInteger increment2 = level1.add(BigInteger.ONE);
        final BigInteger increment3 = level2
                .add(BigInteger.ONE).add(BigInteger.ONE).add(BigInteger.ONE);
        final BigInteger increment4 = level3.add(BigInteger.valueOf(17));
        final int exitLvl = 5;
        int prevLvl = 1;
        BigInteger iTest = BigInteger.ZERO;
        while (true) {
            Throwable err = null;
            String radixEncoded = "(conversion to base " + radix + " failed)";
            String backToBase10 = "(conversion back to base 10 failed)";
            try {
                radixEncoded = baseConversion("" + iTest, 10, radix);
                backToBase10 = baseConversion(radixEncoded, radix, 10);
            } catch (Throwable ex) {
                err = ex;
            }
            if (err != null || !backToBase10.equals("" + iTest)) {
                System.out.println("FAIL: "
                        + iTest + " base " + radix + " = " + radixEncoded);
                System.out.println("FAIL: "
                        + radixEncoded + " base 10 = " + backToBase10
                        + " (should be " + iTest + ")");
                throw new IllegalStateException("Test failed. base 10 '" + iTest
                        + "' conversion to/from base" + radix + ".", err);
            }
            int lvl = (prevLvl == 1 && iTest.compareTo(level1) >= 0) ? 2
                    : (prevLvl == 2 && iTest.compareTo(level2) >= 0) ? 3
                    : (prevLvl == 3 && iTest.compareTo(level3) >= 0) ? 4
                    : (prevLvl == 4 && iTest.compareTo(maxlvl) >= 0) ? exitLvl
                    : prevLvl;
            final BigInteger increment
                    = (lvl == 1) ? increment1
                    : (lvl == 2) ? increment2
                    : (lvl == 3) ? increment3
                    : (lvl == 4) ? increment4
                    : BigInteger.ZERO;
            iTest = iTest.add(increment);
            if (prevLvl != lvl) {
                if (lvl == exitLvl && (radix % 56 == 0 || radix > 2700)) {
                    System.out.println("test():" + " radix " + radix
                            + " level " + prevLvl + " test passed.");
                }
            }
            if (lvl == exitLvl) {
                break;
            }
            prevLvl = lvl;
        }
    }

    /**
     * <pre>
     * String tenAsOctal = toString(BigInteger.TEN, 8); // returns "12"
     * </pre>.
     *
     * @param numberBase10
     * @param radix
     * @return
     */
    public static String toString(BigInteger numberBase10, int radix) {
        return new BaseEncoder(radix).fromBase10(numberBase10);
    }

    /**
     * <pre>
     * String tenAsOctal = toString(BigInteger.TEN, "01234567"); // returns "12"
     * </pre>.
     *
     * @param numberBase10
     * @param digits
     * @return
     */
    public static String toString(BigInteger numberBase10, String digits) {
        return new BaseEncoder(digits).fromBase10(numberBase10);
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * String tenAsDecimal = parseBigInteger(tenAsOctal, 8);
     * System.out.println(tenAsDecimal); // "10"
     * </pre>.
     *
     * @param numberEncoded
     * @param radix
     * @return
     */
    public static BigInteger parseBigInteger(String numberEncoded, int radix) {
        return new BaseEncoder(radix).toBase10(numberEncoded);
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * String tenAsDecimal = parseBigInteger(tenAsOctal, "01234567");
     * System.out.println(tenAsDecimal); // "10"
     * </pre>.
     *
     * @param numberEncoded
     * @param digits
     * @return
     */
    public static BigInteger parseBigInteger(
            String numberEncoded, String digits) {
        return new BaseEncoder(digits).toBase10(numberEncoded);
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * int tenAsDecimal = parseInt(tenAsOctal, 8);
     * System.out.println(tenAsDecimal); // 10
     * </pre>.
     *
     * @param numberEncoded
     * @param radix
     * @return
     */
    public static int parseInt(String numberEncoded, int radix) {
        return new BaseEncoder(radix).toBase10(numberEncoded).intValue();
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * int tenAsDecimal = parseInt(tenAsOctal, "01234567");
     * System.out.println(tenAsDecimal); // 10
     * </pre>.
     *
     * @param numberEncoded
     * @param digits
     * @return
     */
    public static int parseInt(String numberEncoded, String digits) {
        return new BaseEncoder(digits).toBase10(numberEncoded).intValue();
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * long tenAsDecimal = parseLong(tenAsOctal, 8);
     * System.out.prlongln(tenAsDecimal); // 10
     * </pre>.
     *
     * @param numberEncoded
     * @param radix
     * @return
     */
    public static long parseLong(String numberEncoded, int radix) {
        return new BaseEncoder(radix).toBase10(numberEncoded).longValue();
    }

    /**
     * <pre>
     * String tenAsOctal = "12"; // ("1" x 8^1) + ("2" x 8^0) = 10
     * long tenAsDecimal = parseLong(tenAsOctal, "01234567");
     * System.out.prlongln(tenAsDecimal); // 10
     * </pre>.
     *
     * @param numberEncoded
     * @param digits
     * @return
     */
    public static long parseLong(String numberEncoded, String digits) {
        return new BaseEncoder(digits).toBase10(numberEncoded).longValue();
    }

    /**
     * each character in this string represents one digit in the base-X
     * numbering system supported by this instance, where X = the length of the
     * string. e.g.
     * <pre>
     *     base  2 (binary)     digits = "01"
     *     base  8 (octal)      digits = "01234567"
     *     base 10 (decimal)    digits = "0123456789"
     *     base 16 (hexdecimal) digits = "0123456789ABCDEF"
     * </pre> digits follow this pattern until base 64. a somewhat arbitrary
     * character system is utilized to support base 65 to base 3263.
     */
    private final String digits;

    /**
     * specify a numbering system between base 2 and base 64 inclusive
     * <pre>
     * String fiveAsBinary = new BaseEncoder(2).fromBase10(5);
     * System.out.println(fiveAsBinary); // "101"
     * </pre> to use a numbering system with more than 64 digits, or to use your
     * own custom digits, use
     * <pre>
     * new BaseEncoder(String)
     * </pre>.
     *
     * @param radix
     */
    public BaseEncoder(int radix) {
        String digitsTemp = getDefaultDigitsForBase(radix);
        digits = digitsTemp;
    }

    /**
     * specify digits to use for your numbering system for example base 16 could
     * be represented as
     * <pre>
     *      new BaseEncoder("0123456789ABCDEF")
     * </pre> <br>
     * and base 64 could be represented as
     * <pre>
     *      new BaseEncoder("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
     *              + "abcdefgjijklmnopqrstuvwxyz+/")
     * </pre>.
     *
     * @param digits
     */
    public BaseEncoder(String digits) {
        if (digits.length() < 2) {
            final String errorMessage = "Supported bases include 2 and above."
                    + " " + "Please provide at least two characters"
                    + " " + "e.g. new BaseEncoder(\"01\") // binary or base 2";
            throw new IllegalArgumentException(errorMessage);
        }
        this.digits = digits;
    }

    /**
     * convert a number from a non-standard numbering format to base 10
     * (BigInteger).
     *
     * @param numberEncoded
     * @return
     */
    public BigInteger toBase10(final String numberEncoded) {
        final int radix = digits.length();
        final BigInteger magnitude = BigInteger.valueOf(radix);
        final char[] chars = numberEncoded.toCharArray();
        BigInteger numberBase10 = BigInteger.ZERO;
        for (int i = 0; i < chars.length; i++) {
            numberBase10 = numberBase10.multiply(magnitude);
            final char digitEncoded = chars[i];
            final int indexOf = digits.indexOf(digitEncoded);
            final int digitValue;
            if (indexOf == -1) {
                digitValue = digits.toLowerCase().indexOf(
                        Character.toLowerCase(digitEncoded));
            } else {
                digitValue = indexOf;
            }
            if (digitValue == -1) {
                final String errorMessage = "Digit '" + digitEncoded + "'"
                        + " " + "from base " + radix + " number"
                        + " " + "'" + numberEncoded + "' not found in"
                        + " " + "base " + radix + " digits '" + digits + "'.";
                throw new IllegalArgumentException(errorMessage);
            }
            numberBase10 = numberBase10.add(BigInteger.valueOf(digitValue));
        }
        return numberBase10;
    }

    /**
     * convert a number from a non-standard numbering format to base 10
     * (BigInteger).
     *
     * @param numberBase10
     * @return
     */
    public String fromBase10(long numberBase10) {
        return fromBase10(BigInteger.valueOf(numberBase10));
    }

    /**
     * convert a number from a non-standard numbering format to base 10
     * (BigInteger).
     *
     * @param numberBase10
     * @return
     */
    public String fromBase10(BigInteger numberBase10) {
        final StringBuilder encodedNumber = new StringBuilder("");
        final int radix = digits.length();
        final BigInteger magnitude = BigInteger.valueOf(radix);
        while (numberBase10.compareTo(BigInteger.ZERO) > 0) {
            final BigInteger[] divideAndRemainder = numberBase10
                    .divideAndRemainder(magnitude);
            final BigInteger quotient = divideAndRemainder[0];
            final BigInteger remainder = divideAndRemainder[1];
            encodedNumber.insert(0, digits.charAt(remainder.intValue()));
            numberBase10 = quotient;
        }
        return encodedNumber.toString();
    }

    public static String getDefaultDigitsForBase(int radix) throws IllegalArgumentException {
        if (radix < 2) {
            final String errorMessage = "Supported bases include 2 and above."
                    + " " + "Not really sure how to represent"
                    + " " + "base " + radix + " numbers.";
            throw new IllegalArgumentException(errorMessage);
        } else if (radix <= 64) {
            return ("0123456789ABCDEFGHIJKLMNOPQRSTUV" // base 32 ends at V
                    + "WXYZabcdefghijklmnopqrstuvwxyz+/").substring(0, radix);
        }
        int charCount = 0;
        final StringBuilder s = new StringBuilder();
        for (int i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
            switch (Character.getType(i)) {
                case Character.CONNECTOR_PUNCTUATION:
                case Character.CURRENCY_SYMBOL:
                case Character.FINAL_QUOTE_PUNCTUATION:
                case Character.INITIAL_QUOTE_PUNCTUATION:
                case Character.LETTER_NUMBER:
                case Character.LINE_SEPARATOR:
                case Character.LOWERCASE_LETTER:
                case Character.MATH_SYMBOL:
                case Character.UPPERCASE_LETTER:
                    s.append((char) i);
                    if (++charCount >= radix) {
                        return s.toString();
                    }
                    break;
            }
        }
        throw new IllegalArgumentException("Radix '" + radix + "' exceeds maximum '" + charCount + "'");
    }

}
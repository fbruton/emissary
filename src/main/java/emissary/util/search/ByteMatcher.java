package emissary.util.search;

import java.nio.charset.Charset;
import java.util.Arrays;
import javax.annotation.Nullable;

/**
 * This class provides some simple string matching functions on byte arrays
 */
public class ByteMatcher {

    private byte[] mydata = null;

    private KeywordScanner scanner = null;

    public static final int NOTFOUND = -1;

    public ByteMatcher() {
        this(new byte[0]);
    }

    public ByteMatcher(String data) {
        this(data.getBytes());
    }

    public ByteMatcher(byte[] data) {
        resetData(data);
    }

    public void resetData(String data) {
        resetData(data, Charset.defaultCharset());
    }

    public void resetData(String data, String charsetName) {
        resetData(data, Charset.forName(charsetName));
    }

    public void resetData(String data, Charset charset) {
        resetData(data.getBytes(charset));
    }

    /**
     * Reset the byte array. Use of this method avoids having to instantiate a new ByteMatcher.
     *
     * @param data - bytes to match against
     */
    public void resetData(byte[] data) {
        this.mydata = data;
        if (null == this.scanner) {
            this.scanner = new KeywordScanner(data);
        } else {
            this.scanner.resetData(data);
        }
    }

    /**
     * Return a reference to the text we are working on
     */
    public byte[] getText() {
        return mydata;
    }

    /**
     * Return the length of the text
     */
    public int length() {
        return mydata.length;
    }

    public boolean containsAny(String... patterns) {
        return Arrays.stream(patterns).anyMatch(this::contains);
    }

    public boolean containsAnyIgnoreCase(String... patterns) {
        return Arrays.stream(patterns).anyMatch(this::containsIgnoreCase);
    }

    public boolean containsAny(int beginIndex, int endIndex, String... patterns) {
        return Arrays.stream(patterns).anyMatch(pattern -> contains(pattern, beginIndex, endIndex));
    }

    public boolean containsAnyIgnoreCase(int beginIndex, int endIndex, String... patterns) {
        return Arrays.stream(patterns).anyMatch(pattern -> containsIgnoreCase(pattern, beginIndex, endIndex));
    }

    public boolean containsAll(String... patterns) {
        return Arrays.stream(patterns).allMatch(this::contains);
    }

    public boolean containsAllIgnoreCase(String... patterns) {
        return Arrays.stream(patterns).allMatch(this::containsIgnoreCase);
    }

    public boolean containsAll(int beginIndex, int endIndex, String... patterns) {
        return Arrays.stream(patterns).allMatch(pattern -> contains(pattern, beginIndex, endIndex));
    }

    public boolean containsAllIgnoreCase(int beginIndex, int endIndex, String... patterns) {
        return Arrays.stream(patterns).allMatch(pattern -> containsIgnoreCase(pattern, beginIndex, endIndex));
    }

    public boolean contains(String pattern) {
        return contains(pattern.getBytes());
    }

    public boolean containsIgnoreCase(String pattern) {
        return containsIgnoreCase(pattern.getBytes());
    }

    public boolean contains(String pattern, int beginIndex, int endIndex) {
        return contains(pattern.getBytes(), beginIndex, endIndex);
    }

    public boolean containsIgnoreCase(String pattern, int beginIndex, int endIndex) {
        return containsIgnoreCase(pattern.getBytes(), beginIndex, endIndex);
    }

    public boolean contains(byte[] pattern) {
        return indexOf(pattern) >= 0;
    }

    public boolean containsIgnoreCase(byte[] pattern) {
        return indexIgnoreCase(pattern) >= 0;
    }

    public boolean contains(byte[] pattern, int beginIndex, int endIndex) {
        return indexOf(pattern, beginIndex, endIndex) >= 0;
    }

    public boolean containsIgnoreCase(byte[] pattern, int beginIndex, int endIndex) {
        return indexIgnoreCase(pattern, beginIndex, endIndex) >= 0;
    }

    /**
     * This method finds a pattern in the text and returns the offset
     *
     * @param pattern bytes to find
     * @param startOfs start index
     */

    public int indexOf(byte[] pattern, int startOfs) {

        if (mydata == null) {
            return NOTFOUND;
        }

        return indexOf(pattern, startOfs, mydata.length);

    }

    /**
     * This method finds a pattern in the text from {@code beginIndex} to {@code endIndex} and returns the offset
     *
     * @param pattern bytes to find
     * @param beginIndex start index
     * @param endIndex the index to stop searching at, exclusive
     *
     * @return position
     */
    public int indexOf(byte[] pattern, int beginIndex, int endIndex) {

        // Impossible to find under these conditions
        if (mydata == null || beginIndex > (mydata.length - pattern.length) || endIndex > mydata.length)
            return NOTFOUND;

        // Use the Boyer-Moore scanning algorithm.
        return scanner.indexOf(pattern, beginIndex, endIndex);

    }

    /**
     * Sort of like libc's strcmp, find if pattern matches this at offset
     */
    public boolean strcmp(int offset, @Nullable String pattern) {

        if (pattern == null) {
            return false;
        }

        byte[] patternBytes = pattern.getBytes();

        for (int i = 0; i < patternBytes.length; i++) {

            if (offset + i >= mydata.length) {
                return false;
            }

            if (mydata[offset + i] != patternBytes[i]) {
                return false;
            }

        }

        return true;

    }

    /**
     * Return the specified byte
     */
    public byte byteAt(int i) {

        if (i < 0 || i >= mydata.length) {
            throw new ArrayIndexOutOfBoundsException("ByteMatcher.data(" + mydata.length + ") : " + i);
        }

        return mydata[i];
    }

    /**
     * Return a slice
     *
     * @param start index to start
     * @param end index one past the end of desired range
     * @return array slice
     */
    public byte[] slice(int start, int end) {
        if (end > start && start >= 0 && end <= mydata.length) {
            byte[] slice = new byte[end - start];
            System.arraycopy(mydata, start, slice, 0, end - start);
            return slice;
        }
        return new byte[0];
    }


    /**
     * StartsWith
     */
    public boolean startsWith(String s) {
        if (mydata.length < s.length()) {
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (byteAt(i) != (byte) s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method finds a pattern in the text from {@code beginIndex} to {@code endIndex} and returns the offset ignoring
     * upper/lower case
     *
     * @param pattern bytes to find
     * @param beginIndex start index
     * @param endIndex the index to stop searching at, exclusive
     *
     * @return position
     */
    public int indexIgnoreCase(byte[] pattern, int beginIndex, int endIndex) {

        // Impossible to find under these conditions
        if (mydata == null || beginIndex > (mydata.length - pattern.length) || endIndex > mydata.length) {
            return NOTFOUND;
        }

        int matchPos;

        // Use the Boyer-Moore scanner. Set it to
        // ignore case.

        scanner.setCaseSensitive(false);
        matchPos = scanner.indexOf(pattern, beginIndex, endIndex);

        // Reset scanner to default state.
        scanner.setCaseSensitive(true);

        return matchPos;


    }

    /**
     * This method finds a pattern in the text and returns the offset ignoring upper/lower case
     */

    public int indexIgnoreCase(byte[] pattern, int startOfs) {

        if (mydata == null) {
            return NOTFOUND;
        }

        return indexIgnoreCase(pattern, startOfs, mydata.length);
    }


    /**
     * Match pattern in the text
     */
    public int indexOf(byte[] pattern) {

        return indexOf(pattern, 0);

    }

    /**
     * Match pattern in the text from {@code beginIndex} to {@code endIndex} and returns the offset
     *
     * @param pattern bytes to find
     * @param beginIndex start index
     * @param endIndex the index to stop searching at, exclusive
     *
     * @return position
     */
    public int indexOf(String pattern, int beginIndex, int endIndex) {

        return indexOf(pattern.getBytes(), beginIndex, endIndex);

    }

    /**
     * Match pattern in the text beginning at startOfs
     */
    public int indexOf(String pattern, int startOfs) {

        return indexOf(pattern.getBytes(), startOfs);

    }

    /**
     * Match pattern in the text
     */
    public int indexOf(String pattern) {

        return indexOf(pattern.getBytes(), 0);

    }

    public int indexIgnoreCase(String pattern) {

        return indexIgnoreCase(pattern.getBytes(), 0);

    }

    public int indexIgnoreCase(String pattern, int startOfs) {

        return indexIgnoreCase(pattern.getBytes(), startOfs);

    }

    /**
     * Match pattern in the text from {@code beginIndex} to {@code endIndex} and returns the offset ignoring upper/lower
     * case
     *
     * @param pattern bytes to find
     * @param beginIndex start index
     * @param endIndex the index to stop searching at, exclusive
     *
     * @return position
     */
    public int indexIgnoreCase(String pattern, int beginIndex, int endIndex) {

        return indexIgnoreCase(pattern.getBytes(), beginIndex, endIndex);

    }

    public int indexIgnoreCase(byte[] pattern) {

        return indexIgnoreCase(pattern, 0);

    }

    /**
     * Find tags of the form "Key{token}Value" returning "Value" when "Key" is supplied. The value goes after the {token} to
     * the end of the line.
     */
    public String getValue(String key, int ofs, String delim) {

        int keypos = this.indexOf(key, ofs);
        if (keypos == -1) {
            return null;
        }

        int eolpos = keypos + key.length();
        while (eolpos < mydata.length && mydata[eolpos] != '\n' && mydata[eolpos] != '\r') {
            eolpos++;
        }

        int delimpos = this.indexOf(delim, keypos + key.length());
        int eodpos = delimpos + delim.length();
        if (delimpos > -1 && eodpos < eolpos) {
            return new String(mydata, eodpos, eolpos - eodpos);
        } else if (eodpos == eolpos) {
            return "";
        } else {
            return null;
        }
    }

    public String getValue(String key, int ofs) {
        return getValue(key, ofs, "=");
    }

    public String getValue(String key) {
        return getValue(key, 0, "=");
    }

    /**
     * Get the value of a S tag, given the value S values work like this: KEY: length data data tdata data more data
     * NEXTKEY: nextlength
     */

    public byte[] getSValue(String key) {
        return getSValue(key, 0, mydata.length);
    }

    public byte[] getSValue(String key, int ofs, int limit) {

        // Make sure the key exists
        int keypos = this.indexOf(key, ofs);
        if (keypos == -1 || keypos > limit) {
            return null;
        }
        int valpos = this.indexOf("\n", keypos);
        if (valpos > limit) {
            return null;
        }
        if (valpos == -1) {
            valpos = this.mydata.length;
        }
        valpos += 1; // past the new line

        // Get the length out as a string
        String sDelim = ":";
        String strLength = this.getValue(key, ofs, sDelim);
        if (strLength == null) {
            return null;
        }

        // Turn the length into an int
        int length = -1;
        try {
            length = Integer.parseInt(strLength.trim());
        } catch (NumberFormatException e) {
            // empty catch block
        }
        if (length <= 0) {
            return null;
        }

        // Dont let length exceed limit
        if (valpos + length > limit) {
            length = limit;
        }

        // Make sure the length is legal
        if (valpos + length > this.mydata.length) {
            return null;
        }

        // Take off a new line if that's the last char
        if (mydata[valpos + length - 1] == '\n') {
            length--;
        }

        // Return the bytes of the data
        byte[] value = new byte[length];
        System.arraycopy(this.mydata, valpos, value, 0, length);
        return (value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass());
        sb.append("[");
        sb.append(this.length());
        sb.append("] : ");
        sb.append(new String(this.mydata));
        return sb.toString();
    }
}

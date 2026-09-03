/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.RandomizerBumper;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Alexander Chow
 * @author Shuyang Zhou
 * @author Hugo Huijser
 */
public class StringUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		new CodeCoverageAssertor() {

			@Override
			public void appendAssertClasses(List<Class<?>> assertClasses) {
				assertClasses.clear();
			}

			@Override
			public List<Method> getAssertMethods()
				throws ReflectiveOperationException {

				return Arrays.asList(
					StringUtil.class.getDeclaredMethod(
						"read", InputStream.class),
					StringUtil.class.getDeclaredMethod(
						"readLines", InputStream.class, Collection.class),
					StringUtil.class.getDeclaredMethod(
						"_read", InputStream.class));
			}

		};

	@Test
	public void testAppendParentheticalSuffixInteger() {
		Assert.assertEquals(
			"Hello World (2)",
			StringUtil.appendParentheticalSuffix("Hello World", 2));
		Assert.assertEquals(
			"Hello (World) (2)",
			StringUtil.appendParentheticalSuffix("Hello (World)", 2));
		Assert.assertEquals(
			"Hello World (3)",
			StringUtil.appendParentheticalSuffix("Hello World (2)", 3));
		Assert.assertEquals(
			"Hello World (2) (4)",
			StringUtil.appendParentheticalSuffix("Hello World (2)", 4));
	}

	@Test
	public void testAppendParentheticalSuffixString() {
		Assert.assertEquals(
			"Hello (World)",
			StringUtil.appendParentheticalSuffix("Hello", "World"));
		Assert.assertEquals(
			"Hello (World) (Liferay)",
			StringUtil.appendParentheticalSuffix("Hello (World)", "Liferay"));
		Assert.assertEquals(
			"Hello (World) (Liferay Portal)",
			StringUtil.appendParentheticalSuffix(
				"Hello (World)", "Liferay Portal"));
	}

	@Test
	public void testContainsIgnoreCase() {
		Assert.assertFalse(StringUtil.containsIgnoreCase(null, null));
		Assert.assertFalse(StringUtil.containsIgnoreCase("one,two", null));
		Assert.assertFalse(StringUtil.containsIgnoreCase(null, "one"));
		Assert.assertTrue(StringUtil.containsIgnoreCase("one", "one"));
		Assert.assertTrue(StringUtil.containsIgnoreCase("one,two", "Two"));
		Assert.assertTrue(StringUtil.containsIgnoreCase("one,two", "onE"));
	}

	@Test
	public void testCountChar() {
		Assert.assertEquals(0, StringUtil.count(null, 0, 1, 'b'));
		Assert.assertEquals(0, StringUtil.count("", 0, 1, 'b'));
		Assert.assertEquals(0, StringUtil.count("", 0, 0, 'b'));
		Assert.assertEquals(0, StringUtil.count("a", 0, 1, 'b'));
		Assert.assertEquals(1, StringUtil.count("b", 0, 1, 'b'));
		Assert.assertEquals(0, StringUtil.count("ab", 0, 1, 'b'));
		Assert.assertEquals(1, StringUtil.count("ab", 0, 2, 'b'));
		Assert.assertEquals(1, StringUtil.count("abb", 0, 2, 'b'));
		Assert.assertEquals(2, StringUtil.count("abb", 0, 3, 'b'));
		Assert.assertEquals(2, StringUtil.count("abcabfabrgab", 2, 8, 'b'));
	}

	@Test
	public void testCountString() {
		Assert.assertEquals(0, StringUtil.count(null, 0, 1, ""));
		Assert.assertEquals(0, StringUtil.count("", 0, 1, ""));
		Assert.assertEquals(0, StringUtil.count("", 0, 0, ""));
		Assert.assertEquals(0, StringUtil.count("a", 0, 1, ""));
		Assert.assertEquals(0, StringUtil.count("a", 0, 1, null));
		Assert.assertEquals(0, StringUtil.count("a", 0, 1, "b"));
		Assert.assertEquals(1, StringUtil.count("b", 0, 1, "b"));
		Assert.assertEquals(0, StringUtil.count("ab", 0, 1, "b"));
		Assert.assertEquals(1, StringUtil.count("ab", 0, 2, "b"));
		Assert.assertEquals(1, StringUtil.count("abb", 0, 2, "b"));
		Assert.assertEquals(2, StringUtil.count("abb", 0, 3, "b"));
		Assert.assertEquals(2, StringUtil.count("abcabfabrgab", 2, 8, "ab"));
	}

	@Test
	public void testEqualsIgnoreBreakLine() {
		Assert.assertTrue(
			StringUtil.equalsIgnoreBreakLine("Hello\n World", "Hello World"));
		Assert.assertTrue(
			StringUtil.equalsIgnoreBreakLine("Hello\r\n World", "Hello World"));
		Assert.assertTrue(
			StringUtil.equalsIgnoreBreakLine("\nHello World", "Hello World"));
		Assert.assertTrue(
			StringUtil.equalsIgnoreBreakLine("Hello World\n", "Hello World"));
		Assert.assertFalse(StringUtil.equalsIgnoreBreakLine("Hello World", ""));
		Assert.assertFalse(
			StringUtil.equalsIgnoreBreakLine("Hello World\n", null));
	}

	@Test
	public void testEqualsIgnoreCase() {
		Assert.assertTrue(
			StringUtil.equalsIgnoreCase("HELLO WORLD", "Hello World"));
		Assert.assertTrue(
			StringUtil.equalsIgnoreCase("Hello \n World", "hello \n worlD"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("Hello \n World", ""));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("Hello \n World", null));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("!", "A"));
	}

	@Test
	public void testGetTitleCase() {
		Assert.assertTrue(
			Objects.equals(
				StringUtil.getTitleCase(
					"a cat is going up with a letter a", false, ""),
				"A Cat Is Going up With a Letter A"));
		Assert.assertTrue(
			Objects.equals(
				StringUtil.getTitleCase("this is a title test", false, ""),
				"This Is a Title Test"));
		Assert.assertTrue(
			Objects.equals(
				StringUtil.getTitleCase(
					"this is a dxp title test", false, "DXP"),
				"This Is a DXP Title Test"));
		Assert.assertTrue(
			Objects.equals(
				StringUtil.getTitleCase("this is a title-test", true, ""),
				"This Is a Title-test"));
	}

	@Test
	public void testHexCodec() {
		Random random = new Random();

		byte[] data = new byte[1024];

		random.nextBytes(data);

		String hexString = StringUtil.bytesToHexString(data);

		Assert.assertEquals(data.length * 2, hexString.length());

		for (int i = 0; i < data.length; i++) {
			Assert.assertEquals(
				hexString.charAt(i * 2),
				StringUtil.HEX_DIGITS[(data[i] & 0xFF) >> 4]);
			Assert.assertEquals(
				hexString.charAt((i * 2) + 1),
				StringUtil.HEX_DIGITS[data[i] & 0x0F]);
		}

		Assert.assertArrayEquals(data, StringUtil.hexStringToBytes(hexString));
	}

	@Test
	public void testIndexOfAny() {
		char[] chars = {CharPool.COLON, CharPool.COMMA};

		Assert.assertEquals(-1, StringUtil.indexOfAny(null, chars));
		Assert.assertEquals(-1, StringUtil.indexOfAny(null, chars, 1));
		Assert.assertEquals(-1, StringUtil.indexOfAny(null, chars, 1, 5));

		Assert.assertEquals(4, StringUtil.indexOfAny("test,:test", chars));
		Assert.assertEquals(
			5, StringUtil.indexOfAny("test,:test,:test", chars, 5));
		Assert.assertEquals(
			-1, StringUtil.indexOfAny("test,:test,:test", chars, 7, 9));
		Assert.assertEquals(
			10, StringUtil.indexOfAny("test,:test,:test", chars, 7, 12));

		String[] strings = {null, "ab", "cd"};

		Assert.assertEquals(-1, StringUtil.indexOfAny(null, strings));
		Assert.assertEquals(-1, StringUtil.indexOfAny(null, strings, 1));
		Assert.assertEquals(-1, StringUtil.indexOfAny(null, strings, 1, 5));

		Assert.assertEquals(4, StringUtil.indexOfAny("1234cdab1234", strings));
		Assert.assertEquals(
			6, StringUtil.indexOfAny("1234cdabcd1234", strings, 5));
		Assert.assertEquals(
			-1, StringUtil.indexOfAny("1234cdab1234abcd", strings, 7, 9));
		Assert.assertEquals(
			12, StringUtil.indexOfAny("1234cdab1234cdab", strings, 7, 15));

		Assert.assertEquals(
			0, StringUtil.indexOfAny("1234", new String[] {""}));
		Assert.assertEquals(
			2, StringUtil.indexOfAny("1234", new String[] {""}, 2));
		Assert.assertEquals(
			2, StringUtil.indexOfAny("1234", new String[] {""}, 2, 4));
	}

	@Test
	public void testIsLowerCase() {
		Assert.assertTrue(StringUtil.isLowerCase("hello world"));
		Assert.assertFalse(StringUtil.isLowerCase("Hello World"));
		Assert.assertFalse(StringUtil.isLowerCase("HELLO WORLD"));
		Assert.assertTrue(StringUtil.isLowerCase("hello-world-1"));
		Assert.assertFalse(StringUtil.isLowerCase("HELLO-WORLD-1"));
	}

	@Test
	public void testIsUpperCase() {
		Assert.assertFalse(StringUtil.isUpperCase("hello world"));
		Assert.assertFalse(StringUtil.isUpperCase("Hello World"));
		Assert.assertTrue(StringUtil.isUpperCase("HELLO WORLD"));
		Assert.assertFalse(StringUtil.isUpperCase("hello-world-1"));
		Assert.assertTrue(StringUtil.isUpperCase("HELLO-WORLD-1"));
	}

	@Test
	public void testLastIndexOfAny() {
		char[] chars = {CharPool.COLON, CharPool.COMMA};

		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, chars));
		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, chars, 1));
		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, chars, 1, 5));

		Assert.assertEquals(5, StringUtil.lastIndexOfAny("test,:test", chars));
		Assert.assertEquals(
			5, StringUtil.lastIndexOfAny("test,:test,:test", chars, 7));
		Assert.assertEquals(
			-1, StringUtil.lastIndexOfAny("test,:test,:test", chars, 7, 9));
		Assert.assertEquals(
			11, StringUtil.lastIndexOfAny("test,:test,:test", chars, 7, 12));

		String[] strings = {null, "ab", "cd"};

		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, strings));
		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, strings, 1));
		Assert.assertEquals(-1, StringUtil.lastIndexOfAny(null, strings, 1, 5));

		Assert.assertEquals(
			6, StringUtil.lastIndexOfAny("1234cdab1234", strings));
		Assert.assertEquals(
			4, StringUtil.lastIndexOfAny("1234cdabcd1234", strings, 5));
		Assert.assertEquals(
			-1, StringUtil.lastIndexOfAny("1234cdab1234abcd", strings, 7, 9));
		Assert.assertEquals(
			12, StringUtil.lastIndexOfAny("1234cdab1234cdab", strings, 7, 14));

		Assert.assertEquals(
			3, StringUtil.lastIndexOfAny("1234", new String[] {""}));
		Assert.assertEquals(
			2, StringUtil.lastIndexOfAny("1234", new String[] {""}, 2));
		Assert.assertEquals(
			3, StringUtil.lastIndexOfAny("1234", new String[] {""}, 2, 3));
	}

	@Test
	public void testMerge() {
		Assert.assertEquals(
			"1,2,3", StringUtil.merge(new String[] {"1", " 2 ", "3"}));
		Assert.assertEquals("1", StringUtil.merge(new String[] {"1"}));
		Assert.assertEquals("", StringUtil.merge(new String[0]));
		Assert.assertEquals(
			"true,false,true",
			StringUtil.merge(new boolean[] {true, false, true}));
		Assert.assertEquals("true", StringUtil.merge(new boolean[] {true}));
		Assert.assertEquals(
			"1.1,2.2,3.3", StringUtil.merge(new double[] {1.1, 2.2, 3.3}));
		Assert.assertEquals("1.1", StringUtil.merge(new double[] {1.1}));
		Assert.assertEquals("1,2,3", StringUtil.merge(new int[] {1, 2, 3}));
		Assert.assertEquals("1", StringUtil.merge(new int[] {1}));
		Assert.assertEquals("1,2,3", StringUtil.merge(new long[] {1, 2, 3}));
		Assert.assertEquals("1", StringUtil.merge(new long[] {1}));
		Assert.assertEquals(
			"123", StringUtil.merge(Arrays.asList("1", "2", "3"), ""));
	}

	@Test
	public void testQuote() {
		Assert.assertEquals(
			"'Hello, World!'", StringUtil.quote("Hello, World!"));
		Assert.assertEquals("%PATH%", StringUtil.quote("PATH", '%'));
		Assert.assertEquals(
			"Hello World Hello", StringUtil.quote(" World ", "Hello"));
	}

	@Test
	public void testRandomIdNeverReturnsNull() {
		int[] index = new int[1];
		int[] offsets = {
			_getOffset('n'), _getOffset('u'), _getOffset('l'), _getOffset('l'),
			_getOffset('a'), _getOffset('b'), _getOffset('c'), _getOffset('d')
		};

		Random random = new Random() {

			@Override
			public int nextInt(int bound) {
				return offsets[index[0]++];
			}

		};

		String randomId = ReflectionTestUtil.invoke(
			StringUtil.class, "_randomId",
			new Class<?>[] {int.class, Random.class}, 4, random);

		Assert.assertEquals(randomId, "abcd", randomId);

		Assert.assertEquals(String.valueOf(index[0]), 8, index[0]);

		index[0] = 0;

		randomId = ReflectionTestUtil.invoke(
			StringUtil.class, "_randomId",
			new Class<?>[] {int.class, Random.class}, 5, random);

		Assert.assertEquals(randomId, "nulla", randomId);

		Assert.assertEquals(String.valueOf(index[0]), 5, index[0]);

		randomId = StringUtil.randomId(4);

		Assert.assertEquals(randomId, 4, randomId.length());
	}

	@Test
	public void testRead() throws Exception {
		Assert.assertEquals(
			StringPool.BLANK,
			StringUtil.read(new ByteArrayInputStream(new byte[0])));

		Assert.assertEquals(
			StringPool.BLANK,
			StringUtil.read(new ByteArrayInputStream(new byte[0])));

		Assert.assertEquals(
			"A\nB",
			StringUtil.read(new ByteArrayInputStream("A\rB".getBytes())));

		Assert.assertEquals(
			"A\nB",
			StringUtil.read(new ByteArrayInputStream("A\r\nB".getBytes())));

		Assert.assertEquals(
			"Test",
			StringUtil.read(new ByteArrayInputStream(" Test ".getBytes())));

		String expected = RandomTestUtil.randomString(
			8193,
			(RandomizerBumper<String>)randomValue ->
				(randomValue.indexOf(CharPool.RETURN) == -1) &&
				!Character.isWhitespace(randomValue.charAt(0)) &&
				!Character.isWhitespace(randomValue.charAt(8192)));

		Assert.assertEquals(
			expected,
			StringUtil.read(new ByteArrayInputStream(expected.getBytes())));
	}

	@Test
	public void testReadLines() throws Exception {
		List<String> lines = new ArrayList<>();

		StringUtil.readLines(new ByteArrayInputStream(new byte[0]), lines);

		Assert.assertEquals(lines.toString(), 0, lines.size());

		StringUtil.readLines(
			new ByteArrayInputStream(StringPool.SPACE.getBytes()), lines);

		Assert.assertEquals(lines.toString(), 1, lines.size());
		Assert.assertEquals(StringPool.SPACE, lines.get(0));

		StringUtil.readLines(
			new ByteArrayInputStream(StringPool.RETURN.getBytes()), lines);

		Assert.assertEquals(lines.toString(), 2, lines.size());
		Assert.assertEquals(StringPool.BLANK, lines.get(1));

		StringUtil.readLines(
			new ByteArrayInputStream(" Test ".getBytes()), lines);

		Assert.assertEquals(lines.toString(), 3, lines.size());
		Assert.assertEquals(" Test ", lines.get(2));
	}

	@Test
	public void testRemoveChar() {
		Assert.assertEquals("abcd", StringUtil.removeChar("a.b.c.d", '.'));
		Assert.assertEquals("abcd", StringUtil.removeChar(".a.b.c.d.", '.'));

		String s = "a.b.c.d";

		Assert.assertSame(s, StringUtil.removeChar(s, '?'));
	}

	@Test
	public void testRemoveChars() {
		Assert.assertEquals(
			"abcd", StringUtil.removeChars("a.*b./c.*d", '.', '*', '/'));
		Assert.assertEquals(
			"abcd", StringUtil.removeChars("/.*a./b.c.*d./", '.', '*', '/'));

		String s = "/.*a./b.c.*d./";

		Assert.assertSame(s, StringUtil.removeChars(s, 'x', 'y', 'z'));
	}

	@Test
	public void testRemoveFromList() {
		Assert.assertEquals(
			"red,green,yellow,",
			StringUtil.removeFromList("red,blue,green,yellow", "blue"));
		Assert.assertEquals("", StringUtil.removeFromList("blue", "blue"));
		Assert.assertEquals("", StringUtil.removeFromList("blue,", "blue"));
		Assert.assertEquals(
			"red;green;yellow;",
			StringUtil.removeFromList("red;blue;green;yellow", "blue", ";"));
		Assert.assertEquals("", StringUtil.removeFromList("blue", "blue", ";"));
		Assert.assertEquals(
			"", StringUtil.removeFromList("blue;", "blue", ";"));
	}

	@Test
	public void testReplaceChar() {
		Assert.assertEquals(
			"127_0_0_1", StringUtil.replace("127.0.0.1", '.', '_'));
	}

	@Test
	public void testReplaceCharArray() {
		Assert.assertEquals(
			"227_0_0_2",
			StringUtil.replace(
				"127.0.0.1", new char[] {'.', '1'}, new char[] {'_', '2'}));
		Assert.assertEquals(
			"227_0_0_2",
			StringUtil.replace(
				"127.0.0.1", new char[] {'.', '.', '1', '1'},
				new char[] {'_', '_', '2', '2'}));
	}

	@Test
	public void testReplaceCharString() {
		Assert.assertNull(StringUtil.replace(null, ',', "COMMA"));
		Assert.assertNull(StringUtil.replace("Hello World", ',', null));
		Assert.assertEquals(
			"Hello World", StringUtil.replace("Hello World", ',', "COMMA"));
		Assert.assertEquals(
			"COMMAHello World",
			StringUtil.replace(",Hello World", ',', "COMMA"));
		Assert.assertEquals(
			"HelloCOMMA World",
			StringUtil.replace("Hello, World", ',', "COMMA"));
		Assert.assertEquals(
			"Hello WorldCOMMA",
			StringUtil.replace("Hello World,", ',', "COMMA"));
		Assert.assertEquals(
			"COMMAHelloCOMMA WorldCOMMA",
			StringUtil.replace(",Hello, World,", ',', "COMMA"));
	}

	@Test
	public void testReplaceCharStringArrays() {
		Assert.assertEquals(
			"Hello World,HELLO WORLD,Hello World",
			StringUtil.replace(
				"Hello World/HI WORLD/Hello World",
				new char[] {CharPool.SLASH, CharPool.UPPER_CASE_I},
				new String[] {StringPool.COMMA, "ELLO"}));
		Assert.assertEquals(
			"Hello World,HELLO WORLD,Hello World",
			StringUtil.replace(
				"Hello World/HI WORLD/Hello World",
				new char[] {
					CharPool.SLASH, CharPool.SLASH, CharPool.UPPER_CASE_I,
					CharPool.UPPER_CASE_I
				},
				new String[] {
					StringPool.COMMA, StringPool.COMMA, "ELLO", "ELLO"
				}));
	}

	@Test
	public void testReplaceEmptyString() {
		Assert.assertEquals(
			"Hello World HELLO WORLD Hello World",
			StringUtil.replace(
				"Hello World HELLO WORLD Hello World", "", "Aloha"));
	}

	@Test
	public void testReplaceFirstChar() {
		Assert.assertEquals(
			"127_0.0.1", StringUtil.replaceFirst("127.0.0.1", '.', '_'));
	}

	@Test
	public void testReplaceFirstString() {
		Assert.assertEquals(
			"Aloha World HELLO WORLD Hello World",
			StringUtil.replaceFirst(
				"Hello World HELLO WORLD Hello World", "Hello", "Aloha"));
		Assert.assertEquals(
			"Hello World HELLO WORLD Aloha World",
			StringUtil.replaceFirst(
				"Hello World HELLO WORLD Hello World", "Hello", "Aloha", 10));
	}

	@Test
	public void testReplaceFirstStringArray() {
		Assert.assertEquals(
			"Aloha World ALOHA WORLD Hello World HELLO WORLD",
			StringUtil.replaceFirst(
				"Hello World HELLO WORLD Hello World HELLO WORLD",
				new String[] {"Hello", "HELLO"},
				new String[] {"Aloha", "ALOHA"}));
	}

	@Test
	public void testReplaceLastChar() {
		Assert.assertEquals(
			"127.0.0_1", StringUtil.replaceLast("127.0.0.1", '.', '_'));
	}

	@Test
	public void testReplaceLastString() {
		Assert.assertEquals(
			"Hello World HELLO WORLD Aloha World",
			StringUtil.replaceLast(
				"Hello World HELLO WORLD Hello World", "Hello", "Aloha"));
	}

	@Test
	public void testReplaceLastStringArray() {
		Assert.assertEquals(
			"Hello World HELLO WORLD Aloha World ALOHA WORLD",
			StringUtil.replaceLast(
				"Hello World HELLO WORLD Hello World HELLO WORLD",
				new String[] {"Hello", "HELLO"},
				new String[] {"Aloha", "ALOHA"}));
	}

	@Test(timeout = 1000)
	public void testReplaceMap() {
		Map<String, String> map = HashMapBuilder.put(
			"Hallo", "Hello"
		).put(
			"Wirld", "World"
		).build();

		Assert.assertEquals(
			"Hello World",
			StringUtil.replace("AB Hallo CD AB Wirld CD", "AB ", " CD", map));
		Assert.assertEquals(
			"Hello World",
			StringUtil.replace(
				"Hello World", StringPool.BLANK, StringPool.BLANK, map));
		Assert.assertEquals(
			"AB Hi CD AB Liferay CD",
			StringUtil.replace("AB Hi CD AB Liferay CD", "AB ", " CD", map));
	}

	@Test
	public void testReplaceSpaceString() {
		Assert.assertEquals(
			"HelloWorldHELLOWORLDHelloWorld",
			StringUtil.replace(
				"Hello World HELLO WORLD Hello World", " ", StringPool.BLANK));
	}

	@Test
	public void testReplaceString() {
		Assert.assertEquals(
			"Aloha World HELLO WORLD Aloha World",
			StringUtil.replace(
				"Hello World HELLO WORLD Hello World", "Hello", "Aloha"));
	}

	@Test
	public void testReplaceStringArray() {
		Assert.assertEquals(
			"Aloha World ALOHA WORLD Aloha World",
			StringUtil.replace(
				"Hello World HELLO WORLD Hello World",
				new String[] {"Hello", "HELLO"},
				new String[] {"Aloha", "ALOHA"}));
	}

	@Test(timeout = 1000)
	public void testReplaceWithStringBundle() {
		Map<String, StringBundler> map =
			HashMapBuilder.<String, StringBundler>put(
				"Hallo", new StringBundler("Hello")
			).put(
				"Wirld", new StringBundler("World")
			).build();

		Assert.assertEquals(
			"Hello World",
			String.valueOf(
				StringUtil.replaceWithStringBundler(
					"AB Hallo CD AB Wirld CD", "AB ", " CD", map)));
		Assert.assertEquals(
			"Hello World",
			String.valueOf(
				StringUtil.replaceWithStringBundler(
					"Hello World", StringPool.BLANK, StringPool.BLANK, map)));
	}

	@Test
	public void testShortenString() {
		Assert.assertEquals(
			"Hello World HELLO...",
			StringUtil.shorten("Hello World HELLO WORLD Hello World"));
		Assert.assertEquals("Hi Hello", StringUtil.shorten("Hi Hello", 8));
		Assert.assertEquals("Hello...", StringUtil.shorten("Hello World", 8));
		Assert.assertEquals("Hi...", StringUtil.shorten("Hi Hello World", 8));
		Assert.assertEquals("...", StringUtil.shorten(" Hello World", 8));
		Assert.assertEquals(
			"HelloWorldHe... etc.",
			StringUtil.shorten(
				"HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHello", 20,
				"... etc."));
		Assert.assertEquals(
			"abcdef\u00C1...",
			StringUtil.shorten("abcdef\u0041\u0301vwxyz", 10));
	}

	@Test
	public void testShortenStringWith4ByteChars() {
		int space = CharPool.SPACE;

		int[] codePoints = {
			128515, 128516, space, 128517, 128518, 128519, 128520, 128521
		};

		String string = new String(codePoints, 0, codePoints.length);

		Assert.assertEquals(
			new String(codePoints, 0, 1), StringUtil.shorten(string, 1));
		Assert.assertEquals(
			new String(codePoints, 0, 1) + "...",
			StringUtil.shorten(string, 4));
		Assert.assertEquals(
			new String(codePoints, 0, 2) + "...",
			StringUtil.shorten(string, 7));
	}

	@Test
	public void testSplit() {
		Assert.assertArrayEquals(
			new String[] {"Alice", "Bob", "Charlie"},
			StringUtil.split("Alice,Bob,Charlie"));
		Assert.assertArrayEquals(
			new boolean[] {true, false, true},
			StringUtil.split("true,false,true", false));
		Assert.assertArrayEquals(
			new String[] {"First", "Second", "Third"},
			StringUtil.split("First;Second;Third", ';'));
		Assert.assertArrayEquals(
			new String[] {"One", "Two", "Three"},
			StringUtil.split("OnexTwoxThree", 'x'));
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0}, StringUtil.split("1.0,2.0,3.0", 1.0),
			0.0001);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F},
			StringUtil.split("1.0,2.0,3.0", 1.0F), .0001F);
		Assert.assertArrayEquals(
			new int[] {1, 2, 3}, StringUtil.split("1,2,3", 1));
		Assert.assertArrayEquals(
			new long[] {1L, 2L, 3L}, StringUtil.split("1,2,3", 1L));
	}

	@Test
	public void testSplitLines() {
		String singleLine = "abcdefg";

		String[] lines = StringUtil.splitLines(singleLine);

		Assert.assertEquals(Arrays.toString(lines), 1, lines.length);
		Assert.assertEquals(singleLine, lines[0]);

		String splitByReturn = "abcd\refg\rhijk\rlmn\r";

		lines = StringUtil.splitLines(splitByReturn);

		Assert.assertEquals(Arrays.toString(lines), 4, lines.length);
		Assert.assertEquals("abcd", lines[0]);
		Assert.assertEquals("efg", lines[1]);
		Assert.assertEquals("hijk", lines[2]);
		Assert.assertEquals("lmn", lines[3]);

		String splitByNewLine = "abcd\nefg\nhijk\nlmn\n";

		lines = StringUtil.splitLines(splitByNewLine);

		Assert.assertEquals(Arrays.toString(lines), 4, lines.length);
		Assert.assertEquals("abcd", lines[0]);
		Assert.assertEquals("efg", lines[1]);
		Assert.assertEquals("hijk", lines[2]);
		Assert.assertEquals("lmn", lines[3]);

		String splitByBoth = "abcd\r\nefg\r\nhijk\r\nlmn\r\n";

		lines = StringUtil.splitLines(splitByBoth);

		Assert.assertEquals(Arrays.toString(lines), 4, lines.length);
		Assert.assertEquals("abcd", lines[0]);
		Assert.assertEquals("efg", lines[1]);
		Assert.assertEquals("hijk", lines[2]);
		Assert.assertEquals("lmn", lines[3]);

		String splitByMix = "abcd\refg\nhijk\n\rlmn\r\n";

		lines = StringUtil.splitLines(splitByMix);

		Assert.assertEquals(Arrays.toString(lines), 5, lines.length);
		Assert.assertEquals("abcd", lines[0]);
		Assert.assertEquals("efg", lines[1]);
		Assert.assertEquals("hijk", lines[2]);
		Assert.assertEquals("", lines[3]);
		Assert.assertEquals("lmn", lines[4]);
	}

	@Test(timeout = 1000)
	public void testStripBetween() {
		Assert.assertEquals(
			"One small leap for mankind",
			StringUtil.stripBetween(
				"One small step for man, one giant leap for mankind", "step",
				"giant "));
		Assert.assertEquals(
			"One small step for man, one giant leap for mankind",
			StringUtil.stripBetween(
				"One small step for man, one giant leap for mankind",
				StringPool.BLANK, StringPool.BLANK));
	}

	@Test
	public void testStripParentheticalSuffixInteger() {
		Assert.assertEquals(
			"Hello World",
			StringUtil.stripParentheticalSuffix("Hello World (2)"));
		Assert.assertEquals(
			"Hello World(2)",
			StringUtil.stripParentheticalSuffix("Hello World(2)"));
		Assert.assertEquals(
			"Hello (World)",
			StringUtil.stripParentheticalSuffix("Hello (World) (2)"));
		Assert.assertEquals(
			"Hello World (2)",
			StringUtil.stripParentheticalSuffix("Hello World (2) (3)"));
	}

	@Test
	public void testStripParentheticalSuffixString() {
		Assert.assertEquals(
			"Hello", StringUtil.stripParentheticalSuffix("Hello (World)"));
		Assert.assertEquals(
			"Hello (World)(Liferay)",
			StringUtil.stripParentheticalSuffix("Hello (World)(Liferay)"));
		Assert.assertEquals(
			"Hello (World)",
			StringUtil.stripParentheticalSuffix("Hello (World) (Liferay)"));
		Assert.assertEquals(
			"Hello (World)",
			StringUtil.stripParentheticalSuffix(
				"Hello (World) (Liferay Portal)"));
	}

	@Test
	public void testToLowerCase() {
		Assert.assertEquals(
			"hello world", StringUtil.toLowerCase("hello world"));
		Assert.assertEquals(
			"hello world", StringUtil.toLowerCase("HELLO WORLD"));
		Assert.assertEquals(
			"hello world", StringUtil.toLowerCase("hElLo WoRlD"));
		Assert.assertEquals(
			"hello-world-1", StringUtil.toLowerCase("HELLO-WORLD-1"));
	}

	@Test
	public void testToLowerCaseWithNonasciiCharacters() {
		Assert.assertEquals("\u00F1", StringUtil.toLowerCase("\u00D1"));
		Assert.assertEquals(
			"hello world \u00F1", StringUtil.toLowerCase("hello world \u00D1"));
		Assert.assertEquals(
			"hello-world-\u00F1", StringUtil.toLowerCase("HELLO-WORLD-\u00D1"));
	}

	@Test
	public void testToUpperCase() {
		Assert.assertEquals(
			"HELLO WORLD", StringUtil.toUpperCase("hello world"));
		Assert.assertEquals(
			"HELLO WORLD", StringUtil.toUpperCase("HELLO WORLD"));
		Assert.assertEquals(
			"HELLO WORLD", StringUtil.toUpperCase("hElLo WoRlD"));
		Assert.assertEquals(
			"HELLO-WORLD-1", StringUtil.toUpperCase("hello-world-1"));
	}

	@Test
	public void testToUpperCaseWithNonasciiCharacters() {
		Assert.assertEquals("\u00D1", StringUtil.toUpperCase("\u00F1"));
		Assert.assertEquals(
			"HELLO WORLD \u00D1", StringUtil.toUpperCase("hello world \u00F1"));
		Assert.assertEquals(
			"HELLO-WORLD-\u00D1", StringUtil.toUpperCase("HELLO-WORLD-\u00F1"));
	}

	@Test
	public void testTrim() {

		// Null string

		Assert.assertNull(StringUtil.trim(null));

		// Blank string

		Assert.assertSame(StringPool.BLANK, StringUtil.trim(StringPool.BLANK));

		// Spaces string

		Assert.assertSame(StringPool.BLANK, StringUtil.trim(" \t\r\n"));

		// Not trimmable

		Assert.assertSame("a", StringUtil.trim("a"));
		Assert.assertSame("ab", StringUtil.trim("ab"));

		// Leading spaces

		Assert.assertEquals("ab", StringUtil.trim(" \t\r\nab"));

		// Trailing spaces

		Assert.assertEquals("ab", StringUtil.trim("ab \t\r\n"));

		// Surrounding spaces

		Assert.assertEquals("ab", StringUtil.trim(" \t\r\nab \t\r\n"));

		// UTF-8 spaces

		Assert.assertEquals("ab", StringUtil.trim("\u3000ab\u3000"));
	}

	@Test
	public void testTrimLeading() {

		// Null string

		Assert.assertNull(StringUtil.trimLeading(null));

		// Blank string

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimLeading(StringPool.BLANK));

		// Spaces string

		Assert.assertSame(StringPool.BLANK, StringUtil.trimLeading(" \t\r\n"));

		// Not trimmable

		Assert.assertSame("a", StringUtil.trimLeading("a"));
		Assert.assertSame("ab", StringUtil.trimLeading("ab"));

		// Leading spaces

		Assert.assertEquals("ab", StringUtil.trimLeading(" \t\r\nab"));

		// Trailing spaces

		Assert.assertSame("ab \t\r\n", StringUtil.trimLeading("ab \t\r\n"));

		// Surrounding spaces

		Assert.assertEquals(
			"ab \t\r\n", StringUtil.trimLeading(" \t\r\nab \t\r\n"));
	}

	@Test
	public void testTrimLeadingWithExceptions() {

		// Null string

		Assert.assertNull(StringUtil.trimLeading(null, null));

		// Null exceptions

		Assert.assertSame(StringPool.BLANK, StringUtil.trimLeading(" ", null));

		// No exceptions

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimLeading(" ", new char[0]));

		// Blank string

		char[] exceptions = {'\r', '\t'};

		Assert.assertSame(
			StringPool.BLANK,
			StringUtil.trimLeading(StringPool.BLANK, exceptions));

		// Spaces string

		Assert.assertEquals(
			"\t\r\n", StringUtil.trimLeading(" \t\r\n", exceptions));

		// Not trimmable

		Assert.assertSame("\t", StringUtil.trimLeading("\t", exceptions));
		Assert.assertSame("\t\r", StringUtil.trimLeading("\t\r", exceptions));

		// All trimmable

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimLeading(" \n", exceptions));

		// Leading spaces

		Assert.assertEquals(
			"\t\r\n\t\r", StringUtil.trimLeading(" \t\r\n\t\r", exceptions));

		// Trailing spaces

		Assert.assertSame(
			"\t\r \t\r\n", StringUtil.trimLeading("\t\r \t\r\n", exceptions));

		// Surrounding spaces

		Assert.assertEquals(
			"\t\r\n\t\r \t\r\n",
			StringUtil.trimLeading(" \t\r\n\t\r \t\r\n", exceptions));
	}

	@Test
	public void testTrimTrailing() {

		// Null string

		Assert.assertNull(StringUtil.trimTrailing(null));

		// Blank string

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimTrailing(StringPool.BLANK));

		// Spaces string

		Assert.assertSame(StringPool.BLANK, StringUtil.trimTrailing(" \t\r\n"));

		// Not trimmable

		Assert.assertSame("a", StringUtil.trimTrailing("a"));
		Assert.assertSame("ab", StringUtil.trimTrailing("ab"));

		// Leading spaces

		Assert.assertSame(" \t\r\nab", StringUtil.trimTrailing(" \t\r\nab"));

		// Trailing spaces

		Assert.assertEquals("ab", StringUtil.trimTrailing("ab \t\r\n"));

		// Surrounding spaces

		Assert.assertEquals(
			" \t\r\nab", StringUtil.trimTrailing(" \t\r\nab \t\r\n"));
	}

	@Test
	public void testTrimTrailingWithExceptions() {

		// Null string

		Assert.assertNull(StringUtil.trimTrailing(null, null));

		// Null exceptions

		Assert.assertSame(StringPool.BLANK, StringUtil.trimTrailing(" ", null));

		// No exceptions

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimTrailing(" ", new char[0]));

		char[] exceptions = {'\r', '\t'};

		// Blank string

		Assert.assertSame(
			StringPool.BLANK,
			StringUtil.trimTrailing(StringPool.BLANK, exceptions));

		// Spaces string

		Assert.assertEquals(
			" \t\r", StringUtil.trimTrailing(" \t\r\n", exceptions));

		// Not trimmable

		Assert.assertSame("\t", StringUtil.trimTrailing("\t", exceptions));
		Assert.assertSame("\t\r", StringUtil.trimTrailing("\t\r", exceptions));

		// All trimmable

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trimTrailing(" \n", exceptions));

		// Leading spaces

		Assert.assertSame(
			" \t\r\n\t\r", StringUtil.trimTrailing(" \t\r\n\t\r", exceptions));

		// Trailing spaces

		Assert.assertEquals(
			"\t\r \t\r", StringUtil.trimTrailing("\t\r \t\r\n", exceptions));

		// Surrounding spaces

		Assert.assertEquals(
			" \t\r\n\t\r \t\r",
			StringUtil.trimTrailing(" \t\r\n\t\r \t\r\n", exceptions));
	}

	@Test
	public void testTrimWithExceptions() {

		// Null string

		Assert.assertNull(StringUtil.trim(null, null));

		// Null exceptions

		Assert.assertSame(StringPool.BLANK, StringUtil.trim(" ", null));

		// No exceptions

		Assert.assertSame(StringPool.BLANK, StringUtil.trim(" ", new char[0]));

		char[] exceptions = {'\t', '\r'};

		// Blank string

		Assert.assertSame(
			StringPool.BLANK, StringUtil.trim(StringPool.BLANK, exceptions));

		// Spaces string

		String spacesString = " \t\r\n";

		Assert.assertEquals("\t\r", StringUtil.trim(spacesString, exceptions));

		// Not trimmable

		String testString = "\t";

		Assert.assertSame(testString, StringUtil.trim(testString, exceptions));

		testString = "\t\r";

		Assert.assertSame(testString, StringUtil.trim(testString, exceptions));

		// All trimmable

		Assert.assertSame(StringPool.BLANK, StringUtil.trim(" \n", exceptions));

		// Leading spaces

		String leadingSpacesString = " \t\r\n" + testString;

		Assert.assertEquals(
			"\t\r\n" + testString,
			StringUtil.trim(leadingSpacesString, exceptions));

		// Trailing spaces

		String trailingSpacesString = testString + " \t\r\n";

		Assert.assertEquals(
			testString + " \t\r",
			StringUtil.trim(trailingSpacesString, exceptions));

		// Surrounding spaces

		String surroundingSpacesString = " \t\r\n" + testString + " \t\r\n";

		Assert.assertEquals(
			"\t\r\n" + testString + " \t\r",
			StringUtil.trim(surroundingSpacesString, exceptions));
	}

	@Test
	public void testUnquote() {
		Assert.assertEquals("", StringUtil.unquote(""));
		Assert.assertEquals("Hello World", StringUtil.unquote("'Hello World'"));
		Assert.assertEquals("'Hello World", StringUtil.unquote("'Hello World"));
		Assert.assertEquals(
			"Hello World", StringUtil.unquote("\"Hello World\""));
		Assert.assertEquals("Hello World", StringUtil.unquote("Hello World"));
	}

	@Test
	public void testUpperCaseFirstLetter() {
		Assert.assertEquals("", StringUtil.upperCaseFirstLetter(""));
		Assert.assertEquals(
			"Hello World", StringUtil.upperCaseFirstLetter("hello World"));
		Assert.assertNull(StringUtil.upperCaseFirstLetter(null));
	}

	@Test
	public void testWildcardMatches() {

		// Exact match in a case sensitive manner

		String s = "abc";
		String wildcard = "abc";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Exact match in a case insensitive manner

		s = "aBc";
		wildcard = "abc";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, false));

		// Head match with a wildcard multiple character

		s = "abc";
		wildcard = "%c";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Head match with a wildcard single character

		s = "abc";
		wildcard = "__c";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Head match with an insufficient wildcard

		s = "abc";
		wildcard = "ab";

		Assert.assertFalse(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Head mismatch with a single wildcard character

		s = "abc";
		wildcard = "a_Z";

		Assert.assertFalse(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Head mismatch with a multiple wildcard character (this is not
		// logically possible because a head mismatch with a multipe wildcard
		// character is a tail mismatch)

		// Body match with a multiple wildcard character

		s = "abc";
		wildcard = "a%";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Body match with a single wildcard character

		s = "abcd";
		wildcard = "a%__d";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Body mismatch with a short wildcard

		s = "abc";
		wildcard = "%ab";

		Assert.assertFalse(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Tail match

		s = "abc";
		wildcard = "abc%";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Tail mismatch

		s = "abc";
		wildcard = "abc%z";

		Assert.assertFalse(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Match without a conflicting escape wildcard character

		s = "a_b%c";
		wildcard = "a\\_b\\%c";

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));

		// Match with a conflicting escape wildcard character

		s = new String(
			new char[] {(char)0, '_', 'a', (char)2, '%', 'c', 'd', 'e'});
		wildcard = new String(
			new char[] {(char)0, '\\', '_', '_', (char)2, '\\', '%', 'c', '%'});

		Assert.assertTrue(
			s,
			StringUtil.wildcardMatches(
				s, wildcard, CharPool.UNDERLINE, CharPool.PERCENT,
				CharPool.BACK_SLASH, true));
	}

	private int _getOffset(char c) {
		return c - CharPool.LOWER_CASE_A;
	}

}
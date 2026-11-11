/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.string;

import com.liferay.portal.kernel.test.randomizerbumpers.RandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alexander Chow
 * @author Shuyang Zhou
 * @author Hugo Huijser
 * @author Preston Crary
 */
public class StringUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.clear();

					assertClasses.add(CharPool.class);
					assertClasses.add(StringPool.class);
				}

				@Override
				public List<Method> getAssertMethods()
					throws ReflectiveOperationException {

					List<Method> assertMethods = new ArrayList<>(
						Arrays.asList(StringUtil.class.getDeclaredMethods()));

					assertMethods.removeAll(
						Arrays.asList(
							StringUtil.class.getDeclaredMethod(
								"read", ClassLoader.class, String.class),
							StringUtil.class.getDeclaredMethod(
								"read", ClassLoader.class, String.class,
								boolean.class)));

					return assertMethods;
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@Test
	public void testConstructors() {
		new CharPool();
		new StringPool();
		new StringUtil();
	}

	@Test
	public void testEqualsIgnoreCase() {

		// char

		Assert.assertFalse(StringUtil.equalsIgnoreCase('!', 'a'));
		Assert.assertFalse(StringUtil.equalsIgnoreCase('B', 'a'));
		Assert.assertFalse(StringUtil.equalsIgnoreCase('a', '!'));
		Assert.assertFalse(StringUtil.equalsIgnoreCase('a', '{'));
		Assert.assertFalse(StringUtil.equalsIgnoreCase('a', 'ⴀ'));
		Assert.assertFalse(StringUtil.equalsIgnoreCase('{', 'a'));
		Assert.assertTrue(StringUtil.equalsIgnoreCase('A', 'a'));
		Assert.assertTrue(StringUtil.equalsIgnoreCase('a', 'A'));
		Assert.assertTrue(StringUtil.equalsIgnoreCase('a', 'a'));
		Assert.assertTrue(StringUtil.equalsIgnoreCase('ⴀ', 'Ⴀ'));
		Assert.assertTrue(StringUtil.equalsIgnoreCase((char)305, 'i'));

		// java.lang.String

		Assert.assertFalse(StringUtil.equalsIgnoreCase("!", "A"));
		Assert.assertFalse(
			StringUtil.equalsIgnoreCase("HELLO WORLD", "HELLO WORLD1"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("HELLO WORLD", null));
		Assert.assertFalse(StringUtil.equalsIgnoreCase(null, "HELLO WORLD"));
		Assert.assertTrue(
			StringUtil.equalsIgnoreCase("Hello \n World", "hello \n worlD"));

		String string = "HELLO WORLD";

		Assert.assertTrue(StringUtil.equalsIgnoreCase(string, string));
	}

	@Test
	public void testForEachLine() {
		_assertForEachLine(" a \n b ", " a ", " b ");
		_assertForEachLine("\n", StringPool.BLANK);
		_assertForEachLine("\r\n\n", StringPool.BLANK, StringPool.BLANK);
		_assertForEachLine("a", "a");
		_assertForEachLine("a\n", "a");
		_assertForEachLine("a\n\nb", "a", StringPool.BLANK, "b");
		_assertForEachLine("a\n\rb", "a", StringPool.BLANK, "b");
		_assertForEachLine("a\nb", "a", "b");
		_assertForEachLine("a\r", "a");
		_assertForEachLine("a\r\n", "a");
		_assertForEachLine("a\r\n\r\nb\n", "a", StringPool.BLANK, "b");
		_assertForEachLine("a\r\nb", "a", "b");
		_assertForEachLine("a\r\rb", "a", StringPool.BLANK, "b");
		_assertForEachLine("a\rb", "a", "b");
		_assertForEachLine(StringPool.BLANK);
	}

	@Test
	public void testForEachLineUntil() {
		Assert.assertFalse(
			StringUtil.forEachLineUntil(StringPool.BLANK, line -> true));

		List<String> lines = new ArrayList<>();

		Assert.assertTrue(
			StringUtil.forEachLineUntil(
				"a\nb\r\nc",
				line -> {
					lines.add(line);

					return line.equals("b");
				}));
		Assert.assertEquals(Arrays.asList("a", "b"), lines);

		lines.clear();

		Assert.assertTrue(
			StringUtil.forEachLineUntil(
				"a\nb",
				line -> {
					lines.add(line);

					return line.equals("b");
				}));
		Assert.assertEquals(Arrays.asList("a", "b"), lines);

		lines.clear();

		Assert.assertFalse(
			StringUtil.forEachLineUntil(
				"a\nb\n",
				line -> {
					lines.add(line);

					return false;
				}));
		Assert.assertEquals(Arrays.asList("a", "b"), lines);
	}

	@Test
	public void testMerge() {
		Assert.assertNull(StringUtil.merge((boolean[])null, null));
		Assert.assertNull(StringUtil.merge((byte[])null, null));
		Assert.assertNull(StringUtil.merge((char[])null, null));
		Assert.assertNull(StringUtil.merge((double[])null, null));
		Assert.assertNull(StringUtil.merge((float[])null, null));
		Assert.assertNull(StringUtil.merge((int[])null, null));
		Assert.assertNull(StringUtil.merge((long[])null, null));
		Assert.assertNull(StringUtil.merge((short[])null, null));
		Assert.assertNull(StringUtil.merge((String[])null, null));
		Assert.assertNull(StringUtil.merge((Object[])null, null));
		Assert.assertNull(StringUtil.merge((Object[])null, null, null));
		Assert.assertNull(StringUtil.merge((ArrayList<Object>)null, null));
		Assert.assertNull(
			StringUtil.merge((ArrayList<Object>)null, null, null));
		Assert.assertNull(StringUtil.merge((Collection<Object>)null, null));
		Assert.assertNull(
			StringUtil.merge((Collection<Object>)null, null, null));

		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new boolean[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new byte[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new char[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new double[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new float[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new int[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new long[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new short[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new String[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new Object[0], null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new Object[0], null, null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new ArrayList<>(), null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new ArrayList<>(), null, null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new LinkedList<>(), null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new LinkedList<>(), null, null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(Arrays.asList(), null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(Arrays.asList(), null, null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new HashSet<>(), null));
		Assert.assertEquals(
			StringPool.BLANK, StringUtil.merge(new HashSet<>(), null, null));

		Assert.assertEquals(
			StringPool.TRUE, StringUtil.merge(new boolean[] {true}, null));
		Assert.assertEquals("1", StringUtil.merge(new byte[] {1}, null));
		Assert.assertEquals("a", StringUtil.merge(new char[] {'a'}, null));
		Assert.assertEquals("1.2", StringUtil.merge(new double[] {1.2D}, null));
		Assert.assertEquals("1.0", StringUtil.merge(new float[] {1.0F}, null));
		Assert.assertEquals("2", StringUtil.merge(new int[] {2}, null));
		Assert.assertEquals("3", StringUtil.merge(new long[] {3L}, null));
		Assert.assertEquals("4", StringUtil.merge(new short[] {4}, null));
		Assert.assertEquals(
			"abc", StringUtil.merge(new String[] {"abc"}, null));
		Assert.assertEquals("4", StringUtil.merge(new Object[] {4}, null));
		Assert.assertEquals(
			"5x",
			StringUtil.merge(
				new Object[] {5}, object -> object.toString() + "x", null));
		Assert.assertEquals(
			"a", StringUtil.merge(new ArrayList<>(Arrays.asList("a")), null));
		Assert.assertEquals(
			"ax",
			StringUtil.merge(
				new ArrayList<>(Arrays.asList("a")), s -> s + "x", null));
		Assert.assertEquals("a", StringUtil.merge(Arrays.asList("a"), null));
		Assert.assertEquals(
			"ax", StringUtil.merge(Arrays.asList("a"), s -> s + "x", null));
		Assert.assertEquals(
			"a", StringUtil.merge(new HashSet<>(Arrays.asList("a")), null));
		Assert.assertEquals(
			"ax",
			StringUtil.merge(
				new HashSet<>(Arrays.asList("a")), s -> s + "x", null));

		Assert.assertEquals(
			"true,false",
			StringUtil.merge(new boolean[] {true, false}, StringPool.COMMA));
		Assert.assertEquals(
			"1,2", StringUtil.merge(new byte[] {1, 2}, StringPool.COMMA));
		Assert.assertEquals(
			"a,b", StringUtil.merge(new char[] {'a', 'b'}, StringPool.COMMA));
		Assert.assertEquals(
			"1.2,3.4",
			StringUtil.merge(new double[] {1.2D, 3.4D}, StringPool.COMMA));
		Assert.assertEquals(
			"1.0,2.0",
			StringUtil.merge(new float[] {1.0F, 2.0F}, StringPool.COMMA));
		Assert.assertEquals(
			"2,3", StringUtil.merge(new int[] {2, 3}, StringPool.COMMA));
		Assert.assertEquals(
			"3,4", StringUtil.merge(new long[] {3L, 4L}, StringPool.COMMA));
		Assert.assertEquals(
			"4,5", StringUtil.merge(new short[] {4, 5}, StringPool.COMMA));
		Assert.assertEquals(
			"abc,def",
			StringUtil.merge(new String[] {"abc", "def"}, StringPool.COMMA));
		Assert.assertEquals(
			"4,x", StringUtil.merge(new Object[] {4, "x"}, StringPool.COMMA));
		Assert.assertEquals(
			"5x,ax",
			StringUtil.merge(
				new Object[] {5, 'a'}, object -> object.toString() + "x",
				StringPool.COMMA));
		Assert.assertEquals(
			"a,b",
			StringUtil.merge(
				new ArrayList<>(Arrays.asList("a", "b")), StringPool.COMMA));
		Assert.assertEquals(
			"ax,bx",
			StringUtil.merge(
				new ArrayList<>(Arrays.asList("a", "b")), s -> s + "x",
				StringPool.COMMA));
		Assert.assertEquals(
			"a,b", StringUtil.merge(Arrays.asList("a", "b"), StringPool.COMMA));
		Assert.assertEquals(
			"ax,bx",
			StringUtil.merge(
				Arrays.asList("a", "b"), s -> s + "x", StringPool.COMMA));
		Assert.assertEquals(
			"a,b",
			StringUtil.merge(
				new HashSet<>(Arrays.asList("a", "b")), StringPool.COMMA));
		Assert.assertEquals(
			"ax,bx",
			StringUtil.merge(
				new HashSet<>(Arrays.asList("a", "b")), s -> s + "x",
				StringPool.COMMA));
	}

	@Test
	public void testRead() throws Exception {
		Assert.assertEquals(
			StringPool.BLANK,
			StringUtil.read(new ByteArrayInputStream(new byte[0])));

		String string = RandomTestUtil.randomString(
			8193,
			(RandomizerBumper<String>)randomValue ->
				(randomValue.indexOf(CharPool.RETURN) == -1) &&
				!Character.isWhitespace(randomValue.charAt(0)) &&
				!Character.isWhitespace(randomValue.charAt(8192)));

		Assert.assertEquals(
			string,
			StringUtil.read(new ByteArrayInputStream(string.getBytes())));
	}

	@Test
	public void testReplace() {

		// char

		Assert.assertEquals(
			"127_0_0_1", StringUtil.replace("127.0.0.1", '.', '_'));
		Assert.assertNull(StringUtil.replace(null, '.', '_'));

		// java.lang.String

		Assert.assertEquals(
			"hello world",
			StringUtil.replace("hello world", StringPool.BLANK, "HELLO", 0));
		Assert.assertEquals(
			"hello world", StringUtil.replace("hello world", null, "HELLO", 0));
		Assert.assertEquals(
			"world", StringUtil.replace("hello world", "hello ", null, 0));
		Assert.assertNull(StringUtil.replace(null, "hello", "HELLO", 0));
	}

	@Test
	public void testSplit() {
		Assert.assertSame(Collections.emptyList(), StringUtil.split(null));
		Assert.assertSame(
			Collections.emptyList(), StringUtil.split(StringPool.BLANK));
		Assert.assertSame(
			Collections.emptyList(), StringUtil.split(StringPool.SPACE));

		Assert.assertEquals(
			Collections.<String>emptyList(),
			StringUtil.split(StringPool.COMMA));
		Assert.assertEquals(
			Collections.<String>emptyList(), StringUtil.split(",,,"));

		Assert.assertEquals(
			Collections.singletonList("test"), StringUtil.split("test"));
		Assert.assertEquals(
			Collections.singletonList("test"), StringUtil.split("test,"));
		Assert.assertEquals(
			Collections.singletonList("test"), StringUtil.split(",test"));

		Assert.assertEquals(
			Arrays.asList("test1", "test2"),
			StringUtil.split("test1-test2", CharPool.DASH));
	}

	private void _assertForEachLine(String s, String... expectedLines) {
		List<String> lines = new ArrayList<>();

		StringUtil.forEachLine(s, lines::add);

		Assert.assertEquals(Arrays.asList(expectedLines), lines);
	}

}
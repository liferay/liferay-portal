/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.mentions.matcher.MentionsMatcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class DefaultMentionsMatcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_mentionsMatcher = new DefaultMentionsMatcher();
	}

	@Test
	public void testMatchBBCodeAtMention() {
		_assertEquals("user1", _mentionsMatcher.match("[span]@user1[span]"));
	}

	@Test
	public void testMatchBBCodeSpecialCharacters() {
		_assertEquals(
			_SCREEN_NAME_WITH_SPECIAL_CHARS,
			_mentionsMatcher.match(
				"[span]@" + _SCREEN_NAME_WITH_SPECIAL_CHARS + "[span]"));
	}

	@Test
	public void testMatchBBCodeXMLEntityMention() {
		_assertEquals(
			"user1", _mentionsMatcher.match("[span]&#64;user1[span]"));
	}

	@Test
	public void testMatchHTMLAtMention() {
		_assertEquals("user1", _mentionsMatcher.match("<span>@user1</span>"));
	}

	@Test
	public void testMatchHTMLSpecialCharacters() {
		_assertEquals(
			_SCREEN_NAME_WITH_SPECIAL_CHARS,
			_mentionsMatcher.match(
				"<span>@" + _SCREEN_NAME_WITH_SPECIAL_CHARS + "</span>"));
	}

	@Test
	public void testMatchHTMLXMLEntityMention() {
		_assertEquals(
			"user1", _mentionsMatcher.match("<span>&#64;user1</span>"));
	}

	@Test
	public void testMatchMixedContent() {
		String content =
			"Lorem ipsum @user1 dolor sit amet, consectetur adipiscing elit " +
				"Sed non venenatis &#64;user2 justo. Morbi augue mauris, " +
					"suscipit ]@user3 tempus@notthis @@neitherthis et,>@user4";

		_assertEquals(
			Arrays.asList("user1", "user2", "user3", "user4"),
			_mentionsMatcher.match(content));
	}

	@Test
	public void testMatchSimpleAtMention() {
		_assertEquals("user1", _mentionsMatcher.match("@user1"));
	}

	@Test
	public void testMatchSimpleAtMentions() {
		_assertEquals(
			Arrays.asList("user1", "user2"),
			_mentionsMatcher.match("@user1 @user2"));
	}

	@Test
	public void testMatchSimpleXMLEntityMention() {
		_assertEquals("user1", _mentionsMatcher.match("&#64;user1"));
	}

	@Test
	public void testMatchSimpleXMLEntityMentions() {
		_assertEquals(
			Arrays.asList("user1", "user2"),
			_mentionsMatcher.match("&#64;user1 &#64;user2"));
	}

	private <T> void _assertEquals(
		Iterable<T> iterable1, Iterable<T> iterable2) {

		Iterator<T> iterator1 = iterable1.iterator();
		Iterator<T> iterator2 = iterable2.iterator();

		int pos = 0;

		while (iterator1.hasNext()) {
			String message = String.format(
				"The second iterator has fewer elements than the first one " +
					"(exhausted at position %d)",
				pos);

			Assert.assertTrue(message, iterator2.hasNext());

			T value1 = iterator1.next();
			T value2 = iterator2.next();

			message = String.format(
				"Elements differ at position %d because '%s' does not equal " +
					"'%s'",
				pos, value1, value2);

			Assert.assertEquals(message, value1, value2);

			pos++;
		}

		String message = String.format(
			"The first iterator has fewer elements than the second one " +
				"(exhausted at position %d)",
			pos);

		Assert.assertTrue(message, !iterator2.hasNext());
	}

	private <T> void _assertEquals(T value, Iterable<T> iterable) {
		_assertEquals(Collections.singletonList(value), iterable);
	}

	private static final String _SCREEN_NAME_SPECIAL_CHARS = "-._";

	private static final String _SCREEN_NAME_WITH_SPECIAL_CHARS =
		"user" + _SCREEN_NAME_SPECIAL_CHARS;

	private MentionsMatcher _mentionsMatcher;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.mentions.matcher.MentionsMatcher;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DefaultMentionsNotifierTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetMentionsMatcher() {
		String className = RandomTestUtil.randomString();
		DefaultMentionsNotifier defaultMentionsNotifier =
			new DefaultMentionsNotifier();

		MentionsMatcher mentionsMatcher = new MentionsMatcher() {

			@Override
			public Iterable<String> match(String s) {
				return Collections.emptyList();
			}

		};

		ReflectionTestUtil.setFieldValue(
			defaultMentionsNotifier, "_serviceTrackerMap",
			ProxyUtil.newProxyInstance(
				ServiceTrackerMap.class.getClassLoader(),
				new Class<?>[] {ServiceTrackerMap.class},
				(proxy, method, arguments) -> {
					if (className.equals(arguments[0])) {
						return mentionsMatcher;
					}

					return null;
				}));

		Assert.assertSame(
			mentionsMatcher,
			defaultMentionsNotifier.getMentionsMatcher(className));

		MentionsMatcher defaultMentionsMatcher =
			defaultMentionsNotifier.getMentionsMatcher(
				RandomTestUtil.randomString());

		Assert.assertEquals(
			DefaultMentionsMatcher.class, defaultMentionsMatcher.getClass());
	}

}
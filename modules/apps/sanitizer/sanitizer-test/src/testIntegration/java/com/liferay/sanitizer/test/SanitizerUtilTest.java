/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sanitizer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@RunWith(Arquillian.class)
public class SanitizerUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSanitize() throws Exception {
		String string = RandomTestUtil.randomString();

		Assert.assertEquals(
			"&#34;" + string + "&#34;",
			SanitizerUtil.sanitize(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(), JournalArticle.class.getName(), 0,
				ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
				"\"" + string + "\"",
				HashMapBuilder.<String, Object>put(
					"discussion", Boolean.TRUE
				).build()));
		Assert.assertEquals(
			"&quot;" + string + "&quot;",
			SanitizerUtil.sanitize(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(), User.class.getName(), 0,
				ContentTypes.TEXT_HTML, "\"" + string + "\""));
		Assert.assertEquals(
			"<iframe sandbox=\"\">" + string + "</iframe>",
			SanitizerUtil.sanitize(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(), BlogsEntry.class.getName(), 0,
				ContentTypes.TEXT_HTML, "<iframe>" + string + "</iframe>"));
	}

}
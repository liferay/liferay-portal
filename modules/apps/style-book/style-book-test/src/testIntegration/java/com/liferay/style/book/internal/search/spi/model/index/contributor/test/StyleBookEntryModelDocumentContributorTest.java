/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class StyleBookEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testContribute() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry();

		Document document = new DocumentImpl();

		_styleBookEntryModelDocumentContributor.contribute(
			document, styleBookEntry);

		Assert.assertEquals(styleBookEntry.getName(), document.get(Field.NAME));
		Assert.assertEquals(
			styleBookEntry.getName(), document.get(Field.TITLE));
		Assert.assertEquals(
			String.valueOf(styleBookEntry.isDefaultStyleBookEntry()),
			document.get("defaultStyleBookEntry"));
		Assert.assertEquals(
			String.valueOf(styleBookEntry.isHead()), document.get("head"));
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryKey(),
			document.get("styleBookEntryKey"));
		Assert.assertEquals(
			styleBookEntry.getThemeId(), document.get("themeId"));
	}

	private StyleBookEntry _addStyleBookEntry() throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject(
		filter = "indexer.class.name=com.liferay.style.book.model.StyleBookEntry"
	)
	private ModelDocumentContributor<StyleBookEntry>
		_styleBookEntryModelDocumentContributor;

}
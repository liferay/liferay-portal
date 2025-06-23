/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class JournalArticleItemSelectorViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetPayloadWithAssetType() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_getPayload(journalArticle));

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			journalArticle.getDDMStructureId());

		Assert.assertEquals(
			ddmStructure.getName(LocaleUtil.getDefault()),
			jsonObject.getString("assetType"));
	}

	@Test
	public void testGetPayloadWithoutAssetType() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		assetEntry.setClassTypeId(0);

		_assetEntryLocalService.updateAssetEntry(assetEntry);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.web.internal.display.context." +
					"JournalArticleItemSelectorViewDisplayContext",
				LoggerTestUtil.ERROR)) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				_getPayload(journalArticle));

			Assert.assertEquals(
				StringPool.BLANK, jsonObject.getString("assetType"));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get class type for article: " +
					journalArticle.getArticleId(),
				logEntry.getMessage());
		}
	}

	private String _getPayload(JournalArticle journalArticle)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.getDefault());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_itemSelectorView.renderHTML(
			httpServletRequest, new MockHttpServletResponse(), null, null, null,
			false);

		return ReflectionTestUtil.invoke(
			httpServletRequest.getAttribute(
				"JOURNAL_ARTICLE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT"),
			"getPayload", new Class<?>[] {JournalArticle.class},
			journalArticle);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.item.selector.JournalArticleItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<InfoItemItemSelectorCriterion> _itemSelectorView;

}
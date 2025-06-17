/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.list.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class AssetInfoListRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		JournalArticle article1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry1 = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(), article1.getResourcePrimKey());

		_assetEntries.add(assetEntry1);

		JournalArticle article2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry2 = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(), article2.getResourcePrimKey());

		_assetEntries.add(assetEntry2);

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_infoListRenderer =
			(InfoListRenderer<AssetEntry>)
				_infoListRendererRegistry.getInfoListRenderer(
					"com.liferay.asset.info.internal.list.renderer." +
						"UnstyledAssetEntryBasicInfoListRenderer");
		_layout = LayoutTestUtil.addTypePortletLayout(_group);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAssetInfoListRendererAbstract() throws Exception {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		DefaultInfoListRendererContext defaultInfoListRendererContext =
			new DefaultInfoListRendererContext(
				_getHttpServletRequest(),
				new PipingServletResponse(
					mockHttpServletResponse, unsyncStringWriter));

		defaultInfoListRendererContext.setListItemRendererKey(
			"com.liferay.asset.internal.info.renderer." +
				"AssetEntryAbstractInfoItemRenderer");

		_infoListRenderer.render(_assetEntries, defaultInfoListRendererContext);

		_assertContentSummaryExists(unsyncStringWriter.toString());
	}

	@Test
	public void testAssetInfoListRendererFullContent() throws Exception {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		DefaultInfoListRendererContext defaultInfoListRendererContext =
			new DefaultInfoListRendererContext(
				_getHttpServletRequest(),
				new PipingServletResponse(
					mockHttpServletResponse, unsyncStringWriter));

		defaultInfoListRendererContext.setListItemRendererKey(
			"com.liferay.asset.internal.info.renderer." +
				"AssetEntryFullContentInfoItemRenderer");

		_infoListRenderer.render(_assetEntries, defaultInfoListRendererContext);

		_assertContentExists(unsyncStringWriter.toString());
	}

	@Test
	public void testAssetInfoListRendererTitle() throws Exception {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		DefaultInfoListRendererContext defaultInfoListRendererContext =
			new DefaultInfoListRendererContext(
				_getHttpServletRequest(),
				new PipingServletResponse(
					mockHttpServletResponse, unsyncStringWriter));

		defaultInfoListRendererContext.setListItemRendererKey(
			"com.liferay.asset.info.internal.item.renderer." +
				"AssetEntryTitleInfoItemRenderer");

		_infoListRenderer.render(_assetEntries, defaultInfoListRendererContext);

		_assertContentTitleExists(unsyncStringWriter.toString());
	}

	private void _assertContentExists(String content) {
		for (AssetEntry assetEntry : _assetEntries) {
			JournalArticle article =
				_journalArticleLocalService.fetchLatestArticle(
					assetEntry.getClassPK());

			String articleContent = _journalContent.getContent(
				article.getGroupId(), article.getArticleId(), Constants.VIEW,
				article.getDefaultLanguageId());

			Assert.assertThat(
				content, CoreMatchers.containsString(articleContent));
		}
	}

	private void _assertContentSummaryExists(String content) {
		for (AssetEntry assetEntry : _assetEntries) {
			Assert.assertThat(
				content,
				CoreMatchers.containsString(
					assetEntry.getSummary(assetEntry.getDefaultLanguageId())));
		}
	}

	private void _assertContentTitleExists(String content) {
		for (AssetEntry assetEntry : _assetEntries) {
			Assert.assertThat(
				content,
				CoreMatchers.containsString(
					assetEntry.getTitle(assetEntry.getDefaultLanguageId())));
		}
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		return mockHttpServletRequest;
	}

	private final List<AssetEntry> _assetEntries = new ArrayList<>();

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private InfoListRenderer<AssetEntry> _infoListRenderer;

	@Inject
	private InfoListRendererRegistry _infoListRendererRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalContent _journalContent;

	private Layout _layout;

}
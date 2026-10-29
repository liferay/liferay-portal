/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.asset.model.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class JournalArticleAssetRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.fetchCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetURLViewInContext() throws Exception {
		_testGetURLViewInContext();
		_testGetURLViewInContextWhenArticleIsPending();
	}

	@Test
	public void testGetURLViewInContextWithLayoutUuid() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), true);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group,
			_getDynamicSelectionAssetPublisherPortletLayout());

		Assert.assertNull(
			_getURLViewInContext(
				journalArticle.getResourcePrimKey(), themeDisplay));

		Layout layout = _addDefaultAssetPublisherLayout();

		journalArticle = _journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), journalArticle.getGroupId(),
			journalArticle.getFolderId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), journalArticle.getTitleMap(),
			journalArticle.getDescriptionMap(), journalArticle.getContent(),
			layout.getUuid(), _serviceContext);

		String viewInContextURL = _getURLViewInContext(
			journalArticle.getResourcePrimKey(), themeDisplay);

		_assertURL(
			viewInContextURL, JournalArticleConstants.CANONICAL_URL_SEPARATOR,
			journalArticle.getUrlTitle());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertEquals(
			viewInContextURL,
			_getURLViewInContext(
				journalArticle.getResourcePrimKey(), themeDisplay));
	}

	private String _addAssetPublisherPortletToLayout(Layout layout)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private Layout _addDefaultAssetPublisherLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		ContentLayoutTestUtil.publishLayout(
			_addDefaultAssetPublisherPortletToLayout(draftLayout), layout);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private Layout _addDefaultAssetPublisherPortletToLayout(Layout layout)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID,
			_addAssetPublisherPortletToLayout(layout));

		return _layoutLocalService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());
	}

	private void _assertURL(
			JournalArticle article, boolean hasVersion,
			ThemeDisplay themeDisplay)
		throws Exception {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					article.getCompanyId(), JournalArticle.class.getName());

		String viewInContextURL = _getURLViewInContext(
			article.getResourcePrimKey(), themeDisplay);

		_assertURL(
			viewInContextURL, layoutDisplayPageProvider.getURLSeparator(),
			article.getUrlTitle());

		String version = HttpComponentsUtil.getParameter(
			viewInContextURL, "version");

		if (hasVersion) {
			Assert.assertNotNull(version);
			Assert.assertEquals(
				article.getVersion(), GetterUtil.getDouble(version),
				GetterUtil.DEFAULT_DOUBLE);
		}
		else {
			Assert.assertEquals(StringPool.BLANK, version);
		}
	}

	private void _assertURL(String url, String urlSeparator, String urlTitle) {
		Assert.assertNotNull(url);

		int index = url.indexOf(urlSeparator);

		Assert.assertTrue(index >= 0);

		Assert.assertEquals(
			urlTitle,
			HttpComponentsUtil.getPath(
				url.substring(index + urlSeparator.length())));
	}

	private Layout _getDynamicSelectionAssetPublisherPortletLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(
				draftLayout, _addAssetPublisherPortletToLayout(draftLayout));

		portletPreferences.setValue("selectionStyle", "dynamic");

		portletPreferences.store();

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private LiferayPortletRequest _getLiferayPortletRequest(
		ThemeDisplay themeDisplay) {

		MockRenderRequest renderRequest = new MockLiferayPortletRenderRequest();

		renderRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return _portal.getLiferayPortletRequest(renderRequest);
	}

	private String _getURLViewInContext(
			long resourcePrimKey, ThemeDisplay themeDisplay)
		throws Exception {

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetRenderer<JournalArticle> assetRenderer =
			assetRendererFactory.getAssetRenderer(resourcePrimKey);

		return assetRenderer.getURLViewInContext(
			_getLiferayPortletRequest(themeDisplay), null, null);
	}

	private void _testGetURLViewInContext() throws Exception {
		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), false);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group,
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()));

		_assertURL(article, true, themeDisplay);

		article = JournalTestUtil.updateArticleWithWorkflow(article, true);

		_assertURL(article, false, themeDisplay);
	}

	private void _testGetURLViewInContextWhenArticleIsPending()
		throws Exception {

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), _group.getCompanyId(),
			_group.getGroupId(), JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL, "Single Approver", 1);

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				article.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group,
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()));

		_assertURL(article, true, themeDisplay);

		article = JournalArticleLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), article,
			WorkflowConstants.STATUS_APPROVED, null,
			ServiceContextTestUtil.getServiceContext(), new HashMap<>());

		_assertURL(article, false, themeDisplay);

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), _group.getCompanyId(),
			_group.getGroupId(), JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL, null);
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
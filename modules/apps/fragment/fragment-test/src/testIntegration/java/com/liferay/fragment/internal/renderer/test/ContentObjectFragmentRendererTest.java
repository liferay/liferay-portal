/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

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
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class ContentObjectFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), JournalArticleConstants.CLASS_NAME_ID_DEFAULT);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@FeatureFlag("LPD-39437")
	@Test
	public void testRenderAssertAnalyticsAttributesInEditMode()
		throws Exception {

		String content = _render(
			_addFragmentEntryLink(), FragmentEntryLinkConstants.EDIT);

		Assert.assertFalse(content.contains("data-analytics-asset-action"));
	}

	@FeatureFlag("LPD-39437")
	@Test
	public void testRenderAssertAnalyticsAttributesInViewMode()
		throws Exception {

		String content = _render(
			_addFragmentEntryLink(), FragmentEntryLinkConstants.VIEW);

		Assert.assertTrue(
			content.contains("data-analytics-asset-action=\"view\""));
		Assert.assertTrue(
			content.contains(
				"data-analytics-asset-id=\"" +
					_journalArticle.getResourcePrimKey() + "\""));
		Assert.assertTrue(
			content.contains(
				"data-analytics-asset-subtype=\"" +
					_journalArticle.getDDMStructureId() + "\""));
		Assert.assertTrue(
			content.contains(
				"data-analytics-asset-title=\"" +
					_journalArticle.getTitle(LocaleUtil.US) + "\""));
		Assert.assertTrue(
			content.contains(
				"data-analytics-asset-type=\"" +
					JournalArticle.class.getName() + "\""));
	}

	@Test
	@TestInfo("LPD-55325")
	public void testRenderContentInCollectionDisplay() throws Exception {
		Layout draftLayout = _layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		ContentLayoutTestUtil.addCollectionDisplayToLayout(
			JSONUtil.put(
				"itemType", AssetEntry.class.getName()
			).put(
				"key",
				"com.liferay.asset.internal.info.collection.provider." +
					"RecentContentInfoCollectionProvider"
			).put(
				"type", InfoListProviderItemSelectorReturnType.class.getName()
			),
			draftLayout, _layoutStructureProvider, null, null, 0,
			segmentsExperienceId,
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"itemSelector",
						JSONUtil.put(
							"template",
							JSONUtil.put(
								"infoItemRendererKey",
								"com.liferay.asset.info.internal.item." +
									"renderer." +
										"AssetEntryTitleInfoItemRenderer")))
				).toString(),
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			segmentsExperienceId);

		Assert.assertTrue(
			html.contains(
				_journalArticle.getTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	@TestInfo("LPS-173440")
	public void testRenderContentWithoutPermissionsInEditMode()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(UserTestUtil.addUser()));

			Role guestRole = RoleLocalServiceUtil.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST);

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				TestPropsValues.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_journalArticle.getResourcePrimKey()),
				guestRole.getRoleId(), ActionKeys.VIEW);

			String content = _render(
				fragmentEntryLink, FragmentEntryLinkConstants.EDIT);

			Assert.assertTrue(
				content.contains(
					LanguageUtil.get(
						LocaleUtil.getSiteDefault(),
						"this-content-cannot-be-displayed-due-to-permission-" +
							"restrictions")));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	@TestInfo("LPS-173440")
	public void testRenderContentWithoutPermissionsInViewMode()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(UserTestUtil.addUser()));

			Role guestRole = RoleLocalServiceUtil.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST);

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
				TestPropsValues.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_journalArticle.getResourcePrimKey()),
				guestRole.getRoleId(), ActionKeys.VIEW);

			String content = _render(
				fragmentEntryLink, FragmentEntryLinkConstants.VIEW);

			Assert.assertTrue(content.isEmpty());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testRenderDeletedContentInEditMode() throws Exception {
		_journalArticleLocalService.deleteArticle(_journalArticle);

		String content = _render(
			_addFragmentEntryLink(), FragmentEntryLinkConstants.EDIT);

		Assert.assertTrue(
			content.contains(
				LanguageUtil.get(
					LocaleUtil.getSiteDefault(),
					"the-selected-content-is-no-longer-available.-please-" +
						"select-another")));
	}

	@Test
	public void testRenderDeletedContentInViewMode() throws Exception {
		_journalArticleLocalService.deleteArticle(_journalArticle);

		String content = _render(
			_addFragmentEntryLink(), FragmentEntryLinkConstants.VIEW);

		Assert.assertTrue(content.isEmpty());
	}

	@Test
	@TestInfo("LPD-50826")
	public void testRenderDraftContentInViewMode() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addUnmappedFragmentEntryLink();

		String content = _render(fragmentEntryLink, _journalArticle);

		Assert.assertTrue(
			content.contains(
				_journalArticle.getTitle(LocaleUtil.getSiteDefault())));

		String newTitle = RandomTestUtil.randomString();

		JournalArticle draftArticle = JournalTestUtil.updateArticle(
			_journalArticle, newTitle, _journalArticle.getContent(), true,
			false, _serviceContext);

		content = _render(fragmentEntryLink, draftArticle);

		Assert.assertTrue(
			content.contains(
				draftArticle.getTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	@TestInfo("LPD-53199")
	public void testRenderMappedContentInViewMode() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), JournalArticleConstants.CLASS_NAME_ID_DEFAULT);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			journalArticle);

		String content = _render(fragmentEntryLink, _journalArticle);

		Assert.assertTrue(
			content.contains(
				journalArticle.getTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	public void testRenderRestoredContentInEditMode() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		_journalArticle = _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), _journalArticle);

		String content = _render(
			fragmentEntryLink, FragmentEntryLinkConstants.EDIT);

		Assert.assertTrue(
			content.contains(
				LanguageUtil.get(
					LocaleUtil.getSiteDefault(),
					"the-selected-content-is-no-longer-available.-please-" +
						"select-another")));

		_journalArticleLocalService.restoreArticleFromTrash(
			TestPropsValues.getUserId(), _journalArticle);

		content = _render(fragmentEntryLink, FragmentEntryLinkConstants.EDIT);

		Assert.assertTrue(
			content.contains(
				_journalArticle.getTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	public void testRenderRestoredContentInViewMode() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		_journalArticle = _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), _journalArticle);

		String content = _render(
			fragmentEntryLink, FragmentEntryLinkConstants.VIEW);

		Assert.assertTrue(content.isEmpty());

		_journalArticleLocalService.restoreArticleFromTrash(
			TestPropsValues.getUserId(), _journalArticle);

		content = _render(fragmentEntryLink, FragmentEntryLinkConstants.VIEW);

		Assert.assertTrue(
			content.contains(
				_journalArticle.getTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	public void testRenderWithoutConfiguredContentInViewMode()
		throws Exception {

		String content = _render(
			_addFragmentEntryLink(StringPool.BLANK, _layout.getPlid()),
			FragmentEntryLinkConstants.VIEW);

		Assert.assertTrue(content.isEmpty());
	}

	private FragmentEntryLink _addFragmentEntryLink() throws Exception {
		return _addFragmentEntryLink(_journalArticle);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			JournalArticle journalArticle)
		throws Exception {

		TemplateEntry templateEntry = TemplateTestUtil.addTemplateEntry(
			JournalArticle.class.getName(),
			String.valueOf(journalArticle.getDDMStructureId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			TemplateTestUtil.getSampleScriptFTL("JournalArticle_title"),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _addFragmentEntryLink(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"itemSelector",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						String.valueOf(
							_portal.getClassNameId(
								JournalArticle.class.getName()))
					).put(
						"classPK",
						String.valueOf(journalArticle.getResourcePrimKey())
					).put(
						"classTypeId",
						String.valueOf(journalArticle.getDDMStructureId())
					).put(
						"template",
						JSONUtil.put(
							"infoItemRendererKey",
							"com.liferay.template.internal.info.item." +
								"renderer.TemplateInfoItemTemplatedRenderer"
						).put(
							"templateKey",
							String.valueOf(templateEntry.getTemplateEntryId())
						)
					))
			).toString(),
			_layout.getPlid());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, long plid)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), null, null,
			null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid),
			plid, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, editableValues, StringPool.BLANK, 0,
			"com.liferay.fragment.internal.renderer." +
				"ContentObjectFragmentRenderer",
			FragmentConstants.TYPE_COMPONENT, _serviceContext);
	}

	private FragmentEntryLink _addUnmappedFragmentEntryLink() throws Exception {
		TemplateEntry templateEntry = TemplateTestUtil.addTemplateEntry(
			JournalArticle.class.getName(),
			String.valueOf(_journalArticle.getDDMStructureId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			TemplateTestUtil.getSampleScriptFTL("JournalArticle_title"),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return _addFragmentEntryLink(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"itemSelector",
					JSONUtil.put(
						"template",
						JSONUtil.put(
							"infoItemRendererKey",
							"com.liferay.template.internal.info.item." +
								"renderer.TemplateInfoItemTemplatedRenderer"
						).put(
							"templateKey",
							String.valueOf(templateEntry.getTemplateEntryId())
						)))
			).toString(),
			_layout.getPlid());
	}

	private HttpServletRequest _getMockHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			TilesUtil.DEFINITION,
			new Definition(StringPool.BLANK, new HashMap<>()));
		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(httpServletRequest));

		_serviceContext.setRequest(httpServletRequest);

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _render(
			FragmentEntryLink fragmentEntryLink, JournalArticle journalArticle)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setContextInfoItemReference(
			new InfoItemReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()));
		defaultFragmentRendererContext.setMode(FragmentEntryLinkConstants.VIEW);

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		InfoItemObjectProvider<?> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, JournalArticle.class.getName(),
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		InfoItemIdentifier infoItemIdentifier = new ClassPKInfoItemIdentifier(
			journalArticle.getResourcePrimKey());

		infoItemIdentifier.setVersion(
			String.valueOf(journalArticle.getVersion()));

		Object infoItem = infoItemObjectProvider.getInfoItem(
			infoItemIdentifier);

		httpServletRequest.setAttribute(InfoDisplayWebKeys.INFO_ITEM, infoItem);

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, JournalArticle.class.getName());

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM_DETAILS,
			infoItemDetailsProvider.getInfoItemDetails(infoItem));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext, httpServletRequest,
			mockHttpServletResponse);

		return mockHttpServletResponse.getContentAsString();
	}

	private String _render(FragmentEntryLink fragmentEntryLink, String mode)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setMode(mode);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext, _getMockHttpServletRequest(),
			mockHttpServletResponse);

		return mockHttpServletResponse.getContentAsString();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.internal.renderer.ContentObjectFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private Layout _layout;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.content.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class
	NavigationMenuSelectorEditableValuesConfigurationExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(
			_liveGroup, TestPropsValues.getUserId());

		_stagingGroup = _liveGroup.getStagingGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_stagingGroup);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	@TestInfo("LPD-67100")
	public void testNavigationMenuSelectorEditableValues() throws Exception {
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					siteNavigationMenu.getExternalReferenceCode(),
					jsonObject.get("siteNavigationMenuExternalReferenceCode"));
				Assert.assertTrue(
					Validator.isNull(
						jsonObject.get(
							"siteNavigationMenuScopeExternalReferenceCode")));

				Assert.assertNotNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode()
			).put(
				"siteNavigationMenuScopeExternalReferenceCode", StringPool.BLANK
			));
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertTrue(
					Validator.isNull(
						jsonObject.get(
							"siteNavigationMenuExternalReferenceCode")));
				Assert.assertTrue(
					Validator.isNull(
						jsonObject.get(
							"siteNavigationMenuScopeExternalReferenceCode")));

				SiteNavigationMenu liveSiteNavigationMenu =
					_siteNavigationMenuLocalService.
						getSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId());

				Assert.assertEquals(
					liveSiteNavigationMenu.getSiteNavigationMenuId(),
					jsonObject.getLong("siteNavigationMenuId"));
			},
			siteNavigationMenu -> JSONUtil.put(
				"siteNavigationMenuId",
				siteNavigationMenu.getSiteNavigationMenuId()));
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					siteNavigationMenu.getExternalReferenceCode(),
					jsonObject.get("siteNavigationMenuExternalReferenceCode"));
				Assert.assertEquals(
					0, jsonObject.getLong("siteNavigationMenuId"));
				Assert.assertTrue(
					Validator.isNull(
						jsonObject.get(
							"siteNavigationMenuScopeExternalReferenceCode")));

				Assert.assertNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode()
			).put(
				"siteNavigationMenuId", RandomTestUtil.randomLong()
			).put(
				"siteNavigationMenuScopeExternalReferenceCode", StringPool.BLANK
			));

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_testNavigationMenuSelectorEditableValues(
			companyGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					siteNavigationMenu.getExternalReferenceCode(),
					jsonObject.get("siteNavigationMenuExternalReferenceCode"));
				Assert.assertEquals(
					companyGroup.getExternalReferenceCode(),
					jsonObject.get(
						"siteNavigationMenuScopeExternalReferenceCode"));

				Assert.assertNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode()
			).put(
				"siteNavigationMenuScopeExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_stagingGroup);

		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					layout.getExternalReferenceCode(),
					jsonObject.get(
						"parentSiteNavigationMenuItemExternalReferenceCode"));

				SiteNavigationMenu liveSiteNavigationMenu =
					_siteNavigationMenuLocalService.
						getSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId());

				Assert.assertEquals(
					liveSiteNavigationMenu.getSiteNavigationMenuId(),
					jsonObject.getLong("siteNavigationMenuId"));
			},
			siteNavigationMenu -> JSONUtil.put(
				"parentSiteNavigationMenuItemExternalReferenceCode",
				layout.getExternalReferenceCode()
			).put(
				"siteNavigationMenuId",
				siteNavigationMenu.getSiteNavigationMenuId()
			));
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					0, jsonObject.getLong("parentSiteNavigationMenuItemId"));
				Assert.assertEquals(
					siteNavigationMenu.getExternalReferenceCode(),
					jsonObject.get("siteNavigationMenuExternalReferenceCode"));
				Assert.assertEquals(
					0, jsonObject.getLong("siteNavigationMenuId"));

				Assert.assertNotNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"parentSiteNavigationMenuItemId",
				layout.getExternalReferenceCode()
			).put(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode()
			));
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertEquals(
					layout.getExternalReferenceCode(),
					jsonObject.get(
						"parentSiteNavigationMenuItemExternalReferenceCode"));
				Assert.assertEquals(
					0, jsonObject.getLong("parentSiteNavigationMenuItemId"));

				Assert.assertNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"parentSiteNavigationMenuItemExternalReferenceCode",
				layout.getExternalReferenceCode()));
		_testNavigationMenuSelectorEditableValues(
			_stagingGroup.getGroupId(),
			(jsonObject, siteNavigationMenu) -> {
				Assert.assertTrue(
					Validator.isNull(
						jsonObject.get(
							"parentSiteNavigationMenuItemExternalReference" +
								"Code")));

				Layout liveLayout =
					_layoutLocalService.getLayoutByUuidAndGroupId(
						layout.getUuid(), _liveGroup.getGroupId(),
						layout.isPrivateLayout());

				Assert.assertEquals(
					liveLayout.getPlid(),
					jsonObject.getLong("parentSiteNavigationMenuItemId"));

				Assert.assertNull(
					_siteNavigationMenuLocalService.
						fetchSiteNavigationMenuByUuidAndGroupId(
							siteNavigationMenu.getUuid(),
							_liveGroup.getGroupId()));
			},
			siteNavigationMenu -> JSONUtil.put(
				"parentSiteNavigationMenuItemId", layout.getPlid()));
	}

	private void _publishLayouts() throws Exception {
		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, parameterMap);
	}

	private void _testNavigationMenuSelectorEditableValues(
			long groupId,
			UnsafeBiConsumer<JSONObject, SiteNavigationMenu, Exception>
				unsafeBiConsumer,
			UnsafeFunction<SiteNavigationMenu, JSONObject, Exception>
				unsafeFunction)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(),
				SiteNavigationConstants.TYPE_DEFAULT, true,
				ServiceContextTestUtil.getServiceContext(groupId));

		FragmentEntryLink draftFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"source", unsafeFunction.apply(siteNavigationMenu))
				).toString(),
				_fragmentRendererRegistry.getFragmentRenderer(
					"com.liferay.fragment.renderer.menu.display.internal." +
						"MenuDisplayFragmentRenderer"),
				_draftLayout, null, 0, _segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_layout.getGroupId(),
				draftFragmentEntryLink.getExternalReferenceCode(),
				_layout.getPlid());

		_publishLayouts();

		FragmentEntryLink importedFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
				fragmentEntryLink.getUuid(), _liveGroup.getGroupId());

		JSONObject editableValuesJSONObject =
			importedFragmentEntryLink.getEditableValuesJSONObject();

		JSONObject freeMarkerFragmentEntryProcessorJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		unsafeBiConsumer.accept(
			freeMarkerFragmentEntryProcessorJSONObject.getJSONObject("source"),
			siteNavigationMenu);
	}

	private Layout _draftLayout;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	private Group _stagingGroup;

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

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
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class GetLayoutReportsLayoutItemDataStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetRenderTimesDataOfContentLayoutWithCollection()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.addCollectionStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		collectionStyledLayoutStructureItem.setCollectionJSONObject(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			));

		collectionStyledLayoutStructureItem.setListStyle(
			"com.liferay.journal.web.internal.info.list.renderer." +
				"BulletedJournalArticleBasicInfoListRenderer");

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _layout.getGroupId(),
				_layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				layoutStructure.toString());

		JSONArray jsonArray = _serveResource();

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertNotNull(jsonObject);
		Assert.assertFalse(jsonObject.getBoolean("cached"));
		Assert.assertFalse(jsonObject.getBoolean("isPortlet"));
		Assert.assertEquals(
			StringPool.BLANK, jsonObject.getString("fragmentCollectionURL"));
		Assert.assertFalse(jsonObject.getBoolean("fromMaster"));
		Assert.assertEquals(
			"collection-display", jsonObject.getString("hierarchy"));
		Assert.assertNotNull(jsonObject.get("itemId"));
		Assert.assertEquals("collection", jsonObject.getString("itemType"));
		Assert.assertEquals("collection-display", jsonObject.getString("name"));
		Assert.assertNotNull(jsonObject.get("renderTime"));
	}

	@Test
	public void testGetRenderTimesDataOfContentLayoutWithCollectionInheritedFromMaster()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutStructure layoutStructure = _getLayoutStructure(
			masterLayoutPageTemplateEntry.getGroupId(),
			masterLayoutPageTemplateEntry.getPlid());

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.addCollectionStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		collectionStyledLayoutStructureItem.setCollectionJSONObject(
			JSONUtil.put(
				"classNameId", _portal.getClassNameId(AssetListEntry.class)
			).put(
				"classPK", assetListEntry.getAssetListEntryId()
			).put(
				"itemType", JournalArticle.class.getName()
			).put(
				"type", InfoListItemSelectorReturnType.class.getName()
			));

		collectionStyledLayoutStructureItem.setListStyle(
			"com.liferay.journal.web.internal.info.list.renderer." +
				"BulletedJournalArticleBasicInfoListRenderer");

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(),
				masterLayoutPageTemplateEntry.getGroupId(),
				masterLayoutPageTemplateEntry.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						masterLayoutPageTemplateEntry.getPlid()),
				layoutStructure.toString());

		_layout.setMasterLayoutPlid(masterLayoutPageTemplateEntry.getPlid());

		_layout = _layoutLocalService.updateLayout(_layout);

		JSONArray jsonArray = _serveResource();

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertNotNull(jsonObject);
		Assert.assertFalse(jsonObject.getBoolean("cached"));
		Assert.assertFalse(jsonObject.getBoolean("isPortlet"));
		Assert.assertEquals(
			StringPool.BLANK, jsonObject.get("fragmentCollectionURL"));
		Assert.assertTrue(jsonObject.getBoolean("fromMaster"));
		Assert.assertEquals(
			"collection-display", jsonObject.getString("hierarchy"));
		Assert.assertNotNull(jsonObject.get("itemId"));
		Assert.assertEquals("collection", jsonObject.getString("itemType"));
		Assert.assertEquals("collection-display", jsonObject.getString("name"));
		Assert.assertNotNull(jsonObject.get("renderTime"));
	}

	@Test
	public void testGetRenderTimesDataOfContentLayoutWithFragmentEntry()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				null, fragmentEntry.getType(), _serviceContext);

		_addFragmentStyledLayoutStructureItem(fragmentEntryLink);

		JSONArray jsonArray = _serveResource();

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertNotNull(jsonObject);
		Assert.assertTrue(jsonObject.getBoolean("cached"));
		Assert.assertFalse(jsonObject.getBoolean("isPortlet"));

		String fragmentCollectionURL = jsonObject.getString(
			"fragmentCollectionURL");

		Assert.assertNotNull(fragmentCollectionURL);
		Assert.assertTrue(
			fragmentCollectionURL.contains(
				"/fragments/fragment_collection/" +
					fragmentEntry.getFragmentCollectionId()));

		Assert.assertFalse(jsonObject.getBoolean("fromMaster"));
		Assert.assertEquals(
			fragmentEntry.getName(), jsonObject.getString("hierarchy"));
		Assert.assertNotNull(jsonObject.get("itemId"));
		Assert.assertEquals("fragment", jsonObject.getString("itemType"));
		Assert.assertEquals(
			fragmentEntry.getName(), jsonObject.getString("name"));
		Assert.assertNotNull(jsonObject.get("renderTime"));
	}

	@Test
	public void testGetRenderTimesDataOfContentLayoutWithFragmentEntryInGrid()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		LayoutStructureItem layoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem layoutStructureItem2 =
			layoutStructure.addColumnLayoutStructureItem(
				layoutStructureItem1.getItemId(), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _layout.getGroupId(),
				_layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				layoutStructure.toString());

		FragmentEntry fragmentEntry = _addFragmentEntry(true);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				null, fragmentEntry.getType(), _serviceContext);

		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink, layoutStructureItem2.getItemId());

		JSONArray jsonArray = _serveResource();

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertNotNull(jsonObject);
		Assert.assertEquals(
			"grid > " + fragmentEntry.getName(),
			jsonObject.getString("hierarchy"));
	}

	@Test
	public void testGetRenderTimesDataOfContentLayoutWithPortletFragmentEntry()
		throws Exception {

		String portletId = "com_liferay_test_portlet_LayoutTestPortlet";

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, _layout.getUserId(), _group.getGroupId(), 0, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK,
				JSONUtil.put(
					"instanceid", StringUtil.randomString()
				).put(
					"portletId", portletId
				).toString(),
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_PORTLET, _serviceContext);

		_addFragmentStyledLayoutStructureItem(fragmentEntryLink);

		JSONArray jsonArray = _serveResource();

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertNotNull(jsonObject);
		Assert.assertFalse(jsonObject.getBoolean("cached"));
		Assert.assertTrue(jsonObject.getBoolean("isPortlet"));
		Assert.assertNotNull(jsonObject.get("fragmentCollectionURL"));
		Assert.assertFalse(jsonObject.getBoolean("fromMaster"));
		Assert.assertEquals(portletId, jsonObject.getString("hierarchy"));
		Assert.assertNotNull(jsonObject.get("itemId"));
		Assert.assertEquals("fragment", jsonObject.getString("itemType"));
		Assert.assertEquals(portletId, jsonObject.getString("name"));
		Assert.assertNotNull(jsonObject.get("renderTime"));
	}

	private FragmentEntry _addFragmentEntry(boolean cacheable)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK,
			"Fragment Entry HTML", StringPool.BLANK, cacheable, null, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private void _addFragmentStyledLayoutStructureItem(
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		_addFragmentStyledLayoutStructureItem(
			fragmentEntryLink, layoutStructure.getMainItemId());
	}

	private void _addFragmentStyledLayoutStructureItem(
			FragmentEntryLink fragmentEntryLink, String parentItemId)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(), parentItemId, 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _layout.getGroupId(),
				_layout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				layoutStructure.toString());
	}

	private LayoutStructure _getLayoutStructure() {
		return _getLayoutStructure(_layout.getGroupId(), _layout.getPlid());
	}

	private LayoutStructure _getLayoutStructure(long groupId, long plid) {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(groupId, plid);

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private JSONArray _serveResource() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_l_id", String.valueOf(_layout.getPlid()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getLayoutReportsRenderTimesDataStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		return JSONFactoryUtil.createJSONArray(
			mockHttpServletResponse.getContentAsString());
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.reports.web.internal.struts.GetLayoutReportsLayoutItemDataStrutsAction"
	)
	private StrutsAction _getLayoutReportsRenderTimesDataStrutsAction;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}
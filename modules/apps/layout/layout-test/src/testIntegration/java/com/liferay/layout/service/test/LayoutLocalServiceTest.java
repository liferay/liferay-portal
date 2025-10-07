/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateLayoutExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.LayoutJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutTypeException;
import com.liferay.portal.kernel.exception.MasterLayoutException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class LayoutLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = _getServiceContext(_group);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		FriendlyURLEntryLocalServiceUtil.deleteGroupFriendlyURLEntries(
			_group.getGroupId(),
			ClassNameLocalServiceUtil.getClassNameId(User.class));
		FriendlyURLEntryLocalServiceUtil.deleteGroupFriendlyURLEntries(
			_group.getGroupId(),
			ClassNameLocalServiceUtil.getClassNameId(User.class));
	}

	@Test
	public void testAddContentLayoutWithExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		Layout layout = _layoutLocalService.addLayout(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, _serviceContext);

		Assert.assertEquals(
			externalReferenceCode, layout.getExternalReferenceCode());

		layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, layout.getExternalReferenceCode());

		Layout draftLayout1 = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout1);
		Assert.assertNotNull(draftLayout1.getExternalReferenceCode());

		Layout draftLayout2 =
			_layoutLocalService.getLayoutByExternalReferenceCode(
				draftLayout1.getExternalReferenceCode(), _group.getGroupId());

		Assert.assertEquals(draftLayout1, draftLayout2);
	}

	@Test
	public void testAddContentLayoutWithoutExternalReferenceCode()
		throws Exception {

		Layout layout1 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, _serviceContext);

		String externalReferenceCode = layout1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, layout1.getUuid());

		Layout layout2 = _layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(layout1, layout2);

		Layout draftLayout1 = layout1.fetchDraftLayout();

		Assert.assertNotNull(draftLayout1);
		Assert.assertNotNull(draftLayout1.getExternalReferenceCode());

		Layout draftLayout2 =
			_layoutLocalService.getLayoutByExternalReferenceCode(
				draftLayout1.getExternalReferenceCode(), _group.getGroupId());

		Assert.assertEquals(draftLayout1, draftLayout2);
	}

	@Test(expected = DuplicateLayoutExternalReferenceCodeException.class)
	public void testAddLayoutWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_layoutLocalService.addLayout(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, _serviceContext);

		_layoutLocalService.addLayout(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, _serviceContext);
	}

	@Test
	public void testAddWidgetLayoutWithExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		Layout layout = _layoutLocalService.addLayout(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_PORTLET, false, false, null, _serviceContext);

		Assert.assertEquals(
			externalReferenceCode, layout.getExternalReferenceCode());

		layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, layout.getExternalReferenceCode());
	}

	@Test
	public void testAddWidgetLayoutWithoutExternalReferenceCode()
		throws Exception {

		Layout layout1 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_PORTLET, false, false, null, _serviceContext);

		String externalReferenceCode = layout1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, layout1.getUuid());

		Layout layout2 = _layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(layout1, layout2);
	}

	@Test
	public void testDeleteLayoutByExternalReferenceCode() throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, _serviceContext);

		_layoutLocalService.deleteLayout(
			layout.getExternalReferenceCode(), layout.getGroupId());

		Assert.assertNull(_layoutLocalService.fetchLayout(layout.getPlid()));
	}

	@Test
	public void testDeleteLayouts() throws Exception {
		_testDeleteLayouts(false);
		_testDeleteLayouts(true);
	}

	@Test
	public void testEditWidgetLayoutWithEmptyDefaultFriendlyURL()
		throws Exception {

		String name = RandomTestUtil.randomString();

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			Collections.singletonMap(LocaleUtil.US, name),
			Collections.singletonMap(LocaleUtil.US, name),
			Collections.emptyMap(), null, null, LayoutConstants.TYPE_PORTLET,
			StringPool.BLANK, false, false,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "/spanishurl"
			).put(
				LocaleUtil.US, "/englishurl"
			).build(),
			0, _serviceContext);

		Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

		Assert.assertEquals("/englishurl", friendlyURLMap.get(LocaleUtil.US));

		friendlyURLMap.remove(LocaleUtil.US);

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), friendlyURLMap, layout.isIconImage(), null,
			layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
			layout.getMasterLayoutPlid(), _serviceContext);

		friendlyURLMap = layout.getFriendlyURLMap();

		Assert.assertEquals(
			StringPool.SLASH +
				FriendlyURLNormalizerUtil.normalizeWithEncoding(name),
			friendlyURLMap.get(LocaleUtil.US));
	}

	@Test
	public void testExistingLayoutCanHaveTheSameFriendlyURLAsDeletedOne()
		throws Exception {

		String friendlyURL1 = "/friendly-url-1";

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL1));

		String friendlyURL2 = "/friendly-url-2";

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL2));

		_layoutLocalService.deleteLayout(layout1);

		layout2 = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), layout2.getPlid(), friendlyURL1,
			_group.getDefaultLanguageId());

		Assert.assertEquals(
			layout2,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), true, friendlyURL1));
		Assert.assertEquals(
			layout2,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), true, friendlyURL1));
	}

	@Test
	public void testGetLayoutWithOldFriendlyURLWhenNewLayoutWithSameNameIsCreated()
		throws Exception {

		String friendlyURL1 = "/friendly-url-1";

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), "friendly url 1"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL1));

		layout1 = _layoutLocalService.updateLayout(
			layout1.getGroupId(), layout1.isPrivateLayout(),
			layout1.getLayoutId(), layout1.getParentLayoutId(),
			layout1.getNameMap(), layout1.getTitleMap(),
			layout1.getDescriptionMap(), layout1.getKeywordsMap(),
			layout1.getRobotsMap(), layout1.getType(), layout1.isHidden(),
			HashMapBuilder.put(
				LocaleUtil.US, "/friendly-url-2"
			).build(),
			false, null, layout1.getStyleBookEntryId(),
			layout1.getFaviconFileEntryId(), layout1.getMasterLayoutPlid(),
			_serviceContext);

		Layout layout2 = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "friendly url 1", null,
			RandomTestUtil.randomString(), LayoutConstants.TYPE_PORTLET, false,
			false, null, _serviceContext);

		Assert.assertEquals(
			layout1,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), false, friendlyURL1));
		Assert.assertNotEquals(
			layout2,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), false, friendlyURL1));
	}

	@Test
	public void testGetOrAddEmptyLayout() throws Exception {

		// Lazy referencing disabled

		try {
			_layoutLocalService.getOrAddEmptyLayout(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), _serviceContext);

			Assert.fail();
		}
		catch (NoSuchLayoutException noSuchLayoutException) {
			Assert.assertNotNull(noSuchLayoutException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Layout layout = _layoutLocalService.getOrAddEmptyLayout(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), _serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, layout.getStatus());
		}
	}

	@Test
	public void testGetPublishedLayouts() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		LayoutTestUtil.addTypeContentLayout(_group);

		Layout publishedLayout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = publishedLayout.fetchDraftLayout();

		if (draftLayout == null) {
			UnicodeProperties unicodeProperties =
				publishedLayout.getTypeSettingsProperties();

			draftLayout = _layoutLocalService.addLayout(
				null, publishedLayout.getUserId(), publishedLayout.getGroupId(),
				publishedLayout.isPrivateLayout(),
				publishedLayout.getParentLayoutId(),
				_portal.getClassNameId(Layout.class), publishedLayout.getPlid(),
				publishedLayout.getNameMap(), publishedLayout.getTitleMap(),
				publishedLayout.getDescriptionMap(),
				publishedLayout.getKeywordsMap(),
				publishedLayout.getRobotsMap(), publishedLayout.getType(),
				unicodeProperties.toString(), true, true,
				Collections.emptyMap(), publishedLayout.getMasterLayoutPlid(),
				serviceContext);

			draftLayout = _layoutLocalService.copyLayoutContent(
				publishedLayout, draftLayout);
		}

		_layoutLocalService.updateStatus(
			draftLayout.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		publishedLayout = _layoutLocalService.updateStatus(
			publishedLayout.getUserId(), publishedLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Layout widgetLayout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_PORTLET, false, false, null, serviceContext);

		Assert.assertEquals(
			2,
			_layoutLocalService.getPublishedLayoutsCount(_group.getGroupId()));

		List<Layout> layouts = _layoutLocalService.getPublishedLayouts(
			_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(layouts.toString(), 2, layouts.size());

		Assert.assertTrue(layouts.contains(publishedLayout));
		Assert.assertTrue(layouts.contains(widgetLayout));
	}

	@Test
	public void testKeepsAHistoryOfOldFriendlyURLs() throws Exception {
		String friendlyURL = "/friendly-url";

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		for (int i = 0; i < 10; i++) {
			layout = _layoutLocalService.updateFriendlyURL(
				TestPropsValues.getUserId(), layout.getPlid(),
				"/friendly-url-" + i, _group.getDefaultLanguageId());
		}

		for (int i = 0; i < 10; i++) {
			Assert.assertEquals(
				layout,
				_layoutLocalService.fetchLayoutByFriendlyURL(
					_group.getGroupId(), false, "/friendly-url-" + i));
			Assert.assertEquals(
				layout,
				_layoutLocalService.getFriendlyURLLayout(
					_group.getGroupId(), false, "/friendly-url-" + i));
		}
	}

	@Test
	public void testLayoutsAreFoundBasedOnDoubleHttpEncodedLegacyFriendlyURL()
		throws Exception {

		String name = "café";

		String friendlyURL = HttpComponentsUtil.decodeURL(
			StringPool.SLASH + name);

		friendlyURL = HttpComponentsUtil.decodeURL(friendlyURL);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		Assert.assertEquals(
			layout,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), false, friendlyURL));
	}

	@Test
	public void testLayoutsAreFoundBasedOnHttpEncodedFriendlyURL()
		throws Exception {

		String name = "café";

		String friendlyURL = HttpComponentsUtil.decodeURL(
			StringPool.SLASH + name);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.singletonMap(LocaleUtil.getDefault(), null));

		Assert.assertEquals(
			layout,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), false, friendlyURL));
	}

	@Test
	public void testNewLayoutCanHaveTheSameFriendlyURLAsDeletedOne()
		throws Exception {

		String friendlyURL = "/friendly-url";

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		_layoutLocalService.deleteLayout(layout);

		layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		Assert.assertEquals(
			layout,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), true, friendlyURL));
		Assert.assertEquals(
			layout,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), true, friendlyURL));
	}

	@Test
	public void testPrivateLayoutCanHaveTheSameFriendlyURLAsPublicOne()
		throws Exception {

		String friendlyURL = "/friendly-url";

		Layout privateLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), true,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		Layout publicLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), false,
			Collections.singletonMap(LocaleUtil.getDefault(), "name"),
			Collections.singletonMap(LocaleUtil.getDefault(), friendlyURL));

		Assert.assertEquals(
			privateLayout,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), true, friendlyURL));
		Assert.assertEquals(
			privateLayout,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), true, friendlyURL));
		Assert.assertEquals(
			publicLayout,
			_layoutLocalService.fetchLayoutByFriendlyURL(
				_group.getGroupId(), false, friendlyURL));
		Assert.assertEquals(
			publicLayout,
			_layoutLocalService.getFriendlyURLLayout(
				_group.getGroupId(), false, friendlyURL));
	}

	@Test
	public void testSearch() throws Exception {
		String name = RandomTestUtil.randomString();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group, name);

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);

		String keyword = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				"fragment-entry-key", RandomTestUtil.randomString(),
				StringPool.BLANK, "<div>" + keyword + "</div>",
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), draftLayout,
			fragmentEntry.getFragmentEntryKey(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			fragmentEntry.getType());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertSearch(keyword, name, true, 0);
		_assertSearch(keyword, name, false, 1);
		_assertSearch(name, name, true, 1);
		_assertSearch(name, name, false, 1);
	}

	@Test
	public void testUpdateDraftLayoutAfterOriginalLayoutUpdatesWithNewFriendlyURL()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(),
			HashMapBuilder.put(
				LocaleUtil.US, "/friendly-url-2"
			).build(),
			false, null, layout.getStyleBookEntryId(),
			layout.getFaviconFileEntryId(), layout.getMasterLayoutPlid(),
			_serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		_layoutLocalService.updateLayout(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), draftLayout.getParentLayoutId(),
			draftLayout.getNameMap(), draftLayout.getTitleMap(),
			draftLayout.getDescriptionMap(), draftLayout.getKeywordsMap(),
			draftLayout.getRobotsMap(), draftLayout.getType(),
			draftLayout.isHidden(), draftLayout.getFriendlyURLMap(), false,
			null, draftLayout.getStyleBookEntryId(),
			draftLayout.getFaviconFileEntryId(),
			draftLayout.getMasterLayoutPlid(), _serviceContext);
	}

	@Test
	public void testUpdateFriendlyURLMap() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		long userId = layout.getUserId();

		layout.setUserId(-1);

		layout = _layoutLocalService.updateLayout(layout);

		Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

		String friendlyURL = _friendlyURLNormalizer.normalizeWithEncoding(
			StringPool.SLASH + RandomTestUtil.randomString());

		friendlyURLMap.put(LocaleUtil.GERMANY, friendlyURL);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		layout = _layoutLocalService.updateLayout(
			_group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), friendlyURLMap, layout.getIconImage(), null, 0,
			0, 0, serviceContext);

		Assert.assertEquals(
			friendlyURL, layout.getFriendlyURL(LocaleUtil.GERMANY));
	}

	@Test
	public void testUpdateLayoutWithEmptyDefaultFriendlyURLAndAnotherLocaleAdded()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group, "home");

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "/casa"
			).put(
				LocaleUtil.US, ""
			).build(),
			false, null, layout.getStyleBookEntryId(),
			layout.getFaviconFileEntryId(), layout.getMasterLayoutPlid(),
			serviceContext);

		Assert.assertEquals("/home", layout.getFriendlyURL(LocaleUtil.US));
	}

	@Test(expected = LayoutJavaScriptException.class)
	public void testUpdateLayoutWithJavaScriptIvalidValue1() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			UnicodePropertiesBuilder.put(
				"javascript", "<script>"
			).buildString());
	}

	@Test(expected = LayoutJavaScriptException.class)
	public void testUpdateLayoutWithJavaScriptIvalidValue2() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			UnicodePropertiesBuilder.put(
				"javascript", "</script>"
			).buildString());
	}

	@Test
	public void testUpdateLayoutWithLazyReferencingEnabled() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Layout layout = _layoutLocalService.getOrAddEmptyLayout(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), _serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, layout.getStatus());

			try {
				layout = _layoutLocalService.updateLayout(
					_group.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId(), layout.getParentLayoutId(),
					layout.getNameMap(), layout.getTitleMap(),
					layout.getDescriptionMap(), layout.getKeywordsMap(),
					layout.getRobotsMap(), layout.getType(), false,
					layout.getFriendlyURLMap(), layout.isIconImage(), null,
					layout.getStyleBookEntryId(),
					layout.getFaviconFileEntryId(),
					layout.getMasterLayoutPlid(), _serviceContext);

				Assert.fail();
			}
			catch (LayoutTypeException layoutTypeException) {
				Assert.assertNotNull(layoutTypeException);
			}

			layout = _layoutLocalService.updateLayout(
				_group.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), layout.getParentLayoutId(),
				layout.getNameMap(), layout.getTitleMap(),
				layout.getDescriptionMap(), layout.getKeywordsMap(),
				layout.getRobotsMap(), LayoutConstants.TYPE_CONTENT, false,
				layout.getFriendlyURLMap(), layout.isIconImage(), null,
				layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
				layout.getMasterLayoutPlid(), _serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, layout.getStatus());
		}
	}

	@Test
	public void testUpdateLookAndFeel() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		layout = _layoutLocalService.updateLookAndFeel(
			_group.getGroupId(), false, layout.getLayoutId(),
			"dialect_WAR_dialecttheme", "01", StringPool.BLANK);

		Assert.assertEquals(StringPool.BLANK, layout.getCss());

		ColorScheme colorScheme = layout.getColorScheme();

		Assert.assertEquals("01", colorScheme.getColorSchemeId());

		Theme theme = layout.getTheme();

		Assert.assertEquals("dialect_WAR_dialecttheme", theme.getThemeId());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			layout.getUserId(), "1_column", false);

		layout = _layoutLocalService.updateLayout(layout);

		layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

		Assert.assertEquals(
			"1_column", layoutTypePortlet.getLayoutTemplateId());
	}

	@Test(expected = MasterLayoutException.class)
	public void testUpdateMasterLayoutWithInvalidPlid1() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutLocalService.updateMasterLayoutPlid(
			_group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getPlid());
	}

	@Test(expected = MasterLayoutException.class)
	public void testUpdateMasterLayoutWithInvalidPlid2() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), layout.getFriendlyURLMap(),
			layout.getIconImage(), null, layout.getStyleBookEntryId(),
			layout.getFaviconFileEntryId(), layout.getPlid(), _serviceContext);
	}

	@Test
	public void testUpdatePriority() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		Layout layout2 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		Layout layout3 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		Layout layout4 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);
		Layout layout5 = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		_layoutLocalService.updatePriority(layout1.getPlid(), 0);
		_layoutLocalService.updatePriority(layout2.getPlid(), 1);
		_layoutLocalService.updatePriority(layout3.getPlid(), 2);
		_layoutLocalService.updatePriority(layout4.getPlid(), 3);
		_layoutLocalService.updatePriority(layout5.getPlid(), 4);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 1, layout2);
		_testUpdatePriority(0, 2, layout3);
		_testUpdatePriority(0, 3, layout4);
		_testUpdatePriority(0, 4, layout5);

		_layoutLocalService.updatePriority(layout4.getPlid(), 1);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 1, layout4);
		_testUpdatePriority(0, 2, layout2);
		_testUpdatePriority(0, 3, layout3);
		_testUpdatePriority(0, 4, layout5);

		layout5 = _layoutLocalService.updatePriority(layout5.getPlid(), 5);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 1, layout4);
		_testUpdatePriority(0, 2, layout2);
		_testUpdatePriority(0, 3, layout3);
		_testUpdatePriority(0, 4, layout5);

		layout3 = _layoutLocalService.updatePriority(layout3.getPlid(), -1);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 1, layout4);
		_testUpdatePriority(0, 2, layout2);
		_testUpdatePriority(0, 3, layout5);
		_testUpdatePriority(0, 4, layout3);

		layout2 = _layoutLocalService.updateParentLayoutId(
			layout2.getPlid(), layout1.getPlid());

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 1, layout4);
		_testUpdatePriority(0, 3, layout5);
		_testUpdatePriority(0, 4, layout3);
		_testUpdatePriority(layout1.getLayoutId(), 0, layout2);

		layout4 = _layoutLocalService.updateParentLayoutId(
			layout4.getPlid(), layout1.getPlid());

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 3, layout5);
		_testUpdatePriority(0, 4, layout3);
		_testUpdatePriority(layout1.getLayoutId(), 0, layout2);
		_testUpdatePriority(layout1.getLayoutId(), 1, layout4);

		layout3 = _layoutLocalService.updateParentLayoutIdAndPriority(
			layout3.getPlid(), layout1.getPlid(), 3);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(0, 3, layout5);
		_testUpdatePriority(layout1.getLayoutId(), 0, layout2);
		_testUpdatePriority(layout1.getLayoutId(), 1, layout4);
		_testUpdatePriority(layout1.getLayoutId(), 2, layout3);

		layout5 = _layoutLocalService.updateParentLayoutIdAndPriority(
			layout5.getPlid(), layout1.getPlid(), -1);

		_testUpdatePriority(0, 0, layout1);
		_testUpdatePriority(layout1.getLayoutId(), 0, layout2);
		_testUpdatePriority(layout1.getLayoutId(), 1, layout4);
		_testUpdatePriority(layout1.getLayoutId(), 2, layout3);
		_testUpdatePriority(layout1.getLayoutId(), 3, layout5);
	}

	@Test
	public void testUpdateTypeSettings() throws Exception {
		LayoutPrototype layoutPrototype = LayoutTestUtil.addLayoutPrototype(
			RandomTestUtil.randomString());

		Layout layout = layoutPrototype.getLayout();

		layout = _layoutLocalService.updateLayout(layout);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(layout.getUserId());

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), layout.getFriendlyURLMap(),
			layout.getIconImage(), null, 0, 0, 0, serviceContext);

		Layout updatedLayout = _layoutLocalService.getLayout(layout.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			updatedLayout.getTypeSettingsProperties();

		Assert.assertFalse(
			"Updating layout prototype should not add property \"" +
				Sites.LAYOUT_UPDATEABLE + "\"",
			typeSettingsUnicodeProperties.containsKey(Sites.LAYOUT_UPDATEABLE));
	}

	private void _assertSearch(
			String keyword, String name, boolean searchOnlyByName, int count)
		throws Exception {

		Assert.assertEquals(
			count,
			_layoutLocalService.searchCount(
				_group, false, keyword, searchOnlyByName,
				new String[] {LayoutConstants.TYPE_CONTENT}));

		List<Layout> layouts = _layoutLocalService.search(
			_group.getGroupId(), false, keyword, searchOnlyByName,
			new String[] {LayoutConstants.TYPE_CONTENT}, -1, -1, null);

		Assert.assertEquals(layouts.toString(), count, layouts.size());

		if (count == 1) {
			Layout layout = layouts.get(0);

			Assert.assertEquals(layout.getName(LocaleUtil.getDefault()), name);
		}
	}

	private ServiceContext _getServiceContext(Group group) throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			group, TestPropsValues.getUserId());
	}

	private void _testDeleteLayouts(boolean system) throws Exception {
		LayoutTestUtil.addTypeContentLayout(_group, false, system);
		LayoutTestUtil.addTypeContentLayout(_group, true, system);

		_layoutLocalService.deleteLayouts(
			_group.getGroupId(), true, _serviceContext);
		_layoutLocalService.deleteLayouts(
			_group.getGroupId(), false, _serviceContext);

		Assert.assertEquals(
			0, _layoutLocalService.getLayoutsCount(_group.getGroupId()));
	}

	private void _testUpdatePriority(
		long expectedParentLayoutId, int expectedPriority, Layout layout) {

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		Assert.assertEquals(expectedParentLayoutId, layout.getParentLayoutId());
		Assert.assertEquals(expectedPriority, layout.getPriority());
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.content.LayoutContentProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Locale;

import org.junit.After;
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
public class LayoutTypeContentPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddManyContentTypeLayoutsWithLayoutPageTemplate()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry();

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JSONObject jsonObject = ContentLayoutTestUtil.addPortletToLayout(
			draftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		String portletNamespace = editableValuesJSONObject.getString(
			"instanceId");

		String portletInstanceId = PortletIdCodec.encode(
			JournalContentPortletKeys.JOURNAL_CONTENT, portletNamespace);

		_setUpPortletPreferences(
			journalArticle, draftLayout, portletInstanceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		Locale siteDefaultLocale = _portal.getSiteDefaultLocale(_group);

		String templateContent = _getLayoutContent(layout, siteDefaultLocale);

		for (int i = 0; i < 5; i++) {
			layout = _layoutLocalService.addLayout(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				0, _portal.getClassNameId(LayoutPageTemplateEntry.class),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				HashMapBuilder.put(
					siteDefaultLocale, "name_" + i
				).build(),
				new HashMap<>(), new HashMap<>(), new HashMap<>(),
				new HashMap<>(), LayoutConstants.TYPE_CONTENT, StringPool.BLANK,
				false, false, new HashMap<>(), 0, _serviceContext);

			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);

			String content = null;

			_entityCache.clearCache();
			_multiVMPool.clear();

			try (PerformanceTimer performanceTimer = new PerformanceTimer(
					500)) {

				content = _getLayoutContent(layout, siteDefaultLocale);
			}

			Assert.assertNotEquals(StringPool.BLANK, content);
			Assert.assertEquals(templateContent, content);
		}
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, "Page Template Collection", StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					_serviceContext);

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			null, "Page Template One",
			LayoutPageTemplateEntryTypeConstants.BASIC, 0,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private void _assertPortletPreferences(
		JournalArticle journalArticle, Layout layout, String portletId) {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			portletPreferences.getValue("articleExternalReferenceCode", null));
		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	private String _getLayoutContent(Layout layout, Locale locale)
		throws Exception {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			return _layoutContentProvider.getLayoutContent(
				themeDisplay.getRequest(), themeDisplay.getResponse(), layout,
				locale);
		}
	}

	private void _setUpPortletPreferences(
			JournalArticle journalArticle, Layout layout, String portletId)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(layout, portletId, null);

		portletPreferences.setValue(
			"articleExternalReferenceCode",
			journalArticle.getExternalReferenceCode());
		portletPreferences.setValue(
			"groupExternalReferenceCode", _group.getExternalReferenceCode());

		portletPreferences.store();

		_assertPortletPreferences(journalArticle, layout, portletId);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutContentProvider _layoutContentProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	private ServiceContext _serviceContext;

}
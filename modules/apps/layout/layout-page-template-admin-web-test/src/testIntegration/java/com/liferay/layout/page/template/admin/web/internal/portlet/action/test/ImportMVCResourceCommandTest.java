/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class ImportMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_bundle = FrameworkUtil.getBundle(getClass());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testImportFileWithDoNotImportStrategyAndWithExistingLayoutPageTemplateCollection()
		throws Exception {

		_layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, "imported", StringPool.BLANK,
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				_serviceContext);

		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.DO_NOT_IMPORT));

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					_group.getGroupId(), "imported-(1)",
					LayoutPageTemplateEntryTypeConstants.BASIC));
	}

	@Test
	public void testImportFileWithDoNotImportStrategyAndWithExistingLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_createLayoutPageTemplateEntry();

		Layout expectedLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_assertImportResultsJSONObject(
			1, 2, _importFile(LayoutsImportStrategy.DO_NOT_IMPORT));

		Layout actualLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			expectedLayout.getTypeSettings(), actualLayout.getTypeSettings());
	}

	@Test
	public void testImportFileWithDoNotOverwriteStrategy() throws Exception {
		_createLayoutPageTemplateEntry();

		_assertImportResultsJSONObject(
			1, 2, 1, _importFile(LayoutsImportStrategy.DO_NOT_OVERWRITE));
	}

	@Test
	public void testImportFileWithKeepBothStrategyAndWithExistingLayoutPageTemplateCollection()
		throws Exception {

		_layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, "imported", StringPool.BLANK,
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				_serviceContext);

		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.KEEP_BOTH));

		Assert.assertNotNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					_group.getGroupId(), "imported-(copy)",
					LayoutPageTemplateEntryTypeConstants.BASIC));
	}

	@Test
	public void testImportFileWithKeepBothStrategyAndWithExistingLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_createLayoutPageTemplateEntry();

		Layout expectedLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.KEEP_BOTH));

		Layout actualLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertEquals(
			expectedLayout.getTypeSettings(), actualLayout.getTypeSettings());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				_group.getGroupId(), "existing-master-page-(1)"));
	}

	@Test
	public void testImportFileWithOverwriteStrategy() throws Exception {
		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.OVERWRITE));
	}

	@Test
	@TestInfo("LPD-69818")
	public void testImportFileWithOverwriteStrategyAndWithExistingLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_createLayoutPageTemplateEntry();

		Layout expectedLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.OVERWRITE));

		Layout actualLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertNotEquals(
			expectedLayout.getTypeSettings(), actualLayout.getTypeSettings());
	}

	@Test
	@TestInfo("LPS-182022")
	public void testImportFileWithOverwriteStrategyAndWithExistingLockedLayoutPageTemplateEntry()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_createLayoutPageTemplateEntry();

		Layout expectedLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = expectedLayout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(_group.getCompanyId()));

		_layoutLockManager.getLock(draftLayout, user.getUserId());

		Assert.assertTrue(
			draftLayout.isUnlocked(Constants.EDIT, user.getUserId()));

		Assert.assertFalse(
			draftLayout.isUnlocked(
				Constants.EDIT, TestPropsValues.getUserId()));

		_assertImportResultsJSONObject(
			1, 3, _importFile(LayoutsImportStrategy.OVERWRITE));

		Assert.assertTrue(
			draftLayout.isUnlocked(Constants.EDIT, user.getUserId()));

		Assert.assertTrue(
			draftLayout.isUnlocked(
				Constants.EDIT, TestPropsValues.getUserId()));

		Layout actualLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertNotEquals(
			expectedLayout.getTypeSettings(), actualLayout.getTypeSettings());
	}

	private void _assertImportResultsJSONObject(
		long expectedInvalidJSONArrayLength,
		long expectedImportedJSONArrayLength, JSONObject jsonObject) {

		JSONObject importResultsJSONObject = jsonObject.getJSONObject(
			"importResults");

		JSONArray invalidJSONArray = importResultsJSONObject.getJSONArray(
			"error");

		Assert.assertEquals(
			expectedInvalidJSONArrayLength, invalidJSONArray.length());

		JSONArray importedJSONArray = importResultsJSONObject.getJSONArray(
			"success");

		Assert.assertEquals(
			expectedImportedJSONArrayLength, importedJSONArray.length());
	}

	private void _assertImportResultsJSONObject(
		long expectedInvalidJSONArrayLength,
		long expectedImportedJSONArrayLength,
		long expectedIgnoredJSONArrayLength, JSONObject jsonObject) {

		JSONObject importResultsJSONObject = jsonObject.getJSONObject(
			"importResults");

		JSONArray invalidJSONArray = importResultsJSONObject.getJSONArray(
			"error");

		Assert.assertEquals(
			expectedInvalidJSONArrayLength, invalidJSONArray.length());

		JSONArray importedJSONArray = importResultsJSONObject.getJSONArray(
			"success");

		Assert.assertEquals(
			expectedImportedJSONArrayLength, importedJSONArray.length());

		JSONArray ignoredJSONArray = importResultsJSONObject.getJSONArray(
			"warning");

		Assert.assertEquals(
			expectedIgnoredJSONArrayLength, ignoredJSONArray.length());
	}

	private LayoutPageTemplateEntry _createLayoutPageTemplateEntry()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				"Existing Master Page",
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, layoutPageTemplateEntry.getUserId(),
			layoutPageTemplateEntry.getGroupId(), null, null, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateEntry.getPlid()),
			layoutPageTemplateEntry.getPlid(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			JSONUtil.put(
				"instanceId", StringUtil.randomId()
			).put(
				"portletId", PortletKeys.ALERTS
			).toString(),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_PORTLET, serviceContext);

		return layoutPageTemplateEntry;
	}

	private File _getFile() throws Exception {
		Enumeration<URL> enumeration = _bundle.findEntries(
			_RESOURCES_PATH, "*", true);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String path = url.getPath();

			if (path.endsWith(StringPool.SLASH)) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				zipWriter.addEntry(
					StringUtil.removeSubstring(url.getPath(), _RESOURCES_PATH),
					inputStream);
			}
		}

		return zipWriter.getFile();
	}

	private JSONObject _importFile(LayoutsImportStrategy layoutsImportStrategy)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_importFile",
			new Class<?>[] {
				File.class, long.class, long.class, LayoutsImportStrategy.class,
				Locale.class, long.class
			},
			_getFile(), _group.getGroupId(), 0, layoutsImportStrategy,
			LocaleUtil.US, TestPropsValues.getUserId());
	}

	private static final String _RESOURCES_PATH =
		"com/liferay/layout/page/template/admin/web/internal/portlet/action" +
			"/test/dependencies/import";

	private Bundle _bundle;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(filter = "mvc.command.name=/layout_page_template_admin/import")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
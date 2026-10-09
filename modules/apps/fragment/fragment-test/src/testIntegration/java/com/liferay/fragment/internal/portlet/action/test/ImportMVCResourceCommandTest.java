/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.importer.FragmentsImportStrategy;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.List;
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
	public void testImportFragmentEntriesWithDoNotImportStrategyAndWithExistingFragmentEntry()
		throws Exception {

		FragmentEntry expectedFragmentEntry = _addFragmentEntry(
			"fragment", "Fragment", "<h1>Test html</h1>");

		_assertImportResultsJSONObject(
			2, 3, 2,
			_importFragmentEntries(
				FragmentsImportStrategy.DO_NOT_IMPORT, false));

		FragmentEntry actualFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(
				expectedFragmentEntry.getFragmentEntryId());

		Assert.assertEquals(
			expectedFragmentEntry.getHtml(), actualFragmentEntry.getHtml());
	}

	@Test
	public void testImportFragmentEntriesWithDoNotOverwriteStrategy()
		throws Exception {

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(
				FragmentsImportStrategy.DO_NOT_OVERWRITE, false));
	}

	@Test
	public void testImportFragmentEntriesWithKeepBothStrategyAndWithExistingFragmentCollection()
		throws Exception {

		_fragmentCollectionLocalService.addFragmentCollection(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			"collection", "Resources Collection", StringPool.BLANK, false,
			_serviceContext);

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.KEEP_BOTH, false));

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionLocalService.getFragmentCollections(
				_group.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertNotNull(fragmentCollections);
		Assert.assertEquals(
			fragmentCollections.toString(), 2, fragmentCollections.size());
	}

	@Test
	public void testImportFragmentEntriesWithKeepBothStrategyAndWithExistingFragmentEntry()
		throws Exception {

		FragmentEntry expectedFragmentEntry = _addFragmentEntry(
			"fragment", "Fragment", "<h1>Test html</h1>");

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.KEEP_BOTH, false));

		FragmentEntry actualFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(
				expectedFragmentEntry.getFragmentEntryId());

		Assert.assertEquals(
			expectedFragmentEntry.getHtml(), actualFragmentEntry.getHtml());

		Assert.assertNotNull(
			_fragmentEntryLocalService.getUniqueFragmentEntryName(
				_group.getGroupId(), 0, "fragment-0"));
	}

	@Test
	@TestInfo("LPD-50980")
	public void testImportFragmentEntriesWithMarketplaceAndWithExistingFragmentCollection()
		throws Exception {

		_fragmentCollectionLocalService.addFragmentCollection(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			"collection", "Resources Collection", StringPool.BLANK, false,
			_serviceContext);

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.KEEP_BOTH, true));

		FragmentCollection importedFragmentCollection =
			_fragmentCollectionLocalService.fetchFragmentCollection(
				_group.getGroupId(), "collection-0");

		Assert.assertNotNull(importedFragmentCollection);
		Assert.assertTrue(importedFragmentCollection.isMarketplace());
	}

	@Test
	@TestInfo("LPD-50980")
	public void testImportFragmentEntriesWithMarketplaceAndWithExistingFragmentEntry()
		throws Exception {

		_addFragmentEntry("fragment", "Fragment", "<h1>Test html</h1>");

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.KEEP_BOTH, true));

		FragmentEntry importedFragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				_group.getGroupId(), "fragment-0");

		Assert.assertNotNull(importedFragmentEntry);
		Assert.assertTrue(importedFragmentEntry.isMarketplace());
	}

	@Test
	public void testImportFragmentEntriesWithOverwriteStrategy()
		throws Exception {

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.OVERWRITE, false));
	}

	@Test
	public void testImportFragmentEntriesWithOverwriteStrategyAndWithExistingFragmentEntry()
		throws Exception {

		FragmentEntry expectedFragmentEntry = _addFragmentEntry(
			"fragment", "Fragment", "<h1>Test html</h1>");

		_assertImportResultsJSONObject(
			2, 4, 2,
			_importFragmentEntries(FragmentsImportStrategy.OVERWRITE, false));

		FragmentEntry actualFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(
				expectedFragmentEntry.getFragmentEntryId());

		Assert.assertNotEquals(
			expectedFragmentEntry.getHtml(), actualFragmentEntry.getHtml());
	}

	private FragmentEntry _addFragmentEntry(
			String key, String name, String html)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				"Resources Collection", StringPool.BLANK, _serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), key, name,
			StringPool.BLANK, html, StringPool.BLANK, false, StringPool.BLANK,
			null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private void _assertImportResultsJSONObject(
		long expectedImportedDraftJSONArrayLength,
		long expectedImportedJSONArrayLength,
		long expectedInvalidJSONArrayLength, JSONObject jsonObject) {

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

		JSONArray importedDraftJSONArray = importResultsJSONObject.getJSONArray(
			"warning");

		Assert.assertEquals(
			expectedImportedDraftJSONArrayLength,
			importedDraftJSONArray.length());
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

	private JSONObject _importFragmentEntries(
			FragmentsImportStrategy fragmentsImportStrategy,
			boolean marketplace)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_importFragmentEntries",
			new Class<?>[] {
				File.class, long.class, long.class,
				FragmentsImportStrategy.class, Locale.class, boolean.class,
				long.class
			},
			_getFile(), 0, _group.getGroupId(), fragmentsImportStrategy,
			LocaleUtil.US, marketplace, TestPropsValues.getUserId());
	}

	private static final String _RESOURCES_PATH =
		"com/liferay/fragment/dependencies/fragments/import" +
			"/fragment-collection";

	private Bundle _bundle;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Group _group;

	@Inject(filter = "mvc.command.name=/fragment/import")
	private MVCResourceCommand _mvcResourceCommand;

	private ServiceContext _serviceContext;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
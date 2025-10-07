/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.internal.display.context.test.BaseDisplayContextTestCase;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = _getGroup();
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		Group group = depotEntry.getGroup();

		JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			depotEntry.getCompanyId(), depotEntry.getUserId(),
			group.getExternalReferenceCode(), depotEntry.getModelClassName(),
			_filterFactory);

		Assert.assertTrue(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));
		Assert.assertTrue(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES));
		Assert.assertTrue(jsonObject.has("OBJECT_ENTRY_FOLDERS"));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testDeleteDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry();

		Group group = depotEntry.getGroup();

		_depotEntryLocalService.deleteDepotEntry(depotEntry.getDepotEntryId());

		Assert.assertNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				depotEntry.getCompanyId(), depotEntry.getUserId(),
				group.getExternalReferenceCode(),
				depotEntry.getModelClassName(), _filterFactory));
		Assert.assertEquals(
			0,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				depotEntry.getGroupId(), depotEntry.getCompanyId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testUpdateDepotEntry() throws Exception {
		Layout layout = _getRecycleBinLayout(_cmsGroup);

		Assert.assertFalse(layout.isHidden());

		Group defaultGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), "Default");

		_setTrashEnabled(defaultGroup, Boolean.FALSE.toString());

		Assert.assertFalse(
			GetterUtil.getBoolean(
				defaultGroup.getTypeSettingsProperty("trashEnabled")));

		layout = _getRecycleBinLayout(_cmsGroup);

		Assert.assertTrue(layout.isHidden());

		DepotEntry depotEntry = _addDepotEntry();

		Group depotGroup = depotEntry.getGroup();

		_setTrashEnabled(depotGroup, Boolean.TRUE.toString());

		Assert.assertTrue(
			GetterUtil.getBoolean(
				depotGroup.getTypeSettingsProperty("trashEnabled")));

		layout = _getRecycleBinLayout(_cmsGroup);

		Assert.assertFalse(layout.isHidden());

		_setTrashEnabled(defaultGroup, null);
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private Group _getGroup() throws Exception {
		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		if (group != null) {
			return group;
		}

		group = GroupTestUtil.addGroup();

		group.setGroupKey(GroupConstants.CMS);

		group = _groupLocalService.updateGroup(group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		try {

			// These tests require the instance to be created with the feature
			// flag LPD-17564 enabled. On CI, feature flags are enabled on
			// demand for each test, but not during instance initialization.
			// Until the feature flag LPD-17564 is removed, run the instance
			// lifecycle initializer manually so that the role is created.

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(group.getGroupId());

			Bundle testBundle = FrameworkUtil.getBundle(
				BaseDisplayContextTestCase.class);

			BundleContext bundleContext = testBundle.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.site.initializer.cms")) {

					_deleteFile(bundle, "00.list.type.definition");
					_deleteFile(bundle, "01.object.folder");
					_deleteFile(bundle, "02.object.definition");

					CompletableFuture<Void> completableFuture =
						_batchEngineUnitProcessor.processBatchEngineUnits(
							_batchEngineUnitReader.getBatchEngineUnits(bundle));

					completableFuture.join();
				}
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return group;
	}

	private Layout _getRecycleBinLayout(Group group) throws Exception {
		return _layoutLocalService.getLayoutByFriendlyURL(
			group.getGroupId(), false, "/recycle-bin");
	}

	private void _setTrashEnabled(Group group, String value) throws Exception {
		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		if (value == null) {
			unicodeProperties.remove("trashEnabled");
		}
		else {
			unicodeProperties.setProperty("trashEnabled", value);
		}

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private Group _cmsGroup;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}
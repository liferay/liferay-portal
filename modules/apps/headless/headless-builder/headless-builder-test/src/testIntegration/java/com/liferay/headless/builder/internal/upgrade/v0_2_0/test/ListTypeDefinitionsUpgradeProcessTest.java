/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.upgrade.v0_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlag("LPS-178642")
@RunWith(Arquillian.class)
public class ListTypeDefinitionsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_testUpgrade("bundle1");
		_testUpgrade("bundle2");
	}

	private void _assertUpgrade(
			String expectedListTypeDefinitionName,
			String expectedListTypeEntryExternalReferenceCode1,
			String expectedListTypeEntryExternalReferenceCode2,
			String listTypeEntryKey1, String listTypeEntryKey2,
			String listTypeExternalReferenceCode,
			String objectDefinitionExternalReferenceCode,
			String objectFieldExternalReferenceCode,
			String oldListTypeExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					TestPropsValues.getCompanyId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectFieldExternalReferenceCode,
			objectDefinition.getObjectDefinitionId());

		Assert.assertNull(
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId()));

		Assert.assertFalse(objectField.isState());

		Assert.assertNull(
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					oldListTypeExternalReferenceCode,
					TestPropsValues.getCompanyId()));

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				getListTypeDefinitionByExternalReferenceCode(
					listTypeExternalReferenceCode,
					TestPropsValues.getCompanyId());

		Assert.assertEquals(
			expectedListTypeDefinitionName,
			listTypeDefinition.getNameCurrentValue());

		List<ListTypeEntry> listTypeEntries =
			_listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId());

		Assert.assertEquals(
			listTypeEntries.toString(), 2, listTypeEntries.size());

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(),
				listTypeEntryKey1);

		Assert.assertEquals(
			expectedListTypeEntryExternalReferenceCode1,
			listTypeEntry.getExternalReferenceCode());

		if (listTypeEntryKey2 == null) {
			return;
		}

		listTypeEntry = _listTypeEntryLocalService.getListTypeEntry(
			listTypeDefinition.getListTypeDefinitionId(), listTypeEntryKey2);

		Assert.assertEquals(
			expectedListTypeEntryExternalReferenceCode2,
			listTypeEntry.getExternalReferenceCode());
	}

	private void _deleteFile(Bundle bundle, String processedFileName) {
		File processedFile = bundle.getDataFile(
			".com.liferay.headless.builder.internal.batch." +
				processedFileName + ".batch.engine.data.json.0.processed");

		if ((processedFile != null) && processedFile.exists()) {
			processedFile.delete();
		}
	}

	private void _deployLatestVersion() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(BaseTestCase.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.headless.builder.impl")) {

				_deleteFile(bundle, "00.list.type.definition");
				_deleteFile(bundle, "01.object.definition");

				_batchEngineUnitProcessor.processBatchEngineUnits(
					_batchEngineUnitReader.getBatchEngineUnits(bundle));
			}
		}
	}

	private void _installBundle(String bundleName) throws Exception {

		// Deletion order is relevant to avoid constraint errors

		for (String externalReferenceCode :
				Arrays.asList(
					"L_API_SORT", "L_API_FILTER", "L_API_PROPERTY",
					"L_API_SCHEMA", "L_API_ENDPOINT", "L_API_APPLICATION")) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}

		for (String externalReferenceCode :
				Arrays.asList(
					"L_API_APPLICATION_STATUSES", "L_API_ENDPOINT_HTTP_METHODS",
					"L_API_ENDPOINT_RETRIEVE_TYPES", "L_API_ENDPOINT_SCOPES",
					"L_API_PROPERTY_TYPES")) {

			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, TestPropsValues.getCompanyId());

			if (listTypeDefinition != null) {
				_listTypeDefinitionLocalService.deleteListTypeDefinition(
					listTypeDefinition);
			}
		}

		Bundle bundle = FrameworkUtil.getBundle(
			ListTypeDefinitionsUpgradeProcessTest.class);

		String dirName = StringBundler.concat(
			"com/liferay/headless/builder/internal/upgrade/v0_2_0/test",
			"/dependencies/", bundleName, "/");

		Enumeration<URL> enumeration = bundle.findEntries(dirName, "*", true);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				String urlString = url.getPath();

				if (urlString.endsWith(StringPool.SLASH)) {
					continue;
				}

				String name = urlString.substring(dirName.length());

				if (name.startsWith(StringPool.SLASH)) {
					name = name.substring(1);
				}

				try (InputStream inputStream = url.openStream()) {
					zipWriter.addEntry(name, inputStream);
				}
			}
		}

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle testBundle = bundleContext.installBundle(
			RandomTestUtil.randomString(),
			new FileInputStream(zipWriter.getFile()));

		try {
			_batchEngineUnitProcessor.processBatchEngineUnits(
				_batchEngineUnitReader.getBatchEngineUnits(testBundle));
		}
		finally {
			testBundle.uninstall();
		}
	}

	private void _testUpgrade(String bundleName) throws Exception {
		_installBundle(bundleName);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.headless.builder.internal.upgrade.v0_2_0." +
				"ListTypeDefinitionsUpgradeProcess");

		upgradeProcess.upgrade();

		_deployLatestVersion();

		_assertUpgrade(
			"Application Status", "PUBLISHED", "UNPUBLISHED", "published",
			"unpublished", "L_API_APPLICATION_STATUSES", "L_API_APPLICATION",
			"APPLICATION_STATUS", "APPLICATION_STATUS_PICKLIST");
		_assertUpgrade(
			"HTTP Method", "GET", "POST", "get", "post",
			"L_API_ENDPOINT_HTTP_METHODS", "L_API_ENDPOINT", "HTTP_METHOD",
			"HTTP_METHOD_PICKLIST");
		_assertUpgrade(
			"Retrieve Type", "COLLECTION", "SINGLE_ELEMENT", "collection",
			"singleElement", "L_API_ENDPOINT_RETRIEVE_TYPES", "L_API_ENDPOINT",
			"RETRIEVE_TYPE", "RETRIEVE_TYPE_PICKLIST");
		_assertUpgrade(
			"Scope", "COMPANY", "SITE", "company", "site",
			"L_API_ENDPOINT_SCOPES", "L_API_ENDPOINT", "SCOPE",
			"SCOPE_PICKLIST");
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Inject(
		filter = "component.name=com.liferay.headless.builder.internal.upgrade.registry.HeadlessBuilderUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
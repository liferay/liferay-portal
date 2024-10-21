/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.registry;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.data.provider.settings.DDMDataProviderSettingsProvider;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0.UpgradeCompanyId;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0.UpgradeKernelPackage;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0.UpgradeLastPublishDate;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_1.ResourcePermissionUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_0.CheckboxFieldToCheckboxMultipleFieldUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_1.DDMFormFieldSettingsUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_1.DDMStructureIndexTypeUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_3.DDMFormInstanceDefinitionUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_3.DDMFormInstanceEntriesUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_4.DDMFormParagraphFieldsUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_5.DDMFormFieldValidationUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_6.DDMDataProviderInstanceUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMContentTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMDataProviderInstanceTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMFormInstanceRecordTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMFormInstanceRecordVersionTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMFormInstanceTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMFormInstanceVersionTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMStructureLayoutTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMStructureTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMStructureVersionTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMTemplateTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_0_0.util.DDMTemplateVersionTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_1_0.DDMStructureLayoutUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_4.DDMContentUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_5_0.util.DDMFormInstanceReportTable;
import com.liferay.dynamic.data.mapping.internal.upgrade.v3_7_1.DDMStructureEmptyValidationUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0.DDMFieldUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_0.DLFileEntryTypeDataDefinitionIdUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_4.DDMStructureLinkDLFileEntryTypeUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_4.DLFileEntryTypeDDMFieldAttributeUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_5.DDMTemplateVersionUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_2_0.DDMFacetTemplateUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_2_1.WorkflowDefinitionLinkUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_2_2.DLFileEntryDDMFormInstanceRecordUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_3.BrowserSnifferTemplateUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_4_5.DDMTemplateLinkUpgradeProcess;
import com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1.DDMFieldAttributeUpgradeProcess;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.spi.converter.SPIDDMFormRuleConverter;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMDataDefinitionConverter;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.view.count.service.ViewCountEntryLocalService;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = UpgradeStepRegistrator.class)
public class DDMServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		DDMFormSerializer ddmFormSerializer = _jsonDDMFormSerializer;

		DDMFormLayoutSerializer ddmFormLayoutSerializer =
			_jsonDDMFormLayoutSerializer;

		DDMFormDeserializer ddmFormJSONDeserializer = _jsonDDMFormDeserializer;

		DDMFormDeserializer ddmFormXSDDeserializer = _xsdDDMFormDeserializer;

		DDMFormValuesSerializer ddmFormValuesSerializer =
			_jsonDDMFormValuesSerializer;

		DDMFormValuesDeserializer ddmFormValuesDeserializer =
			_jsonDDMFormValuesDeserializer;

		registry.register(
			"0.0.1", "0.0.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v0_0_2.
				SchemaUpgradeProcess());

		registry.register("0.0.2", "0.0.3", new UpgradeKernelPackage());

		registry.register(
			"0.0.3", "1.0.0", new UpgradeCompanyId(),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_0.
				DynamicDataMappingUpgradeProcess(
					_assetEntryLocalService, _classNameLocalService, _ddm,
					ddmFormJSONDeserializer, ddmFormXSDDeserializer,
					ddmFormLayoutSerializer, ddmFormSerializer,
					ddmFormValuesDeserializer, ddmFormValuesSerializer,
					_dlFileEntryLocalService, _dlFileVersionLocalService,
					_dlFolderLocalService, _expandoRowLocalService,
					_expandoTableLocalService, _expandoValueLocalService,
					_resourceActions, _resourceLocalService,
					_resourcePermissionLocalService, _store,
					_viewCountEntryLocalService),
			new UpgradeLastPublishDate());

		registry.register(
			"1.0.0", "1.0.1",
			new ResourcePermissionUpgradeProcess(_resourceActions));

		registry.register(
			"1.0.1", "1.0.2",
			UpgradeProcessFactory.alterColumnType(
				"DDMTemplate", "smallImageURL", "STRING null"));

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_3.
				DDMFormParagraphFieldsUpgradeProcess(_jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_0_3.
				DDMFormFieldValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"1.0.3", "1.1.0",
			new CheckboxFieldToCheckboxMultipleFieldUpgradeProcess(
				ddmFormJSONDeserializer, ddmFormValuesDeserializer,
				ddmFormValuesSerializer, _jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_0.
				DDMStructureUpgradeProcess(
					ddmFormJSONDeserializer, ddmFormSerializer),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_0.
				DataProviderInstanceUpgradeProcess(_jsonFactory));

		registry.register(
			"1.1.0", "1.1.1",
			new DDMFormFieldSettingsUpgradeProcess(
				ddmFormJSONDeserializer, ddmFormSerializer),
			new DDMStructureIndexTypeUpgradeProcess(_jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_1.
				DataProviderInstanceUpgradeProcess(
					_serviceTrackerMap, ddmFormValuesDeserializer,
					ddmFormValuesSerializer));

		registry.register(
			"1.1.1", "1.1.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_2.
				DynamicDataMappingUpgradeProcess(
					ddmFormJSONDeserializer, ddmFormSerializer,
					ddmFormValuesDeserializer, ddmFormValuesSerializer,
					_jsonFactory));

		registry.register(
			"1.1.2", "1.1.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_3.
				DDMStorageLinkUpgradeProcess());

		registry.register(
			"1.1.3", "1.2.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v1_2_0.
				SchemaUpgradeProcess());

		registry.register("1.2.0", "1.2.1", new DummyUpgradeStep());

		registry.register(
			"1.2.1", "2.0.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0.
				DDMFormInstanceUpgradeProcess(
					_classNameLocalService, _counterLocalService,
					_resourceActions, _resourceActionLocalService,
					_resourcePermissionLocalService),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0.
				DDMFormInstanceRecordUpgradeProcess(_assetEntryLocalService),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0.
				DDMFormInstanceRecordVersionUpgradeProcess(),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0.
				ResourceActionUpgradeProcess(_resourceActionLocalService));

		registry.register(
			"2.0.0", "2.0.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_1.
				AutocompleteDDMTextFieldSettingUpgradeProcess(
					ddmFormJSONDeserializer, ddmFormSerializer),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_1.
				DDMFormFieldValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"2.0.1", "2.0.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_2.
				DDMFormInstanceStructureResourceActionUpgradeProcess(
					_resourceActions));

		registry.register(
			"2.0.2", "2.0.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_3.
				DataProviderInstanceUpgradeProcess(_jsonFactory),
			new DDMFormInstanceDefinitionUpgradeProcess(_jsonFactory),
			new DDMFormInstanceEntriesUpgradeProcess(_jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_3.
				DDMFormInstanceSettingsUpgradeProcess(_jsonFactory));

		registry.register(
			"2.0.3", "2.0.4",
			new DDMFormParagraphFieldsUpgradeProcess(_jsonFactory));

		registry.register(
			"2.0.4", "2.0.5",
			new DDMFormFieldValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"2.0.5", "2.0.6",
			new DDMDataProviderInstanceUpgradeProcess(_jsonFactory));

		registry.register("2.0.6", "2.0.7", new DummyUpgradeStep());

		registry.register("2.0.7", "2.0.8", new DummyUpgradeStep());

		registry.register("2.0.8", "2.0.9", new DummyUpgradeStep());

		registry.register("2.0.9", "2.0.10", new DummyUpgradeStep());

		registry.register("2.0.10", "2.0.11", new DummyUpgradeStep());

		registry.register(
			"2.0.11", "3.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					DDMContentTable.class, DDMDataProviderInstanceTable.class,
					DDMFormInstanceRecordTable.class,
					DDMFormInstanceRecordVersionTable.class,
					DDMFormInstanceTable.class,
					DDMFormInstanceVersionTable.class,
					DDMStructureLayoutTable.class, DDMStructureTable.class,
					DDMStructureVersionTable.class, DDMTemplateTable.class,
					DDMTemplateVersionTable.class
				}));

		registry.register(
			"3.0.0", "3.1.0", new DDMStructureLayoutUpgradeProcess());

		registry.register(
			"3.1.0", "3.1.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_1_1.
				DDMStructureLayoutUpgradeProcess());

		registry.register("3.1.1", "3.1.2", new DummyUpgradeStep());

		registry.register(
			"3.1.2", "3.2.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"DDMContent", "DDMDataProviderInstance",
						"DDMDataProviderInstanceLink", "DDMFormInstance",
						"DDMFormInstanceRecord", "DDMFormInstanceRecordVersion",
						"DDMFormInstanceVersion", "DDMStorageLink",
						"DDMStructure", "DDMStructureLayout",
						"DDMStructureLink", "DDMStructureVersion",
						"DDMTemplate", "DDMTemplateLink", "DDMTemplateVersion"
					};
				}

			});

		registry.register("3.2.0", "3.2.1", new DummyUpgradeStep());

		registry.register(
			"3.2.1", "3.2.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_2.
				DDMFormFieldValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"3.2.2", "3.2.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_2_3.
				DDMFormFieldValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"3.2.3", "3.2.4", new DDMContentUpgradeProcess(_jsonFactory));

		registry.register("3.2.4", "3.2.5", new DummyUpgradeStep());

		registry.register("3.2.5", "3.2.6", new DummyUpgradeStep());

		registry.register("3.2.6", "3.2.7", new DummyUpgradeStep());

		registry.register("3.2.7", "3.2.8", new DummyUpgradeStep());

		registry.register("3.2.8", "3.2.9", new DummyUpgradeStep());

		registry.register(
			"3.2.9", "3.3.0",
			new CTModelUpgradeProcess(
				"DDMStructure", "DDMStructureVersion", "DDMTemplate",
				"DDMTemplateVersion"));

		registry.register(
			"3.3.0", "3.4.0",
			new CTModelUpgradeProcess("DDMStructureLink", "DDMTemplateLink"));

		registry.register(
			"3.4.0", "3.5.0", DDMFormInstanceReportTable.create());

		registry.register(
			"3.5.0", "3.6.0",
			new CTModelUpgradeProcess(
				"DDMContent", "DDMDataProviderInstance",
				"DDMDataProviderInstanceLink", "DDMFormInstance",
				"DDMFormInstanceRecord", "DDMFormInstanceRecordVersion",
				"DDMFormInstanceReport", "DDMFormInstanceVersion",
				"DDMStorageLink", "DDMStructureLayout"));

		registry.register(
			"3.6.0", "3.7.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_7_0.
				UpgradeDDMDataProviderInstance());

		registry.register(
			"3.7.0", "3.7.1",
			new DDMStructureEmptyValidationUpgradeProcess(
				ddmFormJSONDeserializer, ddmFormSerializer));

		registry.register(
			"3.7.1", "3.7.2",
			UpgradeProcessFactory.alterColumnType(
				"DDMFormInstance", "description", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"DDMFormInstanceVersion", "description", "TEXT null"));

		registry.register(
			"3.7.2", "3.7.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_7_3.
				DDMFormInstanceReportUpgradeProcess(
					ddmFormJSONDeserializer, _jsonFactory));

		registry.register(
			"3.7.3", "3.7.4",
			UpgradeProcessFactory.runSQL(
				"update DDMTemplate set templateKey = " +
					"CONCAT(CAST_TEXT(templateId), '_key') where templateKey " +
						"is null or templateKey = ''"));

		registry.register(
			"3.7.4", "3.8.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_8_0.
				DDMStructureUpgradeProcess(
					ddmFormJSONDeserializer, _ddmFormLayoutDeserializer,
					ddmFormLayoutSerializer, ddmFormSerializer, _jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_8_0.
				DDMContentUpgradeProcess(
					ddmFormJSONDeserializer, _jsonFactory));

		registry.register(
			"3.8.0", "3.8.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_8_1.
				DDMFormFieldUpgradeProcess(_jsonFactory));

		registry.register(
			"3.8.1", "3.9.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_0.
				DDMDataProviderInstanceUpgradeProcess(
					_serviceTrackerMap, ddmFormValuesDeserializer,
					ddmFormValuesSerializer));

		registry.register(
			"3.9.0", "3.9.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_1.
				DDMStructureUpgradeProcess(
					ddmFormJSONDeserializer, ddmFormSerializer));

		registry.register("3.9.1", "3.10.0", new DummyUpgradeStep());

		registry.register("3.10.0", "3.10.1", new DummyUpgradeStep());

		registry.register(
			"3.10.1", "3.10.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2.
				DDMContentUpgradeProcess(
					ddmFormJSONDeserializer, ddmFormValuesDeserializer,
					ddmFormValuesSerializer, _jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2.
				DDMFormInstanceReportUpgradeProcess(_jsonFactory),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v3_10_2.
				DDMStructureUpgradeProcess(
					ddmFormJSONDeserializer, _ddmFormLayoutDeserializer,
					ddmFormLayoutSerializer, ddmFormSerializer, _jsonFactory));

		registry.register(
			"3.10.2", "4.0.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v4_0_0.
				DDMStructureUpgradeProcess(_ddmDataDefinitionConverter));

		registry.register(
			"4.0.0", "4.1.0",
			new DDMFieldUpgradeProcess(
				_jsonFactory, _jsonDDMFormDeserializer,
				_jsonDDMFormValuesDeserializer),
			new com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0.
				SchemaUpgradeProcess());

		registry.register(
			"4.1.0", "4.2.0",
			UpgradeProcessFactory.addColumns(
				"DDMFormInstanceRecord", "ipAddress VARCHAR(75) null"));

		registry.register(
			"4.2.0", "4.3.0",
			new DLFileEntryTypeDataDefinitionIdUpgradeProcess(
				_dlFileEntryTypeLocalService));

		registry.register(
			"4.3.0", "4.3.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_1.
				DDMFormInstanceUpgradeProcess(_jsonFactory));

		registry.register(
			"4.3.1", "4.3.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_2.
				DDMTemplateUpgradeProcess());

		registry.register(
			"4.3.2", "4.3.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v4_3_3.
				DDMStructureLayoutUpgradeProcess(
					_ddmFormLayoutDeserializer, ddmFormLayoutSerializer,
					_jsonFactory));

		registry.register(
			"4.3.3", "4.3.4",
			new DDMStructureLinkDLFileEntryTypeUpgradeProcess(
				_dlFileEntryTypeLocalService),
			new DLFileEntryTypeDDMFieldAttributeUpgradeProcess(
				_companyLocalService));

		registry.register(
			"4.3.4", "4.3.5", new DDMTemplateVersionUpgradeProcess());

		registry.register(
			"4.3.5", "5.0.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_0_0.
				PollsToDDMUpgradeProcess(
					ddmFormLayoutSerializer, ddmFormSerializer,
					ddmFormValuesSerializer, _resourceActionLocalService,
					_resourcePermissionLocalService));

		registry.register(
			"5.0.0", "5.1.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_0.
				DDMFieldUpgradeProcess());

		registry.register(
			"5.1.0", "5.1.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_1.
				DDMValidationUpgradeProcess(_jsonFactory));

		registry.register(
			"5.1.1", "5.1.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_2.
				DDMFormInstanceUpgradeProcess(_jsonFactory));

		registry.register(
			"5.1.2", "5.1.3",
			UpgradeProcessFactory.alterColumnType(
				"DDMField", "fieldName", "TEXT null"));

		registry.register(
			"5.1.3", "5.1.4",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_4.
				PollsPortletIdToDDMPortletIdUpgradeProcess());

		registry.register(
			"5.1.4", "5.1.5",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_5.
				DDMStructureLayoutUpgradeProcess(_jsonFactory));

		registry.register(
			"5.1.5", "5.2.0",
			new DDMFacetTemplateUpgradeProcess(_classNameLocalService));

		registry.register(
			"5.2.0", "5.2.1",
			new WorkflowDefinitionLinkUpgradeProcess(_classNameLocalService));

		registry.register(
			"5.2.1", "5.2.2",
			new DLFileEntryDDMFormInstanceRecordUpgradeProcess(
				_classNameLocalService));

		registry.register(
			"5.2.2", "5.3.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_0.
				DDMTemplateUpgradeProcess(_classNameLocalService));

		registry.register(
			"5.3.0", "5.3.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_1.
				DDMStructureUpgradeProcess(
					_jsonDDMFormDeserializer, _jsonDDMFormSerializer,
					_language));

		registry.register(
			"5.3.1", "5.3.2",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2.
				DDMFormInstanceSettingsUpgradeProcess(_jsonFactory));

		registry.register(
			"5.3.2", "5.3.3", new BrowserSnifferTemplateUpgradeProcess());

		registry.register(
			"5.3.3", "5.4.0",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_4_0.
				DDMFieldUpgradeProcess());

		registry.register(
			"5.4.0", "5.4.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_4_1.
				DDMStructureUpgradeProcess(
					_jsonDDMFormDeserializer, _jsonDDMFormSerializer,
					_language));

		registry.register("5.4.1", "5.4.2", new DummyUpgradeStep());

		registry.register(
			"5.4.2", "5.4.3",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_4_3.
				PollsPortletIdToDDMPortletIdUpgradeProcess());

		registry.register(
			"5.4.3", "5.4.4",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_4_4.
				DDMStructureUpgradeProcess(
					_jsonDDMFormDeserializer, _jsonDDMFormSerializer,
					_spiDDMFormRuleConverter));

		registry.register(
			"5.4.4", "5.4.5",
			new DDMTemplateLinkUpgradeProcess(_classNameLocalService));

		registry.register("5.4.5", "5.5.0", new DummyUpgradeStep());

		registry.register(
			"5.5.0", "5.5.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1.
				DDMFieldUpgradeProcess(),
			new DDMFieldAttributeUpgradeProcess());

		registry.register(
			"5.5.1", "5.6.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"DDMTemplate", "templateId"}};
				}

			});

		registry.register(
			"5.6.0", "5.6.1",
			new com.liferay.dynamic.data.mapping.internal.upgrade.v5_6_1.
				DDMFieldAttributeUpgradeProcess(
					_classNameLocalService, _dlFileEntryLocalService,
					_fileEntryFriendlyURLResolver, _groupLocalService,
					_userLocalService));

		registry.register(
			"5.6.1", "5.7.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {{"DDMStructure", "structureId"}};
				}

			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMDataProviderSettingsProvider.class,
			"ddm.data.provider.type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DDM _ddm;

	@Reference
	private DDMDataDefinitionConverter _ddmDataDefinitionConverter;

	@Reference
	private DDMFormLayoutDeserializer _ddmFormLayoutDeserializer;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(ddm.form.deserializer.type=json)")
	private DDMFormDeserializer _jsonDDMFormDeserializer;

	@Reference(target = "(ddm.form.layout.serializer.type=json)")
	private DDMFormLayoutSerializer _jsonDDMFormLayoutSerializer;

	@Reference(target = "(ddm.form.serializer.type=json)")
	private DDMFormSerializer _jsonDDMFormSerializer;

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	private DDMFormValuesDeserializer _jsonDDMFormValuesDeserializer;

	@Reference(target = "(ddm.form.values.serializer.type=json)")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceTrackerMap<String, DDMDataProviderSettingsProvider>
		_serviceTrackerMap;

	@Reference
	private SPIDDMFormRuleConverter _spiDDMFormRuleConverter;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ViewCountEntryLocalService _viewCountEntryLocalService;

	@Reference(target = "(ddm.form.deserializer.type=xsd)")
	private DDMFormDeserializer _xsdDDMFormDeserializer;

}
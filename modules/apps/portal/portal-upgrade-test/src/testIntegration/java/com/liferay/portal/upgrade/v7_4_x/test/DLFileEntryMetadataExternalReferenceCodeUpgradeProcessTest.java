/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;

import java.util.HashMap;

import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DLFileEntryMetadataExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		try {
			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.createDLFileEntryMetadata(
					CounterLocalServiceUtil.increment());

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId());

			serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);

			DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				PortalUtil.getClassNameId(DLFileEntryMetadata.class),
				StringPool.BLANK,
				RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
				new HashMap<>(), StringPool.BLANK,
				StorageType.DEFAULT.toString(), serviceContext);

			DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

			DDMFormField ddmFormField =
				DDMFormTestUtil.createLocalizableTextDDMFormField("name");

			ddmFormField.setRequired(true);

			DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

			DDMFormValues ddmFormValues =
				DDMFormValuesTestUtil.createDDMFormValues(ddmForm);

			dlFileEntryMetadata.setDDMStorageId(
				_ddmStorageEngineManager.create(
					group.getCompanyId(), ddmStructure.getStructureId(),
					ddmFormValues, serviceContext));

			DLFolder dlFolder = DLTestUtil.addDLFolder(group.getGroupId());

			DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
				dlFolder.getFolderId());

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			dlFileEntryMetadata.setDDMStructureId(
				ddmStructure.getStructureId());
			dlFileEntryMetadata.setFileEntryId(dlFileEntry.getFileEntryId());
			dlFileEntryMetadata.setFileVersionId(
				dlFileVersion.getFileVersionId());

			return new ExternalReferenceCodeModel[] {
				_dlFileEntryMetadataLocalService.addDLFileEntryMetadata(
					dlFileEntryMetadata)
			};
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
			ExternalReferenceCodeModel externalReferenceCodeModel,
			String tableName)
		throws PortalException {

		DLFileEntryMetadata dlFileEntryMetadata =
			(DLFileEntryMetadata)externalReferenceCodeModel;

		return _dlFileEntryMetadataLocalService.fetchDLFileEntryMetadata(
			dlFileEntryMetadata.getFileEntryMetadataId());
	}

	@Override
	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		if (tableName.equals("DLFileEntryMetadata")) {
			DLFileEntryMetadata dlFileEntryMetadata =
				(DLFileEntryMetadata)externalReferenceCodeModel;

			return dlFileEntryMetadata.getUuid();
		}

		return super.getExternalReferenceCode(
			externalReferenceCodeModel, tableName);
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"DLFileEntryMetadata"};
	}

	@Override
	protected BaseExternalReferenceCodeUpgradeProcess getUpgradeProcess() {
		return new BaseExternalReferenceCodeUpgradeProcess() {

			@Override
			protected String[] getTableNames() {
				return new String[] {"DLFileEntryMetadata"};
			}

		};
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return null;
	}

	@Override
	protected Version getVersion() {
		return null;
	}

	@Inject
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

}
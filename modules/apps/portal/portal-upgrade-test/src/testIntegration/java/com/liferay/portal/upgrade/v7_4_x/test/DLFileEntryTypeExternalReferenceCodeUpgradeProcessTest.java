/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;

import java.util.Collections;
import java.util.HashMap;

import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class DLFileEntryTypeExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
			new HashMap<>(), StringPool.BLANK, StorageType.DEFAULT.toString(),
			serviceContext);

		return new ExternalReferenceCodeModel[] {
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				serviceContext)
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
			ExternalReferenceCodeModel externalReferenceCodeModel,
			String tableName)
		throws PortalException {

		DLFileEntryType dlFileEntryType =
			(DLFileEntryType)externalReferenceCodeModel;

		return _dlFileEntryTypeLocalService.fetchFileEntryType(
			dlFileEntryType.getFileEntryTypeId());
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"DLFileEntryType"};
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new BaseExternalReferenceCodeUpgradeProcess() {

			@Override
			protected String[] getTableNames() {
				return new String[] {"DLFileEntryType"};
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
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

}
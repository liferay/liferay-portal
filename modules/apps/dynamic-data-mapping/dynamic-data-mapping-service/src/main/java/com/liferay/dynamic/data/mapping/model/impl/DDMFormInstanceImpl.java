/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DDMFormInstanceImpl extends DDMFormInstanceBaseImpl {

	@Override
	public DDMForm getDDMForm() throws PortalException {
		DDMStructure ddmStructure = getStructure();

		return ddmStructure.getDDMForm();
	}

	@Override
	public List<DDMFormInstanceRecord> getFormInstanceRecords() {
		return DDMFormInstanceRecordLocalServiceUtil.getFormInstanceRecords(
			getFormInstanceId());
	}

	@Override
	public DDMFormInstanceVersion getFormInstanceVersion(String version)
		throws PortalException {

		return DDMFormInstanceVersionLocalServiceUtil.getFormInstanceVersion(
			getFormInstanceId(), version);
	}

	@Override
	public long getObjectDefinitionId() throws PortalException {
		DDMFormInstanceSettings ddmFormInstanceSettings =
			DDMFormInstanceFactory.create(
				DDMFormInstanceSettings.class, getSettingsDDMFormValues());

		return GetterUtil.getLong(ddmFormInstanceSettings.objectDefinitionId());
	}

	@Override
	public DDMFormValues getSettingsDDMFormValues() {
		if (_ddmFormValues == null) {
			_ddmFormValues =
				DDMFormInstanceLocalServiceUtil.
					getFormInstanceSettingsFormValues(this);

			ddmFormValuesUpdateEntityCacheConsumer.accept(_ddmFormValues);
		}

		return _ddmFormValues;
	}

	@Override
	public DDMFormInstanceSettings getSettingsModel() throws PortalException {
		if (_formInstanceSettings == null) {
			_formInstanceSettings =
				DDMFormInstanceLocalServiceUtil.getFormInstanceSettingsModel(
					this);
		}

		return _formInstanceSettings;
	}

	@Override
	public String getStorageType() throws PortalException {
		DDMFormInstanceSettings ddmFormInstanceSettings =
			DDMFormInstanceFactory.create(
				DDMFormInstanceSettings.class, getSettingsDDMFormValues());

		String storageType = ddmFormInstanceSettings.storageType();

		if (Validator.isNotNull(storageType)) {
			return storageType;
		}

		return StorageType.DEFAULT.toString();
	}

	@Override
	public DDMStructure getStructure() throws PortalException {
		return DDMStructureLocalServiceUtil.getStructure(getStructureId());
	}

	@Override
	public void setSettings(String settings) {
		super.setSettings(settings);

		_formInstanceSettings = null;
	}

	@Override
	public void setSettingsDDMFormValues(DDMFormValues ddmFormValues) {
		_ddmFormValues = ddmFormValues;
	}

	@CacheField(
		methodName = "SettingsDDMFormValues", propagateToInterface = true
	)
	private DDMFormValues _ddmFormValues;

	private DDMFormInstanceSettings _formInstanceSettings;

}
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
		if (_objectDefinitionId == null) {
			_computeObjectDefinitionIdAndStorageType();
		}

		return _objectDefinitionId;
	}

	@Override
	public DDMFormValues getSettingsDDMFormValues() {
		if (_ddmFormValues == null) {
			_ddmFormValues =
				DDMFormInstanceLocalServiceUtil.
					getFormInstanceSettingsFormValues(this);
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
		if (_storageType == null) {
			_computeObjectDefinitionIdAndStorageType();
		}

		return _storageType;
	}

	@Override
	public DDMStructure getStructure() throws PortalException {
		return DDMStructureLocalServiceUtil.getStructure(getStructureId());
	}

	@Override
	public void setSettings(String settings) {
		super.setSettings(settings);

		_ddmFormValues = null;
		_formInstanceSettings = null;
		_objectDefinitionId = null;
		_storageType = null;
	}

	@Override
	public void setSettingsDDMFormValues(DDMFormValues ddmFormValues) {
		_ddmFormValues = ddmFormValues;
	}

	@Override
	public void setSettingsObjectDefinitionId(Long objectDefinitionId) {
		_objectDefinitionId = objectDefinitionId;
	}

	@Override
	public void setSettingsStorageType(String storageType) {
		_storageType = storageType;
	}

	private void _computeObjectDefinitionIdAndStorageType()
		throws PortalException {

		DDMFormInstanceSettings ddmFormInstanceSettings = getSettingsModel();

		String storageType = ddmFormInstanceSettings.storageType();

		if (Validator.isNull(storageType)) {
			storageType = StorageType.DEFAULT.toString();
		}

		_objectDefinitionId = GetterUtil.getLong(
			ddmFormInstanceSettings.objectDefinitionId());
		_storageType = storageType;

		objectDefinitionIdUpdateEntityCacheBiConsumer.accept(
			this, _objectDefinitionId);
		storageTypeUpdateEntityCacheBiConsumer.accept(this, _storageType);
	}

	private DDMFormValues _ddmFormValues;
	private DDMFormInstanceSettings _formInstanceSettings;

	@CacheField(methodName = "SettingsObjectDefinitionId")
	private Long _objectDefinitionId;

	@CacheField(methodName = "SettingsStorageType")
	private String _storageType;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldSettingLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectFieldImpl extends ObjectFieldBaseImpl {

	@Override
	public boolean compareBusinessType(String businessType) {
		return Objects.equals(getBusinessType(), businessType);
	}

	@Override
	public String[] getDBColumnNames() {
		if (compareBusinessType(ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE)) {
			return new String[] {
				"classNameId_" + getDBColumnName(),
				"classPK_" + getDBColumnName()
			};
		}

		return new String[] {getDBColumnName()};
	}

	@Override
	public String getI18nObjectFieldName() {
		return getName() + "_i18n";
	}

	@Override
	public ObjectDefinition getObjectDefinition() throws PortalException {
		return ObjectDefinitionLocalServiceUtil.getObjectDefinition(
			getObjectDefinitionId());
	}

	@Override
	public List<ObjectFieldSetting> getObjectFieldSettings() {
		if (_objectFieldSettings == null) {
			_objectFieldSettings =
				ObjectFieldSettingLocalServiceUtil.
					getObjectFieldObjectFieldSettings(getObjectFieldId());
		}

		return _objectFieldSettings;
	}

	@Override
	public ObjectRelationship getObjectRelationship() {
		return ObjectRelationshipLocalServiceUtil.
			fetchObjectRelationshipByObjectFieldId2(getObjectFieldId());
	}

	@Override
	public String getReadOnly() {
		String readOnly = super.getReadOnly();

		if (Validator.isNull(readOnly)) {
			return ObjectFieldConstants.READ_ONLY_FALSE;
		}

		return readOnly;
	}

	@Override
	public String getSortableDBColumnName() {
		return getDBColumnName() + Field.SORTABLE_FIELD_SUFFIX;
	}

	@Override
	public boolean hasInsertValues() {
		if (compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) ||
			compareBusinessType(ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean hasUniqueValues() {
		if (compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT) ||
			GetterUtil.getBoolean(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.NAME_UNIQUE_VALUES,
					getObjectFieldSettings()))) {

			return true;
		}

		return false;
	}

	@Override
	public boolean hasUpdateValues() {
		if (compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) ||
			compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT) ||
			compareBusinessType(ObjectFieldConstants.BUSINESS_TYPE_FORMULA)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isDeletionAllowed() throws PortalException {
		if (Validator.isNotNull(getRelationshipType())) {
			return false;
		}

		ObjectDefinition objectDefinition = getObjectDefinition();

		if ((objectDefinition.isUnmodifiableSystemObject() &&
			 !Objects.equals(
				 objectDefinition.getExtensionDBTableName(),
				 getDBTableName())) ||
			ObjectFieldUtil.isMetadata(getName())) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isMetadata() {
		return ObjectFieldUtil.isMetadata(getName());
	}

	@Override
	public void setObjectFieldSettings(
		List<ObjectFieldSetting> objectFieldSettings) {

		_objectFieldSettings = objectFieldSettings;
	}

	private List<ObjectFieldSetting> _objectFieldSettings;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryImpl extends ObjectEntryBaseImpl {

	@Override
	public String buildTreePath() throws PortalException {
		if (getObjectEntryFolderId() ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return StringPool.SLASH;
		}

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.getObjectEntryFolder(
				getObjectEntryFolderId());

		return objectEntryFolder.buildTreePath();
	}

	@Override
	public ObjectEntry cloneWithOriginalValues() {
		ObjectEntry objectEntry = super.cloneWithOriginalValues();

		objectEntry.setValues(_transientValues);

		return objectEntry;
	}

	@Override
	public String getModelClassName() {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				getObjectDefinitionId());

		if (objectDefinition == null) {
			return StringPool.BLANK;
		}

		return objectDefinition.getClassName();
	}

	@Override
	public long getNonzeroGroupId() throws PortalException {

		// TODO If permission checking works with the group's company ID, then
		// we should ensure it is always set and remove this workaround

		long groupId = getGroupId();

		if (groupId == 0) {
			Company company = CompanyLocalServiceUtil.getCompany(
				getCompanyId());

			groupId = company.getGroupId();
		}

		return groupId;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(
			PortalUtil.getClassNameId(getModelClassName()));
	}

	@Override
	public Map<Locale, String> getTitleMap() throws PortalException {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.getObjectDefinition(
				getObjectDefinitionId());

		if ((objectDefinition == null) ||
			(objectDefinition.getTitleObjectFieldId() == 0)) {

			return Collections.emptyMap();
		}

		ObjectField objectField = ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getTitleObjectFieldId());

		if ((objectField == null) || !objectField.isLocalized()) {
			return Collections.emptyMap();
		}

		Map<String, Serializable> values = getValues();

		Map<String, Serializable> localizedValues =
			(Map<String, Serializable>)values.get(
				objectField.getI18nObjectFieldName());

		if (MapUtil.isEmpty(localizedValues)) {
			return Collections.emptyMap();
		}

		Map<Locale, String> titleMap = new HashMap<>();

		for (Map.Entry<String, Serializable> entry :
				localizedValues.entrySet()) {

			titleMap.put(
				LocaleUtil.fromLanguageId(entry.getKey()),
				String.valueOf(
					ObjectEntryValuesUtil.getValue(
						entry.getKey(), objectField, new HashMap<>(values))));
		}

		return titleMap;
	}

	@Override
	public String getTitleValue() throws PortalException {
		return getTitleValue(null);
	}

	@Override
	public String getTitleValue(String languageId) throws PortalException {
		return getTitleValue(languageId, false);
	}

	@Override
	public String getTitleValue(String languageId, boolean useDefault)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.getObjectDefinition(
				getObjectDefinitionId());

		if ((objectDefinition != null) &&
			(objectDefinition.getTitleObjectFieldId() > 0)) {

			ObjectField objectField =
				ObjectFieldLocalServiceUtil.fetchObjectField(
					objectDefinition.getTitleObjectFieldId());

			if (objectField != null) {
				String title = String.valueOf(
					ObjectEntryValuesUtil.getValue(
						languageId, objectField, new HashMap<>(getValues())));

				if (Validator.isNull(title) && useDefault) {
					title = String.valueOf(
						ObjectEntryValuesUtil.getValue(
							getDefaultLanguageId(), objectField,
							new HashMap<>(getValues())));
				}

				if (Validator.isNotNull(title)) {
					return title;
				}

				if (Objects.equals(objectField.getName(), "id")) {
					return String.valueOf(getObjectEntryId());
				}

				return ObjectEntryValuesUtil.getValueString(
					objectField,
					ObjectEntryLocalServiceUtil.getSystemValues(this));
			}
		}

		return String.valueOf(getObjectEntryId());
	}

	@Override
	public String getURLTitle(Locale locale) {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
			return null;
		}

		FriendlyURLEntry friendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchMainFriendlyURLEntry(
				ClassNameLocalServiceUtil.getClassNameId(getModelClassName()),
				getObjectEntryId());

		if (friendlyURLEntry == null) {
			return null;
		}

		return friendlyURLEntry.getUrlTitle(LocaleUtil.toLanguageId(locale));
	}

	@Override
	public Map<String, String> getURLTitleMap() {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
			return null;
		}

		FriendlyURLEntry friendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchMainFriendlyURLEntry(
				ClassNameLocalServiceUtil.getClassNameId(getModelClassName()),
				getObjectEntryId());

		if (friendlyURLEntry == null) {
			return null;
		}

		return friendlyURLEntry.getLanguageIdToUrlTitleMap();
	}

	@Override
	public Map<String, Serializable> getValues() {
		if (_values == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Get values for object entry " + getObjectEntryId());
			}

			try {
				_values = ObjectEntryLocalServiceUtil.getValues(this);
			}
			catch (Exception exception) {
				_log.error(exception);

				return new HashMap<>();
			}
		}
		else if (_log.isDebugEnabled()) {
			_log.debug(
				"Use cached values for object entry " + getObjectEntryId());
		}

		return _values;
	}

	@Override
	public boolean isHead() {
		if (getHeadObjectEntryId() == getObjectEntryId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRootDescendantNode() {
		if ((getRootObjectEntryId() != 0) &&
			(getRootObjectEntryId() != getObjectEntryId())) {

			return true;
		}

		return false;
	}

	@Override
	public void setTransientValues(Map<String, Serializable> values) {
		_transientValues = values;
	}

	@Override
	public void setValues(Map<String, Serializable> values) {
		_values = values;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryImpl.class);

	private Map<String, Serializable> _transientValues;
	private Map<String, Serializable> _values;

}
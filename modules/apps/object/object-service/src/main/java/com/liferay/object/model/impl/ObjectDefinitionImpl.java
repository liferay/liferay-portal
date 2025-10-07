/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.definition.tree.util.ObjectDefinitionTreeUtil;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionSettingLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectDefinitionImpl extends ObjectDefinitionBaseImpl {

	public static String getShortName(String name) {
		String shortName = name;

		if (shortName.startsWith("C_")) {
			shortName = shortName.substring(2);
		}

		return shortName;
	}

	@Override
	public Locale getDefaultLocale() {
		return LocaleUtil.fromLanguageId(getDefaultLanguageId());
	}

	@Override
	public String getDestinationName() {
		return StringBundler.concat(
			"liferay/object/", getCompanyId(), StringPool.SLASH,
			getShortName());
	}

	@Override
	public String getExtensionDBTableName() {

		// See DBInspector.java#isObjectTable

		if (isUnmodifiableSystemObject()) {
			String extensionDBTableName = getDBTableName();

			if (extensionDBTableName.endsWith("_")) {
				extensionDBTableName += "x_";
			}
			else {
				extensionDBTableName += "_x_";
			}

			extensionDBTableName += getCompanyId();

			return extensionDBTableName;
		}

		return getDBTableName() + "_x";
	}

	@Override
	public String getLocalizationDBTableName() {
		if (!isEnableLocalization()) {
			return null;
		}

		return getDBTableName() + "_l";
	}

	@Override
	public List<ObjectDefinitionSetting> getObjectDefinitionSettings() {
		if (_objectDefinitionSettings == null) {
			_objectDefinitionSettings =
				ObjectDefinitionSettingLocalServiceUtil.
					getObjectDefinitionSettings(getObjectDefinitionId());
		}

		return _objectDefinitionSettings;
	}

	@Override
	public String getObjectFolderExternalReferenceCode() {
		ObjectFolder objectFolder =
			ObjectFolderLocalServiceUtil.fetchObjectFolder(getObjectFolderId());

		if (objectFolder == null) {
			return null;
		}

		return objectFolder.getExternalReferenceCode();
	}

	@Override
	public String getOSGiJaxRsName() {
		return getOSGiJaxRsName(StringPool.BLANK);
	}

	@Override
	public String getOSGiJaxRsName(String className) {
		return StringUtil.toLowerCase(getName()) + className;
	}

	@Override
	public String getPortletId() {
		if (isUnmodifiableSystemObject()) {
			throw new UnsupportedOperationException();
		}

		return ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE +
			StringUtil.split(getClassName(), StringPool.POUND)[1];
	}

	@Override
	public String getResourceName() {
		if (isUnmodifiableSystemObject()) {
			throw new UnsupportedOperationException();
		}

		return "com.liferay.object#" + getObjectDefinitionId();
	}

	@Override
	public String getRESTContextPath() {
		if (isUnmodifiableSystemObject()) {
			throw new UnsupportedOperationException();
		}

		if (isModifiableAndSystem()) {
			return ObjectDefinitionUtil.
				getModifiableSystemObjectDefinitionRESTContextPath(getName());
		}

		return "/c/" +
			TextFormatter.formatPlural(StringUtil.toLowerCase(getShortName()));
	}

	@Override
	public String getRootObjectDefinitionExternalReferenceCode() {
		ObjectDefinition rootObjectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				getRootObjectDefinitionId());

		if (rootObjectDefinition == null) {
			return null;
		}

		return rootObjectDefinition.getExternalReferenceCode();
	}

	@Override
	public long getRootObjectDefinitionId() {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPD-34594")) {
			return 0L;
		}

		long[] rootObjectDefinitionIds = getRootObjectDefinitionIds();

		if (ArrayUtil.isEmpty(rootObjectDefinitionIds)) {
			return 0L;
		}

		return rootObjectDefinitionIds[0];
	}

	@Override
	public long[] getRootObjectDefinitionIds() {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPD-34594")) {
			return new long[0];
		}

		return ObjectDefinitionTreeUtil.getRootObjectDefinitionIds(
			getObjectDefinitionId(),
			ObjectDefinitionSettingLocalServiceUtil.getService());
	}

	@Override
	public String getShortName() {
		return getShortName(getName());
	}

	@Override
	public boolean isApproved() {
		if (getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isDefaultStorageType() {
		return Objects.equals(
			getStorageType(), ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);
	}

	@Override
	public boolean isLinkedToObjectFolder(long objectFolderId) {
		if (getObjectFolderId() == objectFolderId) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isModifiableAndSystem() {
		if (isModifiable() && isSystem()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRootDescendantNode() {
		if (isRootNode()) {
			return false;
		}

		long[] rootObjectDefinitionIds = getRootObjectDefinitionIds();

		if (rootObjectDefinitionIds.length > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRootDescendantNode(long rootObjectDefinitionId) {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPD-34594")) {
			return false;
		}

		return ArrayUtil.contains(
			getRootObjectDefinitionIds(), rootObjectDefinitionId);
	}

	@Override
	public boolean isRootNode() {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPD-34594")) {
			return false;
		}

		return ArrayUtil.contains(
			getRootObjectDefinitionIds(), getObjectDefinitionId());
	}

	@Override
	public boolean isUnmodifiableSystemObject() {
		if (!isModifiable() && isSystem()) {
			return true;
		}

		return false;
	}

	@Override
	public void setObjectDefinitionSettings(
		List<ObjectDefinitionSetting> objectDefinitionSettings) {

		_objectDefinitionSettings = objectDefinitionSettings;
	}

	private List<ObjectDefinitionSetting> _objectDefinitionSettings;

}
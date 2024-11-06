/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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
	public String getPreviousRESTContextPath() {
		return _previousRESTContextPath;
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

		if (!isRootDescendantNode()) {
			if (isModifiableAndSystem()) {
				return ObjectDefinitionUtil.
					getModifiableSystemObjectDefinitionRESTContextPath(
						getName());
			}

			String lowerCaseShortName = StringUtil.toLowerCase(getShortName());

			return "/c/" + TextFormatter.formatPlural(lowerCaseShortName);
		}

		ObjectDefinition rootObjectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				getRootObjectDefinitionId());

		if (isModifiableAndSystem()) {
			String rootRESTContextPath =
				ObjectDefinitionUtil.
					getModifiableSystemObjectDefinitionRESTContextPath(
						rootObjectDefinition.getName());

			String restContextPath =
				ObjectDefinitionUtil.
					getModifiableSystemObjectDefinitionRESTContextPath(
						getName());

			restContextPath = restContextPath.substring(
				restContextPath.lastIndexOf(StringPool.SLASH));

			return rootRESTContextPath + restContextPath;
		}

		return StringBundler.concat(
			"/c/",
			TextFormatter.formatPlural(
				StringUtil.toLowerCase(rootObjectDefinition.getShortName())),
			StringPool.SLASH,
			TextFormatter.formatPlural(StringUtil.toLowerCase(getShortName())));
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
		if (Objects.equals(
				getStorageType(),
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT)) {

			return true;
		}

		return false;
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
	public boolean isNodeCandidate() {
		if (!isApproved() && !isUnmodifiableSystemObject()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRootDescendantNode() {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPS-187142")) {
			return false;
		}

		if ((getRootObjectDefinitionId() > 0) && !isRootNode()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRootNode() {
		if (!FeatureFlagManagerUtil.isEnabled(getCompanyId(), "LPS-187142")) {
			return false;
		}

		if (getObjectDefinitionId() == getRootObjectDefinitionId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isUnmodifiableSystemObject() {
		if (!isModifiable() && isSystem()) {
			return true;
		}

		return false;
	}

	@Override
	public void setPreviousRESTContextPath(String previousRESTContextPath) {
		_previousRESTContextPath = previousRESTContextPath;
	}

	private String _previousRESTContextPath;

}
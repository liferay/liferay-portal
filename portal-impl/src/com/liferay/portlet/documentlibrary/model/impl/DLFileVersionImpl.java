/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.model.impl;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.io.InputStream;

import java.util.List;

/**
 * @author Jorge Ferrer
 * @author Alexander Chow
 */
public class DLFileVersionImpl extends DLFileVersionBaseImpl {

	@Override
	public String buildTreePath() throws PortalException {
		if (getFolderId() == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return StringPool.SLASH;
		}

		DLFolder dlFolder = getFolder();

		return dlFolder.buildTreePath();
	}

	@Override
	public InputStream getContentStream(boolean incrementCounter)
		throws PortalException {

		return DLFileEntryLocalServiceUtil.getFileAsStream(
			getFileEntryId(), getVersion(), incrementCounter);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             DLFileEntryTypeUtil#getDDMStructures(DLFileEntryType)}
	 */
	@Deprecated
	@Override
	public List<DDMStructure> getDDMStructures() throws PortalException {
		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeLocalServiceUtil.getFileEntryType(
				getFileEntryTypeId());

		return dlFileEntryType.getDDMStructures();
	}

	@Override
	public DLFileEntryType getDLFileEntryType() throws PortalException {
		return DLFileEntryTypeLocalServiceUtil.getFileEntryType(
			getFileEntryTypeId());
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		if (_expandoBridge == null) {
			_expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
				getCompanyId(), DLFileEntry.class.getName(), getPrimaryKey());
		}

		return _expandoBridge;
	}

	@Override
	public String getExtraSettings() {
		if (_extraSettingsUnicodeProperties == null) {
			return super.getExtraSettings();
		}

		return _extraSettingsUnicodeProperties.toString();
	}

	@Override
	public UnicodeProperties getExtraSettingsProperties() {
		if (_extraSettingsUnicodeProperties == null) {
			_extraSettingsUnicodeProperties = UnicodePropertiesBuilder.create(
				true
			).load(
				super.getExtraSettings()
			).build();
		}

		return _extraSettingsUnicodeProperties;
	}

	@Override
	public DLFileEntry getFileEntry() throws PortalException {
		return DLFileEntryLocalServiceUtil.getFileEntry(getFileEntryId());
	}

	@Override
	public DLFolder getFolder() throws PortalException {
		if (getFolderId() <= 0) {
			return new DLFolderImpl();
		}

		return DLFolderLocalServiceUtil.getFolder(getFolderId());
	}

	@Override
	public String getIcon() {
		return DLUtil.getFileIcon(getExtension());
	}

	@Override
	public String getStoreFileName() {
		if (Validator.isNull(getStoreUUID())) {
			return getVersion();
		}

		return getVersion() + StringPool.TILDE + getStoreUUID();
	}

	@Override
	public void setExtraSettings(String extraSettings) {
		_extraSettingsUnicodeProperties = null;

		super.setExtraSettings(extraSettings);
	}

	@Override
	public void setExtraSettingsProperties(
		UnicodeProperties extraSettingsUnicodeProperties) {

		_extraSettingsUnicodeProperties = extraSettingsUnicodeProperties;

		super.setExtraSettings(_extraSettingsUnicodeProperties.toString());
	}

	private transient ExpandoBridge _expandoBridge;
	private UnicodeProperties _extraSettingsUnicodeProperties;

}
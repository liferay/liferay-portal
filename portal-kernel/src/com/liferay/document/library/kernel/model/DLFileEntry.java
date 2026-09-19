/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.TreeModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the DLFileEntry service. Represents a row in the &quot;DLFileEntry&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl"
)
@ProviderType
public interface DLFileEntry
	extends DLFileEntryModel, PersistedModel, TreeModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<DLFileEntry, Long> FILE_ENTRY_ID_ACCESSOR =
		new Accessor<DLFileEntry, Long>() {

			@Override
			public Long get(DLFileEntry dlFileEntry) {
				return dlFileEntry.getFileEntryId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<DLFileEntry> getTypeClass() {
				return DLFileEntry.class;
			}

		};

	@Override
	public String buildTreePath()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.io.InputStream getContentStream()
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.io.InputStream getContentStream(String version)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.Map
		<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
				getDDMFormValuesMap(long fileVersionId)
			throws com.liferay.portal.kernel.exception.PortalException;

	public DLFileEntryType getDLFileEntryType()
		throws com.liferay.portal.kernel.exception.PortalException;

	public long getDataRepositoryId();

	public com.liferay.expando.kernel.model.ExpandoBridge getExpandoBridge();

	public com.liferay.portal.kernel.util.UnicodeProperties
		getExtraSettingsProperties();

	public java.util.List<DLFileShortcut> getFileShortcuts();

	public DLFileVersion getFileVersion()
		throws com.liferay.portal.kernel.exception.PortalException;

	public DLFileVersion getFileVersion(String version)
		throws com.liferay.portal.kernel.exception.PortalException;

	public java.util.List<DLFileVersion> getFileVersions(int status);

	public java.util.List<DLFileVersion> getFileVersions(
		int status, int start, int end);

	public int getFileVersionsCount(int status);

	public DLFolder getFolder()
		throws com.liferay.portal.kernel.exception.PortalException;

	public String getIcon();

	public String getIconCssClass();

	public DLFileVersion getLatestFileVersion(boolean trusted)
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.portal.kernel.lock.Lock getLock();

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	public String getLuceneProperties();

	public long getReadCount();

	public boolean hasLock();

	public boolean isCheckedOut();

	public boolean isInHiddenFolder();

	public void setExtraSettingsProperties(
		com.liferay.portal.kernel.util.UnicodeProperties
			extraSettingsUnicodeProperties);

}
// LIFERAY-SERVICE-BUILDER-HASH:-245621861
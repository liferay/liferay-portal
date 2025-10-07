/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.TreeModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ObjectEntryFolder service. Represents a row in the &quot;ObjectEntryFolder&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectEntryFolderImpl")
@ProviderType
public interface ObjectEntryFolder
	extends ObjectEntryFolderModel, PersistedModel, TreeModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectEntryFolderImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectEntryFolder, Long>
		OBJECT_ENTRY_FOLDER_ID_ACCESSOR =
			new Accessor<ObjectEntryFolder, Long>() {

				@Override
				public Long get(ObjectEntryFolder objectEntryFolder) {
					return objectEntryFolder.getObjectEntryFolderId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<ObjectEntryFolder> getTypeClass() {
					return ObjectEntryFolder.class;
				}

			};

	public java.util.List<Long> getAncestorObjectEntryFolderIds()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isRoot();

}
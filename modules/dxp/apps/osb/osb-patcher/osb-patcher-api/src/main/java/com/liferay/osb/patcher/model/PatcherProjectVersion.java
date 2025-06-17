/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the PatcherProjectVersion service. Represents a row in the &quot;PProjectVersion&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersionModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl"
)
@ProviderType
public interface PatcherProjectVersion
	extends PatcherProjectVersionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherProjectVersion, Long>
		PATCHER_PROJECT_VERSION_ID_ACCESSOR =
			new Accessor<PatcherProjectVersion, Long>() {

				@Override
				public Long get(PatcherProjectVersion patcherProjectVersion) {
					return patcherProjectVersion.getPatcherProjectVersionId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<PatcherProjectVersion> getTypeClass() {
					return PatcherProjectVersion.class;
				}

			};

}
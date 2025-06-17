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
 * The extended model interface for the PatcherProductVersion service. Represents a row in the &quot;PProductVersion&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProductVersionModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl"
)
@ProviderType
public interface PatcherProductVersion
	extends PatcherProductVersionModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherProductVersion, Long>
		PATCHER_PRODUCT_VERSION_ID_ACCESSOR =
			new Accessor<PatcherProductVersion, Long>() {

				@Override
				public Long get(PatcherProductVersion patcherProductVersion) {
					return patcherProductVersion.getPatcherProductVersionId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<PatcherProductVersion> getTypeClass() {
					return PatcherProductVersion.class;
				}

			};

}
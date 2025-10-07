/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CTSParent service. Represents a row in the &quot;CTSParent&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see CTSParentModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.change.tracking.sample.model.impl.CTSParentImpl"
)
@ProviderType
public interface CTSParent extends CTSParentModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.change.tracking.sample.model.impl.CTSParentImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CTSParent, Long> CTS_PARENT_ID_ACCESSOR =
		new Accessor<CTSParent, Long>() {

			@Override
			public Long get(CTSParent ctsParent) {
				return ctsParent.getCtsParentId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CTSParent> getTypeClass() {
				return CTSParent.class;
			}

		};

}
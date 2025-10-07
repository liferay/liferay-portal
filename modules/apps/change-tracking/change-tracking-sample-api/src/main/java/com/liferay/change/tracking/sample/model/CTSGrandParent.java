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
 * The extended model interface for the CTSGrandParent service. Represents a row in the &quot;CTSGrandParent&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParentModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl"
)
@ProviderType
public interface CTSGrandParent extends CTSGrandParentModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CTSGrandParent, Long>
		CTS_GRAND_PARENT_ID_ACCESSOR = new Accessor<CTSGrandParent, Long>() {

			@Override
			public Long get(CTSGrandParent ctsGrandParent) {
				return ctsGrandParent.getCtsGrandParentId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CTSGrandParent> getTypeClass() {
				return CTSGrandParent.class;
			}

		};

}
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
 * The extended model interface for the CTSChild service. Represents a row in the &quot;CTSChild&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see CTSChildModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.change.tracking.sample.model.impl.CTSChildImpl"
)
@ProviderType
public interface CTSChild extends CTSChildModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.change.tracking.sample.model.impl.CTSChildImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CTSChild, Long> CTS_CHILD_ID_ACCESSOR =
		new Accessor<CTSChild, Long>() {

			@Override
			public Long get(CTSChild ctsChild) {
				return ctsChild.getCtsChildId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CTSChild> getTypeClass() {
				return CTSChild.class;
			}

		};

}
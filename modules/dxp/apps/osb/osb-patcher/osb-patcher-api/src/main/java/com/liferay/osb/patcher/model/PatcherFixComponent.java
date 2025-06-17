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
 * The extended model interface for the PatcherFixComponent service. Represents a row in the &quot;OSBPatcher_PatcherFixComponent&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponentModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl"
)
@ProviderType
public interface PatcherFixComponent
	extends PatcherFixComponentModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherFixComponent, Long>
		PATCHER_FIX_COMPONENT_ID_ACCESSOR =
			new Accessor<PatcherFixComponent, Long>() {

				@Override
				public Long get(PatcherFixComponent patcherFixComponent) {
					return patcherFixComponent.getPatcherFixComponentId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<PatcherFixComponent> getTypeClass() {
					return PatcherFixComponent.class;
				}

			};

}
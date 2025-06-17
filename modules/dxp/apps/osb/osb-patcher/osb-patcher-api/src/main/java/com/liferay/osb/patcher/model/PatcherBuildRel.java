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
 * The extended model interface for the PatcherBuildRel service. Represents a row in the &quot;OSBPatcher_PatcherBuildRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl"
)
@ProviderType
public interface PatcherBuildRel extends PatcherBuildRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherBuildRel, Long>
		PATCHER_BUILD_REL_ID_ACCESSOR = new Accessor<PatcherBuildRel, Long>() {

			@Override
			public Long get(PatcherBuildRel patcherBuildRel) {
				return patcherBuildRel.getPatcherBuildRelId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<PatcherBuildRel> getTypeClass() {
				return PatcherBuildRel.class;
			}

		};

}
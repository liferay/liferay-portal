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
 * The extended model interface for the PatcherBuild service. Represents a row in the &quot;OSBPatcher_PatcherBuild&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildModel
 * @generated
 */
@ImplementationClassName("com.liferay.osb.patcher.model.impl.PatcherBuildImpl")
@ProviderType
public interface PatcherBuild extends PatcherBuildModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherBuildImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherBuild, Long> PATCHER_BUILD_ID_ACCESSOR =
		new Accessor<PatcherBuild, Long>() {

			@Override
			public Long get(PatcherBuild patcherBuild) {
				return patcherBuild.getPatcherBuildId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<PatcherBuild> getTypeClass() {
				return PatcherBuild.class;
			}

		};

}
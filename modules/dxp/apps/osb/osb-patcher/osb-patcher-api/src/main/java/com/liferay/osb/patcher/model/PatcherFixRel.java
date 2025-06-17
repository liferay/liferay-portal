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
 * The extended model interface for the PatcherFixRel service. Represents a row in the &quot;OSBPatcher_PatcherFixRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixRelModel
 * @generated
 */
@ImplementationClassName("com.liferay.osb.patcher.model.impl.PatcherFixRelImpl")
@ProviderType
public interface PatcherFixRel extends PatcherFixRelModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherFixRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherFixRel, Long>
		PATCHER_FIX_REL_ID_ACCESSOR = new Accessor<PatcherFixRel, Long>() {

			@Override
			public Long get(PatcherFixRel patcherFixRel) {
				return patcherFixRel.getPatcherFixRelId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<PatcherFixRel> getTypeClass() {
				return PatcherFixRel.class;
			}

		};

}
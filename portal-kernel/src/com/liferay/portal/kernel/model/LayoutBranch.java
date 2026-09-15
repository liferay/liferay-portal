/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the LayoutBranch service. Represents a row in the &quot;LayoutBranch&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchModel
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ImplementationClassName("com.liferay.portal.model.impl.LayoutBranchImpl")
@ProviderType
public interface LayoutBranch extends LayoutBranchModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.LayoutBranchImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<LayoutBranch, Long> LAYOUT_BRANCH_ID_ACCESSOR =
		new Accessor<LayoutBranch, Long>() {

			@Override
			public Long get(LayoutBranch layoutBranch) {
				return layoutBranch.getLayoutBranchId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<LayoutBranch> getTypeClass() {
				return LayoutBranch.class;
			}

		};

}
// LIFERAY-SERVICE-BUILDER-HASH:526839295
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the RecentLayoutSetBranch service. Represents a row in the &quot;RecentLayoutSetBranch&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutSetBranchModel
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ImplementationClassName(
	"com.liferay.portal.model.impl.RecentLayoutSetBranchImpl"
)
@ProviderType
public interface RecentLayoutSetBranch
	extends PersistedModel, RecentLayoutSetBranchModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.RecentLayoutSetBranchImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<RecentLayoutSetBranch, Long>
		RECENT_LAYOUT_SET_BRANCH_ID_ACCESSOR =
			new Accessor<RecentLayoutSetBranch, Long>() {

				@Override
				public Long get(RecentLayoutSetBranch recentLayoutSetBranch) {
					return recentLayoutSetBranch.getRecentLayoutSetBranchId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<RecentLayoutSetBranch> getTypeClass() {
					return RecentLayoutSetBranch.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-715234207
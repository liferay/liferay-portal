/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the FaroDataSourceUsage service. Represents a row in the &quot;OSBFaro_FaroDataSourceUsage&quot; database table, with each column mapped to a property of this class.
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsageModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.faro.model.impl.FaroDataSourceUsageImpl"
)
@ProviderType
public interface FaroDataSourceUsage
	extends FaroDataSourceUsageModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<FaroDataSourceUsage, Long>
		FARO_DATA_SOURCE_USAGE_ID_ACCESSOR =
			new Accessor<FaroDataSourceUsage, Long>() {

				@Override
				public Long get(FaroDataSourceUsage faroDataSourceUsage) {
					return faroDataSourceUsage.getFaroDataSourceUsageId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<FaroDataSourceUsage> getTypeClass() {
					return FaroDataSourceUsage.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-840880719
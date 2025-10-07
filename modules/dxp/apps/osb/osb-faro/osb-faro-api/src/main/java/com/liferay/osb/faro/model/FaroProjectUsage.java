/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the FaroProjectUsage service. Represents a row in the &quot;OSBFaro_FaroProjectUsage&quot; database table, with each column mapped to a property of this class.
 *
 * @author Matthew Kong
 * @see FaroProjectUsageModel
 * @generated
 */
@ImplementationClassName("com.liferay.osb.faro.model.impl.FaroProjectUsageImpl")
@ProviderType
public interface FaroProjectUsage
	extends FaroProjectUsageModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.faro.model.impl.FaroProjectUsageImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<FaroProjectUsage, Long>
		FARO_PROJECT_USAGE_ID_ACCESSOR =
			new Accessor<FaroProjectUsage, Long>() {

				@Override
				public Long get(FaroProjectUsage faroProjectUsage) {
					return faroProjectUsage.getFaroProjectUsageId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<FaroProjectUsage> getTypeClass() {
					return FaroProjectUsage.class;
				}

			};

}
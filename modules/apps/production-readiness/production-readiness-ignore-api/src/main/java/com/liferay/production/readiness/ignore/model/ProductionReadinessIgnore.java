/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ProductionReadinessIgnore service. Represents a row in the &quot;PR_ProductionReadinessIgnore&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnoreModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl"
)
@ProviderType
public interface ProductionReadinessIgnore
	extends PersistedModel, ProductionReadinessIgnoreModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ProductionReadinessIgnore, Long>
		PRODUCTION_READINESS_IGNORE_ID_ACCESSOR =
			new Accessor<ProductionReadinessIgnore, Long>() {

				@Override
				public Long get(
					ProductionReadinessIgnore productionReadinessIgnore) {

					return productionReadinessIgnore.
						getProductionReadinessIgnoreId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<ProductionReadinessIgnore> getTypeClass() {
					return ProductionReadinessIgnore.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-1512964551
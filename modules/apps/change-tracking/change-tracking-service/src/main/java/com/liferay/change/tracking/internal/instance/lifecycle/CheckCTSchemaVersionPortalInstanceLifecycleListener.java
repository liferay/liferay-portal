/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.instance.lifecycle;

import com.liferay.change.tracking.internal.util.CTCollectionExpirationUtil;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.model.CTSchemaVersionTable;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CheckCTSchemaVersionPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		List<CTSchemaVersion> ctSchemaVersions =
			_ctSchemaVersionLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					CTSchemaVersionTable.INSTANCE
				).from(
					CTSchemaVersionTable.INSTANCE
				).where(
					CTSchemaVersionTable.INSTANCE.companyId.eq(
						company.getCompanyId())
				).orderBy(
					CTSchemaVersionTable.INSTANCE.schemaVersionId.descending()
				).limit(
					0, 1
				));

		if (!ctSchemaVersions.isEmpty() &&
			!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
				ctSchemaVersions.get(0), false)) {

			CTCollectionExpirationUtil.expireCTCollections(
				_ctCollectionLocalService, _ctSchemaVersionLocalService);
		}
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.internal.portal.instance.lifecycle;

import com.liferay.address.internal.util.CompanyCountriesUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class PortalInstanceLifecycleListenerImpl
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		_countryLocalService.deleteCompanyCountries(company.getCompanyId());
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		CompanyCountriesUtil.populateCompanyCountries(
			company, _counterLocalService, _countryLocalService,
			_currentConnection.getConnection(
				InfrastructureUtil.getDataSource()));
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private CurrentConnection _currentConnection;

}
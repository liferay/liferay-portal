/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(enabled = false, service = {})
public class DefaultCompanyNameSwapper {

	@Activate
	protected void activate() {
		if (PropsHelperUtil.isCustomized(PropsKeys.COMPANY_DEFAULT_NAME)) {
			return;
		}

		String originalCompanyDefaultName = PropsValues.COMPANY_DEFAULT_NAME;

		PropsValues.COMPANY_DEFAULT_NAME = "Liferay DXP";

		try {
			Company defaultCompany = _companyLocalService.getCompany(
				PortalInstancePool.getDefaultCompanyId());

			if (!_hasCustomCompanyName(
					defaultCompany, originalCompanyDefaultName)) {

				defaultCompany = _updateCompanyName(defaultCompany);
			}

			if (!Objects.equals(
					defaultCompany.getWebId(),
					PropsValues.COMPANY_DEFAULT_WEB_ID)) {

				defaultCompany = _companyLocalService.getCompanyByWebId(
					PropsValues.COMPANY_DEFAULT_WEB_ID);

				if (!_hasCustomCompanyName(
						defaultCompany, originalCompanyDefaultName)) {

					_updateCompanyName(defaultCompany);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to swap default company name", portalException);
			}
		}
	}

	private boolean _hasCustomCompanyName(Company company, String defaultName)
		throws PortalException {

		String name = company.getName();

		if (Validator.isNotNull(name) && !name.equals(defaultName)) {
			return true;
		}

		return false;
	}

	private Company _updateCompanyName(Company company) {
		company.setName(PropsValues.COMPANY_DEFAULT_NAME);

		return _companyLocalService.updateCompany(company);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultCompanyNameSwapper.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}
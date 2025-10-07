/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.util.configuration;

import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Constants;

/**
 * @author Raymond Augé
 */
public class ConfigurationFactoryUtil {

	public static <E extends Throwable> void executeAsCompany(
			CompanyLocalService companyLocalService,
			Map<String, Object> properties,
			UnsafeConsumer<Long, E> unsafeConsumer)
		throws E {

		long companyId = getCompanyId(companyLocalService, properties);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			unsafeConsumer.accept(companyId);
		}
	}

	public static long getCompanyId(
			CompanyLocalService companyLocalService,
			Map<String, Object> properties)
		throws IllegalStateException {

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		if (companyId > 0) {
			return companyId;
		}

		String webId = (String)properties.get(
			"dxp.lxc.liferay.com.virtualInstanceId");

		if (Objects.equals(webId, "default")) {
			webId = PropsValues.COMPANY_DEFAULT_WEB_ID;
		}

		if (Validator.isNull(webId)) {
			throw new IllegalStateException(
				"The property \"companyId\" or " +
					"\"dxp.lxc.liferay.com.virtualInstanceId\" must be set");
		}

		try {
			Company company = companyLocalService.getCompanyByWebId(webId);

			return company.getCompanyId();
		}
		catch (PortalException portalException) {
			throw new IllegalStateException(portalException);
		}
	}

	public static String getExternalReferenceCode(
			Map<String, Object> properties)
		throws IllegalStateException {

		String factoryPid = GetterUtil.getString(
			properties.get("service.factoryPid"));

		if (Validator.isNull(factoryPid)) {
			throw new IllegalStateException("Service factory PID is null");
		}

		List<String> servicePids = StringPlus.asList(
			properties.get(Constants.SERVICE_PID));

		if (servicePids.isEmpty()) {
			throw new IllegalStateException("Service PID is null");
		}

		return getExternalReferenceCode(factoryPid, servicePids);
	}

	public static String getExternalReferenceCode(
			String factoryPid, List<String> servicePids)
		throws IllegalStateException {

		for (String servicePid : servicePids) {
			if (servicePid.startsWith(factoryPid)) {
				String externalReferenceCode = servicePid.substring(
					factoryPid.length() + 1);

				// LPS-172217

				int index = externalReferenceCode.indexOf('/');

				if (index != -1) {
					externalReferenceCode = externalReferenceCode.substring(
						0, index);
				}

				return externalReferenceCode;
			}
		}

		throw new IllegalStateException(
			StringBundler.concat(
				"No service PID starts with factory PID (", factoryPid, ")"));
	}

	public static String getFactoryPidFromPid(String pid) {
		int index = pid.indexOf('~');

		if (index != -1) {
			return pid.substring(0, index);
		}

		return null;
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.instance.lifecycle;

import com.liferay.client.extension.type.configuration.CETConfiguration;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.List;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class ClientExtensionAllCompaniesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		List<String> externalReferenceCodes = StringUtil.split(
			PropsUtil.get(
				"client.extension.all.companies.external.reference.codes"));

		if (ListUtil.isEmpty(externalReferenceCodes)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping because no external reference codes were " +
						"specified");
			}

			return;
		}

		if (Objects.equals(company.getDefaultWebId(), company.getWebId())) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping default company " + company.getWebId());
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Add client extensions ",
					StringUtil.merge(
						externalReferenceCodes, StringPool.COMMA_AND_SPACE),
					" for the default company ", company.getDefaultWebId(),
					" to company ", company.getWebId()));
		}

		for (String externalReferenceCode : externalReferenceCodes) {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(service.pid=", CETConfiguration.class.getName(), "~",
						externalReferenceCode, ")"));

			if (configurations == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"No client extension found with external ",
							"reference code ", externalReferenceCode));
				}

				continue;
			}

			Configuration configuration = configurations[0];

			CETConfiguration cetConfiguration =
				ConfigurableUtil.createConfigurable(
					CETConfiguration.class,
					configuration.getProcessedProperties(null));

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Adding client extension with external reference code ",
						externalReferenceCode, " to company ",
						company.getWebId()));
			}

			_cetManager.addCET(
				cetConfiguration, company.getCompanyId(),
				externalReferenceCode);
		}

		super.portalInstanceRegistered(company);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionAllCompaniesPortalInstanceLifecycleListener.class);

	@Reference
	private CETManager _cetManager;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfiguration;
import com.liferay.user.associated.data.web.internal.util.AnonymousUserConfigurationUtil;

import java.util.Dictionary;
import java.util.ResourceBundle;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "model.class.name=com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfiguration",
	service = ConfigurationModelListener.class
)
public class AnonymousUserConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		long companyId = (long)properties.get("companyId");

		try {
			_companyLocalService.getCompanyById(companyId);

			_userLocalService.getUserById(
				companyId, (long)properties.get("userId"));

			_validateUniqueConfiguration(pid, companyId);
		}
		catch (Exception exception) {
			throw new ConfigurationModelListenerException(
				exception.getMessage(), AnonymousUserConfiguration.class,
				getClass(), properties);
		}
	}

	private void _validateUniqueConfiguration(String pid, long companyId)
		throws Exception {

		Configuration configuration = AnonymousUserConfigurationUtil.get(
			companyId, _configurationAdmin);

		if ((configuration == null) || pid.equals(configuration.getPid())) {
			return;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", LocaleUtil.getMostRelevantLocale(), getClass());

		String message = ResourceBundleUtil.getString(
			resourceBundle,
			"an-anonymous-user-is-already-defined-for-the-company-x",
			String.valueOf(companyId));

		throw new Exception(message);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private UserLocalService _userLocalService;

}
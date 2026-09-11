/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;
import com.liferay.sharepoint.rest.repository.internal.util.SharepointRepositoryClassNameUtil;

import java.io.IOException;

import java.util.Dictionary;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"model.class.name=com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration",
		"model.class.name=com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration.scoped"
	},
	service = ConfigurationModelListener.class
)
public class SharepointRepositoryConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeDelete(String pid)
		throws ConfigurationModelListenerException {

		Dictionary<String, Object> properties = _getProperties(pid);

		if ((properties == null) || (_getRepositoriesCount(properties) == 0)) {
			return;
		}

		throw new ConfigurationModelListenerException(
			_language.get(
				LocaleThreadLocal.getThemeDisplayLocale(),
				"the-sharepoint-configuration-cannot-be-deleted-because-it-" +
					"is-used-by-one-or-more-repositories"),
			SharepointRepositoryConfiguration.class, getClass(), properties);
	}

	private Dictionary<String, Object> _getProperties(String pid)
		throws ConfigurationModelListenerException {

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")");

			if (configurations == null) {
				return null;
			}

			Configuration configuration = configurations[0];

			return configuration.getProperties();
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationModelListenerException(
				exception, SharepointRepositoryConfiguration.class, getClass(),
				null);
		}
	}

	private long _getRepositoriesCount(Dictionary<String, Object> properties) {
		ClassName className = _classNameLocalService.fetchClassName(
			SharepointRepositoryClassNameUtil.getClassName(
				GetterUtil.getString(properties.get("name"))));

		if (className == null) {
			return 0;
		}

		DynamicQuery dynamicQuery = _repositoryLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"classNameId", className.getClassNameId()));

		long companyId = GetterUtil.getLong(properties.get("companyId"));

		if (companyId > 0) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.eq("companyId", companyId));
		}

		return _repositoryLocalService.dynamicQueryCount(dynamicQuery);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private Language _language;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.plugin.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;

/**
 * @author Raymond Augé
 */
public class SiteExternalReferenceCodeToGroupConfigurationPluginImpl
	implements ConfigurationPlugin {

	public SiteExternalReferenceCodeToGroupConfigurationPluginImpl(
		BundleContext bundleContext) {

		_bundleContext = bundleContext;
	}

	@Override
	public void modifyConfiguration(
		ServiceReference<?> serviceReference,
		Dictionary<String, Object> properties) {

		String siteExternalReferenceCode = (String)properties.get(
			"siteExternalReferenceCode");

		if (Validator.isNull(siteExternalReferenceCode)) {
			return;
		}

		try {
			ServiceReference<DataSource> dataSourceServiceReference =
				_bundleContext.getServiceReference(DataSource.class);

			if (dataSourceServiceReference == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Data source service is null");
				}

				return;
			}

			DataSource dataSource = _bundleContext.getService(
				dataSourceServiceReference);

			try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						_db.buildSQL(
							"select groupId from Group_ where " +
								"externalReferenceCode = ?"))) {

				preparedStatement.setString(1, siteExternalReferenceCode);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						long groupId = resultSet.getLong(1);

						properties.put("groupId", groupId);

						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Injected group ID ", groupId,
									" for site external reference code ",
									siteExternalReferenceCode));
						}
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Skip site external reference code " +
						siteExternalReferenceCode);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteExternalReferenceCodeToGroupConfigurationPluginImpl.class);

	private final BundleContext _bundleContext;
	private final DB _db = DBManagerUtil.getDB();

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.net.URL;

import java.sql.Connection;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Luis Ortiz
 */
public class ServiceComponentPostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public ServiceComponentPostUpgradeDataCleanupProcess(
		Connection connection,
		ServiceComponentLocalService serviceComponentLocalService) {

		_connection = connection;
		_serviceComponentLocalService = serviceComponentLocalService;
	}

	@Override
	public void cleanUp() throws Exception {
		DBInspector dbInspector = new DBInspector(_connection);

		Set<ServiceComponent> latestServiceComponents = new HashSet<>(
			_serviceComponentLocalService.getLatestServiceComponents());

		List<ServiceComponent> serviceComponents =
			_serviceComponentLocalService.getServiceComponents(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		String tableName = dbInspector.normalizeName("ServiceComponent");

		Bundle currentBundle = FrameworkUtil.getBundle(
			ServiceComponentPostUpgradeDataCleanupProcess.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		for (ServiceComponent serviceComponent : serviceComponents) {
			String buildNamespace = serviceComponent.getBuildNamespace();

			if (!buildNamespace.contains(StringPool.PERIOD) &&
				!buildNamespace.equals(
					ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME)) {

				_serviceComponentLocalService.deleteServiceComponent(
					serviceComponent);

				DataCleanupLoggingUtil.logDelete(
					_log, 1, tableName,
					buildNamespace + " is not a fully qualified name");
			}
			else if (buildNamespace.startsWith("com.liferay.")) {
				Bundle bundle = BundleUtil.getBundle(
					bundleContext, buildNamespace);

				if (bundle == null) {
					_serviceComponentLocalService.deleteServiceComponent(
						serviceComponent);

					DataCleanupLoggingUtil.logDelete(
						_log, 1, tableName,
						buildNamespace +
							" does not match with any existing bundle");

					continue;
				}

				if (!latestServiceComponents.contains(serviceComponent)) {
					continue;
				}

				String buildNumberServiceProperties = null;

				URL servicePropertiesURL = bundle.getResource(
					"service.properties");

				if (servicePropertiesURL != null) {
					Properties properties = PropertiesUtil.load(
						servicePropertiesURL);

					buildNumberServiceProperties = properties.getProperty(
						"build.number");
				}

				if (((buildNumberServiceProperties != null) &&
					 !StringUtil.equals(
						 buildNumberServiceProperties,
						 String.valueOf(serviceComponent.getBuildNumber()))) ||
					!Objects.equals(
						serviceComponent.getData(), _generateXML(bundle))) {

					_log.error(
						StringBundler.concat(
							"Content of table ", tableName, " for bundle ",
							buildNamespace, " is outdated"));
				}
			}
		}
	}

	private String _generateXML(Bundle bundle) throws Exception {
		Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

		Element dataElement = document.addElement("data");

		Element tablesSQLElement = dataElement.addElement("tables-sql");

		tablesSQLElement.addCDATA(DBResourceUtil.getModuleTablesSQL(bundle));

		Element sequencesSQLElement = dataElement.addElement("sequences-sql");

		sequencesSQLElement.addCDATA(
			DBResourceUtil.getModuleSequencesSQL(bundle));

		Element indexesSQLElement = dataElement.addElement("indexes-sql");

		indexesSQLElement.addCDATA(DBResourceUtil.getModuleIndexesSQL(bundle));

		return document.formattedString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceComponentPostUpgradeDataCleanupProcess.class);

	private final Connection _connection;
	private final ServiceComponentLocalService _serviceComponentLocalService;

}
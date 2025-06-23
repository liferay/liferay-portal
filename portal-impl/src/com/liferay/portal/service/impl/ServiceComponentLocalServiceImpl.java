/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.exception.OldServiceComponentException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.configuration.ServiceComponentConfiguration;
import com.liferay.portal.kernel.service.configuration.servlet.ServletServiceContextComponentConfiguration;
import com.liferay.portal.kernel.upgrade.util.UpgradeTable;
import com.liferay.portal.kernel.upgrade.util.UpgradeTableListener;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.service.base.ServiceComponentLocalServiceBaseImpl;
import com.liferay.portal.upgrade.util.UpgradeTableFactoryUtil;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class ServiceComponentLocalServiceImpl
	extends ServiceComponentLocalServiceBaseImpl {

	@Override
	public List<ServiceComponent> getLatestServiceComponents() {
		return serviceComponentFinder.findByMaxBuildNumber();
	}

	@Override
	public ServiceComponent initServiceComponent(
			ServiceComponentConfiguration serviceComponentConfiguration,
			ClassLoader classLoader, String buildNamespace, long buildNumber,
			long buildDate)
		throws PortalException {

		try {
			ModelHintsUtil.read(
				classLoader,
				serviceComponentConfiguration.getModelHintsInputStream());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		try {
			ModelHintsUtil.read(
				classLoader,
				serviceComponentConfiguration.getModelHintsExtInputStream());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		long previousBuildNumber = 0;
		ServiceComponent previousServiceComponent = null;

		Map<String, ServiceComponent> serviceComponents =
			_serviceComponentsDCLSingleton.getSingleton(
				this::_createServiceComponents);

		ServiceComponent serviceComponent = serviceComponents.get(
			buildNamespace);

		if (serviceComponent == null) {
			long serviceComponentId = counterLocalService.increment();

			serviceComponent = serviceComponentPersistence.create(
				serviceComponentId);

			serviceComponent.setBuildNamespace(buildNamespace);
			serviceComponent.setBuildNumber(buildNumber);
			serviceComponent.setBuildDate(buildDate);
		}
		else {
			previousBuildNumber = serviceComponent.getBuildNumber();

			if (previousBuildNumber < buildNumber) {
				List<ServiceComponent> currentServiceComponents =
					serviceComponentPersistence.findByBuildNamespace(
						buildNamespace, 0, 1);

				ServiceComponent currentServiceComponent =
					currentServiceComponents.get(0);

				long currentBuildNumber =
					currentServiceComponent.getBuildNumber();

				if (currentBuildNumber > previousBuildNumber) {
					serviceComponent = currentServiceComponent;

					previousBuildNumber = currentBuildNumber;

					serviceComponents.put(buildNamespace, serviceComponent);
				}
			}

			if (previousBuildNumber < buildNumber) {
				previousServiceComponent = serviceComponent;

				long serviceComponentId = counterLocalService.increment();

				serviceComponent = serviceComponentPersistence.create(
					serviceComponentId);

				serviceComponent.setBuildNamespace(buildNamespace);
				serviceComponent.setBuildNumber(buildNumber);
				serviceComponent.setBuildDate(buildDate);
			}
			else if (previousBuildNumber > buildNumber) {
				throw new OldServiceComponentException(
					StringBundler.concat(
						"Build namespace ", buildNamespace,
						" has build number ", previousBuildNumber,
						" which is newer than ", buildNumber));
			}
			else if (!StringUtil.equals(
						buildNamespace,
						ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME)) {

				return serviceComponent;
			}
		}

		try {
			Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

			Element dataElement = document.addElement("data");

			Element tablesSQLElement = dataElement.addElement("tables-sql");

			String tablesSQL = StringUtil.read(
				serviceComponentConfiguration.getSQLTablesInputStream());

			tablesSQLElement.addCDATA(tablesSQL);

			Element sequencesSQLElement = dataElement.addElement(
				"sequences-sql");

			String sequencesSQL = StringUtil.read(
				serviceComponentConfiguration.getSQLSequencesInputStream());

			sequencesSQLElement.addCDATA(sequencesSQL);

			Element indexesSQLElement = dataElement.addElement("indexes-sql");

			String indexesSQL = StringUtil.read(
				serviceComponentConfiguration.getSQLIndexesInputStream());

			indexesSQLElement.addCDATA(indexesSQL);

			String dataXML = document.formattedString();

			serviceComponent.setData(dataXML);

			serviceComponent = serviceComponentPersistence.update(
				serviceComponent);

			if (((serviceComponentConfiguration instanceof
					ServletServiceContextComponentConfiguration) &&
				 (previousServiceComponent == null)) ||
				((previousBuildNumber < buildNumber) &&
				 (previousServiceComponent != null))) {

				serviceComponentLocalService.upgradeDB(
					classLoader, buildNamespace, buildNumber,
					previousServiceComponent, tablesSQL, sequencesSQL,
					indexesSQL);
			}

			serviceComponents.put(buildNamespace, serviceComponent);

			removeOldServiceComponents(buildNamespace);

			return serviceComponent;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public void upgradeDB(
			ClassLoader classLoader, String buildNamespace, long buildNumber,
			ServiceComponent previousServiceComponent, String tablesSQL,
			String sequencesSQL, String indexesSQL)
		throws Exception {

		_upgradeDB(
			classLoader, buildNamespace, buildNumber, previousServiceComponent,
			tablesSQL, sequencesSQL, indexesSQL);
	}

	protected List<String> getModelNames(ClassLoader classLoader)
		throws DocumentException, IOException {

		List<String> modelNames = new ArrayList<>();

		String xml = StringUtil.read(
			classLoader, "META-INF/portlet-model-hints.xml");

		modelNames.addAll(getModelNames(xml));

		try {
			xml = StringUtil.read(
				classLoader, "META-INF/portlet-model-hints-ext.xml");

			modelNames.addAll(getModelNames(xml));
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"No optional file META-INF/portlet-model-hints-ext.xml " +
						"found",
					exception);
			}
		}

		return modelNames;
	}

	protected List<String> getModelNames(String xml) throws DocumentException {
		Document document = UnsecureSAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		return TransformUtil.transform(
			rootElement.elements("model"),
			modelElement -> modelElement.attributeValue("name"));
	}

	protected List<String> getModifiedTableNames(
		String previousTablesSQL, String tablesSQL) {

		List<String> previousTablesSQLParts = ListUtil.fromArray(
			StringUtil.split(previousTablesSQL, StringPool.SEMICOLON));
		List<String> tablesSQLParts = ListUtil.fromArray(
			StringUtil.split(tablesSQL, StringPool.SEMICOLON));

		tablesSQLParts.removeAll(previousTablesSQLParts);

		return TransformUtil.transform(
			tablesSQLParts,
			tablesSQLPart -> {
				int x = tablesSQLPart.indexOf("create table ");
				int y = tablesSQLPart.indexOf(" (");

				return tablesSQLPart.substring(x + 13, y);
			});
	}

	protected UpgradeTableListener getUpgradeTableListener(
		ClassLoader classLoader, Class<?> modelClass) {

		String modelClassName = modelClass.getName();

		String upgradeTableListenerClassName = modelClassName;

		upgradeTableListenerClassName = StringUtil.replaceLast(
			upgradeTableListenerClassName, ".model.impl.", ".model.upgrade.");
		upgradeTableListenerClassName = StringUtil.replaceLast(
			upgradeTableListenerClassName, "ModelImpl", "UpgradeTableListener");

		try {
			UpgradeTableListener upgradeTableListener =
				(UpgradeTableListener)InstanceFactory.newInstance(
					classLoader, upgradeTableListenerClassName);

			if (_log.isInfoEnabled()) {
				_log.info("Instantiated " + upgradeTableListenerClassName);
			}

			return upgradeTableListener;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to instantiate " + upgradeTableListenerClassName,
					exception);
			}

			return null;
		}
	}

	protected void removeOldServiceComponents(String buildNamespace) {
		int serviceComponentsCount =
			serviceComponentPersistence.countByBuildNamespace(buildNamespace);

		if (serviceComponentsCount < _SERVICE_COMPONENTS_MAX) {
			return;
		}

		List<ServiceComponent> serviceComponents =
			serviceComponentPersistence.findByBuildNamespace(
				buildNamespace, _SERVICE_COMPONENTS_MAX,
				serviceComponentsCount);

		for (ServiceComponent serviceComponent : serviceComponents) {
			serviceComponentPersistence.remove(serviceComponent);
		}
	}

	protected void upgradeModels(
			ClassLoader classLoader, ServiceComponent previousServiceComponent,
			String tablesSQL)
		throws Exception {

		List<String> modifiedTableNames = getModifiedTableNames(
			previousServiceComponent.getTablesSQL(), tablesSQL);

		List<String> modelNames = getModelNames(classLoader);

		for (String modelName : modelNames) {
			int pos = modelName.lastIndexOf(".model.");

			Class<?> modelClass = Class.forName(
				StringBundler.concat(
					modelName.substring(0, pos), ".model.impl.",
					modelName.substring(pos + 7), "ModelImpl"),
				true, classLoader);

			Field dataSourceField = modelClass.getField("DATA_SOURCE");

			String dataSource = (String)dataSourceField.get(null);

			if (!dataSource.equals(_DATA_SOURCE_DEFAULT)) {
				continue;
			}

			Field tableNameField = modelClass.getField("TABLE_NAME");

			String tableName = (String)tableNameField.get(null);

			if (!modifiedTableNames.contains(tableName)) {
				continue;
			}

			Field tableColumnsField = modelClass.getField("TABLE_COLUMNS");

			Object[][] tableColumns = (Object[][])tableColumnsField.get(null);

			UpgradeTable upgradeTable = UpgradeTableFactoryUtil.getUpgradeTable(
				tableName, tableColumns);

			UpgradeTableListener upgradeTableListener = getUpgradeTableListener(
				classLoader, modelClass);

			Field tableSQLCreateField = modelClass.getField("TABLE_SQL_CREATE");

			String tableSQLCreate = (String)tableSQLCreateField.get(null);

			upgradeTable.setCreateSQL(tableSQLCreate);

			if (upgradeTableListener != null) {
				upgradeTableListener.onBeforeUpdateTable(
					previousServiceComponent, upgradeTable);
			}

			upgradeTable.updateTable();

			if (upgradeTableListener != null) {
				upgradeTableListener.onAfterUpdateTable(
					previousServiceComponent, upgradeTable);
			}
		}
	}

	private Map<String, ServiceComponent> _createServiceComponents() {
		Map<String, ServiceComponent> serviceComponents =
			new ConcurrentHashMap<>();

		for (ServiceComponent serviceComponent :
				serviceComponentPersistence.findAll()) {

			String buildNamespace = serviceComponent.getBuildNamespace();

			ServiceComponent previousServiceComponent = serviceComponents.get(
				buildNamespace);

			if ((previousServiceComponent == null) ||
				(serviceComponent.getBuildNumber() >
					previousServiceComponent.getBuildNumber())) {

				serviceComponents.put(buildNamespace, serviceComponent);
			}
		}

		return serviceComponents;
	}

	private void _upgradeDB(
			ClassLoader classLoader, String buildNamespace, long buildNumber,
			ServiceComponent previousServiceComponent, String tablesSQL,
			String sequencesSQL, String indexesSQL)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		if (previousServiceComponent == null) {
			if (_log.isInfoEnabled()) {
				_log.info("Running " + buildNamespace + " SQL scripts");
			}

			db.runSQLTemplate(tablesSQL, false);
			db.runSQLTemplate(sequencesSQL, false);
			db.runSQLTemplate(indexesSQL, false);
		}
		else if (PropsValues.SCHEMA_MODULE_BUILD_AUTO_UPGRADE) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Auto upgrading ", buildNamespace,
						" database to build number ", buildNumber,
						" is not supported for a production environment. ",
						"Write an UpgradeStep to ensure data is upgraded ",
						"correctly."));
			}

			if (!tablesSQL.equals(previousServiceComponent.getTablesSQL())) {
				if (_log.isInfoEnabled()) {
					_log.info("Upgrading database with tables.sql");
				}

				db.runSQLTemplate(tablesSQL, false);

				upgradeModels(classLoader, previousServiceComponent, tablesSQL);
			}

			if (!sequencesSQL.equals(
					previousServiceComponent.getSequencesSQL())) {

				if (_log.isInfoEnabled()) {
					_log.info("Upgrading database with sequences.sql");
				}

				db.runSQLTemplate(sequencesSQL, false);
			}

			if (!indexesSQL.equals(previousServiceComponent.getIndexesSQL()) ||
				!tablesSQL.equals(previousServiceComponent.getTablesSQL())) {

				if (_log.isInfoEnabled()) {
					_log.info("Upgrading database with indexes.sql");
				}

				db.runSQLTemplate(indexesSQL, false);
			}
		}
	}

	private static final String _DATA_SOURCE_DEFAULT = "liferayDataSource";

	private static final int _SERVICE_COMPONENTS_MAX = 10;

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceComponentLocalServiceImpl.class);

	private final DCLSingleton<Map<String, ServiceComponent>>
		_serviceComponentsDCLSingleton = new DCLSingleton<>();

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.schema.definition.internal.sql.provider;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactory;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.upgrade.release.SchemaCreator;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.util.Collection;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BaseSQLProvider implements SQLProvider {

	public BaseSQLProvider() throws Exception {
		db = _getDB();

		_appendPortalSQL();

		_appendModulesSQL();

		_appendPluginsSQL();
	}

	@Override
	public String getIndexesSQL() {
		return _indexesSQLSB.toString();
	}

	@Override
	public String getTablesSQL() {
		return _tablesSQLSB.toString();
	}

	protected final DB db;

	private void _appendModulesSQL() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<SchemaCreator>> serviceReferences =
			bundleContext.getServiceReferences(SchemaCreator.class, null);

		for (ServiceReference<SchemaCreator> serviceReference :
				serviceReferences) {

			_appendSQL(
				DBResourceUtil.getModuleIndexesSQL(
					serviceReference.getBundle()),
				DBResourceUtil.getModuleTablesSQL(
					serviceReference.getBundle()));
		}
	}

	private void _appendPluginsSQL() throws Exception {
		Set<String> contextNames = new HashSet<>();

		for (PluginPackage pluginPackage :
				PluginPackageUtil.getInstalledPluginPackages()) {

			String contextName = pluginPackage.getArtifactId();

			if (!contextNames.add(contextName)) {
				continue;
			}

			_appendSQL(
				_read(contextName, "/WEB-INF/sql/indexes.sql"),
				_read(contextName, "/WEB-INF/sql/tables.sql"));
		}
	}

	private void _appendPortalSQL() throws Exception {
		_appendSQL(
			DBResourceUtil.getPortalIndexesSQL(),
			DBResourceUtil.getPortalTablesSQL());
	}

	private void _appendSQL(String indexesSQL, String tablesSQL)
		throws Exception {

		if (Validator.isNotNull(indexesSQL)) {
			_indexesSQLSB.append(db.buildSQL(indexesSQL));
		}

		if (Validator.isNotNull(tablesSQL)) {
			_tablesSQLSB.append(db.buildSQL(tablesSQL));
		}
	}

	private DB _getDB() {
		ServiceLoader<DBFactory> serviceLoader = ServiceLoader.load(
			DBFactory.class, DBFactory.class.getClassLoader());

		for (DBFactory dbFactory : serviceLoader) {
			if (dbFactory.getDBType() == DBType.POSTGRESQL) {
				return dbFactory.create(0, 0);
			}
		}

		throw new IllegalStateException(
			"Unable to load database type " + DBType.POSTGRESQL);
	}

	private String _read(String contextName, String path) throws Exception {
		ServletContext servletContext = ServletContextPool.get(contextName);

		if (servletContext == null) {
			return null;
		}

		InputStream inputStream = servletContext.getResourceAsStream(path);

		if (inputStream == null) {
			return null;
		}

		return StringUtil.read(inputStream);
	}

	private final StringBundler _indexesSQLSB = new StringBundler();
	private final StringBundler _tablesSQLSB = new StringBundler();

}
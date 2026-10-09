/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.lang.reflect.Field;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public abstract class UpgradeProcess
	extends BaseDBProcess implements UpgradeStep {

	public UpgradeProcess() {
	}

	public UpgradeProcess(String upgradeInfo) {
		_upgradeInfo = upgradeInfo;
	}

	public void clearIndexesCache() {
		_portalIndexesSQL.clear();
	}

	public int getThreshold() {

		// This upgrade process will only run if the build number is larger than
		// the returned threshold value. Return 0 to always run this upgrade
		// process.

		return 0;
	}

	public final UpgradeStep[] getUpgradeSteps() {
		return ArrayUtil.append(
			getPreUpgradeSteps(), new UpgradeStep[] {this},
			getPostUpgradeSteps());
	}

	@Override
	public void upgrade() throws UpgradeException {
		if (this instanceof DummyUpgradeProcess) {
			return;
		}

		long start = System.currentTimeMillis();

		boolean notificationEnabled = NotificationThreadLocal.isEnabled();
		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		String message = "Completed upgrade process ";

		String info =
			(_upgradeInfo == null) ? ClassUtil.getClassName(this) :
				_upgradeInfo;

		try (Connection connection = getConnection()) {
			this.connection = connection;

			if (isSkipUpgradeProcess()) {
				return;
			}

			process(
				companyId -> {
					NotificationThreadLocal.setEnabled(false);
					WorkflowThreadLocal.setEnabled(false);

					String companyInfo = info;

					if (Validator.isNotNull(companyId)) {
						companyInfo += "#" + companyId;
					}

					if (_log.isInfoEnabled()) {
						_log.info("Upgrading " + companyInfo);
					}

					try {
						doUpgrade();
					}
					finally {
						closeConnections();
					}
				});
		}
		catch (Throwable throwable) {
			message = "Failed upgrade process ";

			throw new UpgradeException(throwable);
		}
		finally {
			this.connection = null;

			NotificationThreadLocal.setEnabled(notificationEnabled);
			WorkflowThreadLocal.setEnabled(workflowEnabled);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						message, info, " in ",
						System.currentTimeMillis() - start, " ms"));
			}
		}
	}

	public void upgrade(UpgradeProcess upgradeProcess) throws UpgradeException {
		upgradeProcess.upgrade();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public interface Alterable {

		public static boolean containsIgnoreCase(
			Collection<String> columnNames, String columnName) {

			for (String curColumnName : columnNames) {
				if (StringUtil.equalsIgnoreCase(curColumnName, columnName)) {
					return true;
				}
			}

			return false;
		}

		public String getSQL(String tableName);

		public boolean shouldAddIndex(Collection<String> columnNames);

		public boolean shouldDropIndex(Collection<String> columnNames);

	}

	protected abstract void doUpgrade() throws Exception;

	protected void ensureTableExists(
			DatabaseMetaData databaseMetaData, DBInspector dbInspector,
			String tableName)
		throws SQLException {

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
				null)) {

			if (!resultSet.next()) {
				throw new SQLException(
					StringBundler.concat(
						"Table with name '", tableName, "' does not exist in ",
						dbInspector.getSchema()));
			}
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getIndexSQLs(Class, String)}
	 */
	@Deprecated
	protected List<ObjectValuePair<String, IndexMetadata>> getIndexesSQL(
			ClassLoader classLoader, String tableName)
		throws IOException {

		if (!PortalClassLoaderUtil.isPortalClassLoader(classLoader)) {
			try (InputStream inputStream = classLoader.getResourceAsStream(
					"META-INF/sql/indexes.sql")) {

				if (inputStream == null) {
					return null;
				}

				List<ObjectValuePair<String, IndexMetadata>> objectValuePairs =
					new ArrayList<>();

				try (Reader reader = new InputStreamReader(inputStream);

					UnsyncBufferedReader unsyncBufferedReader =
						new UnsyncBufferedReader(reader)) {

					String line = null;

					while ((line = unsyncBufferedReader.readLine()) != null) {
						line = line.trim();

						if (line.isEmpty()) {
							continue;
						}

						IndexMetadata indexMetadata =
							IndexMetadataFactoryUtil.createIndexMetadata(line);

						if (!tableName.equals(indexMetadata.getTableName())) {
							continue;
						}

						objectValuePairs.add(
							new ObjectValuePair<>(line, indexMetadata));
					}
				}

				return objectValuePairs;
			}
		}

		if (!_portalIndexesSQL.isEmpty()) {
			return _portalIndexesSQL.get(tableName);
		}

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/tools/sql/dependencies/indexes.sql");

			Reader reader = new InputStreamReader(inputStream);

			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(reader)) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) {
					continue;
				}

				IndexMetadata indexMetadata =
					IndexMetadataFactoryUtil.createIndexMetadata(line);

				List<ObjectValuePair<String, IndexMetadata>> objectValuePairs =
					_portalIndexesSQL.get(indexMetadata.getTableName());

				if (objectValuePairs == null) {
					objectValuePairs = new ArrayList<>();

					_portalIndexesSQL.put(
						indexMetadata.getTableName(), objectValuePairs);
				}

				objectValuePairs.add(
					new ObjectValuePair<>(line, indexMetadata));
			}
		}

		return _portalIndexesSQL.get(tableName);
	}

	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[0];
	}

	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[0];
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	protected Map<String, Integer> getTableColumnsMap(Class<?> tableClass)
		throws Exception {

		Field tableNameField = tableClass.getField("TABLE_COLUMNS_MAP");

		return (Map<String, Integer>)tableNameField.get(null);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	protected String getTableName(Class<?> tableClass) throws Exception {
		Field tableNameField = tableClass.getField("TABLE_NAME");

		return (String)tableNameField.get(null);
	}

	protected long increment() {
		return CounterLocalServiceUtil.increment();
	}

	protected long increment(String name) {
		return CounterLocalServiceUtil.increment(name);
	}

	protected long increment(String name, int size) {
		return CounterLocalServiceUtil.increment(name, size);
	}

	protected boolean isPortal62TableName(String tableName) {
		return _portal62TableNames.contains(StringUtil.toLowerCase(tableName));
	}

	protected boolean isSkipUpgradeProcess() throws Exception {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(UpgradeProcess.class);

	private static final Set<String> _portal62TableNames = new HashSet<>(
		Arrays.asList(
			"account_", "address", "announcementsdelivery",
			"announcementsentry", "announcementsflag", "assetcategory",
			"assetcategoryproperty", "assetentries_assetcategories",
			"assetentries_assettags", "assetentry", "assetlink", "assettag",
			"assettagstats", "assetvocabulary", "backgroundtask", "blogsentry",
			"blogsstatsuser", "bookmarksentry", "bookmarksfolder",
			"browsertracker", "calevent", "classname_", "clustergroup",
			"company", "contact_", "counter", "country", "ddlrecord",
			"ddlrecordset", "ddlrecordversion", "ddmcontent", "ddmstoragelink",
			"ddmstructure", "ddmstructurelink", "ddmtemplate", "dlcontent",
			"dlfileentry", "dlfileentrymetadata", "dlfileentrytype",
			"dlfileentrytypes_dlfolders", "dlfilerank", "dlfileshortcut",
			"dlfileversion", "dlfolder", "dlsyncevent", "emailaddress",
			"expandocolumn", "expandorow", "expandotable", "expandovalue",
			"exportimportconfiguration", "group_", "groups_orgs",
			"groups_roles", "groups_usergroups", "image", "journalarticle",
			"journalarticleimage", "journalarticleresource",
			"journalcontentsearch", "journalfeed", "journalfolder",
			"journalstructure", "journaltemplate", "layout", "layoutbranch",
			"layoutfriendlyurl", "layoutprototype", "layoutrevision",
			"layoutset", "layoutsetbranch", "layoutsetprototype", "listtype",
			"lock_", "marketplace_app", "mbban", "mbcategory", "mbdiscussion",
			"mbmailinglist", "mbmessage", "mbstatsuser", "mbthread",
			"mbthreadflag", "mdraction", "mdrrule", "mdrrulegroup",
			"mdrrulegroupinstance", "membershiprequest", "organization_",
			"orggrouprole", "orglabor", "passwordpolicy", "passwordpolicyrel",
			"passwordtracker", "phone", "pluginsetting", "pollschoice",
			"pollsquestion", "pollsvote", "portalpreferences", "portlet",
			"portletitem", "portletpreferences", "ratingsentry", "ratingsstats",
			"recentlayoutbranch", "recentlayoutrevision",
			"recentlayoutsetbranch", "region", "release_", "repository",
			"repositoryentry", "resourceaction", "resourceblock",
			"resourceblockpermission", "resourcepermission",
			"resourcetypepermission", "role_", "servicecomponent",
			"socialactivity", "socialactivityachievement",
			"socialactivitycounter", "socialactivitylimit", "socialactivityset",
			"socialactivitysetting", "socialrelation", "socialrequest",
			"subscription", "systemevent", "team", "ticket", "trashentry",
			"trashversion", "usernotificationdelivery", "user_", "usergroup",
			"usergroupgrouprole", "usergrouprole", "usergroups_teams",
			"useridmapper", "usernotificationevent", "users_groups",
			"users_orgs", "users_roles", "users_teams", "users_usergroups",
			"usertracker", "usertrackerpath", "virtualhost", "webdavprops",
			"website", "wikinode", "wikipage", "wikipageresource",
			"workflowdefinitionlink", "workflowinstancelink"));
	private static final Map
		<String, List<ObjectValuePair<String, IndexMetadata>>>
			_portalIndexesSQL = new HashMap<>();

	private String _upgradeInfo;

}
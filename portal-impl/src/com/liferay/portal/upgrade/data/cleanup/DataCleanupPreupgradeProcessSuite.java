/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.db.index.PrimaryKeyUpdaterUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;

import java.util.List;
import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class DataCleanupPreupgradeProcessSuite {

	public void cleanUp() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			if (StartupHelperUtil.isDBNew() ||
				PortalUpgradeProcess.isInCompatibleSchemaVersion(connection) ||
				(PortalUpgradeProcess.getCurrentState(connection) !=
					ReleaseConstants.STATE_GOOD)) {

				return;
			}
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			DataCleanupPreupgradeProcessUtil.enableCache();

			try {
				DataCleanupConcurrentExecutor dataCleanupConcurrentExecutor =
					new DataCleanupConcurrentExecutor();

				dataCleanupConcurrentExecutor._execute(
					_getLayeredDataCleanupPreupgradeProcesses(),
					this::_runDataCleanupPreupgradeProcess);
			}
			finally {
				DataCleanupPreupgradeProcessUtil.disableCache();
			}
		}
	}

	public List<DataCleanupPreupgradeProcess>
		getSortedDataCleanupPreupgradeProcesses() {

		return DataCleanupPreupgradeProcess.
			getSortedDataCleanupPreupgradeProcesses(
				_dataCleanupPreupgradeProcessesMap);
	}

	private Map
		<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			_createDataCleanupPreupgradeProcessesMap() {

		DataCleanupPreupgradeProcess
			analyticsMessageDataCleanupPreupgradeProcess =
				new AnalyticsMessageDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess companyDataCleanupPreupgradeProcess =
			new CompanyDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess configurationDataCleanupPreupgradeProcess =
			new ConfigurationDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess contactDataCleanupPreupgradeProcess =
			new ContactDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			databaseTableAndColumnCaseDataCleanupPreupgradeProcess =
				new DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess ddmDataCleanupPreupgradeProcess =
			new DDMDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			ddmStorageLinkDataCleanupPreupgradeProcess =
				new DDMStorageLinkDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess dlFileEntryDataCleanupPreupgradeProcess =
			new DLFileEntryDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess groupDataCleanupPreupgradeProcess =
			new GroupDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			illegalCharactersContentDataCleanupPreupgradeProcess =
				new IllegalCharactersContentDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess journalDataCleanupPreupgradeProcess =
			new JournalDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess layoutDataCleanupPreupgradeProcess =
			new LayoutDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			portalPreferencesDataCleanupPreupgradeProcess =
				new PortalPreferencesDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			portletPreferencesDataCleanupPreupgradeProcess =
				new PortletPreferencesDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			quartzJobDetailsDataCleanupPreupgradeProcess =
				new QuartzJobDetailsDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess roleDataCleanupPreupgradeProcess =
			new RoleDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			resourcePermissionDataCleanupPreupgradeProcess =
				new ResourcePermissionDataCleanupPreupgradeProcess();
		DataCleanupPreupgradeProcess
			updateAllPrimaryKeysDataCleanupPreupgradeProcess =
				new DataCleanupPreupgradeProcess() {

					@Override
					protected void doUpgrade() throws Exception {
						PrimaryKeyUpdaterUtil.updateAllPrimaryKeys();
					}

				};
		DataCleanupPreupgradeProcess userDataCleanupPreupgradeProcess =
			new UserDataCleanupPreupgradeProcess();

		return LinkedHashMapBuilder.
			<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
				put(
					analyticsMessageDataCleanupPreupgradeProcess,
					DataCleanupPreupgradeProcess.dependsOn(
						databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				companyDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess)
			).put(
				configurationDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				contactDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					userDataCleanupPreupgradeProcess)
			).put(
				new CounterDataCleanupPreupgradeProcess(),
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					companyDataCleanupPreupgradeProcess,
					configurationDataCleanupPreupgradeProcess,
					contactDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					ddmStorageLinkDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess,
					layoutDataCleanupPreupgradeProcess,
					illegalCharactersContentDataCleanupPreupgradeProcess,
					portalPreferencesDataCleanupPreupgradeProcess,
					portletPreferencesDataCleanupPreupgradeProcess,
					quartzJobDetailsDataCleanupPreupgradeProcess,
					resourcePermissionDataCleanupPreupgradeProcess,
					roleDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn()
			).put(
				ddmDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess)
			).put(
				ddmStorageLinkDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess)
			).put(
				dlFileEntryDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess)
			).put(
				groupDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				illegalCharactersContentDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess)
			).put(
				journalDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess)
			).put(
				layoutDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				portalPreferencesDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				portletPreferencesDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					layoutDataCleanupPreupgradeProcess)
			).put(
				quartzJobDetailsDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				resourcePermissionDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					analyticsMessageDataCleanupPreupgradeProcess,
					companyDataCleanupPreupgradeProcess,
					configurationDataCleanupPreupgradeProcess,
					contactDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess,
					ddmDataCleanupPreupgradeProcess,
					ddmStorageLinkDataCleanupPreupgradeProcess,
					dlFileEntryDataCleanupPreupgradeProcess,
					groupDataCleanupPreupgradeProcess,
					journalDataCleanupPreupgradeProcess,
					layoutDataCleanupPreupgradeProcess,
					illegalCharactersContentDataCleanupPreupgradeProcess,
					portalPreferencesDataCleanupPreupgradeProcess,
					portletPreferencesDataCleanupPreupgradeProcess,
					quartzJobDetailsDataCleanupPreupgradeProcess,
					roleDataCleanupPreupgradeProcess,
					updateAllPrimaryKeysDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				roleDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					userDataCleanupPreupgradeProcess)
			).put(
				updateAllPrimaryKeysDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).put(
				userDataCleanupPreupgradeProcess,
				DataCleanupPreupgradeProcess.dependsOn(
					companyDataCleanupPreupgradeProcess,
					databaseTableAndColumnCaseDataCleanupPreupgradeProcess)
			).build();
	}

	private List<List<DataCleanupPreupgradeProcess>>
		_getLayeredDataCleanupPreupgradeProcesses() {

		return DataCleanupPreupgradeProcess.
			getLayeredDataCleanupPreupgradeProcesses(
				_dataCleanupPreupgradeProcessesMap);
	}

	private void _runDataCleanupPreupgradeProcess(
			DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess)
		throws Exception {

		Class<?> clazz = dataCleanupPreupgradeProcess.getClass();

		if (ArrayUtil.contains(
				PropsValues.UPGRADE_DATABASE_PREUPGRADE_DATA_CLEANUP_BLACKLIST,
				clazz.getName())) {

			if (_log.isInfoEnabled()) {
				_log.info(
					"Skipping blacklisted data cleanup process: " +
						clazz.getName());
			}

			return;
		}

		try (LoggingTimer loggingTimer = new LoggingTimer(
				clazz.getSimpleName())) {

			dataCleanupPreupgradeProcess.upgrade();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataCleanupPreupgradeProcessSuite.class);

	private final Map
		<DataCleanupPreupgradeProcess, List<DataCleanupPreupgradeProcess>>
			_dataCleanupPreupgradeProcessesMap =
				_createDataCleanupPreupgradeProcessesMap();

	private static class DataCleanupConcurrentExecutor extends BaseDBProcess {

		private void _execute(
				List<List<DataCleanupPreupgradeProcess>>
					dataCleanupPreupgradeProcessesList,
				UnsafeConsumer<DataCleanupPreupgradeProcess, Exception>
					unsafeConsumer)
			throws Exception {

			for (List<DataCleanupPreupgradeProcess>
					dataCleanupPreupgradeProcesses :
						dataCleanupPreupgradeProcessesList) {

				if (dataCleanupPreupgradeProcesses.size() == 1) {
					unsafeConsumer.accept(
						dataCleanupPreupgradeProcesses.get(0));

					continue;
				}

				processConcurrently(
					dataCleanupPreupgradeProcesses.toArray(
						new DataCleanupPreupgradeProcess[0]),
					unsafeConsumer,
					"Unable to run data cleanup processes concurrently");
			}
		}

	}

}
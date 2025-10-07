/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.release;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.osgi.debug.SystemChecker;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.internal.executor.UpgradeExecutor;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;
import com.liferay.portal.upgrade.internal.release.util.ReleaseManagerUtil;
import com.liferay.portal.upgrade.release.SchemaCreator;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Alberto Chaparro
 * @author Samuel Ziemer
 */
@Component(service = ReleaseManager.class)
public class ReleaseManagerImpl implements ReleaseManager {

	@Override
	public String getShortStatusMessage(boolean onlyRequiredUpgrades) {
		String message =
			"%s upgrades in %s are pending. Run the upgrade process or type " +
				"upgrade:checkAll in the Gogo shell to get more information.";

		if (onlyRequiredUpgrades) {
			if (_isPendingRequiredModuleUpgrades()) {
				return String.format(message, "Required", "modules");
			}

			return StringPool.BLANK;
		}

		String where = StringPool.BLANK;

		try (Connection connection = DataAccess.getConnection()) {
			if (!PortalUpgradeProcess.isInLatestSchemaVersion(connection)) {
				where = "portal";
			}
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get pending upgrade information for the portal",
					sqlException);
			}
		}

		if (_isPendingModuleUpgrades()) {
			if (Validator.isNotNull(where)) {
				where = "and " + where;
			}
			else {
				where = "modules";
			}
		}

		if (Validator.isNotNull(where)) {
			return String.format(message, "Optional", where);
		}

		return StringPool.BLANK;
	}

	@Override
	public String getStatus() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			if (!PortalUpgradeProcess.isInLatestSchemaVersion(connection) ||
				_isPendingModuleUpgrades()) {

				return "failure";
			}
		}

		if (_hasUnsatisfiedUpgradeComponents()) {
			return "unresolved";
		}

		return "success";
	}

	@Override
	public String getStatusMessage(boolean showUpgradeSteps) {
		StringBundler sb = new StringBundler(5);

		sb.append(_checkPortal(showUpgradeSteps));

		if (sb.length() > 0) {
			sb.append(StringPool.NEW_LINE);
		}

		sb.append(_checkModules(showUpgradeSteps));

		if (_hasUnsatisfiedUpgradeComponents()) {
			sb.append("Unsatisfied components prevent upgrade processes to ");
			sb.append("be registered\n");
		}

		return sb.toString();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_schemaCreatorServiceTracker = new ServiceTracker<>(
			bundleContext, SchemaCreator.class,
			new SchemaCreatorServiceTrackerCustomizer(bundleContext));

		_schemaCreatorServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_schemaCreatorServiceTracker.close();
	}

	private String _checkModules(boolean showUpgradeSteps) {
		StringBundler sb = new StringBundler();

		Set<String> bundleSymbolicNames =
			_upgradeExecutor.getBundleSymbolicNames();

		for (String bundleSymbolicName : bundleSymbolicNames) {
			String schemaVersionString =
				ReleaseManagerUtil.getSchemaVersionString(
					_releaseLocalService.fetchRelease(bundleSymbolicName));

			ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
				_upgradeExecutor.getUpgradeInfos(bundleSymbolicName));

			List<List<UpgradeInfo>> upgradeInfosList =
				releaseGraphManager.getUpgradeInfosList(schemaVersionString);

			int size = upgradeInfosList.size();

			if (size > 1) {
				sb.append("There are ");
				sb.append(size);
				sb.append(" possible end nodes for ");
				sb.append(schemaVersionString);
				sb.append(StringPool.NEW_LINE);
			}

			if (size == 0) {
				continue;
			}

			List<UpgradeInfo> upgradeInfos = upgradeInfosList.get(0);

			UpgradeInfo lastUpgradeInfo = upgradeInfos.get(
				upgradeInfos.size() - 1);

			sb.append(
				_getModulePendingUpgradeMessage(
					bundleSymbolicName, schemaVersionString,
					lastUpgradeInfo.getToSchemaVersionString()));

			if (showUpgradeSteps) {
				sb.append(StringPool.COLON);

				for (UpgradeInfo upgradeInfo : upgradeInfos) {
					UpgradeStep upgradeStep = upgradeInfo.getUpgradeStep();

					sb.append(StringPool.NEW_LINE);
					sb.append(StringPool.TAB);
					sb.append(
						_getPendingUpgradeProcessMessage(
							upgradeStep.getClass(),
							upgradeInfo.getFromSchemaVersionString(),
							upgradeInfo.getToSchemaVersionString()));
				}
			}

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _checkPortal(boolean showUpgradeSteps) {
		try (Connection connection = DataAccess.getConnection()) {
			Version currentSchemaVersion =
				PortalUpgradeProcess.getCurrentSchemaVersion(connection);

			SortedMap<Version, List<UpgradeProcess>> pendingUpgradeProcesses =
				PortalUpgradeProcess.getPendingUpgradeProcesses(
					currentSchemaVersion);

			if (!pendingUpgradeProcesses.isEmpty()) {
				Version latestSchemaVersion =
					PortalUpgradeProcess.getLatestSchemaVersion();

				StringBundler sb = new StringBundler();

				sb.append(
					_getModulePendingUpgradeMessage(
						"Portal", currentSchemaVersion.toString(),
						latestSchemaVersion.toString()));

				sb.append(" (requires upgrade tool or auto upgrade)");

				if (showUpgradeSteps) {
					sb.append(StringPool.COLON);

					for (SortedMap.Entry<Version, List<UpgradeProcess>> entry :
							pendingUpgradeProcesses.entrySet()) {

						sb.append(StringPool.NEW_LINE);
						sb.append(StringPool.TAB);

						List<UpgradeProcess> upgradeProcesses =
							entry.getValue();
						Version version = entry.getKey();

						sb.append(
							_getPendingUpgradeProcessMessage(
								upgradeProcesses.getClass(),
								currentSchemaVersion.toString(),
								version.toString()));

						sb.append(StringPool.NEW_LINE);

						currentSchemaVersion = version;
					}
				}

				return sb.toString();
			}
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get pending upgrade information for the portal",
					sqlException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getModulePendingUpgradeMessage(
		String moduleName, String currentSchemaVersion,
		String finalSchemaVersion) {

		return StringBundler.concat(
			"There are upgrade processes available for ", moduleName, " from ",
			currentSchemaVersion, " to ", finalSchemaVersion);
	}

	private String _getPendingUpgradeProcessMessage(
		Class<?> upgradeClass, String fromSchemaVersion,
		String toSchemaVersion) {

		StringBundler sb = new StringBundler(6);

		String toMessage = toSchemaVersion;

		if (UpgradeProcessUtil.isRequiredSchemaVersion(
				Version.parseVersion(fromSchemaVersion),
				Version.parseVersion(toSchemaVersion))) {

			toMessage += " (REQUIRED)";
		}

		sb.append(fromSchemaVersion);
		sb.append(" to ");
		sb.append(toMessage);
		sb.append(StringPool.COLON);
		sb.append(StringPool.SPACE);
		sb.append(upgradeClass.getName());

		return sb.toString();
	}

	private boolean _hasUnsatisfiedUpgradeComponents() {
		String result = _systemChecker.check();

		return result.contains("UpgradeStepRegistrator");
	}

	private boolean _isPendingModuleUpgrades() {
		for (String bundleSymbolicName :
				_upgradeExecutor.getBundleSymbolicNames()) {

			if (ReleaseManagerUtil.isUpgradable(
					bundleSymbolicName, _releaseLocalService,
					_upgradeExecutor)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isPendingRequiredModuleUpgrades() {
		Set<String> upgradableBundleSymbolicNames =
			ReleaseManagerUtil.getUpgradableBundleSymbolicNames(
				_upgradeExecutor.getBundleSymbolicNames(), _releaseLocalService,
				_upgradeExecutor);

		for (String bundleSymbolicName : upgradableBundleSymbolicNames) {
			ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
				_upgradeExecutor.getUpgradeInfos(bundleSymbolicName));

			List<List<UpgradeInfo>> upgradeInfosList =
				releaseGraphManager.getUpgradeInfosList(
					ReleaseManagerUtil.getSchemaVersionString(
						_releaseLocalService.fetchRelease(bundleSymbolicName)));

			List<UpgradeInfo> upgradeInfos = upgradeInfosList.get(0);

			for (UpgradeInfo upgradeInfo : upgradeInfos) {
				if (UpgradeProcessUtil.isRequiredSchemaVersion(
						Version.parseVersion(
							upgradeInfo.getFromSchemaVersionString()),
						Version.parseVersion(
							upgradeInfo.getToSchemaVersionString()))) {

					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReleaseManagerImpl.class);

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference
	private ReleasePublisher _releasePublisher;

	private ServiceTracker<SchemaCreator, Release> _schemaCreatorServiceTracker;

	@Reference(
		target = "(component.name=com.liferay.portal.osgi.debug.declarative.service.internal.DeclarativeServiceUnsatisfiedComponentSystemChecker)"
	)
	private volatile SystemChecker _systemChecker;

	@Reference
	private UpgradeExecutor _upgradeExecutor;

	private class SchemaCreatorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<SchemaCreator, Release> {

		@Override
		public Release addingService(
			ServiceReference<SchemaCreator> serviceReference) {

			SchemaCreator schemaCreator = _bundleContext.getService(
				serviceReference);

			String bundleSymbolicName = schemaCreator.getBundleSymbolicName();

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if ((release == null) ||
				StringUtil.equals("0.0.0", release.getSchemaVersion())) {

				try {
					schemaCreator.create();

					release = _releaseLocalService.updateRelease(
						bundleSymbolicName, schemaCreator.getSchemaVersion(),
						"0.0.0");

					release.setVerified(false);
				}
				catch (Exception exception) {
					release = _releaseLocalService.addRelease(
						bundleSymbolicName, "0.0.0");

					release.setState(ReleaseConstants.STATE_UPGRADE_FAILURE);

					ReflectionUtil.throwException(exception);
				}
				finally {
					release = _releaseLocalService.updateRelease(release);

					_releasePublisher.publish(release, true);
				}
			}

			return release;
		}

		@Override
		public void modifiedService(
			ServiceReference<SchemaCreator> serviceReference, Release release) {
		}

		@Override
		public void removedService(
			ServiceReference<SchemaCreator> serviceReference, Release release) {
		}

		private SchemaCreatorServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

}
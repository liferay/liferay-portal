/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.executor;

import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.PortalUpgradeProcess;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;
import com.liferay.portal.upgrade.internal.registry.UpgradeStepRegistry;
import com.liferay.portal.upgrade.internal.release.ReleasePublisher;
import com.liferay.portal.upgrade.log.UpgradeLogContext;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Dictionary;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = UpgradeExecutor.class)
public class UpgradeExecutor {

	public void execute(Bundle bundle, List<UpgradeInfo> upgradeInfos) {
		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			upgradeInfos);

		String schemaVersionString = "0.0.0";

		Release release = _releaseLocalService.fetchRelease(
			bundle.getSymbolicName());

		if ((release != null) &&
			Validator.isNotNull(release.getSchemaVersion())) {

			schemaVersionString = release.getSchemaVersion();
		}

		List<List<UpgradeInfo>> upgradeInfosList =
			releaseGraphManager.getUpgradeInfosList(schemaVersionString);

		int size = upgradeInfosList.size();

		if (size > 1) {
			throw new IllegalStateException(
				StringBundler.concat(
					"There are ", size, " possible end nodes for ",
					schemaVersionString));
		}

		if (size != 0) {
			release = executeUpgradeInfos(bundle, upgradeInfosList.get(0));
		}

		if (release != null) {
			String schemaVersion = release.getSchemaVersion();

			if (Validator.isNull(schemaVersion)) {
				return;
			}

			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			Version requiredVersion = Version.parseVersion(
				headers.get("Liferay-Require-SchemaVersion"));

			if (requiredVersion == null) {
				return;
			}

			if (requiredVersion.compareTo(Version.parseVersion(schemaVersion)) >
					0) {

				throw new IllegalStateException(
					StringBundler.concat(
						"Unable to upgrade ", bundle.getSymbolicName(), " to ",
						requiredVersion, " from ", schemaVersion));
			}
		}
	}

	public Release executeUpgradeInfos(
		Bundle bundle, List<UpgradeInfo> upgradeInfos) {

		String bundleSymbolicName = bundle.getSymbolicName();

		try {
			UpgradeLogContext.setContext(bundleSymbolicName);

			_executeUpgradeInfos(bundle, upgradeInfos);

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release != null) {
				_releasePublisher.publish(
					release, _isInitialRelease(upgradeInfos));
			}

			return release;
		}
		catch (Exception exception) {
			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release != null) {
				_releasePublisher.unpublish(release);
			}

			return ReflectionUtil.throwException(exception);
		}
		finally {
			UpgradeLogContext.clearContext();
		}
	}

	public Set<String> getBundleSymbolicNames() {
		return _serviceTrackerMap.keySet();
	}

	public List<UpgradeInfo> getUpgradeInfos(String bundleSymbolicName) {
		UpgradeStepRegistry upgradeStepRegistry = _serviceTrackerMap.getService(
			bundleSymbolicName);

		return upgradeStepRegistry.getUpgradeInfos();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		try (Connection connection = DataAccess.getConnection()) {
			_portalUpgraded = PortalUpgradeProcess.isInCompatibleSchemaVersion(
				connection);
		}
		catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		}

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, UpgradeStepRegistrator.class, null,
			(serviceReference, emitter) -> {
				Bundle bundle = serviceReference.getBundle();

				emitter.emit(bundle.getSymbolicName());
			},
			new UpgradeStepRegistratorServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _executeUpgradeInfos(
		Bundle bundle, List<UpgradeInfo> upgradeInfos) {

		int state = ReleaseConstants.STATE_GOOD;

		String bundleSymbolicName = bundle.getSymbolicName();

		try {
			Release release = _updateReleaseState(
				bundleSymbolicName, _STATE_IN_PROGRESS);

			if (release != null) {
				StartupHelperUtil.setRunOnPortalUpgradeVerifiers(true);
			}

			for (UpgradeInfo upgradeInfo : upgradeInfos) {
				UpgradeStep upgradeStep = upgradeInfo.getUpgradeStep();

				upgradeStep.upgrade();

				_releaseLocalService.updateRelease(
					bundleSymbolicName, upgradeInfo.getToSchemaVersionString(),
					upgradeInfo.getFromSchemaVersionString());
			}
		}
		catch (Exception exception) {
			state = ReleaseConstants.STATE_UPGRADE_FAILURE;

			ReflectionUtil.throwException(exception);
		}
		finally {
			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release != null) {
				release.setVerified(false);
				release.setState(state);

				_releaseLocalService.updateRelease(release);
			}
		}

		if (_requiresUpdateIndexes(bundle, upgradeInfos)) {
			try {
				IndexUpdaterUtil.updateIndexes(bundle);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		CacheRegistryUtil.clear();
	}

	private boolean _isInitialRelease(List<UpgradeInfo> upgradeInfos) {
		UpgradeInfo upgradeInfo = upgradeInfos.get(0);

		String fromSchemaVersion = upgradeInfo.getFromSchemaVersionString();

		return fromSchemaVersion.equals("0.0.0");
	}

	private boolean _requiresUpdateIndexes(
		Bundle bundle, List<UpgradeInfo> upgradeInfos) {

		if (!BundleUtil.isLiferayRequireSchemaVersionBundle(bundle) &&
			!BundleUtil.isLiferayServiceBundle(bundle)) {

			return false;
		}

		if (upgradeInfos.size() != 1) {
			return true;
		}

		return !_isInitialRelease(upgradeInfos);
	}

	private Release _updateReleaseState(String bundleSymbolicName, int state) {
		Release release = _releaseLocalService.fetchRelease(bundleSymbolicName);

		if (release != null) {
			release.setState(state);

			release = _releaseLocalService.updateRelease(release);
		}

		return release;
	}

	private static final int _STATE_IN_PROGRESS = -1;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeExecutor.class);

	private BundleContext _bundleContext;
	private boolean _portalUpgraded;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference
	private ReleasePublisher _releasePublisher;

	private ServiceTrackerMap<String, UpgradeStepRegistry> _serviceTrackerMap;

	private class UpgradeStepRegistratorServiceTrackerCustomizer
		implements EagerServiceTrackerCustomizer
			<UpgradeStepRegistrator, UpgradeStepRegistry> {

		@Override
		public UpgradeStepRegistry addingService(
			ServiceReference<UpgradeStepRegistrator> serviceReference) {

			UpgradeStepRegistry upgradeStepRegistry = new UpgradeStepRegistry(
				_bundleContext, _portalUpgraded, serviceReference);

			Bundle bundle = serviceReference.getBundle();

			String bundleSymbolicName = bundle.getSymbolicName();

			Release release = _releaseLocalService.fetchRelease(
				bundleSymbolicName);

			if (release == null) {
				for (UpgradeStep releaseUpgradeStep :
						upgradeStepRegistry.getReleaseCreationUpgradeSteps()) {

					try {
						UpgradeLogContext.setContext(bundleSymbolicName);

						releaseUpgradeStep.upgrade();
					}
					catch (UpgradeException upgradeException) {
						_log.error(upgradeException);
					}
					finally {
						UpgradeLogContext.clearContext();
					}
				}
			}

			if (DBUpgrader.isUpgradeDatabaseAutoRunEnabled() ||
				(release == null)) {

				try {
					execute(bundle, upgradeStepRegistry.getUpgradeInfos());
				}
				catch (Throwable throwable) {
					_log.error(
						"Failed upgrade process for module ".concat(
							bundleSymbolicName),
						throwable);
				}
			}

			return upgradeStepRegistry;
		}

		@Override
		public void modifiedService(
			ServiceReference<UpgradeStepRegistrator> serviceReference,
			UpgradeStepRegistry upgradeStepRegistry) {
		}

		@Override
		public void removedService(
			ServiceReference<UpgradeStepRegistrator> serviceReference,
			UpgradeStepRegistry upgradeStepRegistry) {

			upgradeStepRegistry.destroy();
		}

	}

}
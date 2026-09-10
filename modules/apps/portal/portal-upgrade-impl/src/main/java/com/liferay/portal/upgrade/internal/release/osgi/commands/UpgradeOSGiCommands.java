/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.release.osgi.commands;

import com.liferay.gogo.shell.logging.TeeLoggingUtil;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.ReleaseManager;
import com.liferay.portal.upgrade.internal.executor.UpgradeExecutor;
import com.liferay.portal.upgrade.internal.graph.ReleaseGraphManager;
import com.liferay.portal.upgrade.internal.registry.UpgradeInfo;
import com.liferay.portal.upgrade.internal.release.util.ReleaseManagerUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.felix.service.command.Descriptor;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.command.function=check", "osgi.command.function=checkAll",
		"osgi.command.function=execute", "osgi.command.function=executeAll",
		"osgi.command.function=list", "osgi.command.scope=upgrade"
	},
	service = OSGiCommands.class
)
public class UpgradeOSGiCommands implements OSGiCommands {

	@Descriptor("List pending upgrades")
	public String check() {
		return _releaseManager.getStatusMessage(false);
	}

	@Descriptor("List pending upgrade processes and their upgrade steps")
	public String checkAll() {
		return _releaseManager.getStatusMessage(true);
	}

	@Descriptor("Execute upgrade for a specific module")
	public String execute(String bundleSymbolicName) {
		List<UpgradeInfo> upgradeInfos = _upgradeExecutor.getUpgradeInfos(
			bundleSymbolicName);

		if (upgradeInfos == null) {
			return "No upgrade processes registered for " + bundleSymbolicName;
		}

		TeeLoggingUtil.runWithTeeLogging(
			() -> {
				try {
					_upgradeExecutor.execute(
						BundleUtil.getBundle(
							_bundleContext, bundleSymbolicName),
						upgradeInfos);
				}
				catch (Throwable throwable) {
					_log.error(
						"Failed upgrade process for module ".concat(
							bundleSymbolicName),
						throwable);
				}
			});

		return null;
	}

	@Descriptor("Execute upgrade for a specific module and final version")
	public String execute(String bundleSymbolicName, String toVersionString) {
		List<UpgradeInfo> upgradeInfos = _upgradeExecutor.getUpgradeInfos(
			bundleSymbolicName);

		if (upgradeInfos == null) {
			return "No upgrade processes registered for " + bundleSymbolicName;
		}

		ReleaseGraphManager releaseGraphManager = new ReleaseGraphManager(
			upgradeInfos);

		TeeLoggingUtil.runWithTeeLogging(
			() -> _upgradeExecutor.executeUpgradeInfos(
				BundleUtil.getBundle(_bundleContext, bundleSymbolicName),
				releaseGraphManager.getUpgradeInfos(
					ReleaseManagerUtil.getSchemaVersionString(
						_releaseLocalService.fetchRelease(bundleSymbolicName)),
					toVersionString)));

		return null;
	}

	@Descriptor("Execute all pending upgrades")
	public String executeAll() {
		Set<String> upgradeThrewExceptionBundleSymbolicNames = new HashSet<>();

		TeeLoggingUtil.runWithTeeLogging(
			() -> executeAll(upgradeThrewExceptionBundleSymbolicNames));

		if (upgradeThrewExceptionBundleSymbolicNames.isEmpty()) {
			return "All modules were successfully upgraded";
		}

		StringBundler sb = new StringBundler(
			(upgradeThrewExceptionBundleSymbolicNames.size() * 3) + 3);

		sb.append("The following modules had errors while upgrading:\n");

		for (String upgradeThrewExceptionBundleSymbolicName :
				upgradeThrewExceptionBundleSymbolicNames) {

			sb.append(StringPool.TAB);
			sb.append(upgradeThrewExceptionBundleSymbolicName);
			sb.append(StringPool.NEW_LINE);
		}

		sb.append("Use the command upgrade:list <module name> to get more ");
		sb.append("details about the status of a specific upgrade.");

		return sb.toString();
	}

	@Descriptor("List registered upgrade processes for all modules")
	public String list() {
		Set<String> bundleSymbolicNames =
			_upgradeExecutor.getBundleSymbolicNames();

		Set<String> failedBundleSymbolicNames =
			_upgradeExecutor.getFailedBundleSymbolicNames();

		StringBundler sb = new StringBundler(4 * bundleSymbolicNames.size());

		for (String bundleSymbolicName : bundleSymbolicNames) {
			if (failedBundleSymbolicNames.contains(bundleSymbolicName)) {
				sb.append("The upgrade of module ");
				sb.append(bundleSymbolicName);
				sb.append(" failed");
			}
			else {
				sb.append(list(bundleSymbolicName));
			}

			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	@Descriptor("List registered upgrade processes for a specific module")
	public String list(String bundleSymbolicName) {
		List<UpgradeInfo> upgradeInfos = _upgradeExecutor.getUpgradeInfos(
			bundleSymbolicName);

		StringBundler sb = new StringBundler(5 + (3 * upgradeInfos.size()));

		sb.append("Registered upgrade processes for ");
		sb.append(bundleSymbolicName);
		sb.append(StringPool.SPACE);
		sb.append(
			ReleaseManagerUtil.getSchemaVersionString(
				_releaseLocalService.fetchRelease(bundleSymbolicName)));
		sb.append(StringPool.NEW_LINE);

		for (UpgradeInfo upgradeProcess : upgradeInfos) {
			sb.append(StringPool.TAB);
			sb.append(upgradeProcess);
			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	protected void executeAll(
		Set<String> upgradeThrewExceptionBundleSymbolicNames) {

		while (true) {
			Set<String> bundleSymbolicNames = new HashSet<>(
				_upgradeExecutor.getBundleSymbolicNames());

			Set<String> failedBundleSymbolicNames =
				_upgradeExecutor.getFailedBundleSymbolicNames();

			bundleSymbolicNames.removeAll(failedBundleSymbolicNames);

			Set<String> upgradableBundleSymbolicNames =
				ReleaseManagerUtil.getUpgradableBundleSymbolicNames(
					bundleSymbolicNames, _releaseLocalService,
					_upgradeExecutor);

			upgradableBundleSymbolicNames.addAll(failedBundleSymbolicNames);
			upgradableBundleSymbolicNames.removeAll(
				upgradeThrewExceptionBundleSymbolicNames);

			if (upgradableBundleSymbolicNames.isEmpty()) {
				return;
			}

			for (String upgradableBundleSymbolicName :
					upgradableBundleSymbolicNames) {

				try {
					List<UpgradeInfo> upgradeInfos =
						_upgradeExecutor.getUpgradeInfos(
							upgradableBundleSymbolicName);

					_upgradeExecutor.execute(
						BundleUtil.getBundle(
							_bundleContext, upgradableBundleSymbolicName),
						upgradeInfos);
				}
				catch (Throwable throwable) {
					_log.error(
						"Failed upgrade process for module ".concat(
							upgradableBundleSymbolicName),
						throwable);

					upgradeThrewExceptionBundleSymbolicNames.add(
						upgradableBundleSymbolicName);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeOSGiCommands.class);

	private BundleContext _bundleContext;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	@Reference
	private ReleaseManager _releaseManager;

	@Reference
	private UpgradeExecutor _upgradeExecutor;

}
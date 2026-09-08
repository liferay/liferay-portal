/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.registry;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 * @author Preston Crary
 */
public class UpgradeStepRegistry implements UpgradeStepRegistrator.Registry {

	public UpgradeStepRegistry(
		BundleContext bundleContext, boolean portalUpgraded,
		ServiceReference<UpgradeStepRegistrator> serviceReference) {

		_bundleContext = bundleContext;
		_portalUpgraded = portalUpgraded;
		_serviceReference = serviceReference;
	}

	public void destroy() {
		_upgradeInfosDCLSingleton.destroy(null);
	}

	public List<UpgradeStep> getReleaseCreationUpgradeSteps() {
		getUpgradeInfos();

		return _releaseCreationUpgradeSteps;
	}

	public List<UpgradeInfo> getUpgradeInfos() {
		return _upgradeInfosDCLSingleton.getSingleton(this::_getUpgradeInfos);
	}

	@Override
	public void register(
		String fromSchemaVersionString, String toSchemaVersionString,
		UpgradeStep... upgradeSteps) {

		if (ArrayUtil.isEmpty(upgradeSteps)) {
			return;
		}

		List<UpgradeStep> upgradeStepsList = new ArrayList<>();

		for (UpgradeStep upgradeStep : upgradeSteps) {
			if (upgradeStep instanceof UpgradeProcess) {
				UpgradeProcess upgradeProcess = (UpgradeProcess)upgradeStep;

				for (UpgradeStep innerUpgradeStep :
						upgradeProcess.getUpgradeSteps()) {

					upgradeStepsList.add(innerUpgradeStep);
				}
			}
			else {
				upgradeStepsList.add(upgradeStep);
			}
		}

		if (upgradeStepsList.size() == 1) {
			_upgradeInfos.add(
				new UpgradeInfo(
					fromSchemaVersionString, toSchemaVersionString,
					upgradeStepsList.get(0)));
		}
		else {
			_upgradeInfos.add(
				new UpgradeInfo(
					fromSchemaVersionString, toSchemaVersionString,
					() -> {
						for (UpgradeStep upgradeStep : upgradeStepsList) {
							upgradeStep.upgrade();
						}
					}));
		}
	}

	@Override
	public void registerInitialization() {
		_initialization = true;
	}

	@Override
	public void registerReleaseCreationUpgradeSteps(
		UpgradeStep... upgradeSteps) {

		Collections.addAll(_releaseCreationUpgradeSteps, upgradeSteps);
	}

	private String _getFinalSchemaVersion(List<UpgradeInfo> upgradeInfos) {
		Version finalSchemaVersion = null;

		for (UpgradeInfo upgradeInfo : upgradeInfos) {
			Version schemaVersion = Version.parseVersion(
				upgradeInfo.getToSchemaVersionString());

			if (finalSchemaVersion == null) {
				finalSchemaVersion = schemaVersion;
			}
			else {
				finalSchemaVersion =
					(finalSchemaVersion.compareTo(schemaVersion) >= 0) ?
						finalSchemaVersion : schemaVersion;
			}
		}

		return finalSchemaVersion.toString();
	}

	private List<UpgradeInfo> _getUpgradeInfos() {
		UpgradeStepRegistrator upgradeStepRegistrator =
			_bundleContext.getService(_serviceReference);

		try {
			upgradeStepRegistrator.register(this);
		}
		catch (Throwable throwable) {
			_initialization = false;

			_releaseCreationUpgradeSteps.clear();
			_upgradeInfos.clear();

			return ReflectionUtil.throwException(throwable);
		}
		finally {
			_bundleContext.ungetService(_serviceReference);
		}

		if (_initialization && _portalUpgraded) {
			if (_upgradeInfos.isEmpty()) {
				return Arrays.asList(
					new UpgradeInfo("0.0.0", "1.0.0", new DummyUpgradeStep()));
			}

			return ListUtil.concat(
				Arrays.asList(
					new UpgradeInfo(
						"0.0.0", _getFinalSchemaVersion(_upgradeInfos),
						new DummyUpgradeStep())),
				_upgradeInfos);
		}

		return _upgradeInfos;
	}

	private final BundleContext _bundleContext;
	private boolean _initialization;
	private final boolean _portalUpgraded;
	private final List<UpgradeStep> _releaseCreationUpgradeSteps =
		new ArrayList<>();
	private final ServiceReference<UpgradeStepRegistrator> _serviceReference;
	private final List<UpgradeInfo> _upgradeInfos = new ArrayList<>();
	private final DCLSingleton<List<UpgradeInfo>> _upgradeInfosDCLSingleton =
		new DCLSingleton<>();

}
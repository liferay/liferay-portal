/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.extender.internal.osgi.commands;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.gogo.shell.logging.TeeLoggingUtil;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.upgrade.log.UpgradeLogContext;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;

import org.apache.felix.service.command.Descriptor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.command.function=check", "osgi.command.function=checkAll",
		"osgi.command.function=execute", "osgi.command.function=executeAll",
		"osgi.command.function=list", "osgi.command.function=show",
		"osgi.command.scope=verify"
	},
	service = {}
)
public class VerifyProcessTrackerOSGiCommands {

	@Descriptor(
		"List latest execution result for a module's verify process by symbolic name"
	)
	public void check(String bundleSymbolicName) {
		VerifyProcess verifyProcess;

		try {
			verifyProcess = _getVerifyProcess(
				_serviceTrackerMap, bundleSymbolicName);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			System.out.println(
				"No verify process exists for " + bundleSymbolicName);

			return;
		}

		String message =
			"Verify process " + ClassUtil.getClassName(verifyProcess);

		Release release = _fetchRelease(verifyProcess);

		if ((release == null) ||
			(!release.isVerified() &&
			 (release.getState() == ReleaseConstants.STATE_GOOD))) {

			System.out.println(message + " was not executed");
		}
		else {
			if (release.isVerified()) {
				System.out.println(message + " succeeded");
			}
			else if (release.getState() ==
						ReleaseConstants.STATE_VERIFY_FAILURE) {

				System.out.println(message + " failed");
			}
		}
	}

	@Descriptor("List latest execution result for all verify processes")
	public void checkAll() {
		for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
			check(bundleSymbolicName);
		}
	}

	@Descriptor("Execute a module's verify process by symbolic name")
	public void execute(String bundleSymbolicName) {
		TeeLoggingUtil.runWithTeeLogging(
			() -> {
				VerifyProcess verifyProcess = _getVerifyProcess(
					_serviceTrackerMap, bundleSymbolicName);

				_executeVerifyProcess(
					verifyProcess, _fetchRelease(verifyProcess));
			});
	}

	@Descriptor("Execute all verify processes")
	public void executeAll() {
		TeeLoggingUtil.runWithTeeLogging(
			() -> {
				for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
					VerifyProcess verifyProcess = _getVerifyProcess(
						_serviceTrackerMap, bundleSymbolicName);

					_executeVerifyProcess(
						verifyProcess, _fetchRelease(verifyProcess));
				}
			});
	}

	@Descriptor("List all registered verify processes")
	public void list() {
		for (String bundleSymbolicName : _serviceTrackerMap.keySet()) {
			show(bundleSymbolicName);
		}
	}

	@Descriptor(
		"Show the verify process class name by a module's symbolic name"
	)
	public void show(String bundleSymbolicName) {
		VerifyProcess verifyProcess;

		try {
			verifyProcess = _getVerifyProcess(
				_serviceTrackerMap, bundleSymbolicName);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			System.out.println(
				"No verify process exists for " + bundleSymbolicName);

			return;
		}

		System.out.println(
			StringBundler.concat(
				"Registered verify process ",
				ClassUtil.getClassName(verifyProcess), " for module ",
				bundleSymbolicName));
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, VerifyProcess.class, null,
			new ServiceReferenceMapper<String, VerifyProcess>() {

				@Override
				public void map(
					ServiceReference<VerifyProcess> serviceReference,
					Emitter<String> emitter) {

					Bundle bundle = serviceReference.getBundle();

					emitter.emit(bundle.getSymbolicName());
				}

			},
			new EagerServiceTrackerCustomizer
				<VerifyProcess, VerifyProcessHolder>() {

				@Override
				public VerifyProcessHolder addingService(
					ServiceReference<VerifyProcess> serviceReference) {

					VerifyProcessHolder verifyProcessHolder =
						new VerifyProcessHolder(serviceReference);

					Bundle bundle = serviceReference.getBundle();

					Release release = _releaseLocalService.fetchRelease(
						bundle.getSymbolicName());

					boolean initialDeployment = _isInitialDeployment(
						bundleContext, release);

					if ((!initialDeployment && !release.isVerified()) ||
						(GetterUtil.getBoolean(
							serviceReference.getProperty(
								"initial.deployment")) &&
						 initialDeployment) ||
						(StartupHelperUtil.isRunOnPortalUpgradeVerifiers() &&
						 GetterUtil.getBoolean(
							 serviceReference.getProperty(
								 "run.on.portal.upgrade")))) {

						_executeVerifyProcess(
							verifyProcessHolder.getVerifyProcess(), release);
					}
					else if ((release == null) &&
							 !_isServiceBundle(serviceReference.getBundle())) {

						release = _releaseLocalService.createRelease(
							_counterLocalService.increment());

						release.setServletContextName(bundle.getSymbolicName());

						release.setVerified(true);
						release.setState(ReleaseConstants.STATE_GOOD);

						release = _releaseLocalService.updateRelease(release);
					}
					else if ((release != null) && initialDeployment) {
						release.setVerified(true);
						release.setState(ReleaseConstants.STATE_GOOD);

						_releaseLocalService.updateRelease(release);
					}

					return verifyProcessHolder;
				}

				@Override
				public void modifiedService(
					ServiceReference<VerifyProcess> serviceReference,
					VerifyProcessHolder verifyProcessHolder) {
				}

				@Override
				public void removedService(
					ServiceReference<VerifyProcess> serviceReference,
					VerifyProcessHolder verifyProcessHolder) {

					verifyProcessHolder.ungetVerifyProcess();
				}

			});

		Dictionary<String, Object> osgiCommandProperties =
			new HashMapDictionary<>();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith("osgi.command.")) {
				osgiCommandProperties.put(key, entry.getValue());
			}
		}

		_serviceRegistration = bundleContext.registerService(
			VerifyProcessTrackerOSGiCommands.class, this,
			osgiCommandProperties);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_serviceTrackerMap.close();
	}

	private void _executeVerifyProcess(
		VerifyProcess verifyProcess, Release release) {

		NotificationThreadLocal.setEnabled(false);
		StagingAdvicesThreadLocal.setEnabled(false);
		WorkflowThreadLocal.setEnabled(false);

		try {
			Bundle bundle = FrameworkUtil.getBundle(verifyProcess.getClass());

			if ((release == null) && !_isServiceBundle(bundle)) {

				// Verification state must be persisted even though not all
				// verifiers are associated with a database service

				release = _releaseLocalService.createRelease(
					_counterLocalService.increment());

				release.setServletContextName(bundle.getSymbolicName());
				release.setVerified(false);
			}

			System.out.println(
				"Executing verify " + ClassUtil.getClassName(verifyProcess));

			try {
				UpgradeLogContext.setContext(bundle.getSymbolicName());

				verifyProcess.verify();

				if (release != null) {
					release.setVerified(true);
					release.setState(ReleaseConstants.STATE_GOOD);
				}
			}
			catch (VerifyException verifyException) {
				_log.error(verifyException);

				if (release != null) {
					release.setVerified(false);
					release.setState(ReleaseConstants.STATE_VERIFY_FAILURE);
				}
			}
			finally {
				UpgradeLogContext.clearContext();
			}

			if (release != null) {
				_releaseLocalService.updateRelease(release);
			}
		}
		finally {
			NotificationThreadLocal.setEnabled(true);
			StagingAdvicesThreadLocal.setEnabled(true);
			WorkflowThreadLocal.setEnabled(true);
		}
	}

	private Release _fetchRelease(VerifyProcess verifyProcess) {
		Bundle bundle = FrameworkUtil.getBundle(verifyProcess.getClass());

		return _releaseLocalService.fetchRelease(bundle.getSymbolicName());
	}

	private VerifyProcess _getVerifyProcess(
		ServiceTrackerMap<String, VerifyProcessHolder> verifyProcessTrackerMap,
		String bundleSymbolicName) {

		VerifyProcessHolder verifyProcessHolder =
			verifyProcessTrackerMap.getService(bundleSymbolicName);

		if (verifyProcessHolder == null) {
			throw new IllegalArgumentException(
				"No verify processes exists for " + bundleSymbolicName);
		}

		return verifyProcessHolder.getVerifyProcess();
	}

	private boolean _isInitialDeployment(
		BundleContext bundleContext, Release release) {

		if (release == null) {
			return true;
		}

		try {
			Collection<ServiceReference<Release>> releases =
				bundleContext.getServiceReferences(
					Release.class,
					"(&(release.bundle.symbolic.name=" +
						release.getBundleSymbolicName() +
							")(release.initial=true))");

			if (!releases.isEmpty()) {
				return true;
			}
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new RuntimeException(invalidSyntaxException);
		}

		return false;
	}

	private boolean _isServiceBundle(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if ((headers.get("Liferay-Service") == null) &&
			(headers.get("Liferay-Spring-Context") == null)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VerifyProcessTrackerOSGiCommands.class);

	private BundleContext _bundleContext;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference(target = ModuleServiceLifecycle.PORTLETS_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	private ServiceRegistration<?> _serviceRegistration;
	private ServiceTrackerMap<String, VerifyProcessHolder> _serviceTrackerMap;

	private class VerifyProcessHolder {

		public VerifyProcess getVerifyProcess() {
			return _verifyProcessDCLSingleton.getSingleton(
				() -> _bundleContext.getService(_serviceReference));
		}

		public void ungetVerifyProcess() {
			_verifyProcessDCLSingleton.destroy(
				verifyProcess -> _bundleContext.ungetService(
					_serviceReference));
		}

		private VerifyProcessHolder(
			ServiceReference<VerifyProcess> serviceReference) {

			_serviceReference = serviceReference;
		}

		private final ServiceReference<VerifyProcess> _serviceReference;
		private final DCLSingleton<VerifyProcess> _verifyProcessDCLSingleton =
			new DCLSingleton<>();

	}

}
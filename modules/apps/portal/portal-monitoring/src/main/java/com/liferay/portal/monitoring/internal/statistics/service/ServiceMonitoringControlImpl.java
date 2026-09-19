/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.statistics.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.monitoring.DataSampleFactory;
import com.liferay.portal.kernel.monitoring.MethodSignature;
import com.liferay.portal.kernel.monitoring.ServiceMonitoringControl;
import com.liferay.portal.monitoring.internal.aop.ServiceMonitorAdvice;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	service = ServiceMonitoringControl.class
)
public class ServiceMonitoringControlImpl implements ServiceMonitoringControl {

	@Override
	public void addServiceClass(String className) {
		_serviceClasses.add(className);
	}

	@Override
	public void addServiceClassMethod(
		String className, String methodName, String[] parameterTypes) {

		MethodSignature methodSignature = new MethodSignature(
			className, methodName, parameterTypes);

		_serviceClassMethods.add(methodSignature);
	}

	@Override
	public Set<MethodSignature> getServiceClassMethods() {
		return Collections.unmodifiableSet(_serviceClassMethods);
	}

	@Override
	public Set<String> getServiceClasses() {
		return Collections.unmodifiableSet(_serviceClasses);
	}

	@Override
	public boolean isInclusiveMode() {
		return _inclusiveMode;
	}

	@Override
	public void setInclusiveMode(boolean inclusiveMode) {
		_inclusiveMode = inclusiveMode;
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, String> properties) {

		_bundleContext = bundleContext;

		MonitoringConfiguration monitoringConfiguration =
			ConfigurableUtil.createConfigurable(
				MonitoringConfiguration.class, properties);

		boolean monitorServiceRequest =
			monitoringConfiguration.monitorServiceRequest();

		if (monitorServiceRequest == _monitorServiceRequest) {
			return;
		}

		synchronized (this) {
			if (monitorServiceRequest == _monitorServiceRequest) {
				return;
			}

			if (_serviceRegistration == null) {
				_serviceRegistration = _bundleContext.registerService(
					ChainableMethodAdvice.class,
					new ServiceMonitorAdvice(this, _dataSampleFactory), null);
			}
			else {
				_serviceRegistration.unregister();

				_serviceRegistration = null;
			}

			_monitorServiceRequest = monitorServiceRequest;
		}
	}

	@Deactivate
	protected synchronized void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private volatile BundleContext _bundleContext;

	@Reference
	private DataSampleFactory _dataSampleFactory;

	private boolean _inclusiveMode = true;
	private volatile boolean _monitorServiceRequest;
	private final Set<MethodSignature> _serviceClassMethods = new HashSet<>();
	private final Set<String> _serviceClasses = new HashSet<>();
	private volatile ServiceRegistration<ChainableMethodAdvice>
		_serviceRegistration;

}
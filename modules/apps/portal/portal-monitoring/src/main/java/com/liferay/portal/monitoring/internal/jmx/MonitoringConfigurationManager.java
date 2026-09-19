/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.monitoring.internal.jmx;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.monitoring.Level;
import com.liferay.portal.kernel.monitoring.MethodSignature;
import com.liferay.portal.kernel.monitoring.MonitoringControl;
import com.liferay.portal.kernel.monitoring.ServiceMonitoringControl;
import com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration;

import java.util.Map;
import java.util.Set;

import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
	property = {
		"jmx.objectname=com.liferay.portal.monitoring:classification=monitoring_service,name=MonitoringConfigurationManager",
		"jmx.objectname.cache.key=MonitoringProcessorManager"
	},
	service = DynamicMBean.class
)
public class MonitoringConfigurationManager
	extends StandardMBean implements MonitoringConfigurationManagerMBean {

	public MonitoringConfigurationManager() throws NotCompliantMBeanException {
		super(MonitoringConfigurationManagerMBean.class);
	}

	@Override
	public void addServiceClass(String className) {
		_serviceMonitoringControl.addServiceClass(className);
	}

	@Override
	public void addServiceClassMethod(
		String className, String methodName, String[] parameterTypes) {

		_serviceMonitoringControl.addServiceClassMethod(
			className, methodName, parameterTypes);
	}

	@Override
	public String getLevel(String namespace) {
		Level level = _monitoringControl.getLevel(namespace);

		if (level == null) {
			level = Level.OFF;
		}

		return level.toString();
	}

	@Override
	public String[] getNamespaces() {
		Set<String> namespaces = _monitoringControl.getNamespaces();

		return namespaces.toArray(new String[0]);
	}

	@Override
	public Set<MethodSignature> getServiceClassMethods() {
		return _serviceMonitoringControl.getServiceClassMethods();
	}

	@Override
	public Set<String> getServiceClasses() {
		return _serviceMonitoringControl.getServiceClasses();
	}

	@Override
	public boolean isInclusiveMode() {
		return _serviceMonitoringControl.isInclusiveMode();
	}

	@Override
	public boolean isMonitorPortalRequest() {
		return _monitoringConfiguration.monitorPortalRequest();
	}

	@Override
	public boolean isMonitorPortletActionRequest() {
		return _monitoringConfiguration.monitorPortletActionRequest();
	}

	@Override
	public boolean isMonitorPortletEventRequest() {
		return _monitoringConfiguration.monitorPortletEventRequest();
	}

	@Override
	public boolean isMonitorPortletHeaderRequest() {
		return _monitoringConfiguration.monitorPortletHeaderRequest();
	}

	@Override
	public boolean isMonitorPortletRenderRequest() {
		return _monitoringConfiguration.monitorPortletRenderRequest();
	}

	@Override
	public boolean isMonitorPortletResourceRequest() {
		return _monitoringConfiguration.monitorPortletResourceRequest();
	}

	@Override
	public boolean isMonitorServiceRequest() {
		return _monitoringConfiguration.monitorServiceRequest();
	}

	@Override
	public void setInclusiveMode(boolean inclusiveMode) {
		_serviceMonitoringControl.setInclusiveMode(inclusiveMode);
	}

	@Override
	public void setLevel(String namespace, String levelName) {
		Level level = Level.valueOf(levelName);

		_monitoringControl.setLevel(namespace, level);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, properties);
	}

	private volatile MonitoringConfiguration _monitoringConfiguration;

	@Reference
	private MonitoringControl _monitoringControl;

	@Reference
	private ServiceMonitoringControl _serviceMonitoringControl;

}
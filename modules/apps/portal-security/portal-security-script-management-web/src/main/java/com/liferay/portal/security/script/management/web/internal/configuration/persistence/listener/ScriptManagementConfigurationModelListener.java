/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.configuration.persistence.listener;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.script.management.configuration.GroovyScriptUsesCheckThreadLocal;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yuri Monteiro
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration",
	service = ConfigurationModelListener.class
)
public class ScriptManagementConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeDelete(String pid)
		throws ConfigurationModelListenerException {

		_checkActiveGroovyScriptUses(null);
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (GetterUtil.getBoolean(
				properties.get("allowScriptContentToBeExecutedOrIncluded"))) {

			return;
		}

		_checkActiveGroovyScriptUses(properties);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, GroovyScriptUsesFactory.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private void _checkActiveGroovyScriptUses(
			Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!GroovyScriptUsesCheckThreadLocal.isEnabled()) {
			return;
		}

		boolean hasGroovyScriptUses = false;

		try {
			hasGroovyScriptUses = _hasGroovyScriptUses();
		}
		catch (Exception exception) {
			throw new ConfigurationModelListenerException(
				exception, ScriptManagementConfiguration.class, getClass(),
				properties);
		}

		if (!hasGroovyScriptUses) {
			return;
		}

		throw new ConfigurationModelListenerException(
			_language.get(
				LocaleUtil.getMostRelevantLocale(),
				"resolve-all-active-scripting-uses-before-proceeding-you-can-" +
					"deactivate-the-source-entity-or-remove-the-script"),
			ScriptManagementConfiguration.class, getClass(), properties);
	}

	private boolean _hasGroovyScriptUses() throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			boolean[] activeGroovyScriptUses = {false};

			_companyLocalService.forEachCompanyId(
				companyId -> {
					if (activeGroovyScriptUses[0]) {
						return;
					}

					if (_hasGroovyScriptUsesInCurrentCompany()) {
						activeGroovyScriptUses[0] = true;
					}
				});

			return activeGroovyScriptUses[0];
		}

		return _hasGroovyScriptUsesInCurrentCompany();
	}

	private boolean _hasGroovyScriptUsesInCurrentCompany() throws Exception {
		for (GroovyScriptUsesFactory groovyScriptUsesFactory :
				_serviceTrackerList) {

			if (groovyScriptUsesFactory.hasUses()) {
				return true;
			}
		}

		return false;
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Language _language;

	private ServiceTrackerList<GroovyScriptUsesFactory> _serviceTrackerList;

}
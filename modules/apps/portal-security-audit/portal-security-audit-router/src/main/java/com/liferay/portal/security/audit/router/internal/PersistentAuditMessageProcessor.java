/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.BatchProcessor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.security.audit.AuditEventManager;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorCompanyConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration",
	property = "eventTypes=*", service = AuditMessageProcessor.class
)
public class PersistentAuditMessageProcessor implements AuditMessageProcessor {

	@Override
	public void process(AuditMessage auditMessage) {
		try {
			doProcess(auditMessage);
		}
		catch (CTTransactionException ctTransactionException) {
			throw ctTransactionException;
		}
		catch (Exception exception) {
			_log.fatal(
				"Unable to process audit message " + auditMessage, exception);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		PersistentAuditMessageProcessorCompanyConfiguration
			persistentAuditMessageProcessorCompanyConfiguration =
				ConfigurableUtil.createConfigurable(
					PersistentAuditMessageProcessorCompanyConfiguration.class,
					Collections.emptyMap());

		_defaultCompanyEnabled =
			persistentAuditMessageProcessorCompanyConfiguration.enabled();

		_managedServiceFactoryServiceRegistration =
			bundleContext.registerService(
				ManagedServiceFactory.class,
				new PersistentAuditMessageProcessorCompanyManagedServiceFactory(),
				MapUtil.singletonDictionary(
					Constants.SERVICE_PID,
					_COMPANY_CONFIGURATION_PID + ".scoped"));
	}

	@Deactivate
	protected void deactivate() {
		_managedServiceFactoryServiceRegistration.unregister();

		if (_batchProcessor != null) {
			_batchProcessor.close();
		}
	}

	protected void doProcess(AuditMessage auditMessage) throws Exception {
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration =
				_persistentAuditMessageProcessorConfiguration;

		if (!persistentAuditMessageProcessorConfiguration.enabled() ||
			!_isCompanyEnabled(auditMessage.getCompanyId())) {

			return;
		}

		_batchProcessor.add(auditMessage);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration =
				ConfigurableUtil.createConfigurable(
					PersistentAuditMessageProcessorConfiguration.class,
					properties);

		if (persistentAuditMessageProcessorConfiguration.enabled()) {
			if (_batchProcessor == null) {
				_batchProcessor = new BatchProcessor<>(
					persistentAuditMessageProcessorConfiguration.
						flushInterval(),
					persistentAuditMessageProcessorConfiguration.bufferSize(),
					_auditEventManager::addAuditEvents,
					PersistentAuditMessageProcessor.class.getName());
			}
			else {
				_batchProcessor.configure(
					persistentAuditMessageProcessorConfiguration.
						flushInterval(),
					persistentAuditMessageProcessorConfiguration.bufferSize());
			}
		}

		_persistentAuditMessageProcessorConfiguration =
			persistentAuditMessageProcessorConfiguration;
	}

	private boolean _isCompanyEnabled(long companyId) {
		if ((companyId == CompanyConstants.SYSTEM) ||
			_enabledMap.getOrDefault(companyId, _defaultCompanyEnabled)) {

			return true;
		}

		return !FeatureFlagManagerUtil.isEnabled(companyId, "LPD-114718");
	}

	private static final String _COMPANY_CONFIGURATION_PID =
		PersistentAuditMessageProcessorCompanyConfiguration.class.getName();

	private static final Log _log = LogFactoryUtil.getLog(
		PersistentAuditMessageProcessor.class);

	@Reference
	private AuditEventManager _auditEventManager;

	private volatile BatchProcessor<AuditMessage> _batchProcessor;
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private boolean _defaultCompanyEnabled;
	private final Map<Long, Boolean> _enabledMap = new ConcurrentHashMap<>();
	private ServiceRegistration<ManagedServiceFactory>
		_managedServiceFactoryServiceRegistration;
	private volatile PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration;

	private class PersistentAuditMessageProcessorCompanyManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			Long companyId = _companyIds.remove(pid);

			if (companyId != null) {
				_enabledMap.remove(companyId);
			}
		}

		@Override
		public String getName() {
			return _COMPANY_CONFIGURATION_PID + ".scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary) {
			deleted(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			if (companyId == CompanyConstants.SYSTEM) {
				return;
			}

			_companyIds.put(pid, companyId);

			PersistentAuditMessageProcessorCompanyConfiguration
				persistentAuditMessageProcessorCompanyConfiguration =
					ConfigurableUtil.createConfigurable(
						PersistentAuditMessageProcessorCompanyConfiguration.
							class,
						dictionary);

			_enabledMap.put(
				companyId,
				persistentAuditMessageProcessorCompanyConfiguration.enabled());
		}

	}

}
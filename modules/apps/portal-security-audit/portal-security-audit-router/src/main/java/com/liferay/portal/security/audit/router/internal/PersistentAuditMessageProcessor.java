/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.BatchProcessor;
import com.liferay.portal.security.audit.AuditEventManager;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.configuration.AuditConfigurationUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(property = "eventTypes=*", service = AuditMessageProcessor.class)
public class PersistentAuditMessageProcessor implements AuditMessageProcessor {

	@Override
	public void process(AuditMessage auditMessage) {
		try {
			_process(auditMessage);
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
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			ModelListener.class, new CompanyModelListener(), null);
	}

	@Deactivate
	protected void deactivate() {
		_stopped = true;

		_serviceRegistration.unregister();

		for (BatchProcessorHolder batchProcessorHolder :
				_batchProcessorHolders.values()) {

			batchProcessorHolder.close();
		}

		_batchProcessorHolders.clear();
	}

	private BatchProcessorHolder _getBatchProcessorHolder(
		long configurationCompanyId,
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration) {

		BatchProcessorHolder batchProcessorHolder = _batchProcessorHolders.get(
			configurationCompanyId);

		if ((batchProcessorHolder == null) ||
			!batchProcessorHolder.isMatchingConfiguration(
				persistentAuditMessageProcessorConfiguration)) {

			batchProcessorHolder = _batchProcessorHolders.compute(
				configurationCompanyId,
				(curConfigurationCompanyId, curBatchProcessorHolder) -> {
					if (curBatchProcessorHolder == null) {
						if (_stopped) {
							return null;
						}

						return new BatchProcessorHolder(
							curConfigurationCompanyId,
							persistentAuditMessageProcessorConfiguration);
					}

					if (!curBatchProcessorHolder.isMatchingConfiguration(
							persistentAuditMessageProcessorConfiguration)) {

						curBatchProcessorHolder.reconfigure(
							persistentAuditMessageProcessorConfiguration);
					}

					return curBatchProcessorHolder;
				});
		}

		if (_stopped) {
			return null;
		}

		return batchProcessorHolder;
	}

	private void _process(AuditMessage auditMessage) {
		if (_stopped) {
			return;
		}

		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration =
				AuditConfigurationUtil.getConfiguration(
					PersistentAuditMessageProcessorConfiguration.class,
					auditMessage.getCompanyId());

		if (!persistentAuditMessageProcessorConfiguration.enabled()) {
			return;
		}

		BatchProcessorHolder batchProcessorHolder = _getBatchProcessorHolder(
			AuditConfigurationUtil.getConfigurationCompanyId(
				auditMessage.getCompanyId()),
			persistentAuditMessageProcessorConfiguration);

		if (batchProcessorHolder != null) {
			batchProcessorHolder.addAuditMessage(auditMessage);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PersistentAuditMessageProcessor.class);

	@Reference
	private AuditEventManager _auditEventManager;

	private final Map<Long, BatchProcessorHolder> _batchProcessorHolders =
		new ConcurrentHashMap<>();
	private ServiceRegistration<?> _serviceRegistration;
	private volatile boolean _stopped;

	private class BatchProcessorHolder {

		public BatchProcessorHolder(
			long configurationCompanyId,
			PersistentAuditMessageProcessorConfiguration
				persistentAuditMessageProcessorConfiguration) {

			_bufferSize =
				persistentAuditMessageProcessorConfiguration.bufferSize();
			_flushInterval =
				persistentAuditMessageProcessorConfiguration.flushInterval();

			_batchProcessor = new BatchProcessor<>(
				_flushInterval, _bufferSize, _auditEventManager::addAuditEvents,
				PersistentAuditMessageProcessor.class.getName() +
					StringPool.DASH + configurationCompanyId);
		}

		public void addAuditMessage(AuditMessage auditMessage) {
			_batchProcessor.add(auditMessage);
		}

		public void close() {
			_batchProcessor.close();
		}

		public boolean isMatchingConfiguration(
			PersistentAuditMessageProcessorConfiguration
				persistentAuditMessageProcessorConfiguration) {

			if ((_bufferSize ==
					persistentAuditMessageProcessorConfiguration.
						bufferSize()) &&
				(_flushInterval ==
					persistentAuditMessageProcessorConfiguration.
						flushInterval())) {

				return true;
			}

			return false;
		}

		public void reconfigure(
			PersistentAuditMessageProcessorConfiguration
				persistentAuditMessageProcessorConfiguration) {

			_bufferSize =
				persistentAuditMessageProcessorConfiguration.bufferSize();
			_flushInterval =
				persistentAuditMessageProcessorConfiguration.flushInterval();

			_batchProcessor.configure(_flushInterval, _bufferSize);
		}

		private final BatchProcessor<AuditMessage> _batchProcessor;
		private volatile int _bufferSize;
		private volatile long _flushInterval;

	}

	private class CompanyModelListener extends BaseModelListener<Company> {

		@Override
		public void onBeforeRemove(Company company) {
			BatchProcessorHolder batchProcessorHolder =
				_batchProcessorHolders.remove(company.getCompanyId());

			if (batchProcessorHolder != null) {
				batchProcessorHolder.close();
			}
		}

	}

}
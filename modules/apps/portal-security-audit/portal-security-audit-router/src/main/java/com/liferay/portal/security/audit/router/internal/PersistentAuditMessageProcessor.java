/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.BatchProcessor;
import com.liferay.portal.security.audit.AuditEventManager;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

import java.util.Map;

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
	protected void activate(Map<String, Object> properties) {
		modified(properties);
	}

	@Deactivate
	protected void deactivate() {
		_batchProcessor.close();
	}

	protected void doProcess(AuditMessage auditMessage) throws Exception {
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration =
				_persistentAuditMessageProcessorConfiguration;

		if (!persistentAuditMessageProcessorConfiguration.enabled()) {
			return;
		}

		_batchProcessor.add(auditMessage);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_persistentAuditMessageProcessorConfiguration =
			ConfigurableUtil.createConfigurable(
				PersistentAuditMessageProcessorConfiguration.class, properties);

		if (!_persistentAuditMessageProcessorConfiguration.enabled()) {
			return;
		}

		if (_batchProcessor == null) {
			_batchProcessor = new BatchProcessor<>(
				_persistentAuditMessageProcessorConfiguration.flushInterval(),
				_persistentAuditMessageProcessorConfiguration.bufferSize(),
				_auditEventManager::addAuditEvents,
				PersistentAuditMessageProcessor.class.getName());
		}
		else {
			_batchProcessor.configure(
				_persistentAuditMessageProcessorConfiguration.flushInterval(),
				_persistentAuditMessageProcessorConfiguration.bufferSize());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PersistentAuditMessageProcessor.class);

	@Reference
	private AuditEventManager _auditEventManager;

	private volatile BatchProcessor<AuditMessage> _batchProcessor;
	private volatile PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration;

}
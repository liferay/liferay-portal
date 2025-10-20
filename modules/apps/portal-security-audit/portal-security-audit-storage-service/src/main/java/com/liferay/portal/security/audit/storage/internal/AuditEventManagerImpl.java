/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.audit.AuditEvent;
import com.liferay.portal.security.audit.AuditEventManager;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.util.PortalInstances;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Greenwald
 */
@Component(service = AuditEventManager.class)
public class AuditEventManagerImpl implements AuditEventManager {

	@Override
	public AuditEvent addAuditEvent(AuditMessage auditMessage) {
		return _createAuditEvent(
			_auditEventLocalService.addAuditEvent(auditMessage));
	}

	@Override
	public void addAuditEvents(List<AuditMessage> auditMessages) {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			Map<Long, List<AuditMessage>> auditMessagesMap = new HashMap<>();

			for (AuditMessage auditMessage : auditMessages) {
				List<AuditMessage> companyAuditMessages =
					auditMessagesMap.computeIfAbsent(
						auditMessage.getCompanyId(), key -> new ArrayList<>());

				companyAuditMessages.add(auditMessage);
			}

			for (Map.Entry<Long, List<AuditMessage>> entry :
					auditMessagesMap.entrySet()) {

				if (PortalInstances.isCompanyInDeletionProcess(
						entry.getKey()) ||
					!ArrayUtil.contains(
						PortalInstancePool.getCompanyIds(), entry.getKey())) {

					continue;
				}

				_companyLocalService.forEachCompanyId(
					companyId -> _auditEventLocalService.addAuditEvents(
						entry.getValue()),
					new long[] {entry.getKey()});
			}
		}
		else {
			_auditEventLocalService.addAuditEvents(auditMessages);
		}
	}

	@Override
	public AuditEvent fetchAuditEvent(long auditEventId) {
		return _createAuditEvent(
			_auditEventLocalService.fetchAuditEvent(auditEventId));
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, int start, int end,
		OrderByComparator
			<com.liferay.portal.security.audit.storage.model.AuditEvent>
				orderByComparator) {

		return _translate(
			_auditEventLocalService.getAuditEvents(
				companyId, start, end, orderByComparator));
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, String eventType,
		String className, String classPK, String clientHost, String clientIP,
		String serverName, int serverPort, String sessionID, boolean andSearch,
		int start, int end,
		OrderByComparator
			<com.liferay.portal.security.audit.storage.model.AuditEvent>
				orderByComparator) {

		return _translate(
			_auditEventLocalService.getAuditEvents(
				companyId, groupId, userId, userName, createDateGT,
				createDateLT, eventType, className, classPK, clientHost,
				clientIP, serverName, serverPort, sessionID, andSearch, start,
				end, orderByComparator));
	}

	@Override
	public int getAuditEventsCount(long companyId) {
		return _auditEventLocalService.getAuditEventsCount(companyId);
	}

	@Override
	public int getAuditEventsCount(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, String eventType,
		String className, String classPK, String clientHost, String clientIP,
		String serverName, int serverPort, String sessionID,
		boolean andSearch) {

		return _auditEventLocalService.getAuditEventsCount(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			eventType, className, classPK, clientHost, clientIP, serverName,
			serverPort, sessionID, andSearch);
	}

	private AuditEvent _createAuditEvent(
		com.liferay.portal.security.audit.storage.model.AuditEvent
			auditEventModel) {

		return AuditEventAutoEscapeBeanHandler.createProxy(auditEventModel);
	}

	private List<AuditEvent> _translate(
		List<com.liferay.portal.security.audit.storage.model.AuditEvent>
			auditEvents) {

		return TransformUtil.transform(
			auditEvents, auditEvent -> _createAuditEvent(auditEvent));
	}

	@Reference
	private AuditEventLocalService _auditEventLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

}
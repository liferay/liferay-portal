/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.impl;

import com.liferay.counter.kernel.model.Counter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.base.AuditEventLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent",
	service = AopService.class
)
@CTAware
public class AuditEventLocalServiceImpl extends AuditEventLocalServiceBaseImpl {

	@Override
	public AuditEvent addAuditEvent(AuditMessage auditMessage) {
		return auditEventPersistence.update(
			_toAuditEvent(auditMessage, counterLocalService.increment()));
	}

	@Override
	public void addAuditEvents(List<AuditMessage> auditMessages) {
		if (auditMessages.isEmpty()) {
			return;
		}

		int size = auditMessages.size();

		long startAuditEventId =
			counterLocalService.increment(Counter.class.getName(), size) - size;

		for (AuditMessage auditMessage : auditMessages) {
			auditEventPersistence.update(
				_toAuditEvent(auditMessage, ++startAuditEventId));
		}
	}

	@Override
	public AuditEvent fetchAuditEvent(long auditEventId) {
		return auditEventPersistence.fetchByPrimaryKey(auditEventId);
	}

	@Override
	public List<AuditEvent> getAuditEvents(long companyId, int start, int end) {
		return auditEventPersistence.findByCompanyId(
			companyId, start, end, new AuditEventCreateDateComparator());
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, int start, int end,
		OrderByComparator<AuditEvent> orderByComparator) {

		return auditEventPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, long[] accountEntryIds,
		String className, String classPK, String clientHost, String clientIP,
		String contextName, String eventType, String serverName, int serverPort,
		String sessionID, boolean andSearch, int start, int end) {

		return getAuditEvents(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch, start, end, new AuditEventCreateDateComparator());
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, long[] accountEntryIds,
		String className, String classPK, String clientHost, String clientIP,
		String contextName, String eventType, String serverName, int serverPort,
		String sessionID, boolean andSearch, int start, int end,
		OrderByComparator<AuditEvent> orderByComparator) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public int getAuditEventsCount(long companyId) {
		return auditEventPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getAuditEventsCount(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, long[] accountEntryIds,
		String className, String classPK, String clientHost, String clientIP,
		String contextName, String eventType, String serverName, int serverPort,
		String sessionID, boolean andSearch) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch);

		return (int)dynamicQueryCount(dynamicQuery);
	}

	private DynamicQuery _buildDynamicQuery(
		long companyId, long groupId, long userId, String userName,
		Date createDateGT, Date createDateLT, long[] accountEntryIds,
		String className, String classPK, String clientHost, String clientIP,
		String contextName, String eventType, String serverName, int serverPort,
		String sessionID, boolean andSearch) {

		Junction junction = null;

		if (andSearch) {
			junction = RestrictionsFactoryUtil.conjunction();
		}
		else {
			junction = RestrictionsFactoryUtil.disjunction();
		}

		Property accountEntryIdProperty = PropertyFactoryUtil.forName(
			"accountEntryId");

		if (ArrayUtil.isNotEmpty(accountEntryIds)) {
			junction.add(accountEntryIdProperty.in(accountEntryIds));
		}
		else if (andSearch) {
			junction.add(accountEntryIdProperty.eq(0L));
		}

		if (groupId > 0) {
			Property property = PropertyFactoryUtil.forName("groupId");

			junction.add(property.eq(groupId));
		}

		if (userId > 0) {
			Property property = PropertyFactoryUtil.forName("userId");

			junction.add(property.eq(userId));
		}

		if (Validator.isNotNull(userName)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"userName",
					StringPool.PERCENT + userName + StringPool.PERCENT));
		}

		if (Validator.isNotNull(eventType)) {
			Property property = PropertyFactoryUtil.forName("eventType");

			String value =
				StringPool.PERCENT + StringUtil.toUpperCase(eventType) +
					StringPool.PERCENT;

			junction.add(property.like(value));
		}

		if (Validator.isNotNull(className)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"className",
					StringPool.PERCENT + className + StringPool.PERCENT));
		}

		if (Validator.isNotNull(classPK)) {
			Property property = PropertyFactoryUtil.forName("classPK");

			junction.add(property.eq(classPK));
		}

		if (Validator.isNotNull(clientHost)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"clientHost",
					StringPool.PERCENT + clientHost + StringPool.PERCENT));
		}

		if (Validator.isNotNull(clientIP)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"clientIP",
					StringPool.PERCENT + clientIP + StringPool.PERCENT));
		}

		Property contextNameProperty = PropertyFactoryUtil.forName(
			"contextName");

		if (Validator.isNotNull(contextName)) {
			junction.add(contextNameProperty.eq(contextName));
		}
		else if (andSearch) {
			junction.add(contextNameProperty.isNull());
		}

		if (Validator.isNotNull(serverName)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"serverName",
					StringPool.PERCENT + serverName + StringPool.PERCENT));
		}

		if (serverPort > 0) {
			Property property = PropertyFactoryUtil.forName("serverPort");

			junction.add(property.eq(serverPort));
		}

		if (Validator.isNotNull(sessionID)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"sessionID",
					StringPool.PERCENT + sessionID + StringPool.PERCENT));
		}

		DynamicQuery dynamicQuery = dynamicQuery();

		if (companyId > 0) {
			Property property = PropertyFactoryUtil.forName("companyId");

			dynamicQuery.add(property.eq(companyId));
		}

		if (createDateGT != null) {
			Property property = PropertyFactoryUtil.forName("createDate");

			dynamicQuery.add(property.gt(createDateGT));
		}

		if (createDateLT != null) {
			Property property = PropertyFactoryUtil.forName("createDate");

			dynamicQuery.add(property.lt(createDateLT));
		}

		return dynamicQuery.add(junction);
	}

	private AuditEvent _toAuditEvent(
		AuditMessage auditMessage, long auditEventId) {

		AuditEvent auditEvent = auditEventPersistence.create(auditEventId);

		auditEvent.setGroupId(auditMessage.getGroupId());
		auditEvent.setCompanyId(auditMessage.getCompanyId());
		auditEvent.setUserId(auditMessage.getUserId());
		auditEvent.setUserName(auditMessage.getUserName());
		auditEvent.setCreateDate(auditMessage.getTimestampDate());
		auditEvent.setAccountEntryId(auditMessage.getAccountEntryId());
		auditEvent.setAdditionalInfo(
			String.valueOf(auditMessage.getAdditionalInfo()));
		auditEvent.setClassName(auditMessage.getClassName());
		auditEvent.setClassPK(auditMessage.getClassPK());
		auditEvent.setClientHost(auditMessage.getClientHost());
		auditEvent.setClientIP(auditMessage.getClientIP());
		auditEvent.setContextName(auditMessage.getContextName());
		auditEvent.setCorrelationId(auditMessage.getCorrelationId());
		auditEvent.setEventType(auditMessage.getEventType());
		auditEvent.setHttpMethod(auditMessage.getHttpMethod());
		auditEvent.setImpersonated(auditMessage.isImpersonated());
		auditEvent.setImpersonatedUserEmailAddress(
			auditMessage.getImpersonatedUserEmailAddress());
		auditEvent.setImpersonatedUserId(auditMessage.getImpersonatedUserId());
		auditEvent.setImpersonatedUserName(
			auditMessage.getImpersonatedUserName());
		auditEvent.setMessage(auditMessage.getMessage());
		auditEvent.setObjectName(auditMessage.getObjectName());
		auditEvent.setRequestId(auditMessage.getRequestId());
		auditEvent.setRequestIdGenerated(auditMessage.isRequestIdGenerated());
		auditEvent.setResourceAction(auditMessage.getResourceAction());
		auditEvent.setResourceType(auditMessage.getResourceType());
		auditEvent.setRoles(auditMessage.getRoles());
		auditEvent.setServerName(auditMessage.getServerName());
		auditEvent.setServerPort(auditMessage.getServerPort());
		auditEvent.setSessionID(auditMessage.getSessionID());
		auditEvent.setUserAgent(auditMessage.getUserAgent());
		auditEvent.setUserEmailAddress(auditMessage.getUserEmailAddress());

		return auditEvent;
	}

}
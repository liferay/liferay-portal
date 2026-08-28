/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.rest.internal.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.rest.dto.v1_0.AuditEvent;
import com.liferay.portal.security.audit.rest.resource.v1_0.AuditEventResource;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEventTable;
import com.liferay.portal.security.audit.storage.service.AuditEventService;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rafael Praxedes
 * @author Manuele Castro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/audit-event.properties",
	scope = ServiceScope.PROTOTYPE, service = AuditEventResource.class
)
public class AuditEventResourceImpl extends BaseAuditEventResourceImpl {

	@Override
	public Page<AuditEvent> getAuditEventsPage(
			Long[] accountIds, String contextName, Date endDate,
			String eventType, Date startDate, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		long[] accountEntryIds = ArrayUtil.toArray(accountIds);

		return Page.of(
			transform(
				_auditEventService.getAuditEvents(
					contextCompany.getCompanyId(), 0, 0, null, startDate,
					endDate, accountEntryIds, null, null, null, null,
					contextName, eventType, null, 0, null, true,
					pagination.getStartPosition(), pagination.getEndPosition(),
					_toOrderByComparator(sorts)),
				this::_toAuditEvent),
			pagination,
			_auditEventService.getAuditEventsCount(
				contextCompany.getCompanyId(), 0, 0, null, startDate, endDate,
				accountEntryIds, null, null, null, null, contextName, eventType,
				null, 0, null, true));
	}

	private AuditEvent _toAuditEvent(
		com.liferay.portal.security.audit.storage.model.AuditEvent
			serviceBuilderAuditEvent) {

		return new AuditEvent() {
			{
				setAccountId(serviceBuilderAuditEvent::getAccountEntryId);
				setAdditionalInfo(
					() -> _toMap(serviceBuilderAuditEvent.getAdditionalInfo()));
				setClientHost(serviceBuilderAuditEvent::getClientHost);
				setClientIP(serviceBuilderAuditEvent::getClientIP);
				setContextName(serviceBuilderAuditEvent::getContextName);
				setCorrelationId(serviceBuilderAuditEvent::getCorrelationId);
				setCreator(
					() -> CreatorUtil.toCreator(
						null, _portal,
						_userLocalService.fetchUser(
							serviceBuilderAuditEvent.getUserId())));
				setDateCreated(serviceBuilderAuditEvent::getCreateDate);
				setEntityId(
					() -> GetterUtil.getLong(
						serviceBuilderAuditEvent.getClassPK()));
				setEntityType(serviceBuilderAuditEvent::getClassName);
				setEventType(serviceBuilderAuditEvent::getEventType);
				setGroupId(serviceBuilderAuditEvent::getGroupId);
				setHttpMethod(serviceBuilderAuditEvent::getHttpMethod);
				setId(serviceBuilderAuditEvent::getAuditEventId);
				setObjectName(serviceBuilderAuditEvent::getObjectName);
				setRequestId(serviceBuilderAuditEvent::getRequestId);
				setRequestIdGenerated(
					serviceBuilderAuditEvent::isRequestIdGenerated);
				setResourceAction(serviceBuilderAuditEvent::getResourceAction);
				setResourceType(serviceBuilderAuditEvent::getResourceType);
				setRoles(serviceBuilderAuditEvent::getRoles);
				setServerName(serviceBuilderAuditEvent::getServerName);
				setUserAgent(serviceBuilderAuditEvent::getUserAgent);
			}
		};
	}

	private Map<String, ?> _toMap(String additionalInfo) throws JSONException {
		if (Validator.isBlank(additionalInfo)) {
			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(additionalInfo);

		return jsonObject.toMap();
	}

	private OrderByComparator
		<com.liferay.portal.security.audit.storage.model.AuditEvent>
			_toOrderByComparator(Sort[] sorts) {

		if (ArrayUtil.isEmpty(sorts)) {
			return new AuditEventCreateDateComparator();
		}

		List<Object> objects = new ArrayList<>();

		for (Sort sort : sorts) {
			if (Objects.equals(sort.getFieldName(), "accountId")) {
				objects.add("accountEntryId");
				objects.add(!sort.isReverse());
			}
			else if (Objects.equals(sort.getFieldName(), "contextName")) {
				objects.add("contextName");
				objects.add(!sort.isReverse());
			}
			else if (Objects.equals(sort.getFieldName(), "dateCreated")) {
				objects.add("createDate");
				objects.add(!sort.isReverse());
			}
			else if (Objects.equals(sort.getFieldName(), "eventType")) {
				objects.add("eventType");
				objects.add(!sort.isReverse());
			}
		}

		if (objects.isEmpty()) {
			return new AuditEventCreateDateComparator();
		}

		return OrderByComparatorFactoryUtil.create(
			AuditEventTable.INSTANCE.getTableName(), objects.toArray());
	}

	@Reference
	private AuditEventService _auditEventService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
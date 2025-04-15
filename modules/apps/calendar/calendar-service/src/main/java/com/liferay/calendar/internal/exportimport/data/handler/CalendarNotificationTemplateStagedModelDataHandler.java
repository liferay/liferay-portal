/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.exportimport.data.handler;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarNotificationTemplate;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarNotificationTemplateLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Daniel Kocsis
 */
@Component(
	property = "jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR_ADMIN,
	service = StagedModelDataHandler.class
)
public class CalendarNotificationTemplateStagedModelDataHandler
	extends BaseStagedModelDataHandler<CalendarNotificationTemplate> {

	public static final String[] CLASS_NAMES = {
		CalendarNotificationTemplate.class.getName()
	};

	@Override
	public void deleteStagedModel(
			CalendarNotificationTemplate calendarNotificationTemplate)
		throws PortalException {

		_calendarNotificationTemplateLocalService.
			deleteCalendarNotificationTemplate(calendarNotificationTemplate);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		CalendarNotificationTemplate calendarNotificationTemplate =
			fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (calendarNotificationTemplate != null) {
			deleteStagedModel(calendarNotificationTemplate);
		}
	}

	@Override
	public CalendarNotificationTemplate fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarNotificationTemplateLocalService.
			fetchCalendarNotificationTemplateByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<CalendarNotificationTemplate>
		fetchStagedModelsByUuidAndCompanyId(String uuid, long companyId) {

		return _calendarNotificationTemplateLocalService.
			getCalendarNotificationTemplatesByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator
					<CalendarNotificationTemplate>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			CalendarNotificationTemplate calendarNotificationTemplate)
		throws Exception {

		Calendar calendar = _calendarLocalService.getCalendar(
			calendarNotificationTemplate.getCalendarId());

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, calendarNotificationTemplate, calendar,
			PortletDataContext.REFERENCE_TYPE_STRONG);

		String body =
			_calendarNotificationTemplateExportImportContentProcessor.
				replaceExportContentReferences(
					portletDataContext, calendarNotificationTemplate,
					calendarNotificationTemplate.getBody(),
					portletDataContext.getBooleanParameter(
						"calendar", "referenced-content"),
					true);

		calendarNotificationTemplate.setBody(body);

		Element calendarNotificationTemplateElement =
			portletDataContext.getExportDataElement(
				calendarNotificationTemplate);

		portletDataContext.addClassedModel(
			calendarNotificationTemplateElement,
			ExportImportPathUtil.getModelPath(calendarNotificationTemplate),
			calendarNotificationTemplate);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			CalendarNotificationTemplate calendarNotificationTemplate)
		throws Exception {

		long userId = portletDataContext.getUserId(
			calendarNotificationTemplate.getUserUuid());

		Map<Long, Long> calendarIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Calendar.class);

		long calendarId = MapUtil.getLong(
			calendarIds, calendarNotificationTemplate.getCalendarId(),
			calendarNotificationTemplate.getCalendarId());

		NotificationType notificationType = NotificationType.parse(
			calendarNotificationTemplate.getNotificationType());
		NotificationTemplateType notificationTemplateType =
			NotificationTemplateType.parse(
				calendarNotificationTemplate.getNotificationTemplateType());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			calendarNotificationTemplate);

		CalendarNotificationTemplate importedCalendarNotificationTemplate =
			null;

		String body =
			_calendarNotificationTemplateExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, calendarNotificationTemplate,
					calendarNotificationTemplate.getBody());

		if (portletDataContext.isDataStrategyMirror()) {
			CalendarNotificationTemplate existingCalendarNotificationTemplate =
				fetchStagedModelByUuidAndGroupId(
					calendarNotificationTemplate.getUuid(),
					portletDataContext.getScopeGroupId());

			if (existingCalendarNotificationTemplate == null) {
				serviceContext.setUuid(calendarNotificationTemplate.getUuid());

				importedCalendarNotificationTemplate =
					_calendarNotificationTemplateLocalService.
						addCalendarNotificationTemplate(
							userId, calendarId, notificationType,
							calendarNotificationTemplate.
								getNotificationTypeSettings(),
							notificationTemplateType,
							calendarNotificationTemplate.getSubject(), body,
							serviceContext);
			}
			else {
				importedCalendarNotificationTemplate =
					_calendarNotificationTemplateLocalService.
						updateCalendarNotificationTemplate(
							existingCalendarNotificationTemplate.
								getCalendarNotificationTemplateId(),
							calendarNotificationTemplate.
								getNotificationTypeSettings(),
							calendarNotificationTemplate.getSubject(), body,
							serviceContext);
			}
		}
		else {
			importedCalendarNotificationTemplate =
				_calendarNotificationTemplateLocalService.
					addCalendarNotificationTemplate(
						userId, calendarId, notificationType,
						calendarNotificationTemplate.
							getNotificationTypeSettings(),
						notificationTemplateType,
						calendarNotificationTemplate.getSubject(), body,
						serviceContext);
		}

		portletDataContext.importClassedModel(
			calendarNotificationTemplate, importedCalendarNotificationTemplate);
	}

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.CalendarNotificationTemplate)"
	)
	private ExportImportContentProcessor<String>
		_calendarNotificationTemplateExportImportContentProcessor;

	@Reference
	private CalendarNotificationTemplateLocalService
		_calendarNotificationTemplateLocalService;

}
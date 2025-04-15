/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.exportimport.data.handler;

import com.liferay.calendar.constants.CalendarBookingConstants;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
public class CalendarBookingStagedModelDataHandler
	extends BaseStagedModelDataHandler<CalendarBooking> {

	public static final String[] CLASS_NAMES = {
		CalendarBooking.class.getName()
	};

	@Override
	public void deleteStagedModel(CalendarBooking calendarBooking)
		throws PortalException {

		_calendarBookingLocalService.deleteCalendarBooking(calendarBooking);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		CalendarBooking calendarBooking = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (calendarBooking != null) {
			deleteStagedModel(calendarBooking);
		}
	}

	@Override
	public CalendarBooking fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarBookingLocalService.
			fetchCalendarBookingByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<CalendarBooking> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _calendarBookingLocalService.
			getCalendarBookingsByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<CalendarBooking>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(CalendarBooking calendarBooking) {
		return calendarBooking.getTitleCurrentValue();
	}

	@Override
	public int[] getExportableStatuses() {
		return _EXPORTABLE_STATUSES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			CalendarBooking calendarBooking)
		throws Exception {

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, calendarBooking, calendarBooking.getCalendar(),
			PortletDataContext.REFERENCE_TYPE_STRONG);

		if (!calendarBooking.isMasterBooking()) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, calendarBooking,
				calendarBooking.getParentCalendarBooking(),
				PortletDataContext.REFERENCE_TYPE_PARENT);
		}

		Element calendarBookingElement =
			portletDataContext.getExportDataElement(calendarBooking);

		for (CalendarBooking childCalendarBooking :
				calendarBooking.getChildCalendarBookings()) {

			if (childCalendarBooking.isMasterBooking()) {
				continue;
			}

			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, calendarBooking,
				childCalendarBooking.getCalendar(),
				PortletDataContext.REFERENCE_TYPE_STRONG);

			Element childElement = calendarBookingElement.addElement("child");

			childElement.addAttribute(
				"calendarId",
				String.valueOf(childCalendarBooking.getCalendarId()));
		}

		portletDataContext.addClassedModel(
			calendarBookingElement,
			ExportImportPathUtil.getModelPath(calendarBooking),
			calendarBooking);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long calendarBookingId)
		throws Exception {

		CalendarBooking existingCalendarBooking = fetchMissingReference(
			uuid, groupId);

		if (existingCalendarBooking == null) {
			return;
		}

		Map<Long, Long> calendarBookingIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				CalendarBooking.class);

		calendarBookingIds.put(
			calendarBookingId, existingCalendarBooking.getCalendarBookingId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			CalendarBooking calendarBooking)
		throws Exception {

		long userId = portletDataContext.getUserId(
			calendarBooking.getUserUuid());

		Map<Long, Long> calendarIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Calendar.class);

		long calendarId = MapUtil.getLong(
			calendarIds, calendarBooking.getCalendarId(),
			calendarBooking.getCalendarId());

		long parentCalendarBookingId =
			CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT;

		long[] childCalendarIds = new long[0];

		if (!calendarBooking.isMasterBooking()) {
			Map<Long, Long> calendarBookingIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					CalendarBooking.class);

			parentCalendarBookingId = MapUtil.getLong(
				calendarBookingIds,
				calendarBooking.getParentCalendarBookingId(),
				calendarBooking.getParentCalendarBookingId());
		}
		else {
			childCalendarIds = _getChildCalendarIds(
				portletDataContext, calendarIds, calendarBooking);
		}

		long recurringCalendarBookingId =
			CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT;

		if (!calendarBooking.isMasterRecurringBooking()) {
			recurringCalendarBookingId =
				calendarBooking.getRecurringCalendarBookingId();
		}

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			calendarBooking);

		CalendarBooking importedCalendarBooking = null;

		if (portletDataContext.isDataStrategyMirror()) {
			CalendarBooking existingCalendarBooking =
				fetchStagedModelByUuidAndGroupId(
					calendarBooking.getUuid(),
					portletDataContext.getScopeGroupId());

			if (existingCalendarBooking == null) {
				serviceContext.setUuid(calendarBooking.getUuid());

				importedCalendarBooking =
					_calendarBookingLocalService.addCalendarBooking(
						userId, calendarId, childCalendarIds,
						parentCalendarBookingId, recurringCalendarBookingId,
						calendarBooking.getTitleMap(),
						calendarBooking.getDescriptionMap(),
						calendarBooking.getLocation(),
						calendarBooking.getStartTime(),
						calendarBooking.getEndTime(),
						calendarBooking.isAllDay(),
						calendarBooking.getRecurrence(),
						calendarBooking.getFirstReminder(),
						calendarBooking.getFirstReminderType(),
						calendarBooking.getSecondReminder(),
						calendarBooking.getSecondReminderType(),
						serviceContext);
			}
			else {
				importedCalendarBooking =
					_calendarBookingLocalService.updateCalendarBooking(
						userId, existingCalendarBooking.getCalendarBookingId(),
						calendarId, childCalendarIds,
						calendarBooking.getTitleMap(),
						calendarBooking.getDescriptionMap(),
						calendarBooking.getLocation(),
						calendarBooking.getStartTime(),
						calendarBooking.getEndTime(),
						calendarBooking.isAllDay(),
						calendarBooking.getRecurrence(),
						calendarBooking.getFirstReminder(),
						calendarBooking.getFirstReminderType(),
						calendarBooking.getSecondReminder(),
						calendarBooking.getSecondReminderType(),
						serviceContext);
			}
		}
		else {
			importedCalendarBooking =
				_calendarBookingLocalService.addCalendarBooking(
					userId, calendarId, childCalendarIds,
					parentCalendarBookingId, recurringCalendarBookingId,
					calendarBooking.getTitleMap(),
					calendarBooking.getDescriptionMap(),
					calendarBooking.getLocation(),
					calendarBooking.getStartTime(),
					calendarBooking.getEndTime(), calendarBooking.isAllDay(),
					calendarBooking.getRecurrence(),
					calendarBooking.getFirstReminder(),
					calendarBooking.getFirstReminderType(),
					calendarBooking.getSecondReminder(),
					calendarBooking.getSecondReminderType(), serviceContext);
		}

		importedCalendarBooking = _calendarBookingLocalService.updateStatus(
			userId, importedCalendarBooking, calendarBooking.getStatus(),
			serviceContext);

		// The root discussion message is not automatically imported when
		// importing a calendar booking

		List<Element> mbMessageElements =
			portletDataContext.getReferenceElements(
				calendarBooking, MBMessage.class);

		if (ListUtil.isNotEmpty(mbMessageElements)) {
			_mbMessageLocalService.addDiscussionMessage(
				userId, importedCalendarBooking.getUserName(),
				importedCalendarBooking.getGroupId(),
				CalendarBooking.class.getName(),
				importedCalendarBooking.getCalendarBookingId(),
				WorkflowConstants.ACTION_PUBLISH);
		}

		portletDataContext.importClassedModel(
			calendarBooking, importedCalendarBooking);
	}

	@Override
	protected void doRestoreStagedModel(
			PortletDataContext portletDataContext,
			CalendarBooking calendarBooking)
		throws Exception {

		CalendarBooking existingBooking = fetchStagedModelByUuidAndGroupId(
			calendarBooking.getUuid(), portletDataContext.getScopeGroupId());

		if ((existingBooking == null) || !existingBooking.isInTrash()) {
			return;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			CalendarBooking.class.getName());

		if (trashHandler.isRestorable(existingBooking.getCalendarBookingId())) {
			trashHandler.restoreTrashEntry(
				portletDataContext.getUserId(calendarBooking.getUserUuid()),
				existingBooking.getCalendarBookingId());
		}
	}

	private long[] _getChildCalendarIds(
		PortletDataContext portletDataContext, Map<Long, Long> calendarIds,
		CalendarBooking calendarBooking) {

		long[] childCalendarIds = new long[0];

		Element calendarBookingElement =
			portletDataContext.getImportDataElement(calendarBooking);

		List<Element> childElements = calendarBookingElement.elements("child");

		for (Element childElement : childElements) {
			long calendarId = Long.valueOf(
				childElement.attributeValue("calendarId"));

			calendarId = MapUtil.getLong(calendarIds, calendarId, calendarId);

			childCalendarIds = ArrayUtil.append(childCalendarIds, calendarId);
		}

		return childCalendarIds;
	}

	private static final int[] _EXPORTABLE_STATUSES = {
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_DENIED,
		CalendarBookingWorkflowConstants.STATUS_MAYBE
	};

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

}
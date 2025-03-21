/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.exportimport.data.handler;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
public class CalendarStagedModelDataHandler
	extends BaseStagedModelDataHandler<Calendar> {

	public static final String[] CLASS_NAMES = {Calendar.class.getName()};

	@Override
	public void deleteStagedModel(Calendar calendar) throws PortalException {
		_calendarLocalService.deleteCalendar(calendar);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Calendar calendar = fetchStagedModelByUuidAndGroupId(uuid, groupId);

		if (calendar != null) {
			deleteStagedModel(calendar);
		}
	}

	@Override
	public Calendar fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarLocalService.fetchCalendarByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public List<Calendar> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _calendarLocalService.getCalendarsByUuidAndCompanyId(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new StagedModelModifiedDateComparator<Calendar>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(Calendar calendar) {
		return calendar.getNameCurrentValue();
	}

	@Override
	public boolean validateReference(
		PortletDataContext portletDataContext, Element referenceElement) {

		validateMissingGroupReference(portletDataContext, referenceElement);

		long companyId = GetterUtil.getLong(
			referenceElement.attributeValue("company-id"));

		String uuid = referenceElement.attributeValue("uuid");

		Map<Long, Long> groupIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Group.class);

		long groupId = GetterUtil.getLong(
			referenceElement.attributeValue("group-id"));

		groupId = MapUtil.getLong(groupIds, groupId);

		String displayName = referenceElement.attributeValue("display-name");

		return _validateMissingReference(companyId, uuid, groupId, displayName);
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, Calendar calendar)
		throws Exception {

		CalendarResource calendarResource = calendar.getCalendarResource();

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, calendar, calendarResource,
			PortletDataContext.REFERENCE_TYPE_STRONG);

		String calendarName = calendar.getName(LocaleUtil.getSiteDefault());

		Group group = _groupLocalService.getGroup(calendar.getGroupId());

		Element calendarElement = portletDataContext.getExportDataElement(
			calendar);

		if (!Objects.equals(calendarName, group.getDescriptiveName()) ||
			!calendarResource.isGroup()) {

			calendarElement.addAttribute("keepCalendarName", "true");
		}

		portletDataContext.addClassedModel(
			calendarElement, ExportImportPathUtil.getModelPath(calendar),
			calendar);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long calendarId)
		throws Exception {

		Calendar existingCalendar = fetchMissingReference(uuid, groupId);

		if (existingCalendar == null) {
			return;
		}

		Map<Long, Long> calendarIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Calendar.class);

		calendarIds.put(calendarId, existingCalendar.getCalendarId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, Calendar calendar)
		throws Exception {

		long userId = portletDataContext.getUserId(calendar.getUserUuid());

		Map<Long, Long> calendarResourceIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				CalendarResource.class);

		long calendarResourceId = MapUtil.getLong(
			calendarResourceIds, calendar.getCalendarResourceId(),
			calendar.getCalendarResourceId());

		Map<Locale, String> calendarNameMap = _getCalendarNameMap(
			portletDataContext, calendar);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			calendar);

		Calendar importedCalendar = null;

		if (portletDataContext.isDataStrategyMirror()) {
			Calendar existingCalendar = fetchStagedModelByUuidAndGroupId(
				calendar.getUuid(), portletDataContext.getScopeGroupId());

			if (existingCalendar == null) {
				serviceContext.setUuid(calendar.getUuid());

				importedCalendar = _calendarLocalService.addCalendar(
					userId, portletDataContext.getScopeGroupId(),
					calendarResourceId, calendarNameMap,
					calendar.getDescriptionMap(), calendar.getTimeZoneId(),
					calendar.getColor(), calendar.isDefaultCalendar(),
					calendar.isEnableComments(), calendar.isEnableRatings(),
					serviceContext);
			}
			else {
				importedCalendar = _calendarLocalService.updateCalendar(
					existingCalendar.getCalendarId(), calendarNameMap,
					calendar.getDescriptionMap(), calendar.getTimeZoneId(),
					calendar.getColor(), calendar.isDefaultCalendar(),
					calendar.isEnableComments(), calendar.isEnableRatings(),
					serviceContext);
			}
		}
		else {
			importedCalendar = _calendarLocalService.addCalendar(
				userId, portletDataContext.getScopeGroupId(),
				calendarResourceId, calendarNameMap,
				calendar.getDescriptionMap(), calendar.getTimeZoneId(),
				calendar.getColor(), calendar.isDefaultCalendar(),
				calendar.isEnableComments(), calendar.isEnableRatings(),
				serviceContext);
		}

		portletDataContext.importClassedModel(calendar, importedCalendar);
	}

	private Calendar _fetchExistingCalendar(
		long companyId, long groupId, String name) {

		return _calendarLocalService.fetchGroupCalendar(
			companyId, groupId, name);
	}

	private Map<Locale, String> _getCalendarNameMap(
			PortletDataContext portletDataContext, Calendar calendar)
		throws Exception {

		Element element = portletDataContext.getImportDataStagedModelElement(
			calendar);

		boolean keepCalendarName = GetterUtil.getBoolean(
			element.attributeValue("keepCalendarName"));

		if (keepCalendarName) {
			return calendar.getNameMap();
		}

		Group scopeGroup = _groupLocalService.getGroup(
			portletDataContext.getScopeGroupId());

		return _localization.populateLocalizationMap(
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), scopeGroup.getDescriptiveName()
			).build(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			scopeGroup.getGroupId());
	}

	private boolean _validateMissingReference(
		long companyId, String uuid, long groupId, String name) {

		Calendar existingStagedModel = fetchMissingReference(uuid, groupId);

		if (existingStagedModel == null) {
			existingStagedModel = _fetchExistingCalendar(
				companyId, groupId, name);
		}

		if (existingStagedModel == null) {
			return false;
		}

		return true;
	}

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Localization _localization;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.exportimport.data.handler;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.exception.DuplicateCalendarResourceException;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelModifiedDateComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
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
public class CalendarResourceStagedModelDataHandler
	extends BaseStagedModelDataHandler<CalendarResource> {

	public static final String[] CLASS_NAMES = {
		CalendarResource.class.getName()
	};

	@Override
	public void deleteStagedModel(CalendarResource calendarResource)
		throws PortalException {

		_calendarResourceLocalService.deleteCalendarResource(calendarResource);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		CalendarResource calendarResource = fetchStagedModelByUuidAndGroupId(
			uuid, groupId);

		if (calendarResource != null) {
			deleteStagedModel(calendarResource);
		}
	}

	@Override
	public CalendarResource fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarResourceLocalService.
			fetchCalendarResourceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<CalendarResource> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _calendarResourceLocalService.
			getCalendarResourcesByUuidAndCompanyId(
				uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new StagedModelModifiedDateComparator<CalendarResource>());
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(CalendarResource calendarResource) {
		return calendarResource.getNameCurrentValue();
	}

	@Override
	protected boolean countStagedModel(
		PortletDataContext portletDataContext,
		CalendarResource calendarResource) {

		if (calendarResource.getClassNameId() == _portal.getClassNameId(
				CalendarResource.class)) {

			return true;
		}

		return false;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			CalendarResource calendarResource)
		throws Exception {

		Element calendarResourceElement =
			portletDataContext.getExportDataElement(calendarResource);

		for (Calendar calendar : calendarResource.getCalendars()) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, calendarResource, calendar,
				PortletDataContext.REFERENCE_TYPE_STRONG);
		}

		if (calendarResource.getClassNameId() == _portal.getClassNameId(
				User.class)) {

			User user = _userLocalService.getUser(
				calendarResource.getClassPK());

			portletDataContext.addReferenceElement(
				calendarResource, calendarResourceElement, user,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY_DISPOSABLE, true);
		}

		String calendarResourceName = calendarResource.getName(
			LocaleUtil.getSiteDefault());

		Group group = _groupLocalService.getGroup(
			calendarResource.getGroupId());

		if (!Objects.equals(calendarResourceName, group.getDescriptiveName()) ||
			!calendarResource.isGroup()) {

			calendarResourceElement.addAttribute(
				"keepCalendarResourceName", "true");
		}

		portletDataContext.addClassedModel(
			calendarResourceElement,
			ExportImportPathUtil.getModelPath(calendarResource),
			calendarResource);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			CalendarResource calendarResource)
		throws Exception {

		long userId = portletDataContext.getUserId(
			calendarResource.getUserUuid());

		long classPK = _getClassPK(
			portletDataContext, calendarResource, userId);

		Map<Locale, String> calendarResourceNameMap =
			_getCalendarResourceNameMap(portletDataContext, calendarResource);

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			calendarResource);

		CalendarResource importedCalendarResource = null;

		if (portletDataContext.isDataStrategyMirror()) {
			CalendarResource existingCalendarResource =
				fetchStagedModelByUuidAndGroupId(
					calendarResource.getUuid(),
					portletDataContext.getScopeGroupId());

			if (existingCalendarResource == null) {
				existingCalendarResource =
					_calendarResourceLocalService.fetchCalendarResource(
						calendarResource.getClassNameId(), classPK);
			}

			if (existingCalendarResource == null) {
				serviceContext.setUuid(calendarResource.getUuid());

				importedCalendarResource =
					_calendarResourceLocalService.addCalendarResource(
						userId, portletDataContext.getScopeGroupId(),
						calendarResource.getClassNameId(), classPK,
						calendarResource.getClassUuid(),
						_getUniqueCalendarResourceCode(
							portletDataContext, calendarResource),
						calendarResourceNameMap,
						calendarResource.getDescriptionMap(),
						calendarResource.isActive(), serviceContext);
			}
			else {
				importedCalendarResource =
					_calendarResourceLocalService.updateCalendarResource(
						existingCalendarResource.getCalendarResourceId(),
						calendarResourceNameMap,
						calendarResource.getDescriptionMap(),
						calendarResource.isActive(), serviceContext);
			}
		}
		else {
			try {
				importedCalendarResource =
					_calendarResourceLocalService.addCalendarResource(
						userId, portletDataContext.getScopeGroupId(),
						calendarResource.getClassNameId(), classPK,
						calendarResource.getClassUuid(),
						_getUniqueCalendarResourceCode(
							portletDataContext, calendarResource),
						calendarResourceNameMap,
						calendarResource.getDescriptionMap(),
						calendarResource.isActive(), serviceContext);
			}
			catch (DuplicateCalendarResourceException
						duplicateCalendarResourceException) {

				if (_log.isDebugEnabled()) {
					_log.debug(duplicateCalendarResourceException);
				}

				// The calendar resource for the site's default calendar is
				// always generated beforehand, so we only want to add it once

				importedCalendarResource =
					_calendarResourceLocalService.fetchCalendarResource(
						calendarResource.getClassNameId(), classPK);
			}
		}

		_updateCalendars(
			portletDataContext, calendarResource, importedCalendarResource);

		portletDataContext.importClassedModel(
			calendarResource, importedCalendarResource);
	}

	private Map<Locale, String> _getCalendarResourceNameMap(
			PortletDataContext portletDataContext,
			CalendarResource calendarResource)
		throws Exception {

		Element element = portletDataContext.getImportDataStagedModelElement(
			calendarResource);

		boolean keepCalendarResourceName = GetterUtil.getBoolean(
			element.attributeValue("keepCalendarResourceName"));

		if (keepCalendarResourceName) {
			return calendarResource.getNameMap();
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

	private long _getClassPK(
		PortletDataContext portletDataContext,
		CalendarResource calendarResource, long userId) {

		long classPK = 0;

		if (calendarResource.getClassNameId() == _portal.getClassNameId(
				Group.class)) {

			classPK = portletDataContext.getScopeGroupId();
		}
		else if (calendarResource.getClassNameId() == _portal.getClassNameId(
					User.class)) {

			classPK = userId;
		}

		return classPK;
	}

	private String _getUniqueCalendarResourceCode(
			PortletDataContext portletDataContext,
			CalendarResource calendarResource)
		throws Exception {

		String code = calendarResource.getCode();

		for (int i = 1;; i++) {
			CalendarResource existingCalendarResource =
				_calendarResourceLocalService.fetchCalendarResource(
					portletDataContext.getScopeGroupId(), code);

			if (existingCalendarResource == null) {
				break;
			}

			code = code.concat(String.valueOf(i));
		}

		return code;
	}

	private void _updateCalendars(
		PortletDataContext portletDataContext,
		CalendarResource calendarResource,
		CalendarResource importedCalendarResource) {

		Map<Long, Long> calendarIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Calendar.class);

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				calendarResource, Calendar.class);

		for (Element referenceElement : referenceElements) {
			long calendarId = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			Calendar calendar = _calendarLocalService.fetchCalendar(
				MapUtil.getLong(calendarIds, calendarId));

			if (calendar != null) {
				calendar.setCalendarResourceId(
					importedCalendarResource.getCalendarResourceId());

				_calendarLocalService.updateCalendar(calendar);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarResourceStagedModelDataHandler.class);

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference
	private CalendarResourceLocalService _calendarResourceLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
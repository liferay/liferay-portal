/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.calendar.configuration.CalendarServiceConfigurationValues;
import com.liferay.calendar.exception.CalendarResourceCodeException;
import com.liferay.calendar.exception.CalendarResourceNameException;
import com.liferay.calendar.exception.DuplicateCalendarResourceException;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.base.CalendarResourceLocalServiceBaseImpl;
import com.liferay.calendar.service.persistence.CalendarPersistence;
import com.liferay.calendar.util.comparator.CalendarResourceCodeComparator;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 * @author Bruno Basto
 * @author Marcellus Tavares
 * @author Andrea Di Giorgi
 */
@Component(
	property = "model.class.name=com.liferay.calendar.model.CalendarResource",
	service = AopService.class
)
public class CalendarResourceLocalServiceImpl
	extends CalendarResourceLocalServiceBaseImpl {

	@Override
	public CalendarResource addCalendarResource(
			long userId, long groupId, long classNameId, long classPK,
			String classUuid, String code, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		// Calendar resource

		User user = _userLocalService.getUser(userId);

		long calendarResourceId = counterLocalService.increment();

		if (classNameId == _classNameLocalService.getClassNameId(
				CalendarResource.class)) {

			classPK = calendarResourceId;
		}

		if (CalendarServiceConfigurationValues.
				CALENDAR_RESOURCE_FORCE_AUTOGENERATE_CODE ||
			Validator.isNull(code)) {

			code = String.valueOf(calendarResourceId);
		}
		else {
			code = StringUtil.toUpperCase(StringUtil.trim(code));
		}

		Date date = new Date();

		_validate(groupId, classNameId, classPK, code, nameMap);

		CalendarResource calendarResource = calendarResourcePersistence.create(
			calendarResourceId);

		calendarResource.setUuid(serviceContext.getUuid());
		calendarResource.setGroupId(groupId);
		calendarResource.setCompanyId(user.getCompanyId());
		calendarResource.setUserId(user.getUserId());
		calendarResource.setUserName(user.getFullName());
		calendarResource.setCreateDate(serviceContext.getCreateDate(date));
		calendarResource.setModifiedDate(serviceContext.getModifiedDate(date));
		calendarResource.setClassNameId(classNameId);
		calendarResource.setClassPK(classPK);
		calendarResource.setClassUuid(classUuid);
		calendarResource.setCode(code);
		calendarResource.setNameMap(nameMap);
		calendarResource.setDescriptionMap(descriptionMap);
		calendarResource.setActive(active);

		calendarResource = calendarResourcePersistence.update(calendarResource);

		// Resources

		_resourceLocalService.addModelResources(
			calendarResource, serviceContext);

		// Calendar

		if (!ExportImportThreadLocal.isImportInProcess()) {
			ServiceContext calendarServiceContext = new ServiceContext();

			calendarServiceContext.setAddGroupPermissions(true);
			calendarServiceContext.setAddGuestPermissions(true);

			_calendarLocalService.addCalendar(
				userId, calendarResource.getGroupId(), calendarResourceId,
				nameMap, descriptionMap, calendarResource.getTimeZoneId(),
				CalendarServiceConfigurationValues.CALENDAR_COLOR_DEFAULT, true,
				false, false, calendarServiceContext);
		}

		// Asset

		updateAsset(
			calendarResource.getUserId(), calendarResource,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetPriority());

		return calendarResource;
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public CalendarResource deleteCalendarResource(
			CalendarResource calendarResource)
		throws PortalException {

		// Calendar resource

		calendarResourcePersistence.remove(calendarResource);

		// Resources

		_resourceLocalService.deleteResource(
			calendarResource, ResourceConstants.SCOPE_INDIVIDUAL);

		// Calendars

		List<Calendar> calendars = _calendarPersistence.findByG_C(
			calendarResource.getGroupId(),
			calendarResource.getCalendarResourceId());

		for (Calendar calendar : calendars) {
			calendar.setDefaultCalendar(false);

			_calendarLocalService.deleteCalendar(calendar);
		}

		return calendarResource;
	}

	@Override
	public CalendarResource deleteCalendarResource(long calendarResourceId)
		throws PortalException {

		CalendarResource calendarResource =
			calendarResourcePersistence.findByPrimaryKey(calendarResourceId);

		return calendarResourceLocalService.deleteCalendarResource(
			calendarResource);
	}

	@Override
	public void deleteCalendarResources(long groupId) throws PortalException {
		List<CalendarResource> calendarResources =
			calendarResourcePersistence.findByGroupId(groupId);

		for (CalendarResource calendarResource : calendarResources) {
			calendarResourceLocalService.deleteCalendarResource(
				calendarResource);
		}
	}

	@Override
	public CalendarResource fetchCalendarResource(
		long classNameId, long classPK) {

		return calendarResourcePersistence.fetchByC_C(classNameId, classPK);
	}

	@Override
	public CalendarResource fetchCalendarResource(long groupId, String code) {
		return calendarResourcePersistence.fetchByG_C_First(
			groupId, code, CalendarResourceCodeComparator.getInstance(false));
	}

	@Override
	public CalendarResource getCalendarResource(long calendarResourceId)
		throws PortalException {

		return calendarResourcePersistence.findByPrimaryKey(calendarResourceId);
	}

	@Override
	public List<CalendarResource> getCalendarResources(long groupId) {
		return calendarResourcePersistence.findByGroupId(groupId);
	}

	@Override
	public List<CalendarResource> searchByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active, boolean andOperator, int start, int end,
		OrderByComparator<CalendarResource> orderByComparator) {

		return calendarResourceFinder.findByKeywords(
			companyId, groupIds, classNameIds, keywords, active, start, end,
			orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active) {

		return calendarResourceFinder.countByKeywords(
			companyId, groupIds, classNameIds, keywords, active);
	}

	@Override
	public void updateAsset(
			long userId, CalendarResource calendarResource,
			long[] assetCategoryIds, String[] assetTagNames, Double priority)
		throws PortalException {

		_assetEntryLocalService.updateEntry(
			userId, calendarResource.getGroupId(),
			calendarResource.getCreateDate(),
			calendarResource.getModifiedDate(),
			CalendarResource.class.getName(),
			calendarResource.getCalendarResourceId(),
			calendarResource.getUuid(), 0, assetCategoryIds, assetTagNames,
			true, true, null, null, calendarResource.getCreateDate(), null,
			ContentTypes.TEXT, calendarResource.getName(),
			calendarResource.getDescription(), null, null, null, 0, 0,
			priority);
	}

	@Override
	public CalendarResource updateCalendarResource(
			long calendarResourceId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		// Calendar resource

		_validate(nameMap);

		CalendarResource calendarResource =
			calendarResourcePersistence.findByPrimaryKey(calendarResourceId);

		calendarResource.setModifiedDate(serviceContext.getModifiedDate(null));
		calendarResource.setNameMap(nameMap);
		calendarResource.setDescriptionMap(descriptionMap);
		calendarResource.setActive(active);

		calendarResource = calendarResourcePersistence.update(calendarResource);

		// Calendar

		List<Calendar> calendars = _calendarPersistence.findByG_C(
			calendarResource.getGroupId(),
			calendarResource.getCalendarResourceId());

		Indexer<Calendar> indexer = IndexerRegistryUtil.getIndexer(
			Calendar.class);

		for (Calendar calendar : calendars) {
			indexer.reindex(calendar);
		}

		// Asset

		updateAsset(
			calendarResource.getUserId(), calendarResource,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetPriority());

		return calendarResource;
	}

	private void _validate(
			long groupId, long classNameId, long classPK, String code,
			Map<Locale, String> nameMap)
		throws PortalException {

		_validate(nameMap);

		if (Validator.isNull(code) || (code.indexOf(CharPool.SPACE) != -1)) {
			throw new CalendarResourceCodeException();
		}

		if (calendarResourcePersistence.countByG_C(groupId, code) > 0) {
			throw new DuplicateCalendarResourceException();
		}

		CalendarResource calendarResource =
			calendarResourcePersistence.fetchByC_C(classNameId, classPK);

		if (calendarResource != null) {
			throw new DuplicateCalendarResourceException();
		}
	}

	private void _validate(Map<Locale, String> nameMap) throws PortalException {
		Locale locale = LocaleUtil.getSiteDefault();

		if (nameMap.isEmpty() || Validator.isNull(nameMap.get(locale))) {
			throw new CalendarResourceNameException();
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference
	private CalendarPersistence _calendarPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.exception.NoSuchNotificationTemplateException;
import com.liferay.calendar.model.CalendarNotificationTemplate;
import com.liferay.calendar.model.CalendarNotificationTemplateTable;
import com.liferay.calendar.model.impl.CalendarNotificationTemplateImpl;
import com.liferay.calendar.model.impl.CalendarNotificationTemplateModelImpl;
import com.liferay.calendar.service.persistence.CalendarNotificationTemplatePersistence;
import com.liferay.calendar.service.persistence.CalendarNotificationTemplateUtil;
import com.liferay.calendar.service.persistence.impl.constants.CalendarPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the calendar notification template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Lundgren
 * @generated
 */
@Component(service = CalendarNotificationTemplatePersistence.class)
public class CalendarNotificationTemplatePersistenceImpl
	extends BasePersistenceImpl
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
	implements CalendarNotificationTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CalendarNotificationTemplateUtil</code> to access the calendar notification template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CalendarNotificationTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the calendar notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendar notification templates
	 * @param end the upper bound of the range of calendar notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar notification templates
	 */
	@Override
	public List<CalendarNotificationTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template
	 * @throws NoSuchNotificationTemplateException if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByUuid_First(
			String uuid,
			OrderByComparator<CalendarNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template, or <code>null</code> if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByUuid_First(
		String uuid,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the calendar notification templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of calendar notification templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendar notification templates
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the calendar notification template where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar notification template
	 * @throws NoSuchNotificationTemplateException if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByUUID_G(String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the calendar notification template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar notification template, or <code>null</code> if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the calendar notification template where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the calendar notification template that was removed
	 */
	@Override
	public CalendarNotificationTemplate removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		CalendarNotificationTemplate calendarNotificationTemplate =
			findByUUID_G(uuid, groupId);

		return remove(calendarNotificationTemplate);
	}

	/**
	 * Returns the number of calendar notification templates where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching calendar notification templates
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the calendar notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendar notification templates
	 * @param end the upper bound of the range of calendar notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar notification templates
	 */
	@Override
	public List<CalendarNotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template
	 * @throws NoSuchNotificationTemplateException if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CalendarNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template, or <code>null</code> if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the calendar notification templates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of calendar notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching calendar notification templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByCalendarId;

	/**
	 * Returns an ordered range of all the calendar notification templates where calendarId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param start the lower bound of the range of calendar notification templates
	 * @param end the upper bound of the range of calendar notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar notification templates
	 */
	@Override
	public List<CalendarNotificationTemplate> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCalendarId.find(
			finderCache, new Object[] {calendarId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template
	 * @throws NoSuchNotificationTemplateException if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByCalendarId_First(
			long calendarId,
			OrderByComparator<CalendarNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByCalendarId.findFirst(
			finderCache, new Object[] {calendarId}, orderByComparator);
	}

	/**
	 * Returns the first calendar notification template in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar notification template, or <code>null</code> if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByCalendarId_First(
		long calendarId,
		OrderByComparator<CalendarNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByCalendarId.fetchFirst(
			finderCache, new Object[] {calendarId}, orderByComparator);
	}

	/**
	 * Removes all the calendar notification templates where calendarId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 */
	@Override
	public void removeByCalendarId(long calendarId) {
		_collectionPersistenceFinderByCalendarId.remove(
			finderCache, new Object[] {calendarId});
	}

	/**
	 * Returns the number of calendar notification templates where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the number of matching calendar notification templates
	 */
	@Override
	public int countByCalendarId(long calendarId) {
		return _collectionPersistenceFinderByCalendarId.count(
			finderCache, new Object[] {calendarId});
	}

	private UniquePersistenceFinder
		<CalendarNotificationTemplate, NoSuchNotificationTemplateException>
			_uniquePersistenceFinderByC_NT_NTT;

	/**
	 * Returns the calendar notification template where calendarId = &#63; and notificationType = &#63; and notificationTemplateType = &#63; or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param calendarId the calendar ID
	 * @param notificationType the notification type
	 * @param notificationTemplateType the notification template type
	 * @return the matching calendar notification template
	 * @throws NoSuchNotificationTemplateException if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByC_NT_NTT(
			long calendarId, String notificationType,
			String notificationTemplateType)
		throws NoSuchNotificationTemplateException {

		return _uniquePersistenceFinderByC_NT_NTT.find(
			finderCache,
			new Object[] {
				calendarId, notificationType, notificationTemplateType
			});
	}

	/**
	 * Returns the calendar notification template where calendarId = &#63; and notificationType = &#63; and notificationTemplateType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param notificationType the notification type
	 * @param notificationTemplateType the notification template type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar notification template, or <code>null</code> if a matching calendar notification template could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByC_NT_NTT(
		long calendarId, String notificationType,
		String notificationTemplateType, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_NT_NTT.fetch(
			finderCache,
			new Object[] {
				calendarId, notificationType, notificationTemplateType
			},
			useFinderCache);
	}

	/**
	 * Removes the calendar notification template where calendarId = &#63; and notificationType = &#63; and notificationTemplateType = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param notificationType the notification type
	 * @param notificationTemplateType the notification template type
	 * @return the calendar notification template that was removed
	 */
	@Override
	public CalendarNotificationTemplate removeByC_NT_NTT(
			long calendarId, String notificationType,
			String notificationTemplateType)
		throws NoSuchNotificationTemplateException {

		CalendarNotificationTemplate calendarNotificationTemplate =
			findByC_NT_NTT(
				calendarId, notificationType, notificationTemplateType);

		return remove(calendarNotificationTemplate);
	}

	/**
	 * Returns the number of calendar notification templates where calendarId = &#63; and notificationType = &#63; and notificationTemplateType = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param notificationType the notification type
	 * @param notificationTemplateType the notification template type
	 * @return the number of matching calendar notification templates
	 */
	@Override
	public int countByC_NT_NTT(
		long calendarId, String notificationType,
		String notificationTemplateType) {

		return _uniquePersistenceFinderByC_NT_NTT.count(
			finderCache,
			new Object[] {
				calendarId, notificationType, notificationTemplateType
			});
	}

	public CalendarNotificationTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CalendarNotificationTemplate.class);

		setModelImplClass(CalendarNotificationTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(CalendarNotificationTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new calendar notification template with the primary key. Does not add the calendar notification template to the database.
	 *
	 * @param calendarNotificationTemplateId the primary key for the new calendar notification template
	 * @return the new calendar notification template
	 */
	@Override
	public CalendarNotificationTemplate create(
		long calendarNotificationTemplateId) {

		CalendarNotificationTemplate calendarNotificationTemplate =
			new CalendarNotificationTemplateImpl();

		calendarNotificationTemplate.setNew(true);
		calendarNotificationTemplate.setPrimaryKey(
			calendarNotificationTemplateId);

		String uuid = PortalUUIDUtil.generate();

		calendarNotificationTemplate.setUuid(uuid);

		calendarNotificationTemplate.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return calendarNotificationTemplate;
	}

	/**
	 * Removes the calendar notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param calendarNotificationTemplateId the primary key of the calendar notification template
	 * @return the calendar notification template that was removed
	 * @throws NoSuchNotificationTemplateException if a calendar notification template with the primary key could not be found
	 */
	@Override
	public CalendarNotificationTemplate remove(
			long calendarNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return remove((Serializable)calendarNotificationTemplateId);
	}

	@Override
	protected CalendarNotificationTemplate removeImpl(
		CalendarNotificationTemplate calendarNotificationTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(calendarNotificationTemplate)) {
				calendarNotificationTemplate =
					(CalendarNotificationTemplate)session.get(
						CalendarNotificationTemplateImpl.class,
						calendarNotificationTemplate.getPrimaryKeyObj());
			}

			if ((calendarNotificationTemplate != null) &&
				ctPersistenceHelper.isRemove(calendarNotificationTemplate)) {

				session.delete(calendarNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (calendarNotificationTemplate != null) {
			clearCache(calendarNotificationTemplate);
		}

		return calendarNotificationTemplate;
	}

	@Override
	public CalendarNotificationTemplate updateImpl(
		CalendarNotificationTemplate calendarNotificationTemplate) {

		boolean isNew = calendarNotificationTemplate.isNew();

		if (!(calendarNotificationTemplate instanceof
				CalendarNotificationTemplateModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					calendarNotificationTemplate.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					calendarNotificationTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in calendarNotificationTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CalendarNotificationTemplate implementation " +
					calendarNotificationTemplate.getClass());
		}

		CalendarNotificationTemplateModelImpl
			calendarNotificationTemplateModelImpl =
				(CalendarNotificationTemplateModelImpl)
					calendarNotificationTemplate;

		if (Validator.isNull(calendarNotificationTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			calendarNotificationTemplate.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (calendarNotificationTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				calendarNotificationTemplate.setCreateDate(date);
			}
			else {
				calendarNotificationTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!calendarNotificationTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				calendarNotificationTemplate.setModifiedDate(date);
			}
			else {
				calendarNotificationTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(calendarNotificationTemplate)) {
				if (!isNew) {
					session.evict(
						CalendarNotificationTemplateImpl.class,
						calendarNotificationTemplate.getPrimaryKeyObj());
				}

				session.save(calendarNotificationTemplate);
			}
			else {
				calendarNotificationTemplate =
					(CalendarNotificationTemplate)session.merge(
						calendarNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(calendarNotificationTemplate, false);

		if (isNew) {
			calendarNotificationTemplate.setNew(false);
		}

		calendarNotificationTemplate.resetOriginalValues();

		return calendarNotificationTemplate;
	}

	/**
	 * Returns the calendar notification template with the primary key or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param calendarNotificationTemplateId the primary key of the calendar notification template
	 * @return the calendar notification template
	 * @throws NoSuchNotificationTemplateException if a calendar notification template with the primary key could not be found
	 */
	@Override
	public CalendarNotificationTemplate findByPrimaryKey(
			long calendarNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return findByPrimaryKey((Serializable)calendarNotificationTemplateId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the calendar notification template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param calendarNotificationTemplateId the primary key of the calendar notification template
	 * @return the calendar notification template, or <code>null</code> if a calendar notification template with the primary key could not be found
	 */
	@Override
	public CalendarNotificationTemplate fetchByPrimaryKey(
		long calendarNotificationTemplateId) {

		return fetchByPrimaryKey((Serializable)calendarNotificationTemplateId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "calendarNotificationTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CalendarNotificationTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CalendarNotificationTemplate";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("calendarId");
		ctMergeColumnNames.add("notificationType");
		ctMergeColumnNames.add("notificationTypeSettings");
		ctMergeColumnNames.add("notificationTemplateType");
		ctMergeColumnNames.add("subject");
		ctMergeColumnNames.add("body");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("calendarNotificationTemplateId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the calendar notification template persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
			_SQL_COUNT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
			CalendarNotificationTemplateModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"calendarNotificationTemplate.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CalendarNotificationTemplate::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CalendarNotificationTemplate::getUuid),
				CalendarNotificationTemplate::getGroupId),
			_SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"calendarNotificationTemplate.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CalendarNotificationTemplate::getUuid),
			new FinderColumn<>(
				"calendarNotificationTemplate.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CalendarNotificationTemplate::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
				CalendarNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"calendarNotificationTemplate.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CalendarNotificationTemplate::getUuid),
				new FinderColumn<>(
					"calendarNotificationTemplate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CalendarNotificationTemplate::getCompanyId));

		_collectionPersistenceFinderByCalendarId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCalendarId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"calendarId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCalendarId", new String[] {Long.class.getName()},
					new String[] {"calendarId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCalendarId", new String[] {Long.class.getName()},
					new String[] {"calendarId"}, false),
				_SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_CALENDARNOTIFICATIONTEMPLATE_WHERE,
				CalendarNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"calendarNotificationTemplate.", "calendarId",
					FinderColumn.Type.LONG, "=", true, true,
					CalendarNotificationTemplate::getCalendarId));

		_uniquePersistenceFinderByC_NT_NTT = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_NT_NTT",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"calendarId", "notificationType", "notificationTemplateType"
				},
				0, 6, false, CalendarNotificationTemplate::getCalendarId,
				convertNullFunction(
					CalendarNotificationTemplate::getNotificationType),
				convertNullFunction(
					CalendarNotificationTemplate::getNotificationTemplateType)),
			_SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"calendarNotificationTemplate.", "calendarId",
				FinderColumn.Type.LONG, "=", true, true,
				CalendarNotificationTemplate::getCalendarId),
			new FinderColumn<>(
				"calendarNotificationTemplate.", "notificationType",
				FinderColumn.Type.STRING, "=", true, true,
				CalendarNotificationTemplate::getNotificationType),
			new FinderColumn<>(
				"calendarNotificationTemplate.", "notificationTemplateType",
				FinderColumn.Type.STRING, "=", true, true,
				CalendarNotificationTemplate::getNotificationTemplateType));

		CalendarNotificationTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CalendarNotificationTemplateUtil.setPersistence(null);

		entityCache.removeCache(
			CalendarNotificationTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CalendarNotificationTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE =
		"SELECT calendarNotificationTemplate FROM CalendarNotificationTemplate calendarNotificationTemplate";

	private static final String _SQL_SELECT_CALENDARNOTIFICATIONTEMPLATE_WHERE =
		"SELECT calendarNotificationTemplate FROM CalendarNotificationTemplate calendarNotificationTemplate WHERE ";

	private static final String _SQL_COUNT_CALENDARNOTIFICATIONTEMPLATE_WHERE =
		"SELECT COUNT(calendarNotificationTemplate) FROM CalendarNotificationTemplate calendarNotificationTemplate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CalendarNotificationTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarNotificationTemplatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1930985280
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.NoSuchNotificationRecipientSettingException;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationRecipientSettingTable;
import com.liferay.notification.model.impl.NotificationRecipientSettingImpl;
import com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl;
import com.liferay.notification.service.persistence.NotificationRecipientSettingPersistence;
import com.liferay.notification.service.persistence.NotificationRecipientSettingUtil;
import com.liferay.notification.service.persistence.impl.constants.NotificationPersistenceConstants;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the notification recipient setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationRecipientSettingPersistence.class)
public class NotificationRecipientSettingPersistenceImpl
	extends BasePersistenceImpl
		<NotificationRecipientSetting,
		 NoSuchNotificationRecipientSettingException>
	implements NotificationRecipientSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationRecipientSettingUtil</code> to access the notification recipient setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationRecipientSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<NotificationRecipientSetting,
		 NoSuchNotificationRecipientSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the notification recipient settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationRecipientSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of notification recipient settings
	 * @param end the upper bound of the range of notification recipient settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification recipient settings
	 */
	@Override
	public List<NotificationRecipientSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<NotificationRecipientSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting
	 * @throws NoSuchNotificationRecipientSettingException if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting findByUuid_First(
			String uuid,
			OrderByComparator<NotificationRecipientSetting> orderByComparator)
		throws NoSuchNotificationRecipientSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<NotificationRecipientSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the notification recipient settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of notification recipient settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching notification recipient settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<NotificationRecipientSetting,
		 NoSuchNotificationRecipientSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the notification recipient settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationRecipientSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification recipient settings
	 * @param end the upper bound of the range of notification recipient settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification recipient settings
	 */
	@Override
	public List<NotificationRecipientSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<NotificationRecipientSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting
	 * @throws NoSuchNotificationRecipientSettingException if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<NotificationRecipientSetting> orderByComparator)
		throws NoSuchNotificationRecipientSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<NotificationRecipientSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the notification recipient settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of notification recipient settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching notification recipient settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<NotificationRecipientSetting,
		 NoSuchNotificationRecipientSettingException>
			_collectionPersistenceFinderByNotificationRecipientId;

	/**
	 * Returns an ordered range of all the notification recipient settings where notificationRecipientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationRecipientSettingModelImpl</code>.
	 * </p>
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param start the lower bound of the range of notification recipient settings
	 * @param end the upper bound of the range of notification recipient settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification recipient settings
	 */
	@Override
	public List<NotificationRecipientSetting> findByNotificationRecipientId(
		long notificationRecipientId, int start, int end,
		OrderByComparator<NotificationRecipientSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotificationRecipientId.find(
			finderCache, new Object[] {notificationRecipientId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where notificationRecipientId = &#63;.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting
	 * @throws NoSuchNotificationRecipientSettingException if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting findByNotificationRecipientId_First(
			long notificationRecipientId,
			OrderByComparator<NotificationRecipientSetting> orderByComparator)
		throws NoSuchNotificationRecipientSettingException {

		return _collectionPersistenceFinderByNotificationRecipientId.findFirst(
			finderCache, new Object[] {notificationRecipientId},
			orderByComparator);
	}

	/**
	 * Returns the first notification recipient setting in the ordered set where notificationRecipientId = &#63;.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting fetchByNotificationRecipientId_First(
		long notificationRecipientId,
		OrderByComparator<NotificationRecipientSetting> orderByComparator) {

		return _collectionPersistenceFinderByNotificationRecipientId.fetchFirst(
			finderCache, new Object[] {notificationRecipientId},
			orderByComparator);
	}

	/**
	 * Removes all the notification recipient settings where notificationRecipientId = &#63; from the database.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 */
	@Override
	public void removeByNotificationRecipientId(long notificationRecipientId) {
		_collectionPersistenceFinderByNotificationRecipientId.remove(
			finderCache, new Object[] {notificationRecipientId});
	}

	/**
	 * Returns the number of notification recipient settings where notificationRecipientId = &#63;.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @return the number of matching notification recipient settings
	 */
	@Override
	public int countByNotificationRecipientId(long notificationRecipientId) {
		return _collectionPersistenceFinderByNotificationRecipientId.count(
			finderCache, new Object[] {notificationRecipientId});
	}

	private UniquePersistenceFinder
		<NotificationRecipientSetting,
		 NoSuchNotificationRecipientSettingException>
			_uniquePersistenceFinderByNRI_N;

	/**
	 * Returns the notification recipient setting where notificationRecipientId = &#63; and name = &#63; or throws a <code>NoSuchNotificationRecipientSettingException</code> if it could not be found.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param name the name
	 * @return the matching notification recipient setting
	 * @throws NoSuchNotificationRecipientSettingException if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting findByNRI_N(
			long notificationRecipientId, String name)
		throws NoSuchNotificationRecipientSettingException {

		return _uniquePersistenceFinderByNRI_N.find(
			finderCache, new Object[] {notificationRecipientId, name});
	}

	/**
	 * Returns the notification recipient setting where notificationRecipientId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Override
	public NotificationRecipientSetting fetchByNRI_N(
		long notificationRecipientId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByNRI_N.fetch(
			finderCache, new Object[] {notificationRecipientId, name},
			useFinderCache);
	}

	/**
	 * Removes the notification recipient setting where notificationRecipientId = &#63; and name = &#63; from the database.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param name the name
	 * @return the notification recipient setting that was removed
	 */
	@Override
	public NotificationRecipientSetting removeByNRI_N(
			long notificationRecipientId, String name)
		throws NoSuchNotificationRecipientSettingException {

		NotificationRecipientSetting notificationRecipientSetting = findByNRI_N(
			notificationRecipientId, name);

		return remove(notificationRecipientSetting);
	}

	/**
	 * Returns the number of notification recipient settings where notificationRecipientId = &#63; and name = &#63;.
	 *
	 * @param notificationRecipientId the notification recipient ID
	 * @param name the name
	 * @return the number of matching notification recipient settings
	 */
	@Override
	public int countByNRI_N(long notificationRecipientId, String name) {
		return _uniquePersistenceFinderByNRI_N.count(
			finderCache, new Object[] {notificationRecipientId, name});
	}

	public NotificationRecipientSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationRecipientSetting.class);

		setModelImplClass(NotificationRecipientSettingImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationRecipientSettingTable.INSTANCE);
	}

	/**
	 * Creates a new notification recipient setting with the primary key. Does not add the notification recipient setting to the database.
	 *
	 * @param notificationRecipientSettingId the primary key for the new notification recipient setting
	 * @return the new notification recipient setting
	 */
	@Override
	public NotificationRecipientSetting create(
		long notificationRecipientSettingId) {

		NotificationRecipientSetting notificationRecipientSetting =
			new NotificationRecipientSettingImpl();

		notificationRecipientSetting.setNew(true);
		notificationRecipientSetting.setPrimaryKey(
			notificationRecipientSettingId);

		String uuid = PortalUUIDUtil.generate();

		notificationRecipientSetting.setUuid(uuid);

		notificationRecipientSetting.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return notificationRecipientSetting;
	}

	/**
	 * Removes the notification recipient setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting that was removed
	 * @throws NoSuchNotificationRecipientSettingException if a notification recipient setting with the primary key could not be found
	 */
	@Override
	public NotificationRecipientSetting remove(
			long notificationRecipientSettingId)
		throws NoSuchNotificationRecipientSettingException {

		return remove((Serializable)notificationRecipientSettingId);
	}

	@Override
	protected NotificationRecipientSetting removeImpl(
		NotificationRecipientSetting notificationRecipientSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationRecipientSetting)) {
				notificationRecipientSetting =
					(NotificationRecipientSetting)session.get(
						NotificationRecipientSettingImpl.class,
						notificationRecipientSetting.getPrimaryKeyObj());
			}

			if (notificationRecipientSetting != null) {
				session.delete(notificationRecipientSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationRecipientSetting != null) {
			clearCache(notificationRecipientSetting);
		}

		return notificationRecipientSetting;
	}

	@Override
	public NotificationRecipientSetting updateImpl(
		NotificationRecipientSetting notificationRecipientSetting) {

		boolean isNew = notificationRecipientSetting.isNew();

		if (!(notificationRecipientSetting instanceof
				NotificationRecipientSettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					notificationRecipientSetting.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationRecipientSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationRecipientSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationRecipientSetting implementation " +
					notificationRecipientSetting.getClass());
		}

		NotificationRecipientSettingModelImpl
			notificationRecipientSettingModelImpl =
				(NotificationRecipientSettingModelImpl)
					notificationRecipientSetting;

		if (Validator.isNull(notificationRecipientSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			notificationRecipientSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (notificationRecipientSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				notificationRecipientSetting.setCreateDate(date);
			}
			else {
				notificationRecipientSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!notificationRecipientSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				notificationRecipientSetting.setModifiedDate(date);
			}
			else {
				notificationRecipientSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationRecipientSetting);
			}
			else {
				notificationRecipientSetting =
					(NotificationRecipientSetting)session.merge(
						notificationRecipientSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(notificationRecipientSetting, false);

		if (isNew) {
			notificationRecipientSetting.setNew(false);
		}

		notificationRecipientSetting.resetOriginalValues();

		return notificationRecipientSetting;
	}

	/**
	 * Returns the notification recipient setting with the primary key or throws a <code>NoSuchNotificationRecipientSettingException</code> if it could not be found.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting
	 * @throws NoSuchNotificationRecipientSettingException if a notification recipient setting with the primary key could not be found
	 */
	@Override
	public NotificationRecipientSetting findByPrimaryKey(
			long notificationRecipientSettingId)
		throws NoSuchNotificationRecipientSettingException {

		return findByPrimaryKey((Serializable)notificationRecipientSettingId);
	}

	/**
	 * Returns the notification recipient setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting, or <code>null</code> if a notification recipient setting with the primary key could not be found
	 */
	@Override
	public NotificationRecipientSetting fetchByPrimaryKey(
		long notificationRecipientSettingId) {

		return fetchByPrimaryKey((Serializable)notificationRecipientSettingId);
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
		return "notificationRecipientSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONRECIPIENTSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationRecipientSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification recipient setting persistence.
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
			_SQL_SELECT_NOTIFICATIONRECIPIENTSETTING_WHERE,
			_SQL_COUNT_NOTIFICATIONRECIPIENTSETTING_WHERE,
			NotificationRecipientSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"notificationRecipientSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				NotificationRecipientSetting::getUuid));

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
				_SQL_SELECT_NOTIFICATIONRECIPIENTSETTING_WHERE,
				_SQL_COUNT_NOTIFICATIONRECIPIENTSETTING_WHERE,
				NotificationRecipientSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"notificationRecipientSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					NotificationRecipientSetting::getUuid),
				new FinderColumn<>(
					"notificationRecipientSetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationRecipientSetting::getCompanyId));

		_collectionPersistenceFinderByNotificationRecipientId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByNotificationRecipientId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"notificationRecipientId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByNotificationRecipientId",
					new String[] {Long.class.getName()},
					new String[] {"notificationRecipientId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByNotificationRecipientId",
					new String[] {Long.class.getName()},
					new String[] {"notificationRecipientId"}, false),
				_SQL_SELECT_NOTIFICATIONRECIPIENTSETTING_WHERE,
				_SQL_COUNT_NOTIFICATIONRECIPIENTSETTING_WHERE,
				NotificationRecipientSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"notificationRecipientSetting.", "notificationRecipientId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationRecipientSetting::getNotificationRecipientId));

		_uniquePersistenceFinderByNRI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByNRI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"notificationRecipientId", "name"}, 0, 2, false,
				NotificationRecipientSetting::getNotificationRecipientId,
				convertNullFunction(NotificationRecipientSetting::getName)),
			_SQL_SELECT_NOTIFICATIONRECIPIENTSETTING_WHERE, "",
			new FinderColumn<>(
				"notificationRecipientSetting.", "notificationRecipientId",
				FinderColumn.Type.LONG, "=", true, true,
				NotificationRecipientSetting::getNotificationRecipientId),
			new FinderColumn<>(
				"notificationRecipientSetting.", "name",
				FinderColumn.Type.STRING, "=", true, true,
				NotificationRecipientSetting::getName));

		NotificationRecipientSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationRecipientSettingUtil.setPersistence(null);

		entityCache.removeCache(
			NotificationRecipientSettingImpl.class.getName());
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		NotificationRecipientSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_NOTIFICATIONRECIPIENTSETTING =
		"SELECT notificationRecipientSetting FROM NotificationRecipientSetting notificationRecipientSetting";

	private static final String _SQL_SELECT_NOTIFICATIONRECIPIENTSETTING_WHERE =
		"SELECT notificationRecipientSetting FROM NotificationRecipientSetting notificationRecipientSetting WHERE ";

	private static final String _SQL_COUNT_NOTIFICATIONRECIPIENTSETTING_WHERE =
		"SELECT COUNT(notificationRecipientSetting) FROM NotificationRecipientSetting notificationRecipientSetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NotificationRecipientSetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationRecipientSettingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2108597135
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.NoSuchNotificationRecipientException;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.model.NotificationRecipientTable;
import com.liferay.notification.model.impl.NotificationRecipientImpl;
import com.liferay.notification.model.impl.NotificationRecipientModelImpl;
import com.liferay.notification.service.persistence.NotificationRecipientPersistence;
import com.liferay.notification.service.persistence.NotificationRecipientUtil;
import com.liferay.notification.service.persistence.impl.constants.NotificationPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the notification recipient service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationRecipientPersistence.class)
public class NotificationRecipientPersistenceImpl
	extends BasePersistenceImpl
		<NotificationRecipient, NoSuchNotificationRecipientException>
	implements NotificationRecipientPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationRecipientUtil</code> to access the notification recipient persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationRecipientImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<NotificationRecipient, NoSuchNotificationRecipientException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the notification recipients where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of notification recipients
	 * @param end the upper bound of the range of notification recipients (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification recipients
	 */
	@Override
	public List<NotificationRecipient> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<NotificationRecipient> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first notification recipient in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient findByUuid_First(
			String uuid,
			OrderByComparator<NotificationRecipient> orderByComparator)
		throws NoSuchNotificationRecipientException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first notification recipient in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient, or <code>null</code> if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient fetchByUuid_First(
		String uuid,
		OrderByComparator<NotificationRecipient> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the notification recipients where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of notification recipients where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching notification recipients
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<NotificationRecipient, NoSuchNotificationRecipientException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the notification recipients where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationRecipientModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification recipients
	 * @param end the upper bound of the range of notification recipients (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification recipients
	 */
	@Override
	public List<NotificationRecipient> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<NotificationRecipient> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification recipient in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<NotificationRecipient> orderByComparator)
		throws NoSuchNotificationRecipientException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first notification recipient in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification recipient, or <code>null</code> if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<NotificationRecipient> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the notification recipients where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of notification recipients where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching notification recipients
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<NotificationRecipient, NoSuchNotificationRecipientException>
			_uniquePersistenceFinderByClassPK;

	/**
	 * Returns the notification recipient where classPK = &#63; or throws a <code>NoSuchNotificationRecipientException</code> if it could not be found.
	 *
	 * @param classPK the class pk
	 * @return the matching notification recipient
	 * @throws NoSuchNotificationRecipientException if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient findByClassPK(long classPK)
		throws NoSuchNotificationRecipientException {

		return _uniquePersistenceFinderByClassPK.find(
			finderCache, new Object[] {classPK});
	}

	/**
	 * Returns the notification recipient where classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching notification recipient, or <code>null</code> if a matching notification recipient could not be found
	 */
	@Override
	public NotificationRecipient fetchByClassPK(
		long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByClassPK.fetch(
			finderCache, new Object[] {classPK}, useFinderCache);
	}

	/**
	 * Removes the notification recipient where classPK = &#63; from the database.
	 *
	 * @param classPK the class pk
	 * @return the notification recipient that was removed
	 */
	@Override
	public NotificationRecipient removeByClassPK(long classPK)
		throws NoSuchNotificationRecipientException {

		NotificationRecipient notificationRecipient = findByClassPK(classPK);

		return remove(notificationRecipient);
	}

	/**
	 * Returns the number of notification recipients where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the number of matching notification recipients
	 */
	@Override
	public int countByClassPK(long classPK) {
		return _uniquePersistenceFinderByClassPK.count(
			finderCache, new Object[] {classPK});
	}

	public NotificationRecipientPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationRecipient.class);

		setModelImplClass(NotificationRecipientImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationRecipientTable.INSTANCE);
	}

	/**
	 * Creates a new notification recipient with the primary key. Does not add the notification recipient to the database.
	 *
	 * @param notificationRecipientId the primary key for the new notification recipient
	 * @return the new notification recipient
	 */
	@Override
	public NotificationRecipient create(long notificationRecipientId) {
		NotificationRecipient notificationRecipient =
			new NotificationRecipientImpl();

		notificationRecipient.setNew(true);
		notificationRecipient.setPrimaryKey(notificationRecipientId);

		String uuid = PortalUUIDUtil.generate();

		notificationRecipient.setUuid(uuid);

		notificationRecipient.setCompanyId(CompanyThreadLocal.getCompanyId());

		return notificationRecipient;
	}

	/**
	 * Removes the notification recipient with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient that was removed
	 * @throws NoSuchNotificationRecipientException if a notification recipient with the primary key could not be found
	 */
	@Override
	public NotificationRecipient remove(long notificationRecipientId)
		throws NoSuchNotificationRecipientException {

		return remove((Serializable)notificationRecipientId);
	}

	@Override
	protected NotificationRecipient removeImpl(
		NotificationRecipient notificationRecipient) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationRecipient)) {
				notificationRecipient = (NotificationRecipient)session.get(
					NotificationRecipientImpl.class,
					notificationRecipient.getPrimaryKeyObj());
			}

			if (notificationRecipient != null) {
				session.delete(notificationRecipient);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationRecipient != null) {
			clearCache(notificationRecipient);
		}

		return notificationRecipient;
	}

	@Override
	public NotificationRecipient updateImpl(
		NotificationRecipient notificationRecipient) {

		boolean isNew = notificationRecipient.isNew();

		if (!(notificationRecipient instanceof
				NotificationRecipientModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(notificationRecipient.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationRecipient);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationRecipient proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationRecipient implementation " +
					notificationRecipient.getClass());
		}

		NotificationRecipientModelImpl notificationRecipientModelImpl =
			(NotificationRecipientModelImpl)notificationRecipient;

		if (Validator.isNull(notificationRecipient.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			notificationRecipient.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (notificationRecipient.getCreateDate() == null)) {
			if (serviceContext == null) {
				notificationRecipient.setCreateDate(date);
			}
			else {
				notificationRecipient.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!notificationRecipientModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				notificationRecipient.setModifiedDate(date);
			}
			else {
				notificationRecipient.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationRecipient);
			}
			else {
				notificationRecipient = (NotificationRecipient)session.merge(
					notificationRecipient);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(notificationRecipient, false);

		if (isNew) {
			notificationRecipient.setNew(false);
		}

		notificationRecipient.resetOriginalValues();

		return notificationRecipient;
	}

	/**
	 * Returns the notification recipient with the primary key or throws a <code>NoSuchNotificationRecipientException</code> if it could not be found.
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient
	 * @throws NoSuchNotificationRecipientException if a notification recipient with the primary key could not be found
	 */
	@Override
	public NotificationRecipient findByPrimaryKey(long notificationRecipientId)
		throws NoSuchNotificationRecipientException {

		return findByPrimaryKey((Serializable)notificationRecipientId);
	}

	/**
	 * Returns the notification recipient with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationRecipientId the primary key of the notification recipient
	 * @return the notification recipient, or <code>null</code> if a notification recipient with the primary key could not be found
	 */
	@Override
	public NotificationRecipient fetchByPrimaryKey(
		long notificationRecipientId) {

		return fetchByPrimaryKey((Serializable)notificationRecipientId);
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
		return "notificationRecipientId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONRECIPIENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationRecipientModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification recipient persistence.
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
			_SQL_SELECT_NOTIFICATIONRECIPIENT_WHERE,
			_SQL_COUNT_NOTIFICATIONRECIPIENT_WHERE,
			NotificationRecipientModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"notificationRecipient.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				NotificationRecipient::getUuid));

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
				_SQL_SELECT_NOTIFICATIONRECIPIENT_WHERE,
				_SQL_COUNT_NOTIFICATIONRECIPIENT_WHERE,
				NotificationRecipientModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"notificationRecipient.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					NotificationRecipient::getUuid),
				new FinderColumn<>(
					"notificationRecipient.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationRecipient::getCompanyId));

		_uniquePersistenceFinderByClassPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByClassPK",
				new String[] {Long.class.getName()}, new String[] {"classPK"},
				0, 0, false, NotificationRecipient::getClassPK),
			_SQL_SELECT_NOTIFICATIONRECIPIENT_WHERE, "",
			new FinderColumn<>(
				"notificationRecipient.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, NotificationRecipient::getClassPK));

		NotificationRecipientUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationRecipientUtil.setPersistence(null);

		entityCache.removeCache(NotificationRecipientImpl.class.getName());
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
		NotificationRecipientModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_NOTIFICATIONRECIPIENT =
		"SELECT notificationRecipient FROM NotificationRecipient notificationRecipient";

	private static final String _SQL_SELECT_NOTIFICATIONRECIPIENT_WHERE =
		"SELECT notificationRecipient FROM NotificationRecipient notificationRecipient WHERE ";

	private static final String _SQL_COUNT_NOTIFICATIONRECIPIENT_WHERE =
		"SELECT COUNT(notificationRecipient) FROM NotificationRecipient notificationRecipient WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:97202115
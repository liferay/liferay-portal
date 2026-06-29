/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.NoSuchNotificationQueueEntryAttachmentException;
import com.liferay.notification.model.NotificationQueueEntryAttachment;
import com.liferay.notification.model.NotificationQueueEntryAttachmentTable;
import com.liferay.notification.model.impl.NotificationQueueEntryAttachmentImpl;
import com.liferay.notification.model.impl.NotificationQueueEntryAttachmentModelImpl;
import com.liferay.notification.service.persistence.NotificationQueueEntryAttachmentPersistence;
import com.liferay.notification.service.persistence.NotificationQueueEntryAttachmentUtil;
import com.liferay.notification.service.persistence.impl.constants.NotificationPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the notification queue entry attachment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationQueueEntryAttachmentPersistence.class)
public class NotificationQueueEntryAttachmentPersistenceImpl
	extends BasePersistenceImpl
		<NotificationQueueEntryAttachment,
		 NoSuchNotificationQueueEntryAttachmentException>
	implements NotificationQueueEntryAttachmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationQueueEntryAttachmentUtil</code> to access the notification queue entry attachment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationQueueEntryAttachmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<NotificationQueueEntryAttachment,
		 NoSuchNotificationQueueEntryAttachmentException>
			_collectionPersistenceFinderByNotificationQueueEntryId;

	/**
	 * Returns an ordered range of all the notification queue entry attachments where notificationQueueEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param notificationQueueEntryId the notification queue entry ID
	 * @param start the lower bound of the range of notification queue entry attachments
	 * @param end the upper bound of the range of notification queue entry attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification queue entry attachments
	 */
	@Override
	public List<NotificationQueueEntryAttachment>
		findByNotificationQueueEntryId(
			long notificationQueueEntryId, int start, int end,
			OrderByComparator<NotificationQueueEntryAttachment>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByNotificationQueueEntryId.find(
			finderCache, new Object[] {notificationQueueEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification queue entry attachment in the ordered set where notificationQueueEntryId = &#63;.
	 *
	 * @param notificationQueueEntryId the notification queue entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry attachment
	 * @throws NoSuchNotificationQueueEntryAttachmentException if a matching notification queue entry attachment could not be found
	 */
	@Override
	public NotificationQueueEntryAttachment
			findByNotificationQueueEntryId_First(
				long notificationQueueEntryId,
				OrderByComparator<NotificationQueueEntryAttachment>
					orderByComparator)
		throws NoSuchNotificationQueueEntryAttachmentException {

		return _collectionPersistenceFinderByNotificationQueueEntryId.findFirst(
			finderCache, new Object[] {notificationQueueEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first notification queue entry attachment in the ordered set where notificationQueueEntryId = &#63;.
	 *
	 * @param notificationQueueEntryId the notification queue entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry attachment, or <code>null</code> if a matching notification queue entry attachment could not be found
	 */
	@Override
	public NotificationQueueEntryAttachment
		fetchByNotificationQueueEntryId_First(
			long notificationQueueEntryId,
			OrderByComparator<NotificationQueueEntryAttachment>
				orderByComparator) {

		return _collectionPersistenceFinderByNotificationQueueEntryId.
			fetchFirst(
				finderCache, new Object[] {notificationQueueEntryId},
				orderByComparator);
	}

	/**
	 * Removes all the notification queue entry attachments where notificationQueueEntryId = &#63; from the database.
	 *
	 * @param notificationQueueEntryId the notification queue entry ID
	 */
	@Override
	public void removeByNotificationQueueEntryId(
		long notificationQueueEntryId) {

		_collectionPersistenceFinderByNotificationQueueEntryId.remove(
			finderCache, new Object[] {notificationQueueEntryId});
	}

	/**
	 * Returns the number of notification queue entry attachments where notificationQueueEntryId = &#63;.
	 *
	 * @param notificationQueueEntryId the notification queue entry ID
	 * @return the number of matching notification queue entry attachments
	 */
	@Override
	public int countByNotificationQueueEntryId(long notificationQueueEntryId) {
		return _collectionPersistenceFinderByNotificationQueueEntryId.count(
			finderCache, new Object[] {notificationQueueEntryId});
	}

	public NotificationQueueEntryAttachmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"notificationQueueEntryAttachmentId", "NQueueEntryAttachmentId");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationQueueEntryAttachment.class);

		setModelImplClass(NotificationQueueEntryAttachmentImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationQueueEntryAttachmentTable.INSTANCE);
	}

	/**
	 * Creates a new notification queue entry attachment with the primary key. Does not add the notification queue entry attachment to the database.
	 *
	 * @param notificationQueueEntryAttachmentId the primary key for the new notification queue entry attachment
	 * @return the new notification queue entry attachment
	 */
	@Override
	public NotificationQueueEntryAttachment create(
		long notificationQueueEntryAttachmentId) {

		NotificationQueueEntryAttachment notificationQueueEntryAttachment =
			new NotificationQueueEntryAttachmentImpl();

		notificationQueueEntryAttachment.setNew(true);
		notificationQueueEntryAttachment.setPrimaryKey(
			notificationQueueEntryAttachmentId);

		notificationQueueEntryAttachment.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return notificationQueueEntryAttachment;
	}

	/**
	 * Removes the notification queue entry attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationQueueEntryAttachmentId the primary key of the notification queue entry attachment
	 * @return the notification queue entry attachment that was removed
	 * @throws NoSuchNotificationQueueEntryAttachmentException if a notification queue entry attachment with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntryAttachment remove(
			long notificationQueueEntryAttachmentId)
		throws NoSuchNotificationQueueEntryAttachmentException {

		return remove((Serializable)notificationQueueEntryAttachmentId);
	}

	@Override
	protected NotificationQueueEntryAttachment removeImpl(
		NotificationQueueEntryAttachment notificationQueueEntryAttachment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationQueueEntryAttachment)) {
				notificationQueueEntryAttachment =
					(NotificationQueueEntryAttachment)session.get(
						NotificationQueueEntryAttachmentImpl.class,
						notificationQueueEntryAttachment.getPrimaryKeyObj());
			}

			if (notificationQueueEntryAttachment != null) {
				session.delete(notificationQueueEntryAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationQueueEntryAttachment != null) {
			clearCache(notificationQueueEntryAttachment);
		}

		return notificationQueueEntryAttachment;
	}

	@Override
	public NotificationQueueEntryAttachment updateImpl(
		NotificationQueueEntryAttachment notificationQueueEntryAttachment) {

		boolean isNew = notificationQueueEntryAttachment.isNew();

		if (!(notificationQueueEntryAttachment instanceof
				NotificationQueueEntryAttachmentModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					notificationQueueEntryAttachment.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationQueueEntryAttachment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationQueueEntryAttachment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationQueueEntryAttachment implementation " +
					notificationQueueEntryAttachment.getClass());
		}

		NotificationQueueEntryAttachmentModelImpl
			notificationQueueEntryAttachmentModelImpl =
				(NotificationQueueEntryAttachmentModelImpl)
					notificationQueueEntryAttachment;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationQueueEntryAttachment);
			}
			else {
				notificationQueueEntryAttachment =
					(NotificationQueueEntryAttachment)session.merge(
						notificationQueueEntryAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(notificationQueueEntryAttachment, false);

		if (isNew) {
			notificationQueueEntryAttachment.setNew(false);
		}

		notificationQueueEntryAttachment.resetOriginalValues();

		return notificationQueueEntryAttachment;
	}

	/**
	 * Returns the notification queue entry attachment with the primary key or throws a <code>NoSuchNotificationQueueEntryAttachmentException</code> if it could not be found.
	 *
	 * @param notificationQueueEntryAttachmentId the primary key of the notification queue entry attachment
	 * @return the notification queue entry attachment
	 * @throws NoSuchNotificationQueueEntryAttachmentException if a notification queue entry attachment with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntryAttachment findByPrimaryKey(
			long notificationQueueEntryAttachmentId)
		throws NoSuchNotificationQueueEntryAttachmentException {

		return findByPrimaryKey(
			(Serializable)notificationQueueEntryAttachmentId);
	}

	/**
	 * Returns the notification queue entry attachment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationQueueEntryAttachmentId the primary key of the notification queue entry attachment
	 * @return the notification queue entry attachment, or <code>null</code> if a notification queue entry attachment with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntryAttachment fetchByPrimaryKey(
		long notificationQueueEntryAttachmentId) {

		return fetchByPrimaryKey(
			(Serializable)notificationQueueEntryAttachmentId);
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
		return "NQueueEntryAttachmentId";
	}

	@Override
	protected String getPKFieldName() {
		return "notificationQueueEntryAttachmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONQUEUEENTRYATTACHMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationQueueEntryAttachmentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification queue entry attachment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByNotificationQueueEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByNotificationQueueEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"notificationQueueEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByNotificationQueueEntryId",
					new String[] {Long.class.getName()},
					new String[] {"notificationQueueEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByNotificationQueueEntryId",
					new String[] {Long.class.getName()},
					new String[] {"notificationQueueEntryId"}, false),
				_SQL_SELECT_NOTIFICATIONQUEUEENTRYATTACHMENT_WHERE,
				_SQL_COUNT_NOTIFICATIONQUEUEENTRYATTACHMENT_WHERE,
				NotificationQueueEntryAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"notificationQueueEntryAttachment.",
					"notificationQueueEntryId", FinderColumn.Type.LONG, "=",
					true, true,
					NotificationQueueEntryAttachment::
						getNotificationQueueEntryId));

		NotificationQueueEntryAttachmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationQueueEntryAttachmentUtil.setPersistence(null);

		entityCache.removeCache(
			NotificationQueueEntryAttachmentImpl.class.getName());
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
		NotificationQueueEntryAttachmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_NOTIFICATIONQUEUEENTRYATTACHMENT =
		"SELECT notificationQueueEntryAttachment FROM NotificationQueueEntryAttachment notificationQueueEntryAttachment";

	private static final String
		_SQL_SELECT_NOTIFICATIONQUEUEENTRYATTACHMENT_WHERE =
			"SELECT notificationQueueEntryAttachment FROM NotificationQueueEntryAttachment notificationQueueEntryAttachment WHERE ";

	private static final String
		_SQL_COUNT_NOTIFICATIONQUEUEENTRYATTACHMENT_WHERE =
			"SELECT COUNT(notificationQueueEntryAttachment) FROM NotificationQueueEntryAttachment notificationQueueEntryAttachment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NotificationQueueEntryAttachment exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"notificationQueueEntryAttachmentId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-682626341
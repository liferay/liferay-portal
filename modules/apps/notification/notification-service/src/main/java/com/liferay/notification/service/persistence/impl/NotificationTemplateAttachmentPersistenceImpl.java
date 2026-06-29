/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.NoSuchNotificationTemplateAttachmentException;
import com.liferay.notification.model.NotificationTemplateAttachment;
import com.liferay.notification.model.NotificationTemplateAttachmentTable;
import com.liferay.notification.model.impl.NotificationTemplateAttachmentImpl;
import com.liferay.notification.model.impl.NotificationTemplateAttachmentModelImpl;
import com.liferay.notification.service.persistence.NotificationTemplateAttachmentPersistence;
import com.liferay.notification.service.persistence.NotificationTemplateAttachmentUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the notification template attachment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationTemplateAttachmentPersistence.class)
public class NotificationTemplateAttachmentPersistenceImpl
	extends BasePersistenceImpl
		<NotificationTemplateAttachment,
		 NoSuchNotificationTemplateAttachmentException>
	implements NotificationTemplateAttachmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationTemplateAttachmentUtil</code> to access the notification template attachment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationTemplateAttachmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<NotificationTemplateAttachment,
		 NoSuchNotificationTemplateAttachmentException>
			_collectionPersistenceFinderByNotificationTemplateId;

	/**
	 * Returns an ordered range of all the notification template attachments where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification template attachments
	 * @param end the upper bound of the range of notification template attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification template attachments
	 */
	@Override
	public List<NotificationTemplateAttachment> findByNotificationTemplateId(
		long notificationTemplateId, int start, int end,
		OrderByComparator<NotificationTemplateAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByNotificationTemplateId.find(
			finderCache, new Object[] {notificationTemplateId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification template attachment in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template attachment
	 * @throws NoSuchNotificationTemplateAttachmentException if a matching notification template attachment could not be found
	 */
	@Override
	public NotificationTemplateAttachment findByNotificationTemplateId_First(
			long notificationTemplateId,
			OrderByComparator<NotificationTemplateAttachment> orderByComparator)
		throws NoSuchNotificationTemplateAttachmentException {

		return _collectionPersistenceFinderByNotificationTemplateId.findFirst(
			finderCache, new Object[] {notificationTemplateId},
			orderByComparator);
	}

	/**
	 * Returns the first notification template attachment in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template attachment, or <code>null</code> if a matching notification template attachment could not be found
	 */
	@Override
	public NotificationTemplateAttachment fetchByNotificationTemplateId_First(
		long notificationTemplateId,
		OrderByComparator<NotificationTemplateAttachment> orderByComparator) {

		return _collectionPersistenceFinderByNotificationTemplateId.fetchFirst(
			finderCache, new Object[] {notificationTemplateId},
			orderByComparator);
	}

	/**
	 * Removes all the notification template attachments where notificationTemplateId = &#63; from the database.
	 *
	 * @param notificationTemplateId the notification template ID
	 */
	@Override
	public void removeByNotificationTemplateId(long notificationTemplateId) {
		_collectionPersistenceFinderByNotificationTemplateId.remove(
			finderCache, new Object[] {notificationTemplateId});
	}

	/**
	 * Returns the number of notification template attachments where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @return the number of matching notification template attachments
	 */
	@Override
	public int countByNotificationTemplateId(long notificationTemplateId) {
		return _collectionPersistenceFinderByNotificationTemplateId.count(
			finderCache, new Object[] {notificationTemplateId});
	}

	private UniquePersistenceFinder
		<NotificationTemplateAttachment,
		 NoSuchNotificationTemplateAttachmentException>
			_uniquePersistenceFinderByNTI_OFI;

	/**
	 * Returns the notification template attachment where notificationTemplateId = &#63; and objectFieldId = &#63; or throws a <code>NoSuchNotificationTemplateAttachmentException</code> if it could not be found.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param objectFieldId the object field ID
	 * @return the matching notification template attachment
	 * @throws NoSuchNotificationTemplateAttachmentException if a matching notification template attachment could not be found
	 */
	@Override
	public NotificationTemplateAttachment findByNTI_OFI(
			long notificationTemplateId, long objectFieldId)
		throws NoSuchNotificationTemplateAttachmentException {

		return _uniquePersistenceFinderByNTI_OFI.find(
			finderCache, new Object[] {notificationTemplateId, objectFieldId});
	}

	/**
	 * Returns the notification template attachment where notificationTemplateId = &#63; and objectFieldId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param objectFieldId the object field ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching notification template attachment, or <code>null</code> if a matching notification template attachment could not be found
	 */
	@Override
	public NotificationTemplateAttachment fetchByNTI_OFI(
		long notificationTemplateId, long objectFieldId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByNTI_OFI.fetch(
			finderCache, new Object[] {notificationTemplateId, objectFieldId},
			useFinderCache);
	}

	/**
	 * Removes the notification template attachment where notificationTemplateId = &#63; and objectFieldId = &#63; from the database.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param objectFieldId the object field ID
	 * @return the notification template attachment that was removed
	 */
	@Override
	public NotificationTemplateAttachment removeByNTI_OFI(
			long notificationTemplateId, long objectFieldId)
		throws NoSuchNotificationTemplateAttachmentException {

		NotificationTemplateAttachment notificationTemplateAttachment =
			findByNTI_OFI(notificationTemplateId, objectFieldId);

		return remove(notificationTemplateAttachment);
	}

	/**
	 * Returns the number of notification template attachments where notificationTemplateId = &#63; and objectFieldId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param objectFieldId the object field ID
	 * @return the number of matching notification template attachments
	 */
	@Override
	public int countByNTI_OFI(long notificationTemplateId, long objectFieldId) {
		return _uniquePersistenceFinderByNTI_OFI.count(
			finderCache, new Object[] {notificationTemplateId, objectFieldId});
	}

	public NotificationTemplateAttachmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"notificationTemplateAttachmentId", "NTemplateAttachmentId");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationTemplateAttachment.class);

		setModelImplClass(NotificationTemplateAttachmentImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationTemplateAttachmentTable.INSTANCE);
	}

	/**
	 * Creates a new notification template attachment with the primary key. Does not add the notification template attachment to the database.
	 *
	 * @param notificationTemplateAttachmentId the primary key for the new notification template attachment
	 * @return the new notification template attachment
	 */
	@Override
	public NotificationTemplateAttachment create(
		long notificationTemplateAttachmentId) {

		NotificationTemplateAttachment notificationTemplateAttachment =
			new NotificationTemplateAttachmentImpl();

		notificationTemplateAttachment.setNew(true);
		notificationTemplateAttachment.setPrimaryKey(
			notificationTemplateAttachmentId);

		notificationTemplateAttachment.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return notificationTemplateAttachment;
	}

	/**
	 * Removes the notification template attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationTemplateAttachmentId the primary key of the notification template attachment
	 * @return the notification template attachment that was removed
	 * @throws NoSuchNotificationTemplateAttachmentException if a notification template attachment with the primary key could not be found
	 */
	@Override
	public NotificationTemplateAttachment remove(
			long notificationTemplateAttachmentId)
		throws NoSuchNotificationTemplateAttachmentException {

		return remove((Serializable)notificationTemplateAttachmentId);
	}

	@Override
	protected NotificationTemplateAttachment removeImpl(
		NotificationTemplateAttachment notificationTemplateAttachment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationTemplateAttachment)) {
				notificationTemplateAttachment =
					(NotificationTemplateAttachment)session.get(
						NotificationTemplateAttachmentImpl.class,
						notificationTemplateAttachment.getPrimaryKeyObj());
			}

			if (notificationTemplateAttachment != null) {
				session.delete(notificationTemplateAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationTemplateAttachment != null) {
			clearCache(notificationTemplateAttachment);
		}

		return notificationTemplateAttachment;
	}

	@Override
	public NotificationTemplateAttachment updateImpl(
		NotificationTemplateAttachment notificationTemplateAttachment) {

		boolean isNew = notificationTemplateAttachment.isNew();

		if (!(notificationTemplateAttachment instanceof
				NotificationTemplateAttachmentModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					notificationTemplateAttachment.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationTemplateAttachment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationTemplateAttachment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationTemplateAttachment implementation " +
					notificationTemplateAttachment.getClass());
		}

		NotificationTemplateAttachmentModelImpl
			notificationTemplateAttachmentModelImpl =
				(NotificationTemplateAttachmentModelImpl)
					notificationTemplateAttachment;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationTemplateAttachment);
			}
			else {
				notificationTemplateAttachment =
					(NotificationTemplateAttachment)session.merge(
						notificationTemplateAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(notificationTemplateAttachment, false);

		if (isNew) {
			notificationTemplateAttachment.setNew(false);
		}

		notificationTemplateAttachment.resetOriginalValues();

		return notificationTemplateAttachment;
	}

	/**
	 * Returns the notification template attachment with the primary key or throws a <code>NoSuchNotificationTemplateAttachmentException</code> if it could not be found.
	 *
	 * @param notificationTemplateAttachmentId the primary key of the notification template attachment
	 * @return the notification template attachment
	 * @throws NoSuchNotificationTemplateAttachmentException if a notification template attachment with the primary key could not be found
	 */
	@Override
	public NotificationTemplateAttachment findByPrimaryKey(
			long notificationTemplateAttachmentId)
		throws NoSuchNotificationTemplateAttachmentException {

		return findByPrimaryKey((Serializable)notificationTemplateAttachmentId);
	}

	/**
	 * Returns the notification template attachment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationTemplateAttachmentId the primary key of the notification template attachment
	 * @return the notification template attachment, or <code>null</code> if a notification template attachment with the primary key could not be found
	 */
	@Override
	public NotificationTemplateAttachment fetchByPrimaryKey(
		long notificationTemplateAttachmentId) {

		return fetchByPrimaryKey(
			(Serializable)notificationTemplateAttachmentId);
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
		return "NTemplateAttachmentId";
	}

	@Override
	protected String getPKFieldName() {
		return "notificationTemplateAttachmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONTEMPLATEATTACHMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationTemplateAttachmentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification template attachment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByNotificationTemplateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByNotificationTemplateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"notificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"notificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"notificationTemplateId"}, false),
				_SQL_SELECT_NOTIFICATIONTEMPLATEATTACHMENT_WHERE,
				_SQL_COUNT_NOTIFICATIONTEMPLATEATTACHMENT_WHERE,
				NotificationTemplateAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"notificationTemplateAttachment.", "notificationTemplateId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationTemplateAttachment::getNotificationTemplateId));

		_uniquePersistenceFinderByNTI_OFI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByNTI_OFI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"notificationTemplateId", "objectFieldId"}, 0, 0,
				false,
				NotificationTemplateAttachment::getNotificationTemplateId,
				NotificationTemplateAttachment::getObjectFieldId),
			_SQL_SELECT_NOTIFICATIONTEMPLATEATTACHMENT_WHERE, "",
			new FinderColumn<>(
				"notificationTemplateAttachment.", "notificationTemplateId",
				FinderColumn.Type.LONG, "=", true, true,
				NotificationTemplateAttachment::getNotificationTemplateId),
			new FinderColumn<>(
				"notificationTemplateAttachment.", "objectFieldId",
				FinderColumn.Type.LONG, "=", true, true,
				NotificationTemplateAttachment::getObjectFieldId));

		NotificationTemplateAttachmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationTemplateAttachmentUtil.setPersistence(null);

		entityCache.removeCache(
			NotificationTemplateAttachmentImpl.class.getName());
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
		NotificationTemplateAttachmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_NOTIFICATIONTEMPLATEATTACHMENT =
		"SELECT notificationTemplateAttachment FROM NotificationTemplateAttachment notificationTemplateAttachment";

	private static final String
		_SQL_SELECT_NOTIFICATIONTEMPLATEATTACHMENT_WHERE =
			"SELECT notificationTemplateAttachment FROM NotificationTemplateAttachment notificationTemplateAttachment WHERE ";

	private static final String
		_SQL_COUNT_NOTIFICATIONTEMPLATEATTACHMENT_WHERE =
			"SELECT COUNT(notificationTemplateAttachment) FROM NotificationTemplateAttachment notificationTemplateAttachment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NotificationTemplateAttachment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationTemplateAttachmentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"notificationTemplateAttachmentId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1415252225
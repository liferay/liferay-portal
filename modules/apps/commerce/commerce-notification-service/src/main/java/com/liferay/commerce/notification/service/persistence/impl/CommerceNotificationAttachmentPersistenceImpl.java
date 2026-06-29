/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.exception.NoSuchNotificationAttachmentException;
import com.liferay.commerce.notification.model.CommerceNotificationAttachment;
import com.liferay.commerce.notification.model.CommerceNotificationAttachmentTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationAttachmentImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationAttachmentModelImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationAttachmentPersistence;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationAttachmentUtil;
import com.liferay.commerce.notification.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce notification attachment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(service = CommerceNotificationAttachmentPersistence.class)
@Deprecated
public class CommerceNotificationAttachmentPersistenceImpl
	extends BasePersistenceImpl
		<CommerceNotificationAttachment, NoSuchNotificationAttachmentException>
	implements CommerceNotificationAttachmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceNotificationAttachmentUtil</code> to access the commerce notification attachment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceNotificationAttachmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceNotificationAttachment, NoSuchNotificationAttachmentException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce notification attachments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce notification attachments
	 * @param end the upper bound of the range of commerce notification attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification attachments
	 */
	@Override
	public List<CommerceNotificationAttachment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceNotificationAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment
	 * @throws NoSuchNotificationAttachmentException if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment findByUuid_First(
			String uuid,
			OrderByComparator<CommerceNotificationAttachment> orderByComparator)
		throws NoSuchNotificationAttachmentException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceNotificationAttachment> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification attachments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce notification attachments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce notification attachments
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceNotificationAttachment, NoSuchNotificationAttachmentException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce notification attachment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchNotificationAttachmentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce notification attachment
	 * @throws NoSuchNotificationAttachmentException if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment findByUUID_G(
			String uuid, long groupId)
		throws NoSuchNotificationAttachmentException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce notification attachment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce notification attachment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce notification attachment that was removed
	 */
	@Override
	public CommerceNotificationAttachment removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchNotificationAttachmentException {

		CommerceNotificationAttachment commerceNotificationAttachment =
			findByUUID_G(uuid, groupId);

		return remove(commerceNotificationAttachment);
	}

	/**
	 * Returns the number of commerce notification attachments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce notification attachments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationAttachment, NoSuchNotificationAttachmentException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce notification attachments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce notification attachments
	 * @param end the upper bound of the range of commerce notification attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification attachments
	 */
	@Override
	public List<CommerceNotificationAttachment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceNotificationAttachment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment
	 * @throws NoSuchNotificationAttachmentException if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceNotificationAttachment> orderByComparator)
		throws NoSuchNotificationAttachmentException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceNotificationAttachment> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification attachments where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce notification attachments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce notification attachments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationAttachment, NoSuchNotificationAttachmentException>
			_collectionPersistenceFinderByCommerceNotificationQueueEntryId;

	/**
	 * Returns an ordered range of all the commerce notification attachments where commerceNotificationQueueEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationAttachmentModelImpl</code>.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntryId the commerce notification queue entry ID
	 * @param start the lower bound of the range of commerce notification attachments
	 * @param end the upper bound of the range of commerce notification attachments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification attachments
	 */
	@Override
	public List<CommerceNotificationAttachment>
		findByCommerceNotificationQueueEntryId(
			long commerceNotificationQueueEntryId, int start, int end,
			OrderByComparator<CommerceNotificationAttachment> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceNotificationQueueEntryId.
			find(
				finderCache, new Object[] {commerceNotificationQueueEntryId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where commerceNotificationQueueEntryId = &#63;.
	 *
	 * @param commerceNotificationQueueEntryId the commerce notification queue entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment
	 * @throws NoSuchNotificationAttachmentException if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment
			findByCommerceNotificationQueueEntryId_First(
				long commerceNotificationQueueEntryId,
				OrderByComparator<CommerceNotificationAttachment>
					orderByComparator)
		throws NoSuchNotificationAttachmentException {

		return _collectionPersistenceFinderByCommerceNotificationQueueEntryId.
			findFirst(
				finderCache, new Object[] {commerceNotificationQueueEntryId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce notification attachment in the ordered set where commerceNotificationQueueEntryId = &#63;.
	 *
	 * @param commerceNotificationQueueEntryId the commerce notification queue entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification attachment, or <code>null</code> if a matching commerce notification attachment could not be found
	 */
	@Override
	public CommerceNotificationAttachment
		fetchByCommerceNotificationQueueEntryId_First(
			long commerceNotificationQueueEntryId,
			OrderByComparator<CommerceNotificationAttachment>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceNotificationQueueEntryId.
			fetchFirst(
				finderCache, new Object[] {commerceNotificationQueueEntryId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce notification attachments where commerceNotificationQueueEntryId = &#63; from the database.
	 *
	 * @param commerceNotificationQueueEntryId the commerce notification queue entry ID
	 */
	@Override
	public void removeByCommerceNotificationQueueEntryId(
		long commerceNotificationQueueEntryId) {

		_collectionPersistenceFinderByCommerceNotificationQueueEntryId.remove(
			finderCache, new Object[] {commerceNotificationQueueEntryId});
	}

	/**
	 * Returns the number of commerce notification attachments where commerceNotificationQueueEntryId = &#63;.
	 *
	 * @param commerceNotificationQueueEntryId the commerce notification queue entry ID
	 * @return the number of matching commerce notification attachments
	 */
	@Override
	public int countByCommerceNotificationQueueEntryId(
		long commerceNotificationQueueEntryId) {

		return _collectionPersistenceFinderByCommerceNotificationQueueEntryId.
			count(finderCache, new Object[] {commerceNotificationQueueEntryId});
	}

	public CommerceNotificationAttachmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commerceNotificationAttachmentId", "CNotificationAttachmentId");
		dbColumnNames.put(
			"commerceNotificationQueueEntryId", "CNotificationQueueEntryId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceNotificationAttachment.class);

		setModelImplClass(CommerceNotificationAttachmentImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceNotificationAttachmentTable.INSTANCE);
	}

	/**
	 * Creates a new commerce notification attachment with the primary key. Does not add the commerce notification attachment to the database.
	 *
	 * @param commerceNotificationAttachmentId the primary key for the new commerce notification attachment
	 * @return the new commerce notification attachment
	 */
	@Override
	public CommerceNotificationAttachment create(
		long commerceNotificationAttachmentId) {

		CommerceNotificationAttachment commerceNotificationAttachment =
			new CommerceNotificationAttachmentImpl();

		commerceNotificationAttachment.setNew(true);
		commerceNotificationAttachment.setPrimaryKey(
			commerceNotificationAttachmentId);

		String uuid = PortalUUIDUtil.generate();

		commerceNotificationAttachment.setUuid(uuid);

		commerceNotificationAttachment.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceNotificationAttachment;
	}

	/**
	 * Removes the commerce notification attachment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment that was removed
	 * @throws NoSuchNotificationAttachmentException if a commerce notification attachment with the primary key could not be found
	 */
	@Override
	public CommerceNotificationAttachment remove(
			long commerceNotificationAttachmentId)
		throws NoSuchNotificationAttachmentException {

		return remove((Serializable)commerceNotificationAttachmentId);
	}

	@Override
	protected CommerceNotificationAttachment removeImpl(
		CommerceNotificationAttachment commerceNotificationAttachment) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceNotificationAttachment)) {
				commerceNotificationAttachment =
					(CommerceNotificationAttachment)session.get(
						CommerceNotificationAttachmentImpl.class,
						commerceNotificationAttachment.getPrimaryKeyObj());
			}

			if (commerceNotificationAttachment != null) {
				session.delete(commerceNotificationAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceNotificationAttachment != null) {
			clearCache(commerceNotificationAttachment);
		}

		return commerceNotificationAttachment;
	}

	@Override
	public CommerceNotificationAttachment updateImpl(
		CommerceNotificationAttachment commerceNotificationAttachment) {

		boolean isNew = commerceNotificationAttachment.isNew();

		if (!(commerceNotificationAttachment instanceof
				CommerceNotificationAttachmentModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceNotificationAttachment.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceNotificationAttachment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceNotificationAttachment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceNotificationAttachment implementation " +
					commerceNotificationAttachment.getClass());
		}

		CommerceNotificationAttachmentModelImpl
			commerceNotificationAttachmentModelImpl =
				(CommerceNotificationAttachmentModelImpl)
					commerceNotificationAttachment;

		if (Validator.isNull(commerceNotificationAttachment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceNotificationAttachment.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceNotificationAttachment.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceNotificationAttachment.setCreateDate(date);
			}
			else {
				commerceNotificationAttachment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceNotificationAttachmentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceNotificationAttachment.setModifiedDate(date);
			}
			else {
				commerceNotificationAttachment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceNotificationAttachment);
			}
			else {
				commerceNotificationAttachment =
					(CommerceNotificationAttachment)session.merge(
						commerceNotificationAttachment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceNotificationAttachment, false);

		if (isNew) {
			commerceNotificationAttachment.setNew(false);
		}

		commerceNotificationAttachment.resetOriginalValues();

		return commerceNotificationAttachment;
	}

	/**
	 * Returns the commerce notification attachment with the primary key or throws a <code>NoSuchNotificationAttachmentException</code> if it could not be found.
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment
	 * @throws NoSuchNotificationAttachmentException if a commerce notification attachment with the primary key could not be found
	 */
	@Override
	public CommerceNotificationAttachment findByPrimaryKey(
			long commerceNotificationAttachmentId)
		throws NoSuchNotificationAttachmentException {

		return findByPrimaryKey((Serializable)commerceNotificationAttachmentId);
	}

	/**
	 * Returns the commerce notification attachment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceNotificationAttachmentId the primary key of the commerce notification attachment
	 * @return the commerce notification attachment, or <code>null</code> if a commerce notification attachment with the primary key could not be found
	 */
	@Override
	public CommerceNotificationAttachment fetchByPrimaryKey(
		long commerceNotificationAttachmentId) {

		return fetchByPrimaryKey(
			(Serializable)commerceNotificationAttachmentId);
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
		return "CNotificationAttachmentId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceNotificationAttachmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceNotificationAttachmentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce notification attachment persistence.
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
			_SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
			_SQL_COUNT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
			CommerceNotificationAttachmentModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceNotificationAttachment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceNotificationAttachment::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceNotificationAttachment::getUuid),
				CommerceNotificationAttachment::getGroupId),
			_SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT_WHERE, "",
			new FinderColumn<>(
				"commerceNotificationAttachment.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceNotificationAttachment::getUuid),
			new FinderColumn<>(
				"commerceNotificationAttachment.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceNotificationAttachment::getGroupId));

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
				_SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
				CommerceNotificationAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationAttachment.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceNotificationAttachment::getUuid),
				new FinderColumn<>(
					"commerceNotificationAttachment.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationAttachment::getCompanyId));

		_collectionPersistenceFinderByCommerceNotificationQueueEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceNotificationQueueEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CNotificationQueueEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceNotificationQueueEntryId",
					new String[] {Long.class.getName()},
					new String[] {"CNotificationQueueEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceNotificationQueueEntryId",
					new String[] {Long.class.getName()},
					new String[] {"CNotificationQueueEntryId"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONATTACHMENT_WHERE,
				CommerceNotificationAttachmentModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationAttachment.",
					"commerceNotificationQueueEntryId",
					"CNotificationQueueEntryId", FinderColumn.Type.LONG, "=",
					true, true,
					CommerceNotificationAttachment::
						getCommerceNotificationQueueEntryId));

		CommerceNotificationAttachmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceNotificationAttachmentUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceNotificationAttachmentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommerceNotificationAttachmentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT =
		"SELECT commerceNotificationAttachment FROM CommerceNotificationAttachment commerceNotificationAttachment";

	private static final String
		_SQL_SELECT_COMMERCENOTIFICATIONATTACHMENT_WHERE =
			"SELECT commerceNotificationAttachment FROM CommerceNotificationAttachment commerceNotificationAttachment WHERE ";

	private static final String
		_SQL_COUNT_COMMERCENOTIFICATIONATTACHMENT_WHERE =
			"SELECT COUNT(commerceNotificationAttachment) FROM CommerceNotificationAttachment commerceNotificationAttachment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceNotificationAttachment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationAttachmentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "commerceNotificationAttachmentId",
			"commerceNotificationQueueEntryId"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1450506681
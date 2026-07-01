/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.order.exception.NoSuchVirtualOrderItemException;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemTable;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemImpl;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemModelImpl;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemPersistence;
import com.liferay.commerce.product.type.virtual.order.service.persistence.CommerceVirtualOrderItemUtil;
import com.liferay.commerce.product.type.virtual.order.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce virtual order item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceVirtualOrderItemPersistence.class)
public class CommerceVirtualOrderItemPersistenceImpl
	extends BasePersistenceImpl
		<CommerceVirtualOrderItem, NoSuchVirtualOrderItemException>
	implements CommerceVirtualOrderItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceVirtualOrderItemUtil</code> to access the commerce virtual order item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceVirtualOrderItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItem, NoSuchVirtualOrderItemException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce virtual order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce virtual order items
	 * @param end the upper bound of the range of commerce virtual order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order items
	 */
	@Override
	public List<CommerceVirtualOrderItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item
	 * @throws NoSuchVirtualOrderItemException if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceVirtualOrderItem> orderByComparator)
		throws NoSuchVirtualOrderItemException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item, or <code>null</code> if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce virtual order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce virtual order items
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceVirtualOrderItem, NoSuchVirtualOrderItemException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce virtual order item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchVirtualOrderItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce virtual order item
	 * @throws NoSuchVirtualOrderItemException if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem findByUUID_G(String uuid, long groupId)
		throws NoSuchVirtualOrderItemException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce virtual order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce virtual order item, or <code>null</code> if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce virtual order item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce virtual order item that was removed
	 */
	@Override
	public CommerceVirtualOrderItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchVirtualOrderItemException {

		CommerceVirtualOrderItem commerceVirtualOrderItem = findByUUID_G(
			uuid, groupId);

		return remove(commerceVirtualOrderItem);
	}

	/**
	 * Returns the number of commerce virtual order items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce virtual order items
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceVirtualOrderItem, NoSuchVirtualOrderItemException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce virtual order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceVirtualOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce virtual order items
	 * @param end the upper bound of the range of commerce virtual order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce virtual order items
	 */
	@Override
	public List<CommerceVirtualOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce virtual order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item
	 * @throws NoSuchVirtualOrderItemException if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceVirtualOrderItem> orderByComparator)
		throws NoSuchVirtualOrderItemException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce virtual order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce virtual order item, or <code>null</code> if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceVirtualOrderItem> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce virtual order items where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce virtual order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce virtual order items
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<CommerceVirtualOrderItem, NoSuchVirtualOrderItemException>
			_uniquePersistenceFinderByCommerceOrderItemId;

	/**
	 * Returns the commerce virtual order item where commerceOrderItemId = &#63; or throws a <code>NoSuchVirtualOrderItemException</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce virtual order item
	 * @throws NoSuchVirtualOrderItemException if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem findByCommerceOrderItemId(
			long commerceOrderItemId)
		throws NoSuchVirtualOrderItemException {

		return _uniquePersistenceFinderByCommerceOrderItemId.find(
			finderCache, new Object[] {commerceOrderItemId});
	}

	/**
	 * Returns the commerce virtual order item where commerceOrderItemId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce virtual order item, or <code>null</code> if a matching commerce virtual order item could not be found
	 */
	@Override
	public CommerceVirtualOrderItem fetchByCommerceOrderItemId(
		long commerceOrderItemId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCommerceOrderItemId.fetch(
			finderCache, new Object[] {commerceOrderItemId}, useFinderCache);
	}

	/**
	 * Removes the commerce virtual order item where commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the commerce virtual order item that was removed
	 */
	@Override
	public CommerceVirtualOrderItem removeByCommerceOrderItemId(
			long commerceOrderItemId)
		throws NoSuchVirtualOrderItemException {

		CommerceVirtualOrderItem commerceVirtualOrderItem =
			findByCommerceOrderItemId(commerceOrderItemId);

		return remove(commerceVirtualOrderItem);
	}

	/**
	 * Returns the number of commerce virtual order items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce virtual order items
	 */
	@Override
	public int countByCommerceOrderItemId(long commerceOrderItemId) {
		return _uniquePersistenceFinderByCommerceOrderItemId.count(
			finderCache, new Object[] {commerceOrderItemId});
	}

	public CommerceVirtualOrderItemPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceVirtualOrderItem.class);

		setModelImplClass(CommerceVirtualOrderItemImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceVirtualOrderItemTable.INSTANCE);
	}

	/**
	 * Creates a new commerce virtual order item with the primary key. Does not add the commerce virtual order item to the database.
	 *
	 * @param commerceVirtualOrderItemId the primary key for the new commerce virtual order item
	 * @return the new commerce virtual order item
	 */
	@Override
	public CommerceVirtualOrderItem create(long commerceVirtualOrderItemId) {
		CommerceVirtualOrderItem commerceVirtualOrderItem =
			new CommerceVirtualOrderItemImpl();

		commerceVirtualOrderItem.setNew(true);
		commerceVirtualOrderItem.setPrimaryKey(commerceVirtualOrderItemId);

		String uuid = PortalUUIDUtil.generate();

		commerceVirtualOrderItem.setUuid(uuid);

		commerceVirtualOrderItem.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceVirtualOrderItem;
	}

	/**
	 * Removes the commerce virtual order item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceVirtualOrderItemId the primary key of the commerce virtual order item
	 * @return the commerce virtual order item that was removed
	 * @throws NoSuchVirtualOrderItemException if a commerce virtual order item with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItem remove(long commerceVirtualOrderItemId)
		throws NoSuchVirtualOrderItemException {

		return remove((Serializable)commerceVirtualOrderItemId);
	}

	@Override
	protected CommerceVirtualOrderItem removeImpl(
		CommerceVirtualOrderItem commerceVirtualOrderItem) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceVirtualOrderItem)) {
				commerceVirtualOrderItem =
					(CommerceVirtualOrderItem)session.get(
						CommerceVirtualOrderItemImpl.class,
						commerceVirtualOrderItem.getPrimaryKeyObj());
			}

			if (commerceVirtualOrderItem != null) {
				session.delete(commerceVirtualOrderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceVirtualOrderItem != null) {
			clearCache(commerceVirtualOrderItem);
		}

		return commerceVirtualOrderItem;
	}

	@Override
	public CommerceVirtualOrderItem updateImpl(
		CommerceVirtualOrderItem commerceVirtualOrderItem) {

		boolean isNew = commerceVirtualOrderItem.isNew();

		if (!(commerceVirtualOrderItem instanceof
				CommerceVirtualOrderItemModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceVirtualOrderItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceVirtualOrderItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceVirtualOrderItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceVirtualOrderItem implementation " +
					commerceVirtualOrderItem.getClass());
		}

		CommerceVirtualOrderItemModelImpl commerceVirtualOrderItemModelImpl =
			(CommerceVirtualOrderItemModelImpl)commerceVirtualOrderItem;

		if (Validator.isNull(commerceVirtualOrderItem.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceVirtualOrderItem.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceVirtualOrderItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceVirtualOrderItem.setCreateDate(date);
			}
			else {
				commerceVirtualOrderItem.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceVirtualOrderItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceVirtualOrderItem.setModifiedDate(date);
			}
			else {
				commerceVirtualOrderItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceVirtualOrderItem);
			}
			else {
				commerceVirtualOrderItem =
					(CommerceVirtualOrderItem)session.merge(
						commerceVirtualOrderItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceVirtualOrderItem, false);

		if (isNew) {
			commerceVirtualOrderItem.setNew(false);
		}

		commerceVirtualOrderItem.resetOriginalValues();

		return commerceVirtualOrderItem;
	}

	/**
	 * Returns the commerce virtual order item with the primary key or throws a <code>NoSuchVirtualOrderItemException</code> if it could not be found.
	 *
	 * @param commerceVirtualOrderItemId the primary key of the commerce virtual order item
	 * @return the commerce virtual order item
	 * @throws NoSuchVirtualOrderItemException if a commerce virtual order item with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItem findByPrimaryKey(
			long commerceVirtualOrderItemId)
		throws NoSuchVirtualOrderItemException {

		return findByPrimaryKey((Serializable)commerceVirtualOrderItemId);
	}

	/**
	 * Returns the commerce virtual order item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceVirtualOrderItemId the primary key of the commerce virtual order item
	 * @return the commerce virtual order item, or <code>null</code> if a commerce virtual order item with the primary key could not be found
	 */
	@Override
	public CommerceVirtualOrderItem fetchByPrimaryKey(
		long commerceVirtualOrderItemId) {

		return fetchByPrimaryKey((Serializable)commerceVirtualOrderItemId);
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
		return "commerceVirtualOrderItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEVIRTUALORDERITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceVirtualOrderItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce virtual order item persistence.
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
			_SQL_SELECT_COMMERCEVIRTUALORDERITEM_WHERE,
			_SQL_COUNT_COMMERCEVIRTUALORDERITEM_WHERE,
			CommerceVirtualOrderItemModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceVirtualOrderItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceVirtualOrderItem::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceVirtualOrderItem::getUuid),
				CommerceVirtualOrderItem::getGroupId),
			_SQL_SELECT_COMMERCEVIRTUALORDERITEM_WHERE, "",
			new FinderColumn<>(
				"commerceVirtualOrderItem.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceVirtualOrderItem::getUuid),
			new FinderColumn<>(
				"commerceVirtualOrderItem.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CommerceVirtualOrderItem::getGroupId));

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
				_SQL_SELECT_COMMERCEVIRTUALORDERITEM_WHERE,
				_SQL_COUNT_COMMERCEVIRTUALORDERITEM_WHERE,
				CommerceVirtualOrderItemModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceVirtualOrderItem.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceVirtualOrderItem::getUuid),
				new FinderColumn<>(
					"commerceVirtualOrderItem.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceVirtualOrderItem::getCompanyId));

		_uniquePersistenceFinderByCommerceOrderItemId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCommerceOrderItemId",
					new String[] {Long.class.getName()},
					new String[] {"commerceOrderItemId"}, 0, 0, false,
					CommerceVirtualOrderItem::getCommerceOrderItemId),
				_SQL_SELECT_COMMERCEVIRTUALORDERITEM_WHERE, "",
				new FinderColumn<>(
					"commerceVirtualOrderItem.", "commerceOrderItemId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceVirtualOrderItem::getCommerceOrderItemId));

		CommerceVirtualOrderItemUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceVirtualOrderItemUtil.setPersistence(null);

		entityCache.removeCache(CommerceVirtualOrderItemImpl.class.getName());
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
		CommerceVirtualOrderItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEVIRTUALORDERITEM =
		"SELECT commerceVirtualOrderItem FROM CommerceVirtualOrderItem commerceVirtualOrderItem";

	private static final String _SQL_SELECT_COMMERCEVIRTUALORDERITEM_WHERE =
		"SELECT commerceVirtualOrderItem FROM CommerceVirtualOrderItem commerceVirtualOrderItem WHERE ";

	private static final String _SQL_COUNT_COMMERCEVIRTUALORDERITEM_WHERE =
		"SELECT COUNT(commerceVirtualOrderItem) FROM CommerceVirtualOrderItem commerceVirtualOrderItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceVirtualOrderItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceVirtualOrderItemPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:195322108
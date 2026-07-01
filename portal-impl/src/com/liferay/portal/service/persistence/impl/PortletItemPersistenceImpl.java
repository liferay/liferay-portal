/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPortletItemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletItemTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortletItemPersistence;
import com.liferay.portal.kernel.service.persistence.PortletItemUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortletItemImpl;
import com.liferay.portal.model.impl.PortletItemModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the portlet item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletItemPersistenceImpl
	extends BasePersistenceImpl<PortletItem, NoSuchPortletItemException>
	implements PortletItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortletItemUtil</code> to access the portlet item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortletItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<PortletItem, NoSuchPortletItemException>
		_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		return _collectionPersistenceFinderByG_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the portlet items where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder<PortletItem, NoSuchPortletItemException>
		_collectionPersistenceFinderByG_P_C;

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_P_C(
		long groupId, String portletId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, portletId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_P_C_First(
			long groupId, String portletId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		return _collectionPersistenceFinderByG_P_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, portletId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_P_C_First(
		long groupId, String portletId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		return _collectionPersistenceFinderByG_P_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, portletId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_P_C(
		long groupId, String portletId, long classNameId) {

		_collectionPersistenceFinderByG_P_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, portletId, classNameId});
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_P_C(long groupId, String portletId, long classNameId) {
		return _collectionPersistenceFinderByG_P_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, portletId, classNameId});
	}

	private UniquePersistenceFinder<PortletItem, NoSuchPortletItemException>
		_uniquePersistenceFinderByG_N_P_C;

	/**
	 * Returns the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; or throws a <code>NoSuchPortletItemException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_N_P_C(
			long groupId, String name, String portletId, long classNameId)
		throws NoSuchPortletItemException {

		return _uniquePersistenceFinderByG_N_P_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId, classNameId});
	}

	/**
	 * Returns the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_N_P_C(
		long groupId, String name, String portletId, long classNameId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_N_P_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId, classNameId},
			useFinderCache);
	}

	/**
	 * Removes the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the portlet item that was removed
	 */
	@Override
	public PortletItem removeByG_N_P_C(
			long groupId, String name, String portletId, long classNameId)
		throws NoSuchPortletItemException {

		PortletItem portletItem = findByG_N_P_C(
			groupId, name, portletId, classNameId);

		return remove(portletItem);
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_N_P_C(
		long groupId, String name, String portletId, long classNameId) {

		return _uniquePersistenceFinderByG_N_P_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, name, portletId, classNameId});
	}

	public PortletItemPersistenceImpl() {
		setModelClass(PortletItem.class);

		setModelImplClass(PortletItemImpl.class);
		setModelPKClass(long.class);

		setTable(PortletItemTable.INSTANCE);
	}

	/**
	 * Creates a new portlet item with the primary key. Does not add the portlet item to the database.
	 *
	 * @param portletItemId the primary key for the new portlet item
	 * @return the new portlet item
	 */
	@Override
	public PortletItem create(long portletItemId) {
		PortletItem portletItem = new PortletItemImpl();

		portletItem.setNew(true);
		portletItem.setPrimaryKey(portletItemId);

		portletItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portletItem;
	}

	/**
	 * Removes the portlet item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item that was removed
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem remove(long portletItemId)
		throws NoSuchPortletItemException {

		return remove((Serializable)portletItemId);
	}

	@Override
	protected PortletItem removeImpl(PortletItem portletItem) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portletItem)) {
				portletItem = (PortletItem)session.get(
					PortletItemImpl.class, portletItem.getPrimaryKeyObj());
			}

			if (portletItem != null) {
				session.delete(portletItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portletItem != null) {
			clearCache(portletItem);
		}

		return portletItem;
	}

	@Override
	public PortletItem updateImpl(PortletItem portletItem) {
		boolean isNew = portletItem.isNew();

		if (!(portletItem instanceof PortletItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portletItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(portletItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portletItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortletItem implementation " +
					portletItem.getClass());
		}

		PortletItemModelImpl portletItemModelImpl =
			(PortletItemModelImpl)portletItem;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (portletItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				portletItem.setCreateDate(date);
			}
			else {
				portletItem.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!portletItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				portletItem.setModifiedDate(date);
			}
			else {
				portletItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(portletItem);
			}
			else {
				portletItem = (PortletItem)session.merge(portletItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(portletItem, false);

		if (isNew) {
			portletItem.setNew(false);
		}

		portletItem.resetOriginalValues();

		return portletItem;
	}

	/**
	 * Returns the portlet item with the primary key or throws a <code>NoSuchPortletItemException</code> if it could not be found.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem findByPrimaryKey(long portletItemId)
		throws NoSuchPortletItemException {

		return findByPrimaryKey((Serializable)portletItemId);
	}

	/**
	 * Returns the portlet item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item, or <code>null</code> if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem fetchByPrimaryKey(long portletItemId) {
		return fetchByPrimaryKey((Serializable)portletItemId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "portletItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTLETITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PortletItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the portlet item persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_PORTLETITEM_WHERE, _SQL_COUNT_PORTLETITEM_WHERE,
			PortletItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"portletItem.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, PortletItem::getGroupId),
			new FinderColumn<>(
				"portletItem.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, PortletItem::getClassNameId));

		_collectionPersistenceFinderByG_P_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "portletId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "portletId", "classNameId"}, 0, 2,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "portletId", "classNameId"}, 0, 2,
				false, null),
			_SQL_SELECT_PORTLETITEM_WHERE, _SQL_COUNT_PORTLETITEM_WHERE,
			PortletItemModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"portletItem.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, PortletItem::getGroupId),
			new FinderColumn<>(
				"portletItem.", "portletId", FinderColumn.Type.STRING, "=",
				true, true, PortletItem::getPortletId),
			new FinderColumn<>(
				"portletItem.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, PortletItem::getClassNameId));

		_uniquePersistenceFinderByG_N_P_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_N_P_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Long.class.getName()
				},
				new String[] {"groupId", "name", "portletId", "classNameId"}, 2,
				6, false, PortletItem::getGroupId,
				convertCaseFunction(PortletItem::getName),
				convertNullFunction(PortletItem::getPortletId),
				PortletItem::getClassNameId),
			_SQL_SELECT_PORTLETITEM_WHERE, "",
			new FinderColumn<>(
				"portletItem.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, PortletItem::getGroupId),
			new FinderColumn<>(
				"portletItem.", "name", FinderColumn.Type.STRING, "=", false,
				true, PortletItem::getName),
			new FinderColumn<>(
				"portletItem.", "portletId", FinderColumn.Type.STRING, "=",
				true, true, PortletItem::getPortletId),
			new FinderColumn<>(
				"portletItem.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, PortletItem::getClassNameId));

		PortletItemUtil.setPersistence(this);
	}

	public void destroy() {
		PortletItemUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortletItemImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PortletItemModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PORTLETITEM =
		"SELECT portletItem FROM PortletItem portletItem";

	private static final String _SQL_SELECT_PORTLETITEM_WHERE =
		"SELECT portletItem FROM PortletItem portletItem WHERE ";

	private static final String _SQL_COUNT_PORTLETITEM_WHERE =
		"SELECT COUNT(portletItem) FROM PortletItem portletItem WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortletItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletItemPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:238710568
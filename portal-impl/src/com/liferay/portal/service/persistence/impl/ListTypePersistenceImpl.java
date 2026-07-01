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
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.ListTypePersistence;
import com.liferay.portal.kernel.service.persistence.ListTypeUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.ListTypeImpl;
import com.liferay.portal.model.impl.ListTypeModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the list type service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ListTypePersistenceImpl
	extends BasePersistenceImpl<ListType, NoSuchListTypeException>
	implements ListTypePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ListTypeUtil</code> to access the list type persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ListTypeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ListType, NoSuchListTypeException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the list types where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of list types
	 * @param end the upper bound of the range of list types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list types
	 */
	@Override
	public List<ListType> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ListType> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type
	 * @throws NoSuchListTypeException if a matching list type could not be found
	 */
	@Override
	public ListType findByUuid_First(
			String uuid, OrderByComparator<ListType> orderByComparator)
		throws NoSuchListTypeException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first list type in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type, or <code>null</code> if a matching list type could not be found
	 */
	@Override
	public ListType fetchByUuid_First(
		String uuid, OrderByComparator<ListType> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the list types where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of list types where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching list types
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder<ListType, NoSuchListTypeException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the list types where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of list types
	 * @param end the upper bound of the range of list types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list types
	 */
	@Override
	public List<ListType> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ListType> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type
	 * @throws NoSuchListTypeException if a matching list type could not be found
	 */
	@Override
	public ListType findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ListType> orderByComparator)
		throws NoSuchListTypeException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first list type in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type, or <code>null</code> if a matching list type could not be found
	 */
	@Override
	public ListType fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ListType> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the list types where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of list types where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching list types
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<ListType, NoSuchListTypeException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the list types where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of list types
	 * @param end the upper bound of the range of list types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list types
	 */
	@Override
	public List<ListType> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ListType> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type
	 * @throws NoSuchListTypeException if a matching list type could not be found
	 */
	@Override
	public ListType findByCompanyId_First(
			long companyId, OrderByComparator<ListType> orderByComparator)
		throws NoSuchListTypeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first list type in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type, or <code>null</code> if a matching list type could not be found
	 */
	@Override
	public ListType fetchByCompanyId_First(
		long companyId, OrderByComparator<ListType> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the list types where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of list types where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching list types
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<ListType, NoSuchListTypeException>
		_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the list types where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of list types
	 * @param end the upper bound of the range of list types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list types
	 */
	@Override
	public List<ListType> findByC_T(
		long companyId, String type, int start, int end,
		OrderByComparator<ListType> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type
	 * @throws NoSuchListTypeException if a matching list type could not be found
	 */
	@Override
	public ListType findByC_T_First(
			long companyId, String type,
			OrderByComparator<ListType> orderByComparator)
		throws NoSuchListTypeException {

		return _collectionPersistenceFinderByC_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			orderByComparator);
	}

	/**
	 * Returns the first list type in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type, or <code>null</code> if a matching list type could not be found
	 */
	@Override
	public ListType fetchByC_T_First(
		long companyId, String type,
		OrderByComparator<ListType> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type},
			orderByComparator);
	}

	/**
	 * Removes all the list types where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, String type) {
		_collectionPersistenceFinderByC_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type});
	}

	/**
	 * Returns the number of list types where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching list types
	 */
	@Override
	public int countByC_T(long companyId, String type) {
		return _collectionPersistenceFinderByC_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, type});
	}

	private UniquePersistenceFinder<ListType, NoSuchListTypeException>
		_uniquePersistenceFinderByC_N_T;

	/**
	 * Returns the list type where companyId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchListTypeException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param type the type
	 * @return the matching list type
	 * @throws NoSuchListTypeException if a matching list type could not be found
	 */
	@Override
	public ListType findByC_N_T(long companyId, String name, String type)
		throws NoSuchListTypeException {

		return _uniquePersistenceFinderByC_N_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, type});
	}

	/**
	 * Returns the list type where companyId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching list type, or <code>null</code> if a matching list type could not be found
	 */
	@Override
	public ListType fetchByC_N_T(
		long companyId, String name, String type, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N_T.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, type}, useFinderCache);
	}

	/**
	 * Removes the list type where companyId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param type the type
	 * @return the list type that was removed
	 */
	@Override
	public ListType removeByC_N_T(long companyId, String name, String type)
		throws NoSuchListTypeException {

		ListType listType = findByC_N_T(companyId, name, type);

		return remove(listType);
	}

	/**
	 * Returns the number of list types where companyId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching list types
	 */
	@Override
	public int countByC_N_T(long companyId, String name, String type) {
		return _uniquePersistenceFinderByC_N_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, type});
	}

	public ListTypePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ListType.class);

		setModelImplClass(ListTypeImpl.class);
		setModelPKClass(long.class);

		setTable(ListTypeTable.INSTANCE);
	}

	/**
	 * Creates a new list type with the primary key. Does not add the list type to the database.
	 *
	 * @param listTypeId the primary key for the new list type
	 * @return the new list type
	 */
	@Override
	public ListType create(long listTypeId) {
		ListType listType = new ListTypeImpl();

		listType.setNew(true);
		listType.setPrimaryKey(listTypeId);

		String uuid = PortalUUIDUtil.generate();

		listType.setUuid(uuid);

		listType.setCompanyId(CompanyThreadLocal.getCompanyId());

		return listType;
	}

	/**
	 * Removes the list type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param listTypeId the primary key of the list type
	 * @return the list type that was removed
	 * @throws NoSuchListTypeException if a list type with the primary key could not be found
	 */
	@Override
	public ListType remove(long listTypeId) throws NoSuchListTypeException {
		return remove((Serializable)listTypeId);
	}

	@Override
	protected ListType removeImpl(ListType listType) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(listType)) {
				listType = (ListType)session.get(
					ListTypeImpl.class, listType.getPrimaryKeyObj());
			}

			if (listType != null) {
				session.delete(listType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (listType != null) {
			clearCache(listType);
		}

		return listType;
	}

	@Override
	public ListType updateImpl(ListType listType) {
		boolean isNew = listType.isNew();

		if (!(listType instanceof ListTypeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(listType.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(listType);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in listType proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ListType implementation " +
					listType.getClass());
		}

		ListTypeModelImpl listTypeModelImpl = (ListTypeModelImpl)listType;

		if (Validator.isNull(listType.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			listType.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (listType.getCreateDate() == null)) {
			if (serviceContext == null) {
				listType.setCreateDate(date);
			}
			else {
				listType.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!listTypeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				listType.setModifiedDate(date);
			}
			else {
				listType.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(listType);
			}
			else {
				listType = (ListType)session.merge(listType);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(listType, false);

		if (isNew) {
			listType.setNew(false);
		}

		listType.resetOriginalValues();

		return listType;
	}

	/**
	 * Returns the list type with the primary key or throws a <code>NoSuchListTypeException</code> if it could not be found.
	 *
	 * @param listTypeId the primary key of the list type
	 * @return the list type
	 * @throws NoSuchListTypeException if a list type with the primary key could not be found
	 */
	@Override
	public ListType findByPrimaryKey(long listTypeId)
		throws NoSuchListTypeException {

		return findByPrimaryKey((Serializable)listTypeId);
	}

	/**
	 * Returns the list type with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param listTypeId the primary key of the list type
	 * @return the list type, or <code>null</code> if a list type with the primary key could not be found
	 */
	@Override
	public ListType fetchByPrimaryKey(long listTypeId) {
		return fetchByPrimaryKey((Serializable)listTypeId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "listTypeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LISTTYPE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ListTypeModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the list type persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_LISTTYPE_WHERE, _SQL_COUNT_LISTTYPE_WHERE,
			ListTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"listType.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, ListType::getUuid));

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
				_SQL_SELECT_LISTTYPE_WHERE, _SQL_COUNT_LISTTYPE_WHERE,
				ListTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"listType.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, ListType::getUuid),
				new FinderColumn<>(
					"listType.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, ListType::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_LISTTYPE_WHERE, _SQL_COUNT_LISTTYPE_WHERE,
				ListTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"listType.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, ListType::getCompanyId));

		_collectionPersistenceFinderByC_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "type_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "type_"}, 0, 2, false, null),
			_SQL_SELECT_LISTTYPE_WHERE, _SQL_COUNT_LISTTYPE_WHERE,
			ListTypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"listType.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ListType::getCompanyId),
			new FinderColumn<>(
				"listType.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, ListType::getType));

		_uniquePersistenceFinderByC_N_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "name", "type_"}, 0, 6, false,
				ListType::getCompanyId, convertNullFunction(ListType::getName),
				convertNullFunction(ListType::getType)),
			_SQL_SELECT_LISTTYPE_WHERE, "",
			new FinderColumn<>(
				"listType.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ListType::getCompanyId),
			new FinderColumn<>(
				"listType.", "name", FinderColumn.Type.STRING, "=", true, true,
				ListType::getName),
			new FinderColumn<>(
				"listType.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, ListType::getType));

		ListTypeUtil.setPersistence(this);
	}

	public void destroy() {
		ListTypeUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ListTypeImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ListTypeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LISTTYPE =
		"SELECT listType FROM ListType listType";

	private static final String _SQL_SELECT_LISTTYPE_WHERE =
		"SELECT listType FROM ListType listType WHERE ";

	private static final String _SQL_COUNT_LISTTYPE_WHERE =
		"SELECT COUNT(listType) FROM ListType listType WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ListType exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ListTypePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:393409442
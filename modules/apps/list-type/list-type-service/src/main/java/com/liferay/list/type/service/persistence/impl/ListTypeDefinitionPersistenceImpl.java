/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.persistence.impl;

import com.liferay.list.type.exception.DuplicateListTypeDefinitionExternalReferenceCodeException;
import com.liferay.list.type.exception.NoSuchListTypeDefinitionException;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeDefinitionTable;
import com.liferay.list.type.model.impl.ListTypeDefinitionImpl;
import com.liferay.list.type.model.impl.ListTypeDefinitionModelImpl;
import com.liferay.list.type.service.persistence.ListTypeDefinitionPersistence;
import com.liferay.list.type.service.persistence.ListTypeDefinitionUtil;
import com.liferay.list.type.service.persistence.impl.constants.ListTypePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the list type definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = ListTypeDefinitionPersistence.class)
public class ListTypeDefinitionPersistenceImpl
	extends BasePersistenceImpl
		<ListTypeDefinition, NoSuchListTypeDefinitionException>
	implements ListTypeDefinitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ListTypeDefinitionUtil</code> to access the list type definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ListTypeDefinitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<ListTypeDefinition, NoSuchListTypeDefinitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the list type definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type definitions
	 */
	@Override
	public List<ListTypeDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first list type definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition
	 * @throws NoSuchListTypeDefinitionException if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition findByUuid_First(
			String uuid,
			OrderByComparator<ListTypeDefinition> orderByComparator)
		throws NoSuchListTypeDefinitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first list type definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition, or <code>null</code> if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition fetchByUuid_First(
		String uuid, OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the list type definitions that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching list type definitions that the user has permission to view
	 */
	@Override
	public List<ListTypeDefinition> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the list type definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of list type definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching list type definitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of list type definitions that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching list type definitions that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<ListTypeDefinition, NoSuchListTypeDefinitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the list type definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type definitions
	 */
	@Override
	public List<ListTypeDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition
	 * @throws NoSuchListTypeDefinitionException if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ListTypeDefinition> orderByComparator)
		throws NoSuchListTypeDefinitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first list type definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition, or <code>null</code> if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the list type definitions that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching list type definitions that the user has permission to view
	 */
	@Override
	public List<ListTypeDefinition> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the list type definitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of list type definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching list type definitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of list type definitions that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching list type definitions that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<ListTypeDefinition, NoSuchListTypeDefinitionException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the list type definitions where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching list type definitions
	 */
	@Override
	public List<ListTypeDefinition> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first list type definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition
	 * @throws NoSuchListTypeDefinitionException if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition findByC_U_First(
			long companyId, long userId,
			OrderByComparator<ListTypeDefinition> orderByComparator)
		throws NoSuchListTypeDefinitionException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first list type definition in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching list type definition, or <code>null</code> if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the list type definitions that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching list type definitions that the user has permission to view
	 */
	@Override
	public List<ListTypeDefinition> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<ListTypeDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_U.filterFind(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the list type definitions where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of list type definitions where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching list type definitions
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of list type definitions that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching list type definitions that the user has permission to view
	 */
	@Override
	public int filterCountByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.filterCount(
			finderCache, new Object[] {companyId, userId}, companyId, 0);
	}

	private UniquePersistenceFinder
		<ListTypeDefinition, NoSuchListTypeDefinitionException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the list type definition where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchListTypeDefinitionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching list type definition
	 * @throws NoSuchListTypeDefinitionException if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchListTypeDefinitionException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the list type definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching list type definition, or <code>null</code> if a matching list type definition could not be found
	 */
	@Override
	public ListTypeDefinition fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the list type definition where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the list type definition that was removed
	 */
	@Override
	public ListTypeDefinition removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchListTypeDefinitionException {

		ListTypeDefinition listTypeDefinition = findByERC_C(
			externalReferenceCode, companyId);

		return remove(listTypeDefinition);
	}

	/**
	 * Returns the number of list type definitions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching list type definitions
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public ListTypeDefinitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ListTypeDefinition.class);

		setModelImplClass(ListTypeDefinitionImpl.class);
		setModelPKClass(long.class);

		setTable(ListTypeDefinitionTable.INSTANCE);
	}

	/**
	 * Creates a new list type definition with the primary key. Does not add the list type definition to the database.
	 *
	 * @param listTypeDefinitionId the primary key for the new list type definition
	 * @return the new list type definition
	 */
	@Override
	public ListTypeDefinition create(long listTypeDefinitionId) {
		ListTypeDefinition listTypeDefinition = new ListTypeDefinitionImpl();

		listTypeDefinition.setNew(true);
		listTypeDefinition.setPrimaryKey(listTypeDefinitionId);

		String uuid = PortalUUIDUtil.generate();

		listTypeDefinition.setUuid(uuid);

		listTypeDefinition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return listTypeDefinition;
	}

	/**
	 * Removes the list type definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param listTypeDefinitionId the primary key of the list type definition
	 * @return the list type definition that was removed
	 * @throws NoSuchListTypeDefinitionException if a list type definition with the primary key could not be found
	 */
	@Override
	public ListTypeDefinition remove(long listTypeDefinitionId)
		throws NoSuchListTypeDefinitionException {

		return remove((Serializable)listTypeDefinitionId);
	}

	@Override
	protected ListTypeDefinition removeImpl(
		ListTypeDefinition listTypeDefinition) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(listTypeDefinition)) {
				listTypeDefinition = (ListTypeDefinition)session.get(
					ListTypeDefinitionImpl.class,
					listTypeDefinition.getPrimaryKeyObj());
			}

			if (listTypeDefinition != null) {
				session.delete(listTypeDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (listTypeDefinition != null) {
			clearCache(listTypeDefinition);
		}

		return listTypeDefinition;
	}

	@Override
	public ListTypeDefinition updateImpl(
		ListTypeDefinition listTypeDefinition) {

		boolean isNew = listTypeDefinition.isNew();

		if (!(listTypeDefinition instanceof ListTypeDefinitionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(listTypeDefinition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					listTypeDefinition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in listTypeDefinition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ListTypeDefinition implementation " +
					listTypeDefinition.getClass());
		}

		ListTypeDefinitionModelImpl listTypeDefinitionModelImpl =
			(ListTypeDefinitionModelImpl)listTypeDefinition;

		if (Validator.isNull(listTypeDefinition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			listTypeDefinition.setUuid(uuid);
		}

		if (Validator.isNull(listTypeDefinition.getExternalReferenceCode())) {
			listTypeDefinition.setExternalReferenceCode(
				listTypeDefinition.getUuid());
		}
		else {
			if (!Objects.equals(
					listTypeDefinitionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					listTypeDefinition.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = listTypeDefinition.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = listTypeDefinition.getPrimaryKey();
					}

					try {
						listTypeDefinition.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ListTypeDefinition.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								listTypeDefinition.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ListTypeDefinition ercListTypeDefinition = fetchByERC_C(
				listTypeDefinition.getExternalReferenceCode(),
				listTypeDefinition.getCompanyId());

			if (isNew) {
				if (ercListTypeDefinition != null) {
					throw new DuplicateListTypeDefinitionExternalReferenceCodeException(
						"Duplicate list type definition with external reference code " +
							listTypeDefinition.getExternalReferenceCode() +
								" and company " +
									listTypeDefinition.getCompanyId());
				}
			}
			else {
				if ((ercListTypeDefinition != null) &&
					(listTypeDefinition.getListTypeDefinitionId() !=
						ercListTypeDefinition.getListTypeDefinitionId())) {

					throw new DuplicateListTypeDefinitionExternalReferenceCodeException(
						"Duplicate list type definition with external reference code " +
							listTypeDefinition.getExternalReferenceCode() +
								" and company " +
									listTypeDefinition.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (listTypeDefinition.getCreateDate() == null)) {
			if (serviceContext == null) {
				listTypeDefinition.setCreateDate(date);
			}
			else {
				listTypeDefinition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!listTypeDefinitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				listTypeDefinition.setModifiedDate(date);
			}
			else {
				listTypeDefinition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(listTypeDefinition);
			}
			else {
				listTypeDefinition = (ListTypeDefinition)session.merge(
					listTypeDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(listTypeDefinition, false);

		if (isNew) {
			listTypeDefinition.setNew(false);
		}

		listTypeDefinition.resetOriginalValues();

		return listTypeDefinition;
	}

	/**
	 * Returns the list type definition with the primary key or throws a <code>NoSuchListTypeDefinitionException</code> if it could not be found.
	 *
	 * @param listTypeDefinitionId the primary key of the list type definition
	 * @return the list type definition
	 * @throws NoSuchListTypeDefinitionException if a list type definition with the primary key could not be found
	 */
	@Override
	public ListTypeDefinition findByPrimaryKey(long listTypeDefinitionId)
		throws NoSuchListTypeDefinitionException {

		return findByPrimaryKey((Serializable)listTypeDefinitionId);
	}

	/**
	 * Returns the list type definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param listTypeDefinitionId the primary key of the list type definition
	 * @return the list type definition, or <code>null</code> if a list type definition with the primary key could not be found
	 */
	@Override
	public ListTypeDefinition fetchByPrimaryKey(long listTypeDefinitionId) {
		return fetchByPrimaryKey((Serializable)listTypeDefinitionId);
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
		return "listTypeDefinitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LISTTYPEDEFINITION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ListTypeDefinitionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the list type definition persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_LISTTYPEDEFINITION_WHERE,
				_SQL_COUNT_LISTTYPEDEFINITION_WHERE,
				ListTypeDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"listTypeDefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ListTypeDefinition::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_LISTTYPEDEFINITION_WHERE,
				_SQL_COUNT_LISTTYPEDEFINITION_WHERE,
				ListTypeDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"listTypeDefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ListTypeDefinition::getUuid),
				new FinderColumn<>(
					"listTypeDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ListTypeDefinition::getCompanyId));

		_collectionPersistenceFinderByC_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, false),
				_SQL_SELECT_LISTTYPEDEFINITION_WHERE,
				_SQL_COUNT_LISTTYPEDEFINITION_WHERE,
				ListTypeDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"listTypeDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ListTypeDefinition::getCompanyId),
				new FinderColumn<>(
					"listTypeDefinition.", "userId", FinderColumn.Type.LONG,
					"=", true, true, ListTypeDefinition::getUserId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					ListTypeDefinition::getExternalReferenceCode),
				ListTypeDefinition::getCompanyId),
			_SQL_SELECT_LISTTYPEDEFINITION_WHERE, "",
			new FinderColumn<>(
				"listTypeDefinition.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ListTypeDefinition::getExternalReferenceCode),
			new FinderColumn<>(
				"listTypeDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ListTypeDefinition::getCompanyId));

		ListTypeDefinitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ListTypeDefinitionUtil.setPersistence(null);

		entityCache.removeCache(ListTypeDefinitionImpl.class.getName());
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ListTypePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ListTypeDefinitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LISTTYPEDEFINITION =
		"SELECT listTypeDefinition FROM ListTypeDefinition listTypeDefinition";

	private static final String _SQL_SELECT_LISTTYPEDEFINITION_WHERE =
		"SELECT listTypeDefinition FROM ListTypeDefinition listTypeDefinition WHERE ";

	private static final String _SQL_COUNT_LISTTYPEDEFINITION_WHERE =
		"SELECT COUNT(listTypeDefinition) FROM ListTypeDefinition listTypeDefinition WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ListTypeDefinition exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ListTypeDefinitionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1710476135
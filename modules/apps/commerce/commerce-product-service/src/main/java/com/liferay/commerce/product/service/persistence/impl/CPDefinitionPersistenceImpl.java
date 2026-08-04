/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionTable;
import com.liferay.commerce.product.model.impl.CPDefinitionImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the cp definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionPersistence.class)
public class CPDefinitionPersistenceImpl
	extends BasePersistenceImpl<CPDefinition, NoSuchCPDefinitionException>
	implements CPDefinitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionUtil</code> to access the cp definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByUuid_First(
			String uuid, OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByUuid_First(
		String uuid, OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CPDefinition, NoSuchCPDefinitionException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition that was removed
	 */
	@Override
	public CPDefinition removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionException {

		CPDefinition cpDefinition = findByUUID_G(uuid, groupId);

		return remove(cpDefinition);
	}

	/**
	 * Returns the number of cp definitions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cp definitions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByGroupId_First(
			long groupId, OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByGroupId_First(
		long groupId, OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp definitions that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definitions that the user has permission to view
	 */
	@Override
	public List<CPDefinition> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the cp definitions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp definitions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp definitions that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definitions that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByCompanyId_First(
			long companyId, OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByCompanyId_First(
		long companyId, OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByCProductId;

	/**
	 * Returns an ordered range of all the cp definitions where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByCProductId_First(
			long CProductId, OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByCProductId.findFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByCProductId_First(
		long CProductId, OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCProductId.fetchFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		_collectionPersistenceFinderByCProductId.remove(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the number of cp definitions where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _collectionPersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByCPTaxCategoryId;

	/**
	 * Returns an ordered range of all the cp definitions where CPTaxCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByCPTaxCategoryId(
		long CPTaxCategoryId, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPTaxCategoryId.find(
			finderCache, new Object[] {CPTaxCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByCPTaxCategoryId_First(
			long CPTaxCategoryId,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByCPTaxCategoryId.findFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByCPTaxCategoryId_First(
		long CPTaxCategoryId,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCPTaxCategoryId.fetchFirst(
			finderCache, new Object[] {CPTaxCategoryId}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where CPTaxCategoryId = &#63; from the database.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 */
	@Override
	public void removeByCPTaxCategoryId(long CPTaxCategoryId) {
		_collectionPersistenceFinderByCPTaxCategoryId.remove(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	/**
	 * Returns the number of cp definitions where CPTaxCategoryId = &#63;.
	 *
	 * @param CPTaxCategoryId the cp tax category ID
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByCPTaxCategoryId(long CPTaxCategoryId) {
		return _collectionPersistenceFinderByCPTaxCategoryId.count(
			finderCache, new Object[] {CPTaxCategoryId});
	}

	private FilterCollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByG_SE;

	/**
	 * Returns an ordered range of all the cp definitions where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByG_SE(
		long groupId, boolean subscriptionEnabled, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SE.find(
			finderCache, new Object[] {groupId, subscriptionEnabled}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByG_SE_First(
			long groupId, boolean subscriptionEnabled,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByG_SE.findFirst(
			finderCache, new Object[] {groupId, subscriptionEnabled},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByG_SE_First(
		long groupId, boolean subscriptionEnabled,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_SE.fetchFirst(
			finderCache, new Object[] {groupId, subscriptionEnabled},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp definitions that the user has permissions to view where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definitions that the user has permission to view
	 */
	@Override
	public List<CPDefinition> filterFindByG_SE(
		long groupId, boolean subscriptionEnabled, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_SE.filterFind(
			finderCache, new Object[] {groupId, subscriptionEnabled}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the cp definitions where groupId = &#63; and subscriptionEnabled = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 */
	@Override
	public void removeByG_SE(long groupId, boolean subscriptionEnabled) {
		_collectionPersistenceFinderByG_SE.remove(
			finderCache, new Object[] {groupId, subscriptionEnabled});
	}

	/**
	 * Returns the number of cp definitions where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByG_SE(long groupId, boolean subscriptionEnabled) {
		return _collectionPersistenceFinderByG_SE.count(
			finderCache, new Object[] {groupId, subscriptionEnabled});
	}

	/**
	 * Returns the number of cp definitions that the user has permission to view where groupId = &#63; and subscriptionEnabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param subscriptionEnabled the subscription enabled
	 * @return the number of matching cp definitions that the user has permission to view
	 */
	@Override
	public int filterCountByG_SE(long groupId, boolean subscriptionEnabled) {
		return _collectionPersistenceFinderByG_SE.filterCount(
			finderCache, new Object[] {groupId, subscriptionEnabled}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the cp definitions where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByG_S_First(
			long groupId, int status,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp definitions that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definitions that the user has permission to view
	 */
	@Override
	public List<CPDefinition> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_S.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the cp definitions where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of cp definitions where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of cp definitions that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp definitions that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private UniquePersistenceFinder<CPDefinition, NoSuchCPDefinitionException>
		_uniquePersistenceFinderByC_V;

	/**
	 * Returns the cp definition where CProductId = &#63; and version = &#63; or throws a <code>NoSuchCPDefinitionException</code> if it could not be found.
	 *
	 * @param CProductId the c product ID
	 * @param version the version
	 * @return the matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByC_V(long CProductId, int version)
		throws NoSuchCPDefinitionException {

		return _uniquePersistenceFinderByC_V.find(
			finderCache, new Object[] {CProductId, version});
	}

	/**
	 * Returns the cp definition where CProductId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CProductId the c product ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByC_V(
		long CProductId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_V.fetch(
			finderCache, new Object[] {CProductId, version}, useFinderCache);
	}

	/**
	 * Removes the cp definition where CProductId = &#63; and version = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param version the version
	 * @return the cp definition that was removed
	 */
	@Override
	public CPDefinition removeByC_V(long CProductId, int version)
		throws NoSuchCPDefinitionException {

		CPDefinition cpDefinition = findByC_V(CProductId, version);

		return remove(cpDefinition);
	}

	/**
	 * Returns the number of cp definitions where CProductId = &#63; and version = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param version the version
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByC_V(long CProductId, int version) {
		return _uniquePersistenceFinderByC_V.count(
			finderCache, new Object[] {CProductId, version});
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the cp definitions where CProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByC_S(
		long CProductId, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {CProductId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByC_S_First(
			long CProductId, int status,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {CProductId, status}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByC_S_First(
		long CProductId, int status,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {CProductId, status}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where CProductId = &#63; and status = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long CProductId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {CProductId, status});
	}

	/**
	 * Returns the number of cp definitions where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByC_S(long CProductId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {CProductId, status});
	}

	private CollectionPersistenceFinder
		<CPDefinition, NoSuchCPDefinitionException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the cp definitions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp definitions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @return the range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp definitions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp definitions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definitions
	 * @param end the upper bound of the range of cp definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definitions
	 */
	@Override
	public List<CPDefinition> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition
	 * @throws NoSuchCPDefinitionException if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPDefinition> orderByComparator)
		throws NoSuchCPDefinitionException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first cp definition in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition, or <code>null</code> if a matching cp definition could not be found
	 */
	@Override
	public CPDefinition fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPDefinition> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the cp definitions where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByLtD_S(Date displayDate, int status) {
		_collectionPersistenceFinderByLtD_S.remove(
			finderCache, new Object[] {displayDate, status});
	}

	/**
	 * Returns the number of cp definitions where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp definitions
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	public CPDefinitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"deliverySubscriptionTypeSettings", "deliverySubTypeSettings");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinition.class);

		setModelImplClass(CPDefinitionImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition with the primary key. Does not add the cp definition to the database.
	 *
	 * @param CPDefinitionId the primary key for the new cp definition
	 * @return the new cp definition
	 */
	@Override
	public CPDefinition create(long CPDefinitionId) {
		CPDefinition cpDefinition = new CPDefinitionImpl();

		cpDefinition.setNew(true);
		cpDefinition.setPrimaryKey(CPDefinitionId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinition.setUuid(uuid);

		cpDefinition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpDefinition;
	}

	/**
	 * Removes the cp definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition that was removed
	 * @throws NoSuchCPDefinitionException if a cp definition with the primary key could not be found
	 */
	@Override
	public CPDefinition remove(long CPDefinitionId)
		throws NoSuchCPDefinitionException {

		return remove((Serializable)CPDefinitionId);
	}

	@Override
	protected CPDefinition removeImpl(CPDefinition cpDefinition) {
		cpDefinitionLocalizationPersistence.removeByCPDefinitionId(
			cpDefinition.getCPDefinitionId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinition)) {
				cpDefinition = (CPDefinition)session.get(
					CPDefinitionImpl.class, cpDefinition.getPrimaryKeyObj());
			}

			if ((cpDefinition != null) &&
				ctPersistenceHelper.isRemove(cpDefinition)) {

				session.delete(cpDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinition != null) {
			clearCache(cpDefinition);
		}

		return cpDefinition;
	}

	@Override
	public CPDefinition updateImpl(CPDefinition cpDefinition) {
		boolean isNew = cpDefinition.isNew();

		if (!(cpDefinition instanceof CPDefinitionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinition implementation " +
					cpDefinition.getClass());
		}

		CPDefinitionModelImpl cpDefinitionModelImpl =
			(CPDefinitionModelImpl)cpDefinition;

		if (Validator.isNull(cpDefinition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinition.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinition.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinition.setCreateDate(date);
			}
			else {
				cpDefinition.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinition.setModifiedDate(date);
			}
			else {
				cpDefinition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDefinition)) {
				if (!isNew) {
					session.evict(
						CPDefinitionImpl.class,
						cpDefinition.getPrimaryKeyObj());
				}

				session.save(cpDefinition);
			}
			else {
				cpDefinition = (CPDefinition)session.merge(cpDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinition, false);

		if (isNew) {
			cpDefinition.setNew(false);
		}

		cpDefinition.resetOriginalValues();

		return cpDefinition;
	}

	/**
	 * Returns the cp definition with the primary key or throws a <code>NoSuchCPDefinitionException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition
	 * @throws NoSuchCPDefinitionException if a cp definition with the primary key could not be found
	 */
	@Override
	public CPDefinition findByPrimaryKey(long CPDefinitionId)
		throws NoSuchCPDefinitionException {

		return findByPrimaryKey((Serializable)CPDefinitionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionId the primary key of the cp definition
	 * @return the cp definition, or <code>null</code> if a cp definition with the primary key could not be found
	 */
	@Override
	public CPDefinition fetchByPrimaryKey(long CPDefinitionId) {
		return fetchByPrimaryKey((Serializable)CPDefinitionId);
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
		return "CPDefinitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITION;
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
		return CPDefinitionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDefinition";
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
		ctStrictColumnNames.add("defaultLanguageId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CProductExternalReferenceCode");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("CPTaxCategoryId");
		ctMergeColumnNames.add("accountGroupFilterEnabled");
		ctMergeColumnNames.add("availableIndividually");
		ctMergeColumnNames.add("channelFilterEnabled");
		ctMergeColumnNames.add("DDMStructureKey");
		ctMergeColumnNames.add("deliveryMaxSubscriptionCycles");
		ctMergeColumnNames.add("deliverySubscriptionEnabled");
		ctMergeColumnNames.add("deliverySubscriptionLength");
		ctMergeColumnNames.add("deliverySubscriptionType");
		ctMergeColumnNames.add("deliverySubTypeSettings");
		ctMergeColumnNames.add("depth");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("freeShipping");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("ignoreSKUCombinations");
		ctMergeColumnNames.add("maxSubscriptionCycles");
		ctMergeColumnNames.add("productTypeName");
		ctMergeColumnNames.add("published");
		ctMergeColumnNames.add("shipSeparately");
		ctMergeColumnNames.add("shippable");
		ctMergeColumnNames.add("shippingExtraPrice");
		ctMergeColumnNames.add("subscriptionEnabled");
		ctMergeColumnNames.add("subscriptionLength");
		ctMergeColumnNames.add("subscriptionType");
		ctMergeColumnNames.add("subscriptionTypeSettings");
		ctMergeColumnNames.add("taxExempt");
		ctMergeColumnNames.add("telcoOrElectronics");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("weight");
		ctMergeColumnNames.add("width");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("CPDefinitionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"CProductId", "version"});
	}

	/**
	 * Initializes the cp definition persistence.
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
			_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
			CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpDefinition.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CPDefinition::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinition::getUuid),
				CPDefinition::getGroupId),
			_SQL_SELECT_CPDEFINITION_WHERE, "",
			new FinderColumn<>(
				"cpDefinition.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CPDefinition::getUuid),
			new FinderColumn<>(
				"cpDefinition.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, CPDefinition::getGroupId));

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
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, CPDefinition::getUuid),
				new FinderColumn<>(
					"cpDefinition.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getGroupId));

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
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getCompanyId));

		_collectionPersistenceFinderByCProductId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCProductId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, false),
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "CProductId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getCProductId));

		_collectionPersistenceFinderByCPTaxCategoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPTaxCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPTaxCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPTaxCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPTaxCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPTaxCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"CPTaxCategoryId"}, false),
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "CPTaxCategoryId", FinderColumn.Type.LONG,
					"=", true, true, CPDefinition::getCPTaxCategoryId));

		_collectionPersistenceFinderByG_SE =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_SE",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "subscriptionEnabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_SE",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "subscriptionEnabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_SE",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "subscriptionEnabled"}, false),
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getGroupId),
				new FinderColumn<>(
					"cpDefinition.", "subscriptionEnabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CPDefinition::isSubscriptionEnabled));

		_collectionPersistenceFinderByG_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
				CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"cpDefinition.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CPDefinition::getGroupId),
				new FinderColumn<>(
					"cpDefinition.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, CPDefinition::getStatus));

		_uniquePersistenceFinderByC_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_V",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CProductId", "version"}, 0, 0, false,
				CPDefinition::getCProductId, CPDefinition::getVersion),
			_SQL_SELECT_CPDEFINITION_WHERE, "",
			new FinderColumn<>(
				"cpDefinition.", "CProductId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinition::getCProductId),
			new FinderColumn<>(
				"cpDefinition.", "version", FinderColumn.Type.INTEGER, "=",
				true, true, CPDefinition::getVersion));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CProductId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CProductId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CProductId", "status"}, false),
			_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
			CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpDefinition.", "CProductId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinition::getCProductId),
			new FinderColumn<>(
				"cpDefinition.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, CPDefinition::getStatus));

		_collectionPersistenceFinderByLtD_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtD_S",
				new String[] {
					Date.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"displayDate", "status"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtD_S",
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"displayDate", "status"}, false),
			_SQL_SELECT_CPDEFINITION_WHERE, _SQL_COUNT_CPDEFINITION_WHERE,
			CPDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpDefinition.", "displayDate", FinderColumn.Type.DATE, "<",
				true, true, CPDefinition::getDisplayDate),
			new FinderColumn<>(
				"cpDefinition.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, CPDefinition::getStatus));

		CPDefinitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	@Reference
	protected CPDefinitionLocalizationPersistence
		cpDefinitionLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		CPDefinitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITION =
		"SELECT cpDefinition FROM CPDefinition cpDefinition";

	private static final String _SQL_SELECT_CPDEFINITION_WHERE =
		"SELECT cpDefinition FROM CPDefinition cpDefinition WHERE ";

	private static final String _SQL_COUNT_CPDEFINITION_WHERE =
		"SELECT COUNT(cpDefinition) FROM CPDefinition cpDefinition WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "deliverySubscriptionTypeSettings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1838756731
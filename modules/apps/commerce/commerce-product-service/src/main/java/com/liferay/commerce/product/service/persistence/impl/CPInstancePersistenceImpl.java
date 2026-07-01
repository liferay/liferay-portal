/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPInstanceExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceTable;
import com.liferay.commerce.product.model.impl.CPInstanceImpl;
import com.liferay.commerce.product.model.impl.CPInstanceModelImpl;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
 * The persistence implementation for the cp instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPInstancePersistence.class)
public class CPInstancePersistenceImpl
	extends BasePersistenceImpl<CPInstance, NoSuchCPInstanceException>
	implements CPInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPInstanceUtil</code> to access the cp instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPInstanceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_First(
			String uuid, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_First(
		String uuid, OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp instance where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp instance where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByUUID_G(uuid, groupId);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<CPInstance, NoSuchCPInstanceException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByGroupId_First(
			long groupId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByGroupId_First(
		long groupId, OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp instances that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the cp instances where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCompanyId_First(
			long companyId, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCompanyId_First(
		long companyId, OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDefinitionId_First(
		long CPDefinitionId, OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByCPInstanceUuid;

	/**
	 * Returns an ordered range of all the cp instances where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceUuid.find(
			finderCache, new Object[] {CPInstanceUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByCPInstanceUuid.findFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceUuid.fetchFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCPInstanceUuid(String CPInstanceUuid) {
		_collectionPersistenceFinderByCPInstanceUuid.remove(
			finderCache, new Object[] {CPInstanceUuid});
	}

	/**
	 * Returns the number of cp instances where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPInstanceUuid(String CPInstanceUuid) {
		return _collectionPersistenceFinderByCPInstanceUuid.count(
			finderCache, new Object[] {CPInstanceUuid});
	}

	private FilterCollectionPersistenceFinder
		<CPInstance, NoSuchCPInstanceException>
			_collectionPersistenceFinderByG_ST;

	/**
	 * Returns an ordered range of all the cp instances where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_ST.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByG_ST_First(
			long groupId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByG_ST.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByG_ST_First(
		long groupId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByG_ST.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp instances that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances that the user has permission to view
	 */
	@Override
	public List<CPInstance> filterFindByG_ST(
		long groupId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByG_ST.filterFind(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the cp instances where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_ST(long groupId, int status) {
		_collectionPersistenceFinderByG_ST.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of cp instances where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByG_ST(long groupId, int status) {
		return _collectionPersistenceFinderByG_ST.count(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of cp instances that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching cp instances that the user has permission to view
	 */
	@Override
	public int filterCountByG_ST(long groupId, int status) {
		return _collectionPersistenceFinderByG_ST.filterCount(
			finderCache, new Object[] {groupId, status}, groupId);
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_S(
		long companyId, String sku, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, sku}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_S_First(
			long companyId, String sku,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {companyId, sku}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_S_First(
		long companyId, String sku,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {companyId, sku}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where companyId = &#63; and sku = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 */
	@Override
	public void removeByC_S(long companyId, String sku) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {companyId, sku});
	}

	/**
	 * Returns the number of cp instances where companyId = &#63; and sku = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_S(long companyId, String sku) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, sku});
	}

	private UniquePersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_C(long CPDefinitionId, String CPInstanceUuid)
		throws NoSuchCPInstanceException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {CPDefinitionId, CPInstanceUuid});
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_C(
		long CPDefinitionId, String CPInstanceUuid, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {CPDefinitionId, CPInstanceUuid},
			useFinderCache);
	}

	/**
	 * Removes the cp instance where CPDefinitionId = &#63; and CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByC_C(long CPDefinitionId, String CPInstanceUuid)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByC_C(CPDefinitionId, CPInstanceUuid);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_C(long CPDefinitionId, String CPInstanceUuid) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {CPDefinitionId, CPInstanceUuid});
	}

	private UniquePersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_uniquePersistenceFinderByCPDI_S;

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and sku = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByCPDI_S(long CPDefinitionId, String sku)
		throws NoSuchCPInstanceException {

		return _uniquePersistenceFinderByCPDI_S.find(
			finderCache, new Object[] {CPDefinitionId, sku});
	}

	/**
	 * Returns the cp instance where CPDefinitionId = &#63; and sku = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByCPDI_S(
		long CPDefinitionId, String sku, boolean useFinderCache) {

		return _uniquePersistenceFinderByCPDI_S.fetch(
			finderCache, new Object[] {CPDefinitionId, sku}, useFinderCache);
	}

	/**
	 * Removes the cp instance where CPDefinitionId = &#63; and sku = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByCPDI_S(long CPDefinitionId, String sku)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByCPDI_S(CPDefinitionId, sku);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and sku = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sku the sku
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByCPDI_S(long CPDefinitionId, String sku) {
		return _uniquePersistenceFinderByCPDI_S.count(
			finderCache, new Object[] {CPDefinitionId, sku});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByC_ST;

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_ST(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_ST.find(
			finderCache, new Object[] {CPDefinitionId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_ST_First(
			long CPDefinitionId, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByC_ST.findFirst(
			finderCache, new Object[] {CPDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_ST_First(
		long CPDefinitionId, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByC_ST.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 */
	@Override
	public void removeByC_ST(long CPDefinitionId, int status) {
		_collectionPersistenceFinderByC_ST.remove(
			finderCache, new Object[] {CPDefinitionId, status});
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_ST(long CPDefinitionId, int status) {
		return _collectionPersistenceFinderByC_ST.count(
			finderCache, new Object[] {CPDefinitionId, status});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the cp instances where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of cp instances where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByC_LtD_S;

	/**
	 * Returns all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return findByC_LtD_S(
			CPDefinitionId, displayDate, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtD_S.find(
			finderCache, new Object[] {CPDefinitionId, displayDate, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByC_LtD_S_First(
			long CPDefinitionId, Date displayDate, int status,
			OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByC_LtD_S.findFirst(
			finderCache, new Object[] {CPDefinitionId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByC_LtD_S_First(
		long CPDefinitionId, Date displayDate, int status,
		OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByC_LtD_S.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, displayDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	@Override
	public void removeByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		_collectionPersistenceFinderByC_LtD_S.remove(
			finderCache, new Object[] {CPDefinitionId, displayDate, status});
	}

	/**
	 * Returns the number of cp instances where CPDefinitionId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByC_LtD_S(
		long CPDefinitionId, Date displayDate, int status) {

		return _collectionPersistenceFinderByC_LtD_S.count(
			finderCache, new Object[] {CPDefinitionId, displayDate, status});
	}

	private CollectionPersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_collectionPersistenceFinderByR_R_S;

	/**
	 * Returns an ordered range of all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instances
	 */
	@Override
	public List<CPInstance> findByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_R_S.find(
			finderCache,
			new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByR_R_S_First(
			String replacementCPInstanceUuid, long replacementCProductId,
			int status, OrderByComparator<CPInstance> orderByComparator)
		throws NoSuchCPInstanceException {

		return _collectionPersistenceFinderByR_R_S.findFirst(
			finderCache,
			new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance in the ordered set where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByR_R_S_First(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status, OrderByComparator<CPInstance> orderByComparator) {

		return _collectionPersistenceFinderByR_R_S.fetchFirst(
			finderCache,
			new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			},
			orderByComparator);
	}

	/**
	 * Removes all the cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63; from the database.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 */
	@Override
	public void removeByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		_collectionPersistenceFinderByR_R_S.remove(
			finderCache,
			new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			});
	}

	/**
	 * Returns the number of cp instances where replacementCPInstanceUuid = &#63; and replacementCProductId = &#63; and status = &#63;.
	 *
	 * @param replacementCPInstanceUuid the replacement cp instance uuid
	 * @param replacementCProductId the replacement c product ID
	 * @param status the status
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByR_R_S(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		return _collectionPersistenceFinderByR_R_S.count(
			finderCache,
			new Object[] {
				replacementCPInstanceUuid, replacementCProductId, status
			});
	}

	private UniquePersistenceFinder<CPInstance, NoSuchCPInstanceException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp instance where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp instance
	 * @throws NoSuchCPInstanceException if a matching cp instance could not be found
	 */
	@Override
	public CPInstance findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCPInstanceException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp instance where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp instance where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp instance that was removed
	 */
	@Override
	public CPInstance removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPInstanceException {

		CPInstance cpInstance = findByERC_C(externalReferenceCode, companyId);

		return remove(cpInstance);
	}

	/**
	 * Returns the number of cp instances where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp instances
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPInstancePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"deliverySubscriptionTypeSettings", "deliverySubTypeSettings");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPInstance.class);

		setModelImplClass(CPInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(CPInstanceTable.INSTANCE);
	}

	/**
	 * Creates a new cp instance with the primary key. Does not add the cp instance to the database.
	 *
	 * @param CPInstanceId the primary key for the new cp instance
	 * @return the new cp instance
	 */
	@Override
	public CPInstance create(long CPInstanceId) {
		CPInstance cpInstance = new CPInstanceImpl();

		cpInstance.setNew(true);
		cpInstance.setPrimaryKey(CPInstanceId);

		String uuid = PortalUUIDUtil.generate();

		cpInstance.setUuid(uuid);

		cpInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpInstance;
	}

	/**
	 * Removes the cp instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance that was removed
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance remove(long CPInstanceId)
		throws NoSuchCPInstanceException {

		return remove((Serializable)CPInstanceId);
	}

	@Override
	protected CPInstance removeImpl(CPInstance cpInstance) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpInstance)) {
				cpInstance = (CPInstance)session.get(
					CPInstanceImpl.class, cpInstance.getPrimaryKeyObj());
			}

			if ((cpInstance != null) &&
				ctPersistenceHelper.isRemove(cpInstance)) {

				session.delete(cpInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpInstance != null) {
			clearCache(cpInstance);
		}

		return cpInstance;
	}

	@Override
	public CPInstance updateImpl(CPInstance cpInstance) {
		boolean isNew = cpInstance.isNew();

		if (!(cpInstance instanceof CPInstanceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(cpInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPInstance implementation " +
					cpInstance.getClass());
		}

		CPInstanceModelImpl cpInstanceModelImpl =
			(CPInstanceModelImpl)cpInstance;

		if (Validator.isNull(cpInstance.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpInstance.setUuid(uuid);
		}

		if (Validator.isNull(cpInstance.getExternalReferenceCode())) {
			cpInstance.setExternalReferenceCode(cpInstance.getUuid());
		}
		else {
			if (!Objects.equals(
					cpInstanceModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpInstance.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpInstance.getCompanyId();

					long groupId = cpInstance.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpInstance.getPrimaryKey();
					}

					try {
						cpInstance.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPInstance.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpInstance.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPInstance ercCPInstance = fetchByERC_C(
				cpInstance.getExternalReferenceCode(),
				cpInstance.getCompanyId());

			if (isNew) {
				if (ercCPInstance != null) {
					throw new DuplicateCPInstanceExternalReferenceCodeException(
						"Duplicate cp instance with external reference code " +
							cpInstance.getExternalReferenceCode() +
								" and company " + cpInstance.getCompanyId());
				}
			}
			else {
				if ((ercCPInstance != null) &&
					(cpInstance.getCPInstanceId() !=
						ercCPInstance.getCPInstanceId())) {

					throw new DuplicateCPInstanceExternalReferenceCodeException(
						"Duplicate cp instance with external reference code " +
							cpInstance.getExternalReferenceCode() +
								" and company " + cpInstance.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpInstance.setCreateDate(date);
			}
			else {
				cpInstance.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!cpInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpInstance.setModifiedDate(date);
			}
			else {
				cpInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpInstance)) {
				if (!isNew) {
					session.evict(
						CPInstanceImpl.class, cpInstance.getPrimaryKeyObj());
				}

				session.save(cpInstance);
			}
			else {
				cpInstance = (CPInstance)session.merge(cpInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpInstance, false);

		if (isNew) {
			cpInstance.setNew(false);
		}

		cpInstance.resetOriginalValues();

		return cpInstance;
	}

	/**
	 * Returns the cp instance with the primary key or throws a <code>NoSuchCPInstanceException</code> if it could not be found.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance
	 * @throws NoSuchCPInstanceException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance findByPrimaryKey(long CPInstanceId)
		throws NoSuchCPInstanceException {

		return findByPrimaryKey((Serializable)CPInstanceId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance, or <code>null</code> if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance fetchByPrimaryKey(long CPInstanceId) {
		return fetchByPrimaryKey((Serializable)CPInstanceId);
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
		return "CPInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPINSTANCE;
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
		return CPInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPInstance";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("CPInstanceUuid");
		ctMergeColumnNames.add("sku");
		ctMergeColumnNames.add("gtin");
		ctMergeColumnNames.add("manufacturerPartNumber");
		ctMergeColumnNames.add("purchasable");
		ctMergeColumnNames.add("width");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("depth");
		ctMergeColumnNames.add("weight");
		ctMergeColumnNames.add("price");
		ctMergeColumnNames.add("promoPrice");
		ctMergeColumnNames.add("cost");
		ctMergeColumnNames.add("published");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("overrideSubscriptionInfo");
		ctMergeColumnNames.add("subscriptionEnabled");
		ctMergeColumnNames.add("subscriptionLength");
		ctMergeColumnNames.add("subscriptionType");
		ctMergeColumnNames.add("subscriptionTypeSettings");
		ctMergeColumnNames.add("maxSubscriptionCycles");
		ctMergeColumnNames.add("deliverySubscriptionEnabled");
		ctMergeColumnNames.add("deliverySubscriptionLength");
		ctMergeColumnNames.add("deliverySubscriptionType");
		ctMergeColumnNames.add("deliverySubTypeSettings");
		ctMergeColumnNames.add("deliveryMaxSubscriptionCycles");
		ctMergeColumnNames.add("unspsc");
		ctMergeColumnNames.add("discontinued");
		ctMergeColumnNames.add("discontinuedDate");
		ctMergeColumnNames.add("replacementCPInstanceUuid");
		ctMergeColumnNames.add("replacementCProductId");
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
			CTColumnResolutionType.PK, Collections.singleton("CPInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "CPInstanceUuid"});

		_uniqueIndexColumnNames.add(new String[] {"CPDefinitionId", "sku"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp instance persistence.
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
			_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
			CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpInstance.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CPInstance::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPInstance::getUuid),
				CPInstance::getGroupId),
			_SQL_SELECT_CPINSTANCE_WHERE, "",
			new FinderColumn<>(
				"cpInstance.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, CPInstance::getUuid),
			new FinderColumn<>(
				"cpInstance.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, CPInstance::getGroupId));

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
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, CPInstance::getUuid),
				new FinderColumn<>(
					"cpInstance.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPInstance::getCompanyId));

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
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, CPInstance::getGroupId));

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
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CPInstance::getCompanyId));

		_collectionPersistenceFinderByCPDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionId", new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, false),
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "CPDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, CPInstance::getCPDefinitionId));

		_collectionPersistenceFinderByCPInstanceUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceUuid",
					new String[] {String.class.getName()},
					new String[] {"CPInstanceUuid"}, 0, 1, false, null),
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "CPInstanceUuid", FinderColumn.Type.STRING,
					"=", true, true, CPInstance::getCPInstanceUuid));

		_collectionPersistenceFinderByG_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_ST",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "status"}, false),
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, CPInstance::getGroupId),
				new FinderColumn<>(
					"cpInstance.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, CPInstance::getStatus));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "sku"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sku"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "sku"}, 0, 2, false, null),
			_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
			CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpInstance.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CPInstance::getCompanyId),
			new FinderColumn<>(
				"cpInstance.", "sku", FinderColumn.Type.STRING, "=", true, true,
				CPInstance::getSku));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "CPInstanceUuid"}, 0, 2, false,
				CPInstance::getCPDefinitionId,
				convertNullFunction(CPInstance::getCPInstanceUuid)),
			_SQL_SELECT_CPINSTANCE_WHERE, "",
			new FinderColumn<>(
				"cpInstance.", "CPDefinitionId", FinderColumn.Type.LONG, "=",
				true, true, CPInstance::getCPDefinitionId),
			new FinderColumn<>(
				"cpInstance.", "CPInstanceUuid", FinderColumn.Type.STRING, "=",
				true, true, CPInstance::getCPInstanceUuid));

		_uniquePersistenceFinderByCPDI_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCPDI_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "sku"}, 0, 2, false,
				CPInstance::getCPDefinitionId,
				convertNullFunction(CPInstance::getSku)),
			_SQL_SELECT_CPINSTANCE_WHERE, "",
			new FinderColumn<>(
				"cpInstance.", "CPDefinitionId", FinderColumn.Type.LONG, "=",
				true, true, CPInstance::getCPDefinitionId),
			new FinderColumn<>(
				"cpInstance.", "sku", FinderColumn.Type.STRING, "=", true, true,
				CPInstance::getSku));

		_collectionPersistenceFinderByC_ST = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_ST",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPDefinitionId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CPDefinitionId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_ST",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CPDefinitionId", "status"}, false),
			_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
			CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpInstance.", "CPDefinitionId", FinderColumn.Type.LONG, "=",
				true, true, CPInstance::getCPDefinitionId),
			new FinderColumn<>(
				"cpInstance.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, CPInstance::getStatus));

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
			_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
			CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpInstance.", "displayDate", FinderColumn.Type.DATE, "<", true,
				true, CPInstance::getDisplayDate),
			new FinderColumn<>(
				"cpInstance.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, CPInstance::getStatus));

		_collectionPersistenceFinderByC_LtD_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId", "displayDate", "status"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtD_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName()
					},
					new String[] {"CPDefinitionId", "displayDate", "status"},
					false),
				_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
				CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"cpInstance.", "CPDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, CPInstance::getCPDefinitionId),
				new FinderColumn<>(
					"cpInstance.", "displayDate", FinderColumn.Type.DATE, "<",
					true, true, CPInstance::getDisplayDate),
				new FinderColumn<>(
					"cpInstance.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, CPInstance::getStatus));

		_collectionPersistenceFinderByR_R_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_R_S",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"replacementCPInstanceUuid", "replacementCProductId",
					"status"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_R_S",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"replacementCPInstanceUuid", "replacementCProductId",
					"status"
				},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_R_S",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"replacementCPInstanceUuid", "replacementCProductId",
					"status"
				},
				0, 1, false, null),
			_SQL_SELECT_CPINSTANCE_WHERE, _SQL_COUNT_CPINSTANCE_WHERE,
			CPInstanceModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"cpInstance.", "replacementCPInstanceUuid",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstance::getReplacementCPInstanceUuid),
			new FinderColumn<>(
				"cpInstance.", "replacementCProductId", FinderColumn.Type.LONG,
				"=", true, true, CPInstance::getReplacementCProductId),
			new FinderColumn<>(
				"cpInstance.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, CPInstance::getStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CPInstance::getExternalReferenceCode),
				CPInstance::getCompanyId),
			_SQL_SELECT_CPINSTANCE_WHERE, "",
			new FinderColumn<>(
				"cpInstance.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstance::getExternalReferenceCode),
			new FinderColumn<>(
				"cpInstance.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CPInstance::getCompanyId));

		CPInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPInstanceUtil.setPersistence(null);

		entityCache.removeCache(CPInstanceImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		CPInstanceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPINSTANCE =
		"SELECT cpInstance FROM CPInstance cpInstance";

	private static final String _SQL_SELECT_CPINSTANCE_WHERE =
		"SELECT cpInstance FROM CPInstance cpInstance WHERE ";

	private static final String _SQL_COUNT_CPINSTANCE_WHERE =
		"SELECT COUNT(cpInstance) FROM CPInstance cpInstance WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "deliverySubscriptionTypeSettings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-177077099
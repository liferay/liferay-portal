/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPInstanceOptionValueRelException;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CPInstanceOptionValueRelTable;
import com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelImpl;
import com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
 * The persistence implementation for the cp instance option value rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPInstanceOptionValueRelPersistence.class)
public class CPInstanceOptionValueRelPersistenceImpl
	extends BasePersistenceImpl
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
	implements CPInstanceOptionValueRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPInstanceOptionValueRelUtil</code> to access the cp instance option value rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPInstanceOptionValueRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp instance option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance option value rels
	 */
	@Override
	public List<CPInstanceOptionValueRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByUuid_First(
			String uuid,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator)
		throws NoSuchCPInstanceOptionValueRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp instance option value rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp instance option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp instance option value rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPInstanceOptionValueRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceOptionValueRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp instance option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp instance option value rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp instance option value rel that was removed
	 */
	@Override
	public CPInstanceOptionValueRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPInstanceOptionValueRelException {

		CPInstanceOptionValueRel cpInstanceOptionValueRel = findByUUID_G(
			uuid, groupId);

		return remove(cpInstanceOptionValueRel);
	}

	/**
	 * Returns the number of cp instance option value rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp instance option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance option value rels
	 */
	@Override
	public List<CPInstanceOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator)
		throws NoSuchCPInstanceOptionValueRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp instance option value rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp instance option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_collectionPersistenceFinderByCPDefinitionOptionRelId;

	/**
	 * Returns an ordered range of all the cp instance option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance option value rels
	 */
	@Override
	public List<CPInstanceOptionValueRel> findByCPDefinitionOptionRelId(
		long CPDefinitionOptionRelId, int start, int end,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.find(
			finderCache, new Object[] {CPDefinitionOptionRelId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByCPDefinitionOptionRelId_First(
			long CPDefinitionOptionRelId,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator)
		throws NoSuchCPInstanceOptionValueRelException {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.findFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByCPDefinitionOptionRelId_First(
		long CPDefinitionOptionRelId,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.fetchFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId},
			orderByComparator);
	}

	/**
	 * Removes all the cp instance option value rels where CPDefinitionOptionRelId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 */
	@Override
	public void removeByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {
		_collectionPersistenceFinderByCPDefinitionOptionRelId.remove(
			finderCache, new Object[] {CPDefinitionOptionRelId});
	}

	/**
	 * Returns the number of cp instance option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {
		return _collectionPersistenceFinderByCPDefinitionOptionRelId.count(
			finderCache, new Object[] {CPDefinitionOptionRelId});
	}

	private CollectionPersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_collectionPersistenceFinderByCPInstanceId;

	/**
	 * Returns an ordered range of all the cp instance option value rels where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance option value rels
	 */
	@Override
	public List<CPInstanceOptionValueRel> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceId.find(
			finderCache, new Object[] {CPInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator)
		throws NoSuchCPInstanceOptionValueRelException {

		return _collectionPersistenceFinderByCPInstanceId.findFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceId.fetchFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the cp instance option value rels where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCPInstanceId(long CPInstanceId) {
		_collectionPersistenceFinderByCPInstanceId.remove(
			finderCache, new Object[] {CPInstanceId});
	}

	/**
	 * Returns the number of cp instance option value rels where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByCPInstanceId(long CPInstanceId) {
		return _collectionPersistenceFinderByCPInstanceId.count(
			finderCache, new Object[] {CPInstanceId});
	}

	private CollectionPersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_collectionPersistenceFinderByCDORI_CII;

	/**
	 * Returns an ordered range of all the cp instance option value rels where CPDefinitionOptionRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp instance option value rels
	 */
	@Override
	public List<CPInstanceOptionValueRel> findByCDORI_CII(
		long CPDefinitionOptionRelId, long CPInstanceId, int start, int end,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCDORI_CII.find(
			finderCache, new Object[] {CPDefinitionOptionRelId, CPInstanceId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByCDORI_CII_First(
			long CPDefinitionOptionRelId, long CPInstanceId,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator)
		throws NoSuchCPInstanceOptionValueRelException {

		return _collectionPersistenceFinderByCDORI_CII.findFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId, CPInstanceId},
			orderByComparator);
	}

	/**
	 * Returns the first cp instance option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByCDORI_CII_First(
		long CPDefinitionOptionRelId, long CPInstanceId,
		OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCDORI_CII.fetchFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId, CPInstanceId},
			orderByComparator);
	}

	/**
	 * Removes all the cp instance option value rels where CPDefinitionOptionRelId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCDORI_CII(
		long CPDefinitionOptionRelId, long CPInstanceId) {

		_collectionPersistenceFinderByCDORI_CII.remove(
			finderCache, new Object[] {CPDefinitionOptionRelId, CPInstanceId});
	}

	/**
	 * Returns the number of cp instance option value rels where CPDefinitionOptionRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByCDORI_CII(
		long CPDefinitionOptionRelId, long CPInstanceId) {

		return _collectionPersistenceFinderByCDORI_CII.count(
			finderCache, new Object[] {CPDefinitionOptionRelId, CPInstanceId});
	}

	private UniquePersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_uniquePersistenceFinderByCDOVRI_CII;

	/**
	 * Returns the cp instance option value rel where CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; or throws a <code>NoSuchCPInstanceOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByCDOVRI_CII(
			long CPDefinitionOptionValueRelId, long CPInstanceId)
		throws NoSuchCPInstanceOptionValueRelException {

		return _uniquePersistenceFinderByCDOVRI_CII.find(
			finderCache,
			new Object[] {CPDefinitionOptionValueRelId, CPInstanceId});
	}

	/**
	 * Returns the cp instance option value rel where CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByCDOVRI_CII(
		long CPDefinitionOptionValueRelId, long CPInstanceId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCDOVRI_CII.fetch(
			finderCache,
			new Object[] {CPDefinitionOptionValueRelId, CPInstanceId},
			useFinderCache);
	}

	/**
	 * Removes the cp instance option value rel where CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the cp instance option value rel that was removed
	 */
	@Override
	public CPInstanceOptionValueRel removeByCDOVRI_CII(
			long CPDefinitionOptionValueRelId, long CPInstanceId)
		throws NoSuchCPInstanceOptionValueRelException {

		CPInstanceOptionValueRel cpInstanceOptionValueRel = findByCDOVRI_CII(
			CPDefinitionOptionValueRelId, CPInstanceId);

		return remove(cpInstanceOptionValueRel);
	}

	/**
	 * Returns the number of cp instance option value rels where CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByCDOVRI_CII(
		long CPDefinitionOptionValueRelId, long CPInstanceId) {

		return _uniquePersistenceFinderByCDOVRI_CII.count(
			finderCache,
			new Object[] {CPDefinitionOptionValueRelId, CPInstanceId});
	}

	private UniquePersistenceFinder
		<CPInstanceOptionValueRel, NoSuchCPInstanceOptionValueRelException>
			_uniquePersistenceFinderByCDORI_CDOVRI_CII;

	/**
	 * Returns the cp instance option value rel where CPDefinitionOptionRelId = &#63; and CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; or throws a <code>NoSuchCPInstanceOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the matching cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByCDORI_CDOVRI_CII(
			long CPDefinitionOptionRelId, long CPDefinitionOptionValueRelId,
			long CPInstanceId)
		throws NoSuchCPInstanceOptionValueRelException {

		return _uniquePersistenceFinderByCDORI_CDOVRI_CII.find(
			finderCache,
			new Object[] {
				CPDefinitionOptionRelId, CPDefinitionOptionValueRelId,
				CPInstanceId
			});
	}

	/**
	 * Returns the cp instance option value rel where CPDefinitionOptionRelId = &#63; and CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByCDORI_CDOVRI_CII(
		long CPDefinitionOptionRelId, long CPDefinitionOptionValueRelId,
		long CPInstanceId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCDORI_CDOVRI_CII.fetch(
			finderCache,
			new Object[] {
				CPDefinitionOptionRelId, CPDefinitionOptionValueRelId,
				CPInstanceId
			},
			useFinderCache);
	}

	/**
	 * Removes the cp instance option value rel where CPDefinitionOptionRelId = &#63; and CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the cp instance option value rel that was removed
	 */
	@Override
	public CPInstanceOptionValueRel removeByCDORI_CDOVRI_CII(
			long CPDefinitionOptionRelId, long CPDefinitionOptionValueRelId,
			long CPInstanceId)
		throws NoSuchCPInstanceOptionValueRelException {

		CPInstanceOptionValueRel cpInstanceOptionValueRel =
			findByCDORI_CDOVRI_CII(
				CPDefinitionOptionRelId, CPDefinitionOptionValueRelId,
				CPInstanceId);

		return remove(cpInstanceOptionValueRel);
	}

	/**
	 * Returns the number of cp instance option value rels where CPDefinitionOptionRelId = &#63; and CPDefinitionOptionValueRelId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param CPDefinitionOptionValueRelId the cp definition option value rel ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cp instance option value rels
	 */
	@Override
	public int countByCDORI_CDOVRI_CII(
		long CPDefinitionOptionRelId, long CPDefinitionOptionValueRelId,
		long CPInstanceId) {

		return _uniquePersistenceFinderByCDORI_CDOVRI_CII.count(
			finderCache,
			new Object[] {
				CPDefinitionOptionRelId, CPDefinitionOptionValueRelId,
				CPInstanceId
			});
	}

	public CPInstanceOptionValueRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPInstanceOptionValueRel.class);

		setModelImplClass(CPInstanceOptionValueRelImpl.class);
		setModelPKClass(long.class);

		setTable(CPInstanceOptionValueRelTable.INSTANCE);
	}

	/**
	 * Creates a new cp instance option value rel with the primary key. Does not add the cp instance option value rel to the database.
	 *
	 * @param CPInstanceOptionValueRelId the primary key for the new cp instance option value rel
	 * @return the new cp instance option value rel
	 */
	@Override
	public CPInstanceOptionValueRel create(long CPInstanceOptionValueRelId) {
		CPInstanceOptionValueRel cpInstanceOptionValueRel =
			new CPInstanceOptionValueRelImpl();

		cpInstanceOptionValueRel.setNew(true);
		cpInstanceOptionValueRel.setPrimaryKey(CPInstanceOptionValueRelId);

		String uuid = PortalUUIDUtil.generate();

		cpInstanceOptionValueRel.setUuid(uuid);

		cpInstanceOptionValueRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpInstanceOptionValueRel;
	}

	/**
	 * Removes the cp instance option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel that was removed
	 * @throws NoSuchCPInstanceOptionValueRelException if a cp instance option value rel with the primary key could not be found
	 */
	@Override
	public CPInstanceOptionValueRel remove(long CPInstanceOptionValueRelId)
		throws NoSuchCPInstanceOptionValueRelException {

		return remove((Serializable)CPInstanceOptionValueRelId);
	}

	@Override
	protected CPInstanceOptionValueRel removeImpl(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpInstanceOptionValueRel)) {
				cpInstanceOptionValueRel =
					(CPInstanceOptionValueRel)session.get(
						CPInstanceOptionValueRelImpl.class,
						cpInstanceOptionValueRel.getPrimaryKeyObj());
			}

			if ((cpInstanceOptionValueRel != null) &&
				ctPersistenceHelper.isRemove(cpInstanceOptionValueRel)) {

				session.delete(cpInstanceOptionValueRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpInstanceOptionValueRel != null) {
			clearCache(cpInstanceOptionValueRel);
		}

		return cpInstanceOptionValueRel;
	}

	@Override
	public CPInstanceOptionValueRel updateImpl(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		boolean isNew = cpInstanceOptionValueRel.isNew();

		if (!(cpInstanceOptionValueRel instanceof
				CPInstanceOptionValueRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpInstanceOptionValueRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpInstanceOptionValueRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpInstanceOptionValueRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPInstanceOptionValueRel implementation " +
					cpInstanceOptionValueRel.getClass());
		}

		CPInstanceOptionValueRelModelImpl cpInstanceOptionValueRelModelImpl =
			(CPInstanceOptionValueRelModelImpl)cpInstanceOptionValueRel;

		if (Validator.isNull(cpInstanceOptionValueRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpInstanceOptionValueRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpInstanceOptionValueRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpInstanceOptionValueRel.setCreateDate(date);
			}
			else {
				cpInstanceOptionValueRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpInstanceOptionValueRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpInstanceOptionValueRel.setModifiedDate(date);
			}
			else {
				cpInstanceOptionValueRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpInstanceOptionValueRel)) {
				if (!isNew) {
					session.evict(
						CPInstanceOptionValueRelImpl.class,
						cpInstanceOptionValueRel.getPrimaryKeyObj());
				}

				session.save(cpInstanceOptionValueRel);
			}
			else {
				cpInstanceOptionValueRel =
					(CPInstanceOptionValueRel)session.merge(
						cpInstanceOptionValueRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpInstanceOptionValueRel, false);

		if (isNew) {
			cpInstanceOptionValueRel.setNew(false);
		}

		cpInstanceOptionValueRel.resetOriginalValues();

		return cpInstanceOptionValueRel;
	}

	/**
	 * Returns the cp instance option value rel with the primary key or throws a <code>NoSuchCPInstanceOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel
	 * @throws NoSuchCPInstanceOptionValueRelException if a cp instance option value rel with the primary key could not be found
	 */
	@Override
	public CPInstanceOptionValueRel findByPrimaryKey(
			long CPInstanceOptionValueRelId)
		throws NoSuchCPInstanceOptionValueRelException {

		return findByPrimaryKey((Serializable)CPInstanceOptionValueRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp instance option value rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel, or <code>null</code> if a cp instance option value rel with the primary key could not be found
	 */
	@Override
	public CPInstanceOptionValueRel fetchByPrimaryKey(
		long CPInstanceOptionValueRelId) {

		return fetchByPrimaryKey((Serializable)CPInstanceOptionValueRelId);
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
		return "CPInstanceOptionValueRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPINSTANCEOPTIONVALUEREL;
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
		return CPInstanceOptionValueRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPInstanceOptionValueRel";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPDefinitionOptionRelId");
		ctMergeColumnNames.add("CPDefinitionOptionValueRelId");
		ctMergeColumnNames.add("CPInstanceId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPInstanceOptionValueRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionOptionValueRelId", "CPInstanceId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"CPDefinitionOptionRelId", "CPDefinitionOptionValueRelId",
				"CPInstanceId"
			});
	}

	/**
	 * Initializes the cp instance option value rel persistence.
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
			_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE,
			_SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE,
			CPInstanceOptionValueRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"cpInstanceOptionValueRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstanceOptionValueRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPInstanceOptionValueRel::getUuid),
				CPInstanceOptionValueRel::getGroupId),
			_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE, "",
			new FinderColumn<>(
				"cpInstanceOptionValueRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPInstanceOptionValueRel::getUuid),
			new FinderColumn<>(
				"cpInstanceOptionValueRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CPInstanceOptionValueRel::getGroupId));

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
				_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE,
				CPInstanceOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPInstanceOptionValueRel::getUuid),
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCompanyId));

		_collectionPersistenceFinderByCPDefinitionOptionRelId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionOptionRelId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionOptionRelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionOptionRelId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionOptionRelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionOptionRelId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionOptionRelId"}, false),
				_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE,
				CPInstanceOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPDefinitionOptionRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPDefinitionOptionRelId));

		_collectionPersistenceFinderByCPInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, false),
				_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE,
				CPInstanceOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPInstanceId));

		_collectionPersistenceFinderByCDORI_CII =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCDORI_CII",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionOptionRelId", "CPInstanceId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCDORI_CII",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"CPDefinitionOptionRelId", "CPInstanceId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCDORI_CII",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"CPDefinitionOptionRelId", "CPInstanceId"},
					false),
				_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE,
				CPInstanceOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPDefinitionOptionRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPDefinitionOptionRelId),
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPInstanceId));

		_uniquePersistenceFinderByCDOVRI_CII = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCDOVRI_CII",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"CPDefinitionOptionValueRelId", "CPInstanceId"},
				0, 0, false,
				CPInstanceOptionValueRel::getCPDefinitionOptionValueRelId,
				CPInstanceOptionValueRel::getCPInstanceId),
			_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE, "",
			new FinderColumn<>(
				"cpInstanceOptionValueRel.", "CPDefinitionOptionValueRelId",
				FinderColumn.Type.LONG, "=", true, true,
				CPInstanceOptionValueRel::getCPDefinitionOptionValueRelId),
			new FinderColumn<>(
				"cpInstanceOptionValueRel.", "CPInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				CPInstanceOptionValueRel::getCPInstanceId));

		_uniquePersistenceFinderByCDORI_CDOVRI_CII =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCDORI_CDOVRI_CII",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"CPDefinitionOptionRelId",
						"CPDefinitionOptionValueRelId", "CPInstanceId"
					},
					0, 0, false,
					CPInstanceOptionValueRel::getCPDefinitionOptionRelId,
					CPInstanceOptionValueRel::getCPDefinitionOptionValueRelId,
					CPInstanceOptionValueRel::getCPInstanceId),
				_SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE, "",
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPDefinitionOptionRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPDefinitionOptionRelId),
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPDefinitionOptionValueRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPDefinitionOptionValueRelId),
				new FinderColumn<>(
					"cpInstanceOptionValueRel.", "CPInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					CPInstanceOptionValueRel::getCPInstanceId));

		CPInstanceOptionValueRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPInstanceOptionValueRelUtil.setPersistence(null);

		entityCache.removeCache(CPInstanceOptionValueRelImpl.class.getName());
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
		CPInstanceOptionValueRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPINSTANCEOPTIONVALUEREL =
		"SELECT cpInstanceOptionValueRel FROM CPInstanceOptionValueRel cpInstanceOptionValueRel";

	private static final String _SQL_SELECT_CPINSTANCEOPTIONVALUEREL_WHERE =
		"SELECT cpInstanceOptionValueRel FROM CPInstanceOptionValueRel cpInstanceOptionValueRel WHERE ";

	private static final String _SQL_COUNT_CPINSTANCEOPTIONVALUEREL_WHERE =
		"SELECT COUNT(cpInstanceOptionValueRel) FROM CPInstanceOptionValueRel cpInstanceOptionValueRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:330008542
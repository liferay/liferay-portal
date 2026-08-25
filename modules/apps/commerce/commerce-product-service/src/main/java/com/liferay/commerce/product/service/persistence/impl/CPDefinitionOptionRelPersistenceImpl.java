/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPDefinitionOptionRelExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionRelTable;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionRelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the cp definition option rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionOptionRelPersistence.class)
public class CPDefinitionOptionRelPersistenceImpl
	extends BasePersistenceImpl
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
	implements CPDefinitionOptionRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionOptionRelUtil</code> to access the cp definition option rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionOptionRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definition option rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option rel that was removed
	 */
	@Override
	public CPDefinitionOptionRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionRelException {

		CPDefinitionOptionRel cpDefinitionOptionRel = findByUUID_G(
			uuid, groupId);

		return remove(cpDefinitionOptionRel);
	}

	/**
	 * Returns the number of cp definition option rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByGroupId_First(
			long groupId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp definition option rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByCompanyId_First(
			long companyId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp definition option rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByCPOptionId;

	/**
	 * Returns an ordered range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPOptionId.find(
			finderCache, new Object[] {CPOptionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByCPOptionId_First(
			long CPOptionId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByCPOptionId.findFirst(
			finderCache, new Object[] {CPOptionId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByCPOptionId_First(
		long CPOptionId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByCPOptionId.fetchFirst(
			finderCache, new Object[] {CPOptionId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPOptionId = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 */
	@Override
	public void removeByCPOptionId(long CPOptionId) {
		_collectionPersistenceFinderByCPOptionId.remove(
			finderCache, new Object[] {CPOptionId});
	}

	/**
	 * Returns the number of cp definition option rels where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByCPOptionId(long CPOptionId) {
		return _collectionPersistenceFinderByCPOptionId.count(
			finderCache, new Object[] {CPOptionId});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByC_C(long CPDefinitionId, long CPOptionId)
		throws NoSuchCPDefinitionOptionRelException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {CPDefinitionId, CPOptionId});
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByC_C(
		long CPDefinitionId, long CPOptionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {CPDefinitionId, CPOptionId},
			useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the cp definition option rel that was removed
	 */
	@Override
	public CPDefinitionOptionRel removeByC_C(
			long CPDefinitionId, long CPOptionId)
		throws NoSuchCPDefinitionOptionRelException {

		CPDefinitionOptionRel cpDefinitionOptionRel = findByC_C(
			CPDefinitionId, CPOptionId);

		return remove(cpDefinitionOptionRel);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and CPOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByC_C(long CPDefinitionId, long CPOptionId) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {CPDefinitionId, CPOptionId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByCPDI_R;

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDI_R.find(
			finderCache, new Object[] {CPDefinitionId, required}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByCPDI_R_First(
			long CPDefinitionId, boolean required,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByCPDI_R.findFirst(
			finderCache, new Object[] {CPDefinitionId, required},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByCPDI_R_First(
		long CPDefinitionId, boolean required,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByCPDI_R.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, required},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and required = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 */
	@Override
	public void removeByCPDI_R(long CPDefinitionId, boolean required) {
		_collectionPersistenceFinderByCPDI_R.remove(
			finderCache, new Object[] {CPDefinitionId, required});
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByCPDI_R(long CPDefinitionId, boolean required) {
		return _collectionPersistenceFinderByCPDI_R.count(
			finderCache, new Object[] {CPDefinitionId, required});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_collectionPersistenceFinderByC_SC;

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	@Override
	public List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_SC.find(
			finderCache, new Object[] {CPDefinitionId, skuContributor}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByC_SC_First(
			long CPDefinitionId, boolean skuContributor,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException {

		return _collectionPersistenceFinderByC_SC.findFirst(
			finderCache, new Object[] {CPDefinitionId, skuContributor},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByC_SC_First(
		long CPDefinitionId, boolean skuContributor,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return _collectionPersistenceFinderByC_SC.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, skuContributor},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 */
	@Override
	public void removeByC_SC(long CPDefinitionId, boolean skuContributor) {
		_collectionPersistenceFinderByC_SC.remove(
			finderCache, new Object[] {CPDefinitionId, skuContributor});
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByC_SC(long CPDefinitionId, boolean skuContributor) {
		return _collectionPersistenceFinderByC_SC.count(
			finderCache, new Object[] {CPDefinitionId, skuContributor});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByC_K(long CPDefinitionId, String key)
		throws NoSuchCPDefinitionOptionRelException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {CPDefinitionId, key});
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByC_K(
		long CPDefinitionId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {CPDefinitionId, key}, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the cp definition option rel that was removed
	 */
	@Override
	public CPDefinitionOptionRel removeByC_K(long CPDefinitionId, String key)
		throws NoSuchCPDefinitionOptionRelException {

		CPDefinitionOptionRel cpDefinitionOptionRel = findByC_K(
			CPDefinitionId, key);

		return remove(cpDefinitionOptionRel);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByC_K(long CPDefinitionId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {CPDefinitionId, key});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionRel, NoSuchCPDefinitionOptionRelException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionRelException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option rel that was removed
	 */
	@Override
	public CPDefinitionOptionRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionRelException {

		CPDefinitionOptionRel cpDefinitionOptionRel = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpDefinitionOptionRel);
	}

	/**
	 * Returns the number of cp definition option rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPDefinitionOptionRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinitionOptionRel.class);

		setModelImplClass(CPDefinitionOptionRelImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionOptionRelTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition option rel with the primary key. Does not add the cp definition option rel to the database.
	 *
	 * @param CPDefinitionOptionRelId the primary key for the new cp definition option rel
	 * @return the new cp definition option rel
	 */
	@Override
	public CPDefinitionOptionRel create(long CPDefinitionOptionRelId) {
		CPDefinitionOptionRel cpDefinitionOptionRel =
			new CPDefinitionOptionRelImpl();

		cpDefinitionOptionRel.setNew(true);
		cpDefinitionOptionRel.setPrimaryKey(CPDefinitionOptionRelId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinitionOptionRel.setUuid(uuid);

		cpDefinitionOptionRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpDefinitionOptionRel;
	}

	/**
	 * Removes the cp definition option rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel that was removed
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionRel remove(long CPDefinitionOptionRelId)
		throws NoSuchCPDefinitionOptionRelException {

		return remove((Serializable)CPDefinitionOptionRelId);
	}

	@Override
	protected CPDefinitionOptionRel removeImpl(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionOptionRel)) {
				cpDefinitionOptionRel = (CPDefinitionOptionRel)session.get(
					CPDefinitionOptionRelImpl.class,
					cpDefinitionOptionRel.getPrimaryKeyObj());
			}

			if ((cpDefinitionOptionRel != null) &&
				ctPersistenceHelper.isRemove(cpDefinitionOptionRel)) {

				session.delete(cpDefinitionOptionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionOptionRel != null) {
			clearCache(cpDefinitionOptionRel);
		}

		return cpDefinitionOptionRel;
	}

	@Override
	public CPDefinitionOptionRel updateImpl(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		boolean isNew = cpDefinitionOptionRel.isNew();

		if (!(cpDefinitionOptionRel instanceof
				CPDefinitionOptionRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionOptionRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionOptionRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionOptionRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionOptionRel implementation " +
					cpDefinitionOptionRel.getClass());
		}

		CPDefinitionOptionRelModelImpl cpDefinitionOptionRelModelImpl =
			(CPDefinitionOptionRelModelImpl)cpDefinitionOptionRel;

		if (Validator.isNull(cpDefinitionOptionRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinitionOptionRel.setUuid(uuid);
		}

		if (Validator.isNull(
				cpDefinitionOptionRel.getExternalReferenceCode())) {

			cpDefinitionOptionRel.setExternalReferenceCode(
				cpDefinitionOptionRel.getUuid());
		}
		else {
			if (!Objects.equals(
					cpDefinitionOptionRelModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpDefinitionOptionRel.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpDefinitionOptionRel.getCompanyId();

					long groupId = cpDefinitionOptionRel.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpDefinitionOptionRel.getPrimaryKey();
					}

					try {
						cpDefinitionOptionRel.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPDefinitionOptionRel.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								cpDefinitionOptionRel.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPDefinitionOptionRel ercCPDefinitionOptionRel = fetchByERC_C(
				cpDefinitionOptionRel.getExternalReferenceCode(),
				cpDefinitionOptionRel.getCompanyId());

			if (isNew) {
				if (ercCPDefinitionOptionRel != null) {
					throw new DuplicateCPDefinitionOptionRelExternalReferenceCodeException(
						"Duplicate cp definition option rel with external reference code " +
							cpDefinitionOptionRel.getExternalReferenceCode() +
								" and company " +
									cpDefinitionOptionRel.getCompanyId());
				}
			}
			else {
				if ((ercCPDefinitionOptionRel != null) &&
					(cpDefinitionOptionRel.getCPDefinitionOptionRelId() !=
						ercCPDefinitionOptionRel.
							getCPDefinitionOptionRelId())) {

					throw new DuplicateCPDefinitionOptionRelExternalReferenceCodeException(
						"Duplicate cp definition option rel with external reference code " +
							cpDefinitionOptionRel.getExternalReferenceCode() +
								" and company " +
									cpDefinitionOptionRel.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinitionOptionRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinitionOptionRel.setCreateDate(date);
			}
			else {
				cpDefinitionOptionRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionOptionRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinitionOptionRel.setModifiedDate(date);
			}
			else {
				cpDefinitionOptionRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDefinitionOptionRel)) {
				if (!isNew) {
					session.evict(
						CPDefinitionOptionRelImpl.class,
						cpDefinitionOptionRel.getPrimaryKeyObj());
				}

				session.save(cpDefinitionOptionRel);
			}
			else {
				cpDefinitionOptionRel = (CPDefinitionOptionRel)session.merge(
					cpDefinitionOptionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinitionOptionRel, false);

		if (isNew) {
			cpDefinitionOptionRel.setNew(false);
		}

		cpDefinitionOptionRel.resetOriginalValues();

		return cpDefinitionOptionRel;
	}

	/**
	 * Returns the cp definition option rel with the primary key or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionRel findByPrimaryKey(long CPDefinitionOptionRelId)
		throws NoSuchCPDefinitionOptionRelException {

		return findByPrimaryKey((Serializable)CPDefinitionOptionRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp definition option rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel, or <code>null</code> if a cp definition option rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchByPrimaryKey(
		long CPDefinitionOptionRelId) {

		return fetchByPrimaryKey((Serializable)CPDefinitionOptionRelId);
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
		return "CPDefinitionOptionRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONOPTIONREL;
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
		return CPDefinitionOptionRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDefinitionOptionRel";
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
		ctMergeColumnNames.add("CPOptionId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("commerceOptionTypeKey");
		ctMergeColumnNames.add("infoItemServiceKey");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("definedExternally");
		ctMergeColumnNames.add("facetable");
		ctMergeColumnNames.add("required");
		ctMergeColumnNames.add("skuContributor");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("priceType");
		ctMergeColumnNames.add("typeSettings");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPDefinitionOptionRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "CPOptionId"});

		_uniqueIndexColumnNames.add(new String[] {"CPDefinitionId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp definition option rel persistence.
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
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
			_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
			CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinitionOptionRel::getUuid),
				CPDefinitionOptionRel::getGroupId),
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionRel::getUuid),
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionOptionRel::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionOptionRel::getUuid),
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionRel::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, CPDefinitionOptionRel::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionRel::getCompanyId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionRel::getCPDefinitionId));

		_collectionPersistenceFinderByCPOptionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPOptionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPOptionId", new String[] {Long.class.getName()},
					new String[] {"CPOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPOptionId", new String[] {Long.class.getName()},
					new String[] {"CPOptionId"}, false),
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "CPOptionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionRel::getCPOptionId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"CPDefinitionId", "CPOptionId"}, 0, 0, false,
				CPDefinitionOptionRel::getCPDefinitionId,
				CPDefinitionOptionRel::getCPOptionId),
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "CPDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionRel::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "CPOptionId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionOptionRel::getCPOptionId));

		_collectionPersistenceFinderByCPDI_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPDI_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId", "required"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPDI_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"CPDefinitionId", "required"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPDI_R",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"CPDefinitionId", "required"}, false),
				_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
				CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionRel::getCPDefinitionId),
				new FinderColumn<>(
					"cpDefinitionOptionRel.", "required",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CPDefinitionOptionRel::isRequired));

		_collectionPersistenceFinderByC_SC = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_SC",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPDefinitionId", "skuContributor"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_SC",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPDefinitionId", "skuContributor"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_SC",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"CPDefinitionId", "skuContributor"}, false),
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE,
			_SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE,
			CPDefinitionOptionRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "CPDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionRel::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "skuContributor",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CPDefinitionOptionRel::isSkuContributor));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "key_"}, 0, 2, false,
				CPDefinitionOptionRel::getCPDefinitionId,
				convertNullFunction(CPDefinitionOptionRel::getKey)),
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "CPDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionRel::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionRel::getKey));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPDefinitionOptionRel::getExternalReferenceCode),
				CPDefinitionOptionRel::getCompanyId),
			_SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionRel::getExternalReferenceCode),
			new FinderColumn<>(
				"cpDefinitionOptionRel.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionOptionRel::getCompanyId));

		CPDefinitionOptionRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionOptionRelUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionOptionRelImpl.class.getName());
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
		CPDefinitionOptionRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITIONOPTIONREL =
		"SELECT cpDefinitionOptionRel FROM CPDefinitionOptionRel cpDefinitionOptionRel";

	private static final String _SQL_SELECT_CPDEFINITIONOPTIONREL_WHERE =
		"SELECT cpDefinitionOptionRel FROM CPDefinitionOptionRel cpDefinitionOptionRel WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONOPTIONREL_WHERE =
		"SELECT COUNT(cpDefinitionOptionRel) FROM CPDefinitionOptionRel cpDefinitionOptionRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1701395493
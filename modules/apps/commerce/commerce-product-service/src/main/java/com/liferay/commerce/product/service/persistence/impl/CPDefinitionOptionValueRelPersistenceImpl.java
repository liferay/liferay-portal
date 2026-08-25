/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.DuplicateCPDefinitionOptionValueRelExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRelTable;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelUtil;
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
 * The persistence implementation for the cp definition option value rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionOptionValueRelPersistence.class)
public class CPDefinitionOptionValueRelPersistenceImpl
	extends BasePersistenceImpl
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
	implements CPDefinitionOptionValueRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionOptionValueRelUtil</code> to access the cp definition option value rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionOptionValueRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option value rel that was removed
	 */
	@Override
	public CPDefinitionOptionValueRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionValueRelException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = findByUUID_G(
			uuid, groupId);

		return remove(cpDefinitionOptionValueRel);
	}

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByGroupId_First(
			long groupId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp definition option value rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByCompanyId_First(
			long companyId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp definition option value rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByCPDefinitionOptionRelId;

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByCPDefinitionOptionRelId(
		long CPDefinitionOptionRelId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.find(
			finderCache, new Object[] {CPDefinitionOptionRelId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByCPDefinitionOptionRelId_First(
			long CPDefinitionOptionRelId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.findFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByCPDefinitionOptionRelId_First(
		long CPDefinitionOptionRelId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionOptionRelId.fetchFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 */
	@Override
	public void removeByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {
		_collectionPersistenceFinderByCPDefinitionOptionRelId.remove(
			finderCache, new Object[] {CPDefinitionOptionRelId});
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {
		return _collectionPersistenceFinderByCPDefinitionOptionRelId.count(
			finderCache, new Object[] {CPDefinitionOptionRelId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByCPInstanceUuid;

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceUuid.find(
			finderCache, new Object[] {CPInstanceUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByCPInstanceUuid.findFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceUuid.fetchFirst(
			finderCache, new Object[] {CPInstanceUuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	@Override
	public void removeByCPInstanceUuid(String CPInstanceUuid) {
		_collectionPersistenceFinderByCPInstanceUuid.remove(
			finderCache, new Object[] {CPInstanceUuid});
	}

	/**
	 * Returns the number of cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByCPInstanceUuid(String CPInstanceUuid) {
		return _collectionPersistenceFinderByCPInstanceUuid.count(
			finderCache, new Object[] {CPInstanceUuid});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByKey;

	/**
	 * Returns an ordered range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKey.find(
			finderCache, new Object[] {key}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByKey_First(
			String key,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByKey.findFirst(
			finderCache, new Object[] {key}, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByKey_First(
		String key,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByKey.fetchFirst(
			finderCache, new Object[] {key}, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	@Override
	public void removeByKey(String key) {
		_collectionPersistenceFinderByKey.remove(
			finderCache, new Object[] {key});
	}

	/**
	 * Returns the number of cp definition option value rels where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByKey(String key) {
		return _collectionPersistenceFinderByKey.count(
			finderCache, new Object[] {key});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_uniquePersistenceFinderByC_K;

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByC_K(
			long CPDefinitionOptionRelId, String key)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {CPDefinitionOptionRelId, key});
	}

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByC_K(
		long CPDefinitionOptionRelId, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {CPDefinitionOptionRelId, key},
			useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the cp definition option value rel that was removed
	 */
	@Override
	public CPDefinitionOptionValueRel removeByC_K(
			long CPDefinitionOptionRelId, String key)
		throws NoSuchCPDefinitionOptionValueRelException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = findByC_K(
			CPDefinitionOptionRelId, key);

		return remove(cpDefinitionOptionValueRel);
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByC_K(long CPDefinitionOptionRelId, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {CPDefinitionOptionRelId, key});
	}

	private CollectionPersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_collectionPersistenceFinderByCDORI_P;

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	@Override
	public List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCDORI_P.find(
			finderCache, new Object[] {CPDefinitionOptionRelId, preselected},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByCDORI_P_First(
			long CPDefinitionOptionRelId, boolean preselected,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _collectionPersistenceFinderByCDORI_P.findFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId, preselected},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByCDORI_P_First(
		long CPDefinitionOptionRelId, boolean preselected,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return _collectionPersistenceFinderByCDORI_P.fetchFirst(
			finderCache, new Object[] {CPDefinitionOptionRelId, preselected},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 */
	@Override
	public void removeByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		_collectionPersistenceFinderByCDORI_P.remove(
			finderCache, new Object[] {CPDefinitionOptionRelId, preselected});
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		return _collectionPersistenceFinderByCDORI_P.count(
			finderCache, new Object[] {CPDefinitionOptionRelId, preselected});
	}

	private UniquePersistenceFinder
		<CPDefinitionOptionValueRel, NoSuchCPDefinitionOptionValueRelException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionValueRelException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option value rel that was removed
	 */
	@Override
	public CPDefinitionOptionValueRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionValueRelException {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel = findByERC_C(
			externalReferenceCode, companyId);

		return remove(cpDefinitionOptionValueRel);
	}

	/**
	 * Returns the number of cp definition option value rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CPDefinitionOptionValueRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinitionOptionValueRel.class);

		setModelImplClass(CPDefinitionOptionValueRelImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionOptionValueRelTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition option value rel with the primary key. Does not add the cp definition option value rel to the database.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key for the new cp definition option value rel
	 * @return the new cp definition option value rel
	 */
	@Override
	public CPDefinitionOptionValueRel create(
		long CPDefinitionOptionValueRelId) {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			new CPDefinitionOptionValueRelImpl();

		cpDefinitionOptionValueRel.setNew(true);
		cpDefinitionOptionValueRel.setPrimaryKey(CPDefinitionOptionValueRelId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinitionOptionValueRel.setUuid(uuid);

		cpDefinitionOptionValueRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpDefinitionOptionValueRel;
	}

	/**
	 * Removes the cp definition option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel that was removed
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel remove(long CPDefinitionOptionValueRelId)
		throws NoSuchCPDefinitionOptionValueRelException {

		return remove((Serializable)CPDefinitionOptionValueRelId);
	}

	@Override
	protected CPDefinitionOptionValueRel removeImpl(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionOptionValueRel)) {
				cpDefinitionOptionValueRel =
					(CPDefinitionOptionValueRel)session.get(
						CPDefinitionOptionValueRelImpl.class,
						cpDefinitionOptionValueRel.getPrimaryKeyObj());
			}

			if ((cpDefinitionOptionValueRel != null) &&
				ctPersistenceHelper.isRemove(cpDefinitionOptionValueRel)) {

				session.delete(cpDefinitionOptionValueRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionOptionValueRel != null) {
			clearCache(cpDefinitionOptionValueRel);
		}

		return cpDefinitionOptionValueRel;
	}

	@Override
	public CPDefinitionOptionValueRel updateImpl(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		boolean isNew = cpDefinitionOptionValueRel.isNew();

		if (!(cpDefinitionOptionValueRel instanceof
				CPDefinitionOptionValueRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionOptionValueRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionOptionValueRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionOptionValueRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionOptionValueRel implementation " +
					cpDefinitionOptionValueRel.getClass());
		}

		CPDefinitionOptionValueRelModelImpl
			cpDefinitionOptionValueRelModelImpl =
				(CPDefinitionOptionValueRelModelImpl)cpDefinitionOptionValueRel;

		if (Validator.isNull(cpDefinitionOptionValueRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinitionOptionValueRel.setUuid(uuid);
		}

		if (Validator.isNull(
				cpDefinitionOptionValueRel.getExternalReferenceCode())) {

			cpDefinitionOptionValueRel.setExternalReferenceCode(
				cpDefinitionOptionValueRel.getUuid());
		}
		else {
			if (!Objects.equals(
					cpDefinitionOptionValueRelModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					cpDefinitionOptionValueRel.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = cpDefinitionOptionValueRel.getCompanyId();

					long groupId = cpDefinitionOptionValueRel.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = cpDefinitionOptionValueRel.getPrimaryKey();
					}

					try {
						cpDefinitionOptionValueRel.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CPDefinitionOptionValueRel.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								cpDefinitionOptionValueRel.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CPDefinitionOptionValueRel ercCPDefinitionOptionValueRel =
				fetchByERC_C(
					cpDefinitionOptionValueRel.getExternalReferenceCode(),
					cpDefinitionOptionValueRel.getCompanyId());

			if (isNew) {
				if (ercCPDefinitionOptionValueRel != null) {
					throw new DuplicateCPDefinitionOptionValueRelExternalReferenceCodeException(
						"Duplicate cp definition option value rel with external reference code " +
							cpDefinitionOptionValueRel.
								getExternalReferenceCode() + " and company " +
									cpDefinitionOptionValueRel.getCompanyId());
				}
			}
			else {
				if ((ercCPDefinitionOptionValueRel != null) &&
					(cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId() !=
							ercCPDefinitionOptionValueRel.
								getCPDefinitionOptionValueRelId())) {

					throw new DuplicateCPDefinitionOptionValueRelExternalReferenceCodeException(
						"Duplicate cp definition option value rel with external reference code " +
							cpDefinitionOptionValueRel.
								getExternalReferenceCode() + " and company " +
									cpDefinitionOptionValueRel.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinitionOptionValueRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinitionOptionValueRel.setCreateDate(date);
			}
			else {
				cpDefinitionOptionValueRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionOptionValueRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinitionOptionValueRel.setModifiedDate(date);
			}
			else {
				cpDefinitionOptionValueRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDefinitionOptionValueRel)) {
				if (!isNew) {
					session.evict(
						CPDefinitionOptionValueRelImpl.class,
						cpDefinitionOptionValueRel.getPrimaryKeyObj());
				}

				session.save(cpDefinitionOptionValueRel);
			}
			else {
				cpDefinitionOptionValueRel =
					(CPDefinitionOptionValueRel)session.merge(
						cpDefinitionOptionValueRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinitionOptionValueRel, false);

		if (isNew) {
			cpDefinitionOptionValueRel.setNew(false);
		}

		cpDefinitionOptionValueRel.resetOriginalValues();

		return cpDefinitionOptionValueRel;
	}

	/**
	 * Returns the cp definition option value rel with the primary key or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel findByPrimaryKey(
			long CPDefinitionOptionValueRelId)
		throws NoSuchCPDefinitionOptionValueRelException {

		return findByPrimaryKey((Serializable)CPDefinitionOptionValueRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp definition option value rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel, or <code>null</code> if a cp definition option value rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel fetchByPrimaryKey(
		long CPDefinitionOptionValueRelId) {

		return fetchByPrimaryKey((Serializable)CPDefinitionOptionValueRelId);
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
		return "CPDefinitionOptionValueRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONOPTIONVALUEREL;
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
		return CPDefinitionOptionValueRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDefinitionOptionValueRel";
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
		ctMergeColumnNames.add("CPDefinitionOptionRelId");
		ctMergeColumnNames.add("CPInstanceUuid");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("preselected");
		ctMergeColumnNames.add("price");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("quantity");
		ctMergeColumnNames.add("unitOfMeasureKey");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPDefinitionOptionValueRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionOptionRelId", "key_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cp definition option value rel persistence.
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
			_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
			_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
			CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionValueRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinitionOptionValueRel::getUuid),
				CPDefinitionOptionValueRel::getGroupId),
			_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionValueRel::getUuid),
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionValueRel::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionOptionValueRel::getUuid),
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionValueRel::getCompanyId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionValueRel::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionValueRel::getCompanyId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "CPDefinitionOptionRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionValueRel::getCPDefinitionOptionRelId));

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
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "CPInstanceUuid",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionOptionValueRel::getCPInstanceUuid));

		_collectionPersistenceFinderByKey = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKey",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"key_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKey",
				new String[] {String.class.getName()}, new String[] {"key_"}, 0,
				1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKey",
				new String[] {String.class.getName()}, new String[] {"key_"}, 0,
				1, false, null),
			_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
			_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
			CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionValueRel::getKey));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionOptionRelId", "key_"}, 0, 2, false,
				CPDefinitionOptionValueRel::getCPDefinitionOptionRelId,
				convertNullFunction(CPDefinitionOptionValueRel::getKey)),
			_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "CPDefinitionOptionRelId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionValueRel::getCPDefinitionOptionRelId),
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "key", "key_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionValueRel::getKey));

		_collectionPersistenceFinderByCDORI_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCDORI_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionOptionRelId", "preselected"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCDORI_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"CPDefinitionOptionRelId", "preselected"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCDORI_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"CPDefinitionOptionRelId", "preselected"},
					false),
				_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				_SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE,
				CPDefinitionOptionValueRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "CPDefinitionOptionRelId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionOptionValueRel::getCPDefinitionOptionRelId),
				new FinderColumn<>(
					"cpDefinitionOptionValueRel.", "preselected",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CPDefinitionOptionValueRel::isPreselected));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					CPDefinitionOptionValueRel::getExternalReferenceCode),
				CPDefinitionOptionValueRel::getCompanyId),
			_SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionOptionValueRel::getExternalReferenceCode),
			new FinderColumn<>(
				"cpDefinitionOptionValueRel.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionOptionValueRel::getCompanyId));

		CPDefinitionOptionValueRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionOptionValueRelUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionOptionValueRelImpl.class.getName());
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
		CPDefinitionOptionValueRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITIONOPTIONVALUEREL =
		"SELECT cpDefinitionOptionValueRel FROM CPDefinitionOptionValueRel cpDefinitionOptionValueRel";

	private static final String _SQL_SELECT_CPDEFINITIONOPTIONVALUEREL_WHERE =
		"SELECT cpDefinitionOptionValueRel FROM CPDefinitionOptionValueRel cpDefinitionOptionValueRel WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONOPTIONVALUEREL_WHERE =
		"SELECT COUNT(cpDefinitionOptionValueRel) FROM CPDefinitionOptionValueRel cpDefinitionOptionValueRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1940994809
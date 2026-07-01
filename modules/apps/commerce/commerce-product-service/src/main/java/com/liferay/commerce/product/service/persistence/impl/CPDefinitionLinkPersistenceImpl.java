/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionLinkException;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionLinkTable;
import com.liferay.commerce.product.model.impl.CPDefinitionLinkImpl;
import com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl;
import com.liferay.commerce.product.service.persistence.CPDefinitionLinkPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLinkUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * The persistence implementation for the cp definition link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionLinkPersistence.class)
public class CPDefinitionLinkPersistenceImpl
	extends BasePersistenceImpl
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
	implements CPDefinitionLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionLinkUtil</code> to access the cp definition link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByUuid_First(
			String uuid, OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByUuid_First(
		String uuid, OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionLinkException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition link that was removed
	 */
	@Override
	public CPDefinitionLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionLinkException {

		CPDefinitionLink cpDefinitionLink = findByUUID_G(uuid, groupId);

		return remove(cpDefinitionLink);
	}

	/**
	 * Returns the number of cp definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCProductId;

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCProductId_First(
			long CProductId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCProductId.findFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCProductId_First(
		long CProductId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCProductId.fetchFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		_collectionPersistenceFinderByCProductId.remove(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _collectionPersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCPD_T;

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCPD_T(
		long CPDefinitionId, String type, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPD_T.find(
			finderCache, new Object[] {CPDefinitionId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCPD_T_First(
			long CPDefinitionId, String type,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCPD_T.findFirst(
			finderCache, new Object[] {CPDefinitionId, type},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCPD_T_First(
		long CPDefinitionId, String type,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCPD_T.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, type},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and type = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 */
	@Override
	public void removeByCPD_T(long CPDefinitionId, String type) {
		_collectionPersistenceFinderByCPD_T.remove(
			finderCache, new Object[] {CPDefinitionId, type});
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCPD_T(long CPDefinitionId, String type) {
		return _collectionPersistenceFinderByCPD_T.count(
			finderCache, new Object[] {CPDefinitionId, type});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCPD_S;

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCPD_S(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPD_S.find(
			finderCache, new Object[] {CPDefinitionId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCPD_S_First(
			long CPDefinitionId, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCPD_S.findFirst(
			finderCache, new Object[] {CPDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCPD_S_First(
		long CPDefinitionId, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCPD_S.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 */
	@Override
	public void removeByCPD_S(long CPDefinitionId, int status) {
		_collectionPersistenceFinderByCPD_S.remove(
			finderCache, new Object[] {CPDefinitionId, status});
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCPD_S(long CPDefinitionId, int status) {
		return _collectionPersistenceFinderByCPD_S.count(
			finderCache, new Object[] {CPDefinitionId, status});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCP_T;

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCP_T(
		long CProductId, String type, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCP_T.find(
			finderCache, new Object[] {CProductId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCP_T_First(
			long CProductId, String type,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCP_T.findFirst(
			finderCache, new Object[] {CProductId, type}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCP_T_First(
		long CProductId, String type,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCP_T.fetchFirst(
			finderCache, new Object[] {CProductId, type}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; and type = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 */
	@Override
	public void removeByCP_T(long CProductId, String type) {
		_collectionPersistenceFinderByCP_T.remove(
			finderCache, new Object[] {CProductId, type});
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63; and type = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCP_T(long CProductId, String type) {
		return _collectionPersistenceFinderByCP_T.count(
			finderCache, new Object[] {CProductId, type});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCP_S;

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCP_S(
		long CProductId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCP_S.find(
			finderCache, new Object[] {CProductId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCP_S_First(
			long CProductId, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCP_S.findFirst(
			finderCache, new Object[] {CProductId, status}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCP_S_First(
		long CProductId, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCP_S.fetchFirst(
			finderCache, new Object[] {CProductId, status}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; and status = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 */
	@Override
	public void removeByCP_S(long CProductId, int status) {
		_collectionPersistenceFinderByCP_S.remove(
			finderCache, new Object[] {CProductId, status});
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCP_S(long CProductId, int status) {
		return _collectionPersistenceFinderByCP_S.count(
			finderCache, new Object[] {CProductId, status});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByLtD_S;

	/**
	 * Returns all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtD_S(Date displayDate, int status) {
		return findByLtD_S(
			displayDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return findByLtD_S(displayDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return findByLtD_S(
			displayDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtD_S.find(
			finderCache, new Object[] {displayDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByLtD_S.findFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByLtD_S.fetchFirst(
			finderCache, new Object[] {displayDate, status}, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where displayDate &lt; &#63; and status = &#63; from the database.
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
	 * Returns the number of cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByLtD_S(Date displayDate, int status) {
		return _collectionPersistenceFinderByLtD_S.count(
			finderCache, new Object[] {displayDate, status});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByLtE_S;

	/**
	 * Returns all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtE_S(Date expirationDate, int status) {
		return findByLtE_S(
			expirationDate, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return findByLtE_S(expirationDate, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return findByLtE_S(
			expirationDate, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtE_S.find(
			finderCache, new Object[] {expirationDate, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByLtE_S.findFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByLtE_S.fetchFirst(
			finderCache, new Object[] {expirationDate, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition links where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	@Override
	public void removeByLtE_S(Date expirationDate, int status) {
		_collectionPersistenceFinderByLtE_S.remove(
			finderCache, new Object[] {expirationDate, status});
	}

	/**
	 * Returns the number of cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByLtE_S(Date expirationDate, int status) {
		return _collectionPersistenceFinderByLtE_S.count(
			finderCache, new Object[] {expirationDate, status});
	}

	private UniquePersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_uniquePersistenceFinderByC_C_T;

	/**
	 * Returns the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByC_C_T(
			long CPDefinitionId, long CProductId, String type)
		throws NoSuchCPDefinitionLinkException {

		return _uniquePersistenceFinderByC_C_T.find(
			finderCache, new Object[] {CPDefinitionId, CProductId, type});
	}

	/**
	 * Returns the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByC_C_T(
		long CPDefinitionId, long CProductId, String type,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_T.fetch(
			finderCache, new Object[] {CPDefinitionId, CProductId, type},
			useFinderCache);
	}

	/**
	 * Removes the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the cp definition link that was removed
	 */
	@Override
	public CPDefinitionLink removeByC_C_T(
			long CPDefinitionId, long CProductId, String type)
		throws NoSuchCPDefinitionLinkException {

		CPDefinitionLink cpDefinitionLink = findByC_C_T(
			CPDefinitionId, CProductId, type);

		return remove(cpDefinitionLink);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByC_C_T(long CPDefinitionId, long CProductId, String type) {
		return _uniquePersistenceFinderByC_C_T.count(
			finderCache, new Object[] {CPDefinitionId, CProductId, type});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCPD_T_S;

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCPD_T_S(
		long CPDefinitionId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPD_T_S.find(
			finderCache, new Object[] {CPDefinitionId, type, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCPD_T_S_First(
			long CPDefinitionId, String type, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCPD_T_S.findFirst(
			finderCache, new Object[] {CPDefinitionId, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCPD_T_S_First(
		long CPDefinitionId, String type, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCPD_T_S.fetchFirst(
			finderCache, new Object[] {CPDefinitionId, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByCPD_T_S(long CPDefinitionId, String type, int status) {
		_collectionPersistenceFinderByCPD_T_S.remove(
			finderCache, new Object[] {CPDefinitionId, type, status});
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCPD_T_S(long CPDefinitionId, String type, int status) {
		return _collectionPersistenceFinderByCPD_T_S.count(
			finderCache, new Object[] {CPDefinitionId, type, status});
	}

	private CollectionPersistenceFinder
		<CPDefinitionLink, NoSuchCPDefinitionLinkException>
			_collectionPersistenceFinderByCP_T_S;

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	@Override
	public List<CPDefinitionLink> findByCP_T_S(
		long CProductId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCP_T_S.find(
			finderCache, new Object[] {CProductId, type, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink findByCP_T_S_First(
			long CProductId, String type, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws NoSuchCPDefinitionLinkException {

		return _collectionPersistenceFinderByCP_T_S.findFirst(
			finderCache, new Object[] {CProductId, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchByCP_T_S_First(
		long CProductId, String type, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCP_T_S.fetchFirst(
			finderCache, new Object[] {CProductId, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByCP_T_S(long CProductId, String type, int status) {
		_collectionPersistenceFinderByCP_T_S.remove(
			finderCache, new Object[] {CProductId, type, status});
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	@Override
	public int countByCP_T_S(long CProductId, String type, int status) {
		return _collectionPersistenceFinderByCP_T_S.count(
			finderCache, new Object[] {CProductId, type, status});
	}

	public CPDefinitionLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinitionLink.class);

		setModelImplClass(CPDefinitionLinkImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionLinkTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition link with the primary key. Does not add the cp definition link to the database.
	 *
	 * @param CPDefinitionLinkId the primary key for the new cp definition link
	 * @return the new cp definition link
	 */
	@Override
	public CPDefinitionLink create(long CPDefinitionLinkId) {
		CPDefinitionLink cpDefinitionLink = new CPDefinitionLinkImpl();

		cpDefinitionLink.setNew(true);
		cpDefinitionLink.setPrimaryKey(CPDefinitionLinkId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinitionLink.setUuid(uuid);

		cpDefinitionLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpDefinitionLink;
	}

	/**
	 * Removes the cp definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link that was removed
	 * @throws NoSuchCPDefinitionLinkException if a cp definition link with the primary key could not be found
	 */
	@Override
	public CPDefinitionLink remove(long CPDefinitionLinkId)
		throws NoSuchCPDefinitionLinkException {

		return remove((Serializable)CPDefinitionLinkId);
	}

	@Override
	protected CPDefinitionLink removeImpl(CPDefinitionLink cpDefinitionLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionLink)) {
				cpDefinitionLink = (CPDefinitionLink)session.get(
					CPDefinitionLinkImpl.class,
					cpDefinitionLink.getPrimaryKeyObj());
			}

			if ((cpDefinitionLink != null) &&
				ctPersistenceHelper.isRemove(cpDefinitionLink)) {

				session.delete(cpDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionLink != null) {
			clearCache(cpDefinitionLink);
		}

		return cpDefinitionLink;
	}

	@Override
	public CPDefinitionLink updateImpl(CPDefinitionLink cpDefinitionLink) {
		boolean isNew = cpDefinitionLink.isNew();

		if (!(cpDefinitionLink instanceof CPDefinitionLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionLink implementation " +
					cpDefinitionLink.getClass());
		}

		CPDefinitionLinkModelImpl cpDefinitionLinkModelImpl =
			(CPDefinitionLinkModelImpl)cpDefinitionLink;

		if (Validator.isNull(cpDefinitionLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinitionLink.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinitionLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinitionLink.setCreateDate(date);
			}
			else {
				cpDefinitionLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinitionLink.setModifiedDate(date);
			}
			else {
				cpDefinitionLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDefinitionLink)) {
				if (!isNew) {
					session.evict(
						CPDefinitionLinkImpl.class,
						cpDefinitionLink.getPrimaryKeyObj());
				}

				session.save(cpDefinitionLink);
			}
			else {
				cpDefinitionLink = (CPDefinitionLink)session.merge(
					cpDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinitionLink, false);

		if (isNew) {
			cpDefinitionLink.setNew(false);
		}

		cpDefinitionLink.resetOriginalValues();

		return cpDefinitionLink;
	}

	/**
	 * Returns the cp definition link with the primary key or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a cp definition link with the primary key could not be found
	 */
	@Override
	public CPDefinitionLink findByPrimaryKey(long CPDefinitionLinkId)
		throws NoSuchCPDefinitionLinkException {

		return findByPrimaryKey((Serializable)CPDefinitionLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link, or <code>null</code> if a cp definition link with the primary key could not be found
	 */
	@Override
	public CPDefinitionLink fetchByPrimaryKey(long CPDefinitionLinkId) {
		return fetchByPrimaryKey((Serializable)CPDefinitionLinkId);
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
		return "CPDefinitionLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONLINK;
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
		return CPDefinitionLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDefinitionLink";
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
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("displayDate");
		ctMergeColumnNames.add("expirationDate");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("type_");
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
			CTColumnResolutionType.PK,
			Collections.singleton("CPDefinitionLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "CProductId", "type_"});
	}

	/**
	 * Initializes the cp definition link persistence.
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
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPDefinitionLink::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinitionLink::getUuid),
				CPDefinitionLink::getGroupId),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionLink.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPDefinitionLink::getUuid),
			new FinderColumn<>(
				"cpDefinitionLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinitionLink::getGroupId));

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
				_SQL_SELECT_CPDEFINITIONLINK_WHERE,
				_SQL_COUNT_CPDEFINITIONLINK_WHERE,
				CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDefinitionLink.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionLink::getUuid),
				new FinderColumn<>(
					"cpDefinitionLink.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CPDefinitionLink::getCompanyId));

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
				_SQL_SELECT_CPDEFINITIONLINK_WHERE,
				_SQL_COUNT_CPDEFINITIONLINK_WHERE,
				CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDefinitionLink.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionLink::getCPDefinitionId));

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
				_SQL_SELECT_CPDEFINITIONLINK_WHERE,
				_SQL_COUNT_CPDEFINITIONLINK_WHERE,
				CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDefinitionLink.", "CProductId", FinderColumn.Type.LONG,
					"=", true, true, CPDefinitionLink::getCProductId));

		_collectionPersistenceFinderByCPD_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPD_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPDefinitionId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPD_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "type_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPD_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "type_"}, 0, 2, false, null),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "CPDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionLink::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionLink.", "type", "type_", FinderColumn.Type.STRING,
				"=", true, true, CPDefinitionLink::getType));

		_collectionPersistenceFinderByCPD_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPD_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CPDefinitionId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPD_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CPDefinitionId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPD_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CPDefinitionId", "status"}, false),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "CPDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionLink::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CPDefinitionLink::getStatus));

		_collectionPersistenceFinderByCP_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCP_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CProductId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCP_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CProductId", "type_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCP_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CProductId", "type_"}, 0, 2, false, null),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "CProductId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinitionLink::getCProductId),
			new FinderColumn<>(
				"cpDefinitionLink.", "type", "type_", FinderColumn.Type.STRING,
				"=", true, true, CPDefinitionLink::getType));

		_collectionPersistenceFinderByCP_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCP_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"CProductId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CProductId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CProductId", "status"}, false),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "CProductId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinitionLink::getCProductId),
			new FinderColumn<>(
				"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CPDefinitionLink::getStatus));

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
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "displayDate", FinderColumn.Type.DATE, "<",
				true, true, CPDefinitionLink::getDisplayDate),
			new FinderColumn<>(
				"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CPDefinitionLink::getStatus));

		_collectionPersistenceFinderByLtE_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtE_S",
				new String[] {
					Date.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"expirationDate", "status"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtE_S",
				new String[] {Date.class.getName(), Integer.class.getName()},
				new String[] {"expirationDate", "status"}, false),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE,
			_SQL_COUNT_CPDEFINITIONLINK_WHERE,
			CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDefinitionLink.", "expirationDate", FinderColumn.Type.DATE,
				"<", true, true, CPDefinitionLink::getExpirationDate),
			new FinderColumn<>(
				"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CPDefinitionLink::getStatus));

		_uniquePersistenceFinderByC_C_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"CPDefinitionId", "CProductId", "type_"}, 0, 4,
				false, CPDefinitionLink::getCPDefinitionId,
				CPDefinitionLink::getCProductId,
				convertNullFunction(CPDefinitionLink::getType)),
			_SQL_SELECT_CPDEFINITIONLINK_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionLink.", "CPDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, CPDefinitionLink::getCPDefinitionId),
			new FinderColumn<>(
				"cpDefinitionLink.", "CProductId", FinderColumn.Type.LONG, "=",
				true, true, CPDefinitionLink::getCProductId),
			new FinderColumn<>(
				"cpDefinitionLink.", "type", "type_", FinderColumn.Type.STRING,
				"=", true, true, CPDefinitionLink::getType));

		_collectionPersistenceFinderByCPD_T_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPD_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId", "type_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPD_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"CPDefinitionId", "type_", "status"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPD_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"CPDefinitionId", "type_", "status"}, 0, 2,
					false, null),
				_SQL_SELECT_CPDEFINITIONLINK_WHERE,
				_SQL_COUNT_CPDEFINITIONLINK_WHERE,
				CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDefinitionLink.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionLink::getCPDefinitionId),
				new FinderColumn<>(
					"cpDefinitionLink.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionLink::getType),
				new FinderColumn<>(
					"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CPDefinitionLink::getStatus));

		_collectionPersistenceFinderByCP_T_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCP_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CProductId", "type_", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCP_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"CProductId", "type_", "status"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCP_T_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"CProductId", "type_", "status"}, 0, 2, false,
					null),
				_SQL_SELECT_CPDEFINITIONLINK_WHERE,
				_SQL_COUNT_CPDEFINITIONLINK_WHERE,
				CPDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDefinitionLink.", "CProductId", FinderColumn.Type.LONG,
					"=", true, true, CPDefinitionLink::getCProductId),
				new FinderColumn<>(
					"cpDefinitionLink.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionLink::getType),
				new FinderColumn<>(
					"cpDefinitionLink.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, CPDefinitionLink::getStatus));

		CPDefinitionLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionLinkUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionLinkImpl.class.getName());
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
		CPDefinitionLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITIONLINK =
		"SELECT cpDefinitionLink FROM CPDefinitionLink cpDefinitionLink";

	private static final String _SQL_SELECT_CPDEFINITIONLINK_WHERE =
		"SELECT cpDefinitionLink FROM CPDefinitionLink cpDefinitionLink WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONLINK_WHERE =
		"SELECT COUNT(cpDefinitionLink) FROM CPDefinitionLink cpDefinitionLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDefinitionLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLinkPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:836448825
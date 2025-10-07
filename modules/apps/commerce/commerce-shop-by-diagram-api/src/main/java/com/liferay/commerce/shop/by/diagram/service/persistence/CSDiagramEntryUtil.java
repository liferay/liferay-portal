/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cs diagram entry service. This utility wraps <code>com.liferay.commerce.shop.by.diagram.service.persistence.impl.CSDiagramEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryPersistence
 * @generated
 */
public class CSDiagramEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(CSDiagramEntry csDiagramEntry) {
		getPersistence().clearCache(csDiagramEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, CSDiagramEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CSDiagramEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CSDiagramEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CSDiagramEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CSDiagramEntry update(CSDiagramEntry csDiagramEntry) {
		return getPersistence().update(csDiagramEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CSDiagramEntry update(
		CSDiagramEntry csDiagramEntry, ServiceContext serviceContext) {

		return getPersistence().update(csDiagramEntry, serviceContext);
	}

	/**
	 * Returns all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId) {

		return getPersistence().findByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns a range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCPDefinitionId_Last(
			long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPDefinitionId_Last(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPDefinitionId_Last(
		long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCPDefinitionId_Last(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry[] findByCPDefinitionId_PrevAndNext(
			long CSDiagramEntryId, long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPDefinitionId_PrevAndNext(
			CSDiagramEntryId, CPDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public static void removeByCPDefinitionId(long CPDefinitionId) {
		getPersistence().removeByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram entries
	 */
	public static int countByCPDefinitionId(long CPDefinitionId) {
		return getPersistence().countByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPInstanceId(long CPInstanceId) {
		return getPersistence().findByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns a range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end) {

		return getPersistence().findByCPInstanceId(CPInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().findByCPInstanceId(
			CPInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPInstanceId(
			CPInstanceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPInstanceId_First(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCPInstanceId_First(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCPInstanceId_Last(
			long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPInstanceId_Last(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPInstanceId_Last(
		long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCPInstanceId_Last(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry[] findByCPInstanceId_PrevAndNext(
			long CSDiagramEntryId, long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPInstanceId_PrevAndNext(
			CSDiagramEntryId, CPInstanceId, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	public static void removeByCPInstanceId(long CPInstanceId) {
		getPersistence().removeByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns the number of cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cs diagram entries
	 */
	public static int countByCPInstanceId(long CPInstanceId) {
		return getPersistence().countByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns all the cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCProductId(long CProductId) {
		return getPersistence().findByCProductId(CProductId);
	}

	/**
	 * Returns a range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end) {

		return getPersistence().findByCProductId(CProductId, start, end);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	public static List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCProductId_First(
			long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCProductId_First(
		long CProductId, OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCProductId_Last(
			long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCProductId_Last(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the last cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCProductId_Last(
		long CProductId, OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().fetchByCProductId_Last(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the cs diagram entries before and after the current cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CSDiagramEntryId the primary key of the current cs diagram entry
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry[] findByCProductId_PrevAndNext(
			long CSDiagramEntryId, long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCProductId_PrevAndNext(
			CSDiagramEntryId, CProductId, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	public static void removeByCProductId(long CProductId) {
		getPersistence().removeByCProductId(CProductId);
	}

	/**
	 * Returns the number of cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cs diagram entries
	 */
	public static int countByCProductId(long CProductId) {
		return getPersistence().countByCProductId(CProductId);
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByCPDI_S(
			long CPDefinitionId, String sequence)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByCPDI_S(CPDefinitionId, sequence);
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPDI_S(
		long CPDefinitionId, String sequence) {

		return getPersistence().fetchByCPDI_S(CPDefinitionId, sequence);
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByCPDI_S(
		long CPDefinitionId, String sequence, boolean useFinderCache) {

		return getPersistence().fetchByCPDI_S(
			CPDefinitionId, sequence, useFinderCache);
	}

	/**
	 * Removes the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the cs diagram entry that was removed
	 */
	public static CSDiagramEntry removeByCPDI_S(
			long CPDefinitionId, String sequence)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().removeByCPDI_S(CPDefinitionId, sequence);
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63; and sequence = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the number of matching cs diagram entries
	 */
	public static int countByCPDI_S(long CPDefinitionId, String sequence) {
		return getPersistence().countByCPDI_S(CPDefinitionId, sequence);
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	public static CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cs diagram entry that was removed
	 */
	public static CSDiagramEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cs diagram entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cs diagram entries
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Caches the cs diagram entry in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntry the cs diagram entry
	 */
	public static void cacheResult(CSDiagramEntry csDiagramEntry) {
		getPersistence().cacheResult(csDiagramEntry);
	}

	/**
	 * Caches the cs diagram entries in the entity cache if it is enabled.
	 *
	 * @param csDiagramEntries the cs diagram entries
	 */
	public static void cacheResult(List<CSDiagramEntry> csDiagramEntries) {
		getPersistence().cacheResult(csDiagramEntries);
	}

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	public static CSDiagramEntry create(long CSDiagramEntryId) {
		return getPersistence().create(CSDiagramEntryId);
	}

	/**
	 * Removes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry remove(long CSDiagramEntryId)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().remove(CSDiagramEntryId);
	}

	public static CSDiagramEntry updateImpl(CSDiagramEntry csDiagramEntry) {
		return getPersistence().updateImpl(csDiagramEntry);
	}

	/**
	 * Returns the cs diagram entry with the primary key or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry findByPrimaryKey(long CSDiagramEntryId)
		throws com.liferay.commerce.shop.by.diagram.exception.
			NoSuchCSDiagramEntryException {

		return getPersistence().findByPrimaryKey(CSDiagramEntryId);
	}

	/**
	 * Returns the cs diagram entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry, or <code>null</code> if a cs diagram entry with the primary key could not be found
	 */
	public static CSDiagramEntry fetchByPrimaryKey(long CSDiagramEntryId) {
		return getPersistence().fetchByPrimaryKey(CSDiagramEntryId);
	}

	/**
	 * Returns all the cs diagram entries.
	 *
	 * @return the cs diagram entries
	 */
	public static List<CSDiagramEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of cs diagram entries
	 */
	public static List<CSDiagramEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cs diagram entries
	 */
	public static List<CSDiagramEntry> findAll(
		int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cs diagram entries
	 */
	public static List<CSDiagramEntry> findAll(
		int start, int end, OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cs diagram entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cs diagram entries.
	 *
	 * @return the number of cs diagram entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CSDiagramEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CSDiagramEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CSDiagramEntryPersistence _persistence;

}
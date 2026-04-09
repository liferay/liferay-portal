/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.persistence;

import com.liferay.analytics.message.storage.model.AnalyticsAssociation;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the analytics association service. This utility wraps <code>com.liferay.analytics.message.storage.service.persistence.impl.AnalyticsAssociationPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AnalyticsAssociationPersistence
 * @generated
 */
public class AnalyticsAssociationUtil {

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
	public static void clearCache(AnalyticsAssociation analyticsAssociation) {
		getPersistence().clearCache(analyticsAssociation);
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
	public static Map<Serializable, AnalyticsAssociation> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AnalyticsAssociation> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AnalyticsAssociation> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AnalyticsAssociation> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AnalyticsAssociation update(
		AnalyticsAssociation analyticsAssociation) {

		return getPersistence().update(analyticsAssociation);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AnalyticsAssociation update(
		AnalyticsAssociation analyticsAssociation,
		ServiceContext serviceContext) {

		return getPersistence().update(analyticsAssociation, serviceContext);
	}

	/**
	 * Returns all the analytics associations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation findByCompanyId_First(
			long companyId,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching analytics associations
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate) {

		return getPersistence().findByC_LtM(companyId, modifiedDate);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end) {

		return getPersistence().findByC_LtM(
			companyId, modifiedDate, start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findByC_LtM(
			companyId, modifiedDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LtM(
			companyId, modifiedDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation findByC_LtM_First(
			long companyId, Date modifiedDate,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByC_LtM_First(
			companyId, modifiedDate, orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation fetchByC_LtM_First(
		long companyId, Date modifiedDate,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().fetchByC_LtM_First(
			companyId, modifiedDate, orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 */
	public static void removeByC_LtM(long companyId, Date modifiedDate) {
		getPersistence().removeByC_LtM(companyId, modifiedDate);
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the number of matching analytics associations
	 */
	public static int countByC_LtM(long companyId, Date modifiedDate) {
		return getPersistence().countByC_LtM(companyId, modifiedDate);
	}

	/**
	 * Returns all the analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @return the matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A(
		long companyId, String associationClassName) {

		return getPersistence().findByC_A(companyId, associationClassName);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A(
		long companyId, String associationClassName, int start, int end) {

		return getPersistence().findByC_A(
			companyId, associationClassName, start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A(
		long companyId, String associationClassName, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findByC_A(
			companyId, associationClassName, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A(
		long companyId, String associationClassName, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A(
			companyId, associationClassName, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation findByC_A_First(
			long companyId, String associationClassName,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByC_A_First(
			companyId, associationClassName, orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation fetchByC_A_First(
		long companyId, String associationClassName,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().fetchByC_A_First(
			companyId, associationClassName, orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and associationClassName = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 */
	public static void removeByC_A(
		long companyId, String associationClassName) {

		getPersistence().removeByC_A(companyId, associationClassName);
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @return the number of matching analytics associations
	 */
	public static int countByC_A(long companyId, String associationClassName) {
		return getPersistence().countByC_A(companyId, associationClassName);
	}

	/**
	 * Returns all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @return the matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		return getPersistence().findByC_GtM_A(
			companyId, modifiedDate, associationClassName);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end) {

		return getPersistence().findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation findByC_GtM_A_First(
			long companyId, Date modifiedDate, String associationClassName,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByC_GtM_A_First(
			companyId, modifiedDate, associationClassName, orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation fetchByC_GtM_A_First(
		long companyId, Date modifiedDate, String associationClassName,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().fetchByC_GtM_A_First(
			companyId, modifiedDate, associationClassName, orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 */
	public static void removeByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		getPersistence().removeByC_GtM_A(
			companyId, modifiedDate, associationClassName);
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @return the number of matching analytics associations
	 */
	public static int countByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		return getPersistence().countByC_GtM_A(
			companyId, modifiedDate, associationClassName);
	}

	/**
	 * Returns all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @return the matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A_A(
		long companyId, String associationClassName, long associationClassPK) {

		return getPersistence().findByC_A_A(
			companyId, associationClassName, associationClassPK);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A_A(
		long companyId, String associationClassName, long associationClassPK,
		int start, int end) {

		return getPersistence().findByC_A_A(
			companyId, associationClassName, associationClassPK, start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A_A(
		long companyId, String associationClassName, long associationClassPK,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findByC_A_A(
			companyId, associationClassName, associationClassPK, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	public static List<AnalyticsAssociation> findByC_A_A(
		long companyId, String associationClassName, long associationClassPK,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A_A(
			companyId, associationClassName, associationClassPK, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation findByC_A_A_First(
			long companyId, String associationClassName,
			long associationClassPK,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByC_A_A_First(
			companyId, associationClassName, associationClassPK,
			orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	public static AnalyticsAssociation fetchByC_A_A_First(
		long companyId, String associationClassName, long associationClassPK,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().fetchByC_A_A_First(
			companyId, associationClassName, associationClassPK,
			orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 */
	public static void removeByC_A_A(
		long companyId, String associationClassName, long associationClassPK) {

		getPersistence().removeByC_A_A(
			companyId, associationClassName, associationClassPK);
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @return the number of matching analytics associations
	 */
	public static int countByC_A_A(
		long companyId, String associationClassName, long associationClassPK) {

		return getPersistence().countByC_A_A(
			companyId, associationClassName, associationClassPK);
	}

	/**
	 * Caches the analytics association in the entity cache if it is enabled.
	 *
	 * @param analyticsAssociation the analytics association
	 */
	public static void cacheResult(AnalyticsAssociation analyticsAssociation) {
		getPersistence().cacheResult(analyticsAssociation);
	}

	/**
	 * Caches the analytics associations in the entity cache if it is enabled.
	 *
	 * @param analyticsAssociations the analytics associations
	 */
	public static void cacheResult(
		List<AnalyticsAssociation> analyticsAssociations) {

		getPersistence().cacheResult(analyticsAssociations);
	}

	/**
	 * Creates a new analytics association with the primary key. Does not add the analytics association to the database.
	 *
	 * @param analyticsAssociationId the primary key for the new analytics association
	 * @return the new analytics association
	 */
	public static AnalyticsAssociation create(long analyticsAssociationId) {
		return getPersistence().create(analyticsAssociationId);
	}

	/**
	 * Removes the analytics association with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association that was removed
	 * @throws NoSuchAssociationException if a analytics association with the primary key could not be found
	 */
	public static AnalyticsAssociation remove(long analyticsAssociationId)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().remove(analyticsAssociationId);
	}

	public static AnalyticsAssociation updateImpl(
		AnalyticsAssociation analyticsAssociation) {

		return getPersistence().updateImpl(analyticsAssociation);
	}

	/**
	 * Returns the analytics association with the primary key or throws a <code>NoSuchAssociationException</code> if it could not be found.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association
	 * @throws NoSuchAssociationException if a analytics association with the primary key could not be found
	 */
	public static AnalyticsAssociation findByPrimaryKey(
			long analyticsAssociationId)
		throws com.liferay.analytics.message.storage.exception.
			NoSuchAssociationException {

		return getPersistence().findByPrimaryKey(analyticsAssociationId);
	}

	/**
	 * Returns the analytics association with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association, or <code>null</code> if a analytics association with the primary key could not be found
	 */
	public static AnalyticsAssociation fetchByPrimaryKey(
		long analyticsAssociationId) {

		return getPersistence().fetchByPrimaryKey(analyticsAssociationId);
	}

	/**
	 * Returns all the analytics associations.
	 *
	 * @return the analytics associations
	 */
	public static List<AnalyticsAssociation> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the analytics associations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of analytics associations
	 */
	public static List<AnalyticsAssociation> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the analytics associations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of analytics associations
	 */
	public static List<AnalyticsAssociation> findAll(
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the analytics associations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of analytics associations
	 */
	public static List<AnalyticsAssociation> findAll(
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the analytics associations from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of analytics associations.
	 *
	 * @return the number of analytics associations
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static AnalyticsAssociationPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AnalyticsAssociationPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AnalyticsAssociationPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2056282921
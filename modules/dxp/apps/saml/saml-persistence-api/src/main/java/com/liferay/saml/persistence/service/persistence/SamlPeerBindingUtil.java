/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.saml.persistence.model.SamlPeerBinding;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the saml peer binding service. This utility wraps <code>com.liferay.saml.persistence.service.persistence.impl.SamlPeerBindingPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlPeerBindingPersistence
 * @generated
 */
public class SamlPeerBindingUtil {

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
	public static void clearCache(SamlPeerBinding samlPeerBinding) {
		getPersistence().clearCache(samlPeerBinding);
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
	public static Map<Serializable, SamlPeerBinding> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SamlPeerBinding> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SamlPeerBinding> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SamlPeerBinding> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SamlPeerBinding update(SamlPeerBinding samlPeerBinding) {
		return getPersistence().update(samlPeerBinding);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SamlPeerBinding update(
		SamlPeerBinding samlPeerBinding, ServiceContext serviceContext) {

		return getPersistence().update(samlPeerBinding, serviceContext);
	}

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		return getPersistence().findByC_D_SNIV(
			companyId, deleted, samlNameIdValue);
	}

	/**
	 * Returns a range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end) {

		return getPersistence().findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, start, end);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end, OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end, OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_D_SNIV(
			companyId, deleted, samlNameIdValue, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding findByC_D_SNIV_First(
			long companyId, boolean deleted, String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_D_SNIV_First(
			companyId, deleted, samlNameIdValue, orderByComparator);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding fetchByC_D_SNIV_First(
		long companyId, boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().fetchByC_D_SNIV_First(
			companyId, deleted, samlNameIdValue, orderByComparator);
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding findByC_D_SNIV_Last(
			long companyId, boolean deleted, String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_D_SNIV_Last(
			companyId, deleted, samlNameIdValue, orderByComparator);
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding fetchByC_D_SNIV_Last(
		long companyId, boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().fetchByC_D_SNIV_Last(
			companyId, deleted, samlNameIdValue, orderByComparator);
	}

	/**
	 * Returns the saml peer bindings before and after the current saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param samlPeerBindingId the primary key of the current saml peer binding
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding[] findByC_D_SNIV_PrevAndNext(
			long samlPeerBindingId, long companyId, boolean deleted,
			String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_D_SNIV_PrevAndNext(
			samlPeerBindingId, companyId, deleted, samlNameIdValue,
			orderByComparator);
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 */
	public static void removeByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		getPersistence().removeByC_D_SNIV(companyId, deleted, samlNameIdValue);
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the number of matching saml peer bindings
	 */
	public static int countByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		return getPersistence().countByC_D_SNIV(
			companyId, deleted, samlNameIdValue);
	}

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		return getPersistence().findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted);
	}

	/**
	 * Returns a range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end) {

		return getPersistence().findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, start, end);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	public static List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding findByC_U_SPEI_D_First(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_U_SPEI_D_First(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding fetchByC_U_SPEI_D_First(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().fetchByC_U_SPEI_D_First(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding findByC_U_SPEI_D_Last(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_U_SPEI_D_Last(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);
	}

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public static SamlPeerBinding fetchByC_U_SPEI_D_Last(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().fetchByC_U_SPEI_D_Last(
			companyId, userId, samlPeerEntityId, deleted, orderByComparator);
	}

	/**
	 * Returns the saml peer bindings before and after the current saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param samlPeerBindingId the primary key of the current saml peer binding
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding[] findByC_U_SPEI_D_PrevAndNext(
			long samlPeerBindingId, long companyId, long userId,
			String samlPeerEntityId, boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByC_U_SPEI_D_PrevAndNext(
			samlPeerBindingId, companyId, userId, samlPeerEntityId, deleted,
			orderByComparator);
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 */
	public static void removeByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		getPersistence().removeByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted);
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the number of matching saml peer bindings
	 */
	public static int countByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		return getPersistence().countByC_U_SPEI_D(
			companyId, userId, samlPeerEntityId, deleted);
	}

	/**
	 * Caches the saml peer binding in the entity cache if it is enabled.
	 *
	 * @param samlPeerBinding the saml peer binding
	 */
	public static void cacheResult(SamlPeerBinding samlPeerBinding) {
		getPersistence().cacheResult(samlPeerBinding);
	}

	/**
	 * Caches the saml peer bindings in the entity cache if it is enabled.
	 *
	 * @param samlPeerBindings the saml peer bindings
	 */
	public static void cacheResult(List<SamlPeerBinding> samlPeerBindings) {
		getPersistence().cacheResult(samlPeerBindings);
	}

	/**
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	public static SamlPeerBinding create(long samlPeerBindingId) {
		return getPersistence().create(samlPeerBindingId);
	}

	/**
	 * Removes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding remove(long samlPeerBindingId)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().remove(samlPeerBindingId);
	}

	public static SamlPeerBinding updateImpl(SamlPeerBinding samlPeerBinding) {
		return getPersistence().updateImpl(samlPeerBinding);
	}

	/**
	 * Returns the saml peer binding with the primary key or throws a <code>NoSuchPeerBindingException</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding findByPrimaryKey(long samlPeerBindingId)
		throws com.liferay.saml.persistence.exception.
			NoSuchPeerBindingException {

		return getPersistence().findByPrimaryKey(samlPeerBindingId);
	}

	/**
	 * Returns the saml peer binding with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding, or <code>null</code> if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding fetchByPrimaryKey(long samlPeerBindingId) {
		return getPersistence().fetchByPrimaryKey(samlPeerBindingId);
	}

	/**
	 * Returns all the saml peer bindings.
	 *
	 * @return the saml peer bindings
	 */
	public static List<SamlPeerBinding> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of saml peer bindings
	 */
	public static List<SamlPeerBinding> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saml peer bindings
	 */
	public static List<SamlPeerBinding> findAll(
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saml peer bindings
	 */
	public static List<SamlPeerBinding> findAll(
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the saml peer bindings from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of saml peer bindings.
	 *
	 * @return the number of saml peer bindings
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SamlPeerBindingPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(SamlPeerBindingPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile SamlPeerBindingPersistence _persistence;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.saml.persistence.exception.NoSuchPeerBindingException;
import com.liferay.saml.persistence.model.SamlPeerBinding;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the saml peer binding service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlPeerBindingUtil
 * @generated
 */
@ProviderType
public interface SamlPeerBindingPersistence
	extends BasePersistence<SamlPeerBinding> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SamlPeerBindingUtil} to access the saml peer binding persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the matching saml peer bindings
	 */
	public java.util.List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue);

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
	public java.util.List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end);

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
	public java.util.List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public java.util.List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator,
		boolean useFinderCache);

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
	public SamlPeerBinding findByC_D_SNIV_First(
			long companyId, boolean deleted, String samlNameIdValue,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public SamlPeerBinding fetchByC_D_SNIV_First(
		long companyId, boolean deleted, String samlNameIdValue,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public SamlPeerBinding findByC_D_SNIV_Last(
			long companyId, boolean deleted, String samlNameIdValue,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

	/**
	 * Returns the last saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	public SamlPeerBinding fetchByC_D_SNIV_Last(
		long companyId, boolean deleted, String samlNameIdValue,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public SamlPeerBinding[] findByC_D_SNIV_PrevAndNext(
			long samlPeerBindingId, long companyId, boolean deleted,
			String samlNameIdValue,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 */
	public void removeByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue);

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the number of matching saml peer bindings
	 */
	public int countByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue);

	/**
	 * Returns all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the matching saml peer bindings
	 */
	public java.util.List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted);

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
	public java.util.List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end);

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
	public java.util.List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public java.util.List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator,
		boolean useFinderCache);

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
	public SamlPeerBinding findByC_U_SPEI_D_First(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

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
	public SamlPeerBinding fetchByC_U_SPEI_D_First(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public SamlPeerBinding findByC_U_SPEI_D_Last(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

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
	public SamlPeerBinding fetchByC_U_SPEI_D_Last(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public SamlPeerBinding[] findByC_U_SPEI_D_PrevAndNext(
			long samlPeerBindingId, long companyId, long userId,
			String samlPeerEntityId, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
				orderByComparator)
		throws NoSuchPeerBindingException;

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 */
	public void removeByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted);

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the number of matching saml peer bindings
	 */
	public int countByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted);

	/**
	 * Caches the saml peer binding in the entity cache if it is enabled.
	 *
	 * @param samlPeerBinding the saml peer binding
	 */
	public void cacheResult(SamlPeerBinding samlPeerBinding);

	/**
	 * Caches the saml peer bindings in the entity cache if it is enabled.
	 *
	 * @param samlPeerBindings the saml peer bindings
	 */
	public void cacheResult(java.util.List<SamlPeerBinding> samlPeerBindings);

	/**
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	public SamlPeerBinding create(long samlPeerBindingId);

	/**
	 * Removes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public SamlPeerBinding remove(long samlPeerBindingId)
		throws NoSuchPeerBindingException;

	public SamlPeerBinding updateImpl(SamlPeerBinding samlPeerBinding);

	/**
	 * Returns the saml peer binding with the primary key or throws a <code>NoSuchPeerBindingException</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	public SamlPeerBinding findByPrimaryKey(long samlPeerBindingId)
		throws NoSuchPeerBindingException;

	/**
	 * Returns the saml peer binding with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding, or <code>null</code> if a saml peer binding with the primary key could not be found
	 */
	public SamlPeerBinding fetchByPrimaryKey(long samlPeerBindingId);

	/**
	 * Returns all the saml peer bindings.
	 *
	 * @return the saml peer bindings
	 */
	public java.util.List<SamlPeerBinding> findAll();

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
	public java.util.List<SamlPeerBinding> findAll(int start, int end);

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
	public java.util.List<SamlPeerBinding> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator);

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
	public java.util.List<SamlPeerBinding> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlPeerBinding>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the saml peer bindings from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of saml peer bindings.
	 *
	 * @return the number of saml peer bindings
	 */
	public int countAll();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommercePaymentMethodGroupRelQualifier. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentMethodGroupRelQualifierLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommercePaymentMethodGroupRelQualifierLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentMethodGroupRelQualifierLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce payment method group rel qualifier local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePaymentMethodGroupRelQualifierLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce payment method group rel qualifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentMethodGroupRelQualifier
		addCommercePaymentMethodGroupRelQualifier(
			CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier);

	public CommercePaymentMethodGroupRelQualifier
			addCommercePaymentMethodGroupRelQualifier(
				long userId, String className, long classPK,
				long commercePaymentMethodGroupRelId)
		throws PortalException;

	/**
	 * Creates a new commerce payment method group rel qualifier with the primary key. Does not add the commerce payment method group rel qualifier to the database.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key for the new commerce payment method group rel qualifier
	 * @return the new commerce payment method group rel qualifier
	 */
	@Transactional(enabled = false)
	public CommercePaymentMethodGroupRelQualifier
		createCommercePaymentMethodGroupRelQualifier(
			long commercePaymentMethodGroupRelQualifierId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce payment method group rel qualifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				CommercePaymentMethodGroupRelQualifier
					commercePaymentMethodGroupRelQualifier)
		throws PortalException;

	/**
	 * Deletes the commerce payment method group rel qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException;

	public void deleteCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId)
		throws PortalException;

	public void deleteCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentMethodGroupRelQualifier
		fetchCommercePaymentMethodGroupRelQualifier(
			long commercePaymentMethodGroupRelQualifierId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords);

	/**
	 * Returns the commerce payment method group rel qualifier with the primary key.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier
	 * @throws PortalException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentMethodGroupRelQualifier
			getCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce payment method group rel qualifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment method group rel qualifiers
	 * @param end the upper bound of the range of commerce payment method group rel qualifiers (not inclusive)
	 * @return the range of commerce payment method group rel qualifiers
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommercePaymentMethodGroupRelQualifiers(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId);

	/**
	 * Returns the number of commerce payment method group rel qualifiers.
	 *
	 * @return the number of commerce payment method group rel qualifiers
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePaymentMethodGroupRelQualifiersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Updates the commerce payment method group rel qualifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentMethodGroupRelQualifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelQualifier the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePaymentMethodGroupRelQualifier
		updateCommercePaymentMethodGroupRelQualifier(
			CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1993757690
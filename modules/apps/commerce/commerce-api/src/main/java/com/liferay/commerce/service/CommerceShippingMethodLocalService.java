/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.model.CommerceShippingMethod;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceShippingMethod. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethodLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceShippingMethodLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShippingMethodLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce shipping method local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceShippingMethodLocalServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long userId, long groupId, long commerceShippingMethodId,
			long countryId)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public CommerceAddressRestriction addCommerceAddressRestriction(
			long commerceShippingMethodId, long countryId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the commerce shipping method to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceShippingMethod addCommerceShippingMethod(
		CommerceShippingMethod commerceShippingMethod);

	public CommerceShippingMethod addCommerceShippingMethod(
			long userId, long groupId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			String engineKey, File imageFile, double priority,
			String trackingURL)
		throws PortalException;

	/**
	 * Creates a new commerce shipping method with the primary key. Does not add the commerce shipping method to the database.
	 *
	 * @param commerceShippingMethodId the primary key for the new commerce shipping method
	 * @return the new commerce shipping method
	 */
	@Transactional(enabled = false)
	public CommerceShippingMethod createCommerceShippingMethod(
		long commerceShippingMethodId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCommerceAddressRestriction(
			long commerceAddressRestrictionId)
		throws PortalException;

	/**
	 * Deletes the commerce shipping method from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceShippingMethod deleteCommerceShippingMethod(
			CommerceShippingMethod commerceShippingMethod)
		throws PortalException;

	/**
	 * Deletes the commerce shipping method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws PortalException if a commerce shipping method with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceShippingMethod deleteCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException;

	public void deleteCommerceShippingMethods(long groupId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
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
	public CommerceShippingMethod fetchCommerceShippingMethod(
		long commerceShippingMethodId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShippingMethod fetchCommerceShippingMethod(
		long groupId, String engineKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShippingMethod
		fetchCommerceShippingMethodByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddressRestriction> getCommerceAddressRestrictions(
		long commerceShippingMethodId, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceAddressRestrictionsCount(
		long commerceShippingMethodId);

	/**
	 * Returns the commerce shipping method with the primary key.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method
	 * @throws PortalException if a commerce shipping method with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShippingMethod getCommerceShippingMethod(
			long commerceShippingMethodId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShippingMethod
			getCommerceShippingMethodByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce shipping methods.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of commerce shipping methods
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShippingMethod> getCommerceShippingMethods(
		long groupId, long countryId, boolean active);

	/**
	 * Returns the number of commerce shipping methods.
	 *
	 * @return the number of commerce shipping methods
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShippingMethodsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShippingMethodsCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShippingMethodsCount(long groupId, boolean active);

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

	public CommerceShippingMethod setActive(
			long commerceShippingMethodId, boolean active)
		throws PortalException;

	/**
	 * Updates the commerce shipping method in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingMethodLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingMethod the commerce shipping method
	 * @return the commerce shipping method that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceShippingMethod updateCommerceShippingMethod(
		CommerceShippingMethod commerceShippingMethod);

	public CommerceShippingMethod updateCommerceShippingMethod(
			long commerceShippingMethodId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, File imageFile,
			double priority, String trackingURL)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:2049756569
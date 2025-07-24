/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
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
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceDiscountRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CommerceDiscountRelLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceDiscountRelLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.discount.service.impl.CommerceDiscountRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce discount rel local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceDiscountRelLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce discount rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscountRel addCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel);

	public CommerceDiscountRel addCommerceDiscountRel(
			long commerceDiscountId, String className, long classPK,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	@Transactional(enabled = false)
	public CommerceDiscountRel createCommerceDiscountRel(
		long commerceDiscountRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceDiscountRel deleteCommerceDiscountRel(
			CommerceDiscount commerceDiscount,
			CommerceDiscountRel commerceDiscountRel)
		throws PortalException;

	/**
	 * Deletes the commerce discount rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceDiscountRel deleteCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel);

	/**
	 * Deletes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceDiscountRel deleteCommerceDiscountRel(
			long commerceDiscountRelId)
		throws PortalException;

	public void deleteCommerceDiscountRels(CommerceDiscount commerceDiscount)
		throws PortalException;

	public void deleteCommerceDiscountRels(String className, long classPK)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
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
	public CommerceDiscountRel fetchCommerceDiscountRel(
		long commerceDiscountRelId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountRel fetchCommerceDiscountRel(
		long commerceDiscountId, String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountRel fetchCommerceDiscountRel(
		String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCategoriesByCommerceDiscountId(
		long commerceDiscountId, String name, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCategoriesByCommerceDiscountIdCount(
		long commerceDiscountId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getClassPKs(long commerceDiscountId, String className);

	/**
	 * Returns the commerce discount rel with the primary key.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws PortalException if a commerce discount rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceDiscountRel getCommerceDiscountRel(
			long commerceDiscountRelId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of commerce discount rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCommerceDiscountRels(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCommerceDiscountRels(
		long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCommerceDiscountRels(
		long classNameId, long classPK, String unitOfMeasureKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCommerceDiscountRels(
		long commerceDiscountId, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCommerceDiscountRels(
		long commerceDiscountId, String className, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator);

	/**
	 * Returns the number of commerce discount rels.
	 *
	 * @return the number of commerce discount rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountRelsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceDiscountRelsCount(
		long commerceDiscountId, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel>
		getCommercePricingClassesByCommerceDiscountId(
			long commerceDiscountId, String title, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePricingClassesByCommerceDiscountIdCount(
		long commerceDiscountId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCPDefinitionsByCommerceDiscountId(
		long commerceDiscountId, String name, String languageId, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionsByCommerceDiscountIdCount(
		long commerceDiscountId, String name, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceDiscountRel> getCPInstancesByCommerceDiscountId(
		long commerceDiscountId, String sku, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPInstancesByCommerceDiscountIdCount(
		long commerceDiscountId, String sku);

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
	 * Updates the commerce discount rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 * @return the commerce discount rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceDiscountRel updateCommerceDiscountRel(
		CommerceDiscountRel commerceDiscountRel);

}
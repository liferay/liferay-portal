/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
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
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CommerceChannelAccountEntryRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CommerceChannelAccountEntryRelLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceChannelAccountEntryRelLocalService
	extends BaseLocalService, CTService<CommerceChannelAccountEntryRel>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceChannelAccountEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce channel account entry rel local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceChannelAccountEntryRelLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce channel account entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel);

	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
			long userId, long accountEntryId, String className, long classPK,
			long commerceChannelId, boolean overrideEligibility,
			double priority, int type)
		throws PortalException;

	/**
	 * Creates a new commerce channel account entry rel with the primary key. Does not add the commerce channel account entry rel to the database.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key for the new commerce channel account entry rel
	 * @return the new commerce channel account entry rel
	 */
	@Transactional(enabled = false)
	public CommerceChannelAccountEntryRel createCommerceChannelAccountEntryRel(
		long commerceChannelAccountEntryRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce channel account entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel)
		throws PortalException;

	/**
	 * Deletes the commerce channel account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException;

	public void deleteCommerceChannelAccountEntryRels(
			String className, long classPK)
		throws PortalException;

	public void deleteCommerceChannelAccountEntryRelsByAccountEntryId(
			long accountEntryId)
		throws PortalException;

	public void deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
			long commerceChannelId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
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
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long commerceChannelAccountEntryRelId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, long commerceChannelId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, String className, long classPK,
		long commerceChannelId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce channel account entry rel with the primary key.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel
	 * @throws PortalException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceChannelAccountEntryRel getCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce channel account entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @return the range of commerce channel account entry rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long accountEntryId, int type, int start, int end,
			OrderByComparator<CommerceChannelAccountEntryRel>
				orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long commerceChannelId, String name, int type, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			String className, long classPK, long commerceChannelId, int type);

	/**
	 * Returns the number of commerce channel account entry rels.
	 *
	 * @return the number of commerce channel account entry rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceChannelAccountEntryRelsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceChannelAccountEntryRelsCount(
		long accountEntryId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceChannelAccountEntryRelsCount(
		long commerceChannelId, String name, int type);

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
	 * Updates the commerce channel account entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelAccountEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelAccountEntryRel the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel);

	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId, long commerceChannelId,
			long classPK, boolean overrideEligibility, double priority)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CommerceChannelAccountEntryRel> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CommerceChannelAccountEntryRel> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommerceChannelAccountEntryRel>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:713604907
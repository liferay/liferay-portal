/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
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
 * Provides the local service interface for CommercePriceListAccountRel. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListAccountRelLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommercePriceListAccountRelLocalService
	extends BaseLocalService, CTService<CommercePriceListAccountRel>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListAccountRelLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce price list account rel local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePriceListAccountRelLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the commerce price list account rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePriceListAccountRel addCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel);

	public CommercePriceListAccountRel addCommercePriceListAccountRel(
			long userId, long commercePriceListId, long commerceAccountId,
			int order, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new commerce price list account rel with the primary key. Does not add the commerce price list account rel to the database.
	 *
	 * @param commercePriceListAccountRelId the primary key for the new commerce price list account rel
	 * @return the new commerce price list account rel
	 */
	@Transactional(enabled = false)
	public CommercePriceListAccountRel createCommercePriceListAccountRel(
		long commercePriceListAccountRelId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the commerce price list account rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			CommercePriceListAccountRel commercePriceListAccountRel)
		throws PortalException;

	/**
	 * Deletes the commerce price list account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException;

	public void deleteCommercePriceListAccountRels(long commercePriceListId)
		throws PortalException;

	public void deleteCommercePriceListAccountRelsByCommercePriceListId(
			long commercePriceListId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commercePriceListAccountRelId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commerceAccountId, long commercePriceListId);

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceListAccountRel
		fetchCommercePriceListAccountRelByUuidAndCompanyId(
			String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the commerce price list account rel with the primary key.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceListAccountRel getCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException;

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel
	 * @throws PortalException if a matching commerce price list account rel could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceListAccountRel
			getCommercePriceListAccountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the commerce price list account rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @return the range of commerce price list account rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId, String name, int start, int end);

	/**
	 * Returns the number of commerce price list account rels.
	 *
	 * @return the number of commerce price list account rels
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePriceListAccountRelsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePriceListAccountRelsCount(long commercePriceListId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

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
	 * Updates the commerce price list account rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CommercePriceListAccountRel updateCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CommercePriceListAccountRel> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CommercePriceListAccountRel> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceListAccountRel>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1211441900
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPSpecificationOption;
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
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CPSpecificationOption. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CPSpecificationOptionLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CPSpecificationOptionLocalService
	extends BaseLocalService, CTService<CPSpecificationOption>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPSpecificationOptionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp specification option local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPSpecificationOptionLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the cp specification option to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPSpecificationOption addCPSpecificationOption(
		CPSpecificationOption cpSpecificationOption);

	@Indexable(type = IndexableType.REINDEX)
	public CPSpecificationOption addCPSpecificationOption(
			String externalReferenceCode, long userId, long cpOptionCategoryId,
			long[] listTypeDefinitionIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, boolean facetable, String key,
			double priority, boolean visible, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new cp specification option with the primary key. Does not add the cp specification option to the database.
	 *
	 * @param CPSpecificationOptionId the primary key for the new cp specification option
	 * @return the new cp specification option
	 */
	@Transactional(enabled = false)
	public CPSpecificationOption createCPSpecificationOption(
		long CPSpecificationOptionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the cp specification option from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPSpecificationOption deleteCPSpecificationOption(
			CPSpecificationOption cpSpecificationOption)
		throws PortalException;

	/**
	 * Deletes the cp specification option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option that was removed
	 * @throws PortalException if a cp specification option with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CPSpecificationOption deleteCPSpecificationOption(
			long CPSpecificationOptionId)
		throws PortalException;

	public void deleteCPSpecificationOptions(long companyId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
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
	public CPSpecificationOption fetchCPSpecificationOption(
		long CPSpecificationOptionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption fetchCPSpecificationOption(
		long companyId, String key);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption
		fetchCPSpecificationOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the cp specification option with the matching UUID and company.
	 *
	 * @param uuid the cp specification option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp specification option, or <code>null</code> if a matching cp specification option could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption fetchCPSpecificationOptionByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the cp specification option with the primary key.
	 *
	 * @param CPSpecificationOptionId the primary key of the cp specification option
	 * @return the cp specification option
	 * @throws PortalException if a cp specification option with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption getCPSpecificationOption(
			long CPSpecificationOptionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption getCPSpecificationOption(
			long companyId, String key)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption
			getCPSpecificationOptionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the cp specification option with the matching UUID and company.
	 *
	 * @param uuid the cp specification option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp specification option
	 * @throws PortalException if a matching cp specification option could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPSpecificationOption getCPSpecificationOptionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the cp specification options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification options
	 * @param end the upper bound of the range of cp specification options (not inclusive)
	 * @return the range of cp specification options
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPSpecificationOption> getCPSpecificationOptions(
		int start, int end);

	/**
	 * Returns the number of cp specification options.
	 *
	 * @return the number of cp specification options
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPSpecificationOptionsCount();

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CPSpecificationOption>
			searchCPSpecificationOptions(
				long companyId, Boolean facetable, Boolean visible,
				String keywords, int start, int end, Sort sort)
		throws PortalException;

	public CPSpecificationOption updateCPOptionCategoryId(
			long cpSpecificationOptionId, long cpOptionCategoryId)
		throws PortalException;

	/**
	 * Updates the cp specification option in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOption the cp specification option
	 * @return the cp specification option that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPSpecificationOption updateCPSpecificationOption(
		CPSpecificationOption cpSpecificationOption);

	@Indexable(type = IndexableType.REINDEX)
	public CPSpecificationOption updateCPSpecificationOption(
			String externalReferenceCode, long cpSpecificationOptionId,
			long cpOptionCategoryId, long[] listTypeDefinitionIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			boolean facetable, String key, double priority, boolean visible,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CPSpecificationOption> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CPSpecificationOption> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPSpecificationOption>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1514021608
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
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
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CPDefinitionSpecificationOptionValue. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see CPDefinitionSpecificationOptionValueLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CPDefinitionSpecificationOptionValueLocalService
	extends BaseLocalService, CTService<CPDefinitionSpecificationOptionValue>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionSpecificationOptionValueLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the cp definition specification option value local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CPDefinitionSpecificationOptionValueLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the cp definition specification option value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPDefinitionSpecificationOptionValue
		addCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue);

	public CPDefinitionSpecificationOptionValue
			addCPDefinitionSpecificationOptionValue(
				String externalReferenceCode, long cpDefinitionId,
				long cpSpecificationOptionId, long cpOptionCategoryId,
				double priority, Map<Locale, String> valueMap, boolean visible,
				ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new cp definition specification option value with the primary key. Does not add the cp definition specification option value to the database.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key for the new cp definition specification option value
	 * @return the new cp definition specification option value
	 */
	@Transactional(enabled = false)
	public CPDefinitionSpecificationOptionValue
		createCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the cp definition specification option value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue)
		throws PortalException;

	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue,
				boolean makeCopy)
		throws PortalException;

	/**
	 * Deletes the cp definition specification option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws PortalException;

	public void deleteCPDefinitionSpecificationOptionValues(long cpDefinitionId)
		throws PortalException;

	public void deleteCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, boolean makeCopy)
		throws PortalException;

	public void deleteCPSpecificationOptionDefinitionValues(
			long cpSpecificationOptionId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, long cpDefinitionSpecificationOptionValueId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, String key);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByUuidAndGroupId(
			String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the cp definition specification option value with the primary key.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value
	 * @throws PortalException if a matching cp definition specification option value could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the cp definition specification option values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of cp definition specification option values
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(long cpSpecificationOptionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, Boolean visible, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpSpecificationOptionId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, long cpOptionCategoryId, Boolean visible);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByC_CSO(
			long cpDefinitionId, long cpSpecificationOptionId);

	/**
	 * Returns all the cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @return the matching cp definition specification option values, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition specification option values, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator);

	/**
	 * Returns the number of cp definition specification option values.
	 *
	 * @return the number of cp definition specification option values
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionSpecificationOptionValuesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCPDefinitionSpecificationOptionValuesCount(
		long cpDefinitionId, Boolean visible);

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
	 * Updates the cp definition specification option value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CPDefinitionSpecificationOptionValue
		updateCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue);

	public CPDefinitionSpecificationOptionValue
			updateCPDefinitionSpecificationOptionValue(
				String externalReferenceCode,
				long cpDefinitionSpecificationOptionValueId,
				long cpOptionCategoryId, String key, double priority,
				Map<Locale, String> valueMap, boolean visible,
				ServiceContext serviceContext)
		throws PortalException;

	public CPDefinitionSpecificationOptionValue updateCPOptionCategoryId(
			long cpDefinitionSpecificationOptionValueId,
			long cpOptionCategoryId)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CPDefinitionSpecificationOptionValue>
		getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CPDefinitionSpecificationOptionValue> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<CPDefinitionSpecificationOptionValue>, R, E>
					updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:932945492
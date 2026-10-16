/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

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
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.ExceptionRetryAcceptor;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KaleoInstance. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KaleoInstanceLocalService
	extends BaseLocalService, CTService<KaleoInstance>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kaleo instance local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KaleoInstanceLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the kaleo instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance addKaleoInstance(KaleoInstance kaleoInstance);

	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance addKaleoInstance(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException;

	public KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance completeKaleoInstance(long kaleoInstanceId)
		throws PortalException;

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	@Transactional(enabled = false)
	public KaleoInstance createKaleoInstance(long kaleoInstanceId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCompanyKaleoInstances(long companyId);

	public void deleteKaleoDefinitionVersionKaleoInstances(
		long kaleoDefinitionVersionId);

	/**
	 * Deletes the kaleo instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoInstance deleteKaleoInstance(KaleoInstance kaleoInstance);

	/**
	 * Deletes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoInstance deleteKaleoInstance(long kaleoInstanceId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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
	public KaleoInstance fetchKaleoInstance(long kaleoInstanceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoInstance fetchKaleoInstance(
		long kaleoInstanceId, long companyId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionKaleoInstancesCount(
		long kaleoDefinitionId, boolean completed);

	/**
	 * Returns the kaleo instance with the primary key.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws PortalException;

	/**
	 * Returns a range of all the kaleo instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of kaleo instances
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoInstance> getKaleoInstances(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoInstance> getKaleoInstances(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, int start, int end,
			OrderByComparator<KaleoInstance> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoInstance> getKaleoInstances(
			Long userId, String[] assetClassNames, Boolean completed, int start,
			int end, OrderByComparator<KaleoInstance> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoInstance> getKaleoInstances(
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			boolean completed, int start, int end,
			OrderByComparator<KaleoInstance> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the number of kaleo instances.
	 *
	 * @return the number of kaleo instances
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoInstancesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoInstancesCount(
		long kaleoDefinitionVersionId, boolean completed);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoInstancesCount(
		Long userId, String assetClassName, Long assetClassPK,
		Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoInstancesCount(
		Long userId, String[] assetClassNames, Boolean completed,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoInstancesCount(
		String kaleoDefinitionName, int kaleoDefinitionVersion,
		boolean completed, ServiceContext serviceContext);

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
	public List<KaleoInstance> search(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<KaleoInstance> searchKaleoInstances(
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed,
			boolean searchByActiveWorkflowHandlers, int start, int end,
			OrderByComparator<KaleoInstance> orderByComparator,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance updateActive(
			long userId, long kaleoInstanceId, boolean active)
		throws PortalException;

	/**
	 * Updates the kaleo instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance updateKaleoInstance(KaleoInstance kaleoInstance);

	@Indexable(type = IndexableType.REINDEX)
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, long rootKaleoInstanceTokenId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	@Retry(
		acceptor = ExceptionRetryAcceptor.class,
		properties = {
			@Property(
				name = ExceptionRetryAcceptor.EXCEPTION_NAME,
				value = "org.hibernate.StaleStateException"
			)
		}
	)
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<KaleoInstance> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<KaleoInstance> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoInstance>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2068388726
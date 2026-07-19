/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KaleoDefinition. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KaleoDefinitionLocalService
	extends BaseLocalService, CTService<KaleoDefinition>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kaleo definition local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KaleoDefinitionLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			long startKaleoNodeId, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition activateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the kaleo definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition addKaleoDefinition(KaleoDefinition kaleoDefinition);

	public KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, int version,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new kaleo definition with the primary key. Does not add the kaleo definition to the database.
	 *
	 * @param kaleoDefinitionId the primary key for the new kaleo definition
	 * @return the new kaleo definition
	 */
	@Transactional(enabled = false)
	public KaleoDefinition createKaleoDefinition(long kaleoDefinitionId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition deactivateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException;

	public void deleteCompanyKaleoDefinitions(long companyId);

	/**
	 * Deletes the kaleo definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoDefinition deleteKaleoDefinition(
		KaleoDefinition kaleoDefinition);

	/**
	 * Deletes the kaleo definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition that was removed
	 * @throws PortalException if a kaleo definition with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoDefinition deleteKaleoDefinition(long kaleoDefinitionId)
		throws PortalException;

	public void deleteKaleoDefinition(
			String name, ServiceContext serviceContext)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
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
	public KaleoDefinition fetchKaleoDefinition(long kaleoDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition fetchKaleoDefinition(
		String name, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition fetchKaleoDefinitionByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the kaleo definition matching the UUID and group.
	 *
	 * @param uuid the kaleo definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition fetchKaleoDefinitionByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the kaleo definition with the primary key.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition
	 * @throws PortalException if a kaleo definition with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition getKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition getKaleoDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the kaleo definition matching the UUID and group.
	 *
	 * @param uuid the kaleo definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo definition
	 * @throws PortalException if a matching kaleo definition could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoDefinition getKaleoDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext);

	/**
	 * Returns a range of all the kaleo definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @return the range of kaleo definitions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitions(
		int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext);

	/**
	 * Returns all the kaleo definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo definitions
	 * @param companyId the primary key of the company
	 * @return the matching kaleo definitions, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitionsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of kaleo definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo definitions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kaleo definitions, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getKaleoDefinitionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator);

	/**
	 * Returns the number of kaleo definitions.
	 *
	 * @return the number of kaleo definitions
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionsCount(
		boolean active, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionsCount(ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionsCount(
		String name, boolean active, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoDefinitionsCount(
		String name, ServiceContext serviceContext);

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
	public List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getScopeKaleoDefinitionsCount(
		String scope, boolean active, ServiceContext serviceContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getScopeKaleoDefinitionsCount(
		String scope, ServiceContext serviceContext);

	public KaleoDefinition updatedKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the kaleo definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoDefinition updateKaleoDefinition(
		KaleoDefinition kaleoDefinition);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<KaleoDefinition> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<KaleoDefinition> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoDefinition>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:351544488
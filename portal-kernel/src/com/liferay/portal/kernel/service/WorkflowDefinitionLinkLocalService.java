/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

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
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for WorkflowDefinitionLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.kernel.model.WorkflowDefinitionLink"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface WorkflowDefinitionLinkLocalService
	extends BaseLocalService, CTService<WorkflowDefinitionLink>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.WorkflowDefinitionLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the workflow definition link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link WorkflowDefinitionLinkLocalServiceUtil} if injection and service tracking are not available.
	 */
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException;

	/**
	 * Adds the workflow definition link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 * @return the workflow definition link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
		WorkflowDefinitionLink workflowDefinitionLink);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new workflow definition link with the primary key. Does not add the workflow definition link to the database.
	 *
	 * @param workflowDefinitionLinkId the primary key for the new workflow definition link
	 * @return the new workflow definition link
	 */
	@Transactional(enabled = false)
	public WorkflowDefinitionLink createWorkflowDefinitionLink(
		long workflowDefinitionLinkId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws PortalException if a workflow definition link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public WorkflowDefinitionLink deleteWorkflowDefinitionLink(
			long workflowDefinitionLinkId)
		throws PortalException;

	public void deleteWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK);

	/**
	 * Deletes the workflow definition link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 * @return the workflow definition link that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public WorkflowDefinitionLink deleteWorkflowDefinitionLink(
		WorkflowDefinitionLink workflowDefinitionLink);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowDefinitionLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowDefinitionLinkModelImpl</code>.
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
	public WorkflowDefinitionLink fetchDefaultWorkflowDefinitionLink(
		long companyId, String className, long classPK, long typePK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink fetchWorkflowDefinitionLink(
		long workflowDefinitionLinkId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink fetchWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink fetchWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK, boolean strict);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink
		fetchWorkflowDefinitionLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId);

	/**
	 * Returns the workflow definition link matching the UUID and group.
	 *
	 * @param uuid the workflow definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink fetchWorkflowDefinitionLinkByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> fetchWorkflowDefinitionLinks(
		long companyId, long groupId, String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink getDefaultWorkflowDefinitionLink(
			long companyId, String className, long classPK, long typePK)
		throws PortalException;

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
	 * Returns the workflow definition link with the primary key.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws PortalException if a workflow definition link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long workflowDefinitionLinkId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, String className, long classPK,
			long typePK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, String className, long classPK,
			long typePK, boolean strict)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink
			getWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the workflow definition link matching the UUID and group.
	 *
	 * @param uuid the workflow definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching workflow definition link
	 * @throws PortalException if a matching workflow definition link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink getWorkflowDefinitionLinkByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of workflow definition links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, long groupId, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, long groupId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException;

	/**
	 * Returns all the workflow definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the workflow definition links
	 * @param companyId the primary key of the company
	 * @return the matching workflow definition links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink>
		getWorkflowDefinitionLinksByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of workflow definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the workflow definition links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching workflow definition links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink>
		getWorkflowDefinitionLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator);

	/**
	 * Returns the number of workflow definition links.
	 *
	 * @return the number of workflow definition links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getWorkflowDefinitionLinksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getWorkflowDefinitionLinksCount(
		long companyId, long groupId, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getWorkflowDefinitionLinksCount(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion);

	@Transactional(enabled = false)
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasWorkflowDefinitionLink(
		long companyId, long groupId, String className, long classPK,
		long typePK);

	public void updateWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinition)
		throws PortalException;

	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException;

	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException;

	/**
	 * Updates the workflow definition link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WorkflowDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 * @return the workflow definition link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
		WorkflowDefinitionLink workflowDefinitionLink);

	public void updateWorkflowDefinitionLinks(
			long userId, long companyId, long groupId, String className,
			long classPK,
			List<ObjectValuePair<Long, String>> workflowDefinitionOVPs)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<WorkflowDefinitionLink> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<WorkflowDefinitionLink> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WorkflowDefinitionLink>, R, E>
				updateUnsafeFunction)
		throws E;

}
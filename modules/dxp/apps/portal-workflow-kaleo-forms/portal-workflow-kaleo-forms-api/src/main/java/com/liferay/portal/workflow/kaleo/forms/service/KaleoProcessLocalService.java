/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
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
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KaleoProcess. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KaleoProcessLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kaleo process local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KaleoProcessLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the kaleo process to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoProcess addKaleoProcess(KaleoProcess kaleoProcess);

	/**
	 * Adds a Kaleo process.
	 *
	 * @param userId the primary key of the Kaleo process's creator/owner
	 * @param groupId the primary key of the Kaleo process's group
	 * @param ddmStructureId the primary key of the Kaleo process's DDM
	 structure
	 * @param nameMap the Kaleo process's locales and localized names
	 * @param descriptionMap the Kaleo process's locales and localized
	 descriptions
	 * @param ddmTemplateId the primary key of the Kaleo process's DDM template
	 * @param workflowDefinitionName the Kaleo process's workflow definition
	 name
	 * @param workflowDefinitionVersion the Kaleo process's workflow definition
	 version
	 * @param kaleoTaskFormPairs the Kaleo task form pairs used to create a
	 Kaleo process link. See <code>KaleoTaskFormPairs</code> in the
	 <code>portal.workflow.kaleo.forms.api</code> module.
	 * @param serviceContext the service context to be applied. This can set
	 guest permissions and group permissions for the Kaleo process.
	 * @return the Kaleo process
	 * @throws PortalException if a portal exception occurred
	 */
	public KaleoProcess addKaleoProcess(
			long userId, long groupId, long ddmStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			long ddmTemplateId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			KaleoTaskFormPairs kaleoTaskFormPairs,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new kaleo process with the primary key. Does not add the kaleo process to the database.
	 *
	 * @param kaleoProcessId the primary key for the new kaleo process
	 * @return the new kaleo process
	 */
	@Transactional(enabled = false)
	public KaleoProcess createKaleoProcess(long kaleoProcessId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the kaleo process from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public KaleoProcess deleteKaleoProcess(KaleoProcess kaleoProcess)
		throws PortalException;

	/**
	 * Deletes the kaleo process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process that was removed
	 * @throws PortalException if a kaleo process with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoProcess deleteKaleoProcess(long kaleoProcessId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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
	public KaleoProcess fetchKaleoProcess(long kaleoProcessId);

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcess fetchKaleoProcessByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the Kaleo process matching the DDL record set ID.
	 *
	 * @param ddlRecordSetId the primary key of the DDL record set associated
	 with the Kaleo process
	 * @return the Kaleo process
	 * @throws PortalException if a matching Kaleo process could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcess getDDLRecordSetKaleoProcess(long ddlRecordSetId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the kaleo process with the primary key.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws PortalException if a kaleo process with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcess getKaleoProcess(long kaleoProcessId)
		throws PortalException;

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process
	 * @throws PortalException if a matching kaleo process could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcess getKaleoProcessByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the kaleo processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of kaleo processes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoProcess> getKaleoProcesses(int start, int end);

	/**
	 * Returns all the kaleo processes matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo processes
	 * @param companyId the primary key of the company
	 * @return the matching kaleo processes, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoProcess> getKaleoProcessesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of kaleo processes matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo processes
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kaleo processes, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoProcess> getKaleoProcessesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator);

	/**
	 * Returns the number of kaleo processes.
	 *
	 * @return the number of kaleo processes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoProcessesCount();

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
	 * Updates the kaleo process in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoProcess updateKaleoProcess(KaleoProcess kaleoProcess);

	/**
	 * Updates the Kaleo process.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process
	 * @param ddmStructureId the primary key of the Kaleo process's DDM
	 structure
	 * @param nameMap the Kaleo process's locales and localized names
	 * @param descriptionMap the Kaleo process's locales and localized
	 descriptions
	 * @param ddmTemplateId the primary key of the Kaleo process's DDM template
	 * @param workflowDefinitionName the Kaleo process's workflow definition
	 name
	 * @param workflowDefinitionVersion the Kaleo process's workflow definition
	 version
	 * @param kaleoTaskFormPairs the Kaleo task form pairs. For more
	 information, see the <code>portal.workflow.kaleo.forms.api</code>
	 module's <code>KaleoTaskFormPairs</code> class.
	 * @param serviceContext the service context to be applied. This can set
	 guest permissions and group permissions for the Kaleo process.
	 * @return the Kaleo process
	 * @throws PortalException if a portal exception occurred
	 */
	public KaleoProcess updateKaleoProcess(
			long kaleoProcessId, long ddmStructureId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			long ddmTemplateId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			KaleoTaskFormPairs kaleoTaskFormPairs,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1156921652
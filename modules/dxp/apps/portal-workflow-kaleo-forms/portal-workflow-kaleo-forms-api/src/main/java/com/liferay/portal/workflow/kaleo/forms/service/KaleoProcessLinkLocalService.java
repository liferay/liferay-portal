/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KaleoProcessLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLinkLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KaleoProcessLinkLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kaleo process link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KaleoProcessLinkLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the kaleo process link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessLink the kaleo process link
	 * @return the kaleo process link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoProcessLink addKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink);

	/**
	 * Adds a Kaleo process link referencing the Kaleo process.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process
	 * @param workflowTaskName the name of the Kaleo process link's workflow
	 task
	 * @param ddmTemplateId the primary key of the Kaleo process link's DDM
	 template
	 * @return the Kaleo process link
	 */
	public KaleoProcessLink addKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId);

	/**
	 * Creates a new kaleo process link with the primary key. Does not add the kaleo process link to the database.
	 *
	 * @param kaleoProcessLinkId the primary key for the new kaleo process link
	 * @return the new kaleo process link
	 */
	@Transactional(enabled = false)
	public KaleoProcessLink createKaleoProcessLink(long kaleoProcessLinkId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the kaleo process link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessLink the kaleo process link
	 * @return the kaleo process link that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoProcessLink deleteKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink);

	/**
	 * Deletes the kaleo process link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link that was removed
	 * @throws PortalException if a kaleo process link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public KaleoProcessLink deleteKaleoProcessLink(long kaleoProcessLinkId)
		throws PortalException;

	/**
	 * Deletes the Kaleo process links associated with the Kaleo process and
	 * their resources.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process from which to
	 delete Kaleo process links
	 */
	public void deleteKaleoProcessLinks(long kaleoProcessId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessLinkModelImpl</code>.
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
	public KaleoProcessLink fetchKaleoProcessLink(long kaleoProcessLinkId);

	/**
	 * Returns the Kaleo process link matching the Kaleo process and workflow
	 * task name.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 process
	 * @param workflowTaskName the name of the Kaleo process link's workflow
	 task
	 * @return the Kaleo process link matching the Kaleo process and workflow
	 task name, or <code>null</code> if a matching Kaleo process link
	 could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcessLink fetchKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the kaleo process link with the primary key.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link
	 * @throws PortalException if a kaleo process link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KaleoProcessLink getKaleoProcessLink(long kaleoProcessLinkId)
		throws PortalException;

	/**
	 * Returns a range of all the kaleo process links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo process links
	 * @param end the upper bound of the range of kaleo process links (not inclusive)
	 * @return the range of kaleo process links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoProcessLink> getKaleoProcessLinks(int start, int end);

	/**
	 * Returns the Kaleo process links matching the Kaleo process.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 process
	 * @return the Kaleo process links matching the Kaleo process, or
	 <code>null</code> if a matching Kaleo process link could not be
	 found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KaleoProcessLink> getKaleoProcessLinks(long kaleoProcessId);

	/**
	 * Returns the number of kaleo process links.
	 *
	 * @return the number of kaleo process links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKaleoProcessLinksCount();

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
	 * Updates the kaleo process link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessLink the kaleo process link
	 * @return the kaleo process link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public KaleoProcessLink updateKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink);

	/**
	 * Updates the Kaleo process link. If no Kaleo process link is found
	 * matching the primary key of the Kaleo process and the workflow task name,
	 * a new link is created.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 process
	 * @param workflowTaskName the name of the Kaleo process link's workflow
	 task
	 * @param ddmTemplateId the primary key of the Kaleo process link's DDM
	 template
	 * @return the Kaleo process link
	 */
	public KaleoProcessLink updateKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1010492236
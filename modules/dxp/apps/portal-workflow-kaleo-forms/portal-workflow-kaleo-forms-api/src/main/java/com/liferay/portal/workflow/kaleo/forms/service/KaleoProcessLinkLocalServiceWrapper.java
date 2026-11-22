/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link KaleoProcessLinkLocalService}.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLinkLocalService
 * @generated
 */
public class KaleoProcessLinkLocalServiceWrapper
	implements KaleoProcessLinkLocalService,
			   ServiceWrapper<KaleoProcessLinkLocalService> {

	public KaleoProcessLinkLocalServiceWrapper() {
		this(null);
	}

	public KaleoProcessLinkLocalServiceWrapper(
		KaleoProcessLinkLocalService kaleoProcessLinkLocalService) {

		_kaleoProcessLinkLocalService = kaleoProcessLinkLocalService;
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		addKaleoProcessLink(
			com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
				kaleoProcessLink) {

		return _kaleoProcessLinkLocalService.addKaleoProcessLink(
			kaleoProcessLink);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		addKaleoProcessLink(
			long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		return _kaleoProcessLinkLocalService.addKaleoProcessLink(
			kaleoProcessId, workflowTaskName, ddmTemplateId);
	}

	/**
	 * Creates a new kaleo process link with the primary key. Does not add the kaleo process link to the database.
	 *
	 * @param kaleoProcessLinkId the primary key for the new kaleo process link
	 * @return the new kaleo process link
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		createKaleoProcessLink(long kaleoProcessLinkId) {

		return _kaleoProcessLinkLocalService.createKaleoProcessLink(
			kaleoProcessLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		deleteKaleoProcessLink(
			com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
				kaleoProcessLink) {

		return _kaleoProcessLinkLocalService.deleteKaleoProcessLink(
			kaleoProcessLink);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
			deleteKaleoProcessLink(long kaleoProcessLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLinkLocalService.deleteKaleoProcessLink(
			kaleoProcessLinkId);
	}

	/**
	 * Deletes the Kaleo process links associated with the Kaleo process and
	 * their resources.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process from which to
	 delete Kaleo process links
	 */
	@Override
	public void deleteKaleoProcessLinks(long kaleoProcessId) {
		_kaleoProcessLinkLocalService.deleteKaleoProcessLinks(kaleoProcessId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoProcessLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoProcessLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoProcessLinkLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _kaleoProcessLinkLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _kaleoProcessLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _kaleoProcessLinkLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _kaleoProcessLinkLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _kaleoProcessLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		fetchKaleoProcessLink(long kaleoProcessLinkId) {

		return _kaleoProcessLinkLocalService.fetchKaleoProcessLink(
			kaleoProcessLinkId);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		fetchKaleoProcessLink(long kaleoProcessId, String workflowTaskName) {

		return _kaleoProcessLinkLocalService.fetchKaleoProcessLink(
			kaleoProcessId, workflowTaskName);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoProcessLinkLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoProcessLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo process link with the primary key.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link
	 * @throws PortalException if a kaleo process link with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
			getKaleoProcessLink(long kaleoProcessLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLinkLocalService.getKaleoProcessLink(
			kaleoProcessLinkId);
	}

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
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink>
			getKaleoProcessLinks(int start, int end) {

		return _kaleoProcessLinkLocalService.getKaleoProcessLinks(start, end);
	}

	/**
	 * Returns the Kaleo process links matching the Kaleo process.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process link's Kaleo
	 process
	 * @return the Kaleo process links matching the Kaleo process, or
	 <code>null</code> if a matching Kaleo process link could not be
	 found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink>
			getKaleoProcessLinks(long kaleoProcessId) {

		return _kaleoProcessLinkLocalService.getKaleoProcessLinks(
			kaleoProcessId);
	}

	/**
	 * Returns the number of kaleo process links.
	 *
	 * @return the number of kaleo process links
	 */
	@Override
	public int getKaleoProcessLinksCount() {
		return _kaleoProcessLinkLocalService.getKaleoProcessLinksCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoProcessLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLinkLocalService.getPersistedModel(primaryKeyObj);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		updateKaleoProcessLink(
			com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
				kaleoProcessLink) {

		return _kaleoProcessLinkLocalService.updateKaleoProcessLink(
			kaleoProcessLink);
	}

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
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink
		updateKaleoProcessLink(
			long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		return _kaleoProcessLinkLocalService.updateKaleoProcessLink(
			kaleoProcessId, workflowTaskName, ddmTemplateId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoProcessLinkLocalService.getBasePersistence();
	}

	@Override
	public KaleoProcessLinkLocalService getWrappedService() {
		return _kaleoProcessLinkLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoProcessLinkLocalService kaleoProcessLinkLocalService) {

		_kaleoProcessLinkLocalService = kaleoProcessLinkLocalService;
	}

	private KaleoProcessLinkLocalService _kaleoProcessLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:501788555
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessLink;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KaleoProcessLink. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLinkLocalService
 * @generated
 */
public class KaleoProcessLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static KaleoProcessLink addKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink) {

		return getService().addKaleoProcessLink(kaleoProcessLink);
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
	public static KaleoProcessLink addKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		return getService().addKaleoProcessLink(
			kaleoProcessId, workflowTaskName, ddmTemplateId);
	}

	/**
	 * Creates a new kaleo process link with the primary key. Does not add the kaleo process link to the database.
	 *
	 * @param kaleoProcessLinkId the primary key for the new kaleo process link
	 * @return the new kaleo process link
	 */
	public static KaleoProcessLink createKaleoProcessLink(
		long kaleoProcessLinkId) {

		return getService().createKaleoProcessLink(kaleoProcessLinkId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static KaleoProcessLink deleteKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink) {

		return getService().deleteKaleoProcessLink(kaleoProcessLink);
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
	public static KaleoProcessLink deleteKaleoProcessLink(
			long kaleoProcessLinkId)
		throws PortalException {

		return getService().deleteKaleoProcessLink(kaleoProcessLinkId);
	}

	/**
	 * Deletes the Kaleo process links associated with the Kaleo process and
	 * their resources.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process from which to
	 delete Kaleo process links
	 */
	public static void deleteKaleoProcessLinks(long kaleoProcessId) {
		getService().deleteKaleoProcessLinks(kaleoProcessId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static KaleoProcessLink fetchKaleoProcessLink(
		long kaleoProcessLinkId) {

		return getService().fetchKaleoProcessLink(kaleoProcessLinkId);
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
	public static KaleoProcessLink fetchKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName) {

		return getService().fetchKaleoProcessLink(
			kaleoProcessId, workflowTaskName);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo process link with the primary key.
	 *
	 * @param kaleoProcessLinkId the primary key of the kaleo process link
	 * @return the kaleo process link
	 * @throws PortalException if a kaleo process link with the primary key could not be found
	 */
	public static KaleoProcessLink getKaleoProcessLink(long kaleoProcessLinkId)
		throws PortalException {

		return getService().getKaleoProcessLink(kaleoProcessLinkId);
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
	public static List<KaleoProcessLink> getKaleoProcessLinks(
		int start, int end) {

		return getService().getKaleoProcessLinks(start, end);
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
	public static List<KaleoProcessLink> getKaleoProcessLinks(
		long kaleoProcessId) {

		return getService().getKaleoProcessLinks(kaleoProcessId);
	}

	/**
	 * Returns the number of kaleo process links.
	 *
	 * @return the number of kaleo process links
	 */
	public static int getKaleoProcessLinksCount() {
		return getService().getKaleoProcessLinksCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static KaleoProcessLink updateKaleoProcessLink(
		KaleoProcessLink kaleoProcessLink) {

		return getService().updateKaleoProcessLink(kaleoProcessLink);
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
	public static KaleoProcessLink updateKaleoProcessLink(
		long kaleoProcessId, String workflowTaskName, long ddmTemplateId) {

		return getService().updateKaleoProcessLink(
			kaleoProcessId, workflowTaskName, ddmTemplateId);
	}

	public static KaleoProcessLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoProcessLinkLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoProcessLinkLocalServiceUtil.class,
			KaleoProcessLinkLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2028199213
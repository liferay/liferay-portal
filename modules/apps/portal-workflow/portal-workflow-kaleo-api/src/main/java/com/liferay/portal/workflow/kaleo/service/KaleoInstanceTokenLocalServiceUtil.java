/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoInstanceToken. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceTokenLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceTokenLocalService
 * @generated
 */
public class KaleoInstanceTokenLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceTokenLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was added
	 */
	public static KaleoInstanceToken addKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return getService().addKaleoInstanceToken(kaleoInstanceToken);
	}

	public static KaleoInstanceToken addKaleoInstanceToken(
			long currentKaleoNodeId, long kaleoDefinitionId,
			long kaleoDefinitionVersionId, long kaleoInstanceId,
			long parentKaleoInstanceTokenId,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoInstanceToken(
			currentKaleoNodeId, kaleoDefinitionId, kaleoDefinitionVersionId,
			kaleoInstanceId, parentKaleoInstanceTokenId, workflowContext,
			serviceContext);
	}

	public static KaleoInstanceToken addKaleoInstanceToken(
			long parentKaleoInstanceTokenId,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoInstanceToken(
			parentKaleoInstanceTokenId, workflowContext, serviceContext);
	}

	public static KaleoInstanceToken completeKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws PortalException {

		return getService().completeKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * Creates a new kaleo instance token with the primary key. Does not add the kaleo instance token to the database.
	 *
	 * @param kaleoInstanceTokenId the primary key for the new kaleo instance token
	 * @return the new kaleo instance token
	 */
	public static KaleoInstanceToken createKaleoInstanceToken(
		long kaleoInstanceTokenId) {

		return getService().createKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoInstanceTokens(long companyId) {
		getService().deleteCompanyKaleoInstanceTokens(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoInstanceTokens(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoInstanceTokens(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoInstanceKaleoInstanceTokens(
		long kaleoInstanceId) {

		getService().deleteKaleoInstanceKaleoInstanceTokens(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was removed
	 */
	public static KaleoInstanceToken deleteKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return getService().deleteKaleoInstanceToken(kaleoInstanceToken);
	}

	/**
	 * Deletes the kaleo instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token that was removed
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	public static KaleoInstanceToken deleteKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws PortalException {

		return getService().deleteKaleoInstanceToken(kaleoInstanceTokenId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
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

	public static KaleoInstanceToken fetchKaleoInstanceToken(
		long kaleoInstanceTokenId) {

		return getService().fetchKaleoInstanceToken(kaleoInstanceTokenId);
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
	 * Returns the kaleo instance token with the primary key.
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	public static KaleoInstanceToken getKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws PortalException {

		return getService().getKaleoInstanceToken(kaleoInstanceTokenId);
	}

	/**
	 * Returns a range of all the kaleo instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instance tokens
	 * @param end the upper bound of the range of kaleo instance tokens (not inclusive)
	 * @return the range of kaleo instance tokens
	 */
	public static List<KaleoInstanceToken> getKaleoInstanceTokens(
		int start, int end) {

		return getService().getKaleoInstanceTokens(start, end);
	}

	public static List<KaleoInstanceToken> getKaleoInstanceTokens(
		long kaleoInstanceId) {

		return getService().getKaleoInstanceTokens(kaleoInstanceId);
	}

	public static List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId, java.util.Date completionDate,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
	}

	public static List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, serviceContext);
	}

	/**
	 * Returns the number of kaleo instance tokens.
	 *
	 * @return the number of kaleo instance tokens
	 */
	public static int getKaleoInstanceTokensCount() {
		return getService().getKaleoInstanceTokensCount();
	}

	public static int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId, java.util.Date completionDate,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstanceTokensCount(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
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

	public static KaleoInstanceToken getRootKaleoInstanceToken(
			long kaleoInstanceId, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getRootKaleoInstanceToken(
			kaleoInstanceId, workflowContext, serviceContext);
	}

	/**
	 * Updates the kaleo instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was updated
	 */
	public static KaleoInstanceToken updateKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return getService().updateKaleoInstanceToken(kaleoInstanceToken);
	}

	public static KaleoInstanceToken updateKaleoInstanceToken(
			long kaleoInstanceTokenId, long currentKaleoNodeId,
			String currentKaleoNodeName)
		throws PortalException {

		return getService().updateKaleoInstanceToken(
			kaleoInstanceTokenId, currentKaleoNodeId, currentKaleoNodeName);
	}

	public static KaleoInstanceTokenLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoInstanceTokenLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoInstanceTokenLocalServiceUtil.class,
			KaleoInstanceTokenLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1622178739
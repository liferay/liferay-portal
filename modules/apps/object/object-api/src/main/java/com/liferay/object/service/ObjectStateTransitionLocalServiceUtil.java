/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectStateTransition;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectStateTransition. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectStateTransitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectStateTransitionLocalService
 * @generated
 */
public class ObjectStateTransitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectStateTransitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectStateTransition addObjectStateTransition(
			long userId, long objectStateFlowId, long sourceObjectStateId,
			long targetObjectStateId)
		throws PortalException {

		return getService().addObjectStateTransition(
			userId, objectStateFlowId, sourceObjectStateId,
			targetObjectStateId);
	}

	/**
	 * Adds the object state transition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was added
	 */
	public static ObjectStateTransition addObjectStateTransition(
		ObjectStateTransition objectStateTransition) {

		return getService().addObjectStateTransition(objectStateTransition);
	}

	/**
	 * Creates a new object state transition with the primary key. Does not add the object state transition to the database.
	 *
	 * @param objectStateTransitionId the primary key for the new object state transition
	 * @return the new object state transition
	 */
	public static ObjectStateTransition createObjectStateTransition(
		long objectStateTransitionId) {

		return getService().createObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteObjectStateFlowObjectStateTransitions(
		long objectStateFlowId) {

		getService().deleteObjectStateFlowObjectStateTransitions(
			objectStateFlowId);
	}

	public static void deleteObjectStateObjectStateTransitions(
		long objectStateId) {

		getService().deleteObjectStateObjectStateTransitions(objectStateId);
	}

	/**
	 * Deletes the object state transition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition that was removed
	 * @throws PortalException if a object state transition with the primary key could not be found
	 */
	public static ObjectStateTransition deleteObjectStateTransition(
			long objectStateTransitionId)
		throws PortalException {

		return getService().deleteObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * Deletes the object state transition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was removed
	 */
	public static ObjectStateTransition deleteObjectStateTransition(
		ObjectStateTransition objectStateTransition) {

		return getService().deleteObjectStateTransition(objectStateTransition);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
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

	public static ObjectStateTransition fetchObjectStateTransition(
		long objectStateTransitionId) {

		return getService().fetchObjectStateTransition(objectStateTransitionId);
	}

	/**
	 * Returns the object state transition with the matching UUID and company.
	 *
	 * @param uuid the object state transition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	public static ObjectStateTransition
		fetchObjectStateTransitionByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchObjectStateTransitionByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<ObjectStateTransition>
		getObjectStateFlowObjectStateTransitions(long objectStateFlowId) {

		return getService().getObjectStateFlowObjectStateTransitions(
			objectStateFlowId);
	}

	/**
	 * Returns the object state transition with the primary key.
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition
	 * @throws PortalException if a object state transition with the primary key could not be found
	 */
	public static ObjectStateTransition getObjectStateTransition(
			long objectStateTransitionId)
		throws PortalException {

		return getService().getObjectStateTransition(objectStateTransitionId);
	}

	/**
	 * Returns the object state transition with the matching UUID and company.
	 *
	 * @param uuid the object state transition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object state transition
	 * @throws PortalException if a matching object state transition could not be found
	 */
	public static ObjectStateTransition
			getObjectStateTransitionByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getObjectStateTransitionByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object state transitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @return the range of object state transitions
	 */
	public static List<ObjectStateTransition> getObjectStateTransitions(
		int start, int end) {

		return getService().getObjectStateTransitions(start, end);
	}

	/**
	 * Returns the number of object state transitions.
	 *
	 * @return the number of object state transitions
	 */
	public static int getObjectStateTransitionsCount() {
		return getService().getObjectStateTransitionsCount();
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
	 * Updates the object state transition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was updated
	 */
	public static ObjectStateTransition updateObjectStateTransition(
		ObjectStateTransition objectStateTransition) {

		return getService().updateObjectStateTransition(objectStateTransition);
	}

	public static void updateObjectStateTransitions(
			com.liferay.object.model.ObjectStateFlow objectStateFlow)
		throws PortalException {

		getService().updateObjectStateTransitions(objectStateFlow);
	}

	public static ObjectStateTransitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectStateTransitionLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectStateTransitionLocalServiceUtil.class,
			ObjectStateTransitionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-682313312
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPSpecificationOptionListTypeDefinitionRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPSpecificationOptionListTypeDefinitionRelLocalService
 * @generated
 */
public class CPSpecificationOptionListTypeDefinitionRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp specification option list type definition rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was added
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
		addCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return getService().addCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionListTypeDefinitionRel);
	}

	public static CPSpecificationOptionListTypeDefinitionRel
			addCPSpecificationOptionListTypeDefinitionRel(
				long cpSpecificationOptionId, long listTypeDefinitionId)
		throws PortalException {

		return getService().addCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionId, listTypeDefinitionId);
	}

	/**
	 * Creates a new cp specification option list type definition rel with the primary key. Does not add the cp specification option list type definition rel to the database.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key for the new cp specification option list type definition rel
	 * @return the new cp specification option list type definition rel
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
		createCPSpecificationOptionListTypeDefinitionRel(
			long CPSpecificationOptionListTypeDefinitionRelId) {

		return getService().createCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRelId);
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
	 * Deletes the cp specification option list type definition rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was removed
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
		deleteCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return getService().deleteCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionListTypeDefinitionRel);
	}

	/**
	 * Deletes the cp specification option list type definition rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was removed
	 * @throws PortalException if a cp specification option list type definition rel with the primary key could not be found
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
			deleteCPSpecificationOptionListTypeDefinitionRel(
				long CPSpecificationOptionListTypeDefinitionRelId)
		throws PortalException {

		return getService().deleteCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRelId);
	}

	public static void deleteCPSpecificationOptionListTypeDefinitionRel(
			long cpSpecificationOptionId, long listTypeDefinitionId)
		throws PortalException {

		getService().deleteCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionId, listTypeDefinitionId);
	}

	public static void deleteCPSpecificationOptionListTypeDefinitionRels(
		long cpSpecificationOptionId) {

		getService().deleteCPSpecificationOptionListTypeDefinitionRels(
			cpSpecificationOptionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
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

	public static CPSpecificationOptionListTypeDefinitionRel
		fetchCPSpecificationOptionListTypeDefinitionRel(
			long CPSpecificationOptionListTypeDefinitionRelId) {

		return getService().fetchCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp specification option list type definition rel with the primary key.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel
	 * @throws PortalException if a cp specification option list type definition rel with the primary key could not be found
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
			getCPSpecificationOptionListTypeDefinitionRel(
				long CPSpecificationOptionListTypeDefinitionRelId)
		throws PortalException {

		return getService().getCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRelId);
	}

	/**
	 * Returns a range of all the cp specification option list type definition rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification option list type definition rels
	 * @param end the upper bound of the range of cp specification option list type definition rels (not inclusive)
	 * @return the range of cp specification option list type definition rels
	 */
	public static List<CPSpecificationOptionListTypeDefinitionRel>
		getCPSpecificationOptionListTypeDefinitionRels(int start, int end) {

		return getService().getCPSpecificationOptionListTypeDefinitionRels(
			start, end);
	}

	public static List<CPSpecificationOptionListTypeDefinitionRel>
		getCPSpecificationOptionListTypeDefinitionRels(
			long cpSpecificationOptionId) {

		return getService().getCPSpecificationOptionListTypeDefinitionRels(
			cpSpecificationOptionId);
	}

	/**
	 * Returns the number of cp specification option list type definition rels.
	 *
	 * @return the number of cp specification option list type definition rels
	 */
	public static int getCPSpecificationOptionListTypeDefinitionRelsCount() {
		return getService().
			getCPSpecificationOptionListTypeDefinitionRelsCount();
	}

	public static int getCPSpecificationOptionListTypeDefinitionRelsCount(
		long listTypeDefinitionId) {

		return getService().getCPSpecificationOptionListTypeDefinitionRelsCount(
			listTypeDefinitionId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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

	public static boolean hasCPSpecificationOptionListTypeDefinitionRel(
		long cpSpecificationOptionId, long listTypeDefinitionId) {

		return getService().hasCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionId, listTypeDefinitionId);
	}

	/**
	 * Updates the cp specification option list type definition rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was updated
	 */
	public static CPSpecificationOptionListTypeDefinitionRel
		updateCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return getService().updateCPSpecificationOptionListTypeDefinitionRel(
			cpSpecificationOptionListTypeDefinitionRel);
	}

	public static CPSpecificationOptionListTypeDefinitionRelLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CPSpecificationOptionListTypeDefinitionRelLocalService>
			_serviceSnapshot = new Snapshot<>(
				CPSpecificationOptionListTypeDefinitionRelLocalServiceUtil.
					class,
				CPSpecificationOptionListTypeDefinitionRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-821628777
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Set;

/**
 * Provides the local service utility for CTCollectionTemplate. This utility wraps
 * <code>com.liferay.change.tracking.service.impl.CTCollectionTemplateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionTemplateLocalService
 * @generated
 */
public class CTCollectionTemplateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.service.impl.CTCollectionTemplateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ct collection template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was added
	 */
	public static CTCollectionTemplate addCTCollectionTemplate(
		CTCollectionTemplate ctCollectionTemplate) {

		return getService().addCTCollectionTemplate(ctCollectionTemplate);
	}

	public static CTCollectionTemplate addCTCollectionTemplate(
			long userId, String name, String description, String json)
		throws PortalException {

		return getService().addCTCollectionTemplate(
			userId, name, description, json);
	}

	/**
	 * Creates a new ct collection template with the primary key. Does not add the ct collection template to the database.
	 *
	 * @param ctCollectionTemplateId the primary key for the new ct collection template
	 * @return the new ct collection template
	 */
	public static CTCollectionTemplate createCTCollectionTemplate(
		long ctCollectionTemplateId) {

		return getService().createCTCollectionTemplate(ctCollectionTemplateId);
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
	 * Deletes the ct collection template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was removed
	 * @throws PortalException
	 */
	public static CTCollectionTemplate deleteCTCollectionTemplate(
			CTCollectionTemplate ctCollectionTemplate)
		throws PortalException {

		return getService().deleteCTCollectionTemplate(ctCollectionTemplate);
	}

	/**
	 * Deletes the ct collection template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template that was removed
	 * @throws PortalException if a ct collection template with the primary key could not be found
	 */
	public static CTCollectionTemplate deleteCTCollectionTemplate(
			long ctCollectionTemplateId)
		throws PortalException {

		return getService().deleteCTCollectionTemplate(ctCollectionTemplateId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
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

	public static CTCollectionTemplate fetchCTCollectionTemplate(
		long ctCollectionTemplateId) {

		return getService().fetchCTCollectionTemplate(ctCollectionTemplateId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ct collection template with the primary key.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template
	 * @throws PortalException if a ct collection template with the primary key could not be found
	 */
	public static CTCollectionTemplate getCTCollectionTemplate(
			long ctCollectionTemplateId)
		throws PortalException {

		return getService().getCTCollectionTemplate(ctCollectionTemplateId);
	}

	/**
	 * Returns a range of all the ct collection templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @return the range of ct collection templates
	 */
	public static List<CTCollectionTemplate> getCTCollectionTemplates(
		int start, int end) {

		return getService().getCTCollectionTemplates(start, end);
	}

	public static List<CTCollectionTemplate> getCTCollectionTemplates(
		long companyId, int start, int end) {

		return getService().getCTCollectionTemplates(companyId, start, end);
	}

	/**
	 * Returns the number of ct collection templates.
	 *
	 * @return the number of ct collection templates
	 */
	public static int getCTCollectionTemplatesCount() {
		return getService().getCTCollectionTemplatesCount();
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

	public static Set<String> getTokens() {
		return getService().getTokens();
	}

	public static String parseTokens(long ctCollectionTemplateId, String s) {
		return getService().parseTokens(ctCollectionTemplateId, s);
	}

	/**
	 * Updates the ct collection template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionTemplate the ct collection template
	 * @return the ct collection template that was updated
	 */
	public static CTCollectionTemplate updateCTCollectionTemplate(
		CTCollectionTemplate ctCollectionTemplate) {

		return getService().updateCTCollectionTemplate(ctCollectionTemplate);
	}

	public static CTCollectionTemplate updateCTCollectionTemplate(
			long ctCollectionTemplateId, String name, String description,
			String json)
		throws PortalException {

		return getService().updateCTCollectionTemplate(
			ctCollectionTemplateId, name, description, json);
	}

	public static CTCollectionTemplateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTCollectionTemplateLocalService>
		_serviceSnapshot = new Snapshot<>(
			CTCollectionTemplateLocalServiceUtil.class,
			CTCollectionTemplateLocalService.class);

}
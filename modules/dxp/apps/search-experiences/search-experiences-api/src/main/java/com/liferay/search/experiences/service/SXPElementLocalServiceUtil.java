/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.search.experiences.model.SXPElement;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for SXPElement. This utility wraps
 * <code>com.liferay.search.experiences.service.impl.SXPElementLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SXPElementLocalService
 * @generated
 */
public class SXPElementLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.search.experiences.service.impl.SXPElementLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SXPElement addSXPElement(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> descriptionMap,
			String elementDefinitionJSON, String fallbackDescription,
			String fallbackTitle, boolean readOnly, String schemaVersion,
			Map<java.util.Locale, String> titleMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSXPElement(
			externalReferenceCode, userId, descriptionMap,
			elementDefinitionJSON, fallbackDescription, fallbackTitle, readOnly,
			schemaVersion, titleMap, type, serviceContext);
	}

	/**
	 * Adds the sxp element to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was added
	 */
	public static SXPElement addSXPElement(SXPElement sxpElement) {
		return getService().addSXPElement(sxpElement);
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
	 * Creates a new sxp element with the primary key. Does not add the sxp element to the database.
	 *
	 * @param sxpElementId the primary key for the new sxp element
	 * @return the new sxp element
	 */
	public static SXPElement createSXPElement(long sxpElementId) {
		return getService().createSXPElement(sxpElementId);
	}

	public static void deleteCompanySXPElements(long companyId)
		throws PortalException {

		getService().deleteCompanySXPElements(companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sxp element with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	public static SXPElement deleteSXPElement(long sxpElementId)
		throws PortalException {

		return getService().deleteSXPElement(sxpElementId);
	}

	/**
	 * Deletes the sxp element from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was removed
	 * @throws PortalException
	 */
	public static SXPElement deleteSXPElement(SXPElement sxpElement)
		throws PortalException {

		return getService().deleteSXPElement(sxpElement);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
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

	public static SXPElement fetchSXPElement(long sxpElementId) {
		return getService().fetchSXPElement(sxpElementId);
	}

	public static SXPElement fetchSXPElementByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchSXPElementByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element, or <code>null</code> if a matching sxp element could not be found
	 */
	public static SXPElement fetchSXPElementByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchSXPElementByUuidAndCompanyId(uuid, companyId);
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
	 * Returns the sxp element with the primary key.
	 *
	 * @param sxpElementId the primary key of the sxp element
	 * @return the sxp element
	 * @throws PortalException if a sxp element with the primary key could not be found
	 */
	public static SXPElement getSXPElement(long sxpElementId)
		throws PortalException {

		return getService().getSXPElement(sxpElementId);
	}

	public static SXPElement getSXPElementByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getSXPElementByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the sxp element with the matching UUID and company.
	 *
	 * @param uuid the sxp element's UUID
	 * @param companyId the primary key of the company
	 * @return the matching sxp element
	 * @throws PortalException if a matching sxp element could not be found
	 */
	public static SXPElement getSXPElementByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getSXPElementByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the sxp elements.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.search.experiences.model.impl.SXPElementModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sxp elements
	 * @param end the upper bound of the range of sxp elements (not inclusive)
	 * @return the range of sxp elements
	 */
	public static List<SXPElement> getSXPElements(int start, int end) {
		return getService().getSXPElements(start, end);
	}

	public static List<SXPElement> getSXPElements(
		long companyId, boolean readOnly) {

		return getService().getSXPElements(companyId, readOnly);
	}

	/**
	 * Returns the number of sxp elements.
	 *
	 * @return the number of sxp elements
	 */
	public static int getSXPElementsCount() {
		return getService().getSXPElementsCount();
	}

	public static SXPElement updateSXPElement(
			String externalReferenceCode, long userId, long sxpElementId,
			Map<java.util.Locale, String> descriptionMap,
			String elementDefinitionJSON, boolean hidden, String schemaVersion,
			Map<java.util.Locale, String> titleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSXPElement(
			externalReferenceCode, userId, sxpElementId, descriptionMap,
			elementDefinitionJSON, hidden, schemaVersion, titleMap,
			serviceContext);
	}

	/**
	 * Updates the sxp element in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SXPElementLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sxpElement the sxp element
	 * @return the sxp element that was updated
	 */
	public static SXPElement updateSXPElement(SXPElement sxpElement) {
		return getService().updateSXPElement(sxpElement);
	}

	public static SXPElementLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SXPElementLocalService> _serviceSnapshot =
		new Snapshot<>(
			SXPElementLocalServiceUtil.class, SXPElementLocalService.class);

}
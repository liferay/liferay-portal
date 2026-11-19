/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KBTemplate. This utility wraps
 * <code>com.liferay.knowledge.base.service.impl.KBTemplateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KBTemplateLocalService
 * @generated
 */
public class KBTemplateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.knowledge.base.service.impl.KBTemplateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kb template to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was added
	 */
	public static KBTemplate addKBTemplate(KBTemplate kbTemplate) {
		return getService().addKBTemplate(kbTemplate);
	}

	public static KBTemplate addKBTemplate(
			long userId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBTemplate(
			userId, title, content, serviceContext);
	}

	/**
	 * Creates a new kb template with the primary key. Does not add the kb template to the database.
	 *
	 * @param kbTemplateId the primary key for the new kb template
	 * @return the new kb template
	 */
	public static KBTemplate createKBTemplate(long kbTemplateId) {
		return getService().createKBTemplate(kbTemplateId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteGroupKBTemplates(long groupId)
		throws PortalException {

		getService().deleteGroupKBTemplates(groupId);
	}

	/**
	 * Deletes the kb template from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was removed
	 * @throws PortalException
	 */
	public static KBTemplate deleteKBTemplate(KBTemplate kbTemplate)
		throws PortalException {

		return getService().deleteKBTemplate(kbTemplate);
	}

	/**
	 * Deletes the kb template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplateId the primary key of the kb template
	 * @return the kb template that was removed
	 * @throws PortalException if a kb template with the primary key could not be found
	 */
	public static KBTemplate deleteKBTemplate(long kbTemplateId)
		throws PortalException {

		return getService().deleteKBTemplate(kbTemplateId);
	}

	public static void deleteKBTemplates(long[] kbTemplateIds)
		throws PortalException {

		getService().deleteKBTemplates(kbTemplateIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
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

	public static KBTemplate fetchKBTemplate(long kbTemplateId) {
		return getService().fetchKBTemplate(kbTemplateId);
	}

	/**
	 * Returns the kb template matching the UUID and group.
	 *
	 * @param uuid the kb template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb template, or <code>null</code> if a matching kb template could not be found
	 */
	public static KBTemplate fetchKBTemplateByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchKBTemplateByUuidAndGroupId(uuid, groupId);
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
	 * Returns the kb template with the primary key.
	 *
	 * @param kbTemplateId the primary key of the kb template
	 * @return the kb template
	 * @throws PortalException if a kb template with the primary key could not be found
	 */
	public static KBTemplate getKBTemplate(long kbTemplateId)
		throws PortalException {

		return getService().getKBTemplate(kbTemplateId);
	}

	/**
	 * Returns the kb template matching the UUID and group.
	 *
	 * @param uuid the kb template's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb template
	 * @throws PortalException if a matching kb template could not be found
	 */
	public static KBTemplate getKBTemplateByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getKBTemplateByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the kb templates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb templates
	 * @param end the upper bound of the range of kb templates (not inclusive)
	 * @return the range of kb templates
	 */
	public static List<KBTemplate> getKBTemplates(int start, int end) {
		return getService().getKBTemplates(start, end);
	}

	/**
	 * Returns all the kb templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb templates
	 * @param companyId the primary key of the company
	 * @return the matching kb templates, or an empty list if no matches were found
	 */
	public static List<KBTemplate> getKBTemplatesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getKBTemplatesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of kb templates matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb templates
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kb templates
	 * @param end the upper bound of the range of kb templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kb templates, or an empty list if no matches were found
	 */
	public static List<KBTemplate> getKBTemplatesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBTemplate> orderByComparator) {

		return getService().getKBTemplatesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb templates.
	 *
	 * @return the number of kb templates
	 */
	public static int getKBTemplatesCount() {
		return getService().getKBTemplatesCount();
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

	public static List<KBTemplate> search(
		long groupId, String title, String content, java.util.Date startDate,
		java.util.Date endDate, boolean andOperator, int start, int end,
		OrderByComparator<KBTemplate> orderByComparator) {

		return getService().search(
			groupId, title, content, startDate, endDate, andOperator, start,
			end, orderByComparator);
	}

	/**
	 * Updates the kb template in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBTemplateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbTemplate the kb template
	 * @return the kb template that was updated
	 */
	public static KBTemplate updateKBTemplate(KBTemplate kbTemplate) {
		return getService().updateKBTemplate(kbTemplate);
	}

	public static KBTemplate updateKBTemplate(
			long kbTemplateId, String title, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKBTemplate(
			kbTemplateId, title, content, serviceContext);
	}

	public static KBTemplateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KBTemplateLocalService> _serviceSnapshot =
		new Snapshot<>(
			KBTemplateLocalServiceUtil.class, KBTemplateLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2141152284
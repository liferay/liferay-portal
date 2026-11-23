/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.template.model.TemplateEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for TemplateEntry. This utility wraps
 * <code>com.liferay.template.service.impl.TemplateEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see TemplateEntryLocalService
 * @generated
 */
public class TemplateEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.template.service.impl.TemplateEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static TemplateEntry addTemplateEntry(
			String externalReferenceCode, long userId, long groupId,
			long ddmTemplateId, String infoItemClassName,
			String infoItemFormVariationKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTemplateEntry(
			externalReferenceCode, userId, groupId, ddmTemplateId,
			infoItemClassName, infoItemFormVariationKey, serviceContext);
	}

	/**
	 * Adds the template entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was added
	 */
	public static TemplateEntry addTemplateEntry(TemplateEntry templateEntry) {
		return getService().addTemplateEntry(templateEntry);
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
	 * Creates a new template entry with the primary key. Does not add the template entry to the database.
	 *
	 * @param templateEntryId the primary key for the new template entry
	 * @return the new template entry
	 */
	public static TemplateEntry createTemplateEntry(long templateEntryId) {
		return getService().createTemplateEntry(templateEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTemplateEntries(long groupId) {
		getService().deleteTemplateEntries(groupId);
	}

	/**
	 * Deletes the template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry that was removed
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	public static TemplateEntry deleteTemplateEntry(long templateEntryId)
		throws PortalException {

		return getService().deleteTemplateEntry(templateEntryId);
	}

	/**
	 * Deletes the template entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was removed
	 */
	public static TemplateEntry deleteTemplateEntry(
		TemplateEntry templateEntry) {

		return getService().deleteTemplateEntry(templateEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
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

	public static TemplateEntry fetchTemplateEntry(long templateEntryId) {
		return getService().fetchTemplateEntry(templateEntryId);
	}

	public static TemplateEntry fetchTemplateEntryByDDMTemplateId(
		long ddmTemplateId) {

		return getService().fetchTemplateEntryByDDMTemplateId(ddmTemplateId);
	}

	public static TemplateEntry fetchTemplateEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchTemplateEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	public static TemplateEntry fetchTemplateEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchTemplateEntryByUuidAndGroupId(uuid, groupId);
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
	 * Returns a range of all the template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.template.model.impl.TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @return the range of template entries
	 */
	public static List<TemplateEntry> getTemplateEntries(int start, int end) {
		return getService().getTemplateEntries(start, end);
	}

	public static List<TemplateEntry> getTemplateEntries(
		long groupId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getService().getTemplateEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<TemplateEntry> getTemplateEntries(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getService().getTemplateEntries(
			groupId, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	public static List<TemplateEntry> getTemplateEntries(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getService().getTemplateEntries(
			groupIds, infoItemClassName, infoItemFormVariationKey, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @return the matching template entries, or an empty list if no matches were found
	 */
	public static List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getTemplateEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of template entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the template entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching template entries, or an empty list if no matches were found
	 */
	public static List<TemplateEntry> getTemplateEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return getService().getTemplateEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of template entries.
	 *
	 * @return the number of template entries
	 */
	public static int getTemplateEntriesCount() {
		return getService().getTemplateEntriesCount();
	}

	public static int getTemplateEntriesCount(long groupId) {
		return getService().getTemplateEntriesCount(groupId);
	}

	/**
	 * Returns the template entry with the primary key.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry
	 * @throws PortalException if a template entry with the primary key could not be found
	 */
	public static TemplateEntry getTemplateEntry(long templateEntryId)
		throws PortalException {

		return getService().getTemplateEntry(templateEntryId);
	}

	public static TemplateEntry getTemplateEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getTemplateEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the template entry matching the UUID and group.
	 *
	 * @param uuid the template entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching template entry
	 * @throws PortalException if a matching template entry could not be found
	 */
	public static TemplateEntry getTemplateEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getTemplateEntryByUuidAndGroupId(uuid, groupId);
	}

	public static TemplateEntry updateTemplateEntry(long templateEntryId)
		throws PortalException {

		return getService().updateTemplateEntry(templateEntryId);
	}

	/**
	 * Updates the template entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TemplateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateEntry the template entry
	 * @return the template entry that was updated
	 */
	public static TemplateEntry updateTemplateEntry(
		TemplateEntry templateEntry) {

		return getService().updateTemplateEntry(templateEntry);
	}

	public static TemplateEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<TemplateEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			TemplateEntryLocalServiceUtil.class,
			TemplateEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:527597194
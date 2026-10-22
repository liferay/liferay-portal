/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MBCategory. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBCategoryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBCategoryLocalService
 * @generated
 */
public class MBCategoryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBCategoryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			serviceContext);
	}

	public static MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, String displayStyle,
			String emailAddress, String inProtocol, String inServerName,
			int inServerPort, boolean inUseSSL, String inUserName,
			String inPassword, int inReadInterval, String outEmailAddress,
			boolean outCustom, String outServerName, int outServerPort,
			boolean outUseSSL, String outUserName, String outPassword,
			boolean allowAnonymous, boolean mailingListActive,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			displayStyle, emailAddress, inProtocol, inServerName, inServerPort,
			inUseSSL, inUserName, inPassword, inReadInterval, outEmailAddress,
			outCustom, outServerName, outServerPort, outUseSSL, outUserName,
			outPassword, allowAnonymous, mailingListActive, serviceContext);
	}

	public static void addCategoryResources(
			long categoryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addCategoryResources(
			categoryId, addGroupPermissions, addGuestPermissions);
	}

	public static void addCategoryResources(
			long categoryId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addCategoryResources(categoryId, modelPermissions);
	}

	public static void addCategoryResources(
			MBCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addCategoryResources(
			category, addGroupPermissions, addGuestPermissions);
	}

	public static void addCategoryResources(
			MBCategory category,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addCategoryResources(category, modelPermissions);
	}

	/**
	 * Adds the message boards category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was added
	 */
	public static MBCategory addMBCategory(MBCategory mbCategory) {
		return getService().addMBCategory(mbCategory);
	}

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	public static MBCategory createMBCategory(long categoryId) {
		return getService().createMBCategory(categoryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCategories(long groupId) throws PortalException {
		getService().deleteCategories(groupId);
	}

	public static void deleteCategory(long categoryId) throws PortalException {
		getService().deleteCategory(categoryId);
	}

	public static void deleteCategory(MBCategory category)
		throws PortalException {

		getService().deleteCategory(category);
	}

	public static void deleteCategory(
			MBCategory category, boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteCategory(category, includeTrashedEntries);
	}

	public static void deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		getService().deleteCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Deletes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	public static MBCategory deleteMBCategory(long categoryId)
		throws PortalException {

		return getService().deleteMBCategory(categoryId);
	}

	/**
	 * Deletes the message boards category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was removed
	 */
	public static MBCategory deleteMBCategory(MBCategory mbCategory) {
		return getService().deleteMBCategory(mbCategory);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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

	public static MBCategory fetchMBCategory(long categoryId) {
		return getService().fetchMBCategory(categoryId);
	}

	public static MBCategory fetchMBCategory(long groupId, String friendlyURL) {
		return getService().fetchMBCategory(groupId, friendlyURL);
	}

	public static MBCategory fetchMBCategoryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchMBCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchMBCategoryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchMBCategoryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<MBCategory> getCategories(long groupId) {
		return getService().getCategories(groupId);
	}

	public static List<MBCategory> getCategories(long groupId, int status) {
		return getService().getCategories(groupId, status);
	}

	public static List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int start, int end) {

		return getService().getCategories(
			groupId, parentCategoryId, start, end);
	}

	public static List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return getService().getCategories(
			groupId, parentCategoryId, status, start, end);
	}

	public static List<MBCategory> getCategories(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status, int start, int end) {

		return getService().getCategories(
			groupId, excludedCategoryId, parentCategoryId, status, start, end);
	}

	public static List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return getService().getCategories(
			groupId, parentCategoryIds, start, end);
	}

	public static List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return getService().getCategories(
			groupId, parentCategoryIds, status, start, end);
	}

	public static List<MBCategory> getCategories(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status, int start, int end) {

		return getService().getCategories(
			groupId, excludedCategoryIds, parentCategoryIds, status, start,
			end);
	}

	public static List<Object> getCategoriesAndThreads(
		long groupId, long categoryId) {

		return getService().getCategoriesAndThreads(groupId, categoryId);
	}

	public static List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status) {

		return getService().getCategoriesAndThreads(
			groupId, categoryId, status);
	}

	public static List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end) {

		return getService().getCategoriesAndThreads(
			groupId, categoryId, status, start, end);
	}

	public static int getCategoriesAndThreadsCount(
		long groupId, long categoryId, int status) {

		return getService().getCategoriesAndThreadsCount(
			groupId, categoryId, status);
	}

	public static int getCategoriesCount(long groupId) {
		return getService().getCategoriesCount(groupId);
	}

	public static int getCategoriesCount(long groupId, int status) {
		return getService().getCategoriesCount(groupId, status);
	}

	public static int getCategoriesCount(long groupId, long parentCategoryId) {
		return getService().getCategoriesCount(groupId, parentCategoryId);
	}

	public static int getCategoriesCount(
		long groupId, long parentCategoryId, int status) {

		return getService().getCategoriesCount(
			groupId, parentCategoryId, status);
	}

	public static int getCategoriesCount(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status) {

		return getService().getCategoriesCount(
			groupId, excludedCategoryId, parentCategoryId, status);
	}

	public static int getCategoriesCount(
		long groupId, long[] parentCategoryIds) {

		return getService().getCategoriesCount(groupId, parentCategoryIds);
	}

	public static int getCategoriesCount(
		long groupId, long[] parentCategoryIds, int status) {

		return getService().getCategoriesCount(
			groupId, parentCategoryIds, status);
	}

	public static int getCategoriesCount(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status) {

		return getService().getCategoriesCount(
			groupId, excludedCategoryIds, parentCategoryIds, status);
	}

	public static MBCategory getCategory(long categoryId)
		throws PortalException {

		return getService().getCategory(categoryId);
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
	 * Returns a range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of message boards categories
	 */
	public static List<MBCategory> getMBCategories(int start, int end) {
		return getService().getMBCategories(start, end);
	}

	/**
	 * Returns all the message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @return the matching message boards categories, or an empty list if no matches were found
	 */
	public static List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getMBCategoriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards categories, or an empty list if no matches were found
	 */
	public static List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getService().getMBCategoriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards categories.
	 *
	 * @return the number of message boards categories
	 */
	public static int getMBCategoriesCount() {
		return getService().getMBCategoriesCount();
	}

	/**
	 * Returns the message boards category with the primary key.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	public static MBCategory getMBCategory(long categoryId)
		throws PortalException {

		return getService().getMBCategory(categoryId);
	}

	public static MBCategory getMBCategory(long groupId, String friendlyURL)
		throws PortalException {

		return getService().getMBCategory(groupId, friendlyURL);
	}

	public static MBCategory getMBCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getMBCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category
	 * @throws PortalException if a matching message boards category could not be found
	 */
	public static MBCategory getMBCategoryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getMBCategoryByUuidAndGroupId(uuid, groupId);
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

	public static List<Long> getSubcategoryIds(
		List<Long> categoryIds, long groupId, long categoryId) {

		return getService().getSubcategoryIds(categoryIds, groupId, categoryId);
	}

	public static void moveCategoriesToTrash(long groupId, long userId)
		throws PortalException {

		getService().moveCategoriesToTrash(groupId, userId);
	}

	public static MBCategory moveCategory(
			long categoryId, long parentCategoryId,
			boolean mergeWithParentCategory)
		throws PortalException {

		return getService().moveCategory(
			categoryId, parentCategoryId, mergeWithParentCategory);
	}

	public static MBCategory moveCategoryFromTrash(
			long userId, long categoryId, long newCategoryId)
		throws PortalException {

		return getService().moveCategoryFromTrash(
			userId, categoryId, newCategoryId);
	}

	public static MBCategory moveCategoryToTrash(long userId, long categoryId)
		throws PortalException {

		return getService().moveCategoryToTrash(userId, categoryId);
	}

	public static void restoreCategoryFromTrash(long userId, long categoryId)
		throws PortalException {

		getService().restoreCategoryFromTrash(userId, categoryId);
	}

	public static void subscribeCategory(
			long userId, long groupId, long categoryId)
		throws PortalException {

		getService().subscribeCategory(userId, groupId, categoryId);
	}

	public static void unsubscribeCategory(
			long userId, long groupId, long categoryId)
		throws PortalException {

		getService().unsubscribeCategory(userId, groupId, categoryId);
	}

	public static MBCategory updateCategory(
			long categoryId, long parentCategoryId, String name,
			String description, String displayStyle, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean mailingListActive, boolean mergeWithParentCategory,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCategory(
			categoryId, parentCategoryId, name, description, displayStyle,
			emailAddress, inProtocol, inServerName, inServerPort, inUseSSL,
			inUserName, inPassword, inReadInterval, outEmailAddress, outCustom,
			outServerName, outServerPort, outUseSSL, outUserName, outPassword,
			allowAnonymous, mailingListActive, mergeWithParentCategory,
			serviceContext);
	}

	/**
	 * Updates the message boards category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was updated
	 */
	public static MBCategory updateMBCategory(MBCategory mbCategory) {
		return getService().updateMBCategory(mbCategory);
	}

	public static MBCategory updateStatus(
			long userId, long categoryId, int status)
		throws PortalException {

		return getService().updateStatus(userId, categoryId, status);
	}

	public static MBCategoryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBCategoryLocalService> _serviceSnapshot =
		new Snapshot<>(
			MBCategoryLocalServiceUtil.class, MBCategoryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1781819664
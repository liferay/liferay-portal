/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for Organization. This utility wraps
 * <code>com.liferay.portal.service.impl.OrganizationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationLocalService
 * @generated
 */
public class OrganizationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.OrganizationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addGroupOrganization(
		long groupId, long organizationId) {

		return getService().addGroupOrganization(groupId, organizationId);
	}

	public static boolean addGroupOrganization(
		long groupId, Organization organization) {

		return getService().addGroupOrganization(groupId, organization);
	}

	public static boolean addGroupOrganizations(
		long groupId, List<Organization> organizations) {

		return getService().addGroupOrganizations(groupId, organizations);
	}

	public static boolean addGroupOrganizations(
		long groupId, long[] organizationIds) {

		return getService().addGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make a subsequent call to {@link
	 * #addOrganizationResources(long, Organization)}.
	 * </p>
	 *
	 * @param userId the primary key of the creator/owner of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param site whether the organization is to be associated with a main
	 site
	 * @return the organization
	 */
	public static Organization addOrganization(
			long userId, long parentOrganizationId, String name, boolean site)
		throws PortalException {

		return getService().addOrganization(
			userId, parentOrganizationId, name, site);
	}

	/**
	 * Adds the organization to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was added
	 */
	public static Organization addOrganization(Organization organization) {
		return getService().addOrganization(organization);
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures. It is
	 * not necessary to make a subsequent call to {@link
	 * #addOrganizationResources(long, Organization)}.
	 * </p>
	 *
	 * @param userId the primary key of the creator/owner of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs, asset tag names,
	 and expando bridge attributes for the organization.
	 * @return the organization
	 */
	public static Organization addOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrganization(
			externalReferenceCode, userId, parentOrganizationId, name, type,
			regionId, countryId, statusListTypeId, comments, site,
			serviceContext);
	}

	/**
	 * Adds a resource for each type of permission available on the
	 * organization.
	 *
	 * @param userId the primary key of the creator/owner of the organization
	 * @param organization the organization
	 */
	public static void addOrganizationResources(
			long userId, Organization organization)
		throws PortalException {

		getService().addOrganizationResources(userId, organization);
	}

	public static com.liferay.portal.kernel.model.User
			addOrganizationUserByEmailAddress(
				String emailAddress, long organizationId,
				ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrganizationUserByEmailAddress(
			emailAddress, organizationId, serviceContext);
	}

	public static Organization addOrUpdateOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateOrganization(
			externalReferenceCode, userId, parentOrganizationId, name, type,
			regionId, countryId, statusListTypeId, comments, hasLogo, logoBytes,
			site, serviceContext);
	}

	/**
	 * Assigns the password policy to the organizations, removing any other
	 * currently assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void addPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		getService().addPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	public static boolean addUserOrganization(
		long userId, long organizationId) {

		return getService().addUserOrganization(userId, organizationId);
	}

	public static boolean addUserOrganization(
		long userId, Organization organization) {

		return getService().addUserOrganization(userId, organization);
	}

	public static void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		getService().addUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	public static boolean addUserOrganizations(
		long userId, List<Organization> organizations) {

		return getService().addUserOrganizations(userId, organizations);
	}

	public static boolean addUserOrganizations(
		long userId, long[] organizationIds) {

		return getService().addUserOrganizations(userId, organizationIds);
	}

	public static void clearGroupOrganizations(long groupId) {
		getService().clearGroupOrganizations(groupId);
	}

	public static void clearUserOrganizations(long userId) {
		getService().clearUserOrganizations(userId);
	}

	/**
	 * Creates a new organization with the primary key. Does not add the organization to the database.
	 *
	 * @param organizationId the primary key for the new organization
	 * @return the new organization
	 */
	public static Organization createOrganization(long organizationId) {
		return getService().createOrganization(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteGroupOrganization(
		long groupId, long organizationId) {

		getService().deleteGroupOrganization(groupId, organizationId);
	}

	public static void deleteGroupOrganization(
		long groupId, Organization organization) {

		getService().deleteGroupOrganization(groupId, organization);
	}

	public static void deleteGroupOrganizations(
		long groupId, List<Organization> organizations) {

		getService().deleteGroupOrganizations(groupId, organizations);
	}

	public static void deleteGroupOrganizations(
		long groupId, long[] organizationIds) {

		getService().deleteGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	public static void deleteLogo(long organizationId) throws PortalException {
		getService().deleteLogo(organizationId);
	}

	/**
	 * Deletes the organization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization that was removed
	 * @throws PortalException if a organization with the primary key could not be found
	 */
	public static Organization deleteOrganization(long organizationId)
		throws PortalException {

		return getService().deleteOrganization(organizationId);
	}

	/**
	 * Deletes the organization from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was removed
	 * @throws PortalException
	 */
	public static Organization deleteOrganization(Organization organization)
		throws PortalException {

		return getService().deleteOrganization(organization);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteUserOrganization(
		long userId, long organizationId) {

		getService().deleteUserOrganization(userId, organizationId);
	}

	public static void deleteUserOrganization(
		long userId, Organization organization) {

		getService().deleteUserOrganization(userId, organization);
	}

	public static void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		getService().deleteUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	public static void deleteUserOrganizations(
		long userId, List<Organization> organizations) {

		getService().deleteUserOrganizations(userId, organizations);
	}

	public static void deleteUserOrganizations(
		long userId, long[] organizationIds) {

		getService().deleteUserOrganizations(userId, organizationIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
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

	public static Organization fetchOrganization(long organizationId) {
		return getService().fetchOrganization(organizationId);
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name, or <code>null</code> if no
	 organization could be found
	 */
	public static Organization fetchOrganization(long companyId, String name) {
		return getService().fetchOrganization(companyId, name);
	}

	public static Organization fetchOrganizationByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the organization with the matching UUID and company.
	 *
	 * @param uuid the organization's UUID
	 * @param companyId the primary key of the company
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	public static Organization fetchOrganizationByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchOrganizationByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static String[] getChildrenTypes(String type) {
		return getService().getChildrenTypes(type);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<Organization> getGroupOrganizations(long groupId) {
		return getService().getGroupOrganizations(groupId);
	}

	public static List<Organization> getGroupOrganizations(
		long groupId, int start, int end) {

		return getService().getGroupOrganizations(groupId, start, end);
	}

	public static List<Organization> getGroupOrganizations(
		long groupId, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		return getService().getGroupOrganizations(
			groupId, start, end, orderByComparator);
	}

	public static int getGroupOrganizationsCount(long groupId) {
		return getService().getGroupOrganizationsCount(groupId);
	}

	/**
	 * Returns the groupIds of the groups associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the groupIds of groups associated with the organization
	 */
	public static long[] getGroupPrimaryKeys(long organizationId) {
		return getService().getGroupPrimaryKeys(organizationId);
	}

	public static List<Organization> getGroupUserOrganizations(
			long groupId, long userId)
		throws PortalException {

		return getService().getGroupUserOrganizations(groupId, userId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<Organization> getNoAssetOrganizations() {
		return getService().getNoAssetOrganizations();
	}

	public static Organization getOrAddEmptyOrganization(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws PortalException {

		return getService().getOrAddEmptyOrganization(
			externalReferenceCode, companyId, userId, name);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization
	 * @throws PortalException if a organization with the primary key could not be found
	 */
	public static Organization getOrganization(long organizationId)
		throws PortalException {

		return getService().getOrganization(organizationId);
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name
	 */
	public static Organization getOrganization(long companyId, String name)
		throws PortalException {

		return getService().getOrganization(companyId, name);
	}

	public static Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the organization with the matching UUID and company.
	 *
	 * @param uuid the organization's UUID
	 * @param companyId the primary key of the company
	 * @return the matching organization
	 * @throws PortalException if a matching organization could not be found
	 */
	public static Organization getOrganizationByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getOrganizationByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns the primary key of the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the primary key of the organization with the name, or
	 <code>0</code> if the organization could not be found
	 */
	public static long getOrganizationId(long companyId, String name) {
		return getService().getOrganizationId(companyId, name);
	}

	/**
	 * Returns a range of all the organizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.OrganizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of organizations
	 * @param end the upper bound of the range of organizations (not inclusive)
	 * @return the range of organizations
	 */
	public static List<Organization> getOrganizations(int start, int end) {
		return getService().getOrganizations(start, end);
	}

	public static List<Organization> getOrganizations(
			long userId, int start, int end,
			OrderByComparator<Organization> orderByComparator)
		throws PortalException {

		return getService().getOrganizations(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @return the organizations belonging to the parent organization
	 */
	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId) {

		return getService().getOrganizations(companyId, parentOrganizationId);
	}

	/**
	 * Returns a range of all the organizations belonging to the parent
	 * organization.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the range of organizations belonging to the parent organization
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationPersistence#findByC_P(
	 long, long, int, int)
	 */
	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, start, end);
	}

	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, name, start, end);
	}

	public static List<Organization> getOrganizations(
		long companyId, String treePath) {

		return getService().getOrganizations(companyId, treePath);
	}

	public static List<Organization> getOrganizations(
		long companyId, String name, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		return getService().getOrganizations(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the organizations with the primary keys.
	 *
	 * @param organizationIds the primary keys of the organizations
	 * @return the organizations with the primary keys
	 */
	public static List<Organization> getOrganizations(long[] organizationIds)
		throws PortalException {

		return getService().getOrganizations(organizationIds);
	}

	/**
	 * Returns all the organizations and users belonging to the parent
	 * organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param status the user's workflow status
	 * @param start the lower bound of the range of organizations and users to
	 return
	 * @param end the upper bound of the range of organizations and users to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the organizations and
	 users (optionally <code>null</code>)
	 * @return the organizations and users belonging to the parent organization
	 */
	public static List<Object> getOrganizationsAndUsers(
		long companyId, long parentOrganizationId, int status, int start,
		int end, OrderByComparator<?> orderByComparator) {

		return getService().getOrganizationsAndUsers(
			companyId, parentOrganizationId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of organizations and users belonging to the parent
	 * organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param status the user's workflow status
	 * @return the number of organizations and users belonging to the parent
	 organization
	 */
	public static int getOrganizationsAndUsersCount(
		long companyId, long parentOrganizationId, int status) {

		return getService().getOrganizationsAndUsersCount(
			companyId, parentOrganizationId, status);
	}

	public static List<Organization> getOrganizationsByLogoId(long logoId) {
		return getService().getOrganizationsByLogoId(logoId);
	}

	/**
	 * Returns the number of organizations.
	 *
	 * @return the number of organizations
	 */
	public static int getOrganizationsCount() {
		return getService().getOrganizationsCount();
	}

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @return the number of organizations belonging to the parent organization
	 */
	public static int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		return getService().getOrganizationsCount(
			companyId, parentOrganizationId);
	}

	public static int getOrganizationsCount(
		long companyId, long parentOrganizationId, String name) {

		return getService().getOrganizationsCount(
			companyId, parentOrganizationId, name);
	}

	public static int getOrganizationsCount(long companyId, String name) {
		return getService().getOrganizationsCount(companyId, name);
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
	 * Returns the parent organizations in order by closest ancestor. The list
	 * starts with the organization itself.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the parent organizations in order by closest ancestor
	 */
	public static List<Organization> getParentOrganizations(long organizationId)
		throws PortalException {

		return getService().getParentOrganizations(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the suborganizations of the organizations.
	 *
	 * @param organizations the organizations from which to get
	 suborganizations
	 * @return the suborganizations of the organizations
	 */
	public static List<Organization> getSuborganizations(
		List<Organization> organizations) {

		return getService().getSuborganizations(organizations);
	}

	/**
	 * Returns the suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the suborganizations of the organization
	 */
	public static List<Organization> getSuborganizations(
		long companyId, long organizationId) {

		return getService().getSuborganizations(companyId, organizationId);
	}

	/**
	 * Returns the count of suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the count of suborganizations of the organization
	 */
	public static int getSuborganizationsCount(
		long companyId, long organizationId) {

		return getService().getSuborganizationsCount(companyId, organizationId);
	}

	/**
	 * Returns the intersection of <code>allOrganizations</code> and
	 * <code>availableOrganizations</code>.
	 *
	 * @param allOrganizations the organizations to check for availability
	 * @param availableOrganizations the available organizations
	 * @return the intersection of <code>allOrganizations</code> and
	 <code>availableOrganizations</code>
	 */
	public static List<Organization> getSubsetOrganizations(
		List<Organization> allOrganizations,
		List<Organization> availableOrganizations) {

		return getService().getSubsetOrganizations(
			allOrganizations, availableOrganizations);
	}

	public static String[] getTypes() {
		return getService().getTypes();
	}

	/**
	 * Returns all the IDs of organizations with which the user is explicitly
	 * associated, optionally including the IDs of organizations that the user
	 * administers or owns.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added to it.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param includeAdministrative whether to include the IDs of organizations
	 that the user administers or owns, even if he's not a member of
	 the organizations
	 * @return the IDs of organizations with which the user is explicitly
	 associated, optionally including the IDs of organizations that
	 the user administers or owns
	 */
	public static long[] getUserOrganizationIds(
			long userId, boolean includeAdministrative)
		throws PortalException {

		return getService().getUserOrganizationIds(
			userId, includeAdministrative);
	}

	public static List<Organization> getUserOrganizations(long userId) {
		return getService().getUserOrganizations(userId);
	}

	/**
	 * Returns all the organizations with which the user is explicitly
	 * associated, optionally including the organizations that the user
	 * administers or owns.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added as a member.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param includeAdministrative whether to include the IDs of organizations
	 that the user administers or owns, even if he's not a member of
	 the organizations
	 * @return the organizations with which the user is explicitly associated,
	 optionally including the organizations that the user administers
	 or owns
	 */
	public static List<Organization> getUserOrganizations(
			long userId, boolean includeAdministrative)
		throws PortalException {

		return getService().getUserOrganizations(userId, includeAdministrative);
	}

	public static List<Organization> getUserOrganizations(
		long userId, int start, int end) {

		return getService().getUserOrganizations(userId, start, end);
	}

	public static List<Organization> getUserOrganizations(
		long userId, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		return getService().getUserOrganizations(
			userId, start, end, orderByComparator);
	}

	public static int getUserOrganizationsCount(long userId) {
		return getService().getUserOrganizationsCount(userId);
	}

	/**
	 * Returns the userIds of the users associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the userIds of users associated with the organization
	 */
	public static long[] getUserPrimaryKeys(long organizationId) {
		return getService().getUserPrimaryKeys(organizationId);
	}

	public static boolean hasGroupOrganization(
		long groupId, long organizationId) {

		return getService().hasGroupOrganization(groupId, organizationId);
	}

	public static boolean hasGroupOrganizations(long groupId) {
		return getService().hasGroupOrganizations(groupId);
	}

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * organization.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationId the primary key of the organization
	 * @return <code>true</code> if the password policy has been assigned to the
	 organization; <code>false</code> otherwise
	 */
	public static boolean hasPasswordPolicyOrganization(
		long passwordPolicyId, long organizationId) {

		return getService().hasPasswordPolicyOrganization(
			passwordPolicyId, organizationId);
	}

	public static boolean hasUserOrganization(
		long userId, long organizationId) {

		return getService().hasUserOrganization(userId, organizationId);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the organization,
	 * optionally focusing on suborganizations or the specified organization.
	 * This method is usually called to determine if the user has view access to
	 * a resource belonging to the organization.
	 *
	 * <ol>
	 * <li>
	 * If <code>inheritSuborganizations=<code>false</code></code>:
	 * the method checks whether the user belongs to the organization specified
	 * by <code>organizationId</code>. The parameter
	 * <code>includeSpecifiedOrganization</code> is ignored.
	 * </li>
	 * <li>
	 * The parameter <code>includeSpecifiedOrganization</code> is
	 * ignored unless <code>inheritSuborganizations</code> is also
	 * <code>true</code>.
	 * </li>
	 * <li>
	 * If <code>inheritSuborganizations=<code>true</code></code> and
	 * <code>includeSpecifiedOrganization=<code>false</code></code>: the method
	 * checks
	 * whether the user belongs to one of the child organizations of the one
	 * specified by <code>organizationId</code>.
	 * </li>
	 * <li>
	 * If <code>inheritSuborganizations=<code>true</code></code> and
	 * <code>includeSpecifiedOrganization=<code>true</code></code>: the method
	 * checks whether
	 * the user belongs to the organization specified by
	 * <code>organizationId</code> or any of
	 * its child organizations.
	 * </li>
	 * </ol>
	 *
	 * @param userId the primary key of the organization's user
	 * @param organizationId the primary key of the organization
	 * @param inheritSuborganizations if <code>true</code> suborganizations are
	 considered in the determination
	 * @param includeSpecifiedOrganization if <code>true</code> the
	 organization specified by <code>organizationId</code> is
	 considered in the determination
	 * @return <code>true</code> if the user has access to the organization;
	 <code>false</code> otherwise
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static boolean hasUserOrganization(
			long userId, long organizationId, boolean inheritSuborganizations,
			boolean includeSpecifiedOrganization)
		throws PortalException {

		return getService().hasUserOrganization(
			userId, organizationId, inheritSuborganizations,
			includeSpecifiedOrganization);
	}

	public static boolean hasUserOrganizations(long userId) {
		return getService().hasUserOrganizations(userId);
	}

	public static boolean isCountryEnabled(String type) {
		return getService().isCountryEnabled(type);
	}

	public static boolean isCountryRequired(String type) {
		return getService().isCountryRequired(type);
	}

	public static boolean isRootable(String type) {
		return getService().isRootable(type);
	}

	/**
	 * Rebuilds the organization's tree.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations
	 * other than normal CRUD. Under normal circumstances the tree is
	 * automatically rebuilt whenever necessary.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 */
	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	/**
	 * Returns an ordered range of all the organizations that match the
	 * keywords, using the indexer. It is preferable to use this method instead
	 * of the non-indexed version whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, zipcode, type, region or
	 country (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching organizations ordered by name
	 */
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long parentOrganizationId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, parentOrganizationId, keywords, params, start, end,
			sort);
	}

	/**
	 * Returns a name ordered range of all the organizations that match the
	 * keywords, type, region, and country, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, long, String,
	 * LinkedHashMap, int, int, Sort)} instead of this method wherever possible
	 * for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder params. For more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the matching organizations ordered by name
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return getService().search(
			companyId, parentOrganizationId, keywords, type, regionId,
			countryId, params, start, end);
	}

	/**
	 * Returns an ordered range of all the organizations that match the
	 * keywords, type, region, and country, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, long, String,
	 * String, String, String, String, String, String, LinkedHashMap, boolean,
	 * int, int, Sort)} instead of this method wherever possible for performance
	 * reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder params. For more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the organizations
	 (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		return getService().search(
			companyId, parentOrganizationId, keywords, type, regionId,
			countryId, params, start, end, orderByComparator);
	}

	/**
	 * Returns a name ordered range of all the organizations with the type,
	 * region, and country, and whose name, street, city, and zipcode match the
	 * keywords specified for them, without using the indexer. It is preferable
	 * to use the indexed version {@link #search(long, long, String, String,
	 * String, String, String, String, String, LinkedHashMap, boolean, int, int,
	 * Sort)} instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the matching organizations ordered by name
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return getService().search(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionId, countryId, params, andOperator, start, end);
	}

	/**
	 * Returns an ordered range of all the organizations with the type, region,
	 * and country, and whose name, street, city, and zipcode match the keywords
	 * specified for them, without using the indexer. It is preferable to use
	 * the indexed version {@link #search(long, long, String, String, String,
	 * String, String, String, String, LinkedHashMap, boolean, int, int, Sort)}
	 * instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the organizations
	 (optionally <code>null</code>)
	 * @return the matching organizations ordered by comparator
	 <code>orderByComparator</code>
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end, OrderByComparator<Organization> orderByComparator) {

		return getService().search(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionId, countryId, params, andOperator, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the organizations whose name, type, or
	 * location fields match the keywords specified for them, using the indexer.
	 * It is preferable to use this method instead of the non-indexed version
	 * whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the type keywords (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param region the region keywords (optionally <code>null</code>)
	 * @param country the country keywords (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param andSearch whether every field must match its keywords or just one
	 field
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @param sort the field and direction by which to sort (optionally
	 <code>null</code>)
	 * @return the matching organizations ordered by <code>sort</code>
	 */
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, String region, String country,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andSearch, start, end, sort);
	}

	/**
	 * Returns the number of organizations that match the keywords, type,
	 * region, and country.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, street, city, or zipcode (optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @return the number of matching organizations
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static int searchCount(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(
			companyId, parentOrganizationId, keywords, type, regionId,
			countryId, params);
	}

	/**
	 * Returns the number of organizations with the type, region, and country,
	 * and whose name, street, city, and zipcode match the keywords specified
	 * for them.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @param name the name keywords (space separated, optionally
	 <code>null</code>)
	 * @param type the organization's type (optionally <code>null</code>)
	 * @param street the street keywords (optionally <code>null</code>)
	 * @param city the city keywords (optionally <code>null</code>)
	 * @param zip the zipcode keywords (optionally <code>null</code>)
	 * @param regionId the primary key of the organization's region (optionally
	 <code>null</code>)
	 * @param countryId the primary key of the organization's country
	 (optionally <code>null</code>)
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.OrganizationFinder}
	 * @param andOperator whether every field must match its keywords, or just
	 one field. For example, &quot;organizations with the name
	 'Employees' and city 'Chicago'&quot; vs &quot;organizations with
	 the name 'Employees' or the city 'Chicago'&quot;.
	 * @return the number of matching organizations
	 * @see com.liferay.portal.kernel.service.persistence.OrganizationFinder
	 */
	public static int searchCount(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getService().searchCount(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionId, countryId, params, andOperator);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<Organization> searchOrganizations(
				long companyId, long parentOrganizationId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchOrganizations(
			companyId, parentOrganizationId, keywords, params, start, end,
			sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<Organization> searchOrganizations(
				long companyId, long parentOrganizationId, String name,
				String type, String street, String city, String zip,
				String region, String country,
				java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchOrganizations(
			companyId, parentOrganizationId, name, type, street, city, zip,
			region, country, params, andSearch, start, end, sort);
	}

	/**
	 * Returns the organizations and users that match the keywords specified for
	 * them and belong to the parent organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, type, or location fields or user's first
	 name, middle name, last name, screen name, email address, or
	 address fields
	 * @param status user's workflow status
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @param start the lower bound of the range of organizations and users to
	 return
	 * @param end the upper bound of the range of organizations and users to
	 return (not inclusive)
	 * @return the matching organizations and users
	 */
	public static com.liferay.portal.kernel.search.Hits
			searchOrganizationsAndUsers(
				long companyId, long parentOrganizationId, String keywords,
				int status, java.util.LinkedHashMap<String, Object> params,
				int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().searchOrganizationsAndUsers(
			companyId, parentOrganizationId, keywords, status, params, start,
			end, sorts);
	}

	/**
	 * Returns the number of organizations and users that match the keywords
	 * specified for them and belong to the parent organization.
	 *
	 * @param companyId the primary key of the organization and user's company
	 * @param parentOrganizationId the primary key of the organization and
	 user's parent organization
	 * @param keywords the keywords (space separated), which may occur in the
	 organization's name, type, or location fields or user's first
	 name, middle name, last name, screen name, email address, or
	 address fields
	 * @param status user's workflow status
	 * @param params the finder parameters (optionally <code>null</code>).
	 * @return the number of matching organizations and users
	 */
	public static int searchOrganizationsAndUsersCount(
			long companyId, long parentOrganizationId, String keywords,
			int status, java.util.LinkedHashMap<String, Object> params)
		throws PortalException {

		return getService().searchOrganizationsAndUsersCount(
			companyId, parentOrganizationId, keywords, status, params);
	}

	public static void setGroupOrganizations(
		long groupId, long[] organizationIds) {

		getService().setGroupOrganizations(groupId, organizationIds);
	}

	public static void setUserOrganizations(
		long userId, long[] organizationIds) {

		getService().setUserOrganizations(userId, organizationIds);
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void unsetGroupOrganizations(
		long groupId, long[] organizationIds) {

		getService().unsetGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void unsetPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		getService().unsetPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	/**
	 * Updates the organization's asset with the new asset categories and tag
	 * names, removing and adding asset categories and tag names as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param organization the organization
	 * @param assetCategoryIds the primary keys of the asset categories
	 * @param assetTagNames the asset tag names
	 */
	public static void updateAsset(
			long userId, Organization organization, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(
			userId, organization, assetCategoryIds, assetTagNames);
	}

	public static Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException {

		return getService().updateLogo(organizationId, logoBytes);
	}

	/**
	 * Updates the organization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OrganizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param organization the organization
	 * @return the organization that was updated
	 */
	public static Organization updateOrganization(Organization organization) {
		return getService().updateOrganization(organization);
	}

	/**
	 * Updates the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @param parentOrganizationId the primary key of organization's parent
	 organization
	 * @param name the organization's name
	 * @param type the organization's type
	 * @param regionId the primary key of the organization's region
	 * @param countryId the primary key of the organization's country
	 * @param statusListTypeId the organization's workflow status
	 * @param comments the comments about the organization
	 * @param hasLogo if the organization has a custom logo
	 * @param logoBytes the new logo image data
	 * @param site whether the organization is to be associated with a main
	 site
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public static Organization updateOrganization(
			String externalReferenceCode, long companyId, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateOrganization(
			externalReferenceCode, companyId, organizationId,
			parentOrganizationId, name, type, regionId, countryId,
			statusListTypeId, comments, hasLogo, logoBytes, site,
			serviceContext);
	}

	public static OrganizationLocalService getService() {
		return _service;
	}

	public static void setService(OrganizationLocalService service) {
		_service = service;
	}

	private static volatile OrganizationLocalService _service;

}
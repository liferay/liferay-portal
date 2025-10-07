/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for Organization. This utility wraps
 * <code>com.liferay.portal.service.impl.OrganizationServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationService
 * @generated
 */
public class OrganizationServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.OrganizationServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void addGroupOrganizations(
			long groupId, long[] organizationIds)
		throws PortalException {

		getService().addGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Adds an organization with additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
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
	 * @param addresses the organization's addresses
	 * @param emailAddresses the organization's email addresses
	 * @param orgLabors the organization's hours of operation
	 * @param phones the organization's phone numbers
	 * @param websites the organization's websites
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs, asset tag names,
	 and expando bridge attributes for the organization.
	 * @return the organization
	 */
	public static Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrganization(
			externalReferenceCode, parentOrganizationId, name, type, regionId,
			countryId, statusListTypeId, comments, site, addresses,
			emailAddresses, orgLabors, phones, websites, serviceContext);
	}

	/**
	 * Adds an organization.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the organization
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
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
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrganization(
			externalReferenceCode, parentOrganizationId, name, type, regionId,
			countryId, statusListTypeId, comments, site, serviceContext);
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
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean hasLogo,
			byte[] logoBytes, boolean site,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateOrganization(
			externalReferenceCode, parentOrganizationId, name, type, regionId,
			countryId, statusListTypeId, comments, hasLogo, logoBytes, site,
			addresses, emailAddresses, orgLabors, phones, websites,
			serviceContext);
	}

	/**
	 * Assigns the password policy to the organizations, removing any other
	 * currently assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void addPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws PortalException {

		getService().addPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	public static void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		getService().addUserOrganizationByEmailAddress(
			emailAddress, organizationId);
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
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param organizationId the primary key of the organization
	 */
	public static void deleteOrganization(long organizationId)
		throws PortalException {

		getService().deleteOrganization(organizationId);
	}

	public static void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws PortalException {

		getService().deleteUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization with the primary key, or <code>null</code> if an
	 organization with the primary key could not be found or if the
	 user did not have permission to view the organization
	 */
	public static Organization fetchOrganization(long organizationId)
		throws PortalException {

		return getService().fetchOrganization(organizationId);
	}

	public static Organization fetchOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<Organization> getGtOrganizations(
		long gtOrganizationId, long companyId, long parentOrganizationId,
		int size) {

		return getService().getGtOrganizations(
			gtOrganizationId, companyId, parentOrganizationId, size);
	}

	public static Organization getOrAddEmptyOrganization(
			String externalReferenceCode, String name)
		throws PortalException {

		return getService().getOrAddEmptyOrganization(
			externalReferenceCode, name);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization with the primary key
	 */
	public static Organization getOrganization(long organizationId)
		throws PortalException {

		return getService().getOrganization(organizationId);
	}

	public static Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the primary key of the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the primary key of the organization with the name, or
	 <code>0</code> if the organization could not be found
	 */
	public static long getOrganizationId(long companyId, String name)
		throws PortalException {

		return getService().getOrganizationId(companyId, name);
	}

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
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
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @param start the lower bound of the range of organizations to return
	 * @param end the upper bound of the range of organizations to return (not
	 inclusive)
	 * @return the range of organizations belonging to the parent organization
	 */
	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, start, end);
	}

	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end,
		OrderByComparator<Organization> orderByComparator) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, start, end, orderByComparator);
	}

	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, name, start, end);
	}

	public static List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end, OrderByComparator<Organization> orderByComparator) {

		return getService().getOrganizations(
			companyId, parentOrganizationId, name, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @return the number of organizations belonging to the parent organization
	 */
	public static int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		return getService().getOrganizationsCount(
			companyId, parentOrganizationId);
	}

	public static int getOrganizationsCount(
			long companyId, long parentOrganizationId, String name)
		throws PortalException {

		return getService().getOrganizationsCount(
			companyId, parentOrganizationId, name);
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
	 * Returns all the organizations with which the user is explicitly
	 * associated.
	 *
	 * <p>
	 * A user is considered to be <i>explicitly</i> associated with an
	 * organization if his account is individually created within the
	 * organization or if the user is later added as a member.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @return the organizations with which the user is explicitly associated
	 */
	public static List<Organization> getUserOrganizations(long userId)
		throws PortalException {

		return getService().getUserOrganizations(userId);
	}

	/**
	 * Sets the organizations in the group, removing and adding organizations to
	 * the group as necessary.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void setGroupOrganizations(
			long groupId, long[] organizationIds)
		throws PortalException {

		getService().setGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void unsetGroupOrganizations(
			long groupId, long[] organizationIds)
		throws PortalException {

		getService().unsetGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	public static void unsetPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws PortalException {

		getService().unsetPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	public static Organization updateLogo(long organizationId, byte[] logoBytes)
		throws PortalException {

		return getService().updateLogo(organizationId, logoBytes);
	}

	/**
	 * Updates the organization with additional parameters.
	 *
	 * @param organizationId the primary key of the organization
	 * @param parentOrganizationId the primary key of the organization's parent
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
	 * @param addresses the organization's addresses
	 * @param emailAddresses the organization's email addresses
	 * @param orgLabors the organization's hours of operation
	 * @param phones the organization's phone numbers
	 * @param websites the organization's websites
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public static Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateOrganization(
			externalReferenceCode, organizationId, parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, hasLogo,
			logoBytes, site, addresses, emailAddresses, orgLabors, phones,
			websites, serviceContext);
	}

	/**
	 * Updates the organization.
	 *
	 * @param organizationId the primary key of the organization
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
	 <code>null</code>). Can set asset category IDs and asset tag
	 names for the organization, and merge expando bridge attributes
	 for the organization.
	 * @return the organization
	 */
	public static Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateOrganization(
			externalReferenceCode, organizationId, parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, site,
			serviceContext);
	}

	public static OrganizationService getService() {
		return _service;
	}

	public static void setService(OrganizationService service) {
		_service = service;
	}

	private static volatile OrganizationService _service;

}
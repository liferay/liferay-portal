/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.model.Organization;

/**
 * Provides a wrapper for {@link OrganizationService}.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationService
 * @generated
 */
public class OrganizationServiceWrapper
	implements OrganizationService, ServiceWrapper<OrganizationService> {

	public OrganizationServiceWrapper() {
		this(null);
	}

	public OrganizationServiceWrapper(OrganizationService organizationService) {
		_organizationService = organizationService;
	}

	/**
	 * Adds the organizations to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void addGroupOrganizations(long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.addGroupOrganizations(groupId, organizationIds);
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
	@Override
	public Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.addOrganization(
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
	@Override
	public Organization addOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean site,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.addOrganization(
			externalReferenceCode, parentOrganizationId, name, type, regionId,
			countryId, statusListTypeId, comments, site, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.User
			addOrganizationUserByEmailAddress(
				String emailAddress, long organizationId,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.addOrganizationUserByEmailAddress(
			emailAddress, organizationId, serviceContext);
	}

	@Override
	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long parentOrganizationId,
			String name, String type, long regionId, long countryId,
			long statusListTypeId, String comments, boolean hasLogo,
			byte[] logoBytes, boolean site,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.addOrUpdateOrganization(
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
	@Override
	public void addPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.addPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	@Override
	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.addUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteLogo(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.deleteLogo(organizationId);
	}

	/**
	 * Deletes the organization. The organization's associated resources and
	 * assets are also deleted.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteOrganization(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.deleteOrganization(organizationId);
	}

	@Override
	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.deleteUserOrganizationByEmailAddress(
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
	@Override
	public Organization fetchOrganization(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.fetchOrganization(organizationId);
	}

	@Override
	public Organization fetchOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.fetchOrganizationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<Organization> getGtOrganizations(
		long gtOrganizationId, long companyId, long parentOrganizationId,
		int size) {

		return _organizationService.getGtOrganizations(
			gtOrganizationId, companyId, parentOrganizationId, size);
	}

	@Override
	public Organization getOrAddEmptyOrganization(
			String externalReferenceCode, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getOrAddEmptyOrganization(
			externalReferenceCode, name);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization with the primary key
	 */
	@Override
	public Organization getOrganization(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getOrganization(organizationId);
	}

	@Override
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getOrganizationByExternalReferenceCode(
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
	@Override
	public long getOrganizationId(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getOrganizationId(companyId, name);
	}

	/**
	 * Returns all the organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organizations' company
	 * @param parentOrganizationId the primary key of the organizations' parent
	 organization
	 * @return the organizations belonging to the parent organization
	 */
	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId) {

		return _organizationService.getOrganizations(
			companyId, parentOrganizationId);
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
	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		return _organizationService.getOrganizations(
			companyId, parentOrganizationId, start, end);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationService.getOrganizations(
			companyId, parentOrganizationId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		return _organizationService.getOrganizations(
			companyId, parentOrganizationId, name, start, end);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationService.getOrganizations(
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
	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		return _organizationService.getOrganizationsCount(
			companyId, parentOrganizationId);
	}

	@Override
	public int getOrganizationsCount(
			long companyId, long parentOrganizationId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getOrganizationsCount(
			companyId, parentOrganizationId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _organizationService.getOSGiServiceIdentifier();
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
	@Override
	public java.util.List<Organization> getUserOrganizations(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.getUserOrganizations(userId);
	}

	/**
	 * Sets the organizations in the group, removing and adding organizations to
	 * the group as necessary.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void setGroupOrganizations(long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.setGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetGroupOrganizations(long groupId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.unsetGroupOrganizations(groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetPasswordPolicyOrganizations(
			long passwordPolicyId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationService.unsetPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	@Override
	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.updateLogo(organizationId, logoBytes);
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
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.OrgLabor> orgLabors,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.updateOrganization(
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
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationService.updateOrganization(
			externalReferenceCode, organizationId, parentOrganizationId, name,
			type, regionId, countryId, statusListTypeId, comments, site,
			serviceContext);
	}

	@Override
	public OrganizationService getWrappedService() {
		return _organizationService;
	}

	@Override
	public void setWrappedService(OrganizationService organizationService) {
		_organizationService = organizationService;
	}

	private OrganizationService _organizationService;

}
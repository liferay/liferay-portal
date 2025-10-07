/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link OrganizationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OrganizationLocalService
 * @generated
 */
public class OrganizationLocalServiceWrapper
	implements OrganizationLocalService,
			   ServiceWrapper<OrganizationLocalService> {

	public OrganizationLocalServiceWrapper() {
		this(null);
	}

	public OrganizationLocalServiceWrapper(
		OrganizationLocalService organizationLocalService) {

		_organizationLocalService = organizationLocalService;
	}

	@Override
	public boolean addGroupOrganization(long groupId, long organizationId) {
		return _organizationLocalService.addGroupOrganization(
			groupId, organizationId);
	}

	@Override
	public boolean addGroupOrganization(
		long groupId, Organization organization) {

		return _organizationLocalService.addGroupOrganization(
			groupId, organization);
	}

	@Override
	public boolean addGroupOrganizations(
		long groupId, java.util.List<Organization> organizations) {

		return _organizationLocalService.addGroupOrganizations(
			groupId, organizations);
	}

	@Override
	public boolean addGroupOrganizations(long groupId, long[] organizationIds) {
		return _organizationLocalService.addGroupOrganizations(
			groupId, organizationIds);
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
	@Override
	public Organization addOrganization(
			long userId, long parentOrganizationId, String name, boolean site)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.addOrganization(
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
	@Override
	public Organization addOrganization(Organization organization) {
		return _organizationLocalService.addOrganization(organization);
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
	@Override
	public Organization addOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean site, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.addOrganization(
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
	@Override
	public void addOrganizationResources(long userId, Organization organization)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.addOrganizationResources(
			userId, organization);
	}

	@Override
	public com.liferay.portal.kernel.model.User
			addOrganizationUserByEmailAddress(
				String emailAddress, long organizationId,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.addOrganizationUserByEmailAddress(
			emailAddress, organizationId, serviceContext);
	}

	@Override
	public Organization addOrUpdateOrganization(
			String externalReferenceCode, long userId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.addOrUpdateOrganization(
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
	@Override
	public void addPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		_organizationLocalService.addPasswordPolicyOrganizations(
			passwordPolicyId, organizationIds);
	}

	@Override
	public boolean addUserOrganization(long userId, long organizationId) {
		return _organizationLocalService.addUserOrganization(
			userId, organizationId);
	}

	@Override
	public boolean addUserOrganization(long userId, Organization organization) {
		return _organizationLocalService.addUserOrganization(
			userId, organization);
	}

	@Override
	public void addUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.addUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	@Override
	public boolean addUserOrganizations(
		long userId, java.util.List<Organization> organizations) {

		return _organizationLocalService.addUserOrganizations(
			userId, organizations);
	}

	@Override
	public boolean addUserOrganizations(long userId, long[] organizationIds) {
		return _organizationLocalService.addUserOrganizations(
			userId, organizationIds);
	}

	@Override
	public void clearGroupOrganizations(long groupId) {
		_organizationLocalService.clearGroupOrganizations(groupId);
	}

	@Override
	public void clearUserOrganizations(long userId) {
		_organizationLocalService.clearUserOrganizations(userId);
	}

	/**
	 * Creates a new organization with the primary key. Does not add the organization to the database.
	 *
	 * @param organizationId the primary key for the new organization
	 * @return the new organization
	 */
	@Override
	public Organization createOrganization(long organizationId) {
		return _organizationLocalService.createOrganization(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteGroupOrganization(long groupId, long organizationId) {
		_organizationLocalService.deleteGroupOrganization(
			groupId, organizationId);
	}

	@Override
	public void deleteGroupOrganization(
		long groupId, Organization organization) {

		_organizationLocalService.deleteGroupOrganization(
			groupId, organization);
	}

	@Override
	public void deleteGroupOrganizations(
		long groupId, java.util.List<Organization> organizations) {

		_organizationLocalService.deleteGroupOrganizations(
			groupId, organizations);
	}

	@Override
	public void deleteGroupOrganizations(long groupId, long[] organizationIds) {
		_organizationLocalService.deleteGroupOrganizations(
			groupId, organizationIds);
	}

	/**
	 * Deletes the organization's logo.
	 *
	 * @param organizationId the primary key of the organization
	 */
	@Override
	public void deleteLogo(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.deleteLogo(organizationId);
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
	@Override
	public Organization deleteOrganization(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.deleteOrganization(organizationId);
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
	@Override
	public Organization deleteOrganization(Organization organization)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.deleteOrganization(organization);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteUserOrganization(long userId, long organizationId) {
		_organizationLocalService.deleteUserOrganization(
			userId, organizationId);
	}

	@Override
	public void deleteUserOrganization(long userId, Organization organization) {
		_organizationLocalService.deleteUserOrganization(userId, organization);
	}

	@Override
	public void deleteUserOrganizationByEmailAddress(
			String emailAddress, long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.deleteUserOrganizationByEmailAddress(
			emailAddress, organizationId);
	}

	@Override
	public void deleteUserOrganizations(
		long userId, java.util.List<Organization> organizations) {

		_organizationLocalService.deleteUserOrganizations(
			userId, organizations);
	}

	@Override
	public void deleteUserOrganizations(long userId, long[] organizationIds) {
		_organizationLocalService.deleteUserOrganizations(
			userId, organizationIds);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _organizationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _organizationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _organizationLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _organizationLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _organizationLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _organizationLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _organizationLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _organizationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public Organization fetchOrganization(long organizationId) {
		return _organizationLocalService.fetchOrganization(organizationId);
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name, or <code>null</code> if no
	 organization could be found
	 */
	@Override
	public Organization fetchOrganization(long companyId, String name) {
		return _organizationLocalService.fetchOrganization(companyId, name);
	}

	@Override
	public Organization fetchOrganizationByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _organizationLocalService.
			fetchOrganizationByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the organization with the matching UUID and company.
	 *
	 * @param uuid the organization's UUID
	 * @param companyId the primary key of the company
	 * @return the matching organization, or <code>null</code> if a matching organization could not be found
	 */
	@Override
	public Organization fetchOrganizationByUuidAndCompanyId(
		String uuid, long companyId) {

		return _organizationLocalService.fetchOrganizationByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _organizationLocalService.getActionableDynamicQuery();
	}

	@Override
	public String[] getChildrenTypes(String type) {
		return _organizationLocalService.getChildrenTypes(type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _organizationLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<Organization> getGroupOrganizations(long groupId) {
		return _organizationLocalService.getGroupOrganizations(groupId);
	}

	@Override
	public java.util.List<Organization> getGroupOrganizations(
		long groupId, int start, int end) {

		return _organizationLocalService.getGroupOrganizations(
			groupId, start, end);
	}

	@Override
	public java.util.List<Organization> getGroupOrganizations(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationLocalService.getGroupOrganizations(
			groupId, start, end, orderByComparator);
	}

	@Override
	public int getGroupOrganizationsCount(long groupId) {
		return _organizationLocalService.getGroupOrganizationsCount(groupId);
	}

	/**
	 * Returns the groupIds of the groups associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the groupIds of groups associated with the organization
	 */
	@Override
	public long[] getGroupPrimaryKeys(long organizationId) {
		return _organizationLocalService.getGroupPrimaryKeys(organizationId);
	}

	@Override
	public java.util.List<Organization> getGroupUserOrganizations(
			long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getGroupUserOrganizations(
			groupId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _organizationLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<Organization> getNoAssetOrganizations() {
		return _organizationLocalService.getNoAssetOrganizations();
	}

	@Override
	public Organization getOrAddEmptyOrganization(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrAddEmptyOrganization(
			externalReferenceCode, companyId, userId, name);
	}

	/**
	 * Returns the organization with the primary key.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the organization
	 * @throws PortalException if a organization with the primary key could not be found
	 */
	@Override
	public Organization getOrganization(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganization(organizationId);
	}

	/**
	 * Returns the organization with the name.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param name the organization's name
	 * @return the organization with the name
	 */
	@Override
	public Organization getOrganization(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganization(companyId, name);
	}

	@Override
	public Organization getOrganizationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganizationByExternalReferenceCode(
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
	@Override
	public Organization getOrganizationByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganizationByUuidAndCompanyId(
			uuid, companyId);
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
	public long getOrganizationId(long companyId, String name) {
		return _organizationLocalService.getOrganizationId(companyId, name);
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
	@Override
	public java.util.List<Organization> getOrganizations(int start, int end) {
		return _organizationLocalService.getOrganizations(start, end);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
			long userId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<Organization>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganizations(
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
	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId) {

		return _organizationLocalService.getOrganizations(
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
	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, int start, int end) {

		return _organizationLocalService.getOrganizations(
			companyId, parentOrganizationId, start, end);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, long parentOrganizationId, String name, int start,
		int end) {

		return _organizationLocalService.getOrganizations(
			companyId, parentOrganizationId, name, start, end);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, String treePath) {

		return _organizationLocalService.getOrganizations(companyId, treePath);
	}

	@Override
	public java.util.List<Organization> getOrganizations(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationLocalService.getOrganizations(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the organizations with the primary keys.
	 *
	 * @param organizationIds the primary keys of the organizations
	 * @return the organizations with the primary keys
	 */
	@Override
	public java.util.List<Organization> getOrganizations(long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getOrganizations(organizationIds);
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
	@Override
	public java.util.List<Object> getOrganizationsAndUsers(
		long companyId, long parentOrganizationId, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<?> orderByComparator) {

		return _organizationLocalService.getOrganizationsAndUsers(
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
	@Override
	public int getOrganizationsAndUsersCount(
		long companyId, long parentOrganizationId, int status) {

		return _organizationLocalService.getOrganizationsAndUsersCount(
			companyId, parentOrganizationId, status);
	}

	@Override
	public java.util.List<Organization> getOrganizationsByLogoId(long logoId) {
		return _organizationLocalService.getOrganizationsByLogoId(logoId);
	}

	/**
	 * Returns the number of organizations.
	 *
	 * @return the number of organizations
	 */
	@Override
	public int getOrganizationsCount() {
		return _organizationLocalService.getOrganizationsCount();
	}

	/**
	 * Returns the number of organizations belonging to the parent organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param parentOrganizationId the primary key of the organization's parent
	 organization
	 * @return the number of organizations belonging to the parent organization
	 */
	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId) {

		return _organizationLocalService.getOrganizationsCount(
			companyId, parentOrganizationId);
	}

	@Override
	public int getOrganizationsCount(
		long companyId, long parentOrganizationId, String name) {

		return _organizationLocalService.getOrganizationsCount(
			companyId, parentOrganizationId, name);
	}

	@Override
	public int getOrganizationsCount(long companyId, String name) {
		return _organizationLocalService.getOrganizationsCount(companyId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _organizationLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the parent organizations in order by closest ancestor. The list
	 * starts with the organization itself.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the parent organizations in order by closest ancestor
	 */
	@Override
	public java.util.List<Organization> getParentOrganizations(
			long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getParentOrganizations(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the suborganizations of the organizations.
	 *
	 * @param organizations the organizations from which to get
	 suborganizations
	 * @return the suborganizations of the organizations
	 */
	@Override
	public java.util.List<Organization> getSuborganizations(
		java.util.List<Organization> organizations) {

		return _organizationLocalService.getSuborganizations(organizations);
	}

	/**
	 * Returns the suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the suborganizations of the organization
	 */
	@Override
	public java.util.List<Organization> getSuborganizations(
		long companyId, long organizationId) {

		return _organizationLocalService.getSuborganizations(
			companyId, organizationId);
	}

	/**
	 * Returns the count of suborganizations of the organization.
	 *
	 * @param companyId the primary key of the organization's company
	 * @param organizationId the primary key of the organization
	 * @return the count of suborganizations of the organization
	 */
	@Override
	public int getSuborganizationsCount(long companyId, long organizationId) {
		return _organizationLocalService.getSuborganizationsCount(
			companyId, organizationId);
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
	@Override
	public java.util.List<Organization> getSubsetOrganizations(
		java.util.List<Organization> allOrganizations,
		java.util.List<Organization> availableOrganizations) {

		return _organizationLocalService.getSubsetOrganizations(
			allOrganizations, availableOrganizations);
	}

	@Override
	public String[] getTypes() {
		return _organizationLocalService.getTypes();
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
	@Override
	public long[] getUserOrganizationIds(
			long userId, boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getUserOrganizationIds(
			userId, includeAdministrative);
	}

	@Override
	public java.util.List<Organization> getUserOrganizations(long userId) {
		return _organizationLocalService.getUserOrganizations(userId);
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
	@Override
	public java.util.List<Organization> getUserOrganizations(
			long userId, boolean includeAdministrative)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.getUserOrganizations(
			userId, includeAdministrative);
	}

	@Override
	public java.util.List<Organization> getUserOrganizations(
		long userId, int start, int end) {

		return _organizationLocalService.getUserOrganizations(
			userId, start, end);
	}

	@Override
	public java.util.List<Organization> getUserOrganizations(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationLocalService.getUserOrganizations(
			userId, start, end, orderByComparator);
	}

	@Override
	public int getUserOrganizationsCount(long userId) {
		return _organizationLocalService.getUserOrganizationsCount(userId);
	}

	/**
	 * Returns the userIds of the users associated with the organization.
	 *
	 * @param organizationId the organizationId of the organization
	 * @return long[] the userIds of users associated with the organization
	 */
	@Override
	public long[] getUserPrimaryKeys(long organizationId) {
		return _organizationLocalService.getUserPrimaryKeys(organizationId);
	}

	@Override
	public boolean hasGroupOrganization(long groupId, long organizationId) {
		return _organizationLocalService.hasGroupOrganization(
			groupId, organizationId);
	}

	@Override
	public boolean hasGroupOrganizations(long groupId) {
		return _organizationLocalService.hasGroupOrganizations(groupId);
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
	@Override
	public boolean hasPasswordPolicyOrganization(
		long passwordPolicyId, long organizationId) {

		return _organizationLocalService.hasPasswordPolicyOrganization(
			passwordPolicyId, organizationId);
	}

	@Override
	public boolean hasUserOrganization(long userId, long organizationId) {
		return _organizationLocalService.hasUserOrganization(
			userId, organizationId);
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
	@Override
	public boolean hasUserOrganization(
			long userId, long organizationId, boolean inheritSuborganizations,
			boolean includeSpecifiedOrganization)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.hasUserOrganization(
			userId, organizationId, inheritSuborganizations,
			includeSpecifiedOrganization);
	}

	@Override
	public boolean hasUserOrganizations(long userId) {
		return _organizationLocalService.hasUserOrganizations(userId);
	}

	@Override
	public boolean isCountryEnabled(String type) {
		return _organizationLocalService.isCountryEnabled(type);
	}

	@Override
	public boolean isCountryRequired(String type) {
		return _organizationLocalService.isCountryRequired(type);
	}

	@Override
	public boolean isRootable(String type) {
		return _organizationLocalService.isRootable(type);
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
	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.rebuildTree(companyId);
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, long parentOrganizationId, String keywords,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return _organizationLocalService.search(
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
	@Override
	public java.util.List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, int start, int end) {

		return _organizationLocalService.search(
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
	@Override
	public java.util.List<Organization> search(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationLocalService.search(
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
	@Override
	public java.util.List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end) {

		return _organizationLocalService.search(
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
	@Override
	public java.util.List<Organization> search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Organization>
			orderByComparator) {

		return _organizationLocalService.search(
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, String region, String country,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return _organizationLocalService.search(
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
	@Override
	public int searchCount(
		long companyId, long parentOrganizationId, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params) {

		return _organizationLocalService.searchCount(
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
	@Override
	public int searchCount(
		long companyId, long parentOrganizationId, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return _organizationLocalService.searchCount(
			companyId, parentOrganizationId, name, type, street, city, zip,
			regionId, countryId, params, andOperator);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<Organization>
			searchOrganizations(
				long companyId, long parentOrganizationId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.searchOrganizations(
			companyId, parentOrganizationId, keywords, params, start, end,
			sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<Organization>
			searchOrganizations(
				long companyId, long parentOrganizationId, String name,
				String type, String street, String city, String zip,
				String region, String country,
				java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.searchOrganizations(
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
	@Override
	public com.liferay.portal.kernel.search.Hits searchOrganizationsAndUsers(
			long companyId, long parentOrganizationId, String keywords,
			int status, java.util.LinkedHashMap<String, Object> params,
			int start, int end, com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.searchOrganizationsAndUsers(
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
	@Override
	public int searchOrganizationsAndUsersCount(
			long companyId, long parentOrganizationId, String keywords,
			int status, java.util.LinkedHashMap<String, Object> params)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.searchOrganizationsAndUsersCount(
			companyId, parentOrganizationId, keywords, status, params);
	}

	@Override
	public void setGroupOrganizations(long groupId, long[] organizationIds) {
		_organizationLocalService.setGroupOrganizations(
			groupId, organizationIds);
	}

	@Override
	public void setUserOrganizations(long userId, long[] organizationIds) {
		_organizationLocalService.setUserOrganizations(userId, organizationIds);
	}

	/**
	 * Removes the organizations from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetGroupOrganizations(long groupId, long[] organizationIds) {
		_organizationLocalService.unsetGroupOrganizations(
			groupId, organizationIds);
	}

	/**
	 * Removes the organizations from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param organizationIds the primary keys of the organizations
	 */
	@Override
	public void unsetPasswordPolicyOrganizations(
		long passwordPolicyId, long[] organizationIds) {

		_organizationLocalService.unsetPasswordPolicyOrganizations(
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
	@Override
	public void updateAsset(
			long userId, Organization organization, long[] assetCategoryIds,
			String[] assetTagNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		_organizationLocalService.updateAsset(
			userId, organization, assetCategoryIds, assetTagNames);
	}

	@Override
	public Organization updateLogo(long organizationId, byte[] logoBytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.updateLogo(organizationId, logoBytes);
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
	@Override
	public Organization updateOrganization(Organization organization) {
		return _organizationLocalService.updateOrganization(organization);
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
	@Override
	public Organization updateOrganization(
			String externalReferenceCode, long companyId, long organizationId,
			long parentOrganizationId, String name, String type, long regionId,
			long countryId, long statusListTypeId, String comments,
			boolean hasLogo, byte[] logoBytes, boolean site,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _organizationLocalService.updateOrganization(
			externalReferenceCode, companyId, organizationId,
			parentOrganizationId, name, type, regionId, countryId,
			statusListTypeId, comments, hasLogo, logoBytes, site,
			serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _organizationLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Organization> getCTPersistence() {
		return _organizationLocalService.getCTPersistence();
	}

	@Override
	public Class<Organization> getModelClass() {
		return _organizationLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Organization>, R, E>
				updateUnsafeFunction)
		throws E {

		return _organizationLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public OrganizationLocalService getWrappedService() {
		return _organizationLocalService;
	}

	@Override
	public void setWrappedService(
		OrganizationLocalService organizationLocalService) {

		_organizationLocalService = organizationLocalService;
	}

	private OrganizationLocalService _organizationLocalService;

}
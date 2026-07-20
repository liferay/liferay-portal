/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PasswordPolicyLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PasswordPolicyLocalService
 * @generated
 */
public class PasswordPolicyLocalServiceWrapper
	implements PasswordPolicyLocalService,
			   ServiceWrapper<PasswordPolicyLocalService> {

	public PasswordPolicyLocalServiceWrapper() {
		this(null);
	}

	public PasswordPolicyLocalServiceWrapper(
		PasswordPolicyLocalService passwordPolicyLocalService) {

		_passwordPolicyLocalService = passwordPolicyLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy addPasswordPolicy(
			long userId, boolean defaultPolicy, String name, String description,
			boolean changeable, boolean changeRequired, long minAge,
			boolean checkSyntax, boolean allowDictionaryWords,
			int minAlphanumeric, int minLength, int minLowerCase,
			int minNumbers, int minSymbols, int minUpperCase, String regex,
			boolean history, int historyCount, boolean expireable, long maxAge,
			long warningTime, int graceLimit, boolean lockout, int maxFailure,
			long lockoutDuration, long resetFailureCount,
			long resetTicketMaxAge, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.addPasswordPolicy(
			userId, defaultPolicy, name, description, changeable,
			changeRequired, minAge, checkSyntax, allowDictionaryWords,
			minAlphanumeric, minLength, minLowerCase, minNumbers, minSymbols,
			minUpperCase, regex, history, historyCount, expireable, maxAge,
			warningTime, graceLimit, lockout, maxFailure, lockoutDuration,
			resetFailureCount, resetTicketMaxAge, serviceContext);
	}

	/**
	 * Adds the password policy to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicy the password policy
	 * @return the password policy that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy addPasswordPolicy(
		com.liferay.portal.kernel.model.PasswordPolicy passwordPolicy) {

		return _passwordPolicyLocalService.addPasswordPolicy(passwordPolicy);
	}

	@Override
	public void checkDefaultPasswordPolicy(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_passwordPolicyLocalService.checkDefaultPasswordPolicy(companyId);
	}

	/**
	 * Creates a new password policy with the primary key. Does not add the password policy to the database.
	 *
	 * @param passwordPolicyId the primary key for the new password policy
	 * @return the new password policy
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy createPasswordPolicy(
		long passwordPolicyId) {

		return _passwordPolicyLocalService.createPasswordPolicy(
			passwordPolicyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteNondefaultPasswordPolicies(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_passwordPolicyLocalService.deleteNondefaultPasswordPolicies(companyId);
	}

	/**
	 * Deletes the password policy with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy that was removed
	 * @throws PortalException if a password policy with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy deletePasswordPolicy(
			long passwordPolicyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.deletePasswordPolicy(
			passwordPolicyId);
	}

	/**
	 * Deletes the password policy from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicy the password policy
	 * @return the password policy that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy deletePasswordPolicy(
			com.liferay.portal.kernel.model.PasswordPolicy passwordPolicy)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.deletePasswordPolicy(passwordPolicy);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _passwordPolicyLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _passwordPolicyLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _passwordPolicyLocalService.dynamicQuery();
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

		return _passwordPolicyLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyModelImpl</code>.
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

		return _passwordPolicyLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyModelImpl</code>.
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

		return _passwordPolicyLocalService.dynamicQuery(
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

		return _passwordPolicyLocalService.dynamicQueryCount(dynamicQuery);
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

		return _passwordPolicyLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
		fetchDefaultPasswordPolicy(long companyId) {

		return _passwordPolicyLocalService.fetchDefaultPasswordPolicy(
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy fetchPasswordPolicy(
		long passwordPolicyId) {

		return _passwordPolicyLocalService.fetchPasswordPolicy(
			passwordPolicyId);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy fetchPasswordPolicy(
		long companyId, String name) {

		return _passwordPolicyLocalService.fetchPasswordPolicy(companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			fetchPasswordPolicyByUser(com.liferay.portal.kernel.model.User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.fetchPasswordPolicyByUser(user);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			fetchPasswordPolicyByUserId(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.fetchPasswordPolicyByUserId(userId);
	}

	/**
	 * Returns the password policy with the matching UUID and company.
	 *
	 * @param uuid the password policy's UUID
	 * @param companyId the primary key of the company
	 * @return the matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
		fetchPasswordPolicyByUuidAndCompanyId(String uuid, long companyId) {

		return _passwordPolicyLocalService.
			fetchPasswordPolicyByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _passwordPolicyLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			getDefaultPasswordPolicy(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _passwordPolicyLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _passwordPolicyLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _passwordPolicyLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns a range of all the password policies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of password policies
	 * @param end the upper bound of the range of password policies (not inclusive)
	 * @return the range of password policies
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.PasswordPolicy>
		getPasswordPolicies(int start, int end) {

		return _passwordPolicyLocalService.getPasswordPolicies(start, end);
	}

	/**
	 * Returns the number of password policies.
	 *
	 * @return the number of password policies
	 */
	@Override
	public int getPasswordPoliciesCount() {
		return _passwordPolicyLocalService.getPasswordPoliciesCount();
	}

	/**
	 * Returns the password policy with the primary key.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy
	 * @throws PortalException if a password policy with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy getPasswordPolicy(
			long passwordPolicyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPasswordPolicy(passwordPolicyId);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy getPasswordPolicy(
			long companyId, long[] organizationIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPasswordPolicy(
			companyId, organizationIds);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			getPasswordPolicyByUser(com.liferay.portal.kernel.model.User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPasswordPolicyByUser(user);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			getPasswordPolicyByUserId(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPasswordPolicyByUserId(userId);
	}

	/**
	 * Returns the password policy with the matching UUID and company.
	 *
	 * @param uuid the password policy's UUID
	 * @param companyId the primary key of the company
	 * @return the matching password policy
	 * @throws PortalException if a matching password policy could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy
			getPasswordPolicyByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPasswordPolicyByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.PasswordPolicy>
		search(
			long companyId, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.PasswordPolicy>
					orderByComparator) {

		return _passwordPolicyLocalService.search(
			companyId, name, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long companyId, String name) {
		return _passwordPolicyLocalService.searchCount(companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy updatePasswordPolicy(
			long passwordPolicyId, String name, String description,
			boolean changeable, boolean changeRequired, long minAge,
			boolean checkSyntax, boolean allowDictionaryWords,
			int minAlphanumeric, int minLength, int minLowerCase,
			int minNumbers, int minSymbols, int minUpperCase, String regex,
			boolean history, int historyCount, boolean expireable, long maxAge,
			long warningTime, int graceLimit, boolean lockout, int maxFailure,
			long lockoutDuration, long resetFailureCount,
			long resetTicketMaxAge, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyLocalService.updatePasswordPolicy(
			passwordPolicyId, name, description, changeable, changeRequired,
			minAge, checkSyntax, allowDictionaryWords, minAlphanumeric,
			minLength, minLowerCase, minNumbers, minSymbols, minUpperCase,
			regex, history, historyCount, expireable, maxAge, warningTime,
			graceLimit, lockout, maxFailure, lockoutDuration, resetFailureCount,
			resetTicketMaxAge, serviceContext);
	}

	/**
	 * Updates the password policy in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicy the password policy
	 * @return the password policy that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicy updatePasswordPolicy(
		com.liferay.portal.kernel.model.PasswordPolicy passwordPolicy) {

		return _passwordPolicyLocalService.updatePasswordPolicy(passwordPolicy);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _passwordPolicyLocalService.getBasePersistence();
	}

	@Override
	public PasswordPolicyLocalService getWrappedService() {
		return _passwordPolicyLocalService;
	}

	@Override
	public void setWrappedService(
		PasswordPolicyLocalService passwordPolicyLocalService) {

		_passwordPolicyLocalService = passwordPolicyLocalService;
	}

	private PasswordPolicyLocalService _passwordPolicyLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:282024609
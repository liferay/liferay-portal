/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PasswordPolicy. This utility wraps
 * <code>com.liferay.portal.service.impl.PasswordPolicyLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PasswordPolicyLocalService
 * @generated
 */
public class PasswordPolicyLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PasswordPolicyLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PasswordPolicy addPasswordPolicy(
			long userId, boolean defaultPolicy, String name, String description,
			boolean changeable, boolean changeRequired, long minAge,
			boolean checkSyntax, boolean allowDictionaryWords,
			int minAlphanumeric, int minLength, int minLowerCase,
			int minNumbers, int minSymbols, int minUpperCase, String regex,
			boolean history, int historyCount, boolean expireable, long maxAge,
			long warningTime, int graceLimit, boolean lockout, int maxFailure,
			long lockoutDuration, long resetFailureCount,
			long resetTicketMaxAge, ServiceContext serviceContext)
		throws PortalException {

		return getService().addPasswordPolicy(
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
	public static PasswordPolicy addPasswordPolicy(
		PasswordPolicy passwordPolicy) {

		return getService().addPasswordPolicy(passwordPolicy);
	}

	public static void checkDefaultPasswordPolicy(long companyId)
		throws PortalException {

		getService().checkDefaultPasswordPolicy(companyId);
	}

	/**
	 * Creates a new password policy with the primary key. Does not add the password policy to the database.
	 *
	 * @param passwordPolicyId the primary key for the new password policy
	 * @return the new password policy
	 */
	public static PasswordPolicy createPasswordPolicy(long passwordPolicyId) {
		return getService().createPasswordPolicy(passwordPolicyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteNondefaultPasswordPolicies(long companyId)
		throws PortalException {

		getService().deleteNondefaultPasswordPolicies(companyId);
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
	public static PasswordPolicy deletePasswordPolicy(long passwordPolicyId)
		throws PortalException {

		return getService().deletePasswordPolicy(passwordPolicyId);
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
	public static PasswordPolicy deletePasswordPolicy(
			PasswordPolicy passwordPolicy)
		throws PortalException {

		return getService().deletePasswordPolicy(passwordPolicy);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyModelImpl</code>.
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

	public static PasswordPolicy fetchDefaultPasswordPolicy(long companyId) {
		return getService().fetchDefaultPasswordPolicy(companyId);
	}

	public static PasswordPolicy fetchPasswordPolicy(long passwordPolicyId) {
		return getService().fetchPasswordPolicy(passwordPolicyId);
	}

	public static PasswordPolicy fetchPasswordPolicy(
		long companyId, String name) {

		return getService().fetchPasswordPolicy(companyId, name);
	}

	public static PasswordPolicy fetchPasswordPolicyByUser(
			com.liferay.portal.kernel.model.User user)
		throws PortalException {

		return getService().fetchPasswordPolicyByUser(user);
	}

	public static PasswordPolicy fetchPasswordPolicyByUserId(long userId)
		throws PortalException {

		return getService().fetchPasswordPolicyByUserId(userId);
	}

	/**
	 * Returns the password policy with the matching UUID and company.
	 *
	 * @param uuid the password policy's UUID
	 * @param companyId the primary key of the company
	 * @return the matching password policy, or <code>null</code> if a matching password policy could not be found
	 */
	public static PasswordPolicy fetchPasswordPolicyByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchPasswordPolicyByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static PasswordPolicy getDefaultPasswordPolicy(long companyId)
		throws PortalException {

		return getService().getDefaultPasswordPolicy(companyId);
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
	public static List<PasswordPolicy> getPasswordPolicies(int start, int end) {
		return getService().getPasswordPolicies(start, end);
	}

	/**
	 * Returns the number of password policies.
	 *
	 * @return the number of password policies
	 */
	public static int getPasswordPoliciesCount() {
		return getService().getPasswordPoliciesCount();
	}

	/**
	 * Returns the password policy with the primary key.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @return the password policy
	 * @throws PortalException if a password policy with the primary key could not be found
	 */
	public static PasswordPolicy getPasswordPolicy(long passwordPolicyId)
		throws PortalException {

		return getService().getPasswordPolicy(passwordPolicyId);
	}

	public static PasswordPolicy getPasswordPolicy(
			long companyId, long[] organizationIds)
		throws PortalException {

		return getService().getPasswordPolicy(companyId, organizationIds);
	}

	public static PasswordPolicy getPasswordPolicyByUser(
			com.liferay.portal.kernel.model.User user)
		throws PortalException {

		return getService().getPasswordPolicyByUser(user);
	}

	public static PasswordPolicy getPasswordPolicyByUserId(long userId)
		throws PortalException {

		return getService().getPasswordPolicyByUserId(userId);
	}

	/**
	 * Returns the password policy with the matching UUID and company.
	 *
	 * @param uuid the password policy's UUID
	 * @param companyId the primary key of the company
	 * @return the matching password policy
	 * @throws PortalException if a matching password policy could not be found
	 */
	public static PasswordPolicy getPasswordPolicyByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getPasswordPolicyByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<PasswordPolicy> search(
		long companyId, String name, int start, int end,
		OrderByComparator<PasswordPolicy> orderByComparator) {

		return getService().search(
			companyId, name, start, end, orderByComparator);
	}

	public static int searchCount(long companyId, String name) {
		return getService().searchCount(companyId, name);
	}

	public static PasswordPolicy updatePasswordPolicy(
			long passwordPolicyId, String name, String description,
			boolean changeable, boolean changeRequired, long minAge,
			boolean checkSyntax, boolean allowDictionaryWords,
			int minAlphanumeric, int minLength, int minLowerCase,
			int minNumbers, int minSymbols, int minUpperCase, String regex,
			boolean history, int historyCount, boolean expireable, long maxAge,
			long warningTime, int graceLimit, boolean lockout, int maxFailure,
			long lockoutDuration, long resetFailureCount,
			long resetTicketMaxAge, ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePasswordPolicy(
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
	public static PasswordPolicy updatePasswordPolicy(
		PasswordPolicy passwordPolicy) {

		return getService().updatePasswordPolicy(passwordPolicy);
	}

	public static PasswordPolicyLocalService getService() {
		return _service;
	}

	public static void setService(PasswordPolicyLocalService service) {
		_service = service;
	}

	private static volatile PasswordPolicyLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1524807190
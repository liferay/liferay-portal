/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PasswordPolicyRel. This utility wraps
 * <code>com.liferay.portal.service.impl.PasswordPolicyRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PasswordPolicyRelLocalService
 * @generated
 */
public class PasswordPolicyRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PasswordPolicyRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PasswordPolicyRel addPasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		return getService().addPasswordPolicyRel(
			passwordPolicyId, className, classPK);
	}

	/**
	 * Adds the password policy rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicyRel the password policy rel
	 * @return the password policy rel that was added
	 */
	public static PasswordPolicyRel addPasswordPolicyRel(
		PasswordPolicyRel passwordPolicyRel) {

		return getService().addPasswordPolicyRel(passwordPolicyRel);
	}

	public static void addPasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		getService().addPasswordPolicyRels(
			passwordPolicyId, className, classPKs);
	}

	/**
	 * Creates a new password policy rel with the primary key. Does not add the password policy rel to the database.
	 *
	 * @param passwordPolicyRelId the primary key for the new password policy rel
	 * @return the new password policy rel
	 */
	public static PasswordPolicyRel createPasswordPolicyRel(
		long passwordPolicyRelId) {

		return getService().createPasswordPolicyRel(passwordPolicyRelId);
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
	 * Deletes the password policy rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel that was removed
	 * @throws PortalException if a password policy rel with the primary key could not be found
	 */
	public static PasswordPolicyRel deletePasswordPolicyRel(
			long passwordPolicyRelId)
		throws PortalException {

		return getService().deletePasswordPolicyRel(passwordPolicyRelId);
	}

	public static void deletePasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		getService().deletePasswordPolicyRel(
			passwordPolicyId, className, classPK);
	}

	/**
	 * Deletes the password policy rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicyRel the password policy rel
	 * @return the password policy rel that was removed
	 */
	public static PasswordPolicyRel deletePasswordPolicyRel(
		PasswordPolicyRel passwordPolicyRel) {

		return getService().deletePasswordPolicyRel(passwordPolicyRel);
	}

	public static void deletePasswordPolicyRel(String className, long classPK) {
		getService().deletePasswordPolicyRel(className, classPK);
	}

	public static void deletePasswordPolicyRels(long passwordPolicyId) {
		getService().deletePasswordPolicyRels(passwordPolicyId);
	}

	public static void deletePasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		getService().deletePasswordPolicyRels(
			passwordPolicyId, className, classPKs);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyRelModelImpl</code>.
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

	public static PasswordPolicyRel fetchPasswordPolicyRel(
		long passwordPolicyRelId) {

		return getService().fetchPasswordPolicyRel(passwordPolicyRelId);
	}

	public static PasswordPolicyRel fetchPasswordPolicyRel(
		String className, long classPK) {

		return getService().fetchPasswordPolicyRel(className, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the password policy rel with the primary key.
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel
	 * @throws PortalException if a password policy rel with the primary key could not be found
	 */
	public static PasswordPolicyRel getPasswordPolicyRel(
			long passwordPolicyRelId)
		throws PortalException {

		return getService().getPasswordPolicyRel(passwordPolicyRelId);
	}

	/**
	 * Returns a range of all the password policy rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordPolicyRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of password policy rels
	 * @param end the upper bound of the range of password policy rels (not inclusive)
	 * @return the range of password policy rels
	 */
	public static List<PasswordPolicyRel> getPasswordPolicyRels(
		int start, int end) {

		return getService().getPasswordPolicyRels(start, end);
	}

	/**
	 * Returns the number of password policy rels.
	 *
	 * @return the number of password policy rels
	 */
	public static int getPasswordPolicyRelsCount() {
		return getService().getPasswordPolicyRelsCount();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasPasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		return getService().hasPasswordPolicyRel(
			passwordPolicyId, className, classPK);
	}

	/**
	 * Updates the password policy rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PasswordPolicyRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param passwordPolicyRel the password policy rel
	 * @return the password policy rel that was updated
	 */
	public static PasswordPolicyRel updatePasswordPolicyRel(
		PasswordPolicyRel passwordPolicyRel) {

		return getService().updatePasswordPolicyRel(passwordPolicyRel);
	}

	public static PasswordPolicyRelLocalService getService() {
		return _service;
	}

	public static void setService(PasswordPolicyRelLocalService service) {
		_service = service;
	}

	private static volatile PasswordPolicyRelLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1149313670
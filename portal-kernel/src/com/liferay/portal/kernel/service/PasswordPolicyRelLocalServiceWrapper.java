/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PasswordPolicyRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PasswordPolicyRelLocalService
 * @generated
 */
public class PasswordPolicyRelLocalServiceWrapper
	implements PasswordPolicyRelLocalService,
			   ServiceWrapper<PasswordPolicyRelLocalService> {

	public PasswordPolicyRelLocalServiceWrapper() {
		this(null);
	}

	public PasswordPolicyRelLocalServiceWrapper(
		PasswordPolicyRelLocalService passwordPolicyRelLocalService) {

		_passwordPolicyRelLocalService = passwordPolicyRelLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		addPasswordPolicyRel(
			long passwordPolicyId, String className, long classPK) {

		return _passwordPolicyRelLocalService.addPasswordPolicyRel(
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
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		addPasswordPolicyRel(
			com.liferay.portal.kernel.model.PasswordPolicyRel
				passwordPolicyRel) {

		return _passwordPolicyRelLocalService.addPasswordPolicyRel(
			passwordPolicyRel);
	}

	@Override
	public void addPasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		_passwordPolicyRelLocalService.addPasswordPolicyRels(
			passwordPolicyId, className, classPKs);
	}

	/**
	 * Creates a new password policy rel with the primary key. Does not add the password policy rel to the database.
	 *
	 * @param passwordPolicyRelId the primary key for the new password policy rel
	 * @return the new password policy rel
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		createPasswordPolicyRel(long passwordPolicyRelId) {

		return _passwordPolicyRelLocalService.createPasswordPolicyRel(
			passwordPolicyRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
			deletePasswordPolicyRel(long passwordPolicyRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyRelLocalService.deletePasswordPolicyRel(
			passwordPolicyRelId);
	}

	@Override
	public void deletePasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		_passwordPolicyRelLocalService.deletePasswordPolicyRel(
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
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		deletePasswordPolicyRel(
			com.liferay.portal.kernel.model.PasswordPolicyRel
				passwordPolicyRel) {

		return _passwordPolicyRelLocalService.deletePasswordPolicyRel(
			passwordPolicyRel);
	}

	@Override
	public void deletePasswordPolicyRel(String className, long classPK) {
		_passwordPolicyRelLocalService.deletePasswordPolicyRel(
			className, classPK);
	}

	@Override
	public void deletePasswordPolicyRels(long passwordPolicyId) {
		_passwordPolicyRelLocalService.deletePasswordPolicyRels(
			passwordPolicyId);
	}

	@Override
	public void deletePasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		_passwordPolicyRelLocalService.deletePasswordPolicyRels(
			passwordPolicyId, className, classPKs);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _passwordPolicyRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _passwordPolicyRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _passwordPolicyRelLocalService.dynamicQuery();
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

		return _passwordPolicyRelLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _passwordPolicyRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _passwordPolicyRelLocalService.dynamicQuery(
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

		return _passwordPolicyRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _passwordPolicyRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		fetchPasswordPolicyRel(long passwordPolicyRelId) {

		return _passwordPolicyRelLocalService.fetchPasswordPolicyRel(
			passwordPolicyRelId);
	}

	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		fetchPasswordPolicyRel(String className, long classPK) {

		return _passwordPolicyRelLocalService.fetchPasswordPolicyRel(
			className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _passwordPolicyRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _passwordPolicyRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _passwordPolicyRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the password policy rel with the primary key.
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel
	 * @throws PortalException if a password policy rel with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
			getPasswordPolicyRel(long passwordPolicyRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyRelLocalService.getPasswordPolicyRel(
			passwordPolicyRelId);
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
	@Override
	public java.util.List<com.liferay.portal.kernel.model.PasswordPolicyRel>
		getPasswordPolicyRels(int start, int end) {

		return _passwordPolicyRelLocalService.getPasswordPolicyRels(start, end);
	}

	/**
	 * Returns the number of password policy rels.
	 *
	 * @return the number of password policy rels
	 */
	@Override
	public int getPasswordPolicyRelsCount() {
		return _passwordPolicyRelLocalService.getPasswordPolicyRelsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _passwordPolicyRelLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasPasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		return _passwordPolicyRelLocalService.hasPasswordPolicyRel(
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
	@Override
	public com.liferay.portal.kernel.model.PasswordPolicyRel
		updatePasswordPolicyRel(
			com.liferay.portal.kernel.model.PasswordPolicyRel
				passwordPolicyRel) {

		return _passwordPolicyRelLocalService.updatePasswordPolicyRel(
			passwordPolicyRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _passwordPolicyRelLocalService.getBasePersistence();
	}

	@Override
	public PasswordPolicyRelLocalService getWrappedService() {
		return _passwordPolicyRelLocalService;
	}

	@Override
	public void setWrappedService(
		PasswordPolicyRelLocalService passwordPolicyRelLocalService) {

		_passwordPolicyRelLocalService = passwordPolicyRelLocalService;
	}

	private PasswordPolicyRelLocalService _passwordPolicyRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1934008242
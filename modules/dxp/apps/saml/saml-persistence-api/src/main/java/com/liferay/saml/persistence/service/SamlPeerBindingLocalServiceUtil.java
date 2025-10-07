/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.saml.persistence.model.SamlPeerBinding;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlPeerBinding. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlPeerBindingLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlPeerBindingLocalService
 * @generated
 */
public class SamlPeerBindingLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlPeerBindingLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SamlPeerBinding addSamlPeerBinding(
			long userId, String samlPeerEntityId, String samlNameIdFormat,
			String samlNameIdNameQualifier, String samlNameIdSpNameQualifier,
			String samlNameIdSpProvidedId, String samlNameIdValue)
		throws PortalException {

		return getService().addSamlPeerBinding(
			userId, samlPeerEntityId, samlNameIdFormat, samlNameIdNameQualifier,
			samlNameIdSpNameQualifier, samlNameIdSpProvidedId, samlNameIdValue);
	}

	/**
	 * Adds the saml peer binding to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was added
	 */
	public static SamlPeerBinding addSamlPeerBinding(
		SamlPeerBinding samlPeerBinding) {

		return getService().addSamlPeerBinding(samlPeerBinding);
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
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	public static SamlPeerBinding createSamlPeerBinding(
		long samlPeerBindingId) {

		return getService().createSamlPeerBinding(samlPeerBindingId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws PortalException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding deleteSamlPeerBinding(long samlPeerBindingId)
		throws PortalException {

		return getService().deleteSamlPeerBinding(samlPeerBindingId);
	}

	/**
	 * Deletes the saml peer binding from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was removed
	 */
	public static SamlPeerBinding deleteSamlPeerBinding(
		SamlPeerBinding samlPeerBinding) {

		return getService().deleteSamlPeerBinding(samlPeerBinding);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
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

	public static SamlPeerBinding fetchSamlPeerBinding(long samlPeerBindingId) {
		return getService().fetchSamlPeerBinding(samlPeerBindingId);
	}

	public static SamlPeerBinding fetchSamlPeerBinding(
		long companyId, String samlPeerEntityId, boolean deleted,
		String samlNameIdFormat, String samlNameIdNameQualifier,
		String samlNameIdValue) {

		return getService().fetchSamlPeerBinding(
			companyId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier, samlNameIdValue);
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the saml peer binding with the primary key.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws PortalException if a saml peer binding with the primary key could not be found
	 */
	public static SamlPeerBinding getSamlPeerBinding(long samlPeerBindingId)
		throws PortalException {

		return getService().getSamlPeerBinding(samlPeerBindingId);
	}

	/**
	 * Returns a range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of saml peer bindings
	 */
	public static List<SamlPeerBinding> getSamlPeerBindings(
		int start, int end) {

		return getService().getSamlPeerBindings(start, end);
	}

	public static List<SamlPeerBinding> getSamlPeerBindings(
		long companyId, String samlPeerEntityId, boolean deleted,
		String samlNameIdFormat, String samlNameIdNameQualifier,
		String samlNameIdValue) {

		return getService().getSamlPeerBindings(
			companyId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier, samlNameIdValue);
	}

	/**
	 * Returns the number of saml peer bindings.
	 *
	 * @return the number of saml peer bindings
	 */
	public static int getSamlPeerBindingsCount() {
		return getService().getSamlPeerBindingsCount();
	}

	public static List<SamlPeerBinding> getUserSamlPeerBindings(
			long userId, String samlPeerEntityId, boolean deleted,
			String samlNameIdFormat, String samlNameIdNameQualifier)
		throws PortalException {

		return getService().getUserSamlPeerBindings(
			userId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier);
	}

	/**
	 * Updates the saml peer binding in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was updated
	 */
	public static SamlPeerBinding updateSamlPeerBinding(
		SamlPeerBinding samlPeerBinding) {

		return getService().updateSamlPeerBinding(samlPeerBinding);
	}

	public static SamlPeerBindingLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlPeerBindingLocalService>
		_serviceSnapshot = new Snapshot<>(
			SamlPeerBindingLocalServiceUtil.class,
			SamlPeerBindingLocalService.class);

}
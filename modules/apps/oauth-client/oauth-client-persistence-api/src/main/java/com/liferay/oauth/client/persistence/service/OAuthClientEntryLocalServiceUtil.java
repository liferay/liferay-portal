/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OAuthClientEntry. This utility wraps
 * <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntryLocalService
 * @generated
 */
public class OAuthClientEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuthClientEntry addOAuthClientEntry(
			long userId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		return getService().addOAuthClientEntry(
			userId, authRequestParametersJSON, authServerWellKnownURI,
			customClaimsJSON, infoJSON, metadataCacheTime,
			oidcUserInfoMapperJSON, tokenRequestParametersJSON);
	}

	/**
	 * Adds the o auth client entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was added
	 */
	public static OAuthClientEntry addOAuthClientEntry(
		OAuthClientEntry oAuthClientEntry) {

		return getService().addOAuthClientEntry(oAuthClientEntry);
	}

	/**
	 * Creates a new o auth client entry with the primary key. Does not add the o auth client entry to the database.
	 *
	 * @param oAuthClientEntryId the primary key for the new o auth client entry
	 * @return the new o auth client entry
	 */
	public static OAuthClientEntry createOAuthClientEntry(
		long oAuthClientEntryId) {

		return getService().createOAuthClientEntry(oAuthClientEntryId);
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
	 * Deletes the o auth client entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws PortalException if a o auth client entry with the primary key could not be found
	 */
	public static OAuthClientEntry deleteOAuthClientEntry(
			long oAuthClientEntryId)
		throws PortalException {

		return getService().deleteOAuthClientEntry(oAuthClientEntryId);
	}

	public static OAuthClientEntry deleteOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		return getService().deleteOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Deletes the o auth client entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws PortalException
	 */
	public static OAuthClientEntry deleteOAuthClientEntry(
			OAuthClientEntry oAuthClientEntry)
		throws PortalException {

		return getService().deleteOAuthClientEntry(oAuthClientEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
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

	public static OAuthClientEntry fetchOAuthClientEntry(
		long oAuthClientEntryId) {

		return getService().fetchOAuthClientEntry(oAuthClientEntryId);
	}

	public static OAuthClientEntry fetchOAuthClientEntry(
		long companyId, String authServerWellKnownURI, String clientId) {

		return getService().fetchOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<OAuthClientEntry>
		getAuthServerWellKnownURISuffixOAuthClientEntries(
			long companyId, String authServerWellKnownURISuffix) {

		return getService().getAuthServerWellKnownURISuffixOAuthClientEntries(
			companyId, authServerWellKnownURISuffix);
	}

	public static List<OAuthClientEntry> getCompanyOAuthClientEntries(
		long companyId) {

		return getService().getCompanyOAuthClientEntries(companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the o auth client entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of o auth client entries
	 */
	public static List<OAuthClientEntry> getOAuthClientEntries(
		int start, int end) {

		return getService().getOAuthClientEntries(start, end);
	}

	/**
	 * Returns the number of o auth client entries.
	 *
	 * @return the number of o auth client entries
	 */
	public static int getOAuthClientEntriesCount() {
		return getService().getOAuthClientEntriesCount();
	}

	/**
	 * Returns the o auth client entry with the primary key.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry
	 * @throws PortalException if a o auth client entry with the primary key could not be found
	 */
	public static OAuthClientEntry getOAuthClientEntry(long oAuthClientEntryId)
		throws PortalException {

		return getService().getOAuthClientEntry(oAuthClientEntryId);
	}

	public static OAuthClientEntry getOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		return getService().getOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
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

	public static List<OAuthClientEntry> getUserOAuthClientEntries(
		long userId) {

		return getService().getUserOAuthClientEntries(userId);
	}

	public static OAuthClientEntry updateOAuthClientEntry(
			long oAuthClientEntryId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		return getService().updateOAuthClientEntry(
			oAuthClientEntryId, authRequestParametersJSON,
			authServerWellKnownURI, customClaimsJSON, infoJSON,
			metadataCacheTime, oidcUserInfoMapperJSON,
			tokenRequestParametersJSON);
	}

	/**
	 * Updates the o auth client entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was updated
	 */
	public static OAuthClientEntry updateOAuthClientEntry(
		OAuthClientEntry oAuthClientEntry) {

		return getService().updateOAuthClientEntry(oAuthClientEntry);
	}

	public static OAuthClientEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuthClientEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			OAuthClientEntryLocalServiceUtil.class,
			OAuthClientEntryLocalService.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.saml.persistence.model.SamlIbSloMessage;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the saml ib slo message service. This utility wraps <code>com.liferay.saml.persistence.service.persistence.impl.SamlIbSloMessagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessagePersistence
 * @generated
 */
public class SamlIbSloMessageUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(SamlIbSloMessage samlIbSloMessage) {
		getPersistence().clearCache(samlIbSloMessage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, SamlIbSloMessage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SamlIbSloMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SamlIbSloMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SamlIbSloMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SamlIbSloMessage> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SamlIbSloMessage update(SamlIbSloMessage samlIbSloMessage) {
		return getPersistence().update(samlIbSloMessage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SamlIbSloMessage update(
		SamlIbSloMessage samlIbSloMessage, ServiceContext serviceContext) {

		return getPersistence().update(samlIbSloMessage, serviceContext);
	}

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message
	 * @throws NoSuchIbSloMessageException if a matching saml ib slo message could not be found
	 */
	public static SamlIbSloMessage findBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws com.liferay.saml.persistence.exception.
			NoSuchIbSloMessageException {

		return getPersistence().findBySamlIdpSessionIndex(samlIdpSessionIndex);
	}

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	public static SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex) {

		return getPersistence().fetchBySamlIdpSessionIndex(samlIdpSessionIndex);
	}

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	public static SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex, boolean useFinderCache) {

		return getPersistence().fetchBySamlIdpSessionIndex(
			samlIdpSessionIndex, useFinderCache);
	}

	/**
	 * Removes the saml ib slo message where samlIdpSessionIndex = &#63; from the database.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the saml ib slo message that was removed
	 */
	public static SamlIbSloMessage removeBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws com.liferay.saml.persistence.exception.
			NoSuchIbSloMessageException {

		return getPersistence().removeBySamlIdpSessionIndex(
			samlIdpSessionIndex);
	}

	/**
	 * Returns the number of saml ib slo messages where samlIdpSessionIndex = &#63;.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the number of matching saml ib slo messages
	 */
	public static int countBySamlIdpSessionIndex(String samlIdpSessionIndex) {
		return getPersistence().countBySamlIdpSessionIndex(samlIdpSessionIndex);
	}

	/**
	 * Caches the saml ib slo message in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 */
	public static void cacheResult(SamlIbSloMessage samlIbSloMessage) {
		getPersistence().cacheResult(samlIbSloMessage);
	}

	/**
	 * Caches the saml ib slo messages in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessages the saml ib slo messages
	 */
	public static void cacheResult(List<SamlIbSloMessage> samlIbSloMessages) {
		getPersistence().cacheResult(samlIbSloMessages);
	}

	/**
	 * Creates a new saml ib slo message with the primary key. Does not add the saml ib slo message to the database.
	 *
	 * @param samlIbSloMessageId the primary key for the new saml ib slo message
	 * @return the new saml ib slo message
	 */
	public static SamlIbSloMessage create(long samlIbSloMessageId) {
		return getPersistence().create(samlIbSloMessageId);
	}

	/**
	 * Removes the saml ib slo message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message that was removed
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	public static SamlIbSloMessage remove(long samlIbSloMessageId)
		throws com.liferay.saml.persistence.exception.
			NoSuchIbSloMessageException {

		return getPersistence().remove(samlIbSloMessageId);
	}

	public static SamlIbSloMessage updateImpl(
		SamlIbSloMessage samlIbSloMessage) {

		return getPersistence().updateImpl(samlIbSloMessage);
	}

	/**
	 * Returns the saml ib slo message with the primary key or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	public static SamlIbSloMessage findByPrimaryKey(long samlIbSloMessageId)
		throws com.liferay.saml.persistence.exception.
			NoSuchIbSloMessageException {

		return getPersistence().findByPrimaryKey(samlIbSloMessageId);
	}

	/**
	 * Returns the saml ib slo message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message, or <code>null</code> if a saml ib slo message with the primary key could not be found
	 */
	public static SamlIbSloMessage fetchByPrimaryKey(long samlIbSloMessageId) {
		return getPersistence().fetchByPrimaryKey(samlIbSloMessageId);
	}

	/**
	 * Returns all the saml ib slo messages.
	 *
	 * @return the saml ib slo messages
	 */
	public static List<SamlIbSloMessage> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @return the range of saml ib slo messages
	 */
	public static List<SamlIbSloMessage> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of saml ib slo messages
	 */
	public static List<SamlIbSloMessage> findAll(
		int start, int end,
		OrderByComparator<SamlIbSloMessage> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of saml ib slo messages
	 */
	public static List<SamlIbSloMessage> findAll(
		int start, int end,
		OrderByComparator<SamlIbSloMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the saml ib slo messages from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of saml ib slo messages.
	 *
	 * @return the number of saml ib slo messages
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SamlIbSloMessagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(SamlIbSloMessagePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile SamlIbSloMessagePersistence _persistence;

}
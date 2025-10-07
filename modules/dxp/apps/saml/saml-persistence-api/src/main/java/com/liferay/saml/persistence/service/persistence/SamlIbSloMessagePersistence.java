/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.saml.persistence.exception.NoSuchIbSloMessageException;
import com.liferay.saml.persistence.model.SamlIbSloMessage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the saml ib slo message service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessageUtil
 * @generated
 */
@ProviderType
public interface SamlIbSloMessagePersistence
	extends BasePersistence<SamlIbSloMessage> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SamlIbSloMessageUtil} to access the saml ib slo message persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message
	 * @throws NoSuchIbSloMessageException if a matching saml ib slo message could not be found
	 */
	public SamlIbSloMessage findBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws NoSuchIbSloMessageException;

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	public SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex);

	/**
	 * Returns the saml ib slo message where samlIdpSessionIndex = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching saml ib slo message, or <code>null</code> if a matching saml ib slo message could not be found
	 */
	public SamlIbSloMessage fetchBySamlIdpSessionIndex(
		String samlIdpSessionIndex, boolean useFinderCache);

	/**
	 * Removes the saml ib slo message where samlIdpSessionIndex = &#63; from the database.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the saml ib slo message that was removed
	 */
	public SamlIbSloMessage removeBySamlIdpSessionIndex(
			String samlIdpSessionIndex)
		throws NoSuchIbSloMessageException;

	/**
	 * Returns the number of saml ib slo messages where samlIdpSessionIndex = &#63;.
	 *
	 * @param samlIdpSessionIndex the saml idp session index
	 * @return the number of matching saml ib slo messages
	 */
	public int countBySamlIdpSessionIndex(String samlIdpSessionIndex);

	/**
	 * Caches the saml ib slo message in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 */
	public void cacheResult(SamlIbSloMessage samlIbSloMessage);

	/**
	 * Caches the saml ib slo messages in the entity cache if it is enabled.
	 *
	 * @param samlIbSloMessages the saml ib slo messages
	 */
	public void cacheResult(java.util.List<SamlIbSloMessage> samlIbSloMessages);

	/**
	 * Creates a new saml ib slo message with the primary key. Does not add the saml ib slo message to the database.
	 *
	 * @param samlIbSloMessageId the primary key for the new saml ib slo message
	 * @return the new saml ib slo message
	 */
	public SamlIbSloMessage create(long samlIbSloMessageId);

	/**
	 * Removes the saml ib slo message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message that was removed
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	public SamlIbSloMessage remove(long samlIbSloMessageId)
		throws NoSuchIbSloMessageException;

	public SamlIbSloMessage updateImpl(SamlIbSloMessage samlIbSloMessage);

	/**
	 * Returns the saml ib slo message with the primary key or throws a <code>NoSuchIbSloMessageException</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message
	 * @throws NoSuchIbSloMessageException if a saml ib slo message with the primary key could not be found
	 */
	public SamlIbSloMessage findByPrimaryKey(long samlIbSloMessageId)
		throws NoSuchIbSloMessageException;

	/**
	 * Returns the saml ib slo message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message, or <code>null</code> if a saml ib slo message with the primary key could not be found
	 */
	public SamlIbSloMessage fetchByPrimaryKey(long samlIbSloMessageId);

	/**
	 * Returns all the saml ib slo messages.
	 *
	 * @return the saml ib slo messages
	 */
	public java.util.List<SamlIbSloMessage> findAll();

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
	public java.util.List<SamlIbSloMessage> findAll(int start, int end);

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
	public java.util.List<SamlIbSloMessage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlIbSloMessage>
			orderByComparator);

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
	public java.util.List<SamlIbSloMessage> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SamlIbSloMessage>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the saml ib slo messages from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of saml ib slo messages.
	 *
	 * @return the number of saml ib slo messages
	 */
	public int countAll();

}
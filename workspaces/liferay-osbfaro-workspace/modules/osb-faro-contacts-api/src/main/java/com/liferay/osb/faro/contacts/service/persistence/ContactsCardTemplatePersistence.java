/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.service.persistence;

import com.liferay.osb.faro.contacts.exception.NoSuchContactsCardTemplateException;
import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the contacts card template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shinn Lok
 * @see ContactsCardTemplateUtil
 * @generated
 */
@ProviderType
public interface ContactsCardTemplatePersistence
	extends BasePersistence<ContactsCardTemplate> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ContactsCardTemplateUtil} to access the contacts card template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the contacts card templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsCardTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts card templates
	 * @param end the upper bound of the range of contacts card templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts card templates
	 */
	public java.util.List<ContactsCardTemplate> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsCardTemplate>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first contacts card template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts card template
	 * @throws NoSuchContactsCardTemplateException if a matching contacts card template could not be found
	 */
	public ContactsCardTemplate findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ContactsCardTemplate> orderByComparator)
		throws NoSuchContactsCardTemplateException;

	/**
	 * Returns the first contacts card template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts card template, or <code>null</code> if a matching contacts card template could not be found
	 */
	public ContactsCardTemplate fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsCardTemplate>
			orderByComparator);

	/**
	 * Removes all the contacts card templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of contacts card templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching contacts card templates
	 */
	public int countByGroupId(long groupId);

	/**
	 * Creates a new contacts card template with the primary key. Does not add the contacts card template to the database.
	 *
	 * @param contactsCardTemplateId the primary key for the new contacts card template
	 * @return the new contacts card template
	 */
	public ContactsCardTemplate create(long contactsCardTemplateId);

	/**
	 * Removes the contacts card template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template that was removed
	 * @throws NoSuchContactsCardTemplateException if a contacts card template with the primary key could not be found
	 */
	public ContactsCardTemplate remove(long contactsCardTemplateId)
		throws NoSuchContactsCardTemplateException;

	public ContactsCardTemplate updateImpl(
		ContactsCardTemplate contactsCardTemplate);

	/**
	 * Returns the contacts card template with the primary key or throws a <code>NoSuchContactsCardTemplateException</code> if it could not be found.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template
	 * @throws NoSuchContactsCardTemplateException if a contacts card template with the primary key could not be found
	 */
	public ContactsCardTemplate findByPrimaryKey(long contactsCardTemplateId)
		throws NoSuchContactsCardTemplateException;

	/**
	 * Returns the contacts card template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template, or <code>null</code> if a contacts card template with the primary key could not be found
	 */
	public ContactsCardTemplate fetchByPrimaryKey(long contactsCardTemplateId);

	/**
	 * Returns all the contacts card templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching contacts card templates
	 */
	public default java.util.List<ContactsCardTemplate> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the contacts card templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsCardTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts card templates
	 * @param end the upper bound of the range of contacts card templates (not inclusive)
	 * @return the range of matching contacts card templates
	 */
	public default java.util.List<ContactsCardTemplate> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the contacts card templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsCardTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts card templates
	 * @param end the upper bound of the range of contacts card templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching contacts card templates
	 */
	public default java.util.List<ContactsCardTemplate> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsCardTemplate>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1618085428
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.service.persistence;

import com.liferay.osb.faro.contacts.exception.NoSuchContactsLayoutTemplateException;
import com.liferay.osb.faro.contacts.model.ContactsLayoutTemplate;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the contacts layout template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shinn Lok
 * @see ContactsLayoutTemplateUtil
 * @generated
 */
@ProviderType
public interface ContactsLayoutTemplatePersistence
	extends BasePersistence<ContactsLayoutTemplate> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ContactsLayoutTemplateUtil} to access the contacts layout template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts layout templates
	 */
	public java.util.List<ContactsLayoutTemplate> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a matching contacts layout template could not be found
	 */
	public ContactsLayoutTemplate findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ContactsLayoutTemplate> orderByComparator)
		throws NoSuchContactsLayoutTemplateException;

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template, or <code>null</code> if a matching contacts layout template could not be found
	 */
	public ContactsLayoutTemplate fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator);

	/**
	 * Removes all the contacts layout templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of contacts layout templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching contacts layout templates
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts layout templates
	 */
	public java.util.List<ContactsLayoutTemplate> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a matching contacts layout template could not be found
	 */
	public ContactsLayoutTemplate findByG_T_First(
			long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<ContactsLayoutTemplate> orderByComparator)
		throws NoSuchContactsLayoutTemplateException;

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template, or <code>null</code> if a matching contacts layout template could not be found
	 */
	public ContactsLayoutTemplate fetchByG_T_First(
		long groupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator);

	/**
	 * Removes all the contacts layout templates where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public void removeByG_T(long groupId, int type);

	/**
	 * Returns the number of contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching contacts layout templates
	 */
	public int countByG_T(long groupId, int type);

	/**
	 * Creates a new contacts layout template with the primary key. Does not add the contacts layout template to the database.
	 *
	 * @param contactsLayoutTemplateId the primary key for the new contacts layout template
	 * @return the new contacts layout template
	 */
	public ContactsLayoutTemplate create(long contactsLayoutTemplateId);

	/**
	 * Removes the contacts layout template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template that was removed
	 * @throws NoSuchContactsLayoutTemplateException if a contacts layout template with the primary key could not be found
	 */
	public ContactsLayoutTemplate remove(long contactsLayoutTemplateId)
		throws NoSuchContactsLayoutTemplateException;

	public ContactsLayoutTemplate updateImpl(
		ContactsLayoutTemplate contactsLayoutTemplate);

	/**
	 * Returns the contacts layout template with the primary key or throws a <code>NoSuchContactsLayoutTemplateException</code> if it could not be found.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a contacts layout template with the primary key could not be found
	 */
	public ContactsLayoutTemplate findByPrimaryKey(
			long contactsLayoutTemplateId)
		throws NoSuchContactsLayoutTemplateException;

	/**
	 * Returns the contacts layout template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template, or <code>null</code> if a contacts layout template with the primary key could not be found
	 */
	public ContactsLayoutTemplate fetchByPrimaryKey(
		long contactsLayoutTemplateId);

	/**
	 * Returns all the contacts layout templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the contacts layout templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @return the range of matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByG_T(
		long groupId, int type) {

		return findByG_T(
			groupId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @return the range of matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByG_T(
		long groupId, int type, int start, int end) {

		return findByG_T(groupId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching contacts layout templates
	 */
	public default java.util.List<ContactsLayoutTemplate> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ContactsLayoutTemplate>
			orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1329646111
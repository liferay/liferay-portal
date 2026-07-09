/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.style.book.exception.DuplicateStyleBookEntryExternalReferenceCodeException;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.model.StyleBookEntryTable;
import com.liferay.style.book.model.impl.StyleBookEntryImpl;
import com.liferay.style.book.model.impl.StyleBookEntryModelImpl;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;
import com.liferay.style.book.service.persistence.StyleBookEntryUtil;
import com.liferay.style.book.service.persistence.impl.constants.StyleBookPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the style book entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = StyleBookEntryPersistence.class)
public class StyleBookEntryPersistenceImpl
	extends BasePersistenceImpl<StyleBookEntry, NoSuchEntryException>
	implements StyleBookEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>StyleBookEntryUtil</code> to access the style book entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		StyleBookEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the style book entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUuid_First(
			String uuid, OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUuid_First(
		String uuid, OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of style book entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_Head;

	/**
	 * Returns an ordered range of all the style book entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_Head.find(
			finderCache, new Object[] {uuid, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUuid_Head_First(
			String uuid, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_Head.findFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUuid_Head_First(
		String uuid, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_Head.fetchFirst(
			finderCache, new Object[] {uuid, head}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where uuid = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 */
	@Override
	public void removeByUuid_Head(String uuid, boolean head) {
		_collectionPersistenceFinderByUuid_Head.remove(
			finderCache, new Object[] {uuid, head});
	}

	/**
	 * Returns the number of style book entries where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUuid_Head(String uuid, boolean head) {
		return _collectionPersistenceFinderByUuid_Head.count(
			finderCache, new Object[] {uuid, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUUID_G;

	/**
	 * Returns an ordered range of all the style book entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUUID_G.findFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByUUID_G.fetchFirst(
			finderCache, new Object[] {uuid, groupId}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	@Override
	public void removeByUUID_G(String uuid, long groupId) {
		_collectionPersistenceFinderByUUID_G.remove(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the number of style book entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _collectionPersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private UniquePersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G_Head;

	/**
	 * Returns the style book entry where uuid = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G_Head.find(
			finderCache, new Object[] {uuid, groupId, head});
	}

	/**
	 * Returns the style book entry where uuid = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUUID_G_Head(
		String uuid, long groupId, boolean head, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_Head.fetch(
			finderCache, new Object[] {uuid, groupId, head}, useFinderCache);
	}

	/**
	 * Removes the style book entry where uuid = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the style book entry that was removed
	 */
	@Override
	public StyleBookEntry removeByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws NoSuchEntryException {

		StyleBookEntry styleBookEntry = findByUUID_G_Head(uuid, groupId, head);

		return remove(styleBookEntry);
	}

	/**
	 * Returns the number of style book entries where uuid = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUUID_G_Head(String uuid, long groupId, boolean head) {
		return _uniquePersistenceFinderByUUID_G_Head.count(
			finderCache, new Object[] {uuid, groupId, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the style book entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of style book entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C_Head;

	/**
	 * Returns an ordered range of all the style book entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C_Head.find(
			finderCache, new Object[] {uuid, companyId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByUuid_C_Head_First(
			String uuid, long companyId, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C_Head.findFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByUuid_C_Head_First(
		String uuid, long companyId, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C_Head.fetchFirst(
			finderCache, new Object[] {uuid, companyId, head},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where uuid = &#63; and companyId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 */
	@Override
	public void removeByUuid_C_Head(String uuid, long companyId, boolean head) {
		_collectionPersistenceFinderByUuid_C_Head.remove(
			finderCache, new Object[] {uuid, companyId, head});
	}

	/**
	 * Returns the number of style book entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByUuid_C_Head(String uuid, long companyId, boolean head) {
		return _collectionPersistenceFinderByUuid_C_Head.count(
			finderCache, new Object[] {uuid, companyId, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByGroupId_First(
			long groupId, OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByGroupId_First(
		long groupId, OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId_Head;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Head.find(
			finderCache, new Object[] {new long[] {groupId}, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByGroupId_Head_First(
			long groupId, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId_Head.findFirst(
			finderCache, new Object[] {new long[] {groupId}, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByGroupId_Head_First(
		long groupId, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Head.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, head},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and head = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByGroupId_Head(
		long[] groupIds, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Head.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds), head},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 */
	@Override
	public void removeByGroupId_Head(long groupId, boolean head) {
		_collectionPersistenceFinderByGroupId_Head.remove(
			finderCache, new Object[] {new long[] {groupId}, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByGroupId_Head(long groupId, boolean head) {
		return _collectionPersistenceFinderByGroupId_Head.count(
			finderCache, new Object[] {new long[] {groupId}, head});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63; and head = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByGroupId_Head(long[] groupIds, boolean head) {
		return _collectionPersistenceFinderByGroupId_Head.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds), head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_D;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_D_First(
			long groupId, boolean defaultStyleBookEntry,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_D_First(
		long groupId, boolean defaultStyleBookEntry,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 */
	@Override
	public void removeByG_D(long groupId, boolean defaultStyleBookEntry) {
		_collectionPersistenceFinderByG_D.remove(
			finderCache, new Object[] {groupId, defaultStyleBookEntry});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_D(long groupId, boolean defaultStyleBookEntry) {
		return _collectionPersistenceFinderByG_D.count(
			finderCache, new Object[] {groupId, defaultStyleBookEntry});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_D_Head;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_D_Head(
		long groupId, boolean defaultStyleBookEntry, boolean head, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_Head.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, head},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_D_Head_First(
			long groupId, boolean defaultStyleBookEntry, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D_Head.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_D_Head_First(
		long groupId, boolean defaultStyleBookEntry, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_Head.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, head},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param head the head
	 */
	@Override
	public void removeByG_D_Head(
		long groupId, boolean defaultStyleBookEntry, boolean head) {

		_collectionPersistenceFinderByG_D_Head.remove(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_D_Head(
		long groupId, boolean defaultStyleBookEntry, boolean head) {

		return _collectionPersistenceFinderByG_D_Head.count(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_N;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N.find(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_N_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_N.findFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N.fetchFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_N(long groupId, String name) {
		_collectionPersistenceFinderByG_N.remove(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.count(
			finderCache, new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_N_Head;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_N_Head(
		long groupId, String name, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_Head.find(
			finderCache, new Object[] {groupId, name, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_N_Head_First(
			long groupId, String name, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_N_Head.findFirst(
			finderCache, new Object[] {groupId, name, head}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_N_Head_First(
		long groupId, String name, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_N_Head.fetchFirst(
			finderCache, new Object[] {groupId, name, head}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 */
	@Override
	public void removeByG_N_Head(long groupId, String name, boolean head) {
		_collectionPersistenceFinderByG_N_Head.remove(
			finderCache, new Object[] {groupId, name, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_N_Head(long groupId, String name, boolean head) {
		return _collectionPersistenceFinderByG_N_Head.count(
			finderCache, new Object[] {groupId, name, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LikeN;

	/**
	 * Returns all the style book entries where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN(long groupId, String name) {
		return findByG_LikeN(
			groupId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return findByG_LikeN(groupId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN(
			groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN.find(
			finderCache, new Object[] {groupId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LikeN.findFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN.fetchFirst(
			finderCache, new Object[] {groupId, name}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_LikeN(long groupId, String name) {
		_collectionPersistenceFinderByG_LikeN.remove(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN(long groupId, String name) {
		return _collectionPersistenceFinderByG_LikeN.count(
			finderCache, new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LikeN_Head;

	/**
	 * Returns all the style book entries where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_Head(
		long groupId, String name, boolean head) {

		return findByG_LikeN_Head(
			groupId, name, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_Head(
		long groupId, String name, boolean head, int start, int end) {

		return findByG_LikeN_Head(groupId, name, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_Head(
		long groupId, String name, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN_Head(
			groupId, name, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_Head(
		long groupId, String name, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_Head.find(
			finderCache, new Object[] {groupId, name, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_LikeN_Head_First(
			long groupId, String name, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LikeN_Head.findFirst(
			finderCache, new Object[] {groupId, name, head}, orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_LikeN_Head_First(
		long groupId, String name, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_Head.fetchFirst(
			finderCache, new Object[] {groupId, name, head}, orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name LIKE &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 */
	@Override
	public void removeByG_LikeN_Head(long groupId, String name, boolean head) {
		_collectionPersistenceFinderByG_LikeN_Head.remove(
			finderCache, new Object[] {groupId, name, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN_Head(long groupId, String name, boolean head) {
		return _collectionPersistenceFinderByG_LikeN_Head.count(
			finderCache, new Object[] {groupId, name, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_SBEK;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_SBEK.find(
			finderCache, new Object[] {groupId, styleBookEntryKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_SBEK_First(
			long groupId, String styleBookEntryKey,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_SBEK.findFirst(
			finderCache, new Object[] {groupId, styleBookEntryKey},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_SBEK_First(
		long groupId, String styleBookEntryKey,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_SBEK.fetchFirst(
			finderCache, new Object[] {groupId, styleBookEntryKey},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and styleBookEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 */
	@Override
	public void removeByG_SBEK(long groupId, String styleBookEntryKey) {
		_collectionPersistenceFinderByG_SBEK.remove(
			finderCache, new Object[] {groupId, styleBookEntryKey});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_SBEK(long groupId, String styleBookEntryKey) {
		return _collectionPersistenceFinderByG_SBEK.count(
			finderCache, new Object[] {groupId, styleBookEntryKey});
	}

	private UniquePersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_SBEK_Head;

	/**
	 * Returns the style book entry where groupId = &#63; and styleBookEntryKey = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param head the head
	 * @return the matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_SBEK_Head(
			long groupId, String styleBookEntryKey, boolean head)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_SBEK_Head.find(
			finderCache, new Object[] {groupId, styleBookEntryKey, head});
	}

	/**
	 * Returns the style book entry where groupId = &#63; and styleBookEntryKey = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_SBEK_Head(
		long groupId, String styleBookEntryKey, boolean head,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_SBEK_Head.fetch(
			finderCache, new Object[] {groupId, styleBookEntryKey, head},
			useFinderCache);
	}

	/**
	 * Removes the style book entry where groupId = &#63; and styleBookEntryKey = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param head the head
	 * @return the style book entry that was removed
	 */
	@Override
	public StyleBookEntry removeByG_SBEK_Head(
			long groupId, String styleBookEntryKey, boolean head)
		throws NoSuchEntryException {

		StyleBookEntry styleBookEntry = findByG_SBEK_Head(
			groupId, styleBookEntryKey, head);

		return remove(styleBookEntry);
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and styleBookEntryKey = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_SBEK_Head(
		long groupId, String styleBookEntryKey, boolean head) {

		return _uniquePersistenceFinderByG_SBEK_Head.count(
			finderCache, new Object[] {groupId, styleBookEntryKey, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_T(
		long groupId, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {new long[] {groupId}, themeId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_T_First(
			long groupId, String themeId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_T.findFirst(
			finderCache, new Object[] {new long[] {groupId}, themeId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_T_First(
		long groupId, String themeId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, themeId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and themeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_T(
		long[] groupIds, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), themeId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_T(long groupId, String themeId) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {new long[] {groupId}, themeId});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_T(long groupId, String themeId) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {new long[] {groupId}, themeId});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63; and themeId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param themeId the theme ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_T(long[] groupIds, String themeId) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), themeId});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_T_Head;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_T_Head(
		long groupId, String themeId, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_Head.find(
			finderCache, new Object[] {new long[] {groupId}, themeId, head},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_T_Head_First(
			long groupId, String themeId, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_T_Head.findFirst(
			finderCache, new Object[] {new long[] {groupId}, themeId, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_T_Head_First(
		long groupId, String themeId, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_T_Head.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, themeId, head},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and themeId = &#63; and head = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_T_Head(
		long[] groupIds, String themeId, boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_Head.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), themeId, head},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and themeId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param head the head
	 */
	@Override
	public void removeByG_T_Head(long groupId, String themeId, boolean head) {
		_collectionPersistenceFinderByG_T_Head.remove(
			finderCache, new Object[] {new long[] {groupId}, themeId, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_T_Head(long groupId, String themeId, boolean head) {
		return _collectionPersistenceFinderByG_T_Head.count(
			finderCache, new Object[] {new long[] {groupId}, themeId, head});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_T_Head(long[] groupIds, String themeId, boolean head) {
		return _collectionPersistenceFinderByG_T_Head.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), themeId, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_D_T;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_T.find(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_D_T_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D_T.findFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_D_T_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T.fetchFirst(
			finderCache, new Object[] {groupId, defaultStyleBookEntry, themeId},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		_collectionPersistenceFinderByG_D_T.remove(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		return _collectionPersistenceFinderByG_D_T.count(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_D_T_Head;

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_D_T_Head(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		boolean head, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_D_T_Head.find(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, head}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_D_T_Head_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			boolean head, OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_D_T_Head.findFirst(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_D_T_Head_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		boolean head, OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_D_T_Head.fetchFirst(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, head},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param head the head
	 */
	@Override
	public void removeByG_D_T_Head(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		boolean head) {

		_collectionPersistenceFinderByG_D_T_Head.remove(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_D_T_Head(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		boolean head) {

		return _collectionPersistenceFinderByG_D_T_Head.count(
			finderCache,
			new Object[] {groupId, defaultStyleBookEntry, themeId, head});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LikeN_T;

	/**
	 * Returns all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long groupId, String name, String themeId) {

		return findByG_LikeN_T(
			groupId, name, themeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end) {

		return findByG_LikeN_T(groupId, name, themeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, themeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache, new Object[] {new long[] {groupId}, name, themeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_LikeN_T_First(
			long groupId, String name, String themeId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LikeN_T.findFirst(
			finderCache, new Object[] {new long[] {groupId}, name, themeId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_LikeN_T_First(
		long groupId, String name, String themeId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, name, themeId},
			orderByComparator);
	}

	/**
	 * Returns all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long[] groupIds, String name, String themeId) {

		return findByG_LikeN_T(
			groupIds, name, themeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long[] groupIds, String name, String themeId, int start, int end) {

		return findByG_LikeN_T(groupIds, name, themeId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long[] groupIds, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN_T(
			groupIds, name, themeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T(
		long[] groupIds, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), name, themeId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, String themeId) {
		_collectionPersistenceFinderByG_LikeN_T.remove(
			finderCache, new Object[] {new long[] {groupId}, name, themeId});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, String themeId) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache, new Object[] {new long[] {groupId}, name, themeId});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN_T(long[] groupIds, String name, String themeId) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), name, themeId});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LikeN_T_Head;

	/**
	 * Returns all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head) {

		return findByG_LikeN_T_Head(
			groupId, name, themeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head, int start,
		int end) {

		return findByG_LikeN_T_Head(
			groupId, name, themeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN_T_Head(
			groupId, name, themeId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T_Head.find(
			finderCache,
			new Object[] {new long[] {groupId}, name, themeId, head}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByG_LikeN_T_Head_First(
			long groupId, String name, String themeId, boolean head,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LikeN_T_Head.findFirst(
			finderCache,
			new Object[] {new long[] {groupId}, name, themeId, head},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByG_LikeN_T_Head_First(
		long groupId, String name, String themeId, boolean head,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T_Head.fetchFirst(
			finderCache,
			new Object[] {new long[] {groupId}, name, themeId, head},
			orderByComparator);
	}

	/**
	 * Returns all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long[] groupIds, String name, String themeId, boolean head) {

		return findByG_LikeN_T_Head(
			groupIds, name, themeId, head, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long[] groupIds, String name, String themeId, boolean head, int start,
		int end) {

		return findByG_LikeN_T_Head(
			groupIds, name, themeId, head, start, end, null);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long[] groupIds, String name, String themeId, boolean head, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator) {

		return findByG_LikeN_T_Head(
			groupIds, name, themeId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByG_LikeN_T_Head(
		long[] groupIds, String name, String themeId, boolean head, int start,
		int end, OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T_Head.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name, themeId, head
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 */
	@Override
	public void removeByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head) {

		_collectionPersistenceFinderByG_LikeN_T_Head.remove(
			finderCache,
			new Object[] {new long[] {groupId}, name, themeId, head});
	}

	/**
	 * Returns the number of style book entries where groupId = &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN_T_Head(
		long groupId, String name, String themeId, boolean head) {

		return _collectionPersistenceFinderByG_LikeN_T_Head.count(
			finderCache,
			new Object[] {new long[] {groupId}, name, themeId, head});
	}

	/**
	 * Returns the number of style book entries where groupId = any &#63; and name LIKE &#63; and themeId = &#63; and head = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param themeId the theme ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByG_LikeN_T_Head(
		long[] groupIds, String name, String themeId, boolean head) {

		return _collectionPersistenceFinderByG_LikeN_T_Head.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name, themeId, head
			});
	}

	private CollectionPersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_collectionPersistenceFinderByERC_G;

	/**
	 * Returns an ordered range of all the style book entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entries
	 */
	@Override
	public List<StyleBookEntry> findByERC_G(
		String externalReferenceCode, long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByERC_G_First(
			String externalReferenceCode, long groupId,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByERC_G.findFirst(
			finderCache, new Object[] {externalReferenceCode, groupId},
			orderByComparator);
	}

	/**
	 * Returns the first style book entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByERC_G_First(
		String externalReferenceCode, long groupId,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return _collectionPersistenceFinderByERC_G.fetchFirst(
			finderCache, new Object[] {externalReferenceCode, groupId},
			orderByComparator);
	}

	/**
	 * Removes all the style book entries where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 */
	@Override
	public void removeByERC_G(String externalReferenceCode, long groupId) {
		_collectionPersistenceFinderByERC_G.remove(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the number of style book entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _collectionPersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	private UniquePersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_uniquePersistenceFinderByERC_G_Head;

	/**
	 * Returns the style book entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByERC_G_Head.find(
			finderCache, new Object[] {externalReferenceCode, groupId, head});
	}

	/**
	 * Returns the style book entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G_Head.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId, head},
			useFinderCache);
	}

	/**
	 * Removes the style book entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the style book entry that was removed
	 */
	@Override
	public StyleBookEntry removeByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws NoSuchEntryException {

		StyleBookEntry styleBookEntry = findByERC_G_Head(
			externalReferenceCode, groupId, head);

		return remove(styleBookEntry);
	}

	/**
	 * Returns the number of style book entries where externalReferenceCode = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head) {

		return _uniquePersistenceFinderByERC_G_Head.count(
			finderCache, new Object[] {externalReferenceCode, groupId, head});
	}

	private UniquePersistenceFinder<StyleBookEntry, NoSuchEntryException>
		_uniquePersistenceFinderByHeadId;

	/**
	 * Returns the style book entry where headId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching style book entry
	 * @throws NoSuchEntryException if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry findByHeadId(long headId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByHeadId.find(
			finderCache, new Object[] {headId});
	}

	/**
	 * Returns the style book entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry, or <code>null</code> if a matching style book entry could not be found
	 */
	@Override
	public StyleBookEntry fetchByHeadId(long headId, boolean useFinderCache) {
		return _uniquePersistenceFinderByHeadId.fetch(
			finderCache, new Object[] {headId}, useFinderCache);
	}

	/**
	 * Removes the style book entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the style book entry that was removed
	 */
	@Override
	public StyleBookEntry removeByHeadId(long headId)
		throws NoSuchEntryException {

		StyleBookEntry styleBookEntry = findByHeadId(headId);

		return remove(styleBookEntry);
	}

	/**
	 * Returns the number of style book entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching style book entries
	 */
	@Override
	public int countByHeadId(long headId) {
		return _uniquePersistenceFinderByHeadId.count(
			finderCache, new Object[] {headId});
	}

	public StyleBookEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(StyleBookEntry.class);

		setModelImplClass(StyleBookEntryImpl.class);
		setModelPKClass(long.class);

		setTable(StyleBookEntryTable.INSTANCE);
	}

	/**
	 * Creates a new style book entry with the primary key. Does not add the style book entry to the database.
	 *
	 * @param styleBookEntryId the primary key for the new style book entry
	 * @return the new style book entry
	 */
	@Override
	public StyleBookEntry create(long styleBookEntryId) {
		StyleBookEntry styleBookEntry = new StyleBookEntryImpl();

		styleBookEntry.setNew(true);
		styleBookEntry.setPrimaryKey(styleBookEntryId);

		String uuid = PortalUUIDUtil.generate();

		styleBookEntry.setUuid(uuid);

		styleBookEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return styleBookEntry;
	}

	/**
	 * Removes the style book entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry that was removed
	 * @throws NoSuchEntryException if a style book entry with the primary key could not be found
	 */
	@Override
	public StyleBookEntry remove(long styleBookEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)styleBookEntryId);
	}

	@Override
	protected StyleBookEntry removeImpl(StyleBookEntry styleBookEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(styleBookEntry)) {
				styleBookEntry = (StyleBookEntry)session.get(
					StyleBookEntryImpl.class,
					styleBookEntry.getPrimaryKeyObj());
			}

			if ((styleBookEntry != null) &&
				ctPersistenceHelper.isRemove(styleBookEntry)) {

				session.delete(styleBookEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (styleBookEntry != null) {
			clearCache(styleBookEntry);
		}

		return styleBookEntry;
	}

	@Override
	public StyleBookEntry updateImpl(StyleBookEntry styleBookEntry) {
		boolean isNew = styleBookEntry.isNew();

		if (!(styleBookEntry instanceof StyleBookEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(styleBookEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					styleBookEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in styleBookEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom StyleBookEntry implementation " +
					styleBookEntry.getClass());
		}

		StyleBookEntryModelImpl styleBookEntryModelImpl =
			(StyleBookEntryModelImpl)styleBookEntry;

		if (Validator.isNull(styleBookEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			styleBookEntry.setUuid(uuid);
		}

		if (Validator.isNull(styleBookEntry.getExternalReferenceCode())) {
			styleBookEntry.setExternalReferenceCode(styleBookEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					styleBookEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					styleBookEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = styleBookEntry.getCompanyId();

					long groupId = styleBookEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = styleBookEntry.getPrimaryKey();
					}

					try {
						styleBookEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								StyleBookEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								styleBookEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			StyleBookEntry ercStyleBookEntry = fetchByERC_G_Head(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId(), styleBookEntry.isHead());

			if (isNew) {
				if (ercStyleBookEntry != null) {
					throw new DuplicateStyleBookEntryExternalReferenceCodeException(
						"Duplicate style book entry with external reference code " +
							styleBookEntry.getExternalReferenceCode() +
								" and group " + styleBookEntry.getGroupId());
				}
			}
			else {
				if ((ercStyleBookEntry != null) &&
					(styleBookEntry.getStyleBookEntryId() !=
						ercStyleBookEntry.getStyleBookEntryId())) {

					throw new DuplicateStyleBookEntryExternalReferenceCodeException(
						"Duplicate style book entry with external reference code " +
							styleBookEntry.getExternalReferenceCode() +
								" and group " + styleBookEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (styleBookEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				styleBookEntry.setCreateDate(date);
			}
			else {
				styleBookEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!styleBookEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				styleBookEntry.setModifiedDate(date);
			}
			else {
				styleBookEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(styleBookEntry)) {
				if (!isNew) {
					session.evict(
						StyleBookEntryImpl.class,
						styleBookEntry.getPrimaryKeyObj());
				}

				session.save(styleBookEntry);
			}
			else {
				styleBookEntry = (StyleBookEntry)session.merge(styleBookEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(styleBookEntry, false);

		if (isNew) {
			styleBookEntry.setNew(false);
		}

		styleBookEntry.resetOriginalValues();

		return styleBookEntry;
	}

	/**
	 * Returns the style book entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry
	 * @throws NoSuchEntryException if a style book entry with the primary key could not be found
	 */
	@Override
	public StyleBookEntry findByPrimaryKey(long styleBookEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)styleBookEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the style book entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry, or <code>null</code> if a style book entry with the primary key could not be found
	 */
	@Override
	public StyleBookEntry fetchByPrimaryKey(long styleBookEntryId) {
		return fetchByPrimaryKey((Serializable)styleBookEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "styleBookEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_STYLEBOOKENTRY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return StyleBookEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "StyleBookEntry";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("headId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("defaultStyleBookEntry");
		ctMergeColumnNames.add("frontendTokensValues");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("previewFileEntryId");
		ctMergeColumnNames.add("styleBookEntryKey");
		ctMergeColumnNames.add("themeId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("styleBookEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId", "head"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "styleBookEntryKey", "head"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId", "head"});

		_uniqueIndexColumnNames.add(new String[] {"headId"});
	}

	/**
	 * Initializes the style book entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"styleBookEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, StyleBookEntry::getUuid));

		_collectionPersistenceFinderByUuid_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"uuid_", "head"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_Head",
					new String[] {
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"uuid_", "head"}, 0, 1, false, null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntry::getUuid),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByUUID_G =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUUID_G",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUUID_G",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "groupId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUUID_G",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "groupId"}, 0, 1, false, null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntry::getUuid),
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId));

		_uniquePersistenceFinderByUUID_G_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_Head",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"uuid_", "groupId", "head"}, 0, 1, false,
				convertNullFunction(StyleBookEntry::getUuid),
				StyleBookEntry::getGroupId, StyleBookEntry::isHead),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, "",
			new FinderColumn<>(
				"styleBookEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, StyleBookEntry::getUuid),
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntry::getUuid),
				new FinderColumn<>(
					"styleBookEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getCompanyId));

		_collectionPersistenceFinderByUuid_C_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUuid_C_Head",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"uuid_", "companyId", "head"}, 0, 1, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntry::getUuid),
				new FinderColumn<>(
					"styleBookEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getCompanyId),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, StyleBookEntry::getGroupId));

		_collectionPersistenceFinderByGroupId_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "head"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "defaultStyleBookEntry"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "defaultStyleBookEntry"}, false),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "defaultStyleBookEntry",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				StyleBookEntry::isDefaultStyleBookEntry));

		_collectionPersistenceFinderByG_D_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "defaultStyleBookEntry", "head"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "defaultStyleBookEntry", "head"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_D_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "defaultStyleBookEntry", "head"},
					false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "defaultStyleBookEntry",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					StyleBookEntry::isDefaultStyleBookEntry),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, false, null),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "name", FinderColumn.Type.STRING, "=", true,
				true, StyleBookEntry::getName));

		_collectionPersistenceFinderByG_N_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "name", "head"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_N_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "name", "head"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "name", FinderColumn.Type.STRING, "=",
					true, true, StyleBookEntry::getName),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_LikeN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "name"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "name", FinderColumn.Type.STRING, "LIKE",
					true, true, StyleBookEntry::getName));

		_collectionPersistenceFinderByG_LikeN_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeN_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "head"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeN_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "name", "head"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "name", FinderColumn.Type.STRING, "LIKE",
					true, true, StyleBookEntry::getName),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_SBEK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_SBEK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "styleBookEntryKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_SBEK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "styleBookEntryKey"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_SBEK",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "styleBookEntryKey"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "styleBookEntryKey",
					FinderColumn.Type.STRING, "=", true, true,
					StyleBookEntry::getStyleBookEntryKey));

		_uniquePersistenceFinderByG_SBEK_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_SBEK_Head",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "styleBookEntryKey", "head"}, 0, 2,
				false, StyleBookEntry::getGroupId,
				convertNullFunction(StyleBookEntry::getStyleBookEntryKey),
				StyleBookEntry::isHead),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, "",
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "styleBookEntryKey",
				FinderColumn.Type.STRING, "=", true, true,
				StyleBookEntry::getStyleBookEntryKey),
			new FinderColumn<>(
				"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "themeId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "themeId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "themeId"}, 0, 2, false, null),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new ArrayableFinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
				false, true, true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
				true, true, StyleBookEntry::getThemeId));

		_collectionPersistenceFinderByG_T_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "themeId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "themeId", "head"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "themeId", "head"}, 0, 2, false,
					null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
					true, true, StyleBookEntry::getThemeId),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_D_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"}, 0,
				4, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_D_T",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "defaultStyleBookEntry", "themeId"}, 0,
				4, false, null),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "defaultStyleBookEntry",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				StyleBookEntry::isDefaultStyleBookEntry),
			new FinderColumn<>(
				"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
				true, true, StyleBookEntry::getThemeId));

		_collectionPersistenceFinderByG_D_T_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_D_T_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "head"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_D_T_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "head"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_D_T_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "defaultStyleBookEntry", "themeId", "head"
					},
					0, 4, false, null),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "defaultStyleBookEntry",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					StyleBookEntry::isDefaultStyleBookEntry),
				new FinderColumn<>(
					"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
					true, true, StyleBookEntry::getThemeId),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByG_LikeN_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "themeId"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "name", "themeId"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "name", FinderColumn.Type.STRING, "LIKE",
					true, true, StyleBookEntry::getName),
				new FinderColumn<>(
					"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
					true, true, StyleBookEntry::getThemeId));

		_collectionPersistenceFinderByG_LikeN_T_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_LikeN_T_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "themeId", "head"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_LikeN_T_Head",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "name", "themeId", "head"}, false),
				_SQL_SELECT_STYLEBOOKENTRY_WHERE,
				_SQL_COUNT_STYLEBOOKENTRY_WHERE,
				StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, StyleBookEntry::getGroupId),
				new FinderColumn<>(
					"styleBookEntry.", "name", FinderColumn.Type.STRING, "LIKE",
					true, true, StyleBookEntry::getName),
				new FinderColumn<>(
					"styleBookEntry.", "themeId", FinderColumn.Type.STRING, "=",
					true, true, StyleBookEntry::getThemeId),
				new FinderColumn<>(
					"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, StyleBookEntry::isHead));

		_collectionPersistenceFinderByERC_G = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByERC_G",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"externalReferenceCode", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				null),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, _SQL_COUNT_STYLEBOOKENTRY_WHERE,
			StyleBookEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"styleBookEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				StyleBookEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId));

		_uniquePersistenceFinderByERC_G_Head = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G_Head",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"externalReferenceCode", "groupId", "head"}, 0, 1,
				false,
				convertNullFunction(StyleBookEntry::getExternalReferenceCode),
				StyleBookEntry::getGroupId, StyleBookEntry::isHead),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, "",
			new FinderColumn<>(
				"styleBookEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				StyleBookEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"styleBookEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getGroupId),
			new FinderColumn<>(
				"styleBookEntry.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, StyleBookEntry::isHead));

		_uniquePersistenceFinderByHeadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
				new String[] {Long.class.getName()}, new String[] {"headId"}, 0,
				0, false, StyleBookEntry::getHeadId),
			_SQL_SELECT_STYLEBOOKENTRY_WHERE, "",
			new FinderColumn<>(
				"styleBookEntry.", "headId", FinderColumn.Type.LONG, "=", true,
				true, StyleBookEntry::getHeadId));

		StyleBookEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		StyleBookEntryUtil.setPersistence(null);

		entityCache.removeCache(StyleBookEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		StyleBookEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_STYLEBOOKENTRY =
		"SELECT styleBookEntry FROM StyleBookEntry styleBookEntry";

	private static final String _SQL_SELECT_STYLEBOOKENTRY_WHERE =
		"SELECT styleBookEntry FROM StyleBookEntry styleBookEntry WHERE ";

	private static final String _SQL_COUNT_STYLEBOOKENTRY_WHERE =
		"SELECT COUNT(styleBookEntry) FROM StyleBookEntry styleBookEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1986777486
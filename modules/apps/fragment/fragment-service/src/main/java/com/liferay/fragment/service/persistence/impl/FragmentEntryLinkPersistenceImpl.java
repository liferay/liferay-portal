/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.impl;

import com.liferay.fragment.exception.DuplicateFragmentEntryLinkExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchEntryLinkException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryLinkTable;
import com.liferay.fragment.model.impl.FragmentEntryLinkImpl;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.fragment.service.persistence.FragmentEntryLinkPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryLinkUtil;
import com.liferay.fragment.service.persistence.impl.constants.FragmentPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * The persistence implementation for the fragment entry link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FragmentEntryLinkPersistence.class)
public class FragmentEntryLinkPersistenceImpl
	extends BasePersistenceImpl<FragmentEntryLink, NoSuchEntryLinkException>
	implements FragmentEntryLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FragmentEntryLinkUtil</code> to access the fragment entry link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FragmentEntryLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByUuid_First(
			String uuid, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByUuid_First(
		String uuid, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of fragment entry links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<FragmentEntryLink, NoSuchEntryLinkException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryLinkException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the fragment entry link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	@Override
	public FragmentEntryLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryLinkException {

		FragmentEntryLink fragmentEntryLink = findByUUID_G(uuid, groupId);

		return remove(fragmentEntryLink);
	}

	/**
	 * Returns the number of fragment entry links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByGroupId_First(
			long groupId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByGroupId_First(
		long groupId, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByRendererKey;

	/**
	 * Returns an ordered range of all the fragment entry links where rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByRendererKey(
		String rendererKey, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRendererKey.find(
			finderCache, new Object[] {rendererKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByRendererKey_First(
			String rendererKey,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByRendererKey.findFirst(
			finderCache, new Object[] {rendererKey}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByRendererKey_First(
		String rendererKey,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByRendererKey.fetchFirst(
			finderCache, new Object[] {rendererKey}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where rendererKey = &#63; from the database.
	 *
	 * @param rendererKey the renderer key
	 */
	@Override
	public void removeByRendererKey(String rendererKey) {
		_collectionPersistenceFinderByRendererKey.remove(
			finderCache, new Object[] {rendererKey});
	}

	/**
	 * Returns the number of fragment entry links where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByRendererKey(String rendererKey) {
		return _collectionPersistenceFinderByRendererKey.count(
			finderCache, new Object[] {rendererKey});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			finderCache, new Object[] {type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByType_First(
			int type, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByType.findFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByType_First(
		int type, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(int type) {
		_collectionPersistenceFinderByType.remove(
			finderCache, new Object[] {type});
	}

	/**
	 * Returns the number of fragment entry links where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByType(int type) {
		return _collectionPersistenceFinderByType.count(
			finderCache, new Object[] {type});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_P_First(
			long groupId, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_P.findFirst(
			finderCache, new Object[] {groupId, plid}, orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_P_First(
		long groupId, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache, new Object[] {groupId, plid}, orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	@Override
	public void removeByG_P(long groupId, long plid) {
		_collectionPersistenceFinderByG_P.remove(
			finderCache, new Object[] {groupId, plid});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_P(long groupId, long plid) {
		return _collectionPersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, plid});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByC_R;

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R.find(
			finderCache, new Object[] {companyId, new String[] {rendererKey}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByC_R_First(
			long companyId, String rendererKey,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		FragmentEntryLink fragmentEntryLink = fetchByC_R_First(
			companyId, rendererKey, orderByComparator);

		if (fragmentEntryLink != null) {
			return fragmentEntryLink;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", rendererKey=");
		sb.append(rendererKey);

		sb.append("}");

		throw new NoSuchEntryLinkException(sb.toString());
	}

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByC_R_First(
		long companyId, String rendererKey,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByC_R.fetchFirst(
			finderCache, new Object[] {companyId, new String[] {rendererKey}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R.find(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(rendererKeys)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where companyId = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 */
	@Override
	public void removeByC_R(long companyId, String rendererKey) {
		_collectionPersistenceFinderByC_R.remove(
			finderCache, new Object[] {companyId, new String[] {rendererKey}});
	}

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByC_R(long companyId, String rendererKey) {
		return _collectionPersistenceFinderByC_R.count(
			finderCache, new Object[] {companyId, new String[] {rendererKey}});
	}

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByC_R(long companyId, String[] rendererKeys) {
		return _collectionPersistenceFinderByC_R.count(
			finderCache,
			new Object[] {companyId, ArrayUtil.sortedUnique(rendererKeys)});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByFEERC_FESERC;

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFEERC_FESERC.find(
			finderCache, new Object[] {fragmentEntryERC, fragmentEntryScopeERC},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByFEERC_FESERC_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByFEERC_FESERC.findFirst(
			finderCache, new Object[] {fragmentEntryERC, fragmentEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByFEERC_FESERC_First(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByFEERC_FESERC.fetchFirst(
			finderCache, new Object[] {fragmentEntryERC, fragmentEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	@Override
	public void removeByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		_collectionPersistenceFinderByFEERC_FESERC.remove(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC});
	}

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		return _collectionPersistenceFinderByFEERC_FESERC.count(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_OFELERC_P;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_OFELERC_P.find(
			finderCache,
			new Object[] {groupId, originalFragmentEntryLinkERC, plid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_OFELERC_P_First(
			long groupId, String originalFragmentEntryLinkERC, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_OFELERC_P.findFirst(
			finderCache,
			new Object[] {groupId, originalFragmentEntryLinkERC, plid},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_OFELERC_P_First(
		long groupId, String originalFragmentEntryLinkERC, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_OFELERC_P.fetchFirst(
			finderCache,
			new Object[] {groupId, originalFragmentEntryLinkERC, plid},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 */
	@Override
	public void removeByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		_collectionPersistenceFinderByG_OFELERC_P.remove(
			finderCache,
			new Object[] {groupId, originalFragmentEntryLinkERC, plid});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		return _collectionPersistenceFinderByG_OFELERC_P.count(
			finderCache,
			new Object[] {groupId, originalFragmentEntryLinkERC, plid});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_FEERC_FESERC;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEERC_FESERC.find(
			finderCache,
			new Object[] {groupId, fragmentEntryERC, fragmentEntryScopeERC},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_FEERC_FESERC_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_FEERC_FESERC.findFirst(
			finderCache,
			new Object[] {groupId, fragmentEntryERC, fragmentEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_FEERC_FESERC_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_FEERC_FESERC.fetchFirst(
			finderCache,
			new Object[] {groupId, fragmentEntryERC, fragmentEntryScopeERC},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	@Override
	public void removeByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		_collectionPersistenceFinderByG_FEERC_FESERC.remove(
			finderCache,
			new Object[] {groupId, fragmentEntryERC, fragmentEntryScopeERC});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		return _collectionPersistenceFinderByG_FEERC_FESERC.count(
			finderCache,
			new Object[] {groupId, fragmentEntryERC, fragmentEntryScopeERC});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_S_P;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_P.find(
			finderCache,
			new Object[] {groupId, new long[] {segmentsExperienceId}, plid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_S_P_First(
			long groupId, long segmentsExperienceId, long plid,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		FragmentEntryLink fragmentEntryLink = fetchByG_S_P_First(
			groupId, segmentsExperienceId, plid, orderByComparator);

		if (fragmentEntryLink != null) {
			return fragmentEntryLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsExperienceId=");
		sb.append(segmentsExperienceId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchEntryLinkException(sb.toString());
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_S_P_First(
		long groupId, long segmentsExperienceId, long plid,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_S_P.fetchFirst(
			finderCache,
			new Object[] {groupId, new long[] {segmentsExperienceId}, plid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end, OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_P.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsExperienceIds), plid
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 */
	@Override
	public void removeByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		_collectionPersistenceFinderByG_S_P.remove(
			finderCache,
			new Object[] {groupId, new long[] {segmentsExperienceId}, plid});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_P(
		long groupId, long segmentsExperienceId, long plid) {

		return _collectionPersistenceFinderByG_S_P.count(
			finderCache,
			new Object[] {groupId, new long[] {segmentsExperienceId}, plid});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid) {

		return _collectionPersistenceFinderByG_S_P.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsExperienceIds), plid
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			finderCache, new Object[] {groupId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_C_C.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		_collectionPersistenceFinderByG_C_C.remove(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_C_C.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_P_D;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_D.find(
			finderCache, new Object[] {groupId, plid, deleted}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_P_D_First(
			long groupId, long plid, boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_P_D.findFirst(
			finderCache, new Object[] {groupId, plid, deleted},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_P_D_First(
		long groupId, long plid, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_P_D.fetchFirst(
			finderCache, new Object[] {groupId, plid, deleted},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	@Override
	public void removeByG_P_D(long groupId, long plid, boolean deleted) {
		_collectionPersistenceFinderByG_P_D.remove(
			finderCache, new Object[] {groupId, plid, deleted});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_P_D(long groupId, long plid, boolean deleted) {
		return _collectionPersistenceFinderByG_P_D.count(
			finderCache, new Object[] {groupId, plid, deleted});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByFEERC_FESERC_D;

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFEERC_FESERC_D.find(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC, deleted},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByFEERC_FESERC_D_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByFEERC_FESERC_D.findFirst(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC, deleted},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByFEERC_FESERC_D_First(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByFEERC_FESERC_D.fetchFirst(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC, deleted},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	@Override
	public void removeByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		_collectionPersistenceFinderByFEERC_FESERC_D.remove(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC, deleted});
	}

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return _collectionPersistenceFinderByFEERC_FESERC_D.count(
			finderCache,
			new Object[] {fragmentEntryERC, fragmentEntryScopeERC, deleted});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_FEERC_FESERC_C;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C.find(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_FEERC_FESERC_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C.findFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId
			},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C.fetchFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId
			},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId) {

		_collectionPersistenceFinderByG_FEERC_FESERC_C.remove(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C.count(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_FEERC_FESERC_P;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_P.find(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, plid
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_FEERC_FESERC_P_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long plid, OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_FEERC_FESERC_P.findFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, plid
			},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_FEERC_FESERC_P_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_P.fetchFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, plid
			},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 */
	@Override
	public void removeByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid) {

		_collectionPersistenceFinderByG_FEERC_FESERC_P.remove(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, plid
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_P.count(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, plid
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_FEERC_FESERC_D;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_D.find(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_FEERC_FESERC_D_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_FEERC_FESERC_D.findFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted
			},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_FEERC_FESERC_D_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_D.fetchFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted
			},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	@Override
	public void removeByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		_collectionPersistenceFinderByG_FEERC_FESERC_D.remove(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_D.count(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_S_C_C;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_C_C.find(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, classNameId, classPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_S_C_C_First(
			long groupId, long segmentsExperienceId, long classNameId,
			long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_S_C_C.findFirst(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_S_C_C_First(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_S_C_C.fetchFirst(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK) {

		_collectionPersistenceFinderByG_S_C_C.remove(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, classNameId, classPK});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK) {

		return _collectionPersistenceFinderByG_S_C_C.count(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_S_P_D;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_P_D.find(
			finderCache,
			new Object[] {
				groupId, new long[] {segmentsExperienceId}, plid, deleted
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_S_P_D_First(
			long groupId, long segmentsExperienceId, long plid, boolean deleted,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		FragmentEntryLink fragmentEntryLink = fetchByG_S_P_D_First(
			groupId, segmentsExperienceId, plid, deleted, orderByComparator);

		if (fragmentEntryLink != null) {
			return fragmentEntryLink;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", segmentsExperienceId=");
		sb.append(segmentsExperienceId);

		sb.append(", plid=");
		sb.append(plid);

		sb.append(", deleted=");
		sb.append(deleted);

		sb.append("}");

		throw new NoSuchEntryLinkException(sb.toString());
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_S_P_D_First(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_S_P_D.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new long[] {segmentsExperienceId}, plid, deleted
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_P_D.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsExperienceIds), plid,
				deleted
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	@Override
	public void removeByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		_collectionPersistenceFinderByG_S_P_D.remove(
			finderCache,
			new Object[] {
				groupId, new long[] {segmentsExperienceId}, plid, deleted
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		return _collectionPersistenceFinderByG_S_P_D.count(
			finderCache,
			new Object[] {
				groupId, new long[] {segmentsExperienceId}, plid, deleted
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid,
		boolean deleted) {

		return _collectionPersistenceFinderByG_S_P_D.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(segmentsExperienceIds), plid,
				deleted
			});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_S_P_R;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S_P_R.find(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, plid, rendererKey},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_S_P_R_First(
			long groupId, long segmentsExperienceId, long plid,
			String rendererKey,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_S_P_R.findFirst(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, plid, rendererKey},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_S_P_R_First(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_S_P_R.fetchFirst(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, plid, rendererKey},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 */
	@Override
	public void removeByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		_collectionPersistenceFinderByG_S_P_R.remove(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, plid, rendererKey});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		return _collectionPersistenceFinderByG_S_P_R.count(
			finderCache,
			new Object[] {groupId, segmentsExperienceId, plid, rendererKey});
	}

	private CollectionPersistenceFinder
		<FragmentEntryLink, NoSuchEntryLinkException>
			_collectionPersistenceFinderByG_FEERC_FESERC_C_C;

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	@Override
	public List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C_C.find(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
				classPK
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByG_FEERC_FESERC_C_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId, long classPK,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws NoSuchEntryLinkException {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C_C.findFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
				classPK
			},
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C_C.fetchFirst(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
				classPK
			},
			orderByComparator);
	}

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK) {

		_collectionPersistenceFinderByG_FEERC_FESERC_C_C.remove(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
				classPK
			});
	}

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK) {

		return _collectionPersistenceFinderByG_FEERC_FESERC_C_C.count(
			finderCache,
			new Object[] {
				groupId, fragmentEntryERC, fragmentEntryScopeERC, classNameId,
				classPK
			});
	}

	private UniquePersistenceFinder<FragmentEntryLink, NoSuchEntryLinkException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryLinkException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Override
	public FragmentEntryLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	@Override
	public FragmentEntryLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryLinkException {

		FragmentEntryLink fragmentEntryLink = findByERC_G(
			externalReferenceCode, groupId);

		return remove(fragmentEntryLink);
	}

	/**
	 * Returns the number of fragment entry links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public FragmentEntryLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FragmentEntryLink.class);

		setModelImplClass(FragmentEntryLinkImpl.class);
		setModelPKClass(long.class);

		setTable(FragmentEntryLinkTable.INSTANCE);
	}

	/**
	 * Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	 *
	 * @param fragmentEntryLinkId the primary key for the new fragment entry link
	 * @return the new fragment entry link
	 */
	@Override
	public FragmentEntryLink create(long fragmentEntryLinkId) {
		FragmentEntryLink fragmentEntryLink = new FragmentEntryLinkImpl();

		fragmentEntryLink.setNew(true);
		fragmentEntryLink.setPrimaryKey(fragmentEntryLinkId);

		String uuid = PortalUUIDUtil.generate();

		fragmentEntryLink.setUuid(uuid);

		fragmentEntryLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return fragmentEntryLink;
	}

	/**
	 * Removes the fragment entry link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link that was removed
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	@Override
	public FragmentEntryLink remove(long fragmentEntryLinkId)
		throws NoSuchEntryLinkException {

		return remove((Serializable)fragmentEntryLinkId);
	}

	@Override
	protected FragmentEntryLink removeImpl(
		FragmentEntryLink fragmentEntryLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(fragmentEntryLink)) {
				fragmentEntryLink = (FragmentEntryLink)session.get(
					FragmentEntryLinkImpl.class,
					fragmentEntryLink.getPrimaryKeyObj());
			}

			if ((fragmentEntryLink != null) &&
				ctPersistenceHelper.isRemove(fragmentEntryLink)) {

				session.delete(fragmentEntryLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (fragmentEntryLink != null) {
			clearCache(fragmentEntryLink);
		}

		return fragmentEntryLink;
	}

	@Override
	public FragmentEntryLink updateImpl(FragmentEntryLink fragmentEntryLink) {
		boolean isNew = fragmentEntryLink.isNew();

		if (!(fragmentEntryLink instanceof FragmentEntryLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(fragmentEntryLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					fragmentEntryLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in fragmentEntryLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FragmentEntryLink implementation " +
					fragmentEntryLink.getClass());
		}

		FragmentEntryLinkModelImpl fragmentEntryLinkModelImpl =
			(FragmentEntryLinkModelImpl)fragmentEntryLink;

		if (Validator.isNull(fragmentEntryLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			fragmentEntryLink.setUuid(uuid);
		}

		if (Validator.isNull(fragmentEntryLink.getExternalReferenceCode())) {
			fragmentEntryLink.setExternalReferenceCode(
				fragmentEntryLink.getUuid());
		}
		else {
			if (!Objects.equals(
					fragmentEntryLinkModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					fragmentEntryLink.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = fragmentEntryLink.getCompanyId();

					long groupId = fragmentEntryLink.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = fragmentEntryLink.getPrimaryKey();
					}

					try {
						fragmentEntryLink.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								FragmentEntryLink.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								fragmentEntryLink.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			FragmentEntryLink ercFragmentEntryLink = fetchByERC_G(
				fragmentEntryLink.getExternalReferenceCode(),
				fragmentEntryLink.getGroupId());

			if (isNew) {
				if (ercFragmentEntryLink != null) {
					throw new DuplicateFragmentEntryLinkExternalReferenceCodeException(
						"Duplicate fragment entry link with external reference code " +
							fragmentEntryLink.getExternalReferenceCode() +
								" and group " + fragmentEntryLink.getGroupId());
				}
			}
			else {
				if ((ercFragmentEntryLink != null) &&
					(fragmentEntryLink.getFragmentEntryLinkId() !=
						ercFragmentEntryLink.getFragmentEntryLinkId())) {

					throw new DuplicateFragmentEntryLinkExternalReferenceCodeException(
						"Duplicate fragment entry link with external reference code " +
							fragmentEntryLink.getExternalReferenceCode() +
								" and group " + fragmentEntryLink.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (fragmentEntryLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				fragmentEntryLink.setCreateDate(date);
			}
			else {
				fragmentEntryLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!fragmentEntryLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				fragmentEntryLink.setModifiedDate(date);
			}
			else {
				fragmentEntryLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(fragmentEntryLink)) {
				if (!isNew) {
					session.evict(
						FragmentEntryLinkImpl.class,
						fragmentEntryLink.getPrimaryKeyObj());
				}

				session.save(fragmentEntryLink);
			}
			else {
				fragmentEntryLink = (FragmentEntryLink)session.merge(
					fragmentEntryLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(fragmentEntryLink, false);

		if (isNew) {
			fragmentEntryLink.setNew(false);
		}

		fragmentEntryLink.resetOriginalValues();

		return fragmentEntryLink;
	}

	/**
	 * Returns the fragment entry link with the primary key or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	@Override
	public FragmentEntryLink findByPrimaryKey(long fragmentEntryLinkId)
		throws NoSuchEntryLinkException {

		return findByPrimaryKey((Serializable)fragmentEntryLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the fragment entry link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link, or <code>null</code> if a fragment entry link with the primary key could not be found
	 */
	@Override
	public FragmentEntryLink fetchByPrimaryKey(long fragmentEntryLinkId) {
		return fetchByPrimaryKey((Serializable)fragmentEntryLinkId);
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
		return "fragmentEntryLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRAGMENTENTRYLINK;
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
		return FragmentEntryLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FragmentEntryLink";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("originalFragmentEntryLinkERC");
		ctMergeColumnNames.add("fragmentEntryERC");
		ctMergeColumnNames.add("fragmentEntryScopeERC");
		ctMergeColumnNames.add("segmentsExperienceId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("css");
		ctMergeColumnNames.add("html");
		ctMergeColumnNames.add("js");
		ctMergeColumnNames.add("configuration");
		ctMergeColumnNames.add("deleted");
		ctMergeColumnNames.add("editableValues");
		ctMergeColumnNames.add("namespace");
		ctMergeColumnNames.add("position");
		ctMergeColumnNames.add("rendererKey");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPropagationDate");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("fragmentEntryLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the fragment entry link persistence.
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
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, FragmentEntryLink::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(FragmentEntryLink::getUuid),
				FragmentEntryLink::getGroupId),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE, "",
			new FinderColumn<>(
				"fragmentEntryLink.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, FragmentEntryLink::getUuid),
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId));

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
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getUuid),
				new FinderColumn<>(
					"fragmentEntryLink.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getCompanyId));

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
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId));

		_collectionPersistenceFinderByRendererKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRendererKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"rendererKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRendererKey", new String[] {String.class.getName()},
					new String[] {"rendererKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRendererKey", new String[] {String.class.getName()},
					new String[] {"rendererKey"}, 0, 1, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "rendererKey",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getRendererKey));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {Integer.class.getName()}, new String[] {"type_"},
				false),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				FragmentEntryLink::getType));

		_collectionPersistenceFinderByG_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "plid"}, false),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId),
			new FinderColumn<>(
				"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=", true,
				true, FragmentEntryLink::getPlid));

		_collectionPersistenceFinderByC_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "rendererKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "rendererKey"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "rendererKey"}, 0, 2, false, null),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getCompanyId),
			new ArrayableFinderColumn<>(
				"fragmentEntryLink.", "rendererKey", FinderColumn.Type.STRING,
				"=", false, true, true, FragmentEntryLink::getRendererKey));

		_collectionPersistenceFinderByFEERC_FESERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFEERC_FESERC",
					new String[] {
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fragmentEntryERC", "fragmentEntryScopeERC"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFEERC_FESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"fragmentEntryERC", "fragmentEntryScopeERC"},
					0, 3, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFEERC_FESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"fragmentEntryERC", "fragmentEntryScopeERC"},
					0, 3, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC));

		_collectionPersistenceFinderByG_OFELERC_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_OFELERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "originalFragmentEntryLinkERC", "plid"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_OFELERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "originalFragmentEntryLinkERC", "plid"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_OFELERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "originalFragmentEntryLinkERC", "plid"
					},
					0, 2, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "originalFragmentEntryLinkERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getOriginalFragmentEntryLinkERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=",
					true, true, FragmentEntryLink::getPlid));

		_collectionPersistenceFinderByG_FEERC_FESERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FEERC_FESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FEERC_FESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FEERC_FESERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC"
					},
					0, 6, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC));

		_collectionPersistenceFinderByG_S_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "segmentsExperienceId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "segmentsExperienceId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_S_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "segmentsExperienceId", "plid"},
				false),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId),
			new ArrayableFinderColumn<>(
				"fragmentEntryLink.", "segmentsExperienceId",
				FinderColumn.Type.LONG, "=", false, true, true,
				FragmentEntryLink::getSegmentsExperienceId),
			new FinderColumn<>(
				"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=", true,
				true, FragmentEntryLink::getPlid));

		_collectionPersistenceFinderByG_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, false),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId),
			new FinderColumn<>(
				"fragmentEntryLink.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, FragmentEntryLink::getClassNameId),
			new FinderColumn<>(
				"fragmentEntryLink.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getClassPK));

		_collectionPersistenceFinderByG_P_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "plid", "deleted"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "plid", "deleted"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "plid", "deleted"}, false),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
			_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
			FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId),
			new FinderColumn<>(
				"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=", true,
				true, FragmentEntryLink::getPlid),
			new FinderColumn<>(
				"fragmentEntryLink.", "deleted", FinderColumn.Type.BOOLEAN, "=",
				true, true, FragmentEntryLink::isDeleted));

		_collectionPersistenceFinderByFEERC_FESERC_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFEERC_FESERC_D",
					new String[] {
						String.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"fragmentEntryERC", "fragmentEntryScopeERC", "deleted"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFEERC_FESERC_D",
					new String[] {
						String.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"fragmentEntryERC", "fragmentEntryScopeERC", "deleted"
					},
					0, 3, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFEERC_FESERC_D",
					new String[] {
						String.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"fragmentEntryERC", "fragmentEntryScopeERC", "deleted"
					},
					0, 3, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "deleted", FinderColumn.Type.BOOLEAN,
					"=", true, true, FragmentEntryLink::isDeleted));

		_collectionPersistenceFinderByG_FEERC_FESERC_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FEERC_FESERC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FEERC_FESERC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FEERC_FESERC_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId"
					},
					0, 6, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getClassNameId));

		_collectionPersistenceFinderByG_FEERC_FESERC_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FEERC_FESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"plid"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FEERC_FESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"plid"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FEERC_FESERC_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"plid"
					},
					0, 6, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=",
					true, true, FragmentEntryLink::getPlid));

		_collectionPersistenceFinderByG_FEERC_FESERC_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FEERC_FESERC_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"deleted"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FEERC_FESERC_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"deleted"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FEERC_FESERC_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"deleted"
					},
					0, 6, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "deleted", FinderColumn.Type.BOOLEAN,
					"=", true, true, FragmentEntryLink::isDeleted));

		_collectionPersistenceFinderByG_S_C_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "classNameId",
						"classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "classNameId",
						"classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "classNameId",
						"classPK"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "segmentsExperienceId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryLink::getSegmentsExperienceId),
				new FinderColumn<>(
					"fragmentEntryLink.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getClassNameId),
				new FinderColumn<>(
					"fragmentEntryLink.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getClassPK));

		_collectionPersistenceFinderByG_S_P_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_P_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "deleted"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_P_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "deleted"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_S_P_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "deleted"
					},
					false),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new ArrayableFinderColumn<>(
					"fragmentEntryLink.", "segmentsExperienceId",
					FinderColumn.Type.LONG, "=", false, true, true,
					FragmentEntryLink::getSegmentsExperienceId),
				new FinderColumn<>(
					"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=",
					true, true, FragmentEntryLink::getPlid),
				new FinderColumn<>(
					"fragmentEntryLink.", "deleted", FinderColumn.Type.BOOLEAN,
					"=", true, true, FragmentEntryLink::isDeleted));

		_collectionPersistenceFinderByG_S_P_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S_P_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "rendererKey"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S_P_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "rendererKey"
					},
					0, 8, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S_P_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "segmentsExperienceId", "plid", "rendererKey"
					},
					0, 8, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "segmentsExperienceId",
					FinderColumn.Type.LONG, "=", true, true,
					FragmentEntryLink::getSegmentsExperienceId),
				new FinderColumn<>(
					"fragmentEntryLink.", "plid", FinderColumn.Type.LONG, "=",
					true, true, FragmentEntryLink::getPlid),
				new FinderColumn<>(
					"fragmentEntryLink.", "rendererKey",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getRendererKey));

		_collectionPersistenceFinderByG_FEERC_FESERC_C_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_FEERC_FESERC_C_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_FEERC_FESERC_C_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId", "classPK"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_FEERC_FESERC_C_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "fragmentEntryERC", "fragmentEntryScopeERC",
						"classNameId", "classPK"
					},
					0, 6, false, null),
				_SQL_SELECT_FRAGMENTENTRYLINK_WHERE,
				_SQL_COUNT_FRAGMENTENTRYLINK_WHERE,
				FragmentEntryLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getGroupId),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "fragmentEntryScopeERC",
					FinderColumn.Type.STRING, "=", true, true,
					FragmentEntryLink::getFragmentEntryScopeERC),
				new FinderColumn<>(
					"fragmentEntryLink.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getClassNameId),
				new FinderColumn<>(
					"fragmentEntryLink.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, FragmentEntryLink::getClassPK));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					FragmentEntryLink::getExternalReferenceCode),
				FragmentEntryLink::getGroupId),
			_SQL_SELECT_FRAGMENTENTRYLINK_WHERE, "",
			new FinderColumn<>(
				"fragmentEntryLink.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				FragmentEntryLink::getExternalReferenceCode),
			new FinderColumn<>(
				"fragmentEntryLink.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FragmentEntryLink::getGroupId));

		FragmentEntryLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FragmentEntryLinkUtil.setPersistence(null);

		entityCache.removeCache(FragmentEntryLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = FragmentPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		FragmentEntryLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FRAGMENTENTRYLINK =
		"SELECT fragmentEntryLink FROM FragmentEntryLink fragmentEntryLink";

	private static final String _SQL_SELECT_FRAGMENTENTRYLINK_WHERE =
		"SELECT fragmentEntryLink FROM FragmentEntryLink fragmentEntryLink WHERE ";

	private static final String _SQL_COUNT_FRAGMENTENTRYLINK_WHERE =
		"SELECT COUNT(fragmentEntryLink) FROM FragmentEntryLink fragmentEntryLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FragmentEntryLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1768072252
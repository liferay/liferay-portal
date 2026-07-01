/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollectionTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the layout page template collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateCollectionPersistence.class)
public class LayoutPageTemplateCollectionPersistenceImpl
	extends BasePersistenceImpl
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
	implements LayoutPageTemplateCollectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateCollectionUtil</code> to access the layout page template collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateCollectionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template collection where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByUUID_G(uuid, groupId);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_First(
			long groupId, long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByG_P.findFirst(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_First(
		long groupId, long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 */
	@Override
	public void removeByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		_collectionPersistenceFinderByG_P.remove(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return _collectionPersistenceFinderByG_P.count(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return _collectionPersistenceFinderByG_P.filterCount(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_T_First(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByG_T.findFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			finderCache, new Object[] {groupId, type}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, int type) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {groupId, type});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, type});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.filterCount(
			finderCache, new Object[] {groupId, type}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByG_P_T;

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_T.find(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_T_First(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByG_P_T.findFirst(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_T_First(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_P_T.fetchFirst(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_P_T.filterFind(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 */
	@Override
	public void removeByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		_collectionPersistenceFinderByG_P_T.remove(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return _collectionPersistenceFinderByG_P_T.count(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return _collectionPersistenceFinderByG_P_T.filterCount(
			finderCache,
			new Object[] {groupId, parentLayoutPageTemplateCollectionId, type},
			groupId);
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_uniquePersistenceFinderByG_LPTCK_T;

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException {

		return _uniquePersistenceFinderByG_LPTCK_T.find(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionKey, type});
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_LPTCK_T.fetch(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionKey, type},
			useFinderCache);
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByG_LPTCK_T(groupId, layoutPageTemplateCollectionKey, type);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return _uniquePersistenceFinderByG_LPTCK_T.count(
			finderCache,
			new Object[] {groupId, layoutPageTemplateCollectionKey, type});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByG_N_T;

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T.find(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_N_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByG_N_T.findFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_N_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T.fetchFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T.filterFind(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_N_T(long groupId, String name, int type) {
		_collectionPersistenceFinderByG_N_T.remove(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_N_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_N_T.count(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_N_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_N_T.filterCount(
			finderCache, new Object[] {groupId, name, type}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_collectionPersistenceFinderByG_LikeN_T;

	/**
	 * Returns all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type) {

		return findByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return findByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	@Override
	public List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LikeN_T.find(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_LikeN_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws NoSuchPageTemplateCollectionException {

		return _collectionPersistenceFinderByG_LikeN_T.findFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_LikeN_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.fetchFirst(
			finderCache, new Object[] {groupId, name, type}, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type) {

		return filterFindByG_LikeN_T(
			groupId, name, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return filterFindByG_LikeN_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	@Override
	public List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return _collectionPersistenceFinderByG_LikeN_T.filterFind(
			finderCache, new Object[] {groupId, name, type}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	@Override
	public void removeByG_LikeN_T(long groupId, String name, int type) {
		_collectionPersistenceFinderByG_LikeN_T.remove(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_LikeN_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_LikeN_T.count(
			finderCache, new Object[] {groupId, name, type});
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	@Override
	public int filterCountByG_LikeN_T(long groupId, String name, int type) {
		return _collectionPersistenceFinderByG_LikeN_T.filterCount(
			finderCache, new Object[] {groupId, name, type}, groupId);
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_uniquePersistenceFinderByG_P_N_T;

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException {

		return _uniquePersistenceFinderByG_P_N_T.find(
			finderCache,
			new Object[] {
				groupId, parentLayoutPageTemplateCollectionId, name, type
			});
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_N_T.fetch(
			finderCache,
			new Object[] {
				groupId, parentLayoutPageTemplateCollectionId, name, type
			},
			useFinderCache);
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			findByG_P_N_T(
				groupId, parentLayoutPageTemplateCollectionId, name, type);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		return _uniquePersistenceFinderByG_P_N_T.count(
			finderCache,
			new Object[] {
				groupId, parentLayoutPageTemplateCollectionId, name, type
			});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateCollection, NoSuchPageTemplateCollectionException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	@Override
	public LayoutPageTemplateCollection removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateCollectionException {

		LayoutPageTemplateCollection layoutPageTemplateCollection = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutPageTemplateCollection);
	}

	/**
	 * Returns the number of layout page template collections where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutPageTemplateCollectionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"parentLayoutPageTemplateCollectionId", "parentLPTCollectionId");
		dbColumnNames.put(
			"layoutPageTemplateCollectionKey", "lptCollectionKey");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateCollection.class);

		setModelImplClass(LayoutPageTemplateCollectionImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateCollectionTable.INSTANCE);
	}

	/**
	 * Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	 *
	 * @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	 * @return the new layout page template collection
	 */
	@Override
	public LayoutPageTemplateCollection create(
		long layoutPageTemplateCollectionId) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			new LayoutPageTemplateCollectionImpl();

		layoutPageTemplateCollection.setNew(true);
		layoutPageTemplateCollection.setPrimaryKey(
			layoutPageTemplateCollectionId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateCollection.setUuid(uuid);

		layoutPageTemplateCollection.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateCollection;
	}

	/**
	 * Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection remove(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException {

		return remove((Serializable)layoutPageTemplateCollectionId);
	}

	@Override
	protected LayoutPageTemplateCollection removeImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateCollection)) {
				layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)session.get(
						LayoutPageTemplateCollectionImpl.class,
						layoutPageTemplateCollection.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateCollection != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateCollection)) {

				session.delete(layoutPageTemplateCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateCollection != null) {
			clearCache(layoutPageTemplateCollection);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection updateImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		boolean isNew = layoutPageTemplateCollection.isNew();

		if (!(layoutPageTemplateCollection instanceof
				LayoutPageTemplateCollectionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateCollection.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateCollection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateCollection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateCollection implementation " +
					layoutPageTemplateCollection.getClass());
		}

		LayoutPageTemplateCollectionModelImpl
			layoutPageTemplateCollectionModelImpl =
				(LayoutPageTemplateCollectionModelImpl)
					layoutPageTemplateCollection;

		if (Validator.isNull(layoutPageTemplateCollection.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateCollection.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutPageTemplateCollection.getExternalReferenceCode())) {

			layoutPageTemplateCollection.setExternalReferenceCode(
				layoutPageTemplateCollection.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutPageTemplateCollectionModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					layoutPageTemplateCollection.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						layoutPageTemplateCollection.getCompanyId();

					long groupId = layoutPageTemplateCollection.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutPageTemplateCollection.getPrimaryKey();
					}

					try {
						layoutPageTemplateCollection.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutPageTemplateCollection.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								layoutPageTemplateCollection.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutPageTemplateCollection ercLayoutPageTemplateCollection =
				fetchByERC_G(
					layoutPageTemplateCollection.getExternalReferenceCode(),
					layoutPageTemplateCollection.getGroupId());

			if (isNew) {
				if (ercLayoutPageTemplateCollection != null) {
					throw new DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException(
						"Duplicate layout page template collection with external reference code " +
							layoutPageTemplateCollection.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateCollection.getGroupId());
				}
			}
			else {
				if ((ercLayoutPageTemplateCollection != null) &&
					(layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId() !=
							ercLayoutPageTemplateCollection.
								getLayoutPageTemplateCollectionId())) {

					throw new DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException(
						"Duplicate layout page template collection with external reference code " +
							layoutPageTemplateCollection.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateCollection.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateCollection.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateCollection.setCreateDate(date);
			}
			else {
				layoutPageTemplateCollection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateCollectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateCollection.setModifiedDate(date);
			}
			else {
				layoutPageTemplateCollection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateCollection)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateCollectionImpl.class,
						layoutPageTemplateCollection.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateCollection);
			}
			else {
				layoutPageTemplateCollection =
					(LayoutPageTemplateCollection)session.merge(
						layoutPageTemplateCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutPageTemplateCollection, false);

		if (isNew) {
			layoutPageTemplateCollection.setNew(false);
		}

		layoutPageTemplateCollection.resetOriginalValues();

		return layoutPageTemplateCollection;
	}

	/**
	 * Returns the layout page template collection with the primary key or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection findByPrimaryKey(
			long layoutPageTemplateCollectionId)
		throws NoSuchPageTemplateCollectionException {

		return findByPrimaryKey((Serializable)layoutPageTemplateCollectionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateCollection fetchByPrimaryKey(
		long layoutPageTemplateCollectionId) {

		return fetchByPrimaryKey((Serializable)layoutPageTemplateCollectionId);
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
		return "layoutPageTemplateCollectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION;
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
		return LayoutPageTemplateCollectionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateCollection";
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
		ctMergeColumnNames.add("parentLPTCollectionId");
		ctMergeColumnNames.add("lptCollectionKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutPageTemplateCollectionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "lptCollectionKey", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "parentLPTCollectionId", "name", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout page template collection persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
			LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateCollection::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutPageTemplateCollection::getUuid),
				LayoutPageTemplateCollection::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateCollection::getUuid),
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateCollection::getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateCollection::getUuid),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentLPTCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentLPTCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "parentLPTCollectionId"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.",
					"parentLayoutPageTemplateCollectionId",
					"parentLPTCollectionId", FinderColumn.Type.LONG, "=", true,
					true,
					LayoutPageTemplateCollection::
						getParentLayoutPageTemplateCollectionId));

		_collectionPersistenceFinderByG_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "type_"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateCollection::getType));

		_collectionPersistenceFinderByG_P_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "parentLPTCollectionId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentLPTCollectionId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "parentLPTCollectionId", "type_"},
					false),
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.",
					"parentLayoutPageTemplateCollectionId",
					"parentLPTCollectionId", FinderColumn.Type.LONG, "=", true,
					true,
					LayoutPageTemplateCollection::
						getParentLayoutPageTemplateCollectionId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateCollection::getType));

		_uniquePersistenceFinderByG_LPTCK_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_LPTCK_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "lptCollectionKey", "type_"}, 0, 2,
				false, LayoutPageTemplateCollection::getGroupId,
				convertNullFunction(
					LayoutPageTemplateCollection::
						getLayoutPageTemplateCollectionKey),
				LayoutPageTemplateCollection::getType),
			_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateCollection::getGroupId),
			new FinderColumn<>(
				"layoutPageTemplateCollection.",
				"layoutPageTemplateCollectionKey", "lptCollectionKey",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateCollection::
					getLayoutPageTemplateCollectionKey),
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				LayoutPageTemplateCollection::getType));

		_collectionPersistenceFinderByG_N_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, 0, 2, false,
					null),
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateCollection::getName),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateCollection::getType));

		_collectionPersistenceFinderByG_LikeN_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LikeN_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "name", "type_"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE,
				LayoutPageTemplateCollectionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateCollection::getGroupId),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "name",
					FinderColumn.Type.STRING, "LIKE", true, true,
					LayoutPageTemplateCollection::getName),
				new FinderColumn<>(
					"layoutPageTemplateCollection.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					LayoutPageTemplateCollection::getType));

		_uniquePersistenceFinderByG_P_N_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName()
				},
				new String[] {
					"groupId", "parentLPTCollectionId", "name", "type_"
				},
				0, 4, false, LayoutPageTemplateCollection::getGroupId,
				LayoutPageTemplateCollection::
					getParentLayoutPageTemplateCollectionId,
				convertNullFunction(LayoutPageTemplateCollection::getName),
				LayoutPageTemplateCollection::getType),
			_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateCollection::getGroupId),
			new FinderColumn<>(
				"layoutPageTemplateCollection.",
				"parentLayoutPageTemplateCollectionId", "parentLPTCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateCollection::
					getParentLayoutPageTemplateCollectionId),
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "name",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateCollection::getName),
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				LayoutPageTemplateCollection::getType));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateCollection::getExternalReferenceCode),
				LayoutPageTemplateCollection::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateCollection::getExternalReferenceCode),
			new FinderColumn<>(
				"layoutPageTemplateCollection.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateCollection::getGroupId));

		LayoutPageTemplateCollectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateCollectionUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateCollectionImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutPageTemplateCollectionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION =
		"SELECT layoutPageTemplateCollection FROM LayoutPageTemplateCollection layoutPageTemplateCollection";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
		"SELECT layoutPageTemplateCollection FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATECOLLECTION_WHERE =
		"SELECT COUNT(layoutPageTemplateCollection) FROM LayoutPageTemplateCollection layoutPageTemplateCollection WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "parentLayoutPageTemplateCollectionId",
			"layoutPageTemplateCollectionKey", "type"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1758616305
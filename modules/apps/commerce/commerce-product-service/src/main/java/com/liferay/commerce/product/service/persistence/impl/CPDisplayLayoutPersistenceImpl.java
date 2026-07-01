/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPDisplayLayoutException;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CPDisplayLayoutTable;
import com.liferay.commerce.product.model.impl.CPDisplayLayoutImpl;
import com.liferay.commerce.product.model.impl.CPDisplayLayoutModelImpl;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutPersistence;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cp display layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDisplayLayoutPersistence.class)
public class CPDisplayLayoutPersistenceImpl
	extends BasePersistenceImpl<CPDisplayLayout, NoSuchCPDisplayLayoutException>
	implements CPDisplayLayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDisplayLayoutUtil</code> to access the cp display layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDisplayLayoutImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp display layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByUuid_First(
			String uuid, OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByUuid_First(
		String uuid, OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp display layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp display layout where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDisplayLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDisplayLayoutException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp display layout where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp display layout where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp display layout that was removed
	 */
	@Override
	public CPDisplayLayout removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDisplayLayoutException {

		CPDisplayLayout cpDisplayLayout = findByUUID_G(uuid, groupId);

		return remove(cpDisplayLayout);
	}

	/**
	 * Returns the number of cp display layouts where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp display layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp display layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cp display layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByGroupId_First(
			long groupId, OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByGroupId_First(
		long groupId, OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cp display layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the cp display layouts where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of cp display layouts where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByG_LPTEU;

	/**
	 * Returns an ordered range of all the cp display layouts where groupId = &#63; and layoutPageTemplateEntryUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryUuid the layout page template entry uuid
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByG_LPTEU(
		long groupId, String layoutPageTemplateEntryUuid, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LPTEU.find(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryUuid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and layoutPageTemplateEntryUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryUuid the layout page template entry uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByG_LPTEU_First(
			long groupId, String layoutPageTemplateEntryUuid,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByG_LPTEU.findFirst(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryUuid},
			orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and layoutPageTemplateEntryUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryUuid the layout page template entry uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByG_LPTEU_First(
		long groupId, String layoutPageTemplateEntryUuid,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByG_LPTEU.fetchFirst(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryUuid},
			orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where groupId = &#63; and layoutPageTemplateEntryUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryUuid the layout page template entry uuid
	 */
	@Override
	public void removeByG_LPTEU(
		long groupId, String layoutPageTemplateEntryUuid) {

		_collectionPersistenceFinderByG_LPTEU.remove(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryUuid});
	}

	/**
	 * Returns the number of cp display layouts where groupId = &#63; and layoutPageTemplateEntryUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryUuid the layout page template entry uuid
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByG_LPTEU(
		long groupId, String layoutPageTemplateEntryUuid) {

		return _collectionPersistenceFinderByG_LPTEU.count(
			finderCache, new Object[] {groupId, layoutPageTemplateEntryUuid});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByG_L;

	/**
	 * Returns an ordered range of all the cp display layouts where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_L.find(
			finderCache, new Object[] {groupId, layoutUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByG_L_First(
			long groupId, String layoutUuid,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByG_L.findFirst(
			finderCache, new Object[] {groupId, layoutUuid}, orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByG_L_First(
		long groupId, String layoutUuid,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByG_L.fetchFirst(
			finderCache, new Object[] {groupId, layoutUuid}, orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where groupId = &#63; and layoutUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 */
	@Override
	public void removeByG_L(long groupId, String layoutUuid) {
		_collectionPersistenceFinderByG_L.remove(
			finderCache, new Object[] {groupId, layoutUuid});
	}

	/**
	 * Returns the number of cp display layouts where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByG_L(long groupId, String layoutUuid) {
		return _collectionPersistenceFinderByG_L.count(
			finderCache, new Object[] {groupId, layoutUuid});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByC_C_LPTEU;

	/**
	 * Returns an ordered range of all the cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByC_C_LPTEU(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_LPTEU.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByC_C_LPTEU_First(
			long classNameId, long classPK,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByC_C_LPTEU.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByC_C_LPTEU_First(
		long classNameId, long classPK,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByC_C_LPTEU.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_LPTEU(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_LPTEU.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByC_C_LPTEU(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_LPTEU.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_collectionPersistenceFinderByC_C_L;

	/**
	 * Returns an ordered range of all the cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDisplayLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp display layouts
	 * @param end the upper bound of the range of cp display layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp display layouts
	 */
	@Override
	public List<CPDisplayLayout> findByC_C_L(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPDisplayLayout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_L.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByC_C_L_First(
			long classNameId, long classPK,
			OrderByComparator<CPDisplayLayout> orderByComparator)
		throws NoSuchCPDisplayLayoutException {

		return _collectionPersistenceFinderByC_C_L.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first cp display layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByC_C_L_First(
		long classNameId, long classPK,
		OrderByComparator<CPDisplayLayout> orderByComparator) {

		return _collectionPersistenceFinderByC_C_L.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the cp display layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_L(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_L.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of cp display layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByC_C_L(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_L.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<CPDisplayLayout, NoSuchCPDisplayLayoutException>
			_uniquePersistenceFinderByG_C_C;

	/**
	 * Returns the cp display layout where groupId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchCPDisplayLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout findByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchCPDisplayLayoutException {

		return _uniquePersistenceFinderByG_C_C.find(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the cp display layout where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp display layout, or <code>null</code> if a matching cp display layout could not be found
	 */
	@Override
	public CPDisplayLayout fetchByG_C_C(
		long groupId, long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_C.fetch(
			finderCache, new Object[] {groupId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the cp display layout where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the cp display layout that was removed
	 */
	@Override
	public CPDisplayLayout removeByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchCPDisplayLayoutException {

		CPDisplayLayout cpDisplayLayout = findByG_C_C(
			groupId, classNameId, classPK);

		return remove(cpDisplayLayout);
	}

	/**
	 * Returns the number of cp display layouts where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp display layouts
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _uniquePersistenceFinderByG_C_C.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	public CPDisplayLayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDisplayLayout.class);

		setModelImplClass(CPDisplayLayoutImpl.class);
		setModelPKClass(long.class);

		setTable(CPDisplayLayoutTable.INSTANCE);
	}

	/**
	 * Creates a new cp display layout with the primary key. Does not add the cp display layout to the database.
	 *
	 * @param CPDisplayLayoutId the primary key for the new cp display layout
	 * @return the new cp display layout
	 */
	@Override
	public CPDisplayLayout create(long CPDisplayLayoutId) {
		CPDisplayLayout cpDisplayLayout = new CPDisplayLayoutImpl();

		cpDisplayLayout.setNew(true);
		cpDisplayLayout.setPrimaryKey(CPDisplayLayoutId);

		String uuid = PortalUUIDUtil.generate();

		cpDisplayLayout.setUuid(uuid);

		cpDisplayLayout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpDisplayLayout;
	}

	/**
	 * Removes the cp display layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDisplayLayoutId the primary key of the cp display layout
	 * @return the cp display layout that was removed
	 * @throws NoSuchCPDisplayLayoutException if a cp display layout with the primary key could not be found
	 */
	@Override
	public CPDisplayLayout remove(long CPDisplayLayoutId)
		throws NoSuchCPDisplayLayoutException {

		return remove((Serializable)CPDisplayLayoutId);
	}

	@Override
	protected CPDisplayLayout removeImpl(CPDisplayLayout cpDisplayLayout) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDisplayLayout)) {
				cpDisplayLayout = (CPDisplayLayout)session.get(
					CPDisplayLayoutImpl.class,
					cpDisplayLayout.getPrimaryKeyObj());
			}

			if ((cpDisplayLayout != null) &&
				ctPersistenceHelper.isRemove(cpDisplayLayout)) {

				session.delete(cpDisplayLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDisplayLayout != null) {
			clearCache(cpDisplayLayout);
		}

		return cpDisplayLayout;
	}

	@Override
	public CPDisplayLayout updateImpl(CPDisplayLayout cpDisplayLayout) {
		boolean isNew = cpDisplayLayout.isNew();

		if (!(cpDisplayLayout instanceof CPDisplayLayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDisplayLayout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDisplayLayout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDisplayLayout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDisplayLayout implementation " +
					cpDisplayLayout.getClass());
		}

		CPDisplayLayoutModelImpl cpDisplayLayoutModelImpl =
			(CPDisplayLayoutModelImpl)cpDisplayLayout;

		if (Validator.isNull(cpDisplayLayout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDisplayLayout.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDisplayLayout.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDisplayLayout.setCreateDate(date);
			}
			else {
				cpDisplayLayout.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDisplayLayoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDisplayLayout.setModifiedDate(date);
			}
			else {
				cpDisplayLayout.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpDisplayLayout)) {
				if (!isNew) {
					session.evict(
						CPDisplayLayoutImpl.class,
						cpDisplayLayout.getPrimaryKeyObj());
				}

				session.save(cpDisplayLayout);
			}
			else {
				cpDisplayLayout = (CPDisplayLayout)session.merge(
					cpDisplayLayout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDisplayLayout, false);

		if (isNew) {
			cpDisplayLayout.setNew(false);
		}

		cpDisplayLayout.resetOriginalValues();

		return cpDisplayLayout;
	}

	/**
	 * Returns the cp display layout with the primary key or throws a <code>NoSuchCPDisplayLayoutException</code> if it could not be found.
	 *
	 * @param CPDisplayLayoutId the primary key of the cp display layout
	 * @return the cp display layout
	 * @throws NoSuchCPDisplayLayoutException if a cp display layout with the primary key could not be found
	 */
	@Override
	public CPDisplayLayout findByPrimaryKey(long CPDisplayLayoutId)
		throws NoSuchCPDisplayLayoutException {

		return findByPrimaryKey((Serializable)CPDisplayLayoutId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp display layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDisplayLayoutId the primary key of the cp display layout
	 * @return the cp display layout, or <code>null</code> if a cp display layout with the primary key could not be found
	 */
	@Override
	public CPDisplayLayout fetchByPrimaryKey(long CPDisplayLayoutId) {
		return fetchByPrimaryKey((Serializable)CPDisplayLayoutId);
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
		return "CPDisplayLayoutId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDISPLAYLAYOUT;
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
		return CPDisplayLayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPDisplayLayout";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("layoutPageTemplateEntryUuid");
		ctMergeColumnNames.add("layoutUuid");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPDisplayLayoutId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the cp display layout persistence.
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
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, _SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
			CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDisplayLayout.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPDisplayLayout::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDisplayLayout::getUuid),
				CPDisplayLayout::getGroupId),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, "",
			new FinderColumn<>(
				"cpDisplayLayout.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CPDisplayLayout::getUuid),
			new FinderColumn<>(
				"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getGroupId));

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
				_SQL_SELECT_CPDISPLAYLAYOUT_WHERE,
				_SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
				CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDisplayLayout.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDisplayLayout::getUuid),
				new FinderColumn<>(
					"cpDisplayLayout.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CPDisplayLayout::getCompanyId));

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
				_SQL_SELECT_CPDISPLAYLAYOUT_WHERE,
				_SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
				CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CPDisplayLayout::getGroupId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, _SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
			CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getGroupId),
			new FinderColumn<>(
				"cpDisplayLayout.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassNameId));

		_collectionPersistenceFinderByG_LPTEU =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LPTEU",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "layoutPageTemplateEntryUuid"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_LPTEU",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "layoutPageTemplateEntryUuid"}, 0,
					2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_LPTEU",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "layoutPageTemplateEntryUuid"}, 0,
					2, false, null),
				_SQL_SELECT_CPDISPLAYLAYOUT_WHERE,
				_SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
				CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CPDisplayLayout::getGroupId),
				new FinderColumn<>(
					"cpDisplayLayout.", "layoutPageTemplateEntryUuid",
					FinderColumn.Type.STRING, "=", true, true,
					CPDisplayLayout::getLayoutPageTemplateEntryUuid));

		_collectionPersistenceFinderByG_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "layoutUuid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "layoutUuid"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "layoutUuid"}, 0, 2, false, null),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, _SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
			CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getGroupId),
			new FinderColumn<>(
				"cpDisplayLayout.", "layoutUuid", FinderColumn.Type.STRING, "=",
				true, true, CPDisplayLayout::getLayoutUuid));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, _SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
			CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"cpDisplayLayout.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassNameId),
			new FinderColumn<>(
				"cpDisplayLayout.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassPK));

		_collectionPersistenceFinderByC_C_LPTEU =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_LPTEU",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_C_LPTEU",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_C_LPTEU",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, false),
				_SQL_SELECT_CPDISPLAYLAYOUT_WHERE,
				_SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
				CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"cpDisplayLayout.layoutPageTemplateEntryUuid IS NOT NULL",
				"cpDisplayLayout.layoutPageTemplateEntryUuid IS NOT NULL",
				new FinderColumn<>(
					"cpDisplayLayout.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, CPDisplayLayout::getClassNameId),
				new FinderColumn<>(
					"cpDisplayLayout.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, CPDisplayLayout::getClassPK));

		_collectionPersistenceFinderByC_C_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_L",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, _SQL_COUNT_CPDISPLAYLAYOUT_WHERE,
			CPDisplayLayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"cpDisplayLayout.layoutUuid IS NOT NULL",
			"cpDisplayLayout.layoutUuid IS NOT NULL",
			new FinderColumn<>(
				"cpDisplayLayout.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassNameId),
			new FinderColumn<>(
				"cpDisplayLayout.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassPK));

		_uniquePersistenceFinderByG_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, 0, 0, false,
				CPDisplayLayout::getGroupId, CPDisplayLayout::getClassNameId,
				CPDisplayLayout::getClassPK),
			_SQL_SELECT_CPDISPLAYLAYOUT_WHERE, "",
			new FinderColumn<>(
				"cpDisplayLayout.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getGroupId),
			new FinderColumn<>(
				"cpDisplayLayout.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassNameId),
			new FinderColumn<>(
				"cpDisplayLayout.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, CPDisplayLayout::getClassPK));

		CPDisplayLayoutUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDisplayLayoutUtil.setPersistence(null);

		entityCache.removeCache(CPDisplayLayoutImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CPDisplayLayoutModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDISPLAYLAYOUT =
		"SELECT cpDisplayLayout FROM CPDisplayLayout cpDisplayLayout";

	private static final String _SQL_SELECT_CPDISPLAYLAYOUT_WHERE =
		"SELECT cpDisplayLayout FROM CPDisplayLayout cpDisplayLayout WHERE ";

	private static final String _SQL_COUNT_CPDISPLAYLAYOUT_WHERE =
		"SELECT COUNT(cpDisplayLayout) FROM CPDisplayLayout cpDisplayLayout WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDisplayLayout exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDisplayLayoutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-342193945
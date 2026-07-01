/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructurePersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the layout page template structure service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateStructurePersistence.class)
public class LayoutPageTemplateStructurePersistenceImpl
	extends BasePersistenceImpl
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
	implements LayoutPageTemplateStructurePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateStructureUtil</code> to access the layout page template structure persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateStructureImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structures
	 * @param end the upper bound of the range of layout page template structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structures
	 */
	@Override
	public List<LayoutPageTemplateStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructure> orderByComparator)
		throws NoSuchPageTemplateStructureException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structures
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template structure where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template structure where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure that was removed
	 */
	@Override
	public LayoutPageTemplateStructure removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureException {

		LayoutPageTemplateStructure layoutPageTemplateStructure = findByUUID_G(
			uuid, groupId);

		return remove(layoutPageTemplateStructure);
	}

	/**
	 * Returns the number of layout page template structures where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structures
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structures
	 * @param end the upper bound of the range of layout page template structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structures
	 */
	@Override
	public List<LayoutPageTemplateStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructure> orderByComparator)
		throws NoSuchPageTemplateStructureException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structures where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structures
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout page template structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template structures
	 * @param end the upper bound of the range of layout page template structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structures
	 */
	@Override
	public List<LayoutPageTemplateStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateStructure> orderByComparator)
		throws NoSuchPageTemplateStructureException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateStructure> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structures where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of layout page template structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template structures
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructure, NoSuchPageTemplateStructureException>
			_uniquePersistenceFinderByG_P;

	/**
	 * Returns the layout page template structure where groupId = &#63; and plid = &#63; or throws a <code>NoSuchPageTemplateStructureException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByG_P(long groupId, long plid)
		throws NoSuchPageTemplateStructureException {

		return _uniquePersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, plid});
	}

	/**
	 * Returns the layout page template structure where groupId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure, or <code>null</code> if a matching layout page template structure could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByG_P(
		long groupId, long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P.fetch(
			finderCache, new Object[] {groupId, plid}, useFinderCache);
	}

	/**
	 * Removes the layout page template structure where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the layout page template structure that was removed
	 */
	@Override
	public LayoutPageTemplateStructure removeByG_P(long groupId, long plid)
		throws NoSuchPageTemplateStructureException {

		LayoutPageTemplateStructure layoutPageTemplateStructure = findByG_P(
			groupId, plid);

		return remove(layoutPageTemplateStructure);
	}

	/**
	 * Returns the number of layout page template structures where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching layout page template structures
	 */
	@Override
	public int countByG_P(long groupId, long plid) {
		return _uniquePersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, plid});
	}

	public LayoutPageTemplateStructurePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateStructure.class);

		setModelImplClass(LayoutPageTemplateStructureImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateStructureTable.INSTANCE);
	}

	/**
	 * Creates a new layout page template structure with the primary key. Does not add the layout page template structure to the database.
	 *
	 * @param layoutPageTemplateStructureId the primary key for the new layout page template structure
	 * @return the new layout page template structure
	 */
	@Override
	public LayoutPageTemplateStructure create(
		long layoutPageTemplateStructureId) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			new LayoutPageTemplateStructureImpl();

		layoutPageTemplateStructure.setNew(true);
		layoutPageTemplateStructure.setPrimaryKey(
			layoutPageTemplateStructureId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateStructure.setUuid(uuid);

		layoutPageTemplateStructure.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateStructure;
	}

	/**
	 * Removes the layout page template structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureId the primary key of the layout page template structure
	 * @return the layout page template structure that was removed
	 * @throws NoSuchPageTemplateStructureException if a layout page template structure with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructure remove(
			long layoutPageTemplateStructureId)
		throws NoSuchPageTemplateStructureException {

		return remove((Serializable)layoutPageTemplateStructureId);
	}

	@Override
	protected LayoutPageTemplateStructure removeImpl(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateStructure)) {
				layoutPageTemplateStructure =
					(LayoutPageTemplateStructure)session.get(
						LayoutPageTemplateStructureImpl.class,
						layoutPageTemplateStructure.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateStructure != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateStructure)) {

				session.delete(layoutPageTemplateStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateStructure != null) {
			clearCache(layoutPageTemplateStructure);
		}

		return layoutPageTemplateStructure;
	}

	@Override
	public LayoutPageTemplateStructure updateImpl(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		boolean isNew = layoutPageTemplateStructure.isNew();

		if (!(layoutPageTemplateStructure instanceof
				LayoutPageTemplateStructureModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateStructure.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateStructure);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateStructure proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateStructure implementation " +
					layoutPageTemplateStructure.getClass());
		}

		LayoutPageTemplateStructureModelImpl
			layoutPageTemplateStructureModelImpl =
				(LayoutPageTemplateStructureModelImpl)
					layoutPageTemplateStructure;

		if (Validator.isNull(layoutPageTemplateStructure.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateStructure.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateStructure.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateStructure.setCreateDate(date);
			}
			else {
				layoutPageTemplateStructure.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateStructureModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateStructure.setModifiedDate(date);
			}
			else {
				layoutPageTemplateStructure.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateStructure)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateStructureImpl.class,
						layoutPageTemplateStructure.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateStructure);
			}
			else {
				layoutPageTemplateStructure =
					(LayoutPageTemplateStructure)session.merge(
						layoutPageTemplateStructure);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutPageTemplateStructure, false);

		if (isNew) {
			layoutPageTemplateStructure.setNew(false);
		}

		layoutPageTemplateStructure.resetOriginalValues();

		return layoutPageTemplateStructure;
	}

	/**
	 * Returns the layout page template structure with the primary key or throws a <code>NoSuchPageTemplateStructureException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureId the primary key of the layout page template structure
	 * @return the layout page template structure
	 * @throws NoSuchPageTemplateStructureException if a layout page template structure with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructure findByPrimaryKey(
			long layoutPageTemplateStructureId)
		throws NoSuchPageTemplateStructureException {

		return findByPrimaryKey((Serializable)layoutPageTemplateStructureId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureId the primary key of the layout page template structure
	 * @return the layout page template structure, or <code>null</code> if a layout page template structure with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructure fetchByPrimaryKey(
		long layoutPageTemplateStructureId) {

		return fetchByPrimaryKey((Serializable)layoutPageTemplateStructureId);
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
		return "layoutPageTemplateStructureId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE;
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
		return LayoutPageTemplateStructureModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateStructure";
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
		ctMergeColumnNames.add("plid");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutPageTemplateStructureId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "plid"});
	}

	/**
	 * Initializes the layout page template structure persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
			LayoutPageTemplateStructureModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"layoutPageTemplateStructure.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructure::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutPageTemplateStructure::getUuid),
				LayoutPageTemplateStructure::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateStructure.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructure::getUuid),
			new FinderColumn<>(
				"layoutPageTemplateStructure.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructure::getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
				LayoutPageTemplateStructureModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateStructure.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateStructure::getUuid),
				new FinderColumn<>(
					"layoutPageTemplateStructure.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructure::getCompanyId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE,
				LayoutPageTemplateStructureModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutPageTemplateStructure.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructure::getGroupId));

		_uniquePersistenceFinderByG_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "plid"}, 0, 0, false,
				LayoutPageTemplateStructure::getGroupId,
				LayoutPageTemplateStructure::getPlid),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateStructure.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructure::getGroupId),
			new FinderColumn<>(
				"layoutPageTemplateStructure.", "plid", FinderColumn.Type.LONG,
				"=", true, true, LayoutPageTemplateStructure::getPlid));

		LayoutPageTemplateStructureUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateStructureUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateStructureImpl.class.getName());
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
		LayoutPageTemplateStructureModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE =
		"SELECT layoutPageTemplateStructure FROM LayoutPageTemplateStructure layoutPageTemplateStructure";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE =
		"SELECT layoutPageTemplateStructure FROM LayoutPageTemplateStructure layoutPageTemplateStructure WHERE ";

	private static final String _SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURE_WHERE =
		"SELECT COUNT(layoutPageTemplateStructure) FROM LayoutPageTemplateStructure layoutPageTemplateStructure WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1626589071
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

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
import com.liferay.segments.exception.NoSuchEntryRelException;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.model.SegmentsEntryRelTable;
import com.liferay.segments.model.impl.SegmentsEntryRelImpl;
import com.liferay.segments.model.impl.SegmentsEntryRelModelImpl;
import com.liferay.segments.service.persistence.SegmentsEntryRelPersistence;
import com.liferay.segments.service.persistence.SegmentsEntryRelUtil;
import com.liferay.segments.service.persistence.impl.constants.SegmentsPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
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
 * The persistence implementation for the segments entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsEntryRelPersistence.class)
public class SegmentsEntryRelPersistenceImpl
	extends BasePersistenceImpl<SegmentsEntryRel, NoSuchEntryRelException>
	implements SegmentsEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsEntryRelUtil</code> to access the segments entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsEntryRel, NoSuchEntryRelException>
			_collectionPersistenceFinderBySegmentsEntryId;

	/**
	 * Returns an ordered range of all the segments entry rels where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of segments entry rels
	 * @param end the upper bound of the range of segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments entry rels
	 */
	@Override
	public List<SegmentsEntryRel> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsEntryId.find(
			finderCache, new Object[] {segmentsEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel
	 * @throws NoSuchEntryRelException if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel findBySegmentsEntryId_First(
			long segmentsEntryId,
			OrderByComparator<SegmentsEntryRel> orderByComparator)
		throws NoSuchEntryRelException {

		return _collectionPersistenceFinderBySegmentsEntryId.findFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel, or <code>null</code> if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel fetchBySegmentsEntryId_First(
		long segmentsEntryId,
		OrderByComparator<SegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsEntryId.fetchFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Removes all the segments entry rels where segmentsEntryId = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeBySegmentsEntryId(long segmentsEntryId) {
		_collectionPersistenceFinderBySegmentsEntryId.remove(
			finderCache, new Object[] {segmentsEntryId});
	}

	/**
	 * Returns the number of segments entry rels where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching segments entry rels
	 */
	@Override
	public int countBySegmentsEntryId(long segmentsEntryId) {
		return _collectionPersistenceFinderBySegmentsEntryId.count(
			finderCache, new Object[] {segmentsEntryId});
	}

	private CollectionPersistenceFinder
		<SegmentsEntryRel, NoSuchEntryRelException>
			_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the segments entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of segments entry rels
	 * @param end the upper bound of the range of segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments entry rels
	 */
	@Override
	public List<SegmentsEntryRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel
	 * @throws NoSuchEntryRelException if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<SegmentsEntryRel> orderByComparator)
		throws NoSuchEntryRelException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel, or <code>null</code> if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<SegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the segments entry rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(long classNameId, long classPK) {
		_collectionPersistenceFinderByCN_CPK.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of segments entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching segments entry rels
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<SegmentsEntryRel, NoSuchEntryRelException>
			_collectionPersistenceFinderByG_CN_CPK;

	/**
	 * Returns an ordered range of all the segments entry rels where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of segments entry rels
	 * @param end the upper bound of the range of segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments entry rels
	 */
	@Override
	public List<SegmentsEntryRel> findByG_CN_CPK(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<SegmentsEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_CN_CPK.find(
			finderCache, new Object[] {groupId, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel
	 * @throws NoSuchEntryRelException if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel findByG_CN_CPK_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<SegmentsEntryRel> orderByComparator)
		throws NoSuchEntryRelException {

		return _collectionPersistenceFinderByG_CN_CPK.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first segments entry rel in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry rel, or <code>null</code> if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel fetchByG_CN_CPK_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<SegmentsEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByG_CN_CPK.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the segments entry rels where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_CN_CPK(long groupId, long classNameId, long classPK) {
		_collectionPersistenceFinderByG_CN_CPK.remove(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the number of segments entry rels where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching segments entry rels
	 */
	@Override
	public int countByG_CN_CPK(long groupId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_CN_CPK.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	private UniquePersistenceFinder<SegmentsEntryRel, NoSuchEntryRelException>
		_uniquePersistenceFinderByS_CN_CPK;

	/**
	 * Returns the segments entry rel where segmentsEntryId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryRelException</code> if it could not be found.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching segments entry rel
	 * @throws NoSuchEntryRelException if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel findByS_CN_CPK(
			long segmentsEntryId, long classNameId, long classPK)
		throws NoSuchEntryRelException {

		return _uniquePersistenceFinderByS_CN_CPK.find(
			finderCache, new Object[] {segmentsEntryId, classNameId, classPK});
	}

	/**
	 * Returns the segments entry rel where segmentsEntryId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments entry rel, or <code>null</code> if a matching segments entry rel could not be found
	 */
	@Override
	public SegmentsEntryRel fetchByS_CN_CPK(
		long segmentsEntryId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByS_CN_CPK.fetch(
			finderCache, new Object[] {segmentsEntryId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the segments entry rel where segmentsEntryId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the segments entry rel that was removed
	 */
	@Override
	public SegmentsEntryRel removeByS_CN_CPK(
			long segmentsEntryId, long classNameId, long classPK)
		throws NoSuchEntryRelException {

		SegmentsEntryRel segmentsEntryRel = findByS_CN_CPK(
			segmentsEntryId, classNameId, classPK);

		return remove(segmentsEntryRel);
	}

	/**
	 * Returns the number of segments entry rels where segmentsEntryId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching segments entry rels
	 */
	@Override
	public int countByS_CN_CPK(
		long segmentsEntryId, long classNameId, long classPK) {

		return _uniquePersistenceFinderByS_CN_CPK.count(
			finderCache, new Object[] {segmentsEntryId, classNameId, classPK});
	}

	public SegmentsEntryRelPersistenceImpl() {
		setModelClass(SegmentsEntryRel.class);

		setModelImplClass(SegmentsEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new segments entry rel with the primary key. Does not add the segments entry rel to the database.
	 *
	 * @param segmentsEntryRelId the primary key for the new segments entry rel
	 * @return the new segments entry rel
	 */
	@Override
	public SegmentsEntryRel create(long segmentsEntryRelId) {
		SegmentsEntryRel segmentsEntryRel = new SegmentsEntryRelImpl();

		segmentsEntryRel.setNew(true);
		segmentsEntryRel.setPrimaryKey(segmentsEntryRelId);

		segmentsEntryRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsEntryRel;
	}

	/**
	 * Removes the segments entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsEntryRelId the primary key of the segments entry rel
	 * @return the segments entry rel that was removed
	 * @throws NoSuchEntryRelException if a segments entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRel remove(long segmentsEntryRelId)
		throws NoSuchEntryRelException {

		return remove((Serializable)segmentsEntryRelId);
	}

	@Override
	protected SegmentsEntryRel removeImpl(SegmentsEntryRel segmentsEntryRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsEntryRel)) {
				segmentsEntryRel = (SegmentsEntryRel)session.get(
					SegmentsEntryRelImpl.class,
					segmentsEntryRel.getPrimaryKeyObj());
			}

			if ((segmentsEntryRel != null) &&
				ctPersistenceHelper.isRemove(segmentsEntryRel)) {

				session.delete(segmentsEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsEntryRel != null) {
			clearCache(segmentsEntryRel);
		}

		return segmentsEntryRel;
	}

	@Override
	public SegmentsEntryRel updateImpl(SegmentsEntryRel segmentsEntryRel) {
		boolean isNew = segmentsEntryRel.isNew();

		if (!(segmentsEntryRel instanceof SegmentsEntryRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsEntryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsEntryRel implementation " +
					segmentsEntryRel.getClass());
		}

		SegmentsEntryRelModelImpl segmentsEntryRelModelImpl =
			(SegmentsEntryRelModelImpl)segmentsEntryRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsEntryRel.setCreateDate(date);
			}
			else {
				segmentsEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsEntryRel.setModifiedDate(date);
			}
			else {
				segmentsEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsEntryRel)) {
				if (!isNew) {
					session.evict(
						SegmentsEntryRelImpl.class,
						segmentsEntryRel.getPrimaryKeyObj());
				}

				session.save(segmentsEntryRel);
			}
			else {
				segmentsEntryRel = (SegmentsEntryRel)session.merge(
					segmentsEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsEntryRel, false);

		if (isNew) {
			segmentsEntryRel.setNew(false);
		}

		segmentsEntryRel.resetOriginalValues();

		return segmentsEntryRel;
	}

	/**
	 * Returns the segments entry rel with the primary key or throws a <code>NoSuchEntryRelException</code> if it could not be found.
	 *
	 * @param segmentsEntryRelId the primary key of the segments entry rel
	 * @return the segments entry rel
	 * @throws NoSuchEntryRelException if a segments entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRel findByPrimaryKey(long segmentsEntryRelId)
		throws NoSuchEntryRelException {

		return findByPrimaryKey((Serializable)segmentsEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsEntryRelId the primary key of the segments entry rel
	 * @return the segments entry rel, or <code>null</code> if a segments entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRel fetchByPrimaryKey(long segmentsEntryRelId) {
		return fetchByPrimaryKey((Serializable)segmentsEntryRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "segmentsEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSENTRYREL;
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
		return SegmentsEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsEntryRel";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("segmentsEntryId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"segmentsEntryId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the segments entry rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderBySegmentsEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, false),
				_SQL_SELECT_SEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_SEGMENTSENTRYREL_WHERE,
				SegmentsEntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsEntryRel.", "segmentsEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					SegmentsEntryRel::getSegmentsEntryId));

		_collectionPersistenceFinderByCN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, false),
				_SQL_SELECT_SEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_SEGMENTSENTRYREL_WHERE,
				SegmentsEntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsEntryRel.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsEntryRel::getClassNameId),
				new FinderColumn<>(
					"segmentsEntryRel.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SegmentsEntryRel::getClassPK));

		_collectionPersistenceFinderByG_CN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, false),
				_SQL_SELECT_SEGMENTSENTRYREL_WHERE,
				_SQL_COUNT_SEGMENTSENTRYREL_WHERE,
				SegmentsEntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"segmentsEntryRel.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, SegmentsEntryRel::getGroupId),
				new FinderColumn<>(
					"segmentsEntryRel.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SegmentsEntryRel::getClassNameId),
				new FinderColumn<>(
					"segmentsEntryRel.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SegmentsEntryRel::getClassPK));

		_uniquePersistenceFinderByS_CN_CPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_CN_CPK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"segmentsEntryId", "classNameId", "classPK"}, 0,
				0, false, SegmentsEntryRel::getSegmentsEntryId,
				SegmentsEntryRel::getClassNameId, SegmentsEntryRel::getClassPK),
			_SQL_SELECT_SEGMENTSENTRYREL_WHERE, "",
			new FinderColumn<>(
				"segmentsEntryRel.", "segmentsEntryId", FinderColumn.Type.LONG,
				"=", true, true, SegmentsEntryRel::getSegmentsEntryId),
			new FinderColumn<>(
				"segmentsEntryRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsEntryRel::getClassNameId),
			new FinderColumn<>(
				"segmentsEntryRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, SegmentsEntryRel::getClassPK));

		SegmentsEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsEntryRelUtil.setPersistence(null);

		entityCache.removeCache(SegmentsEntryRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SegmentsEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSENTRYREL =
		"SELECT segmentsEntryRel FROM SegmentsEntryRel segmentsEntryRel";

	private static final String _SQL_SELECT_SEGMENTSENTRYREL_WHERE =
		"SELECT segmentsEntryRel FROM SegmentsEntryRel segmentsEntryRel WHERE ";

	private static final String _SQL_COUNT_SEGMENTSENTRYREL_WHERE =
		"SELECT COUNT(segmentsEntryRel) FROM SegmentsEntryRel segmentsEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SegmentsEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntryRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1811039692
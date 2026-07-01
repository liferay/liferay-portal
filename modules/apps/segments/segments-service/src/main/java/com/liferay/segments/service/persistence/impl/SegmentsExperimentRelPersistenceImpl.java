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
import com.liferay.segments.exception.NoSuchExperimentRelException;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.model.SegmentsExperimentRelTable;
import com.liferay.segments.model.impl.SegmentsExperimentRelImpl;
import com.liferay.segments.model.impl.SegmentsExperimentRelModelImpl;
import com.liferay.segments.service.persistence.SegmentsExperimentRelPersistence;
import com.liferay.segments.service.persistence.SegmentsExperimentRelUtil;
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
 * The persistence implementation for the segments experiment rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsExperimentRelPersistence.class)
public class SegmentsExperimentRelPersistenceImpl
	extends BasePersistenceImpl
		<SegmentsExperimentRel, NoSuchExperimentRelException>
	implements SegmentsExperimentRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsExperimentRelUtil</code> to access the segments experiment rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsExperimentRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsExperimentRel, NoSuchExperimentRelException>
			_collectionPersistenceFinderBySegmentsExperimentId;

	/**
	 * Returns an ordered range of all the segments experiment rels where segmentsExperimentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param start the lower bound of the range of segments experiment rels
	 * @param end the upper bound of the range of segments experiment rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiment rels
	 */
	@Override
	public List<SegmentsExperimentRel> findBySegmentsExperimentId(
		long segmentsExperimentId, int start, int end,
		OrderByComparator<SegmentsExperimentRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperimentId.find(
			finderCache, new Object[] {segmentsExperimentId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experiment rel in the ordered set where segmentsExperimentId = &#63;.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment rel
	 * @throws NoSuchExperimentRelException if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel findBySegmentsExperimentId_First(
			long segmentsExperimentId,
			OrderByComparator<SegmentsExperimentRel> orderByComparator)
		throws NoSuchExperimentRelException {

		return _collectionPersistenceFinderBySegmentsExperimentId.findFirst(
			finderCache, new Object[] {segmentsExperimentId},
			orderByComparator);
	}

	/**
	 * Returns the first segments experiment rel in the ordered set where segmentsExperimentId = &#63;.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment rel, or <code>null</code> if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel fetchBySegmentsExperimentId_First(
		long segmentsExperimentId,
		OrderByComparator<SegmentsExperimentRel> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperimentId.fetchFirst(
			finderCache, new Object[] {segmentsExperimentId},
			orderByComparator);
	}

	/**
	 * Removes all the segments experiment rels where segmentsExperimentId = &#63; from the database.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 */
	@Override
	public void removeBySegmentsExperimentId(long segmentsExperimentId) {
		_collectionPersistenceFinderBySegmentsExperimentId.remove(
			finderCache, new Object[] {segmentsExperimentId});
	}

	/**
	 * Returns the number of segments experiment rels where segmentsExperimentId = &#63;.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @return the number of matching segments experiment rels
	 */
	@Override
	public int countBySegmentsExperimentId(long segmentsExperimentId) {
		return _collectionPersistenceFinderBySegmentsExperimentId.count(
			finderCache, new Object[] {segmentsExperimentId});
	}

	private CollectionPersistenceFinder
		<SegmentsExperimentRel, NoSuchExperimentRelException>
			_collectionPersistenceFinderBySegmentsExperienceId;

	/**
	 * Returns an ordered range of all the segments experiment rels where segmentsExperienceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperimentRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param start the lower bound of the range of segments experiment rels
	 * @param end the upper bound of the range of segments experiment rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiment rels
	 */
	@Override
	public List<SegmentsExperimentRel> findBySegmentsExperienceId(
		long segmentsExperienceId, int start, int end,
		OrderByComparator<SegmentsExperimentRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperienceId.find(
			finderCache, new Object[] {segmentsExperienceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experiment rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment rel
	 * @throws NoSuchExperimentRelException if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel findBySegmentsExperienceId_First(
			long segmentsExperienceId,
			OrderByComparator<SegmentsExperimentRel> orderByComparator)
		throws NoSuchExperimentRelException {

		return _collectionPersistenceFinderBySegmentsExperienceId.findFirst(
			finderCache, new Object[] {segmentsExperienceId},
			orderByComparator);
	}

	/**
	 * Returns the first segments experiment rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experiment rel, or <code>null</code> if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel fetchBySegmentsExperienceId_First(
		long segmentsExperienceId,
		OrderByComparator<SegmentsExperimentRel> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperienceId.fetchFirst(
			finderCache, new Object[] {segmentsExperienceId},
			orderByComparator);
	}

	/**
	 * Removes all the segments experiment rels where segmentsExperienceId = &#63; from the database.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 */
	@Override
	public void removeBySegmentsExperienceId(long segmentsExperienceId) {
		_collectionPersistenceFinderBySegmentsExperienceId.remove(
			finderCache, new Object[] {segmentsExperienceId});
	}

	/**
	 * Returns the number of segments experiment rels where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching segments experiment rels
	 */
	@Override
	public int countBySegmentsExperienceId(long segmentsExperienceId) {
		return _collectionPersistenceFinderBySegmentsExperienceId.count(
			finderCache, new Object[] {segmentsExperienceId});
	}

	private UniquePersistenceFinder
		<SegmentsExperimentRel, NoSuchExperimentRelException>
			_uniquePersistenceFinderByS_S;

	/**
	 * Returns the segments experiment rel where segmentsExperimentId = &#63; and segmentsExperienceId = &#63; or throws a <code>NoSuchExperimentRelException</code> if it could not be found.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the matching segments experiment rel
	 * @throws NoSuchExperimentRelException if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel findByS_S(
			long segmentsExperimentId, long segmentsExperienceId)
		throws NoSuchExperimentRelException {

		return _uniquePersistenceFinderByS_S.find(
			finderCache,
			new Object[] {segmentsExperimentId, segmentsExperienceId});
	}

	/**
	 * Returns the segments experiment rel where segmentsExperimentId = &#63; and segmentsExperienceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experiment rel, or <code>null</code> if a matching segments experiment rel could not be found
	 */
	@Override
	public SegmentsExperimentRel fetchByS_S(
		long segmentsExperimentId, long segmentsExperienceId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByS_S.fetch(
			finderCache,
			new Object[] {segmentsExperimentId, segmentsExperienceId},
			useFinderCache);
	}

	/**
	 * Removes the segments experiment rel where segmentsExperimentId = &#63; and segmentsExperienceId = &#63; from the database.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the segments experiment rel that was removed
	 */
	@Override
	public SegmentsExperimentRel removeByS_S(
			long segmentsExperimentId, long segmentsExperienceId)
		throws NoSuchExperimentRelException {

		SegmentsExperimentRel segmentsExperimentRel = findByS_S(
			segmentsExperimentId, segmentsExperienceId);

		return remove(segmentsExperimentRel);
	}

	/**
	 * Returns the number of segments experiment rels where segmentsExperimentId = &#63; and segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperimentId the segments experiment ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching segments experiment rels
	 */
	@Override
	public int countByS_S(
		long segmentsExperimentId, long segmentsExperienceId) {

		return _uniquePersistenceFinderByS_S.count(
			finderCache,
			new Object[] {segmentsExperimentId, segmentsExperienceId});
	}

	public SegmentsExperimentRelPersistenceImpl() {
		setModelClass(SegmentsExperimentRel.class);

		setModelImplClass(SegmentsExperimentRelImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsExperimentRelTable.INSTANCE);
	}

	/**
	 * Creates a new segments experiment rel with the primary key. Does not add the segments experiment rel to the database.
	 *
	 * @param segmentsExperimentRelId the primary key for the new segments experiment rel
	 * @return the new segments experiment rel
	 */
	@Override
	public SegmentsExperimentRel create(long segmentsExperimentRelId) {
		SegmentsExperimentRel segmentsExperimentRel =
			new SegmentsExperimentRelImpl();

		segmentsExperimentRel.setNew(true);
		segmentsExperimentRel.setPrimaryKey(segmentsExperimentRelId);

		segmentsExperimentRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsExperimentRel;
	}

	/**
	 * Removes the segments experiment rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperimentRelId the primary key of the segments experiment rel
	 * @return the segments experiment rel that was removed
	 * @throws NoSuchExperimentRelException if a segments experiment rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperimentRel remove(long segmentsExperimentRelId)
		throws NoSuchExperimentRelException {

		return remove((Serializable)segmentsExperimentRelId);
	}

	@Override
	protected SegmentsExperimentRel removeImpl(
		SegmentsExperimentRel segmentsExperimentRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsExperimentRel)) {
				segmentsExperimentRel = (SegmentsExperimentRel)session.get(
					SegmentsExperimentRelImpl.class,
					segmentsExperimentRel.getPrimaryKeyObj());
			}

			if ((segmentsExperimentRel != null) &&
				ctPersistenceHelper.isRemove(segmentsExperimentRel)) {

				session.delete(segmentsExperimentRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsExperimentRel != null) {
			clearCache(segmentsExperimentRel);
		}

		return segmentsExperimentRel;
	}

	@Override
	public SegmentsExperimentRel updateImpl(
		SegmentsExperimentRel segmentsExperimentRel) {

		boolean isNew = segmentsExperimentRel.isNew();

		if (!(segmentsExperimentRel instanceof
				SegmentsExperimentRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsExperimentRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsExperimentRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsExperimentRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsExperimentRel implementation " +
					segmentsExperimentRel.getClass());
		}

		SegmentsExperimentRelModelImpl segmentsExperimentRelModelImpl =
			(SegmentsExperimentRelModelImpl)segmentsExperimentRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsExperimentRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsExperimentRel.setCreateDate(date);
			}
			else {
				segmentsExperimentRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsExperimentRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsExperimentRel.setModifiedDate(date);
			}
			else {
				segmentsExperimentRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsExperimentRel)) {
				if (!isNew) {
					session.evict(
						SegmentsExperimentRelImpl.class,
						segmentsExperimentRel.getPrimaryKeyObj());
				}

				session.save(segmentsExperimentRel);
			}
			else {
				segmentsExperimentRel = (SegmentsExperimentRel)session.merge(
					segmentsExperimentRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsExperimentRel, false);

		if (isNew) {
			segmentsExperimentRel.setNew(false);
		}

		segmentsExperimentRel.resetOriginalValues();

		return segmentsExperimentRel;
	}

	/**
	 * Returns the segments experiment rel with the primary key or throws a <code>NoSuchExperimentRelException</code> if it could not be found.
	 *
	 * @param segmentsExperimentRelId the primary key of the segments experiment rel
	 * @return the segments experiment rel
	 * @throws NoSuchExperimentRelException if a segments experiment rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperimentRel findByPrimaryKey(long segmentsExperimentRelId)
		throws NoSuchExperimentRelException {

		return findByPrimaryKey((Serializable)segmentsExperimentRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments experiment rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperimentRelId the primary key of the segments experiment rel
	 * @return the segments experiment rel, or <code>null</code> if a segments experiment rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperimentRel fetchByPrimaryKey(
		long segmentsExperimentRelId) {

		return fetchByPrimaryKey((Serializable)segmentsExperimentRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "segmentsExperimentRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSEXPERIMENTREL;
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
		return SegmentsExperimentRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsExperimentRel";
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
		ctMergeColumnNames.add("segmentsExperimentId");
		ctMergeColumnNames.add("segmentsExperienceId");
		ctMergeColumnNames.add("split");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsExperimentRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"segmentsExperimentId", "segmentsExperienceId"});
	}

	/**
	 * Initializes the segments experiment rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderBySegmentsExperimentId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperimentId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperimentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperimentId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperimentId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperimentId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperimentId"}, false),
				_SQL_SELECT_SEGMENTSEXPERIMENTREL_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIMENTREL_WHERE,
				SegmentsExperimentRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"segmentsExperimentRel.", "segmentsExperimentId",
					FinderColumn.Type.LONG, "=", true, true,
					SegmentsExperimentRel::getSegmentsExperimentId));

		_collectionPersistenceFinderBySegmentsExperienceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperienceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperienceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperienceId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperienceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperienceId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperienceId"}, false),
				_SQL_SELECT_SEGMENTSEXPERIMENTREL_WHERE,
				_SQL_COUNT_SEGMENTSEXPERIMENTREL_WHERE,
				SegmentsExperimentRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"segmentsExperimentRel.", "segmentsExperienceId",
					FinderColumn.Type.LONG, "=", true, true,
					SegmentsExperimentRel::getSegmentsExperienceId));

		_uniquePersistenceFinderByS_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"segmentsExperimentId", "segmentsExperienceId"},
				0, 0, false, SegmentsExperimentRel::getSegmentsExperimentId,
				SegmentsExperimentRel::getSegmentsExperienceId),
			_SQL_SELECT_SEGMENTSEXPERIMENTREL_WHERE, "",
			new FinderColumn<>(
				"segmentsExperimentRel.", "segmentsExperimentId",
				FinderColumn.Type.LONG, "=", true, true,
				SegmentsExperimentRel::getSegmentsExperimentId),
			new FinderColumn<>(
				"segmentsExperimentRel.", "segmentsExperienceId",
				FinderColumn.Type.LONG, "=", true, true,
				SegmentsExperimentRel::getSegmentsExperienceId));

		SegmentsExperimentRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsExperimentRelUtil.setPersistence(null);

		entityCache.removeCache(SegmentsExperimentRelImpl.class.getName());
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
		SegmentsExperimentRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSEXPERIMENTREL =
		"SELECT segmentsExperimentRel FROM SegmentsExperimentRel segmentsExperimentRel";

	private static final String _SQL_SELECT_SEGMENTSEXPERIMENTREL_WHERE =
		"SELECT segmentsExperimentRel FROM SegmentsExperimentRel segmentsExperimentRel WHERE ";

	private static final String _SQL_COUNT_SEGMENTSEXPERIMENTREL_WHERE =
		"SELECT COUNT(segmentsExperimentRel) FROM SegmentsExperimentRel segmentsExperimentRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1609627544
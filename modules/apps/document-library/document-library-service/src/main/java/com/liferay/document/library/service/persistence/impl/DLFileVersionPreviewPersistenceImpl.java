/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.persistence.impl;

import com.liferay.document.library.exception.NoSuchFileVersionPreviewException;
import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.document.library.model.DLFileVersionPreviewTable;
import com.liferay.document.library.model.impl.DLFileVersionPreviewImpl;
import com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl;
import com.liferay.document.library.service.persistence.DLFileVersionPreviewPersistence;
import com.liferay.document.library.service.persistence.DLFileVersionPreviewUtil;
import com.liferay.document.library.service.persistence.impl.constants.DLPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
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
 * The persistence implementation for the dl file version preview service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DLFileVersionPreviewPersistence.class)
public class DLFileVersionPreviewPersistenceImpl
	extends BasePersistenceImpl
		<DLFileVersionPreview, NoSuchFileVersionPreviewException>
	implements DLFileVersionPreviewPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLFileVersionPreviewUtil</code> to access the dl file version preview persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLFileVersionPreviewImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DLFileVersionPreview, NoSuchFileVersionPreviewException>
			_collectionPersistenceFinderByFileEntryId;

	/**
	 * Returns an ordered range of all the dl file version previews where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dl file version previews
	 */
	@Override
	public List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<DLFileVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileEntryId.find(
			finderCache, new Object[] {fileEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<DLFileVersionPreview> orderByComparator)
		throws NoSuchFileVersionPreviewException {

		return _collectionPersistenceFinderByFileEntryId.findFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview fetchByFileEntryId_First(
		long fileEntryId,
		OrderByComparator<DLFileVersionPreview> orderByComparator) {

		return _collectionPersistenceFinderByFileEntryId.fetchFirst(
			finderCache, new Object[] {fileEntryId}, orderByComparator);
	}

	/**
	 * Removes all the dl file version previews where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	@Override
	public void removeByFileEntryId(long fileEntryId) {
		_collectionPersistenceFinderByFileEntryId.remove(
			finderCache, new Object[] {fileEntryId});
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching dl file version previews
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _collectionPersistenceFinderByFileEntryId.count(
			finderCache, new Object[] {fileEntryId});
	}

	private CollectionPersistenceFinder
		<DLFileVersionPreview, NoSuchFileVersionPreviewException>
			_collectionPersistenceFinderByFileVersionId;

	/**
	 * Returns an ordered range of all the dl file version previews where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dl file version previews
	 */
	@Override
	public List<DLFileVersionPreview> findByFileVersionId(
		long fileVersionId, int start, int end,
		OrderByComparator<DLFileVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFileVersionId.find(
			finderCache, new Object[] {fileVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview findByFileVersionId_First(
			long fileVersionId,
			OrderByComparator<DLFileVersionPreview> orderByComparator)
		throws NoSuchFileVersionPreviewException {

		return _collectionPersistenceFinderByFileVersionId.findFirst(
			finderCache, new Object[] {fileVersionId}, orderByComparator);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview fetchByFileVersionId_First(
		long fileVersionId,
		OrderByComparator<DLFileVersionPreview> orderByComparator) {

		return _collectionPersistenceFinderByFileVersionId.fetchFirst(
			finderCache, new Object[] {fileVersionId}, orderByComparator);
	}

	/**
	 * Removes all the dl file version previews where fileVersionId = &#63; from the database.
	 *
	 * @param fileVersionId the file version ID
	 */
	@Override
	public void removeByFileVersionId(long fileVersionId) {
		_collectionPersistenceFinderByFileVersionId.remove(
			finderCache, new Object[] {fileVersionId});
	}

	/**
	 * Returns the number of dl file version previews where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the number of matching dl file version previews
	 */
	@Override
	public int countByFileVersionId(long fileVersionId) {
		return _collectionPersistenceFinderByFileVersionId.count(
			finderCache, new Object[] {fileVersionId});
	}

	private UniquePersistenceFinder
		<DLFileVersionPreview, NoSuchFileVersionPreviewException>
			_uniquePersistenceFinderByF_F;

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview findByF_F(long fileEntryId, long fileVersionId)
		throws NoSuchFileVersionPreviewException {

		return _uniquePersistenceFinderByF_F.find(
			finderCache, new Object[] {fileEntryId, fileVersionId});
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview fetchByF_F(
		long fileEntryId, long fileVersionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByF_F.fetch(
			finderCache, new Object[] {fileEntryId, fileVersionId},
			useFinderCache);
	}

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the dl file version preview that was removed
	 */
	@Override
	public DLFileVersionPreview removeByF_F(
			long fileEntryId, long fileVersionId)
		throws NoSuchFileVersionPreviewException {

		DLFileVersionPreview dlFileVersionPreview = findByF_F(
			fileEntryId, fileVersionId);

		return remove(dlFileVersionPreview);
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the number of matching dl file version previews
	 */
	@Override
	public int countByF_F(long fileEntryId, long fileVersionId) {
		return _uniquePersistenceFinderByF_F.count(
			finderCache, new Object[] {fileEntryId, fileVersionId});
	}

	private UniquePersistenceFinder
		<DLFileVersionPreview, NoSuchFileVersionPreviewException>
			_uniquePersistenceFinderByF_F_P;

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview findByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws NoSuchFileVersionPreviewException {

		return _uniquePersistenceFinderByF_F_P.find(
			finderCache,
			new Object[] {fileEntryId, fileVersionId, previewStatus});
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	@Override
	public DLFileVersionPreview fetchByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByF_F_P.fetch(
			finderCache,
			new Object[] {fileEntryId, fileVersionId, previewStatus},
			useFinderCache);
	}

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the dl file version preview that was removed
	 */
	@Override
	public DLFileVersionPreview removeByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws NoSuchFileVersionPreviewException {

		DLFileVersionPreview dlFileVersionPreview = findByF_F_P(
			fileEntryId, fileVersionId, previewStatus);

		return remove(dlFileVersionPreview);
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the number of matching dl file version previews
	 */
	@Override
	public int countByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return _uniquePersistenceFinderByF_F_P.count(
			finderCache,
			new Object[] {fileEntryId, fileVersionId, previewStatus});
	}

	public DLFileVersionPreviewPersistenceImpl() {
		setModelClass(DLFileVersionPreview.class);

		setModelImplClass(DLFileVersionPreviewImpl.class);
		setModelPKClass(long.class);

		setTable(DLFileVersionPreviewTable.INSTANCE);
	}

	/**
	 * Creates a new dl file version preview with the primary key. Does not add the dl file version preview to the database.
	 *
	 * @param dlFileVersionPreviewId the primary key for the new dl file version preview
	 * @return the new dl file version preview
	 */
	@Override
	public DLFileVersionPreview create(long dlFileVersionPreviewId) {
		DLFileVersionPreview dlFileVersionPreview =
			new DLFileVersionPreviewImpl();

		dlFileVersionPreview.setNew(true);
		dlFileVersionPreview.setPrimaryKey(dlFileVersionPreviewId);

		dlFileVersionPreview.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlFileVersionPreview;
	}

	/**
	 * Removes the dl file version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview that was removed
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	@Override
	public DLFileVersionPreview remove(long dlFileVersionPreviewId)
		throws NoSuchFileVersionPreviewException {

		return remove((Serializable)dlFileVersionPreviewId);
	}

	@Override
	protected DLFileVersionPreview removeImpl(
		DLFileVersionPreview dlFileVersionPreview) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlFileVersionPreview)) {
				dlFileVersionPreview = (DLFileVersionPreview)session.get(
					DLFileVersionPreviewImpl.class,
					dlFileVersionPreview.getPrimaryKeyObj());
			}

			if ((dlFileVersionPreview != null) &&
				ctPersistenceHelper.isRemove(dlFileVersionPreview)) {

				session.delete(dlFileVersionPreview);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlFileVersionPreview != null) {
			clearCache(dlFileVersionPreview);
		}

		return dlFileVersionPreview;
	}

	@Override
	public DLFileVersionPreview updateImpl(
		DLFileVersionPreview dlFileVersionPreview) {

		boolean isNew = dlFileVersionPreview.isNew();

		if (!(dlFileVersionPreview instanceof DLFileVersionPreviewModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlFileVersionPreview.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlFileVersionPreview);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlFileVersionPreview proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLFileVersionPreview implementation " +
					dlFileVersionPreview.getClass());
		}

		DLFileVersionPreviewModelImpl dlFileVersionPreviewModelImpl =
			(DLFileVersionPreviewModelImpl)dlFileVersionPreview;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(dlFileVersionPreview)) {
				if (!isNew) {
					session.evict(
						DLFileVersionPreviewImpl.class,
						dlFileVersionPreview.getPrimaryKeyObj());
				}

				session.save(dlFileVersionPreview);
			}
			else {
				dlFileVersionPreview = (DLFileVersionPreview)session.merge(
					dlFileVersionPreview);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlFileVersionPreview, false);

		if (isNew) {
			dlFileVersionPreview.setNew(false);
		}

		dlFileVersionPreview.resetOriginalValues();

		return dlFileVersionPreview;
	}

	/**
	 * Returns the dl file version preview with the primary key or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	@Override
	public DLFileVersionPreview findByPrimaryKey(long dlFileVersionPreviewId)
		throws NoSuchFileVersionPreviewException {

		return findByPrimaryKey((Serializable)dlFileVersionPreviewId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the dl file version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview, or <code>null</code> if a dl file version preview with the primary key could not be found
	 */
	@Override
	public DLFileVersionPreview fetchByPrimaryKey(long dlFileVersionPreviewId) {
		return fetchByPrimaryKey((Serializable)dlFileVersionPreviewId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dlFileVersionPreviewId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLFILEVERSIONPREVIEW;
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
		return DLFileVersionPreviewModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DLFileVersionPreview";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("fileEntryId");
		ctMergeColumnNames.add("fileVersionId");
		ctMergeColumnNames.add("previewStatus");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("dlFileVersionPreviewId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"fileEntryId", "fileVersionId"});

		_uniqueIndexColumnNames.add(
			new String[] {"fileEntryId", "fileVersionId", "previewStatus"});
	}

	/**
	 * Initializes the dl file version preview persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFileEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByFileEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileEntryId", new String[] {Long.class.getName()},
					new String[] {"fileEntryId"}, false),
				_SQL_SELECT_DLFILEVERSIONPREVIEW_WHERE,
				_SQL_COUNT_DLFILEVERSIONPREVIEW_WHERE,
				DLFileVersionPreviewModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"dlFileVersionPreview.", "fileEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					DLFileVersionPreview::getFileEntryId));

		_collectionPersistenceFinderByFileVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFileVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fileVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFileVersionId", new String[] {Long.class.getName()},
					new String[] {"fileVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFileVersionId", new String[] {Long.class.getName()},
					new String[] {"fileVersionId"}, false),
				_SQL_SELECT_DLFILEVERSIONPREVIEW_WHERE,
				_SQL_COUNT_DLFILEVERSIONPREVIEW_WHERE,
				DLFileVersionPreviewModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"dlFileVersionPreview.", "fileVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					DLFileVersionPreview::getFileVersionId));

		_uniquePersistenceFinderByF_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"fileEntryId", "fileVersionId"}, 0, 0, false,
				DLFileVersionPreview::getFileEntryId,
				DLFileVersionPreview::getFileVersionId),
			_SQL_SELECT_DLFILEVERSIONPREVIEW_WHERE, "",
			new FinderColumn<>(
				"dlFileVersionPreview.", "fileEntryId", FinderColumn.Type.LONG,
				"=", true, true, DLFileVersionPreview::getFileEntryId),
			new FinderColumn<>(
				"dlFileVersionPreview.", "fileVersionId",
				FinderColumn.Type.LONG, "=", true, true,
				DLFileVersionPreview::getFileVersionId));

		_uniquePersistenceFinderByF_F_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_F_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"fileEntryId", "fileVersionId", "previewStatus"},
				0, 0, false, DLFileVersionPreview::getFileEntryId,
				DLFileVersionPreview::getFileVersionId,
				DLFileVersionPreview::getPreviewStatus),
			_SQL_SELECT_DLFILEVERSIONPREVIEW_WHERE, "",
			new FinderColumn<>(
				"dlFileVersionPreview.", "fileEntryId", FinderColumn.Type.LONG,
				"=", true, true, DLFileVersionPreview::getFileEntryId),
			new FinderColumn<>(
				"dlFileVersionPreview.", "fileVersionId",
				FinderColumn.Type.LONG, "=", true, true,
				DLFileVersionPreview::getFileVersionId),
			new FinderColumn<>(
				"dlFileVersionPreview.", "previewStatus",
				FinderColumn.Type.INTEGER, "=", true, true,
				DLFileVersionPreview::getPreviewStatus));

		DLFileVersionPreviewUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DLFileVersionPreviewUtil.setPersistence(null);

		entityCache.removeCache(DLFileVersionPreviewImpl.class.getName());
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DLFileVersionPreviewModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLFILEVERSIONPREVIEW =
		"SELECT dlFileVersionPreview FROM DLFileVersionPreview dlFileVersionPreview";

	private static final String _SQL_SELECT_DLFILEVERSIONPREVIEW_WHERE =
		"SELECT dlFileVersionPreview FROM DLFileVersionPreview dlFileVersionPreview WHERE ";

	private static final String _SQL_COUNT_DLFILEVERSIONPREVIEW_WHERE =
		"SELECT COUNT(dlFileVersionPreview) FROM DLFileVersionPreview dlFileVersionPreview WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLFileVersionPreview exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileVersionPreviewPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1940461743
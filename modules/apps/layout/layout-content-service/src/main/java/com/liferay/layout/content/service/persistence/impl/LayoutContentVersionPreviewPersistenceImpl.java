/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence.impl;

import com.liferay.layout.content.exception.NoSuchLayoutContentVersionPreviewException;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.model.LayoutContentVersionPreviewTable;
import com.liferay.layout.content.model.impl.LayoutContentVersionPreviewImpl;
import com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl;
import com.liferay.layout.content.service.persistence.LayoutContentVersionPreviewPersistence;
import com.liferay.layout.content.service.persistence.LayoutContentVersionPreviewUtil;
import com.liferay.layout.content.service.persistence.impl.constants.LayoutContentVersionPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the layout content version preview service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @generated
 */
@Component(service = LayoutContentVersionPreviewPersistence.class)
public class LayoutContentVersionPreviewPersistenceImpl
	extends BasePersistenceImpl
		<LayoutContentVersionPreview,
		 NoSuchLayoutContentVersionPreviewException>
	implements LayoutContentVersionPreviewPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutContentVersionPreviewUtil</code> to access the layout content version preview persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutContentVersionPreviewImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutContentVersionPreview,
		 NoSuchLayoutContentVersionPreviewException>
			_collectionPersistenceFinderByLayoutContentVersionId;

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	@Override
	public List<LayoutContentVersionPreview> findByLayoutContentVersionId(
		long layoutContentVersionId, int start, int end,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutContentVersionId.find(
			finderCache, new Object[] {layoutContentVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview findByLayoutContentVersionId_First(
			long layoutContentVersionId,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator)
		throws NoSuchLayoutContentVersionPreviewException {

		return _collectionPersistenceFinderByLayoutContentVersionId.findFirst(
			finderCache, new Object[] {layoutContentVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview fetchByLayoutContentVersionId_First(
		long layoutContentVersionId,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return _collectionPersistenceFinderByLayoutContentVersionId.fetchFirst(
			finderCache, new Object[] {layoutContentVersionId},
			orderByComparator);
	}

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 */
	@Override
	public void removeByLayoutContentVersionId(long layoutContentVersionId) {
		_collectionPersistenceFinderByLayoutContentVersionId.remove(
			finderCache, new Object[] {layoutContentVersionId});
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @return the number of matching layout content version previews
	 */
	@Override
	public int countByLayoutContentVersionId(long layoutContentVersionId) {
		return _collectionPersistenceFinderByLayoutContentVersionId.count(
			finderCache, new Object[] {layoutContentVersionId});
	}

	private CollectionPersistenceFinder
		<LayoutContentVersionPreview,
		 NoSuchLayoutContentVersionPreviewException>
			_collectionPersistenceFinderByLCVI_SEERC;

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	@Override
	public List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLCVI_SEERC.find(
			finderCache,
			new Object[] {layoutContentVersionId, segmentsExperienceERC}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview findByLCVI_SEERC_First(
			long layoutContentVersionId, String segmentsExperienceERC,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator)
		throws NoSuchLayoutContentVersionPreviewException {

		return _collectionPersistenceFinderByLCVI_SEERC.findFirst(
			finderCache,
			new Object[] {layoutContentVersionId, segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview fetchByLCVI_SEERC_First(
		long layoutContentVersionId, String segmentsExperienceERC,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return _collectionPersistenceFinderByLCVI_SEERC.fetchFirst(
			finderCache,
			new Object[] {layoutContentVersionId, segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 */
	@Override
	public void removeByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		_collectionPersistenceFinderByLCVI_SEERC.remove(
			finderCache,
			new Object[] {layoutContentVersionId, segmentsExperienceERC});
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	@Override
	public int countByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		return _collectionPersistenceFinderByLCVI_SEERC.count(
			finderCache,
			new Object[] {layoutContentVersionId, segmentsExperienceERC});
	}

	private UniquePersistenceFinder
		<LayoutContentVersionPreview,
		 NoSuchLayoutContentVersionPreviewException>
			_uniquePersistenceFinderByLCVI_L_SEERC;

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview findByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws NoSuchLayoutContentVersionPreviewException {

		return _uniquePersistenceFinderByLCVI_L_SEERC.find(
			finderCache,
			new Object[] {
				layoutContentVersionId, languageId, segmentsExperienceERC
			});
	}

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	@Override
	public LayoutContentVersionPreview fetchByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC, boolean useFinderCache) {

		return _uniquePersistenceFinderByLCVI_L_SEERC.fetch(
			finderCache,
			new Object[] {
				layoutContentVersionId, languageId, segmentsExperienceERC
			},
			useFinderCache);
	}

	/**
	 * Removes the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the layout content version preview that was removed
	 */
	@Override
	public LayoutContentVersionPreview removeByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws NoSuchLayoutContentVersionPreviewException {

		LayoutContentVersionPreview layoutContentVersionPreview =
			findByLCVI_L_SEERC(
				layoutContentVersionId, languageId, segmentsExperienceERC);

		return remove(layoutContentVersionPreview);
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	@Override
	public int countByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC) {

		return _uniquePersistenceFinderByLCVI_L_SEERC.count(
			finderCache,
			new Object[] {
				layoutContentVersionId, languageId, segmentsExperienceERC
			});
	}

	public LayoutContentVersionPreviewPersistenceImpl() {
		setModelClass(LayoutContentVersionPreview.class);

		setModelImplClass(LayoutContentVersionPreviewImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutContentVersionPreviewTable.INSTANCE);
	}

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	@Override
	public LayoutContentVersionPreview create(
		long layoutContentVersionPreviewId) {

		LayoutContentVersionPreview layoutContentVersionPreview =
			new LayoutContentVersionPreviewImpl();

		layoutContentVersionPreview.setNew(true);
		layoutContentVersionPreview.setPrimaryKey(
			layoutContentVersionPreviewId);

		layoutContentVersionPreview.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutContentVersionPreview;
	}

	/**
	 * Removes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	@Override
	public LayoutContentVersionPreview remove(
			long layoutContentVersionPreviewId)
		throws NoSuchLayoutContentVersionPreviewException {

		return remove((Serializable)layoutContentVersionPreviewId);
	}

	@Override
	protected LayoutContentVersionPreview removeImpl(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutContentVersionPreview)) {
				layoutContentVersionPreview =
					(LayoutContentVersionPreview)session.get(
						LayoutContentVersionPreviewImpl.class,
						layoutContentVersionPreview.getPrimaryKeyObj());
			}

			if (layoutContentVersionPreview != null) {
				session.delete(layoutContentVersionPreview);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutContentVersionPreview != null) {
			clearCache(layoutContentVersionPreview);
		}

		return layoutContentVersionPreview;
	}

	@Override
	public LayoutContentVersionPreview updateImpl(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		boolean isNew = layoutContentVersionPreview.isNew();

		if (!(layoutContentVersionPreview instanceof
				LayoutContentVersionPreviewModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutContentVersionPreview.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutContentVersionPreview);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutContentVersionPreview proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutContentVersionPreview implementation " +
					layoutContentVersionPreview.getClass());
		}

		LayoutContentVersionPreviewModelImpl
			layoutContentVersionPreviewModelImpl =
				(LayoutContentVersionPreviewModelImpl)
					layoutContentVersionPreview;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutContentVersionPreview.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutContentVersionPreview.setCreateDate(date);
			}
			else {
				layoutContentVersionPreview.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutContentVersionPreviewModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutContentVersionPreview.setModifiedDate(date);
			}
			else {
				layoutContentVersionPreview.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(layoutContentVersionPreview);
			}
			else {
				layoutContentVersionPreview =
					(LayoutContentVersionPreview)session.merge(
						layoutContentVersionPreview);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutContentVersionPreview, false);

		if (isNew) {
			layoutContentVersionPreview.setNew(false);
		}

		layoutContentVersionPreview.resetOriginalValues();

		return layoutContentVersionPreview;
	}

	/**
	 * Returns the layout content version preview with the primary key or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	@Override
	public LayoutContentVersionPreview findByPrimaryKey(
			long layoutContentVersionPreviewId)
		throws NoSuchLayoutContentVersionPreviewException {

		return findByPrimaryKey((Serializable)layoutContentVersionPreviewId);
	}

	/**
	 * Returns the layout content version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview, or <code>null</code> if a layout content version preview with the primary key could not be found
	 */
	@Override
	public LayoutContentVersionPreview fetchByPrimaryKey(
		long layoutContentVersionPreviewId) {

		return fetchByPrimaryKey((Serializable)layoutContentVersionPreviewId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "layoutContentVersionPreviewId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LayoutContentVersionPreviewModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the layout content version preview persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLayoutContentVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutContentVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutContentVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutContentVersionId",
					new String[] {Long.class.getName()},
					new String[] {"layoutContentVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutContentVersionId",
					new String[] {Long.class.getName()},
					new String[] {"layoutContentVersionId"}, false),
				_SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW_WHERE,
				_SQL_COUNT_LAYOUTCONTENTVERSIONPREVIEW_WHERE,
				LayoutContentVersionPreviewModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutContentVersionPreview.", "layoutContentVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutContentVersionPreview::getLayoutContentVersionId));

		_collectionPersistenceFinderByLCVI_SEERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLCVI_SEERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"layoutContentVersionId", "segmentsExperienceERC"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLCVI_SEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {
						"layoutContentVersionId", "segmentsExperienceERC"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLCVI_SEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {
						"layoutContentVersionId", "segmentsExperienceERC"
					},
					0, 2, false, null),
				_SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW_WHERE,
				_SQL_COUNT_LAYOUTCONTENTVERSIONPREVIEW_WHERE,
				LayoutContentVersionPreviewModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"layoutContentVersionPreview.", "layoutContentVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutContentVersionPreview::getLayoutContentVersionId),
				new FinderColumn<>(
					"layoutContentVersionPreview.", "segmentsExperienceERC",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutContentVersionPreview::getSegmentsExperienceERC));

		_uniquePersistenceFinderByLCVI_L_SEERC = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByLCVI_L_SEERC",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"layoutContentVersionId", "languageId",
					"segmentsExperienceERC"
				},
				0, 6, false,
				LayoutContentVersionPreview::getLayoutContentVersionId,
				convertNullFunction(LayoutContentVersionPreview::getLanguageId),
				convertNullFunction(
					LayoutContentVersionPreview::getSegmentsExperienceERC)),
			_SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW_WHERE, "",
			new FinderColumn<>(
				"layoutContentVersionPreview.", "layoutContentVersionId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutContentVersionPreview::getLayoutContentVersionId),
			new FinderColumn<>(
				"layoutContentVersionPreview.", "languageId",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutContentVersionPreview::getLanguageId),
			new FinderColumn<>(
				"layoutContentVersionPreview.", "segmentsExperienceERC",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutContentVersionPreview::getSegmentsExperienceERC));

		LayoutContentVersionPreviewUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutContentVersionPreviewUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutContentVersionPreviewImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutContentVersionPreviewModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW =
		"SELECT layoutContentVersionPreview FROM LayoutContentVersionPreview layoutContentVersionPreview";

	private static final String _SQL_SELECT_LAYOUTCONTENTVERSIONPREVIEW_WHERE =
		"SELECT layoutContentVersionPreview FROM LayoutContentVersionPreview layoutContentVersionPreview WHERE ";

	private static final String _SQL_COUNT_LAYOUTCONTENTVERSIONPREVIEW_WHERE =
		"SELECT COUNT(layoutContentVersionPreview) FROM LayoutContentVersionPreview layoutContentVersionPreview WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:564391685
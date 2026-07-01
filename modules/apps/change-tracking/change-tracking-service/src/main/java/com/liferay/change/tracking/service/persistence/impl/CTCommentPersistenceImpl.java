/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchCommentException;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.change.tracking.model.CTCommentTable;
import com.liferay.change.tracking.model.impl.CTCommentImpl;
import com.liferay.change.tracking.model.impl.CTCommentModelImpl;
import com.liferay.change.tracking.service.persistence.CTCommentPersistence;
import com.liferay.change.tracking.service.persistence.CTCommentUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
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
 * The persistence implementation for the ct comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTCommentPersistence.class)
public class CTCommentPersistenceImpl
	extends BasePersistenceImpl<CTComment, NoSuchCommentException>
	implements CTCommentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTCommentUtil</code> to access the ct comment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTCommentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTComment, NoSuchCommentException>
		_collectionPersistenceFinderByCtCollectionId;

	/**
	 * Returns an ordered range of all the ct comments where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCommentModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct comments
	 * @param end the upper bound of the range of ct comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct comments
	 */
	@Override
	public List<CTComment> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCtCollectionId.find(
			finderCache, new Object[] {ctCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct comment in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct comment
	 * @throws NoSuchCommentException if a matching ct comment could not be found
	 */
	@Override
	public CTComment findByCtCollectionId_First(
			long ctCollectionId, OrderByComparator<CTComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByCtCollectionId.findFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Returns the first ct comment in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct comment, or <code>null</code> if a matching ct comment could not be found
	 */
	@Override
	public CTComment fetchByCtCollectionId_First(
		long ctCollectionId, OrderByComparator<CTComment> orderByComparator) {

		return _collectionPersistenceFinderByCtCollectionId.fetchFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Removes all the ct comments where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 */
	@Override
	public void removeByCtCollectionId(long ctCollectionId) {
		_collectionPersistenceFinderByCtCollectionId.remove(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the number of ct comments where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct comments
	 */
	@Override
	public int countByCtCollectionId(long ctCollectionId) {
		return _collectionPersistenceFinderByCtCollectionId.count(
			finderCache, new Object[] {ctCollectionId});
	}

	private CollectionPersistenceFinder<CTComment, NoSuchCommentException>
		_collectionPersistenceFinderByCtEntryId;

	/**
	 * Returns an ordered range of all the ct comments where ctEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCommentModelImpl</code>.
	 * </p>
	 *
	 * @param ctEntryId the ct entry ID
	 * @param start the lower bound of the range of ct comments
	 * @param end the upper bound of the range of ct comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct comments
	 */
	@Override
	public List<CTComment> findByCtEntryId(
		long ctEntryId, int start, int end,
		OrderByComparator<CTComment> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCtEntryId.find(
			finderCache, new Object[] {ctEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct comment in the ordered set where ctEntryId = &#63;.
	 *
	 * @param ctEntryId the ct entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct comment
	 * @throws NoSuchCommentException if a matching ct comment could not be found
	 */
	@Override
	public CTComment findByCtEntryId_First(
			long ctEntryId, OrderByComparator<CTComment> orderByComparator)
		throws NoSuchCommentException {

		return _collectionPersistenceFinderByCtEntryId.findFirst(
			finderCache, new Object[] {ctEntryId}, orderByComparator);
	}

	/**
	 * Returns the first ct comment in the ordered set where ctEntryId = &#63;.
	 *
	 * @param ctEntryId the ct entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct comment, or <code>null</code> if a matching ct comment could not be found
	 */
	@Override
	public CTComment fetchByCtEntryId_First(
		long ctEntryId, OrderByComparator<CTComment> orderByComparator) {

		return _collectionPersistenceFinderByCtEntryId.fetchFirst(
			finderCache, new Object[] {ctEntryId}, orderByComparator);
	}

	/**
	 * Removes all the ct comments where ctEntryId = &#63; from the database.
	 *
	 * @param ctEntryId the ct entry ID
	 */
	@Override
	public void removeByCtEntryId(long ctEntryId) {
		_collectionPersistenceFinderByCtEntryId.remove(
			finderCache, new Object[] {ctEntryId});
	}

	/**
	 * Returns the number of ct comments where ctEntryId = &#63;.
	 *
	 * @param ctEntryId the ct entry ID
	 * @return the number of matching ct comments
	 */
	@Override
	public int countByCtEntryId(long ctEntryId) {
		return _collectionPersistenceFinderByCtEntryId.count(
			finderCache, new Object[] {ctEntryId});
	}

	public CTCommentPersistenceImpl() {
		setModelClass(CTComment.class);

		setModelImplClass(CTCommentImpl.class);
		setModelPKClass(long.class);

		setTable(CTCommentTable.INSTANCE);
	}

	/**
	 * Creates a new ct comment with the primary key. Does not add the ct comment to the database.
	 *
	 * @param ctCommentId the primary key for the new ct comment
	 * @return the new ct comment
	 */
	@Override
	public CTComment create(long ctCommentId) {
		CTComment ctComment = new CTCommentImpl();

		ctComment.setNew(true);
		ctComment.setPrimaryKey(ctCommentId);

		ctComment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctComment;
	}

	/**
	 * Removes the ct comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctCommentId the primary key of the ct comment
	 * @return the ct comment that was removed
	 * @throws NoSuchCommentException if a ct comment with the primary key could not be found
	 */
	@Override
	public CTComment remove(long ctCommentId) throws NoSuchCommentException {
		return remove((Serializable)ctCommentId);
	}

	@Override
	protected CTComment removeImpl(CTComment ctComment) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctComment)) {
				ctComment = (CTComment)session.get(
					CTCommentImpl.class, ctComment.getPrimaryKeyObj());
			}

			if (ctComment != null) {
				session.delete(ctComment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctComment != null) {
			clearCache(ctComment);
		}

		return ctComment;
	}

	@Override
	public CTComment updateImpl(CTComment ctComment) {
		boolean isNew = ctComment.isNew();

		if (!(ctComment instanceof CTCommentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctComment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctComment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctComment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTComment implementation " +
					ctComment.getClass());
		}

		CTCommentModelImpl ctCommentModelImpl = (CTCommentModelImpl)ctComment;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ctComment.getCreateDate() == null)) {
			if (serviceContext == null) {
				ctComment.setCreateDate(date);
			}
			else {
				ctComment.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ctCommentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ctComment.setModifiedDate(date);
			}
			else {
				ctComment.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctComment);
			}
			else {
				ctComment = (CTComment)session.merge(ctComment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctComment, false);

		if (isNew) {
			ctComment.setNew(false);
		}

		ctComment.resetOriginalValues();

		return ctComment;
	}

	/**
	 * Returns the ct comment with the primary key or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param ctCommentId the primary key of the ct comment
	 * @return the ct comment
	 * @throws NoSuchCommentException if a ct comment with the primary key could not be found
	 */
	@Override
	public CTComment findByPrimaryKey(long ctCommentId)
		throws NoSuchCommentException {

		return findByPrimaryKey((Serializable)ctCommentId);
	}

	/**
	 * Returns the ct comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctCommentId the primary key of the ct comment
	 * @return the ct comment, or <code>null</code> if a ct comment with the primary key could not be found
	 */
	@Override
	public CTComment fetchByPrimaryKey(long ctCommentId) {
		return fetchByPrimaryKey((Serializable)ctCommentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctCommentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTCOMMENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTCommentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct comment persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCtCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCtCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCtCollectionId", new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, false),
				_SQL_SELECT_CTCOMMENT_WHERE, _SQL_COUNT_CTCOMMENT_WHERE,
				CTCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ctComment.", "ctCollectionId", FinderColumn.Type.LONG, "=",
					true, true, CTComment::getCtCollectionId));

		_collectionPersistenceFinderByCtEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCtEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCtEntryId", new String[] {Long.class.getName()},
					new String[] {"ctEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCtEntryId", new String[] {Long.class.getName()},
					new String[] {"ctEntryId"}, false),
				_SQL_SELECT_CTCOMMENT_WHERE, _SQL_COUNT_CTCOMMENT_WHERE,
				CTCommentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ctComment.", "ctEntryId", FinderColumn.Type.LONG, "=",
					true, true, CTComment::getCtEntryId));

		CTCommentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTCommentUtil.setPersistence(null);

		entityCache.removeCache(CTCommentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTCommentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTCOMMENT =
		"SELECT ctComment FROM CTComment ctComment";

	private static final String _SQL_SELECT_CTCOMMENT_WHERE =
		"SELECT ctComment FROM CTComment ctComment WHERE ";

	private static final String _SQL_COUNT_CTCOMMENT_WHERE =
		"SELECT COUNT(ctComment) FROM CTComment ctComment WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTComment exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:528407118
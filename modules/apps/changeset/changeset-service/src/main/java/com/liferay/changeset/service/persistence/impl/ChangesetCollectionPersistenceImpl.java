/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence.impl;

import com.liferay.changeset.exception.NoSuchCollectionException;
import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.model.ChangesetCollectionTable;
import com.liferay.changeset.model.impl.ChangesetCollectionImpl;
import com.liferay.changeset.model.impl.ChangesetCollectionModelImpl;
import com.liferay.changeset.service.persistence.ChangesetCollectionPersistence;
import com.liferay.changeset.service.persistence.ChangesetCollectionUtil;
import com.liferay.changeset.service.persistence.impl.constants.ChangesetPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the changeset collection service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ChangesetCollectionPersistence.class)
public class ChangesetCollectionPersistenceImpl
	extends BasePersistenceImpl<ChangesetCollection, NoSuchCollectionException>
	implements ChangesetCollectionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ChangesetCollectionUtil</code> to access the changeset collection persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ChangesetCollectionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<ChangesetCollection, NoSuchCollectionException>
			_uniquePersistenceFinderByG_N;

	/**
	 * Returns the changeset collection where groupId = &#63; and name = &#63; or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching changeset collection
	 * @throws NoSuchCollectionException if a matching changeset collection could not be found
	 */
	@Override
	public ChangesetCollection findByG_N(long groupId, String name)
		throws NoSuchCollectionException {

		return _uniquePersistenceFinderByG_N.find(
			finderCache, new Object[] {groupId, name});
	}

	/**
	 * Returns the changeset collection where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset collection, or <code>null</code> if a matching changeset collection could not be found
	 */
	@Override
	public ChangesetCollection fetchByG_N(
		long groupId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_N.fetch(
			finderCache, new Object[] {groupId, name}, useFinderCache);
	}

	/**
	 * Removes the changeset collection where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the changeset collection that was removed
	 */
	@Override
	public ChangesetCollection removeByG_N(long groupId, String name)
		throws NoSuchCollectionException {

		ChangesetCollection changesetCollection = findByG_N(groupId, name);

		return remove(changesetCollection);
	}

	/**
	 * Returns the number of changeset collections where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching changeset collections
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _uniquePersistenceFinderByG_N.count(
			finderCache, new Object[] {groupId, name});
	}

	public ChangesetCollectionPersistenceImpl() {
		setModelClass(ChangesetCollection.class);

		setModelImplClass(ChangesetCollectionImpl.class);
		setModelPKClass(long.class);

		setTable(ChangesetCollectionTable.INSTANCE);
	}

	/**
	 * Creates a new changeset collection with the primary key. Does not add the changeset collection to the database.
	 *
	 * @param changesetCollectionId the primary key for the new changeset collection
	 * @return the new changeset collection
	 */
	@Override
	public ChangesetCollection create(long changesetCollectionId) {
		ChangesetCollection changesetCollection = new ChangesetCollectionImpl();

		changesetCollection.setNew(true);
		changesetCollection.setPrimaryKey(changesetCollectionId);

		changesetCollection.setCompanyId(CompanyThreadLocal.getCompanyId());

		return changesetCollection;
	}

	/**
	 * Removes the changeset collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection that was removed
	 * @throws NoSuchCollectionException if a changeset collection with the primary key could not be found
	 */
	@Override
	public ChangesetCollection remove(long changesetCollectionId)
		throws NoSuchCollectionException {

		return remove((Serializable)changesetCollectionId);
	}

	@Override
	protected ChangesetCollection removeImpl(
		ChangesetCollection changesetCollection) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(changesetCollection)) {
				changesetCollection = (ChangesetCollection)session.get(
					ChangesetCollectionImpl.class,
					changesetCollection.getPrimaryKeyObj());
			}

			if (changesetCollection != null) {
				session.delete(changesetCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (changesetCollection != null) {
			clearCache(changesetCollection);
		}

		return changesetCollection;
	}

	@Override
	public ChangesetCollection updateImpl(
		ChangesetCollection changesetCollection) {

		boolean isNew = changesetCollection.isNew();

		if (!(changesetCollection instanceof ChangesetCollectionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(changesetCollection.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					changesetCollection);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in changesetCollection proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ChangesetCollection implementation " +
					changesetCollection.getClass());
		}

		ChangesetCollectionModelImpl changesetCollectionModelImpl =
			(ChangesetCollectionModelImpl)changesetCollection;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (changesetCollection.getCreateDate() == null)) {
			if (serviceContext == null) {
				changesetCollection.setCreateDate(date);
			}
			else {
				changesetCollection.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!changesetCollectionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				changesetCollection.setModifiedDate(date);
			}
			else {
				changesetCollection.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(changesetCollection);
			}
			else {
				changesetCollection = (ChangesetCollection)session.merge(
					changesetCollection);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(changesetCollection, false);

		if (isNew) {
			changesetCollection.setNew(false);
		}

		changesetCollection.resetOriginalValues();

		return changesetCollection;
	}

	/**
	 * Returns the changeset collection with the primary key or throws a <code>NoSuchCollectionException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection
	 * @throws NoSuchCollectionException if a changeset collection with the primary key could not be found
	 */
	@Override
	public ChangesetCollection findByPrimaryKey(long changesetCollectionId)
		throws NoSuchCollectionException {

		return findByPrimaryKey((Serializable)changesetCollectionId);
	}

	/**
	 * Returns the changeset collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection, or <code>null</code> if a changeset collection with the primary key could not be found
	 */
	@Override
	public ChangesetCollection fetchByPrimaryKey(long changesetCollectionId) {
		return fetchByPrimaryKey((Serializable)changesetCollectionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "changesetCollectionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CHANGESETCOLLECTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ChangesetCollectionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the changeset collection persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByG_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, false,
				ChangesetCollection::getGroupId,
				convertNullFunction(ChangesetCollection::getName)),
			_SQL_SELECT_CHANGESETCOLLECTION_WHERE, "",
			new FinderColumn<>(
				"changesetCollection.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ChangesetCollection::getGroupId),
			new FinderColumn<>(
				"changesetCollection.", "name", FinderColumn.Type.STRING, "=",
				true, true, ChangesetCollection::getName));

		ChangesetCollectionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ChangesetCollectionUtil.setPersistence(null);

		entityCache.removeCache(ChangesetCollectionImpl.class.getName());
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CHANGESETCOLLECTION =
		"SELECT changesetCollection FROM ChangesetCollection changesetCollection";

	private static final String _SQL_SELECT_CHANGESETCOLLECTION_WHERE =
		"SELECT changesetCollection FROM ChangesetCollection changesetCollection WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2136611629
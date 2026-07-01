/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchSchemaVersionException;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.model.CTSchemaVersionTable;
import com.liferay.change.tracking.model.impl.CTSchemaVersionImpl;
import com.liferay.change.tracking.model.impl.CTSchemaVersionModelImpl;
import com.liferay.change.tracking.service.persistence.CTSchemaVersionPersistence;
import com.liferay.change.tracking.service.persistence.CTSchemaVersionUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ct schema version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSchemaVersionPersistence.class)
public class CTSchemaVersionPersistenceImpl
	extends BasePersistenceImpl<CTSchemaVersion, NoSuchSchemaVersionException>
	implements CTSchemaVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSchemaVersionUtil</code> to access the ct schema version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSchemaVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CTSchemaVersion, NoSuchSchemaVersionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the ct schema versions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSchemaVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct schema versions
	 * @param end the upper bound of the range of ct schema versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct schema versions
	 */
	@Override
	public List<CTSchemaVersion> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSchemaVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct schema version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct schema version
	 * @throws NoSuchSchemaVersionException if a matching ct schema version could not be found
	 */
	@Override
	public CTSchemaVersion findByCompanyId_First(
			long companyId,
			OrderByComparator<CTSchemaVersion> orderByComparator)
		throws NoSuchSchemaVersionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first ct schema version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct schema version, or <code>null</code> if a matching ct schema version could not be found
	 */
	@Override
	public CTSchemaVersion fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSchemaVersion> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the ct schema versions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct schema versions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct schema versions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	public CTSchemaVersionPersistenceImpl() {
		setModelClass(CTSchemaVersion.class);

		setModelImplClass(CTSchemaVersionImpl.class);
		setModelPKClass(long.class);

		setTable(CTSchemaVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ct schema version with the primary key. Does not add the ct schema version to the database.
	 *
	 * @param schemaVersionId the primary key for the new ct schema version
	 * @return the new ct schema version
	 */
	@Override
	public CTSchemaVersion create(long schemaVersionId) {
		CTSchemaVersion ctSchemaVersion = new CTSchemaVersionImpl();

		ctSchemaVersion.setNew(true);
		ctSchemaVersion.setPrimaryKey(schemaVersionId);

		ctSchemaVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctSchemaVersion;
	}

	/**
	 * Removes the ct schema version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param schemaVersionId the primary key of the ct schema version
	 * @return the ct schema version that was removed
	 * @throws NoSuchSchemaVersionException if a ct schema version with the primary key could not be found
	 */
	@Override
	public CTSchemaVersion remove(long schemaVersionId)
		throws NoSuchSchemaVersionException {

		return remove((Serializable)schemaVersionId);
	}

	@Override
	protected CTSchemaVersion removeImpl(CTSchemaVersion ctSchemaVersion) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctSchemaVersion)) {
				ctSchemaVersion = (CTSchemaVersion)session.get(
					CTSchemaVersionImpl.class,
					ctSchemaVersion.getPrimaryKeyObj());
			}

			if (ctSchemaVersion != null) {
				session.delete(ctSchemaVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctSchemaVersion != null) {
			clearCache(ctSchemaVersion);
		}

		return ctSchemaVersion;
	}

	@Override
	public CTSchemaVersion updateImpl(CTSchemaVersion ctSchemaVersion) {
		boolean isNew = ctSchemaVersion.isNew();

		if (!(ctSchemaVersion instanceof CTSchemaVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctSchemaVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctSchemaVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctSchemaVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSchemaVersion implementation " +
					ctSchemaVersion.getClass());
		}

		CTSchemaVersionModelImpl ctSchemaVersionModelImpl =
			(CTSchemaVersionModelImpl)ctSchemaVersion;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctSchemaVersion);
			}
			else {
				ctSchemaVersion = (CTSchemaVersion)session.merge(
					ctSchemaVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctSchemaVersion, false);

		if (isNew) {
			ctSchemaVersion.setNew(false);
		}

		ctSchemaVersion.resetOriginalValues();

		return ctSchemaVersion;
	}

	/**
	 * Returns the ct schema version with the primary key or throws a <code>NoSuchSchemaVersionException</code> if it could not be found.
	 *
	 * @param schemaVersionId the primary key of the ct schema version
	 * @return the ct schema version
	 * @throws NoSuchSchemaVersionException if a ct schema version with the primary key could not be found
	 */
	@Override
	public CTSchemaVersion findByPrimaryKey(long schemaVersionId)
		throws NoSuchSchemaVersionException {

		return findByPrimaryKey((Serializable)schemaVersionId);
	}

	/**
	 * Returns the ct schema version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param schemaVersionId the primary key of the ct schema version
	 * @return the ct schema version, or <code>null</code> if a ct schema version with the primary key could not be found
	 */
	@Override
	public CTSchemaVersion fetchByPrimaryKey(long schemaVersionId) {
		return fetchByPrimaryKey((Serializable)schemaVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "schemaVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSCHEMAVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTSchemaVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct schema version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_CTSCHEMAVERSION_WHERE,
				_SQL_COUNT_CTSCHEMAVERSION_WHERE,
				CTSchemaVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ctSchemaVersion.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CTSchemaVersion::getCompanyId));

		CTSchemaVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSchemaVersionUtil.setPersistence(null);

		entityCache.removeCache(CTSchemaVersionImpl.class.getName());
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
		CTSchemaVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTSCHEMAVERSION =
		"SELECT ctSchemaVersion FROM CTSchemaVersion ctSchemaVersion";

	private static final String _SQL_SELECT_CTSCHEMAVERSION_WHERE =
		"SELECT ctSchemaVersion FROM CTSchemaVersion ctSchemaVersion WHERE ";

	private static final String _SQL_COUNT_CTSCHEMAVERSION_WHERE =
		"SELECT COUNT(ctSchemaVersion) FROM CTSchemaVersion ctSchemaVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTSchemaVersion exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1273137720
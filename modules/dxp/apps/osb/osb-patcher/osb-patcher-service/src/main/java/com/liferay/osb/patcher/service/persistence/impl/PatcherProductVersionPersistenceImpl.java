/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProductVersionTable;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the patcher product version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherProductVersionPersistence.class)
public class PatcherProductVersionPersistenceImpl
	extends BasePersistenceImpl
		<PatcherProductVersion, NoSuchPatcherProductVersionException>
	implements PatcherProductVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherProductVersionUtil</code> to access the patcher product version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherProductVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PatcherProductVersion, NoSuchPatcherProductVersionException>
			_collectionPersistenceFinderByFixDeliveryMethod;

	/**
	 * Returns an ordered range of all the patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher product versions
	 */
	@Override
	public List<PatcherProductVersion> findByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFixDeliveryMethod.find(
			finderCache, new Object[] {fixDeliveryMethod}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher product version
	 * @throws NoSuchPatcherProductVersionException if a matching patcher product version could not be found
	 */
	@Override
	public PatcherProductVersion findByFixDeliveryMethod_First(
			int fixDeliveryMethod,
			OrderByComparator<PatcherProductVersion> orderByComparator)
		throws NoSuchPatcherProductVersionException {

		return _collectionPersistenceFinderByFixDeliveryMethod.findFirst(
			finderCache, new Object[] {fixDeliveryMethod}, orderByComparator);
	}

	/**
	 * Returns the first patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	@Override
	public PatcherProductVersion fetchByFixDeliveryMethod_First(
		int fixDeliveryMethod,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return _collectionPersistenceFinderByFixDeliveryMethod.fetchFirst(
			finderCache, new Object[] {fixDeliveryMethod}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher product versions that the user has permissions to view where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher product versions that the user has permission to view
	 */
	@Override
	public List<PatcherProductVersion> filterFindByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return _collectionPersistenceFinderByFixDeliveryMethod.filterFind(
			finderCache, new Object[] {fixDeliveryMethod}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the patcher product versions where fixDeliveryMethod = &#63; from the database.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 */
	@Override
	public void removeByFixDeliveryMethod(int fixDeliveryMethod) {
		_collectionPersistenceFinderByFixDeliveryMethod.remove(
			finderCache, new Object[] {fixDeliveryMethod});
	}

	/**
	 * Returns the number of patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the number of matching patcher product versions
	 */
	@Override
	public int countByFixDeliveryMethod(int fixDeliveryMethod) {
		return _collectionPersistenceFinderByFixDeliveryMethod.count(
			finderCache, new Object[] {fixDeliveryMethod});
	}

	/**
	 * Returns the number of patcher product versions that the user has permission to view where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the number of matching patcher product versions that the user has permission to view
	 */
	@Override
	public int filterCountByFixDeliveryMethod(int fixDeliveryMethod) {
		return _collectionPersistenceFinderByFixDeliveryMethod.filterCount(
			finderCache, new Object[] {fixDeliveryMethod});
	}

	private UniquePersistenceFinder
		<PatcherProductVersion, NoSuchPatcherProductVersionException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the patcher product version where name = &#63; or throws a <code>NoSuchPatcherProductVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher product version
	 * @throws NoSuchPatcherProductVersionException if a matching patcher product version could not be found
	 */
	@Override
	public PatcherProductVersion findByName(String name)
		throws NoSuchPatcherProductVersionException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the patcher product version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	@Override
	public PatcherProductVersion fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the patcher product version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher product version that was removed
	 */
	@Override
	public PatcherProductVersion removeByName(String name)
		throws NoSuchPatcherProductVersionException {

		PatcherProductVersion patcherProductVersion = findByName(name);

		return remove(patcherProductVersion);
	}

	/**
	 * Returns the number of patcher product versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher product versions
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	public PatcherProductVersionPersistenceImpl() {
		setModelClass(PatcherProductVersion.class);

		setModelImplClass(PatcherProductVersionImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherProductVersionTable.INSTANCE);
	}

	/**
	 * Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	 *
	 * @param patcherProductVersionId the primary key for the new patcher product version
	 * @return the new patcher product version
	 */
	@Override
	public PatcherProductVersion create(long patcherProductVersionId) {
		PatcherProductVersion patcherProductVersion =
			new PatcherProductVersionImpl();

		patcherProductVersion.setNew(true);
		patcherProductVersion.setPrimaryKey(patcherProductVersionId);

		patcherProductVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherProductVersion;
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion remove(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException {

		return remove((Serializable)patcherProductVersionId);
	}

	@Override
	protected PatcherProductVersion removeImpl(
		PatcherProductVersion patcherProductVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProductVersion)) {
				patcherProductVersion = (PatcherProductVersion)session.get(
					PatcherProductVersionImpl.class,
					patcherProductVersion.getPrimaryKeyObj());
			}

			if (patcherProductVersion != null) {
				session.delete(patcherProductVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherProductVersion != null) {
			clearCache(patcherProductVersion);
		}

		return patcherProductVersion;
	}

	@Override
	public PatcherProductVersion updateImpl(
		PatcherProductVersion patcherProductVersion) {

		boolean isNew = patcherProductVersion.isNew();

		if (!(patcherProductVersion instanceof
				PatcherProductVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherProductVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherProductVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherProductVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherProductVersion implementation " +
					patcherProductVersion.getClass());
		}

		PatcherProductVersionModelImpl patcherProductVersionModelImpl =
			(PatcherProductVersionModelImpl)patcherProductVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherProductVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherProductVersion.setCreateDate(date);
			}
			else {
				patcherProductVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherProductVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherProductVersion.setModifiedDate(date);
			}
			else {
				patcherProductVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherProductVersion);
			}
			else {
				patcherProductVersion = (PatcherProductVersion)session.merge(
					patcherProductVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherProductVersion, false);

		if (isNew) {
			patcherProductVersion.setNew(false);
		}

		patcherProductVersion.resetOriginalValues();

		return patcherProductVersion;
	}

	/**
	 * Returns the patcher product version with the primary key or throws a <code>NoSuchPatcherProductVersionException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion findByPrimaryKey(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException {

		return findByPrimaryKey((Serializable)patcherProductVersionId);
	}

	/**
	 * Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion fetchByPrimaryKey(
		long patcherProductVersionId) {

		return fetchByPrimaryKey((Serializable)patcherProductVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherProductVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERPRODUCTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherProductVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher product version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFixDeliveryMethod =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFixDeliveryMethod",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"fixDeliveryMethod"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFixDeliveryMethod",
					new String[] {Integer.class.getName()},
					new String[] {"fixDeliveryMethod"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFixDeliveryMethod",
					new String[] {Integer.class.getName()},
					new String[] {"fixDeliveryMethod"}, false),
				_SQL_SELECT_PATCHERPRODUCTVERSION_WHERE,
				_SQL_COUNT_PATCHERPRODUCTVERSION_WHERE,
				PatcherProductVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"patcherProductVersion.", "fixDeliveryMethod",
					FinderColumn.Type.INTEGER, "=", true, true,
					PatcherProductVersion::getFixDeliveryMethod));

		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(PatcherProductVersion::getName)),
			_SQL_SELECT_PATCHERPRODUCTVERSION_WHERE, "",
			new FinderColumn<>(
				"patcherProductVersion.", "name", FinderColumn.Type.STRING, "=",
				true, true, PatcherProductVersion::getName));

		PatcherProductVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherProductVersionUtil.setPersistence(null);

		entityCache.removeCache(PatcherProductVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		PatcherProductVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PATCHERPRODUCTVERSION =
		"SELECT patcherProductVersion FROM PatcherProductVersion patcherProductVersion";

	private static final String _SQL_SELECT_PATCHERPRODUCTVERSION_WHERE =
		"SELECT patcherProductVersion FROM PatcherProductVersion patcherProductVersion WHERE ";

	private static final String _SQL_COUNT_PATCHERPRODUCTVERSION_WHERE =
		"SELECT COUNT(patcherProductVersion) FROM PatcherProductVersion patcherProductVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:288342876
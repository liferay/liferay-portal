/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.impl;

import com.liferay.depot.exception.NoSuchAppCustomizationException;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.model.DepotAppCustomizationTable;
import com.liferay.depot.model.impl.DepotAppCustomizationImpl;
import com.liferay.depot.model.impl.DepotAppCustomizationModelImpl;
import com.liferay.depot.service.persistence.DepotAppCustomizationPersistence;
import com.liferay.depot.service.persistence.DepotAppCustomizationUtil;
import com.liferay.depot.service.persistence.impl.constants.DepotPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the depot app customization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DepotAppCustomizationPersistence.class)
public class DepotAppCustomizationPersistenceImpl
	extends BasePersistenceImpl
		<DepotAppCustomization, NoSuchAppCustomizationException>
	implements DepotAppCustomizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DepotAppCustomizationUtil</code> to access the depot app customization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DepotAppCustomizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DepotAppCustomization, NoSuchAppCustomizationException>
			_collectionPersistenceFinderByDepotEntryId;

	/**
	 * Returns an ordered range of all the depot app customizations where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotAppCustomizationModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot app customizations
	 * @param end the upper bound of the range of depot app customizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot app customizations
	 */
	@Override
	public List<DepotAppCustomization> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotAppCustomization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDepotEntryId.find(
			finderCache, new Object[] {depotEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot app customization in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot app customization
	 * @throws NoSuchAppCustomizationException if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization findByDepotEntryId_First(
			long depotEntryId,
			OrderByComparator<DepotAppCustomization> orderByComparator)
		throws NoSuchAppCustomizationException {

		return _collectionPersistenceFinderByDepotEntryId.findFirst(
			finderCache, new Object[] {depotEntryId}, orderByComparator);
	}

	/**
	 * Returns the first depot app customization in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot app customization, or <code>null</code> if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization fetchByDepotEntryId_First(
		long depotEntryId,
		OrderByComparator<DepotAppCustomization> orderByComparator) {

		return _collectionPersistenceFinderByDepotEntryId.fetchFirst(
			finderCache, new Object[] {depotEntryId}, orderByComparator);
	}

	/**
	 * Removes all the depot app customizations where depotEntryId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 */
	@Override
	public void removeByDepotEntryId(long depotEntryId) {
		_collectionPersistenceFinderByDepotEntryId.remove(
			finderCache, new Object[] {depotEntryId});
	}

	/**
	 * Returns the number of depot app customizations where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot app customizations
	 */
	@Override
	public int countByDepotEntryId(long depotEntryId) {
		return _collectionPersistenceFinderByDepotEntryId.count(
			finderCache, new Object[] {depotEntryId});
	}

	private UniquePersistenceFinder
		<DepotAppCustomization, NoSuchAppCustomizationException>
			_uniquePersistenceFinderByD_E;

	/**
	 * Returns the depot app customization where depotEntryId = &#63; and enabled = &#63; or throws a <code>NoSuchAppCustomizationException</code> if it could not be found.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param enabled the enabled
	 * @return the matching depot app customization
	 * @throws NoSuchAppCustomizationException if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization findByD_E(long depotEntryId, boolean enabled)
		throws NoSuchAppCustomizationException {

		return _uniquePersistenceFinderByD_E.find(
			finderCache, new Object[] {depotEntryId, enabled});
	}

	/**
	 * Returns the depot app customization where depotEntryId = &#63; and enabled = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param enabled the enabled
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot app customization, or <code>null</code> if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization fetchByD_E(
		long depotEntryId, boolean enabled, boolean useFinderCache) {

		return _uniquePersistenceFinderByD_E.fetch(
			finderCache, new Object[] {depotEntryId, enabled}, useFinderCache);
	}

	/**
	 * Removes the depot app customization where depotEntryId = &#63; and enabled = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param enabled the enabled
	 * @return the depot app customization that was removed
	 */
	@Override
	public DepotAppCustomization removeByD_E(long depotEntryId, boolean enabled)
		throws NoSuchAppCustomizationException {

		DepotAppCustomization depotAppCustomization = findByD_E(
			depotEntryId, enabled);

		return remove(depotAppCustomization);
	}

	/**
	 * Returns the number of depot app customizations where depotEntryId = &#63; and enabled = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param enabled the enabled
	 * @return the number of matching depot app customizations
	 */
	@Override
	public int countByD_E(long depotEntryId, boolean enabled) {
		return _uniquePersistenceFinderByD_E.count(
			finderCache, new Object[] {depotEntryId, enabled});
	}

	private UniquePersistenceFinder
		<DepotAppCustomization, NoSuchAppCustomizationException>
			_uniquePersistenceFinderByD_P;

	/**
	 * Returns the depot app customization where depotEntryId = &#63; and portletId = &#63; or throws a <code>NoSuchAppCustomizationException</code> if it could not be found.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param portletId the portlet ID
	 * @return the matching depot app customization
	 * @throws NoSuchAppCustomizationException if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization findByD_P(long depotEntryId, String portletId)
		throws NoSuchAppCustomizationException {

		return _uniquePersistenceFinderByD_P.find(
			finderCache, new Object[] {depotEntryId, portletId});
	}

	/**
	 * Returns the depot app customization where depotEntryId = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot app customization, or <code>null</code> if a matching depot app customization could not be found
	 */
	@Override
	public DepotAppCustomization fetchByD_P(
		long depotEntryId, String portletId, boolean useFinderCache) {

		return _uniquePersistenceFinderByD_P.fetch(
			finderCache, new Object[] {depotEntryId, portletId},
			useFinderCache);
	}

	/**
	 * Removes the depot app customization where depotEntryId = &#63; and portletId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param portletId the portlet ID
	 * @return the depot app customization that was removed
	 */
	@Override
	public DepotAppCustomization removeByD_P(
			long depotEntryId, String portletId)
		throws NoSuchAppCustomizationException {

		DepotAppCustomization depotAppCustomization = findByD_P(
			depotEntryId, portletId);

		return remove(depotAppCustomization);
	}

	/**
	 * Returns the number of depot app customizations where depotEntryId = &#63; and portletId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param portletId the portlet ID
	 * @return the number of matching depot app customizations
	 */
	@Override
	public int countByD_P(long depotEntryId, String portletId) {
		return _uniquePersistenceFinderByD_P.count(
			finderCache, new Object[] {depotEntryId, portletId});
	}

	public DepotAppCustomizationPersistenceImpl() {
		setModelClass(DepotAppCustomization.class);

		setModelImplClass(DepotAppCustomizationImpl.class);
		setModelPKClass(long.class);

		setTable(DepotAppCustomizationTable.INSTANCE);
	}

	/**
	 * Creates a new depot app customization with the primary key. Does not add the depot app customization to the database.
	 *
	 * @param depotAppCustomizationId the primary key for the new depot app customization
	 * @return the new depot app customization
	 */
	@Override
	public DepotAppCustomization create(long depotAppCustomizationId) {
		DepotAppCustomization depotAppCustomization =
			new DepotAppCustomizationImpl();

		depotAppCustomization.setNew(true);
		depotAppCustomization.setPrimaryKey(depotAppCustomizationId);

		depotAppCustomization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return depotAppCustomization;
	}

	/**
	 * Removes the depot app customization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotAppCustomizationId the primary key of the depot app customization
	 * @return the depot app customization that was removed
	 * @throws NoSuchAppCustomizationException if a depot app customization with the primary key could not be found
	 */
	@Override
	public DepotAppCustomization remove(long depotAppCustomizationId)
		throws NoSuchAppCustomizationException {

		return remove((Serializable)depotAppCustomizationId);
	}

	@Override
	protected DepotAppCustomization removeImpl(
		DepotAppCustomization depotAppCustomization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(depotAppCustomization)) {
				depotAppCustomization = (DepotAppCustomization)session.get(
					DepotAppCustomizationImpl.class,
					depotAppCustomization.getPrimaryKeyObj());
			}

			if ((depotAppCustomization != null) &&
				ctPersistenceHelper.isRemove(depotAppCustomization)) {

				session.delete(depotAppCustomization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (depotAppCustomization != null) {
			clearCache(depotAppCustomization);
		}

		return depotAppCustomization;
	}

	@Override
	public DepotAppCustomization updateImpl(
		DepotAppCustomization depotAppCustomization) {

		boolean isNew = depotAppCustomization.isNew();

		if (!(depotAppCustomization instanceof
				DepotAppCustomizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(depotAppCustomization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					depotAppCustomization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in depotAppCustomization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DepotAppCustomization implementation " +
					depotAppCustomization.getClass());
		}

		DepotAppCustomizationModelImpl depotAppCustomizationModelImpl =
			(DepotAppCustomizationModelImpl)depotAppCustomization;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(depotAppCustomization)) {
				if (!isNew) {
					session.evict(
						DepotAppCustomizationImpl.class,
						depotAppCustomization.getPrimaryKeyObj());
				}

				session.save(depotAppCustomization);
			}
			else {
				depotAppCustomization = (DepotAppCustomization)session.merge(
					depotAppCustomization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(depotAppCustomization, false);

		if (isNew) {
			depotAppCustomization.setNew(false);
		}

		depotAppCustomization.resetOriginalValues();

		return depotAppCustomization;
	}

	/**
	 * Returns the depot app customization with the primary key or throws a <code>NoSuchAppCustomizationException</code> if it could not be found.
	 *
	 * @param depotAppCustomizationId the primary key of the depot app customization
	 * @return the depot app customization
	 * @throws NoSuchAppCustomizationException if a depot app customization with the primary key could not be found
	 */
	@Override
	public DepotAppCustomization findByPrimaryKey(long depotAppCustomizationId)
		throws NoSuchAppCustomizationException {

		return findByPrimaryKey((Serializable)depotAppCustomizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the depot app customization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotAppCustomizationId the primary key of the depot app customization
	 * @return the depot app customization, or <code>null</code> if a depot app customization with the primary key could not be found
	 */
	@Override
	public DepotAppCustomization fetchByPrimaryKey(
		long depotAppCustomizationId) {

		return fetchByPrimaryKey((Serializable)depotAppCustomizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "depotAppCustomizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEPOTAPPCUSTOMIZATION;
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
		return DepotAppCustomizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DepotAppCustomization";
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
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("depotEntryId");
		ctMergeColumnNames.add("enabled");
		ctMergeColumnNames.add("portletId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("depotAppCustomizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"depotEntryId", "portletId"});
	}

	/**
	 * Initializes the depot app customization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByDepotEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDepotEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"depotEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDepotEntryId", new String[] {Long.class.getName()},
					new String[] {"depotEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDepotEntryId", new String[] {Long.class.getName()},
					new String[] {"depotEntryId"}, false),
				_SQL_SELECT_DEPOTAPPCUSTOMIZATION_WHERE,
				_SQL_COUNT_DEPOTAPPCUSTOMIZATION_WHERE,
				DepotAppCustomizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"depotAppCustomization.", "depotEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					DepotAppCustomization::getDepotEntryId));

		_uniquePersistenceFinderByD_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByD_E",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"depotEntryId", "enabled"}, 0, 0, false,
				DepotAppCustomization::getDepotEntryId,
				DepotAppCustomization::isEnabled),
			_SQL_SELECT_DEPOTAPPCUSTOMIZATION_WHERE, "",
			new FinderColumn<>(
				"depotAppCustomization.", "depotEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				DepotAppCustomization::getDepotEntryId),
			new FinderColumn<>(
				"depotAppCustomization.", "enabled", FinderColumn.Type.BOOLEAN,
				"=", true, true, DepotAppCustomization::isEnabled));

		_uniquePersistenceFinderByD_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByD_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"depotEntryId", "portletId"}, 0, 2, false,
				DepotAppCustomization::getDepotEntryId,
				convertNullFunction(DepotAppCustomization::getPortletId)),
			_SQL_SELECT_DEPOTAPPCUSTOMIZATION_WHERE, "",
			new FinderColumn<>(
				"depotAppCustomization.", "depotEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				DepotAppCustomization::getDepotEntryId),
			new FinderColumn<>(
				"depotAppCustomization.", "portletId", FinderColumn.Type.STRING,
				"=", true, true, DepotAppCustomization::getPortletId));

		DepotAppCustomizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DepotAppCustomizationUtil.setPersistence(null);

		entityCache.removeCache(DepotAppCustomizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DepotAppCustomizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEPOTAPPCUSTOMIZATION =
		"SELECT depotAppCustomization FROM DepotAppCustomization depotAppCustomization";

	private static final String _SQL_SELECT_DEPOTAPPCUSTOMIZATION_WHERE =
		"SELECT depotAppCustomization FROM DepotAppCustomization depotAppCustomization WHERE ";

	private static final String _SQL_COUNT_DEPOTAPPCUSTOMIZATION_WHERE =
		"SELECT COUNT(depotAppCustomization) FROM DepotAppCustomization depotAppCustomization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:896640607
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPluginSettingException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PluginSetting;
import com.liferay.portal.kernel.model.PluginSettingTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PluginSettingPersistence;
import com.liferay.portal.kernel.service.persistence.PluginSettingUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.PluginSettingImpl;
import com.liferay.portal.model.impl.PluginSettingModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the plugin setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PluginSettingPersistenceImpl
	extends BasePersistenceImpl<PluginSetting, NoSuchPluginSettingException>
	implements PluginSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PluginSettingUtil</code> to access the plugin setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PluginSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PluginSetting, NoSuchPluginSettingException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the plugin settings where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PluginSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of plugin settings
	 * @param end the upper bound of the range of plugin settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching plugin settings
	 */
	@Override
	public List<PluginSetting> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PluginSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first plugin setting in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plugin setting
	 * @throws NoSuchPluginSettingException if a matching plugin setting could not be found
	 */
	@Override
	public PluginSetting findByCompanyId_First(
			long companyId, OrderByComparator<PluginSetting> orderByComparator)
		throws NoSuchPluginSettingException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first plugin setting in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	@Override
	public PluginSetting fetchByCompanyId_First(
		long companyId, OrderByComparator<PluginSetting> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the plugin settings where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of plugin settings where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching plugin settings
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private UniquePersistenceFinder<PluginSetting, NoSuchPluginSettingException>
		_uniquePersistenceFinderByC_P_P;

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the matching plugin setting
	 * @throws NoSuchPluginSettingException if a matching plugin setting could not be found
	 */
	@Override
	public PluginSetting findByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws NoSuchPluginSettingException {

		return _uniquePersistenceFinderByC_P_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, pluginId, pluginType});
	}

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	@Override
	public PluginSetting fetchByC_P_P(
		long companyId, String pluginId, String pluginType,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_P_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, pluginId, pluginType}, useFinderCache);
	}

	/**
	 * Removes the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the plugin setting that was removed
	 */
	@Override
	public PluginSetting removeByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws NoSuchPluginSettingException {

		PluginSetting pluginSetting = findByC_P_P(
			companyId, pluginId, pluginType);

		return remove(pluginSetting);
	}

	/**
	 * Returns the number of plugin settings where companyId = &#63; and pluginId = &#63; and pluginType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the number of matching plugin settings
	 */
	@Override
	public int countByC_P_P(
		long companyId, String pluginId, String pluginType) {

		return _uniquePersistenceFinderByC_P_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, pluginId, pluginType});
	}

	public PluginSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PluginSetting.class);

		setModelImplClass(PluginSettingImpl.class);
		setModelPKClass(long.class);

		setTable(PluginSettingTable.INSTANCE);
	}

	/**
	 * Creates a new plugin setting with the primary key. Does not add the plugin setting to the database.
	 *
	 * @param pluginSettingId the primary key for the new plugin setting
	 * @return the new plugin setting
	 */
	@Override
	public PluginSetting create(long pluginSettingId) {
		PluginSetting pluginSetting = new PluginSettingImpl();

		pluginSetting.setNew(true);
		pluginSetting.setPrimaryKey(pluginSettingId);

		pluginSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return pluginSetting;
	}

	/**
	 * Removes the plugin setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting that was removed
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	@Override
	public PluginSetting remove(long pluginSettingId)
		throws NoSuchPluginSettingException {

		return remove((Serializable)pluginSettingId);
	}

	@Override
	protected PluginSetting removeImpl(PluginSetting pluginSetting) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(pluginSetting)) {
				pluginSetting = (PluginSetting)session.get(
					PluginSettingImpl.class, pluginSetting.getPrimaryKeyObj());
			}

			if (pluginSetting != null) {
				session.delete(pluginSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (pluginSetting != null) {
			clearCache(pluginSetting);
		}

		return pluginSetting;
	}

	@Override
	public PluginSetting updateImpl(PluginSetting pluginSetting) {
		boolean isNew = pluginSetting.isNew();

		if (!(pluginSetting instanceof PluginSettingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(pluginSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					pluginSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in pluginSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PluginSetting implementation " +
					pluginSetting.getClass());
		}

		PluginSettingModelImpl pluginSettingModelImpl =
			(PluginSettingModelImpl)pluginSetting;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(pluginSetting);
			}
			else {
				pluginSetting = (PluginSetting)session.merge(pluginSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(pluginSetting, false);

		if (isNew) {
			pluginSetting.setNew(false);
		}

		pluginSetting.resetOriginalValues();

		return pluginSetting;
	}

	/**
	 * Returns the plugin setting with the primary key or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	@Override
	public PluginSetting findByPrimaryKey(long pluginSettingId)
		throws NoSuchPluginSettingException {

		return findByPrimaryKey((Serializable)pluginSettingId);
	}

	/**
	 * Returns the plugin setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting, or <code>null</code> if a plugin setting with the primary key could not be found
	 */
	@Override
	public PluginSetting fetchByPrimaryKey(long pluginSettingId) {
		return fetchByPrimaryKey((Serializable)pluginSettingId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "pluginSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PLUGINSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PluginSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the plugin setting persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_PLUGINSETTING_WHERE, _SQL_COUNT_PLUGINSETTING_WHERE,
				PluginSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"pluginSetting.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, PluginSetting::getCompanyId));

		_uniquePersistenceFinderByC_P_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_P_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "pluginId", "pluginType"}, 0, 6,
				false, PluginSetting::getCompanyId,
				convertNullFunction(PluginSetting::getPluginId),
				convertNullFunction(PluginSetting::getPluginType)),
			_SQL_SELECT_PLUGINSETTING_WHERE, "",
			new FinderColumn<>(
				"pluginSetting.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, PluginSetting::getCompanyId),
			new FinderColumn<>(
				"pluginSetting.", "pluginId", FinderColumn.Type.STRING, "=",
				true, true, PluginSetting::getPluginId),
			new FinderColumn<>(
				"pluginSetting.", "pluginType", FinderColumn.Type.STRING, "=",
				true, true, PluginSetting::getPluginType));

		PluginSettingUtil.setPersistence(this);
	}

	public void destroy() {
		PluginSettingUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PluginSettingImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PluginSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PLUGINSETTING =
		"SELECT pluginSetting FROM PluginSetting pluginSetting";

	private static final String _SQL_SELECT_PLUGINSETTING_WHERE =
		"SELECT pluginSetting FROM PluginSetting pluginSetting WHERE ";

	private static final String _SQL_COUNT_PLUGINSETTING_WHERE =
		"SELECT COUNT(pluginSetting) FROM PluginSetting pluginSetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PluginSetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PluginSettingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:51615441
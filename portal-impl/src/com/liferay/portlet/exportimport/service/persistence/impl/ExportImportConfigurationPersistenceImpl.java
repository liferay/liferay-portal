/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.persistence.impl;

import com.liferay.exportimport.kernel.exception.NoSuchConfigurationException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.model.ExportImportConfigurationTable;
import com.liferay.exportimport.kernel.service.persistence.ExportImportConfigurationPersistence;
import com.liferay.exportimport.kernel.service.persistence.ExportImportConfigurationUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationImpl;
import com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the export import configuration service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExportImportConfigurationPersistenceImpl
	extends BasePersistenceImpl
		<ExportImportConfiguration, NoSuchConfigurationException>
	implements ExportImportConfigurationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExportImportConfigurationUtil</code> to access the export import configuration persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExportImportConfigurationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ExportImportConfiguration, NoSuchConfigurationException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the export import configurations where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import configurations
	 */
	@Override
	public List<ExportImportConfiguration> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration
	 * @throws NoSuchConfigurationException if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration findByGroupId_First(
			long groupId,
			OrderByComparator<ExportImportConfiguration> orderByComparator)
		throws NoSuchConfigurationException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration, or <code>null</code> if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByGroupId_First(
		long groupId,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the export import configurations where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of export import configurations where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching export import configurations
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<ExportImportConfiguration, NoSuchConfigurationException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the export import configurations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import configurations
	 */
	@Override
	public List<ExportImportConfiguration> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first export import configuration in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration
	 * @throws NoSuchConfigurationException if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration findByCompanyId_First(
			long companyId,
			OrderByComparator<ExportImportConfiguration> orderByComparator)
		throws NoSuchConfigurationException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first export import configuration in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration, or <code>null</code> if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the export import configurations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of export import configurations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching export import configurations
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<ExportImportConfiguration, NoSuchConfigurationException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the export import configurations where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import configurations
	 */
	@Override
	public List<ExportImportConfiguration> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration
	 * @throws NoSuchConfigurationException if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration findByG_T_First(
			long groupId, int type,
			OrderByComparator<ExportImportConfiguration> orderByComparator)
		throws NoSuchConfigurationException {

		return _collectionPersistenceFinderByG_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			orderByComparator);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration, or <code>null</code> if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			orderByComparator);
	}

	/**
	 * Removes all the export import configurations where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, int type) {
		_collectionPersistenceFinderByG_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type});
	}

	/**
	 * Returns the number of export import configurations where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching export import configurations
	 */
	@Override
	public int countByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type});
	}

	private CollectionPersistenceFinder
		<ExportImportConfiguration, NoSuchConfigurationException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the export import configurations where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import configurations
	 */
	@Override
	public List<ExportImportConfiguration> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration
	 * @throws NoSuchConfigurationException if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration findByG_S_First(
			long groupId, int status,
			OrderByComparator<ExportImportConfiguration> orderByComparator)
		throws NoSuchConfigurationException {

		return _collectionPersistenceFinderByG_S.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, status},
			orderByComparator);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration, or <code>null</code> if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, status},
			orderByComparator);
	}

	/**
	 * Removes all the export import configurations where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, status});
	}

	/**
	 * Returns the number of export import configurations where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching export import configurations
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, status});
	}

	private CollectionPersistenceFinder
		<ExportImportConfiguration, NoSuchConfigurationException>
			_collectionPersistenceFinderByG_T_S;

	/**
	 * Returns an ordered range of all the export import configurations where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import configurations
	 */
	@Override
	public List<ExportImportConfiguration> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, type, status}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration
	 * @throws NoSuchConfigurationException if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration findByG_T_S_First(
			long groupId, int type, int status,
			OrderByComparator<ExportImportConfiguration> orderByComparator)
		throws NoSuchConfigurationException {

		return _collectionPersistenceFinderByG_T_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, type, status}, orderByComparator);
	}

	/**
	 * Returns the first export import configuration in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import configuration, or <code>null</code> if a matching export import configuration could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByG_T_S_First(
		long groupId, int type, int status,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return _collectionPersistenceFinderByG_T_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, type, status}, orderByComparator);
	}

	/**
	 * Removes all the export import configurations where groupId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByG_T_S(long groupId, int type, int status) {
		_collectionPersistenceFinderByG_T_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, type, status});
	}

	/**
	 * Returns the number of export import configurations where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching export import configurations
	 */
	@Override
	public int countByG_T_S(long groupId, int type, int status) {
		return _collectionPersistenceFinderByG_T_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, type, status});
	}

	public ExportImportConfigurationPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");
		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExportImportConfiguration.class);

		setModelImplClass(ExportImportConfigurationImpl.class);
		setModelPKClass(long.class);

		setTable(ExportImportConfigurationTable.INSTANCE);
	}

	/**
	 * Creates a new export import configuration with the primary key. Does not add the export import configuration to the database.
	 *
	 * @param exportImportConfigurationId the primary key for the new export import configuration
	 * @return the new export import configuration
	 */
	@Override
	public ExportImportConfiguration create(long exportImportConfigurationId) {
		ExportImportConfiguration exportImportConfiguration =
			new ExportImportConfigurationImpl();

		exportImportConfiguration.setNew(true);
		exportImportConfiguration.setPrimaryKey(exportImportConfigurationId);

		exportImportConfiguration.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return exportImportConfiguration;
	}

	/**
	 * Removes the export import configuration with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration that was removed
	 * @throws NoSuchConfigurationException if a export import configuration with the primary key could not be found
	 */
	@Override
	public ExportImportConfiguration remove(long exportImportConfigurationId)
		throws NoSuchConfigurationException {

		return remove((Serializable)exportImportConfigurationId);
	}

	@Override
	protected ExportImportConfiguration removeImpl(
		ExportImportConfiguration exportImportConfiguration) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(exportImportConfiguration)) {
				exportImportConfiguration =
					(ExportImportConfiguration)session.get(
						ExportImportConfigurationImpl.class,
						exportImportConfiguration.getPrimaryKeyObj());
			}

			if (exportImportConfiguration != null) {
				session.delete(exportImportConfiguration);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (exportImportConfiguration != null) {
			clearCache(exportImportConfiguration);
		}

		return exportImportConfiguration;
	}

	@Override
	public ExportImportConfiguration updateImpl(
		ExportImportConfiguration exportImportConfiguration) {

		boolean isNew = exportImportConfiguration.isNew();

		if (!(exportImportConfiguration instanceof
				ExportImportConfigurationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(exportImportConfiguration.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					exportImportConfiguration);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in exportImportConfiguration proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExportImportConfiguration implementation " +
					exportImportConfiguration.getClass());
		}

		ExportImportConfigurationModelImpl exportImportConfigurationModelImpl =
			(ExportImportConfigurationModelImpl)exportImportConfiguration;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (exportImportConfiguration.getCreateDate() == null)) {
			if (serviceContext == null) {
				exportImportConfiguration.setCreateDate(date);
			}
			else {
				exportImportConfiguration.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!exportImportConfigurationModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				exportImportConfiguration.setModifiedDate(date);
			}
			else {
				exportImportConfiguration.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(exportImportConfiguration);
			}
			else {
				exportImportConfiguration =
					(ExportImportConfiguration)session.merge(
						exportImportConfiguration);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(exportImportConfiguration, false);

		if (isNew) {
			exportImportConfiguration.setNew(false);
		}

		exportImportConfiguration.resetOriginalValues();

		return exportImportConfiguration;
	}

	/**
	 * Returns the export import configuration with the primary key or throws a <code>NoSuchConfigurationException</code> if it could not be found.
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration
	 * @throws NoSuchConfigurationException if a export import configuration with the primary key could not be found
	 */
	@Override
	public ExportImportConfiguration findByPrimaryKey(
			long exportImportConfigurationId)
		throws NoSuchConfigurationException {

		return findByPrimaryKey((Serializable)exportImportConfigurationId);
	}

	/**
	 * Returns the export import configuration with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration, or <code>null</code> if a export import configuration with the primary key could not be found
	 */
	@Override
	public ExportImportConfiguration fetchByPrimaryKey(
		long exportImportConfigurationId) {

		return fetchByPrimaryKey((Serializable)exportImportConfigurationId);
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
		return "exportImportConfigurationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPORTIMPORTCONFIGURATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ExportImportConfigurationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the export import configuration persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE,
				_SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE,
				ExportImportConfigurationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"exportImportConfiguration.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					ExportImportConfiguration::getGroupId));

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
				_SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE,
				_SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE,
				ExportImportConfigurationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"exportImportConfiguration.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ExportImportConfiguration::getCompanyId));

		_collectionPersistenceFinderByG_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "type_"}, false),
			_SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE,
			_SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE,
			ExportImportConfigurationModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"exportImportConfiguration.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportConfiguration::getGroupId),
			new FinderColumn<>(
				"exportImportConfiguration.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				ExportImportConfiguration::getType));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, false),
			_SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE,
			_SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE,
			ExportImportConfigurationModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"exportImportConfiguration.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportConfiguration::getGroupId),
			new FinderColumn<>(
				"exportImportConfiguration.", "status",
				FinderColumn.Type.INTEGER, "=", true, true,
				ExportImportConfiguration::getStatus));

		_collectionPersistenceFinderByG_T_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "type_", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "type_", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "type_", "status"}, false),
			_SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE,
			_SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE,
			ExportImportConfigurationModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"exportImportConfiguration.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ExportImportConfiguration::getGroupId),
			new FinderColumn<>(
				"exportImportConfiguration.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				ExportImportConfiguration::getType),
			new FinderColumn<>(
				"exportImportConfiguration.", "status",
				FinderColumn.Type.INTEGER, "=", true, true,
				ExportImportConfiguration::getStatus));

		ExportImportConfigurationUtil.setPersistence(this);
	}

	public void destroy() {
		ExportImportConfigurationUtil.setPersistence(null);

		EntityCacheUtil.removeCache(
			ExportImportConfigurationImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ExportImportConfigurationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPORTIMPORTCONFIGURATION =
		"SELECT exportImportConfiguration FROM ExportImportConfiguration exportImportConfiguration";

	private static final String _SQL_SELECT_EXPORTIMPORTCONFIGURATION_WHERE =
		"SELECT exportImportConfiguration FROM ExportImportConfiguration exportImportConfiguration WHERE ";

	private static final String _SQL_COUNT_EXPORTIMPORTCONFIGURATION_WHERE =
		"SELECT COUNT(exportImportConfiguration) FROM ExportImportConfiguration exportImportConfiguration WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExportImportConfiguration exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type", "settings"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-978917097
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSettingTable;
import com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingImpl;
import com.liferay.commerce.product.type.virtual.model.impl.CPDefinitionVirtualSettingModelImpl;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDefinitionVirtualSettingPersistence;
import com.liferay.commerce.product.type.virtual.service.persistence.CPDefinitionVirtualSettingUtil;
import com.liferay.commerce.product.type.virtual.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cp definition virtual setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPDefinitionVirtualSettingPersistence.class)
public class CPDefinitionVirtualSettingPersistenceImpl
	extends BasePersistenceImpl
		<CPDefinitionVirtualSetting, NoSuchCPDefinitionVirtualSettingException>
	implements CPDefinitionVirtualSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPDefinitionVirtualSettingUtil</code> to access the cp definition virtual setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPDefinitionVirtualSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPDefinitionVirtualSetting, NoSuchCPDefinitionVirtualSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp definition virtual settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionVirtualSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition virtual settings
	 * @param end the upper bound of the range of cp definition virtual settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition virtual settings
	 */
	@Override
	public List<CPDefinitionVirtualSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionVirtualSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition virtual setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition virtual setting
	 * @throws NoSuchCPDefinitionVirtualSettingException if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionVirtualSetting> orderByComparator)
		throws NoSuchCPDefinitionVirtualSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp definition virtual setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionVirtualSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp definition virtual settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp definition virtual settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition virtual settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPDefinitionVirtualSetting, NoSuchCPDefinitionVirtualSettingException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp definition virtual setting where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionVirtualSettingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition virtual setting
	 * @throws NoSuchCPDefinitionVirtualSettingException if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionVirtualSettingException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp definition virtual setting where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp definition virtual setting where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition virtual setting that was removed
	 */
	@Override
	public CPDefinitionVirtualSetting removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionVirtualSettingException {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting = findByUUID_G(
			uuid, groupId);

		return remove(cpDefinitionVirtualSetting);
	}

	/**
	 * Returns the number of cp definition virtual settings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition virtual settings
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPDefinitionVirtualSetting, NoSuchCPDefinitionVirtualSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp definition virtual settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPDefinitionVirtualSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition virtual settings
	 * @param end the upper bound of the range of cp definition virtual settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition virtual settings
	 */
	@Override
	public List<CPDefinitionVirtualSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionVirtualSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition virtual setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition virtual setting
	 * @throws NoSuchCPDefinitionVirtualSettingException if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionVirtualSetting> orderByComparator)
		throws NoSuchCPDefinitionVirtualSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp definition virtual setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionVirtualSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp definition virtual settings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of cp definition virtual settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition virtual settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<CPDefinitionVirtualSetting, NoSuchCPDefinitionVirtualSettingException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the cp definition virtual setting where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchCPDefinitionVirtualSettingException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching cp definition virtual setting
	 * @throws NoSuchCPDefinitionVirtualSettingException if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting findByC_C(long classNameId, long classPK)
		throws NoSuchCPDefinitionVirtualSettingException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the cp definition virtual setting where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition virtual setting, or <code>null</code> if a matching cp definition virtual setting could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the cp definition virtual setting where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the cp definition virtual setting that was removed
	 */
	@Override
	public CPDefinitionVirtualSetting removeByC_C(
			long classNameId, long classPK)
		throws NoSuchCPDefinitionVirtualSettingException {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting = findByC_C(
			classNameId, classPK);

		return remove(cpDefinitionVirtualSetting);
	}

	/**
	 * Returns the number of cp definition virtual settings where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp definition virtual settings
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public CPDefinitionVirtualSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"termsOfUseJournalArticleResourcePrimKey",
			"termsOfUseArticleResourcePK");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPDefinitionVirtualSetting.class);

		setModelImplClass(CPDefinitionVirtualSettingImpl.class);
		setModelPKClass(long.class);

		setTable(CPDefinitionVirtualSettingTable.INSTANCE);
	}

	/**
	 * Creates a new cp definition virtual setting with the primary key. Does not add the cp definition virtual setting to the database.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key for the new cp definition virtual setting
	 * @return the new cp definition virtual setting
	 */
	@Override
	public CPDefinitionVirtualSetting create(
		long CPDefinitionVirtualSettingId) {

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			new CPDefinitionVirtualSettingImpl();

		cpDefinitionVirtualSetting.setNew(true);
		cpDefinitionVirtualSetting.setPrimaryKey(CPDefinitionVirtualSettingId);

		String uuid = PortalUUIDUtil.generate();

		cpDefinitionVirtualSetting.setUuid(uuid);

		cpDefinitionVirtualSetting.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpDefinitionVirtualSetting;
	}

	/**
	 * Removes the cp definition virtual setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting that was removed
	 * @throws NoSuchCPDefinitionVirtualSettingException if a cp definition virtual setting with the primary key could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting remove(long CPDefinitionVirtualSettingId)
		throws NoSuchCPDefinitionVirtualSettingException {

		return remove((Serializable)CPDefinitionVirtualSettingId);
	}

	@Override
	protected CPDefinitionVirtualSetting removeImpl(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpDefinitionVirtualSetting)) {
				cpDefinitionVirtualSetting =
					(CPDefinitionVirtualSetting)session.get(
						CPDefinitionVirtualSettingImpl.class,
						cpDefinitionVirtualSetting.getPrimaryKeyObj());
			}

			if (cpDefinitionVirtualSetting != null) {
				session.delete(cpDefinitionVirtualSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpDefinitionVirtualSetting != null) {
			clearCache(cpDefinitionVirtualSetting);
		}

		return cpDefinitionVirtualSetting;
	}

	@Override
	public CPDefinitionVirtualSetting updateImpl(
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting) {

		boolean isNew = cpDefinitionVirtualSetting.isNew();

		if (!(cpDefinitionVirtualSetting instanceof
				CPDefinitionVirtualSettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpDefinitionVirtualSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpDefinitionVirtualSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpDefinitionVirtualSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPDefinitionVirtualSetting implementation " +
					cpDefinitionVirtualSetting.getClass());
		}

		CPDefinitionVirtualSettingModelImpl
			cpDefinitionVirtualSettingModelImpl =
				(CPDefinitionVirtualSettingModelImpl)cpDefinitionVirtualSetting;

		if (Validator.isNull(cpDefinitionVirtualSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpDefinitionVirtualSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpDefinitionVirtualSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpDefinitionVirtualSetting.setCreateDate(date);
			}
			else {
				cpDefinitionVirtualSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpDefinitionVirtualSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpDefinitionVirtualSetting.setModifiedDate(date);
			}
			else {
				cpDefinitionVirtualSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cpDefinitionVirtualSetting);
			}
			else {
				cpDefinitionVirtualSetting =
					(CPDefinitionVirtualSetting)session.merge(
						cpDefinitionVirtualSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpDefinitionVirtualSetting, false);

		if (isNew) {
			cpDefinitionVirtualSetting.setNew(false);
		}

		cpDefinitionVirtualSetting.resetOriginalValues();

		return cpDefinitionVirtualSetting;
	}

	/**
	 * Returns the cp definition virtual setting with the primary key or throws a <code>NoSuchCPDefinitionVirtualSettingException</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting
	 * @throws NoSuchCPDefinitionVirtualSettingException if a cp definition virtual setting with the primary key could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting findByPrimaryKey(
			long CPDefinitionVirtualSettingId)
		throws NoSuchCPDefinitionVirtualSettingException {

		return findByPrimaryKey((Serializable)CPDefinitionVirtualSettingId);
	}

	/**
	 * Returns the cp definition virtual setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingId the primary key of the cp definition virtual setting
	 * @return the cp definition virtual setting, or <code>null</code> if a cp definition virtual setting with the primary key could not be found
	 */
	@Override
	public CPDefinitionVirtualSetting fetchByPrimaryKey(
		long CPDefinitionVirtualSettingId) {

		return fetchByPrimaryKey((Serializable)CPDefinitionVirtualSettingId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CPDefinitionVirtualSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPDEFINITIONVIRTUALSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CPDefinitionVirtualSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cp definition virtual setting persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_CPDEFINITIONVIRTUALSETTING_WHERE,
			_SQL_COUNT_CPDEFINITIONVIRTUALSETTING_WHERE,
			CPDefinitionVirtualSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpDefinitionVirtualSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionVirtualSetting::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPDefinitionVirtualSetting::getUuid),
				CPDefinitionVirtualSetting::getGroupId),
			_SQL_SELECT_CPDEFINITIONVIRTUALSETTING_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionVirtualSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPDefinitionVirtualSetting::getUuid),
			new FinderColumn<>(
				"cpDefinitionVirtualSetting.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionVirtualSetting::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_CPDEFINITIONVIRTUALSETTING_WHERE,
				_SQL_COUNT_CPDEFINITIONVIRTUALSETTING_WHERE,
				CPDefinitionVirtualSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpDefinitionVirtualSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPDefinitionVirtualSetting::getUuid),
				new FinderColumn<>(
					"cpDefinitionVirtualSetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPDefinitionVirtualSetting::getCompanyId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				CPDefinitionVirtualSetting::getClassNameId,
				CPDefinitionVirtualSetting::getClassPK),
			_SQL_SELECT_CPDEFINITIONVIRTUALSETTING_WHERE, "",
			new FinderColumn<>(
				"cpDefinitionVirtualSetting.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionVirtualSetting::getClassNameId),
			new FinderColumn<>(
				"cpDefinitionVirtualSetting.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CPDefinitionVirtualSetting::getClassPK));

		CPDefinitionVirtualSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPDefinitionVirtualSettingUtil.setPersistence(null);

		entityCache.removeCache(CPDefinitionVirtualSettingImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CPDefinitionVirtualSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPDEFINITIONVIRTUALSETTING =
		"SELECT cpDefinitionVirtualSetting FROM CPDefinitionVirtualSetting cpDefinitionVirtualSetting";

	private static final String _SQL_SELECT_CPDEFINITIONVIRTUALSETTING_WHERE =
		"SELECT cpDefinitionVirtualSetting FROM CPDefinitionVirtualSetting cpDefinitionVirtualSetting WHERE ";

	private static final String _SQL_COUNT_CPDEFINITIONVIRTUALSETTING_WHERE =
		"SELECT COUNT(cpDefinitionVirtualSetting) FROM CPDefinitionVirtualSetting cpDefinitionVirtualSetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPDefinitionVirtualSetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionVirtualSettingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "termsOfUseJournalArticleResourcePrimKey"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1925256496
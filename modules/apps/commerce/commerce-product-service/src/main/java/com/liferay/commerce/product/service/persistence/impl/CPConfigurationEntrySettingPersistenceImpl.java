/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntrySettingException;
import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.commerce.product.model.CPConfigurationEntrySettingTable;
import com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingImpl;
import com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntrySettingPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntrySettingUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the cp configuration entry setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPConfigurationEntrySettingPersistence.class)
public class CPConfigurationEntrySettingPersistenceImpl
	extends BasePersistenceImpl
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
	implements CPConfigurationEntrySettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPConfigurationEntrySettingUtil</code> to access the cp configuration entry setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPConfigurationEntrySettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entry settings
	 */
	@Override
	public List<CPConfigurationEntrySetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByUuid_First(
			String uuid,
			OrderByComparator<CPConfigurationEntrySetting> orderByComparator)
		throws NoSuchCPConfigurationEntrySettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByUuid_First(
		String uuid,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entry settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cp configuration entry settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entry settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the cp configuration entry setting where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntrySettingException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the cp configuration entry setting where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry setting where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry setting that was removed
	 */
	@Override
	public CPConfigurationEntrySetting removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntrySettingException {

		CPConfigurationEntrySetting cpConfigurationEntrySetting = findByUUID_G(
			uuid, groupId);

		return remove(cpConfigurationEntrySetting);
	}

	/**
	 * Returns the number of cp configuration entry settings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entry settings
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entry settings
	 */
	@Override
	public List<CPConfigurationEntrySetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntrySetting> orderByComparator)
		throws NoSuchCPConfigurationEntrySettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entry settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entry settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cp configuration entry settings where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entry settings
	 */
	@Override
	public List<CPConfigurationEntrySetting> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByCompanyId_First(
			long companyId,
			OrderByComparator<CPConfigurationEntrySetting> orderByComparator)
		throws NoSuchCPConfigurationEntrySettingException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entry settings where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cp configuration entry settings where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entry settings
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<CPConfigurationEntrySetting,
		 NoSuchCPConfigurationEntrySettingException>
			_uniquePersistenceFinderByC_T;

	/**
	 * Returns the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByC_T(
			long CPConfigurationEntryId, int type)
		throws NoSuchCPConfigurationEntrySettingException {

		return _uniquePersistenceFinderByC_T.find(
			finderCache, new Object[] {CPConfigurationEntryId, type});
	}

	/**
	 * Returns the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByC_T(
		long CPConfigurationEntryId, int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_T.fetch(
			finderCache, new Object[] {CPConfigurationEntryId, type},
			useFinderCache);
	}

	/**
	 * Removes the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; from the database.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the cp configuration entry setting that was removed
	 */
	@Override
	public CPConfigurationEntrySetting removeByC_T(
			long CPConfigurationEntryId, int type)
		throws NoSuchCPConfigurationEntrySettingException {

		CPConfigurationEntrySetting cpConfigurationEntrySetting = findByC_T(
			CPConfigurationEntryId, type);

		return remove(cpConfigurationEntrySetting);
	}

	/**
	 * Returns the number of cp configuration entry settings where CPConfigurationEntryId = &#63; and type = &#63;.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the number of matching cp configuration entry settings
	 */
	@Override
	public int countByC_T(long CPConfigurationEntryId, int type) {
		return _uniquePersistenceFinderByC_T.count(
			finderCache, new Object[] {CPConfigurationEntryId, type});
	}

	public CPConfigurationEntrySettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPConfigurationEntrySetting.class);

		setModelImplClass(CPConfigurationEntrySettingImpl.class);
		setModelPKClass(long.class);

		setTable(CPConfigurationEntrySettingTable.INSTANCE);
	}

	/**
	 * Creates a new cp configuration entry setting with the primary key. Does not add the cp configuration entry setting to the database.
	 *
	 * @param CPConfigurationEntrySettingId the primary key for the new cp configuration entry setting
	 * @return the new cp configuration entry setting
	 */
	@Override
	public CPConfigurationEntrySetting create(
		long CPConfigurationEntrySettingId) {

		CPConfigurationEntrySetting cpConfigurationEntrySetting =
			new CPConfigurationEntrySettingImpl();

		cpConfigurationEntrySetting.setNew(true);
		cpConfigurationEntrySetting.setPrimaryKey(
			CPConfigurationEntrySettingId);

		String uuid = PortalUUIDUtil.generate();

		cpConfigurationEntrySetting.setUuid(uuid);

		cpConfigurationEntrySetting.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpConfigurationEntrySetting;
	}

	/**
	 * Removes the cp configuration entry setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting that was removed
	 * @throws NoSuchCPConfigurationEntrySettingException if a cp configuration entry setting with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntrySetting remove(
			long CPConfigurationEntrySettingId)
		throws NoSuchCPConfigurationEntrySettingException {

		return remove((Serializable)CPConfigurationEntrySettingId);
	}

	@Override
	protected CPConfigurationEntrySetting removeImpl(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpConfigurationEntrySetting)) {
				cpConfigurationEntrySetting =
					(CPConfigurationEntrySetting)session.get(
						CPConfigurationEntrySettingImpl.class,
						cpConfigurationEntrySetting.getPrimaryKeyObj());
			}

			if ((cpConfigurationEntrySetting != null) &&
				ctPersistenceHelper.isRemove(cpConfigurationEntrySetting)) {

				session.delete(cpConfigurationEntrySetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpConfigurationEntrySetting != null) {
			clearCache(cpConfigurationEntrySetting);
		}

		return cpConfigurationEntrySetting;
	}

	@Override
	public CPConfigurationEntrySetting updateImpl(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		boolean isNew = cpConfigurationEntrySetting.isNew();

		if (!(cpConfigurationEntrySetting instanceof
				CPConfigurationEntrySettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					cpConfigurationEntrySetting.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					cpConfigurationEntrySetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpConfigurationEntrySetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPConfigurationEntrySetting implementation " +
					cpConfigurationEntrySetting.getClass());
		}

		CPConfigurationEntrySettingModelImpl
			cpConfigurationEntrySettingModelImpl =
				(CPConfigurationEntrySettingModelImpl)
					cpConfigurationEntrySetting;

		if (Validator.isNull(cpConfigurationEntrySetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			cpConfigurationEntrySetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpConfigurationEntrySetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpConfigurationEntrySetting.setCreateDate(date);
			}
			else {
				cpConfigurationEntrySetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpConfigurationEntrySettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpConfigurationEntrySetting.setModifiedDate(date);
			}
			else {
				cpConfigurationEntrySetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpConfigurationEntrySetting)) {
				if (!isNew) {
					session.evict(
						CPConfigurationEntrySettingImpl.class,
						cpConfigurationEntrySetting.getPrimaryKeyObj());
				}

				session.save(cpConfigurationEntrySetting);
			}
			else {
				cpConfigurationEntrySetting =
					(CPConfigurationEntrySetting)session.merge(
						cpConfigurationEntrySetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpConfigurationEntrySetting, false);

		if (isNew) {
			cpConfigurationEntrySetting.setNew(false);
		}

		cpConfigurationEntrySetting.resetOriginalValues();

		return cpConfigurationEntrySetting;
	}

	/**
	 * Returns the cp configuration entry setting with the primary key or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a cp configuration entry setting with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntrySetting findByPrimaryKey(
			long CPConfigurationEntrySettingId)
		throws NoSuchCPConfigurationEntrySettingException {

		return findByPrimaryKey((Serializable)CPConfigurationEntrySettingId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp configuration entry setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting, or <code>null</code> if a cp configuration entry setting with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntrySetting fetchByPrimaryKey(
		long CPConfigurationEntrySettingId) {

		return fetchByPrimaryKey((Serializable)CPConfigurationEntrySettingId);
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
		return "CPConfigurationEntrySettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPCONFIGURATIONENTRYSETTING;
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
		return CPConfigurationEntrySettingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPConfigurationEntrySetting";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPConfigurationEntryId");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPConfigurationEntrySettingId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"CPConfigurationEntryId", "type_"});
	}

	/**
	 * Initializes the cp configuration entry setting persistence.
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
			_SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE,
			_SQL_COUNT_CPCONFIGURATIONENTRYSETTING_WHERE,
			CPConfigurationEntrySettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cpConfigurationEntrySetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPConfigurationEntrySetting::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CPConfigurationEntrySetting::getUuid),
				CPConfigurationEntrySetting::getGroupId),
			_SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationEntrySetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CPConfigurationEntrySetting::getUuid),
			new FinderColumn<>(
				"cpConfigurationEntrySetting.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationEntrySetting::getGroupId));

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
				_SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE,
				_SQL_COUNT_CPCONFIGURATIONENTRYSETTING_WHERE,
				CPConfigurationEntrySettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpConfigurationEntrySetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CPConfigurationEntrySetting::getUuid),
				new FinderColumn<>(
					"cpConfigurationEntrySetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationEntrySetting::getCompanyId));

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
				_SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE,
				_SQL_COUNT_CPCONFIGURATIONENTRYSETTING_WHERE,
				CPConfigurationEntrySettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpConfigurationEntrySetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationEntrySetting::getCompanyId));

		_uniquePersistenceFinderByC_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"CPConfigurationEntryId", "type_"}, 0, 0, false,
				CPConfigurationEntrySetting::getCPConfigurationEntryId,
				CPConfigurationEntrySetting::getType),
			_SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationEntrySetting.", "CPConfigurationEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationEntrySetting::getCPConfigurationEntryId),
			new FinderColumn<>(
				"cpConfigurationEntrySetting.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CPConfigurationEntrySetting::getType));

		CPConfigurationEntrySettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPConfigurationEntrySettingUtil.setPersistence(null);

		entityCache.removeCache(
			CPConfigurationEntrySettingImpl.class.getName());
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
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CPConfigurationEntrySettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRYSETTING =
		"SELECT cpConfigurationEntrySetting FROM CPConfigurationEntrySetting cpConfigurationEntrySetting";

	private static final String _SQL_SELECT_CPCONFIGURATIONENTRYSETTING_WHERE =
		"SELECT cpConfigurationEntrySetting FROM CPConfigurationEntrySetting cpConfigurationEntrySetting WHERE ";

	private static final String _SQL_COUNT_CPCONFIGURATIONENTRYSETTING_WHERE =
		"SELECT COUNT(cpConfigurationEntrySetting) FROM CPConfigurationEntrySetting cpConfigurationEntrySetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPConfigurationEntrySetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationEntrySettingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2005921025
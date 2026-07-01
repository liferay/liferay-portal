/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.impl;

import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramSettingException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSettingTable;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramSettingImpl;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramSettingModelImpl;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramSettingPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramSettingUtil;
import com.liferay.commerce.shop.by.diagram.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the cs diagram setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CSDiagramSettingPersistence.class)
public class CSDiagramSettingPersistenceImpl
	extends BasePersistenceImpl
		<CSDiagramSetting, NoSuchCSDiagramSettingException>
	implements CSDiagramSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CSDiagramSettingUtil</code> to access the cs diagram setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CSDiagramSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CSDiagramSetting, NoSuchCSDiagramSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the cs diagram settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cs diagram settings
	 * @param end the upper bound of the range of cs diagram settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram settings
	 */
	@Override
	public List<CSDiagramSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CSDiagramSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cs diagram setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram setting
	 * @throws NoSuchCSDiagramSettingException if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting findByUuid_First(
			String uuid, OrderByComparator<CSDiagramSetting> orderByComparator)
		throws NoSuchCSDiagramSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram setting, or <code>null</code> if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting fetchByUuid_First(
		String uuid, OrderByComparator<CSDiagramSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of cs diagram settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cs diagram settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CSDiagramSetting, NoSuchCSDiagramSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the cs diagram settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cs diagram settings
	 * @param end the upper bound of the range of cs diagram settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram settings
	 */
	@Override
	public List<CSDiagramSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CSDiagramSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram setting
	 * @throws NoSuchCSDiagramSettingException if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CSDiagramSetting> orderByComparator)
		throws NoSuchCSDiagramSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram setting, or <code>null</code> if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CSDiagramSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of cs diagram settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cs diagram settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<CSDiagramSetting, NoSuchCSDiagramSettingException>
			_uniquePersistenceFinderByCPDefinitionId;

	/**
	 * Returns the cs diagram setting where CPDefinitionId = &#63; or throws a <code>NoSuchCSDiagramSettingException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cs diagram setting
	 * @throws NoSuchCSDiagramSettingException if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting findByCPDefinitionId(long CPDefinitionId)
		throws NoSuchCSDiagramSettingException {

		return _uniquePersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the cs diagram setting where CPDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram setting, or <code>null</code> if a matching cs diagram setting could not be found
	 */
	@Override
	public CSDiagramSetting fetchByCPDefinitionId(
		long CPDefinitionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCPDefinitionId.fetch(
			finderCache, new Object[] {CPDefinitionId}, useFinderCache);
	}

	/**
	 * Removes the cs diagram setting where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the cs diagram setting that was removed
	 */
	@Override
	public CSDiagramSetting removeByCPDefinitionId(long CPDefinitionId)
		throws NoSuchCSDiagramSettingException {

		CSDiagramSetting csDiagramSetting = findByCPDefinitionId(
			CPDefinitionId);

		return remove(csDiagramSetting);
	}

	/**
	 * Returns the number of cs diagram settings where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram settings
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _uniquePersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	public CSDiagramSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CSDiagramSetting.class);

		setModelImplClass(CSDiagramSettingImpl.class);
		setModelPKClass(long.class);

		setTable(CSDiagramSettingTable.INSTANCE);
	}

	/**
	 * Creates a new cs diagram setting with the primary key. Does not add the cs diagram setting to the database.
	 *
	 * @param CSDiagramSettingId the primary key for the new cs diagram setting
	 * @return the new cs diagram setting
	 */
	@Override
	public CSDiagramSetting create(long CSDiagramSettingId) {
		CSDiagramSetting csDiagramSetting = new CSDiagramSettingImpl();

		csDiagramSetting.setNew(true);
		csDiagramSetting.setPrimaryKey(CSDiagramSettingId);

		String uuid = PortalUUIDUtil.generate();

		csDiagramSetting.setUuid(uuid);

		csDiagramSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return csDiagramSetting;
	}

	/**
	 * Removes the cs diagram setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramSettingId the primary key of the cs diagram setting
	 * @return the cs diagram setting that was removed
	 * @throws NoSuchCSDiagramSettingException if a cs diagram setting with the primary key could not be found
	 */
	@Override
	public CSDiagramSetting remove(long CSDiagramSettingId)
		throws NoSuchCSDiagramSettingException {

		return remove((Serializable)CSDiagramSettingId);
	}

	@Override
	protected CSDiagramSetting removeImpl(CSDiagramSetting csDiagramSetting) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(csDiagramSetting)) {
				csDiagramSetting = (CSDiagramSetting)session.get(
					CSDiagramSettingImpl.class,
					csDiagramSetting.getPrimaryKeyObj());
			}

			if ((csDiagramSetting != null) &&
				ctPersistenceHelper.isRemove(csDiagramSetting)) {

				session.delete(csDiagramSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (csDiagramSetting != null) {
			clearCache(csDiagramSetting);
		}

		return csDiagramSetting;
	}

	@Override
	public CSDiagramSetting updateImpl(CSDiagramSetting csDiagramSetting) {
		boolean isNew = csDiagramSetting.isNew();

		if (!(csDiagramSetting instanceof CSDiagramSettingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(csDiagramSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					csDiagramSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in csDiagramSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CSDiagramSetting implementation " +
					csDiagramSetting.getClass());
		}

		CSDiagramSettingModelImpl csDiagramSettingModelImpl =
			(CSDiagramSettingModelImpl)csDiagramSetting;

		if (Validator.isNull(csDiagramSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			csDiagramSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (csDiagramSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				csDiagramSetting.setCreateDate(date);
			}
			else {
				csDiagramSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!csDiagramSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				csDiagramSetting.setModifiedDate(date);
			}
			else {
				csDiagramSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(csDiagramSetting)) {
				if (!isNew) {
					session.evict(
						CSDiagramSettingImpl.class,
						csDiagramSetting.getPrimaryKeyObj());
				}

				session.save(csDiagramSetting);
			}
			else {
				csDiagramSetting = (CSDiagramSetting)session.merge(
					csDiagramSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(csDiagramSetting, false);

		if (isNew) {
			csDiagramSetting.setNew(false);
		}

		csDiagramSetting.resetOriginalValues();

		return csDiagramSetting;
	}

	/**
	 * Returns the cs diagram setting with the primary key or throws a <code>NoSuchCSDiagramSettingException</code> if it could not be found.
	 *
	 * @param CSDiagramSettingId the primary key of the cs diagram setting
	 * @return the cs diagram setting
	 * @throws NoSuchCSDiagramSettingException if a cs diagram setting with the primary key could not be found
	 */
	@Override
	public CSDiagramSetting findByPrimaryKey(long CSDiagramSettingId)
		throws NoSuchCSDiagramSettingException {

		return findByPrimaryKey((Serializable)CSDiagramSettingId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cs diagram setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramSettingId the primary key of the cs diagram setting
	 * @return the cs diagram setting, or <code>null</code> if a cs diagram setting with the primary key could not be found
	 */
	@Override
	public CSDiagramSetting fetchByPrimaryKey(long CSDiagramSettingId) {
		return fetchByPrimaryKey((Serializable)CSDiagramSettingId);
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
		return "CSDiagramSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CSDIAGRAMSETTING;
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
		return CSDiagramSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CSDiagramSetting";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPAttachmentFileEntryId");
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("color");
		ctMergeColumnNames.add("radius");
		ctMergeColumnNames.add("type_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CSDiagramSettingId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"CPDefinitionId"});
	}

	/**
	 * Initializes the cs diagram setting persistence.
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
			_SQL_SELECT_CSDIAGRAMSETTING_WHERE,
			_SQL_COUNT_CSDIAGRAMSETTING_WHERE,
			CSDiagramSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"csDiagramSetting.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CSDiagramSetting::getUuid));

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
				_SQL_SELECT_CSDIAGRAMSETTING_WHERE,
				_SQL_COUNT_CSDIAGRAMSETTING_WHERE,
				CSDiagramSettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"csDiagramSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CSDiagramSetting::getUuid),
				new FinderColumn<>(
					"csDiagramSetting.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CSDiagramSetting::getCompanyId));

		_uniquePersistenceFinderByCPDefinitionId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, 0, 0, false,
					CSDiagramSetting::getCPDefinitionId),
				_SQL_SELECT_CSDIAGRAMSETTING_WHERE, "",
				new FinderColumn<>(
					"csDiagramSetting.", "CPDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					CSDiagramSetting::getCPDefinitionId));

		CSDiagramSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CSDiagramSettingUtil.setPersistence(null);

		entityCache.removeCache(CSDiagramSettingImpl.class.getName());
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
		CSDiagramSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CSDIAGRAMSETTING =
		"SELECT csDiagramSetting FROM CSDiagramSetting csDiagramSetting";

	private static final String _SQL_SELECT_CSDIAGRAMSETTING_WHERE =
		"SELECT csDiagramSetting FROM CSDiagramSetting csDiagramSetting WHERE ";

	private static final String _SQL_COUNT_CSDIAGRAMSETTING_WHERE =
		"SELECT COUNT(csDiagramSetting) FROM CSDiagramSetting csDiagramSetting WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1365690516
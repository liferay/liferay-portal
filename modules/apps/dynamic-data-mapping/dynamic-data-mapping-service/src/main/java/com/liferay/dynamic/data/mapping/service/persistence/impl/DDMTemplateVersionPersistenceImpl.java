/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateVersionException;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersionTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateVersionUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 * The persistence implementation for the ddm template version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMTemplateVersionPersistence.class)
public class DDMTemplateVersionPersistenceImpl
	extends BasePersistenceImpl
		<DDMTemplateVersion, NoSuchTemplateVersionException>
	implements DDMTemplateVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMTemplateVersionUtil</code> to access the ddm template version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMTemplateVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMTemplateVersion, NoSuchTemplateVersionException>
			_collectionPersistenceFinderByTemplateId;

	/**
	 * Returns an ordered range of all the ddm template versions where templateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateVersionModelImpl</code>.
	 * </p>
	 *
	 * @param templateId the template ID
	 * @param start the lower bound of the range of ddm template versions
	 * @param end the upper bound of the range of ddm template versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm template versions
	 */
	@Override
	public List<DDMTemplateVersion> findByTemplateId(
		long templateId, int start, int end,
		OrderByComparator<DDMTemplateVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTemplateId.find(
			finderCache, new Object[] {templateId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template version in the ordered set where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template version
	 * @throws NoSuchTemplateVersionException if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion findByTemplateId_First(
			long templateId,
			OrderByComparator<DDMTemplateVersion> orderByComparator)
		throws NoSuchTemplateVersionException {

		return _collectionPersistenceFinderByTemplateId.findFirst(
			finderCache, new Object[] {templateId}, orderByComparator);
	}

	/**
	 * Returns the first ddm template version in the ordered set where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template version, or <code>null</code> if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion fetchByTemplateId_First(
		long templateId,
		OrderByComparator<DDMTemplateVersion> orderByComparator) {

		return _collectionPersistenceFinderByTemplateId.fetchFirst(
			finderCache, new Object[] {templateId}, orderByComparator);
	}

	/**
	 * Removes all the ddm template versions where templateId = &#63; from the database.
	 *
	 * @param templateId the template ID
	 */
	@Override
	public void removeByTemplateId(long templateId) {
		_collectionPersistenceFinderByTemplateId.remove(
			finderCache, new Object[] {templateId});
	}

	/**
	 * Returns the number of ddm template versions where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @return the number of matching ddm template versions
	 */
	@Override
	public int countByTemplateId(long templateId) {
		return _collectionPersistenceFinderByTemplateId.count(
			finderCache, new Object[] {templateId});
	}

	private UniquePersistenceFinder
		<DDMTemplateVersion, NoSuchTemplateVersionException>
			_uniquePersistenceFinderByT_V;

	/**
	 * Returns the ddm template version where templateId = &#63; and version = &#63; or throws a <code>NoSuchTemplateVersionException</code> if it could not be found.
	 *
	 * @param templateId the template ID
	 * @param version the version
	 * @return the matching ddm template version
	 * @throws NoSuchTemplateVersionException if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion findByT_V(long templateId, String version)
		throws NoSuchTemplateVersionException {

		return _uniquePersistenceFinderByT_V.find(
			finderCache, new Object[] {templateId, version});
	}

	/**
	 * Returns the ddm template version where templateId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param templateId the template ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template version, or <code>null</code> if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion fetchByT_V(
		long templateId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByT_V.fetch(
			finderCache, new Object[] {templateId, version}, useFinderCache);
	}

	/**
	 * Removes the ddm template version where templateId = &#63; and version = &#63; from the database.
	 *
	 * @param templateId the template ID
	 * @param version the version
	 * @return the ddm template version that was removed
	 */
	@Override
	public DDMTemplateVersion removeByT_V(long templateId, String version)
		throws NoSuchTemplateVersionException {

		DDMTemplateVersion ddmTemplateVersion = findByT_V(templateId, version);

		return remove(ddmTemplateVersion);
	}

	/**
	 * Returns the number of ddm template versions where templateId = &#63; and version = &#63;.
	 *
	 * @param templateId the template ID
	 * @param version the version
	 * @return the number of matching ddm template versions
	 */
	@Override
	public int countByT_V(long templateId, String version) {
		return _uniquePersistenceFinderByT_V.count(
			finderCache, new Object[] {templateId, version});
	}

	private CollectionPersistenceFinder
		<DDMTemplateVersion, NoSuchTemplateVersionException>
			_collectionPersistenceFinderByT_S;

	/**
	 * Returns an ordered range of all the ddm template versions where templateId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateVersionModelImpl</code>.
	 * </p>
	 *
	 * @param templateId the template ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm template versions
	 * @param end the upper bound of the range of ddm template versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm template versions
	 */
	@Override
	public List<DDMTemplateVersion> findByT_S(
		long templateId, int status, int start, int end,
		OrderByComparator<DDMTemplateVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_S.find(
			finderCache, new Object[] {templateId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template version in the ordered set where templateId = &#63; and status = &#63;.
	 *
	 * @param templateId the template ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template version
	 * @throws NoSuchTemplateVersionException if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion findByT_S_First(
			long templateId, int status,
			OrderByComparator<DDMTemplateVersion> orderByComparator)
		throws NoSuchTemplateVersionException {

		return _collectionPersistenceFinderByT_S.findFirst(
			finderCache, new Object[] {templateId, status}, orderByComparator);
	}

	/**
	 * Returns the first ddm template version in the ordered set where templateId = &#63; and status = &#63;.
	 *
	 * @param templateId the template ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template version, or <code>null</code> if a matching ddm template version could not be found
	 */
	@Override
	public DDMTemplateVersion fetchByT_S_First(
		long templateId, int status,
		OrderByComparator<DDMTemplateVersion> orderByComparator) {

		return _collectionPersistenceFinderByT_S.fetchFirst(
			finderCache, new Object[] {templateId, status}, orderByComparator);
	}

	/**
	 * Removes all the ddm template versions where templateId = &#63; and status = &#63; from the database.
	 *
	 * @param templateId the template ID
	 * @param status the status
	 */
	@Override
	public void removeByT_S(long templateId, int status) {
		_collectionPersistenceFinderByT_S.remove(
			finderCache, new Object[] {templateId, status});
	}

	/**
	 * Returns the number of ddm template versions where templateId = &#63; and status = &#63;.
	 *
	 * @param templateId the template ID
	 * @param status the status
	 * @return the number of matching ddm template versions
	 */
	@Override
	public int countByT_S(long templateId, int status) {
		return _collectionPersistenceFinderByT_S.count(
			finderCache, new Object[] {templateId, status});
	}

	public DDMTemplateVersionPersistenceImpl() {
		setModelClass(DDMTemplateVersion.class);

		setModelImplClass(DDMTemplateVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDMTemplateVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddm template version with the primary key. Does not add the ddm template version to the database.
	 *
	 * @param templateVersionId the primary key for the new ddm template version
	 * @return the new ddm template version
	 */
	@Override
	public DDMTemplateVersion create(long templateVersionId) {
		DDMTemplateVersion ddmTemplateVersion = new DDMTemplateVersionImpl();

		ddmTemplateVersion.setNew(true);
		ddmTemplateVersion.setPrimaryKey(templateVersionId);

		ddmTemplateVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmTemplateVersion;
	}

	/**
	 * Removes the ddm template version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateVersionId the primary key of the ddm template version
	 * @return the ddm template version that was removed
	 * @throws NoSuchTemplateVersionException if a ddm template version with the primary key could not be found
	 */
	@Override
	public DDMTemplateVersion remove(long templateVersionId)
		throws NoSuchTemplateVersionException {

		return remove((Serializable)templateVersionId);
	}

	@Override
	protected DDMTemplateVersion removeImpl(
		DDMTemplateVersion ddmTemplateVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmTemplateVersion)) {
				ddmTemplateVersion = (DDMTemplateVersion)session.get(
					DDMTemplateVersionImpl.class,
					ddmTemplateVersion.getPrimaryKeyObj());
			}

			if ((ddmTemplateVersion != null) &&
				ctPersistenceHelper.isRemove(ddmTemplateVersion)) {

				session.delete(ddmTemplateVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTemplateVersion != null) {
			clearCache(ddmTemplateVersion);
		}

		return ddmTemplateVersion;
	}

	@Override
	public DDMTemplateVersion updateImpl(
		DDMTemplateVersion ddmTemplateVersion) {

		boolean isNew = ddmTemplateVersion.isNew();

		if (!(ddmTemplateVersion instanceof DDMTemplateVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmTemplateVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmTemplateVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmTemplateVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMTemplateVersion implementation " +
					ddmTemplateVersion.getClass());
		}

		DDMTemplateVersionModelImpl ddmTemplateVersionModelImpl =
			(DDMTemplateVersionModelImpl)ddmTemplateVersion;

		if (isNew && (ddmTemplateVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddmTemplateVersion.setCreateDate(date);
			}
			else {
				ddmTemplateVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmTemplateVersion)) {
				if (!isNew) {
					session.evict(
						DDMTemplateVersionImpl.class,
						ddmTemplateVersion.getPrimaryKeyObj());
				}

				session.save(ddmTemplateVersion);
			}
			else {
				ddmTemplateVersion = (DDMTemplateVersion)session.merge(
					ddmTemplateVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmTemplateVersion, false);

		if (isNew) {
			ddmTemplateVersion.setNew(false);
		}

		ddmTemplateVersion.resetOriginalValues();

		return ddmTemplateVersion;
	}

	/**
	 * Returns the ddm template version with the primary key or throws a <code>NoSuchTemplateVersionException</code> if it could not be found.
	 *
	 * @param templateVersionId the primary key of the ddm template version
	 * @return the ddm template version
	 * @throws NoSuchTemplateVersionException if a ddm template version with the primary key could not be found
	 */
	@Override
	public DDMTemplateVersion findByPrimaryKey(long templateVersionId)
		throws NoSuchTemplateVersionException {

		return findByPrimaryKey((Serializable)templateVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm template version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateVersionId the primary key of the ddm template version
	 * @return the ddm template version, or <code>null</code> if a ddm template version with the primary key could not be found
	 */
	@Override
	public DDMTemplateVersion fetchByPrimaryKey(long templateVersionId) {
		return fetchByPrimaryKey((Serializable)templateVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "templateVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMTEMPLATEVERSION;
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
		return DDMTemplateVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMTemplateVersion";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("templateId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("language");
		ctMergeColumnNames.add("script");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("templateVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"templateId", "version"});
	}

	/**
	 * Initializes the ddm template version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByTemplateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTemplateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"templateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByTemplateId", new String[] {Long.class.getName()},
					new String[] {"templateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByTemplateId", new String[] {Long.class.getName()},
					new String[] {"templateId"}, false),
				_SQL_SELECT_DDMTEMPLATEVERSION_WHERE,
				_SQL_COUNT_DDMTEMPLATEVERSION_WHERE,
				DDMTemplateVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ddmTemplateVersion.", "templateId", FinderColumn.Type.LONG,
					"=", true, true, DDMTemplateVersion::getTemplateId));

		_uniquePersistenceFinderByT_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByT_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"templateId", "version"}, 0, 2, false,
				DDMTemplateVersion::getTemplateId,
				convertNullFunction(DDMTemplateVersion::getVersion)),
			_SQL_SELECT_DDMTEMPLATEVERSION_WHERE, "",
			new FinderColumn<>(
				"ddmTemplateVersion.", "templateId", FinderColumn.Type.LONG,
				"=", true, true, DDMTemplateVersion::getTemplateId),
			new FinderColumn<>(
				"ddmTemplateVersion.", "version", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplateVersion::getVersion));

		_collectionPersistenceFinderByT_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"templateId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"templateId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"templateId", "status"}, false),
			_SQL_SELECT_DDMTEMPLATEVERSION_WHERE,
			_SQL_COUNT_DDMTEMPLATEVERSION_WHERE,
			DDMTemplateVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"ddmTemplateVersion.", "templateId", FinderColumn.Type.LONG,
				"=", true, true, DDMTemplateVersion::getTemplateId),
			new FinderColumn<>(
				"ddmTemplateVersion.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, DDMTemplateVersion::getStatus));

		DDMTemplateVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMTemplateVersionUtil.setPersistence(null);

		entityCache.removeCache(DDMTemplateVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMTemplateVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMTEMPLATEVERSION =
		"SELECT ddmTemplateVersion FROM DDMTemplateVersion ddmTemplateVersion";

	private static final String _SQL_SELECT_DDMTEMPLATEVERSION_WHERE =
		"SELECT ddmTemplateVersion FROM DDMTemplateVersion ddmTemplateVersion WHERE ";

	private static final String _SQL_COUNT_DDMTEMPLATEVERSION_WHERE =
		"SELECT COUNT(ddmTemplateVersion) FROM DDMTemplateVersion ddmTemplateVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-539251751
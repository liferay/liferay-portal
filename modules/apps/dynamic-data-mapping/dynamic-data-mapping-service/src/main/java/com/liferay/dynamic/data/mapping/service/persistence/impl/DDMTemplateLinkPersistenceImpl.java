/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateLinkException;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateLinkModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateLinkUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm template link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMTemplateLinkPersistence.class)
public class DDMTemplateLinkPersistenceImpl
	extends BasePersistenceImpl<DDMTemplateLink, NoSuchTemplateLinkException>
	implements DDMTemplateLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMTemplateLinkUtil</code> to access the ddm template link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMTemplateLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMTemplateLink, NoSuchTemplateLinkException>
			_collectionPersistenceFinderByTemplateId;

	/**
	 * Returns an ordered range of all the ddm template links where templateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateLinkModelImpl</code>.
	 * </p>
	 *
	 * @param templateId the template ID
	 * @param start the lower bound of the range of ddm template links
	 * @param end the upper bound of the range of ddm template links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm template links
	 */
	@Override
	public List<DDMTemplateLink> findByTemplateId(
		long templateId, int start, int end,
		OrderByComparator<DDMTemplateLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTemplateId.find(
			finderCache, new Object[] {templateId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template link in the ordered set where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template link
	 * @throws NoSuchTemplateLinkException if a matching ddm template link could not be found
	 */
	@Override
	public DDMTemplateLink findByTemplateId_First(
			long templateId,
			OrderByComparator<DDMTemplateLink> orderByComparator)
		throws NoSuchTemplateLinkException {

		return _collectionPersistenceFinderByTemplateId.findFirst(
			finderCache, new Object[] {templateId}, orderByComparator);
	}

	/**
	 * Returns the first ddm template link in the ordered set where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template link, or <code>null</code> if a matching ddm template link could not be found
	 */
	@Override
	public DDMTemplateLink fetchByTemplateId_First(
		long templateId, OrderByComparator<DDMTemplateLink> orderByComparator) {

		return _collectionPersistenceFinderByTemplateId.fetchFirst(
			finderCache, new Object[] {templateId}, orderByComparator);
	}

	/**
	 * Removes all the ddm template links where templateId = &#63; from the database.
	 *
	 * @param templateId the template ID
	 */
	@Override
	public void removeByTemplateId(long templateId) {
		_collectionPersistenceFinderByTemplateId.remove(
			finderCache, new Object[] {templateId});
	}

	/**
	 * Returns the number of ddm template links where templateId = &#63;.
	 *
	 * @param templateId the template ID
	 * @return the number of matching ddm template links
	 */
	@Override
	public int countByTemplateId(long templateId) {
		return _collectionPersistenceFinderByTemplateId.count(
			finderCache, new Object[] {templateId});
	}

	private UniquePersistenceFinder
		<DDMTemplateLink, NoSuchTemplateLinkException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the ddm template link where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchTemplateLinkException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching ddm template link
	 * @throws NoSuchTemplateLinkException if a matching ddm template link could not be found
	 */
	@Override
	public DDMTemplateLink findByC_C(long classNameId, long classPK)
		throws NoSuchTemplateLinkException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the ddm template link where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template link, or <code>null</code> if a matching ddm template link could not be found
	 */
	@Override
	public DDMTemplateLink fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the ddm template link where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the ddm template link that was removed
	 */
	@Override
	public DDMTemplateLink removeByC_C(long classNameId, long classPK)
		throws NoSuchTemplateLinkException {

		DDMTemplateLink ddmTemplateLink = findByC_C(classNameId, classPK);

		return remove(ddmTemplateLink);
	}

	/**
	 * Returns the number of ddm template links where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm template links
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public DDMTemplateLinkPersistenceImpl() {
		setModelClass(DDMTemplateLink.class);

		setModelImplClass(DDMTemplateLinkImpl.class);
		setModelPKClass(long.class);

		setTable(DDMTemplateLinkTable.INSTANCE);
	}

	/**
	 * Creates a new ddm template link with the primary key. Does not add the ddm template link to the database.
	 *
	 * @param templateLinkId the primary key for the new ddm template link
	 * @return the new ddm template link
	 */
	@Override
	public DDMTemplateLink create(long templateLinkId) {
		DDMTemplateLink ddmTemplateLink = new DDMTemplateLinkImpl();

		ddmTemplateLink.setNew(true);
		ddmTemplateLink.setPrimaryKey(templateLinkId);

		ddmTemplateLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmTemplateLink;
	}

	/**
	 * Removes the ddm template link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateLinkId the primary key of the ddm template link
	 * @return the ddm template link that was removed
	 * @throws NoSuchTemplateLinkException if a ddm template link with the primary key could not be found
	 */
	@Override
	public DDMTemplateLink remove(long templateLinkId)
		throws NoSuchTemplateLinkException {

		return remove((Serializable)templateLinkId);
	}

	@Override
	protected DDMTemplateLink removeImpl(DDMTemplateLink ddmTemplateLink) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmTemplateLink)) {
				ddmTemplateLink = (DDMTemplateLink)session.get(
					DDMTemplateLinkImpl.class,
					ddmTemplateLink.getPrimaryKeyObj());
			}

			if ((ddmTemplateLink != null) &&
				ctPersistenceHelper.isRemove(ddmTemplateLink)) {

				session.delete(ddmTemplateLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTemplateLink != null) {
			clearCache(ddmTemplateLink);
		}

		return ddmTemplateLink;
	}

	@Override
	public DDMTemplateLink updateImpl(DDMTemplateLink ddmTemplateLink) {
		boolean isNew = ddmTemplateLink.isNew();

		if (!(ddmTemplateLink instanceof DDMTemplateLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmTemplateLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmTemplateLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmTemplateLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMTemplateLink implementation " +
					ddmTemplateLink.getClass());
		}

		DDMTemplateLinkModelImpl ddmTemplateLinkModelImpl =
			(DDMTemplateLinkModelImpl)ddmTemplateLink;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmTemplateLink)) {
				if (!isNew) {
					session.evict(
						DDMTemplateLinkImpl.class,
						ddmTemplateLink.getPrimaryKeyObj());
				}

				session.save(ddmTemplateLink);
			}
			else {
				ddmTemplateLink = (DDMTemplateLink)session.merge(
					ddmTemplateLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmTemplateLink, false);

		if (isNew) {
			ddmTemplateLink.setNew(false);
		}

		ddmTemplateLink.resetOriginalValues();

		return ddmTemplateLink;
	}

	/**
	 * Returns the ddm template link with the primary key or throws a <code>NoSuchTemplateLinkException</code> if it could not be found.
	 *
	 * @param templateLinkId the primary key of the ddm template link
	 * @return the ddm template link
	 * @throws NoSuchTemplateLinkException if a ddm template link with the primary key could not be found
	 */
	@Override
	public DDMTemplateLink findByPrimaryKey(long templateLinkId)
		throws NoSuchTemplateLinkException {

		return findByPrimaryKey((Serializable)templateLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm template link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateLinkId the primary key of the ddm template link
	 * @return the ddm template link, or <code>null</code> if a ddm template link with the primary key could not be found
	 */
	@Override
	public DDMTemplateLink fetchByPrimaryKey(long templateLinkId) {
		return fetchByPrimaryKey((Serializable)templateLinkId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "templateLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMTEMPLATELINK;
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
		return DDMTemplateLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMTemplateLink";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("templateId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("templateLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the ddm template link persistence.
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
				_SQL_SELECT_DDMTEMPLATELINK_WHERE,
				_SQL_COUNT_DDMTEMPLATELINK_WHERE,
				DDMTemplateLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddmTemplateLink.", "templateId", FinderColumn.Type.LONG,
					"=", true, true, DDMTemplateLink::getTemplateId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				DDMTemplateLink::getClassNameId, DDMTemplateLink::getClassPK),
			_SQL_SELECT_DDMTEMPLATELINK_WHERE, "",
			new FinderColumn<>(
				"ddmTemplateLink.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMTemplateLink::getClassNameId),
			new FinderColumn<>(
				"ddmTemplateLink.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, DDMTemplateLink::getClassPK));

		DDMTemplateLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMTemplateLinkUtil.setPersistence(null);

		entityCache.removeCache(DDMTemplateLinkImpl.class.getName());
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
		DDMTemplateLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMTEMPLATELINK =
		"SELECT ddmTemplateLink FROM DDMTemplateLink ddmTemplateLink";

	private static final String _SQL_SELECT_DDMTEMPLATELINK_WHERE =
		"SELECT ddmTemplateLink FROM DDMTemplateLink ddmTemplateLink WHERE ";

	private static final String _SQL_COUNT_DDMTEMPLATELINK_WHERE =
		"SELECT COUNT(ddmTemplateLink) FROM DDMTemplateLink ddmTemplateLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMTemplateLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTemplateLinkPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2068294518
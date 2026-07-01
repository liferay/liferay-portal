/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.impl;

import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramPinException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPinTable;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramPinImpl;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramPinModelImpl;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinUtil;
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
 * The persistence implementation for the cs diagram pin service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CSDiagramPinPersistence.class)
public class CSDiagramPinPersistenceImpl
	extends BasePersistenceImpl<CSDiagramPin, NoSuchCSDiagramPinException>
	implements CSDiagramPinPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CSDiagramPinUtil</code> to access the cs diagram pin persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CSDiagramPinImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CSDiagramPin, NoSuchCSDiagramPinException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cs diagram pins where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramPinModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram pins
	 * @param end the upper bound of the range of cs diagram pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram pins
	 */
	@Override
	public List<CSDiagramPin> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramPin> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram pin in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram pin
	 * @throws NoSuchCSDiagramPinException if a matching cs diagram pin could not be found
	 */
	@Override
	public CSDiagramPin findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CSDiagramPin> orderByComparator)
		throws NoSuchCSDiagramPinException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram pin in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram pin, or <code>null</code> if a matching cs diagram pin could not be found
	 */
	@Override
	public CSDiagramPin fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CSDiagramPin> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram pins where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cs diagram pins where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram pins
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	public CSDiagramPinPersistenceImpl() {
		setModelClass(CSDiagramPin.class);

		setModelImplClass(CSDiagramPinImpl.class);
		setModelPKClass(long.class);

		setTable(CSDiagramPinTable.INSTANCE);
	}

	/**
	 * Creates a new cs diagram pin with the primary key. Does not add the cs diagram pin to the database.
	 *
	 * @param CSDiagramPinId the primary key for the new cs diagram pin
	 * @return the new cs diagram pin
	 */
	@Override
	public CSDiagramPin create(long CSDiagramPinId) {
		CSDiagramPin csDiagramPin = new CSDiagramPinImpl();

		csDiagramPin.setNew(true);
		csDiagramPin.setPrimaryKey(CSDiagramPinId);

		csDiagramPin.setCompanyId(CompanyThreadLocal.getCompanyId());

		return csDiagramPin;
	}

	/**
	 * Removes the cs diagram pin with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramPinId the primary key of the cs diagram pin
	 * @return the cs diagram pin that was removed
	 * @throws NoSuchCSDiagramPinException if a cs diagram pin with the primary key could not be found
	 */
	@Override
	public CSDiagramPin remove(long CSDiagramPinId)
		throws NoSuchCSDiagramPinException {

		return remove((Serializable)CSDiagramPinId);
	}

	@Override
	protected CSDiagramPin removeImpl(CSDiagramPin csDiagramPin) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(csDiagramPin)) {
				csDiagramPin = (CSDiagramPin)session.get(
					CSDiagramPinImpl.class, csDiagramPin.getPrimaryKeyObj());
			}

			if ((csDiagramPin != null) &&
				ctPersistenceHelper.isRemove(csDiagramPin)) {

				session.delete(csDiagramPin);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (csDiagramPin != null) {
			clearCache(csDiagramPin);
		}

		return csDiagramPin;
	}

	@Override
	public CSDiagramPin updateImpl(CSDiagramPin csDiagramPin) {
		boolean isNew = csDiagramPin.isNew();

		if (!(csDiagramPin instanceof CSDiagramPinModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(csDiagramPin.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					csDiagramPin);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in csDiagramPin proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CSDiagramPin implementation " +
					csDiagramPin.getClass());
		}

		CSDiagramPinModelImpl csDiagramPinModelImpl =
			(CSDiagramPinModelImpl)csDiagramPin;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (csDiagramPin.getCreateDate() == null)) {
			if (serviceContext == null) {
				csDiagramPin.setCreateDate(date);
			}
			else {
				csDiagramPin.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!csDiagramPinModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				csDiagramPin.setModifiedDate(date);
			}
			else {
				csDiagramPin.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(csDiagramPin)) {
				if (!isNew) {
					session.evict(
						CSDiagramPinImpl.class,
						csDiagramPin.getPrimaryKeyObj());
				}

				session.save(csDiagramPin);
			}
			else {
				csDiagramPin = (CSDiagramPin)session.merge(csDiagramPin);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(csDiagramPin, false);

		if (isNew) {
			csDiagramPin.setNew(false);
		}

		csDiagramPin.resetOriginalValues();

		return csDiagramPin;
	}

	/**
	 * Returns the cs diagram pin with the primary key or throws a <code>NoSuchCSDiagramPinException</code> if it could not be found.
	 *
	 * @param CSDiagramPinId the primary key of the cs diagram pin
	 * @return the cs diagram pin
	 * @throws NoSuchCSDiagramPinException if a cs diagram pin with the primary key could not be found
	 */
	@Override
	public CSDiagramPin findByPrimaryKey(long CSDiagramPinId)
		throws NoSuchCSDiagramPinException {

		return findByPrimaryKey((Serializable)CSDiagramPinId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cs diagram pin with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramPinId the primary key of the cs diagram pin
	 * @return the cs diagram pin, or <code>null</code> if a cs diagram pin with the primary key could not be found
	 */
	@Override
	public CSDiagramPin fetchByPrimaryKey(long CSDiagramPinId) {
		return fetchByPrimaryKey((Serializable)CSDiagramPinId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CSDiagramPinId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CSDIAGRAMPIN;
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
		return CSDiagramPinModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CSDiagramPin";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("positionX");
		ctMergeColumnNames.add("positionY");
		ctMergeColumnNames.add("sequence");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("CSDiagramPinId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the cs diagram pin persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCPDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionId", new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, false),
				_SQL_SELECT_CSDIAGRAMPIN_WHERE, _SQL_COUNT_CSDIAGRAMPIN_WHERE,
				CSDiagramPinModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"csDiagramPin.", "CPDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, CSDiagramPin::getCPDefinitionId));

		CSDiagramPinUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CSDiagramPinUtil.setPersistence(null);

		entityCache.removeCache(CSDiagramPinImpl.class.getName());
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
		CSDiagramPinModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CSDIAGRAMPIN =
		"SELECT csDiagramPin FROM CSDiagramPin csDiagramPin";

	private static final String _SQL_SELECT_CSDIAGRAMPIN_WHERE =
		"SELECT csDiagramPin FROM CSDiagramPin csDiagramPin WHERE ";

	private static final String _SQL_COUNT_CSDIAGRAMPIN_WHERE =
		"SELECT COUNT(csDiagramPin) FROM CSDiagramPin csDiagramPin WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1015049894
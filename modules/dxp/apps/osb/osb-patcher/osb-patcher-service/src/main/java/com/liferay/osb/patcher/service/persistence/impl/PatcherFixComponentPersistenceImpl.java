/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixComponentTable;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixComponentPersistence.class)
public class PatcherFixComponentPersistenceImpl
	extends BasePersistenceImpl
		<PatcherFixComponent, NoSuchPatcherFixComponentException>
	implements PatcherFixComponentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixComponentUtil</code> to access the patcher fix component persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixComponentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<PatcherFixComponent, NoSuchPatcherFixComponentException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the patcher fix component where name = &#63; or throws a <code>NoSuchPatcherFixComponentException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a matching patcher fix component could not be found
	 */
	@Override
	public PatcherFixComponent findByName(String name)
		throws NoSuchPatcherFixComponentException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the patcher fix component where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix component, or <code>null</code> if a matching patcher fix component could not be found
	 */
	@Override
	public PatcherFixComponent fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the patcher fix component where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher fix component that was removed
	 */
	@Override
	public PatcherFixComponent removeByName(String name)
		throws NoSuchPatcherFixComponentException {

		PatcherFixComponent patcherFixComponent = findByName(name);

		return remove(patcherFixComponent);
	}

	/**
	 * Returns the number of patcher fix components where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher fix components
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	public PatcherFixComponentPersistenceImpl() {
		setModelClass(PatcherFixComponent.class);

		setModelImplClass(PatcherFixComponentImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixComponentTable.INSTANCE);
	}

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	@Override
	public PatcherFixComponent create(long patcherFixComponentId) {
		PatcherFixComponent patcherFixComponent = new PatcherFixComponentImpl();

		patcherFixComponent.setNew(true);
		patcherFixComponent.setPrimaryKey(patcherFixComponentId);

		patcherFixComponent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixComponent;
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent remove(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException {

		return remove((Serializable)patcherFixComponentId);
	}

	@Override
	protected PatcherFixComponent removeImpl(
		PatcherFixComponent patcherFixComponent) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixComponent)) {
				patcherFixComponent = (PatcherFixComponent)session.get(
					PatcherFixComponentImpl.class,
					patcherFixComponent.getPrimaryKeyObj());
			}

			if (patcherFixComponent != null) {
				session.delete(patcherFixComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixComponent != null) {
			clearCache(patcherFixComponent);
		}

		return patcherFixComponent;
	}

	@Override
	public PatcherFixComponent updateImpl(
		PatcherFixComponent patcherFixComponent) {

		boolean isNew = patcherFixComponent.isNew();

		if (!(patcherFixComponent instanceof PatcherFixComponentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixComponent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixComponent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixComponent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixComponent implementation " +
					patcherFixComponent.getClass());
		}

		PatcherFixComponentModelImpl patcherFixComponentModelImpl =
			(PatcherFixComponentModelImpl)patcherFixComponent;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFixComponent.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFixComponent.setCreateDate(date);
			}
			else {
				patcherFixComponent.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixComponentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFixComponent.setModifiedDate(date);
			}
			else {
				patcherFixComponent.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixComponent);
			}
			else {
				patcherFixComponent = (PatcherFixComponent)session.merge(
					patcherFixComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherFixComponent, false);

		if (isNew) {
			patcherFixComponent.setNew(false);
		}

		patcherFixComponent.resetOriginalValues();

		return patcherFixComponent;
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a <code>NoSuchPatcherFixComponentException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent findByPrimaryKey(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException {

		return findByPrimaryKey((Serializable)patcherFixComponentId);
	}

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent fetchByPrimaryKey(long patcherFixComponentId) {
		return fetchByPrimaryKey((Serializable)patcherFixComponentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixComponentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXCOMPONENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixComponentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix component persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(PatcherFixComponent::getName)),
			_SQL_SELECT_PATCHERFIXCOMPONENT_WHERE, "",
			new FinderColumn<>(
				"patcherFixComponent.", "name", FinderColumn.Type.STRING, "=",
				true, true, PatcherFixComponent::getName));

		PatcherFixComponentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixComponentUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixComponentImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERFIXCOMPONENT =
		"SELECT patcherFixComponent FROM PatcherFixComponent patcherFixComponent";

	private static final String _SQL_SELECT_PATCHERFIXCOMPONENT_WHERE =
		"SELECT patcherFixComponent FROM PatcherFixComponent patcherFixComponent WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1115939261
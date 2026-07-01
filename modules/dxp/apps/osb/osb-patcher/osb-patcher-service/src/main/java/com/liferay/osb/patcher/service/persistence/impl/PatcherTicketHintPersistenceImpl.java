/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherTicketHintException;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.model.PatcherTicketHintTable;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * The persistence implementation for the patcher ticket hint service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherTicketHintPersistence.class)
public class PatcherTicketHintPersistenceImpl
	extends BasePersistenceImpl
		<PatcherTicketHint, NoSuchPatcherTicketHintException>
	implements PatcherTicketHintPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherTicketHintUtil</code> to access the patcher ticket hint persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherTicketHintImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<PatcherTicketHint, NoSuchPatcherTicketHintException>
			_uniquePersistenceFinderByPatcherProductVersionId;

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	 */
	@Override
	public PatcherTicketHint findByPatcherProductVersionId(
			long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException {

		return _uniquePersistenceFinderByPatcherProductVersionId.find(
			finderCache, new Object[] {patcherProductVersionId});
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 */
	@Override
	public PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByPatcherProductVersionId.fetch(
			finderCache, new Object[] {patcherProductVersionId},
			useFinderCache);
	}

	/**
	 * Removes the patcher ticket hint where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the patcher ticket hint that was removed
	 */
	@Override
	public PatcherTicketHint removeByPatcherProductVersionId(
			long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException {

		PatcherTicketHint patcherTicketHint = findByPatcherProductVersionId(
			patcherProductVersionId);

		return remove(patcherTicketHint);
	}

	/**
	 * Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher ticket hints
	 */
	@Override
	public int countByPatcherProductVersionId(long patcherProductVersionId) {
		return _uniquePersistenceFinderByPatcherProductVersionId.count(
			finderCache, new Object[] {patcherProductVersionId});
	}

	public PatcherTicketHintPersistenceImpl() {
		setModelClass(PatcherTicketHint.class);

		setModelImplClass(PatcherTicketHintImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherTicketHintTable.INSTANCE);
	}

	/**
	 * Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	 *
	 * @param patcherTicketHintId the primary key for the new patcher ticket hint
	 * @return the new patcher ticket hint
	 */
	@Override
	public PatcherTicketHint create(long patcherTicketHintId) {
		PatcherTicketHint patcherTicketHint = new PatcherTicketHintImpl();

		patcherTicketHint.setNew(true);
		patcherTicketHint.setPrimaryKey(patcherTicketHintId);

		patcherTicketHint.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherTicketHint;
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint remove(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException {

		return remove((Serializable)patcherTicketHintId);
	}

	@Override
	protected PatcherTicketHint removeImpl(
		PatcherTicketHint patcherTicketHint) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherTicketHint)) {
				patcherTicketHint = (PatcherTicketHint)session.get(
					PatcherTicketHintImpl.class,
					patcherTicketHint.getPrimaryKeyObj());
			}

			if (patcherTicketHint != null) {
				session.delete(patcherTicketHint);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherTicketHint != null) {
			clearCache(patcherTicketHint);
		}

		return patcherTicketHint;
	}

	@Override
	public PatcherTicketHint updateImpl(PatcherTicketHint patcherTicketHint) {
		boolean isNew = patcherTicketHint.isNew();

		if (!(patcherTicketHint instanceof PatcherTicketHintModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherTicketHint.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherTicketHint);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherTicketHint proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherTicketHint implementation " +
					patcherTicketHint.getClass());
		}

		PatcherTicketHintModelImpl patcherTicketHintModelImpl =
			(PatcherTicketHintModelImpl)patcherTicketHint;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherTicketHint.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherTicketHint.setCreateDate(date);
			}
			else {
				patcherTicketHint.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherTicketHintModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherTicketHint.setModifiedDate(date);
			}
			else {
				patcherTicketHint.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherTicketHint);
			}
			else {
				patcherTicketHint = (PatcherTicketHint)session.merge(
					patcherTicketHint);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(patcherTicketHint, false);

		if (isNew) {
			patcherTicketHint.setNew(false);
		}

		patcherTicketHint.resetOriginalValues();

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint findByPrimaryKey(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException {

		return findByPrimaryKey((Serializable)patcherTicketHintId);
	}

	/**
	 * Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint fetchByPrimaryKey(long patcherTicketHintId) {
		return fetchByPrimaryKey((Serializable)patcherTicketHintId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherTicketHintId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERTICKETHINT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherTicketHintModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher ticket hint persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByPatcherProductVersionId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByPatcherProductVersionId",
					new String[] {Long.class.getName()},
					new String[] {"patcherProductVersionId"}, 0, 0, false,
					PatcherTicketHint::getPatcherProductVersionId),
				_SQL_SELECT_PATCHERTICKETHINT_WHERE, "",
				new FinderColumn<>(
					"patcherTicketHint.", "patcherProductVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					PatcherTicketHint::getPatcherProductVersionId));

		PatcherTicketHintUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherTicketHintUtil.setPersistence(null);

		entityCache.removeCache(PatcherTicketHintImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERTICKETHINT =
		"SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint";

	private static final String _SQL_SELECT_PATCHERTICKETHINT_WHERE =
		"SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherTicketHint exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherTicketHintPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1097762928
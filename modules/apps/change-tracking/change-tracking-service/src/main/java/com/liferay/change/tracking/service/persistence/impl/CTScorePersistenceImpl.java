/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchScoreException;
import com.liferay.change.tracking.model.CTScore;
import com.liferay.change.tracking.model.CTScoreTable;
import com.liferay.change.tracking.model.impl.CTScoreImpl;
import com.liferay.change.tracking.model.impl.CTScoreModelImpl;
import com.liferay.change.tracking.service.persistence.CTScorePersistence;
import com.liferay.change.tracking.service.persistence.CTScoreUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ct score service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTScorePersistence.class)
public class CTScorePersistenceImpl
	extends BasePersistenceImpl<CTScore, NoSuchScoreException>
	implements CTScorePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTScoreUtil</code> to access the ct score persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTScoreImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<CTScore, NoSuchScoreException>
		_uniquePersistenceFinderByCtCollectionId;

	/**
	 * Returns the ct score where ctCollectionId = &#63; or throws a <code>NoSuchScoreException</code> if it could not be found.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the matching ct score
	 * @throws NoSuchScoreException if a matching ct score could not be found
	 */
	@Override
	public CTScore findByCtCollectionId(long ctCollectionId)
		throws NoSuchScoreException {

		return _uniquePersistenceFinderByCtCollectionId.find(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the ct score where ctCollectionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ct score, or <code>null</code> if a matching ct score could not be found
	 */
	@Override
	public CTScore fetchByCtCollectionId(
		long ctCollectionId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCtCollectionId.fetch(
			finderCache, new Object[] {ctCollectionId}, useFinderCache);
	}

	/**
	 * Removes the ct score where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the ct score that was removed
	 */
	@Override
	public CTScore removeByCtCollectionId(long ctCollectionId)
		throws NoSuchScoreException {

		CTScore ctScore = findByCtCollectionId(ctCollectionId);

		return remove(ctScore);
	}

	/**
	 * Returns the number of ct scores where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct scores
	 */
	@Override
	public int countByCtCollectionId(long ctCollectionId) {
		return _uniquePersistenceFinderByCtCollectionId.count(
			finderCache, new Object[] {ctCollectionId});
	}

	public CTScorePersistenceImpl() {
		setModelClass(CTScore.class);

		setModelImplClass(CTScoreImpl.class);
		setModelPKClass(long.class);

		setTable(CTScoreTable.INSTANCE);
	}

	/**
	 * Creates a new ct score with the primary key. Does not add the ct score to the database.
	 *
	 * @param ctScoreId the primary key for the new ct score
	 * @return the new ct score
	 */
	@Override
	public CTScore create(long ctScoreId) {
		CTScore ctScore = new CTScoreImpl();

		ctScore.setNew(true);
		ctScore.setPrimaryKey(ctScoreId);

		ctScore.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctScore;
	}

	/**
	 * Removes the ct score with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctScoreId the primary key of the ct score
	 * @return the ct score that was removed
	 * @throws NoSuchScoreException if a ct score with the primary key could not be found
	 */
	@Override
	public CTScore remove(long ctScoreId) throws NoSuchScoreException {
		return remove((Serializable)ctScoreId);
	}

	@Override
	protected CTScore removeImpl(CTScore ctScore) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctScore)) {
				ctScore = (CTScore)session.get(
					CTScoreImpl.class, ctScore.getPrimaryKeyObj());
			}

			if (ctScore != null) {
				session.delete(ctScore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctScore != null) {
			clearCache(ctScore);
		}

		return ctScore;
	}

	@Override
	public CTScore updateImpl(CTScore ctScore) {
		boolean isNew = ctScore.isNew();

		if (!(ctScore instanceof CTScoreModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctScore.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctScore);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctScore proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTScore implementation " +
					ctScore.getClass());
		}

		CTScoreModelImpl ctScoreModelImpl = (CTScoreModelImpl)ctScore;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctScore);
			}
			else {
				ctScore = (CTScore)session.merge(ctScore);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctScore, false);

		if (isNew) {
			ctScore.setNew(false);
		}

		ctScore.resetOriginalValues();

		return ctScore;
	}

	/**
	 * Returns the ct score with the primary key or throws a <code>NoSuchScoreException</code> if it could not be found.
	 *
	 * @param ctScoreId the primary key of the ct score
	 * @return the ct score
	 * @throws NoSuchScoreException if a ct score with the primary key could not be found
	 */
	@Override
	public CTScore findByPrimaryKey(long ctScoreId)
		throws NoSuchScoreException {

		return findByPrimaryKey((Serializable)ctScoreId);
	}

	/**
	 * Returns the ct score with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctScoreId the primary key of the ct score
	 * @return the ct score, or <code>null</code> if a ct score with the primary key could not be found
	 */
	@Override
	public CTScore fetchByPrimaryKey(long ctScoreId) {
		return fetchByPrimaryKey((Serializable)ctScoreId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctScoreId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSCORE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTScoreModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct score persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByCtCollectionId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, 0, 0, false,
					CTScore::getCtCollectionId),
				_SQL_SELECT_CTSCORE_WHERE, "",
				new FinderColumn<>(
					"ctScore.", "ctCollectionId", FinderColumn.Type.LONG, "=",
					true, true, CTScore::getCtCollectionId));

		CTScoreUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTScoreUtil.setPersistence(null);

		entityCache.removeCache(CTScoreImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_CTSCORE =
		"SELECT ctScore FROM CTScore ctScore";

	private static final String _SQL_SELECT_CTSCORE_WHERE =
		"SELECT ctScore FROM CTScore ctScore WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1067410312
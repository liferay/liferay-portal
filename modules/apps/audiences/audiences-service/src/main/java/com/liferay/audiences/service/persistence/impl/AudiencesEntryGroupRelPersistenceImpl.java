/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence.impl;

import com.liferay.audiences.exception.NoSuchAudiencesEntryGroupRelException;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.model.AudiencesEntryGroupRelTable;
import com.liferay.audiences.model.impl.AudiencesEntryGroupRelImpl;
import com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl;
import com.liferay.audiences.service.persistence.AudiencesEntryGroupRelPersistence;
import com.liferay.audiences.service.persistence.AudiencesEntryGroupRelUtil;
import com.liferay.audiences.service.persistence.impl.constants.AudiencesPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the audiences entry group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AudiencesEntryGroupRelPersistence.class)
public class AudiencesEntryGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<AudiencesEntryGroupRel, NoSuchAudiencesEntryGroupRelException>
	implements AudiencesEntryGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AudiencesEntryGroupRelUtil</code> to access the audiences entry group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AudiencesEntryGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AudiencesEntryGroupRel, NoSuchAudiencesEntryGroupRelException>
			_collectionPersistenceFinderByC_AEERC;

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	@Override
	public List<AudiencesEntryGroupRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_AEERC.find(
			finderCache, new Object[] {companyId, audienceEntryERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator<AudiencesEntryGroupRel> orderByComparator)
		throws NoSuchAudiencesEntryGroupRelException {

		return _collectionPersistenceFinderByC_AEERC.findFirst(
			finderCache, new Object[] {companyId, audienceEntryERC},
			orderByComparator);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByC_AEERC.fetchFirst(
			finderCache, new Object[] {companyId, audienceEntryERC},
			orderByComparator);
	}

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	@Override
	public void removeByC_AEERC(long companyId, String audienceEntryERC) {
		_collectionPersistenceFinderByC_AEERC.remove(
			finderCache, new Object[] {companyId, audienceEntryERC});
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching audiences entry group rels
	 */
	@Override
	public int countByC_AEERC(long companyId, String audienceEntryERC) {
		return _collectionPersistenceFinderByC_AEERC.count(
			finderCache, new Object[] {companyId, audienceEntryERC});
	}

	private CollectionPersistenceFinder
		<AudiencesEntryGroupRel, NoSuchAudiencesEntryGroupRelException>
			_collectionPersistenceFinderByC_GERC;

	/**
	 * Returns an ordered range of all the audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entry group rels
	 */
	@Override
	public List<AudiencesEntryGroupRel> findByC_GERC(
		long companyId, String groupERC, int start, int end,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_GERC.find(
			finderCache, new Object[] {companyId, groupERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel findByC_GERC_First(
			long companyId, String groupERC,
			OrderByComparator<AudiencesEntryGroupRel> orderByComparator)
		throws NoSuchAudiencesEntryGroupRelException {

		return _collectionPersistenceFinderByC_GERC.findFirst(
			finderCache, new Object[] {companyId, groupERC}, orderByComparator);
	}

	/**
	 * Returns the first audiences entry group rel in the ordered set where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel fetchByC_GERC_First(
		long companyId, String groupERC,
		OrderByComparator<AudiencesEntryGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByC_GERC.fetchFirst(
			finderCache, new Object[] {companyId, groupERC}, orderByComparator);
	}

	/**
	 * Removes all the audiences entry group rels where companyId = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 */
	@Override
	public void removeByC_GERC(long companyId, String groupERC) {
		_collectionPersistenceFinderByC_GERC.remove(
			finderCache, new Object[] {companyId, groupERC});
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	@Override
	public int countByC_GERC(long companyId, String groupERC) {
		return _collectionPersistenceFinderByC_GERC.count(
			finderCache, new Object[] {companyId, groupERC});
	}

	private UniquePersistenceFinder
		<AudiencesEntryGroupRel, NoSuchAudiencesEntryGroupRelException>
			_uniquePersistenceFinderByC_AEERC_GERC;

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the matching audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel findByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws NoSuchAudiencesEntryGroupRelException {

		return _uniquePersistenceFinderByC_AEERC_GERC.find(
			finderCache, new Object[] {companyId, audienceEntryERC, groupERC});
	}

	/**
	 * Returns the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching audiences entry group rel, or <code>null</code> if a matching audiences entry group rel could not be found
	 */
	@Override
	public AudiencesEntryGroupRel fetchByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_AEERC_GERC.fetch(
			finderCache, new Object[] {companyId, audienceEntryERC, groupERC},
			useFinderCache);
	}

	/**
	 * Removes the audiences entry group rel where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the audiences entry group rel that was removed
	 */
	@Override
	public AudiencesEntryGroupRel removeByC_AEERC_GERC(
			long companyId, String audienceEntryERC, String groupERC)
		throws NoSuchAudiencesEntryGroupRelException {

		AudiencesEntryGroupRel audiencesEntryGroupRel = findByC_AEERC_GERC(
			companyId, audienceEntryERC, groupERC);

		return remove(audiencesEntryGroupRel);
	}

	/**
	 * Returns the number of audiences entry group rels where companyId = &#63; and audienceEntryERC = &#63; and groupERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param groupERC the group erc
	 * @return the number of matching audiences entry group rels
	 */
	@Override
	public int countByC_AEERC_GERC(
		long companyId, String audienceEntryERC, String groupERC) {

		return _uniquePersistenceFinderByC_AEERC_GERC.count(
			finderCache, new Object[] {companyId, audienceEntryERC, groupERC});
	}

	public AudiencesEntryGroupRelPersistenceImpl() {
		setModelClass(AudiencesEntryGroupRel.class);

		setModelImplClass(AudiencesEntryGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(AudiencesEntryGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new audiences entry group rel with the primary key. Does not add the audiences entry group rel to the database.
	 *
	 * @param audiencesEntryGroupRelId the primary key for the new audiences entry group rel
	 * @return the new audiences entry group rel
	 */
	@Override
	public AudiencesEntryGroupRel create(long audiencesEntryGroupRelId) {
		AudiencesEntryGroupRel audiencesEntryGroupRel =
			new AudiencesEntryGroupRelImpl();

		audiencesEntryGroupRel.setNew(true);
		audiencesEntryGroupRel.setPrimaryKey(audiencesEntryGroupRelId);

		audiencesEntryGroupRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return audiencesEntryGroupRel;
	}

	/**
	 * Removes the audiences entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	@Override
	public AudiencesEntryGroupRel remove(long audiencesEntryGroupRelId)
		throws NoSuchAudiencesEntryGroupRelException {

		return remove((Serializable)audiencesEntryGroupRelId);
	}

	@Override
	protected AudiencesEntryGroupRel removeImpl(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(audiencesEntryGroupRel)) {
				audiencesEntryGroupRel = (AudiencesEntryGroupRel)session.get(
					AudiencesEntryGroupRelImpl.class,
					audiencesEntryGroupRel.getPrimaryKeyObj());
			}

			if (audiencesEntryGroupRel != null) {
				session.delete(audiencesEntryGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (audiencesEntryGroupRel != null) {
			clearCache(audiencesEntryGroupRel);
		}

		return audiencesEntryGroupRel;
	}

	@Override
	public AudiencesEntryGroupRel updateImpl(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		boolean isNew = audiencesEntryGroupRel.isNew();

		if (!(audiencesEntryGroupRel instanceof
				AudiencesEntryGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(audiencesEntryGroupRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					audiencesEntryGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in audiencesEntryGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AudiencesEntryGroupRel implementation " +
					audiencesEntryGroupRel.getClass());
		}

		AudiencesEntryGroupRelModelImpl audiencesEntryGroupRelModelImpl =
			(AudiencesEntryGroupRelModelImpl)audiencesEntryGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (audiencesEntryGroupRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				audiencesEntryGroupRel.setCreateDate(date);
			}
			else {
				audiencesEntryGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!audiencesEntryGroupRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				audiencesEntryGroupRel.setModifiedDate(date);
			}
			else {
				audiencesEntryGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(audiencesEntryGroupRel);
			}
			else {
				audiencesEntryGroupRel = (AudiencesEntryGroupRel)session.merge(
					audiencesEntryGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(audiencesEntryGroupRel, false);

		if (isNew) {
			audiencesEntryGroupRel.setNew(false);
		}

		audiencesEntryGroupRel.resetOriginalValues();

		return audiencesEntryGroupRel;
	}

	/**
	 * Returns the audiences entry group rel with the primary key or throws a <code>NoSuchAudiencesEntryGroupRelException</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel
	 * @throws NoSuchAudiencesEntryGroupRelException if a audiences entry group rel with the primary key could not be found
	 */
	@Override
	public AudiencesEntryGroupRel findByPrimaryKey(
			long audiencesEntryGroupRelId)
		throws NoSuchAudiencesEntryGroupRelException {

		return findByPrimaryKey((Serializable)audiencesEntryGroupRelId);
	}

	/**
	 * Returns the audiences entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel, or <code>null</code> if a audiences entry group rel with the primary key could not be found
	 */
	@Override
	public AudiencesEntryGroupRel fetchByPrimaryKey(
		long audiencesEntryGroupRelId) {

		return fetchByPrimaryKey((Serializable)audiencesEntryGroupRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "audiencesEntryGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AUDIENCESENTRYGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AudiencesEntryGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the audiences entry group rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByC_AEERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_AEERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "audienceEntryERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_AEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "audienceEntryERC"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_AEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "audienceEntryERC"}, 0, 2, false,
					null),
				_SQL_SELECT_AUDIENCESENTRYGROUPREL_WHERE,
				_SQL_COUNT_AUDIENCESENTRYGROUPREL_WHERE,
				AudiencesEntryGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"audiencesEntryGroupRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AudiencesEntryGroupRel::getCompanyId),
				new FinderColumn<>(
					"audiencesEntryGroupRel.", "audienceEntryERC",
					FinderColumn.Type.STRING, "=", true, true,
					AudiencesEntryGroupRel::getAudienceEntryERC));

		_collectionPersistenceFinderByC_GERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_GERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "groupERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_GERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "groupERC"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_GERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "groupERC"}, 0, 2, false, null),
				_SQL_SELECT_AUDIENCESENTRYGROUPREL_WHERE,
				_SQL_COUNT_AUDIENCESENTRYGROUPREL_WHERE,
				AudiencesEntryGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"audiencesEntryGroupRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AudiencesEntryGroupRel::getCompanyId),
				new FinderColumn<>(
					"audiencesEntryGroupRel.", "groupERC",
					FinderColumn.Type.STRING, "=", true, true,
					AudiencesEntryGroupRel::getGroupERC));

		_uniquePersistenceFinderByC_AEERC_GERC = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_AEERC_GERC",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "audienceEntryERC", "groupERC"}, 0,
				6, false, AudiencesEntryGroupRel::getCompanyId,
				convertNullFunction(
					AudiencesEntryGroupRel::getAudienceEntryERC),
				convertNullFunction(AudiencesEntryGroupRel::getGroupERC)),
			_SQL_SELECT_AUDIENCESENTRYGROUPREL_WHERE, "",
			new FinderColumn<>(
				"audiencesEntryGroupRel.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AudiencesEntryGroupRel::getCompanyId),
			new FinderColumn<>(
				"audiencesEntryGroupRel.", "audienceEntryERC",
				FinderColumn.Type.STRING, "=", true, true,
				AudiencesEntryGroupRel::getAudienceEntryERC),
			new FinderColumn<>(
				"audiencesEntryGroupRel.", "groupERC", FinderColumn.Type.STRING,
				"=", true, true, AudiencesEntryGroupRel::getGroupERC));

		AudiencesEntryGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AudiencesEntryGroupRelUtil.setPersistence(null);

		entityCache.removeCache(AudiencesEntryGroupRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AudiencesEntryGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_AUDIENCESENTRYGROUPREL =
		"SELECT audiencesEntryGroupRel FROM AudiencesEntryGroupRel audiencesEntryGroupRel";

	private static final String _SQL_SELECT_AUDIENCESENTRYGROUPREL_WHERE =
		"SELECT audiencesEntryGroupRel FROM AudiencesEntryGroupRel audiencesEntryGroupRel WHERE ";

	private static final String _SQL_COUNT_AUDIENCESENTRYGROUPREL_WHERE =
		"SELECT COUNT(audiencesEntryGroupRel) FROM AudiencesEntryGroupRel audiencesEntryGroupRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1092933468
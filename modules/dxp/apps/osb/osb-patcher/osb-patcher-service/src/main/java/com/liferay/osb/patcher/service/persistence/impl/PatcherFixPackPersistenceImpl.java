/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherFixPackTable;
import com.liferay.osb.patcher.model.impl.PatcherFixPackImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixPackPersistence.class)
public class PatcherFixPackPersistenceImpl
	extends BasePersistenceImpl<PatcherFixPack>
	implements PatcherFixPackPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixPackUtil</code> to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixPackImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByPFCI_PPVI_N_V;

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		if (patcherFixPack == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("patcherFixComponentId=");
			sb.append(patcherFixComponentId);

			sb.append(", patcherProjectVersionId=");
			sb.append(patcherProjectVersionId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", version=");
			sb.append(version);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherFixPackException(sb.toString());
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		return fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version,
			true);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByPFCI_PPVI_N_V, finderArgs, this);
		}

		if (result instanceof PatcherFixPack) {
			PatcherFixPack patcherFixPack = (PatcherFixPack)result;

			if ((patcherFixComponentId !=
					patcherFixPack.getPatcherFixComponentId()) ||
				(patcherProjectVersionId !=
					patcherFixPack.getPatcherProjectVersionId()) ||
				!Objects.equals(name, patcherFixPack.getName()) ||
				(version != patcherFixPack.getVersion())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2);

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2);
			}

			sb.append(_FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherFixComponentId);

				queryPos.add(patcherProjectVersionId);

				if (bindName) {
					queryPos.add(name);
				}

				queryPos.add(version);

				List<PatcherFixPack> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByPFCI_PPVI_N_V, finderArgs, list);
					}
				}
				else {
					PatcherFixPack patcherFixPack = list.get(0);

					result = patcherFixPack;

					cacheResult(patcherFixPack);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (PatcherFixPack)result;
		}
	}

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 */
	@Override
	public PatcherFixPack removeByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = findByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	@Override
	public int countByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);

		if (patcherFixPack == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2 =
			"patcherFixPack.patcherFixComponentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2 =
			"patcherFixPack.patcherProjectVersionId = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2 =
		"patcherFixPack.name = ? AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3 =
		"(patcherFixPack.name IS NULL OR patcherFixPack.name = '') AND ";

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2 =
		"patcherFixPack.version = ?";

	public PatcherFixPackPersistenceImpl() {
		setModelClass(PatcherFixPack.class);

		setModelImplClass(PatcherFixPackImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixPackTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix pack in the entity cache if it is enabled.
	 *
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void cacheResult(PatcherFixPack patcherFixPack) {
		entityCache.putResult(
			PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey(),
			patcherFixPack);

		finderCache.putResult(
			_finderPathFetchByPFCI_PPVI_N_V,
			new Object[] {
				patcherFixPack.getPatcherFixComponentId(),
				patcherFixPack.getPatcherProjectVersionId(),
				patcherFixPack.getName(), patcherFixPack.getVersion()
			},
			patcherFixPack);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fix packs in the entity cache if it is enabled.
	 *
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void cacheResult(List<PatcherFixPack> patcherFixPacks) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixPacks.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			if (entityCache.getResult(
					PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey()) ==
						null) {

				cacheResult(patcherFixPack);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix packs.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixPackImpl.class);

		finderCache.clearCache(PatcherFixPackImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix pack.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixPack patcherFixPack) {
		entityCache.removeResult(PatcherFixPackImpl.class, patcherFixPack);
	}

	@Override
	public void clearCache(List<PatcherFixPack> patcherFixPacks) {
		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			entityCache.removeResult(PatcherFixPackImpl.class, patcherFixPack);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixPackImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixPackImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherFixPackModelImpl patcherFixPackModelImpl) {

		Object[] args = new Object[] {
			patcherFixPackModelImpl.getPatcherFixComponentId(),
			patcherFixPackModelImpl.getPatcherProjectVersionId(),
			patcherFixPackModelImpl.getName(),
			patcherFixPackModelImpl.getVersion()
		};

		finderCache.putResult(
			_finderPathFetchByPFCI_PPVI_N_V, args, patcherFixPackModelImpl);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Override
	public PatcherFixPack create(long patcherFixPackId) {
		PatcherFixPack patcherFixPack = new PatcherFixPackImpl();

		patcherFixPack.setNew(true);
		patcherFixPack.setPrimaryKey(patcherFixPackId);

		patcherFixPack.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixPack;
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack remove(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return remove((Serializable)patcherFixPackId);
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack remove(Serializable primaryKey)
		throws NoSuchPatcherFixPackException {

		Session session = null;

		try {
			session = openSession();

			PatcherFixPack patcherFixPack = (PatcherFixPack)session.get(
				PatcherFixPackImpl.class, primaryKey);

			if (patcherFixPack == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixPackException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFixPack);
		}
		catch (NoSuchPatcherFixPackException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PatcherFixPack removeImpl(PatcherFixPack patcherFixPack) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			patcherFixPack.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixPack)) {
				patcherFixPack = (PatcherFixPack)session.get(
					PatcherFixPackImpl.class,
					patcherFixPack.getPrimaryKeyObj());
			}

			if (patcherFixPack != null) {
				session.delete(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixPack != null) {
			clearCache(patcherFixPack);
		}

		return patcherFixPack;
	}

	@Override
	public PatcherFixPack updateImpl(PatcherFixPack patcherFixPack) {
		boolean isNew = patcherFixPack.isNew();

		if (!(patcherFixPack instanceof PatcherFixPackModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixPack.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixPack);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixPack proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixPack implementation " +
					patcherFixPack.getClass());
		}

		PatcherFixPackModelImpl patcherFixPackModelImpl =
			(PatcherFixPackModelImpl)patcherFixPack;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFixPack.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFixPack.setCreateDate(date);
			}
			else {
				patcherFixPack.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixPackModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFixPack.setModifiedDate(date);
			}
			else {
				patcherFixPack.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixPack);
			}
			else {
				patcherFixPack = (PatcherFixPack)session.merge(patcherFixPack);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixPackImpl.class, patcherFixPackModelImpl, false, true);

		cacheUniqueFindersCache(patcherFixPackModelImpl);

		if (isNew) {
			patcherFixPack.setNew(false);
		}

		patcherFixPack.resetOriginalValues();

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixPackException {

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(primaryKey);

		if (patcherFixPack == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixPackException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws NoSuchPatcherFixPackException {

		return findByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 */
	@Override
	public PatcherFixPack fetchByPrimaryKey(long patcherFixPackId) {
		return fetchByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns all the patcher fix packs.
	 *
	 * @return the patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix packs
	 */
	@Override
	public List<PatcherFixPack> findAll(
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<PatcherFixPack> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixPack>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIXPACK);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXPACK;

				sql = sql.concat(PatcherFixPackModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFixPack>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the patcher fix packs from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFixPack patcherFixPack : findAll()) {
			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERFIXPACK);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public long[] getPatcherFixPrimaryKeys(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(long pk) {
		return getPatcherFixPatcherFixPacks(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end) {

		return getPatcherFixPatcherFixPacks(pk, start, end, null);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 */
	@Override
	public List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return patcherFixPackToPatcherFixTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixes associated with the patcher fix pack
	 */
	@Override
	public int getPatcherFixesSize(long pk) {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK) {
		return patcherFixPackToPatcherFixTableMapper.containsTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher fix pack has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsPatcherFixes(long pk) {
		if (getPatcherFixesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, long patcherFixPK) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, patcherFixPK);
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addPatcherFix(long pk, PatcherFix patcherFix) {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				patcherFix.getPrimaryKey());
		}
		else {
			return patcherFixPackToPatcherFixTableMapper.addTableMapping(
				patcherFixPack.getCompanyId(), pk, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		long[] addedKeys =
			patcherFixPackToPatcherFixTableMapper.addTableMappings(
				companyId, pk, patcherFixPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		return addPatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixes from
	 */
	@Override
	public void clearPatcherFixes(long pk) {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void removePatcherFix(long pk, PatcherFix patcherFix) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(
			pk, patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, long[] patcherFixPKs) {
		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 */
	@Override
	public void removePatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		removePatcherFixes(
			pk,
			ListUtil.toLongArray(
				patcherFixes, PatcherFix.PATCHER_FIX_ID_ACCESSOR));
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher fix pack
	 */
	@Override
	public void setPatcherFixes(long pk, long[] patcherFixPKs) {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(
			patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(
			oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		patcherFixPackToPatcherFixTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removePatcherFixPKsSet));

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		long companyId = 0;

		PatcherFixPack patcherFixPack = fetchByPrimaryKey(pk);

		if (patcherFixPack == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = patcherFixPack.getCompanyId();
		}

		patcherFixPackToPatcherFixTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newPatcherFixPKsSet));
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes to be associated with the patcher fix pack
	 */
	@Override
	public void setPatcherFixes(long pk, List<PatcherFix> patcherFixes) {
		try {
			long[] patcherFixPKs = new long[patcherFixes.size()];

			for (int i = 0; i < patcherFixes.size(); i++) {
				PatcherFix patcherFix = patcherFixes.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixes(pk, patcherFixPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherFixPackId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXPACK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixPackModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix pack persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		patcherFixPackToPatcherFixTableMapper =
			TableMapperFactory.getTableMapper(
				"OSBPatcher_PFixes_PFixPacks#patcherFixPackId",
				"OSBPatcher_PFixes_PFixPacks", "companyId", "patcherFixPackId",
				"patcherFixId", this, PatcherFix.class);

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByPFCI_PPVI_N_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPFCI_PPVI_N_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			new String[] {
				"patcherFixComponentId", "patcherProjectVersionId", "name",
				"version"
			},
			true);

		PatcherFixPackUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixPackUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixPackImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"OSBPatcher_PFixes_PFixPacks#patcherFixPackId");
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

	protected TableMapper<PatcherFixPack, PatcherFix>
		patcherFixPackToPatcherFixTableMapper;

	private static final String _SQL_SELECT_PATCHERFIXPACK =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack";

	private static final String _SQL_SELECT_PATCHERFIXPACK_WHERE =
		"SELECT patcherFixPack FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _SQL_COUNT_PATCHERFIXPACK =
		"SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack";

	private static final String _SQL_COUNT_PATCHERFIXPACK_WHERE =
		"SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixPack.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFixPack exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherFixPack exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixPackPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
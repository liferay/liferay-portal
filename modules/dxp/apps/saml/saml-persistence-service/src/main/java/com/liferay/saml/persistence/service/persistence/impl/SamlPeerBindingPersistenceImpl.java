/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.impl;

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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.saml.persistence.exception.NoSuchPeerBindingException;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.model.SamlPeerBindingTable;
import com.liferay.saml.persistence.model.impl.SamlPeerBindingImpl;
import com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingPersistence;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingUtil;
import com.liferay.saml.persistence.service.persistence.impl.constants.SamlPersistenceConstants;

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
 * The persistence implementation for the saml peer binding service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Mika Koivisto
 * @generated
 */
@Component(service = SamlPeerBindingPersistence.class)
public class SamlPeerBindingPersistenceImpl
	extends BasePersistenceImpl<SamlPeerBinding, NoSuchPeerBindingException>
	implements SamlPeerBindingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SamlPeerBindingUtil</code> to access the saml peer binding persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SamlPeerBindingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SamlPeerBinding, NoSuchPeerBindingException>
			_collectionPersistenceFinderByC_D_SNIV;

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue, int start,
		int end, OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_D_SNIV.find(
			finderCache, new Object[] {companyId, deleted, samlNameIdValue},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_D_SNIV_First(
			long companyId, boolean deleted, String samlNameIdValue,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		return _collectionPersistenceFinderByC_D_SNIV.findFirst(
			finderCache, new Object[] {companyId, deleted, samlNameIdValue},
			orderByComparator);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_D_SNIV_First(
		long companyId, boolean deleted, String samlNameIdValue,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return _collectionPersistenceFinderByC_D_SNIV.fetchFirst(
			finderCache, new Object[] {companyId, deleted, samlNameIdValue},
			orderByComparator);
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 */
	@Override
	public void removeByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		_collectionPersistenceFinderByC_D_SNIV.remove(
			finderCache, new Object[] {companyId, deleted, samlNameIdValue});
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and deleted = &#63; and samlNameIdValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param deleted the deleted
	 * @param samlNameIdValue the saml name ID value
	 * @return the number of matching saml peer bindings
	 */
	@Override
	public int countByC_D_SNIV(
		long companyId, boolean deleted, String samlNameIdValue) {

		return _collectionPersistenceFinderByC_D_SNIV.count(
			finderCache, new Object[] {companyId, deleted, samlNameIdValue});
	}

	private CollectionPersistenceFinder
		<SamlPeerBinding, NoSuchPeerBindingException>
			_collectionPersistenceFinderByC_U_SPEI_D;

	/**
	 * Returns an ordered range of all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching saml peer bindings
	 */
	@Override
	public List<SamlPeerBinding> findByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		int start, int end,
		OrderByComparator<SamlPeerBinding> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U_SPEI_D.find(
			finderCache,
			new Object[] {companyId, userId, samlPeerEntityId, deleted}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding
	 * @throws NoSuchPeerBindingException if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding findByC_U_SPEI_D_First(
			long companyId, long userId, String samlPeerEntityId,
			boolean deleted,
			OrderByComparator<SamlPeerBinding> orderByComparator)
		throws NoSuchPeerBindingException {

		return _collectionPersistenceFinderByC_U_SPEI_D.findFirst(
			finderCache,
			new Object[] {companyId, userId, samlPeerEntityId, deleted},
			orderByComparator);
	}

	/**
	 * Returns the first saml peer binding in the ordered set where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching saml peer binding, or <code>null</code> if a matching saml peer binding could not be found
	 */
	@Override
	public SamlPeerBinding fetchByC_U_SPEI_D_First(
		long companyId, long userId, String samlPeerEntityId, boolean deleted,
		OrderByComparator<SamlPeerBinding> orderByComparator) {

		return _collectionPersistenceFinderByC_U_SPEI_D.fetchFirst(
			finderCache,
			new Object[] {companyId, userId, samlPeerEntityId, deleted},
			orderByComparator);
	}

	/**
	 * Removes all the saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 */
	@Override
	public void removeByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		_collectionPersistenceFinderByC_U_SPEI_D.remove(
			finderCache,
			new Object[] {companyId, userId, samlPeerEntityId, deleted});
	}

	/**
	 * Returns the number of saml peer bindings where companyId = &#63; and userId = &#63; and samlPeerEntityId = &#63; and deleted = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param samlPeerEntityId the saml peer entity ID
	 * @param deleted the deleted
	 * @return the number of matching saml peer bindings
	 */
	@Override
	public int countByC_U_SPEI_D(
		long companyId, long userId, String samlPeerEntityId, boolean deleted) {

		return _collectionPersistenceFinderByC_U_SPEI_D.count(
			finderCache,
			new Object[] {companyId, userId, samlPeerEntityId, deleted});
	}

	public SamlPeerBindingPersistenceImpl() {
		setModelClass(SamlPeerBinding.class);

		setModelImplClass(SamlPeerBindingImpl.class);
		setModelPKClass(long.class);

		setTable(SamlPeerBindingTable.INSTANCE);
	}

	/**
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	@Override
	public SamlPeerBinding create(long samlPeerBindingId) {
		SamlPeerBinding samlPeerBinding = new SamlPeerBindingImpl();

		samlPeerBinding.setNew(true);
		samlPeerBinding.setPrimaryKey(samlPeerBindingId);

		samlPeerBinding.setCompanyId(CompanyThreadLocal.getCompanyId());

		return samlPeerBinding;
	}

	/**
	 * Removes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding remove(long samlPeerBindingId)
		throws NoSuchPeerBindingException {

		return remove((Serializable)samlPeerBindingId);
	}

	@Override
	protected SamlPeerBinding removeImpl(SamlPeerBinding samlPeerBinding) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(samlPeerBinding)) {
				samlPeerBinding = (SamlPeerBinding)session.get(
					SamlPeerBindingImpl.class,
					samlPeerBinding.getPrimaryKeyObj());
			}

			if (samlPeerBinding != null) {
				session.delete(samlPeerBinding);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (samlPeerBinding != null) {
			clearCache(samlPeerBinding);
		}

		return samlPeerBinding;
	}

	@Override
	public SamlPeerBinding updateImpl(SamlPeerBinding samlPeerBinding) {
		boolean isNew = samlPeerBinding.isNew();

		if (!(samlPeerBinding instanceof SamlPeerBindingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(samlPeerBinding.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					samlPeerBinding);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in samlPeerBinding proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SamlPeerBinding implementation " +
					samlPeerBinding.getClass());
		}

		SamlPeerBindingModelImpl samlPeerBindingModelImpl =
			(SamlPeerBindingModelImpl)samlPeerBinding;

		if (isNew && (samlPeerBinding.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				samlPeerBinding.setCreateDate(date);
			}
			else {
				samlPeerBinding.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(samlPeerBinding);
			}
			else {
				samlPeerBinding = (SamlPeerBinding)session.merge(
					samlPeerBinding);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(samlPeerBinding, false);

		if (isNew) {
			samlPeerBinding.setNew(false);
		}

		samlPeerBinding.resetOriginalValues();

		return samlPeerBinding;
	}

	/**
	 * Returns the saml peer binding with the primary key or throws a <code>NoSuchPeerBindingException</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws NoSuchPeerBindingException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding findByPrimaryKey(long samlPeerBindingId)
		throws NoSuchPeerBindingException {

		return findByPrimaryKey((Serializable)samlPeerBindingId);
	}

	/**
	 * Returns the saml peer binding with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding, or <code>null</code> if a saml peer binding with the primary key could not be found
	 */
	@Override
	public SamlPeerBinding fetchByPrimaryKey(long samlPeerBindingId) {
		return fetchByPrimaryKey((Serializable)samlPeerBindingId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "samlPeerBindingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SAMLPEERBINDING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SamlPeerBindingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the saml peer binding persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByC_D_SNIV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_D_SNIV",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "deleted", "samlNameIdValue"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_D_SNIV",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "deleted", "samlNameIdValue"}, 0,
					4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_D_SNIV",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "deleted", "samlNameIdValue"}, 0,
					4, false, null),
				_SQL_SELECT_SAMLPEERBINDING_WHERE,
				_SQL_COUNT_SAMLPEERBINDING_WHERE,
				SamlPeerBindingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"samlPeerBinding.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SamlPeerBinding::getCompanyId),
				new FinderColumn<>(
					"samlPeerBinding.", "deleted", FinderColumn.Type.BOOLEAN,
					"=", true, true, SamlPeerBinding::isDeleted),
				new FinderColumn<>(
					"samlPeerBinding.", "samlNameIdValue",
					FinderColumn.Type.STRING, "=", true, true,
					SamlPeerBinding::getSamlNameIdValue));

		_collectionPersistenceFinderByC_U_SPEI_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_SPEI_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "userId", "samlPeerEntityId", "deleted"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_U_SPEI_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "userId", "samlPeerEntityId", "deleted"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_U_SPEI_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "userId", "samlPeerEntityId", "deleted"
					},
					0, 4, false, null),
				_SQL_SELECT_SAMLPEERBINDING_WHERE,
				_SQL_COUNT_SAMLPEERBINDING_WHERE,
				SamlPeerBindingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"samlPeerBinding.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, SamlPeerBinding::getCompanyId),
				new FinderColumn<>(
					"samlPeerBinding.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SamlPeerBinding::getUserId),
				new FinderColumn<>(
					"samlPeerBinding.", "samlPeerEntityId",
					FinderColumn.Type.STRING, "=", true, true,
					SamlPeerBinding::getSamlPeerEntityId),
				new FinderColumn<>(
					"samlPeerBinding.", "deleted", FinderColumn.Type.BOOLEAN,
					"=", true, true, SamlPeerBinding::isDeleted));

		SamlPeerBindingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SamlPeerBindingUtil.setPersistence(null);

		entityCache.removeCache(SamlPeerBindingImpl.class.getName());
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SamlPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SamlPeerBindingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SAMLPEERBINDING =
		"SELECT samlPeerBinding FROM SamlPeerBinding samlPeerBinding";

	private static final String _SQL_SELECT_SAMLPEERBINDING_WHERE =
		"SELECT samlPeerBinding FROM SamlPeerBinding samlPeerBinding WHERE ";

	private static final String _SQL_COUNT_SAMLPEERBINDING_WHERE =
		"SELECT COUNT(samlPeerBinding) FROM SamlPeerBinding samlPeerBinding WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SamlPeerBinding exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1646543176
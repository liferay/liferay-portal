/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.impl;

import com.liferay.commerce.term.exception.NoSuchCTermEntryLocalizationException;
import com.liferay.commerce.term.model.CTermEntryLocalization;
import com.liferay.commerce.term.model.CTermEntryLocalizationTable;
import com.liferay.commerce.term.model.impl.CTermEntryLocalizationImpl;
import com.liferay.commerce.term.model.impl.CTermEntryLocalizationModelImpl;
import com.liferay.commerce.term.service.persistence.CTermEntryLocalizationPersistence;
import com.liferay.commerce.term.service.persistence.CTermEntryLocalizationUtil;
import com.liferay.commerce.term.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the c term entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CTermEntryLocalizationPersistence.class)
public class CTermEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<CTermEntryLocalization, NoSuchCTermEntryLocalizationException>
	implements CTermEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTermEntryLocalizationUtil</code> to access the c term entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTermEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CTermEntryLocalization, NoSuchCTermEntryLocalizationException>
			_collectionPersistenceFinderByCommerceTermEntryId;

	/**
	 * Returns an ordered range of all the c term entry localizations where commerceTermEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTermEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param start the lower bound of the range of c term entry localizations
	 * @param end the upper bound of the range of c term entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching c term entry localizations
	 */
	@Override
	public List<CTermEntryLocalization> findByCommerceTermEntryId(
		long commerceTermEntryId, int start, int end,
		OrderByComparator<CTermEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceTermEntryId.find(
			finderCache, new Object[] {commerceTermEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first c term entry localization in the ordered set where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c term entry localization
	 * @throws NoSuchCTermEntryLocalizationException if a matching c term entry localization could not be found
	 */
	@Override
	public CTermEntryLocalization findByCommerceTermEntryId_First(
			long commerceTermEntryId,
			OrderByComparator<CTermEntryLocalization> orderByComparator)
		throws NoSuchCTermEntryLocalizationException {

		return _collectionPersistenceFinderByCommerceTermEntryId.findFirst(
			finderCache, new Object[] {commerceTermEntryId}, orderByComparator);
	}

	/**
	 * Returns the first c term entry localization in the ordered set where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching c term entry localization, or <code>null</code> if a matching c term entry localization could not be found
	 */
	@Override
	public CTermEntryLocalization fetchByCommerceTermEntryId_First(
		long commerceTermEntryId,
		OrderByComparator<CTermEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByCommerceTermEntryId.fetchFirst(
			finderCache, new Object[] {commerceTermEntryId}, orderByComparator);
	}

	/**
	 * Removes all the c term entry localizations where commerceTermEntryId = &#63; from the database.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 */
	@Override
	public void removeByCommerceTermEntryId(long commerceTermEntryId) {
		_collectionPersistenceFinderByCommerceTermEntryId.remove(
			finderCache, new Object[] {commerceTermEntryId});
	}

	/**
	 * Returns the number of c term entry localizations where commerceTermEntryId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @return the number of matching c term entry localizations
	 */
	@Override
	public int countByCommerceTermEntryId(long commerceTermEntryId) {
		return _collectionPersistenceFinderByCommerceTermEntryId.count(
			finderCache, new Object[] {commerceTermEntryId});
	}

	private UniquePersistenceFinder
		<CTermEntryLocalization, NoSuchCTermEntryLocalizationException>
			_uniquePersistenceFinderByCommerceTermEntryId_LanguageId;

	/**
	 * Returns the c term entry localization where commerceTermEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchCTermEntryLocalizationException</code> if it could not be found.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param languageId the language ID
	 * @return the matching c term entry localization
	 * @throws NoSuchCTermEntryLocalizationException if a matching c term entry localization could not be found
	 */
	@Override
	public CTermEntryLocalization findByCommerceTermEntryId_LanguageId(
			long commerceTermEntryId, String languageId)
		throws NoSuchCTermEntryLocalizationException {

		return _uniquePersistenceFinderByCommerceTermEntryId_LanguageId.find(
			finderCache, new Object[] {commerceTermEntryId, languageId});
	}

	/**
	 * Returns the c term entry localization where commerceTermEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching c term entry localization, or <code>null</code> if a matching c term entry localization could not be found
	 */
	@Override
	public CTermEntryLocalization fetchByCommerceTermEntryId_LanguageId(
		long commerceTermEntryId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCommerceTermEntryId_LanguageId.fetch(
			finderCache, new Object[] {commerceTermEntryId, languageId},
			useFinderCache);
	}

	/**
	 * Removes the c term entry localization where commerceTermEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param languageId the language ID
	 * @return the c term entry localization that was removed
	 */
	@Override
	public CTermEntryLocalization removeByCommerceTermEntryId_LanguageId(
			long commerceTermEntryId, String languageId)
		throws NoSuchCTermEntryLocalizationException {

		CTermEntryLocalization cTermEntryLocalization =
			findByCommerceTermEntryId_LanguageId(
				commerceTermEntryId, languageId);

		return remove(cTermEntryLocalization);
	}

	/**
	 * Returns the number of c term entry localizations where commerceTermEntryId = &#63; and languageId = &#63;.
	 *
	 * @param commerceTermEntryId the commerce term entry ID
	 * @param languageId the language ID
	 * @return the number of matching c term entry localizations
	 */
	@Override
	public int countByCommerceTermEntryId_LanguageId(
		long commerceTermEntryId, String languageId) {

		return _uniquePersistenceFinderByCommerceTermEntryId_LanguageId.count(
			finderCache, new Object[] {commerceTermEntryId, languageId});
	}

	public CTermEntryLocalizationPersistenceImpl() {
		setModelClass(CTermEntryLocalization.class);

		setModelImplClass(CTermEntryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(CTermEntryLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new c term entry localization with the primary key. Does not add the c term entry localization to the database.
	 *
	 * @param cTermEntryLocalizationId the primary key for the new c term entry localization
	 * @return the new c term entry localization
	 */
	@Override
	public CTermEntryLocalization create(long cTermEntryLocalizationId) {
		CTermEntryLocalization cTermEntryLocalization =
			new CTermEntryLocalizationImpl();

		cTermEntryLocalization.setNew(true);
		cTermEntryLocalization.setPrimaryKey(cTermEntryLocalizationId);

		cTermEntryLocalization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cTermEntryLocalization;
	}

	/**
	 * Removes the c term entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cTermEntryLocalizationId the primary key of the c term entry localization
	 * @return the c term entry localization that was removed
	 * @throws NoSuchCTermEntryLocalizationException if a c term entry localization with the primary key could not be found
	 */
	@Override
	public CTermEntryLocalization remove(long cTermEntryLocalizationId)
		throws NoSuchCTermEntryLocalizationException {

		return remove((Serializable)cTermEntryLocalizationId);
	}

	@Override
	protected CTermEntryLocalization removeImpl(
		CTermEntryLocalization cTermEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cTermEntryLocalization)) {
				cTermEntryLocalization = (CTermEntryLocalization)session.get(
					CTermEntryLocalizationImpl.class,
					cTermEntryLocalization.getPrimaryKeyObj());
			}

			if (cTermEntryLocalization != null) {
				session.delete(cTermEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cTermEntryLocalization != null) {
			clearCache(cTermEntryLocalization);
		}

		return cTermEntryLocalization;
	}

	@Override
	public CTermEntryLocalization updateImpl(
		CTermEntryLocalization cTermEntryLocalization) {

		boolean isNew = cTermEntryLocalization.isNew();

		if (!(cTermEntryLocalization instanceof
				CTermEntryLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cTermEntryLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cTermEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cTermEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTermEntryLocalization implementation " +
					cTermEntryLocalization.getClass());
		}

		CTermEntryLocalizationModelImpl cTermEntryLocalizationModelImpl =
			(CTermEntryLocalizationModelImpl)cTermEntryLocalization;

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = cTermEntryLocalization.getCompanyId();

			long groupId = 0;

			long cTermEntryLocalizationId = 0;

			if (!isNew) {
				cTermEntryLocalizationId =
					cTermEntryLocalization.getPrimaryKey();
			}

			try {
				cTermEntryLocalization.setDescription(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CTermEntryLocalization.class.getName(),
						cTermEntryLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL,
						cTermEntryLocalization.getDescription(), null));

				cTermEntryLocalization.setLabel(
					SanitizerUtil.sanitize(
						companyId, groupId, userId,
						CTermEntryLocalization.class.getName(),
						cTermEntryLocalizationId, ContentTypes.TEXT_HTML,
						Sanitizer.MODE_ALL, cTermEntryLocalization.getLabel(),
						null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cTermEntryLocalization);
			}
			else {
				cTermEntryLocalization = (CTermEntryLocalization)session.merge(
					cTermEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cTermEntryLocalization, false);

		if (isNew) {
			cTermEntryLocalization.setNew(false);
		}

		cTermEntryLocalization.resetOriginalValues();

		return cTermEntryLocalization;
	}

	/**
	 * Returns the c term entry localization with the primary key or throws a <code>NoSuchCTermEntryLocalizationException</code> if it could not be found.
	 *
	 * @param cTermEntryLocalizationId the primary key of the c term entry localization
	 * @return the c term entry localization
	 * @throws NoSuchCTermEntryLocalizationException if a c term entry localization with the primary key could not be found
	 */
	@Override
	public CTermEntryLocalization findByPrimaryKey(
			long cTermEntryLocalizationId)
		throws NoSuchCTermEntryLocalizationException {

		return findByPrimaryKey((Serializable)cTermEntryLocalizationId);
	}

	/**
	 * Returns the c term entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cTermEntryLocalizationId the primary key of the c term entry localization
	 * @return the c term entry localization, or <code>null</code> if a c term entry localization with the primary key could not be found
	 */
	@Override
	public CTermEntryLocalization fetchByPrimaryKey(
		long cTermEntryLocalizationId) {

		return fetchByPrimaryKey((Serializable)cTermEntryLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cTermEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTERMENTRYLOCALIZATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTermEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the c term entry localization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceTermEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceTermEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceTermEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceTermEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTermEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceTermEntryId",
					new String[] {Long.class.getName()},
					new String[] {"commerceTermEntryId"}, false),
				_SQL_SELECT_CTERMENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_CTERMENTRYLOCALIZATION_WHERE,
				CTermEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cTermEntryLocalization.", "commerceTermEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CTermEntryLocalization::getCommerceTermEntryId));

		_uniquePersistenceFinderByCommerceTermEntryId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY,
					"fetchByCommerceTermEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"commerceTermEntryId", "languageId"}, 0, 2,
					false, CTermEntryLocalization::getCommerceTermEntryId,
					convertNullFunction(CTermEntryLocalization::getLanguageId)),
				_SQL_SELECT_CTERMENTRYLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"cTermEntryLocalization.", "commerceTermEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CTermEntryLocalization::getCommerceTermEntryId),
				new FinderColumn<>(
					"cTermEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					CTermEntryLocalization::getLanguageId));

		CTermEntryLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTermEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(CTermEntryLocalizationImpl.class.getName());
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
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CTermEntryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTERMENTRYLOCALIZATION =
		"SELECT cTermEntryLocalization FROM CTermEntryLocalization cTermEntryLocalization";

	private static final String _SQL_SELECT_CTERMENTRYLOCALIZATION_WHERE =
		"SELECT cTermEntryLocalization FROM CTermEntryLocalization cTermEntryLocalization WHERE ";

	private static final String _SQL_COUNT_CTERMENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(cTermEntryLocalization) FROM CTermEntryLocalization cTermEntryLocalization WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTermEntryLocalization exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CTermEntryLocalizationPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1798370656
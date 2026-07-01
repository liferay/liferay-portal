/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchCollectionTemplateException;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.model.CTCollectionTemplateTable;
import com.liferay.change.tracking.model.impl.CTCollectionTemplateImpl;
import com.liferay.change.tracking.model.impl.CTCollectionTemplateModelImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplatePersistence;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplateUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
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
 * The persistence implementation for the ct collection template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTCollectionTemplatePersistence.class)
public class CTCollectionTemplatePersistenceImpl
	extends BasePersistenceImpl
		<CTCollectionTemplate, NoSuchCollectionTemplateException>
	implements CTCollectionTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTCollectionTemplateUtil</code> to access the ct collection template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTCollectionTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CTCollectionTemplate, NoSuchCollectionTemplateException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the ct collection templates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct collection templates
	 */
	@Override
	public List<CTCollectionTemplate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection template
	 * @throws NoSuchCollectionTemplateException if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate findByCompanyId_First(
			long companyId,
			OrderByComparator<CTCollectionTemplate> orderByComparator)
		throws NoSuchCollectionTemplateException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first ct collection template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct collection template, or <code>null</code> if a matching ct collection template could not be found
	 */
	@Override
	public CTCollectionTemplate fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ct collection templates that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTCollectionTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct collection templates
	 * @param end the upper bound of the range of ct collection templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct collection templates that the user has permission to view
	 */
	@Override
	public List<CTCollectionTemplate> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTCollectionTemplate> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the ct collection templates where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct collection templates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collection templates
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct collection templates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct collection templates that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	public CTCollectionTemplatePersistenceImpl() {
		setModelClass(CTCollectionTemplate.class);

		setModelImplClass(CTCollectionTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(CTCollectionTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new ct collection template with the primary key. Does not add the ct collection template to the database.
	 *
	 * @param ctCollectionTemplateId the primary key for the new ct collection template
	 * @return the new ct collection template
	 */
	@Override
	public CTCollectionTemplate create(long ctCollectionTemplateId) {
		CTCollectionTemplate ctCollectionTemplate =
			new CTCollectionTemplateImpl();

		ctCollectionTemplate.setNew(true);
		ctCollectionTemplate.setPrimaryKey(ctCollectionTemplateId);

		ctCollectionTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctCollectionTemplate;
	}

	/**
	 * Removes the ct collection template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template that was removed
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate remove(long ctCollectionTemplateId)
		throws NoSuchCollectionTemplateException {

		return remove((Serializable)ctCollectionTemplateId);
	}

	@Override
	protected CTCollectionTemplate removeImpl(
		CTCollectionTemplate ctCollectionTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctCollectionTemplate)) {
				ctCollectionTemplate = (CTCollectionTemplate)session.get(
					CTCollectionTemplateImpl.class,
					ctCollectionTemplate.getPrimaryKeyObj());
			}

			if (ctCollectionTemplate != null) {
				session.delete(ctCollectionTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctCollectionTemplate != null) {
			clearCache(ctCollectionTemplate);
		}

		return ctCollectionTemplate;
	}

	@Override
	public CTCollectionTemplate updateImpl(
		CTCollectionTemplate ctCollectionTemplate) {

		boolean isNew = ctCollectionTemplate.isNew();

		if (!(ctCollectionTemplate instanceof CTCollectionTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctCollectionTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctCollectionTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctCollectionTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTCollectionTemplate implementation " +
					ctCollectionTemplate.getClass());
		}

		CTCollectionTemplateModelImpl ctCollectionTemplateModelImpl =
			(CTCollectionTemplateModelImpl)ctCollectionTemplate;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ctCollectionTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				ctCollectionTemplate.setCreateDate(date);
			}
			else {
				ctCollectionTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ctCollectionTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ctCollectionTemplate.setModifiedDate(date);
			}
			else {
				ctCollectionTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctCollectionTemplate);
			}
			else {
				ctCollectionTemplate = (CTCollectionTemplate)session.merge(
					ctCollectionTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctCollectionTemplate, false);

		if (isNew) {
			ctCollectionTemplate.setNew(false);
		}

		ctCollectionTemplate.resetOriginalValues();

		return ctCollectionTemplate;
	}

	/**
	 * Returns the ct collection template with the primary key or throws a <code>NoSuchCollectionTemplateException</code> if it could not be found.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template
	 * @throws NoSuchCollectionTemplateException if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate findByPrimaryKey(long ctCollectionTemplateId)
		throws NoSuchCollectionTemplateException {

		return findByPrimaryKey((Serializable)ctCollectionTemplateId);
	}

	/**
	 * Returns the ct collection template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctCollectionTemplateId the primary key of the ct collection template
	 * @return the ct collection template, or <code>null</code> if a ct collection template with the primary key could not be found
	 */
	@Override
	public CTCollectionTemplate fetchByPrimaryKey(long ctCollectionTemplateId) {
		return fetchByPrimaryKey((Serializable)ctCollectionTemplateId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctCollectionTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTCOLLECTIONTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTCollectionTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct collection template persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE,
				_SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE,
				CTCollectionTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ctCollectionTemplate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CTCollectionTemplate::getCompanyId));

		CTCollectionTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTCollectionTemplateUtil.setPersistence(null);

		entityCache.removeCache(CTCollectionTemplateImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		CTCollectionTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTCOLLECTIONTEMPLATE =
		"SELECT ctCollectionTemplate FROM CTCollectionTemplate ctCollectionTemplate";

	private static final String _SQL_SELECT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT ctCollectionTemplate FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String _SQL_COUNT_CTCOLLECTIONTEMPLATE_WHERE =
		"SELECT COUNT(ctCollectionTemplate) FROM CTCollectionTemplate ctCollectionTemplate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTCollectionTemplate exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2069454046
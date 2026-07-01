/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.service.persistence.impl;

import com.liferay.osb.faro.contacts.exception.NoSuchContactsLayoutTemplateException;
import com.liferay.osb.faro.contacts.model.ContactsLayoutTemplate;
import com.liferay.osb.faro.contacts.model.ContactsLayoutTemplateTable;
import com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateImpl;
import com.liferay.osb.faro.contacts.model.impl.ContactsLayoutTemplateModelImpl;
import com.liferay.osb.faro.contacts.service.persistence.ContactsLayoutTemplatePersistence;
import com.liferay.osb.faro.contacts.service.persistence.ContactsLayoutTemplateUtil;
import com.liferay.osb.faro.contacts.service.persistence.impl.constants.OSBFaroPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the contacts layout template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shinn Lok
 * @generated
 */
@Component(service = ContactsLayoutTemplatePersistence.class)
public class ContactsLayoutTemplatePersistenceImpl
	extends BasePersistenceImpl
		<ContactsLayoutTemplate, NoSuchContactsLayoutTemplateException>
	implements ContactsLayoutTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ContactsLayoutTemplateUtil</code> to access the contacts layout template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ContactsLayoutTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ContactsLayoutTemplate, NoSuchContactsLayoutTemplateException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts layout templates
	 */
	@Override
	public List<ContactsLayoutTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ContactsLayoutTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a matching contacts layout template could not be found
	 */
	@Override
	public ContactsLayoutTemplate findByGroupId_First(
			long groupId,
			OrderByComparator<ContactsLayoutTemplate> orderByComparator)
		throws NoSuchContactsLayoutTemplateException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template, or <code>null</code> if a matching contacts layout template could not be found
	 */
	@Override
	public ContactsLayoutTemplate fetchByGroupId_First(
		long groupId,
		OrderByComparator<ContactsLayoutTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the contacts layout templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of contacts layout templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching contacts layout templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<ContactsLayoutTemplate, NoSuchContactsLayoutTemplateException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ContactsLayoutTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of contacts layout templates
	 * @param end the upper bound of the range of contacts layout templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts layout templates
	 */
	@Override
	public List<ContactsLayoutTemplate> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<ContactsLayoutTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache, new Object[] {groupId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a matching contacts layout template could not be found
	 */
	@Override
	public ContactsLayoutTemplate findByG_T_First(
			long groupId, int type,
			OrderByComparator<ContactsLayoutTemplate> orderByComparator)
		throws NoSuchContactsLayoutTemplateException {

		return _collectionPersistenceFinderByG_T.findFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Returns the first contacts layout template in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts layout template, or <code>null</code> if a matching contacts layout template could not be found
	 */
	@Override
	public ContactsLayoutTemplate fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<ContactsLayoutTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache, new Object[] {groupId, type}, orderByComparator);
	}

	/**
	 * Removes all the contacts layout templates where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, int type) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache, new Object[] {groupId, type});
	}

	/**
	 * Returns the number of contacts layout templates where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching contacts layout templates
	 */
	@Override
	public int countByG_T(long groupId, int type) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache, new Object[] {groupId, type});
	}

	public ContactsLayoutTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ContactsLayoutTemplate.class);

		setModelImplClass(ContactsLayoutTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(ContactsLayoutTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new contacts layout template with the primary key. Does not add the contacts layout template to the database.
	 *
	 * @param contactsLayoutTemplateId the primary key for the new contacts layout template
	 * @return the new contacts layout template
	 */
	@Override
	public ContactsLayoutTemplate create(long contactsLayoutTemplateId) {
		ContactsLayoutTemplate contactsLayoutTemplate =
			new ContactsLayoutTemplateImpl();

		contactsLayoutTemplate.setNew(true);
		contactsLayoutTemplate.setPrimaryKey(contactsLayoutTemplateId);

		contactsLayoutTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return contactsLayoutTemplate;
	}

	/**
	 * Removes the contacts layout template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template that was removed
	 * @throws NoSuchContactsLayoutTemplateException if a contacts layout template with the primary key could not be found
	 */
	@Override
	public ContactsLayoutTemplate remove(long contactsLayoutTemplateId)
		throws NoSuchContactsLayoutTemplateException {

		return remove((Serializable)contactsLayoutTemplateId);
	}

	@Override
	protected ContactsLayoutTemplate removeImpl(
		ContactsLayoutTemplate contactsLayoutTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(contactsLayoutTemplate)) {
				contactsLayoutTemplate = (ContactsLayoutTemplate)session.get(
					ContactsLayoutTemplateImpl.class,
					contactsLayoutTemplate.getPrimaryKeyObj());
			}

			if (contactsLayoutTemplate != null) {
				session.delete(contactsLayoutTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (contactsLayoutTemplate != null) {
			clearCache(contactsLayoutTemplate);
		}

		return contactsLayoutTemplate;
	}

	@Override
	public ContactsLayoutTemplate updateImpl(
		ContactsLayoutTemplate contactsLayoutTemplate) {

		boolean isNew = contactsLayoutTemplate.isNew();

		if (!(contactsLayoutTemplate instanceof
				ContactsLayoutTemplateModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(contactsLayoutTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					contactsLayoutTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in contactsLayoutTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ContactsLayoutTemplate implementation " +
					contactsLayoutTemplate.getClass());
		}

		ContactsLayoutTemplateModelImpl contactsLayoutTemplateModelImpl =
			(ContactsLayoutTemplateModelImpl)contactsLayoutTemplate;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(contactsLayoutTemplate);
			}
			else {
				contactsLayoutTemplate = (ContactsLayoutTemplate)session.merge(
					contactsLayoutTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(contactsLayoutTemplate, false);

		if (isNew) {
			contactsLayoutTemplate.setNew(false);
		}

		contactsLayoutTemplate.resetOriginalValues();

		return contactsLayoutTemplate;
	}

	/**
	 * Returns the contacts layout template with the primary key or throws a <code>NoSuchContactsLayoutTemplateException</code> if it could not be found.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template
	 * @throws NoSuchContactsLayoutTemplateException if a contacts layout template with the primary key could not be found
	 */
	@Override
	public ContactsLayoutTemplate findByPrimaryKey(
			long contactsLayoutTemplateId)
		throws NoSuchContactsLayoutTemplateException {

		return findByPrimaryKey((Serializable)contactsLayoutTemplateId);
	}

	/**
	 * Returns the contacts layout template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contactsLayoutTemplateId the primary key of the contacts layout template
	 * @return the contacts layout template, or <code>null</code> if a contacts layout template with the primary key could not be found
	 */
	@Override
	public ContactsLayoutTemplate fetchByPrimaryKey(
		long contactsLayoutTemplateId) {

		return fetchByPrimaryKey((Serializable)contactsLayoutTemplateId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "contactsLayoutTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CONTACTSLAYOUTTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ContactsLayoutTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the contacts layout template persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_CONTACTSLAYOUTTEMPLATE_WHERE,
				_SQL_COUNT_CONTACTSLAYOUTTEMPLATE_WHERE,
				ContactsLayoutTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"contactsLayoutTemplate.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					ContactsLayoutTemplate::getGroupId));

		_collectionPersistenceFinderByG_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "type_"}, false),
			_SQL_SELECT_CONTACTSLAYOUTTEMPLATE_WHERE,
			_SQL_COUNT_CONTACTSLAYOUTTEMPLATE_WHERE,
			ContactsLayoutTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"contactsLayoutTemplate.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, ContactsLayoutTemplate::getGroupId),
			new FinderColumn<>(
				"contactsLayoutTemplate.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				ContactsLayoutTemplate::getType));

		ContactsLayoutTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ContactsLayoutTemplateUtil.setPersistence(null);

		entityCache.removeCache(ContactsLayoutTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ContactsLayoutTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CONTACTSLAYOUTTEMPLATE =
		"SELECT contactsLayoutTemplate FROM ContactsLayoutTemplate contactsLayoutTemplate";

	private static final String _SQL_SELECT_CONTACTSLAYOUTTEMPLATE_WHERE =
		"SELECT contactsLayoutTemplate FROM ContactsLayoutTemplate contactsLayoutTemplate WHERE ";

	private static final String _SQL_COUNT_CONTACTSLAYOUTTEMPLATE_WHERE =
		"SELECT COUNT(contactsLayoutTemplate) FROM ContactsLayoutTemplate contactsLayoutTemplate WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1323218574
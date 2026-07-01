/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.service.persistence.impl;

import com.liferay.osb.faro.contacts.exception.NoSuchContactsCardTemplateException;
import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.osb.faro.contacts.model.ContactsCardTemplateTable;
import com.liferay.osb.faro.contacts.model.impl.ContactsCardTemplateImpl;
import com.liferay.osb.faro.contacts.model.impl.ContactsCardTemplateModelImpl;
import com.liferay.osb.faro.contacts.service.persistence.ContactsCardTemplatePersistence;
import com.liferay.osb.faro.contacts.service.persistence.ContactsCardTemplateUtil;
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
 * The persistence implementation for the contacts card template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shinn Lok
 * @generated
 */
@Component(service = ContactsCardTemplatePersistence.class)
public class ContactsCardTemplatePersistenceImpl
	extends BasePersistenceImpl
		<ContactsCardTemplate, NoSuchContactsCardTemplateException>
	implements ContactsCardTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ContactsCardTemplateUtil</code> to access the contacts card template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ContactsCardTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ContactsCardTemplate, NoSuchContactsCardTemplateException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the contacts card templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ContactsCardTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of contacts card templates
	 * @param end the upper bound of the range of contacts card templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching contacts card templates
	 */
	@Override
	public List<ContactsCardTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ContactsCardTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first contacts card template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts card template
	 * @throws NoSuchContactsCardTemplateException if a matching contacts card template could not be found
	 */
	@Override
	public ContactsCardTemplate findByGroupId_First(
			long groupId,
			OrderByComparator<ContactsCardTemplate> orderByComparator)
		throws NoSuchContactsCardTemplateException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first contacts card template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching contacts card template, or <code>null</code> if a matching contacts card template could not be found
	 */
	@Override
	public ContactsCardTemplate fetchByGroupId_First(
		long groupId,
		OrderByComparator<ContactsCardTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the contacts card templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of contacts card templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching contacts card templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	public ContactsCardTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ContactsCardTemplate.class);

		setModelImplClass(ContactsCardTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(ContactsCardTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new contacts card template with the primary key. Does not add the contacts card template to the database.
	 *
	 * @param contactsCardTemplateId the primary key for the new contacts card template
	 * @return the new contacts card template
	 */
	@Override
	public ContactsCardTemplate create(long contactsCardTemplateId) {
		ContactsCardTemplate contactsCardTemplate =
			new ContactsCardTemplateImpl();

		contactsCardTemplate.setNew(true);
		contactsCardTemplate.setPrimaryKey(contactsCardTemplateId);

		contactsCardTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return contactsCardTemplate;
	}

	/**
	 * Removes the contacts card template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template that was removed
	 * @throws NoSuchContactsCardTemplateException if a contacts card template with the primary key could not be found
	 */
	@Override
	public ContactsCardTemplate remove(long contactsCardTemplateId)
		throws NoSuchContactsCardTemplateException {

		return remove((Serializable)contactsCardTemplateId);
	}

	@Override
	protected ContactsCardTemplate removeImpl(
		ContactsCardTemplate contactsCardTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(contactsCardTemplate)) {
				contactsCardTemplate = (ContactsCardTemplate)session.get(
					ContactsCardTemplateImpl.class,
					contactsCardTemplate.getPrimaryKeyObj());
			}

			if (contactsCardTemplate != null) {
				session.delete(contactsCardTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (contactsCardTemplate != null) {
			clearCache(contactsCardTemplate);
		}

		return contactsCardTemplate;
	}

	@Override
	public ContactsCardTemplate updateImpl(
		ContactsCardTemplate contactsCardTemplate) {

		boolean isNew = contactsCardTemplate.isNew();

		if (!(contactsCardTemplate instanceof ContactsCardTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(contactsCardTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					contactsCardTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in contactsCardTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ContactsCardTemplate implementation " +
					contactsCardTemplate.getClass());
		}

		ContactsCardTemplateModelImpl contactsCardTemplateModelImpl =
			(ContactsCardTemplateModelImpl)contactsCardTemplate;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(contactsCardTemplate);
			}
			else {
				contactsCardTemplate = (ContactsCardTemplate)session.merge(
					contactsCardTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(contactsCardTemplate, false);

		if (isNew) {
			contactsCardTemplate.setNew(false);
		}

		contactsCardTemplate.resetOriginalValues();

		return contactsCardTemplate;
	}

	/**
	 * Returns the contacts card template with the primary key or throws a <code>NoSuchContactsCardTemplateException</code> if it could not be found.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template
	 * @throws NoSuchContactsCardTemplateException if a contacts card template with the primary key could not be found
	 */
	@Override
	public ContactsCardTemplate findByPrimaryKey(long contactsCardTemplateId)
		throws NoSuchContactsCardTemplateException {

		return findByPrimaryKey((Serializable)contactsCardTemplateId);
	}

	/**
	 * Returns the contacts card template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param contactsCardTemplateId the primary key of the contacts card template
	 * @return the contacts card template, or <code>null</code> if a contacts card template with the primary key could not be found
	 */
	@Override
	public ContactsCardTemplate fetchByPrimaryKey(long contactsCardTemplateId) {
		return fetchByPrimaryKey((Serializable)contactsCardTemplateId);
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
		return "contactsCardTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CONTACTSCARDTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ContactsCardTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the contacts card template persistence.
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
				_SQL_SELECT_CONTACTSCARDTEMPLATE_WHERE,
				_SQL_COUNT_CONTACTSCARDTEMPLATE_WHERE,
				ContactsCardTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"contactsCardTemplate.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, ContactsCardTemplate::getGroupId));

		ContactsCardTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ContactsCardTemplateUtil.setPersistence(null);

		entityCache.removeCache(ContactsCardTemplateImpl.class.getName());
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
		ContactsCardTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CONTACTSCARDTEMPLATE =
		"SELECT contactsCardTemplate FROM ContactsCardTemplate contactsCardTemplate";

	private static final String _SQL_SELECT_CONTACTSCARDTEMPLATE_WHERE =
		"SELECT contactsCardTemplate FROM ContactsCardTemplate contactsCardTemplate WHERE ";

	private static final String _SQL_COUNT_CONTACTSCARDTEMPLATE_WHERE =
		"SELECT COUNT(contactsCardTemplate) FROM ContactsCardTemplate contactsCardTemplate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ContactsCardTemplate exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2001086987
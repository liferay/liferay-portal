/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.cache.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.ast.DefaultASTNodeListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactTable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.model.impl.ContactImpl;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.service.persistence.impl.ContactPersistenceImpl;
import com.liferay.portal.service.persistence.impl.LayoutPersistenceImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class CTCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testCTAwareDSLQueryCacheResults() throws Exception {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			LayoutTable.INSTANCE
		).from(
			LayoutTable.INSTANCE
		).where(
			LayoutTable.INSTANCE.groupId.eq(_group.getGroupId())
		);

		Object[] finderArgs = {_group.getGroupId()};

		StringBundler sb = new StringBundler();

		dslQuery.toSQL(sb::append, new DefaultASTNodeListener());

		FinderPath finderPath = new FinderPath(
			FinderPath.encodeDSLQueryCacheName(new String[] {"Layout"}),
			"dslQuery", sb.getStrings(), new String[0], true);

		BasePersistence<?> basePersistence =
			_layoutLocalService.getBasePersistence();

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		List<Layout> cachedLayouts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			LayoutTestUtil.addTypePortletLayout(_group);

			_layoutLocalService.dslQuery(dslQuery);

			cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedLayouts.toString(), 1, cachedLayouts.size());
		}

		_ctProcessLocalService.addCTProcess(
			ctCollection1.getUserId(), ctCollection1.getCtCollectionId());

		CTCollection ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection2.getCtCollectionId())) {

			LayoutTestUtil.addTypePortletLayout(_group);

			_layoutLocalService.dslQuery(dslQuery);

			cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedLayouts.toString(), 2, cachedLayouts.size());
		}

		_layoutLocalService.dslQuery(dslQuery);

		cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
			finderPath, finderArgs, basePersistence);

		Assert.assertEquals(cachedLayouts.toString(), 1, cachedLayouts.size());
	}

	@Test
	public void testCTAwareEntityCacheResults() throws Exception {
		Layout productionLayout = LayoutTestUtil.addTypePortletLayout(_group);

		Layout cachedLayout = (Layout)EntityCacheUtil.getResult(
			LayoutImpl.class, productionLayout.getPrimaryKey());

		Assert.assertEquals(
			productionLayout.getCtCollectionId(),
			cachedLayout.getCtCollectionId());
		Assert.assertEquals(
			productionLayout.getMvccVersion(), cachedLayout.getMvccVersion());

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_ctCollections.add(ctCollection1);

		Layout ctCollection1Layout = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			ctCollection1Layout = _layoutLocalService.updateLayout(
				productionLayout.getGroupId(),
				productionLayout.isPrivateLayout(),
				productionLayout.getLayoutId(), new Date());

			Assert.assertEquals(
				productionLayout.getPrimaryKey(),
				ctCollection1Layout.getPrimaryKey());
			Assert.assertNotEquals(
				productionLayout.getCtCollectionId(),
				ctCollection1Layout.getCtCollectionId());

			cachedLayout = (Layout)EntityCacheUtil.getResult(
				LayoutImpl.class, productionLayout.getPrimaryKey());

			Assert.assertNotEquals(
				productionLayout.getCtCollectionId(),
				cachedLayout.getCtCollectionId());
			Assert.assertEquals(
				ctCollection1Layout.getCtCollectionId(),
				cachedLayout.getCtCollectionId());
		}

		cachedLayout = (Layout)EntityCacheUtil.getResult(
			LayoutImpl.class, productionLayout.getPrimaryKey());

		Assert.assertEquals(
			productionLayout.getCtCollectionId(),
			cachedLayout.getCtCollectionId());
		Assert.assertNotEquals(
			ctCollection1Layout.getCtCollectionId(),
			cachedLayout.getCtCollectionId());

		CTCollection ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_ctCollections.add(ctCollection2);

		Layout ctCollection2Layout = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection2.getCtCollectionId())) {

			ctCollection2Layout = _layoutLocalService.updateLayout(
				productionLayout.getGroupId(),
				productionLayout.isPrivateLayout(),
				productionLayout.getLayoutId(), new Date());

			Assert.assertEquals(
				productionLayout.getPrimaryKey(),
				ctCollection2Layout.getPrimaryKey());
			Assert.assertNotEquals(
				productionLayout.getCtCollectionId(),
				ctCollection2Layout.getCtCollectionId());

			cachedLayout = (Layout)EntityCacheUtil.getResult(
				LayoutImpl.class, productionLayout.getPrimaryKey());

			Assert.assertNotEquals(
				productionLayout.getCtCollectionId(),
				cachedLayout.getCtCollectionId());
			Assert.assertNotEquals(
				ctCollection1Layout.getCtCollectionId(),
				cachedLayout.getCtCollectionId());
			Assert.assertEquals(
				ctCollection2Layout.getCtCollectionId(),
				cachedLayout.getCtCollectionId());
		}
	}

	@Test
	public void testCTAwareFinderCacheResults() throws Exception {

		// Add 1 layout so production has 1 total

		LayoutTestUtil.addTypePortletLayout(_group);

		FinderPath finderPath = new FinderPath(
			LayoutPersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByCompanyId", new String[] {Long.class.getName()},
			new String[] {"companyId"}, true);

		Object[] finderArgs = {TestPropsValues.getCompanyId()};

		BasePersistence<?> basePersistence =
			_layoutLocalService.getBasePersistence();

		List<Layout> productionLayouts = _layoutLocalService.getLayouts(
			TestPropsValues.getCompanyId());

		List<Layout> cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
			finderPath, finderArgs, basePersistence);

		Assert.assertEquals(
			cachedLayouts.toString(), productionLayouts.size(),
			cachedLayouts.size());

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		List<Layout> ctCollection1Layouts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			// Add 1 layout so this publication has 1 more than production

			LayoutTestUtil.addTypePortletLayout(_group);

			ctCollection1Layouts = _layoutLocalService.getLayouts(
				TestPropsValues.getCompanyId());

			cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedLayouts.toString(), ctCollection1Layouts.size(),
				cachedLayouts.size());
			Assert.assertNotEquals(
				cachedLayouts.toString(), cachedLayouts.size(),
				productionLayouts.size());
		}

		CTCollection ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		List<Layout> ctCollection2Layouts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection2.getCtCollectionId())) {

			// Adding 2 layouts so this publication has 2 more than produdction

			LayoutTestUtil.addTypePortletLayout(_group);
			LayoutTestUtil.addTypePortletLayout(_group);

			ctCollection2Layouts = _layoutLocalService.getLayouts(
				TestPropsValues.getCompanyId());

			cachedLayouts = (List<Layout>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedLayouts.toString(), ctCollection2Layouts.size(),
				cachedLayouts.size());
			Assert.assertNotEquals(
				cachedLayouts.toString(), cachedLayouts.size(),
				productionLayouts.size());
			Assert.assertNotEquals(
				cachedLayouts.toString(), cachedLayouts.size(),
				ctCollection1Layouts.size());
		}
	}

	@Test
	public void testDeleteClearsCTCollectionCounts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		FinderPath finderPath = new FinderPath(
			LayoutPersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByGroupId", new String[] {Long.class.getName()},
			new String[] {"groupId"}, false);

		Object[] finderArgs = {RandomTestUtil.randomLong()};

		BasePersistence<?> basePersistence =
			_layoutLocalService.getBasePersistence();

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_ctCollections.add(ctCollection);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Assert.assertNull(
				FinderCacheUtil.getResult(
					finderPath, finderArgs, basePersistence));

			FinderCacheUtil.putResult(finderPath, finderArgs, 1L);

			Assert.assertEquals(
				1L,
				FinderCacheUtil.getResult(
					finderPath, finderArgs, basePersistence));
		}

		_layoutLocalService.deleteLayout(layout);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			Assert.assertNull(
				FinderCacheUtil.getResult(
					finderPath, finderArgs, basePersistence));
		}
	}

	@Test
	public void testWithoutCTAwareDSLQueryCacheResults() throws Exception {
		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			ContactTable.INSTANCE
		).from(
			ContactTable.INSTANCE
		).where(
			ContactTable.INSTANCE.userId.eq(TestPropsValues.getUserId())
		);

		Object[] finderArgs = {TestPropsValues.getUserId()};

		StringBundler sb = new StringBundler();

		dslQuery.toSQL(sb::append, new DefaultASTNodeListener());

		FinderPath finderPath = new FinderPath(
			FinderPath.encodeDSLQueryCacheName(new String[] {"Contact_"}),
			"dslQuery", sb.getStrings(), new String[0], true);

		BasePersistence<?> basePersistence =
			_contactLocalService.getBasePersistence();

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		int count = _contactLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				ContactTable.INSTANCE
			).where(
				ContactTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(User.class.getName())
				).and(
					ContactTable.INSTANCE.classPK.eq(
						TestPropsValues.getUserId())
				)
			));

		List<Contact> cachedContacts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			_contactLocalService.addContact(
				TestPropsValues.getUserId(), StringPool.STAR, 1,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				0, RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
				RandomTestUtil.randomInt(1, 28),
				RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			_contactLocalService.dslQuery(dslQuery);

			cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedContacts.toString(), 1 + count, cachedContacts.size());
		}

		CTCollection ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection2.getCtCollectionId())) {

			_contactLocalService.addContact(
				TestPropsValues.getUserId(), StringPool.STAR, 1,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				0, RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
				RandomTestUtil.randomInt(1, 28),
				RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			_contactLocalService.dslQuery(dslQuery);

			cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedContacts.toString(), 2 + count, cachedContacts.size());
		}

		_contactLocalService.dslQuery(dslQuery);

		cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
			finderPath, finderArgs, basePersistence);

		Assert.assertEquals(
			cachedContacts.toString(), 2 + count, cachedContacts.size());
	}

	@Test
	public void testWithoutCTAwareEntityCacheResults() throws Exception {
		Contact productionContact = _contactLocalService.addContact(
			TestPropsValues.getUserId(), StringPool.STAR, 1,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0, 0,
			RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
			RandomTestUtil.randomInt(1, 28),
			RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Contact cachedContact = (Contact)EntityCacheUtil.getResult(
			ContactImpl.class, productionContact.getPrimaryKey());

		Assert.assertEquals(
			productionContact.getMvccVersion(), cachedContact.getMvccVersion());

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Contact ctCollection1Contact = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			ctCollection1Contact = _contactLocalService.updateContact(
				productionContact.getContactId(),
				productionContact.getEmailAddress(),
				productionContact.getFirstName(),
				productionContact.getMiddleName(),
				productionContact.getLastName(),
				productionContact.getPrefixListTypeId(),
				productionContact.getSuffixListTypeId(),
				productionContact.isMale(),
				RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
				RandomTestUtil.randomInt(1, 28),
				RandomTestUtil.randomInt(1970, 2017),
				productionContact.getSmsSn(), productionContact.getFacebookSn(),
				productionContact.getJabberSn(), productionContact.getSkypeSn(),
				productionContact.getTwitterSn(),
				RandomTestUtil.randomString());

			Assert.assertEquals(
				ctCollection1Contact.getPrimaryKey(),
				productionContact.getPrimaryKey());
			Assert.assertNotEquals(
				ctCollection1Contact.getJobTitle(),
				productionContact.getJobTitle());

			cachedContact = (Contact)EntityCacheUtil.getResult(
				ContactImpl.class, productionContact.getPrimaryKey());

			Assert.assertNotEquals(
				cachedContact.getJobTitle(), productionContact.getJobTitle());
			Assert.assertEquals(
				cachedContact.getJobTitle(),
				ctCollection1Contact.getJobTitle());
		}

		productionContact = _contactLocalService.fetchContact(
			productionContact.getContactId());

		cachedContact = (Contact)EntityCacheUtil.getResult(
			ContactImpl.class, productionContact.getPrimaryKey());

		Assert.assertEquals(
			cachedContact.getJobTitle(), productionContact.getJobTitle());

		Assert.assertEquals(
			ctCollection1Contact.getJobTitle(),
			productionContact.getJobTitle());
	}

	@Test
	public void testWithoutCTAwareFinderCacheResults() throws Exception {
		_contactLocalService.addContact(
			TestPropsValues.getUserId(), StringPool.STAR, 1,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0, 0,
			RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
			RandomTestUtil.randomInt(1, 28),
			RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		FinderPath finderPath = new FinderPath(
			ContactPersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findAll", new String[0], new String[0], true);

		Object[] finderArgs = new Object[0];

		BasePersistence<?> basePersistence =
			_contactLocalService.getBasePersistence();

		List<Contact> productionContacts = _contactLocalService.getContacts(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		List<Contact> cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
			finderPath, finderArgs, basePersistence);

		Assert.assertEquals(
			cachedContacts.toString(), productionContacts.size(),
			cachedContacts.size());

		CTCollection ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		List<Contact> ctCollection1Contacts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection1.getCtCollectionId())) {

			_contactLocalService.addContact(
				TestPropsValues.getUserId(), StringPool.STAR, 1,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				0, RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
				RandomTestUtil.randomInt(1, 28),
				RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			ctCollection1Contacts = _contactLocalService.getContacts(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedContacts.toString(), ctCollection1Contacts.size(),
				cachedContacts.size());
			Assert.assertNotEquals(
				cachedContacts.toString(), cachedContacts.size(),
				productionContacts.size());
		}

		productionContacts = _contactLocalService.getContacts(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			cachedContacts.toString(), cachedContacts.size(),
			productionContacts.size());

		CTCollection ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		List<Contact> ctCollection2Contacts = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection2.getCtCollectionId())) {

			_contactLocalService.addContact(
				TestPropsValues.getUserId(), StringPool.STAR, 1,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				0, RandomTestUtil.randomBoolean(),
				RandomTestUtil.randomInt(Calendar.JANUARY, Calendar.DECEMBER),
				RandomTestUtil.randomInt(1, 28),
				RandomTestUtil.randomInt(1970, 2017), StringPool.BLANK,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			ctCollection2Contacts = _contactLocalService.getContacts(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			cachedContacts = (List<Contact>)FinderCacheUtil.getResult(
				finderPath, finderArgs, basePersistence);

			Assert.assertEquals(
				cachedContacts.toString(), ctCollection2Contacts.size(),
				cachedContacts.size());
			Assert.assertNotEquals(
				cachedContacts.toString(), cachedContacts.size(),
				productionContacts.size());
			Assert.assertNotEquals(
				cachedContacts.toString(), cachedContacts.size(),
				ctCollection1Contacts.size());
		}

		productionContacts = _contactLocalService.getContacts(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			cachedContacts.toString(), cachedContacts.size(),
			productionContacts.size());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ContactLocalService _contactLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private final List<CTCollection> _ctCollections = new ArrayList<>();

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

}
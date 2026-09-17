/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.persistence.ContactPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.servlet.filters.threadlocal.ThreadLocalFilterThreadLocal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class EntityCacheRollbackTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRollbackRestoresEntityCacheMvccVersion() throws Exception {
		User user = TestPropsValues.getUser();

		long contactId = user.getContactId();

		_contactLocalService.getContact(contactId);

		try (SafeCloseable safeCloseable =
				ThreadLocalFilterThreadLocal.setFilterInvokedWithSafeCloseable(
					true)) {

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					Contact contact = _contactPersistence.findByPrimaryKey(
						contactId);

					_contactPersistence.reassociateIfAbsent(contact);

					contact.setJobTitle(RandomTestUtil.randomString());

					_contactLocalService.getContactsCount(
						RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

					_contactPersistence.update(contact);

					throw new PortalException("Roll back");
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertEquals("Roll back", throwable.getMessage());
		}

		Contact entityCacheContact = _contactLocalService.getContact(contactId);

		_entityCache.clearCache();

		Contact databaseContact = _contactLocalService.getContact(contactId);

		Assert.assertEquals(
			databaseContact.getMvccVersion(),
			entityCacheContact.getMvccVersion());
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Inject
	private ContactLocalService _contactLocalService;

	@Inject
	private ContactPersistence _contactPersistence;

	@Inject
	private EntityCache _entityCache;

}
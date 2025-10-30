/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.persistence.TicketPersistence;
import com.liferay.portal.kernel.service.persistence.TicketUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class TicketPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = TicketUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Ticket> iterator = _tickets.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Ticket ticket = _persistence.create(pk);

		Assert.assertNotNull(ticket);

		Assert.assertEquals(ticket.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Ticket newTicket = addTicket();

		_persistence.remove(newTicket);

		Ticket existingTicket = _persistence.fetchByPrimaryKey(
			newTicket.getPrimaryKey());

		Assert.assertNull(existingTicket);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTicket();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Ticket newTicket = _persistence.create(pk);

		newTicket.setMvccVersion(RandomTestUtil.nextLong());

		newTicket.setCompanyId(RandomTestUtil.nextLong());

		newTicket.setCreateDate(RandomTestUtil.nextDate());

		newTicket.setClassNameId(RandomTestUtil.nextLong());

		newTicket.setClassPK(RandomTestUtil.nextLong());

		newTicket.setKey(RandomTestUtil.randomString());

		newTicket.setType(RandomTestUtil.nextInt());

		newTicket.setExtraInfo(RandomTestUtil.randomString());

		newTicket.setExpirationDate(RandomTestUtil.nextDate());

		_tickets.add(_persistence.update(newTicket));

		Ticket existingTicket = _persistence.findByPrimaryKey(
			newTicket.getPrimaryKey());

		Assert.assertEquals(
			existingTicket.getMvccVersion(), newTicket.getMvccVersion());
		Assert.assertEquals(
			existingTicket.getTicketId(), newTicket.getTicketId());
		Assert.assertEquals(
			existingTicket.getCompanyId(), newTicket.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTicket.getCreateDate()),
			Time.getShortTimestamp(newTicket.getCreateDate()));
		Assert.assertEquals(
			existingTicket.getClassNameId(), newTicket.getClassNameId());
		Assert.assertEquals(
			existingTicket.getClassPK(), newTicket.getClassPK());
		Assert.assertEquals(existingTicket.getKey(), newTicket.getKey());
		Assert.assertEquals(existingTicket.getType(), newTicket.getType());
		Assert.assertEquals(
			existingTicket.getExtraInfo(), newTicket.getExtraInfo());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTicket.getExpirationDate()),
			Time.getShortTimestamp(newTicket.getExpirationDate()));
	}

	@Test
	public void testCountByKey() throws Exception {
		_persistence.countByKey("");

		_persistence.countByKey("null");

		_persistence.countByKey((String)null);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_T() throws Exception {
		_persistence.countByC_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_T(0L, 0L, 0);
	}

	@Test
	public void testCountByC_C_C_T() throws Exception {
		_persistence.countByC_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_C_C_T(0L, 0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Ticket newTicket = addTicket();

		Ticket existingTicket = _persistence.findByPrimaryKey(
			newTicket.getPrimaryKey());

		Assert.assertEquals(existingTicket, newTicket);
	}

	@Test(expected = NoSuchTicketException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Ticket> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Ticket", "mvccVersion", true, "ticketId", true, "companyId", true,
			"createDate", true, "classNameId", true, "classPK", true, "key",
			true, "type", true, "expirationDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Ticket newTicket = addTicket();

		Ticket existingTicket = _persistence.fetchByPrimaryKey(
			newTicket.getPrimaryKey());

		Assert.assertEquals(existingTicket, newTicket);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Ticket missingTicket = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTicket);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Ticket newTicket1 = addTicket();
		Ticket newTicket2 = addTicket();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTicket1.getPrimaryKey());
		primaryKeys.add(newTicket2.getPrimaryKey());

		Map<Serializable, Ticket> tickets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, tickets.size());
		Assert.assertEquals(
			newTicket1, tickets.get(newTicket1.getPrimaryKey()));
		Assert.assertEquals(
			newTicket2, tickets.get(newTicket2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Ticket> tickets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(tickets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Ticket newTicket = addTicket();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTicket.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Ticket> tickets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, tickets.size());
		Assert.assertEquals(newTicket, tickets.get(newTicket.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Ticket> tickets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(tickets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Ticket newTicket = addTicket();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTicket.getPrimaryKey());

		Map<Serializable, Ticket> tickets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, tickets.size());
		Assert.assertEquals(newTicket, tickets.get(newTicket.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Ticket newTicket = addTicket();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newTicket.getPrimaryKey()));
	}

	private void _assertOriginalValues(Ticket ticket) {
		Assert.assertEquals(
			ticket.getKey(),
			ReflectionTestUtil.invoke(
				ticket, "getColumnOriginalValue", new Class<?>[] {String.class},
				"key_"));
	}

	protected Ticket addTicket() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Ticket ticket = _persistence.create(pk);

		ticket.setMvccVersion(RandomTestUtil.nextLong());

		ticket.setCompanyId(RandomTestUtil.nextLong());

		ticket.setCreateDate(RandomTestUtil.nextDate());

		ticket.setClassNameId(RandomTestUtil.nextLong());

		ticket.setClassPK(RandomTestUtil.nextLong());

		ticket.setKey(RandomTestUtil.randomString());

		ticket.setType(RandomTestUtil.nextInt());

		ticket.setExtraInfo(RandomTestUtil.randomString());

		ticket.setExpirationDate(RandomTestUtil.nextDate());

		_tickets.add(_persistence.update(ticket));

		return ticket;
	}

	private List<Ticket> _tickets = new ArrayList<Ticket>();
	private TicketPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
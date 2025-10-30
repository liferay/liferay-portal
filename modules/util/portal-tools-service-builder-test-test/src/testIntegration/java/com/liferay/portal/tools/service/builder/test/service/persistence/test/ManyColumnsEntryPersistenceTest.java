/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchManyColumnsEntryException;
import com.liferay.portal.tools.service.builder.test.model.ManyColumnsEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ManyColumnsEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ManyColumnsEntryUtil;

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
public class ManyColumnsEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = ManyColumnsEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ManyColumnsEntry> iterator = _manyColumnsEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ManyColumnsEntry manyColumnsEntry = _persistence.create(pk);

		Assert.assertNotNull(manyColumnsEntry);

		Assert.assertEquals(manyColumnsEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ManyColumnsEntry newManyColumnsEntry = addManyColumnsEntry();

		_persistence.remove(newManyColumnsEntry);

		ManyColumnsEntry existingManyColumnsEntry =
			_persistence.fetchByPrimaryKey(newManyColumnsEntry.getPrimaryKey());

		Assert.assertNull(existingManyColumnsEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addManyColumnsEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ManyColumnsEntry newManyColumnsEntry = _persistence.create(pk);

		newManyColumnsEntry.setColumn1(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn2(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn3(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn4(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn5(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn6(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn7(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn8(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn9(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn10(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn11(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn12(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn13(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn14(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn15(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn16(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn17(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn18(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn19(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn20(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn21(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn22(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn23(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn24(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn25(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn26(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn27(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn28(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn29(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn30(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn31(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn32(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn33(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn34(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn35(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn36(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn37(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn38(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn39(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn40(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn41(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn42(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn43(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn44(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn45(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn46(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn47(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn48(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn49(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn50(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn51(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn52(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn53(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn54(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn55(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn56(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn57(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn58(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn59(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn60(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn61(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn62(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn63(RandomTestUtil.nextInt());

		newManyColumnsEntry.setColumn64(RandomTestUtil.nextInt());

		_manyColumnsEntries.add(_persistence.update(newManyColumnsEntry));

		ManyColumnsEntry existingManyColumnsEntry =
			_persistence.findByPrimaryKey(newManyColumnsEntry.getPrimaryKey());

		Assert.assertEquals(
			existingManyColumnsEntry.getManyColumnsEntryId(),
			newManyColumnsEntry.getManyColumnsEntryId());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn1(),
			newManyColumnsEntry.getColumn1());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn2(),
			newManyColumnsEntry.getColumn2());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn3(),
			newManyColumnsEntry.getColumn3());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn4(),
			newManyColumnsEntry.getColumn4());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn5(),
			newManyColumnsEntry.getColumn5());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn6(),
			newManyColumnsEntry.getColumn6());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn7(),
			newManyColumnsEntry.getColumn7());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn8(),
			newManyColumnsEntry.getColumn8());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn9(),
			newManyColumnsEntry.getColumn9());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn10(),
			newManyColumnsEntry.getColumn10());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn11(),
			newManyColumnsEntry.getColumn11());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn12(),
			newManyColumnsEntry.getColumn12());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn13(),
			newManyColumnsEntry.getColumn13());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn14(),
			newManyColumnsEntry.getColumn14());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn15(),
			newManyColumnsEntry.getColumn15());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn16(),
			newManyColumnsEntry.getColumn16());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn17(),
			newManyColumnsEntry.getColumn17());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn18(),
			newManyColumnsEntry.getColumn18());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn19(),
			newManyColumnsEntry.getColumn19());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn20(),
			newManyColumnsEntry.getColumn20());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn21(),
			newManyColumnsEntry.getColumn21());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn22(),
			newManyColumnsEntry.getColumn22());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn23(),
			newManyColumnsEntry.getColumn23());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn24(),
			newManyColumnsEntry.getColumn24());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn25(),
			newManyColumnsEntry.getColumn25());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn26(),
			newManyColumnsEntry.getColumn26());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn27(),
			newManyColumnsEntry.getColumn27());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn28(),
			newManyColumnsEntry.getColumn28());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn29(),
			newManyColumnsEntry.getColumn29());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn30(),
			newManyColumnsEntry.getColumn30());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn31(),
			newManyColumnsEntry.getColumn31());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn32(),
			newManyColumnsEntry.getColumn32());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn33(),
			newManyColumnsEntry.getColumn33());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn34(),
			newManyColumnsEntry.getColumn34());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn35(),
			newManyColumnsEntry.getColumn35());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn36(),
			newManyColumnsEntry.getColumn36());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn37(),
			newManyColumnsEntry.getColumn37());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn38(),
			newManyColumnsEntry.getColumn38());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn39(),
			newManyColumnsEntry.getColumn39());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn40(),
			newManyColumnsEntry.getColumn40());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn41(),
			newManyColumnsEntry.getColumn41());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn42(),
			newManyColumnsEntry.getColumn42());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn43(),
			newManyColumnsEntry.getColumn43());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn44(),
			newManyColumnsEntry.getColumn44());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn45(),
			newManyColumnsEntry.getColumn45());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn46(),
			newManyColumnsEntry.getColumn46());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn47(),
			newManyColumnsEntry.getColumn47());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn48(),
			newManyColumnsEntry.getColumn48());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn49(),
			newManyColumnsEntry.getColumn49());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn50(),
			newManyColumnsEntry.getColumn50());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn51(),
			newManyColumnsEntry.getColumn51());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn52(),
			newManyColumnsEntry.getColumn52());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn53(),
			newManyColumnsEntry.getColumn53());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn54(),
			newManyColumnsEntry.getColumn54());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn55(),
			newManyColumnsEntry.getColumn55());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn56(),
			newManyColumnsEntry.getColumn56());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn57(),
			newManyColumnsEntry.getColumn57());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn58(),
			newManyColumnsEntry.getColumn58());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn59(),
			newManyColumnsEntry.getColumn59());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn60(),
			newManyColumnsEntry.getColumn60());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn61(),
			newManyColumnsEntry.getColumn61());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn62(),
			newManyColumnsEntry.getColumn62());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn63(),
			newManyColumnsEntry.getColumn63());
		Assert.assertEquals(
			existingManyColumnsEntry.getColumn64(),
			newManyColumnsEntry.getColumn64());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ManyColumnsEntry newManyColumnsEntry = addManyColumnsEntry();

		ManyColumnsEntry existingManyColumnsEntry =
			_persistence.findByPrimaryKey(newManyColumnsEntry.getPrimaryKey());

		Assert.assertEquals(existingManyColumnsEntry, newManyColumnsEntry);
	}

	@Test(expected = NoSuchManyColumnsEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ManyColumnsEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ManyColumnsEntry", "manyColumnsEntryId", true, "column1", true,
			"column2", true, "column3", true, "column4", true, "column5", true,
			"column6", true, "column7", true, "column8", true, "column9", true,
			"column10", true, "column11", true, "column12", true, "column13",
			true, "column14", true, "column15", true, "column16", true,
			"column17", true, "column18", true, "column19", true, "column20",
			true, "column21", true, "column22", true, "column23", true,
			"column24", true, "column25", true, "column26", true, "column27",
			true, "column28", true, "column29", true, "column30", true,
			"column31", true, "column32", true, "column33", true, "column34",
			true, "column35", true, "column36", true, "column37", true,
			"column38", true, "column39", true, "column40", true, "column41",
			true, "column42", true, "column43", true, "column44", true,
			"column45", true, "column46", true, "column47", true, "column48",
			true, "column49", true, "column50", true, "column51", true,
			"column52", true, "column53", true, "column54", true, "column55",
			true, "column56", true, "column57", true, "column58", true,
			"column59", true, "column60", true, "column61", true, "column62",
			true, "column63", true, "column64", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ManyColumnsEntry newManyColumnsEntry = addManyColumnsEntry();

		ManyColumnsEntry existingManyColumnsEntry =
			_persistence.fetchByPrimaryKey(newManyColumnsEntry.getPrimaryKey());

		Assert.assertEquals(existingManyColumnsEntry, newManyColumnsEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ManyColumnsEntry missingManyColumnsEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingManyColumnsEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ManyColumnsEntry newManyColumnsEntry1 = addManyColumnsEntry();
		ManyColumnsEntry newManyColumnsEntry2 = addManyColumnsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newManyColumnsEntry1.getPrimaryKey());
		primaryKeys.add(newManyColumnsEntry2.getPrimaryKey());

		Map<Serializable, ManyColumnsEntry> manyColumnsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, manyColumnsEntries.size());
		Assert.assertEquals(
			newManyColumnsEntry1,
			manyColumnsEntries.get(newManyColumnsEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newManyColumnsEntry2,
			manyColumnsEntries.get(newManyColumnsEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ManyColumnsEntry> manyColumnsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(manyColumnsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ManyColumnsEntry newManyColumnsEntry = addManyColumnsEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newManyColumnsEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ManyColumnsEntry> manyColumnsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, manyColumnsEntries.size());
		Assert.assertEquals(
			newManyColumnsEntry,
			manyColumnsEntries.get(newManyColumnsEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ManyColumnsEntry> manyColumnsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(manyColumnsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ManyColumnsEntry newManyColumnsEntry = addManyColumnsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newManyColumnsEntry.getPrimaryKey());

		Map<Serializable, ManyColumnsEntry> manyColumnsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, manyColumnsEntries.size());
		Assert.assertEquals(
			newManyColumnsEntry,
			manyColumnsEntries.get(newManyColumnsEntry.getPrimaryKey()));
	}

	protected ManyColumnsEntry addManyColumnsEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ManyColumnsEntry manyColumnsEntry = _persistence.create(pk);

		manyColumnsEntry.setColumn1(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn2(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn3(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn4(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn5(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn6(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn7(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn8(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn9(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn10(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn11(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn12(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn13(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn14(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn15(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn16(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn17(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn18(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn19(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn20(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn21(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn22(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn23(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn24(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn25(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn26(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn27(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn28(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn29(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn30(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn31(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn32(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn33(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn34(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn35(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn36(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn37(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn38(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn39(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn40(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn41(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn42(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn43(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn44(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn45(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn46(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn47(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn48(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn49(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn50(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn51(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn52(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn53(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn54(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn55(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn56(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn57(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn58(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn59(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn60(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn61(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn62(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn63(RandomTestUtil.nextInt());

		manyColumnsEntry.setColumn64(RandomTestUtil.nextInt());

		_manyColumnsEntries.add(_persistence.update(manyColumnsEntry));

		return manyColumnsEntry;
	}

	private List<ManyColumnsEntry> _manyColumnsEntries =
		new ArrayList<ManyColumnsEntry>();
	private ManyColumnsEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
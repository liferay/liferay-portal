/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.closure.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.closure.CTClosure;
import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.change.tracking.sample.service.CTSChildLocalService;
import com.liferay.change.tracking.sample.service.CTSParentLocalService;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.test.util.CTSampleTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class CTClosureFactoryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		CTSampleTestUtil.reset();

		_db = DBManagerUtil.getDB();

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTClosureFactoryImplTest.class.getSimpleName(),
			StringPool.BLANK);
	}

	@After
	public void tearDown() throws Exception {
		CTSampleTestUtil.reset();

		_ctCollectionLocalService.deleteCTCollection(_ctCollection);
	}

	@Test
	public void testAddClosureCyclesNoParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_ADDITION, false);
	}

	@Test
	public void testAddClosureCyclesParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_ADDITION, true);
	}

	@Test
	public void testAddClosureNoCyclesNoParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_ADDITION, false);
	}

	@Test
	public void testAddClosureNoCyclesParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_ADDITION, true);
	}

	@Test
	public void testDeleteClosureCyclesNoParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_DELETION, false);
	}

	@Test
	public void testDeleteClosureCyclesParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_DELETION, true);
	}

	@Test
	public void testDeleteClosureNoCyclesNoParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_DELETION, false);
	}

	@Test
	public void testDeleteClosureNoCyclesParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_DELETION, true);
	}

	@Test
	public void testEmptyClosure() {
		CTClosure ctClosure = _ctClosureFactory.create(
			_ctCollection.getCtCollectionId());

		Map<Long, List<Long>> rootPKsMap = ctClosure.getRootPKsMap();

		Assert.assertTrue(rootPKsMap.toString(), rootPKsMap.isEmpty());
	}

	@Test
	public void testModifyClosureCyclesNoParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_MODIFICATION, false);
	}

	@Test
	public void testModifyClosureCyclesParents() throws Exception {
		_testClosureCycles(CTConstants.CT_CHANGE_TYPE_MODIFICATION, true);
	}

	@Test
	public void testModifyClosureNoCyclesNoParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_MODIFICATION, false);
	}

	@Test
	public void testModifyClosureNoCyclesParents() throws Exception {
		_testClosureNoCycles(CTConstants.CT_CHANGE_TYPE_MODIFICATION, true);
	}

	private void _addCTEntry(CTModel<?> ctModel, int changeType)
		throws Exception {

		_ctEntryLocalService.addCTEntry(
			null, _ctCollection.getCtCollectionId(),
			_classNameLocalService.getClassNameId(ctModel.getModelClass()),
			ctModel, TestPropsValues.getUserId(), changeType);
	}

	private void _assertMapContent(
		Map<Long, List<Long>> expectedMap, Map<Long, List<Long>> actualMap) {

		for (Map.Entry<Long, List<Long>> entry : actualMap.entrySet()) {
			List<Long> primaryKeys = entry.getValue();

			primaryKeys.sort(null);
		}

		Assert.assertEquals(expectedMap, actualMap);
	}

	private void _testClosureCycles(int changeType, boolean addParents)
		throws Exception {

		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (1, 2)");
		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (2, 1)");
		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (3, 1)");

		if (addParents) {
			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 11, 0, 'p1')");

			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 12, 2, 'p2')");

			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 13, 2, 'p3')");

			if (changeType != CTConstants.CT_CHANGE_TYPE_ADDITION) {
				_db.runSQL(
					"insert into CTSParent (ctCollectionId, ctsParentId, " +
						"ctsGrandParentId, name) values (0, 14, 3, 'p4')");
			}

			if (changeType != CTConstants.CT_CHANGE_TYPE_DELETION) {
				_db.runSQL(
					StringBundler.concat(
						"insert into CTSParent (ctCollectionId, ctsParentId, ",
						"ctsGrandParentId, name) values (",
						_ctCollection.getCtCollectionId(), ", 14, 3, 'p4')"));
			}

			_addCTEntry(_ctsParentLocalService.createCTSParent(14), changeType);
		}

		_db.runSQL(
			StringBundler.concat(
				"insert into CTSChild (ctCollectionId, ctsChildId, ",
				"ctsGrandParentId, parentCTSChildId, ctsParentName) values ",
				"(0, 21, 2, 0, 'p1')"));

		if (changeType != CTConstants.CT_CHANGE_TYPE_ADDITION) {
			_db.runSQL(
				StringBundler.concat(
					"insert into CTSChild (ctCollectionId, ctsChildId, ",
					"ctsGrandParentId, parentCTSChildId, ctsParentName) ",
					"values (0, 22, 2, 21, 'p2')"));
		}

		if (changeType != CTConstants.CT_CHANGE_TYPE_DELETION) {
			_db.runSQL(
				StringBundler.concat(
					"insert into CTSChild (ctCollectionId, ctsChildId, ",
					"ctsGrandParentId, parentCTSChildId, ctsParentName) ",
					"values (", _ctCollection.getCtCollectionId(),
					", 22, 2, 21, 'p2')"));
		}

		_addCTEntry(_ctsChildLocalService.createCTSChild(22), changeType);

		_db.runSQL(
			StringBundler.concat(
				"insert into CTSChild (ctCollectionId, ctsChildId, ",
				"ctsGrandParentId, parentCTSChildId, ctsParentName) values ",
				"(0, 23, 2, 21, 'p3')"));

		CTClosure ctClosure = _ctClosureFactory.create(
			_ctCollection.getCtCollectionId());

		long ctsGrandParentClassNameId = _classNameLocalService.getClassNameId(
			CTSGrandParent.class);

		Assert.assertEquals(
			Collections.singletonMap(
				ctsGrandParentClassNameId, Collections.singletonList(1L)),
			ctClosure.getRootPKsMap());

		long ctsChildClassNameId = _classNameLocalService.getClassNameId(
			CTSChild.class);

		if (addParents) {
			_assertMapContent(
				Collections.singletonMap(
					ctsGrandParentClassNameId, Arrays.asList(2L, 3L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 1L));

			long ctsParentClassNameId = _classNameLocalService.getClassNameId(
				CTSParent.class);

			Assert.assertEquals(
				HashMapBuilder.put(
					ctsChildClassNameId, Collections.singletonList(21L)
				).put(
					ctsParentClassNameId, Collections.singletonList(12L)
				).build(),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 2L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsParentClassNameId, Collections.singletonList(14L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 3L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsChildClassNameId, Collections.singletonList(22L)),
				ctClosure.getChildPKsMap(ctsParentClassNameId, 12L));

			Assert.assertEquals(
				Collections.emptyMap(),
				ctClosure.getChildPKsMap(ctsParentClassNameId, 14L));
		}
		else {
			Assert.assertEquals(
				Collections.singletonMap(
					ctsGrandParentClassNameId, Collections.singletonList(2L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 1L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsChildClassNameId, Collections.singletonList(21L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 2L));
		}

		Assert.assertEquals(
			Collections.singletonMap(
				ctsChildClassNameId, Collections.singletonList(22L)),
			ctClosure.getChildPKsMap(ctsChildClassNameId, 21L));

		Assert.assertEquals(
			Collections.emptyMap(),
			ctClosure.getChildPKsMap(ctsChildClassNameId, 22L));
	}

	private void _testClosureNoCycles(int changeType, boolean addParents)
		throws Exception {

		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (1, 0)");
		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (2, 1)");
		_db.runSQL(
			"insert into CTSGrandParent (ctsGrandParentId, " +
				"parentCTSGrandParentId) values (3, 1)");

		if (addParents) {
			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 11, 0, 'p1')");

			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 12, 2, 'p2')");

			_db.runSQL(
				"insert into CTSParent (ctCollectionId, ctsParentId, " +
					"ctsGrandParentId, name) values (0, 13, 2, 'p3')");

			if (changeType != CTConstants.CT_CHANGE_TYPE_ADDITION) {
				_db.runSQL(
					"insert into CTSParent (ctCollectionId, ctsParentId, " +
						"ctsGrandParentId, name) values (0, 14, 3, 'p4')");
			}

			if (changeType != CTConstants.CT_CHANGE_TYPE_DELETION) {
				_db.runSQL(
					StringBundler.concat(
						"insert into CTSParent (ctCollectionId, ctsParentId, ",
						"ctsGrandParentId, name) values (",
						_ctCollection.getCtCollectionId(), ", 14, 3, 'p4')"));
			}

			_addCTEntry(_ctsParentLocalService.createCTSParent(14), changeType);
		}

		_db.runSQL(
			StringBundler.concat(
				"insert into CTSChild (ctCollectionId, ctsChildId, ",
				"ctsGrandParentId, parentCTSChildId, ctsParentName) values ",
				"(0, 21, 2, 0, 'p2')"));

		if (changeType != CTConstants.CT_CHANGE_TYPE_ADDITION) {
			_db.runSQL(
				StringBundler.concat(
					"insert into CTSChild (ctCollectionId, ctsChildId, ",
					"ctsGrandParentId, parentCTSChildId, ctsParentName) ",
					"values (0, 22, 2, 21, 'p2')"));
		}

		if (changeType != CTConstants.CT_CHANGE_TYPE_DELETION) {
			_db.runSQL(
				StringBundler.concat(
					"insert into CTSChild (ctCollectionId, ctsChildId, ",
					"ctsGrandParentId, parentCTSChildId, ctsParentName) ",
					"values (", _ctCollection.getCtCollectionId(),
					", 22, 2, 21, 'p2')"));
		}

		_addCTEntry(_ctsChildLocalService.createCTSChild(22), changeType);

		_db.runSQL(
			StringBundler.concat(
				"insert into CTSChild (ctCollectionId, ctsChildId, ",
				"ctsGrandParentId, parentCTSChildId, ctsParentName) values ",
				"(0, 23, 2, 21, 'p3')"));

		CTClosure ctClosure = _ctClosureFactory.create(
			_ctCollection.getCtCollectionId());

		long ctsGrandParentClassNameId = _classNameLocalService.getClassNameId(
			CTSGrandParent.class);

		Assert.assertEquals(
			Collections.singletonMap(
				ctsGrandParentClassNameId, Collections.singletonList(1L)),
			ctClosure.getRootPKsMap());

		long ctsChildClassNameId = _classNameLocalService.getClassNameId(
			CTSChild.class);

		if (addParents) {
			_assertMapContent(
				Collections.singletonMap(
					ctsGrandParentClassNameId, Arrays.asList(2L, 3L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 1L));

			long ctsParentClassNameId = _classNameLocalService.getClassNameId(
				CTSParent.class);

			Assert.assertEquals(
				Collections.singletonMap(
					ctsParentClassNameId, Collections.singletonList(12L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 2L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsParentClassNameId, Collections.singletonList(14L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 3L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsChildClassNameId, Collections.singletonList(21L)),
				ctClosure.getChildPKsMap(ctsParentClassNameId, 12L));

			Assert.assertEquals(
				Collections.emptyMap(),
				ctClosure.getChildPKsMap(ctsParentClassNameId, 14L));
		}
		else {
			Assert.assertEquals(
				Collections.singletonMap(
					ctsGrandParentClassNameId, Collections.singletonList(2L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 1L));

			Assert.assertEquals(
				Collections.singletonMap(
					ctsChildClassNameId, Collections.singletonList(21L)),
				ctClosure.getChildPKsMap(ctsGrandParentClassNameId, 2L));
		}

		Assert.assertEquals(
			Collections.singletonMap(
				ctsChildClassNameId, Collections.singletonList(22L)),
			ctClosure.getChildPKsMap(ctsChildClassNameId, 21L));

		Assert.assertEquals(
			Collections.emptyMap(),
			ctClosure.getChildPKsMap(ctsChildClassNameId, 22L));
	}

	@Inject
	private static CTClosureFactory _ctClosureFactory;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTSChildLocalService _ctsChildLocalService;

	@Inject
	private CTSParentLocalService _ctsParentLocalService;

	private DB _db;

}
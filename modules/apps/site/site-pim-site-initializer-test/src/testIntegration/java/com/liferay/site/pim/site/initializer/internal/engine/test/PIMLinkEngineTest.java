/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.engine.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.engine.PIMLinkEngine;
import com.liferay.site.pim.site.initializer.test.util.PIMBaseSKUTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-96666")
@RunWith(Arquillian.class)
public class PIMLinkEngineTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		PIMTestUtil.getOrAddGroup();
	}

	@Test
	public void testAddPIMLinks() throws Exception {
		_testAddPIMLinks();
		_testAddPIMLinksWithExistingCluster();
		_testAddPIMLinksWithInvalidGroupId();
		_testAddPIMLinksWithInvalidType();
	}

	@Test
	public void testDeletePIMLink() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			objectEntry1, Collections.singletonList(objectEntry2), _TYPE);

		Assert.assertNotNull(_getClusterKey(objectEntry2));

		_pimLinkEngine.deletePIMLink(objectEntry2, _TYPE);

		Assert.assertNotNull(_getClusterKey(objectEntry1));
		Assert.assertNull(_getClusterKey(objectEntry2));
	}

	private String _getClusterKey(ObjectEntry objectEntry) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					TestPropsValues.getCompanyId());

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				objectEntry.getGroupId(), objectEntry.getCompanyId(), 0,
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					StringBundler.concat(
						"sourceClassExternalReferenceCode eq '",
						objectEntry.getExternalReferenceCode(),
						"' and type eq '", _TYPE, "'"),
					objectDefinition),
				null, 0, 1, null);

		if (valuesList.isEmpty()) {
			return null;
		}

		return MapUtil.getString(valuesList.get(0), "clusterKey");
	}

	private void _testAddPIMLinks() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			objectEntry1, Collections.singletonList(objectEntry2), _TYPE);

		String clusterKey = _getClusterKey(objectEntry1);

		Assert.assertNotNull(clusterKey);
		Assert.assertEquals(clusterKey, _getClusterKey(objectEntry2));
	}

	private void _testAddPIMLinksWithExistingCluster() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			objectEntry1, Collections.singletonList(objectEntry2), _TYPE);

		ObjectEntry objectEntry3 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry4 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		_pimLinkEngine.addPIMLinks(
			objectEntry3, Collections.singletonList(objectEntry4), _TYPE);

		Assert.assertNotEquals(
			_getClusterKey(objectEntry1), _getClusterKey(objectEntry3));

		_pimLinkEngine.addPIMLinks(
			objectEntry1, Collections.singletonList(objectEntry3), _TYPE);

		String clusterKey = _getClusterKey(objectEntry1);

		Assert.assertEquals(clusterKey, _getClusterKey(objectEntry2));
		Assert.assertEquals(clusterKey, _getClusterKey(objectEntry3));
		Assert.assertEquals(clusterKey, _getClusterKey(objectEntry4));
	}

	private void _testAddPIMLinksWithInvalidGroupId() throws Exception {
		DepotEntry depotEntry1 = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry1.getGroupId());

		DepotEntry depotEntry2 = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry2.getGroupId());

		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> _pimLinkEngine.addPIMLinks(
				objectEntry1, Collections.singletonList(objectEntry2), _TYPE));
	}

	private void _testAddPIMLinksWithInvalidType() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> _pimLinkEngine.addPIMLinks(
				objectEntry1, Collections.singletonList(objectEntry2),
				RandomTestUtil.randomString()));
	}

	private static final String _TYPE = "variant";

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private PIMLinkEngine _pimLinkEngine;

}
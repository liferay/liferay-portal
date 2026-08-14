/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.headless.pim.client.dto.v1_0.Link;
import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMBaseSKUTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class LinkResourceTest extends BaseLinkResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PIMTestUtil.getOrAddGroup();
	}

	@Override
	@Test
	public void testDeleteScopeScopeKeyLink() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		linkResource.postScopeScopeKeyLink(
			String.valueOf(depotEntry.getGroupId()),
			_toLink(
				_TYPE, objectEntry1, Collections.singletonList(objectEntry2)));

		Assert.assertNotNull(
			_getClusterKey(
				depotEntry.getGroupId(),
				objectEntry2.getExternalReferenceCode()));

		linkResource.deleteScopeScopeKeyLink(
			String.valueOf(depotEntry.getGroupId()),
			objectEntry2.getModelClassName(),
			objectEntry2.getExternalReferenceCode(), _TYPE);

		Assert.assertNotNull(
			_getClusterKey(
				depotEntry.getGroupId(),
				objectEntry1.getExternalReferenceCode()));
		Assert.assertNull(
			_getClusterKey(
				depotEntry.getGroupId(),
				objectEntry2.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPostScopeScopeKeyLink() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());
		ObjectEntry objectEntry2 = PIMBaseSKUTestUtil.addPIMBaseSKUObjectEntry(
			depotEntry.getGroupId());

		linkResource.postScopeScopeKeyLink(
			String.valueOf(depotEntry.getGroupId()),
			_toLink(
				_TYPE, objectEntry1, Collections.singletonList(objectEntry2)));

		String clusterKey = _getClusterKey(
			depotEntry.getGroupId(), objectEntry1.getExternalReferenceCode());

		Assert.assertNotNull(clusterKey);
		Assert.assertEquals(
			clusterKey,
			_getClusterKey(
				depotEntry.getGroupId(),
				objectEntry2.getExternalReferenceCode()));
	}

	private String _getClusterKey(long groupId, String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					TestPropsValues.getCompanyId());

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				groupId, TestPropsValues.getCompanyId(), 0,
				objectDefinition.getObjectDefinitionId(), null, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (Map<String, Serializable> values : valuesList) {
			if (Objects.equals(
					externalReferenceCode,
					MapUtil.getString(
						values, "sourceClassExternalReferenceCode")) &&
				Objects.equals(_TYPE, MapUtil.getString(values, "type"))) {

				return MapUtil.getString(values, "clusterKey");
			}
		}

		return null;
	}

	private Link _toLink(
		String linkType, ObjectEntry sourceObjectEntry,
		List<ObjectEntry> targetObjectEntries) {

		return new Link() {
			{
				setSourceLinkReference(_toLinkReference(sourceObjectEntry));
				setTargetLinkReferences(
					TransformUtil.transformToArray(
						targetObjectEntries,
						LinkResourceTest.this::_toLinkReference,
						LinkReference.class));
				setType(() -> linkType);
			}
		};
	}

	private LinkReference _toLinkReference(ObjectEntry objectEntry) {
		return new LinkReference() {
			{
				setClassName(objectEntry.getModelClassName());
				setExternalReferenceCode(
					objectEntry.getExternalReferenceCode());
			}
		};
	}

	private static final String _TYPE = "variant";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}
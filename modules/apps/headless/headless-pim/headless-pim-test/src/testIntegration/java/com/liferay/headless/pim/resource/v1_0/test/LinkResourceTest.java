/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.pim.client.dto.v1_0.Link;
import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectEntryFolderConstants;
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
		long groupId = _addSpaceDepotEntryGroupId();

		ObjectEntry objectEntry1 = _addPIMBaseSKUObjectEntry(groupId);
		ObjectEntry objectEntry2 = _addPIMBaseSKUObjectEntry(groupId);

		linkResource.postScopeScopeKeyLink(
			String.valueOf(groupId),
			_toLink(
				objectEntry1, Collections.singletonList(objectEntry2), _TYPE));

		Assert.assertNotNull(
			_getClusterKey(groupId, objectEntry2.getExternalReferenceCode()));

		linkResource.deleteScopeScopeKeyLink(
			String.valueOf(groupId), objectEntry2.getModelClassName(),
			objectEntry2.getExternalReferenceCode(), _TYPE);

		Assert.assertNotNull(
			_getClusterKey(groupId, objectEntry1.getExternalReferenceCode()));
		Assert.assertNull(
			_getClusterKey(groupId, objectEntry2.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPostScopeScopeKeyLink() throws Exception {
		long groupId = _addSpaceDepotEntryGroupId();

		ObjectEntry objectEntry1 = _addPIMBaseSKUObjectEntry(groupId);
		ObjectEntry objectEntry2 = _addPIMBaseSKUObjectEntry(groupId);

		linkResource.postScopeScopeKeyLink(
			String.valueOf(groupId),
			_toLink(
				objectEntry1, Collections.singletonList(objectEntry2), _TYPE));

		String clusterKey = _getClusterKey(
			groupId, objectEntry1.getExternalReferenceCode());

		Assert.assertNotNull(clusterKey);
		Assert.assertEquals(
			clusterKey,
			_getClusterKey(groupId, objectEntry2.getExternalReferenceCode()));
	}

	private ObjectEntry _addPIMBaseSKUObjectEntry(long groupId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					TestPropsValues.getCompanyId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					groupId, TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"code", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private long _addSpaceDepotEntryGroupId() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		return depotEntry.getGroupId();
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
		ObjectEntry sourceObjectEntry, List<ObjectEntry> targetObjectEntries,
		String type) {

		Link link = new Link();

		link.setSourceLinkReference(_toLinkReference(sourceObjectEntry));
		link.setTargetLinkReferences(
			TransformUtil.transformToArray(
				targetObjectEntries, this::_toLinkReference,
				LinkReference.class));
		link.setType(type);

		return link;
	}

	private LinkReference _toLinkReference(ObjectEntry objectEntry) {
		LinkReference linkReference = new LinkReference();

		linkReference.setClassName(objectEntry.getModelClassName());
		linkReference.setExternalReferenceCode(
			objectEntry.getExternalReferenceCode());

		return linkReference;
	}

	private static final String _TYPE = "variant";

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}
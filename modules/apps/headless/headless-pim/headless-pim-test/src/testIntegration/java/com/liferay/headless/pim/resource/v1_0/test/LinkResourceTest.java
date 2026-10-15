/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.headless.pim.client.dto.v1_0.Link;
import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.headless.pim.client.problem.Problem;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMBasicProductTestUtil;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
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

		ObjectEntry objectEntry1 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				depotEntry.getGroupId());
		ObjectEntry objectEntry2 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
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

		ObjectEntry objectEntry1 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				depotEntry.getGroupId());
		ObjectEntry objectEntry2 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
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

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "title")),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			Boolean.TRUE.toString());

		ObjectEntry objectEntry3 = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null, new HashMap<>(),
			ServiceContextTestUtil.getServiceContext(depotEntry.getGroupId()));

		LinkReference linkReference = _toLinkReference(objectEntry3);

		linkReference.setClassName(objectEntry1.getModelClassName());

		try {
			linkResource.postScopeScopeKeyLink(
				String.valueOf(depotEntry.getGroupId()),
				new Link() {
					{
						setSourceLinkReference(_toLinkReference(objectEntry1));
						setTargetLinkReferences(
							new LinkReference[] {linkReference});
						setType(() -> _TYPE);
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNull(
				_getClusterKey(
					depotEntry.getGroupId(),
					objectEntry3.getExternalReferenceCode()));
		}

		linkResource.postScopeScopeKeyLink(
			String.valueOf(depotEntry.getGroupId()),
			_toLink(
				_TYPE, objectEntry1, Collections.singletonList(objectEntry3)));

		Assert.assertEquals(
			clusterKey,
			_getClusterKey(
				depotEntry.getGroupId(),
				objectEntry3.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPutScopeScopeKeyLinkPrimary() throws Exception {
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntry objectEntry1 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				depotEntry.getGroupId());
		ObjectEntry objectEntry2 =
			PIMBasicProductTestUtil.addPIMBasicProductObjectEntry(
				depotEntry.getGroupId());

		linkResource.postScopeScopeKeyLink(
			String.valueOf(depotEntry.getGroupId()),
			_toLink(
				_TYPE, objectEntry1, Collections.singletonList(objectEntry2)));

		linkResource.putScopeScopeKeyLinkPrimary(
			String.valueOf(depotEntry.getGroupId()),
			objectEntry2.getModelClassName(),
			objectEntry2.getExternalReferenceCode(), _TYPE);

		Assert.assertTrue(
			_isPrimary(
				depotEntry.getGroupId(),
				objectEntry2.getExternalReferenceCode()));
	}

	private Map<String, Serializable> _fetchValues(
			long groupId, String externalReferenceCode)
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

				return values;
			}
		}

		return null;
	}

	private String _getClusterKey(long groupId, String externalReferenceCode)
		throws Exception {

		Map<String, Serializable> values = _fetchValues(
			groupId, externalReferenceCode);

		if (values == null) {
			return null;
		}

		return MapUtil.getString(values, "clusterKey");
	}

	private boolean _isPrimary(long groupId, String externalReferenceCode)
		throws Exception {

		Map<String, Serializable> values = _fetchValues(
			groupId, externalReferenceCode);

		if (values == null) {
			return false;
		}

		return MapUtil.getBoolean(values, "primary");
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
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}
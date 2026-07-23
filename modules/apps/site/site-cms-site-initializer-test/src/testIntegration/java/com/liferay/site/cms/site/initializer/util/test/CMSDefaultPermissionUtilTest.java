/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.io.Serializable;

import java.util.ArrayList;
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
@RunWith(Arquillian.class)
public class CMSDefaultPermissionUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddOrUpdateObjectEntry() throws Exception {
		Group group = _depotEntry.getGroup();
		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 =
			CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
				RandomTestUtil.randomString(), group.getCompanyId(),
				TestPropsValues.getUserId(), externalReferenceCode,
				_depotEntry.getModelClassName(),
				JSONUtil.put(
					"L_CMS_BASIC_WEB_CONTENT",
					JSONUtil.putAll(ActionKeys.VIEW)),
				group.getGroupId(), StringPool.BLANK);

		Map<String, Serializable> values = objectEntry1.getValues();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			String.valueOf(values.getOrDefault("defaultPermissions", "{}")));

		JSONArray jsonArray = jsonObject.getJSONArray(
			"L_CMS_BASIC_WEB_CONTENT");

		Assert.assertEquals(ActionKeys.VIEW, jsonArray.getString(0));
		Assert.assertEquals(1, jsonArray.length());

		ObjectEntry objectEntry2 =
			CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
				objectEntry1.getExternalReferenceCode(), group.getCompanyId(),
				TestPropsValues.getUserId(), externalReferenceCode,
				_depotEntry.getModelClassName(),
				JSONUtil.put(
					"L_CMS_BASIC_WEB_CONTENT",
					JSONUtil.putAll(ActionKeys.UPDATE, ActionKeys.VIEW)),
				group.getGroupId(), StringPool.BLANK);

		Assert.assertEquals(
			objectEntry1.getObjectEntryId(), objectEntry2.getObjectEntryId());

		values = objectEntry2.getValues();

		jsonObject = JSONFactoryUtil.createJSONObject(
			String.valueOf(values.getOrDefault("defaultPermissions", "{}")));

		jsonArray = jsonObject.getJSONArray("L_CMS_BASIC_WEB_CONTENT");

		Assert.assertEquals(ActionKeys.UPDATE, jsonArray.getString(0));
		Assert.assertEquals(ActionKeys.VIEW, jsonArray.getString(1));
		Assert.assertEquals(2, jsonArray.length());
	}

	@Test
	public void testFetchObjectEntry() throws Exception {
		Group group = _depotEntry.getGroup();

		ObjectEntry objectEntry = CMSDefaultPermissionUtil.fetchObjectEntry(
			group.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), _depotEntry.getModelClassName(),
			_filterFactory);

		Assert.assertNull(objectEntry);

		String externalReferenceCode = RandomTestUtil.randomString();

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			RandomTestUtil.randomString(), group.getCompanyId(),
			TestPropsValues.getUserId(), externalReferenceCode,
			_depotEntry.getModelClassName(),
			JSONUtil.put(
				"L_CMS_BASIC_WEB_CONTENT",
				JSONUtil.putAll(ActionKeys.UPDATE, ActionKeys.VIEW)),
			group.getGroupId(), StringPool.BLANK);

		Assert.assertNotNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				group.getCompanyId(), TestPropsValues.getUserId(),
				externalReferenceCode, _depotEntry.getModelClassName(),
				_filterFactory));
	}

	@Test
	public void testGetJSONObject() throws Exception {
		Group group = _depotEntry.getGroup();
		String externalReferenceCode = RandomTestUtil.randomString();

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			RandomTestUtil.randomString(), group.getCompanyId(),
			TestPropsValues.getUserId(), externalReferenceCode,
			_depotEntry.getModelClassName(),
			JSONUtil.put(
				"L_CMS_BASIC_WEB_CONTENT",
				JSONUtil.putAll(ActionKeys.UPDATE, ActionKeys.VIEW)),
			group.getGroupId(), StringPool.BLANK);

		JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			group.getCompanyId(), TestPropsValues.getUserId(),
			externalReferenceCode, _depotEntry.getModelClassName(),
			_filterFactory);

		JSONArray jsonArray = jsonObject.getJSONArray(
			"L_CMS_BASIC_WEB_CONTENT");

		Assert.assertEquals(ActionKeys.UPDATE, jsonArray.getString(0));
		Assert.assertEquals(ActionKeys.VIEW, jsonArray.getString(1));
		Assert.assertEquals(2, jsonArray.length());
	}

	@Test
	@TestInfo("LPD-92654")
	public void testGetJSONObjectWhenSpaceCreatedWithGroupExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"groupExternalReferenceCode", externalReferenceCode);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, serviceContext);

		_depotEntries.add(depotEntry);

		Group group = depotEntry.getGroup();

		Assert.assertEquals(
			externalReferenceCode, group.getExternalReferenceCode());

		Assert.assertFalse(
			JSONUtil.isEmpty(
				CMSDefaultPermissionUtil.getJSONObject(
					group.getCompanyId(), group.getCreatorUserId(),
					group.getExternalReferenceCode(),
					depotEntry.getModelClassName(), _filterFactory)));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private GroupLocalService _groupLocalService;

}
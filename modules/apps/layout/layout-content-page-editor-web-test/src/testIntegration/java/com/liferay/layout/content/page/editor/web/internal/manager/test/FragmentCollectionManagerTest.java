/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class FragmentCollectionManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-98540")
	public void testGetGroupIds() throws Exception {
		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			assetLibraryDepotEntry.getDepotEntryId(), _group.getGroupId());

		DepotEntry designLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			designLibraryDepotEntry.getDepotEntryId(), _group.getGroupId());

		_addDepotEntry(DepotConstants.TYPE_DESIGN_LIBRARY);

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.FALSE.toString())) {

			_testGetGroupIds(
				new long[] {
					_company.getGroupId(), _group.getGroupId(),
					CompanyConstants.SYSTEM
				});
		}

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.TRUE.toString())) {

			_testGetGroupIds(
				new long[] {
					_company.getGroupId(), designLibraryDepotEntry.getGroupId(),
					_group.getGroupId(), CompanyConstants.SYSTEM
				});
		}
	}

	@Test
	@TestInfo("LPS-162848")
	public void testGetLayoutElementMapsListMapWithoutApprovedObjectDefinition() {
		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			ReflectionTestUtil.invoke(
				_fragmentCollectionManager, "getLayoutElementMapsListMap",
				new Class<?>[] {PermissionChecker.class},
				PermissionThreadLocal.getPermissionChecker());

		Assert.assertFalse(layoutElementMapsListMap.containsKey("INPUTS"));
	}

	@Test
	@TestInfo("LPS-162848")
	public void testGetLayoutElementMapsListMapWithoutPermissions()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")),
				false);

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
				ReflectionTestUtil.invoke(
					_fragmentCollectionManager, "getLayoutElementMapsListMap",
					new Class<?>[] {PermissionChecker.class},
					PermissionThreadLocal.getPermissionChecker());

			Assert.assertFalse(layoutElementMapsListMap.containsKey("INPUTS"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	@TestInfo("LPS-162848")
	public void testGetLayoutElementMapsListMapWithPermissions()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")),
				false);

		try {
			Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
				ReflectionTestUtil.invoke(
					_fragmentCollectionManager, "getLayoutElementMapsListMap",
					new Class<?>[] {PermissionChecker.class},
					PermissionThreadLocal.getPermissionChecker());

			Assert.assertTrue(layoutElementMapsListMap.containsKey("INPUTS"));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	@TestInfo("LPD-98540")
	public void testGetScopeMap() throws Exception {
		DepotEntry designLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);

		_testGetScopeMap("design-library", designLibraryDepotEntry.getGroup());

		_testGetScopeMap("global", _company.getGroup());
		_testGetScopeMap("site", _group);

		Assert.assertNull(
			_getScopeMap(CompanyConstants.SYSTEM, new HashMap<>()));
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), type,
			ServiceContextTestUtil.getServiceContext());
	}

	private Map<String, Object> _getScopeMap(
		long groupId, Map<Long, Map<String, Object>> scopeMaps) {

		return ReflectionTestUtil.invoke(
			_fragmentCollectionManager, "_getScopeMap",
			new Class<?>[] {long.class, long.class, Locale.class, Map.class},
			_company.getGroupId(), groupId, LocaleUtil.getDefault(), scopeMaps);
	}

	private void _testGetGroupIds(long[] expectedGroupIds) throws Exception {
		long[] groupIds = ReflectionTestUtil.invoke(
			_fragmentCollectionManager, "getGroupIds",
			new Class<?>[] {long.class, long.class, long.class},
			_group.getCompanyId(), _company.getGroupId(), _group.getGroupId());

		Assert.assertTrue(
			Arrays.toString(groupIds),
			ArrayUtil.containsAll(groupIds, expectedGroupIds));
		Assert.assertEquals(
			Arrays.toString(groupIds), expectedGroupIds.length,
			groupIds.length);
	}

	private void _testGetScopeMap(String expectedType, Group group)
		throws Exception {

		Map<Long, Map<String, Object>> scopeMaps = new HashMap<>();

		Map<String, Object> scopeMap = _getScopeMap(
			group.getGroupId(), scopeMaps);

		Assert.assertEquals(
			String.valueOf(group.getGroupId()), scopeMap.get("id"));
		Assert.assertEquals(
			group.getDescriptiveName(LocaleUtil.getDefault()),
			scopeMap.get("label"));
		Assert.assertEquals(expectedType, scopeMap.get("type"));
		Assert.assertSame(
			scopeMap, _getScopeMap(group.getGroupId(), scopeMaps));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager",
		type = Inject.NoType.class
	)
	private Object _fragmentCollectionManager;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.list.type.client.dto.v1_0.Creator;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionResourceTest
	extends BaseListTypeDefinitionResourceTestCase {

	@Override
	@Test
	public void testGetListTypeDefinitionsPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeDefinition1, listTypeDefinition2) -> {
				if (BeanTestUtil.hasProperty(
						listTypeDefinition1, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeDefinition1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						listTypeDefinition2, entityField.getName())) {

					BeanTestUtil.setProperty(
						listTypeDefinition2, entityField.getName(), 1);
				}
			});
	}

	@Override
	@Test
	public void testPatchListTypeDefinition() throws Exception {
		super.testPatchListTypeDefinition();

		ListTypeDefinition postListTypeDefinition =
			testPatchListTypeDefinition_addListTypeDefinition();

		ListTypeDefinition patchListTypeDefinition =
			listTypeDefinitionResource.patchListTypeDefinition(
				postListTypeDefinition.getId(),
				new ListTypeDefinition() {
					{
						listTypeEntries = ArrayUtil.append(
							postListTypeDefinition.getListTypeEntries(),
							new ListTypeEntry() {
								{
									key = RandomTestUtil.randomString();
									name_i18n =
										RandomTestUtil.
											randomLanguageIdStringMap();
								}
							});
					}
				});

		ListTypeDefinition getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				patchListTypeDefinition.getId());

		ListTypeEntry[] listTypeEntries =
			getListTypeDefinition.getListTypeEntries();

		Assert.assertEquals(
			Arrays.toString(listTypeEntries), 2, listTypeEntries.length);

		String patchedName = RandomTestUtil.randomString();

		patchListTypeDefinition =
			listTypeDefinitionResource.patchListTypeDefinition(
				postListTypeDefinition.getId(),
				new ListTypeDefinition() {
					{
						name_i18n = Collections.singletonMap(
							"en-US", patchedName);
					}
				});

		getListTypeDefinition =
			listTypeDefinitionResource.getListTypeDefinition(
				patchListTypeDefinition.getId());

		Assert.assertEquals(
			Collections.singletonMap("en-US", patchedName),
			getListTypeDefinition.getName_i18n());

		listTypeEntries = getListTypeDefinition.getListTypeEntries();

		Assert.assertEquals(
			Arrays.toString(listTypeEntries), 2, listTypeEntries.length);
	}

	@Test
	public void testPatchPostPutListTypeDefinitionWithPermissions()
		throws Exception {

		// Invalid permissions

		JSONArray invalidPermissionsJSONArray = JSONUtil.putAll(
			_getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE},
				RandomTestUtil.randomString()));

		_assertNotFound(
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PATCH,
				_postListTypeDefinitionWithPermissions(true, null), true,
				invalidPermissionsJSONArray));
		_assertNotFound(
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PUT,
				_postListTypeDefinitionWithPermissions(true, null), true,
				invalidPermissionsJSONArray));
		_assertNotFound(
			_postListTypeDefinitionWithPermissions(
				true, invalidPermissionsJSONArray));
		_assertNotFound(
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				_postListTypeDefinitionWithPermissions(true, null), true,
				invalidPermissionsJSONArray));

		// No permissions in the body request

		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(_getOwnerPermissionsJSONObject()),
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PATCH,
				_postListTypeDefinitionWithPermissions(true, null), true,
				null));
		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(_getOwnerPermissionsJSONObject()),
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PUT,
				_postListTypeDefinitionWithPermissions(true, null), true,
				null));
		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(_getOwnerPermissionsJSONObject()),
			_postListTypeDefinitionWithPermissions(true, null));
		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(_getOwnerPermissionsJSONObject()),
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				_postListTypeDefinitionWithPermissions(true, null), true,
				null));

		// Permissions with different roles

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			"com.liferay.list.type.model.ListTypeDefinition",
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role1.getRoleId(),
			ActionKeys.DELETE);

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		JSONObject listTypeDefinitionJSONObject =
			_postListTypeDefinitionWithPermissions(
				true,
				JSONUtil.putAll(
					_getPermissionsJSONObject(
						new String[] {ActionKeys.PERMISSIONS}, role1.getName()),
					_getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
						role2.getName())));

		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(
				_getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.PERMISSIONS},
					role1.getName()),
				_getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
					role2.getName())),
			listTypeDefinitionJSONObject);

		Role role3 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(
				_getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				_getPermissionsJSONObject(
					new String[] {ActionKeys.VIEW}, role3.getName())),
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PATCH, listTypeDefinitionJSONObject, true,
				JSONUtil.putAll(
					_getPermissionsJSONObject(
						new String[] {ActionKeys.VIEW}, role3.getName()))));
		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(
				_getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE}, role1.getName()),
				_getPermissionsJSONObject(
					new String[] {ActionKeys.UPDATE}, role3.getName())),
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PUT, listTypeDefinitionJSONObject, true,
				JSONUtil.putAll(
					_getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role3.getName()))));
		_assertListTypeDefinitionWithPermissions(
			JSONUtil.putAll(
				_getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.UPDATE},
					role1.getName()),
				_getPermissionsJSONObject(
					new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
					role3.getName())),
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				listTypeDefinitionJSONObject, true,
				JSONUtil.putAll(
					_getPermissionsJSONObject(
						new String[] {ActionKeys.UPDATE}, role1.getName()),
					_getPermissionsJSONObject(
						new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
						role3.getName()))));

		// Permissions with empty list

		JSONArray companyPermissionsJSONArray = JSONUtil.putAll(
			_getPermissionsJSONObject(
				new String[] {ActionKeys.DELETE}, role1.getName()));

		_assertListTypeDefinitionWithPermissions(
			companyPermissionsJSONArray,
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PATCH, listTypeDefinitionJSONObject, true,
				_jsonFactory.createJSONArray()));
		_assertListTypeDefinitionWithPermissions(
			companyPermissionsJSONArray,
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PUT, listTypeDefinitionJSONObject, true,
				_jsonFactory.createJSONArray()));
		_assertListTypeDefinitionWithPermissions(
			companyPermissionsJSONArray,
			_postListTypeDefinitionWithPermissions(
				true, _jsonFactory.createJSONArray()));
		_assertListTypeDefinitionWithPermissions(
			companyPermissionsJSONArray,
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				listTypeDefinitionJSONObject, true,
				_jsonFactory.createJSONArray()));

		// Permissions without nested fields

		listTypeDefinitionJSONObject =
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PATCH, listTypeDefinitionJSONObject, false, null);

		Assert.assertNull(listTypeDefinitionJSONObject.get("permissions"));

		listTypeDefinitionJSONObject =
			_patchPutListTypeDefinitionWithPermissions(
				Http.Method.PUT, listTypeDefinitionJSONObject, false, null);

		Assert.assertNull(listTypeDefinitionJSONObject.get("permissions"));

		listTypeDefinitionJSONObject = _postListTypeDefinitionWithPermissions(
			false, null);

		Assert.assertNull(listTypeDefinitionJSONObject.get("permissions"));

		listTypeDefinitionJSONObject =
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				listTypeDefinitionJSONObject, false, null);

		Assert.assertNull(listTypeDefinitionJSONObject.get("permissions"));
	}

	@Override
	@Test
	public void testPostListTypeDefinition() throws Exception {
		super.testPostListTypeDefinition();

		// Creator

		User user = TestPropsValues.getUser();

		ListTypeDefinition listTypeDefinition =
			testPostListTypeDefinition_addListTypeDefinition(
				randomListTypeDefinition());

		Creator creator = listTypeDefinition.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());

		user = UserTestUtil.addUser();

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderListTypeDefinition =
				_listTypeDefinitionLocalService.addListTypeDefinition(
					RandomTestUtil.randomString(), user.getUserId(),
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					false, Collections.emptyList(), new ServiceContext());

		listTypeDefinition = listTypeDefinitionResource.getListTypeDefinition(
			serviceBuilderListTypeDefinition.getListTypeDefinitionId());

		creator = listTypeDefinition.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());

		// Name

		listTypeDefinition = randomListTypeDefinition();

		listTypeDefinition.setDefaultLanguageId("pt_BR");
		listTypeDefinition.setName_i18n(
			Collections.singletonMap("pt_BR", RandomTestUtil.randomString()));
		listTypeDefinition.setSystem(true);

		ListTypeDefinition postListTypeDefinition =
			testPostListTypeDefinition_addListTypeDefinition(
				listTypeDefinition);

		assertEquals(listTypeDefinition, postListTypeDefinition);
		assertValid(postListTypeDefinition);

		listTypeDefinition = randomListTypeDefinition();

		listTypeDefinition.setName(RandomTestUtil.randomString());
		listTypeDefinition.setName_i18n((Map<String, String>)null);

		_assertListTypeDefinitionNameLocalizedMap(
			testPostListTypeDefinition_addListTypeDefinition(
				listTypeDefinition));
	}

	@Override
	@Test
	public void testPutListTypeDefinition() throws Exception {
		super.testPutListTypeDefinition();

		ListTypeDefinition listTypeDefinition =
			testPutListTypeDefinition_addListTypeDefinition();

		listTypeDefinition.setName(RandomTestUtil.randomString());
		listTypeDefinition.setName_i18n((Map<String, String>)null);

		_assertListTypeDefinitionNameLocalizedMap(
			listTypeDefinitionResource.putListTypeDefinition(
				listTypeDefinition.getId(), listTypeDefinition));
	}

	@Override
	protected ListTypeDefinition randomListTypeDefinition() throws Exception {
		ListTypeDefinition listTypeDefinition =
			super.randomListTypeDefinition();

		listTypeDefinition.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		listTypeDefinition.setSystem(false);

		ListTypeEntry listTypeEntry = new ListTypeEntry();

		listTypeEntry.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));
		listTypeEntry.setKey(RandomTestUtil.randomString());

		listTypeDefinition.setListTypeEntries(
			new ListTypeEntry[] {listTypeEntry});

		return listTypeDefinition;
	}

	@Override
	protected ListTypeDefinition
			testDeleteListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGetListTypeDefinitionsPage_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testGraphQLListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testPatchListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPostListTypeDefinition_addListTypeDefinition(
				ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _addListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinition_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_addListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	@Override
	protected ListTypeDefinition
			testPutListTypeDefinitionByExternalReferenceCode_createListTypeDefinition()
		throws Exception {

		return _addListTypeDefinition(randomListTypeDefinition());
	}

	private ListTypeDefinition _addListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		listTypeDefinition = listTypeDefinitionResource.postListTypeDefinition(
			listTypeDefinition);

		_listTypeDefinitions.add(
			_listTypeDefinitionLocalService.fetchListTypeDefinition(
				listTypeDefinition.getId()));

		return listTypeDefinition;
	}

	private void _assertListTypeDefinitionNameLocalizedMap(
		ListTypeDefinition listTypeDefinition) {

		Map<Locale, String> nameLocalizedMap = LocalizedMapUtil.getLocalizedMap(
			listTypeDefinition.getName_i18n());

		Assert.assertEquals(
			listTypeDefinition.getName(),
			nameLocalizedMap.get(LocaleUtil.getSiteDefault()));
	}

	private void _assertListTypeDefinitionWithPermissions(
			JSONArray expectedPermissionsJSONArray, JSONObject jsonObject)
		throws Exception {

		JSONArray actualPermissionsJSONArray = jsonObject.getJSONArray(
			"permissions");

		if (actualPermissionsJSONArray == null) {
			actualPermissionsJSONArray = jsonObject.getJSONArray("items");
		}

		JSONAssert.assertEquals(
			String.valueOf(expectedPermissionsJSONArray),
			String.valueOf(actualPermissionsJSONArray),
			JSONCompareMode.LENIENT);
	}

	private void _assertNotFound(JSONObject jsonObject) {
		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
		Assert.assertNull(jsonObject.get("title"));
	}

	private JSONObject _getOwnerPermissionsJSONObject() {
		return _getPermissionsJSONObject(
			new String[] {
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW
			},
			RoleConstants.OWNER);
	}

	private JSONObject _getPermissionsJSONObject(
		String[] actionIds, String roleName) {

		return JSONUtil.put(
			"actionIds", actionIds
		).put(
			"roleName", roleName
		);
	}

	private JSONObject _patchPutListTypeDefinitionWithPermissions(
			Http.Method httpMethod, JSONObject listTypeDefinitionJSONObject,
			boolean nestedFields, JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint =
			"headless-admin-list-type/v1.0/list-type-definitions/" +
				listTypeDefinitionJSONObject.getLong("id");

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, httpMethod);
	}

	private JSONObject _postListTypeDefinitionWithPermissions(
			boolean nestedFields, JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint = "headless-admin-list-type/v1.0/list-type-definitions";

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, Http.Method.POST);
	}

	private JSONObject
			_putByExternalReferenceCodeListTypeDefinitionWithPermissions(
				JSONObject listTypeDefinitionJSONObject, boolean nestedFields,
				JSONArray permissionsJSONArray)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-admin-list-type/v1.0/list-type-definitions",
			"/by-external-reference-code/",
			listTypeDefinitionJSONObject.getString("externalReferenceCode"));

		if (nestedFields) {
			endpoint = endpoint + "?nestedFields=permissions";
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", RandomTestUtil.randomString()
			).put(
				"permissions", permissionsJSONArray
			).toString(),
			endpoint, Http.Method.PUT);
	}

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private List<com.liferay.list.type.model.ListTypeDefinition>
		_listTypeDefinitions = new ArrayList<>();

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}
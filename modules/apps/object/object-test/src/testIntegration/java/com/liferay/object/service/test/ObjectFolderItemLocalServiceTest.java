/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.exception.NoSuchObjectFolderException;
import com.liferay.object.exception.NoSuchObjectFolderItemException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectFolderItem;
import com.liferay.object.model.ObjectFolderItemModel;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderItemLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectFolderItemUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Murilo Stodolni
 */
@RunWith(Arquillian.class)
public class ObjectFolderItemLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectFolderA = _addObjectFolder();
		_objectFolderB = _addObjectFolder();
		_objectFolderC = _addObjectFolder();

		_objectDefinition1 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			_objectFolderA.getObjectFolderId());
		_objectDefinition2 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			_objectFolderB.getObjectFolderId());
		_objectDefinition3 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			_objectFolderA.getObjectFolderId());
		_objectDefinition4 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			_objectFolderC.getObjectFolderId());

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition1,
			_objectDefinition2);
		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition1,
			_objectDefinition4);
		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition2,
			_objectDefinition3);
	}

	@Test
	public void testAddObjectFolderItem() throws Exception {

		// Add object folder item

		long objectDefinitionId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectDefinitionException.class,
			"No ObjectDefinition exists with the primary key " +
				objectDefinitionId,
			() -> _objectFolderItemLocalService.addObjectFolderItem(
				TestPropsValues.getUserId(), objectDefinitionId,
				_objectFolderA.getObjectFolderId(), 0, 0));

		long objectFolderId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectFolderException.class,
			"No ObjectFolder exists with the primary key " + objectFolderId,
			() -> _objectFolderItemLocalService.addObjectFolderItem(
				TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(), objectFolderId, 0,
				0));

		ObjectFolderItem objectFolderItem =
			_objectFolderItemLocalService.addObjectFolderItem(
				TestPropsValues.getUserId(),
				_objectDefinition4.getObjectDefinitionId(),
				_objectFolderB.getObjectFolderId(), 0, 0);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), objectFolderItem.getCompanyId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), objectFolderItem.getUserId());
		Assert.assertEquals(
			_objectDefinition4.getObjectDefinitionId(),
			objectFolderItem.getObjectDefinitionId());
		Assert.assertEquals(
			_objectFolderB.getObjectFolderId(),
			objectFolderItem.getObjectFolderId());
		Assert.assertEquals(0, objectFolderItem.getPositionX());
		Assert.assertEquals(0, objectFolderItem.getPositionY());

		_objectFolderItemLocalService.deleteObjectFolderItem(objectFolderItem);

		// Add object folder item when adding object definition

		ObjectFolder objectFolder = _addObjectFolder();

		_assertObjectFolderItems(new Long[0], objectFolder.getObjectFolderId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				objectFolder.getObjectFolderId());

		_assertObjectFolderItems(
			new Long[] {objectDefinition.getObjectDefinitionId()},
			objectFolder.getObjectFolderId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);

		// Add object folder item when adding object relationship

		_assertObjectFolderItems(
			new Long[] {
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition4.getObjectDefinitionId()
			},
			_objectFolderC.getObjectFolderId());

		objectFolder = _addObjectFolder();

		objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			objectFolder.getObjectFolderId());

		_assertObjectFolderItems(
			new Long[] {objectDefinition.getObjectDefinitionId()},
			objectFolder.getObjectFolderId());

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition4,
			objectDefinition);

		_assertObjectFolderItems(
			new Long[] {
				objectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition4.getObjectDefinitionId()
			},
			_objectFolderC.getObjectFolderId());
		_assertObjectFolderItems(
			new Long[] {
				objectDefinition.getObjectDefinitionId(),
				_objectDefinition4.getObjectDefinitionId()
			},
			objectFolder.getObjectFolderId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	@Test
	public void testDeleteObjectFolderItem() throws Exception {

		// Object definition 1 does not belong to object folder B, but object
		// definition 1's related object definitions belong to object folder B

		_objectFolderItemLocalService.deleteObjectFolderItem(
			_objectDefinition1.getObjectDefinitionId(),
			_objectFolderB.getObjectFolderId());

		Assert.assertNotNull(
			_getObjectFolderItem(
				_objectDefinition1.getObjectDefinitionId(),
				_objectFolderB.getObjectFolderId()));

		// Object definition 2 belongs to object folder B

		_objectFolderItemLocalService.deleteObjectFolderItem(
			_objectDefinition2.getObjectDefinitionId(),
			_objectFolderB.getObjectFolderId());

		Assert.assertNotNull(
			_getObjectFolderItem(
				_objectDefinition2.getObjectDefinitionId(),
				_objectFolderB.getObjectFolderId()));

		// Object definition 4 does not belong to object folder B and object
		// definition 4's related object definitions do not belong to object
		// folder B

		ObjectFolderItem objectFolderItem =
			_objectFolderItemLocalService.addObjectFolderItem(
				TestPropsValues.getUserId(),
				_objectDefinition4.getObjectDefinitionId(),
				_objectFolderB.getObjectFolderId(), 0, 0);

		_objectFolderItemLocalService.deleteObjectFolderItem(objectFolderItem);

		Assert.assertNull(
			_objectFolderItemLocalService.fetchObjectFolderItem(
				objectFolderItem.getObjectFolderItemId()));
	}

	@Test
	public void testDeleteObjectFolderItemByObjectDefinitionId()
		throws Exception {

		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_getObjectFolderItems(
					_objectDefinition1.getObjectDefinitionId())));

		_objectFolderItemLocalService.
			deleteObjectFolderItemByObjectDefinitionId(
				_objectDefinition1.getObjectDefinitionId());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_getObjectFolderItems(
					_objectDefinition1.getObjectDefinitionId())));
	}

	@Test
	public void testDeleteObjectFolderItemByObjectFolderId() {
		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_objectFolderItemLocalService.
					getObjectFolderItemsByObjectFolderId(
						_objectFolderA.getObjectFolderId())));

		_objectFolderItemLocalService.deleteObjectFolderItemByObjectFolderId(
			_objectFolderA.getObjectFolderId());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_objectFolderItemLocalService.
					getObjectFolderItemsByObjectFolderId(
						_objectFolderA.getObjectFolderId())));
	}

	@Test
	public void testGetObjectFolderItemsByObjectFolderId() throws Exception {
		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), ObjectFolder.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});

			_userLocalService.addRoleUser(role.getRoleId(), user);

			ObjectFolder objectFolder =
				_objectFolderLocalService.getDefaultObjectFolder(
					TestPropsValues.getCompanyId());

			List<ObjectFolderItem> objectFolderItems =
				_objectFolderItemLocalService.
					getObjectFolderItemsByObjectFolderId(
						objectFolder.getObjectFolderId());

			Assert.assertEquals(
				objectFolderItems.toString(), 0, objectFolderItems.size());

			_roleLocalService.deleteRole(role.getRoleId());
			_userLocalService.deleteUser(user);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testUpdateObjectFolderItem() throws PortalException {
		long objectDefinitionId = RandomTestUtil.randomLong();
		long objectFolderId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectFolderItemException.class,
			StringBundler.concat(
				"No ObjectFolderItem exists with the key {objectDefinitionId=",
				objectDefinitionId, ", objectFolderId=", objectFolderId, "}"),
			() -> _objectFolderItemLocalService.updateObjectFolderItem(
				objectDefinitionId, objectFolderId, 1, 1));

		ObjectFolderItem objectFolderItem =
			_objectFolderItemLocalService.updateObjectFolderItem(
				_objectDefinition1.getObjectDefinitionId(),
				_objectFolderA.getObjectFolderId(), 1, 1);

		Assert.assertEquals(1, objectFolderItem.getPositionX());
		Assert.assertEquals(1, objectFolderItem.getPositionY());
	}

	@Test
	public void testUpdateObjectFolderObjectFolderItem()
		throws PortalException {

		_objectDefinitionLocalService.updateObjectFolderId(
			_objectDefinition1.getObjectDefinitionId(),
			_objectFolderC.getObjectFolderId());

		_assertObjectFolderItems(
			new Long[] {
				_objectDefinition2.getObjectDefinitionId(),
				_objectDefinition3.getObjectDefinitionId()
			},
			_objectFolderA.getObjectFolderId());
		_assertObjectFolderItems(
			new Long[] {
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(),
				_objectDefinition4.getObjectDefinitionId()
			},
			_objectFolderC.getObjectFolderId());
	}

	private ObjectFolder _addObjectFolder() throws Exception {
		return _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());
	}

	private void _assertObjectFolderItems(
		Long[] expectedObjectDefinitionIds, long objectFolderId) {

		Arrays.sort(expectedObjectDefinitionIds);

		Long[] actualObjectDefinitionIds = TransformUtil.transformToArray(
			_objectFolderItemLocalService.getObjectFolderItemsByObjectFolderId(
				objectFolderId),
			ObjectFolderItemModel::getObjectDefinitionId, Long.class);

		Arrays.sort(actualObjectDefinitionIds);

		Assert.assertArrayEquals(
			expectedObjectDefinitionIds, actualObjectDefinitionIds);
	}

	private ObjectFolderItem _getObjectFolderItem(
			long objectDefinitionId, long objectFolderId)
		throws Exception {

		return TransactionTestUtil.invoke(
			ObjectFolderItemUtil.getPersistence(),
			objectFolderItemPersistence ->
				objectFolderItemPersistence.findByODI_OFI(
					objectDefinitionId, objectFolderId));
	}

	private List<ObjectFolderItem> _getObjectFolderItems(
			long objectDefinitionId)
		throws Exception {

		return TransactionTestUtil.invoke(
			ObjectFolderItemUtil.getPersistence(),
			objectFolderItemPersistence ->
				objectFolderItemPersistence.findByObjectDefinitionId(
					objectDefinitionId));
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition3;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition4;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolderA;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolderB;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolderC;

	@Inject
	private ObjectFolderItemLocalService _objectFolderItemLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
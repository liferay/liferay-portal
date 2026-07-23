/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.SharingEntryService;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class SharingEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = CMSTestUtil.getOrAddGroup(SharingEntryServiceTest.class);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddOrUpdateSharingEntry() throws Exception {
		_testAddOrUpdateSharingEntryForBasicDocumentAsCMSAdministrator();
		_testAddOrUpdateSharingEntryForBasicDocumentWithoutPermissions();
	}

	@Test
	public void testAddSharingEntry() throws Exception {
		_testAddSharingEntryForBasicDocumentWithViewAddsDownloadAction();
		_testAddSharingEntryForBasicWebContentWithViewDoesNotAddDownloadAction();
	}

	private ObjectEntry _addCMSBasicDocumentObjectEntry(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _addObjectEntry(
			objectDefinition, "L_FILES",
			HashMapBuilder.<String, Serializable>put(
				"file", String.valueOf(_addDLFileEntry())
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());
	}

	private long _addDLFileEntry() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_depotEntry.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		return dlFileEntry.getFileEntryId();
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition,
			String objectEntryFolderExternalReferenceCode,
			Map<String, Serializable> values)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					objectEntryFolderExternalReferenceCode,
					_depotEntry.getGroupId(), _depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US", values,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));
	}

	private void _assertAddOrUpdateSharingEntry(
			long classNameId, boolean expectedContainsSharePermission,
			ObjectEntry objectEntry, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			Assert.assertEquals(
				expectedContainsSharePermission,
				_sharingPermission.containsSharePermission(
					PermissionThreadLocal.getPermissionChecker(), classNameId,
					objectEntry.getObjectEntryId(), _depotEntry.getGroupId()));

			_sharingEntryService.addOrUpdateSharingEntry(
				null, 0, 0, _user.getUserId(), classNameId,
				objectEntry.getObjectEntryId(), _depotEntry.getGroupId(), true,
				Collections.singletonList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _assertAddSharingEntry(
			long classNameId, boolean expectedDownloadPermission,
			ObjectEntry objectEntry)
		throws Exception {

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, 0, _user.getUserId(), classNameId,
			objectEntry.getObjectEntryId(), _depotEntry.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));

		Assert.assertTrue(
			sharingEntry.hasSharingPermission(SharingEntryAction.VIEW));
		Assert.assertEquals(
			expectedDownloadPermission,
			sharingEntry.hasSharingPermission(SharingEntryAction.DOWNLOAD));
	}

	private ObjectDefinition _getCMSBasicDocumentObjectDefinition()
		throws Exception {

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				"L_CMS_BASIC_DOCUMENT", _group.getCompanyId());
	}

	private void _testAddOrUpdateSharingEntryForBasicDocumentAsCMSAdministrator()
		throws Exception {

		ObjectDefinition objectDefinition =
			_getCMSBasicDocumentObjectDefinition();

		_assertAddOrUpdateSharingEntry(
			_portal.getClassNameId(objectDefinition.getClassName()), true,
			_addCMSBasicDocumentObjectEntry(objectDefinition),
			UserTestUtil.addCompanyUser(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				RoleConstants.CMS_ADMINISTRATOR));
	}

	private void _testAddOrUpdateSharingEntryForBasicDocumentWithoutPermissions()
		throws Exception {

		ObjectDefinition objectDefinition =
			_getCMSBasicDocumentObjectDefinition();

		ObjectEntry objectEntry = _addCMSBasicDocumentObjectEntry(
			objectDefinition);

		User user = UserTestUtil.addUser();

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(),
				" must have DOWNLOAD, VIEW permission for ",
				objectDefinition.getClassName(), " ",
				objectEntry.getObjectEntryId()),
			() -> _assertAddOrUpdateSharingEntry(
				_portal.getClassNameId(objectDefinition.getClassName()), false,
				objectEntry, user));
	}

	private void _testAddSharingEntryForBasicDocumentWithViewAddsDownloadAction()
		throws Exception {

		ObjectDefinition objectDefinition =
			_getCMSBasicDocumentObjectDefinition();

		_assertAddSharingEntry(
			_portal.getClassNameId(objectDefinition.getClassName()), true,
			_addCMSBasicDocumentObjectEntry(objectDefinition));
	}

	private void _testAddSharingEntryForBasicWebContentWithViewDoesNotAddDownloadAction()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", _group.getCompanyId());

		_assertAddSharingEntry(
			_portal.getClassNameId(objectDefinition.getClassName()), false,
			_addObjectEntry(
				objectDefinition, "L_CONTENTS",
				HashMapBuilder.<String, Serializable>put(
					"content_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).put(
					"title_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).build()));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SharingEntryService _sharingEntryService;

	@Inject
	private SharingPermission _sharingPermission;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
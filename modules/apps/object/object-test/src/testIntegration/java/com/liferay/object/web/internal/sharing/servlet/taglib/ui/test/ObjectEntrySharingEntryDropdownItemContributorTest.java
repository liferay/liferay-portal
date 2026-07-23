/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.sharing.servlet.taglib.ui.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryService;
import com.liferay.sharing.servlet.taglib.ui.SharingEntryDropdownItemContributor;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class ObjectEntrySharingEntryDropdownItemContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();

		_group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_serviceContext.setRequest(new MockHttpServletRequest());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE, _serviceContext);

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetSharingEntryDropdownItems() throws Exception {
		_testGetSharingEntryDropdownItemsWithCMSBasicDocument();
		_testGetSharingEntryDropdownItemsWithCMSBasicWebContent();
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
			_serviceContext);
	}

	private List<DropdownItem> _getSharingEntryDropdownItems(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		SharingEntry sharingEntry = _sharingEntryService.addSharingEntry(
			null, 0, 0, _user.getUserId(),
			_portal.getClassNameId(objectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), _depotEntry.getGroupId(), true,
			Collections.singletonList(SharingEntryAction.VIEW), null,
			_serviceContext);

		Collection<ServiceReference<SharingEntryDropdownItemContributor>>
			serviceReferences = _bundleContext.getServiceReferences(
				SharingEntryDropdownItemContributor.class,
				"(model.class.name=" + objectDefinition.getClassName() + ")");

		if (serviceReferences.isEmpty()) {
			return Collections.emptyList();
		}

		ThemeDisplay themeDisplay = _getThemeDisplay();

		List<DropdownItem> dropdownItems = new ArrayList<>();

		for (ServiceReference<SharingEntryDropdownItemContributor>
				serviceReference : serviceReferences) {

			SharingEntryDropdownItemContributor
				sharingEntryDropdownItemContributor = _bundleContext.getService(
					serviceReference);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					_user, PermissionCheckerFactoryUtil.create(_user))) {

				ServiceContextThreadLocal.pushServiceContext(_serviceContext);

				dropdownItems.addAll(
					sharingEntryDropdownItemContributor.
						getSharingEntryDropdownItems(
							sharingEntry, themeDisplay));
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

		return dropdownItems;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testGetSharingEntryDropdownItemsWithCMSBasicDocument()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", _group.getCompanyId());

		List<DropdownItem> dropdownItems = _getSharingEntryDropdownItems(
			objectDefinition,
			_addObjectEntry(
				objectDefinition, "L_FILES",
				HashMapBuilder.<String, Serializable>put(
					"file",
					() -> {
						DLFolder dlFolder = DLTestUtil.addDLFolder(
							_depotEntry.getGroupId());

						DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
							dlFolder.getFolderId());

						return dlFileEntry.getFileEntryId();
					}
				).put(
					"title_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).build()
				).build()));

		Assert.assertEquals(dropdownItems.toString(), 1, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Assert.assertEquals("download", dropdownItem.get("icon"));
		Assert.assertNotNull(dropdownItem.get("href"));
		Assert.assertNotNull(dropdownItem.get("label"));
	}

	private void _testGetSharingEntryDropdownItemsWithCMSBasicWebContent()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", _group.getCompanyId());

		List<DropdownItem> dropdownItems = _getSharingEntryDropdownItems(
			objectDefinition,
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

		Assert.assertTrue(dropdownItems.toString(), dropdownItems.isEmpty());
	}

	private BundleContext _bundleContext;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private SharingEntryService _sharingEntryService;

	@DeleteAfterTestRun
	private User _user;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.exception.StyleBookEntryFileException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testAddStyleBookEntryToDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_DESIGN_LIBRARY, _serviceContext);

		try {
			long depotGroupId = depotEntry.getGroupId();

			StyleBookEntry styleBookEntry =
				_styleBookEntryService.addStyleBookEntry(
					null, depotGroupId, RandomTestUtil.randomString(),
					StringPool.BLANK, RandomTestUtil.randomString(),
					ServiceContextTestUtil.getServiceContext(
						depotGroupId, TestPropsValues.getUserId()));

			Assert.assertEquals(depotGroupId, styleBookEntry.getGroupId());
			Assert.assertFalse(styleBookEntry.isDefaultStyleBookEntry());
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	@Test
	public void testAddStyleBookEntryToDepotEntryWithoutAddPermission()
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_DESIGN_LIBRARY, _serviceContext);

		try {
			long depotGroupId = depotEntry.getGroupId();

			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookEntryService.addStyleBookEntry(
				null, depotGroupId, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					depotGroupId, TestPropsValues.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());

			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	@Test
	public void testAddStyleBookEntryWithoutAddPermission() throws Exception {
		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testCopyStyleBookEntry() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		_irrelevantGroup = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_irrelevantGroup, TestPropsValues.getUserId());

		StyleBookEntry irrelevantStyleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _irrelevantGroup.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), serviceContext);

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(
				_group, RoleConstants.SITE_ADMINISTRATOR));

		try {
			StyleBookEntry copyStyleBookEntry =
				_styleBookEntryService.copyStyleBookEntry(
					_group.getGroupId(), styleBookEntry.getStyleBookEntryId(),
					_serviceContext);

			Assert.assertEquals(
				_group.getGroupId(), copyStyleBookEntry.getGroupId());

			try {
				_styleBookEntryService.copyStyleBookEntry(
					_group.getGroupId(),
					irrelevantStyleBookEntry.getStyleBookEntryId(),
					_serviceContext);

				Assert.fail();
			}
			catch (PrincipalException principalException) {
			}
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testDeleteStyleBookEntryByExternalReferenceCode()
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		try {
			_assertDeleteStyleBookEntryWithoutPermission(
				RandomTestUtil.randomString());
			_assertDeleteStyleBookEntryWithoutPermission(
				styleBookEntry.getExternalReferenceCode());
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}

		_styleBookEntryService.deleteStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			styleBookEntry.getGroupId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));

		try {
			_styleBookEntryService.deleteStyleBookEntry(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId());

			Assert.fail();
		}
		catch (NoSuchEntryException noSuchEntryException) {
		}
	}

	@Test
	public void testGetStyleBookEntry() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		Assert.assertEquals(
			styleBookEntry,
			_styleBookEntryService.getStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookEntryService.getStyleBookEntry(
				styleBookEntry.getStyleBookEntryId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testGetStyleBookEntryByExternalReferenceCode()
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		Assert.assertEquals(
			styleBookEntry,
			_styleBookEntryService.getStyleBookEntryByExternalReferenceCode(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId()));

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookEntryService.getStyleBookEntryByExternalReferenceCode(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testUpdateFrontendTokenDefinition() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		try {
			UserTestUtil.setUser(user);

			_styleBookEntryService.updateFrontendTokenDefinition(
				styleBookEntry.getStyleBookEntryId(), frontendTokenDefinition,
				_serviceContext);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message,
				message.contains(
					StringBundler.concat(
						"User ", user.getUserId(), " must have ",
						StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES,
						" permission for")));
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}

		styleBookEntry = _styleBookEntryService.updateFrontendTokenDefinition(
			styleBookEntry.getStyleBookEntryId(), frontendTokenDefinition,
			_serviceContext);

		Assert.assertEquals(
			frontendTokenDefinition,
			styleBookEntry.getFrontendTokenDefinition());
	}

	@Test
	public void testUpdatePreviewFileEntryId() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		PortletFileRepositoryUtil.addPortletRepository(
			_group.getGroupId(), StyleBookPortletKeys.STYLE_BOOK,
			_serviceContext);

		_irrelevantGroup = GroupTestUtil.addGroup();

		FileEntry fileEntry = _addFileEntry(
			_irrelevantGroup, StyleBookPortletKeys.STYLE_BOOK);

		_assertUpdateWithInvalidPreviewFileEntryId(
			fileEntry.getFileEntryId(), styleBookEntry);

		fileEntry = _addFileEntry(_group, RandomTestUtil.randomString());

		_assertUpdateWithInvalidPreviewFileEntryId(
			fileEntry.getFileEntryId(), styleBookEntry);

		_assertUpdateWithInvalidPreviewFileEntryId(
			RandomTestUtil.nextLong(), styleBookEntry);

		fileEntry = _addFileEntry(_group, StyleBookPortletKeys.STYLE_BOOK);

		styleBookEntry = _styleBookEntryService.updatePreviewFileEntryId(
			styleBookEntry.getStyleBookEntryId(), fileEntry.getFileEntryId(),
			_serviceContext);

		Assert.assertEquals(
			fileEntry.getFileEntryId(), styleBookEntry.getPreviewFileEntryId());
	}

	private FileEntry _addFileEntry(Group group, String portletId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			group.getGroupId(), portletId, serviceContext);

		return PortletFileRepositoryUtil.addPortletFileEntry(
			group.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(), 0, portletId,
			repository.getDlFolderId(), RandomTestUtil.randomBytes(),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
	}

	private void _assertDeleteStyleBookEntryWithoutPermission(
			String externalReferenceCode)
		throws Exception {

		try {
			_styleBookEntryService.deleteStyleBookEntry(
				externalReferenceCode, _group.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
	}

	private void _assertUpdateWithInvalidPreviewFileEntryId(
			long previewFileEntryId, StyleBookEntry styleBookEntry)
		throws Exception {

		try {
			_styleBookEntryService.updatePreviewFileEntryId(
				styleBookEntry.getStyleBookEntryId(), previewFileEntryId,
				_serviceContext);

			Assert.fail();
		}
		catch (StyleBookEntryFileException styleBookEntryFileException) {
		}

		try {
			_styleBookEntryService.updateStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(),
				styleBookEntry.isDefaultStyleBookEntry(),
				styleBookEntry.getFrontendTokenDefinition(),
				styleBookEntry.getFrontendTokensValues(),
				styleBookEntry.getName(), styleBookEntry.getStyleBookEntryKey(),
				previewFileEntryId, styleBookEntry.getThemeId(),
				_serviceContext);

			Assert.fail();
		}
		catch (StyleBookEntryFileException styleBookEntryFileException) {
		}
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Group _irrelevantGroup;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private StyleBookEntryService _styleBookEntryService;

}
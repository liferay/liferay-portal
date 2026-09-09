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
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;

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
	public void testDeleteStyleBookEntryByExternalReferenceCode()
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				RandomTestUtil.randomString(), _group.getGroupId(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookEntryService.deleteStyleBookEntry(
				styleBookEntry.getExternalReferenceCode(),
				styleBookEntry.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
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

		String frontendTokenDefinition = JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put(
									"defaultValue",
									RandomTestUtil.randomString()
								).put(
									"editorType", "ColorPicker"
								).put(
									"label", RandomTestUtil.randomString()
								).put(
									"mappings",
									JSONUtil.putAll(
										JSONUtil.put(
											"type", "cssVariable"
										).put(
											"value",
											RandomTestUtil.randomString()
										))
								).put(
									"name", RandomTestUtil.randomString()
								).put(
									"type", "String"
								))
						).put(
							"label", RandomTestUtil.randomString()
						).put(
							"name", RandomTestUtil.randomString()
						))
				).put(
					"name", RandomTestUtil.randomString()
				))
		).toString();

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

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private StyleBookEntryService _styleBookEntryService;

}
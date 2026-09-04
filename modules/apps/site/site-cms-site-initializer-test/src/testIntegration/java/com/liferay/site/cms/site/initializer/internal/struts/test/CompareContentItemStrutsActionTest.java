/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@FeatureFlag("LPD-56634")
@RunWith(Arquillian.class)
public class CompareContentItemStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_objectDefinitions = new ArrayList<>();
	}

	@Test
	@TestInfo("LPD-104324")
	public void testExecute() throws Exception {
		_testExecuteWithLayoutPageTemplateEntry();
		_testExecuteWithParameters();
		_testExecuteWithoutFeatureFlag();
		_testExecuteWithoutLayoutPageTemplateEntry();
		_testExecuteWithoutViewPermission();
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"A" + RandomTestUtil.randomString(),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitions.add(objectDefinition);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_depotEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	private MockHttpServletResponse _execute(
			MockHttpServletRequest mockHttpServletRequest, User user)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_compareContentItemStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);
		}

		return mockHttpServletResponse;
	}

	private LayoutPageTemplateEntry _fetchLayoutPageTemplateEntry(
			ObjectEntry objectEntry)
		throws Exception {

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntry(
				_group.getGroupId(),
				"LFR_CMS_COMPARE_" + _getClassNameId(objectEntry));
	}

	private long _getClassNameId(ObjectEntry objectEntry) throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return _portal.getClassNameId(objectDefinition.getClassName());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			ObjectEntry objectEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setParameter(
			"objectEntryId", String.valueOf(objectEntry.getObjectEntryId()));
		mockHttpServletRequest.setRequestURI(_layout.getFriendlyURL());

		return mockHttpServletRequest;
	}

	private void _testExecuteWithLayoutPageTemplateEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(objectEntry);

		_execute(mockHttpServletRequest, TestPropsValues.getUser());

		LayoutPageTemplateEntry expectedLayoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(objectEntry);

		_execute(mockHttpServletRequest, TestPropsValues.getUser());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(objectEntry);

		Assert.assertEquals(
			expectedLayoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
	}

	private void _testExecuteWithoutFeatureFlag() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-56634"),
					Boolean.FALSE.toString())) {

			_execute(
				_getMockHttpServletRequest(objectEntry),
				TestPropsValues.getUser());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof UnsupportedOperationException);
		}

		Assert.assertNull(_fetchLayoutPageTemplateEntry(objectEntry));
	}

	private void _testExecuteWithoutLayoutPageTemplateEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		Assert.assertNull(_fetchLayoutPageTemplateEntry(objectEntry));

		MockHttpServletResponse mockHttpServletResponse = _execute(
			_getMockHttpServletRequest(objectEntry), TestPropsValues.getUser());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(objectEntry);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layoutPageTemplateEntry.getGroupId(),
					layoutPageTemplateEntry.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		Assert.assertEquals(
			formStyledLayoutStructureItems.toString(), 1,
			formStyledLayoutStructureItems.size());

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		Assert.assertEquals(
			objectDefinition.getClassName(),
			formStyledLayoutStructureItem.getClassName());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Locale locale = PortalUtil.getSiteDefaultLocale(_group);

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(
			redirectedURL.contains(layout.getFriendlyURL(locale)));

		Assert.assertTrue(
			redirectedURL.contains(
				StringBundler.concat(
					StringPool.SLASH, _getClassNameId(objectEntry),
					StringPool.SLASH, objectEntry.getObjectEntryId())));
	}

	private void _testExecuteWithoutViewPermission() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		User user = UserTestUtil.addUser();

		try {
			_execute(_getMockHttpServletRequest(objectEntry), user);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof PrincipalException.MustHavePermission);

			Assert.assertTrue(
				StringUtil.startsWith(
					exception.getMessage(),
					"User " + user.getUserId() +
						" must have VIEW permission for"));
		}
		finally {
			_userLocalService.deleteUser(user);
		}

		Assert.assertNull(_fetchLayoutPageTemplateEntry(objectEntry));
	}

	private void _testExecuteWithParameters() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry();

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(objectEntry);

		mockHttpServletRequest.setParameter("p_p_state", "maximized");
		mockHttpServletRequest.setParameter("version", "2");

		MockHttpServletResponse mockHttpServletResponse = _execute(
			mockHttpServletRequest, TestPropsValues.getUser());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(redirectedURL.contains("p_p_state=maximized"));

		Assert.assertTrue(redirectedURL.contains("version=2"));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "path=/cms/compare_content_item")
	private StrutsAction _compareContentItemStrutsAction;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private List<ObjectDefinition> _objectDefinitions;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}
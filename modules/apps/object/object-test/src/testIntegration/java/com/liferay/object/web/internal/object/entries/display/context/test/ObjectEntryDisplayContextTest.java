/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.display.context.ObjectEntryDisplayContext;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
@FeatureFlag("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;
		_dtoConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(), _dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, TestPropsValues.getUser());
		_group = GroupTestUtil.addGroup();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_companyObjectDefinitionA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);
		_companyObjectDefinitionAA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_companyObjectRelationshipA_AA = TreeTestUtil.bind(
			_companyObjectDefinitionA.getObjectDefinitionId(),
			_companyObjectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_companyObjectDefinitionAAA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_companyObjectRelationshipAA_AAA = TreeTestUtil.bind(
			_companyObjectDefinitionAA.getObjectDefinitionId(),
			_companyObjectDefinitionAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_companyObjectEntryA = _addObjectEntry(
			_companyObjectDefinitionA, ObjectDefinitionConstants.SCOPE_COMPANY);

		_companyObjectEntryAA = _addRelatedObjectEntry(
			_companyObjectEntryA, _companyObjectRelationshipA_AA,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_companyObjectEntryAAA = _addRelatedObjectEntry(
			_companyObjectEntryAA, _companyObjectRelationshipAA_AAA,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_siteObjectDefinitionA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);
		_siteObjectDefinitionAA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		_siteObjectRelationshipA_AA = TreeTestUtil.bind(
			_siteObjectDefinitionA.getObjectDefinitionId(),
			_siteObjectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_siteObjectDefinitionAAA = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		_siteObjectRelationshipAA_AAA = TreeTestUtil.bind(
			_siteObjectDefinitionAA.getObjectDefinitionId(),
			_siteObjectDefinitionAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_siteObjectEntryA = _addObjectEntry(
			_siteObjectDefinitionA, _group.getGroupKey());

		_siteObjectEntryAA = _addRelatedObjectEntry(
			_siteObjectEntryA, _siteObjectRelationshipA_AA,
			_group.getGroupKey());

		_siteObjectEntryAAA = _addRelatedObjectEntry(
			_siteObjectEntryAA, _siteObjectRelationshipAA_AAA,
			_group.getGroupKey());
	}

	@Test
	public void testGetAPIURL() throws Exception {
		_testGetAPIURL(
			_companyObjectEntryAA, _companyObjectRelationshipA_AA,
			_companyObjectEntryA);
		_testGetAPIURL(
			_companyObjectEntryAAA, _companyObjectRelationshipAA_AAA,
			_companyObjectEntryAA);
		_testGetAPIURL(
			_siteObjectEntryAA, _siteObjectRelationshipA_AA, _siteObjectEntryA);
		_testGetAPIURL(
			_siteObjectEntryAAA, _siteObjectRelationshipAA_AAA,
			_siteObjectEntryAA);
	}

	@Test
	public void testGetBackURL() throws Exception {

		// Root descendant object entry

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				_companyObjectEntryAAA.getExternalReferenceCode(),
				_companyObjectDefinitionAAA,
				_companyObjectRelationshipAA_AAA.getObjectRelationshipId(),
				_companyObjectEntryAA.getExternalReferenceCode());

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					mockHttpServletRequest,
					_companyObjectDefinitionAA.getPortletId(),
					PortletRequest.ACTION_PHASE)
			).setMVCRenderCommandName(
				"/object_entries/edit_object_entry"
			).setParameter(
				"externalReferenceCode",
				_companyObjectEntryAA.getExternalReferenceCode()
			).setParameter(
				"objectRelationshipId",
				_companyObjectRelationshipA_AA.getObjectRelationshipId()
			).setParameter(
				"parentObjectEntryERC",
				_companyObjectEntryA.getExternalReferenceCode()
			).setParameter(
				"screenNavigationCategoryKey",
				_companyObjectRelationshipAA_AAA.getObjectRelationshipId()
			).buildString(),
			_getBackURL(mockHttpServletRequest));

		// Root object entry

		mockHttpServletRequest = _getMockHttpServletRequest(
			_companyObjectEntryAA.getExternalReferenceCode(),
			_companyObjectDefinitionAA,
			_companyObjectRelationshipA_AA.getObjectRelationshipId(),
			_companyObjectEntryA.getExternalReferenceCode());

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					mockHttpServletRequest,
					_companyObjectDefinitionA.getPortletId(),
					PortletRequest.ACTION_PHASE)
			).setMVCRenderCommandName(
				"/object_entries/edit_object_entry"
			).setParameter(
				"externalReferenceCode",
				_companyObjectEntryA.getExternalReferenceCode()
			).setParameter(
				"screenNavigationCategoryKey",
				_companyObjectRelationshipA_AA.getObjectRelationshipId()
			).buildString(),
			_getBackURL(mockHttpServletRequest));
	}

	@Test
	public void testIsShowScreenNavigation() throws Exception {
		com.liferay.object.model.ObjectEntry objectEntry =
			ObjectEntryTestUtil.addObjectEntry(_companyObjectDefinitionAA);

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(
				_getMockHttpServletRequest(
					objectEntry.getExternalReferenceCode(),
					_companyObjectDefinitionAA, 0L, null));

		Assert.assertFalse(objectEntryDisplayContext.isShowScreenNavigation());
	}

	@Test
	public void testIsShowScreenNavigationWithRootDescendantObjectEntry()
		throws Exception {

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(
				_getMockHttpServletRequest(
					_companyObjectEntryAA.getExternalReferenceCode(),
					_companyObjectDefinitionAA,
					_companyObjectRelationshipA_AA.getObjectRelationshipId(),
					_companyObjectEntryA.getExternalReferenceCode()));

		Assert.assertTrue(objectEntryDisplayContext.isShowScreenNavigation());
	}

	@Test
	public void testIsShowScreenNavigationWithRootObjectEntry()
		throws Exception {

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(
				_getMockHttpServletRequest(
					_companyObjectEntryA.getExternalReferenceCode(),
					_companyObjectDefinitionA, 0L, null));

		Assert.assertTrue(objectEntryDisplayContext.isShowScreenNavigation());
	}

	private static ObjectDefinition _addObjectDefinition(String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				List.of(
					new TextObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"textObjectFieldName"
					).build()),
				Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private static ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, String scopeKey)
		throws Exception {

		return _defaultObjectEntryManager.addObjectEntry(
			_dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = new HashMap<>(Collections.emptyMap());
				}
			},
			scopeKey);
	}

	private static ObjectEntry _addRelatedObjectEntry(
			ObjectEntry objectEntry, ObjectRelationship objectRelationship,
			String scopeKey)
		throws Exception {

		return _defaultObjectEntryManager.addRelatedObjectEntry(
			_dtoConverterContext, objectEntry.getExternalReferenceCode(),
			new ObjectEntry() {
				{
					properties = new HashMap<>(Collections.emptyMap());
				}
			},
			objectRelationship, scopeKey);
	}

	private String _getAPIURL(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(mockHttpServletRequest);

		return objectEntryDisplayContext.getAPIURL();
	}

	private String _getBackURL(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(mockHttpServletRequest);

		return objectEntryDisplayContext.getBackURL();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			long objectRelationshipId, String parentObjectEntryERC)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, objectDefinition);
		mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_READ_ONLY, Boolean.FALSE);
		mockHttpServletRequest.setAttribute(
			WebKeys.PORTLET_ID, objectDefinition.getPortletId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"externalReferenceCode", externalReferenceCode);
		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/object_entries/edit_object_entry");
		mockHttpServletRequest.setParameter(
			"objectRelationshipId", String.valueOf(objectRelationshipId));
		mockHttpServletRequest.setParameter(
			"parentObjectEntryERC", parentObjectEntryERC);

		return mockHttpServletRequest;
	}

	private void _testGetAPIURL(
			ObjectEntry objectEntry, ObjectRelationship objectRelationship,
			ObjectEntry parentObjectEntry)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		String apiURL = StringBundler.concat(
			"/o", objectDefinition.getRESTContextPath(),
			"/by-external-reference-code/",
			parentObjectEntry.getExternalReferenceCode(), "/",
			objectRelationship.getName());

		if (Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			apiURL = StringBundler.concat(
				"/o", objectDefinition.getRESTContextPath(), "/scopes/",
				_group.getGroupId(), "/by-external-reference-code/",
				parentObjectEntry.getExternalReferenceCode(), "/",
				objectRelationship.getName());
		}

		Assert.assertEquals(
			apiURL,
			_getAPIURL(
				_getMockHttpServletRequest(
					objectEntry.getExternalReferenceCode(),
					_objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2()),
					objectRelationship.getObjectRelationshipId(),
					parentObjectEntry.getExternalReferenceCode())));
	}

	private static ObjectDefinition _companyObjectDefinitionA;
	private static ObjectDefinition _companyObjectDefinitionAA;
	private static ObjectDefinition _companyObjectDefinitionAAA;
	private static ObjectEntry _companyObjectEntryA;
	private static ObjectEntry _companyObjectEntryAA;
	private static ObjectEntry _companyObjectEntryAAA;
	private static ObjectRelationship _companyObjectRelationshipA_AA;
	private static ObjectRelationship _companyObjectRelationshipAA_AAA;
	private static DefaultObjectEntryManager _defaultObjectEntryManager;
	private static DTOConverterContext _dtoConverterContext;

	@Inject
	private static DTOConverterRegistry _dtoConverterRegistry;

	private static Group _group;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private static ObjectEntryManager _objectEntryManager;

	@Inject
	private static ObjectRelationshipLocalService
		_objectRelationshipLocalService;

	private static ObjectDefinition _siteObjectDefinitionA;
	private static ObjectDefinition _siteObjectDefinitionAA;
	private static ObjectDefinition _siteObjectDefinitionAAA;
	private static ObjectEntry _siteObjectEntryA;
	private static ObjectEntry _siteObjectEntryAA;
	private static ObjectEntry _siteObjectEntryAAA;
	private static ObjectRelationship _siteObjectRelationshipA_AA;
	private static ObjectRelationship _siteObjectRelationshipAA_AAA;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ObjectEntryDisplayContextFactory _objectEntryDisplayContextFactory;

}
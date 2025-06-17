/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.display.context.ObjectEntryDisplayContext;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletRequest;

import org.junit.Assert;
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

	@Test
	public void testGetBackURL() throws Exception {
		Tree objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build());

		Node nodeA = objectDefinitionTree.getRootNode();

		TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			nodeA.getPrimaryKey());

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		ObjectEntry objectEntryAA1 = _objectEntryLocalService.getObjectEntry(
			"AA1", objectDefinitionAA.getObjectDefinitionId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				objectEntryAA1.getExternalReferenceCode(), objectDefinitionAA);

		ObjectEntry objectEntryA1 = _objectEntryLocalService.getObjectEntry(
			"A1", nodeA.getPrimaryKey());

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				nodeA.getPrimaryKey());

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					mockHttpServletRequest, objectDefinitionA.getPortletId(),
					PortletRequest.ACTION_PHASE)
			).setMVCRenderCommandName(
				"/object_entries/edit_object_entry"
			).setParameter(
				"externalReferenceCode",
				objectEntryA1.getExternalReferenceCode()
			).setParameter(
				"screenNavigationCategoryKey",
				() -> {
					Node nodeAA = objectDefinitionTree.getNode(
						objectDefinitionAA.getPrimaryKey());

					Edge edge = nodeAA.getEdge();

					return edge.getObjectRelationshipId();
				}
			).buildString(),
			_getBackURL(mockHttpServletRequest));

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		ObjectEntry objectEntryAAA1 = _objectEntryLocalService.getObjectEntry(
			"AAA1", objectDefinitionAAA.getObjectDefinitionId());

		mockHttpServletRequest = _getMockHttpServletRequest(
			objectEntryAAA1.getExternalReferenceCode(), objectDefinitionAAA);

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					mockHttpServletRequest, objectDefinitionAA.getPortletId(),
					PortletRequest.ACTION_PHASE)
			).setMVCRenderCommandName(
				"/object_entries/edit_object_entry"
			).setParameter(
				"externalReferenceCode",
				objectEntryAA1.getExternalReferenceCode()
			).setParameter(
				"screenNavigationCategoryKey",
				() -> {
					Node nodeAAA = objectDefinitionTree.getNode(
						objectDefinitionAAA.getPrimaryKey());

					Edge edge = nodeAAA.getEdge();

					return edge.getObjectRelationshipId();
				}
			).buildString(),
			_getBackURL(mockHttpServletRequest));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	private String _getBackURL(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ObjectEntryDisplayContext objectEntryDisplayContext =
			_objectEntryDisplayContextFactory.create(mockHttpServletRequest);

		return objectEntryDisplayContext.getBackURL();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			String externalReferenceCode, ObjectDefinition objectDefinition)
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
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"externalReferenceCode", externalReferenceCode);
		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/object_entries/edit_object_entry");

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryDisplayContextFactory _objectEntryDisplayContextFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}
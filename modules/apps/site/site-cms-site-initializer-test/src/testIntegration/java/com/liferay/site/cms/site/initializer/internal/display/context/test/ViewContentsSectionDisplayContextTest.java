/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class ViewContentsSectionDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Ignore
	@Test
	@TestInfo("LPD-50664")
	public void testGetCreationMenu() throws Exception {
		Map<String, String> expectedResultMap = LinkedHashMapBuilder.put(
			"folder", StringPool.BLANK
		).put(
			"Basic Web Content",
			getHref(
				objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId()))
		).build();

		testGetCreationMenu(
			ReflectionTestUtil.invoke(
				_getViewContentsSectionDisplayContext(
					getMockHttpServletRequest()),
				"getCreationMenu", new Class<?>[0]),
			expectedResultMap);

		ObjectFolder objectFolder =
			objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);

		expectedResultMap.put(
			objectDefinition.getLabel(LocaleUtil.US),
			getHref(objectDefinition));

		addCustomObjectDefinition(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			false, true, ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), false, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, false,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			WorkflowConstants.STATUS_APPROVED);
		addCustomObjectDefinition(
			objectFolder.getObjectFolderId(), true, true,
			ObjectDefinitionConstants.SCOPE_SITE,
			WorkflowConstants.STATUS_DRAFT);

		testGetCreationMenu(
			ReflectionTestUtil.invoke(
				_getViewContentsSectionDisplayContext(
					getMockHttpServletRequest()),
				"getCreationMenu", new Class<?>[0]),
			expectedResultMap);
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 5,
			fdsActionDropdownItems.size());

		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(0), "view", "actionLinkFolder",
			"view-folder", "get", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(1), "pencil", "editFolder", "edit",
			"get", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(2), "pencil", "actionLink", "edit",
			"get", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(3), "password-policies", "permissions",
			"permissions", "get", "item");
		_assertFDSActionDropdownItem(
			fdsActionDropdownItems.get(4), "trash", "delete", "delete",
			"delete", "item");
	}

	private void _assertFDSActionDropdownItem(
		FDSActionDropdownItem fdsActionDropdownItem, String icon, String id,
		String label, String method, String type) {

		Assert.assertNotNull(fdsActionDropdownItem);

		Map<String, String> data =
			(Map<String, String>)fdsActionDropdownItem.get("data");

		Assert.assertEquals(id, data.get("id"));
		Assert.assertEquals(method, data.get("method"));

		Assert.assertEquals(icon, fdsActionDropdownItem.get("icon"));
		Assert.assertEquals(label, fdsActionDropdownItem.get("label"));
		Assert.assertEquals(type, fdsActionDropdownItem.get("type"));
	}

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems()
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getViewContentsSectionDisplayContext(getMockHttpServletRequest()),
			"getFDSActionDropdownItems", new Class<?>[0]);
	}

	private Object _getViewContentsSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object contentsSectionDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewContentsSectionDisplayContext");

		Assert.assertNotNull(contentsSectionDisplayContext);

		return contentsSectionDisplayContext;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewContentsJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}
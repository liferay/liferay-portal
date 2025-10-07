/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.item.selector;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectEntryItemDescriptorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-63670")
	public void testGetPayload() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		String scopeExternalReferenceCode = RandomTestUtil.randomString();

		_setUpGroupLocalService(groupId, scopeExternalReferenceCode);

		String className = RandomTestUtil.randomString();

		_setUpObjectDefinition(className);

		long classPK = RandomTestUtil.randomLong();
		String externalReferenceCode = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		_setUpObjectEntry(classPK, externalReferenceCode, groupId, title);

		long classNameId = RandomTestUtil.randomLong();

		_setUpPortal(className, classNameId);

		_setUpThemeDisplay(groupId);

		ObjectEntryItemDescriptor objectEntryItemDescriptor =
			new ObjectEntryItemDescriptor(
				_groupLocalService, _httpServletRequest, _objectDefinition,
				_objectEntry, _portal);

		Assert.assertEquals(
			JSONUtil.put(
				"className", className
			).put(
				"classNameId", classNameId
			).put(
				"classPK", classPK
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"title", title
			).toString(),
			objectEntryItemDescriptor.getPayload());

		Mockito.verifyNoInteractions(_groupLocalService);

		_setUpThemeDisplay(RandomTestUtil.randomLong());

		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_COMPANY
		);

		Assert.assertEquals(
			JSONUtil.put(
				"className", className
			).put(
				"classNameId", classNameId
			).put(
				"classPK", classPK
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"title", title
			).toString(),
			objectEntryItemDescriptor.getPayload());

		Mockito.verifyNoInteractions(_groupLocalService);

		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_SITE
		);

		Assert.assertEquals(
			JSONUtil.put(
				"className", className
			).put(
				"classNameId", classNameId
			).put(
				"classPK", classPK
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode", scopeExternalReferenceCode
			).put(
				"title", title
			).toString(),
			objectEntryItemDescriptor.getPayload());

		Mockito.verify(
			_groupLocalService
		).fetchGroup(
			groupId
		);
	}

	private void _setUpGroupLocalService(
		long groupId, String scopeExternalReferenceCode) {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			scopeExternalReferenceCode
		);

		Mockito.when(
			_groupLocalService.fetchGroup(groupId)
		).thenReturn(
			group
		);
	}

	private void _setUpObjectDefinition(String className) {
		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_objectDefinition.isDefaultStorageType()
		).thenReturn(
			Boolean.TRUE
		);
	}

	private void _setUpObjectEntry(
			long classPK, String externalReferenceCode, long groupId,
			String title)
		throws Exception {

		Mockito.when(
			_objectEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			_objectEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_objectEntry.getObjectEntryId()
		).thenReturn(
			classPK
		);

		Mockito.when(
			_objectEntry.getTitleValue()
		).thenReturn(
			title
		);
	}

	private void _setUpPortal(String className, long classNameId) {
		Mockito.when(
			_portal.getClassNameId(className)
		).thenReturn(
			classNameId
		);
	}

	private void _setUpThemeDisplay(long groupId) throws Exception {
		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			groupId
		);

		_httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final HttpServletRequest _httpServletRequest =
		new MockHttpServletRequest();
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}
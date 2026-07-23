/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.item.selector;

import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class ObjectEntryItemSelectorViewTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTitle() throws Exception {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		Locale locale = LocaleUtil.getDefault();
		String title = RandomTestUtil.randomString();

		Mockito.when(
			objectDefinition.getPluralLabel(locale)
		).thenReturn(
			title
		);

		Mockito.when(
			objectDefinition.isCMS()
		).thenReturn(
			false
		);

		_assertGetTitle(title, locale, objectDefinition);

		Mockito.when(
			objectDefinition.isCMS()
		).thenReturn(
			true
		);

		_assertGetTitle(
			StringUtil.appendParentheticalSuffix(title, "CMS"), locale,
			objectDefinition);
	}

	private void _assertGetTitle(
		String expectedTitle, Locale locale,
		ObjectDefinition objectDefinition) {

		ObjectEntryItemSelectorView objectEntryItemSelectorView =
			_getObjectEntryItemSelectorView(objectDefinition);

		Assert.assertEquals(
			expectedTitle, objectEntryItemSelectorView.getTitle(locale));
	}

	private ObjectEntryItemSelectorView _getObjectEntryItemSelectorView(
		ObjectDefinition objectDefinition) {

		return new ObjectEntryItemSelectorView(
			Mockito.mock(GroupLocalService.class),
			Mockito.mock(InfoPermissionProvider.class),
			Mockito.mock(ItemSelectorViewDescriptorRenderer.class),
			objectDefinition, Mockito.mock(ObjectEntryManager.class),
			Mockito.mock(ObjectRelatedModelsProviderRegistry.class),
			Mockito.mock(ObjectScopeProviderRegistry.class),
			Mockito.mock(Portal.class));
	}

}
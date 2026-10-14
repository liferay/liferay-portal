/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.util.CETUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

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
public class ClientExtensionEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddClientExtensionEntry() throws Exception {
		ClientExtensionEntry clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"url", "http://" + RandomTestUtil.randomString() + ".com"
				).buildString());

		int intialCount =
			_clientExtensionEntryRelLocalService.
				getClientExtensionEntryRelsCount();

		LayoutSet publicLayoutSet = _group.getPublicLayoutSet();

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).put(
				"scriptLocation", "head"
			).build();

		_clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(LayoutSet.class),
			publicLayoutSet.getLayoutSetId(),
			clientExtensionEntry.getExternalReferenceCode(),
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
			typeSettingsUnicodeProperties.toString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			intialCount + 1,
			_clientExtensionEntryRelLocalService.
				getClientExtensionEntryRelsCount());

		_clientExtensionEntryLocalService.deleteClientExtensionEntry(
			clientExtensionEntry.getClientExtensionEntryId());

		Assert.assertEquals(
			intialCount,
			_clientExtensionEntryRelLocalService.
				getClientExtensionEntryRelsCount());

		clientExtensionEntry =
			_clientExtensionEntryLocalService.addClientExtensionEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				StringPool.BLANK, StringPool.BLANK,
				ClientExtensionEntryConstants.TYPE_CUSTOM_ELEMENT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"htmlElementName", "valid-html-element-name"
				).put(
					"instanceable", false
				).put(
					"panelAppOrder", "100"
				).put(
					"panelCategoryKey",
					PortletCategoryKeys.SITE_ADMINISTRATION_CONTENT
				).put(
					"urls", "http://" + RandomTestUtil.randomString() + ".com"
				).buildString());

		Portlet portlet = _portletLocalService.getPortletById(
			TestPropsValues.getCompanyId(),
			StringBundler.concat(
				"com_liferay_client_extension_web_internal_portlet_",
				"ClientExtensionEntryPortlet_", TestPropsValues.getCompanyId(),
				StringPool.UNDERLINE,
				CETUtil.normalizeExternalReferenceCodeForPortletId(
					clientExtensionEntry.getExternalReferenceCode())));

		Assert.assertEquals(
			PortletCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			portlet.getControlPanelEntryCategory());

		_clientExtensionEntryLocalService.deleteClientExtensionEntry(
			clientExtensionEntry.getClientExtensionEntryId());
	}

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

}
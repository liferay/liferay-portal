/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.provider;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMConnectorChannelFieldDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMConnectorChannelFieldFDSActionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSActionProvider, "_language", _language);
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_language.get(Mockito.eq(_httpServletRequest), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);
	}

	@Test
	public void testGetDropdownItems() {
		List<DropdownItem> dropdownItems =
			_pimConnectorChannelFieldFDSActionProvider.getDropdownItems(
				_GROUP_ID, _httpServletRequest,
				new PIMConnectorChannelFieldDisplay(
					_API_URL, "SKU", Collections.emptyList(), _HREF,
					LocaleUtil.US, false, Collections.emptyList()));

		Assert.assertEquals(dropdownItems.toString(), 2, dropdownItems.size());

		DropdownItem dropdownItem = dropdownItems.get(0);

		Assert.assertEquals(_HREF, dropdownItem.get("href"));
		Assert.assertEquals("pencil", dropdownItem.get("icon"));
		Assert.assertEquals("edit", dropdownItem.get("label"));
		Assert.assertEquals("edit", _getDataValue(dropdownItem, "id"));

		dropdownItems =
			_pimConnectorChannelFieldFDSActionProvider.getDropdownItems(
				_GROUP_ID, _httpServletRequest,
				new PIMConnectorChannelFieldDisplay(
					_API_URL, "SKU", Arrays.asList(1L, 2L), _HREF,
					LocaleUtil.US, false, Collections.emptyList()));

		Assert.assertEquals(dropdownItems.toString(), 2, dropdownItems.size());

		dropdownItem = dropdownItems.get(1);

		Assert.assertEquals("text-danger", dropdownItem.get("className"));
		Assert.assertEquals("times-circle", dropdownItem.get("icon"));
		Assert.assertEquals("clear", dropdownItem.get("label"));
		Assert.assertEquals(_API_URL, _getDataValue(dropdownItem, "apiURL"));
		Assert.assertEquals(
			"are-you-sure-you-want-to-clear-this-mapping",
			_getDataValue(dropdownItem, "confirmationMessage"));
		Assert.assertEquals("clear", _getDataValue(dropdownItem, "id"));
	}

	private Object _getDataValue(DropdownItem dropdownItem, String key) {
		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		return data.get(key);
	}

	private static final String _API_URL = "/o/pim/connector-field-mappings";

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _HREF =
		"/web/pim/edit-field-mappings?objectEntryId=3&channelField=" +
			"skus%5B%5D.sku";

	private HttpServletRequest _httpServletRequest;
	private final Language _language = Mockito.mock(Language.class);
	private final PIMConnectorChannelFieldFDSActionProvider
		_pimConnectorChannelFieldFDSActionProvider =
			new PIMConnectorChannelFieldFDSActionProvider();

}
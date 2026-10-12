/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gianmarco Brunialti Masera
 */
@FeatureFlag("LPD-109588")
@RunWith(Arquillian.class)
public class CPDefinitionAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId(), commerceCurrency.getCode());

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(CProduct.class),
				_cpDefinition.getCProductId());

		_friendlyURL =
			_friendlyURLResolver.getURLSeparator() +
				friendlyURLEntry.getUrlTitle();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				_userLocalService.getGuestUser(_group.getCompanyId())));
	}

	@Test
	public void testGetActualURLWhenGuestBrowsingIsDisabled() throws Exception {
		CommerceTestUtil.runWithGuestBrowsingDisabled(
			_commerceChannel.getGroupId(),
			() -> {
				Assert.assertThrows(
					NoSuchCPDefinitionException.class, () -> _getActualURL());

				try (ConfigurationTemporarySwapper
						configurationTemporarySwapper =
							new ConfigurationTemporarySwapper(
								_PID,
								HashMapDictionaryBuilder.<String, Object>put(
									"promptEnabled", true
								).build())) {

					String actualURL = _getActualURL();

					Assert.assertTrue(
						actualURL, actualURL.startsWith("/c/portal/login?"));
					Assert.assertEquals(
						URLCodec.encodeURL(_getCurrentURL()),
						HttpComponentsUtil.getParameter(
							actualURL, "redirect", false));
				}
			});
	}

	private String _getActualURL() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setRequestURI(_getCurrentURL());

		return _friendlyURLResolver.getActualURL(
			_group.getCompanyId(), _group.getGroupId(), false, "/c",
			_friendlyURL, Collections.emptyMap(),
			HashMapBuilder.<String, Object>put(
				"request", mockHttpServletRequest
			).build());
	}

	private String _getCurrentURL() {
		return "/web" + _group.getFriendlyURL() + _friendlyURL;
	}

	private static final String _PID =
		"com.liferay.login.web.internal.configuration.AuthLoginConfiguration";

	private CommerceChannel _commerceChannel;
	private CPDefinition _cpDefinition;
	private String _friendlyURL;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.content.web.internal.asset.display.page.portlet.CPDefinitionAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brian I. Kim
 */
public class PaymentProcessCheckoutStepDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		Mockito.when(
			_liferayPortletResponse.createRenderURL()
		).thenReturn(
			_liferayPortletURL
		);

		Mockito.when(
			_liferayPortletURL.toString()
		).thenReturn(
			"http://localhost/checkout"
		);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getLiferayPortletResponse(Mockito.any())
		).thenReturn(
			_liferayPortletResponse
		);

		portalUtil.setPortal(portal);
	}

	@Test
	public void testGetCallbackURL() {
		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-69290"))
			).thenReturn(
				false
			);

			Assert.assertEquals(
				StringPool.BLANK, _getCallbackURL(true, "shipping"));

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-69290"))
			).thenReturn(
				true
			);

			Assert.assertEquals(StringPool.BLANK, _getCallbackURL(false, null));
			Assert.assertEquals(
				StringPool.BLANK, _getCallbackURL(false, "shipping"));
			Assert.assertEquals(
				URLCodec.encodeURL("http://localhost/checkout"),
				_getCallbackURL(true, "shipping"));

			Mockito.verify(
				_liferayPortletURL
			).setParameter(
				"checkoutStepName", "shipping"
			);

			Mockito.verify(
				_liferayPortletURL
			).setParameter(
				"commerceOrderUuid", "commerceOrderUuid"
			);
		}
	}

	private String _getCallbackURL(
		boolean continueAfterPayment, String nextCommerceCheckoutStepName) {

		CommerceOrder commerceOrder = Mockito.mock(CommerceOrder.class);

		Mockito.when(
			commerceOrder.getUuid()
		).thenReturn(
			"commerceOrderUuid"
		);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER)
		).thenReturn(
			commerceOrder
		);

		Mockito.when(
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE)
		).thenReturn(
			Mockito.mock(PortletResponse.class)
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		CommerceCheckoutStep nextCommerceCheckoutStep = null;

		if (nextCommerceCheckoutStepName != null) {
			nextCommerceCheckoutStep = Mockito.mock(CommerceCheckoutStep.class);

			Mockito.when(
				nextCommerceCheckoutStep.getName()
			).thenReturn(
				nextCommerceCheckoutStepName
			);

			Mockito.when(
				nextCommerceCheckoutStep.isContinueAfterPayment()
			).thenReturn(
				continueAfterPayment
			);
		}

		return ReflectionTestUtil.invoke(
			new PaymentProcessCheckoutStepDisplayContext(
				null, httpServletRequest, nextCommerceCheckoutStep),
			"_getCallbackURL", new Class<?>[0]);
	}

	private static final LiferayPortletResponse _liferayPortletResponse =
		Mockito.mock(LiferayPortletResponse.class);
	private static final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);

}
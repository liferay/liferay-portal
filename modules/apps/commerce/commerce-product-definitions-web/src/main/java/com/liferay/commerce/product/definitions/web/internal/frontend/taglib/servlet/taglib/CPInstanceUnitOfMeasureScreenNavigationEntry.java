/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.definitions.web.internal.display.context.CPInstanceUnitOfMeasureDisplayContext;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CPInstanceUnitOfMeasureScreenNavigationEntry
	extends CPInstanceUnitOfMeasureScreenNavigationCategory
	implements ScreenNavigationEntry<CPInstance> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CPInstance cpInstance) {
		if (cpInstance != null) {
			return true;
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			CPInstanceUnitOfMeasureDisplayContext
				cpInstanceUnitOfMeasureDisplayContext =
					new CPInstanceUnitOfMeasureDisplayContext(
						_actionHelper, httpServletRequest,
						_commerceCurrencyLocalService,
						_commerceProductPriceCalculation,
						_cpInstanceUnitOfMeasureService);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpInstanceUnitOfMeasureDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/view_cp_instance_unit_of_measures.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPInstanceUnitOfMeasureScreenNavigationEntry.class);

	@Reference
	private ActionHelper _actionHelper;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.definitions.web)"
	)
	private ServletContext _servletContext;

}
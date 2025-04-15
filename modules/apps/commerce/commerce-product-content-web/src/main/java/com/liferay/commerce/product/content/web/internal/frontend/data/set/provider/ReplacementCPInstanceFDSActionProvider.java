/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.product.content.web.internal.constants.CPContentFDSNames;
import com.liferay.commerce.product.content.web.internal.model.ReplacementSku;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CPContentFDSNames.REPLACEMENT_CP_INSTANCES,
	service = FDSActionProvider.class
)
public class ReplacementCPInstanceFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		ReplacementSku replacementSku = (ReplacementSku)model;

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			replacementSku.getReplacementSkuId());

		return Collections.singletonList(
			DropdownItemBuilder.setHref(
				_cpDefinitionHelper.getFriendlyURL(
					cpInstance.getCPDefinitionId(),
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY))
			).setLabel(
				_language.get(httpServletRequest, "view")
			).build());
	}

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

}
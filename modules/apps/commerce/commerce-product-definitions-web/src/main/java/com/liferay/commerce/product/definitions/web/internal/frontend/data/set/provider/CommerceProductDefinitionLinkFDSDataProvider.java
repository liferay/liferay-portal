/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.frontend.model.ImageField;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.product.definitions.web.internal.constants.CommerceProductFDSNames;
import com.liferay.commerce.product.definitions.web.internal.model.ProductLink;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceProductFDSNames.PRODUCT_LINKS,
	service = FDSDataProvider.class
)
public class CommerceProductDefinitionLinkFDSDataProvider
	implements FDSDataProvider<ProductLink> {

	@Override
	public List<ProductLink> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<ProductLink> productLinks = new ArrayList<>();

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
				DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
				themeDisplay.getTimeZone());

			long cpDefinitionId = ParamUtil.getLong(
				httpServletRequest, "cpDefinitionId");

			List<CPDefinitionLink> cpDefinitionLinks =
				_cpDefinitionLinkService.getCPDefinitionLinks(
					cpDefinitionId, fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition());

			for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
				CProduct cProduct = cpDefinitionLink.getCProduct();

				CPDefinition cpDefinition =
					_cpDefinitionLocalService.getCPDefinition(
						cProduct.getPublishedCPDefinitionId());

				String name = cpDefinition.getName(
					_language.getLanguageId(
						_portal.getLocale(httpServletRequest)));

				String statusDisplayStyle = StringPool.BLANK;

				if (cpDefinitionLink.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) {

					statusDisplayStyle = "success";
				}

				productLinks.add(
					new ProductLink(
						cpDefinitionLink.getCPDefinitionLinkId(),
						dateTimeFormat.format(cpDefinitionLink.getCreateDate()),
						new ImageField(
							name, "rounded", "lg",
							cpDefinition.getDefaultImageThumbnailSrc(
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN)),
						name, cpDefinitionLink.getPriority(),
						_language.get(
							httpServletRequest, cpDefinitionLink.getType()),
						new LabelField(
							statusDisplayStyle,
							_language.get(
								httpServletRequest,
								WorkflowConstants.getStatusLabel(
									cpDefinitionLink.getStatus())))));
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return productLinks;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpDefinitionId = ParamUtil.getLong(
			httpServletRequest, "cpDefinitionId");

		return _cpDefinitionLinkService.getCPDefinitionLinksCount(
			cpDefinitionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceProductDefinitionLinkFDSDataProvider.class);

	@Reference
	private CPDefinitionLinkService _cpDefinitionLinkService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
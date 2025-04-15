/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.portlet.action.CommercePriceListActionHelper;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CPInstanceCommerceTierPriceEntryDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPInstanceCommerceTierPriceEntryDisplayContext(
		ActionHelper actionHelper,
		CommercePriceListActionHelper commercePriceListActionHelper,
		CPInstanceLocalService cpInstanceLocalService,
		HttpServletRequest httpServletRequest) {

		super(actionHelper, httpServletRequest);

		_commercePriceListActionHelper = commercePriceListActionHelper;
		_cpInstanceLocalService = cpInstanceLocalService;
	}

	public CommercePriceEntry getCommercePriceEntry() throws PortalException {
		return _commercePriceListActionHelper.getCommercePriceEntry(
			cpRequestHelper.getRenderRequest());
	}

	public long getCommercePriceEntryId() throws PortalException {
		long commercePriceEntryId = 0;

		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		if (commercePriceEntry != null) {
			commercePriceEntryId = commercePriceEntry.getCommercePriceEntryId();
		}

		return commercePriceEntryId;
	}

	public CommerceTierPriceEntry getCommerceTierPriceEntry()
		throws PortalException {

		if (_commerceTierPriceEntry != null) {
			return _commerceTierPriceEntry;
		}

		_commerceTierPriceEntry =
			_commercePriceListActionHelper.getCommerceTierPriceEntry(
				cpRequestHelper.getRenderRequest());

		return _commerceTierPriceEntry;
	}

	public long getCommerceTierPriceEntryId() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return 0;
		}

		return commerceTierPriceEntry.getCommerceTierPriceEntryId();
	}

	public String getContextTitle() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return LanguageUtil.get(
				themeDisplay.getRequest(), "add-tier-price-entry");
		}

		StringBundler sb = new StringBundler(5);

		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		if (commercePriceEntry != null) {
			CPInstance cpInstance =
				_cpInstanceLocalService.fetchCProductInstance(
					commercePriceEntry.getCProductId(),
					commercePriceEntry.getCPInstanceUuid());

			if (cpInstance != null) {
				CPDefinition cpDefinition = cpInstance.getCPDefinition();

				if (cpDefinition != null) {
					sb.append(
						cpDefinition.getName(themeDisplay.getLanguageId()));
					sb.append(" - ");
					sb.append(cpInstance.getSku());

					CommercePriceList commercePriceList =
						commercePriceEntry.getCommercePriceList();

					if (commercePriceList != null) {
						sb.append(" - ");
						sb.append(commercePriceList.getName());
					}
				}
			}
		}

		return sb.toString();
	}

	public CPInstance getCPInstance() throws PortalException {
		if (_cpInstance != null) {
			return _cpInstance;
		}

		_cpInstance = actionHelper.getCPInstance(
			cpRequestHelper.getRenderRequest());

		return _cpInstance;
	}

	public long getCPInstanceId() throws PortalException {
		long cpInstanceId = 0;

		CPInstance cpInstance = getCPInstance();

		if (cpInstance != null) {
			cpInstanceId = cpInstance.getCPInstanceId();
		}

		return cpInstanceId;
	}

	public CreationMenu getCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getAddCommerceTierPriceEntryURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-new-price-tier"));
				dropdownItem.setTarget("modal-lg");
			}
		).build();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"viewCPInstanceCommerceTierPriceEntries"
		).setParameter(
			"commercePriceEntryId", getCommercePriceEntryId()
		).setParameter(
			"cpInstanceId", getCPInstanceId()
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return "price-lists";
	}

	public boolean hasCustomAttributes() throws Exception {
		return CustomAttributesUtil.hasCustomAttributes(
			cpRequestHelper.getCompanyId(),
			CommerceTierPriceEntry.class.getName(),
			getCommerceTierPriceEntryId(), null);
	}

	private String _getAddCommerceTierPriceEntryURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_instance_commerce_tier_price_entry"
		).setRedirect(
			cpRequestHelper.getCurrentURL()
		).setParameter(
			"commercePriceEntryId", getCommercePriceEntryId()
		).setParameter(
			"cpDefinitionId", getCPDefinitionId()
		).setParameter(
			"cpInstanceId", getCPInstanceId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private final CommercePriceListActionHelper _commercePriceListActionHelper;
	private CommerceTierPriceEntry _commerceTierPriceEntry;
	private CPInstance _cpInstance;
	private final CPInstanceLocalService _cpInstanceLocalService;

}
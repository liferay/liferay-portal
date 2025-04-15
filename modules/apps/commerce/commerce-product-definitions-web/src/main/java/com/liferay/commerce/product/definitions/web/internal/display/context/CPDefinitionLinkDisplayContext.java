/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.item.selector.criterion.CPDefinitionItemSelectorCriterion;
import com.liferay.commerce.product.links.CPDefinitionLinkTypeRegistry;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPDefinitionLinkService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionLinkDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionLinkDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPDefinitionLinkService cpDefinitionLinkService,
		CPDefinitionLinkTypeRegistry cpDefinitionLinkTypeRegistry,
		ItemSelector itemSelector,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		super(actionHelper, httpServletRequest);

		_cpDefinitionLinkService = cpDefinitionLinkService;
		_cpDefinitionLinkTypeRegistry = cpDefinitionLinkTypeRegistry;
		_itemSelector = itemSelector;
		_workflowDefinitionLinkLocalService =
			workflowDefinitionLinkLocalService;
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			DropdownItemBuilder.putData(
				Constants.ACTION, "bulkDeleteProductRelations"
			).putData(
				Constants.CMD, Constants.DELETE
			).setHref(
				PortletURLBuilder.createActionURL(
					cpRequestHelper.getRenderResponse()
				).setActionName(
					"/cp_definitions/edit_cp_definition_link"
				).setCMD(
					Constants.DELETE
				).buildString()
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setQuickAction(
				true
			).build());
	}

	public CPDefinitionLink getCPDefinitionLink() throws PortalException {
		if (_cpDefinitionLink != null) {
			return _cpDefinitionLink;
		}

		_cpDefinitionLink = actionHelper.getCPDefinitionLink(
			cpRequestHelper.getRenderRequest());

		return _cpDefinitionLink;
	}

	public long getCPDefinitionLinkId() throws PortalException {
		CPDefinitionLink cpDefinitionLink = getCPDefinitionLink();

		if (cpDefinitionLink == null) {
			return 0;
		}

		return cpDefinitionLink.getCPDefinitionLinkId();
	}

	public String[] getCPDefinitionLinkTypes() {
		return ArrayUtil.toStringArray(
			_cpDefinitionLinkTypeRegistry.getTypes());
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		for (String type : getCPDefinitionLinkTypes()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						StringBundler.concat(
							liferayPortletResponse.getNamespace(),
							"addCommerceProductDefinitionLink", type));
					dropdownItem.setLabel(
						LanguageUtil.format(
							httpServletRequest, "add-x-product", type, true));
					dropdownItem.setTarget("event");
				});
		}

		return creationMenu;
	}

	public String getItemSelectorUrl(String type) throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		CPDefinitionItemSelectorCriterion cpDefinitionItemSelectorCriterion =
			new CPDefinitionItemSelectorCriterion();

		cpDefinitionItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, "productDefinitionsSelectItem",
			cpDefinitionItemSelectorCriterion);

		long cpDefinitionId = getCPDefinitionId();

		if (cpDefinitionId > 0) {
			itemSelectorURL.setParameter(
				"cpDefinitionId", String.valueOf(cpDefinitionId));

			String checkedCPDefinitionIds = StringUtil.merge(
				_getCheckedCPDefinitionIds(cpDefinitionId, type));

			String disabledCPDefinitionIds = StringUtil.merge(
				_getDisabledCPDefinitionIds(cpDefinitionId, type));

			itemSelectorURL.setParameter(
				"checkedCPDefinitionIds", checkedCPDefinitionIds);
			itemSelectorURL.setParameter(
				"disabledCPDefinitionIds", disabledCPDefinitionIds);
		}

		return itemSelectorURL.toString();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId", getCPDefinitionId()
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.
			CATEGORY_KEY_PRODUCT_RELATIONS;
	}

	public boolean hasCustomAttributesAvailable() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return CustomAttributesUtil.hasCustomAttributes(
			themeDisplay.getCompanyId(), CPDefinitionLink.class.getName(),
			getCPDefinitionLinkId(), null);
	}

	public boolean hasWorkflowDefinitionLink() {
		return _workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
			cpRequestHelper.getCompanyId(), cpRequestHelper.getScopeGroupId(),
			CPDefinitionLink.class.getName());
	}

	private long[] _getCheckedCPDefinitionIds(long cpDefinitionId, String type)
		throws PortalException {

		List<Long> cpDefinitionIdsList = new ArrayList<>();

		List<CPDefinitionLink> cpDefinitionLinks = _getCPDefinitionLinks(
			cpDefinitionId, type);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			CProduct cProduct = cpDefinitionLink.getCProduct();

			cpDefinitionIdsList.add(cProduct.getPublishedCPDefinitionId());
		}

		if (!cpDefinitionIdsList.isEmpty()) {
			return ArrayUtil.toLongArray(cpDefinitionIdsList);
		}

		return new long[0];
	}

	private List<CPDefinitionLink> _getCPDefinitionLinks(
			long cpDefinitionId, String type)
		throws PortalException {

		return _cpDefinitionLinkService.getCPDefinitionLinks(
			cpDefinitionId, type);
	}

	private long[] _getDisabledCPDefinitionIds(long cpDefinitionId, String type)
		throws PortalException {

		List<Long> cpDefinitionIdsList = new ArrayList<>();

		List<CPDefinitionLink> cpDefinitionLinks = _getCPDefinitionLinks(
			cpDefinitionId, type);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			cpDefinitionIdsList.add(cpDefinitionLink.getCPDefinitionId());
		}

		if (!cpDefinitionIdsList.isEmpty()) {
			return ArrayUtil.toLongArray(cpDefinitionIdsList);
		}

		return new long[0];
	}

	private CPDefinitionLink _cpDefinitionLink;
	private final CPDefinitionLinkService _cpDefinitionLinkService;
	private final CPDefinitionLinkTypeRegistry _cpDefinitionLinkTypeRegistry;
	private final ItemSelector _itemSelector;
	private final WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
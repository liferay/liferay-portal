/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.display.context;

import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.shop.by.diagram.configuration.CSDiagramSettingImageConfiguration;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingService;
import com.liferay.commerce.shop.by.diagram.type.CSDiagramType;
import com.liferay.commerce.shop.by.diagram.type.CSDiagramTypeRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CSDiagramSettingDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CSDiagramSettingDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CSDiagramSettingImageConfiguration csDiagramSettingImageConfiguration,
		CSDiagramSettingService csDiagramSettingService,
		CSDiagramTypeRegistry csDiagramTypeRegistry,
		ItemSelector itemSelector) {

		super(actionHelper, httpServletRequest);

		_csDiagramSettingImageConfiguration =
			csDiagramSettingImageConfiguration;
		_csDiagramSettingService = csDiagramSettingService;
		_csDiagramTypeRegistry = csDiagramTypeRegistry;
		_itemSelector = itemSelector;
	}

	public CSDiagramSetting fetchCSDiagramSetting() throws PortalException {
		if (_csDiagramSetting != null) {
			return _csDiagramSetting;
		}

		_csDiagramSetting =
			_csDiagramSettingService.fetchCSDiagramSettingByCPDefinitionId(
				getCPDefinitionId());

		return _csDiagramSetting;
	}

	public FileEntry fetchFileEntry() throws PortalException {
		CSDiagramSetting csDiagramSetting = fetchCSDiagramSetting();

		if (csDiagramSetting == null) {
			return null;
		}

		try {
			CPAttachmentFileEntry cpAttachmentFileEntry =
				csDiagramSetting.getCPAttachmentFileEntry();

			return cpAttachmentFileEntry.fetchFileEntry();
		}
		catch (NoSuchCPAttachmentFileEntryException
					noSuchCPAttachmentFileEntryException) {

			if (_log.isInfoEnabled()) {
				_log.info(noSuchCPAttachmentFileEntryException);
			}

			return null;
		}
	}

	public String getCSDiagramEntriesAPIURL() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();

		return "/o/headless-commerce-admin-catalog/v1.0/products/" +
			cpDefinition.getCProductId() + "/mapped-products";
	}

	public CSDiagramType getCSDiagramType(String type) {
		return _csDiagramTypeRegistry.getCSDiagramType(type);
	}

	public List<CSDiagramType> getCSDiagramTypes() {
		return _csDiagramTypeRegistry.getCSDiagramTypes();
	}

	public String[] getImageExtensions() {
		return _csDiagramSettingImageConfiguration.imageExtensions();
	}

	public String getImageItemSelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		ImageItemSelectorCriterion imageItemSelectorCriterion =
			new ImageItemSelectorCriterion();

		imageItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "addFileEntry",
				imageItemSelectorCriterion));
	}

	public long getImageMaxSize() {
		return _csDiagramSettingImageConfiguration.imageMaxSize();
	}

	public double getRadius() {
		return _csDiagramSettingImageConfiguration.radius();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		CPType cpType = null;

		try {
			cpType = getCPType();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if (cpType != null) {
			return cpType.getName();
		}

		return super.getScreenNavigationCategoryKey();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CSDiagramSettingDisplayContext.class);

	private CSDiagramSetting _csDiagramSetting;
	private final CSDiagramSettingImageConfiguration
		_csDiagramSettingImageConfiguration;
	private final CSDiagramSettingService _csDiagramSettingService;
	private final CSDiagramTypeRegistry _csDiagramTypeRegistry;
	private final ItemSelector _itemSelector;

}
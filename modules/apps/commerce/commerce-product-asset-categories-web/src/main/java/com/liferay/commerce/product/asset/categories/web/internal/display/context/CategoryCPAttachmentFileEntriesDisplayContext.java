/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.display.context;

import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * @author Alessio Antonio Rendina
 */
public class CategoryCPAttachmentFileEntriesDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CategoryCPAttachmentFileEntriesDisplayContext(
			ActionHelper actionHelper,
			AttachmentsConfiguration attachmentsConfiguration,
			HttpServletRequest httpServletRequest, ItemSelector itemSelector)
		throws PortalException {

		super(actionHelper, httpServletRequest);

		_attachmentsConfiguration = attachmentsConfiguration;
		_itemSelector = itemSelector;
	}

	public CPAttachmentFileEntry getCPAttachmentFileEntry()
		throws PortalException {

		if (_cpAttachmentFileEntry != null) {
			return _cpAttachmentFileEntry;
		}

		_cpAttachmentFileEntry = actionHelper.getCPAttachmentFileEntry(
			cpRequestHelper.getRenderRequest());

		return _cpAttachmentFileEntry;
	}

	public String[] getImageExtensions() {
		return _attachmentsConfiguration.imageExtensions();
	}

	public long getImageMaxSize() {
		return _attachmentsConfiguration.imageMaxSize();
	}

	public String getItemSelectorUrl() {
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
				requestBackedPortletURLFactory,
				"addCategoryCPAttachmentFileEntry",
				imageItemSelectorCriterion));
	}

	private final AttachmentsConfiguration _attachmentsConfiguration;
	private CPAttachmentFileEntry _cpAttachmentFileEntry;
	private final ItemSelector _itemSelector;

}
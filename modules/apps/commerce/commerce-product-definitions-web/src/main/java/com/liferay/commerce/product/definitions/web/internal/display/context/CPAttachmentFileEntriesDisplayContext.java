/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.display.context;

import com.liferay.commerce.product.configuration.AttachmentsConfiguration;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.definitions.web.internal.security.permission.resource.CommerceCatalogPermission;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPAttachmentFileEntriesDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPAttachmentFileEntriesDisplayContext(
		ActionHelper actionHelper,
		AttachmentsConfiguration attachmentsConfiguration,
		CommerceOptionTypeRegistry commerceOptionTypeRegistry,
		CPAttachmentFileEntryService cpAttachmentFileEntryService,
		CPDefinitionOptionRelService cpDefinitionOptionRelService,
		CPInstanceHelper cpInstanceHelper,
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext,
		HttpServletRequest httpServletRequest, ItemSelector itemSelector) {

		super(actionHelper, httpServletRequest);

		_attachmentsConfiguration = attachmentsConfiguration;
		_commerceOptionTypeRegistry = commerceOptionTypeRegistry;
		_cpAttachmentFileEntryService = cpAttachmentFileEntryService;
		_cpDefinitionOptionRelService = cpDefinitionOptionRelService;
		_cpInstanceHelper = cpInstanceHelper;
		_dlMimeTypeDisplayContext = dlMimeTypeDisplayContext;
		_itemSelector = itemSelector;
	}

	public String getAttachmentItemSelectorUrl() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "addCPAttachmentFileEntry",
				fileItemSelectorCriterion));
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

	public long getCPAttachmentFileEntryId() throws PortalException {
		CPAttachmentFileEntry cpAttachmentFileEntry =
			getCPAttachmentFileEntry();

		if (cpAttachmentFileEntry == null) {
			return 0;
		}

		return cpAttachmentFileEntry.getCPAttachmentFileEntryId();
	}

	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels()
		throws PortalException {

		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return Collections.emptyList();
		}

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRels(
			cpDefinition.getCPDefinitionId(), true);
	}

	public CreationMenu getCreationMenu(int type) throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (CommerceCatalogPermission.contains(
				cpRequestHelper.getPermissionChecker(), getCPDefinition(),
				ActionKeys.UPDATE)) {

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/cp_definitions/edit_cp_attachment_file_entry"
						).setParameter(
							"cpDefinitionId", getCPDefinitionId()
						).setParameter(
							"type", type
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString());
					dropdownItem.setLabel(_getTypeLabel(type));
					dropdownItem.setTarget("sidePanel");
				});
		}

		return creationMenu;
	}

	public String getCssClassFileMimeType(FileEntry fileEntry) {
		if (fileEntry == null) {
			return StringPool.BLANK;
		}

		return _dlMimeTypeDisplayContext.getCssClassFileMimeType(
			fileEntry.getMimeType());
	}

	public String getFileEntryName() throws PortalException {
		CPAttachmentFileEntry cpAttachmentFileEntry =
			getCPAttachmentFileEntry();

		FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

		if (fileEntry == null) {
			return StringPool.BLANK;
		}

		return fileEntry.getFileName();
	}

	public String[] getImageExtensions() {
		return _attachmentsConfiguration.imageExtensions();
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
				requestBackedPortletURLFactory, "addCPAttachmentFileEntry",
				imageItemSelectorCriterion));
	}

	public long getImageMaxSize() {
		return _attachmentsConfiguration.imageMaxSize();
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.CATEGORY_KEY_MEDIA;
	}

	public boolean hasCustomAttributes() throws Exception {
		return CustomAttributesUtil.hasCustomAttributes(
			cpRequestHelper.getCompanyId(),
			CPAttachmentFileEntry.class.getName(), getCPAttachmentFileEntryId(),
			null);
	}

	public boolean hasOptions() throws PortalException {
		int skuContributorCPDefinitionOptionRelCount =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRelsCount(
				getCPDefinitionId(), true);

		if (skuContributorCPDefinitionOptionRelCount > 0) {
			return true;
		}

		return false;
	}

	public Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
			parseCPAttachmentFileEntry(long cpAttachmentFileEntryId)
		throws PortalException {

		if (cpAttachmentFileEntryId <= 0) {
			return Collections.emptyMap();
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.fetchCPAttachmentFileEntry(
				cpAttachmentFileEntryId);

		if (cpAttachmentFileEntry == null) {
			return Collections.emptyMap();
		}

		return _cpInstanceHelper.getCPDefinitionOptionValueRelsMap(
			cpAttachmentFileEntry.getClassPK(),
			cpAttachmentFileEntry.getJson());
	}

	public void renderOptions(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long cpDefinitionId = getCPDefinitionId();

		CPAttachmentFileEntry cpAttachmentFileEntry =
			getCPAttachmentFileEntry();

		String json = null;

		if (cpAttachmentFileEntry != null) {
			json = cpAttachmentFileEntry.getJson();
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelService.getCPDefinitionOptionRels(
				cpDefinitionId, true);

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			CommerceOptionType commerceOptionType =
				_commerceOptionTypeRegistry.getCommerceOptionType(
					cpDefinitionOptionRel.getCommerceOptionTypeKey());

			commerceOptionType.render(
				cpDefinitionOptionRel, 0, false, json, httpServletRequest,
				httpServletResponse);
		}
	}

	private String _getTypeLabel(int type) {
		if (type == CPAttachmentFileEntryConstants.TYPE_IMAGE) {
			return LanguageUtil.get(httpServletRequest, "add-image");
		}
		else if (type == CPAttachmentFileEntryConstants.TYPE_OTHER) {
			return LanguageUtil.get(httpServletRequest, "add-attachment");
		}

		return StringPool.BLANK;
	}

	private final AttachmentsConfiguration _attachmentsConfiguration;
	private final CommerceOptionTypeRegistry _commerceOptionTypeRegistry;
	private CPAttachmentFileEntry _cpAttachmentFileEntry;
	private final CPAttachmentFileEntryService _cpAttachmentFileEntryService;
	private final CPDefinitionOptionRelService _cpDefinitionOptionRelService;
	private final CPInstanceHelper _cpInstanceHelper;
	private final DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;
	private final ItemSelector _itemSelector;

}
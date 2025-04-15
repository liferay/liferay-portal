/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.display.context;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.display.context.BaseCPDefinitionsDisplayContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.virtual.constants.VirtualCPTypeConstants;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.web.internal.portlet.action.helper.CPDefinitionVirtualSettingActionHelper;
import com.liferay.commerce.product.type.virtual.web.internal.security.permission.resource.CommerceCatalogPermission;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.JournalArticleItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionVirtualSettingDisplayContext
	extends BaseCPDefinitionsDisplayContext {

	public CPDefinitionVirtualSettingDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		DLAppService dlAppService, JournalArticleService journalArticleService,
		CPDefinitionVirtualSettingActionHelper
			cpDefinitionVirtualSettingActionHelper,
		ItemSelector itemSelector) {

		super(actionHelper, httpServletRequest);

		_dlAppService = dlAppService;
		_journalArticleService = journalArticleService;
		_cpDefinitionVirtualSettingActionHelper =
			cpDefinitionVirtualSettingActionHelper;
		_itemSelector = itemSelector;
	}

	public int[] getActivationStatuses() {
		return VirtualCPTypeConstants.ACTIVATION_STATUSES;
	}

	public String getActivationStatusLabel(int status) {
		return CommerceOrderConstants.getOrderStatusLabel(status);
	}

	public CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntry()
		throws PortalException {

		if (_commerceVirtualOrderItemFileEntry != null) {
			return _commerceVirtualOrderItemFileEntry;
		}

		_commerceVirtualOrderItemFileEntry =
			_cpDefinitionVirtualSettingActionHelper.
				getCommerceVirtualOrderItemFileEntry(
					cpRequestHelper.getRenderRequest());

		return _commerceVirtualOrderItemFileEntry;
	}

	@Override
	public CPDefinition getCPDefinition() throws PortalException {
		CPDefinition cpDefinition = super.getCPDefinition();

		if (cpDefinition == null) {
			CPInstance cpInstance = getCPInstance();

			if (cpInstance != null) {
				return cpInstance.getCPDefinition();
			}
		}

		return cpDefinition;
	}

	public CPDefinitionVirtualSetting getCPDefinitionVirtualSetting()
		throws PortalException {

		if (_cpDefinitionVirtualSetting != null) {
			return _cpDefinitionVirtualSetting;
		}

		if (_cpdVirtualSettingFileEntry != null) {
			return _cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();
		}

		_cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingActionHelper.
				getCPDefinitionVirtualSetting(
					cpRequestHelper.getRenderRequest());

		return _cpDefinitionVirtualSetting;
	}

	public CPDVirtualSettingFileEntry getCPDVirtualSettingFileEntry()
		throws PortalException {

		if (_cpdVirtualSettingFileEntry != null) {
			return _cpdVirtualSettingFileEntry;
		}

		_cpdVirtualSettingFileEntry =
			_cpDefinitionVirtualSettingActionHelper.
				getCPDVirtualSettingFileEntry(
					cpRequestHelper.getRenderRequest());

		return _cpdVirtualSettingFileEntry;
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
							"/cp_definitions" +
								"/edit_cpd_virtual_setting_file_entry"
						).setParameter(
							"cpDefinitionId", getCPDefinitionId()
						).setParameter(
							"cpInstanceId", getCPInstanceId()
						).setWindowState(
							LiferayWindowState.POP_UP
						).buildString());
					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "add-file-entry"));
					dropdownItem.setTarget("sidePanel");
				});
		}

		return creationMenu;
	}

	public String getDownloadFileEntryURL(long fileEntryId)
		throws PortalException {

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DLURLHelperUtil.getDownloadURL(
			fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
			StringPool.BLANK, true, true);
	}

	public String getDownloadSampleFileEntryURL() throws PortalException {
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			getCPDefinitionVirtualSetting();

		if (cpDefinitionVirtualSetting == null) {
			return null;
		}

		long fileEntryId = cpDefinitionVirtualSetting.getSampleFileEntryId();

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DLURLHelperUtil.getDownloadURL(
			fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
			StringPool.BLANK, true, true);
	}

	public FileEntry getFileEntry(long fileEntryId) throws PortalException {
		if (fileEntryId > 0) {
			return _dlAppService.getFileEntry(fileEntryId);
		}

		return null;
	}

	public String getFileEntryItemSelectorURL() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory,
				GroupLocalServiceUtil.getGroup(_getGroupId()), _getGroupId(),
				"uploadCPDefinitionVirtualSetting",
				UploadItemSelectorCriterion.builder(
				).desiredItemSelectorReturnTypes(
					new FileEntryItemSelectorReturnType()
				).repositoryName(
					CPConstants.SERVICE_NAME_PRODUCT
				).url(
					PortletURLBuilder.create(
						requestBackedPortletURLFactory.createActionURL(
							CPPortletKeys.CP_DEFINITIONS)
					).setActionName(
						"/cp_definitions/upload_cpd_virtual_setting_file_entry"
					).setParameter(
						"catalogGroupId", _getGroupId()
					).buildString()
				).build()));
	}

	public JournalArticle getJournalArticle() throws PortalException {
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			getCPDefinitionVirtualSetting();

		if (cpDefinitionVirtualSetting == null) {
			return null;
		}

		long journalArticleResourcePK =
			cpDefinitionVirtualSetting.
				getTermsOfUseJournalArticleResourcePrimKey();

		if (journalArticleResourcePK <= 0) {
			return null;
		}

		return _journalArticleService.getLatestArticle(
			journalArticleResourcePK);
	}

	public FileEntry getSampleFileEntry() throws PortalException {
		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			getCPDefinitionVirtualSetting();

		if (cpDefinitionVirtualSetting == null) {
			return null;
		}

		long fileEntryId = cpDefinitionVirtualSetting.getSampleFileEntryId();

		if (fileEntryId > 0) {
			return _dlAppService.getFileEntry(fileEntryId);
		}

		return null;
	}

	public String getSampleItemSelctorURL() throws PortalException {
		FileItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new FileEntryItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					cpRequestHelper.getRenderRequest()),
				GroupLocalServiceUtil.getGroup(_getGroupId()), 0,
				"uploadCPDefinitionVirtualSetting", fileItemSelectorCriterion));
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

	public String getTermsOfUseJournalArticleBrowserURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new JournalArticleItemSelectorReturnType());
		itemSelectorCriterion.setItemType(JournalArticle.class.getName());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "selectedItem",
				itemSelectorCriterion));
	}

	private long _getGroupId() throws PortalException {
		CommerceVirtualOrderItemFileEntry commerceVirtualOrderItemFileEntry =
			getCommerceVirtualOrderItemFileEntry();

		if (commerceVirtualOrderItemFileEntry != null) {
			CommerceVirtualOrderItem commerceVirtualOrderItem =
				commerceVirtualOrderItemFileEntry.getCommerceVirtualOrderItem();

			CommerceOrderItem commerceOrderItem =
				commerceVirtualOrderItem.getCommerceOrderItem();

			CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

			return cpDefinition.getGroupId();
		}

		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition != null) {
			return cpDefinition.getGroupId();
		}

		CPInstance cpInstance = getCPInstance();

		if (cpInstance != null) {
			return cpInstance.getGroupId();
		}

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			getCPDefinitionVirtualSetting();

		if (cpDefinitionVirtualSetting != null) {
			return cpDefinitionVirtualSetting.getGroupId();
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionVirtualSettingDisplayContext.class);

	private CommerceVirtualOrderItemFileEntry
		_commerceVirtualOrderItemFileEntry;
	private CPDefinitionVirtualSetting _cpDefinitionVirtualSetting;
	private final CPDefinitionVirtualSettingActionHelper
		_cpDefinitionVirtualSettingActionHelper;
	private CPDVirtualSettingFileEntry _cpdVirtualSettingFileEntry;
	private CPInstance _cpInstance;
	private final DLAppService _dlAppService;
	private final ItemSelector _itemSelector;
	private final JournalArticleService _journalArticleService;

}
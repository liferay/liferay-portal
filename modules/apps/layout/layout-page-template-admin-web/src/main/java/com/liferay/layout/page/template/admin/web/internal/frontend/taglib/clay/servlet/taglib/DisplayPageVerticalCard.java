/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util.DisplayPageActionDropdownItemsProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DisplayPageVerticalCard
	extends BaseBaseClayCard implements VerticalCard {

	public DisplayPageVerticalCard(
		boolean allowedMappedContentType, BaseModel<?> baseModel,
		boolean existsMappedContentType, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(baseModel, rowChecker);

		_allowedMappedContentType = allowedMappedContentType;
		_existsMappedContentType = existsMappedContentType;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_infoItemServiceRegistry =
			(InfoItemServiceRegistry)renderRequest.getAttribute(
				InfoItemServiceRegistry.class.getName());
		_layoutPageTemplateEntry = (LayoutPageTemplateEntry)baseModel;
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			_layoutPageTemplateEntry.getPlid());
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			DisplayPageActionDropdownItemsProvider
				displayPageActionDropdownItemsProvider =
					new DisplayPageActionDropdownItemsProvider(
						_allowedMappedContentType, _existsMappedContentType,
						_layoutPageTemplateEntry, _renderRequest,
						_renderResponse);

			return displayPageActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getHref() {
		if (!_existsMappedContentType) {
			return null;
		}

		try {
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(_draftLayout, _themeDisplay),
				"p_l_back_url", _themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getPortletDisplayName(),
				"p_l_mode", Constants.EDIT);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getImageSrc() {
		return _layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public List<LabelItem> getLabels() {
		if (_draftLayout == null) {
			return Collections.emptyList();
		}

		if (!GetterUtil.getBoolean(
				_draftLayout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.KEY_PUBLISHED))) {

			return LabelItemListBuilder.add(
				labelItem -> labelItem.setStatus(WorkflowConstants.STATUS_DRAFT)
			).build();
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(_draftLayout.getStatus())
		).build();
	}

	@Override
	public String getStickerIcon() {
		if (_layoutPageTemplateEntry.isDefaultTemplate()) {
			return "check-circle";
		}

		return null;
	}

	@Override
	public String getStickerStyle() {
		return "primary";
	}

	@Override
	public String getSubtitle() {
		String typeLabel = _getTypeLabel();

		if (Validator.isNull(typeLabel)) {
			return StringPool.DASH;
		}

		String subtypeLabel = StringPool.BLANK;

		try {
			subtypeLabel = _getSubtypeLabel();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (Validator.isNull(subtypeLabel)) {
			return typeLabel;
		}

		return typeLabel + " - " + subtypeLabel;
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateEntry.getName();
	}

	private String _getSubtypeLabel() {
		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				_layoutPageTemplateEntry.getClassName());

		if (infoItemFormVariationsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				_layoutPageTemplateEntry.getGroupId(),
				String.valueOf(_layoutPageTemplateEntry.getClassTypeId()));

		if (infoItemFormVariation != null) {
			return infoItemFormVariation.getLabel(_themeDisplay.getLocale());
		}

		return StringPool.BLANK;
	}

	private String _getTypeLabel() {
		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				_layoutPageTemplateEntry.getClassName());

		if (infoItemDetailsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		return infoItemClassDetails.getLabel(_themeDisplay.getLocale());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageVerticalCard.class);

	private final boolean _allowedMappedContentType;
	private final Layout _draftLayout;
	private final boolean _existsMappedContentType;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}
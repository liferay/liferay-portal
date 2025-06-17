/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.item.selector.web.internal;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateEntryItemSelectorCriterion;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateEntryItemSelectorReturnType;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateEntryNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ItemSelectorView.class)
public class LayoutPageTemplateEntryItemSelectorView
	implements ItemSelectorView<LayoutPageTemplateEntryItemSelectorCriterion> {

	@Override
	public Class<LayoutPageTemplateEntryItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return LayoutPageTemplateEntryItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "page-template");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			LayoutPageTemplateEntryItemSelectorCriterion
				layoutPageTemplateEntryItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			layoutPageTemplateEntryItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new LayoutPageTemplateEntryItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest,
				layoutPageTemplateEntryItemSelectorCriterion, portletURL));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryItemSelectorView.class.getName());

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new LayoutPageTemplateEntryItemSelectorReturnType());

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<LayoutPageTemplateEntryItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private class LayoutPageTemplateEntryItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public LayoutPageTemplateEntryItemDescriptor(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry,
			ThemeDisplay themeDisplay) {

			_httpServletRequest = httpServletRequest;
			_layoutPageTemplateEntry = layoutPageTemplateEntry;
			_themeDisplay = themeDisplay;
		}

		@Override
		public String getIcon() {
			return "page";
		}

		@Override
		public String getImageURL() {
			return _layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay);
		}

		@Override
		public Date getModifiedDate() {
			return _layoutPageTemplateEntry.getModifiedDate();
		}

		@Override
		public String getPayload() {
			return JSONUtil.put(
				"layoutPageTemplateEntryId",
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
			).put(
				"name", _layoutPageTemplateEntry.getName()
			).put(
				"previewURL",
				() -> {
					if (_layoutPageTemplateEntry.getType() ==
							LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

						String previewURL = HttpComponentsUtil.addParameters(
							_themeDisplay.getPortalURL() +
								_themeDisplay.getPathMain() +
									"/portal/get_page_preview",
							"p_l_mode", Constants.PREVIEW, "selPlid",
							_layoutPageTemplateEntry.getPlid(),
							"segmentsExperienceId",
							_segmentsExperienceLocalService.
								fetchDefaultSegmentsExperienceId(
									_layoutPageTemplateEntry.getPlid()));

						if (Validator.isNotNull(
								_themeDisplay.getDoAsUserId())) {

							previewURL = _portal.addPreservedParameters(
								_themeDisplay, previewURL, false, true);
						}

						return previewURL;
					}

					String previewURL = HttpComponentsUtil.addParameters(
						PortalUtil.getLayoutFullURL(
							_layoutLocalService.getLayout(
								_layoutPageTemplateEntry.getPlid()),
							_themeDisplay),
						"p_l_mode", Constants.PREVIEW, "p_p_auth",
						AuthTokenUtil.getToken(_httpServletRequest));

					if (Validator.isNotNull(_themeDisplay.getDoAsUserId())) {
						previewURL = _portal.addPreservedParameters(
							_themeDisplay, previewURL, false, true);
					}

					return previewURL;
				}
			).put(
				"url",
				() -> _portal.getLayoutFullURL(
					_layoutLocalService.getLayout(
						_layoutPageTemplateEntry.getPlid()),
					_themeDisplay)
			).put(
				"uuid", _layoutPageTemplateEntry.getUuid()
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			if (Objects.equals(
					_layoutPageTemplateEntry.getType(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)) {

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
			else if (Objects.equals(
						_layoutPageTemplateEntry.getType(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

				return _language.format(
					_httpServletRequest, "x-usages",
					_layoutLocalService.getMasterLayoutsCount(
						_layoutPageTemplateEntry.getGroupId(),
						_layoutPageTemplateEntry.getPlid()));
			}

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollection(
						_layoutPageTemplateEntry.
							getLayoutPageTemplateCollectionId());

			if (layoutPageTemplateCollection == null) {
				return StringPool.BLANK;
			}

			return layoutPageTemplateCollection.getName();
		}

		@Override
		public String getTitle(Locale locale) {
			return _layoutPageTemplateEntry.getName();
		}

		@Override
		public long getUserId() {
			return _layoutPageTemplateEntry.getUserId();
		}

		@Override
		public String getUserName() {
			return _layoutPageTemplateEntry.getUserName();
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
				return infoItemFormVariation.getLabel(
					_themeDisplay.getLocale());
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

		private final HttpServletRequest _httpServletRequest;
		private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
		private final ThemeDisplay _themeDisplay;

	}

	private class LayoutPageTemplateEntryItemSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<LayoutPageTemplateEntry> {

		public LayoutPageTemplateEntryItemSelectorViewDescriptor(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateEntryItemSelectorCriterion
				layoutPageTemplateEntryItemSelectorCriterion,
			PortletURL portletURL) {

			_httpServletRequest = httpServletRequest;
			_layoutPageTemplateEntryItemSelectorCriterion =
				layoutPageTemplateEntryItemSelectorCriterion;
			_portletURL = portletURL;

			_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		@Override
		public ItemDescriptor getItemDescriptor(
			LayoutPageTemplateEntry layoutPageTemplateEntry) {

			return new LayoutPageTemplateEntryItemSelectorView.
				LayoutPageTemplateEntryItemDescriptor(
					_httpServletRequest, layoutPageTemplateEntry,
					_themeDisplay);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new LayoutPageTemplateEntryItemSelectorReturnType();
		}

		@Override
		public String[] getOrderByKeys() {
			return new String[] {"name"};
		}

		@Override
		public SearchContainer<LayoutPageTemplateEntry> getSearchContainer() {
			SearchContainer<LayoutPageTemplateEntry> searchContainer =
				new SearchContainer<>(
					_portletRequest, _portletURL, null,
					"no-entries-were-found");

			searchContainer.setOrderByCol(
				ParamUtil.getString(_httpServletRequest, "orderByCol", "name"));

			boolean orderByAsc = true;

			String orderByType = ParamUtil.getString(
				_httpServletRequest, "orderByType", "asc");

			if (orderByType.equals("desc")) {
				orderByAsc = false;
			}

			searchContainer.setOrderByComparator(
				LayoutPageTemplateEntryNameComparator.getInstance(orderByAsc));
			searchContainer.setOrderByType(orderByType);

			String keywords = ParamUtil.getString(
				_httpServletRequest, "keywords");

			if (Validator.isNull(keywords)) {
				searchContainer.setResultsAndTotal(
					() ->
						_layoutPageTemplateEntryService.
							getLayoutPageTemplateEntries(
								_getGroupId(),
								_layoutPageTemplateEntryItemSelectorCriterion.
									getLayoutTypes(),
								_layoutPageTemplateEntryItemSelectorCriterion.
									getStatus(),
								searchContainer.getStart(),
								searchContainer.getEnd(),
								searchContainer.getOrderByComparator()),
					_layoutPageTemplateEntryService.
						getLayoutPageTemplateEntriesCount(
							_getGroupId(),
							_layoutPageTemplateEntryItemSelectorCriterion.
								getLayoutTypes(),
							_layoutPageTemplateEntryItemSelectorCriterion.
								getStatus()));
			}
			else {
				searchContainer.setResultsAndTotal(
					() ->
						_layoutPageTemplateEntryService.
							getLayoutPageTemplateEntries(
								_getGroupId(), keywords,
								_layoutPageTemplateEntryItemSelectorCriterion.
									getLayoutTypes(),
								_layoutPageTemplateEntryItemSelectorCriterion.
									getStatus(),
								searchContainer.getStart(),
								searchContainer.getEnd(),
								searchContainer.getOrderByComparator()),
					_layoutPageTemplateEntryService.
						getLayoutPageTemplateEntriesCount(
							_getGroupId(), keywords,
							_layoutPageTemplateEntryItemSelectorCriterion.
								getLayoutTypes(),
							_layoutPageTemplateEntryItemSelectorCriterion.
								getStatus()));
			}

			return searchContainer;
		}

		@Override
		public boolean isShowBreadcrumb() {
			return false;
		}

		@Override
		public boolean isShowManagementToolbar() {
			return true;
		}

		@Override
		public boolean isShowSearch() {
			return true;
		}

		private long _getGroupId() {
			if (_groupId != null) {
				return _groupId;
			}

			long groupId =
				_layoutPageTemplateEntryItemSelectorCriterion.getGroupId();

			if (groupId <= 0) {
				groupId = _themeDisplay.getScopeGroupId();
			}

			_groupId = groupId;

			return _groupId;
		}

		private Long _groupId;
		private final HttpServletRequest _httpServletRequest;
		private final LayoutPageTemplateEntryItemSelectorCriterion
			_layoutPageTemplateEntryItemSelectorCriterion;
		private final PortletRequest _portletRequest;
		private final PortletURL _portletURL;
		private final ThemeDisplay _themeDisplay;

	}

}
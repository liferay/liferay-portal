/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.selector.view.order:Integer=200",
	service = ItemSelectorView.class
)
public class InfoCollectionProviderItemSelectorView
	implements ItemSelectorView<InfoCollectionProviderItemSelectorCriterion> {

	@Override
	public Class<? extends InfoCollectionProviderItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoCollectionProviderItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "collection-providers");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoCollectionProviderItemSelectorCriterion
				infoCollectionProviderItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			infoCollectionProviderItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new InfoCollectionProviderItemSelectorViewDescriptor(
				httpServletRequest, portletURL,
				_getInfoCollectionProviders(
					httpServletRequest,
					infoCollectionProviderItemSelectorCriterion),
				_infoItemServiceRegistry));
	}

	private List<InfoCollectionProvider<?>> _getInfoCollectionProviders(
		HttpServletRequest httpServletRequest,
		InfoCollectionProviderItemSelectorCriterion
			infoCollectionProviderItemSelectorCriterion) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (infoCollectionProviderItemSelectorCriterion.getType() ==
				InfoCollectionProviderItemSelectorCriterion.Type.
					SUPPORTED_INFO_FRAMEWORK_COLLECTIONS) {

			return Collections.unmodifiableList(
				ListUtil.sort(
					ListUtil.filter(
						_infoItemServiceRegistry.getAllInfoItemServices(
							(Class<InfoCollectionProvider<?>>)
								(Class<?>)InfoCollectionProvider.class),
						InfoCollectionProvider::isAvailable),
					Comparator.comparing(
						infoCollectionProvider ->
							infoCollectionProvider.getLabel(
								themeDisplay.getLocale()),
						String.CASE_INSENSITIVE_ORDER)));
		}

		String itemType =
			infoCollectionProviderItemSelectorCriterion.getItemType();

		if (infoCollectionProviderItemSelectorCriterion.getType() ==
				InfoCollectionProviderItemSelectorCriterion.Type.
					ALL_COLLECTIONS) {

			itemType = AssetEntry.class.getName();
		}

		return Collections.unmodifiableList(
			ListUtil.sort(
				ListUtil.filter(
					_infoItemServiceRegistry.getAllInfoItemServices(
						(Class<InfoCollectionProvider<?>>)
							(Class<?>)InfoCollectionProvider.class,
						itemType),
					InfoCollectionProvider::isAvailable),
				Comparator.comparing(
					infoCollectionProvider -> infoCollectionProvider.getLabel(
						themeDisplay.getLocale()),
					String.CASE_INSENSITIVE_ORDER)));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoListProviderItemSelectorReturnType());

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<InfoCollectionProviderItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}
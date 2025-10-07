/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.item.selector;

import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.ActionableInfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	public ObjectEntryItemSelectorView(
		GroupLocalService groupLocalService,
		InfoPermissionProvider<ObjectEntry> infoPermissionProvider,
		ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
			itemSelectorViewDescriptorRenderer,
		ObjectDefinition objectDefinition,
		ObjectEntryManager objectEntryManager,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		Portal portal) {

		_groupLocalService = groupLocalService;
		_infoPermissionProvider = infoPermissionProvider;
		_itemSelectorViewDescriptorRenderer =
			itemSelectorViewDescriptorRenderer;
		_objectDefinition = objectDefinition;
		_objectEntryManager = objectEntryManager;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_portal = portal;
	}

	@Override
	public String getClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public boolean isVisible(
		InfoItemItemSelectorCriterion itemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return _infoPermissionProvider.hasViewPermission(
			themeDisplay.getPermissionChecker());
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, infoItemItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new ObjectEntryItemSelectorViewDescriptor(
				_groupLocalService, (HttpServletRequest)servletRequest,
				infoItemItemSelectorCriterion, _objectDefinition,
				_objectEntryManager, _objectRelatedModelsProviderRegistry,
				_objectScopeProviderRegistry, _portal, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Arrays.asList(
			new ActionableInfoItemItemSelectorReturnType(),
			new InfoItemItemSelectorReturnType(),
			new ObjectEntryItemSelectorReturnType());

	private final GroupLocalService _groupLocalService;
	private final InfoPermissionProvider<ObjectEntry> _infoPermissionProvider;
	private final ItemSelectorViewDescriptorRenderer
		<InfoItemItemSelectorCriterion> _itemSelectorViewDescriptorRenderer;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryManager _objectEntryManager;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final Portal _portal;

}
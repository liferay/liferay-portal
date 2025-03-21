/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorCriterionHandler;
import com.liferay.item.selector.ItemSelectorCriterionSerializer;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.item.selector.ItemSelectorViewRendererCustomizer;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.item.selector.web.internal.util.ItemSelectorKeyUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Roberto Díaz
 */
@Component(service = ItemSelector.class)
public class ItemSelectorImpl implements ItemSelector {

	public static final String JSON = "_json";

	public static final String PARAMETER_CRITERIA = "criteria";

	public static final String PARAMETER_ITEM_SELECTED_EVENT_NAME =
		"itemSelectedEventName";

	public static final String PARAMETER_SELECTED_TAB = "selectedTab";

	@Override
	public String getItemSelectedEventName(String itemSelectorURL) {
		Matcher matcher = _itemSelectorURLPattern.matcher(itemSelectorURL);

		if (matcher.find()) {
			return URLCodec.decodeURL(matcher.group(2));
		}

		String namespace = _portal.getPortletNamespace(
			ItemSelectorPortletKeys.ITEM_SELECTOR);

		return HttpComponentsUtil.getParameter(
			itemSelectorURL,
			namespace.concat(PARAMETER_ITEM_SELECTED_EVENT_NAME), true);
	}

	@Override
	public List<ItemSelectorCriterion> getItemSelectorCriteria(
		Map<String, String[]> parameters) {

		List<Class<? extends ItemSelectorCriterion>>
			itemSelectorCriterionClasses = _getItemSelectorCriterionClasses(
				parameters);

		List<ItemSelectorCriterion> itemSelectorCriteria = new ArrayList<>(
			itemSelectorCriterionClasses.size());

		for (int i = 0; i < itemSelectorCriterionClasses.size(); i++) {
			String[] values = parameters.get(i + JSON);

			if (ArrayUtil.isNotEmpty(values)) {
				itemSelectorCriteria.add(
					_itemSelectionCriterionSerializer.deserialize(
						itemSelectorCriterionClasses.get(i), values[0]));
			}
		}

		return itemSelectorCriteria;
	}

	@Override
	public List<ItemSelectorCriterion> getItemSelectorCriteria(
		String itemSelectorURL) {

		Map<String, String[]> parameters = HttpComponentsUtil.getParameterMap(
			HttpComponentsUtil.getQueryString(itemSelectorURL));

		Map<String, String[]> itemSelectorURLParameterMap = new HashMap<>();

		String namespace = _portal.getPortletNamespace(
			ItemSelectorPortletKeys.ITEM_SELECTOR);

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String parameterName = entry.getKey();

			if (parameterName.contains(namespace)) {
				String key = StringUtil.removeSubstring(
					parameterName, namespace);

				itemSelectorURLParameterMap.put(key, entry.getValue());
			}
		}

		Matcher matcher = _itemSelectorURLPattern.matcher(itemSelectorURL);

		if (matcher.matches()) {
			itemSelectorURLParameterMap.put(
				PARAMETER_CRITERIA,
				new String[] {HttpComponentsUtil.decodePath(matcher.group(1))});
			itemSelectorURLParameterMap.put(
				PARAMETER_ITEM_SELECTED_EVENT_NAME,
				new String[] {matcher.group(2)});
		}

		return getItemSelectorCriteria(itemSelectorURLParameterMap);
	}

	@Override
	public ItemSelectorRendering getItemSelectorRendering(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		Map<String, String[]> parameters, ThemeDisplay themeDisplay) {

		String itemSelectedEventName = getValue(
			parameters, PARAMETER_ITEM_SELECTED_EVENT_NAME);
		String selectedTab = getValue(parameters, PARAMETER_SELECTED_TAB);

		List<ItemSelectorViewRenderer> itemSelectorViewRenderers =
			new ArrayList<>();

		List<ItemSelectorCriterion> itemSelectorCriteria =
			getItemSelectorCriteria(parameters);

		ItemSelectorCriterion[] itemSelectorCriteriaArray =
			itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]);

		for (ItemSelectorCriterion itemSelectorCriterion :
				itemSelectorCriteria) {

			Class<? extends ItemSelectorCriterion> itemSelectorCriterionClass =
				itemSelectorCriterion.getClass();

			ItemSelectorCriterionHandler<ItemSelectorCriterion>
				itemSelectorCriterionHandler =
					_itemSelectorCriterionHandlerServiceTrackerMap.getService(
						ItemSelectorKeyUtil.getItemSelectorCriterionKey(
							itemSelectorCriterionClass));

			List<ItemSelectorView<ItemSelectorCriterion>> itemSelectorViews =
				itemSelectorCriterionHandler.getItemSelectorViews(
					itemSelectorCriterion);

			for (ItemSelectorView<ItemSelectorCriterion> itemSelectorView :
					itemSelectorViews) {

				if (!itemSelectorView.isVisible(
						itemSelectorCriterion, themeDisplay)) {

					continue;
				}

				PortletURL portletURL = getPortletURL(
					requestBackedPortletURLFactory,
					itemSelectorView.getTitle(themeDisplay.getLocale()),
					selectedTab, itemSelectedEventName,
					itemSelectorCriteriaArray, themeDisplay);

				itemSelectorViewRenderers.add(
					_applyCustomizations(
						new ItemSelectorViewRendererImpl(
							itemSelectorView, itemSelectorCriterion, portletURL,
							itemSelectedEventName, _isSearch(parameters))));
			}
		}

		return new ItemSelectorRenderingImpl(
			itemSelectedEventName, selectedTab, itemSelectorViewRenderers);
	}

	@Override
	public PortletURL getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		Group group, long refererGroupId, String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria) {

		PortletURL portletURL =
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				ItemSelectorPortletKeys.ITEM_SELECTOR, group, refererGroupId,
				0);

		try {
			portletURL.setPortletMode(PortletMode.VIEW);
		}
		catch (PortletModeException portletModeException) {
			throw new SystemException(portletModeException);
		}

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new SystemException(windowStateException);
		}

		Map<String, String[]> parameters = getItemSelectorParameters(
			itemSelectedEventName, itemSelectorCriteria);

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			portletURL.setParameter(entry.getKey(), entry.getValue());
		}

		return portletURL;
	}

	@Override
	public PortletURL getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria) {

		return getItemSelectorURL(
			requestBackedPortletURLFactory, null, 0, itemSelectedEventName,
			itemSelectorCriteria);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_itemSelectorCriterionHandlerServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<ItemSelectorCriterionHandler<ItemSelectorCriterion>>)
					(Class<?>)ItemSelectorCriterionHandler.class,
				null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(itemSelectorCriterionHandler, emitter) -> emitter.emit(
						ItemSelectorKeyUtil.getItemSelectorCriterionKey(
							itemSelectorCriterionHandler.
								getItemSelectorCriterionClass()))));
		_itemSelectorViewRendererCustomizersServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, ItemSelectorViewRendererCustomizer.class, null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(itemSelectorViewRendererCustomizer, emitter) -> {
						for (Class<? extends ItemSelectorCriterion>
								itemSelectorCriterionClass :
									itemSelectorViewRendererCustomizer.
										getSupportedItemSelectorCriterionClasses()) {

							emitter.emit(itemSelectorCriterionClass.getName());
						}
					}));
	}

	@Deactivate
	protected void deactivate() {
		_itemSelectorCriterionHandlerServiceTrackerMap.close();

		_itemSelectorViewRendererCustomizersServiceTrackerMap.close();
	}

	protected Map<String, String[]> getItemSelectorParameters(
		String itemSelectedEventName,
		ItemSelectorCriterion... itemSelectorCriteria) {

		Map<String, String[]> parameters = HashMapBuilder.put(
			PARAMETER_CRITERIA,
			() -> {
				StringBundler sb = new StringBundler(
					itemSelectorCriteria.length * 2);

				for (ItemSelectorCriterion itemSelectorCriterion :
						itemSelectorCriteria) {

					sb.append(
						ItemSelectorKeyUtil.getItemSelectorCriterionKey(
							itemSelectorCriterion.getClass()));
					sb.append(StringPool.COMMA);
				}

				if (itemSelectorCriteria.length > 0) {
					sb.setIndex(sb.index() - 1);
				}

				return new String[] {sb.toString()};
			}
		).put(
			PARAMETER_ITEM_SELECTED_EVENT_NAME,
			new String[] {itemSelectedEventName}
		).build();

		for (int i = 0; i < itemSelectorCriteria.length; i++) {
			ItemSelectorCriterion itemSelectorCriterion =
				itemSelectorCriteria[i];

			String countValue = String.valueOf(i);

			parameters.put(
				countValue.concat(JSON),
				new String[] {
					_itemSelectionCriterionSerializer.serialize(
						itemSelectorCriterion)
				});
		}

		return parameters;
	}

	protected PortletURL getPortletURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String title, String selectedTab, String itemSelectedEventName,
		ItemSelectorCriterion[] itemSelectorCriteriaArray,
		ThemeDisplay themeDisplay) {

		PortletURL portletURL = null;

		if (Validator.isNotNull(selectedTab) && selectedTab.equals(title)) {
			portletURL = getItemSelectorURL(
				requestBackedPortletURLFactory, themeDisplay.getScopeGroup(),
				themeDisplay.getRefererGroupId(), itemSelectedEventName,
				itemSelectorCriteriaArray);
		}
		else {
			Group group = themeDisplay.getRefererGroup();

			if (group == null) {
				group = themeDisplay.getScopeGroup();
			}

			portletURL = getItemSelectorURL(
				requestBackedPortletURLFactory, group, 0, itemSelectedEventName,
				itemSelectorCriteriaArray);
		}

		portletURL.setParameter(PARAMETER_SELECTED_TAB, title);

		return portletURL;
	}

	protected String getValue(Map<String, String[]> parameters, String name) {
		String[] values = parameters.get(name);

		if (ArrayUtil.isEmpty(values)) {
			return StringPool.BLANK;
		}

		return values[0];
	}

	private ItemSelectorViewRenderer _applyCustomizations(
		ItemSelectorViewRenderer itemSelectorViewRenderer) {

		ItemSelectorCriterion itemSelectorCriterion =
			itemSelectorViewRenderer.getItemSelectorCriterion();

		Class<? extends ItemSelectorCriterion> clazz =
			itemSelectorCriterion.getClass();

		List<ItemSelectorViewRendererCustomizer>
			itemSelectorViewRendererCustomizers =
				_itemSelectorViewRendererCustomizersServiceTrackerMap.
					getService(clazz.getName());

		if (itemSelectorViewRendererCustomizers == null) {
			return itemSelectorViewRenderer;
		}

		for (ItemSelectorViewRendererCustomizer
				itemSelectorViewRendererCustomizer :
					itemSelectorViewRendererCustomizers) {

			itemSelectorViewRenderer =
				itemSelectorViewRendererCustomizer.
					customizeItemSelectorViewRenderer(itemSelectorViewRenderer);
		}

		return itemSelectorViewRenderer;
	}

	private List<Class<? extends ItemSelectorCriterion>>
		_getItemSelectorCriterionClasses(Map<String, String[]> parameters) {

		String criteria = getValue(parameters, PARAMETER_CRITERIA);

		String[] itemSelectorCriterionClassNames = criteria.split(",");

		List<Class<? extends ItemSelectorCriterion>>
			itemSelectorCriterionClasses = new ArrayList<>(
				itemSelectorCriterionClassNames.length);

		for (String itemSelectorCriterionClassName :
				itemSelectorCriterionClassNames) {

			ItemSelectorCriterionHandler<?> itemSelectorCriterionHandler =
				_itemSelectorCriterionHandlerServiceTrackerMap.getService(
					itemSelectorCriterionClassName);

			if (itemSelectorCriterionHandler != null) {
				itemSelectorCriterionClasses.add(
					itemSelectorCriterionHandler.
						getItemSelectorCriterionClass());
			}
		}

		return itemSelectorCriterionClasses;
	}

	private boolean _isSearch(Map<String, String[]> parameters) {
		String keywords = getValue(parameters, "keywords");

		return Validator.isNotNull(keywords);
	}

	private static final Pattern _itemSelectorURLPattern = Pattern.compile(
		".*select\\/([^/]+)\\/([^$?/]+).*");

	@Reference
	private ItemSelectorCriterionSerializer _itemSelectionCriterionSerializer;

	private ServiceTrackerMap
		<String, ItemSelectorCriterionHandler<ItemSelectorCriterion>>
			_itemSelectorCriterionHandlerServiceTrackerMap;
	private ServiceTrackerMap<String, List<ItemSelectorViewRendererCustomizer>>
		_itemSelectorViewRendererCustomizersServiceTrackerMap;

	@Reference
	private Portal _portal;

}
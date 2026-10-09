/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.util;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorCriterionSerializer;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewReturnTypeProvider;
import com.liferay.item.selector.ItemSelectorViewReturnTypeProviderHandler;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONContext;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONDeserializerTransformer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.json.JSONTransformer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera
 * @author Roberto Díaz
 */
@Component(service = ItemSelectorCriterionSerializer.class)
public class ItemSelectorCriterionSerializerImpl
	implements ItemSelectorCriterionSerializer {

	@Override
	public <T extends ItemSelectorCriterion> T deserialize(
		Class<T> itemSelectorCriterionClass, String json) {

		JSONDeserializer<T> jsonDeserializer =
			_jsonFactory.createJSONDeserializer();

		DesiredItemSelectorReturnTypesJSONDeserializerTransformer
			desiredItemSelectorReturnTypesJSONDeserializerTransformer =
				_desiredItemSelectorReturnTypesJSONDeserializerTransformerDCLSingleton.
					getSingleton(
						this::
							_createDesiredItemSelectorReturnTypesJSONDeserializerTransformer);

		jsonDeserializer.transform(
			desiredItemSelectorReturnTypesJSONDeserializerTransformer,
			"desiredItemSelectorReturnTypes");

		return jsonDeserializer.deserialize(json, itemSelectorCriterionClass);
	}

	@Override
	public String serialize(ItemSelectorCriterion itemSelectorCriterion) {
		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		jsonSerializer.transform(
			_desiredItemSelectorReturnTypesJSONTransformer,
			"desiredItemSelectorReturnTypes");

		jsonSerializer.exclude(_EXCLUDED_FIELD_NAMES);

		return jsonSerializer.serializeDeep(itemSelectorCriterion);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<ItemSelectorView<?>>)(Class<?>)ItemSelectorView.class,
			"item.selector.view.key");

		_serviceTracker = ServiceTrackerFactory.create(
			bundleContext, ItemSelectorViewReturnTypeProvider.class,
			new ItemSelectorViewReturnTypeProviderServiceTrackerCustomizer());

		_serviceTrackerItemSelectorView = ServiceTrackerFactory.create(
			bundleContext,
			(Class<ItemSelectorView<?>>)(Class<?>)ItemSelectorView.class,
			new ItemSelectorReturnTypeServiceTrackerCustomizer());
	}

	protected void addItemSelectorReturnType(
		ItemSelectorReturnType itemSelectorReturnType) {

		Class<? extends ItemSelectorReturnType> itemSelectorReturnTypeClass =
			itemSelectorReturnType.getClass();

		addItemSelectorReturnType(
			itemSelectorReturnTypeClass.getName(), itemSelectorReturnType);
		addItemSelectorReturnType(
			ItemSelectorKeyUtil.getItemSelectorReturnTypeKey(
				itemSelectorReturnTypeClass),
			itemSelectorReturnType);
	}

	protected void addItemSelectorReturnType(
		String key, ItemSelectorReturnType itemSelectorReturnType) {

		List<ItemSelectorReturnType> itemSelectorReturnTypes =
			_itemSelectorReturnTypes.get(key);

		if (itemSelectorReturnTypes == null) {
			itemSelectorReturnTypes = new CopyOnWriteArrayList<>();

			List<ItemSelectorReturnType> previousItemSelectorReturnTypes =
				_itemSelectorReturnTypes.putIfAbsent(
					key, itemSelectorReturnTypes);

			if (previousItemSelectorReturnTypes != null) {
				itemSelectorReturnTypes = previousItemSelectorReturnTypes;
			}
		}

		itemSelectorReturnTypes.add(itemSelectorReturnType);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
		_serviceTrackerItemSelectorView.close();
		_serviceTrackerMap.close();
	}

	private DesiredItemSelectorReturnTypesJSONDeserializerTransformer
		_createDesiredItemSelectorReturnTypesJSONDeserializerTransformer() {

		_serviceTracker.open();
		_serviceTrackerItemSelectorView.open();

		return new DesiredItemSelectorReturnTypesJSONDeserializerTransformer();
	}

	private static final String[] _EXCLUDED_FIELD_NAMES = {
		"availableItemSelectorReturnTypes", "class"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		ItemSelectorCriterionSerializerImpl.class);

	private BundleContext _bundleContext;
	private final DCLSingleton
		<DesiredItemSelectorReturnTypesJSONDeserializerTransformer>
			_desiredItemSelectorReturnTypesJSONDeserializerTransformerDCLSingleton =
				new DCLSingleton<>();
	private final DesiredItemSelectorReturnTypesJSONTransformer
		_desiredItemSelectorReturnTypesJSONTransformer =
			new DesiredItemSelectorReturnTypesJSONTransformer();
	private final ConcurrentMap<String, List<ItemSelectorReturnType>>
		_itemSelectorReturnTypes = new ConcurrentHashMap<>();

	@Reference
	private ItemSelectorViewReturnTypeProviderHandler
		_itemSelectorViewReturnTypeProviderHandler;

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTracker
		<ItemSelectorViewReturnTypeProvider, ItemSelectorViewReturnTypeProvider>
			_serviceTracker;
	private ServiceTracker<ItemSelectorView<?>, ItemSelectorView<?>>
		_serviceTrackerItemSelectorView;
	private ServiceTrackerMap<String, ItemSelectorView<?>> _serviceTrackerMap;

	private static class DesiredItemSelectorReturnTypesJSONTransformer
		implements JSONTransformer {

		@Override
		public void transform(JSONContext jsonContext, Object object) {
			List<ItemSelectorReturnType> desiredItemSelectorReturnTypes =
				(List<ItemSelectorReturnType>)object;

			StringBundler sb = new StringBundler(
				(desiredItemSelectorReturnTypes.size() * 2) + 1);

			sb.append(StringPool.QUOTE);

			for (ItemSelectorReturnType itemSelectorReturnType :
					desiredItemSelectorReturnTypes) {

				sb.append(
					ItemSelectorKeyUtil.getItemSelectorReturnTypeKey(
						itemSelectorReturnType.getClass()));

				sb.append(StringPool.COMMA);
			}

			if (desiredItemSelectorReturnTypes.isEmpty()) {
				sb.append(StringPool.QUOTE);
			}
			else {
				sb.setStringAt(StringPool.QUOTE, sb.index() - 1);
			}

			jsonContext.write(sb.toString());
		}

	}

	private class DesiredItemSelectorReturnTypesJSONDeserializerTransformer
		implements JSONDeserializerTransformer
			<String, List<ItemSelectorReturnType>> {

		@Override
		public List<ItemSelectorReturnType> transform(String key) {
			List<ItemSelectorReturnType> desiredItemSelectorReturnTypes =
				new ArrayList<>();

			String[] desiredItemSelectorReturnTypeNames = StringUtil.split(key);

			for (String desiredItemSelectorReturnTypeName :
					desiredItemSelectorReturnTypeNames) {

				List<ItemSelectorReturnType> itemSelectorReturnTypes =
					_itemSelectorReturnTypes.get(
						desiredItemSelectorReturnTypeName);

				if (ListUtil.isEmpty(itemSelectorReturnTypes)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No return types are registered for " +
								desiredItemSelectorReturnTypeName);
					}

					continue;
				}

				Iterator<ItemSelectorReturnType> iterator =
					itemSelectorReturnTypes.iterator();

				if (!iterator.hasNext()) {
					continue;
				}

				desiredItemSelectorReturnTypes.add(iterator.next());
			}

			if (desiredItemSelectorReturnTypes.isEmpty()) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No valid desired item selector return types found");
				}
			}

			return desiredItemSelectorReturnTypes;
		}

	}

	private class ItemSelectorReturnTypeServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ItemSelectorView<?>, ItemSelectorView<?>> {

		@Override
		public ItemSelectorView<?> addingService(
			ServiceReference<ItemSelectorView<?>> serviceReference) {

			ItemSelectorView<?> itemSelectorView = _bundleContext.getService(
				serviceReference);

			String itemSelectorViewKey = GetterUtil.getString(
				serviceReference.getProperty("item.selector.view.key"));

			List<ItemSelectorReturnType> supportedItemSelectorReturnTypes =
				_itemSelectorViewReturnTypeProviderHandler.
					getSupportedItemSelectorReturnTypes(
						itemSelectorView.getSupportedItemSelectorReturnTypes(),
						itemSelectorViewKey);

			for (ItemSelectorReturnType supportedItemSelectorReturnType :
					supportedItemSelectorReturnTypes) {

				addItemSelectorReturnType(supportedItemSelectorReturnType);
			}

			return itemSelectorView;
		}

		@Override
		public void modifiedService(
			ServiceReference<ItemSelectorView<?>> serviceReference,
			ItemSelectorView<?> itemSelectorView) {
		}

		@Override
		public void removedService(
			ServiceReference<ItemSelectorView<?>> serviceReference,
			ItemSelectorView<?> itemSelectorView) {

			try {
				List<ItemSelectorReturnType> supportedItemSelectorReturnTypes =
					itemSelectorView.getSupportedItemSelectorReturnTypes();

				for (ItemSelectorReturnType supportedItemSelectorReturnType :
						supportedItemSelectorReturnTypes) {

					Class<? extends ItemSelectorReturnType>
						supportedItemSelectorReturnTypeClass =
							supportedItemSelectorReturnType.getClass();

					List<ItemSelectorReturnType> itemSelectorReturnTypes =
						_itemSelectorReturnTypes.get(
							supportedItemSelectorReturnTypeClass.getName());

					itemSelectorReturnTypes.remove(0);

					itemSelectorReturnTypes = _itemSelectorReturnTypes.get(
						ItemSelectorKeyUtil.getItemSelectorReturnTypeKey(
							supportedItemSelectorReturnTypeClass));

					itemSelectorReturnTypes.remove(0);
				}
			}
			finally {
				_bundleContext.ungetService(serviceReference);
			}
		}

	}

	private class ItemSelectorViewReturnTypeProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ItemSelectorViewReturnTypeProvider,
			 ItemSelectorViewReturnTypeProvider> {

		@Override
		public ItemSelectorViewReturnTypeProvider addingService(
			ServiceReference<ItemSelectorViewReturnTypeProvider>
				serviceReference) {

			String itemSelectorViewKey = GetterUtil.getString(
				serviceReference.getProperty("item.selector.view.key"));

			ItemSelectorView<?> itemSelectorView =
				_serviceTrackerMap.getService(itemSelectorViewKey);

			if (itemSelectorView == null) {
				return null;
			}

			ItemSelectorViewReturnTypeProvider
				itemSelectorViewReturnTypeProvider = _bundleContext.getService(
					serviceReference);

			List<ItemSelectorReturnType> supportedItemSelectorReturnTypes =
				itemSelectorViewReturnTypeProvider.
					populateSupportedItemSelectorReturnTypes(
						ListUtil.copy(
							itemSelectorView.
								getSupportedItemSelectorReturnTypes()));

			for (ItemSelectorReturnType supportedItemSelectorReturnType :
					supportedItemSelectorReturnTypes) {

				addItemSelectorReturnType(supportedItemSelectorReturnType);
			}

			return itemSelectorViewReturnTypeProvider;
		}

		@Override
		public void modifiedService(
			ServiceReference<ItemSelectorViewReturnTypeProvider>
				serviceReference,
			ItemSelectorViewReturnTypeProvider
				itemSelectorViewReturnTypeProvider) {
		}

		@Override
		public void removedService(
			ServiceReference<ItemSelectorViewReturnTypeProvider>
				serviceReference,
			ItemSelectorViewReturnTypeProvider
				itemSelectorViewReturnTypeProvider) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}
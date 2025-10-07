/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.internal.item;

import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.info.item.updater.InfoItemFriendlyURLUpdater;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.RepeatableFieldInfoItemCollectionProvider;
import com.liferay.info.exception.CapabilityVerificationException;
import com.liferay.info.filter.InfoFilterProvider;
import com.liferay.info.filter.InfoRequestItemProvider;
import com.liferay.info.formatter.InfoCollectionTextFormatter;
import com.liferay.info.formatter.InfoTextFormatter;
import com.liferay.info.internal.util.ItemClassNameServiceReferenceMapper;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.action.executor.InfoItemActionExecutor;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.creator.InfoItemCreator;
import com.liferay.info.item.provider.InfoItemActionDetailsProvider;
import com.liferay.info.item.provider.InfoItemCapabilitiesProvider;
import com.liferay.info.item.provider.InfoItemCategorizationProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectVariationProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.item.provider.InfoItemScopeProvider;
import com.liferay.info.item.provider.InfoItemStatusProvider;
import com.liferay.info.item.provider.RelatedInfoItemProvider;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.item.provider.filter.InfoItemServiceFilter;
import com.liferay.info.item.provider.filter.OptionalPropertyInfoItemServiceFilter;
import com.liferay.info.item.provider.filter.PropertyInfoItemServiceFilter;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.translator.InfoItemIdentifierTranslator;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.info.type.Keyed;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@Component(service = InfoItemServiceRegistry.class)
public class InfoItemServiceRegistryImpl implements InfoItemServiceRegistry {

	@Override
	public <P> List<P> getAllInfoItemServices(Class<P> serviceClass) {
		ServiceTrackerMap<String, ?> serviceTrackerMap =
			_getKeyedInfoItemServiceTrackerMap(serviceClass);

		return new ArrayList<>(
			(Collection<? extends P>)serviceTrackerMap.values());
	}

	@Override
	public <P> List<P> getAllInfoItemServices(
		Class<P> serviceClass, String itemClassName,
		InfoItemServiceFilter infoItemServiceFilter) {

		ServiceTrackerMap
			<String, ? extends List<ServiceReferenceServiceTuple<P, P>>>
				infoItemServiceTrackerMap =
					(ServiceTrackerMap)
						_getItemClassNameInfoItemServiceTrackerMap(
							serviceClass);

		List<ServiceReferenceServiceTuple<P, P>> serviceReferenceServiceTuples =
			infoItemServiceTrackerMap.getService(itemClassName);

		if (serviceReferenceServiceTuples != null) {
			PropertyInfoItemServiceFilter
				companyIdPropertyInfoItemServiceFilter =
					new OptionalPropertyInfoItemServiceFilter(
						"company.id",
						String.valueOf(CompanyThreadLocal.getCompanyId()));

			if (infoItemServiceFilter != null) {
				if (infoItemServiceFilter instanceof
						PropertyInfoItemServiceFilter) {

					PropertyInfoItemServiceFilter
						propertyInfoItemServiceFilter =
							(PropertyInfoItemServiceFilter)
								infoItemServiceFilter;

					serviceReferenceServiceTuples = ListUtil.filter(
						serviceReferenceServiceTuples,
						serviceReferenceServiceTuple -> {
							ServiceReference<?> serviceReference =
								serviceReferenceServiceTuple.
									getServiceReference();

							if (companyIdPropertyInfoItemServiceFilter.match(
									serviceReference) &&
								propertyInfoItemServiceFilter.match(
									serviceReference)) {

								return true;
							}

							return false;
						});

					return ListUtil.toList(
						serviceReferenceServiceTuples,
						ServiceReferenceServiceTuple::getService);
				}

				serviceReferenceServiceTuples =
					_filterServiceReferenceServiceTuples(
						infoItemServiceFilter, serviceReferenceServiceTuples);
			}

			serviceReferenceServiceTuples = ListUtil.filter(
				serviceReferenceServiceTuples,
				serviceReferenceServiceTuple ->
					companyIdPropertyInfoItemServiceFilter.match(
						serviceReferenceServiceTuple.getServiceReference()));

			return ListUtil.toList(
				serviceReferenceServiceTuples,
				ServiceReferenceServiceTuple::getService);
		}

		return Collections.emptyList();
	}

	@Override
	public <P> P getFirstInfoItemService(
		Class<P> serviceClass, String itemClassName,
		InfoItemServiceFilter infoItemServiceFilter) {

		List<?> infoItemServices = getAllInfoItemServices(
			serviceClass, itemClassName, infoItemServiceFilter);

		if (ListUtil.isEmpty(infoItemServices)) {
			return null;
		}

		return (P)infoItemServices.get(0);
	}

	@Override
	public List<InfoItemCapability> getInfoItemCapabilities(
		String itemClassName) {

		InfoItemCapabilitiesProvider<?> infoItemCapabilitiesProvider =
			getFirstInfoItemService(
				InfoItemCapabilitiesProvider.class, itemClassName, null);

		if (infoItemCapabilitiesProvider == null) {
			return null;
		}

		return infoItemCapabilitiesProvider.getInfoItemCapabilities();
	}

	@Override
	public InfoItemCapability getInfoItemCapability(
		String infoItemCapabilityKey) {

		ServiceTrackerMap<String, InfoItemCapability>
			infoItemCapabilityServiceTrackerMap =
				_getInfoItemCapabilityServiceTrackerMap();

		return infoItemCapabilityServiceTrackerMap.getService(
			infoItemCapabilityKey);
	}

	@Override
	public <P> List<InfoItemClassDetails> getInfoItemClassDetails(
		Class<P> serviceClass) {

		return TransformUtil.transform(
			getInfoItemClassNames(serviceClass),
			this::_getInfoItemClassDetails);
	}

	@Override
	public List<InfoItemClassDetails> getInfoItemClassDetails(
			InfoItemCapability infoItemCapability)
		throws CapabilityVerificationException {

		List<InfoItemClassDetails> infoItemClassDetailsList = new ArrayList<>();

		for (InfoItemClassDetails curInfoItemClassDetails :
				getInfoItemClassDetails(InfoItemCapabilitiesProvider.class)) {

			InfoItemCapabilitiesProvider<?> infoItemCapabilitiesProvider =
				getFirstInfoItemService(
					InfoItemCapabilitiesProvider.class,
					curInfoItemClassDetails.getClassName(), null);

			if (infoItemCapabilitiesProvider == null) {
				continue;
			}

			List<InfoItemCapability> infoItemCapabilities =
				infoItemCapabilitiesProvider.getInfoItemCapabilities();

			if (infoItemCapabilities.contains(infoItemCapability)) {
				infoItemCapability.verify(
					curInfoItemClassDetails.getClassName());

				infoItemClassDetailsList.add(curInfoItemClassDetails);
			}
		}

		return infoItemClassDetailsList;
	}

	@Override
	public List<InfoItemClassDetails> getInfoItemClassDetails(
			long groupId, String itemCapabilityKey,
			PermissionChecker permissionChecker)
		throws CapabilityVerificationException {

		List<InfoItemClassDetails> infoItemClassDetails = new ArrayList<>();

		for (InfoItemClassDetails infoItemClassDetail :
				getInfoItemClassDetails(itemCapabilityKey)) {

			InfoPermissionProvider infoPermissionProvider =
				getFirstInfoItemService(
					InfoPermissionProvider.class,
					infoItemClassDetail.getClassName());

			if ((infoPermissionProvider == null) ||
				infoPermissionProvider.hasViewPermission(permissionChecker)) {

				infoItemClassDetails.add(infoItemClassDetail);
			}
		}

		return infoItemClassDetails;
	}

	@Override
	public List<InfoItemClassDetails> getInfoItemClassDetails(
			String infoItemCapabilityKey)
		throws CapabilityVerificationException {

		ServiceTrackerMap<String, InfoItemCapability>
			infoItemCapabilityServiceTrackerMap =
				_getInfoItemCapabilityServiceTrackerMap();

		InfoItemCapability infoItemCapability =
			infoItemCapabilityServiceTrackerMap.getService(
				infoItemCapabilityKey);

		if (infoItemCapability == null) {
			throw new RuntimeException(
				"Unable to find info item capability with key " +
					infoItemCapabilityKey);
		}

		return getInfoItemClassDetails(infoItemCapability);
	}

	@Override
	public <P> List<String> getInfoItemClassNames(Class<P> serviceClass) {
		ServiceTrackerMap<String, ?> infoItemServiceTrackerMap =
			_getItemClassNameInfoItemServiceTrackerMap(serviceClass);

		return new ArrayList<>(infoItemServiceTrackerMap.keySet());
	}

	@Override
	public <P> P getInfoItemService(Class<P> serviceClass, String serviceKey) {
		if (Validator.isNull(serviceKey)) {
			return null;
		}

		ServiceTrackerMap<String, ?> infoItemServiceTrackerMap =
			_getKeyedInfoItemServiceTrackerMap(serviceClass);

		return (P)infoItemServiceTrackerMap.getService(serviceKey);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();
		}

		for (ServiceTrackerMap<?, ?> serviceTrackerMap :
				_itemClassNameInfoItemServiceTrackerMap.values()) {

			serviceTrackerMap.close();
		}

		for (ServiceTrackerMap<?, ?> serviceTrackerMap :
				_keyedInfoItemServiceTrackerMap.values()) {

			serviceTrackerMap.close();
		}
	}

	private <P> List<ServiceReferenceServiceTuple<P, P>>
		_filterServiceReferenceServiceTuples(
			InfoItemServiceFilter infoItemServiceFilter,
			List<ServiceReferenceServiceTuple<P, P>>
				serviceReferenceServiceTuples) {

		try {
			Filter filter = FrameworkUtil.createFilter(
				infoItemServiceFilter.getFilterString());

			return ListUtil.filter(
				serviceReferenceServiceTuples,
				serviceReferenceServiceTuple -> filter.match(
					serviceReferenceServiceTuple.getServiceReference()));
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new RuntimeException(
				"Invalid filter string", invalidSyntaxException);
		}
	}

	private ServiceTrackerMap<String, InfoItemCapability>
		_getInfoItemCapabilityServiceTrackerMap() {

		if (_serviceTrackerMap == null) {
			_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, InfoItemCapability.class, null,
				ServiceReferenceMapperFactory.create(
					_bundleContext,
					(service, emitter) -> emitter.emit(service.getKey())));
		}

		return _serviceTrackerMap;
	}

	private InfoItemClassDetails _getInfoItemClassDetails(
		String itemClassName) {

		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			getFirstInfoItemService(
				InfoItemDetailsProvider.class, itemClassName, null);

		if (infoItemDetailsProvider != null) {
			return infoItemDetailsProvider.getInfoItemClassDetails();
		}

		return new InfoItemClassDetails(
			itemClassName, InfoLocalizedValue.modelResource(itemClassName));
	}

	private ServiceTrackerMap<String, ?>
		_getItemClassNameInfoItemServiceTrackerMap(Class<?> clazz) {

		if (!_validInfoClasses.contains(clazz)) {
			return null;
		}

		return _itemClassNameInfoItemServiceTrackerMap.computeIfAbsent(
			clazz,
			keyClass -> ServiceTrackerMapFactory.openMultiValueMap(
				_bundleContext, (Class<Object>)keyClass, null,
				new ItemClassNameServiceReferenceMapper(_bundleContext),
				ServiceTrackerCustomizerFactory.serviceReferenceServiceTuple(
					_bundleContext)));
	}

	private ServiceTrackerMap<String, ?> _getKeyedInfoItemServiceTrackerMap(
		Class<?> clazz) {

		if (!_validInfoClasses.contains(clazz)) {
			return null;
		}

		return _keyedInfoItemServiceTrackerMap.computeIfAbsent(
			clazz,
			keyClass -> ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext, keyClass, null,
				ServiceReferenceMapperFactory.create(
					_bundleContext,
					(service, emitter) -> {
						String key = keyClass.getName();

						if (service instanceof Keyed) {
							Keyed keyedService = (Keyed)service;

							key = keyedService.getKey();
						}

						emitter.emit(key);
					}),
				new PropertyServiceReferenceComparator<>("service.ranking")));
	}

	private static final Set<Class<?>> _validInfoClasses = new HashSet<>(
		Arrays.asList(
			InfoCollectionProvider.class, InfoCollectionTextFormatter.class,
			InfoFilterProvider.class, InfoItemActionDetailsProvider.class,
			InfoItemActionExecutor.class, InfoItemCapabilitiesProvider.class,
			InfoItemCategorizationProvider.class, InfoItemCreator.class,
			InfoItemDetailsProvider.class, InfoItemFieldValuesProvider.class,
			InfoItemFieldValuesUpdater.class, InfoItemFormProvider.class,
			InfoItemFormVariationsProvider.class,
			InfoItemFriendlyURLProvider.class, InfoItemFriendlyURLUpdater.class,
			InfoItemIdentifierTranslator.class, InfoItemLanguagesProvider.class,
			InfoItemObjectProvider.class, InfoItemObjectVariationProvider.class,
			InfoItemPermissionProvider.class, InfoItemRenderer.class,
			InfoItemScopeProvider.class, InfoItemStatusProvider.class,
			InfoListRenderer.class, InfoPermissionProvider.class,
			InfoRequestItemProvider.class, InfoTextFormatter.class,
			RelatedInfoItemCollectionProvider.class,
			RelatedInfoItemProvider.class,
			RepeatableFieldInfoItemCollectionProvider.class,
			RepeatableFieldsInfoItemFormProvider.class));

	private BundleContext _bundleContext;
	private final Map
		<Class<?>,
		 ServiceTrackerMap
			 <String, List<ServiceReferenceServiceTuple<Object, Object>>>>
				_itemClassNameInfoItemServiceTrackerMap =
					new ConcurrentHashMap<>();
	private final Map<Class<?>, ServiceTrackerMap<String, ?>>
		_keyedInfoItemServiceTrackerMap = new ConcurrentHashMap<>();
	private ServiceTrackerMap<String, InfoItemCapability> _serviceTrackerMap;

}
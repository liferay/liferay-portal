/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 */
public class AssetRendererFactoryRegistryUtil {

	public static List<AssetRendererFactory<?>> getAssetRendererFactories(
		long companyId) {

		return _filterAssetRendererFactories(companyId, false);
	}

	public static List<AssetRendererFactory<?>> getAssetRendererFactories(
		long companyId, boolean filterSelectable) {

		return _filterAssetRendererFactories(companyId, filterSelectable);
	}

	public static <T> AssetRendererFactory<T> getAssetRendererFactoryByClass(
		Class<T> clazz) {

		return _customize(
			(AssetRendererFactory<T>)
				_classNameAssetRenderFactoriesServiceTrackerMap.getService(
					clazz.getName()));
	}

	public static <T> AssetRendererFactory<T>
		getAssetRendererFactoryByClassName(String className) {

		return _customize(
			(AssetRendererFactory<T>)
				_classNameAssetRenderFactoriesServiceTrackerMap.getService(
					className));
	}

	public static <T> AssetRendererFactory<T>
		getAssetRendererFactoryByClassNameId(long classNameId) {

		return _customize(
			(AssetRendererFactory<T>)
				_classNameAssetRenderFactoriesServiceTrackerMap.getService(
					PortalUtil.getClassName(classNameId)));
	}

	public static AssetRendererFactory<?> getAssetRendererFactoryByType(
		String type) {

		return _typeAssetRenderFactoriesServiceTrackerMap.getService(type);
	}

	public static long[] getClassNameIds(long companyId) {
		return getClassNameIds(companyId, false);
	}

	public static long[] getClassNameIds(
		long companyId, boolean filterSelectable) {

		if (companyId > 0) {
			return TransformUtil.transformToLongArray(
				_filterAssetRendererFactories(companyId, filterSelectable),
				AssetRendererFactory::getClassNameId);
		}

		return TransformUtil.transformToLongArray(
			_classNameAssetRenderFactoriesServiceTrackerMap.keySet(),
			className -> {
				AssetRendererFactory<?> assetRendererFactory = _customize(
					_classNameAssetRenderFactoriesServiceTrackerMap.getService(
						className));

				return assetRendererFactory.getClassNameId();
			});
	}

	public static long[] getIndexableClassNameIds(
		long companyId, boolean filterSelectable) {

		return ArrayUtil.filter(
			getClassNameIds(companyId, filterSelectable),
			classNameId -> {
				Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
					PortalUtil.getClassName(classNameId));

				if (indexer == null) {
					return false;
				}

				return true;
			});
	}

	private static <T> AssetRendererFactory<T> _customize(
		AssetRendererFactory<T> assetRendererFactory) {

		if (assetRendererFactory == null) {
			return null;
		}

		AssetRendererFactoryCustomizer assetRendererFactoryCustomizer =
			_assetRendererFactoryCustomizerSnapshot.get();

		if (assetRendererFactoryCustomizer != null) {
			assetRendererFactory = assetRendererFactoryCustomizer.customize(
				assetRendererFactory);
		}

		return assetRendererFactory;
	}

	private static List<AssetRendererFactory<?>> _filterAssetRendererFactories(
		long companyId, boolean filterSelectable) {

		List<AssetRendererFactory<?>> filteredAssetRendererFactories =
			new CopyOnWriteArrayList<>();

		for (String key :
				_classNameAssetRenderFactoriesServiceTrackerMap.keySet()) {

			AssetRendererFactory<?> assetRendererFactory = _customize(
				_classNameAssetRenderFactoriesServiceTrackerMap.getService(
					key));

			if (!assetRendererFactory.isActive(companyId) ||
				(filterSelectable && !assetRendererFactory.isSelectable())) {

				continue;
			}

			filteredAssetRendererFactories.add(assetRendererFactory);
		}

		return filteredAssetRendererFactories;
	}

	private AssetRendererFactoryRegistryUtil() {
	}

	private static final Snapshot<AssetRendererFactoryCustomizer>
		_assetRendererFactoryCustomizerSnapshot = new Snapshot<>(
			AssetRendererFactoryRegistryUtil.class,
			AssetRendererFactoryCustomizer.class);
	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, AssetRendererFactory<?>>
		_classNameAssetRenderFactoriesServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext,
				(Class<AssetRendererFactory<?>>)
					(Class<?>)AssetRendererFactory.class,
				null,
				(serviceReference, emitter) -> {
					AssetRendererFactory<?> assetRendererFactory =
						_bundleContext.getService(serviceReference);

					emitter.emit(assetRendererFactory.getClassName());
				});

	private static final ServiceTrackerMap<String, AssetRendererFactory<?>>
		_typeAssetRenderFactoriesServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				_bundleContext,
				(Class<AssetRendererFactory<?>>)
					(Class<?>)AssetRendererFactory.class,
				null,
				(serviceReference, emitter) -> {
					AssetRendererFactory<?> assetRendererFactory =
						_bundleContext.getService(serviceReference);

					emitter.emit(assetRendererFactory.getType());
				});

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.internal.SystemFDSEntryRegistryImpl;
import com.liferay.frontend.data.set.internal.action.FDSCreationMenuRegistryImpl;
import com.liferay.frontend.data.set.internal.action.FDSItemsActionsRegistryImpl;
import com.liferay.frontend.data.set.internal.sort.FDSSortsRegistryImpl;
import com.liferay.frontend.data.set.internal.view.FDSViewContextContributorRegistryImpl;
import com.liferay.frontend.data.set.internal.view.FDSViewRegistryImpl;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Daniel Sanz
 */
public abstract class BaseFDSSerializerTestCase {

	@Before
	public void setUp() {
		bundleContext = SystemBundleUtil.getBundleContext();

		systemFDSSerializer.fdsCreationMenuRegistry =
			new FDSCreationMenuRegistryImpl(
				(ServiceTrackerMap
					<String,
					 ServiceTrackerCustomizerFactory.ServiceWrapper
						 <FDSCreationMenu>>)_registerServiceTrackerMap(
							 ServiceTrackerMapFactory.openSingleValueMap(
								 bundleContext, FDSCreationMenu.class,
								 "frontend.data.set.name",
								 ServiceTrackerCustomizerFactory.
									 <FDSCreationMenu>serviceWrapper(
										 bundleContext))));
		systemFDSSerializer.fdsItemsActionsRegistry =
			new FDSItemsActionsRegistryImpl(
				(ServiceTrackerMap
					<String,
					 ServiceTrackerCustomizerFactory.ServiceWrapper
						 <FDSItemsActions>>)_registerServiceTrackerMap(
							 ServiceTrackerMapFactory.openSingleValueMap(
								 bundleContext, FDSItemsActions.class,
								 "frontend.data.set.name",
								 ServiceTrackerCustomizerFactory.
									 <FDSItemsActions>serviceWrapper(
										 bundleContext))));
		systemFDSSerializer.fdsSortsRegistry = new FDSSortsRegistryImpl(
			(ServiceTrackerMap
				<String,
				 ServiceTrackerCustomizerFactory.ServiceWrapper<FDSSorts>>)
					 _registerServiceTrackerMap(
						 ServiceTrackerMapFactory.openSingleValueMap(
							 bundleContext, FDSSorts.class,
							 "frontend.data.set.name",
							 ServiceTrackerCustomizerFactory.
								 <FDSSorts>serviceWrapper(bundleContext))));
		systemFDSSerializer.fdsViewContextContributorRegistry =
			new FDSViewContextContributorRegistryImpl(
				(ServiceTrackerMap
					<String,
					 List
						 <ServiceTrackerCustomizerFactory.ServiceWrapper
							 <FDSViewContextContributor>>>)
								 _registerServiceTrackerMap(
									 ServiceTrackerMapFactory.openMultiValueMap(
										 bundleContext,
										 FDSViewContextContributor.class,
										 "frontend.data.set.view.name",
										 ServiceTrackerCustomizerFactory.
											 <FDSViewContextContributor>
												 serviceWrapper(
													 bundleContext))));
		systemFDSSerializer.fdsViewRegistry = new FDSViewRegistryImpl(
			(ServiceTrackerMap
				<String,
				 List<ServiceTrackerCustomizerFactory.ServiceWrapper<FDSView>>>)
					 _registerServiceTrackerMap(
						 ServiceTrackerMapFactory.openMultiValueMap(
							 bundleContext, FDSView.class,
							 "frontend.data.set.name",
							 ServiceTrackerCustomizerFactory.
								 <FDSView>serviceWrapper(bundleContext))));
		systemFDSSerializer.systemFDSEntryRegistry =
			new SystemFDSEntryRegistryImpl(
				(ServiceTrackerMap<String, SystemFDSEntry>)
					_registerServiceTrackerMap(
						ServiceTrackerMapFactory.openSingleValueMap(
							bundleContext, SystemFDSEntry.class,
							"frontend.data.set.name")));
	}

	@After
	public void tearDown() {
		for (ServiceTrackerMap<String, ?> serviceTrackerMap :
				_serviceTrackerMaps) {

			serviceTrackerMap.close();
		}
	}

	protected void mockLanguage() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(LocaleUtil.US, null)
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1, String.class)
		);

		Mockito.when(
			language.get(
				Mockito.eq(ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE),
				Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1, String.class)
		);

		languageUtil.setLanguage(language);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getLocale(httpServletRequest)
		).thenReturn(
			LocaleUtil.US
		);

		portalUtil.setPortal(portal);

		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(
				Mockito.nullable(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);
	}

	protected static final String API_URL_PARAMETERS =
		RandomTestUtil.randomString();

	protected static final String[] CONTENT_RENDERERS =
		RandomTestUtil.randomStrings(2);

	protected static final int[] DEFAULT_ITEMS_PER_PAGE_ARRAY = {
		RandomTestUtil.randomInt(), RandomTestUtil.randomInt()
	};

	protected static final String[] DESCRIPTIONS = RandomTestUtil.randomStrings(
		2);

	protected static final String[] DISPLAY_TYPE_KEYS =
		RandomTestUtil.randomStrings(4);

	protected static final String[] DISPLAY_TYPE_VALUES =
		RandomTestUtil.randomStrings(4);

	protected static final String[] FDS_NAMES = RandomTestUtil.randomStrings(2);

	protected static final String[] FIELD_NAMES = RandomTestUtil.randomStrings(
		3);

	protected static final String[] ICONS = RandomTestUtil.randomStrings(2);

	protected static final String[] IDS = RandomTestUtil.randomStrings(4);

	protected static final String[] IMAGES = RandomTestUtil.randomStrings(2);

	protected static final String ITEM_KEY = RandomTestUtil.randomString();

	protected static final String[] LABELS = RandomTestUtil.randomStrings(4);

	protected static final String LINK = RandomTestUtil.randomString();

	protected static final int[][] LIST_OF_ITEMS_PER_PAGE_ARRAY = {
		{RandomTestUtil.randomInt(), RandomTestUtil.randomInt()},
		{
			RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
			RandomTestUtil.randomInt()
		},
		{-1, 3, 0, 5}, {3, 5}
	};

	protected static final String[] PROPS_TRANSFORMERS =
		RandomTestUtil.randomStrings(2);

	protected static final String[] STICKERS = RandomTestUtil.randomStrings(2);

	protected static final String[] SYMBOLS = RandomTestUtil.randomStrings(2);

	protected static final String[] TITLES = RandomTestUtil.randomStrings(3);

	protected static final String URL = RandomTestUtil.randomString();

	protected BundleContext bundleContext;
	protected String defaultPagination = JSONUtil.put(
		"deltas",
		() -> JSONUtil.toJSONArray(
			ListUtil.fromArray(PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES),
			itemsPerPage -> JSONUtil.put("label", itemsPerPage))
	).put(
		"initialDelta", PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA
	).toString();
	protected final HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	protected final SystemFDSSerializer systemFDSSerializer =
		new SystemFDSSerializer();

	private ServiceTrackerMap<String, ?> _registerServiceTrackerMap(
		ServiceTrackerMap<String, ?> serviceTrackerMap) {

		_serviceTrackerMaps.add(serviceTrackerMap);

		return serviceTrackerMap;
	}

	private final List<ServiceTrackerMap<String, ?>> _serviceTrackerMaps =
		new ArrayList<>();

}
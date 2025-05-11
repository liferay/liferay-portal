/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.serializer;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSBulkActions;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.constants.FDSConstants;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseClientExtensionFDSFilter;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilterContextContributor;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.frontend.data.set.internal.action.FDSBulkActionsRegistryImpl;
import com.liferay.frontend.data.set.internal.filter.ClientExtensionFDSFilterContextContributor;
import com.liferay.frontend.data.set.internal.filter.DateRangeFDSFilterContextContributor;
import com.liferay.frontend.data.set.internal.filter.FDSFilterContextContributorRegistryImpl;
import com.liferay.frontend.data.set.internal.filter.FDSFilterRegistryImpl;
import com.liferay.frontend.data.set.internal.filter.SelectionFDSFilterContextContributor;
import com.liferay.frontend.data.set.internal.url.FDSAPIURLResolverRegistryImpl;
import com.liferay.frontend.data.set.internal.view.cards.CardsFDSViewContextContributor;
import com.liferay.frontend.data.set.internal.view.cards.FDSCardSchemaBuilderImpl;
import com.liferay.frontend.data.set.internal.view.list.ListFDSViewContextContributor;
import com.liferay.frontend.data.set.internal.view.table.FDSTableSchemaBuilderImpl;
import com.liferay.frontend.data.set.internal.view.table.TableFDSViewContextContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.frontend.data.set.url.FDSAPIURLResolver;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewContextContributor;
import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Daniel Sanz
 */
public class SystemFDSSerializerTest extends BaseFDSSerializerTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSerializeAdditionalAPIURLParameters() throws Exception {

		// No parameters

		ServiceTrackerMap
			<String,
			 ServiceTrackerCustomizerFactory.ServiceWrapper<FDSAPIURLResolver>>
				serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
					bundleContext, FDSAPIURLResolver.class,
					"fds.rest.application.key",
					ServiceTrackerCustomizerFactory.
						<FDSAPIURLResolver>serviceWrapper(bundleContext));

		systemFDSSerializer.fdsAPIURLResolverRegistry =
			new FDSAPIURLResolverRegistryImpl(serviceTrackerMap);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertNull(
			systemFDSSerializer.serializeAdditionalAPIURLParameters(
				FDS_NAMES[0], httpServletRequest));

		_unregisterServices();

		// Parameters

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withAdditionalURLParameters(
					API_URL_PARAMETERS
				)));

		Assert.assertEquals(
			API_URL_PARAMETERS,
			systemFDSSerializer.serializeAdditionalAPIURLParameters(
				FDS_NAMES[0], httpServletRequest));

		_unregisterServices();

		serviceTrackerMap.close();
	}

	@Test
	public void testSerializeAPIURL() throws Exception {

		// No parameters

		ServiceTrackerMap
			<String,
			 ServiceTrackerCustomizerFactory.ServiceWrapper<FDSAPIURLResolver>>
				serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
					bundleContext, FDSAPIURLResolver.class,
					"fds.rest.application.key",
					ServiceTrackerCustomizerFactory.
						<FDSAPIURLResolver>serviceWrapper(bundleContext));

		systemFDSSerializer.fdsAPIURLResolverRegistry =
			new FDSAPIURLResolverRegistryImpl(serviceTrackerMap);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			"/o/app/endpoint",
			systemFDSSerializer.serializeAPIURL(
				FDS_NAMES[0], httpServletRequest));

		_unregisterServices();

		// Parameters

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withAdditionalURLParameters(
					"nestedFields=creator"
				)));

		Assert.assertEquals(
			"/o/app/endpoint",
			systemFDSSerializer.serializeAPIURL(
				FDS_NAMES[0], httpServletRequest));

		_unregisterServices();

		serviceTrackerMap.close();
	}

	@Test
	public void testSerializeBulkActions() throws Exception {

		// Different bulk actions

		ServiceTrackerMap
			<String,
			 ServiceTrackerCustomizerFactory.ServiceWrapper<FDSBulkActions>>
				serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
					bundleContext, FDSBulkActions.class,
					"frontend.data.set.name",
					ServiceTrackerCustomizerFactory.
						<FDSBulkActions>serviceWrapper(bundleContext));

		systemFDSSerializer.fdsBulkActionsRegistry =
			new FDSBulkActionsRegistryImpl(serviceTrackerMap);

		List<FDSActionDropdownItem> fdsActionDropdownItems1 =
			ListUtil.fromArray(
				new FDSActionDropdownItem(
					null, ICONS[0], IDS[0], LABELS[0], "delete", "delete",
					"headless"));

		_registerServices(
			_registerFDSBulkActions(fdsActionDropdownItems1, FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			fdsActionDropdownItems1,
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[0], httpServletRequest));

		List<FDSActionDropdownItem> fdsActionDropdownItems2 =
			ListUtil.fromArray(
				new FDSActionDropdownItem(
					null, ICONS[1], IDS[1], LABELS[1], "get", "permissions",
					"modal-permissions"));

		_registerServices(
			_registerFDSBulkActions(fdsActionDropdownItems2, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			fdsActionDropdownItems2,
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[1], httpServletRequest));

		Assert.assertNotEquals(
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		// No bulk actions

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertTrue(
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[0], httpServletRequest
			).isEmpty());

		_unregisterServices();

		// Shared bulk actions

		fdsActionDropdownItems1 = ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, ICONS[0], IDS[0], LABELS[0], "delete", "delete",
				"headless"));

		_registerServices(
			_registerFDSBulkActions(fdsActionDropdownItems1, FDS_NAMES[0]),
			_registerFDSBulkActions(fdsActionDropdownItems1, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeBulkActions(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		serviceTrackerMap.close();
	}

	@Test
	public void testSerializeCreationMenu() throws Exception {

		// Different creation menu

		CreationMenu creationMenu1 = CreationMenuBuilder.addDropdownItem(
			DropdownItemBuilder.setIcon(
				ICONS[0]
			).setLabel(
				LABELS[0]
			).build()
		).build();

		_registerServices(
			_registerFDSCreationMenu(creationMenu1, FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			creationMenu1,
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[0], httpServletRequest));

		CreationMenu creationMenu2 = CreationMenuBuilder.addDropdownItem(
			DropdownItemBuilder.setIcon(
				ICONS[1]
			).setLabel(
				LABELS[1]
			).build()
		).build();

		_registerServices(
			_registerFDSCreationMenu(creationMenu2, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			creationMenu2,
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[1], httpServletRequest));

		Assert.assertNotEquals(
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		// No creation menu

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertTrue(
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[0], httpServletRequest
			).isEmpty());

		_unregisterServices();

		// Shared creation menu

		creationMenu1 = CreationMenuBuilder.addDropdownItem(
			DropdownItemBuilder.setIcon(
				ICONS[0]
			).setLabel(
				LABELS[0]
			).build()
		).build();

		_registerServices(
			_registerFDSCreationMenu(creationMenu1, FDS_NAMES[0]),
			_registerFDSCreationMenu(creationMenu1, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeCreationMenu(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();
	}

	@Test
	public void testSerializeFilters() throws Exception {

		// Client extension filter

		ServiceTrackerMap
			<String,
			 List
				 <ServiceTrackerCustomizerFactory.ServiceWrapper
					 <FDSFilterContextContributor>>> serviceTrackerMap1 =
						ServiceTrackerMapFactory.openMultiValueMap(
							bundleContext, FDSFilterContextContributor.class,
							"frontend.data.set.filter.type",
							ServiceTrackerCustomizerFactory.
								<FDSFilterContextContributor>serviceWrapper(
									bundleContext));

		systemFDSSerializer.fdsFilterContextContributorRegistry =
			new FDSFilterContextContributorRegistryImpl(serviceTrackerMap1);

		ServiceTrackerMap
			<String,
			 List<ServiceTrackerCustomizerFactory.ServiceWrapper<FDSFilter>>>
				serviceTrackerMap2 = ServiceTrackerMapFactory.openMultiValueMap(
					bundleContext, FDSFilter.class, "frontend.data.set.name",
					ServiceTrackerCustomizerFactory.<FDSFilter>serviceWrapper(
						bundleContext));

		systemFDSSerializer.fdsFilterRegistry = new FDSFilterRegistryImpl(
			serviceTrackerMap2);

		mockLanguage();

		_registerServices(
			_registerFDSFilter(
				new BaseClientExtensionFDSFilter() {

					@Override
					public String getCETExternalReferenceCode() {
						return "";
					}

					@Override
					public String getId() {
						return IDS[0];
					}

					@Override
					public String getLabel() {
						return LABELS[0];
					}

					@Override
					public String getModuleURL() {
						return URL;
					}

					@Override
					public Map<String, Object> getPreloadedData() {
						return new HashMapBuilder<>().<String, Object>put(
							IDS[1], LABELS[1]
						).put(
							IDS[2], LABELS[2]
						).build();
					}

				},
				FDS_NAMES[0]),
			bundleContext.registerService(
				FDSFilterContextContributor.class,
				new ClientExtensionFDSFilterContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.filter.type", "clientExtension")),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"clientExtensionFilterURL", URL
				).put(
					"id", IDS[0]
				).put(
					"label", LABELS[0]
				).put(
					"preloadedData",
					JSONUtil.put(
						IDS[1], LABELS[1]
					).put(
						IDS[2], LABELS[2]
					)
				).put(
					"type", "clientExtension"
				)
			).toString(),
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Date range filter

		_registerServices(
			_registerFDSFilter(
				_createFDSFilterDate(
					IDS[0], LABELS[0], new DateFDSFilterItem(1, 12, 2000),
					new DateFDSFilterItem(16, 3, 1977),
					new HashMapBuilder<>().<String, Object>put(
						"from", new DateFDSFilterItem(30, 11, 1985)
					).put(
						"to", new DateFDSFilterItem(27, 5, 1995)
					).build()),
				FDS_NAMES[0]),
			bundleContext.registerService(
				FDSFilterContextContributor.class,
				new DateRangeFDSFilterContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.filter.type", "dateRange")),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"entityFieldType", FDSEntityFieldTypes.DATE
				).put(
					"id", IDS[0]
				).put(
					"label", LABELS[0]
				).put(
					"max",
					JSONUtil.put(
						"day", 1
					).put(
						"month", 12
					).put(
						"year", 2000
					)
				).put(
					"min",
					JSONUtil.put(
						"day", 16
					).put(
						"month", 3
					).put(
						"year", 1977
					)
				).put(
					"preloadedData",
					JSONUtil.put(
						"from",
						JSONUtil.put(
							"day", 30
						).put(
							"month", 11
						).put(
							"year", 1985
						)
					).put(
						"to",
						JSONUtil.put(
							"day", 27
						).put(
							"month", 5
						).put(
							"year", 1995
						)
					)
				).put(
					"type", "dateRange"
				)
			).toString(),
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Different filters

		_registerServices(
			_registerFDSFilter(
				_createFDSFilterDate(
					IDS[0], LABELS[0], new DateFDSFilterItem(1, 1, 1980),
					new DateFDSFilterItem(0, 0, 0), null),
				FDS_NAMES[0]),
			_registerFDSFilter(
				_createFDSFilterDate(
					IDS[1], LABELS[1], new DateFDSFilterItem(31, 12, 1987),
					new DateFDSFilterItem(1, 2, 1900), null),
				FDS_NAMES[1]),
			bundleContext.registerService(
				FDSFilterContextContributor.class,
				new DateRangeFDSFilterContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.filter.type", "dateRange")),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		JSONAssert.assertNotEquals(
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Disabled filter

		_registerServices(
			_registerFDSFilter(
				new FDSFilter() {

					@Override
					public String getId() {
						return IDS[0];
					}

					@Override
					public String getLabel() {
						return LABELS[0];
					}

					@Override
					public String getType() {
						return "dateRange";
					}

					@Override
					public boolean isEnabled() {
						return false;
					}

				},
				FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			"[]",
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// No filter

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			"[]",
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Selection filter

		_registerServices(
			_registerFDSFilter(
				new BaseSelectionFDSFilter() {

					@Override
					public String getAPIURL() {
						return URL;
					}

					@Override
					public String getEntityFieldType() {
						return FDSEntityFieldTypes.COLLECTION;
					}

					@Override
					public String getId() {
						return IDS[0];
					}

					@Override
					public String getItemKey() {
						return ITEM_KEY;
					}

					@Override
					public String getItemLabel() {
						return LABELS[0];
					}

					@Override
					public String getLabel() {
						return LABELS[1];
					}

					@Override
					public Map<String, Object> getPreloadedData() {
						return new HashMapBuilder<>().<String, Object>put(
							"exclude", false
						).build();
					}

					@Override
					public List<SelectionFDSFilterItem>
						getSelectionFDSFilterItems(Locale locale) {

						return ListUtil.fromArray(
							new SelectionFDSFilterItem(LABELS[2], IDS[2]),
							new SelectionFDSFilterItem(LABELS[3], IDS[3]));
					}

					@Override
					public boolean isAutocompleteEnabled() {
						return true;
					}

					@Override
					public boolean isMultiple() {
						return true;
					}

				},
				FDS_NAMES[0]),
			bundleContext.registerService(
				FDSFilterContextContributor.class,
				new SelectionFDSFilterContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.filter.type", "selection")),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(FDS_NAMES[0])));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"apiURL", URL
				).put(
					"autocompleteEnabled", true
				).put(
					"entityFieldType", FDSEntityFieldTypes.COLLECTION
				).put(
					"id", IDS[0]
				).put(
					"inputPlaceholder", "search"
				).put(
					"itemKey", ITEM_KEY
				).put(
					"itemLabel", LABELS[0]
				).put(
					"items",
					JSONUtil.putAll(
						JSONUtil.put(
							"label", LABELS[2]
						).put(
							"value", IDS[2]
						),
						JSONUtil.put(
							"label", LABELS[3]
						).put(
							"value", IDS[3]
						))
				).put(
					"label", LABELS[1]
				).put(
					"multiple", true
				).put(
					"preloadedData", JSONUtil.put("exclude", false)
				).put(
					"type", "selection"
				)
			).toString(),
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Shared filters

		FDSFilter dateRangeFDSFilter = _createFDSFilterDate(
			FIELD_NAMES[0], LABELS[0], new DateFDSFilterItem(1, 1, 1980),
			new DateFDSFilterItem(0, 0, 0), null);

		_registerServices(
			_registerFDSFilter(dateRangeFDSFilter, FDS_NAMES[0]),
			_registerFDSFilter(dateRangeFDSFilter, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		JSONAssert.assertEquals(
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			systemFDSSerializer.serializeFilters(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		serviceTrackerMap1.close();
		serviceTrackerMap2.close();
	}

	@Test
	public void testSerializeItemsActions() throws Exception {

		// Different items actions

		List<FDSActionDropdownItem> fdsActionDropdownItems1 =
			ListUtil.fromArray(
				new FDSActionDropdownItem(
					null, ICONS[0], IDS[0], LABELS[0], "delete", "delete",
					"headless"));

		List<FDSActionDropdownItem> fdsActionDropdownItems2 =
			ListUtil.fromArray(
				new FDSActionDropdownItem(
					null, ICONS[1], IDS[1], LABELS[1], "get", "permissions",
					"modal-permissions"));

		_registerServices(
			_registerFDSItemsActions(fdsActionDropdownItems1, FDS_NAMES[0]),
			_registerFDSItemsActions(fdsActionDropdownItems2, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			fdsActionDropdownItems1,
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[0], httpServletRequest));
		Assert.assertEquals(
			fdsActionDropdownItems2,
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[1], httpServletRequest));
		Assert.assertNotEquals(
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		// No items actions

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertTrue(
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[0], httpServletRequest
			).isEmpty());

		_unregisterServices();

		// Shared items actions

		fdsActionDropdownItems1 = ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, ICONS[0], IDS[0], LABELS[0], "delete", "delete",
				"headless"));

		_registerServices(
			_registerFDSItemsActions(fdsActionDropdownItems1, FDS_NAMES[0]),
			_registerFDSItemsActions(fdsActionDropdownItems1, FDS_NAMES[1]),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeItemsActions(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();
	}

	@Test
	public void testSerializePagination() throws Exception {

		// Default pagination

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			defaultPagination,
			systemFDSSerializer.serializePagination(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Different pagination

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withPagination(
					DEFAULT_ITEMS_PER_PAGE_ARRAY[0],
					LIST_OF_ITEMS_PER_PAGE_ARRAY[0]
				)),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[1]
				).withPagination(
					DEFAULT_ITEMS_PER_PAGE_ARRAY[1],
					LIST_OF_ITEMS_PER_PAGE_ARRAY[1]
				)));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"deltas",
				() -> JSONUtil.toJSONArray(
					ListUtil.fromArray(LIST_OF_ITEMS_PER_PAGE_ARRAY[0]),
					itemsPerPage -> JSONUtil.put("label", itemsPerPage))
			).put(
				"initialDelta", DEFAULT_ITEMS_PER_PAGE_ARRAY[0]
			).toString(),
			systemFDSSerializer.serializePagination(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"deltas",
				() -> JSONUtil.toJSONArray(
					ListUtil.fromArray(LIST_OF_ITEMS_PER_PAGE_ARRAY[1]),
					itemsPerPage -> JSONUtil.put("label", itemsPerPage))
			).put(
				"initialDelta", DEFAULT_ITEMS_PER_PAGE_ARRAY[1]
			).toString(),
			systemFDSSerializer.serializePagination(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Shared pagination

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withPagination(
					DEFAULT_ITEMS_PER_PAGE_ARRAY[0],
					LIST_OF_ITEMS_PER_PAGE_ARRAY[0]
				)),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[1]
				).withPagination(
					DEFAULT_ITEMS_PER_PAGE_ARRAY[0],
					LIST_OF_ITEMS_PER_PAGE_ARRAY[0]
				)));

		JSONAssert.assertEquals(
			systemFDSSerializer.serializePagination(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			systemFDSSerializer.serializePagination(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Wrong pagination

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withPagination(
					0, LIST_OF_ITEMS_PER_PAGE_ARRAY[2]
				)),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[1]
				).withPagination(
					-1, null
				)));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"deltas",
				() -> JSONUtil.toJSONArray(
					ListUtil.fromArray(LIST_OF_ITEMS_PER_PAGE_ARRAY[3]),
					itemsPerPage -> JSONUtil.put("label", itemsPerPage))
			).put(
				"initialDelta", PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA
			).toString(),
			systemFDSSerializer.serializePagination(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			defaultPagination,
			systemFDSSerializer.serializePagination(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();
	}

	@Test
	public void testSerializePropsTransformer() throws Exception {

		// Different props transformer

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withPropsTransformer(
					PROPS_TRANSFORMERS[0]
				)),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[1]
				).withPropsTransformer(
					PROPS_TRANSFORMERS[1]
				)));

		Assert.assertEquals(
			PROPS_TRANSFORMERS[0],
			systemFDSSerializer.serializePropsTransformer(
				FDS_NAMES[0], httpServletRequest));

		Assert.assertEquals(
			PROPS_TRANSFORMERS[1],
			systemFDSSerializer.serializePropsTransformer(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		// No props transformer

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertNull(
			systemFDSSerializer.serializePropsTransformer(
				FDS_NAMES[0], httpServletRequest));

		_unregisterServices();

		// Shared props transformer

		_registerServices(
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[0]
				).withPropsTransformer(
					PROPS_TRANSFORMERS[0]
				)),
			_registerSystemFDSEntry(
				SystemFDSEntryFactory.create(
					FDS_NAMES[1]
				).withPropsTransformer(
					PROPS_TRANSFORMERS[0]
				)));

		Assert.assertEquals(
			PROPS_TRANSFORMERS[0],
			systemFDSSerializer.serializePropsTransformer(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializePropsTransformer(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();
	}

	@Test
	public void testSerializeSortItems() throws Exception {

		// Different sorts

		FDSSortItemList fdsSortItemList1 = FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setActive(
				true
			).setDirection(
				"asc"
			).setKey(
				IDS[0]
			).setLabel(
				LABELS[0]
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"desc"
			).setKey(
				IDS[1]
			).setLabel(
				LABELS[1]
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"desc"
			).setKey(
				IDS[2]
			).setLabel(
				LABELS[2]
			).build()
		).build();

		_registerServices(
			_registerFDSSorts(FDS_NAMES[0], fdsSortItemList1),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertEquals(
			fdsSortItemList1,
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[0], httpServletRequest));

		FDSSortItemList fdsSortItemList2 = FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"asc"
			).setKey(
				IDS[2]
			).setLabel(
				LABELS[0]
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				true
			).setDirection(
				"asc"
			).setKey(
				IDS[1]
			).setLabel(
				LABELS[1]
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"asc"
			).setKey(
				IDS[0]
			).setLabel(
				LABELS[2]
			).build()
		).build();

		_registerServices(
			_registerFDSSorts(FDS_NAMES[1], fdsSortItemList2),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			fdsSortItemList2,
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[1], httpServletRequest));

		Assert.assertNotEquals(
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();

		// No sorts

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		Assert.assertTrue(
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[0], httpServletRequest
			).isEmpty());

		_unregisterServices();

		// Shared sort

		_registerServices(
			_registerFDSSorts(FDS_NAMES[0], fdsSortItemList1),
			_registerFDSSorts(FDS_NAMES[1], fdsSortItemList1),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		Assert.assertEquals(
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[0], httpServletRequest),
			systemFDSSerializer.serializeSorts(
				FDS_NAMES[1], httpServletRequest));

		_unregisterServices();
	}

	@Test
	public void testSerializeViews() throws Exception {

		// Cards view

		mockLanguage();

		FDSView cardsFDSView = new BaseCardsFDSView() {

			@Override
			public String getDescription() {
				return DESCRIPTIONS[0];
			}

			@Override
			public FDSCardSchema getFDSCardSchema(Locale locale) {
				FDSCardSchemaBuilder fdsCardSchemaBuilder =
					new FDSCardSchemaBuilderImpl();

				return fdsCardSchemaBuilder.add(
					DISPLAY_TYPE_KEYS[0], DISPLAY_TYPE_VALUES[0]
				).add(
					DISPLAY_TYPE_KEYS[1],
					HashMapBuilder.put(
						DISPLAY_TYPE_KEYS[2], DISPLAY_TYPE_VALUES[1]
					).put(
						DISPLAY_TYPE_KEYS[3], DISPLAY_TYPE_VALUES[2]
					).build(),
					DISPLAY_TYPE_VALUES[3]
				).build();
			}

			@Override
			public String getImage() {
				return IMAGES[0];
			}

			@Override
			public String getLink() {
				return LINK;
			}

			@Override
			public String getSticker() {
				return STICKERS[0];
			}

			@Override
			public String getSymbol() {
				return SYMBOLS[0];
			}

			@Override
			public String getTitle() {
				return TITLES[0];
			}

		};

		_registerServices(
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new CardsFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.CARDS)),
			_registerFDSView(FDS_NAMES[0], cardsFDSView),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"contentRenderer", "cards"
				).put(
					"default", false
				).put(
					"label", "cards"
				).put(
					"name", "cards"
				).put(
					"schema",
					JSONUtil.put(
						"description", DESCRIPTIONS[0]
					).put(
						"image", IMAGES[0]
					).put(
						"labels",
						JSONUtil.putAll(
							JSONUtil.put(
								"displayType", DISPLAY_TYPE_KEYS[0]
							).put(
								"value", DISPLAY_TYPE_VALUES[0]
							),
							JSONUtil.put(
								"displayTypeKey", DISPLAY_TYPE_KEYS[1]
							).put(
								"displayTypeValues",
								JSONUtil.put(
									DISPLAY_TYPE_KEYS[2], DISPLAY_TYPE_VALUES[1]
								).put(
									DISPLAY_TYPE_KEYS[3], DISPLAY_TYPE_VALUES[2]
								)
							).put(
								"value", DISPLAY_TYPE_VALUES[3]
							))
					).put(
						"link", LINK
					).put(
						"sticker", STICKERS[0]
					).put(
						"symbol", SYMBOLS[0]
					).put(
						"title", TITLES[0]
					)
				).put(
					"thumbnail", "cards2"
				)
			).toString(),
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Different views

		FDSView listFDSView = new BaseListFDSView() {

			@Override
			public String getDescription() {
				return DESCRIPTIONS[1];
			}

			@Override
			public String getImage() {
				return IMAGES[1];
			}

			@Override
			public String getSticker() {
				return STICKERS[1];
			}

			@Override
			public String getSymbol() {
				return SYMBOLS[1];
			}

			@Override
			public String getTitle() {
				return TITLES[1];
			}

		};

		_registerServices(
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new CardsFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.CARDS)),
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new ListFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.LIST)),
			_registerFDSView(FDS_NAMES[0], cardsFDSView),
			_registerFDSView(FDS_NAMES[1], listFDSView),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		JSONAssert.assertNotEquals(
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			systemFDSSerializer.serializeViews(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Empty view

		_registerServices(_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			"[]",
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// List view

		_registerServices(
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new ListFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.LIST)),
			_registerFDSView(FDS_NAMES[0], listFDSView),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"contentRenderer", "list"
				).put(
					"default", false
				).put(
					"label", "list"
				).put(
					"name", "list"
				).put(
					"schema",
					JSONUtil.put(
						"description", DESCRIPTIONS[1]
					).put(
						"image", IMAGES[1]
					).put(
						"sticker", STICKERS[1]
					).put(
						"symbol", SYMBOLS[1]
					).put(
						"title", TITLES[1]
					)
				).put(
					"thumbnail", "list"
				)
			).toString(),
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Shared view

		_registerServices(
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new CardsFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.CARDS)),
			_registerFDSView(FDS_NAMES[0], cardsFDSView),
			_registerFDSView(FDS_NAMES[1], cardsFDSView),
			_registerSystemFDSEntry(FDS_NAMES[0]),
			_registerSystemFDSEntry(FDS_NAMES[1]));

		JSONAssert.assertEquals(
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			systemFDSSerializer.serializeViews(
				FDS_NAMES[1], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();

		// Table view

		_registerServices(
			bundleContext.registerService(
				FDSViewContextContributor.class,
				new TableFDSViewContextContributor(),
				MapUtil.singletonDictionary(
					"frontend.data.set.view.name", FDSConstants.TABLE)),
			_registerFDSView(
				FDS_NAMES[0],
				new BaseTableFDSView() {

					@Override
					public FDSTableSchema getFDSTableSchema(Locale locale) {
						FDSTableSchemaBuilder fdsTableSchemaBuilder =
							new FDSTableSchemaBuilderImpl();

						return fdsTableSchemaBuilder.add(
							FIELD_NAMES[0], LABELS[0],
							fdsTableSchemaField ->
								fdsTableSchemaField.setContentRenderer(
									CONTENT_RENDERERS[0])
						).add(
							FIELD_NAMES[1], LABELS[1],
							fdsTableSchemaField ->
								fdsTableSchemaField.setContentRenderer(
									CONTENT_RENDERERS[1])
						).add(
							FIELD_NAMES[2], LABELS[2],
							fdsTableSchemaField ->
								fdsTableSchemaField.setSortable(true)
						).build();
					}

					@Override
					public boolean isQuickActionsEnabled() {
						return false;
					}

				}),
			_registerSystemFDSEntry(FDS_NAMES[0]));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"contentRenderer", "table"
				).put(
					"default", false
				).put(
					"label", "table"
				).put(
					"name", "table"
				).put(
					"quickActionsEnabled", false
				).put(
					"schema",
					JSONUtil.put(
						"fields",
						JSONUtil.putAll(
							JSONUtil.put(
								"contentRenderer", CONTENT_RENDERERS[0]
							).put(
								"contentRendererClientExtension", false
							).put(
								"fieldName", FIELD_NAMES[0]
							).put(
								"label", LABELS[0]
							).put(
								"localizeLabel", true
							).put(
								"sortable", false
							),
							JSONUtil.put(
								"contentRenderer", CONTENT_RENDERERS[1]
							).put(
								"contentRendererClientExtension", false
							).put(
								"fieldName", FIELD_NAMES[1]
							).put(
								"label", LABELS[1]
							).put(
								"localizeLabel", true
							).put(
								"sortable", false
							),
							JSONUtil.put(
								"contentRendererClientExtension", false
							).put(
								"fieldName", FIELD_NAMES[2]
							).put(
								"label", LABELS[2]
							).put(
								"localizeLabel", true
							).put(
								"sortable", true
							)))
				).put(
					"thumbnail", "table"
				)
			).toString(),
			systemFDSSerializer.serializeViews(
				FDS_NAMES[0], httpServletRequest
			).toString(),
			JSONCompareMode.STRICT);

		_unregisterServices();
	}

	public class SystemFDSEntryFactory {

		public static SystemFDSEntryWrapper create(String fdsName) {
			return new SystemFDSEntryWrapper(fdsName);
		}

	}

	private FDSFilter _createFDSFilterDate(
		String id, String label, DateFDSFilterItem maxDateFDSFilterItem,
		DateFDSFilterItem minDateFDSFilterItem,
		Map<String, Object> preloadedData) {

		return new BaseDateRangeFDSFilter() {

			@Override
			public String getEntityFieldType() {
				return FDSEntityFieldTypes.DATE;
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getLabel() {
				return label;
			}

			@Override
			public DateFDSFilterItem getMaxDateFDSFilterItem() {
				return maxDateFDSFilterItem;
			}

			@Override
			public DateFDSFilterItem getMinDateFDSFilterItem() {
				return minDateFDSFilterItem;
			}

			@Override
			public Map<String, Object> getPreloadedData() {
				return preloadedData;
			}

		};
	}

	private ServiceRegistration<FDSBulkActions> _registerFDSBulkActions(
		List<FDSActionDropdownItem> fdsActionDropdownItems, String fdsName) {

		return bundleContext.registerService(
			FDSBulkActions.class,
			new FDSBulkActions() {

				@Override
				public List<FDSActionDropdownItem> getFDSActionDropdownItems(
					HttpServletRequest httpServletRequest) {

					return fdsActionDropdownItems;
				}

			},
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private ServiceRegistration<FDSCreationMenu> _registerFDSCreationMenu(
		CreationMenu creationMenu, String fdsName) {

		return bundleContext.registerService(
			FDSCreationMenu.class,
			new FDSCreationMenu() {

				@Override
				public CreationMenu getCreationMenu(
					HttpServletRequest httpServletRequest) {

					return creationMenu;
				}

			},
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private ServiceRegistration<FDSFilter> _registerFDSFilter(
		FDSFilter fdsFilter, String fdsName) {

		return bundleContext.registerService(
			FDSFilter.class, fdsFilter,
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private ServiceRegistration<FDSItemsActions> _registerFDSItemsActions(
		List<FDSActionDropdownItem> fdsActionDropdownItems, String fdsName) {

		return bundleContext.registerService(
			FDSItemsActions.class,
			new FDSItemsActions() {

				@Override
				public List<FDSActionDropdownItem> getFDSActionDropdownItems(
					HttpServletRequest httpServletRequest) {

					return fdsActionDropdownItems;
				}

			},
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private ServiceRegistration<FDSSorts> _registerFDSSorts(
		String fdsName, List<FDSSortItem> fdsSortItems) {

		return bundleContext.registerService(
			FDSSorts.class,
			new FDSSorts() {

				@Override
				public List<FDSSortItem> getFDSSortItems(
					HttpServletRequest httpServletRequest) {

					return fdsSortItems;
				}

			},
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private ServiceRegistration<FDSView> _registerFDSView(
		String fdsName, FDSView fdsView) {

		return bundleContext.registerService(
			FDSView.class, fdsView,
			MapUtil.singletonDictionary("frontend.data.set.name", fdsName));
	}

	private void _registerServices(
		ServiceRegistration<?>... serviceRegistrations) {

		for (ServiceRegistration<?> serviceRegistration :
				serviceRegistrations) {

			_serviceRegistrations.add(serviceRegistration);
		}
	}

	private ServiceRegistration<SystemFDSEntry> _registerSystemFDSEntry(
		String fdsName) {

		return _registerSystemFDSEntry(SystemFDSEntryFactory.create(fdsName));
	}

	private ServiceRegistration<SystemFDSEntry> _registerSystemFDSEntry(
		SystemFDSEntryWrapper systemFDSEntryWrapper) {

		return systemFDSEntryWrapper.register(bundleContext);
	}

	private void _unregisterServices() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private static class SystemFDSEntryWrapper {

		public SystemFDSEntryWrapper(String fdsName) {
			_fdsName = fdsName;
		}

		public ServiceRegistration<SystemFDSEntry> register(
			BundleContext bundleContext) {

			return bundleContext.registerService(
				SystemFDSEntry.class,
				new SystemFDSEntry() {

					@Override
					public String getAdditionalAPIURLParameters() {
						return _additionalURLParameters;
					}

					public int getDefaultItemsPerPage() {
						if (_defaultItemsPerPage > 0) {
							return _defaultItemsPerPage;
						}

						return SystemFDSEntry.super.getDefaultItemsPerPage();
					}

					@Override
					public String getDescription() {
						return "";
					}

					public int[] getListOfItemsPerPage() {
						if (_listOfItemsPerPage != null) {
							return _listOfItemsPerPage;
						}

						return SystemFDSEntry.super.getListOfItemsPerPage();
					}

					@Override
					public String getName() {
						return _fdsName;
					}

					@Override
					public String getPropsTransformer() {
						if (Validator.isNotNull(_propsTransformer)) {
							return _propsTransformer;
						}

						return SystemFDSEntry.super.getPropsTransformer();
					}

					@Override
					public String getRESTApplication() {
						return "/app";
					}

					@Override
					public String getRESTEndpoint() {
						return "/endpoint";
					}

					@Override
					public String getRESTSchema() {
						return "schema";
					}

					@Override
					public String getTitle() {
						return "";
					}

				},
				MapUtil.singletonDictionary(
					"frontend.data.set.name", _fdsName));
		}

		public SystemFDSEntryWrapper withAdditionalURLParameters(
			String additionalURLParameters) {

			_additionalURLParameters = additionalURLParameters;

			return this;
		}

		public SystemFDSEntryWrapper withPagination(
			int defaultItemsPerPage, int[] listOfItemsPerPage) {

			_defaultItemsPerPage = defaultItemsPerPage;
			_listOfItemsPerPage = listOfItemsPerPage;

			return this;
		}

		public SystemFDSEntryWrapper withPropsTransformer(
			String propsTransformer) {

			_propsTransformer = propsTransformer;

			return this;
		}

		private String _additionalURLParameters;
		private int _defaultItemsPerPage = -1;
		private final String _fdsName;
		private int[] _listOfItemsPerPage;
		private String _propsTransformer;

	}

}
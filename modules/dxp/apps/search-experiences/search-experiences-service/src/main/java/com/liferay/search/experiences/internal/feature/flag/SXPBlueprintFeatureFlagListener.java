/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.feature.flag;

import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.internal.model.listener.InfoCollectionProviderSXPBlueprintModelListener;
import com.liferay.search.experiences.internal.model.listener.SXPBlueprintInfoCollectionProviderSXPBlueprintModelListener;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	enabled = false, property = "feature.flag.key=LPS-129412",
	service = FeatureFlagListener.class
)
public class SXPBlueprintFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		Map.Entry
			<InfoCollectionProviderSXPBlueprintModelListener,
			 ServiceRegistration<?>> entry = null;

		if (enabled && Objects.equals(featureFlagKey, "LPS-129412")) {
			InfoCollectionProviderSXPBlueprintModelListener
				infoCollectionProviderSXPBlueprintModelListener =
					new SXPBlueprintInfoCollectionProviderSXPBlueprintModelListener(
						_assetHelper, _assetSubtypeIdentifierBuilder,
						_blogsEntryLocalService, _bundleContext,
						_calendarBookingLocalService, _classNameLocalService,
						_companyLocalService, _ddmStructureService,
						_dlFileEntryTypeLocalService, _dlAppLocalService,
						_groupService, _journalArticleService,
						_kbArticleLocalService, _objectDefinitionLocalService,
						_objectEntryLocalService, _searcher,
						_searchRequestBuilderFactory,
						_sxpBlueprintLocalService);

			infoCollectionProviderSXPBlueprintModelListener.start();

			entry = _serviceRegistrationEntries.put(
				featureFlagKey,
				new AbstractMap.SimpleImmutableEntry<>(
					infoCollectionProviderSXPBlueprintModelListener,
					_bundleContext.registerService(
						ModelListener.class,
						infoCollectionProviderSXPBlueprintModelListener,
						null)));
		}
		else {
			entry = _serviceRegistrationEntries.remove(featureFlagKey);
		}

		if (entry != null) {
			ServiceRegistration<?> serviceRegistration = entry.getValue();

			serviceRegistration.unregister();

			InfoCollectionProviderSXPBlueprintModelListener
				infoCollectionProviderSXPBlueprintModelListener =
					entry.getKey();

			infoCollectionProviderSXPBlueprintModelListener.stop();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	private BundleContext _bundleContext;

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private final Map
		<String,
		 Map.Entry
			 <InfoCollectionProviderSXPBlueprintModelListener,
			  ServiceRegistration<?>>> _serviceRegistrationEntries =
				new ConcurrentHashMap<>();

	@Reference
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}
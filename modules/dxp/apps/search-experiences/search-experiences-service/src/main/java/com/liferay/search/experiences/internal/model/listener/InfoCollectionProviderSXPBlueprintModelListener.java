/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.model.listener;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.asset.AssetSubtypeIdentifier;
import com.liferay.portal.search.asset.AssetSubtypeIdentifierBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.internal.info.collection.provider.AssetEntrySXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.BlogsEntrySXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.CalendarBookingSXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.FileEntrySXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.JournalArticleSXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.KBArticleSXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.internal.info.collection.provider.ObjectEntrySXPBlueprintInfoCollectionProvider;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.Configuration;
import com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Joshua Cords
 */
@Component(enabled = false, service = ModelListener.class)
public class InfoCollectionProviderSXPBlueprintModelListener
	extends BaseModelListener<SXPBlueprint> {

	@Override
	public void onAfterCreate(SXPBlueprint sxpBlueprint) {
		if (!_isCollectionProvider(sxpBlueprint)) {
			return;
		}

		_className = _getClassName(sxpBlueprint);

		ServiceRegistration<?> serviceRegistration = _serviceRegistrations.put(
			sxpBlueprint.getSXPBlueprintId(),
			_bundleContext.registerService(
				InfoCollectionProvider.class,
				_createInfoCollectionProvider(sxpBlueprint),
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", sxpBlueprint.getCompanyId()
				).put(
					"item.class.name", _className
				).build()));

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@Override
	public void onAfterRemove(SXPBlueprint sxpBlueprint) {
		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(sxpBlueprint.getSXPBlueprintId());

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@Override
	public void onAfterUpdate(
		SXPBlueprint originalSXPBlueprint, SXPBlueprint newSXPBlueprint) {

		onAfterRemove(originalSXPBlueprint);

		onAfterCreate(newSXPBlueprint);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_companyLocalService.forEachCompanyId(
			companyId -> {
				List<SXPBlueprint> sxpBlueprints =
					_sxpBlueprintLocalService.getSXPBlueprints(companyId);

				for (SXPBlueprint sxpBlueprint : sxpBlueprints) {
					onAfterCreate(sxpBlueprint);
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	private InfoCollectionProvider<?> _createInfoCollectionProvider(
		SXPBlueprint sxpBlueprint) {

		if (_className.equals(BlogsEntry.class.getName())) {
			return new BlogsEntrySXPBlueprintInfoCollectionProvider(
				_assetHelper, _blogsEntryLocalService, _searcher,
				_searchRequestBuilderFactory, sxpBlueprint);
		}

		if (_className.equals(CalendarBooking.class.getName())) {
			return new CalendarBookingSXPBlueprintInfoCollectionProvider(
				_assetHelper, _calendarBookingLocalService, _searcher,
				_searchRequestBuilderFactory, sxpBlueprint);
		}

		if (_className.equals(DLFileEntry.class.getName())) {
			return new FileEntrySXPBlueprintInfoCollectionProvider(
				_assetHelper, _assetSubtypeIdentifierBuilder,
				_dlAppLocalService, _dlFileEntryTypeLocalService, _groupService,
				_searcher, _searchRequestBuilderFactory, sxpBlueprint);
		}

		if (_className.equals(JournalArticle.class.getName())) {
			return new JournalArticleSXPBlueprintInfoCollectionProvider(
				_assetHelper, _assetSubtypeIdentifierBuilder,
				_classNameLocalService, _ddmStructureService, _groupService,
				_journalArticleService, _searcher, _searchRequestBuilderFactory,
				sxpBlueprint);
		}

		if (_className.equals(KBArticle.class.getName())) {
			return new KBArticleSXPBlueprintInfoCollectionProvider(
				_assetHelper, _kbArticleLocalService, _searcher,
				_searchRequestBuilderFactory, sxpBlueprint);
		}

		if (_className.contains(ObjectDefinition.class.getName())) {
			return new ObjectEntrySXPBlueprintInfoCollectionProvider(
				_assetHelper, _className, _objectDefinitionLocalService,
				_objectEntryLocalService, _searcher,
				_searchRequestBuilderFactory, sxpBlueprint);
		}

		return new AssetEntrySXPBlueprintInfoCollectionProvider(
			_assetHelper, _searcher, _searchRequestBuilderFactory,
			sxpBlueprint);
	}

	private String _getClassName(SXPBlueprint sxpBlueprint) {
		Configuration configuration = Configuration.unsafeToDTO(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		if (generalConfiguration == null) {
			return AssetEntry.class.getName();
		}

		String collectionProviderType =
			generalConfiguration.getCollectionProviderType();

		if (Validator.isNull(collectionProviderType)) {
			return AssetEntry.class.getName();
		}

		AssetSubtypeIdentifier assetSubtypeIdentifier =
			_assetSubtypeIdentifierBuilder.searchableAssetType(
				collectionProviderType
			).build();

		return assetSubtypeIdentifier.getClassName();
	}

	private boolean _isCollectionProvider(SXPBlueprint sxpBlueprint) {
		if (!JSONUtil.isJSONObject(sxpBlueprint.getConfigurationJSON())) {
			return false;
		}

		Configuration configuration = Configuration.unsafeToDTO(
			sxpBlueprint.getConfigurationJSON());

		if (configuration == null) {
			return false;
		}

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		if (generalConfiguration == null) {
			return false;
		}

		return GetterUtil.getBoolean(
			generalConfiguration.getCollectionProvider());
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

	private String _className = AssetEntry.class.getName();

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
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	private final Map<Long, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<>();

	@Reference
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

}
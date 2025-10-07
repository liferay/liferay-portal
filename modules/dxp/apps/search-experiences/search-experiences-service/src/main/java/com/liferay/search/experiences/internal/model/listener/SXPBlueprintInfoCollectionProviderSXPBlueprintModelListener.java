/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.GetterUtil;
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

import org.osgi.framework.BundleContext;

/**
 * @author Joshua Cords
 */
public class SXPBlueprintInfoCollectionProviderSXPBlueprintModelListener
	extends InfoCollectionProviderSXPBlueprintModelListener {

	public SXPBlueprintInfoCollectionProviderSXPBlueprintModelListener(
		AssetHelper assetHelper,
		AssetSubtypeIdentifierBuilder assetSubtypeIdentifierBuilder,
		BlogsEntryLocalService blogsEntryLocalService,
		BundleContext bundleContext,
		CalendarBookingLocalService calendarBookingLocalService,
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		DDMStructureService ddmStructureService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		DLAppLocalService dlAppLocalService, GroupService groupService,
		JournalArticleService journalArticleService,
		KBArticleLocalService kbArticleLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprintLocalService sxpBlueprintLocalService) {

		super(bundleContext, companyLocalService, sxpBlueprintLocalService);

		_assetHelper = assetHelper;
		_assetSubtypeIdentifierBuilder = assetSubtypeIdentifierBuilder;
		_blogsEntryLocalService = blogsEntryLocalService;
		_calendarBookingLocalService = calendarBookingLocalService;
		_classNameLocalService = classNameLocalService;
		_ddmStructureService = ddmStructureService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_dlAppLocalService = dlAppLocalService;
		_groupService = groupService;
		_journalArticleService = journalArticleService;
		_kbArticleLocalService = kbArticleLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	@Override
	public void onAfterCreate(SXPBlueprint sxpBlueprint) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-129412") ||
			!_isCollectionProvider(sxpBlueprint)) {

			return;
		}

		_className = _getClassName(sxpBlueprint);
		_sxpBlueprint = sxpBlueprint;

		super.onAfterCreate(sxpBlueprint);
	}

	@Override
	protected InfoCollectionProvider<?> createInfoCollectionProvider(
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

	@Override
	protected String getItemClassName() {
		return _className;
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
		Configuration configuration = Configuration.unsafeToDTO(
			sxpBlueprint.getConfigurationJSON());

		GeneralConfiguration generalConfiguration =
			configuration.getGeneralConfiguration();

		if (generalConfiguration == null) {
			return false;
		}

		return GetterUtil.getBoolean(
			generalConfiguration.getCollectionProvider());
	}

	private final AssetHelper _assetHelper;
	private final AssetSubtypeIdentifierBuilder _assetSubtypeIdentifierBuilder;
	private final BlogsEntryLocalService _blogsEntryLocalService;
	private final CalendarBookingLocalService _calendarBookingLocalService;
	private String _className = AssetEntry.class.getName();
	private final ClassNameLocalService _classNameLocalService;
	private final DDMStructureService _ddmStructureService;
	private final DLAppLocalService _dlAppLocalService;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final GroupService _groupService;
	private final JournalArticleService _journalArticleService;
	private final KBArticleLocalService _kbArticleLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private SXPBlueprint _sxpBlueprint;

}
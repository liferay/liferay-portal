/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.exception.NoSuchEntryException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.headless.delivery.dto.v1_0.ContentSetElement;
import com.liferay.headless.delivery.resource.v1_0.ContentSetElementResource;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-set-element.properties",
	scope = ServiceScope.PROTOTYPE, service = ContentSetElementResource.class
)
public class ContentSetElementResourceImpl
	extends BaseContentSetElementResourceImpl {

	@Override
	public Page<ContentSetElement>
			getAssetLibraryContentSetByKeyContentSetElementsPage(
				Long assetLibraryId, String key, Pagination pagination)
		throws Exception {

		return getSiteContentSetByKeyContentSetElementsPage(
			assetLibraryId, key, pagination);
	}

	@Override
	public Page<ContentSetElement>
			getAssetLibraryContentSetByUuidContentSetElementsPage(
				Long assetLibraryId, String uuid, Pagination pagination)
		throws Exception {

		return getSiteContentSetByUuidContentSetElementsPage(
			assetLibraryId, uuid, pagination);
	}

	@Override
	public Page<ContentSetElement> getContentSetContentSetElementsPage(
			Long contentSetId, Pagination pagination)
		throws Exception {

		return _getContentSetContentSetElementsPage(
			_assetListEntryService.getAssetListEntry(contentSetId), pagination);
	}

	@Override
	public Page<ContentSetElement> getSiteContentSetByKeyContentSetElementsPage(
			Long siteId, String key, Pagination pagination)
		throws Exception {

		return _getContentSetContentSetElementsPage(
			_assetListEntryService.getAssetListEntry(siteId, key), pagination);
	}

	@Override
	public Page<ContentSetElement>
			getSiteContentSetByUuidContentSetElementsPage(
				Long siteId, String uuid, Pagination pagination)
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryService.getAssetListEntryByUuidAndGroupId(
				uuid, siteId);

		return _getContentSetContentSetElementsPage(assetListEntry, pagination);
	}

	@Override
	public Page<ContentSetElement>
			getSiteContentSetProviderByKeyContentSetElementsPage(
				Long siteId, String key, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-32867")) {
			throw new UnsupportedOperationException();
		}

		InfoCollectionProvider<?> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, key);

		if (infoCollectionProvider == null) {
			throw new NoSuchEntryException();
		}

		if (!infoCollectionProvider.isAvailable() ||
			!Objects.equals(
				AssetEntry.class.getName(),
				infoCollectionProvider.getCollectionItemClassName())) {

			return Page.of(Collections.emptyList());
		}

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(siteId));

		try {
			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setPagination(
				com.liferay.info.pagination.Pagination.of(
					pagination.getEndPosition(),
					pagination.getStartPosition()));

			InfoPage<AssetEntry> infoPage =
				(InfoPage<AssetEntry>)
					infoCollectionProvider.getCollectionInfoPage(
						collectionQuery);

			return Page.of(
				transform(infoPage.getPageItems(), this::_toContentSetElement),
				pagination, infoPage.getTotalCount());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private Page<ContentSetElement> _getContentSetContentSetElementsPage(
			AssetListEntry assetListEntry, Pagination pagination)
		throws Exception {

		long[] segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
			assetListEntry.getGroupId(), contextUser.getUserId(),
			_requestContextMapper.map(contextHttpServletRequest), new long[0]);

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, segmentsEntryIds, null, null, StringPool.BLANK,
				StringPool.BLANK, pagination.getStartPosition(),
				pagination.getEndPosition());

		return Page.of(
			transform(infoPage.getPageItems(), this::_toContentSetElement),
			pagination, infoPage.getTotalCount());
	}

	private ServiceContext _getServiceContext(Long siteId) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setRequest(contextHttpServletRequest);
		serviceContext.setScopeGroupId(siteId);
		serviceContext.setUserId(contextUser.getUserId());

		_initThemeDisplay(siteId);

		return serviceContext;
	}

	private void _initThemeDisplay(Long siteId) throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(siteId);
		themeDisplay.setSiteGroupId(siteId);
	}

	private ContentSetElement _toContentSetElement(AssetEntry assetEntry) {
		DTOConverter<?, ?> dtoConverter = _dtoConverterRegistry.getDTOConverter(
			assetEntry.getClassName());

		return new ContentSetElement() {
			{
				setContent(
					() -> {
						if (dtoConverter == null) {
							return null;
						}

						return dtoConverter.toDTO(
							new DefaultDTOConverterContext(
								contextAcceptLanguage.isAcceptAllLanguages(),
								new HashMap<>(), _dtoConverterRegistry,
								contextHttpServletRequest,
								assetEntry.getClassPK(),
								contextAcceptLanguage.getPreferredLocale(),
								contextUriInfo, contextUser));
					});
				setContentType(
					() -> {
						if (dtoConverter == null) {
							return assetEntry.getClassName();
						}

						return dtoConverter.getContentType();
					});
				setId(assetEntry::getClassPK);
				setTitle(
					() -> assetEntry.getTitle(
						contextAcceptLanguage.getPreferredLocale()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						assetEntry.getTitleMap()));
			}
		};
	}

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

}
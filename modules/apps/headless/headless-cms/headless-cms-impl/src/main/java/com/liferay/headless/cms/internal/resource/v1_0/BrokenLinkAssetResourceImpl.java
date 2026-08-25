/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.BrokenLinkAsset;
import com.liferay.headless.cms.internal.links.BrokenLinkAssetSearcher;
import com.liferay.headless.cms.internal.util.CMSGroupUtil;
import com.liferay.headless.cms.resource.v1_0.BrokenLinkAssetResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jürgen Kappler
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/broken-link-asset.properties",
	scope = ServiceScope.PROTOTYPE, service = BrokenLinkAssetResource.class
)
public class BrokenLinkAssetResourceImpl
	extends BaseBrokenLinkAssetResourceImpl {

	@Override
	public Page<BrokenLinkAsset> getBrokenLinkAssetsPage(
			Long assetLibraryId, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-82226")) {

			throw new UnsupportedOperationException();
		}

		Long[] spaceGroupIds = CMSGroupUtil.getSpaceGroupIds(
			contextCompany.getCompanyId(), _depotEntryService,
			contextUser.getUserId());

		Long[] selectedSpaceGroupIds = CMSGroupUtil.getSelectedSpaceGroupIds(
			assetLibraryId, contextCompany.getCompanyId(),
			_depotEntryLocalService, groupLocalService, spaceGroupIds);

		if (ArrayUtil.isEmpty(selectedSpaceGroupIds)) {
			return Page.of(Collections.emptyList());
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionService.getCMSObjectDefinitions(
				contextCompany.getCompanyId(),
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				});

		Long[] objectDefinitionIds = transformToArray(
			objectDefinitions, ObjectDefinition::getObjectDefinitionId,
			Long.class);

		if (ArrayUtil.isEmpty(objectDefinitionIds)) {
			return Page.of(Collections.emptyList());
		}

		BrokenLinkAssetSearcher brokenLinkAssetSearcher =
			new BrokenLinkAssetSearcher(
				_objectEntryLocalService, _searcher,
				_searchRequestBuilderFactory);

		Map<String, Long> expiredAssetObjectEntryIdsMap =
			brokenLinkAssetSearcher.getExpiredAssetObjectEntryIdsMap(
				contextCompany.getCompanyId(), objectDefinitionIds,
				spaceGroupIds);

		if (expiredAssetObjectEntryIdsMap.isEmpty()) {
			return Page.of(Collections.emptyList());
		}

		SearchResponse searchResponse = brokenLinkAssetSearcher.search(
			contextCompany.getCompanyId(), selectedSpaceGroupIds,
			contextAcceptLanguage.getPreferredLanguageId(),
			expiredAssetObjectEntryIdsMap.keySet(), pagination, search, sorts);

		Map<Long, String> externalReferenceCodes = new HashMap<>();

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			externalReferenceCodes.put(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getExternalReferenceCode());
		}

		return Page.of(
			transform(
				searchResponse.getDocuments(),
				document -> _toBrokenLinkAsset(
					document, expiredAssetObjectEntryIdsMap,
					externalReferenceCodes)),
			pagination, searchResponse.getCount());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	private String _getBrokenLinkTitle(Set<Long> brokenLinkObjectEntryIds)
		throws PortalException {

		for (long brokenLinkObjectEntryId : brokenLinkObjectEntryIds) {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				brokenLinkObjectEntryId);

			if (objectEntry == null) {
				continue;
			}

			return objectEntry.getTitleValue(
				contextAcceptLanguage.getPreferredLanguageId(), true);
		}

		return null;
	}

	private String _getTitle(Document document) {
		String title = document.getString(
			Field.getLocalizedName(
				contextAcceptLanguage.getPreferredLocale(),
				"objectEntryTitle"));

		if (Validator.isNotNull(title)) {
			return title;
		}

		return document.getString("objectEntryTitle");
	}

	private BrokenLinkAsset _toBrokenLinkAsset(
		Document document, Map<String, Long> expiredAssetObjectEntryIdsMap,
		Map<Long, String> externalReferenceCodes) {

		Set<Long> brokenLinkObjectEntryIds = new LinkedHashSet<>();

		for (String outboundLink : document.getStrings("outboundLinks")) {
			Long brokenLinkObjectEntryId = expiredAssetObjectEntryIdsMap.get(
				outboundLink);

			if (brokenLinkObjectEntryId != null) {
				brokenLinkObjectEntryIds.add(brokenLinkObjectEntryId);
			}
		}

		long objectDefinitionId = GetterUtil.getLong(
			document.getString("objectDefinitionId"));

		long objectEntryId = GetterUtil.getLong(
			document.getString(Field.ENTRY_CLASS_PK));

		return new BrokenLinkAsset() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"update",
						() -> addAction(
							ActionKeys.UPDATE, objectEntryId,
							"getBrokenLinkAssetsPage",
							_objectEntryService.getModelResourcePermission(
								objectDefinitionId))
					).build());
				setBrokenLinksCount(
					() -> (long)brokenLinkObjectEntryIds.size());
				setBrokenLinkTitle(
					() -> _getBrokenLinkTitle(brokenLinkObjectEntryIds));
				setHref(
					() -> StringBundler.concat(
						_portal.getPortalURL(contextHttpServletRequest),
						_portal.getPathMain(), GroupConstants.CMS_FRIENDLY_URL,
						"/edit_content_item?p_l_mode=read&p_p_state=",
						LiferayWindowState.POP_UP, "&objectEntryId=",
						objectEntryId));
				setId(() -> objectEntryId);
				setObjectDefinitionExternalReferenceCode(
					() -> externalReferenceCodes.get(objectDefinitionId));
				setTitle(() -> _getTitle(document));
			}
		};
	}

	private static final EntityModel _entityModel =
		() -> EntityModel.toEntityFieldsMap(
			new StringEntityField(
				"title",
				locale -> Field.getSortableFieldName(
					Field.getLocalizedName(locale, "localized_title"))));

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}
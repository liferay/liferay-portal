/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.MissingRequiredFieldsAsset;
import com.liferay.headless.cms.internal.field.MissingRequiredFieldsAssetSearcher;
import com.liferay.headless.cms.internal.util.CMSGroupUtil;
import com.liferay.headless.cms.resource.v1_0.MissingRequiredFieldsAssetResource;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
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
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Mikel Lorza
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/missing-required-fields-asset.properties",
	scope = ServiceScope.PROTOTYPE,
	service = MissingRequiredFieldsAssetResource.class
)
public class MissingRequiredFieldsAssetResourceImpl
	extends BaseMissingRequiredFieldsAssetResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<MissingRequiredFieldsAsset> getMissingRequiredFieldsAssetsPage(
			Long assetLibraryId, String search, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		Long[] selectedSpaceGroupIds = CMSGroupUtil.getSelectedSpaceGroupIds(
			assetLibraryId, contextCompany.getCompanyId(),
			_depotEntryLocalService, groupLocalService,
			CMSGroupUtil.getSpaceGroupIds(
				contextCompany.getCompanyId(), _depotEntryService,
				contextUser.getUserId()));

		if (ArrayUtil.isEmpty(selectedSpaceGroupIds)) {
			return Page.of(Collections.emptyList());
		}

		MissingRequiredFieldsAssetSearcher missingRequiredFieldsAssetSearcher =
			new MissingRequiredFieldsAssetSearcher(
				_searcher, _searchRequestBuilderFactory);

		SearchResponse searchResponse =
			missingRequiredFieldsAssetSearcher.search(
				contextCompany.getCompanyId(), selectedSpaceGroupIds,
				contextAcceptLanguage.getPreferredLanguageId(), pagination,
				search, sorts);

		Map<Long, String> externalReferenceCodes = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				_objectDefinitionService.getCMSObjectDefinitions(
					contextCompany.getCompanyId(),
					new String[] {
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
						ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
					})) {

			externalReferenceCodes.put(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getExternalReferenceCode());
		}

		return Page.of(
			transform(
				searchResponse.getDocuments(),
				document -> _toMissingRequiredFieldsAsset(
					document, externalReferenceCodes)),
			pagination, searchResponse.getCount());
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

	private MissingRequiredFieldsAsset _toMissingRequiredFieldsAsset(
		Document document, Map<Long, String> externalReferenceCodes) {

		long objectDefinitionId = GetterUtil.getLong(
			document.getString("objectDefinitionId"));
		long objectEntryId = GetterUtil.getLong(
			document.getString(Field.ENTRY_CLASS_PK));

		return new MissingRequiredFieldsAsset() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"update",
						() -> addAction(
							ActionKeys.UPDATE, objectEntryId,
							"getMissingRequiredFieldsAssetsPage",
							_objectEntryService.getModelResourcePermission(
								objectDefinitionId))
					).build());
				setHref(
					() -> StringBundler.concat(
						_portal.getPortalURL(contextHttpServletRequest),
						_portal.getPathMain(), GroupConstants.CMS_FRIENDLY_URL,
						"/edit_content_item?p_l_mode=read&p_p_state=",
						LiferayWindowState.POP_UP, "&objectEntryId=",
						objectEntryId));
				setId(() -> objectEntryId);
				setMissingRequiredFieldsCount(
					() -> GetterUtil.getLong(
						document.getString("missingRequiredFieldsCount")));
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
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

}
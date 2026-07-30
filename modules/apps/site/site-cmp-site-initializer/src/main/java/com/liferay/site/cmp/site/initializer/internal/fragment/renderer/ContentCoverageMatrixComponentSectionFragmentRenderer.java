/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cmp.site.initializer.internal.util.ObjectEntryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Larissa Ribeiro
 */
@Component(service = FragmentRenderer.class)
public class ContentCoverageMatrixComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getComponentName(HttpServletRequest httpServletRequest) {
		return "ContentGapMatrixCard";
	}

	@Override
	protected String getLabelKey() {
		return "content-coverage-matrix";
	}

	@Override
	protected String getModuleName() {
		return "site-cmp-site-initializer";
	}

	@Override
	protected Map<String, Object> getProps(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		ObjectEntry objectEntry = ObjectEntryUtil.getObjectEntry(
			httpServletRequest);

		if (objectEntry == null) {
			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (objectDefinition == null) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"assetFDSId",
			"com.liferay.site.cms.site.initializer-allRelatedAssetsSection"
		).put(
			"cmpProjectObjectEntryId", objectEntry.getObjectEntryId()
		).put(
			"cmpProjectObjectEntryTitle",
			MapUtil.getString(objectEntry.getValues(), "title")
		).put(
			"cmpProjectScopeKey",
			() -> {
				Group group = _groupLocalService.fetchGroup(
					objectEntry.getGroupId());

				if (group == null) {
					return null;
				}

				return group.getGroupKey();
			}
		).put(
			"editProjectURL",
			() -> {
				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							objectEntry.getModelClassName());

				if (!modelResourcePermission.contains(
						themeDisplay.getPermissionChecker(),
						objectEntry.getObjectEntryId(), ActionKeys.UPDATE)) {

					return null;
				}

				return StringBundler.concat(
					ActionUtil.getBaseEditProjectURL(
						objectDefinition, themeDisplay),
					objectEntry.getObjectEntryId(), "?redirect=",
					themeDisplay.getURLCurrent());
			}
		).put(
			"hasFunnelStagesOrPersonas",
			() -> {
				AssetVocabulary funnelStageAssetVocabulary =
					_assetVocabularyLocalService.
						fetchAssetVocabularyByExternalReferenceCode(
							"L_CMP_FUNNEL_STAGE",
							themeDisplay.getSiteGroupId());
				AssetVocabulary personasAssetVocabulary =
					_assetVocabularyLocalService.
						fetchAssetVocabularyByExternalReferenceCode(
							"L_CMP_PERSONAS", themeDisplay.getSiteGroupId());

				for (AssetCategory assetCategory :
						_assetCategoryLocalService.getCategories(
							objectEntry.getModelClassName(),
							objectEntry.getObjectEntryId())) {

					if (((funnelStageAssetVocabulary != null) &&
						 (assetCategory.getVocabularyId() ==
							 funnelStageAssetVocabulary.getVocabularyId())) ||
						((personasAssetVocabulary != null) &&
						 (assetCategory.getVocabularyId() ==
							 personasAssetVocabulary.getVocabularyId()))) {

						return true;
					}
				}

				return false;
			}
		).build();
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
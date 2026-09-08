/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.dto.v1_0.Experience;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PagePermission;
import com.liferay.headless.delivery.dto.v1_0.ParentSitePage;
import com.liferay.headless.delivery.dto.v1_0.SitePage;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.PageSettingsUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RenderedPageUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier de Arcos
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"default=true", "dto.class.name=com.liferay.portal.kernel.model.Layout"
	},
	service = DTOConverter.class
)
public class SitePageDTOConverter implements DTOConverter<Layout, SitePage> {

	@Override
	public String getContentType() {
		return SitePage.class.getSimpleName();
	}

	@Override
	public SitePage toDTO(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws Exception {

		return new SitePage() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							Layout.class.getName(), layout.getPlid())));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						layout.getAvailableLanguageIds()));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(layout.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						Layout.class.getName(), layout.getPlid(),
						layout.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(layout::getCreateDate);
				setDateModified(layout::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExperience(
					() -> {
						boolean showSegmentsExperience = GetterUtil.getBoolean(
							dtoConverterContext.getAttribute("showExperience"));

						if (!showSegmentsExperience) {
							return null;
						}

						SegmentsExperience segmentsExperience =
							(SegmentsExperience)
								dtoConverterContext.getAttribute(
									"segmentsExperience");

						return _experienceDTOConverter.toDTO(
							dtoConverterContext, segmentsExperience);
					});
				setFriendlyUrlPath(
					() -> layout.getFriendlyURL(
						dtoConverterContext.getLocale()));
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						layout.getFriendlyURLMap()));
				setId(layout::getPlid);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							Layout.class.getName(), layout.getPlid()),
						AssetTag.NAME_ACCESSOR));
				setPageDefinition(
					() -> {
						boolean embeddedPageDefinition = GetterUtil.getBoolean(
							dtoConverterContext.getAttribute(
								"embeddedPageDefinition"));

						if (!layout.isTypeContent() ||
							!embeddedPageDefinition) {

							return null;
						}

						dtoConverterContext.setAttribute("layout", layout);

						LayoutPageTemplateStructure
							layoutPageTemplateStructure =
								_layoutPageTemplateStructureLocalService.
									fetchLayoutPageTemplateStructure(
										layout.getGroupId(), layout.getPlid());

						if (layoutPageTemplateStructure == null) {
							return null;
						}

						String segmentsExperienceKey =
							SegmentsExperienceConstants.KEY_DEFAULT;

						SegmentsExperience segmentsExperience =
							(SegmentsExperience)
								dtoConverterContext.getAttribute(
									"segmentsExperience");

						if (segmentsExperience != null) {
							segmentsExperienceKey =
								segmentsExperience.getSegmentsExperienceKey();
						}

						LayoutStructure layoutStructure = LayoutStructure.of(
							layoutPageTemplateStructure.getData(
								segmentsExperienceKey));

						return _pageDefinitionDTOConverter.toDTO(
							dtoConverterContext, layoutStructure);
					});
				setPagePermissions(
					() -> {
						List<ResourcePermission> resourcePermissions =
							_resourcePermissionLocalService.
								getResourcePermissions(
									layout.getCompanyId(),
									Layout.class.getName(),
									ResourceConstants.SCOPE_INDIVIDUAL,
									String.valueOf(layout.getPlid()));

						if (ListUtil.isEmpty(resourcePermissions)) {
							return null;
						}

						List<ResourceAction> resourceActions =
							_resourceActionLocalService.getResourceActions(
								Layout.class.getName());

						if (ListUtil.isEmpty(resourceActions)) {
							return null;
						}

						ArrayList<PagePermission> pagePermissions =
							new ArrayList<>();

						for (ResourcePermission resourcePermission :
								resourcePermissions) {

							Role role = _roleLocalService.fetchRole(
								resourcePermission.getRoleId());

							if (role == null) {
								if (_log.isWarnEnabled()) {
									_log.warn(
										String.format(
											"Resource permission %s will not " +
												"be included since no role " +
													"was found with role ID %s",
											resourcePermission.getName(),
											resourcePermission.getRoleId()));
								}

								continue;
							}

							Set<String> actionIdsSet = new HashSet<>();

							long actionIds = resourcePermission.getActionIds();

							for (ResourceAction resourceAction :
									resourceActions) {

								long bitwiseValue =
									resourceAction.getBitwiseValue();

								if ((actionIds & bitwiseValue) ==
										bitwiseValue) {

									actionIdsSet.add(
										resourceAction.getActionId());
								}
							}

							String roleKey = role.getName();

							if (role.getClassNameId() == _portal.getClassNameId(
									Team.class)) {

								Team team = _teamLocalService.fetchTeam(
									role.getClassPK());

								if (team != null) {
									roleKey = team.getName();
								}
							}

							String finalRoleKey = roleKey;

							pagePermissions.add(
								new PagePermission() {
									{
										setActionKeys(
											() -> actionIdsSet.toArray(
												new String[0]));
										setRoleKey(() -> finalRoleKey);
									}
								});
						}

						return pagePermissions.toArray(new PagePermission[0]);
					});
				setPageSettings(
					() -> PageSettingsUtil.getPageSettings(
						_dlAppService, _dlURLHelper, dtoConverterContext,
						_layoutSEOEntryLocalService, layout));
				setPageType(
					() -> {
						LayoutTypeController layoutTypeController =
							LayoutTypeControllerTracker.getLayoutTypeController(
								layout.getType());

						return _language.get(
							dtoConverterContext.getHttpServletRequest(),
							ResourceBundleUtil.getBundle(
								"content.Language",
								dtoConverterContext.getLocale(),
								layoutTypeController.getClass()),
							"layout.types." + layout.getType());
					});
				setParentSitePage(
					() -> {
						if (layout.getParentLayoutId() ==
								LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {

							return null;
						}

						Layout parentLayout = _layoutService.getLayout(
							layout.getParentPlid());

						return new ParentSitePage() {
							{
								setFriendlyUrlPath(
									parentLayout::getFriendlyURL);
							}
						};
					});
				setRenderedPage(
					() -> RenderedPageUtil.getRenderedPage(
						dtoConverterContext, layout,
						_layoutPageTemplateEntryLocalService, _portal));
				setSiteId(layout::getGroupId);
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							Layout.class.getName(), layout.getPlid()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setTitle(() -> layout.getName(dtoConverterContext.getLocale()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						layout.getNameMap()));
				setUuid(layout::getUuid);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitePageDTOConverter.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.ExperienceDTOConverter)"
	)
	private DTOConverter<SegmentsExperience, Experience>
		_experienceDTOConverter;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.PageDefinitionDTOConverter)"
	)
	private DTOConverter<LayoutStructure, PageDefinition>
		_pageDefinitionDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TeamLocalService _teamLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
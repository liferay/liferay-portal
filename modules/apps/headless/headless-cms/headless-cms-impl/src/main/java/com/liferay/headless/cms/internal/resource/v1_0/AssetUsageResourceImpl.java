/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.headless.cms.dto.v1_0.AssetUsage;
import com.liferay.headless.cms.resource.v1_0.AssetUsageResource;
import com.liferay.layout.model.LayoutClassedModelUsageTable;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.util.constants.LayoutClassedModelUsageConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-usage.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetUsageResource.class
)
public class AssetUsageResourceImpl extends BaseAssetUsageResourceImpl {

	@Override
	public Page<AssetUsage> getAssetUsagesAssetPage(
			Long assetId, String search, Pagination pagination, Sort[] sorts)
		throws Exception {

		List<AssetUsage> assetUsages = _getAssetUsages(assetId, search);

		Sort sort = null;

		if (ArrayUtil.isEmpty(sorts)) {
			sort = new Sort("name", false);
		}
		else {
			sort = sorts[0];
		}

		boolean reverse = sort.isReverse();

		return Page.of(
			ListUtil.subList(
				ListUtil.sort(
					assetUsages,
					(assetUsage1, assetUsage2) -> {
						String name = assetUsage1.getName();

						int value = name.compareTo(assetUsage2.getName());

						if (!reverse) {
							return value;
						}

						return -value;
					}),
				pagination.getStartPosition(), pagination.getEndPosition()),
			pagination, assetUsages.size());
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	private ThemeDisplay _createThemeDisplay(Layout layout) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(contextCompany);
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLocale(contextAcceptLanguage.getPreferredLocale());
		themeDisplay.setPathMain(_portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(layout.getPlid());

		String portalURL = _portal.getPortalURL(contextHttpServletRequest);

		themeDisplay.setPortalDomain(HttpComponentsUtil.getDomain(portalURL));
		themeDisplay.setPortalURL(portalURL);

		themeDisplay.setRequest(contextHttpServletRequest);
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());
		themeDisplay.setURLCurrent(
			_portal.getCurrentURL(contextHttpServletRequest));
		themeDisplay.setUser(contextUser);

		return themeDisplay;
	}

	private List<AssetUsage> _getAssetUsages(Long assetId, String search)
		throws Exception {

		List<AssetUsage> assetUsages = new ArrayList<>();

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			assetId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		assetUsages.addAll(
			_getLayoutAssetUsages(
				assetId, objectDefinition.getClassName(), search));
		assetUsages.addAll(
			_getObjectEntryAssetUsages(assetId, objectDefinition, search));

		return assetUsages;
	}

	private Long[] _getGroupIds(List<DepotEntry> depotEntries) {
		List<Long> groupIds = new ArrayList<>();

		for (DepotEntry depotEntry : depotEntries) {
			groupIds.add(depotEntry.getGroupId());

			List<DepotEntryGroupRel> depotEntryGroupRels =
				_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
					depotEntry);

			for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
				groupIds.add(depotEntryGroupRel.getGroupId());
			}
		}

		return groupIds.toArray(new Long[0]);
	}

	private List<AssetUsage> _getLayoutAssetUsages(
		Long assetId, String className, String search) {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(contextUser);

		return transform(
			_getLayoutClassedModelUsageObjectsList(className, assetId, search),
			objects -> {
				Layout layout = _layoutLocalService.getLayout((Long)objects[0]);

				Integer finalType = (Integer)objects[1];

				if ((finalType ==
						LayoutClassedModelUsageConstants.
							TYPE_DISPLAY_PAGE_TEMPLATE) ||
					(finalType ==
						LayoutClassedModelUsageConstants.TYPE_PAGE_TEMPLATE)) {

					if (!_hasPermission(
							ActionKeys.UPDATE, layout, permissionChecker)) {

						return null;
					}
				}
				else if (!_hasPermission(
							ActionKeys.VIEW, layout, permissionChecker)) {

					return null;
				}

				return new AssetUsage() {
					{
						setName(
							() -> _getName(
								layout.isDraftLayout(),
								_localization.getLocalization(
									(String)objects[2],
									contextUser.getLanguageId(), true)));
						setType(() -> _getLayoutUsageTypeLabel(finalType));
						setUrl(() -> _getLayoutURL(layout, finalType));
					}
				};
			});
	}

	private List<Object[]> _getLayoutClassedModelUsageObjectsList(
		String className, Long assetId, String search) {

		LayoutClassedModelUsageTable layoutClassedModelUsageTable =
			LayoutClassedModelUsageTable.INSTANCE;

		LayoutTable layoutTable = LayoutTable.INSTANCE;

		return _layoutClassedModelUsageLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				layoutClassedModelUsageTable.plid,
				layoutClassedModelUsageTable.type, layoutTable.name
			).from(
				layoutClassedModelUsageTable
			).innerJoinON(
				layoutTable,
				layoutClassedModelUsageTable.plid.eq(layoutTable.plid)
			).where(
				layoutClassedModelUsageTable.classNameId.eq(
					_portal.getClassNameId(className)
				).and(
					layoutClassedModelUsageTable.classPK.eq(assetId)
				).and(
					layoutClassedModelUsageTable.containerKey.isNotNull()
				).and(
					layoutClassedModelUsageTable.groupId.in(
						ArrayUtil.toArray(contextUser.getGroupIds()))
				).and(
					layoutTable.name.like(
						StringBundler.concat(
							"%<Name%>%", GetterUtil.getString(search, ""),
							"%</Name>%"))
				)
			));
	}

	private String _getLayoutURL(Layout layout, Integer type) throws Exception {
		if (type ==
				LayoutClassedModelUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE) {

			return null;
		}

		ThemeDisplay themeDisplay = _createThemeDisplay(layout);

		if (type == LayoutClassedModelUsageConstants.TYPE_PAGE_TEMPLATE) {
			String layoutFullURL = PortalUtil.getLayoutFullURL(
				layout, themeDisplay);

			layoutFullURL = HttpComponentsUtil.addParameter(
				layoutFullURL, "p_l_mode", Constants.PREVIEW);

			return HttpComponentsUtil.addParameter(
				layoutFullURL, "p_p_auth",
				AuthTokenUtil.getToken(contextHttpServletRequest));
		}

		return _portal.getLayoutURL(layout, themeDisplay);
	}

	private String _getLayoutUsageTypeLabel(long type) {
		if (type ==
				LayoutClassedModelUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE) {

			return LanguageUtil.get(
				contextUser.getLocale(), "display-page-template");
		}
		else if (type == LayoutClassedModelUsageConstants.TYPE_PAGE_TEMPLATE) {
			return LanguageUtil.get(contextUser.getLocale(), "page-template");
		}

		return LanguageUtil.get(contextUser.getLocale(), "page");
	}

	private String _getName(boolean draft, String name) {
		if (draft) {
			name += StringBundler.concat(
				StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
				LanguageUtil.get(
					LocaleUtil.fromLanguageId(
						contextUser.getLanguageId(), true, true),
					"draft"),
				StringPool.CLOSE_PARENTHESIS);
		}

		return name;
	}

	private List<AssetUsage> _getObjectEntryAssetUsages(
			Long assetId, ObjectDefinition objectDefinition, String search)
		throws Exception {

		List<AssetUsage> assetUsages = new ArrayList<>();

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipLocalService.getObjectRelationships(
				objectDefinition.getObjectDefinitionId());

		Long[] groupIds = _getGroupIds(_getViewableDepotEntries());

		String languageId = contextUser.getLanguageId();

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						objectDefinition.getClassName(),
						contextCompany.getCompanyId(),
						objectRelationship.getType());

			List<ObjectEntry> objectEntries =
				objectRelatedModelsProvider.getRelatedModels(
					0, objectRelationship.getObjectRelationshipId(),
					ObjectEntryTable.INSTANCE.groupId.in(groupIds), false,
					assetId, search, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

			for (ObjectEntry objectEntry : objectEntries) {
				String finalName = _getName(
					objectEntry.isDraft(),
					objectEntry.getTitleValue(languageId));

				if (!Validator.isBlank(search) &&
					!StringUtil.containsIgnoreCase(finalName, search)) {

					continue;
				}

				ObjectDefinition relatedObjectDefinition =
					_objectDefinitionLocalService.getObjectDefinition(
						objectEntry.getObjectDefinitionId());

				assetUsages.add(
					new AssetUsage() {
						{
							setName(() -> finalName);
							setType(
								() -> relatedObjectDefinition.getLabel(
									languageId));
							setUrl(
								() -> StringBundler.concat(
									_portal.getPortalURL(
										contextHttpServletRequest),
									_portal.getPathMain(),
									GroupConstants.CMS_FRIENDLY_URL,
									"/edit_content_item?&p_l_mode=read&",
									"p_p_state=", LiferayWindowState.POP_UP,
									"&objectEntryId=",
									objectEntry.getObjectEntryId()));
						}
					});
			}
		}

		return assetUsages;
	}

	private List<DepotEntry> _getViewableDepotEntries() throws Exception {
		List<DepotEntry> depotEntries = new ArrayList<>();

		SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			null, DepotEntry.class.getName(), null,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			queryConfig -> {
			},
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			null,
			document -> {
				try {
					depotEntries.add(
						_depotEntryService.getDepotEntry(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK))));
				}
				catch (PortalException portalException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"User does not have access to view space " +
								document.get(Field.ENTRY_CLASS_PK),
							portalException);
					}
				}

				return null;
			});

		return depotEntries;
	}

	private boolean _hasPermission(
			String actionId, Layout layout, PermissionChecker permissionChecker)
		throws Exception {

		long plid = layout.getPlid();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		return LayoutPermissionUtil.contains(permissionChecker, plid, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetUsageResourceImpl.class);

	private static final EntityModel _entityModel =
		() -> EntityModel.toEntityFieldsMap(
			new StringEntityField("name", locale -> "name"));

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

}
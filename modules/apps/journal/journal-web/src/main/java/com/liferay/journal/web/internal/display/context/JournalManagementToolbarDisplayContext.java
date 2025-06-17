/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.util.comparator.StructureModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.StructureNameComparator;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.translation.url.provider.TranslationURLProvider;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class JournalManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public JournalManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			JournalDisplayContext journalDisplayContext,
			TrashHelper trashHelper)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			journalDisplayContext.getSearchContainer());

		_journalDisplayContext = journalDisplayContext;
		_trashHelper = trashHelper;

		_assetVocabularyLocalService =
			(AssetVocabularyLocalService)httpServletRequest.getAttribute(
				AssetVocabularyLocalService.class.getName());
		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_journalWebConfiguration =
			(JournalWebConfiguration)httpServletRequest.getAttribute(
				JournalWebConfiguration.class.getName());
		_siteConnectedGroupGroupProvider =
			(SiteConnectedGroupGroupProvider)httpServletRequest.getAttribute(
				SiteConnectedGroupGroupProvider.class.getName());
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
		_translationURLProvider =
			(TranslationURLProvider)httpServletRequest.getAttribute(
				TranslationURLProvider.class.getName());
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "expireEntries");
							dropdownItem.setIcon("time");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "expire"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "moveEntries");
							dropdownItem.setIcon("move-folder");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "move"));
							dropdownItem.setQuickAction(true);
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData("action", "exportTranslation");
							dropdownItem.setIcon("upload");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									"export-for-translations"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "changePermissions");
							dropdownItem.putData(
								"maxItemsToShowInfoMessage",
								String.valueOf(200));
							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "permissions"));
							dropdownItem.setQuickAction(false);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "deleteEntries");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			() -> {
				Group group = _themeDisplay.getScopeGroup();

				if (_isShowPublishArticlesAction() && !group.isLayout()) {
					return true;
				}

				return false;
			},
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "publishEntriesToLive");
							dropdownItem.setIcon("live");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									"publish-selected-elements"));
							dropdownItem.setQuickAction(false);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addArticleURL",
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/journal/edit_article"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"folderId", _journalDisplayContext.getFolderId()
			).setParameter(
				"groupId", _themeDisplay.getScopeGroupId()
			).buildString()
		).put(
			"changePermissionsURL",
			() -> PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/journal/change_articles_permissions"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"exportTranslationURL",
			() -> PortletURLBuilder.create(
				_translationURLProvider.getExportTranslationURL(
					_themeDisplay.getScopeGroupId(),
					PortalUtil.getClassNameId(JournalArticle.class.getName()),
					RequestBackedPortletURLFactoryUtil.create(
						liferayPortletRequest))
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"moveArticlesAndFoldersURL",
			() -> {
				String redirect = ParamUtil.getString(
					liferayPortletRequest, "redirect",
					_themeDisplay.getURLCurrent());

				String referringPortletResource = ParamUtil.getString(
					liferayPortletRequest, "referringPortletResource");

				return PortletURLBuilder.createRenderURL(
					liferayPortletResponse
				).setMVCPath(
					"/move_articles_and_folders.jsp"
				).setRedirect(
					redirect
				).setParameter(
					"referringPortletResource", referringPortletResource
				).buildString();
			}
		).put(
			"openViewMoreStructuresURL",
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCPath(
				"/view_more_menu_items.jsp"
			).setParameter(
				"eventName",
				liferayPortletResponse.getNamespace() + "selectAddMenuItem"
			).setParameter(
				"folderId", _journalDisplayContext.getFolderId()
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"selectCategoryURL", _getAssetCategorySelectorURL()
		).put(
			"selectEntityURL", _journalDisplayContext.getSelectDDMStructureURL()
		).put(
			"selectTagURL", _getAssetTagSelectorURL()
		).put(
			"trashEnabled", _isTrashEnabled()
		).put(
			"viewDDMStructureArticlesURL",
			PortletURLBuilder.create(
				getPortletURL()
			).setNavigation(
				"structure"
			).setParameter(
				"ddmStructureId", (String)null
			).buildString()
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).setParameter(
			"assetCategoryId", (String)null
		).setParameter(
			"assetTagId", (String)null
		).setParameter(
			"ddmStructureId", (String)null
		).setParameter(
			"navigationMine", (Boolean)null
		).setParameter(
			"navigationRecent", (Boolean)null
		).setParameter(
			"orderByCol", StringPool.BLANK
		).setParameter(
			"orderByType", StringPool.BLANK
		).setParameter(
			"searchIn", StringPool.BLANK
		).setParameter(
			"status", WorkflowConstants.STATUS_ANY
		).setParameter(
			"type", (String)null
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "journalWebManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		try {
			return _getCreationMenu();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get creation menu", portalException);
			}
		}

		return null;
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					getFilterNavigationDropdownItemsLabel());
			}
		).addGroup(
			_journalDisplayContext::isIndexAllArticleVersions,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					getFilterStatusDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-status"));
			}
		).addGroup(
			_journalDisplayContext::isIndexAllArticleVersions,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(
									_journalDisplayContext.getType(),
									"web-content"));
							dropdownItem.setHref(
								getPortletURL(), "type", "web-content");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "web-content"));
						}
					).add(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(
									_journalDisplayContext.getType(),
									"versions"));
							dropdownItem.setHref(
								getPortletURL(), "type", "versions");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "versions"));
						}
					).build());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-type"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		int status = _journalDisplayContext.getStatus();

		LabelItemListBuilder.LabelItemListWrapper labelItemListWrapper =
			new LabelItemListBuilder.LabelItemListWrapper();

		labelItemListWrapper.add(
			_journalDisplayContext::isNavigationMine,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).setParameter(
						"navigationMine", (String)null
					).buildString());
				labelItem.setCloseable(true);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				User user = themeDisplay.getUser();

				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "owner") + ": " +
						user.getFullName());
			}
		).add(
			_journalDisplayContext::isNavigationRecent,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).setParameter(
						"navigationRecent", (String)null
					).buildString());
				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "recent"));
			}
		).add(
			_journalDisplayContext::isNavigationStructure,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).setParameter(
						"ddmStructureId", (String)null
					).buildString());

				labelItem.setCloseable(true);

				String ddmStructureName =
					_journalDisplayContext.getDDMStructureName();

				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "structures") + ": " +
						ddmStructureName);
			}
		).add(
			() -> status != WorkflowConstants.STATUS_ANY,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setParameter(
						"status", WorkflowConstants.STATUS_ANY
					).buildString());

				labelItem.setCloseable(true);

				String statusLabel = LanguageUtil.get(
					httpServletRequest,
					WorkflowConstants.getStatusLabel(status));

				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "status") + ": " +
						statusLabel);
			}
		).add(
			_journalDisplayContext::isTypeVersions,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setParameter(
						"type", (String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "type") + ": " +
						LanguageUtil.get(httpServletRequest, "versions"));
			}
		);

		_addAssetCategoriesFilterLabelItems(labelItemListWrapper);

		_addAssetTagsFilterLabelItems(labelItemListWrapper);

		return labelItemListWrapper.build();
	}

	@Override
	public String getInfoPanelId() {
		return "infoPanelId";
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"folderId", _journalDisplayContext.getFolderId()
		).setParameter(
			"highlightedDDMStructureId",
			() -> {
				if (!_journalDisplayContext.isHighlightedDDMStructure()) {
					return null;
				}

				return _journalDisplayContext.getHighlightedDDMStructureId();
			}
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "articles";
	}

	@Override
	public String getSearchFormName() {
		return "fm1";
	}

	@Override
	public String getSortingOrder() {
		if (Objects.equals(getOrderByCol(), "relevance") ||
			_journalDisplayContext.isNavigationRecent()) {

			return null;
		}

		return super.getSortingOrder();
	}

	@Override
	public String getSortingURL() {
		if (_journalDisplayContext.isNavigationRecent()) {
			return null;
		}

		return super.getSortingURL();
	}

	@Override
	public Boolean isDisabled() {
		if ((getItemsTotal() > 0) || _journalDisplayContext.hasAssetFilter() ||
			_journalDisplayContext.isSearch() ||
			!_journalDisplayContext.isNavigationHome() ||
			(_journalDisplayContext.getStatus() !=
				WorkflowConstants.STATUS_ANY)) {

			return false;
		}

		return true;
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			return _isShowAddButton();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get creation menu", portalException);
			}

			return false;
		}
	}

	@Override
	public Boolean isShowInfoButton() {
		try {
			return _journalDisplayContext.isShowInfoButton();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "descriptive";
	}

	@Override
	protected String getDisplayStyle() {
		return _journalDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		List<DropdownItem> filterNavigationDropdownItems = new ArrayList<>();

		filterNavigationDropdownItems.add(
			DropdownItemBuilder.setActive(
				!_journalDisplayContext.hasAssetFilter() &&
				_journalDisplayContext.isNavigationHome()
			).setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setNavigation(
					"all"
				).setParameter(
					"assetCategoryId", (String)null
				).setParameter(
					"assetTagId", (String)null
				).setParameter(
					"ddmStructureId", (String)null
				).setParameter(
					"navigationMine", (Boolean)null
				).setParameter(
					"navigationRecent", (Boolean)null
				).buildPortletURL()
			).setLabel(
				LanguageUtil.get(httpServletRequest, "all")
			).build());

		if (!_journalDisplayContext.isNavigationRecent()) {
			filterNavigationDropdownItems.add(
				DropdownItemBuilder.setActive(
					_journalDisplayContext.isNavigationMine()
				).setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"mine"
					).setParameter(
						"navigationMine", Boolean.TRUE
					).setParameter(
						"orderByCol", "create-date"
					).setParameter(
						"orderByType", "desc"
					).buildPortletURL()
				).setLabel(
					LanguageUtil.get(httpServletRequest, "mine")
				).build());
		}

		filterNavigationDropdownItems.add(
			DropdownItemBuilder.setActive(
				_journalDisplayContext.isNavigationRecent()
			).setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setNavigation(
					"recent"
				).setParameter(
					"navigationMine", (Boolean)null
				).setParameter(
					"navigationRecent", Boolean.TRUE
				).buildPortletURL()
			).setLabel(
				LanguageUtil.get(httpServletRequest, "recent")
			).build());

		if (!_journalDisplayContext.isHighlightedDDMStructure()) {
			filterNavigationDropdownItems.add(
				DropdownItemBuilder.putData(
					"action", "openDDMStructuresSelector"
				).setActive(
					_journalDisplayContext.isNavigationStructure()
				).setLabel(
					LanguageUtil.get(httpServletRequest, "structures")
				).build());
		}

		filterNavigationDropdownItems.add(
			DropdownItemBuilder.putData(
				"action", "openCategoriesSelector"
			).putData(
				"redirectURL",
				PortletURLBuilder.create(
					getPortletURL()
				).setParameter(
					"assetCategoryId", (String)null
				).buildString()
			).setActive(
				ArrayUtil.isNotEmpty(_getAssetCategoryIds())
			).setLabel(
				LanguageUtil.get(httpServletRequest, "categories")
			).build());
		filterNavigationDropdownItems.add(
			DropdownItemBuilder.putData(
				"action", "openTagsSelector"
			).putData(
				"redirectURL",
				PortletURLBuilder.create(
					getPortletURL()
				).setParameter(
					"assetTagId", (String)null
				).buildString()
			).setActive(
				ArrayUtil.isNotEmpty(_getAssetTagIds())
			).setLabel(
				LanguageUtil.get(httpServletRequest, "tags")
			).build());

		return filterNavigationDropdownItems;
	}

	protected List<DropdownItem> getFilterStatusDropdownItems() {
		return new DropdownItemList() {
			{
				for (int status : _getStatuses()) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(
								_journalDisplayContext.getStatus() == status);
							dropdownItem.setHref(
								getPortletURL(), "status",
								String.valueOf(status));
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									WorkflowConstants.getStatusLabel(status)));
						});
				}
			}
		};
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "mine", "recent"};
	}

	@Override
	protected String[] getOrderByKeys() {
		if (_journalDisplayContext.isNavigationRecent()) {
			return null;
		}

		return _journalDisplayContext.getOrderColumns();
	}

	private void _addAssetCategoriesFilterLabelItems(
		LabelItemListBuilder.LabelItemListWrapper labelItemListWrapper) {

		Set<Long> assetCategoryIds = SetUtil.fromArray(_getAssetCategoryIds());

		for (Long assetCategoryId : assetCategoryIds) {
			labelItemListWrapper.add(
				labelItem -> {
					labelItem.putData(
						"removeLabelURL",
						PortletURLBuilder.create(
							PortletURLUtil.clone(
								currentURLObj, liferayPortletResponse)
						).setParameter(
							"assetCategoryId",
							() -> TransformUtil.transformToArray(
								assetCategoryIds,
								curAssetCategoryId -> {
									if (Objects.equals(
											assetCategoryId,
											curAssetCategoryId)) {

										return null;
									}

									return String.valueOf(curAssetCategoryId);
								},
								String.class)
						).buildString());

					labelItem.setCloseable(true);

					String title = StringPool.BLANK;

					AssetCategory assetCategory =
						AssetCategoryServiceUtil.fetchCategory(assetCategoryId);

					if (assetCategory != null) {
						title = assetCategory.getTitle(
							httpServletRequest.getLocale());
					}

					labelItem.setLabel(
						LanguageUtil.get(httpServletRequest, "categories") +
							": " + title);
				});
		}
	}

	private void _addAssetTagsFilterLabelItems(
		LabelItemListBuilder.LabelItemListWrapper labelItemListWrapper) {

		Set<String> assetTagIds = SetUtil.fromArray(_getAssetTagIds());

		for (String assetTagId : assetTagIds) {
			labelItemListWrapper.add(
				labelItem -> {
					labelItem.putData(
						"removeLabelURL",
						PortletURLBuilder.create(
							PortletURLUtil.clone(
								currentURLObj, liferayPortletResponse)
						).setParameter(
							"assetTagId",
							() -> TransformUtil.transformToArray(
								assetTagIds,
								curAssetTagId -> {
									if (Objects.equals(
											assetTagId, curAssetTagId)) {

										return null;
									}

									return curAssetTagId;
								},
								String.class)
						).buildString());

					labelItem.setCloseable(true);
					labelItem.setLabel(
						LanguageUtil.get(httpServletRequest, "tags") + ": " +
							assetTagId);
				});
		}
	}

	private long[] _getAssetCategoryIds() {
		if (_assetCategoryIds == null) {
			_assetCategoryIds = ParamUtil.getLongValues(
				httpServletRequest, "assetCategoryId");
		}

		return _assetCategoryIds;
	}

	private String _getAssetCategorySelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(liferayPortletRequest);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());
		itemSelectorCriterion.setMultiSelection(true);

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, _themeDisplay.getScopeGroup(),
				_themeDisplay.getScopeGroupId(),
				liferayPortletResponse.getNamespace() + "selectedAssetCategory",
				itemSelectorCriterion)
		).setParameter(
			"selectedCategoryIds",
			StringUtil.merge(_getAssetCategoryIds(), StringPool.COMMA)
		).setParameter(
			"vocabularyIds",
			() -> ListUtil.toString(
				_assetVocabularyLocalService.getGroupsVocabularies(
					_getGroupIds()),
				AssetVocabulary.VOCABULARY_ID_ACCESSOR)
		).buildString();
	}

	private String[] _getAssetTagIds() {
		if (_assetTagIds == null) {
			_assetTagIds = ParamUtil.getStringValues(
				httpServletRequest, "assetTagId");
		}

		return _assetTagIds;
	}

	private String _getAssetTagSelectorURL() {
		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
			new AssetTagsItemSelectorCriterion();

		assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetTagsItemSelectorReturnType());
		assetTagsItemSelectorCriterion.setGroupIds(_getGroupIds());
		assetTagsItemSelectorCriterion.setMultiSelection(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					liferayPortletRequest),
				liferayPortletResponse.getNamespace() + "selectTag",
				assetTagsItemSelectorCriterion));
	}

	private PortletURL _getControlPanelPortletURL(
		PortletRequest portletRequest) {

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			portletRequest, _themeDisplay.getScopeGroup(),
			JournalPortletKeys.JOURNAL, 0, 0, PortletRequest.RENDER_PHASE);

		try {
			portletURL.setWindowState(portletRequest.getWindowState());
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private CreationMenu _getCreationMenu() throws PortalException {
		return new CreationMenu() {
			{
				if (JournalFolderPermission.contains(
						_themeDisplay.getPermissionChecker(),
						_themeDisplay.getScopeGroupId(),
						_journalDisplayContext.getFolderId(),
						ActionKeys.ADD_FOLDER)) {

					addPrimaryDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								liferayPortletResponse.createRenderURL(),
								"mvcPath", "/edit_folder.jsp", "redirect",
								PortalUtil.getCurrentURL(httpServletRequest),
								"groupId",
								String.valueOf(_themeDisplay.getScopeGroupId()),
								"parentFolderId",
								String.valueOf(
									_journalDisplayContext.getFolderId()));
							dropdownItem.setIcon("folder");

							String label = "folder";

							if (_journalDisplayContext.getFolder() != null) {
								label = "subfolder";
							}

							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, label));
						});
				}

				if (JournalFolderPermission.contains(
						_themeDisplay.getPermissionChecker(),
						_themeDisplay.getScopeGroupId(),
						_journalDisplayContext.getFolderId(),
						ActionKeys.ADD_ARTICLE)) {

					List<DDMStructure> ddmStructures =
						_journalDisplayContext.getDDMStructures();

					Collections.sort(
						ddmStructures, _getDDMStructureOrderByComparator());

					PortletRequest portletRequest =
						(PortletRequest)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_REQUEST);

					PortletURL controlPanelPortletURL =
						_getControlPanelPortletURL(portletRequest);

					for (DDMStructure ddmStructure : ddmStructures) {
						PortletURL portletURL = PortletURLBuilder.create(
							controlPanelPortletURL
						).setMVCRenderCommandName(
							"/journal/edit_article"
						).setRedirect(
							() -> {
								if (_journalDisplayContext.isFilterApplied() ||
									_journalDisplayContext.isSearch()) {

									return PortletURLBuilder.createRenderURL(
										liferayPortletResponse
									).buildString();
								}

								return PortalUtil.getCurrentURL(
									httpServletRequest);
							}
						).setBackURL(
							_themeDisplay.getURLCurrent()
						).setParameter(
							"backURLTitle",
							() -> {
								PortletDisplay portletDisplay =
									_themeDisplay.getPortletDisplay();

								return portletDisplay.getPortletDisplayName();
							}
						).setParameter(
							"ddmStructureId", ddmStructure.getStructureId()
						).setParameter(
							"folderId", _journalDisplayContext.getFolderId()
						).setParameter(
							"groupId", _themeDisplay.getScopeGroupId()
						).setParameter(
							"showSelectFolder", false
						).buildPortletURL();

						UnsafeConsumer<DropdownItem, Exception> unsafeConsumer =
							dropdownItem -> {
								dropdownItem.setHref(portletURL);
								dropdownItem.setLabel(
									HtmlUtil.escape(
										ddmStructure.getUnambiguousName(
											ddmStructures,
											_themeDisplay.getScopeGroupId(),
											_themeDisplay.getLocale())));
							};

						if (ArrayUtil.contains(
								_journalDisplayContext.getAddMenuFavItems(),
								ddmStructure.getStructureId())) {

							addFavoriteDropdownItem(unsafeConsumer);
						}
						else {
							addRestDropdownItem(unsafeConsumer);
						}
					}
				}

				setHelpText(
					LanguageUtil.get(
						httpServletRequest,
						"you-can-customize-this-menu-or-see-all-you-have-by-" +
							"clicking-more"));
			}
		};
	}

	private String _getDDMStructureOrderByCol() {
		if (Validator.isNotNull(_ddmStructureOrderByCol)) {
			return _ddmStructureOrderByCol;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		String orderByCol = portalPreferences.getValue(
			JournalPortletKeys.JOURNAL, "view-more-items-order-by-col",
			"modified-date");

		if (Validator.isNull(orderByCol)) {
			orderByCol = ParamUtil.getString(
				httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM);
		}

		_ddmStructureOrderByCol = orderByCol;

		return _ddmStructureOrderByCol;
	}

	private OrderByComparator<DDMStructure>
		_getDDMStructureOrderByComparator() {

		OrderByComparator<DDMStructure> orderByComparator = null;

		boolean orderByAsc = false;

		if (Objects.equals(_getDDMStructureOrderByType(), "asc")) {
			orderByAsc = true;
		}

		String orderByCol = _getDDMStructureOrderByCol();

		if (orderByCol.equals("modified-date")) {
			orderByComparator = new StructureModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new StructureNameComparator(
				orderByAsc, _themeDisplay.getLocale());
		}

		return orderByComparator;
	}

	private String _getDDMStructureOrderByType() {
		if (Validator.isNotNull(_ddmStructureOrderByType)) {
			return _ddmStructureOrderByType;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		String orderByType = portalPreferences.getValue(
			JournalPortletKeys.JOURNAL, "view-more-items-order-by-type",
			"desc");

		if (Validator.isNull(orderByType)) {
			orderByType = ParamUtil.getString(
				httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM);
		}

		_ddmStructureOrderByType = orderByType;

		return _ddmStructureOrderByType;
	}

	private long[] _getGroupIds() {
		if (_groupIds != null) {
			return _groupIds;
		}

		try {
			_groupIds =
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_themeDisplay.getScopeGroupId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return _groupIds;
	}

	private List<Integer> _getStatuses() {
		List<Integer> statuses = new ArrayList<>();

		statuses.add(WorkflowConstants.STATUS_ANY);
		statuses.add(WorkflowConstants.STATUS_DRAFT);

		if (JournalUtil.hasWorkflowDefinitionsLinks(_themeDisplay)) {
			statuses.add(WorkflowConstants.STATUS_PENDING);
			statuses.add(WorkflowConstants.STATUS_DENIED);
		}

		statuses.add(WorkflowConstants.STATUS_APPROVED);
		statuses.add(WorkflowConstants.STATUS_EXPIRED);
		statuses.add(WorkflowConstants.STATUS_SCHEDULED);

		return statuses;
	}

	private boolean _isShowAddButton() throws PortalException {
		Group group = _themeDisplay.getScopeGroup();

		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if ((stagingGroupHelper.isLocalLiveGroup(group) ||
			 stagingGroupHelper.isRemoteLiveGroup(group)) &&
			stagingGroupHelper.isStagedPortlet(
				group, JournalPortletKeys.JOURNAL)) {

			return false;
		}

		if (JournalFolderPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				_journalDisplayContext.getFolderId(), ActionKeys.ADD_FOLDER) ||
			JournalFolderPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				_journalDisplayContext.getFolderId(), ActionKeys.ADD_ARTICLE)) {

			return true;
		}

		return false;
	}

	private boolean _isShowPublishAction() {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		long scopeGroupId = _themeDisplay.getScopeGroupId();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		try {
			if (GroupPermissionUtil.contains(
					permissionChecker, scopeGroupId,
					ActionKeys.EXPORT_IMPORT_PORTLET_INFO) &&
				stagingGroupHelper.isStagingGroup(scopeGroupId) &&
				stagingGroupHelper.isStagedPortlet(
					scopeGroupId, JournalPortletKeys.JOURNAL)) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"An exception occured when checking if the publish " +
						"action should be displayed",
					portalException);
			}

			return false;
		}
	}

	private boolean _isShowPublishArticlesAction() {
		return _isShowPublishAction();
	}

	private boolean _isTrashEnabled() {
		try {
			return _trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId());
		}
		catch (PortalException portalException) {

			// LPS-52675

			_log.error(portalException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalManagementToolbarDisplayContext.class);

	private long[] _assetCategoryIds;
	private String[] _assetTagIds;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private String _ddmStructureOrderByCol;
	private String _ddmStructureOrderByType;
	private long[] _groupIds;
	private final ItemSelector _itemSelector;
	private final JournalDisplayContext _journalDisplayContext;
	private final JournalWebConfiguration _journalWebConfiguration;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;
	private final ThemeDisplay _themeDisplay;
	private final TranslationURLProvider _translationURLProvider;
	private final TrashHelper _trashHelper;

}
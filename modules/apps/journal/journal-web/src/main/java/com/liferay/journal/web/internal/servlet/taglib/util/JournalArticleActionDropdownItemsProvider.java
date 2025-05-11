/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.servlet.taglib.util;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.web.internal.asset.model.JournalArticleAssetRenderer;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.item.selector.JournalArticleTranslationsItemSelectorCriterion;
import com.liferay.journal.web.internal.portlet.JournalPortlet;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.journal.web.internal.util.JournalPortletUtil;
import com.liferay.journal.web.internal.util.JournalUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.translation.constants.TranslationActionKeys;
import com.liferay.translation.security.permission.TranslationPermission;
import com.liferay.translation.url.provider.TranslationURLProvider;
import com.liferay.trash.TrashHelper;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleActionDropdownItemsProvider {

	public JournalArticleActionDropdownItemsProvider(
		JournalArticle article, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		TrashHelper trashHelper) {

		_article = article;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_trashHelper = trashHelper;

		_journalWebConfiguration =
			(JournalWebConfiguration)liferayPortletRequest.getAttribute(
				JournalWebConfiguration.class.getName());
		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
		_translationPermission =
			(TranslationPermission)liferayPortletRequest.getAttribute(
				TranslationPermission.class.getName());
		_translationURLProvider =
			(TranslationURLProvider)liferayPortletRequest.getAttribute(
				TranslationURLProvider.class.getName());
		_itemSelector = (ItemSelector)_httpServletRequest.getAttribute(
			ItemSelector.class.getName());
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		String[] availableLanguageIds = _article.getAvailableLanguageIds();
		boolean hasDeletePermission = JournalArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), _article, ActionKeys.DELETE);
		boolean hasTranslatePermission = _hasTranslatePermission();
		boolean hasUpdatePermission = JournalArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), _article, ActionKeys.UPDATE);
		boolean hasViewPermission = JournalArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), _article, ActionKeys.VIEW);
		boolean trashEnabled = _trashHelper.isTrashEnabled(
			_themeDisplay.getScopeGroupId());
		UnsafeConsumer<DropdownItem, Exception> previewContentArticleAction =
			_getPreviewArticleActionUnsafeConsumer();
		UnsafeConsumer<DropdownItem, Exception> viewContentArticleAction =
			_getViewContentArticleActionUnsafeConsumer();

		boolean singleLanguageSite = _isSingleLanguageSite();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getEditArticleActionUnsafeConsumer()
					).add(
						() ->
							FeatureFlagManagerUtil.isEnabled("LPD-11228") &&
							hasUpdatePermission && _article.isDraft() &&
							_article.hasApprovedVersion(),
						_getDiscardDraftActionUnsafeConsumer()
					).add(
						() ->
							hasViewPermission &&
							(previewContentArticleAction != null),
						previewContentArticleAction
					).add(
						() -> {
							Group group = _themeDisplay.getScopeGroup();

							if (_isShowPublishArticleAction() &&
								!group.isLayout()) {

								return true;
							}

							return false;
						},
						_getPublishToLiveArticleActionUnsafeConsumer()
					).add(
						() ->
							hasTranslatePermission && hasViewPermission &&
							!singleLanguageSite,
						_getTranslateActionUnsafeConsumer()
					).add(
						() ->
							hasViewPermission &&
							(viewContentArticleAction != null),
						viewContentArticleAction
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.EXPIRE) &&
							(_article.hasApprovedVersion() ||
							 _article.isScheduled()),
						_getExpireArticleActionConsumer(_article.getArticleId())
					).add(
						() ->
							hasViewPermission &&
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.SUBSCRIBE),
						_getSubscribeArticleActionUnsafeConsumer()
					).add(
						() -> hasViewPermission && hasUpdatePermission,
						_getViewHistoryArticleActionUnsafeConsumer()
					).add(
						_getViewUsagesArticleActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFolderPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_themeDisplay.getScopeGroupId(),
							_article.getFolderId(), ActionKeys.ADD_ARTICLE),
						_getCopyArticleActionUnsafeConsumer()
					).add(
						() ->
							hasTranslatePermission && hasViewPermission &&
							!singleLanguageSite,
						_getExportForTranslationActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission && !singleLanguageSite,
						_getImportTranslationActionUnsafeConsumer()
					).add(
						() ->
							hasUpdatePermission &&
							(availableLanguageIds.length > 1),
						_getDeleteArticleTranslationsActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getMoveArticleActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalArticlePermission.contains(
							_themeDisplay.getPermissionChecker(), _article,
							ActionKeys.PERMISSIONS),
						_getPermissionsArticleActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasDeletePermission && trashEnabled,
						_getMoveToTrashArticleActionUnsafeConsumer()
					).add(
						() -> hasDeletePermission && !trashEnabled,
						_getDeleteArticleAction(_article.getArticleId())
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getArticleHistoryActionDropdownItems()
		throws Exception {

		UnsafeConsumer<DropdownItem, Exception> previewContentArticleAction =
			_getPreviewArticleActionUnsafeConsumer();

		String articleId =
			_article.getArticleId() + JournalPortlet.VERSION_SEPARATOR +
				_article.getVersion();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.VIEW) &&
							(previewContentArticleAction != null),
						previewContentArticleAction
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getCompareArticleVersionsActionUnsafeConsumer()
					).add(
						() ->
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.EXPIRE) &&
							((_article.getStatus() ==
								WorkflowConstants.STATUS_APPROVED) ||
							 (_article.getStatus() ==
								 WorkflowConstants.STATUS_SCHEDULED)),
						_getExpireArticleActionConsumer(
							articleId, _themeDisplay.getURLCurrent())
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFolderPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_themeDisplay.getScopeGroupId(),
							_article.getFolderId(), ActionKeys.ADD_ARTICLE),
						_getAutoCopyArticleActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			() -> !JournalArticleLocalServiceUtil.isLatestVersion(
				_article.getGroupId(), _article.getArticleId(),
				_article.getVersion()),
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalArticlePermission.contains(
							_themeDisplay.getPermissionChecker(), _article,
							ActionKeys.DELETE),
						_getDeleteArticleAction(
							articleId, _themeDisplay.getURLCurrent())
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getArticleVersionActionDropdownItems()
		throws Exception {

		DropdownItemList dropdownItems = DropdownItemListBuilder.add(
			() -> JournalArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), _article,
				ActionKeys.UPDATE),
			_getEditArticleActionUnsafeConsumer()
		).build();

		dropdownItems.addAll(getArticleHistoryActionDropdownItems());

		return dropdownItems;
	}

	public List<DropdownItem> getArticleVersionTabActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				UnsafeConsumer<DropdownItem, Exception>
					previewContentArticleAction =
						_getPreviewArticleActionUnsafeConsumer();

				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.VIEW) &&
							(previewContentArticleAction != null),
						previewContentArticleAction
					).build());

				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				String articleId =
					_article.getArticleId() + JournalPortlet.VERSION_SEPARATOR +
						_article.getVersion();

				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							JournalArticlePermission.contains(
								_themeDisplay.getPermissionChecker(), _article,
								ActionKeys.EXPIRE) &&
							((_article.getStatus() ==
								WorkflowConstants.STATUS_APPROVED) ||
							 (_article.getStatus() ==
								 WorkflowConstants.STATUS_SCHEDULED)),
						_getExpireArticleActionConsumer(articleId)
					).build());

				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAutoCopyArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/copy_article.jsp", "redirect", _getRedirect(), "groupId",
				_article.getGroupId(), "oldArticleId", _article.getArticleId(),
				"version", _article.getVersion());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getCompareArticleVersionsActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "compareVersions");
			dropdownItem.putData(
				"compareVersionsURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/select_version.jsp"
				).setParameter(
					"articleId", _article.getArticleId()
				).setParameter(
					"groupId", _article.getGroupId()
				).setParameter(
					"sourceVersion", _article.getVersion()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.putData(
				"redirectURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/journal/compare_versions"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"articleId", _article.getArticleId()
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "compare-to"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCopyArticleActionUnsafeConsumer() {

		if (_journalWebConfiguration.journalArticleForceAutogenerateId()) {
			return dropdownItem -> {
				dropdownItem.putData("action", "copyArticle");
				dropdownItem.putData(
					"copyArticleURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setActionName(
						"/journal/copy_article"
					).setRedirect(
						_getRedirect()
					).setParameter(
						"autoArticleId", true
					).setParameter(
						"groupId", _article.getGroupId()
					).setParameter(
						"oldArticleId", _article.getArticleId()
					).setParameter(
						"version", _article.getVersion()
					).buildString());
				dropdownItem.setIcon("copy");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "make-a-copy"));
			};
		}

		return _getAutoCopyArticleActionUnsafeConsumer();
	}

	private UnsafeConsumer<DropdownItem, Exception> _getDeleteArticleAction(
		String articleId) {

		return _getDeleteArticleAction(articleId, _getRedirect());
	}

	private UnsafeConsumer<DropdownItem, Exception> _getDeleteArticleAction(
		String articleId, String redirect) {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/delete_article"
				).setRedirect(
					redirect
				).setParameter(
					"articleId", articleId
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getDeleteArticleTranslationsActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteArticleTranslations");
			dropdownItem.putData(
				"deleteArticleTranslationsURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/delete_article_translations"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"id", _article.getId()
				).buildString());

			JournalArticleTranslationsItemSelectorCriterion
				journalArticleTranslationsItemSelectorCriterion =
					new JournalArticleTranslationsItemSelectorCriterion();

			journalArticleTranslationsItemSelectorCriterion.setArticleId(
				_article.getArticleId());
			journalArticleTranslationsItemSelectorCriterion.setGroupId(
				_article.getGroupId());
			journalArticleTranslationsItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					new UUIDItemSelectorReturnType());

			RequestBackedPortletURLFactory requestBackedPortletURLFactory =
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

			dropdownItem.putData(
				"selectArticleTranslationsURL",
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory,
						_liferayPortletResponse.getNamespace() +
							"changePreview",
						journalArticleTranslationsItemSelectorCriterion)));

			dropdownItem.putData(
				"title",
				LanguageUtil.get(_httpServletRequest, "delete-translations"));

			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete-translations"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDiscardDraftActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "discardArticleDraft");
			dropdownItem.putData(
				"discardArticleDraftURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/discard_article_draft"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"articleId", _article.getArticleId()
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "discard-draft"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditArticleActionUnsafeConsumer() {

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return dropdownItem -> {
			dropdownItem.setHref(
				JournalPortletUtil.getEditArticlePortletURL(
					_article, _httpServletRequest, portletDisplay,
					_getRedirect(), _getReferringPortletResource()));
			dropdownItem.setIcon("pencil");

			String label = "edit";

			if (!JournalArticleLocalServiceUtil.isLatestVersion(
					_article.getGroupId(), _article.getArticleId(),
					_article.getVersion())) {

				label = "edit-latest-version";
			}

			dropdownItem.setLabel(LanguageUtil.get(_httpServletRequest, label));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExpireArticleActionConsumer(String articleId) {

		return _getExpireArticleActionConsumer(articleId, _getRedirect());
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExpireArticleActionConsumer(String articleId, String redirect) {

		return dropdownItem -> {
			dropdownItem.putData("action", "expireArticles");
			dropdownItem.putData(
				"expireURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/expire_articles"
				).setRedirect(
					redirect
				).setParameter(
					"articleId", articleId
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setIcon("time");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "expire"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportForTranslationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getExportTranslationURL(
						_article.getGroupId(),
						PortalUtil.getClassNameId(JournalArticle.class),
						_article.getResourcePrimKey(),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_getRedirect()
				).setPortletResource(
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getId();
					}
				).setParameter(
					"backURLTitle",
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getPortletDisplayName();
					}
				).build());
			dropdownItem.setIcon("upload");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "export-for-translation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getImportTranslationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getImportTranslationURL(
						_article.getGroupId(),
						PortalUtil.getClassNameId(JournalArticle.class),
						_article.getResourcePrimKey(),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_getRedirect()
				).setPortletResource(
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getId();
					}
				).setParameter(
					"backURLTitle",
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getPortletDisplayName();
					}
				).buildPortletURL());
			dropdownItem.setIcon("download");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "import-translation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/move_articles_and_folders.jsp", "redirect", _getRedirect(),
				"referringPortletResource", _getReferringPortletResource(),
				"rowIdsJournalArticle", _article.getArticleId());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "move"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveToTrashArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/move_to_trash"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"articleId", _article.getArticleId()
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsArticleActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, JournalArticle.class.getName(),
			HtmlUtil.escape(_article.getTitle(_themeDisplay.getLocale())), null,
			String.valueOf(_article.getResourcePrimKey()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData("permissionsURL", permissionsURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPreviewArticleActionUnsafeConsumer()
		throws Exception {

		String previewURL = _getPreviewURL();

		if (Validator.isNull(previewURL)) {
			return null;
		}

		return dropdownItem -> {
			dropdownItem.putData("action", "preview");
			dropdownItem.putData("previewURL", previewURL);
			dropdownItem.putData(
				"title", _article.getTitle(_themeDisplay.getLocale()));

			String status = "preview";

			if (_article.isDraft()) {
				status = "preview-draft";
			}

			dropdownItem.setIcon("view");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, status));
		};
	}

	private String _getPreviewURL() throws Exception {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(), _article.getResourcePrimKey());

		if (AssetDisplayPageUtil.hasAssetDisplayPage(
				_themeDisplay.getScopeGroupId(), assetEntry)) {

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				new ClassPKInfoItemIdentifier(assetEntry.getClassPK());

			classPKInfoItemIdentifier.setVersion(
				String.valueOf(_article.getVersion()));

			String previewURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						assetEntry.getClassName(), classPKInfoItemIdentifier),
					_themeDisplay);

			previewURL = HttpComponentsUtil.addParameter(
				previewURL, "p_l_mode", Constants.PREVIEW);

			return HttpComponentsUtil.addParameter(
				previewURL, "version", _article.getVersion());
		}

		if (Validator.isNull(_article.getDDMTemplateKey())) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createLiferayPortletURL(
			_liferayPortletResponse,
			JournalUtil.getPreviewPlid(_article, _themeDisplay),
			JournalPortletKeys.JOURNAL, PortletRequest.RENDER_PHASE
		).setParameters(
			HashMapBuilder.put(
				"articleId", new String[] {_article.getArticleId()}
			).put(
				"groupId", new String[] {String.valueOf(_article.getGroupId())}
			).put(
				"mvcPath", new String[] {"/preview_article_content.jsp"}
			).put(
				"version", new String[] {String.valueOf(_article.getVersion())}
			).build()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPublishToLiveArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "publishArticleToLive");
			dropdownItem.putData(
				"publishArticleURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/publish_articles"
				).setBackURL(
					_getRedirect()
				).setParameter(
					"articleId", _article.getArticleId()
				).setParameter(
					"groupId", _article.getGroupId()
				).buildString());
			dropdownItem.setIcon("live");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "publish-to-live"));
		};
	}

	private String _getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(
			_liferayPortletRequest, "redirect", _themeDisplay.getURLCurrent());

		return _redirect;
	}

	private String _getReferringPortletResource() {
		if (_referringPortletResource != null) {
			return _referringPortletResource;
		}

		_referringPortletResource = ParamUtil.getString(
			_liferayPortletRequest, "referringPortletResource");

		return _referringPortletResource;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getSubscribeArticleActionUnsafeConsumer() {

		if (JournalUtil.isSubscribedToArticle(
				_article.getCompanyId(), _themeDisplay.getScopeGroupId(),
				_themeDisplay.getUserId(), _article.getResourcePrimKey())) {

			return dropdownItem -> {
				dropdownItem.putData("action", "unsubscribeArticle");
				dropdownItem.putData(
					"unsubscribeArticleURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setActionName(
						"/journal/unsubscribe_article"
					).setRedirect(
						_getRedirect()
					).setParameter(
						"articleId", _article.getResourcePrimKey()
					).buildString());
				dropdownItem.setIcon("bell-off");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unsubscribe"));
			};
		}

		return dropdownItem -> {
			dropdownItem.putData("action", "subscribeArticle");
			dropdownItem.putData(
				"subscribeArticleURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/subscribe_article"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"articleId", _article.getResourcePrimKey()
				).buildString());
			dropdownItem.setIcon("bell-on");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "subscribe"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getTranslateActionUnsafeConsumer() {

		return dropdownItem -> {
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getTranslateURL(
						_themeDisplay.getScopeGroupId(),
						PortalUtil.getClassNameId(
							JournalArticle.class.getName()),
						_article.getResourcePrimKey(),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_getRedirect()
				).setPortletResource(
					portletDisplay.getId()
				).setParameter(
					"backURLTitle", portletDisplay.getPortletDisplayName()
				).setParameter(
					"modifiedDateTime",
					() -> {
						Date modifiedDate = _article.getModifiedDate();

						if (modifiedDate == null) {
							return null;
						}

						return modifiedDate.getTime();
					}
				).buildString());

			dropdownItem.setIcon("automatic-translate");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "translate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getViewContentArticleActionUnsafeConsumer()
		throws Exception {

		String viewContentURL = _getViewContentURL();

		if (Validator.isNull(viewContentURL)) {
			return null;
		}

		return dropdownItem -> {
			dropdownItem.setHref(viewContentURL);
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-content"));
		};
	}

	private String _getViewContentURL() throws Exception {
		if (!_isShowViewContentURL()) {
			return StringPool.BLANK;
		}

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		AssetRenderer<JournalArticle> assetRenderer =
			assetRendererFactory.getAssetRenderer(
				_article.getResourcePrimKey());

		String viewContentURL = StringPool.BLANK;

		try {
			viewContentURL = assetRenderer.getURLViewInContext(
				_liferayPortletRequest, _liferayPortletResponse,
				_getRedirect());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return viewContentURL;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewHistoryArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/view_article_history.jsp", "redirect", _getRedirect(),
				"backURL", _getRedirect(), "referringPortletResource",
				_getReferringPortletResource(), "articleId",
				_article.getArticleId());
			dropdownItem.setIcon("date-time");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-history"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewUsagesArticleActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/view_asset_entry_usages.jsp", "redirect", _getRedirect(),
				"groupId", _article.getGroupId(), "articleId",
				_article.getArticleId());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-usages"));
		};
	}

	private boolean _hasTranslatePermission() {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();
		long scopeGroupId = _themeDisplay.getScopeGroupId();

		for (Locale locale : LanguageUtil.getAvailableLocales(scopeGroupId)) {
			if (_translationPermission.contains(
					permissionChecker, scopeGroupId,
					LanguageUtil.getLanguageId(locale),
					TranslationActionKeys.TRANSLATE)) {

				return true;
			}
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

	private boolean _isShowPublishArticleAction() {
		if (_article == null) {
			return false;
		}

		StagedModelDataHandler<JournalArticle> stagedModelDataHandler =
			(StagedModelDataHandler<JournalArticle>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					JournalArticle.class.getName());

		if (_isShowPublishAction() &&
			ArrayUtil.contains(
				stagedModelDataHandler.getExportableStatuses(),
				_article.getStatus())) {

			return true;
		}

		return false;
	}

	private boolean _isShowViewContentURL() {
		if ((_article == null) || !_article.hasApprovedVersion()) {
			return false;
		}

		JournalArticle curArticle = _article;

		if (!_article.isApproved()) {
			curArticle =
				JournalArticleLocalServiceUtil.getPreviousApprovedArticle(
					_article);
		}

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			JournalArticle.class.getName(),
			JournalArticleAssetRenderer.getClassPK(curArticle));

		if (AssetDisplayPageUtil.hasAssetDisplayPage(
				_themeDisplay.getScopeGroupId(), assetEntry) ||
			Validator.isNotNull(_article.getLayoutUuid())) {

			return true;
		}

		return false;
	}

	private boolean _isSingleLanguageSite() {
		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(
			_themeDisplay.getSiteGroupId());

		if (availableLocales.size() == 1) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleActionDropdownItemsProvider.class);

	private final JournalArticle _article;
	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final JournalWebConfiguration _journalWebConfiguration;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _redirect;
	private String _referringPortletResource;
	private final ThemeDisplay _themeDisplay;
	private final TranslationPermission _translationPermission;
	private final TranslationURLProvider _translationURLProvider;
	private final TrashHelper _trashHelper;

}
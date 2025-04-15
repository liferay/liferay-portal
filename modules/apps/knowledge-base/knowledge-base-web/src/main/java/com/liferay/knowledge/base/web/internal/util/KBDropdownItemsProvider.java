/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.knowledge.base.web.internal.constants.KBWebKeys;
import com.liferay.knowledge.base.web.internal.security.permission.resource.AdminPermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.DisplayPermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBArticlePermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBCommentPermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBFolderPermission;
import com.liferay.knowledge.base.web.internal.security.permission.resource.KBTemplatePermission;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.util.RSSUtil;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class KBDropdownItemsProvider {

	public KBDropdownItemsProvider(
		KBGroupServiceConfiguration kbGroupServiceConfiguration,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		TrashHelper trashHelper) {

		_kbGroupServiceConfiguration = kbGroupServiceConfiguration;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_trashHelper = trashHelper;

		_currentURL = String.valueOf(
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse));
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public KBDropdownItemsProvider(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		this(null, liferayPortletRequest, liferayPortletResponse, null);
	}

	public KBDropdownItemsProvider(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		TrashHelper trashHelper) {

		this(null, liferayPortletRequest, liferayPortletResponse, trashHelper);
	}

	public List<DropdownItem> getKBArticleDropdownItems(KBArticle kbArticle) {
		return getKBArticleDropdownItems(kbArticle, null);
	}

	public List<DropdownItem> getKBArticleDropdownItems(
		KBArticle kbArticle, List<Long> selectedItemAncestorIds) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> _hasUpdatePermission(kbArticle),
					_getEditActionUnsafeConsumer(kbArticle)
				).add(
					this::_hasAddPermission,
					_getAddChildKBArticleActionUnsafeConsumer(kbArticle)
				).add(
					() ->
						_hasChildKBArticles(kbArticle) &&
						_hasViewChildKBArticlesPermission(),
					_getViewChildKBArticlesActionUnsafeConsumer(kbArticle)
				).build())
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasExpirationPermission(kbArticle),
						_getExpireArticleActionConsumer(kbArticle)
					).add(
						() ->
							_isSubscriptionEnabled() &&
							_hasSubscriptionPermission(kbArticle) &&
							_hasSubscription(kbArticle),
						_getUnsubscribeActionUnsafeConsumer(kbArticle)
					).add(
						() ->
							_isSubscriptionEnabled() &&
							_hasSubscriptionPermission(kbArticle) &&
							!_hasSubscription(kbArticle),
						_getSubscribeActionUnsafeConsumer(kbArticle)
					).add(
						() ->
							_isHistoryEnabled() &&
							_hasHistoryPermission(kbArticle),
						_getHistoryActionUnsafeConsumer(kbArticle)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasRSSPermission(kbArticle),
						_getRSSActionUnsafeConsumer(kbArticle)
					).add(
						() -> _isPrintEnabled() && _hasPrintPermission(),
						_getPrintActionUnsafeConsumer(kbArticle)
					).add(
						() -> _hasMovePermission(kbArticle),
						_getMoveActionUnsafeConsumer(kbArticle)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermissionsPermission(kbArticle),
						_getPermissionsActionUnsafeConsumer(kbArticle)
					).add(
						() -> _hasDeletePermission(kbArticle),
						_getDeleteActionUnsafeConsumer(
							kbArticle, selectedItemAncestorIds)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getKBCommentDropdownItems(
			KBArticle kbArticle, KBComment kbComment)
		throws Exception {

		if (!KBArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), kbArticle,
				KBActionKeys.UPDATE)) {

			return null;
		}

		int previousStatus = KBUtil.getPreviousStatus(kbComment.getStatus());
		int nextStatus = KBUtil.getNextStatus(kbComment.getStatus());

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> previousStatus != KBCommentConstants.STATUS_NONE,
					_getUpdateStatusActionUnsafeConsumer(
						kbComment, previousStatus)
				).add(
					() -> nextStatus != KBCommentConstants.STATUS_NONE,
					_getUpdateStatusActionUnsafeConsumer(kbComment, nextStatus)
				).add(
					() -> _hasDeletePermission(kbComment),
					_getDeleteActionUnsafeConsumer(kbComment)
				).build())
		).build();
	}

	public List<DropdownItem> getKBFolderDropdownItems(KBFolder kbFolder) {
		return getKBFolderDropdownItems(kbFolder, null);
	}

	public List<DropdownItem> getKBFolderDropdownItems(
		KBFolder kbFolder, List<Long> selectedItemAncestorIds) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> _hasUpdatePermission(kbFolder),
					_getEditActionUnsafeConsumer(kbFolder)
				).add(
					() -> _hasImportPermission(kbFolder),
					_getImportActionUnsafeConsumer(kbFolder)
				).add(
					() -> _hasMovePermission(kbFolder),
					_getMoveActionUnsafeConsumer(kbFolder)
				).add(
					() ->
						_hasSubscriptionPermission(kbFolder) &&
						!_hasSubscription(),
					_getGroupSubscribeActionUnsafeConsumer()
				).add(
					() ->
						_hasSubscriptionPermission(kbFolder) &&
						_hasSubscription(),
					_getGroupUnsubscribeActionUnsafeConsumer()
				).build())
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermissionsPermission(kbFolder),
						_getPermissionsActionUnsafeConsumer(kbFolder)
					).add(
						() -> _hasDeletePermission(kbFolder),
						_getDeleteActionUnsafeConsumer(
							kbFolder, selectedItemAncestorIds)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getKBTemplateDropdownItems(
		KBTemplate kbTemplate) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> _hasViewPermission(kbTemplate),
					dropdownItem -> {
						dropdownItem.setHref(
							PortletURLBuilder.createRenderURL(
								_liferayPortletResponse
							).setMVCPath(
								"/admin/view_kb_template.jsp"
							).setRedirect(
								_currentURL
							).setParameter(
								"kbTemplateId", kbTemplate.getKbTemplateId()
							).setParameter(
								"selectedItemId", kbTemplate.getPrimaryKey()
							).buildRenderURL());
						dropdownItem.setIcon("view");
						dropdownItem.setLabel(
							LanguageUtil.get(
								_liferayPortletRequest.getHttpServletRequest(),
								"view"));
					}
				).add(
					() -> _hasUpdatePermission(kbTemplate),
					_getEditActionUnsafeConsumer(kbTemplate)
				).build())
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermissionsPermission(kbTemplate),
						_getPermissionsActionUnsafeConsumer(kbTemplate)
					).add(
						() -> _hasDeletePermission(kbTemplate),
						_getDeleteActionUnsafeConsumer(kbTemplate)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getKBTemplateMoreActionsDropdownItems(
		KBTemplate kbTemplate) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> _hasUpdatePermission(kbTemplate),
					_getEditActionUnsafeConsumer(kbTemplate)
				).add(
					dropdownItem -> {
						dropdownItem.putData("action", "print");
						dropdownItem.putData(
							"printURL",
							PortletURLBuilder.createRenderURL(
								_liferayPortletResponse
							).setMVCPath(
								"/admin/print_kb_template.jsp"
							).setParameter(
								"kbTemplateId", kbTemplate.getKbTemplateId()
							).setParameter(
								"viewMode", Constants.PRINT
							).setWindowState(
								LiferayWindowState.POP_UP
							).buildString());
						dropdownItem.setIcon("print");
						dropdownItem.setLabel(
							LanguageUtil.get(
								_liferayPortletRequest.getHttpServletRequest(),
								"print"));
					}
				).build())
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermissionsPermission(kbTemplate),
						_getPermissionsActionUnsafeConsumer(kbTemplate)
					).add(
						() -> _hasDeletePermission(kbTemplate),
						_getDeleteActionUnsafeConsumer(kbTemplate)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private String _createKBArticleRenderURL(KBArticle kbArticle) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/admin/view_kb_article.jsp"
		).setParameter(
			"resourceClassNameId", kbArticle.getClassNameId()
		).setParameter(
			"resourcePrimKey", kbArticle.getResourcePrimKey()
		).setParameter(
			"selectedItemId", kbArticle.getResourcePrimKey()
		).buildString();
	}

	private String _createKBFolderRenderURL(long kbFolderId) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/admin/view_kb_folders.jsp"
		).setParameter(
			"parentResourceClassNameId",
			PortalUtil.getClassNameId(KBFolderConstants.getClassName())
		).setParameter(
			"parentResourcePrimKey", kbFolderId
		).setParameter(
			"selectedItemId", kbFolderId
		).buildString();
	}

	private String _createKbHomeRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/knowledge_base/view"
		).buildString();
	}

	private String _createKbTemplatesAdminHomeRenderURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_templates"
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddChildKBArticleActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_liferayPortletRequest,
						KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
						PortletRequest.RENDER_PHASE)
				).setMVCPath(
					"/admin/common/edit_kb_article.jsp"
				).setRedirect(
					_currentURL
				).setParameter(
					"parentResourceClassNameId", kbArticle.getClassNameId()
				).setParameter(
					"parentResourcePrimKey", kbArticle.getResourcePrimKey()
				).buildString());
			dropdownItem.setIcon("document-text");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"add-child-article"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer(
			KBArticle kbArticle, List<Long> selectedItemAncestorIds) {

		return dropdownItem -> {
			if (_trashHelper.isTrashEnabled(kbArticle.getGroupId())) {
				dropdownItem.setHref(
					_getDeleteActionURL(
						Constants.MOVE_TO_TRASH, kbArticle,
						selectedItemAncestorIds));
			}
			else {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL",
					_getDeleteActionURL(
						Constants.DELETE, kbArticle, selectedItemAncestorIds));
			}

			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer(KBComment kbComment) {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/delete_kb_comment"
				).setRedirect(
					_currentURL
				).setParameter(
					"kbCommentId", kbComment.getKbCommentId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer(
			KBFolder kbFolder, List<Long> selectedItemAncestorIds) {

		return dropdownItem -> {
			if (_trashHelper.isTrashEnabled(kbFolder.getGroupId())) {
				dropdownItem.setHref(
					_getDeleteActionURL(
						Constants.MOVE_TO_TRASH, kbFolder,
						selectedItemAncestorIds));
			}
			else {
				dropdownItem.putData("action", "delete");
				dropdownItem.putData(
					"deleteURL",
					_getDeleteActionURL(
						Constants.DELETE, kbFolder, selectedItemAncestorIds));
			}

			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer(KBTemplate kbTemplate) {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/delete_kb_template"
				).setRedirect(
					() -> {
						if (_isKBTemplateSelected(kbTemplate)) {
							return _createKbTemplatesAdminHomeRenderURL();
						}

						return _currentURL;
					}
				).setParameter(
					"kbTemplateId", kbTemplate.getKbTemplateId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "delete"));
		};
	}

	private String _getDeleteActionURL(
		String cmd, KBArticle kbArticle, List<Long> selectedItemAncestorIds) {

		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/knowledge_base/delete_kb_article"
		).setCMD(
			cmd
		).setRedirect(
			() -> {
				PortletDisplay portletDisplay =
					_themeDisplay.getPortletDisplay();

				if (!Objects.equals(
						portletDisplay.getRootPortletId(),
						KBPortletKeys.KNOWLEDGE_BASE_ADMIN) &&
					!Objects.equals(
						portletDisplay.getRootPortletId(),
						KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {

					return _createKbHomeRenderURL();
				}

				if (Objects.equals(
						portletDisplay.getRootPortletId(),
						KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {

					return PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setParameter(
						"resourcePrimKey",
						() -> {
							if (kbArticle.getParentResourcePrimKey() ==
									kbArticle.getKbFolderId()) {

								return null;
							}

							return kbArticle.getParentResourcePrimKey();
						}
					).buildString();
				}

				if (((selectedItemAncestorIds == null) &&
					 _isKBArticleSelected(kbArticle)) ||
					((selectedItemAncestorIds != null) &&
					 selectedItemAncestorIds.contains(
						 kbArticle.getResourcePrimKey()))) {

					return _getParentNodeURL(kbArticle);
				}

				return _currentURL;
			}
		).setParameter(
			"resourcePrimKey", kbArticle.getResourcePrimKey()
		).buildString();
	}

	private String _getDeleteActionURL(
		String cmd, KBFolder kbFolder, List<Long> selectedItemAncestorIds) {

		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/knowledge_base/delete_kb_folder"
		).setCMD(
			cmd
		).setRedirect(
			() -> {
				if (((selectedItemAncestorIds == null) &&
					 _isKBFolderSelected(kbFolder)) ||
					((selectedItemAncestorIds != null) &&
					 selectedItemAncestorIds.contains(
						 kbFolder.getKbFolderId()))) {

					return _createKBFolderRenderURL(
						kbFolder.getParentKBFolderId());
				}

				return _currentURL;
			}
		).setParameter(
			"kbFolderId", kbFolder.getKbFolderId()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_liferayPortletRequest,
						KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/knowledge_base/edit_kb_article"
				).setRedirect(
					_currentURL
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).buildString());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditActionUnsafeConsumer(KBFolder kbFolder) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/common/edit_kb_folder.jsp"
				).setRedirect(
					_currentURL
				).setParameter(
					"kbFolderId", kbFolder.getKbFolderId()
				).buildRenderURL());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditActionUnsafeConsumer(KBTemplate kbTemplate) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/common/edit_kb_template.jsp"
				).setRedirect(
					_currentURL
				).setParameter(
					"kbTemplateId", kbTemplate.getKbTemplateId()
				).buildRenderURL());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExpireArticleActionConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/expire_kb_article"
				).setRedirect(
					_currentURL
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).buildActionURL());
			dropdownItem.setIcon("time");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "expire"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getGroupSubscribeActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/subscribe_group_kb_articles"
				).setRedirect(
					_currentURL
				).buildActionURL());
			dropdownItem.setIcon("bell-on");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"subscribe"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getGroupUnsubscribeActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/unsubscribe_group_kb_articles"
				).setRedirect(
					_currentURL
				).buildActionURL());
			dropdownItem.setIcon("bell-off");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"unsubscribe"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getHistoryActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_liferayPortletRequest,
						KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
						PortletRequest.RENDER_PHASE)
				).setMVCPath(
					"/admin/common/kb_history.jsp"
				).setRedirect(
					_currentURL
				).setParameter(
					"resourceClassNameId", kbArticle.getClassNameId()
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).setParameter(
					"status",
					_liferayPortletRequest.getAttribute(
						KBWebKeys.KNOWLEDGE_BASE_STATUS)
				).buildString());
			dropdownItem.setIcon("date-time");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "history"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getImportActionUnsafeConsumer(KBFolder kbFolder) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/import.jsp"
				).setRedirect(
					PortalUtil.getCurrentURL(
						_liferayPortletRequest.getHttpServletRequest())
				).setParameter(
					"parentKBFolderId",
					() -> {
						if (kbFolder == null) {
							return KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;
						}

						return kbFolder.getKbFolderId();
					}
				).buildPortletURL());
			dropdownItem.setIcon("import");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "import"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.putData("action", "move");
			dropdownItem.putData(
				"kbObjectClassNameId",
				String.valueOf(kbArticle.getClassNameId()));
			dropdownItem.putData(
				"kbObjectId", String.valueOf(kbArticle.getResourcePrimKey()));
			dropdownItem.putData("kbObjectTitle", kbArticle.getTitle());
			dropdownItem.putData(
				"kbObjectType", KBArticle.class.getSimpleName());
			dropdownItem.putData(
				"moveKBObjectActionURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/move_kb_object"
				).buildString());
			dropdownItem.putData(
				"moveKBObjectModalURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/common/move_kb_object_modal.jsp"
				).setParameter(
					"kbObjectVersion", kbArticle.getVersion()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "move"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveActionUnsafeConsumer(KBFolder kbFolder) {

		return dropdownItem -> {
			dropdownItem.putData("action", "move");
			dropdownItem.putData(
				"kbObjectClassNameId",
				String.valueOf(kbFolder.getClassNameId()));
			dropdownItem.putData(
				"kbObjectId", String.valueOf(kbFolder.getKbFolderId()));
			dropdownItem.putData("kbObjectTitle", kbFolder.getName());
			dropdownItem.putData(
				"kbObjectType", KBFolder.class.getSimpleName());
			dropdownItem.putData(
				"moveKBObjectActionURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/move_kb_object"
				).buildString());
			dropdownItem.putData(
				"moveKBObjectModalURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/common/move_kb_object_modal.jsp"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "move"));
		};
	}

	private String _getParentNodeURL(KBArticle kbArticle)
		throws PortalException {

		long parentResourcePrimKey = kbArticle.getParentResourcePrimKey();

		if ((parentResourcePrimKey <= 0) ||
			(kbArticle.getParentResourceClassNameId() !=
				kbArticle.getClassNameId())) {

			return _createKBFolderRenderURL(kbArticle.getKbFolderId());
		}

		KBArticle parentKBArticle =
			KBArticleLocalServiceUtil.getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

		return _createKBArticleRenderURL(parentKBArticle);
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionsActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData(
				"permissionsURL",
				PermissionsURLTag.doTag(
					null, KBArticle.class.getName(), kbArticle.getTitle(),
					kbArticle.getGroupId(),
					String.valueOf(kbArticle.getResourcePrimKey()),
					LiferayWindowState.POP_UP.toString(), null,
					_liferayPortletRequest.getHttpServletRequest()));
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionsActionUnsafeConsumer(KBFolder kbFolder) {

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData(
				"permissionsURL", _getPermissionsURL(kbFolder));
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionsActionUnsafeConsumer(KBTemplate kbTemplate) {

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData(
				"permissionsURL",
				PermissionsURLTag.doTag(
					null, KBTemplate.class.getName(), kbTemplate.getTitle(),
					String.valueOf(kbTemplate.getGroupId()),
					String.valueOf(kbTemplate.getKbTemplateId()),
					LiferayWindowState.POP_UP.toString(), null,
					_liferayPortletRequest.getHttpServletRequest()));
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"permissions"));
		};
	}

	private String _getPermissionsURL(KBFolder kbFolder) throws Exception {
		if (kbFolder == null) {
			return PermissionsURLTag.doTag(
				null, KBConstants.RESOURCE_NAME_ADMIN,
				_themeDisplay.getScopeGroupName(), null,
				String.valueOf(_themeDisplay.getScopeGroupId()),
				LiferayWindowState.POP_UP.toString(), null,
				_liferayPortletRequest.getHttpServletRequest());
		}

		return PermissionsURLTag.doTag(
			null, KBFolder.class.getName(), kbFolder.getName(),
			String.valueOf(_themeDisplay.getScopeGroupId()),
			String.valueOf(kbFolder.getKbFolderId()),
			LiferayWindowState.POP_UP.toString(), null,
			_liferayPortletRequest.getHttpServletRequest());
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPrintActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.putData("action", "print");
			dropdownItem.putData(
				"printURL",
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/common/print_kb_article.jsp"
				).setParameter(
					"resourceClassNameId", kbArticle.getClassNameId()
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).setParameter(
					"viewMode", Constants.PRINT
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setIcon("print");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(), "print"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception> _getRSSActionUnsafeConsumer(
		KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				RSSUtil.getURL(
					ResourceURLBuilder.createResourceURL(
						_liferayPortletResponse
					).setParameter(
						"resourceClassNameId", kbArticle.getClassNameId()
					).setParameter(
						"resourcePrimKey", kbArticle.getResourcePrimKey()
					).setResourceID(
						"kbArticleRSS"
					).buildResourceURL(),
					GetterUtil.getInteger(
						_kbGroupServiceConfiguration.rssDelta()),
					_kbGroupServiceConfiguration.rssDisplayStyle(),
					_kbGroupServiceConfiguration.rssFeedType(), null));
			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel("RSS");
			dropdownItem.setTarget("_blank");
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getSubscribeActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/subscribe_kb_article"
				).setRedirect(
					_currentURL
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).buildActionURL());
			dropdownItem.setIcon("bell-on");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"subscribe"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUnsubscribeActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/unsubscribe_kb_article"
				).setRedirect(
					_currentURL
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).buildActionURL());
			dropdownItem.setIcon("bell-off");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"unsubscribe"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateStatusActionUnsafeConsumer(
			KBComment kbComment, int previousStatus) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/knowledge_base/update_kb_comment_status"
				).setRedirect(
					_currentURL
				).setParameter(
					"kbCommentId", kbComment.getKbCommentId()
				).setParameter(
					"kbCommentStatus", previousStatus
				).buildActionURL());
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					KBUtil.getStatusTransitionLabel(previousStatus)));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewChildKBArticlesActionUnsafeConsumer(KBArticle kbArticle) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/admin/view_kb_articles.jsp"
				).setRedirect(
					_currentURL
				).setParameter(
					"parentResourceClassNameId", kbArticle.getClassNameId()
				).setParameter(
					"parentResourcePrimKey", kbArticle.getResourcePrimKey()
				).setParameter(
					"resourceClassNameId", kbArticle.getClassNameId()
				).setParameter(
					"resourcePrimKey", kbArticle.getResourcePrimKey()
				).setParameter(
					"selectedItemId", kbArticle.getResourcePrimKey()
				).buildRenderURL());
			dropdownItem.setLabel(
				LanguageUtil.get(
					_liferayPortletRequest.getHttpServletRequest(),
					"view-child-articles"));
		};
	}

	private boolean _hasAddPermission() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.ADD_KB_ARTICLE) &&
			Objects.equals(
				portletDisplay.getRootPortletId(),
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {

			return true;
		}

		if (DisplayPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.ADD_KB_ARTICLE) &&
			DisplayPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.ADMINISTRATOR) &&
			Objects.equals(
				portletDisplay.getRootPortletId(),
				KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)) {

			return true;
		}

		return false;
	}

	private boolean _hasChildKBArticles(KBArticle kbArticle) {
		int kbArticlesCount = KBArticleServiceUtil.getKBArticlesCount(
			_themeDisplay.getScopeGroupId(), kbArticle.getResourcePrimKey(),
			WorkflowConstants.STATUS_ANY);

		if (kbArticlesCount > 0) {
			return true;
		}

		return false;
	}

	private boolean _hasDeletePermission(KBArticle kbArticle) throws Exception {
		return KBArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), kbArticle,
			KBActionKeys.DELETE);
	}

	private boolean _hasDeletePermission(KBComment kbComment) throws Exception {
		return KBCommentPermission.contains(
			_themeDisplay.getPermissionChecker(), kbComment,
			KBActionKeys.DELETE);
	}

	private boolean _hasDeletePermission(KBFolder kbFolder) throws Exception {
		if (kbFolder == null) {
			return false;
		}

		return KBFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), kbFolder,
			KBActionKeys.DELETE);
	}

	private boolean _hasDeletePermission(KBTemplate kbTemplate)
		throws Exception {

		return KBTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(), kbTemplate,
			KBActionKeys.DELETE);
	}

	private boolean _hasExpirationPermission(KBArticle kbArticle)
		throws Exception {

		if (kbArticle.isApproved() &&
			KBArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), kbArticle,
				KBActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	private Boolean _hasHistoryPermission(KBArticle kbArticle) {
		if (kbArticle.isApproved() || !kbArticle.isFirstVersion()) {
			return true;
		}

		return false;
	}

	private boolean _hasImportPermission(KBFolder kbFolder)
		throws PortalException {

		if ((kbFolder == null) &&
			AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.ADD_KB_ARTICLE)) {

			return true;
		}

		if ((kbFolder != null) &&
			KBFolderPermission.contains(
				_themeDisplay.getPermissionChecker(), kbFolder,
				KBActionKeys.ADD_KB_ARTICLE)) {

			return true;
		}

		return false;
	}

	private boolean _hasMovePermission(KBArticle kbArticle) throws Exception {
		return KBArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), kbArticle,
			KBActionKeys.MOVE_KB_ARTICLE);
	}

	private boolean _hasMovePermission(KBFolder kbFolder) throws Exception {
		if (kbFolder == null) {
			return false;
		}

		return KBFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), kbFolder,
			KBActionKeys.MOVE_KB_FOLDER);
	}

	private boolean _hasPermissionsPermission(KBArticle kbArticle)
		throws Exception {

		return KBArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), kbArticle,
			KBActionKeys.PERMISSIONS);
	}

	private boolean _hasPermissionsPermission(KBFolder kbFolder)
		throws Exception {

		if (kbFolder == null) {
			if (AdminPermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), ActionKeys.PERMISSIONS) &&
				GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), ActionKeys.PERMISSIONS)) {

				return true;
			}

			return false;
		}

		return KBFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), kbFolder,
			KBActionKeys.PERMISSIONS);
	}

	private boolean _hasPermissionsPermission(KBTemplate kbTemplate)
		throws Exception {

		return KBTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(), kbTemplate,
			KBActionKeys.PERMISSIONS);
	}

	private Boolean _hasPrintPermission() {
		return true;
	}

	private Boolean _hasRSSPermission(KBArticle kbArticle) {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if ((_kbGroupServiceConfiguration != null) &&
			_kbGroupServiceConfiguration.enableRss() &&
			(kbArticle.isApproved() || !kbArticle.isFirstVersion()) &&
			!Objects.equals(
				portletDisplay.getRootPortletId(),
				KBPortletKeys.KNOWLEDGE_BASE_ADMIN)) {

			return true;
		}

		return false;
	}

	private boolean _hasSubscription() {
		return SubscriptionLocalServiceUtil.isSubscribed(
			_themeDisplay.getCompanyId(), _themeDisplay.getUserId(),
			KBArticle.class.getName(), _themeDisplay.getScopeGroupId());
	}

	private boolean _hasSubscription(KBArticle kbArticle) {
		return SubscriptionLocalServiceUtil.isSubscribed(
			_themeDisplay.getCompanyId(), _themeDisplay.getUserId(),
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());
	}

	private boolean _hasSubscriptionPermission(KBArticle kbArticle)
		throws Exception {

		if ((kbArticle.isApproved() || !kbArticle.isFirstVersion()) &&
			KBArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), kbArticle,
				KBActionKeys.SUBSCRIBE)) {

			return true;
		}

		return false;
	}

	private boolean _hasSubscriptionPermission(KBFolder kbFolder) {
		if ((kbFolder == null) &&
			AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), KBActionKeys.SUBSCRIBE)) {

			return true;
		}

		return false;
	}

	private boolean _hasUpdatePermission(KBArticle kbArticle) throws Exception {
		return KBArticlePermission.contains(
			_themeDisplay.getPermissionChecker(), kbArticle,
			KBActionKeys.UPDATE);
	}

	private boolean _hasUpdatePermission(KBFolder kbFolder) throws Exception {
		if (kbFolder == null) {
			return false;
		}

		return KBFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), kbFolder,
			KBActionKeys.UPDATE);
	}

	private boolean _hasUpdatePermission(KBTemplate kbTemplate)
		throws Exception {

		return KBTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(), kbTemplate,
			KBActionKeys.UPDATE);
	}

	private boolean _hasViewChildKBArticlesPermission() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return Objects.equals(
			portletDisplay.getRootPortletId(),
			KBPortletKeys.KNOWLEDGE_BASE_ADMIN);
	}

	private boolean _hasViewPermission(KBTemplate kbTemplate) throws Exception {
		return KBTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(), kbTemplate,
			KBActionKeys.VIEW);
	}

	private Boolean _isHistoryEnabled() {
		return GetterUtil.getBoolean(
			_liferayPortletRequest.getAttribute(
				"init.jsp-enableKBArticleHistory"),
			true);
	}

	private boolean _isKBArticleSelected(KBArticle kbArticle) {
		long resourceClassNameId = ParamUtil.getLong(
			_liferayPortletRequest, "resourceClassNameId");

		if (resourceClassNameId != kbArticle.getClassNameId()) {
			return false;
		}

		long resourcePrimaryKey = ParamUtil.getLong(
			_liferayPortletRequest, "resourcePrimKey",
			KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY);

		if (resourcePrimaryKey == kbArticle.getResourcePrimKey()) {
			return true;
		}

		return false;
	}

	private boolean _isKBFolderSelected(KBFolder kbFolder) {
		long parentResourceClassNameId = ParamUtil.getLong(
			_liferayPortletRequest, "parentResourceClassNameId");

		if (parentResourceClassNameId != kbFolder.getClassNameId()) {
			return false;
		}

		long parentResourcePrimaryKey = ParamUtil.getLong(
			_liferayPortletRequest, "parentResourcePrimKey",
			KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		if (parentResourcePrimaryKey == kbFolder.getKbFolderId()) {
			return true;
		}

		return false;
	}

	private boolean _isKBTemplateSelected(KBTemplate kbTemplate) {
		long kbTemplateId = ParamUtil.getLong(
			_liferayPortletRequest, "kbTemplateId");

		if (kbTemplateId == kbTemplate.getKbTemplateId()) {
			return true;
		}

		return false;
	}

	private Boolean _isPrintEnabled() {
		return GetterUtil.getBoolean(
			_liferayPortletRequest.getAttribute(
				"init.jsp-enableKBArticlePrint"),
			true);
	}

	private Boolean _isSubscriptionEnabled() {
		return GetterUtil.getBoolean(
			_liferayPortletRequest.getAttribute(
				"init.jsp-enableKBArticleSubscriptions"),
			true);
	}

	private final String _currentURL;
	private final KBGroupServiceConfiguration _kbGroupServiceConfiguration;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}
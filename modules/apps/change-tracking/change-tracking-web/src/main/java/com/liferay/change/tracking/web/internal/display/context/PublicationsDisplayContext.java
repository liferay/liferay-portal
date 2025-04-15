/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTPermission;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Samuel Trong Tran
 */
public class PublicationsDisplayContext {

	public PublicationsDisplayContext(
		CTCollectionLocalService ctCollectionLocalService,
		CTDisplayRendererRegistry ctDisplayRendererRegistry,
		CTPreferencesLocalService ctPreferencesLocalService,
		CTRemoteLocalService ctRemoteLocalService,
		HttpServletRequest httpServletRequest, Language language,
		PublicationHelper publicationHelper, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_ctCollectionLocalService = ctCollectionLocalService;
		_ctDisplayRendererRegistry = ctDisplayRendererRegistry;
		_ctRemoteLocalService = ctRemoteLocalService;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_publicationHelper = publicationHelper;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CTPreferences ctPreferences =
			ctPreferencesLocalService.fetchCTPreferences(
				_themeDisplay.getCompanyId(), _themeDisplay.getUserId());

		if (ctPreferences == null) {
			_ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}
		else {
			_ctCollectionId = ctPreferences.getCtCollectionId();
		}
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/change-tracking-rest/v1.0/ct-collections?status=",
			WorkflowConstants.STATUS_DRAFT, "&status=",
			WorkflowConstants.STATUS_EXPIRED);
	}

	public Map<String, Object> getCollaboratorsReactData(
		long ctCollectionId, boolean publicationTemplate) {

		return HashMapBuilder.<String, Object>put(
			"autocompleteUserURL",
			() -> {
				ResourceURL autocompleteUserURL =
					_renderResponse.createResourceURL();

				autocompleteUserURL.setResourceID(
					"/change_tracking/autocomplete_user");
				autocompleteUserURL.setParameter(
					"ctCollectionId",
					String.valueOf(
						publicationTemplate ?
							CTConstants.CT_COLLECTION_ID_PRODUCTION :
								ctCollectionId));

				return autocompleteUserURL.toString();
			}
		).put(
			"getCollaboratorsURL",
			() -> {
				ResourceURL getCollaboratorsURL =
					_renderResponse.createResourceURL();

				getCollaboratorsURL.setResourceID(
					"/change_tracking/get_collaborators");
				getCollaboratorsURL.setParameter(
					"ctCollectionId",
					String.valueOf(
						publicationTemplate ?
							CTConstants.CT_COLLECTION_ID_PRODUCTION :
								ctCollectionId));

				return getCollaboratorsURL.toString();
			}
		).put(
			"getSharePublicationLinkURL",
			() -> {
				if (publicationTemplate) {
					return null;
				}

				ResourceURL getSharePublicationLinkURL =
					_renderResponse.createResourceURL();

				getSharePublicationLinkURL.setResourceID(
					"/change_tracking/get_share_publication_link");
				getSharePublicationLinkURL.setParameter(
					"ctCollectionId", String.valueOf(ctCollectionId));

				return getSharePublicationLinkURL.toString();
			}
		).put(
			"inviteUsersURL",
			() -> {
				ResourceURL inviteUsersURL =
					_renderResponse.createResourceURL();

				inviteUsersURL.setResourceID("/change_tracking/invite_users");
				inviteUsersURL.setParameter(
					"ctCollectionId",
					String.valueOf(
						publicationTemplate ?
							CTConstants.CT_COLLECTION_ID_PRODUCTION :
								ctCollectionId));

				return inviteUsersURL.toString();
			}
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"readOnly",
			() -> {
				if ((ctCollectionId ==
						CTConstants.CT_COLLECTION_ID_PRODUCTION) ||
					publicationTemplate) {

					return false;
				}

				CTCollection ctCollection =
					_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

				if ((ctCollection == null) ||
					(ctCollection.getStatus() ==
						WorkflowConstants.STATUS_APPROVED) ||
					(ctCollection.getStatus() ==
						WorkflowConstants.STATUS_EXPIRED)) {

					return true;
				}

				return !CTCollectionPermission.contains(
					_themeDisplay.getPermissionChecker(), ctCollectionId,
					CTActionKeys.INVITE_USERS);
			}
		).put(
			"roles",
			JSONUtil.putAll(
				JSONUtil.put(
					"default", true
				).put(
					"label",
					_language.get(
						_httpServletRequest,
						PublicationRoleConstants.LABEL_VIEWER)
				).put(
					"longDescription",
					StringBundler.concat(
						_language.get(_httpServletRequest, "viewers-can-view"),
						StringPool.SPACE,
						_language.get(
							_httpServletRequest,
							"viewers-cannot-edit,-publish,-or-invite-other-" +
								"users"))
				).put(
					"shortDescription",
					_language.get(_httpServletRequest, "viewers-can-view")
				).put(
					"value", PublicationRoleConstants.ROLE_VIEWER
				),
				JSONUtil.put(
					"label",
					_language.get(
						_httpServletRequest,
						PublicationRoleConstants.LABEL_EDITOR)
				).put(
					"longDescription",
					StringBundler.concat(
						_language.get(
							_httpServletRequest, "editors-can-view-and-edit"),
						StringPool.SPACE,
						_language.get(
							_httpServletRequest,
							"editors-cannot-publish-or-invite-other-users"))
				).put(
					"shortDescription",
					_language.get(
						_httpServletRequest, "editors-can-view-and-edit")
				).put(
					"value", PublicationRoleConstants.ROLE_EDITOR
				),
				JSONUtil.put(
					"label",
					_language.get(
						_httpServletRequest,
						PublicationRoleConstants.LABEL_PUBLISHER)
				).put(
					"longDescription",
					StringBundler.concat(
						_language.get(
							_httpServletRequest,
							"publishers-can-view,-edit,-and-publish"),
						StringPool.SPACE,
						_language.get(
							_httpServletRequest,
							"publishers-cannot-invite-other-users"))
				).put(
					"shortDescription",
					_language.get(
						_httpServletRequest,
						"publishers-can-view,-edit,-and-publish")
				).put(
					"value", PublicationRoleConstants.ROLE_PUBLISHER
				),
				JSONUtil.put(
					"label",
					_language.get(
						_httpServletRequest,
						PublicationRoleConstants.LABEL_ADMIN)
				).put(
					"longDescription",
					_language.get(
						_httpServletRequest,
						"administrators-can-view,-edit,-publish,-and-invite-" +
							"other-users")
				).put(
					"shortDescription",
					_language.get(
						_httpServletRequest,
						"administrators-can-view,-edit,-publish,-and-invite-" +
							"other-users")
				).put(
					"value", PublicationRoleConstants.ROLE_ADMIN
				))
		).put(
			"sharePublicationLink",
			() -> _publicationHelper.getShareURL(ctCollectionId, _renderRequest)
		).put(
			"showShareLinkTab", true
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"verifyEmailAddressURL",
			() -> {
				ResourceURL sharingVerifyEmailAddressURL =
					_renderResponse.createResourceURL();

				sharingVerifyEmailAddressURL.setResourceID(
					"/change_tracking/verify_email_address");
				sharingVerifyEmailAddressURL.setParameter(
					"ctCollectionId",
					String.valueOf(
						publicationTemplate ?
							CTConstants.CT_COLLECTION_ID_PRODUCTION :
								ctCollectionId));

				return sharingVerifyEmailAddressURL.toString();
			}
		).build();
	}

	public CreationMenu getCreationMenu() {
		if (!CTPermission.contains(
				_themeDisplay.getPermissionChecker(),
				CTActionKeys.ADD_PUBLICATION)) {

			return null;
		}

		CreationMenuBuilder.CreationMenuWrapper creationMenuWrapper =
			CreationMenuBuilder.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						_renderResponse.createRenderURL(),
						"mvcRenderCommandName",
						"/change_tracking/add_ct_collection", "redirect",
						_themeDisplay.getURLCurrent());
					dropdownItem.setLabel(
						_language.get(
							_httpServletRequest, "create-new-publication"));
				});

		if (FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPS-186360")) {

			List<CTRemote> ctRemotes = _ctRemoteLocalService.getCTRemotes(
				_themeDisplay.getCompanyId());

			for (CTRemote ctRemote : ctRemotes) {
				creationMenuWrapper.addDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(
							_renderResponse.createRenderURL(),
							"mvcRenderCommandName",
							"/change_tracking/add_ct_collection", "ctRemoteId",
							ctRemote.getCtRemoteId(), "redirect",
							_themeDisplay.getURLCurrent());
						dropdownItem.setLabel(
							_language.format(
								_httpServletRequest,
								"create-new-publication-on-x",
								ctRemote.getName()));
					});
			}
		}

		return creationMenuWrapper.build();
	}

	public long getCtCollectionId() {
		return _ctCollectionId;
	}

	public CTDisplayRendererRegistry getCtDisplayRendererRegistry() {
		return _ctDisplayRendererRegistry;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/checkout_ct_collection"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"radio-button", "checkout",
				_language.get(_httpServletRequest, "work-on-publication"),
				"post", "checkout", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/edit_ct_collection"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"pencil", "edit", _language.get(_httpServletRequest, "edit"),
				"get", "update", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_changes"
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"list-ul", "review-changes",
				_language.get(_httpServletRequest, "review-changes"), "get",
				"get", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/manage_collaborators"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"users", "invite-users",
				_language.get(_httpServletRequest, "invite-users"), "get",
				"permissions", "modal"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						"com_liferay_portlet_configuration_web_portlet_" +
							"PortletConfigurationPortlet",
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_permissions.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"modelResource", CTCollection.class.getName()
				).setParameter(
					"modelResourceDescription", "{name}"
				).setParameter(
					"resourcePrimKey", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"password-policies", "permissions",
				_language.get(_httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/reactivate_ct_collection"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"reset", "reactivate",
				_language.get(_httpServletRequest, "reactivate"), "post",
				"reactivate", null),
			new FDSActionDropdownItem(
				null, "times-circle", "delete",
				_language.get(_httpServletRequest, "delete"), null, "delete",
				null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_conflicts"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).setParameter(
					"schedule", Boolean.TRUE
				).buildString(),
				"calendar", "schedule",
				_language.get(_httpServletRequest, "schedule"), "get",
				"schedule", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_conflicts"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"change", "publish",
				_language.get(_httpServletRequest, "publish"), "get", "publish",
				null));
	}

	public String getStatusLabel(int status) {
		if (status == WorkflowConstants.STATUS_APPROVED) {
			return "published";
		}
		else if (status == WorkflowConstants.STATUS_EXPIRED) {
			return "out-of-date";
		}
		else if (status == WorkflowConstants.STATUS_DRAFT) {
			return "in-progress";
		}
		else if (status == WorkflowConstants.STATUS_DENIED) {
			return "failed";
		}
		else if (status == WorkflowConstants.STATUS_SCHEDULED) {
			return "scheduled";
		}

		return StringPool.BLANK;
	}

	public String getStatusStyle(int status) {
		if (status == WorkflowConstants.STATUS_EXPIRED) {
			return "warning";
		}

		return WorkflowConstants.getStatusStyle(status);
	}

	public List<NavigationItem> getViewNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setHref(_renderResponse.createRenderURL());
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "ongoing"));
			}
		).add(
			() -> PropsValues.SCHEDULER_ENABLED,
			navigationItem -> {
				navigationItem.setActive(false);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_scheduled");
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "scheduled"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(false);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_history");
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "history"));
			}
		).build();
	}

	private final long _ctCollectionId;
	private final CTCollectionLocalService _ctCollectionLocalService;
	private final CTDisplayRendererRegistry _ctDisplayRendererRegistry;
	private final CTRemoteLocalService _ctRemoteLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final PublicationHelper _publicationHelper;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}
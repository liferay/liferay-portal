/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.exception.NoSuchEntryLinkException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.content.page.editor.web.internal.exception.FormContainerParentItemRequiredException;
import com.liferay.layout.content.page.editor.web.internal.exception.NoninstanceablePortletException;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseDuplicateItemMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	protected long duplicateFragmentEntryLink(
			ActionRequest actionRequest, long fragmentEntryLinkId)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				CheckNoninstanceablePortletThreadLocal.
					setCheckNoninstanceablePortletWithSafeCloseable(true)) {

			FragmentEntryLink fragmentEntryLink =
				fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkId);

			JSONObject editableValuesJSONObject = jsonFactory.createJSONObject(
				fragmentEntryLink.getEditableValues());

			String portletId = editableValuesJSONObject.getString("portletId");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			String namespace = StringUtil.randomId();

			if (Validator.isNotNull(portletId)) {
				Portlet portlet = portletLocalService.getPortletById(portletId);

				if (!portlet.isInstanceable()) {
					editableValuesJSONObject.put(
						"instanceId", StringPool.BLANK);
				}
				else {
					String oldInstanceId = editableValuesJSONObject.getString(
						"instanceId");

					editableValuesJSONObject.put("instanceId", namespace);

					_copyPortletPermissions(
						fragmentEntryLink.getCompanyId(),
						fragmentEntryLink.getGroupId(), namespace,
						oldInstanceId, fragmentEntryLink.getPlid(), portletId);
					_copyPortletPreferences(
						serviceContext.getRequest(), portletId, oldInstanceId,
						namespace);
				}
			}

			if (fragmentEntryLink.isTypeInput()) {
				JSONObject freeMarkerFragmentEntryProcessorJSONObject =
					editableValuesJSONObject.getJSONObject(
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

				if (freeMarkerFragmentEntryProcessorJSONObject != null) {
					freeMarkerFragmentEntryProcessorJSONObject.remove(
						"inputFieldId");
				}
			}

			FragmentEntryLink duplicatedFragmentEntryLink =
				fragmentEntryLinkService.addFragmentEntryLink(
					null, fragmentEntryLink.getGroupId(), 0,
					fragmentEntryLink.getFragmentEntryId(),
					fragmentEntryLink.getSegmentsExperienceId(),
					fragmentEntryLink.getPlid(), fragmentEntryLink.getCss(),
					fragmentEntryLink.getHtml(), fragmentEntryLink.getJs(),
					fragmentEntryLink.getConfiguration(),
					editableValuesJSONObject.toString(), namespace, 0,
					fragmentEntryLink.getRendererKey(),
					fragmentEntryLink.getType(), serviceContext);

			return duplicatedFragmentEntryLink.getFragmentEntryLinkId();
		}
	}

	protected JSONArray getFragmentEntryLinksJSONArray(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Set<Long> duplicatedFragmentEntryLinkIds, long segmentsExperienceId,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONArray jsonArray = jsonFactory.createJSONArray();

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		for (long fragmentEntryLinkId : duplicatedFragmentEntryLinkIds) {
			jsonArray.put(
				fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLinkLocalService.getFragmentEntryLink(
						fragmentEntryLinkId),
					portal.getHttpServletRequest(actionRequest),
					portal.getHttpServletResponse(actionResponse),
					layoutStructure));
		}

		return jsonArray;
	}

	protected abstract String getNoninstanceablePortletExceptionMessage();

	protected abstract String getNoSuchEntryLinkExceptionMessage();

	@Override
	protected JSONObject processException(
		ActionRequest actionRequest, Exception exception) {

		if (exception instanceof LockedLayoutException) {
			return processLockedLayoutException(actionRequest);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String errorMessage = StringPool.BLANK;

		if (exception instanceof FormContainerParentItemRequiredException) {
			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(),
				"this-form-component-can-only-be-placed-inside-a-mapped-form-" +
					"container");
		}
		else if (exception instanceof NoninstanceablePortletException) {
			errorMessage = _getNoninstanceablePortletErrorMessage(
				actionRequest, (NoninstanceablePortletException)exception,
				themeDisplay);
		}
		else if (exception.getCause() instanceof
					NoninstanceablePortletException) {

			errorMessage = _getNoninstanceablePortletErrorMessage(
				actionRequest,
				(NoninstanceablePortletException)exception.getCause(),
				themeDisplay);
		}
		else if (exception instanceof NoSuchEntryLinkException) {
			errorMessage = language.get(
				themeDisplay.getRequest(),
				getNoSuchEntryLinkExceptionMessage());
		}
		else {
			errorMessage = language.get(
				themeDisplay.getRequest(), "an-unexpected-error-occurred");
		}

		return JSONUtil.put("error", errorMessage);
	}

	@Reference
	protected FragmentEntryLinkLocalService fragmentEntryLinkLocalService;

	@Reference
	protected FragmentEntryLinkManager fragmentEntryLinkManager;

	@Reference
	protected FragmentEntryLinkService fragmentEntryLinkService;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletLocalService portletLocalService;

	@Reference
	protected PortletPreferencesFactory portletPreferencesFactory;

	@Reference
	protected PortletPreferencesLocalService portletPreferencesLocalService;

	@Reference
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Reference
	protected RoleLocalService roleLocalService;

	private void _copyPortletPermissions(
			long companyId, long groupId, String newInstanceId,
			String oldInstanceId, long plid, String portletId)
		throws PortalException {

		String sourceResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			plid, PortletIdCodec.encode(portletId, oldInstanceId));
		String targetResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			plid, PortletIdCodec.encode(portletId, newInstanceId));
		List<String> actionIds = ResourceActionsUtil.getPortletResourceActions(
			portletId);

		List<Role> roles = roleLocalService.getGroupRelatedRoles(groupId);

		for (Role role : roles) {
			List<String> actions =
				resourcePermissionLocalService.
					getAvailableResourcePermissionActionIds(
						companyId, portletId,
						ResourceConstants.SCOPE_INDIVIDUAL,
						sourceResourcePrimKey, role.getRoleId(), actionIds);

			resourcePermissionLocalService.setResourcePermissions(
				companyId, portletId, ResourceConstants.SCOPE_INDIVIDUAL,
				targetResourcePrimKey, role.getRoleId(),
				actions.toArray(new String[0]));
		}
	}

	private void _copyPortletPreferences(
			HttpServletRequest httpServletRequest, String portletId,
			String oldInstanceId, String newInstanceId)
		throws PortalException {

		PortletPreferences portletPreferences =
			portletPreferencesFactory.getPortletPreferences(
				httpServletRequest,
				PortletIdCodec.encode(portletId, oldInstanceId));

		PortletPreferencesIds portletPreferencesIds =
			portletPreferencesFactory.getPortletPreferencesIds(
				httpServletRequest,
				PortletIdCodec.encode(portletId, oldInstanceId));

		portletPreferencesLocalService.addPortletPreferences(
			portletPreferencesIds.getCompanyId(),
			portletPreferencesIds.getOwnerId(),
			portletPreferencesIds.getOwnerType(),
			portletPreferencesIds.getPlid(),
			PortletIdCodec.encode(portletId, newInstanceId), null,
			PortletPreferencesFactoryUtil.toXML(portletPreferences));
	}

	private String _getNoninstanceablePortletErrorMessage(
		ActionRequest actionRequest,
		NoninstanceablePortletException noninstanceablePortletException,
		ThemeDisplay themeDisplay) {

		Portlet portlet = portletLocalService.getPortletById(
			themeDisplay.getCompanyId(),
			noninstanceablePortletException.getPortletId());

		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
			actionRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		return language.format(
			themeDisplay.getRequest(),
			getNoninstanceablePortletExceptionMessage(),
			new String[] {
				portal.getPortletTitle(
					portlet, httpSession.getServletContext(),
					themeDisplay.getLocale())
			});
	}

}
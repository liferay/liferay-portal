/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.display.context.ObjectEntryDisplayContextFactory;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Feliphe Marinho
 */
public class ObjectEntryAssetRenderer
	extends BaseJSPAssetRenderer<ObjectEntry> implements TrashRenderer {

	public ObjectEntryAssetRenderer(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		DLAppLocalService dlAppLocalService, DLURLHelper dlURLHelper,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		ObjectEntryDisplayContextFactory objectEntryDisplayContextFactory,
		ObjectEntryService objectEntryService,
		ObjectFieldLocalService objectFieldLocalService) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_dlAppLocalService = dlAppLocalService;
		_dlURLHelper = dlURLHelper;
		_objectDefinition = objectDefinition;
		_objectEntry = objectEntry;
		_objectEntryDisplayContextFactory = objectEntryDisplayContextFactory;
		_objectEntryService = objectEntryService;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	public ObjectEntry getAssetObject() {
		return _objectEntry;
	}

	@Override
	public String getClassName() {
		return _objectEntry.getModelClassName();
	}

	@Override
	public long getClassPK() {
		return _objectEntry.getObjectEntryId();
	}

	@Override
	public long getGroupId() {
		return _objectEntry.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/object_entries/edit_object_entry.jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		return _objectDefinition.getPortletId();
	}

	@Override
	public String getSharingEntryRowPortletURL(
			boolean editable, ThemeDisplay themeDisplay)
		throws Exception {

		if (_objectDefinition.isCMS()) {
			return getURLSharingNotification(editable, themeDisplay);
		}

		return null;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		try {
			return _objectEntry.getTitleValue(
				LocaleUtil.toLanguageId(locale), true);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getType() {
		return _objectDefinition.getName();
	}

	@Override
	public String getURLDownload(ThemeDisplay themeDisplay) {
		if (!_objectDefinition.isCMS()) {
			return null;
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "file");

		if ((objectField == null) ||
			!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			return null;
		}

		try {
			return ObjectFieldUtil.getAttachmentDownloadURL(
				_dlURLHelper,
				_dlAppLocalService.getFileEntry(
					MapUtil.getLong(
						_objectEntry.getValues(), objectField.getName())),
				_objectEntry.getGroupId(),
				_objectDefinition.getExternalReferenceCode(), _objectEntry,
				_objectEntryService, objectField,
				_getPermissionChecker(themeDisplay), themeDisplay);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public PortletURL getURLEdit(HttpServletRequest httpServletRequest) {
		Group group = GroupLocalServiceUtil.fetchGroup(
			_objectEntry.getGroupId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((group != null) && group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		if (_objectDefinition.isCMS()) {
			return PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest, group,
					ObjectPortletKeys.CMS_OBJECT_ENTRY, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setParameter(
				"objectEntryId", _objectEntry.getObjectEntryId()
			).buildPortletURL();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, _objectDefinition.getPortletId(), 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/object_entries/edit_object_entry"
		).setParameter(
			"externalReferenceCode", _objectEntry.getExternalReferenceCode()
		).setParameter(
			"groupId", _objectEntry.getGroupId()
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return getURLEdit(
			PortalUtil.getHttpServletRequest(liferayPortletRequest));
	}

	@Override
	public String getURLSharingNotification(
			boolean editable, ThemeDisplay themeDisplay)
		throws Exception {

		if (themeDisplay == null) {
			return null;
		}

		if (!_objectDefinition.isCMS()) {
			return getURLViewInContext(themeDisplay, StringPool.BLANK);
		}

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		String redirect = PortalUtil.escapeRedirect(
			httpServletRequest.getHeader(HttpHeaders.REFERER));

		if (Validator.isNull(redirect)) {
			redirect = themeDisplay.getURLHome();
		}

		if (editable) {
			return StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?objectEntryId=",
				_objectEntry.getObjectEntryId(), "&p_l_mode=", Constants.EDIT,
				"&redirect=", HtmlUtil.escapeURL(redirect));
		}

		return StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			GroupConstants.CMS_FRIENDLY_URL,
			"/edit_content_item?objectEntryId=",
			_objectEntry.getObjectEntryId(), "&p_l_mode=", Constants.READ,
			"&p_p_state=", LiferayWindowState.POP_UP, "&redirect=",
			HtmlUtil.escapeURL(redirect));
	}

	@Override
	public String getURLSharingNotification(ThemeDisplay themeDisplay)
		throws Exception {

		return getURLSharingNotification(false, themeDisplay);
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return null;
		}

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws Exception {

		if (themeDisplay == null) {
			return null;
		}

		return _assetDisplayPageFriendlyURLProvider.getFriendlyURL(
			new InfoItemReference(
				getClassName(), new ClassPKInfoItemIdentifier(getClassPK())),
			themeDisplay);
	}

	@Override
	public long getUserId() {
		return _objectEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _objectEntry.getUserName();
	}

	@Override
	public String getUuid() {
		return _objectEntry.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker) {
		try {
			return _objectEntryService.hasModelResourcePermission(
				_objectEntry, ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		try {
			return _objectEntryService.hasModelResourcePermission(
				_objectEntry, ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, _objectDefinition);
		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_EXTERNAL_REFERENCE_CODE,
			_objectEntry.getExternalReferenceCode());
		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_GROUP_ID, _objectEntry.getGroupId());
		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_READ_ONLY, Boolean.TRUE);
		httpServletRequest.setAttribute(WebKeys.TEMPLATE, template);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_objectEntryDisplayContextFactory.create(httpServletRequest));

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isCommentable() {
		return _objectDefinition.isEnableComments();
	}

	private PermissionChecker _getPermissionChecker(ThemeDisplay themeDisplay) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			permissionChecker = themeDisplay.getPermissionChecker();
		}

		return permissionChecker;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryAssetRenderer.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DLAppLocalService _dlAppLocalService;
	private final DLURLHelper _dlURLHelper;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntry _objectEntry;
	private final ObjectEntryDisplayContextFactory
		_objectEntryDisplayContextFactory;
	private final ObjectEntryService _objectEntryService;
	private final ObjectFieldLocalService _objectFieldLocalService;

}
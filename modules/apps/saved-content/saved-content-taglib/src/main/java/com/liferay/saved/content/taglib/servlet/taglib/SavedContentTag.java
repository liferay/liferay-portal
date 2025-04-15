/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.taglib.servlet.taglib;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.saved.content.constants.MySavedContentPortletKeys;
import com.liferay.saved.content.model.SavedContentEntry;
import com.liferay.saved.content.security.permission.SavedContentPermission;
import com.liferay.saved.content.service.SavedContentEntryLocalServiceUtil;
import com.liferay.saved.content.taglib.internal.permission.util.SavedContentPermissionUtil;
import com.liferay.saved.content.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Locale;
import java.util.Map;

/**
 * @author Alicia Garcia
 */
public class SavedContentTag extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public long getGroupId() {
		return _groupId;
	}

	public boolean isInTrash() {
		return _inTrash;
	}

	public void setClassName(String className) {
		if (className.equals(DLFileEntry.class.getName()) ||
			className.equals(FileEntry.class.getName()) ||
			className.equals(LiferayFileEntry.class.getName())) {

			className = DLFileEntryConstants.getClassName();
		}

		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setInTrash(boolean inTrash) {
		_inTrash = inTrash;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setViewMode(boolean viewMode) {
		_viewMode = viewMode;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
		_contentTitle = null;
		_groupId = 0;
		_inTrash = null;
		_saved = false;
		_url = null;
		_viewMode = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				"liferay-saved-content:saved-content:data",
				_getData(httpServletRequest, themeDisplay));
			httpServletRequest.setAttribute(
				"liferay-saved-content:saved-content:label",
				_getLabel(httpServletRequest, themeDisplay));

			httpServletRequest.setAttribute(
				"liferay-saved-content:saved-content:saved", _saved);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private String _getContentTitle(Locale locale) {
		if (Validator.isNotNull(_contentTitle)) {
			return _contentTitle;
		}

		try {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(_className);

			if (assetRendererFactory == null) {
				return null;
			}

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(_classPK);

			if (assetRenderer == null) {
				return null;
			}

			_contentTitle = assetRenderer.getTitle(locale);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get asset renderer for class ", _className,
						" with primary key ", _classPK),
					portalException);
			}
		}

		return _contentTitle;
	}

	private Map<String, Object> _getData(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"className", _className
		).put(
			"classPK", _classPK
		).put(
			"contentTitle", _getContentTitle(themeDisplay.getLocale())
		).put(
			"enabled", _isEnabled(themeDisplay)
		).put(
			"mySavedContentURL",
			_getMySavedContentURL(httpServletRequest, themeDisplay)
		).put(
			"portletNamespace",
			PortalUtil.getPortletNamespace(
				MySavedContentPortletKeys.MY_SAVED_CONTENT)
		).put(
			"saved", _isSaved(themeDisplay.getUserId())
		).put(
			"savedContentEntryURL", _getURL(httpServletRequest)
		).build();
	}

	private String _getLabel(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		if (_saved) {
			return LanguageUtil.format(
				httpServletRequest, "remove-x",
				_getContentTitle(themeDisplay.getLocale()));
		}

		return LanguageUtil.format(
			httpServletRequest, "save-x",
			_getContentTitle(themeDisplay.getLocale()));
	}

	private String _getMySavedContentURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, MySavedContentPortletKeys.MY_SAVED_CONTENT,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/saved_content/view_my_saved_content"
		).setBackURL(
			themeDisplay.getURLCurrent()
		).buildString();
	}

	private String _getURL(HttpServletRequest httpServletRequest) {
		if (Validator.isNull(_url)) {
			_url = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest,
					MySavedContentPortletKeys.MY_SAVED_CONTENT,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/saved_content/edit_saved_content_entry"
			).setRedirect(
				PortalUtil.getCurrentURL(httpServletRequest)
			).buildString();
		}

		return _url;
	}

	private boolean _isEnabled(ThemeDisplay themeDisplay)
		throws PortalException {

		if (_viewMode && !_isInTrash() && themeDisplay.isSignedIn()) {
			Group group = themeDisplay.getSiteGroup();

			SavedContentPermission savedContentPermission =
				SavedContentPermissionUtil.getSavedContentPermission();

			if (savedContentPermission.contains(
					themeDisplay.getPermissionChecker(), group.getGroupId(),
					ActionKeys.ADD_ENTRY) &&
				!group.isStagingGroup() && !group.isStagedRemotely()) {

				return true;
			}
		}

		return false;
	}

	private boolean _isInTrash() throws PortalException {
		if (_inTrash == null) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(_className);

			if (trashHandler == null) {
				return false;
			}

			return trashHandler.isInTrash(_classPK);
		}

		return _inTrash;
	}

	private boolean _isSaved(long userId) {
		if (!_saved) {
			SavedContentEntry savedContentEntry =
				SavedContentEntryLocalServiceUtil.fetchSavedContentEntry(
					userId, _groupId, _className, _classPK);

			if (savedContentEntry != null) {
				_saved = true;
			}
		}

		return _saved;
	}

	private static final String _PAGE = "/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SavedContentTag.class);

	private String _className;
	private long _classPK;
	private String _contentTitle;
	private long _groupId;
	private Boolean _inTrash;
	private boolean _saved;
	private String _url;
	private boolean _viewMode;

}
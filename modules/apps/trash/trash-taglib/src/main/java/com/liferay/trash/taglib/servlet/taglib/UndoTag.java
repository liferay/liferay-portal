/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.taglib.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.taglib.internal.servlet.ServletContextUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a message confirming items were moved to the Recycle Bin, and
 * presents an option to restore them.
 *
 * @author Julio Camarero
 */
public class UndoTag extends IncludeTag {

	@Override
	public int doStartTag() {
		if (_getData() == null) {
			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	public String getPortletURL() {
		return _portletURL;
	}

	public String getRedirect() {
		return _redirect;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL.toString();
	}

	public void setPortletURL(String portletURL) {
		_portletURL = portletURL;
	}

	public void setRedirect(String redirect) {
		_redirect = redirect;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_portletURL = null;
		_redirect = null;
	}

	@Override
	protected String getPage() {
		if (ListUtil.isEmpty(_getTrashedModels())) {
			return null;
		}

		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<TrashedModel> trashedModels = _getTrashedModels();

		List<Long> restoreTrashEntryIds = new ArrayList<>();
		List<String> titles = new ArrayList<>();

		for (TrashedModel trashedModel : trashedModels) {
			try {
				if (!(trashedModel instanceof BaseModel)) {
					continue;
				}

				TrashHelper trashHelper = ServletContextUtil.getTrashHelper();

				TrashEntry trashEntry = trashHelper.getTrashEntry(trashedModel);

				restoreTrashEntryIds.add(trashEntry.getEntryId());

				BaseModel<?> baseModel = (BaseModel<?>)trashedModel;

				TrashHandler trashHandler =
					TrashHandlerRegistryUtil.getTrashHandler(
						baseModel.getModelClassName());

				TrashRenderer trashRenderer = trashHandler.getTrashRenderer(
					trashedModel.getTrashEntryClassPK());

				titles.add(trashRenderer.getTitle(themeDisplay.getLocale()));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		httpServletRequest.setAttribute("liferay-trash:undo:cmd", _getCmd());
		httpServletRequest.setAttribute(
			"liferay-trash:undo:portletURL", _portletURL);
		httpServletRequest.setAttribute(
			"liferay-trash:undo:redirect", _redirect);
		httpServletRequest.setAttribute(
			"liferay-trash:undo:restoreTrashEntryIds", restoreTrashEntryIds);
		httpServletRequest.setAttribute("liferay-trash:undo:titles", titles);
		httpServletRequest.setAttribute(
			"liferay-trash:undo:trashedEntriesCount",
			restoreTrashEntryIds.size());
	}

	private String _getCmd() {
		Map<String, Object> data = _getData();

		if (data == null) {
			return Constants.MOVE_TO_TRASH;
		}

		return MapUtil.getString(data, Constants.CMD);
	}

	private Map<String, Object> _getData() {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String key =
			portletDisplay.getId() +
				SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA;

		if (!SessionMessages.contains(portletRequest, key)) {
			return null;
		}

		return (HashMap<String, Object>)SessionMessages.get(
			portletRequest, key);
	}

	private List<TrashedModel> _getTrashedModels() {
		Map<String, Object> data = _getData();

		if (data == null) {
			return Collections.emptyList();
		}

		List<TrashedModel> trashedModels = (List<TrashedModel>)data.get(
			"trashedModels");

		if (ListUtil.isEmpty(trashedModels)) {
			return Collections.emptyList();
		}

		return trashedModels;
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

	private static final String _PAGE = "/undo/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(UndoTag.class);

	private String _portletURL;
	private String _redirect;

}
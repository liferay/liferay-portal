/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * Provides utility methods moved from the trash entry model container's JSP
 * file to reduce the complexity of that particular view.
 *
 * @author Jürgen Kappler
 */
public class TrashContainerModelDisplayContext {

	public TrashContainerModelDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
	}

	public String getClassName() {
		if (Validator.isNotNull(_className)) {
			return _className;
		}

		_className = PortalUtil.getClassName(getClassNameId());

		return _className;
	}

	public long getClassNameId() {
		if (_classNameId != null) {
			return _classNameId;
		}

		_classNameId = ParamUtil.getLong(_httpServletRequest, "classNameId");

		return _classNameId;
	}

	public long getClassPK() {
		if (_classPK != null) {
			return _classPK;
		}

		_classPK = ParamUtil.getLong(_httpServletRequest, "classPK");

		return _classPK;
	}

	public String getContainerModelClassName() {
		if (Validator.isNotNull(_containerModelClassName)) {
			return _containerModelClassName;
		}

		_containerModelClassName = PortalUtil.getClassName(
			getContainerModelClassNameId());

		return _containerModelClassName;
	}

	public long getContainerModelClassNameId() {
		if (_containerModelClassNameId != null) {
			return _containerModelClassNameId;
		}

		String containerModelClassName = StringPool.BLANK;

		TrashHandler trashHandler = getTrashHandler();

		if (trashHandler != null) {
			containerModelClassName = trashHandler.getContainerModelClassName(
				getClassPK());
		}

		_containerModelClassNameId = ParamUtil.getLong(
			_httpServletRequest, "containerModelClassNameId",
			PortalUtil.getClassNameId(containerModelClassName));

		return _containerModelClassNameId;
	}

	public long getContainerModelId() {
		if (_containerModelId != null) {
			return _containerModelId;
		}

		_containerModelId = ParamUtil.getLong(
			_httpServletRequest, "containerModelId");

		return _containerModelId;
	}

	public String getContainerModelName() {
		if (Validator.isNotNull(_containerModelName)) {
			return _containerModelName;
		}

		TrashHandler trashHandler = getTrashHandler();

		if (trashHandler != null) {
			_containerModelName = trashHandler.getContainerModelName();
		}

		return _containerModelName;
	}

	public List<ContainerModel> getContainerModels() throws PortalException {
		if (ListUtil.isNotEmpty(_containerModels)) {
			return _containerModels;
		}

		SearchContainer<?> searchContainer = getSearchContainer();

		TrashHandler trashHandler = getTrashHandler();

		if ((searchContainer != null) && (trashHandler != null)) {
			_containerModels = trashHandler.getContainerModels(
				getClassPK(), getContainerModelId(), searchContainer.getStart(),
				searchContainer.getEnd());
		}

		return _containerModels;
	}

	public int getContainerModelsCount() throws PortalException {
		if (_containerModelsCount != null) {
			return _containerModelsCount;
		}

		int containerModelsCount = 0;

		TrashHandler trashHandler = getTrashHandler();

		if (trashHandler != null) {
			containerModelsCount = trashHandler.getContainerModelsCount(
				getClassPK(), getContainerModelId());
		}

		_containerModelsCount = containerModelsCount;

		return _containerModelsCount;
	}

	public PortletURL getContainerURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/view_container_model.jsp"
		).setRedirect(
			getRedirect()
		).setBackURL(
			(String)_httpServletRequest.getAttribute(WebKeys.CURRENT_URL)
		).setParameter(
			"classNameId", getClassNameId()
		).setParameter(
			"classPK", getClassPK()
		).buildPortletURL();
	}

	public Object[] getMissingContainerMessageArguments()
		throws PortalException {

		if (_missingContainerMessageArguments != null) {
			return _missingContainerMessageArguments;
		}

		String trashRendererTitle = StringPool.BLANK;

		TrashRenderer trashRenderer = getTrashRenderer();

		if (trashRenderer != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			trashRendererTitle = trashRenderer.getTitle(
				themeDisplay.getLocale());
		}

		_missingContainerMessageArguments = new Object[] {
			LanguageUtil.get(_httpServletRequest, getContainerModelName()),
			HtmlUtil.escape(trashRendererTitle)
		};

		return _missingContainerMessageArguments;
	}

	public String getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public SearchContainer<?> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		PortletURL containerURL = PortletURLBuilder.create(
			getContainerURL()
		).setParameter(
			"containerModelId", getContainerModelId()
		).buildPortletURL();

		_searchContainer = new SearchContainer(
			_liferayPortletRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA,
			containerURL, null, null);

		return _searchContainer;
	}

	public TrashHandler getTrashHandler() {
		if (_trashHandler != null) {
			return _trashHandler;
		}

		_trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			getClassName());

		return _trashHandler;
	}

	public TrashRenderer getTrashRenderer() throws PortalException {
		if (_trashRenderer != null) {
			return _trashRenderer;
		}

		TrashHandler trashHandler = getTrashHandler();

		if (trashHandler != null) {
			_trashRenderer = trashHandler.getTrashRenderer(getClassPK());
		}

		return _trashRenderer;
	}

	private String _className;
	private Long _classNameId;
	private Long _classPK;
	private String _containerModelClassName;
	private Long _containerModelClassNameId;
	private Long _containerModelId;
	private String _containerModelName;
	private List<ContainerModel> _containerModels;
	private Integer _containerModelsCount;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Object[] _missingContainerMessageArguments;
	private String _redirect;
	private SearchContainer<?> _searchContainer;
	private TrashHandler _trashHandler;
	private TrashRenderer _trashRenderer;

}
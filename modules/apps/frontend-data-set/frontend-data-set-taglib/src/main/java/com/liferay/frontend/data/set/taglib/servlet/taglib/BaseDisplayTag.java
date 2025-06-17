/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.servlet.taglib;

import com.liferay.frontend.data.set.model.FDSPaginationEntry;
import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.data.set.taglib.internal.servlet.ServletContextUtil;
import com.liferay.frontend.data.set.taglib.servlet.taglib.util.ServicesProvider;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.util.PropsValues;
import com.liferay.taglib.util.AttributesTagSupport;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Marko Cikos
 */
public class BaseDisplayTag extends AttributesTagSupport {

	@Override
	public int doEndTag() throws JspException {
		try {
			return processEndTag();
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			doClearTag();
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			_fdsPaginationEntries = new ArrayList<>();

			for (int curDelta :
					PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES) {

				if (curDelta > SearchContainer.MAX_DELTA) {
					continue;
				}

				_fdsPaginationEntries.add(
					new FDSPaginationEntry(null, curDelta));
			}

			_setViewsJSONArray();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.doStartTag();
	}

	public Map<String, Object> getAdditionalProps() {
		return _additionalProps;
	}

	public Map<String, Object> getEmptyState() {
		return _emptyState;
	}

	public String getId() {
		return _id;
	}

	public int getItemsPerPage() {
		return _itemsPerPage;
	}

	public String getNamespace() {
		if (_namespace != null) {
			return _namespace;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse != null) {
			_namespace = portletResponse.getNamespace();
		}

		return _namespace;
	}

	public int getPageNumber() {
		return _pageNumber;
	}

	public String getPropsTransformer() {
		return _propsTransformer;
	}

	public String getRandomNamespace() {
		return _randomNamespace;
	}

	public List<Object> getSelectedItems() {
		return _selectedItems;
	}

	public boolean getUniformActionsDisplay() {
		return _uniformActionsDisplay;
	}

	public void setAdditionalProps(Map<String, Object> additionalProps) {
		_additionalProps = additionalProps;
	}

	public void setEmptyState(Map<String, Object> emptyState) {
		_emptyState = emptyState;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setItemsPerPage(int itemsPerPage) {
		_itemsPerPage = itemsPerPage;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		fdsSerializer = ServletContextUtil.getFDSSerializer();

		super.setPageContext(pageContext);
	}

	public void setPageNumber(int pageNumber) {
		_pageNumber = pageNumber;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setPropsTransformer(String propsTransformer) {
		_propsTransformer = propsTransformer;
	}

	public void setPropsTransformerServletContext(
		ServletContext propsTransformerServletContext) {

		_propsTransformerServletContext = propsTransformerServletContext;
	}

	public void setRandomNamespace(String randomNamespace) {
		_randomNamespace = randomNamespace;
	}

	public void setSelectedItems(List<Object> selectedItems) {
		_selectedItems = selectedItems;
	}

	public void setUniformActionsDisplay(boolean uniformActionsDisplay) {
		_uniformActionsDisplay = uniformActionsDisplay;
	}

	protected void cleanUp() {
		_additionalProps = null;
		_emptyState = null;
		_fdsPaginationEntries = null;
		_id = null;
		_itemsPerPage = 0;
		_namespace = null;
		_pageNumber = 0;
		_portletURL = null;
		_propsTransformer = null;
		_propsTransformerServletContext = null;
		_randomNamespace = null;
		_selectedItems = null;
		_uniformActionsDisplay = false;
		_viewsJSONArray = null;
		fdsSerializer = null;
	}

	protected void doClearTag() {
		clearDynamicAttributes();
		clearParams();
		clearProperties();

		cleanUp();
	}

	protected ServletContext getPropsTransformerServletContext() {
		if (_propsTransformerServletContext != null) {
			return _propsTransformerServletContext;
		}

		return pageContext.getServletContext();
	}

	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		return HashMapBuilder.<String, Object>putAll(
			props
		).put(
			"additionalProps",
			() -> {
				if (_additionalProps != null) {
					return _additionalProps;
				}

				return null;
			}
		).put(
			"customViews", _getCustomViews()
		).put(
			"emptyState", _emptyState
		).put(
			"namespace", getNamespace()
		).put(
			"pagination",
			HashMapBuilder.<String, Object>put(
				"deltas", _fdsPaginationEntries
			).put(
				"initialDelta", _itemsPerPage
			).put(
				"initialPageNumber", _pageNumber
			).build()
		).put(
			"selectedItems", _selectedItems
		).put(
			"uniformActionsDisplay", getUniformActionsDisplay()
		).put(
			"views", _viewsJSONArray
		).build();
	}

	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div class=\"table-root\" id=\"");
		jspWriter.write(getRandomNamespace());
		jspWriter.write("table-id\"><span aria-hidden=\"true\" class=\"");
		jspWriter.write("loading-animation my-7\"></span>");

		String propsTransformer = null;

		if (Validator.isNotNull(_propsTransformer)) {
			if (_propsTransformer.contains(" from ")) {
				propsTransformer = _propsTransformer;
			}
			else {
				String resolvedPackageName = NPMResolvedPackageNameUtil.get(
					getPropsTransformerServletContext());

				propsTransformer =
					resolvedPackageName + "/" + _propsTransformer;
			}
		}

		if (FeatureFlagManagerUtil.isEnabled("LPS-164563")) {
			FDSRenderer fdsRenderer = ServicesProvider.getFDSRenderer();

			fdsRenderer.render(
				prepareProps(new HashMap<>()), getId(), getId(), getRequest(),
				(HttpServletResponse)pageContext.getResponse(), true,
				propsTransformer, jspWriter);
		}
		else {
			ComponentDescriptor componentDescriptor = new ComponentDescriptor(
				"{FrontendDataSet} from frontend-data-set-web", getId(),
				new LinkedHashSet<>(), false, propsTransformer);

			ReactRenderer reactRenderer = ServicesProvider.getReactRenderer();

			reactRenderer.renderReact(
				componentDescriptor, prepareProps(new HashMap<>()),
				getRequest(), jspWriter);
		}

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	protected FDSSerializer fdsSerializer;

	private String _getCustomViews() {
		HttpServletRequest httpServletRequest = getRequest();

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		return portalPreferences.getValue(
			ServletContextUtil.getFDSSettingsNamespace(httpServletRequest, _id),
			"customViews", "{}");
	}

	private void _setViewsJSONArray() {
		_viewsJSONArray = fdsSerializer.serializeViews(getId(), getRequest());
	}

	private static final Log _log = LogFactoryUtil.getLog(BaseDisplayTag.class);

	private Map<String, Object> _additionalProps;
	private Map<String, Object> _emptyState;
	private List<FDSPaginationEntry> _fdsPaginationEntries;
	private String _id;
	private int _itemsPerPage;
	private String _namespace;
	private int _pageNumber;
	private PortletURL _portletURL;
	private String _propsTransformer;
	private ServletContext _propsTransformerServletContext;
	private String _randomNamespace;
	private List<Object> _selectedItems;
	private boolean _uniformActionsDisplay;
	private JSONArray _viewsJSONArray;

}
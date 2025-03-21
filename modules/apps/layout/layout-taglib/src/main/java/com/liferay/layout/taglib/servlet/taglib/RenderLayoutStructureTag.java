/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.taglib.internal.util.SegmentsExperienceUtil;
import com.liferay.layout.taglib.servlet.taglib.renderer.LayoutStructureRenderer;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class RenderLayoutStructureTag extends IncludeTag {

	public LayoutStructure getLayoutStructure() {
		return _layoutStructure;
	}

	public String getMainItemId() {
		return _mainItemId;
	}

	public String getMode() {
		return _mode;
	}

	public boolean isShowPreview() {
		return _showPreview;
	}

	public void setLayoutStructure(LayoutStructure layoutStructure) {
		_layoutStructure = layoutStructure;
	}

	public void setMainItemId(String mainItemId) {
		_mainItemId = mainItemId;
	}

	public void setMode(String mode) {
		_mode = mode;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRenderActionHandler(boolean renderActionHandler) {
		_renderActionHandler = renderActionHandler;
	}

	public void setShowPreview(boolean showPreview) {
		_showPreview = showPreview;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_layoutStructure = null;
		_mainItemId = null;
		_mode = FragmentEntryLinkConstants.VIEW;
		_renderActionHandler = true;
		_showPreview = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		if (_layoutStructure == null) {
			_layoutStructure = _getLayoutStructure();
		}

		if (_layoutStructure != null) {
			LayoutStructureRenderer layoutStructureRenderer =
				new LayoutStructureRenderer(
					getRequest(), _layoutStructure, _mainItemId, _mode,
					pageContext, _renderActionHandler, _showPreview);

			layoutStructureRenderer.render();
		}

		return SKIP_BODY;
	}

	private LayoutStructure _getLayoutStructure() {
		HttpServletRequest httpServletRequest = getRequest();

		LayoutStructure layoutStructure =
			(LayoutStructure)httpServletRequest.getAttribute(
				LayoutWebKeys.LAYOUT_STRUCTURE);

		if (layoutStructure != null) {
			return layoutStructure;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutStructureProvider layoutStructureProvider =
			ServletContextUtil.getLayoutStructureHelper();

		return layoutStructureProvider.getLayoutStructure(
			themeDisplay.getPlid(),
			SegmentsExperienceUtil.getSegmentsExperienceId(httpServletRequest));
	}

	private static final String _PAGE = "/render_layout_structure/page.jsp";

	private LayoutStructure _layoutStructure;
	private String _mainItemId;
	private String _mode = FragmentEntryLinkConstants.VIEW;
	private boolean _renderActionHandler = true;
	private boolean _showPreview;

}
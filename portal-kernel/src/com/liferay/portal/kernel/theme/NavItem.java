/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.theme;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a portal navigation item, providing access to layouts and metadata
 * from templates, which can be found in a theme's
 * <code>portal-normal.vm</code>.
 *
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class NavItem implements Serializable {

	public static List<NavItem> fromLayouts(
			HttpServletRequest httpServletRequest, List<Layout> parentLayouts,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (parentLayouts == null) {
			return Collections.emptyList();
		}

		Map<Long, List<Layout>> layoutChildLayouts =
			LayoutLocalServiceUtil.getLayoutChildLayouts(parentLayouts);

		for (List<Layout> childLayouts : layoutChildLayouts.values()) {
			Iterator<Layout> iterator = childLayouts.iterator();

			while (iterator.hasNext()) {
				Layout childLayout = iterator.next();

				if (_isContentLayoutDraft(childLayout) ||
					!_isLayoutRevisionDisplayable(childLayout) ||
					childLayout.isHidden() ||
					!LayoutPermissionUtil.contains(
						themeDisplay.getPermissionChecker(), childLayout,
						ActionKeys.VIEW)) {

					iterator.remove();
				}
			}
		}

		List<NavItem> navItems = new ArrayList<>(parentLayouts.size());

		for (Layout parentLayout : parentLayouts) {
			List<Layout> childLayouts = layoutChildLayouts.get(
				parentLayout.getPlid());

			if (_isContentLayoutDraft(parentLayout) ||
				!_isLayoutRevisionDisplayable(parentLayout)) {

				continue;
			}

			navItems.add(
				new NavItem(
					httpServletRequest, themeDisplay, parentLayout,
					childLayouts));
		}

		return navItems;
	}

	public NavItem(HttpServletRequest httpServletRequest, Layout layout) {
		this(
			httpServletRequest,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY),
			layout);
	}

	public NavItem(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		Layout layout) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
		_layout = layout;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavItem)) {
			return false;
		}

		NavItem navItem = (NavItem)object;

		if (getLayoutId() == navItem.getLayoutId()) {
			return true;
		}

		return false;
	}

	/**
	 * Returns all of the browsable child layouts that the current user has
	 * permission to access from this navigation item's layout.
	 *
	 * @return the list of all browsable child layouts that the current user has
	 *         permission to access from this navigation item's layout
	 * @throws Exception if an exception occurred
	 */
	public List<NavItem> getBrowsableChildren() throws Exception {
		if (_browsableChildren == null) {
			_browsableChildren = ListUtil.filter(
				getChildren(), NavItem::isBrowsable);
		}

		return _browsableChildren;
	}

	/**
	 * Returns all of child layouts that the current user has permission to
	 * access from this navigation item's layout.
	 *
	 * @return the list of all child layouts that the current user has
	 *         permission to access from this navigation item's layout
	 * @throws Exception if an exception occurred
	 */
	public List<NavItem> getChildren() throws Exception {
		if (_children == null) {
			List<Layout> layouts = _layout.getChildren(
				_themeDisplay.getPermissionChecker());

			_children = _fromLayouts(
				_httpServletRequest, _themeDisplay, layouts);
		}

		return _children;
	}

	public String getDisplayIcon() throws Exception {
		return StringPool.BLANK;
	}

	public Map<String, Serializable> getExpandoAttributes() {
		if (_layout != null) {
			ExpandoBridge expandoBridge = _layout.getExpandoBridge();

			if (expandoBridge != null) {
				return expandoBridge.getAttributes();
			}
		}

		return new HashMap<>();
	}

	/**
	 * Returns the navigation item's layout.
	 *
	 * @return the navigation item's layout
	 */
	public Layout getLayout() {
		return _layout;
	}

	/**
	 * Returns the ID of the navigation item's layout.
	 *
	 * @return the ID of the navigation item's layout
	 */
	public long getLayoutId() {
		return _layout.getLayoutId();
	}

	/**
	 * Returns the HTML-escaped name of the navigation item's layout.
	 *
	 * @return the HTML-escaped name of the navigation item's layout
	 */
	public String getName() {
		return HtmlUtil.escape(getUnescapedName());
	}

	/**
	 * Returns the full, absolute URL (including the portal's URL) of the
	 * navigation item's layout.
	 *
	 * @return the full, absolute URL of the navigation item's layout
	 * @throws Exception if an exception occurred
	 */
	public String getRegularFullURL() throws Exception {
		String portalURL = _themeDisplay.getPortalURL();

		String regularURL = getRegularURL();

		if (StringUtil.startsWith(regularURL, portalURL) ||
			Validator.isUrl(regularURL)) {

			return regularURL;
		}

		return portalURL.concat(regularURL);
	}

	/**
	 * Returns the regular URL of the navigation item's layout.
	 *
	 * @return the regular URL of the navigation item's layout
	 * @throws Exception if an exception occurred
	 */
	public String getRegularURL() throws Exception {
		return _layout.getRegularURL(_httpServletRequest);
	}

	public String getResetLayoutURL() throws Exception {
		return _layout.getResetLayoutURL(_httpServletRequest);
	}

	public String getResetMaxStateURL() throws Exception {
		return _layout.getResetMaxStateURL(_httpServletRequest);
	}

	/**
	 * Returns the target of the navigation item's layout.
	 *
	 * @return the target of the navigation item's layout
	 */
	public String getTarget() {
		return _layout.getTarget();
	}

	/**
	 * Returns the title of the navigation item's layout in the current
	 * request's locale.
	 *
	 * @return the title of the navigation item's layout in the current
	 *         request's locale
	 */
	public String getTitle() {
		return _layout.getTitle(_themeDisplay.getLanguageId());
	}

	/**
	 * Returns the unescaped name of the navigation item's layout in the current
	 * request's locale.
	 *
	 * @return the unescaped name of the navigation item's layout in the current
	 *         request's locale
	 */
	public String getUnescapedName() {
		return _layout.getName(_themeDisplay.getLanguageId());
	}

	/**
	 * Returns the URL of the navigation item's layout, in a format that makes
	 * it safe to use the URL as an HREF attribute value
	 *
	 * @return the URL of the navigation item's layout, in a format that makes
	 *         it safe to use the URL as an HREF attribute value
	 * @throws Exception if an exception occurred
	 */
	public String getURL() throws Exception {
		return HtmlUtil.escapeHREF(getRegularFullURL());
	}

	/**
	 * Returns <code>true</code> if the navigation item's layout has browsable
	 * child layouts.
	 *
	 * @return <code>true</code> if the navigation item's layout has browsable
	 *         child layouts; <code>false</code> otherwise
	 * @throws Exception if an exception occurred
	 */
	public boolean hasBrowsableChildren() throws Exception {
		List<NavItem> browsableChildren = getBrowsableChildren();

		return !browsableChildren.isEmpty();
	}

	/**
	 * Returns <code>true</code> if the navigation item's layout has child
	 * layouts.
	 *
	 * @return <code>true</code> if the navigation item's layout has child
	 *         layouts; <code>false</code> otherwise
	 * @throws Exception if an exception occurred
	 */
	public boolean hasChildren() throws Exception {
		List<NavItem> children = getChildren();

		return !children.isEmpty();
	}

	@Override
	public int hashCode() {
		return _layout.hashCode();
	}

	public String iconURL() throws Exception {
		if ((_layout == null) || !_layout.isIconImage()) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			_themeDisplay.getPathImage(), "/layout_icon?img_id=",
			_layout.getIconImageId(), "&t=",
			WebServerServletTokenUtil.getToken(_layout.getIconImageId()));
	}

	public boolean isBrowsable() {
		LayoutType layoutType = _layout.getLayoutType();

		return layoutType.isBrowsable();
	}

	public boolean isChildSelected() throws PortalException {
		return _layout.isChildSelected(
			_themeDisplay.isTilesSelectable(), _themeDisplay.getLayout());
	}

	public boolean isInNavigation(List<NavItem> navItems) {
		if (navItems == null) {
			return false;
		}

		return navItems.contains(this);
	}

	public boolean isSelected() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		return _layout.isSelected(
			_themeDisplay.isTilesSelectable(), _themeDisplay.getLayout(),
			layout.getAncestorPlid());
	}

	private static boolean _isContentLayoutDraft(Layout layout) {
		if (!layout.isTypeContent()) {
			return false;
		}

		if (layout.fetchDraftLayout() != null) {
			return !layout.isPublished();
		}

		if (layout.isApproved() && !layout.isHidden() && !layout.isSystem()) {
			return false;
		}

		return true;
	}

	private static boolean _isLayoutRevisionDisplayable(Layout layout) {
		if (layout.isTypeContent() ||
			!LayoutStagingUtil.isBranchingLayout(layout)) {

			return true;
		}

		LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
			layout);

		if (!layoutRevision.isIncomplete()) {
			return true;
		}

		LayoutType layoutType = layout.getLayoutType();

		LayoutTypeController layoutTypeController =
			layoutType.getLayoutTypeController();

		return layoutTypeController.isWorkflowEnabled();
	}

	private NavItem(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		Layout layout, List<Layout> childLayouts) {

		_httpServletRequest = httpServletRequest;
		_themeDisplay = themeDisplay;
		_layout = layout;

		_children = _fromLayouts(
			httpServletRequest, themeDisplay, childLayouts);
	}

	private List<NavItem> _fromLayouts(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		List<Layout> layouts) {

		if (ListUtil.isEmpty(layouts)) {
			return Collections.emptyList();
		}

		List<NavItem> navItems = new ArrayList<>(layouts.size());

		for (Layout layout : layouts) {
			navItems.add(new NavItem(httpServletRequest, themeDisplay, layout));
		}

		return navItems;
	}

	private List<NavItem> _browsableChildren;
	private List<NavItem> _children;
	private final HttpServletRequest _httpServletRequest;
	private final Layout _layout;
	private final ThemeDisplay _themeDisplay;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutWrapper;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LayoutTypePortletFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Raymond Augé
 */
public class VirtualLayout extends LayoutWrapper {

	public VirtualLayout(Layout sourceLayout, Group targetGroup) {
		super(sourceLayout);

		_sourceLayout = sourceLayout;
		_targetGroup = targetGroup;
	}

	@Override
	public Object clone() {
		return new VirtualLayout((Layout)_sourceLayout.clone(), _targetGroup);
	}

	@Override
	public List<Portlet> getEmbeddedPortlets() {
		return super.getEmbeddedPortlets(getGroupId());
	}

	@Override
	public String getFriendlyURL() {
		return getFriendlyURL(null);
	}

	@Override
	public String getFriendlyURL(Locale locale) {
		StringBundler sb = new StringBundler(3);

		sb.append(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

		try {
			Group group = _sourceLayout.getGroup();

			sb.append(group.getFriendlyURL());
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (locale == null) {
			sb.append(_sourceLayout.getFriendlyURL());
		}
		else {
			sb.append(_sourceLayout.getFriendlyURL(locale));
		}

		return sb.toString();
	}

	@Override
	public Group getGroup() {
		return getHostGroup();
	}

	@Override
	public long getGroupId() {
		return getVirtualGroupId();
	}

	public Group getHostGroup() {
		return _targetGroup;
	}

	@Override
	public LayoutSet getLayoutSet() {
		if (_layoutSet == null) {
			if (isPrivateLayout()) {
				_layoutSet = _targetGroup.getPrivateLayoutSet();
			}
			else {
				_layoutSet = _targetGroup.getPublicLayoutSet();
			}
		}

		return _layoutSet;
	}

	@Override
	public LayoutType getLayoutType() {
		if (_layoutType == null) {
			_layoutType = LayoutTypePortletFactoryUtil.create(this);
		}

		return _layoutType;
	}

	@Override
	public String getRegularURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		String layoutURL = _sourceLayout.getRegularURL(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return injectVirtualGroupURL(layoutURL, themeDisplay.getLocale());
	}

	@Override
	public String getResetLayoutURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		String layoutURL = _sourceLayout.getResetLayoutURL(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return injectVirtualGroupURL(layoutURL, themeDisplay.getLocale());
	}

	@Override
	public String getResetMaxStateURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		String layoutURL = _sourceLayout.getResetMaxStateURL(
			httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return injectVirtualGroupURL(layoutURL, themeDisplay.getLocale());
	}

	public long getSourceGroupId() {
		return _sourceLayout.getGroupId();
	}

	public Layout getSourceLayout() {
		return _sourceLayout;
	}

	public long getVirtualGroupId() {
		return _targetGroup.getGroupId();
	}

	@Override
	public void setLayoutSet(LayoutSet layoutSet) {
		super.setLayoutSet(layoutSet);

		_layoutSet = null;
	}

	@Override
	public void setPrivateLayout(boolean privateLayout) {
		super.setPrivateLayout(privateLayout);

		_layoutSet = null;
	}

	protected String injectVirtualGroupURL(String layoutURL, Locale locale) {
		if (_sourceLayout.isTypeURL()) {
			return layoutURL;
		}

		try {
			Group group = _sourceLayout.getGroup();

			StringBundler sb = new StringBundler(4);

			if (_targetGroup.isUser() && isPrivateLayout()) {
				layoutURL = layoutURL.replaceFirst(
					_LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
					_LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING);
			}

			int pos = layoutURL.indexOf(group.getFriendlyURL());

			sb.append(layoutURL.substring(0, pos));

			sb.append(_targetGroup.getFriendlyURL());
			sb.append(getFriendlyURL(locale));

			pos = layoutURL.indexOf(StringPool.QUESTION);

			if (pos > 0) {
				sb.append(layoutURL.substring(pos));
			}

			return sb.toString();
		}
		catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private static final String
		_LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING = PropsUtil.get(
			PropsKeys.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);

	private static final String
		_LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING = PropsUtil.get(
			PropsKeys.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING);

	private static final Log _log = LogFactoryUtil.getLog(VirtualLayout.class);

	private LayoutSet _layoutSet;
	private LayoutType _layoutType;
	private final Layout _sourceLayout;
	private final Group _targetGroup;

}
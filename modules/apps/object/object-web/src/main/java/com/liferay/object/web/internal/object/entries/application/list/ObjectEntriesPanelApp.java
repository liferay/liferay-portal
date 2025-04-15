/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntriesPanelApp extends BasePanelApp {

	public ObjectEntriesPanelApp(
		ObjectDefinition objectDefinition, Supplier<Portlet> supplier) {

		_objectDefinition = objectDefinition;
		_supplier = supplier;
	}

	@Override
	public String getKey() {
		return super.getKey() + StringPool.POUND +
			_objectDefinition.getObjectDefinitionId();
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public Portlet getPortlet() {
		Portlet portlet = _portlet;

		if (portlet == null) {
			portlet = _supplier.get();

			if (portlet.getCompanyId() == _objectDefinition.getCompanyId()) {
				_portlet = portlet;
			}
		}

		return portlet;
	}

	@Override
	public String getPortletId() {
		return _objectDefinition.getPortletId();
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest)
		).setParameter(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
		).buildPortletURL();
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if ((permissionChecker.getCompanyId() !=
				_objectDefinition.getCompanyId()) ||
			_objectDefinition.isRootDescendantNode()) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		if (StringUtil.equals(
				_objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return themeDisplay.getControlPanelGroup();
		}

		return super.getGroup(httpServletRequest);
	}

	private final ObjectDefinition _objectDefinition;
	private volatile Portlet _portlet;
	private final Supplier<Portlet> _supplier;

}
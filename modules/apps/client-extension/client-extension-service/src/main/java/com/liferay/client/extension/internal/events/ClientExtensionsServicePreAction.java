/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.events;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.ThemeFaviconCET;
import com.liferay.client.extension.type.ThemeSpritemapCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class ClientExtensionsServicePreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = _getLayout(httpServletRequest, themeDisplay);

		if (layout == null) {
			return;
		}

		themeDisplay.setFaviconURL(_getFaviconURL(layout));

		ThemeCSSCET themeCSSCET = _getThemeCSSCET(layout);

		if (themeCSSCET != null) {
			if (_portal.isRightToLeft(httpServletRequest)) {
				themeDisplay.setClayCSSURL(themeCSSCET.getClayRTLURL());
				themeDisplay.setMainCSSURL(themeCSSCET.getMainRTLURL());
			}
			else {
				themeDisplay.setClayCSSURL(themeCSSCET.getClayURL());
				themeDisplay.setMainCSSURL(themeCSSCET.getMainURL());
			}
		}

		ThemeSpritemapCET themeSpritemapCET = _getThemeSpritemapCET(layout);

		if (themeSpritemapCET != null) {
			themeDisplay.setPathThemeSpritemap(themeSpritemapCET.getURL());
		}
	}

	private CET _getCET(
		long classNameId, long classPK, long companyId, String type) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				classNameId, classPK, type);

		if (clientExtensionEntryRel == null) {
			return null;
		}

		return _cetManager.getCET(
			companyId, clientExtensionEntryRel.getCETExternalReferenceCode());
	}

	private ThemeCSSCET _getControlPanelThemeCSSCET(Layout layout) {
		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				_portal.getClassNameId(Layout.class), layout.getPlid(),
				ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if ((clientExtensionEntryRels == null) ||
			(clientExtensionEntryRels.size() != 1)) {

			return null;
		}

		ClientExtensionEntryRel clientExtensionEntryRel =
			clientExtensionEntryRels.get(0);

		return (ThemeCSSCET)_cetManager.getCET(
			layout.getCompanyId(),
			clientExtensionEntryRel.getCETExternalReferenceCode());
	}

	private String _getFaviconURL(Layout layout) {
		String faviconURL = _getThemeFaviconCETURL(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			layout.getCompanyId());

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		faviconURL = layout.getFaviconURL();

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		long masterLayoutPlid = layout.getMasterLayoutPlid();

		if (masterLayoutPlid > 0) {
			Layout masterLayout = _layoutLocalService.fetchLayout(
				masterLayoutPlid);

			if (masterLayout != null) {
				faviconURL = _getThemeFaviconCETURL(
					_portal.getClassNameId(Layout.class),
					masterLayout.getPlid(), layout.getCompanyId());

				if (Validator.isNotNull(faviconURL)) {
					return faviconURL;
				}

				faviconURL = masterLayout.getFaviconURL();

				if (Validator.isNotNull(faviconURL)) {
					return faviconURL;
				}
			}
		}

		LayoutSet layoutSet = layout.getLayoutSet();

		faviconURL = _getThemeFaviconCETURL(
			_portal.getClassNameId(LayoutSet.class), layoutSet.getLayoutSetId(),
			layout.getCompanyId());

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		faviconURL = layoutSet.getFaviconURL();

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		return null;
	}

	private Layout _getLayout(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeControlPanel()) {
			return layout;
		}

		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (!Objects.equals(mode, Constants.PREVIEW)) {
			return layout;
		}

		long selPlid = ParamUtil.getLong(
			httpServletRequest,
			StringBundler.concat(
				StringPool.UNDERLINE,
				ParamUtil.getString(httpServletRequest, "p_p_id"), "_selPlid"));

		if (selPlid <= 0) {
			return layout;
		}

		return _layoutLocalService.fetchLayout(selPlid);
	}

	private ThemeCSSCET _getThemeCSSCET(Layout layout) {
		if (layout.isTypeControlPanel()) {
			return _getControlPanelThemeCSSCET(layout);
		}

		CET cet = _getCET(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			layout.getCompanyId(),
			ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if ((cet == null) && (layout.getMasterLayoutPlid() > 0)) {
			cet = _getCET(
				_portal.getClassNameId(Layout.class),
				layout.getMasterLayoutPlid(), layout.getCompanyId(),
				ClientExtensionEntryConstants.TYPE_THEME_CSS);
		}

		if (cet == null) {
			LayoutSet layoutSet = layout.getLayoutSet();

			cet = _getCET(
				_portal.getClassNameId(LayoutSet.class),
				layoutSet.getLayoutSetId(), layout.getCompanyId(),
				ClientExtensionEntryConstants.TYPE_THEME_CSS);
		}

		if (cet != null) {
			return (ThemeCSSCET)cet;
		}

		return null;
	}

	private String _getThemeFaviconCETURL(
		long classNameId, long classPK, long companyId) {

		CET cet = _getCET(
			classNameId, classPK, companyId,
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		if (cet == null) {
			return null;
		}

		ThemeFaviconCET themeFaviconCET = (ThemeFaviconCET)cet;

		return themeFaviconCET.getURL();
	}

	private ThemeSpritemapCET _getThemeSpritemapCET(Layout layout) {
		CET cet = _getCET(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			layout.getCompanyId(),
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);

		if ((cet == null) && (layout.getMasterLayoutPlid() > 0)) {
			cet = _getCET(
				_portal.getClassNameId(Layout.class),
				layout.getMasterLayoutPlid(), layout.getCompanyId(),
				ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);
		}

		if (cet == null) {
			LayoutSet layoutSet = layout.getLayoutSet();

			cet = _getCET(
				_portal.getClassNameId(LayoutSet.class),
				layoutSet.getLayoutSetId(), layout.getCompanyId(),
				ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);
		}

		if (cet != null) {
			return (ThemeSpritemapCET)cet;
		}

		return null;
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}
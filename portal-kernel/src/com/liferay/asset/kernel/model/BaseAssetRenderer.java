/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
public abstract class BaseAssetRenderer<T> implements AssetRenderer<T> {

	@Override
	public AssetRendererFactory<T> getAssetRendererFactory() {
		if (_assetRendererFactory != null) {
			return _assetRendererFactory;
		}

		_assetRendererFactory =
			(AssetRendererFactory<T>)
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(getClassName());

		return _assetRendererFactory;
	}

	@Override
	public int getAssetRendererType() {
		return _assetRendererType;
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return _AVAILABLE_LANGUAGE_IDS;
	}

	@Override
	public DDMFormValuesReader getDDMFormValuesReader() {
		return _nullDDMFormValuesReader;
	}

	@Override
	public String getDiscussionPath() {
		return null;
	}

	@Override
	public String getIconCssClass() throws PortalException {
		return getAssetRendererFactory().getIconCssClass();
	}

	@Override
	public String getNewName(String oldName, String token) {
		return StringBundler.concat(oldName, StringPool.SPACE, token);
	}

	@Override
	public String getSearchSummary(Locale locale) {
		return getSummary(null, null);
	}

	@Override
	public int getStatus() {
		return WorkflowConstants.STATUS_APPROVED;
	}

	@Override
	public String getSummary() {
		return getSummary(null, null);
	}

	@Override
	public String[] getSupportedConversions() {
		return null;
	}

	@Override
	public String getThumbnailPath(PortletRequest portletRequest)
		throws Exception {

		return null;
	}

	@Override
	public String getURLDownload(ThemeDisplay themeDisplay) {
		return null;
	}

	@Override
	public PortletURL getURLEdit(
			HttpServletRequest httpServletRequest, WindowState windowState,
			PortletURL redirectURL)
		throws Exception {

		String redirect = null;

		if (redirectURL != null) {
			redirect = redirectURL.toString();
		}

		return getURLEdit(httpServletRequest, windowState, redirect);
	}

	@Override
	public PortletURL getURLEdit(
			HttpServletRequest httpServletRequest, WindowState windowState,
			String redirect)
		throws Exception {

		LiferayPortletURL editPortletURL = (LiferayPortletURL)getURLEdit(
			httpServletRequest);

		if (editPortletURL == null) {
			return null;
		}

		return _getURLEdit(
			editPortletURL, httpServletRequest, windowState, redirect);
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return null;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState, PortletURL redirectURL)
		throws Exception {

		String redirect = null;

		if (redirectURL != null) {
			redirect = redirectURL.toString();
		}

		return getURLEdit(
			liferayPortletRequest, liferayPortletResponse, windowState,
			redirect);
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState, String redirect)
		throws Exception {

		LiferayPortletURL editPortletURL = (LiferayPortletURL)getURLEdit(
			liferayPortletRequest, liferayPortletResponse);

		if (editPortletURL == null) {
			return null;
		}

		return _getURLEdit(
			editPortletURL,
			PortalUtil.getHttpServletRequest(liferayPortletRequest),
			windowState, redirect);
	}

	@Override
	public PortletURL getURLExport(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return null;
	}

	@Override
	public String getURLImagePreview(PortletRequest portletRequest)
		throws Exception {

		return getThumbnailPath(portletRequest);
	}

	@Override
	public String getURLShare(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		return null;
	}

	@Override
	public String getUrlTitle() {
		return null;
	}

	@Override
	public String getUrlTitle(Locale locale) {
		return getUrlTitle();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		return StringPool.BLANK;
	}

	@Override
	public PortletURL getURLViewDiffs(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return null;
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws Exception {

		return null;
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws Exception {

		return null;
	}

	@Override
	public String getViewInContextMessage() {
		return "view-on-new-page";
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return false;
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return true;
	}

	@Override
	public boolean isCommentable() {
		return Validator.isNotNull(getDiscussionPath());
	}

	@Override
	public boolean isConvertible() {
		return false;
	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public boolean isLocalizable() {
		return false;
	}

	@Override
	public boolean isPreviewInContext() {
		return false;
	}

	@Override
	public boolean isPrintable() {
		return false;
	}

	@Override
	public boolean isRatable() {
		return true;
	}

	public void setAssetRendererType(int assetRendererType) {
		_assetRendererType = assetRendererType;
	}

	protected long getControlPanelPlid(
			LiferayPortletRequest liferayPortletRequest)
		throws PortalException {

		return PortalUtil.getControlPanelPlid(liferayPortletRequest);
	}

	protected long getControlPanelPlid(ThemeDisplay themeDisplay)
		throws PortalException {

		return PortalUtil.getControlPanelPlid(themeDisplay.getCompanyId());
	}

	protected Locale getLocale(PortletRequest portletRequest) {
		if (portletRequest != null) {
			return portletRequest.getLocale();
		}

		return LocaleUtil.getMostRelevantLocale();
	}

	protected String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest, String noSuchEntryRedirect,
		String path, String primaryKeyParameterName,
		long primaryKeyParameterValue) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, path, primaryKeyParameterName,
			primaryKeyParameterValue);
	}

	protected String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect, String path,
		String primaryKeyParameterName, long primaryKeyParameterValue) {

		return PortalUtil.addPreservedParameters(
			themeDisplay,
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(), path,
				"?p_l_id=", themeDisplay.getPlid(), "&noSuchEntryRedirect=",
				URLCodec.encodeURL(noSuchEntryRedirect), StringPool.AMPERSAND,
				primaryKeyParameterName, StringPool.EQUAL,
				primaryKeyParameterValue));
	}

	private PortletURL _getURLEdit(
			LiferayPortletURL editPortletURL,
			HttpServletRequest httpServletRequest, WindowState windowState,
			String redirect)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.isLayout()) {
			Layout layout = themeDisplay.getLayout();

			group = layout.getGroup();
		}

		if (group.hasStagingGroup() && _STAGING_LIVE_GROUP_LOCKING_ENABLED) {
			return null;
		}

		if (Validator.isNotNull(redirect)) {
			editPortletURL.setParameter("redirect", redirect);
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource", portletDisplay.getId());

		if (Validator.isNotNull(portletResource)) {
			editPortletURL.setParameter(
				"referringPortletResource", portletResource);
		}
		else {
			editPortletURL.setParameter(
				"referringPortletResource", portletDisplay.getId());
		}

		editPortletURL.setPortletMode(PortletMode.VIEW);
		editPortletURL.setRefererPlid(themeDisplay.getPlid());
		editPortletURL.setWindowState(windowState);

		return editPortletURL;
	}

	private static final String[] _AVAILABLE_LANGUAGE_IDS = new String[0];

	private static final boolean _STAGING_LIVE_GROUP_LOCKING_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.STAGING_LIVE_GROUP_LOCKING_ENABLED));

	private static final DDMFormValuesReader _nullDDMFormValuesReader =
		new NullDDMFormValuesReader();

	private AssetRendererFactory<T> _assetRendererFactory;
	private int _assetRendererType = AssetRendererFactory.TYPE_LATEST_APPROVED;

	private static final class NullDDMFormValuesReader
		implements DDMFormValuesReader {

		@Override
		public List<DDMFormFieldValue> getDDMFormFieldValues(
			String ddmFormFieldType) {

			return Collections.emptyList();
		}

		@Override
		public DDMFormValues getDDMFormValues() {
			return new DDMFormValues(new DDMForm());
		}

	}

}
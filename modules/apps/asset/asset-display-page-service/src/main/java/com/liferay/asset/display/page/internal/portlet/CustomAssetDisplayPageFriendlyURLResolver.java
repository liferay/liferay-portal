/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.portlet;

import com.liferay.asset.display.page.portlet.BaseAssetDisplayPageFriendlyURLResolver;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Víctor Galán
 */
@Component(service = FriendlyURLResolver.class)
public class CustomAssetDisplayPageFriendlyURLResolver
	extends BaseAssetDisplayPageFriendlyURLResolver {

	@Override
	public String getActualURL(
			long companyId, long groupId, boolean privateLayout,
			String mainPath, String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		String actualURL = super.getActualURL(
			companyId, groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)requestContext.get("request");

		httpServletRequest.setAttribute(
			WebKeys.PAGE_ROBOTS, "noindex, nofollow");

		return actualURL;
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_CUSTOM_ASSET;
	}

	@Override
	public String getKey() {
		return "custom-asset-display-page";
	}

	@Override
	public boolean isURLSeparatorConfigurable() {
		return true;
	}

	@Override
	protected LayoutDisplayPageObjectProvider<?>
		getLayoutDisplayPageObjectProvider(
			LayoutDisplayPageProvider<?> layoutDisplayPageProvider,
			long groupId, String friendlyURL, Map<String, String[]> params) {

		String[] parts = _getPathParts(friendlyURL);

		if (parts.length < 3) {
			return null;
		}

		InfoItemIdentifier infoItemIdentifier = null;

		if (Validator.isNumber(parts[2])) {
			infoItemIdentifier = new ClassPKInfoItemIdentifier(
				GetterUtil.getLong(parts[2]));
		}
		else {
			infoItemIdentifier = new ERCInfoItemIdentifier(parts[2]);
		}

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			new InfoItemReference(
				portal.getClassName(GetterUtil.getLong(parts[1])),
				infoItemIdentifier));
	}

	@Override
	protected Layout getLayoutDisplayPageObjectProviderLayout(
		long groupId, String friendlyURL,
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		LayoutDisplayPageProvider<?> layoutDisplayPageProvider) {

		String[] parts = _getPathParts(friendlyURL);

		if (parts.length < 3) {
			return null;
		}

		return layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, false, StringPool.SLASH + parts[0]);
	}

	@Override
	protected LayoutDisplayPageProvider<?> getLayoutDisplayPageProvider(
		String friendlyURL) {

		String[] parts = _getPathParts(friendlyURL);

		if (parts.length < 3) {
			return null;
		}

		return layoutDisplayPageProviderRegistry.
			getLayoutDisplayPageProviderByClassName(
				portal.getClassName(GetterUtil.getLong(parts[1])));
	}

	@Override
	protected boolean useOriginalFriendlyURL() {
		return false;
	}

	private String[] _getPathParts(String path) {
		String urlSeparator = getURLSeparator();

		String urlInfo = path.substring(
			path.indexOf(urlSeparator) + urlSeparator.length() - 1);

		List<String> parts = StringUtil.split(urlInfo, CharPool.SLASH);

		String classNameId = parts.get(parts.size() - 2);
		String identifier = parts.get(parts.size() - 1);
		String friendlyURL = ListUtil.toString(
			ListUtil.subList(parts, 0, parts.size() - 2), StringPool.BLANK,
			StringPool.SLASH);

		return new String[] {friendlyURL, classNameId, identifier};
	}

}
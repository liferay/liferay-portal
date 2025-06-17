/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.cache.CacheField;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.PropsValues;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class DDMTemplateImpl extends DDMTemplateBaseImpl {

	@Override
	public String getDefaultLanguageId() {
		Document document = null;

		try {
			document = SAXReaderUtil.read(getName());

			if (document != null) {
				Element rootElement = document.getRootElement();

				return rootElement.attributeValue("default-locale");
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		Locale locale = LocaleUtil.getSiteDefault();

		return locale.toString();
	}

	@Override
	public DDMTemplateVersion getLatestTemplateVersion()
		throws PortalException {

		return DDMTemplateVersionLocalServiceUtil.getLatestTemplateVersion(
			getTemplateId());
	}

	@Override
	public String getResourceClassName() {
		if (_resourceClassName == null) {
			_resourceClassName = PortalUtil.getClassName(
				getResourceClassNameId());

			resourceClassNameUpdateEntityCacheConsumer.accept(
				_resourceClassName);
		}

		return _resourceClassName;
	}

	@Override
	public String getSmallImageType() throws PortalException {
		if ((_smallImageType == null) && isSmallImage()) {
			Image smallImage = ImageLocalServiceUtil.getImage(
				getSmallImageId());

			_smallImageType = smallImage.getType();
		}

		return _smallImageType;
	}

	@Override
	public String getTemplateImageURL(ThemeDisplay themeDisplay) {
		if (!isSmallImage()) {
			return null;
		}

		if (Validator.isNotNull(getSmallImageURL())) {
			return getSmallImageURL();
		}

		return StringBundler.concat(
			themeDisplay.getPathImage(), "/template?img_id=", getSmallImageId(),
			"&t=", WebServerServletTokenUtil.getToken(getSmallImageId()));
	}

	@Override
	public DDMTemplateVersion getTemplateVersion() throws PortalException {
		return DDMTemplateVersionLocalServiceUtil.getTemplateVersion(
			getTemplateId(), getVersion());
	}

	/**
	 * Returns the WebDAV URL to access the template.
	 *
	 * @param  themeDisplay the theme display needed to build the URL. It can
	 *         set HTTPS access, the server name, the server port, the path
	 *         context, and the scope group.
	 * @param  webDAVToken the WebDAV token for the URL
	 * @return the WebDAV URL
	 */
	@Override
	public String getWebDavURL(ThemeDisplay themeDisplay, String webDAVToken) {
		StringBundler sb = new StringBundler(8);

		boolean secure = false;

		if (themeDisplay.isSecure() ||
			PropsValues.WEBDAV_SERVLET_HTTPS_REQUIRED) {

			secure = true;
		}

		sb.append(
			PortalUtil.getPortalURL(
				themeDisplay.getServerName(), themeDisplay.getServerPort(),
				secure));
		sb.append(themeDisplay.getPathContext());
		sb.append("/webdav");

		Group group = themeDisplay.getScopeGroup();

		sb.append(group.getFriendlyURL());

		sb.append(StringPool.SLASH);
		sb.append(webDAVToken);
		sb.append("/Templates/");
		sb.append(getTemplateId());

		return sb.toString();
	}

	@Override
	public void setResourceClassName(String resourceClassName) {
		_resourceClassName = resourceClassName;
	}

	@Override
	public void setResourceClassNameId(long resourceClassNameId) {
		super.setResourceClassNameId(resourceClassNameId);

		_resourceClassName = null;
	}

	@Override
	public void setSmallImageType(String smallImageType) {
		_smallImageType = smallImageType;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTemplateImpl.class);

	@CacheField(propagateToInterface = true)
	private String _resourceClassName;

	private String _smallImageType;

}
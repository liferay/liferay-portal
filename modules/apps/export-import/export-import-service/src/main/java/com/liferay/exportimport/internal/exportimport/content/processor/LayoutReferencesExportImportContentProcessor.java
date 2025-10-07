/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.exportimport.content.processor;

import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.configuration.ExportImportServiceConfigurationWhitelistedURLPatternsHelper;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.exception.ExportImportContentProcessorException;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PortalInstances;
import com.liferay.staging.StagingGroupHelper;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	configurationPid = "com.liferay.exportimport.configuration.ExportImportServiceConfiguration",
	property = "content.processor.type=LayoutReferences",
	service = ExportImportContentProcessor.class
)
public class LayoutReferencesExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content, boolean exportReferencedContent,
			boolean escapeContent)
		throws Exception {

		return replaceExportLayoutReferences(
			portletDataContext, stagedModel, content);
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		return replaceImportLayoutReferences(portletDataContext, content);
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		validateLayoutReferences(groupId, content);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_exportImportServiceConfiguration = ConfigurableUtil.createConfigurable(
			ExportImportServiceConfiguration.class, properties);
	}

	protected String replaceExportHostname(
			Group group, String url, StringBundler urlSB)
		throws PortalException {

		if (!HttpComponentsUtil.hasProtocol(url)) {
			return url;
		}

		boolean secure = HttpComponentsUtil.isSecure(url);

		int serverPort = _portal.getPortalServerPort(secure);

		if (serverPort == -1) {
			return url;
		}

		LayoutSet publicLayoutSet = group.getPublicLayoutSet();

		NavigableMap<String, String> publicLayoutSetVirtualHostnames =
			publicLayoutSet.getVirtualHostnames();

		String portalURL = StringPool.BLANK;

		if (!publicLayoutSetVirtualHostnames.isEmpty()) {
			portalURL = _portal.getPortalURL(
				publicLayoutSetVirtualHostnames.firstKey(), serverPort, secure);

			if (url.startsWith(portalURL)) {
				if (secure) {
					urlSB.append(_DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL);
				}
				else {
					urlSB.append(_DATA_HANDLER_PUBLIC_LAYOUT_SET_URL);
				}

				return url.substring(portalURL.length());
			}
		}

		LayoutSet privateLayoutSet = group.getPrivateLayoutSet();

		NavigableMap<String, String> privateLayoutSetVirtualHostnames =
			privateLayoutSet.getVirtualHostnames();

		if (!privateLayoutSetVirtualHostnames.isEmpty()) {
			portalURL = _portal.getPortalURL(
				privateLayoutSetVirtualHostnames.firstKey(), serverPort,
				secure);

			if (url.startsWith(portalURL)) {
				if (secure) {
					urlSB.append(_DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL);
				}
				else {
					urlSB.append(_DATA_HANDLER_PRIVATE_LAYOUT_SET_URL);
				}

				return url.substring(portalURL.length());
			}
		}

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		String companyVirtualHostname = company.getVirtualHostname();

		if (Validator.isNotNull(companyVirtualHostname)) {
			portalURL = _getPortalURL(
				url,
				_portal.getPortalURL(
					companyVirtualHostname, serverPort, secure));

			if (url.startsWith(portalURL)) {
				if (_isDefaultGroup(group)) {
					if (secure) {
						urlSB.append(
							_DATA_HANDLER_COMPANY_SECURE_DEFAULT_GROUP_URL);
					}
					else {
						urlSB.append(_DATA_HANDLER_COMPANY_DEFAULT_GROUP_URL);
					}
				}
				else if (secure) {
					urlSB.append(_DATA_HANDLER_COMPANY_SECURE_URL);
				}
				else {
					urlSB.append(_DATA_HANDLER_COMPANY_URL);
				}

				return url.substring(portalURL.length());
			}
		}

		portalURL = _portal.getPortalURL("localhost", serverPort, secure);

		if (url.startsWith(portalURL)) {
			return url.substring(portalURL.length());
		}

		return url;
	}

	protected String replaceExportLayoutReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			portletDataContext.getScopeGroupId());

		StringBundler hostnameSB = new StringBundler(2);

		content = replaceExportHostname(group, content, hostnameSB);

		if (hostnameSB.index() > 0) {
			hostnameSB.append(content);

			content = hostnameSB.toString();
		}

		StringBuilder sb = new StringBuilder(content);

		String[] patterns = {"href=", "[[", "{{"};

		int beginPos = -1;
		int endPos = content.length();
		int offset = 0;

		while (true) {
			if (beginPos > -1) {
				endPos = beginPos - 1;
			}

			beginPos = StringUtil.lastIndexOfAny(content, patterns, endPos);

			if (beginPos == -1) {
				break;
			}

			if (content.startsWith("href=", beginPos)) {
				offset = 5;

				char c = content.charAt(beginPos + offset);

				if (c == CharPool.BACK_SLASH) {
					offset = 7;
				}
				else if ((c == CharPool.APOSTROPHE) || (c == CharPool.QUOTE)) {
					offset++;
				}
			}
			else if ((content.charAt(beginPos) == CharPool.OPEN_BRACKET) ||
					 (content.charAt(beginPos) == CharPool.OPEN_CURLY_BRACE)) {

				offset = 2;
			}

			if (content.startsWith("href=", beginPos)) {
				endPos = StringUtil.indexOfAny(
					content, _URL_REFERENCE_STOP_CHARS, beginPos + offset,
					endPos);
			}
			else {
				endPos = StringUtil.indexOfAny(
					content, _LAYOUT_REFERENCE_STOP_CHARS, beginPos + offset,
					endPos);
			}

			if (endPos == -1) {
				continue;
			}

			String url = content.substring(beginPos + offset, endPos);

			int pos = url.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

			if (pos != -1) {
				url = url.substring(0, pos);

				endPos = beginPos + offset + pos;
			}

			if (url.endsWith(StringPool.SLASH)) {
				url = url.substring(0, url.length() - 1);

				endPos--;
			}

			StringBundler urlSB = new StringBundler(6);

			try {
				url = replaceExportHostname(group, url, urlSB);

				if (!url.startsWith(StringPool.SLASH)) {
					continue;
				}

				String pathContext = _portal.getPathContext();

				if (pathContext.length() > 1) {
					if (!url.startsWith(pathContext)) {
						continue;
					}

					urlSB.append(_DATA_HANDLER_PATH_CONTEXT);

					url = url.substring(pathContext.length());
				}

				pos = url.indexOf(StringPool.SLASH, 1);

				if (pos == -1) {
					pos = url.length();
				}

				Locale locale = null;

				String localePath = url.substring(0, pos);

				if (localePath.length() > 1) {
					locale = LocaleUtil.fromLanguageId(
						localePath.substring(1), true, false);
				}

				if (locale != null) {
					String urlWithoutLocale = url.substring(
						localePath.length());

					if (urlWithoutLocale.startsWith(
							_PRIVATE_GROUP_SERVLET_MAPPING) ||
						urlWithoutLocale.startsWith(
							_PRIVATE_USER_SERVLET_MAPPING) ||
						urlWithoutLocale.startsWith(
							_PUBLIC_GROUP_SERVLET_MAPPING)) {

						urlSB.append(localePath);

						url = urlWithoutLocale;
					}
					else if ((urlWithoutLocale.indexOf(StringPool.SLASH, 1) ==
								-1) &&
							 !localePath.equals(
								 _PRIVATE_GROUP_SERVLET_MAPPING) &&
							 !localePath.equals(
								 _PRIVATE_USER_SERVLET_MAPPING) &&
							 !localePath.equals(
								 _PUBLIC_GROUP_SERVLET_MAPPING)) {

						urlSB.append(localePath);

						url = urlWithoutLocale;
					}
					else {
						Layout layout =
							_layoutLocalService.fetchLayoutByFriendlyURL(
								group.getGroupId(), false, urlWithoutLocale);

						if (layout == null) {
							layout =
								_layoutLocalService.fetchLayoutByFriendlyURL(
									group.getGroupId(), true, urlWithoutLocale);
						}

						if (layout != null) {
							urlSB.append(localePath);

							url = urlWithoutLocale;
						}
					}
				}

				if (!url.startsWith(StringPool.SLASH)) {
					continue;
				}

				boolean privateLayout = false;

				if (url.startsWith(_PRIVATE_GROUP_SERVLET_MAPPING)) {
					urlSB.append(_DATA_HANDLER_PRIVATE_GROUP_SERVLET_MAPPING);

					url = url.substring(
						_PRIVATE_GROUP_SERVLET_MAPPING.length() - 1);

					privateLayout = true;
				}
				else if (url.startsWith(_PRIVATE_USER_SERVLET_MAPPING)) {
					urlSB.append(_DATA_HANDLER_PRIVATE_USER_SERVLET_MAPPING);

					url = url.substring(
						_PRIVATE_USER_SERVLET_MAPPING.length() - 1);

					privateLayout = true;
				}
				else if (url.startsWith(_PUBLIC_GROUP_SERVLET_MAPPING)) {
					urlSB.append(_DATA_HANDLER_PUBLIC_SERVLET_MAPPING);

					url = url.substring(
						_PUBLIC_GROUP_SERVLET_MAPPING.length() - 1);
				}
				else {
					String urlSBString = urlSB.toString();

					LayoutSet layoutSet = null;

					if (urlSBString.contains(
							_DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL) ||
						urlSBString.contains(
							_DATA_HANDLER_PUBLIC_LAYOUT_SET_URL)) {

						layoutSet = group.getPublicLayoutSet();
					}
					else if (urlSBString.contains(
								_DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL) ||
							 urlSBString.contains(
								 _DATA_HANDLER_PRIVATE_LAYOUT_SET_URL)) {

						layoutSet = group.getPrivateLayoutSet();
					}
					else if (urlSBString.contains(
								_DATA_HANDLER_COMPANY_SECURE_DEFAULT_GROUP_URL) ||
							 urlSBString.contains(
								 _DATA_HANDLER_COMPANY_DEFAULT_GROUP_URL)) {

						layoutSet = group.getPublicLayoutSet();
					}
					else {
						LayoutSet publicLayoutSet = group.getPublicLayoutSet();

						NavigableMap<String, String> publicVirtualHostnames =
							publicLayoutSet.getVirtualHostnames();

						if (!publicVirtualHostnames.isEmpty() ||
							_isDefaultGroup(group)) {

							layoutSet = group.getPublicLayoutSet();
						}
					}

					if (layoutSet == null) {
						continue;
					}

					privateLayout = layoutSet.isPrivateLayout();

					LayoutFriendlyURL layoutFriendlyURL =
						_layoutFriendlyURLLocalService.
							fetchFirstLayoutFriendlyURL(
								group.getGroupId(), privateLayout, url);

					if (layoutFriendlyURL == null) {
						continue;
					}

					if (privateLayout) {
						urlSB.append(
							_DATA_HANDLER_VIRTUAL_HOST_PRIVATE_LAYOUT_FRIENDLY_URL);
					}
					else {
						urlSB.append(
							_DATA_HANDLER_VIRTUAL_HOST_PUBLIC_LAYOUT_FRIENDLY_URL);
					}

					continue;
				}

				Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
					group.getGroupId(), privateLayout, url);

				if (layout != null) {
					Element entityElement =
						portletDataContext.getExportDataElement(stagedModel);

					portletDataContext.addReferenceElement(
						stagedModel, entityElement, layout,
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

					continue;
				}

				pos = url.indexOf(StringPool.SLASH, 1);

				String groupFriendlyURL = url;

				if (pos != -1) {
					groupFriendlyURL = url.substring(0, pos);
				}

				Group urlGroup = _groupLocalService.fetchFriendlyURLGroup(
					group.getCompanyId(), groupFriendlyURL);

				if (urlGroup == null) {
					throw new NoSuchLayoutException();
				}

				urlSB.append(_DATA_HANDLER_GROUP_FRIENDLY_URL);

				// Append the UUID. This information will be used during the
				// import process when looking up the proper group for the link.

				urlSB.append(StringPool.AT);

				if (urlGroup.isStagedRemotely()) {
					String remoteGroupUuid = urlGroup.getTypeSettingsProperty(
						"remoteGroupUUID");

					if (Validator.isNotNull(remoteGroupUuid)) {
						urlSB.append(remoteGroupUuid);
					}
				}
				else if (_stagingGroupHelper.isStagingGroup(urlGroup)) {
					Group liveGroup = urlGroup.getLiveGroup();

					urlSB.append(liveGroup.getUuid());
				}
				else if (urlGroup.isControlPanel() ||
						 (_stagingGroupHelper.isLiveGroup(urlGroup) &&
						  (group.getLiveGroupId() == urlGroup.getGroupId()))) {

					urlSB.append(urlGroup.getUuid());
				}
				else {
					urlSB.append(urlGroup.getFriendlyURL());
				}

				urlSB.append(StringPool.AT);

				String siteAdminURL =
					GroupConstants.CONTROL_PANEL_FRIENDLY_URL +
						PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL;

				if (url.endsWith(siteAdminURL)) {
					urlSB.append(_DATA_HANDLER_SITE_ADMIN_URL);

					url = StringPool.BLANK;

					continue;
				}

				if (pos == -1) {
					url = StringPool.BLANK;

					continue;
				}

				url = url.substring(pos);

				layout = _layoutLocalService.getFriendlyURLLayout(
					urlGroup.getGroupId(), privateLayout, url);

				Element entityElement = portletDataContext.getExportDataElement(
					stagedModel);

				portletDataContext.addReferenceElement(
					stagedModel, entityElement, layout,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
			}
			catch (Exception exception) {
				if (((exception instanceof NoSuchLayoutException) &&
					 !_exportImportServiceConfiguration.
						 validateLayoutReferences()) ||
					_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
						isWhitelistedURL(
							CompanyThreadLocal.getCompanyId(), url)) {

					continue;
				}

				StringBundler exceptionSB = new StringBundler(6);

				exceptionSB.append("Unable to process layout URL ");
				exceptionSB.append(url);
				exceptionSB.append(" for staged model ");
				exceptionSB.append(stagedModel.getModelClassName());
				exceptionSB.append(" with primary key ");
				exceptionSB.append(stagedModel.getPrimaryKeyObj());

				ExportImportContentProcessorException
					exportImportContentProcessorException =
						new ExportImportContentProcessorException(
							exceptionSB.toString(), exception);

				if (_log.isDebugEnabled()) {
					_log.debug(
						exceptionSB.toString(),
						exportImportContentProcessorException);
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(exceptionSB.toString());
				}
			}
			finally {
				if (urlSB.length() > 0) {
					urlSB.append(url);

					url = urlSB.toString();
				}

				sb.replace(beginPos + offset, endPos, url);
			}
		}

		return sb.toString();
	}

	protected String replaceImportLayoutReferences(
			PortletDataContext portletDataContext, String content)
		throws Exception {

		String companyDefaultGroupPortalURL = StringPool.BLANK;
		String companyPortalURL = StringPool.BLANK;
		String privateLayoutSetPortalURL = StringPool.BLANK;
		String publicLayoutSetPortalURL = StringPool.BLANK;

		Group group = _groupLocalService.getGroup(
			portletDataContext.getScopeGroupId());

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		LayoutSet privateLayoutSet = group.getPrivateLayoutSet();
		LayoutSet publicLayoutSet = group.getPublicLayoutSet();

		int serverPort = _portal.getPortalServerPort(false);

		if (serverPort != -1) {
			if (Validator.isNotNull(company.getVirtualHostname())) {
				companyPortalURL = _portal.getPortalURL(
					company.getVirtualHostname(), serverPort, false);
			}

			NavigableMap<String, String> privateVirtualHostnames =
				privateLayoutSet.getVirtualHostnames();

			if (!privateVirtualHostnames.isEmpty()) {
				privateLayoutSetPortalURL = _portal.getPortalURL(
					privateVirtualHostnames.firstKey(), serverPort, false);
			}
			else {
				privateLayoutSetPortalURL = companyPortalURL;
			}

			NavigableMap<String, String> publicVirtualHostnames =
				publicLayoutSet.getVirtualHostnames();

			if (!publicVirtualHostnames.isEmpty()) {
				publicLayoutSetPortalURL = _portal.getPortalURL(
					publicVirtualHostnames.firstKey(), serverPort, false);
			}
			else {
				publicLayoutSetPortalURL = companyPortalURL;
			}

			if (_isDefaultGroup(group)) {
				companyDefaultGroupPortalURL = companyPortalURL;
			}
			else {
				companyDefaultGroupPortalURL = publicLayoutSetPortalURL;
			}
		}

		int secureSecurePort = _portal.getPortalServerPort(true);

		String companySecureDefaultGroupPortalURL = StringPool.BLANK;
		String companySecurePortalURL = StringPool.BLANK;
		String privateLayoutSetSecurePortalURL = StringPool.BLANK;
		String publicLayoutSetSecurePortalURL = StringPool.BLANK;

		if (secureSecurePort != -1) {
			if (Validator.isNotNull(company.getVirtualHostname())) {
				companySecurePortalURL = _portal.getPortalURL(
					company.getVirtualHostname(), secureSecurePort, true);
			}

			NavigableMap<String, String> privateVirtualHostnames =
				privateLayoutSet.getVirtualHostnames();

			if (!privateVirtualHostnames.isEmpty()) {
				privateLayoutSetSecurePortalURL = _portal.getPortalURL(
					privateVirtualHostnames.firstKey(), secureSecurePort, true);
			}

			NavigableMap<String, String> publicVirtualHostnames =
				publicLayoutSet.getVirtualHostnames();

			if (!publicVirtualHostnames.isEmpty()) {
				publicLayoutSetSecurePortalURL = _portal.getPortalURL(
					publicVirtualHostnames.firstKey(), secureSecurePort, true);
			}

			if (_isDefaultGroup(group)) {
				companySecureDefaultGroupPortalURL = companySecurePortalURL;
			}
			else {
				companySecureDefaultGroupPortalURL =
					publicLayoutSetSecurePortalURL;
			}
		}

		content = StringUtil.replace(
			content, _DATA_HANDLER_COMPANY_DEFAULT_GROUP_URL,
			companyDefaultGroupPortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_COMPANY_SECURE_DEFAULT_GROUP_URL,
			companySecureDefaultGroupPortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_COMPANY_SECURE_URL, companySecurePortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_COMPANY_URL, companyPortalURL);

		String virtualHostPrivateLayoutFriendlyURLReplacement =
			StringPool.BLANK;
		String virtualHostPublicLayoutFriendlyURLReplacement = StringPool.BLANK;

		NavigableMap<String, String> privateVirtualHostnames =
			privateLayoutSet.getVirtualHostnames();

		if (privateVirtualHostnames.isEmpty()) {
			if (group.isUser()) {
				virtualHostPrivateLayoutFriendlyURLReplacement =
					PropsValues.
						LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;
			}
			else {
				virtualHostPrivateLayoutFriendlyURLReplacement =
					PropsValues.
						LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING;
			}

			virtualHostPrivateLayoutFriendlyURLReplacement +=
				group.getFriendlyURL();
		}

		NavigableMap<String, String> publicVirtualHostnames =
			publicLayoutSet.getVirtualHostnames();

		if (publicVirtualHostnames.isEmpty() && !_isDefaultGroup(group)) {
			virtualHostPublicLayoutFriendlyURLReplacement =
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING +
					group.getFriendlyURL();
		}

		// Group friendly URLs

		while (true) {
			int groupFriendlyUrlPos = content.indexOf(
				_DATA_HANDLER_GROUP_FRIENDLY_URL);

			if (groupFriendlyUrlPos == -1) {
				break;
			}

			int groupUuidPos =
				groupFriendlyUrlPos + _DATA_HANDLER_GROUP_FRIENDLY_URL.length();

			int endIndex = -1;

			if (content.charAt(groupUuidPos) == CharPool.AT) {
				endIndex = content.indexOf(StringPool.AT, groupUuidPos + 1);
			}
			else {
				content = StringUtil.replaceFirst(
					content, _DATA_HANDLER_GROUP_FRIENDLY_URL,
					group.getFriendlyURL(), groupFriendlyUrlPos);

				continue;
			}

			if (endIndex < (groupUuidPos + 1)) {
				content = StringUtil.replaceFirst(
					content, _DATA_HANDLER_GROUP_FRIENDLY_URL, StringPool.BLANK,
					groupFriendlyUrlPos);

				continue;
			}

			String groupUuid = content.substring(groupUuidPos + 1, endIndex);

			Group groupFriendlyUrlGroup =
				_groupLocalService.fetchGroupByUuidAndCompanyId(
					groupUuid, portletDataContext.getCompanyId());

			if (groupFriendlyUrlGroup == null) {
				groupFriendlyUrlGroup =
					_groupLocalService.fetchFriendlyURLGroup(
						portletDataContext.getCompanyId(), groupUuid);
			}

			if ((groupFriendlyUrlGroup == null) ||
				groupUuid.contains(_TEMPLATE_NAME_PREFIX)) {

				content = StringUtil.replaceFirst(
					content, _DATA_HANDLER_GROUP_FRIENDLY_URL,
					group.getFriendlyURL(), groupFriendlyUrlPos);
				content = StringUtil.replaceFirst(
					content, StringPool.AT + groupUuid + StringPool.AT,
					StringPool.BLANK, groupFriendlyUrlPos);

				if (groupUuid.contains(_TEMPLATE_NAME_PREFIX)) {
					content = _replaceTemplateLinkToLayout(
						content, portletDataContext.isPrivateLayout());
				}

				continue;
			}

			content = StringUtil.replaceFirst(
				content, _DATA_HANDLER_GROUP_FRIENDLY_URL, StringPool.BLANK,
				groupFriendlyUrlPos);
			content = StringUtil.replaceFirst(
				content, StringPool.AT + groupUuid + StringPool.AT,
				groupFriendlyUrlGroup.getFriendlyURL(), groupFriendlyUrlPos);
		}

		content = StringUtil.replace(
			content, _DATA_HANDLER_PATH_CONTEXT, _portal.getPathContext());
		content = StringUtil.replace(
			content, _DATA_HANDLER_PRIVATE_GROUP_SERVLET_MAPPING,
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL,
			privateLayoutSetSecurePortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PRIVATE_LAYOUT_SET_URL,
			privateLayoutSetPortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PRIVATE_USER_SERVLET_MAPPING,
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL,
			publicLayoutSetSecurePortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PUBLIC_LAYOUT_SET_URL,
			publicLayoutSetPortalURL);
		content = StringUtil.replace(
			content, _DATA_HANDLER_PUBLIC_SERVLET_MAPPING,
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING);
		content = StringUtil.replace(
			content, _DATA_HANDLER_SITE_ADMIN_URL,
			StringBundler.concat(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
				GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
				PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL));
		content = StringUtil.replace(
			content, _DATA_HANDLER_VIRTUAL_HOST_PRIVATE_LAYOUT_FRIENDLY_URL,
			virtualHostPrivateLayoutFriendlyURLReplacement);
		content = StringUtil.replace(
			content, _DATA_HANDLER_VIRTUAL_HOST_PUBLIC_LAYOUT_FRIENDLY_URL,
			virtualHostPublicLayoutFriendlyURLReplacement);

		return content;
	}

	protected void validateLayoutReferences(long groupId, String content)
		throws PortalException {

		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			_exportImportServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ExportImportServiceConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		if (!_exportImportServiceConfiguration.validateLayoutReferences()) {
			return;
		}

		Group group = _groupLocalService.getGroup(groupId);

		String[] patterns = {"href=", "[[", "{{"};

		int beginPos = -1;
		int endPos = content.length();
		int offset = 0;

		while (true) {
			if (beginPos > -1) {
				endPos = beginPos - 1;
			}

			beginPos = StringUtil.lastIndexOfAny(content, patterns, endPos);

			if (beginPos == -1) {
				break;
			}

			if (content.startsWith("href=", beginPos)) {
				offset = 5;

				char c = content.charAt(beginPos + offset);

				if (c == CharPool.BACK_SLASH) {
					offset = 7;
				}
				else if ((c == CharPool.APOSTROPHE) || (c == CharPool.QUOTE)) {
					offset++;
				}
			}
			else if ((content.charAt(beginPos) == CharPool.OPEN_BRACKET) ||
					 (content.charAt(beginPos) == CharPool.OPEN_CURLY_BRACE)) {

				offset = 2;
			}

			if (content.startsWith("href=", beginPos)) {
				endPos = StringUtil.indexOfAny(
					content, _URL_REFERENCE_STOP_CHARS, beginPos + offset,
					endPos);
			}
			else {
				endPos = StringUtil.indexOfAny(
					content, _LAYOUT_REFERENCE_STOP_CHARS, beginPos + offset,
					endPos);
			}

			if (endPos == -1) {
				continue;
			}

			String url = content.substring(beginPos + offset, endPos + 1);

			if (url.contains("/c/document_library/get_file?") ||
				url.contains("/documents/") ||
				url.contains("/image/image_gallery?")) {

				continue;
			}

			url = content.substring(beginPos + offset, endPos);

			endPos = StringUtil.indexOfAny(
				url,
				ArrayUtil.remove(
					FriendlyURLResolverRegistryUtil.getURLSeparators(),
					VirtualLayoutConstants.CANONICAL_URL_SEPARATOR));

			if (endPos != -1) {
				url = url.substring(0, endPos);
			}

			if (url.endsWith(StringPool.SLASH)) {
				url = url.substring(0, url.length() - 1);
			}

			StringBundler urlSB = new StringBundler(1);

			url = replaceExportHostname(group, url, urlSB);

			if (!url.startsWith(StringPool.SLASH) ||
				PortalInstances.isVirtualHostsIgnorePath(url)) {

				continue;
			}

			String pathContext = _portal.getPathContext();

			if (pathContext.length() > 1) {
				if (!url.startsWith(pathContext)) {
					continue;
				}

				url = url.substring(pathContext.length());
			}

			int pos = url.indexOf(StringPool.SLASH, 1);

			if (pos == -1) {
				pos = url.length();
			}

			Locale locale = null;

			String localePath = url.substring(0, pos);

			if (localePath.length() > 1) {
				locale = LocaleUtil.fromLanguageId(
					localePath.substring(1), true, false);
			}

			if (locale != null) {
				String urlWithoutLocale = url.substring(localePath.length());

				if (urlWithoutLocale.startsWith(
						_PRIVATE_GROUP_SERVLET_MAPPING) ||
					urlWithoutLocale.startsWith(
						_PRIVATE_USER_SERVLET_MAPPING) ||
					urlWithoutLocale.startsWith(
						_PUBLIC_GROUP_SERVLET_MAPPING) ||
					_isVirtualHostDefined(urlSB)) {

					url = urlWithoutLocale;
				}
				else if ((urlWithoutLocale.indexOf(StringPool.SLASH, 1) ==
							-1) &&
						 !localePath.equals(_PRIVATE_GROUP_SERVLET_MAPPING) &&
						 !localePath.equals(_PRIVATE_USER_SERVLET_MAPPING) &&
						 !localePath.equals(_PUBLIC_GROUP_SERVLET_MAPPING)) {

					urlSB.append(localePath);

					url = urlWithoutLocale;
				}
				else {
					Layout layout =
						_layoutLocalService.fetchLayoutByFriendlyURL(
							group.getGroupId(), false, urlWithoutLocale);

					if (layout == null) {
						layout = _layoutLocalService.fetchLayoutByFriendlyURL(
							group.getGroupId(), true, urlWithoutLocale);
					}

					if (layout != null) {
						urlSB.append(localePath);

						url = urlWithoutLocale;
					}
				}
			}

			if (!url.startsWith(StringPool.SLASH) ||
				_exportImportServiceConfigurationWhitelistedURLPatternsHelper.
					isWhitelistedURL(companyId, url)) {

				continue;
			}

			boolean privateLayout = false;

			if (url.startsWith(_PRIVATE_GROUP_SERVLET_MAPPING)) {
				url = url.substring(
					_PRIVATE_GROUP_SERVLET_MAPPING.length() - 1);

				privateLayout = true;
			}
			else if (url.startsWith(_PRIVATE_USER_SERVLET_MAPPING)) {
				url = url.substring(_PRIVATE_USER_SERVLET_MAPPING.length() - 1);

				privateLayout = true;
			}
			else if (url.startsWith(_PUBLIC_GROUP_SERVLET_MAPPING)) {
				url = url.substring(_PUBLIC_GROUP_SERVLET_MAPPING.length() - 1);
			}
			else {
				String urlSBString = urlSB.toString();

				LayoutSet layoutSet = null;

				if (urlSBString.contains(
						_DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL) ||
					urlSBString.contains(_DATA_HANDLER_PUBLIC_LAYOUT_SET_URL)) {

					layoutSet = group.getPublicLayoutSet();
				}
				else if (urlSBString.contains(
							_DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL) ||
						 urlSBString.contains(
							 _DATA_HANDLER_PRIVATE_LAYOUT_SET_URL)) {

					layoutSet = group.getPrivateLayoutSet();
				}
				else if (urlSBString.contains(
							_DATA_HANDLER_COMPANY_SECURE_DEFAULT_GROUP_URL) ||
						 urlSBString.contains(
							 _DATA_HANDLER_COMPANY_DEFAULT_GROUP_URL)) {

					layoutSet = group.getPublicLayoutSet();
				}
				else {
					LayoutSet publicLayoutSet = group.getPublicLayoutSet();

					NavigableMap<String, String> publicVirtualHostnames =
						publicLayoutSet.getVirtualHostnames();

					if (!publicVirtualHostnames.isEmpty() ||
						_isDefaultGroup(group)) {

						layoutSet = group.getPublicLayoutSet();
					}
				}

				if (layoutSet == null) {
					continue;
				}

				privateLayout = layoutSet.isPrivateLayout();
			}

			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				groupId, privateLayout, url);

			if (layout != null) {
				continue;
			}

			String siteAdminURL =
				GroupConstants.CONTROL_PANEL_FRIENDLY_URL +
					PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL;

			if (url.endsWith(
					VirtualLayoutConstants.CANONICAL_URL_SEPARATOR +
						siteAdminURL)) {

				url = url.substring(url.indexOf(siteAdminURL));
			}

			pos = url.indexOf(StringPool.SLASH, 1);

			String groupFriendlyURL = url;

			if (pos != -1) {
				groupFriendlyURL = url.substring(0, pos);
			}

			Group urlGroup = _groupLocalService.fetchFriendlyURLGroup(
				group.getCompanyId(), groupFriendlyURL);

			if (urlGroup == null) {
				ExportImportContentValidationException
					exportImportContentValidationException =
						new ExportImportContentValidationException(
							LayoutReferencesExportImportContentProcessor.class.
								getName());

				exportImportContentValidationException.setGroupFriendlyURL(
					groupFriendlyURL);
				exportImportContentValidationException.setLayoutURL(url);
				exportImportContentValidationException.setType(
					ExportImportContentValidationException.
						LAYOUT_GROUP_NOT_FOUND);

				throw exportImportContentValidationException;
			}

			if (pos == -1) {
				continue;
			}

			url = url.substring(pos);

			try {
				_layoutLocalService.getFriendlyURLLayout(
					urlGroup.getGroupId(), privateLayout, url);
			}
			catch (NoSuchLayoutException noSuchLayoutException) {
				ExportImportContentValidationException
					exportImportContentValidationException =
						new ExportImportContentValidationException(
							LayoutReferencesExportImportContentProcessor.class.
								getName(),
							noSuchLayoutException);

				exportImportContentValidationException.setLayoutURL(url);
				exportImportContentValidationException.setType(
					ExportImportContentValidationException.
						LAYOUT_WITH_URL_NOT_FOUND);

				throw exportImportContentValidationException;
			}
		}
	}

	private String _getPortalURL(String url, String portalURL)
		throws PortalException {

		try {
			URI uri = null;

			try {
				uri = HttpComponentsUtil.getURI(url);
			}
			catch (URISyntaxException uriSyntaxException) {
				if (_log.isDebugEnabled()) {
					_log.debug(uriSyntaxException);
				}
			}

			if ((uri != null) && Validator.isIPAddress(uri.getHost())) {
				InetAddress inetAddress = InetAddressUtil.getInetAddressByName(
					uri.getHost());

				if ((inetAddress != null) &&
					InetAddressUtil.isLocalInetAddress(inetAddress)) {

					return StringBundler.concat(
						uri.getScheme(), "://", uri.getHost(), StringPool.COLON,
						uri.getPort());
				}
			}
		}
		catch (UnknownHostException unknownHostException) {
			if (_log.isWarnEnabled()) {
				_log.warn(unknownHostException);
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		return portalURL;
	}

	private boolean _isDefaultGroup(Group group) {
		return StringUtil.equals(
			group.getGroupKey(), PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);
	}

	private boolean _isVirtualHostDefined(StringBundler urlSB) {
		String urlSBString = urlSB.toString();

		if (urlSBString.contains(_DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL) ||
			urlSBString.contains(_DATA_HANDLER_PUBLIC_LAYOUT_SET_URL) ||
			urlSBString.contains(_DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL) ||
			urlSBString.contains(_DATA_HANDLER_PRIVATE_LAYOUT_SET_URL)) {

			return true;
		}

		return false;
	}

	private String _replaceTemplateLinkToLayout(
		String content, boolean privateLayout) {

		if (privateLayout) {
			content = StringUtil.replace(
				content, _DATA_HANDLER_PRIVATE_GROUP_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING);
		}
		else {
			content = StringUtil.replace(
				content, _DATA_HANDLER_PRIVATE_GROUP_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING);
		}

		return content;
	}

	private static final String _DATA_HANDLER_COMPANY_DEFAULT_GROUP_URL =
		"@data_handler_company_default_group_url@";

	private static final String _DATA_HANDLER_COMPANY_SECURE_DEFAULT_GROUP_URL =
		"@data_handler_company_secure_default_group_url@";

	private static final String _DATA_HANDLER_COMPANY_SECURE_URL =
		"@data_handler_company_secure_url@";

	private static final String _DATA_HANDLER_COMPANY_URL =
		"@data_handler_company_url@";

	private static final String _DATA_HANDLER_GROUP_FRIENDLY_URL =
		"@data_handler_group_friendly_url@";

	private static final String _DATA_HANDLER_PATH_CONTEXT =
		"@data_handler_path_context@";

	private static final String _DATA_HANDLER_PRIVATE_GROUP_SERVLET_MAPPING =
		"@data_handler_private_group_servlet_mapping@";

	private static final String _DATA_HANDLER_PRIVATE_LAYOUT_SET_SECURE_URL =
		"@data_handler_private_layout_set_secure_url@";

	private static final String _DATA_HANDLER_PRIVATE_LAYOUT_SET_URL =
		"@data_handler_private_layout_set_url@";

	private static final String _DATA_HANDLER_PRIVATE_USER_SERVLET_MAPPING =
		"@data_handler_private_user_servlet_mapping@";

	private static final String _DATA_HANDLER_PUBLIC_LAYOUT_SET_SECURE_URL =
		"@data_handler_public_layout_set_secure_url@";

	private static final String _DATA_HANDLER_PUBLIC_LAYOUT_SET_URL =
		"@data_handler_public_layout_set_url@";

	private static final String _DATA_HANDLER_PUBLIC_SERVLET_MAPPING =
		"@data_handler_public_servlet_mapping@";

	private static final String _DATA_HANDLER_SITE_ADMIN_URL =
		"@data_handler_site_admin_url@";

	private static final String
		_DATA_HANDLER_VIRTUAL_HOST_PRIVATE_LAYOUT_FRIENDLY_URL =
			"@data_handler_virtual_host_private_layout_friendly_url@";

	private static final String
		_DATA_HANDLER_VIRTUAL_HOST_PUBLIC_LAYOUT_FRIENDLY_URL =
			"@data_handler_virtual_host_public_layout_friendly_url@";

	private static final char[] _LAYOUT_REFERENCE_STOP_CHARS = {
		CharPool.APOSTROPHE, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.CLOSE_PARENTHESIS, CharPool.GREATER_THAN, CharPool.LESS_THAN,
		CharPool.PIPE, CharPool.POUND, CharPool.QUESTION, CharPool.QUOTE,
		CharPool.SPACE
	};

	private static final String _PRIVATE_GROUP_SERVLET_MAPPING =
		PropsUtil.get(
			PropsKeys.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING) +
				StringPool.SLASH;

	private static final String _PRIVATE_USER_SERVLET_MAPPING =
		PropsUtil.get(
			PropsKeys.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING) +
				StringPool.SLASH;

	private static final String _PUBLIC_GROUP_SERVLET_MAPPING =
		PropsUtil.get(PropsKeys.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING) +
			StringPool.SLASH;

	private static final String _TEMPLATE_NAME_PREFIX = "template";

	private static final char[] _URL_REFERENCE_STOP_CHARS = {
		CharPool.APOSTROPHE, CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET,
		CharPool.GREATER_THAN, CharPool.PIPE, CharPool.POUND, CharPool.QUESTION,
		CharPool.QUOTE, CharPool.SPACE
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutReferencesExportImportContentProcessor.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile ExportImportServiceConfiguration
		_exportImportServiceConfiguration;

	@Reference
	private ExportImportServiceConfigurationWhitelistedURLPatternsHelper
		_exportImportServiceConfigurationWhitelistedURLPatternsHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.manager;

import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.provider.SitemapURLProvider;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Vilmos Papp
 */
@Component(service = SitemapManager.class)
public class SitemapManagerImpl implements SitemapManager {

	@Override
	public void addURLElement(
		Element element, String url,
		UnicodeProperties typeSettingsUnicodeProperties, Date modifiedDate,
		String canonicalURL, Map<Locale, String> alternateURLs) {

		Element urlElement = element.addElement("url");

		Element locElement = urlElement.addElement("loc");

		locElement.addText(encodeXML(url));

		if (modifiedDate != null) {
			Element modifiedDateElement = urlElement.addElement("lastmod");

			DateFormat iso8601DateFormat = DateUtil.getISO8601Format();

			modifiedDateElement.addText(iso8601DateFormat.format(modifiedDate));
		}

		if (typeSettingsUnicodeProperties == null) {
			if (Validator.isNotNull(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY)) {

				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY);
			}

			if (Validator.isNotNull(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY)) {

				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY);
			}
		}
		else {
			String changefreq = typeSettingsUnicodeProperties.getProperty(
				"sitemap-changefreq");

			if (Validator.isNotNull(changefreq)) {
				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(changefreq);
			}
			else if (Validator.isNotNull(
						PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY)) {

				Element changefreqElement = urlElement.addElement("changefreq");

				changefreqElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_CHANGE_FREQUENCY);
			}

			String priority = typeSettingsUnicodeProperties.getProperty(
				"sitemap-priority");

			if (Validator.isNotNull(priority)) {
				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(priority);
			}
			else if (Validator.isNotNull(
						PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY)) {

				Element priorityElement = urlElement.addElement("priority");

				priorityElement.addText(
					PropsValues.SITES_SITEMAP_DEFAULT_PRIORITY);
			}
		}

		if (alternateURLs != null) {
			for (Map.Entry<Locale, String> entry : alternateURLs.entrySet()) {
				Locale locale = entry.getKey();
				String href = entry.getValue();

				Element alternateURLElement = urlElement.addElement(
					"xhtml:link", "http://www.w3.org/1999/xhtml");

				alternateURLElement.addAttribute("href", href);
				alternateURLElement.addAttribute(
					"hreflang", LocaleUtil.toW3cLanguageId(locale));
				alternateURLElement.addAttribute("rel", "alternate");
			}

			Element alternateURLElement = urlElement.addElement(
				"xhtml:link", "http://www.w3.org/1999/xhtml");

			alternateURLElement.addAttribute("rel", "alternate");
			alternateURLElement.addAttribute("hreflang", "x-default");
			alternateURLElement.addAttribute("href", canonicalURL);
		}

		_removeOldestElement(element, urlElement);
	}

	@Override
	public String encodeXML(String input) {
		return StringUtil.replace(
			input, new char[] {'&', '<', '>', '\'', '\"'},
			new String[] {"&amp;", "&lt;", "&gt;", "&apos;", "&quot;"});
	}

	@Override
	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		return _portal.getAlternateURLs(canonicalURL, themeDisplay, layout);
	}

	@Override
	public String getSitemap(
			long groupId, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getSitemap(null, groupId, privateLayout, themeDisplay);
	}

	@Override
	public String getSitemap(
			String layoutUuid, long groupId, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (Validator.isNull(layoutUuid) &&
			_sitemapConfigurationManager.xmlSitemapIndexCompanyEnabled(
				themeDisplay.getCompanyId())) {

			return _getIndexSitemap(groupId, privateLayout, themeDisplay);
		}

		return _getSitemap(layoutUuid, groupId, privateLayout, themeDisplay);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, SitemapURLProvider.class, null,
			(serviceReference, emitter) -> {
				SitemapURLProvider sitemapURLProvider =
					_bundleContext.getService(serviceReference);

				emitter.emit(sitemapURLProvider.getClassName());
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getIndexSitemap(
			long groupId, boolean privateLayout, ThemeDisplay themeDisplay)
		throws PortalException {

		Document document = _saxReader.createDocument();

		document.setXMLEncoding(StringPool.UTF8);

		Element rootElement = document.addElement(
			"sitemapindex", "http://www.sitemaps.org/schemas/sitemap/0.9");

		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		_initEntriesAndSize(rootElement);

		for (LayoutSet layoutSet :
				_getLayoutSets(groupId, null, privateLayout, themeDisplay)) {

			_visitLayoutSet(rootElement, layoutSet, themeDisplay);
		}

		_removeEntriesAndSize(rootElement);

		return document.asXML();
	}

	private List<LayoutSet> _getLayoutSets(
			long groupId, String layoutUuid, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayout);

		if (Validator.isNotNull(layoutUuid) ||
			!_isCompanyVirtualHostname(themeDisplay)) {

			return ListUtil.fromArray(layoutSet);
		}

		Group group = _groupLocalService.getGroup(groupId);

		if (!group.isGuest()) {
			return ListUtil.fromArray(layoutSet);
		}

		try {
			List<LayoutSet> layoutSets = new ArrayList<>();

			if (MapUtil.isEmpty(layoutSet.getVirtualHostnames())) {
				layoutSets.add(layoutSet);
			}

			layoutSets.addAll(
				TransformUtil.transformToList(
					_sitemapConfigurationManager.getCompanySitemapGroupIds(
						themeDisplay.getCompanyId()),
					curGroupId -> {
						Group curGroup = _groupLocalService.fetchGroup(
							curGroupId);

						if ((curGroup == null) || curGroup.isGuest()) {
							return null;
						}

						LayoutSet curLayoutSet =
							_layoutSetLocalService.getLayoutSet(
								curGroup.getGroupId(), privateLayout);

						if (MapUtil.isNotEmpty(
								curLayoutSet.getVirtualHostnames())) {

							return null;
						}

						return curLayoutSet;
					}));

			return layoutSets;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return Collections.emptyList();
	}

	private String _getSitemap(
			String layoutUuid, long groupId, boolean privateLayout,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Document document = _saxReader.createDocument();

		document.setXMLEncoding(StringPool.UTF8);

		Element rootElement = document.addElement(
			"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

		rootElement.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");
		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		_initEntriesAndSize(rootElement);

		_visitLayoutSets(
			_getLayoutSets(groupId, layoutUuid, privateLayout, themeDisplay),
			layoutUuid, rootElement, themeDisplay);

		if (!rootElement.hasContent()) {
			return StringPool.BLANK;
		}

		_removeEntriesAndSize(rootElement);

		return document.asXML();
	}

	private List<SitemapURLProvider> _getSitemapURLProviders() {
		return TransformUtil.transform(
			_serviceTrackerMap.keySet(),
			className -> _serviceTrackerMap.getService(className));
	}

	private int _getSize(Element element) {
		String string = element.asXML();

		byte[] bytes = string.getBytes();

		int offset = 0;

		String name = element.getName();

		if (name.equals("url")) {
			Set<Locale> availableLocales = _language.getAvailableLocales();

			int availableLocalesSize = availableLocales.size();

			offset = (availableLocalesSize + 1) * _ATTRIBUTE_XHTML.length;

			offset += _ATTRIBUTE_XMLNS.length;
		}

		return bytes.length - offset;
	}

	private void _initEntriesAndSize(Element rootElement) {
		rootElement.addAttribute("entries", "0");

		int size = _getSize(rootElement);

		rootElement.addAttribute("size", String.valueOf(size));
	}

	private boolean _isCompanyVirtualHostname(ThemeDisplay themeDisplay) {
		Company company = themeDisplay.getCompany();

		String virtualHostname = company.getVirtualHostname();

		if (Validator.isNull(virtualHostname)) {
			virtualHostname = "localhost";
		}

		return Objects.equals(virtualHostname, themeDisplay.getServerName());
	}

	private void _removeEntriesAndSize(Element rootElement) {
		Attribute entriesAttribute = rootElement.attribute("entries");
		Attribute sizeAttribute = rootElement.attribute("size");

		if (_log.isDebugEnabled()) {
			StringBundler sb = new StringBundler(4);

			sb.append("Created site map with ");

			if (entriesAttribute != null) {
				sb.append(entriesAttribute.getValue());
			}
			else {
				sb.append("no");
			}

			sb.append(" entries and size ");

			if (sizeAttribute != null) {
				int size = GetterUtil.getInteger(sizeAttribute.getValue());

				sb.append(
					TextFormatter.formatStorageSize(
						size, LocaleUtil.fromLanguageId("en_US")));
			}
			else {
				sb.append("0");
			}

			_log.debug(sb.toString());
		}

		if (entriesAttribute != null) {
			rootElement.remove(entriesAttribute);
		}

		if (sizeAttribute != null) {
			rootElement.remove(sizeAttribute);
		}
	}

	private void _removeOldestElement(Element rootElement, Element newElement) {
		int entries = GetterUtil.getInteger(
			rootElement.attributeValue("entries"));
		int size = GetterUtil.getInteger(rootElement.attributeValue("size"));

		entries++;
		size += _getSize(newElement);

		while ((entries > MAXIMUM_ENTRIES) || (size >= _MAXIMUM_SIZE)) {
			Element oldestUrlElement = rootElement.element(
				newElement.getName());

			entries--;
			size -= _getSize(oldestUrlElement);

			rootElement.remove(oldestUrlElement);
		}

		rootElement.addAttribute("entries", String.valueOf(entries));
		rootElement.addAttribute("size", String.valueOf(size));
	}

	private void _visitLayoutSet(
			Element element, LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		if (layoutSet.isPrivateLayout()) {
			return;
		}

		String portalURL = themeDisplay.getPortalURL();

		Map<String, LayoutTypeController> layoutTypeControllers =
			LayoutTypeControllerTracker.getLayoutTypeControllers();

		for (Map.Entry<String, LayoutTypeController> entry :
				layoutTypeControllers.entrySet()) {

			LayoutTypeController layoutTypeController = entry.getValue();

			if (!layoutTypeController.isSitemapable()) {
				continue;
			}

			List<Layout> layouts = _layoutLocalService.getAllLayouts(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				entry.getKey());

			for (Layout layout : layouts) {
				if (layout.isSystem() && !layout.isTypeAssetDisplay()) {
					continue;
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					layout.getTypeSettingsProperties();

				boolean sitemapInclude = GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						LayoutTypePortletConstants.SITEMAP_INCLUDE),
					true);

				if (!sitemapInclude) {
					continue;
				}

				Element sitemapElement = element.addElement("sitemap");

				Element locationElement = sitemapElement.addElement("loc");

				locationElement.addText(
					StringBundler.concat(
						portalURL, _portal.getPathContext(),
						"/sitemap.xml?p_l_id=", layout.getPlid(),
						"&layoutUuid=", layout.getUuid(), "&groupId=",
						layoutSet.getGroupId(), "&privateLayout=",
						layout.isPrivateLayout()));

				_removeOldestElement(element, sitemapElement);
			}
		}
	}

	private void _visitLayoutSets(
			List<LayoutSet> layoutSets, String layoutUuid, Element rootElement,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (ListUtil.isEmpty(layoutSets)) {
			return;
		}

		for (SitemapURLProvider sitemapURLProvider :
				_getSitemapURLProviders()) {

			if (!sitemapURLProvider.isInclude(
					themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId())) {

				continue;
			}

			if (Validator.isNull(layoutUuid)) {
				for (LayoutSet curLayoutSet : layoutSets) {
					sitemapURLProvider.visitLayoutSet(
						rootElement, curLayoutSet, themeDisplay);
				}
			}
			else {
				sitemapURLProvider.visitLayout(
					rootElement, layoutUuid, layoutSets.get(0), themeDisplay);
			}
		}
	}

	private static final byte[] _ATTRIBUTE_XHTML =
		" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"".getBytes();

	private static final byte[] _ATTRIBUTE_XMLNS =
		" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"".getBytes();

	private static final int _MAXIMUM_SIZE = 50 * 1024 * 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		SitemapManagerImpl.class.getName());

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SAXReader _saxReader;

	private ServiceTrackerMap<String, SitemapURLProvider> _serviceTrackerMap;

	@Reference
	private SitemapConfigurationManager _sitemapConfigurationManager;

}
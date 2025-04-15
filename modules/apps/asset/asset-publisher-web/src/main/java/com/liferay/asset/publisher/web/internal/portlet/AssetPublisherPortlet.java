/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet;

import com.liferay.asset.constants.AssetWebKeys;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.display.context.AssetPublisherDisplayContext;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizerRegistry;
import com.liferay.asset.util.AssetHelper;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;
import com.liferay.rss.model.SyndModelFactory;
import com.liferay.rss.util.RSSUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-asset-publisher",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.display-category=category.highlighted",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Asset Publisher",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.supported-public-render-parameter=assetEntryId",
		"jakarta.portlet.supported-public-render-parameter=categoryId",
		"jakarta.portlet.supported-public-render-parameter=resetCur",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.supported-public-render-parameter=tags",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetPublisherPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_WEB_HELPER,
			assetPublisherWebHelper);

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (resourceID.equals("getFieldValue")) {
			_getFieldValue(resourceRequest, resourceResponse);
		}
		else if (resourceID.equals("getRSS")) {
			_getRSS(resourceRequest, resourceResponse);
		}
		else {
			super.serveResource(resourceRequest, resourceResponse);
		}
	}

	public void subscribe(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		assetPublisherWebHelper.subscribe(
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroupId(),
			themeDisplay.getPlid(), themeDisplay.getPpid());
	}

	public void unsubscribe(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		assetPublisherWebHelper.unsubscribe(
			themeDisplay.getPermissionChecker(), themeDisplay.getPlid(),
			themeDisplay.getPpid());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		assetPublisherWebConfiguration = ConfigurableUtil.createConfigurable(
			AssetPublisherWebConfiguration.class, properties);

		assetPublisherCustomizerRegistry = new AssetPublisherCustomizerRegistry(
			assetPublisherHelper, assetPublisherWebConfiguration);

		portletRegistry.registerAlias(
			_ALIAS, AssetPublisherPortletKeys.ASSET_PUBLISHER);
	}

	@Deactivate
	protected void deactivate() {
		portletRegistry.unregisterAlias(_ALIAS);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			renderRequest.setAttribute(AssetWebKeys.ASSET_HELPER, assetHelper);

			String rootPortletId = PortletIdCodec.decodePortletName(
				portal.getPortletId(renderRequest));

			PortletPreferences portletPreferences =
				renderRequest.getPreferences();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (portletDisplay != null) {
				portletPreferences = portletDisplay.getPortletPreferences();
			}

			AssetPublisherDisplayContext assetPublisherDisplayContext =
				new AssetPublisherDisplayContext(
					assetHelper, assetListAssetEntryProvider,
					assetListEntrySegmentsEntryRelLocalService,
					assetPublisherCustomizerRegistry.
						getAssetPublisherCustomizer(rootPortletId),
					assetPublisherHelper, assetPublisherWebConfiguration,
					assetPublisherWebHelper, infoItemServiceRegistry,
					itemSelector, portal, renderRequest, renderResponse,
					portletPreferences, requestContextMapper,
					segmentsEntryRetriever);

			renderRequest.setAttribute(
				AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT,
				assetPublisherDisplayContext);

			renderRequest.setAttribute(
				AssetPublisherWebKeys.ASSET_PUBLISHER_HELPER,
				assetPublisherHelper);
			renderRequest.setAttribute(
				WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
		}
		catch (Exception exception) {
			_log.error("Unable to get asset publisher customizer", exception);
		}

		if (SessionErrors.contains(
				renderRequest, NoSuchGroupException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof NoSuchGroupException ||
			throwable instanceof PrincipalException) {

			return true;
		}

		return false;
	}

	@Reference
	protected AssetHelper assetHelper;

	@Reference
	protected AssetListAssetEntryProvider assetListAssetEntryProvider;

	@Reference
	protected AssetListEntrySegmentsEntryRelLocalService
		assetListEntrySegmentsEntryRelLocalService;

	protected volatile AssetPublisherCustomizerRegistry
		assetPublisherCustomizerRegistry;

	@Reference
	protected AssetPublisherHelper assetPublisherHelper;

	protected volatile AssetPublisherWebConfiguration
		assetPublisherWebConfiguration;

	@Reference
	protected AssetPublisherWebHelper assetPublisherWebHelper;

	@Reference
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	@Reference
	protected ItemSelector itemSelector;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletRegistry portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.asset.publisher.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	protected Release release;

	@Reference
	protected RequestContextMapper requestContextMapper;

	@Reference
	protected RSSExporter rssExporter;

	@Reference
	protected SegmentsEntryRetriever segmentsEntryRetriever;

	@Reference
	protected SyndModelFactory syndModelFactory;

	private String _exportToRSS(
			PortletRequest portletRequest, PortletResponse portletResponse,
			String name, String format, double version, String displayStyle,
			String linkBehavior, List<AssetEntry> assetEntries)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SyndFeed syndFeed = syndModelFactory.createSyndFeed();

		syndFeed.setDescription(name);

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		for (AssetEntry assetEntry : assetEntries) {
			SyndEntry syndEntry = syndModelFactory.createSyndEntry();

			syndEntry.setAuthor(portal.getUserName(assetEntry));

			SyndContent syndContent = syndModelFactory.createSyndContent();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);

			String value = null;

			String languageId = language.getLanguageId(portletRequest);

			if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE)) {
				value = StringPool.BLANK;
			}
			else {
				value = assetEntry.getSummary(languageId, true);
			}

			syndContent.setValue(value);

			syndEntry.setDescription(syndContent);

			String link = _getEntryURL(
				portletRequest, portletResponse, linkBehavior, assetEntry);

			syndEntry.setLink(link);

			syndEntry.setPublishedDate(assetEntry.getPublishDate());
			syndEntry.setTitle(assetEntry.getTitle(languageId, true));
			syndEntry.setUpdatedDate(assetEntry.getModifiedDate());
			syndEntry.setUri(link);

			syndEntries.add(syndEntry);
		}

		syndFeed.setFeedType(RSSUtil.getFeedType(format, version));

		List<SyndLink> syndLinks = new ArrayList<>();

		syndFeed.setLinks(syndLinks);

		SyndLink selfSyndLink = syndModelFactory.createSyndLink();

		syndLinks.add(selfSyndLink);

		String feedURL = _getFeedURL(portletRequest);

		selfSyndLink.setHref(feedURL);

		selfSyndLink.setRel("self");

		SyndLink alternateSyndLink = syndModelFactory.createSyndLink();

		syndLinks.add(alternateSyndLink);

		alternateSyndLink.setHref(portal.getLayoutFullURL(themeDisplay));
		alternateSyndLink.setRel("alternate");

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(name);
		syndFeed.setUri(feedURL);

		return rssExporter.export(syndFeed);
	}

	private List<AssetEntry> _getAssetEntries(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences)
		throws Exception {

		List<AssetEntry> assetEntries = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SearchContainer<AssetEntry> searchContainer = new SearchContainer();

		AssetPublisherDisplayContext assetPublisherDisplayContext =
			(AssetPublisherDisplayContext)portletRequest.getAttribute(
				AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT);

		searchContainer.setDelta(assetPublisherDisplayContext.getRSSDelta());

		Map<String, Serializable> attributes =
			assetPublisherDisplayContext.getAttributes();

		attributes.put("filterExpired", Boolean.TRUE);

		List<AssetEntryResult> assetEntryResults =
			assetPublisherHelper.getAssetEntryResults(
				searchContainer,
				assetPublisherDisplayContext.getAssetEntryQuery(),
				themeDisplay.getLayout(), portletPreferences,
				assetPublisherDisplayContext.getPortletName(),
				themeDisplay.getLocale(), themeDisplay.getTimeZone(),
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				themeDisplay.getUserId(),
				assetPublisherDisplayContext.getClassNameIds(), attributes);

		for (AssetEntryResult assetEntryResult : assetEntryResults) {
			assetEntries.addAll(assetEntryResult.getAssetEntries());
		}

		return assetEntries;
	}

	private String _getAssetPublisherURL(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		StringBundler sb = new StringBundler(6);

		String layoutFriendlyURL = GetterUtil.getString(
			portal.getLayoutFriendlyURL(
				themeDisplay.getLayout(), themeDisplay));

		if (!layoutFriendlyURL.startsWith(Http.HTTP_WITH_SLASH) &&
			!layoutFriendlyURL.startsWith(Http.HTTPS_WITH_SLASH)) {

			sb.append(themeDisplay.getPortalURL());
		}

		sb.append(layoutFriendlyURL);
		sb.append(Portal.FRIENDLY_URL_SEPARATOR);
		sb.append("asset_publisher/");
		sb.append(portletDisplay.getInstanceId());
		sb.append(StringPool.SLASH);

		return sb.toString();
	}

	private String _getDisplayFieldValue(Field field, ThemeDisplay themeDisplay)
		throws Exception {

		String fieldValue = String.valueOf(
			DDMUtil.getDisplayFieldValue(
				themeDisplay, field.getValue(themeDisplay.getLocale(), 0),
				field.getType()));

		DDMStructure ddmStructure = field.getDDMStructure();

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(
			field.getName());

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		LocalizedValue localizedValue = ddmFormFieldOptions.getOptionLabels(
			String.valueOf(fieldValue));

		if (localizedValue != null) {
			return localizedValue.getString(themeDisplay.getLocale());
		}

		return fieldValue;
	}

	private String _getEntryURL(
			PortletRequest portletRequest, PortletResponse portletResponse,
			String linkBehavior, AssetEntry assetEntry)
		throws Exception {

		if (linkBehavior.equals("viewInPortlet")) {
			return _getEntryURLViewInContext(
				portletRequest, portletResponse, assetEntry);
		}

		return _getEntryURLAssetPublisher(portletRequest, assetEntry);
	}

	private String _getEntryURLAssetPublisher(
			PortletRequest portletRequest, AssetEntry assetEntry)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntry.getClassName());

		return StringBundler.concat(
			_getAssetPublisherURL(portletRequest),
			assetRendererFactory.getType(), "/id/", assetEntry.getEntryId());
	}

	private String _getEntryURLViewInContext(
		PortletRequest portletRequest, PortletResponse portletResponse,
		AssetEntry assetEntry) {

		String assetViewURL = assetPublisherHelper.getAssetViewURL(
			portal.getLiferayPortletRequest(portletRequest),
			portal.getLiferayPortletResponse(portletResponse), assetEntry,
			true);

		if (Validator.isNotNull(assetViewURL) &&
			!assetViewURL.startsWith(Http.HTTP_WITH_SLASH) &&
			!assetViewURL.startsWith(Http.HTTPS_WITH_SLASH)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			assetViewURL = themeDisplay.getPortalURL() + assetViewURL;
		}

		return assetViewURL;
	}

	private String _getFeedURL(PortletRequest portletRequest) throws Exception {
		String feedURL = _getAssetPublisherURL(portletRequest);

		return feedURL.concat("rss");
	}

	private void _getFieldValue(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				resourceRequest);

			String fieldName = ParamUtil.getString(resourceRequest, "name");

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						ParamUtil.getString(resourceRequest, "className"));

			ClassTypeReader classTypeReader =
				assetRendererFactory.getClassTypeReader();

			ClassType classType = classTypeReader.getClassType(
				ParamUtil.getLong(resourceRequest, "classTypeId"),
				themeDisplay.getLocale());

			ClassTypeField classTypeField = classType.getClassTypeField(
				fieldName);

			Fields fields = (Fields)serviceContext.getAttribute(
				Fields.class.getName() + classTypeField.getClassTypeId());

			if (fields == null) {
				String fieldsNamespace = ParamUtil.getString(
					resourceRequest, "fieldsNamespace");

				fields = DDMUtil.getFields(
					classTypeField.getClassTypeId(), fieldsNamespace,
					serviceContext);
			}

			Field field = fields.get(fieldName);

			Serializable fieldValue = field.getValue(
				themeDisplay.getLocale(), 0);

			JSONObject jsonObject = jsonFactory.createJSONObject();

			if (fieldValue != null) {
				jsonObject.put("success", true);
			}
			else {
				jsonObject.put("success", false);

				writeJSON(resourceRequest, resourceResponse, jsonObject);

				return;
			}

			jsonObject.put(
				"displayValue", _getDisplayFieldValue(field, themeDisplay)
			).put(
				"value",
				() -> {
					if (fieldValue instanceof Boolean) {
						return (Boolean)fieldValue;
					}

					if (fieldValue instanceof Date) {
						DateFormat dateFormat =
							DateFormatFactoryUtil.getSimpleDateFormat(
								"yyyyMM ddHHmmss");

						return dateFormat.format(fieldValue);
					}

					if (fieldValue instanceof Double) {
						return (Double)fieldValue;
					}

					if (fieldValue instanceof Float) {
						return (Float)fieldValue;
					}

					if (fieldValue instanceof Integer) {
						return (Integer)fieldValue;
					}

					if (fieldValue instanceof Number) {
						return String.valueOf(fieldValue);
					}

					return (String)fieldValue;
				}
			);

			writeJSON(resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private void _getRSS(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		PortletPreferences portletPreferences =
			resourceRequest.getPreferences();

		if (!portal.isRSSFeedsEnabled() ||
			!GetterUtil.getBoolean(
				portletPreferences.getValue("enableRss", null))) {

			try {
				portal.sendRSSFeedsDisabledError(
					resourceRequest, resourceResponse);
			}
			catch (ServletException servletException) {
				if (_log.isDebugEnabled()) {
					_log.debug(servletException);
				}
			}

			return;
		}

		String currentURL = portal.getCurrentURL(resourceRequest);

		String cacheability = HttpComponentsUtil.getParameter(
			currentURL, "p_p_cacheability", false);

		if (cacheability.equals(ResourceURL.FULL)) {
			HttpServletResponse httpServletResponse =
				portal.getHttpServletResponse(resourceResponse);

			String redirectURL = HttpComponentsUtil.removeParameter(
				currentURL, "p_p_cacheability");

			httpServletResponse.sendRedirect(redirectURL);

			return;
		}

		resourceResponse.setContentType(ContentTypes.TEXT_XML_UTF8);

		try (OutputStream outputStream =
				resourceResponse.getPortletOutputStream()) {

			resourceRequest.setAttribute(
				AssetPublisherWebKeys.ASSET_PUBLISHER_DISPLAY_CONTEXT,
				new AssetPublisherDisplayContext(
					assetHelper, assetListAssetEntryProvider,
					assetListEntrySegmentsEntryRelLocalService,
					assetPublisherCustomizerRegistry.
						getAssetPublisherCustomizer(
							PortletIdCodec.decodePortletName(
								portal.getPortletId(resourceRequest))),
					assetPublisherHelper, assetPublisherWebConfiguration,
					assetPublisherWebHelper, infoItemServiceRegistry,
					itemSelector, portal, resourceRequest, resourceResponse,
					resourceRequest.getPreferences(), requestContextMapper,
					segmentsEntryRetriever));

			portletPreferences = resourceRequest.getPreferences();

			String selectionStyle = portletPreferences.getValue(
				"selectionStyle",
				AssetPublisherSelectionStyleConfigurationUtil.
					defaultSelectionStyle());

			if (!selectionStyle.equals(
					AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

				outputStream.write(new byte[0]);

				return;
			}

			String rssFeedType = portletPreferences.getValue(
				"rssFeedType", RSSUtil.FEED_TYPE_DEFAULT);

			String rss = _exportToRSS(
				resourceRequest, resourceResponse,
				portletPreferences.getValue("rssName", null),
				RSSUtil.getFeedTypeFormat(rssFeedType),
				RSSUtil.getFeedTypeVersion(rssFeedType),
				portletPreferences.getValue(
					"rssDisplayStyle", RSSUtil.DISPLAY_STYLE_ABSTRACT),
				portletPreferences.getValue(
					"assetLinkBehavior", "showFullContent"),
				_getAssetEntries(resourceRequest, portletPreferences));

			outputStream.write(rss.getBytes(StringPool.UTF8));
		}
		catch (Exception exception) {
			_log.error("Unable to get RSS feed", exception);
		}
	}

	private static final String _ALIAS = "asset-list";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherPortlet.class);

}
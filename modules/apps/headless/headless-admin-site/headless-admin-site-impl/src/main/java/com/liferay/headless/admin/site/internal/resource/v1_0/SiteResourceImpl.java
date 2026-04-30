/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.google.places.constants.GooglePlacesWebKeys;
import com.liferay.headless.admin.site.dto.v1_0.AnalyticsConfiguration;
import com.liferay.headless.admin.site.dto.v1_0.GoogleAnalyticsConfiguration;
import com.liferay.headless.admin.site.dto.v1_0.RatingsTypes;
import com.liferay.headless.admin.site.dto.v1_0.Site;
import com.liferay.headless.admin.site.resource.v1_0.SiteResource;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredGroupException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.GroupNameComparator;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerFactory;
import com.liferay.site.initializer.SiteInitializerRegistry;
import com.liferay.site.initializer.SiteInitializerSerializer;
import com.liferay.sites.kernel.util.Sites;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.Serializable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteResource.class
)
@CTAware
public class SiteResourceImpl extends BaseSiteResourceImpl {

	@Override
	public void deleteSite(String externalReferenceCode) throws Exception {
		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			throw new NoSuchGroupException(
				"No site exists with external reference code " +
					externalReferenceCode);
		}

		_groupService.deleteGroup(group.getGroupId());
	}

	@Override
	public Response getSiteSiteInitializer(String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-19870")) {

			throw new UnsupportedOperationException();
		}

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		File file = _siteInitializerSerializer.serialize(group.getGroupId());

		try {
			return Response.ok(
				file
			).header(
				"Content-Disposition",
				"attachment; filename=\"" + file.getName() + "\""
			).build();
		}
		finally {

			// TODO LPD-19870

			//file.delete();
		}
	}

	@Override
	public Site postSiteSiteInitializer(MultipartBody multipartBody)
		throws Exception {

		Site site = multipartBody.getValueAsInstance("site", Site.class);

		return putSiteSiteInitializer(
			site.getExternalReferenceCode(), multipartBody);
	}

	@Override
	public void putSiteActivate(String siteExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), group,
			ActionKeys.UPDATE);

		if (!group.isActive()) {
			group.setActive(true);

			_groupLocalService.updateGroup(group);
		}
	}

	@Override
	public void putSiteDeactivate(String siteExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), group,
			ActionKeys.UPDATE);

		if (group.isCompany() || group.isControlPanel() || group.isGuest()) {
			throw new RequiredGroupException.MustNotDeactivateSystemGroup(
				siteExternalReferenceCode);
		}

		if (group.isActive()) {
			group.setActive(false);

			_groupLocalService.updateGroup(group);
		}
	}

	@Override
	public Site putSiteSiteInitializer(
			String externalReferenceCode, MultipartBody multipartBody)
		throws Exception {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			group = _addGroup(
				externalReferenceCode,
				multipartBody.getValueAsInstance("site", Site.class));
		}
		else {
			if (!group.isSite()) {
				throw new IllegalArgumentException(
					"No site exists with external reference code " +
						externalReferenceCode);
			}

			GroupPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(), group,
				ActionKeys.UPDATE);

			group = _updateGroup(
				group, multipartBody.getValueAsInstance("site", Site.class));
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String name = PrincipalThreadLocal.getName();

		File tempFile = FileUtil.createTempFile(
			multipartBody.getBinaryFileAsBytes("file"));
		File tempFolder = FileUtil.createTempFolder();

		FileUtil.unzip(tempFile, tempFolder);

		tempFile.delete();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(contextUser));
			PrincipalThreadLocal.setName(contextUser.getUserId());

			ServiceContextThreadLocal.pushServiceContext(
				_getServiceContext(group));

			SiteInitializer siteInitializer = _siteInitializerFactory.create(
				new File(tempFolder, "site-initializer"),
				group.getName(LocaleUtil.getDefault()));

			siteInitializer.initialize(group.getGroupId());
		}
		catch (Exception exception) {

			// LPS-169057

			PermissionCacheUtil.clearCache(contextUser.getUserId());

			throw exception;
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			PrincipalThreadLocal.setName(name);

			ServiceContextThreadLocal.popServiceContext();

			tempFolder.delete();
		}

		Group finalGroup = group;

		return _toSite(finalGroup);
	}

	@Override
	public void update(
			Collection<Site> sites, Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<Site, Site, Exception> unsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			unsafeFunction = site -> putSite(
				site.getExternalReferenceCode(), site);
		}

		if (unsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for Site");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(sites, unsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(sites, unsafeFunction::apply);
		}
		else {
			for (Site site : sites) {
				unsafeFunction.apply(site);
			}
		}
	}

	@Override
	protected Site doGetSite(String externalReferenceCode) throws Exception {
		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return _toSite(group);
	}

	@Override
	protected Page<Site> doGetSitesPage(
			Boolean active, String search, Pagination pagination)
		throws Exception {

		long[] classNameIds = {
			_portal.getClassNameId(Company.class.getName()),
			_portal.getClassNameId(Group.class.getName())
		};
		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"active",
				() -> {
					if (active != null) {
						return GetterUtil.getBoolean(active);
					}

					return null;
				}
			).put(
				"site", true
			).build();

		return Page.of(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postSite", Group.class.getName(), null)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postSiteBatch", Group.class.getName(),
					null)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteSiteBatch", Group.class.getName(),
					null)
			).build(),
			transform(
				_groupService.search(
					contextCompany.getCompanyId(), classNameIds, search, null,
					params, true, pagination.getStartPosition(),
					pagination.getEndPosition(), new GroupNameComparator()),
				this::_toSite),
			pagination,
			_groupService.searchCount(
				contextCompany.getCompanyId(), classNameIds, search, params));
	}

	@Override
	protected Site doPostSite(Site site) throws Exception {
		Group group = _addGroup(site.getExternalReferenceCode(), site);

		return _toSite(group);
	}

	@Override
	protected Site doPutSite(String siteExternalReferenceCode, Site site)
		throws Exception {

		if (site.getExternalReferenceCode() == null) {
			site.setExternalReferenceCode(() -> siteExternalReferenceCode);
		}

		if (!Objects.equals(
				siteExternalReferenceCode, site.getExternalReferenceCode())) {

			throw new UnsupportedOperationException();
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			group = _addGroup(siteExternalReferenceCode, site);
		}
		else {
			group = _updateGroup(group, site);
		}

		return _toSite(group);
	}

	@Override
	protected Long getPermissionCheckerResourceId(String externalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return group.getPrimaryKey();
	}

	@Override
	protected String getPermissionCheckerResourceName(
		String externalReferenceCode) {

		return Group.class.getName();
	}

	private Group _addGroup(String externalReferenceCode, Site site)
		throws Exception {

		if (Validator.isNull(site.getTemplateKey()) &&
			Validator.isNotNull(site.getTemplateType())) {

			throw new IllegalArgumentException(
				"Template key cannot be empty if template type is specified");
		}

		if (Validator.isNotNull(site.getTemplateKey()) &&
			Validator.isNull(site.getTemplateType())) {

			throw new IllegalArgumentException(
				"Template type cannot be empty if template key is specified");
		}

		if (Objects.equals(
				Site.TemplateType.SITE_INITIALIZER, site.getTemplateType())) {

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					site.getTemplateKey());

			if (siteInitializer == null) {
				throw new IllegalArgumentException(
					"No site initializer was found for site template key " +
						site.getTemplateKey());
			}

			if (!siteInitializer.isActive(contextCompany.getCompanyId())) {
				throw new IllegalArgumentException(
					"Site initializer with site template key " +
						site.getTemplateKey() + " is inactive");
			}
		}
		else if (Objects.equals(
					Site.TemplateType.SITE_TEMPLATE, site.getTemplateType())) {

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
					GetterUtil.getLongStrict(site.getTemplateKey()));

			if (layoutSetPrototype == null) {
				throw new IllegalArgumentException(
					"No site template was found for site template key " +
						site.getTemplateKey());
			}

			if (!layoutSetPrototype.isActive()) {
				throw new IllegalArgumentException(
					"Site template with site template key " +
						site.getTemplateKey() + " is inactive");
			}
		}

		_initThemeDisplay();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					contextCompany, contextUser)) {

			return _addGroup(externalReferenceCode, site, _getServiceContext());
		}
		catch (Exception exception) {

			// LPS-169057

			PermissionCacheUtil.clearCache(contextUser.getUserId());

			throw exception;
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private Group _addGroup(
			String externalReferenceCode, Site site,
			ServiceContext serviceContext)
		throws Exception {

		Group group = _groupService.addGroup(
			externalReferenceCode,
			_getParentGroupId(null, site.getParentSiteExternalReferenceCode()),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, _getNameMap(site),
			_getDescriptionMap(site), _getType(site.getMembershipType()),
			_getTypeSettings(site, null),
			_isManualMembership(site.getManualMembership()),
			_getMembershipRestriction(site.getMembershipRestriction()),
			_getFriendlyUrlPath(null, site), true, false,
			_isActive(site.getActive()), serviceContext);

		LiveUsers.joinGroup(
			contextCompany.getCompanyId(), group.getGroupId(),
			contextUser.getUserId());

		if (Objects.equals(
				Site.TemplateType.SITE_TEMPLATE, site.getTemplateType())) {

			_sites.updateLayoutSetPrototypesLinks(
				group, GetterUtil.getLongStrict(site.getTemplateKey()), 0L,
				true, false);
		}
		else {
			String siteInitializerKey = "blank-site-initializer";

			if (Validator.isNotNull(site.getTemplateKey())) {
				siteInitializerKey = site.getTemplateKey();
			}

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(siteInitializerKey);

			siteInitializer.initialize(group.getGroupId());
		}

		return group;
	}

	private Map<Locale, String> _getDescriptionMap(Site site) {
		if (Validator.isNotNull(site.getDescription_i18n())) {
			return LocalizedMapUtil.getLocalizedMap(site.getDescription_i18n());
		}

		if (site.getDescription() == null) {
			return null;
		}

		return HashMapBuilder.put(
			LocaleUtil.getDefault(), site.getDescription()
		).build();
	}

	private String _getFriendlyUrlPath(Group group, Site site) {
		String friendlyUrlPath = site.getFriendlyUrlPath();

		if (Validator.isNull(friendlyUrlPath) && (group != null)) {
			friendlyUrlPath = group.getFriendlyURL();
		}

		if (Validator.isNotNull(friendlyUrlPath) &&
			!friendlyUrlPath.startsWith(StringPool.SLASH)) {

			friendlyUrlPath = StringPool.SLASH + friendlyUrlPath;
		}

		return friendlyUrlPath;
	}

	private GoogleAnalyticsConfiguration _getGoogleAnalyticsConfiguration(
		Group group) {

		return new GoogleAnalyticsConfiguration() {
			{
				setGoogleAnalytics4CustomConfig(
					() -> group.getTypeSettingsProperty(
						"googleAnalytics4CustomConfiguration"));
				setGoogleAnalytics4Id(
					() -> group.getTypeSettingsProperty("googleAnalytics4Id"));
				setGoogleAnalyticsCreateCustomConfig(
					() -> group.getTypeSettingsProperty(
						"googleAnalyticsCreateCustomConfiguration"));
				setGoogleAnalyticsCustomConfig(
					() -> group.getTypeSettingsProperty(
						"googleAnalyticsCustomConfiguration"));
				setGoogleAnalyticsId(
					() -> group.getTypeSettingsProperty("googleAnalyticsId"));
			}
		};
	}

	private int _getMembershipRestriction(Integer membershipRestriction) {
		if (membershipRestriction == null) {
			return GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
		}

		return membershipRestriction;
	}

	private Map<Locale, String> _getNameMap(Site site) {
		if (Validator.isNotNull(site.getName_i18n())) {
			return LocalizedMapUtil.getLocalizedMap(site.getName_i18n());
		}

		return HashMapBuilder.put(
			LocaleUtil.getDefault(), site.getName()
		).build();
	}

	private long _getParentGroupId(
		Group group, String parentSiteExternalReferenceCode) {

		if (Validator.isNull(parentSiteExternalReferenceCode)) {
			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		Group parentGroup =
			_groupLocalService.fetchGroupByExternalReferenceCode(
				parentSiteExternalReferenceCode, contextCompany.getCompanyId());

		if (parentGroup == null) {
			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			return parentGroup.getGroupId();
		}

		if (group != null) {
			Group currentParentGroup = group.getParentGroup();

			if ((currentParentGroup != null) &&
				Objects.equals(
					currentParentGroup.getExternalReferenceCode(),
					parentSiteExternalReferenceCode)) {

				return currentParentGroup.getGroupId();
			}

			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		return parentGroup.getGroupId();
	}

	private RatingsTypes _getRatingsTypes(Group group) {
		return new RatingsTypes() {
			{
				setBlogPosting(
					() -> RatingsTypes.BlogPosting.create(
						group.getTypeSettingsProperty(
							"com.liferay.blogs.model.BlogsEntry_RatingsType")));
				setBookmarksEntry(
					() -> RatingsTypes.BookmarksEntry.create(
						group.getTypeSettingsProperty(
							"com.liferay.bookmarks.model." +
								"BookmarksEntry_RatingsType")));
				setComment(
					() -> RatingsTypes.Comment.create(
						group.getTypeSettingsProperty(
							"com.liferay.message.boards.model." +
								"MBDiscussion_RatingsType")));
				setDocument(
					() -> RatingsTypes.Document.create(
						group.getTypeSettingsProperty(
							"com.liferay.document.library.kernel.model." +
								"DLFileEntry_RatingsType")));
				setKnowledgeBaseArticle(
					() -> RatingsTypes.KnowledgeBaseArticle.create(
						group.getTypeSettingsProperty(
							"com.liferay.knowledge.base.model." +
								"KBArticle_RatingsType")));
				setMessageBoardMessage(
					() -> RatingsTypes.MessageBoardMessage.create(
						group.getTypeSettingsProperty(
							"com.liferay.message.boards.model." +
								"MBMessage_RatingsType")));
				setSitePage(
					() -> RatingsTypes.SitePage.create(
						group.getTypeSettingsProperty(
							"com.liferay.portal.kernel.model." +
								"Layout_RatingsType")));
				setStructuredContent(
					() -> RatingsTypes.StructuredContent.create(
						group.getTypeSettingsProperty(
							"com.liferay.journal.model." +
								"JournalArticle_RatingsType")));
				setWikiPage(
					() -> RatingsTypes.WikiPage.create(
						group.getTypeSettingsProperty(
							"com.liferay.wiki.model.WikiPage_RatingsType")));
			}
		};
	}

	private ServiceContext _getServiceContext() throws PortalException {
		ServiceContext serviceContext = null;

		if (contextHttpServletRequest != null) {
			serviceContext = ServiceContextFactory.getInstance(
				contextHttpServletRequest);
		}
		else {
			serviceContext = new ServiceContext();

			serviceContext.setCompanyId(contextCompany.getCompanyId());
			serviceContext.setRequest(contextHttpServletRequest);
			serviceContext.setUserId(contextUser.getUserId());
		}

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return serviceContext;
	}

	private ServiceContext _getServiceContext(Group group) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setRequest(contextHttpServletRequest);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(contextUser.getUserId());

		_initThemeDisplay();

		return serviceContext;
	}

	private int _getType(Site.MembershipType membershipType) {
		if (membershipType == null) {
			return GroupConstants.TYPE_SITE_RESTRICTED;
		}
		else if (membershipType.equals(Site.MembershipType.OPEN)) {
			return GroupConstants.TYPE_SITE_OPEN;
		}
		else if (membershipType.equals(Site.MembershipType.PRIVATE)) {
			return GroupConstants.TYPE_SITE_PRIVATE;
		}

		return GroupConstants.TYPE_SITE_RESTRICTED;
	}

	private String _getTypeSettings(
		Site site, UnicodeProperties oldUnicodeProperties) {

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		AnalyticsConfiguration analyticsConfiguration =
			site.getAnalyticsConfiguration();

		if (analyticsConfiguration != null) {
			GoogleAnalyticsConfiguration googleAnalyticsConfiguration =
				analyticsConfiguration.getGoogleAnalyticsConfiguration();

			if (googleAnalyticsConfiguration != null) {
				unicodeProperties.put(
					"googleAnalytics4CustomConfiguration",
					googleAnalyticsConfiguration.
						getGoogleAnalytics4CustomConfig());
				unicodeProperties.put(
					"googleAnalytics4Id",
					googleAnalyticsConfiguration.getGoogleAnalytics4Id());
				unicodeProperties.put(
					"googleAnalyticsCreateCustomConfiguration",
					googleAnalyticsConfiguration.
						getGoogleAnalyticsCreateCustomConfig());
				unicodeProperties.put(
					"googleAnalyticsCustomConfiguration",
					googleAnalyticsConfiguration.
						getGoogleAnalyticsCustomConfig());
				unicodeProperties.put(
					"googleAnalyticsId",
					googleAnalyticsConfiguration.getGoogleAnalyticsId());
			}

			unicodeProperties.put(
				"analytics_matomo",
				analyticsConfiguration.getMatomoAnalyticsScript());
		}

		unicodeProperties.put(
			"assetAutoTaggingEnabled",
			String.valueOf(site.getAssetAutoTaggingEnabled()));

		int contentSharingWithChildrenEnabled =
			Sites.CONTENT_SHARING_WITH_CHILDREN_DEFAULT_VALUE;

		if (site.getContentSharingWithChildrenEnabled() != null) {
			if (site.getContentSharingWithChildrenEnabled()) {
				contentSharingWithChildrenEnabled =
					Sites.CONTENT_SHARING_WITH_CHILDREN_ENABLED;
			}
			else {
				contentSharingWithChildrenEnabled =
					Sites.CONTENT_SHARING_WITH_CHILDREN_DISABLED;
			}
		}

		unicodeProperties.put(
			"contentSharingWithChildrenEnabled",
			String.valueOf(contentSharingWithChildrenEnabled));
		unicodeProperties.put("languageId", site.getDefaultLanguageId());
		unicodeProperties.put(
			"directoryIndexingEnabled",
			String.valueOf(site.getDirectoryIndexingEnabled()));

		if (site.getLocales() != null) {
			unicodeProperties.put(
				"locales",
				StringUtil.merge(
					LocaleUtil.fromLanguageIds(site.getLocales()),
					StringPool.COMMA));
		}

		if (site.getInheritLocales() == null) {
			unicodeProperties.put(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES,
				String.valueOf(
					!unicodeProperties.containsKey(PropsKeys.LOCALES)));
		}
		else {
			unicodeProperties.put(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES,
				String.valueOf(site.getInheritLocales()));
		}

		unicodeProperties.put(
			"MAP_PROVIDER_KEY", site.getMapProviderKeyAsString());
		unicodeProperties.put(
			"mentionsEnabled", String.valueOf(site.getMentionsEnabled()));

		RatingsTypes ratingsTypes = site.getRatingsTypes();

		if (ratingsTypes != null) {
			if (ratingsTypes.getBlogPosting() != null) {
				unicodeProperties.put(
					"com.liferay.blogs.model.BlogsEntry_RatingsType",
					ratingsTypes.getBlogPostingAsString());
			}

			if (ratingsTypes.getBookmarksEntry() != null) {
				unicodeProperties.put(
					"com.liferay.bookmarks.model.BookmarksEntry_RatingsType",
					ratingsTypes.getBookmarksEntryAsString());
			}

			if (ratingsTypes.getComment() != null) {
				unicodeProperties.put(
					"com.liferay.message.boards.model.MBDiscussion_RatingsType",
					ratingsTypes.getCommentAsString());
			}

			if (ratingsTypes.getDocument() != null) {
				unicodeProperties.put(
					"com.liferay.document.library.kernel.model." +
						"DLFileEntry_RatingsType",
					ratingsTypes.getDocumentAsString());
			}

			if (ratingsTypes.getKnowledgeBaseArticle() != null) {
				unicodeProperties.put(
					"com.liferay.knowledge.base.model.KBArticle_RatingsType",
					ratingsTypes.getKnowledgeBaseArticleAsString());
			}

			if (ratingsTypes.getMessageBoardMessage() != null) {
				unicodeProperties.put(
					"com.liferay.message.boards.model.MBMessage_RatingsType",
					ratingsTypes.getMessageBoardMessageAsString());
			}

			if (ratingsTypes.getSitePage() != null) {
				unicodeProperties.put(
					"com.liferay.portal.kernel.model.Layout_RatingsType",
					ratingsTypes.getSitePageAsString());
			}

			if (ratingsTypes.getStructuredContent() != null) {
				unicodeProperties.put(
					"com.liferay.journal.model.JournalArticle_RatingsType",
					ratingsTypes.getStructuredContentAsString());
			}

			if (ratingsTypes.getWikiPage() != null) {
				unicodeProperties.put(
					"com.liferay.wiki.model.WikiPage_RatingsType",
					ratingsTypes.getWikiPageAsString());
			}
		}

		unicodeProperties.put(
			"sharingEnabled", String.valueOf(site.getSharingEnabled()));
		unicodeProperties.put(
			"trashEnabled", String.valueOf(site.getTrashEnabled()));
		unicodeProperties.put(
			"trashEntriesMaxAge", String.valueOf(site.getTrashEntriesMaxAge()));

		if (oldUnicodeProperties == null) {
			return unicodeProperties.toString();
		}

		for (String excludedTypeSetting : _EXCLUDED_TYPE_SETTINGS) {
			if (oldUnicodeProperties.containsKey(excludedTypeSetting)) {
				unicodeProperties.put(
					excludedTypeSetting,
					oldUnicodeProperties.get(excludedTypeSetting));
			}
		}

		return unicodeProperties.toString();
	}

	private void _initThemeDisplay() throws Exception {
		if (contextHttpServletRequest == null) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		servicePreAction.servicePre(
			contextHttpServletRequest, contextHttpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, contextHttpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setResponse(new DummyHttpServletResponse());
	}

	private boolean _isActive(Boolean active) {
		if (active == null) {
			return true;
		}

		return active;
	}

	private boolean _isManualMembership(Boolean manualMembership) {
		if (manualMembership == null) {
			return true;
		}

		return manualMembership;
	}

	private Site _toSite(Group group) {
		return new Site() {
			{
				setActive(group::getActive);
				setAnalyticsConfiguration(
					() -> new AnalyticsConfiguration() {
						{
							setGoogleAnalyticsConfiguration(
								() -> _getGoogleAnalyticsConfiguration(group));
							setMatomoAnalyticsScript(
								() -> group.getTypeSettingsProperty(
									"analytics_matomo"));
						}
					});
				setAssetAutoTaggingEnabled(
					() -> Boolean.parseBoolean(
						group.getTypeSettingsProperty(
							"assetAutoTaggingEnabled")));
				setContentSharingWithChildrenEnabled(
					group::isContentSharingWithChildrenEnabled);
				setDefaultLanguageId(group::getDefaultLanguageId);
				setDescription(
					() -> group.getDescription(LocaleUtil.getDefault()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						group.getDescriptionMap()));
				setDescriptiveName(
					() -> group.getDescriptiveName(LocaleUtil.getDefault()));
				setDescriptiveName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						group.getDescriptiveNameMap()));
				setDirectoryIndexingEnabled(
					() -> Boolean.parseBoolean(
						group.getTypeSettingsProperty(
							"directoryIndexingEnabled")));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setFriendlyUrlPath(group::getFriendlyURL);
				setId(group::getGroupId);
				setInheritLocales(
					() -> {
						if (group.getTypeSettingsProperty("inheritLocales") !=
								null) {

							return Boolean.parseBoolean(
								group.getTypeSettingsProperty(
									"inheritLocales"));
						}

						return true;
					});
				setKey(group::getGroupKey);
				setLocales(
					() -> LocaleUtil.toW3cLanguageIds(
						StringUtil.split(
							group.getTypeSettingsProperty("locales"))));
				setManualMembership(group::getManualMembership);
				setMapProviderKey(
					() -> MapProviderKey.create(
						group.getTypeSettingsProperty("MAP_PROVIDER_KEY")));
				setMembershipRestriction(group::getMembershipRestriction);
				setMembershipType(
					() -> MembershipType.create(
						GroupConstants.getTypeLabel(group.getType())));
				setMentionsEnabled(
					() -> Boolean.parseBoolean(
						group.getTypeSettingsProperty("mentionsEnabled")));
				setName(() -> group.getName(LocaleUtil.getDefault()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(group.getNameMap()));
				setParentSiteExternalReferenceCode(
					() -> {
						Group parentGroup = _groupLocalService.fetchGroup(
							group.getParentGroupId());

						if (parentGroup != null) {
							return parentGroup.getExternalReferenceCode();
						}

						return StringPool.BLANK;
					});
				setRatingsTypes(() -> _getRatingsTypes(group));
				setSharingEnabled(
					() -> Boolean.parseBoolean(
						group.getTypeSettingsProperty("sharingEnabled")));
				setTrashEnabled(
					() -> Boolean.parseBoolean(
						group.getTypeSettingsProperty("trashEnabled")));
				setTrashEntriesMaxAge(
					() -> GetterUtil.getInteger(
						group.getTypeSettingsProperty("trashEntriesMaxAge")));
			}
		};
	}

	private Group _updateGroup(Group group, Site site) throws Exception {
		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					contextCompany, contextUser)) {

			if ((group.isCompany() || group.isControlPanel() ||
				 group.isGuest()) &&
				(site.getActive() == false)) {

				throw new RequiredGroupException.MustNotDeactivateSystemGroup(
					site.getExternalReferenceCode());
			}

			Group updatedGroup = _groupService.updateGroup(
				group.getGroupId(),
				_getParentGroupId(
					group, site.getParentSiteExternalReferenceCode()),
				_getNameMap(site), _getDescriptionMap(site),
				_getType(site.getMembershipType()),
				_getTypeSettings(site, group.getTypeSettingsProperties()),
				_isManualMembership(site.getManualMembership()),
				_getMembershipRestriction(site.getMembershipRestriction()),
				_getFriendlyUrlPath(group, site), false,
				_isActive(site.getActive()), _getServiceContext());

			LiveUsers.joinGroup(
				contextCompany.getCompanyId(), updatedGroup.getGroupId(),
				contextUser.getUserId());

			return updatedGroup;
		}
		catch (Exception exception) {
			PermissionCacheUtil.clearCache(contextUser.getUserId());

			throw exception;
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static final String[] _EXCLUDED_TYPE_SETTINGS = {
		GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY, "defaultSiteRoleIds",
		"defaultTeamIds", "googleMapsAPIKey"
	};

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SiteInitializerFactory _siteInitializerFactory;

	@Reference
	private SiteInitializerRegistry _siteInitializerRegistry;

	@Reference
	private SiteInitializerSerializer _siteInitializerSerializer;

	@Reference
	private Sites _sites;

}
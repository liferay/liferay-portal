/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.internal.resource.v1_0;

import com.liferay.google.places.constants.GooglePlacesWebKeys;
import com.liferay.headless.site.dto.v1_0.Site;
import com.liferay.headless.site.resource.v1_0.SiteResource;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
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
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
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

import jakarta.ws.rs.core.Response;

import java.io.File;

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
	public void deleteSite(Long siteId) throws Exception {
		_groupService.deleteGroup(siteId);
	}

	@Override
	public void deleteSiteByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

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
	public Site getSite(Long siteId) {
		Group group = _groupLocalService.fetchGroup(siteId);

		return _toSite(group);
	}

	@Override
	public Site getSiteByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return _toSite(group);
	}

	@Override
	public Response getSiteByExternalReferenceCodeSiteInitializer(
			String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-19870")) {
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
	public Page<Site> getSitesPage(
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
	public Site postSite(Site site) throws Exception {
		Group group = _addGroup(site.getExternalReferenceCode(), site);

		return _toSite(group);
	}

	@Override
	public Site postSiteSiteInitializer(MultipartBody multipartBody)
		throws Exception {

		Site site = multipartBody.getValueAsInstance("site", Site.class);

		return putSiteByExternalReferenceCode(
			site.getExternalReferenceCode(), multipartBody);
	}

	@Override
	public Site putSite(Site site) throws Exception {
		String externalReferenceCode = site.getExternalReferenceCode();

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			group = _addGroup(externalReferenceCode, site);
		}
		else {
			group = _updateGroup(group, site);
		}

		return _toSite(group);
	}

	@Override
	public Site putSiteByExternalReferenceCode(
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

			return _toSite(group);
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String name = PrincipalThreadLocal.getName();

		File tempFile = FileUtil.createTempFile(
			multipartBody.getBinaryFileAsBytes("file"));
		File tempFolder = FileUtil.createTempFolder();

		FileUtil.unzip(tempFile, tempFolder);

		tempFile.delete();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					contextCompany.getCompanyId())) {

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
			_getParentGroupId(
				null, site.getParentSiteExternalReferenceCode(),
				site.getParentSiteKey()),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, _getNameMap(site),
			_getLocalizationMap(site.getDescription()),
			_getType(site.getMembershipType()),
			_isManualMembership(site.getManualMembership()),
			_getMembershipRestriction(site.getMembershipRestriction()),
			site.getFriendlyUrlPath(), true, false, _isActive(site.getActive()),
			serviceContext);

		if (Validator.isNotNull(externalReferenceCode)) {
			group.setExternalReferenceCode(externalReferenceCode);

			group = _groupLocalService.updateGroup(group);
		}

		if (Validator.isNotNull(site.getTypeSettings())) {
			group = _groupService.updateGroup(
				group.getGroupId(),
				_getTypeSettings(site.getTypeSettings(), null));
		}

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

	private Map<Locale, String> _getLocalizationMap(Map<String, String> map) {
		if (map == null) {
			return null;
		}

		return _localization.getLocalizationMap(
			map.keySet(
			).toArray(
				new String[0]
			),
			map.values(
			).toArray(
				new String[0]
			));
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
		Group group, String parentSiteExternalReferenceCode,
		String parentSiteKey) {

		if (Validator.isNull(parentSiteExternalReferenceCode) &&
			Validator.isNull(parentSiteKey)) {

			return GroupConstants.DEFAULT_PARENT_GROUP_ID;
		}

		Group parentGroup = _groupLocalService.loadFetchGroup(
			contextCompany.getCompanyId(), parentSiteKey);

		if (parentGroup == null) {
			parentGroup = _groupLocalService.fetchGroupByExternalReferenceCode(
				parentSiteExternalReferenceCode, contextCompany.getCompanyId());

			if (parentGroup == null) {
				return GroupConstants.DEFAULT_PARENT_GROUP_ID;
			}
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
		if ((membershipType == null) ||
			membershipType.equals(Site.MembershipType.OPEN)) {

			return GroupConstants.TYPE_SITE_OPEN;
		}
		else if (membershipType.equals(Site.MembershipType.PRIVATE)) {
			return GroupConstants.TYPE_SITE_PRIVATE;
		}

		return GroupConstants.TYPE_SITE_RESTRICTED;
	}

	private String _getTypeSettings(
			Map<String, String> typeSettings,
			UnicodeProperties oldUnicodeProperties)
		throws Exception {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.putAll(
			typeSettings
		).build();

		unicodeProperties.putIfAbsent(
			GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES,
			String.valueOf(!unicodeProperties.containsKey(PropsKeys.LOCALES)));

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
		String[] availableLanguageIds = group.getAvailableLanguageIds();

		return new Site() {
			{
				setActive(group::getActive);
				setDescription(
					() -> {
						Map<String, String> descriptionMap =
							new LinkedHashMap<>();

						for (String availableLanguageId :
								availableLanguageIds) {

							String description = group.getDescription(
								availableLanguageId, false);

							if (Validator.isNotNull(description)) {
								descriptionMap.put(
									availableLanguageId, description);
							}
						}

						return descriptionMap;
					});
				setExternalReferenceCode(group::getExternalReferenceCode);
				setFriendlyUrlPath(group::getFriendlyURL);
				setId(group::getGroupId);
				setKey(group::getGroupKey);
				setManualMembership(group::getManualMembership);
				setMembershipRestriction(group::getMembershipRestriction);
				setMembershipType(
					() -> MembershipType.create(
						GroupConstants.getTypeLabel(group.getType())));
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
				setTypeSettings(
					() -> {
						UnicodeProperties unicodeProperties =
							UnicodePropertiesBuilder.fastLoad(
								group.getTypeSettings()
							).build();

						for (String excludedTypeSetting :
								_EXCLUDED_TYPE_SETTINGS) {

							unicodeProperties.remove(excludedTypeSetting);
						}

						return unicodeProperties;
					});
			}
		};
	}

	private Group _updateGroup(Group group, Site site) throws Exception {
		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					contextCompany, contextUser)) {

			Group updatedGroup = _groupLocalService.updateGroup(
				group.getGroupId(),
				_getParentGroupId(
					group, site.getParentSiteExternalReferenceCode(),
					site.getParentSiteKey()),
				_getNameMap(site), _getLocalizationMap(site.getDescription()),
				_getType(site.getMembershipType()),
				_isManualMembership(site.getManualMembership()),
				_getMembershipRestriction(site.getMembershipRestriction()),
				site.getFriendlyUrlPath(), false, _isActive(site.getActive()),
				_getServiceContext());

			if (Validator.isNotNull(site.getTypeSettings())) {
				updatedGroup = _groupService.updateGroup(
					updatedGroup.getGroupId(),
					_getTypeSettings(
						site.getTypeSettings(),
						group.getTypeSettingsProperties()));
			}

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
	private Localization _localization;

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
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Site;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.resource.v1_0.SiteResource;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.validation.ValidationException;

import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site.properties",
	scope = ServiceScope.PROTOTYPE, service = SiteResource.class
)
public class SiteResourceImpl extends BaseSiteResourceImpl {

	@Override
	public Page<Site> getMyUserAccountSitesPage(Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_groupService.getUserSitesGroups(
					contextUser.getUserId(), pagination.getStartPosition(),
					pagination.getEndPosition()),
				this::_toSite),
			pagination, _groupService.getUserSitesGroupsCount());
	}

	@Override
	public Site getSite(Long siteId) throws Exception {
		return _toSite(_groupService.getGroup(siteId));
	}

	@Override
	public Site getSiteByFriendlyUrlPath(String url) throws Exception {
		Group group = _groupLocalService.fetchFriendlyURLGroup(
			contextCompany.getCompanyId(), "/" + url);

		if (group == null) {
			throw new ValidationException(
				"No site exists with friendly URL " + url);
		}

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), group,
			ActionKeys.VIEW);

		return _toSite(group);
	}

	private Site _toSite(Group group) throws Exception {
		return new Site() {
			{
				setAvailableLanguages(
					() -> {
						Set<Locale> availableLocales =
							_language.getAvailableLocales(group.getGroupId());

						return LocaleUtil.toW3cLanguageIds(
							availableLocales.toArray(new Locale[0]));
					});

				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(group.getCreatorUserId())));
				setDescription(
					() -> group.getDescription(
						contextAcceptLanguage.getPreferredLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						group.getDescriptionMap()));
				setDescriptiveName(
					() -> group.getDescriptiveName(
						contextAcceptLanguage.getPreferredLocale()));
				setFriendlyUrlPath(group::getFriendlyURL);
				setId(group::getGroupId);
				setKey(group::getGroupKey);
				setMembershipType(group::getTypeLabel);
				setName(
					() -> group.getName(
						contextAcceptLanguage.getPreferredLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						group.getNameMap()));
				setParentSiteId(group::getParentGroupId);
				setSites(
					() -> transformToArray(
						_groupService.getGroups(
							group.getCompanyId(), group.getGroupId(), true),
						SiteResourceImpl.this::_toSite, Site.class));
			}
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
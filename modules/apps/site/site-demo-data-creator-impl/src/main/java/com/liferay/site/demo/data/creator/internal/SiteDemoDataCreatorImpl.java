/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.demo.data.creator.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.demo.data.creator.SiteDemoDataCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = SiteDemoDataCreator.class)
public class SiteDemoDataCreatorImpl implements SiteDemoDataCreator {

	@Override
	public Group create(long companyId) throws PortalException {
		return create(companyId, StringUtil.randomString());
	}

	@Override
	public Group create(long companyId, String name) throws PortalException {
		Company company = _companyLocalService.fetchCompany(companyId);

		User user = company.getGuestUser();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), name
		).build();

		Map<Locale, String> descriptionMap = new HashMap<>();
		int type = GroupConstants.TYPE_SITE_OPEN;
		String friendlyURL =
			StringPool.SLASH + _friendlyURLNormalizer.normalize(name);
		boolean site = true;
		boolean active = true;
		boolean manualMembership = true;
		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		Group group = _groupLocalService.addGroup(
			StringPool.BLANK, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, type,
			null, manualMembership, membershipRestriction, friendlyURL, site,
			false, active, null);

		_groupIds.add(group.getGroupId());

		return group;
	}

	@Override
	public void delete() throws PortalException {
		for (long groupId : _groupIds) {
			try {
				_groupLocalService.deleteGroup(groupId);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}

			_groupIds.remove(groupId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteDemoDataCreatorImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	private final List<Long> _groupIds = new CopyOnWriteArrayList<>();

	@Reference
	private GroupLocalService _groupLocalService;

}
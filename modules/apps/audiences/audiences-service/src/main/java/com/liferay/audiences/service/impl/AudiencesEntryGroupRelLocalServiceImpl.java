/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.impl;

import com.liferay.audiences.exception.AudiencesEntryGroupERCException;
import com.liferay.audiences.listener.AudiencesEntryGroupRelListener;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.base.AudiencesEntryGroupRelLocalServiceBaseImpl;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.audiences.model.AudiencesEntryGroupRel",
	service = AopService.class
)
public class AudiencesEntryGroupRelLocalServiceImpl
	extends AudiencesEntryGroupRelLocalServiceBaseImpl {

	@Override
	public AudiencesEntryGroupRel addAudiencesEntryGroupRel(
			long userId, String audienceEntryERC, String groupERC)
		throws PortalException {

		AudiencesEntryGroupRel audiencesEntryGroupRel =
			audiencesEntryGroupRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		audiencesEntryGroupRel.setCompanyId(user.getCompanyId());
		audiencesEntryGroupRel.setUserId(user.getUserId());
		audiencesEntryGroupRel.setUserName(user.getFullName());

		audiencesEntryGroupRel.setAudienceEntryERC(audienceEntryERC);
		audiencesEntryGroupRel.setGroupERC(groupERC);

		return audiencesEntryGroupRelPersistence.update(audiencesEntryGroupRel);
	}

	@Override
	public List<AudiencesEntryGroupRel> addAudiencesEntryGroupRels(
			long userId, String audienceEntryERC, String[] groupERCs)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(user.getCompanyId(), groupERCs);

		return _addAudiencesEntryGroupRels(userId, audienceEntryERC, groupERCs);
	}

	@Override
	public void deleteAudiencesEntryGroupRelsByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		audiencesEntryGroupRelPersistence.removeByC_AEERC(
			companyId, audienceEntryERC);
	}

	@Override
	public void deleteAudiencesEntryGroupRelsByGroupERC(
		long companyId, String groupERC) {

		audiencesEntryGroupRelPersistence.removeByC_GERC(companyId, groupERC);
	}

	@Override
	public List<AudiencesEntryGroupRel>
		getAudiencesEntryGroupRelsByAudienceEntryERC(
			long companyId, String audienceEntryERC) {

		return audiencesEntryGroupRelPersistence.findByC_AEERC(
			companyId, audienceEntryERC);
	}

	@Override
	public List<AudiencesEntryGroupRel> updateAudiencesEntryGroupRels(
			long userId, String audienceEntryERC, String[] groupERCs)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validate(user.getCompanyId(), groupERCs);

		List<String> removedGroupERCs = new ArrayList<>();

		for (AudiencesEntryGroupRel audiencesEntryGroupRel :
				audiencesEntryGroupRelPersistence.findByC_AEERC(
					user.getCompanyId(), audienceEntryERC)) {

			if (!ArrayUtil.contains(
					groupERCs, audiencesEntryGroupRel.getGroupERC())) {

				removedGroupERCs.add(audiencesEntryGroupRel.getGroupERC());
			}
		}

		deleteAudiencesEntryGroupRelsByAudienceEntryERC(
			user.getCompanyId(), audienceEntryERC);

		audiencesEntryGroupRelPersistence.flush();

		List<AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_addAudiencesEntryGroupRels(userId, audienceEntryERC, groupERCs);

		if (ArrayUtil.isEmpty(groupERCs) || removedGroupERCs.isEmpty()) {
			return audiencesEntryGroupRels;
		}

		for (AudiencesEntryGroupRelListener audiencesEntryGroupRelListener :
				_serviceTrackerList) {

			audiencesEntryGroupRelListener.onDeleteAudiencesEntryGroupRels(
				user.getCompanyId(), audienceEntryERC,
				ArrayUtil.toStringArray(removedGroupERCs));
		}

		return audiencesEntryGroupRels;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, AudiencesEntryGroupRelListener.class);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_serviceTrackerList.close();
	}

	private List<AudiencesEntryGroupRel> _addAudiencesEntryGroupRels(
			long userId, String audienceEntryERC, String[] groupERCs)
		throws PortalException {

		List<AudiencesEntryGroupRel> audiencesEntryGroupRels =
			new ArrayList<>();

		if (groupERCs == null) {
			return audiencesEntryGroupRels;
		}

		for (String groupERC : groupERCs) {
			audiencesEntryGroupRels.add(
				addAudiencesEntryGroupRel(userId, audienceEntryERC, groupERC));
		}

		return audiencesEntryGroupRels;
	}

	private void _validate(long companyId, String[] groupERCs)
		throws PortalException {

		if (groupERCs == null) {
			return;
		}

		for (String groupERC : groupERCs) {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				groupERC, companyId);

			if ((group == null) || group.isCompany() || !group.isSite()) {
				throw new AudiencesEntryGroupERCException(
					StringBundler.concat(
						"External reference code \"", groupERC,
						"\" does not reference a site"));
			}
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	private ServiceTrackerList<AudiencesEntryGroupRelListener>
		_serviceTrackerList;

	@Reference
	private UserLocalService _userLocalService;

}
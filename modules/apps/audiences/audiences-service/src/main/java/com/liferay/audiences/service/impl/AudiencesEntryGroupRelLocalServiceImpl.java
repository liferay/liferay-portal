/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.impl;

import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.base.AudiencesEntryGroupRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

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

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.exception.CTStagingEnabledException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTPreferencesTable;
import com.liferay.change.tracking.service.base.CTPreferencesServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTCollectionPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = {
		"json.web.service.context.name=ct",
		"json.web.service.context.path=CTPreferences"
	},
	service = AopService.class
)
public class CTPreferencesServiceImpl extends CTPreferencesServiceBaseImpl {

	@Override
	public CTPreferences checkoutCTCollection(
			long companyId, long userId, long ctCollectionId)
		throws PortalException {

		if (userId == 0) {
			return enablePublications(companyId, true);
		}

		if (ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			CTCollection ctCollection =
				_ctCollectionPersistence.fetchByPrimaryKey(ctCollectionId);

			if ((ctCollection == null) || !ctCollection.isInProgress()) {
				return null;
			}

			_ctCollectionModelResourcePermission.check(
				getPermissionChecker(), ctCollection, ActionKeys.VIEW);
		}

		CTPreferences ctPreferences =
			ctPreferencesLocalService.getCTPreferences(companyId, userId);

		long currentCtCollectionId = ctPreferences.getCtCollectionId();

		if (currentCtCollectionId != ctCollectionId) {
			ctPreferences.setCtCollectionId(ctCollectionId);

			if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
				ctPreferences.setPreviousCtCollectionId(currentCtCollectionId);
			}
			else {
				ctPreferences.setPreviousCtCollectionId(
					CTConstants.CT_COLLECTION_ID_PRODUCTION);
			}

			ctPreferences = ctPreferencesPersistence.update(ctPreferences);
		}

		return ctPreferences;
	}

	@Override
	public CTPreferences enablePublications(long companyId, boolean enable)
		throws PortalException {

		PortletPermissionUtil.check(
			getPermissionChecker(), CTPortletKeys.PUBLICATIONS,
			ActionKeys.CONFIGURATION);

		if (enable) {
			for (Group group :
					_groupLocalService.<List<Group>>dslQuery(
						DSLQueryFactoryUtil.select(
							GroupTable.INSTANCE
						).from(
							GroupTable.INSTANCE
						).where(
							GroupTable.INSTANCE.companyId.eq(
								companyId
							).and(
								GroupTable.INSTANCE.liveGroupId.neq(
									GroupConstants.DEFAULT_LIVE_GROUP_ID
								).or(
									GroupTable.INSTANCE.typeSettings.like(
										"%staged=true%")
								).withParentheses()
							)
						))) {

				if (group.isStaged() || group.isStagingGroup()) {
					throw new CTStagingEnabledException();
				}
			}

			return ctPreferencesLocalService.getCTPreferences(companyId, 0);
		}

		for (CTPreferences ctPreferences :
				ctPreferencesPersistence.<List<CTPreferences>>dslQuery(
					DSLQueryFactoryUtil.select(
						CTPreferencesTable.INSTANCE
					).from(
						CTPreferencesTable.INSTANCE
					).where(
						CTPreferencesTable.INSTANCE.companyId.eq(companyId)
					))) {

			ctPreferencesPersistence.remove(ctPreferences);
		}

		return null;
	}

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTCollectionPersistence _ctCollectionPersistence;

	@Reference
	private GroupLocalService _groupLocalService;

}
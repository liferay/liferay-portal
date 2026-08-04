/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Balázs Sáfrány-Kovalik
 */
public class LayoutPageTemplateStructureRelUpgradeProcess
	extends UpgradeProcess {

	public LayoutPageTemplateStructureRelUpgradeProcess(
		LayoutLocalService layoutLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		UserLocalService userLocalService) {

		_layoutLocalService = layoutLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct LayoutPageTemplateStructureRel.",
					"ctCollectionId, LayoutPageTemplateStructureRel.",
					"lPageTemplateStructureRelId, ",
					"LayoutPageTemplateStructureRel.",
					"layoutPageTemplateStructureId, ",
					"LayoutPageTemplateStructure.companyId, ",
					"LayoutPageTemplateStructure.plid, ",
					"LayoutPageTemplateStructure.userId from ",
					"LayoutPageTemplateStructure inner join ",
					"LayoutPageTemplateStructureRel on ",
					"LayoutPageTemplateStructureRel.",
					"layoutPageTemplateStructureId = ",
					"LayoutPageTemplateStructure.",
					"layoutPageTemplateStructureId where ",
					"LayoutPageTemplateStructureRel.segmentsExperienceId = 0"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long ctCollectionId = resultSet.getLong("ctCollectionId");

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollectionId)) {

					_repairLayoutPageTemplateStructureRel(
						resultSet.getLong("companyId"), ctCollectionId,
						resultSet.getLong("layoutPageTemplateStructureId"),
						resultSet.getLong("lPageTemplateStructureRelId"),
						resultSet.getLong("plid"), resultSet.getLong("userId"));
				}
			}
		}
	}

	private void _deleteLayoutPageTemplateStructureRel(
			long ctCollectionId, long layoutPageTemplateStructureRelId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"delete from LayoutPageTemplateStructureRel where ",
					"ctCollectionId = ? and lPageTemplateStructureRelId = ",
					"?"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, layoutPageTemplateStructureRelId);

			preparedStatement.executeUpdate();
		}
	}

	private long _getDefaultSegmentsExperienceId(
			long companyId, long plid, long userId)
		throws Exception {

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid);

		if (defaultSegmentsExperienceId !=
				SegmentsExperienceConstants.ID_DEFAULT) {

			return defaultSegmentsExperienceId;
		}

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			userId = _userLocalService.getGuestUserId(companyId);
		}

		SegmentsExperience defaultSegmentsExperience =
			_segmentsExperienceLocalService.addDefaultSegmentsExperience(
				null, userId, plid, new ServiceContext());

		return defaultSegmentsExperience.getSegmentsExperienceId();
	}

	private boolean _hasDefaultSegmentsExperienceLayoutPageTemplateStructureRel(
			long ctCollectionId, long defaultSegmentsExperienceId,
			long layoutPageTemplateStructureId,
			long layoutPageTemplateStructureRelId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select lPageTemplateStructureRelId from ",
					"LayoutPageTemplateStructureRel where ctCollectionId in ",
					"(0, ?) and lPageTemplateStructureRelId != ? and ",
					"layoutPageTemplateStructureId = ? and ",
					"segmentsExperienceId = ?"))) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, layoutPageTemplateStructureRelId);
			preparedStatement.setLong(3, layoutPageTemplateStructureId);
			preparedStatement.setLong(4, defaultSegmentsExperienceId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private void _repairLayoutPageTemplateStructureRel(
			long companyId, long ctCollectionId,
			long layoutPageTemplateStructureId,
			long layoutPageTemplateStructureRelId, long plid, long userId)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			_deleteLayoutPageTemplateStructureRel(
				ctCollectionId, layoutPageTemplateStructureRelId);

			return;
		}

		long defaultSegmentsExperienceId = _getDefaultSegmentsExperienceId(
			companyId, plid, userId);

		if (_hasDefaultSegmentsExperienceLayoutPageTemplateStructureRel(
				ctCollectionId, defaultSegmentsExperienceId,
				layoutPageTemplateStructureId,
				layoutPageTemplateStructureRelId)) {

			_deleteLayoutPageTemplateStructureRel(
				ctCollectionId, layoutPageTemplateStructureRelId);
		}
		else {
			_updateLayoutPageTemplateStructureRel(
				ctCollectionId, defaultSegmentsExperienceId,
				layoutPageTemplateStructureRelId);
		}
	}

	private void _updateLayoutPageTemplateStructureRel(
			long ctCollectionId, long defaultSegmentsExperienceId,
			long layoutPageTemplateStructureRelId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update LayoutPageTemplateStructureRel set ",
					"segmentsExperienceId = ? where ctCollectionId = ? and ",
					"lPageTemplateStructureRelId = ? and segmentsExperienceId ",
					"= 0"))) {

			preparedStatement.setLong(1, defaultSegmentsExperienceId);
			preparedStatement.setLong(2, ctCollectionId);
			preparedStatement.setLong(3, layoutPageTemplateStructureRelId);

			preparedStatement.executeUpdate();
		}
	}

	private final LayoutLocalService _layoutLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private final UserLocalService _userLocalService;

}
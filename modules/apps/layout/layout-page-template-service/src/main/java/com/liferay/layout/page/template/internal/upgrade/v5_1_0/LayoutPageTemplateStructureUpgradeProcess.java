/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v5_1_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateStructureUpgradeProcess extends UpgradeProcess {

	public LayoutPageTemplateStructureUpgradeProcess(
		LayoutLocalService layoutLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		UserLocalService userLocalService) {

		_layoutLocalService = layoutLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select layoutPageTemplateStructureId, companyId, userId," +
					"classPK from LayoutPageTemplateStructure");
			SafeCloseable safeCloseable1 = db.addTemporaryIndex(
				connection, "FragmentEntryLink", false, "segmentsExperienceId",
				"plid");
			SafeCloseable safeCloseable2 = db.addTemporaryIndex(
				connection, "SegmentsExperiment", false, "plid",
				"segmentsExperienceId")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long layoutPageTemplateStructureId = resultSet.getLong(
						"layoutPageTemplateStructureId");
					long companyId = resultSet.getLong("companyId");
					long userId = resultSet.getLong("userId");
					long classPK = resultSet.getLong("classPK");

					_addDefaultSegmentsExperience(
						classPK, companyId, layoutPageTemplateStructureId,
						userId);
				}
			}
		}
	}

	private void _addDefaultSegmentsExperience(
			long classPK, long companyId, long layoutPageTemplateStructureId,
			long userId)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(classPK);

		if (layout == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No layout found with PLID " + classPK);
			}

			return;
		}

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				classPK);

		if (defaultSegmentsExperienceId <= 0) {
			User user = _userLocalService.fetchUser(userId);

			if (user == null) {
				userId = _userLocalService.getGuestUserId(companyId);
			}

			SegmentsExperience defaultSegmentsExperience =
				_segmentsExperienceLocalService.addDefaultSegmentsExperience(
					null, userId, classPK, new ServiceContext());

			defaultSegmentsExperienceId =
				defaultSegmentsExperience.getSegmentsExperienceId();
		}

		long draftClassPK = 0;
		long publishedClassPK = 0;

		if (layout.isDraftLayout()) {
			draftClassPK = layout.getPlid();
			publishedClassPK = layout.getClassPK();
		}
		else {
			Layout draftLayout = _layoutLocalService.fetchDraftLayout(
				layout.getPlid());

			if (draftLayout != null) {
				draftClassPK = draftLayout.getPlid();
			}

			publishedClassPK = layout.getPlid();
		}

		_updateFragmentEntryLinks(
			defaultSegmentsExperienceId, layout.getPlid());

		_updateLayoutPageTemplateStructureRels(
			defaultSegmentsExperienceId, layoutPageTemplateStructureId);

		_updateSegmentsExperiments(
			defaultSegmentsExperienceId, draftClassPK, publishedClassPK);

		_updateSegmentsExperimentRels(
			defaultSegmentsExperienceId, draftClassPK, publishedClassPK);
	}

	private void _updateFragmentEntryLinks(
			long defaultSegmentsExperienceId, long layoutPLid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update FragmentEntryLink set segmentsExperienceId = ? where " +
					"segmentsExperienceId = 0 and plid = ?")) {

			preparedStatement.setLong(1, defaultSegmentsExperienceId);
			preparedStatement.setLong(2, layoutPLid);

			preparedStatement.executeUpdate();
		}
	}

	private void _updateLayoutPageTemplateStructureRels(
			long defaultSegmentsExperienceId,
			long layoutPageTemplateStructureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update LayoutPageTemplateStructureRel set " +
					"segmentsExperienceId = ? where segmentsExperienceId = 0 " +
						"and layoutPageTemplateStructureId = ?")) {

			preparedStatement.setLong(1, defaultSegmentsExperienceId);
			preparedStatement.setLong(2, layoutPageTemplateStructureId);

			preparedStatement.executeUpdate();
		}
	}

	private void _updateSegmentsExperimentRels(
			long defaultSegmentsExperienceId, long draftPlid,
			long publishedPlid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update SegmentsExperimentRel set segmentsExperienceId = ",
					"? where segmentsExperienceId = 0 and ",
					"segmentsExperimentId in (select ",
					"SegmentsExperiment.segmentsExperimentId from ",
					"SegmentsExperiment where plid = ? or plid = ?)"))) {

			preparedStatement.setLong(1, defaultSegmentsExperienceId);
			preparedStatement.setLong(2, draftPlid);
			preparedStatement.setLong(3, publishedPlid);

			preparedStatement.executeUpdate();
		}
	}

	private void _updateSegmentsExperiments(
			long defaultSegmentsExperienceId, long draftPlid,
			long publishedPlid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update SegmentsExperiment set segmentsExperienceId = ? " +
					"where segmentsExperienceId = 0 and (plid = ? or plid = " +
						"?)")) {

			preparedStatement.setLong(1, defaultSegmentsExperienceId);
			preparedStatement.setLong(2, draftPlid);
			preparedStatement.setLong(3, publishedPlid);

			preparedStatement.executeUpdate();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureUpgradeProcess.class);

	private final LayoutLocalService _layoutLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private final UserLocalService _userLocalService;

}
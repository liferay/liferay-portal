/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.impl;

import com.liferay.layout.content.exception.DuplicateLayoutContentVersionPreviewException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.model.LayoutContentVersionPreviewTable;
import com.liferay.layout.content.service.base.LayoutContentVersionPreviewLocalServiceBaseImpl;
import com.liferay.layout.content.service.persistence.LayoutContentVersionPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "model.class.name=com.liferay.layout.content.model.LayoutContentVersionPreview",
	service = AopService.class
)
public class LayoutContentVersionPreviewLocalServiceImpl
	extends LayoutContentVersionPreviewLocalServiceBaseImpl {

	@Override
	public LayoutContentVersionPreview addLayoutContentVersionPreview(
			long userId, long layoutContentVersionId, String html,
			String languageId, String segmentsExperienceERC)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		_validate(layoutContentVersionId, languageId, segmentsExperienceERC);

		LayoutContentVersionPreview layoutContentVersionPreview =
			layoutContentVersionPreviewPersistence.create(
				counterLocalService.increment(
					LayoutContentVersionPreview.class.getName()));

		layoutContentVersionPreview.setGroupId(
			layoutContentVersion.getGroupId());
		layoutContentVersionPreview.setCompanyId(
			layoutContentVersion.getCompanyId());
		layoutContentVersionPreview.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		layoutContentVersionPreview.setUserName(user.getFullName());

		layoutContentVersionPreview.setLayoutContentVersionId(
			layoutContentVersionId);
		layoutContentVersionPreview.setHtml(html);
		layoutContentVersionPreview.setLanguageId(languageId);
		layoutContentVersionPreview.setSegmentsExperienceERC(
			segmentsExperienceERC);

		return layoutContentVersionPreviewPersistence.update(
			layoutContentVersionPreview);
	}

	@Override
	public void deleteLayoutContentVersionPreviews(long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		layoutContentVersionPreviewPersistence.removeByLayoutContentVersionId(
			layoutContentVersionId);
	}

	@Override
	public LayoutContentVersionPreview fetchLayoutContentVersionPreview(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersionPreviewPersistence.fetchByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	@Override
	public List<LayoutContentVersionPreview> getLayoutContentVersionPreviews(
			long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersionPreviewPersistence.
			findByLayoutContentVersionId(layoutContentVersionId);
	}

	@Override
	public Map<String, List<String>> getSegmentsExperienceERCsLanguageIds(
			long layoutContentVersionId)
		throws PortalException {

		Map<String, List<String>> segmentsExperienceERCsLanguageIds =
			new HashMap<>();

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		List<Object[]> rows = dslQuery(
			DSLQueryFactoryUtil.select(
				LayoutContentVersionPreviewTable.INSTANCE.segmentsExperienceERC,
				LayoutContentVersionPreviewTable.INSTANCE.languageId
			).from(
				LayoutContentVersionPreviewTable.INSTANCE
			).where(
				LayoutContentVersionPreviewTable.INSTANCE.
					layoutContentVersionId.eq(layoutContentVersionId)
			).orderBy(
				LayoutContentVersionPreviewTable.INSTANCE.segmentsExperienceERC.
					ascending(),
				LayoutContentVersionPreviewTable.INSTANCE.languageId.ascending()
			));

		for (Object[] row : rows) {
			List<String> languageIds =
				segmentsExperienceERCsLanguageIds.computeIfAbsent(
					(String)row[0], segmentsExperienceERC -> new ArrayList<>());

			languageIds.add((String)row[1]);
		}

		return segmentsExperienceERCsLanguageIds;
	}

	private void _validate(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws PortalException {

		LayoutContentVersionPreview layoutContentVersionPreview =
			layoutContentVersionPreviewPersistence.fetchByLCVI_L_SEERC(
				layoutContentVersionId, languageId, segmentsExperienceERC);

		if (layoutContentVersionPreview == null) {
			return;
		}

		throw new DuplicateLayoutContentVersionPreviewException(
			StringBundler.concat(
				"Duplicate layout content version preview for layout content ",
				"version ", layoutContentVersionId, ", language ID ",
				languageId,
				", and segments experience external reference code ",
				segmentsExperienceERC));
	}

	@Reference
	private LayoutContentVersionPersistence _layoutContentVersionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}
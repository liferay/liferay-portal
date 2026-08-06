/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.impl;

import com.liferay.layout.content.exception.DuplicateLayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.LayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.LayoutContentVersionNameException;
import com.liferay.layout.content.exception.RequiredLayoutContentVersionException;
import com.liferay.layout.content.exception.UnsupportedLayoutLayoutContentVersionException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.base.LayoutContentVersionLocalServiceBaseImpl;
import com.liferay.layout.content.util.comparator.LayoutContentVersionVersionComparator;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "model.class.name=com.liferay.layout.content.model.LayoutContentVersion",
	service = AopService.class
)
public class LayoutContentVersionLocalServiceImpl
	extends LayoutContentVersionLocalServiceBaseImpl {

	@Override
	public LayoutContentVersion addLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<Locale, String> nameMap, long plid, int status)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		_validateExternalReferenceCode(
			externalReferenceCode, layout.getGroupId());
		_validateLayout(layout);

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.create(
				counterLocalService.increment(
					LayoutContentVersion.class.getName()));

		int version = _getNextVersion(plid);

		if (Validator.isNull(externalReferenceCode)) {
			String defaultExternalReferenceCode =
				layout.getExternalReferenceCode() + "_v_" + version;

			if (defaultExternalReferenceCode.length() <=
					ModelHintsUtil.getMaxLength(
						LayoutContentVersion.class.getName(),
						"externalReferenceCode")) {

				externalReferenceCode = defaultExternalReferenceCode;
			}
		}

		layoutContentVersion.setExternalReferenceCode(externalReferenceCode);

		layoutContentVersion.setGroupId(layout.getGroupId());
		layoutContentVersion.setCompanyId(layout.getCompanyId());
		layoutContentVersion.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		layoutContentVersion.setUserName(user.getFullName());

		layoutContentVersion.setData(data);
		layoutContentVersion.setDataHash(
			DigesterUtil.digestHex(
				DigesterUtil.SHA_256, GetterUtil.getString(data)));
		layoutContentVersion.setNameMap(
			MapUtil.isEmpty(nameMap) ? layout.getNameMap() : nameMap);
		layoutContentVersion.setPlid(plid);
		layoutContentVersion.setSpecSchemaVersion("v1.0");
		layoutContentVersion.setVersion(version);
		layoutContentVersion.setStatus(status);
		layoutContentVersion.setStatusByUserId(userId);
		layoutContentVersion.setStatusByUserName(user.getFullName());
		layoutContentVersion.setStatusDate(new Date());

		return layoutContentVersionPersistence.update(layoutContentVersion);
	}

	@Override
	public LayoutContentVersion addOrUpdateLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<Locale, String> nameMap, long plid, int status)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		_validateLayout(layout);

		LayoutContentVersion latestLayoutContentVersion =
			layoutContentVersionPersistence.fetchByPlid_First(
				plid, LayoutContentVersionVersionComparator.getInstance(false));

		String dataHash = DigesterUtil.digestHex(
			DigesterUtil.SHA_256, GetterUtil.getString(data));

		if ((latestLayoutContentVersion == null) ||
			!dataHash.equals(latestLayoutContentVersion.getDataHash())) {

			return addLayoutContentVersion(
				externalReferenceCode, userId, data, nameMap, plid, status);
		}

		latestLayoutContentVersion.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		latestLayoutContentVersion.setUserName(user.getFullName());

		return layoutContentVersionPersistence.update(
			latestLayoutContentVersion);
	}

	@Override
	public LayoutContentVersion deleteLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		_validateLayout(
			_layoutLocalService.getLayout(layoutContentVersion.getPlid()));

		if ((layoutContentVersion.getStatus() ==
				WorkflowConstants.STATUS_APPROVED) &&
			(layoutContentVersion.getLayoutContentVersionId() ==
				getLatestApprovedLayoutContentVersionId(
					layoutContentVersion.getPlid()))) {

			throw new RequiredLayoutContentVersionException();
		}

		return layoutContentVersionPersistence.remove(layoutContentVersionId);
	}

	@Override
	public long getLatestApprovedLayoutContentVersionId(long plid) {
		LayoutContentVersion latestApprovedLayoutContentVersion =
			layoutContentVersionPersistence.fetchByP_S_First(
				plid, WorkflowConstants.STATUS_APPROVED,
				LayoutContentVersionVersionComparator.getInstance(false));

		if (latestApprovedLayoutContentVersion == null) {
			return 0;
		}

		return latestApprovedLayoutContentVersion.getLayoutContentVersionId();
	}

	@Override
	public LayoutContentVersion getLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			super.getLayoutContentVersion(layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersion;
	}

	@Override
	public LayoutContentVersion getLayoutContentVersionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			super.getLayoutContentVersionByExternalReferenceCode(
				externalReferenceCode, groupId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersion;
	}

	@Override
	public List<LayoutContentVersion> getLayoutContentVersions(long plid)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		return layoutContentVersionPersistence.findByPlid(plid);
	}

	@Override
	public LayoutContentVersion updateLayoutContentVersion(
			long layoutContentVersionId, Map<Locale, String> nameMap)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		_validateLayout(
			_layoutLocalService.getLayout(layoutContentVersion.getPlid()));

		if (MapUtil.isEmpty(nameMap)) {
			throw new LayoutContentVersionNameException("Name is null");
		}

		layoutContentVersion.setNameMap(nameMap);

		return layoutContentVersionPersistence.update(layoutContentVersion);
	}

	private int _getNextVersion(long plid) {
		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.fetchByPlid_First(
				plid, LayoutContentVersionVersionComparator.getInstance(false));

		if (layoutContentVersion == null) {
			return 1;
		}

		return layoutContentVersion.getVersion() + 1;
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (layoutContentVersion != null) {
			throw new DuplicateLayoutContentVersionExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate layout content version external reference code ",
					externalReferenceCode, " in group ", groupId));
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			LayoutContentVersion.class.getName(), "externalReferenceCode");

		if (externalReferenceCode.length() > maxLength) {
			throw new LayoutContentVersionExternalReferenceCodeException(
				"External reference code must be less than " + maxLength +
					" characters");
		}
	}

	private void _validateLayout(Layout layout) throws PortalException {
		if (!layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new UnsupportedLayoutLayoutContentVersionException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new UnsupportedLayoutLayoutContentVersionException();
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
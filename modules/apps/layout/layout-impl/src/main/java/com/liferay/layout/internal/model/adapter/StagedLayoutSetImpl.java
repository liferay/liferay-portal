/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.model.adapter;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.layout.set.model.adapter.StagedLayoutSet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutSetWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.time.Instant;

import java.util.Date;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * @author Máté Thurzó
 */
public class StagedLayoutSetImpl
	extends LayoutSetWrapper implements StagedLayoutSet {

	public StagedLayoutSetImpl(LayoutSet layoutSet) {
		super(layoutSet);

		Objects.requireNonNull(
			layoutSet,
			"Unable to create a new staged layout set for a null layout set");

		_layoutSet = layoutSet;

		// Last publish date

		UnicodeProperties settingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		String lastPublishDateString = settingsUnicodeProperties.getProperty(
			"last-publish-date");

		Instant instant = Instant.ofEpochMilli(
			GetterUtil.getLong(lastPublishDateString));

		_lastPublishDate = Date.from(instant);

		// Layout set prototype

		if (Validator.isNotNull(layoutSet.getLayoutSetPrototypeUuid())) {
			LayoutSetPrototype layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.
					fetchLayoutSetPrototypeByUuidAndCompanyId(
						layoutSet.getLayoutSetPrototypeUuid(),
						layoutSet.getCompanyId());

			if (layoutSetPrototype != null) {
				_layoutSetPrototypeName = layoutSetPrototype.getName(
					LocaleUtil.getDefault());
			}
		}

		try {
			Group layoutSetGroup = layoutSet.getGroup();

			_userId = layoutSetGroup.getCreatorUserId();

			User user = UserLocalServiceUtil.getUser(_userId);

			_userName = user.getFullName();
			_userUuid = user.getUuid();
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	@Override
	public Object clone() {
		return new StagedLayoutSetImpl((LayoutSet)_layoutSet.clone());
	}

	@Override
	public long getGroupId() {
		return _layoutSet.getGroupId();
	}

	@Override
	public Date getLastPublishDate() {
		return _lastPublishDate;
	}

	@Override
	public LayoutSet getLayoutSet() {
		return _layoutSet;
	}

	@Override
	public String getLayoutSetPrototypeName() {
		return _layoutSetPrototypeName;
	}

	@Override
	public Class<?> getModelClass() {
		return StagedLayoutSet.class;
	}

	@Override
	public String getModelClassName() {
		return StagedLayoutSet.class.getName();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _layoutSet.getPrimaryKeyObj();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(StagedLayoutSet.class);
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public String getUserName() {
		return _userName;
	}

	@Override
	public String getUserUuid() {
		return _userUuid;
	}

	@Override
	public String getUuid() {
		return String.valueOf(_layoutSet.isPrivateLayout());
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_lastPublishDate = lastPublishDate;

		UnicodeProperties settingsUnicodeProperties = getSettingsProperties();

		settingsUnicodeProperties.setProperty(
			"last-publish-date", String.valueOf(_lastPublishDate.getTime()));
	}

	public void setLayoutSet(LayoutSet layoutSet) {
		_layoutSet = layoutSet;
	}

	public void setLayoutSetPrototypeName(String layoutSetPrototypeName) {
		_layoutSetPrototypeName = layoutSetPrototypeName;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;
	}

	@Override
	public void setUserUuid(String userUuid) {
		_userUuid = userUuid;
	}

	@Override
	public void setUuid(String uuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVirtualHostnames(
		NavigableMap<String, String> virtualHostnames) {

		_layoutSet.setVirtualHostnames(virtualHostnames);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedLayoutSetImpl.class);

	private Date _lastPublishDate;
	private LayoutSet _layoutSet;
	private String _layoutSetPrototypeName;
	private long _userId;
	private String _userName;
	private String _userUuid;

}
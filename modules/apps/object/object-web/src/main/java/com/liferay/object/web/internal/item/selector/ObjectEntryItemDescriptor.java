/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Guilherme Camacho
 */
public class ObjectEntryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public ObjectEntryItemDescriptor(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		Portal portal) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;
		_objectEntry = objectEntry;
		_portal = portal;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		if (!_objectDefinition.isDefaultStorageType()) {
			return null;
		}

		return _objectEntry.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"className", _objectDefinition.getClassName()
		).put(
			"classNameId",
			_portal.getClassNameId(_objectDefinition.getClassName())
		).put(
			"classPK",
			() -> {
				if (!_objectDefinition.isDefaultStorageType()) {
					return null;
				}

				return String.valueOf(_objectEntry.getObjectEntryId());
			}
		).put(
			"externalReferenceCode",
			() -> {
				if (!_objectDefinition.isDefaultStorageType()) {
					return _objectEntry.getExternalReferenceCode();
				}

				return null;
			}
		).put(
			"title", getTitle(themeDisplay.getLocale())
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return _getId();
	}

	@Override
	public String getTitle(Locale locale) {
		if (!_objectDefinition.isDefaultStorageType()) {
			return _objectEntry.getExternalReferenceCode();
		}

		try {
			return _objectEntry.getTitleValue();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Override
	public long getUserId() {
		if (!_objectDefinition.isDefaultStorageType()) {
			return 0;
		}

		return _objectEntry.getUserId();
	}

	@Override
	public String getUserName() {
		if (!_objectDefinition.isDefaultStorageType()) {
			return StringPool.BLANK;
		}

		return _objectEntry.getUserName();
	}

	private String _getId() {
		if (!_objectDefinition.isDefaultStorageType()) {
			return _objectEntry.getExternalReferenceCode();
		}

		return String.valueOf(_objectEntry.getObjectEntryId());
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntry _objectEntry;
	private final Portal _portal;

}
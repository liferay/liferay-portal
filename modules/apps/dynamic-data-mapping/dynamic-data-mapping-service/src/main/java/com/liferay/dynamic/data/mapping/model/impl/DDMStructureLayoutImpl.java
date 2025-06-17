/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.cache.CacheField;

/**
 * @author Marcellus Tavares
 */
public class DDMStructureLayoutImpl extends DDMStructureLayoutBaseImpl {

	@Override
	public DDMFormLayout getDDMFormLayout() {
		if (_ddmFormLayout == null) {
			try {
				_ddmFormLayout =
					DDMStructureLayoutLocalServiceUtil.
						getStructureLayoutDDMFormLayout(this);

				ddmFormLayoutUpdateEntityCacheConsumer.accept(_ddmFormLayout);
			}
			catch (Exception exception) {
				_log.error(exception);

				return new DDMFormLayout();
			}
		}

		return new DDMFormLayout(_ddmFormLayout);
	}

	@Override
	public DDMStructure getDDMStructure() throws PortalException {
		DDMStructureVersion ddmStructureVersion =
			DDMStructureVersionLocalServiceUtil.getDDMStructureVersion(
				getStructureVersionId());

		return ddmStructureVersion.getStructure();
	}

	@Override
	public long getDDMStructureId() throws PortalException {
		DDMStructure ddmStructure = getDDMStructure();

		return ddmStructure.getStructureId();
	}

	@Override
	public void setDDMFormLayout(DDMFormLayout ddmFormLayout) {
		_ddmFormLayout = ddmFormLayout;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureLayoutImpl.class);

	@CacheField(methodName = "DDMFormLayout", propagateToInterface = true)
	private DDMFormLayout _ddmFormLayout;

}
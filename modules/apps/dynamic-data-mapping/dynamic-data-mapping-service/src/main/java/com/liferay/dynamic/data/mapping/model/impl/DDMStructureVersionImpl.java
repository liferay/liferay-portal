/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.cache.CacheField;

/**
 * @author Brian Wing Shun Chan
 * @author Leonardo Barros
 */
public class DDMStructureVersionImpl extends DDMStructureVersionBaseImpl {

	@Override
	public DDMForm getDDMForm() {
		if (_ddmForm == null) {
			try {
				_ddmForm =
					DDMStructureVersionLocalServiceUtil.
						getStructureVersionDDMForm(this);

				ddmFormUpdateEntityCacheConsumer.accept(_ddmForm);
			}
			catch (Exception exception) {
				_log.error(exception);

				return new DDMForm();
			}
		}

		return new DDMForm(_ddmForm);
	}

	@Override
	public DDMFormLayout getDDMFormLayout() throws PortalException {
		DDMStructureLayout ddmStructureLayout =
			DDMStructureLayoutLocalServiceUtil.
				getStructureLayoutByStructureVersionId(getStructureVersionId());

		return ddmStructureLayout.getDDMFormLayout();
	}

	@Override
	public DDMStructure getStructure() throws PortalException {
		return DDMStructureLocalServiceUtil.getStructure(getStructureId());
	}

	@Override
	public void setDDMForm(DDMForm ddmForm) {
		_ddmForm = ddmForm;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureVersionImpl.class);

	@CacheField(methodName = "DDMForm", propagateToInterface = true)
	private DDMForm _ddmForm;

}
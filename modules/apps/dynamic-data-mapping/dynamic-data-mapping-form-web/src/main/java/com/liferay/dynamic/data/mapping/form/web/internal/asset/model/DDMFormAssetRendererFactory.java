/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
	service = AssetRendererFactory.class
)
public class DDMFormAssetRendererFactory
	extends BaseAssetRendererFactory<DDMFormInstanceRecord> {

	public static final String TYPE = "form";

	public DDMFormAssetRendererFactory() {
		setCategorizable(false);
		setClassName(DDMFormInstanceRecord.class.getName());
		setPortletId(DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM);
		setSearchable(true);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<DDMFormInstanceRecord> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		DDMFormInstanceRecord formInstanceRecord =
			_ddmFormInstanceRecordLocalService.fetchDDMFormInstanceRecord(
				classPK);

		DDMFormInstanceRecordVersion formInstanceRecordVersion = null;

		if (formInstanceRecord == null) {
			formInstanceRecordVersion =
				_ddmFormInstanceRecordVersionLocalService.
					getFormInstanceRecordVersion(classPK);

			formInstanceRecord =
				formInstanceRecordVersion.getFormInstanceRecord();
		}
		else {
			if (type == TYPE_LATEST) {
				formInstanceRecordVersion =
					formInstanceRecord.getLatestFormInstanceRecordVersion();
			}
			else if (type == TYPE_LATEST_APPROVED) {
				formInstanceRecordVersion =
					formInstanceRecord.getFormInstanceRecordVersion();
			}
			else {
				throw new IllegalArgumentException(
					"Unknown asset renderer type " + type);
			}
		}

		return _createAssetRenderer(
			formInstanceRecord, formInstanceRecordVersion, type);
	}

	@Override
	public String getClassName() {
		return DDMFormInstanceRecord.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "dynamic-data-mapping";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.getFormInstanceRecord(classPK);

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		return _ddmFormInstanceModelResourcePermission.contains(
			permissionChecker, ddmFormInstance, actionId);
	}

	private AssetRenderer<DDMFormInstanceRecord> _createAssetRenderer(
		DDMFormInstanceRecord formInstanceRecord,
		DDMFormInstanceRecordVersion formInstanceRecordVersion, int type) {

		DDMFormAssetRenderer ddmFormAssetRenderer = new DDMFormAssetRenderer(
			formInstanceRecord, _ddmFormInstanceRecordLocalService,
			_ddmFormInstanceRecordModelResourcePermission,
			formInstanceRecordVersion, _ddmFormInstanceVersionLocalService,
			_ddmFormRenderer, _ddmFormValuesFactory, _ddmFormValuesMerger,
			_portal);

		ddmFormAssetRenderer.setAssetRendererType(type);
		ddmFormAssetRenderer.setServletContext(_servletContext);

		return ddmFormAssetRenderer;
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceModelResourcePermission;

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord)"
	)
	private ModelResourcePermission<DDMFormInstanceRecord>
		_ddmFormInstanceRecordModelResourcePermission;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesMerger _ddmFormValuesMerger;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dynamic.data.mapping.form.web)"
	)
	private ServletContext _servletContext;

}
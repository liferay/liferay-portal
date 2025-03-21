/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.dynamic.data.lists.constants.DDLActionKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "jakarta.portlet.name=" + DDLPortletKeys.DYNAMIC_DATA_LISTS,
	service = AssetRendererFactory.class
)
public class DDLRecordAssetRendererFactory
	extends BaseAssetRendererFactory<DDLRecord> {

	public static final String TYPE = "record";

	public DDLRecordAssetRendererFactory() {
		setCategorizable(false);
		setClassName(DDLRecord.class.getName());
		setPortletId(DDLPortletKeys.DYNAMIC_DATA_LISTS);
		setSearchable(true);
		setSelectable(true);
	}

	@Override
	public AssetRenderer<DDLRecord> getAssetRenderer(long classPK, int type)
		throws PortalException {

		DDLRecord record = _ddlRecordLocalService.fetchDDLRecord(classPK);

		DDLRecordVersion recordVersion = null;

		if (record == null) {
			recordVersion = _ddlRecordVersionLocalService.getRecordVersion(
				classPK);

			record = recordVersion.getRecord();
		}
		else {
			if (type == TYPE_LATEST) {
				recordVersion = record.getLatestRecordVersion();
			}
			else if (type == TYPE_LATEST_APPROVED) {
				recordVersion = record.getRecordVersion();
			}
			else {
				throw new IllegalArgumentException(
					"Unknown asset renderer type " + type);
			}
		}

		DDLRecordAssetRenderer ddlRecordAssetRenderer =
			new DDLRecordAssetRenderer(record, recordVersion);

		ddlRecordAssetRenderer.setAssetRendererType(type);
		ddlRecordAssetRenderer.setServletContext(_servletContext);

		return ddlRecordAssetRenderer;
	}

	@Override
	public String getClassName() {
		return DDLRecord.class.getName();
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		return new DDLRecordSetClassTypeReader();
	}

	@Override
	public String getIconCssClass() {
		return "dynamic-data-list";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLAdd(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, getGroup(liferayPortletRequest),
				DDLPortletKeys.DYNAMIC_DATA_LISTS, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_record.jsp"
		).setParameter(
			"recordSetId",
			() -> {
				if (classTypeId > 0) {
					return classTypeId;
				}

				return null;
			}
		).buildPortletURL();
	}

	@Override
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		if (classTypeId == 0) {
			return false;
		}

		return _ddlRecordSetModelResourcePermission.contains(
			permissionChecker, classTypeId, DDLActionKeys.ADD_RECORD);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		DDLRecord record = _ddlRecordLocalService.getDDLRecord(classPK);

		return _ddlRecordSetModelResourcePermission.contains(
			permissionChecker, record.getRecordSetId(), actionId);
	}

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecordSet)"
	)
	private ModelResourcePermission<DDLRecordSet>
		_ddlRecordSetModelResourcePermission;

	@Reference
	private DDLRecordVersionLocalService _ddlRecordVersionLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dynamic.data.lists.web)"
	)
	private ServletContext _servletContext;

}
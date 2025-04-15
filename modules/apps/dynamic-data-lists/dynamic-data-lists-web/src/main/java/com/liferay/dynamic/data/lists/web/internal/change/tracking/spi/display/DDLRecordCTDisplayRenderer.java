/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class DDLRecordCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDLRecord> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, DDLRecord ddlRecord)
		throws PortalException {

		Group group = _groupLocalService.getGroup(ddlRecord.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DDLPortletKeys.DYNAMIC_DATA_LISTS, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_record.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"groupId", ddlRecord.getGroupId()
		).setParameter(
			"recordId", ddlRecord.getPrimaryKey()
		).setParameter(
			"version", ddlRecord.getVersion()
		).buildString();
	}

	@Override
	public Class<DDLRecord> getModelClass() {
		return DDLRecord.class;
	}

	@Override
	public String getTitle(Locale locale, DDLRecord ddlRecord) {
		return String.valueOf(ddlRecord.getPrimaryKey());
	}

	@Override
	public String renderPreview(DisplayContext<DDLRecord> displayContext)
		throws Exception {

		DDLRecord ddlRecord = displayContext.getModel();

		DDLRecordSet ddlRecordSet = ddlRecord.getRecordSet();

		HTMLTag htmlTag = new HTMLTag();

		htmlTag.setClassNameId(
			_classNameLocalService.getClassNameId(DDMStructure.class));
		htmlTag.setClassPK(ddlRecordSet.getDDMStructureId());
		htmlTag.setDdmFormValues(ddlRecord.getDDMFormValues());
		htmlTag.setGroupId(ddlRecord.getGroupId());
		htmlTag.setReadOnly(true);
		htmlTag.setRequestedLocale(displayContext.getLocale());

		try (UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter()) {
			htmlTag.doTag(
				displayContext.getHttpServletRequest(),
				new PipingServletResponse(
					displayContext.getHttpServletResponse(),
					unsyncStringWriter));

			return unsyncStringWriter.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DDLRecord> displayBuilder) {
		DDLRecord ddlRecord = displayBuilder.getModel();

		displayBuilder.display(
			"created-by",
			() -> {
				String userName = ddlRecord.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"version", ddlRecord.getVersion()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDLRecordCTDisplayRenderer.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}
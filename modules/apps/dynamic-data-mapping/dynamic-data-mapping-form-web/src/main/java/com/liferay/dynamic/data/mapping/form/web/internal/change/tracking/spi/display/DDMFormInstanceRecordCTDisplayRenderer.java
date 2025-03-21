/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.taglib.servlet.taglib.HTMLTag;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class DDMFormInstanceRecordCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDMFormInstanceRecord> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		Group group = _groupLocalService.getGroup(
			ddmFormInstanceRecord.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/admin/edit_form_instance_record.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"formInstanceId", ddmFormInstanceRecord.getFormInstanceId()
		).setParameter(
			"formInstanceRecordId",
			ddmFormInstanceRecord.getFormInstanceRecordId()
		).buildString();
	}

	@Override
	public Class<DDMFormInstanceRecord> getModelClass() {
		return DDMFormInstanceRecord.class;
	}

	@Override
	public String getTitle(
		Locale locale, DDMFormInstanceRecord ddmFormInstanceRecord) {

		return String.valueOf(ddmFormInstanceRecord.getPrimaryKey());
	}

	@Override
	public String renderPreview(
			DisplayContext<DDMFormInstanceRecord> displayContext)
		throws Exception {

		HTMLTag htmlTag = new HTMLTag();

		htmlTag.setClassNameId(
			_classNameLocalService.getClassNameId(DDMStructure.class));

		DDMFormInstanceRecord ddmFormInstanceRecord = displayContext.getModel();

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		htmlTag.setClassPK(ddmFormInstance.getStructureId());

		htmlTag.setDdmFormValues(ddmFormInstanceRecord.getDDMFormValues());
		htmlTag.setGroupId(ddmFormInstanceRecord.getGroupId());
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
	protected void buildDisplay(
		DisplayBuilder<DDMFormInstanceRecord> displayBuilder) {

		DDMFormInstanceRecord ddmFormInstanceRecord = displayBuilder.getModel();

		displayBuilder.display(
			"author",
			() -> {
				String userName = ddmFormInstanceRecord.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"status",
			() -> _language.get(
				displayBuilder.getLocale(),
				WorkflowConstants.getStatusLabel(
					ddmFormInstanceRecord.getStatus()))
		).display(
			"version", ddmFormInstanceRecord.getVersion()
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceRecordCTDisplayRenderer.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}
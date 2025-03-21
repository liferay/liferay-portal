/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseImageCard
	extends BaseBaseClayCard implements ImageCard {

	public BaseImageCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(baseModel, rowChecker);

		this.renderRequest = renderRequest;

		themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<LabelItem> getLabels() {
		if (!(baseModel instanceof WorkflowedModel)) {
			return Collections.emptyList();
		}

		return LabelItemListBuilder.add(
			labelItem -> {
				WorkflowedModel workflowedModel = (WorkflowedModel)baseModel;

				labelItem.setStatus(workflowedModel.getStatus());
			}
		).build();
	}

	@Override
	public String getStickerImageSrc() {
		if (!(baseModel instanceof AuditedModel)) {
			return StringPool.BLANK;
		}

		AuditedModel auditedModel = (AuditedModel)baseModel;

		User user = UserLocalServiceUtil.fetchUser(auditedModel.getUserId());

		if (user.getPortraitId() > 0) {
			try {
				return user.getPortraitURL(themeDisplay);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public String getStickerLabel() {
		if (!(baseModel instanceof AuditedModel)) {
			return StringPool.BLANK;
		}

		AuditedModel auditedModel = (AuditedModel)baseModel;

		User user = UserLocalServiceUtil.fetchUser(auditedModel.getUserId());

		if (user == null) {
			return StringPool.BLANK;
		}

		return user.getInitials();
	}

	@Override
	public String getStickerShape() {
		return "circle";
	}

	protected final RenderRequest renderRequest;
	protected final ThemeDisplay themeDisplay;

	private static final Log _log = LogFactoryUtil.getLog(BaseImageCard.class);

}
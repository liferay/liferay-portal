/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.uad.display;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class MBMessageUADDisplay extends BaseMBMessageUADDisplay {

	@Override
	public String[] getColumnFieldNames() {
		return new String[] {"subject", "body"};
	}

	@Override
	public String[] getDisplayFieldNames() {
		return new String[] {
			"subject", "body", "userId", "userName", "statusByUserId",
			"statusByUserName"
		};
	}

	@Override
	public String getEditURL(
			MBMessage mbMessage, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		if (mbMessage.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			return null;
		}

		if (mbMessage.isInTrash()) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			MBPortletKeys.MESSAGE_BOARDS_ADMIN, PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/message_boards/edit_message"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"messageId", mbMessage.getMessageId()
		).buildString();
	}

	@Override
	public Map<String, Object> getFieldValues(
		MBMessage mbMessage, String[] fieldNames, Locale locale) {

		Map<String, Object> fieldValues = super.getFieldValues(
			mbMessage, fieldNames, locale);

		List<String> fieldNamesList = Arrays.asList(fieldNames);

		if (fieldNamesList.contains("content")) {
			fieldValues.put("content", mbMessage.getBody());
		}

		return fieldValues;
	}

	@Override
	public String getName(MBMessage mbMessage, Locale locale) {
		return mbMessage.getSubject();
	}

	@Override
	public Class<?> getParentContainerClass() {
		return MBThread.class;
	}

	@Override
	public Serializable getParentContainerId(MBMessage mbMessage) {
		return mbMessage.getThreadId();
	}

	@Override
	public boolean isUserOwned(MBMessage mbMessage, long userId) {
		if (mbMessage.getUserId() == userId) {
			return true;
		}

		return false;
	}

	@Reference
	protected Portal portal;

}
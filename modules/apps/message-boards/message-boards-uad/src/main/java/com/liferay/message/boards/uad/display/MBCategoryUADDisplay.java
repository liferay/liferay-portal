/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.uad.display;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
public class MBCategoryUADDisplay extends BaseMBCategoryUADDisplay {

	@Override
	public String getEditURL(
			MBCategory mbCategory, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			MBPortletKeys.MESSAGE_BOARDS_ADMIN, PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/message_boards/edit_category"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"mbCategoryId", mbCategory.getCategoryId()
		).buildString();
	}

	@Override
	public Map<String, Object> getFieldValues(
		MBCategory mbCategory, String[] fieldNames, Locale locale) {

		Map<String, Object> fieldValues = super.getFieldValues(
			mbCategory, fieldNames, locale);

		List<String> fieldNamesList = Arrays.asList(fieldNames);

		if (fieldNamesList.contains("content")) {
			fieldValues.put("content", mbCategory.getDescription());
		}

		return fieldValues;
	}

	@Override
	public String getName(MBCategory mbCategory, Locale locale) {
		return mbCategory.getName();
	}

	@Override
	public Class<?> getParentContainerClass() {
		return MBCategory.class;
	}

	@Override
	public Serializable getParentContainerId(MBCategory mbCategory) {
		return mbCategory.getParentCategoryId();
	}

	@Override
	public MBCategory getTopLevelContainer(
		Class<?> parentContainerClass, Serializable parentContainerId,
		Object childObject) {

		if (!parentContainerClass.equals(MBCategory.class)) {
			return null;
		}

		try {
			MBCategory childCategory = null;

			if (childObject instanceof MBMessage) {
				MBMessage mbMessage = (MBMessage)childObject;

				if (mbMessage.getCategoryId() ==
						MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

					return null;
				}

				childCategory = mbMessage.getCategory();
			}
			else if (childObject instanceof MBThread) {
				MBThread mbThread = (MBThread)childObject;

				if ((mbThread.getCategoryId() ==
						MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
					(mbThread.getCategoryId() ==
						MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

					return null;
				}

				childCategory = mbThread.getCategory();
			}
			else {
				childCategory = (MBCategory)childObject;
			}

			long parentCategoryId = (long)parentContainerId;

			if (childCategory.getCategoryId() == parentCategoryId) {
				return null;
			}

			List<Long> ancestorCategoryIds =
				childCategory.getAncestorCategoryIds();

			if ((parentCategoryId !=
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
				!ancestorCategoryIds.contains(parentCategoryId)) {

				return null;
			}

			if (childCategory.getParentCategoryId() == parentCategoryId) {
				return childCategory;
			}

			if (parentCategoryId ==
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

				return get(
					ancestorCategoryIds.get(ancestorCategoryIds.size() - 1));
			}

			if (ancestorCategoryIds.contains(parentCategoryId)) {
				return get(
					ancestorCategoryIds.get(
						ancestorCategoryIds.indexOf(parentCategoryId) - 1));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	@Override
	public boolean isUserOwned(MBCategory mbCategory, long userId) {
		if (mbCategory.getUserId() == userId) {
			return true;
		}

		return false;
	}

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		MBCategoryUADDisplay.class);

}
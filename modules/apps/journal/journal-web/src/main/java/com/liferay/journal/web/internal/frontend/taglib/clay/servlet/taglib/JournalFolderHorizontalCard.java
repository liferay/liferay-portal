/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseHorizontalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.web.internal.constants.JournalWebConstants;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalFolderActionDropdownItems;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalFolderHorizontalCard extends BaseHorizontalCard {

	public JournalFolderHorizontalCard(
		BaseModel<?> baseModel, String displayStyle,
		RenderRequest renderRequest, RenderResponse renderResponse,
		RowChecker rowChecker, TrashHelper trashHelper) {

		super(baseModel, renderRequest, rowChecker);

		_displayStyle = displayStyle;
		_renderResponse = renderResponse;
		_trashHelper = trashHelper;

		_folder = (JournalFolder)baseModel;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		JournalFolderActionDropdownItems folderActionDropdownItems =
			new JournalFolderActionDropdownItems(
				_folder, PortalUtil.getLiferayPortletRequest(renderRequest),
				PortalUtil.getLiferayPortletResponse(_renderResponse),
				_trashHelper);

		try {
			return folderActionDropdownItems.getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getDefaultEventHandler() {
		return JournalWebConstants.JOURNAL_ELEMENTS_DEFAULT_EVENT_HANDLER;
	}

	@Override
	public String getHref() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"displayStyle", _displayStyle
		).setParameter(
			"folderId", _folder.getFolderId()
		).setParameter(
			"groupId", _folder.getGroupId()
		).buildString();
	}

	@Override
	public String getIcon() {
		return "folder";
	}

	@Override
	public String getInputName() {
		return rowChecker.getRowIds() + JournalFolder.class.getSimpleName();
	}

	@Override
	public String getInputValue() {
		return String.valueOf(_folder.getFolderId());
	}

	@Override
	public String getTitle() {
		return _folder.getName();
	}

	@Override
	public boolean isTranslated() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderHorizontalCard.class);

	private final String _displayStyle;
	private final JournalFolder _folder;
	private final RenderResponse _renderResponse;
	private final TrashHelper _trashHelper;

}
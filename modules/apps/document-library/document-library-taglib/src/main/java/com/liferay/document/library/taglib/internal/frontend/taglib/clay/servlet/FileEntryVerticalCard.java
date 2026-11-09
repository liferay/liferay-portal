/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.frontend.taglib.clay.servlet;

import com.liferay.document.library.taglib.internal.display.context.RepositoryBrowserTagDisplayContext;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class FileEntryVerticalCard implements VerticalCard {

	public FileEntryVerticalCard(
			Set<String> actions, FileEntry fileEntry,
			HttpServletRequest httpServletRequest,
			RepositoryBrowserTagDisplayContext
				repositoryBrowserTagDisplayContext)
		throws PortalException {

		_actions = actions;
		_fileEntry = fileEntry;
		_httpServletRequest = httpServletRequest;
		_repositoryBrowserTagDisplayContext =
			repositoryBrowserTagDisplayContext;

		_fileVersion = fileEntry.getFileVersion();
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return _repositoryBrowserTagDisplayContext.getActionDropdownItems(
			_fileEntry);
	}

	@Override
	public String getDefaultEventHandler() {
		return "repositoryBrowserEventHandler";
	}

	@Override
	public String getImageSrc() {
		try {
			return DLURLHelperUtil.getThumbnailSrc(
				_fileEntry, _fileVersion,
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public String getInputName() {
		try {
			SearchContainer<Object> searchContainer =
				_repositoryBrowserTagDisplayContext.getSearchContainer();

			RowChecker rowChecker = searchContainer.getRowChecker();

			if (rowChecker == null) {
				return null;
			}

			return rowChecker.getRowIds();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public String getInputValue() {
		return String.valueOf(_fileEntry.getFileEntryId());
	}

	@Override
	public List<LabelItem> getLabels() {
		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(
				BeanPropertiesUtil.getInteger(
					_fileEntry.getModel(), "status",
					WorkflowConstants.STATUS_APPROVED))
		).build();
	}

	@Override
	public String getSubtitle() {
		Date modifiedDate = _fileEntry.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "x-ago-by-x",
			new Object[] {
				modifiedDateDescription,
				HtmlUtil.escape(_fileEntry.getUserName())
			});
	}

	@Override
	public String getTitle() {
		return _fileEntry.getTitle();
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	@Override
	public boolean isSelectable() {
		return !_actions.isEmpty();
	}

	private final Set<String> _actions;
	private final FileEntry _fileEntry;
	private final FileVersion _fileVersion;
	private final HttpServletRequest _httpServletRequest;
	private final RepositoryBrowserTagDisplayContext
		_repositoryBrowserTagDisplayContext;

}
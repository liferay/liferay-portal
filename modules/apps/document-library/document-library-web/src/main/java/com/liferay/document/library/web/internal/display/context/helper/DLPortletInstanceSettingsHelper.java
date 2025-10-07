/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.helper;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManagerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class DLPortletInstanceSettingsHelper {

	public DLPortletInstanceSettingsHelper(DLRequestHelper dlRequestHelper) {
		_dlRequestHelper = dlRequestHelper;
	}

	public List<KeyValuePair> getAvailableDisplayViews() {
		if (_availableDisplayViews == null) {
			_populateDisplayViews();
		}

		return _availableDisplayViews;
	}

	public List<KeyValuePair> getAvailableEntryColumns() {
		if (_availableEntryColumns == null) {
			_populateEntryColumns();
		}

		return _availableEntryColumns;
	}

	public List<KeyValuePair> getAvailableMimeTypes() {
		if (_availableMimeTypes == null) {
			_populateMimeTypes();
		}

		return _availableMimeTypes;
	}

	public List<KeyValuePair> getCurrentDisplayViews() {
		if (_currentDisplayViews == null) {
			_populateDisplayViews();
		}

		return _currentDisplayViews;
	}

	public List<KeyValuePair> getCurrentEntryColumns() {
		if (_currentEntryColumns == null) {
			_populateEntryColumns();
		}

		return _currentEntryColumns;
	}

	public List<KeyValuePair> getCurrentMimeTypes() {
		if (_currentMimeTypes == null) {
			_populateMimeTypes();
		}

		return _currentMimeTypes;
	}

	public String[] getEntryColumns() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] entryColumns = dlPortletInstanceSettings.getEntryColumns();

		String portletName = _dlRequestHelper.getPortletName();

		if (!isShowActions()) {
			entryColumns = ArrayUtil.remove(entryColumns, "action");
		}
		else if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) &&
				 !portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) &&
				 !ArrayUtil.contains(entryColumns, "action")) {

			entryColumns = ArrayUtil.append(entryColumns, "action");
		}

		return entryColumns;
	}

	public Folder getRootFolder() throws PortalException {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String rootFolderExternalReferenceCode =
			dlPortletInstanceSettings.getRootFolderExternalReferenceCode();

		if (Validator.isBlank(rootFolderExternalReferenceCode)) {
			return null;
		}

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		Group selectedGroup =
			GroupLocalServiceUtil.getGroupByExternalReferenceCode(
				dlPortletInstanceSettings.
					getSelectedGroupExternalReferenceCode(),
				themeDisplay.getCompanyId());

		return DLAppLocalServiceUtil.getFolderByExternalReferenceCode(
			rootFolderExternalReferenceCode, selectedGroup.getGroupId());
	}

	public long getRootFolderId() throws PortalException {
		Folder rootFolder = getRootFolder();

		if (rootFolder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return rootFolder.getFolderId();
	}

	public long getSelectedRepositoryId() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String selectedGroupExternalReferenceCode =
			dlPortletInstanceSettings.getSelectedGroupExternalReferenceCode();

		if (Validator.isBlank(selectedGroupExternalReferenceCode)) {
			return 0;
		}

		try {
			ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

			Group selectedGroup =
				GroupLocalServiceUtil.getGroupByExternalReferenceCode(
					selectedGroupExternalReferenceCode,
					themeDisplay.getCompanyId());

			String selectedRepositoryExternalReferenceCode =
				dlPortletInstanceSettings.
					getSelectedRepositoryExternalReferenceCode();

			if (Validator.isBlank(selectedRepositoryExternalReferenceCode)) {
				return selectedGroup.getGroupId();
			}

			Repository selectedRepository =
				RepositoryLocalServiceUtil.getRepositoryByExternalReferenceCode(
					selectedRepositoryExternalReferenceCode,
					selectedGroup.getGroupId());

			return selectedRepository.getRepositoryId();
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	public boolean isShowActions() {
		String portletName = _dlRequestHelper.getPortletName();
		String portletResource = _dlRequestHelper.getPortletResource();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) ||
			portletName.equals(PortletKeys.MY_WORKFLOW_TASK) ||
			portletResource.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) ||
			portletResource.equals(PortletKeys.MY_WORKFLOW_TASK)) {

			return true;
		}

		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		return dlPortletInstanceSettings.isShowActions();
	}

	public boolean isShowSearch() {
		String portletName = _dlRequestHelper.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return true;
		}

		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		return dlPortletInstanceSettings.isShowFoldersSearch();
	}

	public boolean isShowTabs() {
		String portletName = _dlRequestHelper.getPortletName();

		return portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);
	}

	private String[] _getAllEntryColumns() {
		String allEntryColumns = "name,description,document-type,size,status";

		if (ViewCountManagerUtil.isViewCountEnabled(
				ClassNameLocalServiceUtil.getClassNameId(
					DLFileEntryConstants.getClassName()))) {

			allEntryColumns += ",downloads";
		}

		if (isShowActions()) {
			allEntryColumns += ",action";
		}

		allEntryColumns += ",modified-date,create-date";

		return StringUtil.split(allEntryColumns);
	}

	private void _populateDisplayViews() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] displayViews = dlPortletInstanceSettings.getDisplayViews();

		_currentDisplayViews = new ArrayList<>();

		for (String displayView : displayViews) {
			_currentDisplayViews.add(
				new KeyValuePair(
					displayView,
					LanguageUtil.get(
						_dlRequestHelper.getLocale(),
						_displayViews.get(displayView))));
		}

		Arrays.sort(displayViews);

		_availableDisplayViews = new ArrayList<>();

		Set<String> allDisplayViews = SetUtil.fromArray(
			PropsValues.DL_DISPLAY_VIEWS);

		for (String displayView : allDisplayViews) {
			if (Arrays.binarySearch(displayViews, displayView) < 0) {
				_availableDisplayViews.add(
					new KeyValuePair(
						displayView,
						LanguageUtil.get(
							_dlRequestHelper.getLocale(),
							_displayViews.get(displayView))));
			}
		}

		_availableDisplayViews = ListUtil.sort(
			_availableDisplayViews, new KeyValuePairComparator(false, true));
	}

	private void _populateEntryColumns() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] entryColumns = dlPortletInstanceSettings.getEntryColumns();

		_currentEntryColumns = new ArrayList<>();

		for (String entryColumn : entryColumns) {
			if (entryColumn.equals("action") && !isShowActions()) {
				continue;
			}

			_currentEntryColumns.add(
				new KeyValuePair(
					entryColumn,
					LanguageUtil.get(
						_dlRequestHelper.getLocale(), entryColumn)));
		}

		Arrays.sort(entryColumns);

		_availableEntryColumns = new ArrayList<>();

		Set<String> allEntryColumns = SetUtil.fromArray(_getAllEntryColumns());

		for (String entryColumn : allEntryColumns) {
			if (Arrays.binarySearch(entryColumns, entryColumn) < 0) {
				_availableEntryColumns.add(
					new KeyValuePair(
						entryColumn,
						LanguageUtil.get(
							_dlRequestHelper.getLocale(), entryColumn)));
			}
		}

		_availableEntryColumns = ListUtil.sort(
			_availableEntryColumns, new KeyValuePairComparator(false, true));
	}

	private void _populateMimeTypes() {
		DLPortletInstanceSettings dlPortletInstanceSettings =
			_dlRequestHelper.getDLPortletInstanceSettings();

		String[] mediaGalleryMimeTypes =
			dlPortletInstanceSettings.getMimeTypes();

		Arrays.sort(mediaGalleryMimeTypes);

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		_currentMimeTypes = new ArrayList<>();

		for (String mimeType : mediaGalleryMimeTypes) {
			_currentMimeTypes.add(
				new KeyValuePair(
					mimeType,
					LanguageUtil.get(themeDisplay.getLocale(), mimeType)));
		}

		_availableMimeTypes = new ArrayList<>();

		Set<String> allMediaGalleryMimeTypes =
			DLUtil.getAllMediaGalleryMimeTypes();

		for (String mimeType : allMediaGalleryMimeTypes) {
			if (Arrays.binarySearch(mediaGalleryMimeTypes, mimeType) < 0) {
				_availableMimeTypes.add(
					new KeyValuePair(
						mimeType,
						LanguageUtil.get(themeDisplay.getLocale(), mimeType)));
			}
		}
	}

	private static final Map<String, String> _displayViews = HashMapBuilder.put(
		"descriptive", "list"
	).put(
		"icon", "cards"
	).put(
		"list", "table"
	).build();

	private List<KeyValuePair> _availableDisplayViews;
	private List<KeyValuePair> _availableEntryColumns;
	private List<KeyValuePair> _availableMimeTypes;
	private List<KeyValuePair> _currentDisplayViews;
	private List<KeyValuePair> _currentEntryColumns;
	private List<KeyValuePair> _currentMimeTypes;
	private final DLRequestHelper _dlRequestHelper;

}
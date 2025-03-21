/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bárbara Cabrera
 */
public class LayoutUtilityPageEntryImportDisplayContext {

	public LayoutUtilityPageEntryImportDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
	}

	public String getDialogMessage() {
		String dialogMessage =
			"some-utility-pages-could-not-be-imported-but-other-utility-" +
				"pages-were-imported-correctly-or-with-warnings";

		List<LayoutsImporterResultEntry> importedLayoutsImporterResultEntries =
			getImportedLayoutsImporterResultEntries();

		List<LayoutsImporterResultEntry>
			layoutsImporterResultEntriesWithWarnings =
				getLayoutsImporterResultEntriesWithWarnings();

		List<LayoutsImporterResultEntry>
			notImportedLayoutsImporterResultEntries =
				getNotImportedLayoutsImporterResultEntries();

		if (ListUtil.isNotEmpty(importedLayoutsImporterResultEntries) &&
			ListUtil.isNotEmpty(layoutsImporterResultEntriesWithWarnings) &&
			ListUtil.isEmpty(notImportedLayoutsImporterResultEntries)) {

			dialogMessage =
				"some-utility-pages-were-imported-correctly-and-other-" +
					"utility-pages-were-imported-with-warnings";
		}
		else if (ListUtil.isEmpty(layoutsImporterResultEntriesWithWarnings) &&
				 ListUtil.isEmpty(notImportedLayoutsImporterResultEntries)) {

			dialogMessage = "all-utility-pages-were-imported-correctly";
		}
		else if (ListUtil.isEmpty(importedLayoutsImporterResultEntries) &&
				 ListUtil.isEmpty(layoutsImporterResultEntriesWithWarnings)) {

			dialogMessage = "no-utility-pages-could-be-imported";
		}
		else if (ListUtil.isEmpty(importedLayoutsImporterResultEntries)) {
			dialogMessage = "some-utility-pages-were-imported-with-warnings";
		}

		return LanguageUtil.get(_httpServletRequest, dialogMessage);
	}

	public String getDialogType() {
		if (ListUtil.isEmpty(getImportedLayoutsImporterResultEntries()) &&
			ListUtil.isEmpty(getLayoutsImporterResultEntriesWithWarnings())) {

			return "danger";
		}

		if (ListUtil.isEmpty(getNotImportedLayoutsImporterResultEntries()) &&
			ListUtil.isEmpty(getLayoutsImporterResultEntriesWithWarnings())) {

			return "success";
		}

		return "warning";
	}

	public List<LayoutsImporterResultEntry>
		getImportedLayoutsImporterResultEntries() {

		if (_importedLayoutsImporterResultEntries != null) {
			return _importedLayoutsImporterResultEntries;
		}

		Map<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			layoutsImporterResultEntryMap = getLayoutsImporterResultEntryMap();

		if (MapUtil.isEmpty(layoutsImporterResultEntryMap)) {
			return null;
		}

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			layoutsImporterResultEntryMap.get(
				LayoutsImporterResultEntry.Status.IMPORTED);

		if (layoutsImporterResultEntries == null) {
			return null;
		}

		List<LayoutsImporterResultEntry> importedLayoutsImporterResultEntries =
			new ArrayList<>();

		for (LayoutsImporterResultEntry layoutsImporterResultEntry :
				layoutsImporterResultEntries) {

			if (ArrayUtil.isEmpty(
					layoutsImporterResultEntry.getWarningMessages())) {

				importedLayoutsImporterResultEntries.add(
					layoutsImporterResultEntry);
			}
		}

		_importedLayoutsImporterResultEntries =
			importedLayoutsImporterResultEntries;

		return importedLayoutsImporterResultEntries;
	}

	public List<LayoutsImporterResultEntry>
		getLayoutsImporterResultEntriesWithWarnings() {

		if (_layoutsImporterResultEntriesWithWarnings != null) {
			return _layoutsImporterResultEntriesWithWarnings;
		}

		Map<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			layoutsImporterResultEntryMap = getLayoutsImporterResultEntryMap();

		if (MapUtil.isEmpty(layoutsImporterResultEntryMap)) {
			return null;
		}

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			layoutsImporterResultEntryMap.get(
				LayoutsImporterResultEntry.Status.IMPORTED);

		if (layoutsImporterResultEntries == null) {
			return null;
		}

		List<LayoutsImporterResultEntry>
			layoutsImporterResultEntriesWithWarnings = new ArrayList<>();

		for (LayoutsImporterResultEntry layoutsImporterResultEntry :
				layoutsImporterResultEntries) {

			if (ArrayUtil.isNotEmpty(
					layoutsImporterResultEntry.getWarningMessages())) {

				layoutsImporterResultEntriesWithWarnings.add(
					layoutsImporterResultEntry);
			}
		}

		_layoutsImporterResultEntriesWithWarnings =
			layoutsImporterResultEntriesWithWarnings;

		return _layoutsImporterResultEntriesWithWarnings;
	}

	public Map
		<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			getLayoutsImporterResultEntryMap() {

		if (_layoutsImporterResultEntryMap != null) {
			return _layoutsImporterResultEntryMap;
		}

		List<LayoutsImporterResultEntry> layoutsImporterResultEntries =
			(List<LayoutsImporterResultEntry>)SessionMessages.get(
				_renderRequest, "layoutUtilityPageImporterResultEntries");

		if (layoutsImporterResultEntries == null) {
			return null;
		}

		Map<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			layoutsImporterResultEntryMap = new HashMap<>();

		for (LayoutsImporterResultEntry layoutsImporterResultEntry :
				layoutsImporterResultEntries) {

			List<LayoutsImporterResultEntry>
				statusLayoutsImporterResultEntries = new ArrayList<>();

			LayoutsImporterResultEntry.Status status =
				layoutsImporterResultEntry.getStatus();

			if (layoutsImporterResultEntryMap.get(status) != null) {
				statusLayoutsImporterResultEntries =
					layoutsImporterResultEntryMap.get(status);
			}

			statusLayoutsImporterResultEntries.add(layoutsImporterResultEntry);

			layoutsImporterResultEntryMap.put(
				status, statusLayoutsImporterResultEntries);
		}

		_layoutsImporterResultEntryMap = layoutsImporterResultEntryMap;

		return _layoutsImporterResultEntryMap;
	}

	public List<LayoutsImporterResultEntry>
		getNotImportedLayoutsImporterResultEntries() {

		if (_notImportedLayoutsImporterResultEntries != null) {
			return _notImportedLayoutsImporterResultEntries;
		}

		Map<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			layoutsImporterResultEntryMap = getLayoutsImporterResultEntryMap();

		if (MapUtil.isEmpty(layoutsImporterResultEntryMap)) {
			return null;
		}

		List<LayoutsImporterResultEntry>
			notImportedLayoutsImporterResultEntries = new ArrayList<>();

		for (Map.Entry
				<LayoutsImporterResultEntry.Status,
				 List<LayoutsImporterResultEntry>> entrySet :
					layoutsImporterResultEntryMap.entrySet()) {

			if (entrySet.getKey() !=
					LayoutsImporterResultEntry.Status.IMPORTED) {

				notImportedLayoutsImporterResultEntries.addAll(
					entrySet.getValue());
			}
		}

		_notImportedLayoutsImporterResultEntries =
			notImportedLayoutsImporterResultEntries;

		return _notImportedLayoutsImporterResultEntries;
	}

	public String getSuccessMessage(
		List<LayoutsImporterResultEntry> layoutsImporterResultEntries) {

		return LanguageUtil.format(
			_httpServletRequest, "x-x-s-imported-correctly",
			new String[] {
				"utility-page",
				String.valueOf(layoutsImporterResultEntries.size())
			},
			true);
	}

	public String getWarningMessage(String layoutsImporterResultEntryName) {
		return LanguageUtil.format(
			_httpServletRequest, "x-was-imported-with-warnings",
			new Object[] {layoutsImporterResultEntryName}, true);
	}

	private final HttpServletRequest _httpServletRequest;
	private List<LayoutsImporterResultEntry>
		_importedLayoutsImporterResultEntries;
	private List<LayoutsImporterResultEntry>
		_layoutsImporterResultEntriesWithWarnings;
	private Map
		<LayoutsImporterResultEntry.Status, List<LayoutsImporterResultEntry>>
			_layoutsImporterResultEntryMap;
	private List<LayoutsImporterResultEntry>
		_notImportedLayoutsImporterResultEntries;
	private final RenderRequest _renderRequest;

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherEditFixPackFieldsDisplayContext {

	public PatcherEditFixPackFieldsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public List<KeyValuePair> getAvailablePatcherFixPacks() {
		List<KeyValuePair> availablePatcherFixPacks = new ArrayList<>();

		PatcherFix patcherFix = getPatcherFix();

		List<PatcherFixPack> availablePatcherFixPatcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPacksByStatus(
				patcherFix.getPatcherProjectVersionId(),
				WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT);

		OrderByComparator<PatcherFixPack> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				"PatcherFixPack", "patcherProjectVersionId", true,
				"patcherFixComponentId", true, "version", true);

		availablePatcherFixPatcherFixPacks = ListUtil.sort(
			availablePatcherFixPatcherFixPacks, orderByComparator);

		availablePatcherFixPatcherFixPacks.removeAll(getPatcherFixPacks());

		for (PatcherFixPack availablePatcherFixPack :
				availablePatcherFixPatcherFixPacks) {

			availablePatcherFixPacks.add(
				new KeyValuePair(
					String.valueOf(
						availablePatcherFixPack.getPatcherFixPackId()),
					LanguageUtil.get(
						_httpServletRequest,
						availablePatcherFixPack.getName())));
		}

		return ListUtil.sort(
			availablePatcherFixPacks, new KeyValuePairComparator(false, true));
	}

	public List<KeyValuePair> getCurrentPatcherFixPacks() {
		List<KeyValuePair> currentPatcherFixPacks = new ArrayList<>();

		for (PatcherFixPack currentPatcherFixPack : getPatcherFixPacks()) {
			currentPatcherFixPacks.add(
				new KeyValuePair(
					String.valueOf(currentPatcherFixPack.getPatcherFixPackId()),
					LanguageUtil.get(
						_httpServletRequest, currentPatcherFixPack.getName())));
		}

		return ListUtil.sort(
			currentPatcherFixPacks, new KeyValuePairComparator(false, true));
	}

	public PatcherFix getPatcherFix() {
		if (_patcherFix != null) {
			return _patcherFix;
		}

		_patcherFix = PatcherFixLocalServiceUtil.fetchPatcherFix(
			_getPatcherFixId());

		return _patcherFix;
	}

	public List<PatcherFixPack> getPatcherFixPacks() {
		if (_patcherFixPacks != null) {
			return _patcherFixPacks;
		}

		List<PatcherFixPack> patcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(
				_getPatcherFixId());

		OrderByComparator<PatcherFixPack> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				"PatcherFixPack", "patcherProjectVersionId", true,
				"patcherFixComponentId", true, "version", true);

		_patcherFixPacks = ListUtil.sort(patcherFixPacks, orderByComparator);

		return _patcherFixPacks;
	}

	public boolean isDisabled() {
		if (_disabled != null) {
			return _disabled;
		}

		_disabled = false;

		for (PatcherFixPack patcherFixPack : getPatcherFixPacks()) {
			if ((patcherFixPack.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_FROZEN) ||
				(patcherFixPack.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

				_disabled = true;

				return _disabled;
			}
		}

		return _disabled;
	}

	private long _getPatcherFixId() {
		if (_patcherFixId != null) {
			return _patcherFixId;
		}

		_patcherFixId = ParamUtil.getLong(_httpServletRequest, "patcherFixId");

		return _patcherFixId;
	}

	private Boolean _disabled;
	private final HttpServletRequest _httpServletRequest;
	private PatcherFix _patcherFix;
	private Long _patcherFixId;
	private List<PatcherFixPack> _patcherFixPacks;

}
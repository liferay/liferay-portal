/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.portlet.RenderRequest;

/**
 * @author Víctor Galán
 */
public class RepeatableFieldInfoCollectionProviderVerticalCard
	extends BaseVerticalCard {

	public RepeatableFieldInfoCollectionProviderVerticalCard(
		RenderRequest renderRequest, InfoFieldSetEntry infoFieldSetEntry,
		RowChecker rowChecker) {

		super(null, renderRequest, rowChecker);

		_infoFieldSetEntry = infoFieldSetEntry;
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary";
	}

	@Override
	public String getIcon() {
		return "list";
	}

	@Override
	public String getInputValue() {
		return null;
	}

	@Override
	public String getSubtitle() {
		if (_infoFieldSetEntry instanceof InfoFieldSet) {
			return LanguageUtil.get(
				themeDisplay.getLocale(), "repeatable-fieldset");
		}

		return LanguageUtil.get(themeDisplay.getLocale(), "repeatable-field");
	}

	@Override
	public String getTitle() {
		return _infoFieldSetEntry.getLabel(themeDisplay.getLocale());
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	private final InfoFieldSetEntry _infoFieldSetEntry;

}
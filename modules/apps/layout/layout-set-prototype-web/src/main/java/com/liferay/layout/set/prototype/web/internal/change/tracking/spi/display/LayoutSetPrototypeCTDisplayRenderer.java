/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.LayoutSetPrototype;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutSetPrototypeCTDisplayRenderer
	extends BaseCTDisplayRenderer<LayoutSetPrototype> {

	@Override
	public Class<LayoutSetPrototype> getModelClass() {
		return LayoutSetPrototype.class;
	}

	@Override
	public String getTitle(
		Locale locale, LayoutSetPrototype layoutSetPrototype) {

		return layoutSetPrototype.getName(locale);
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "site-template");
	}

	@Override
	public boolean isHideable(LayoutSetPrototype layoutSetPrototype) {
		return true;
	}

	@Reference
	private Language _language;

}
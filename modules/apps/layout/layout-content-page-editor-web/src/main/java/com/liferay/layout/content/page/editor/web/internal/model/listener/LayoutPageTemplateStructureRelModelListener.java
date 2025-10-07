/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener;

import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ModelListener.class)
public class LayoutPageTemplateStructureRelModelListener
	extends BaseModelListener<LayoutPageTemplateStructureRel> {

	@Override
	public void onAfterUpdate(
			LayoutPageTemplateStructureRel
				originalLayoutPageTemplateStructureRel,
			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws ModelListenerException {

		_contentManager.updateLayoutClassedModelUsage(
			layoutPageTemplateStructureRel);
	}

	@Reference
	private ContentManager _contentManager;

}
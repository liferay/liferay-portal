/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.renderer.FragmentDropZoneRenderer;
import com.liferay.layout.taglib.servlet.taglib.RenderLayoutStructureTag;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.servlet.PipingServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentDropZoneRenderer.class)
public class FragmentDropZoneRendererImpl implements FragmentDropZoneRenderer {

	@Override
	public String renderDropZone(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String mainItemId,
			String mode, boolean showPreview)
		throws PortalException {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		try {
			RenderLayoutStructureTag renderLayoutStructureTag =
				new RenderLayoutStructureTag();

			renderLayoutStructureTag.setMainItemId(mainItemId);
			renderLayoutStructureTag.setMode(mode);
			renderLayoutStructureTag.setRenderActionHandler(false);
			renderLayoutStructureTag.setShowPreview(showPreview);

			renderLayoutStructureTag.doTag(
				httpServletRequest, pipingServletResponse);
		}
		catch (Exception exception) {
			throw new FragmentEntryContentException(exception);
		}

		return unsyncStringWriter.toString();
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener;

import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ModelListener.class)
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onAfterUpdate(Layout originalLayout, Layout layout) {
		_clearPortalCache(layout);

		if (!layout.isDraftLayout() ||
			Objects.equals(layout.getStatus(), originalLayout.getStatus()) ||
			!Objects.equals(
				originalLayout.getStatus(), WorkflowConstants.STATUS_DRAFT)) {

			return;
		}

		LockManagerUtil.unlock(Layout.class.getName(), layout.getPlid());
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<String, ElementVariations>)_multiVMPool.getPortalCache(
				LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(
			LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	private void _clearPortalCache(Layout layout) {
		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				layout.getGroupId(), layout.getPlid());

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			_portalCache.remove(
				layout.getPlid() + StringPool.POUND +
					segmentsExperience.getSegmentsExperienceId());
		}
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<String, ElementVariations> _portalCache;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}

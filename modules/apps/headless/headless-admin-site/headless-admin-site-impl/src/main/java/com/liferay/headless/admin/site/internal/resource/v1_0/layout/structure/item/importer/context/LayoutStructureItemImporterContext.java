/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.model.Layout;

/**
 * @author Eudaldo Alonso
 */
public class LayoutStructureItemImporterContext {

	public LayoutStructureItemImporterContext(
		long companyId, long groupId,
		InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
		long segmentsExperienceId, long userId) {

		_companyId = companyId;
		_groupId = groupId;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_layout = layout;
		_segmentsExperienceId = segmentsExperienceId;
		_userId = userId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public InfoItemServiceRegistry getInfoItemServiceRegistry() {
		return _infoItemServiceRegistry;
	}

	public Layout getLayout() {
		return _layout;
	}

	public long getSegmentsExperienceId() {
		return _segmentsExperienceId;
	}

	public long getUserId() {
		return _userId;
	}

	private final long _companyId;
	private final long _groupId;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final Layout _layout;
	private final long _segmentsExperienceId;
	private final long _userId;

}
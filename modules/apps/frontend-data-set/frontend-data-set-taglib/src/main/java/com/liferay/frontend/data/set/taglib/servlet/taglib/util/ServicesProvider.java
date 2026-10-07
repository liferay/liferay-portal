/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.servlet.taglib.util;

import com.liferay.frontend.data.set.renderer.FDSRenderer;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Daniel Sanz
 */
public class ServicesProvider {

	public static FDSRenderer getFDSRenderer() {
		return _fdsRendererSnapshot.get();
	}

	private static final Snapshot<FDSRenderer> _fdsRendererSnapshot =
		new Snapshot<>(ServicesProvider.class, FDSRenderer.class);

}
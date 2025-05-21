/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyControllerImpl;
import com.liferay.alloy.mvc.MockAlloyControllerImpl;
import com.liferay.portal.kernel.model.BaseModel;

/**
 * @author Zsolt Balogh
 */
public class PatcherMockAlloyControllerImpl extends MockAlloyControllerImpl {

	public PatcherMockAlloyControllerImpl(
		PatcherAlloyControllerImpl patcherAlloyControllerImpl) {

		super((BaseAlloyControllerImpl)patcherAlloyControllerImpl);

		_patcherAlloyControllerImpl = patcherAlloyControllerImpl;
	}

	public String getDisplayURL(String controllerPath, long classPK)
		throws Exception {

		return _patcherAlloyControllerImpl.getDisplayURL(
			controllerPath, classPK);
	}

	@Override
	public void updateModelIgnoreRequest(
			BaseModel<?> baseModel, Object... properties)
		throws Exception {

		_patcherAlloyControllerImpl.updateModelIgnoreRequest(
			baseModel, properties);
	}

	private static final PatcherAlloyControllerImpl _patcherAlloyControllerImpl;

}
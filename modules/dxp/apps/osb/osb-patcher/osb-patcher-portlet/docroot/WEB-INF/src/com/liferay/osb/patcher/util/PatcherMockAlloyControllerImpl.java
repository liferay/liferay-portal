/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.util;

import com.liferay.alloy.mvc.BaseAlloyControllerImpl;
import com.liferay.alloy.mvc.MockAlloyControllerImpl;
import com.liferay.portal.model.BaseModel;

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

	private static PatcherAlloyControllerImpl _patcherAlloyControllerImpl;

}
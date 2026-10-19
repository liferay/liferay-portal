/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

/**
 * @author Shuyang Zhou
 */
public interface MVCCModel {

	public static <T extends MVCCModel> T scratchClone(BaseModel<T> baseModel) {
		T cloneT = (T)baseModel.clone();

		cloneT.setMvccVersion(cloneT.getMvccVersion() - 1);

		return cloneT;
	}

	public long getMvccVersion();

	public void setMvccVersion(long mvccVersion);

}
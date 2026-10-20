/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.template.TemplateRestricted;

/**
 * The extended model implementation for the CompanyInfo service. Represents a
 * row in the &quot;CompanyInfo&quot; database table, with each column mapped to
 * a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class.
 * Whenever methods are added, rerun ServiceBuilder to copy their definitions
 * into the <code>com.liferay.portal.kernel.model.CompanyInfo</code> interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class CompanyInfoImpl extends CompanyInfoBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. All methods that expect a company
	 * info model instance should use the {@link
	 * com.liferay.portal.kernel.model.CompanyInfo} interface instead.
	 */
	@Override
	@TemplateRestricted
	public String getKey() {
		return super.getKey();
	}

}
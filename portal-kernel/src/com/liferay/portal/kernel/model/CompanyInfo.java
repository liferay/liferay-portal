/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CompanyInfo service. Represents a row in the &quot;CompanyInfo&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyInfoModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.CompanyInfoImpl")
@ProviderType
public interface CompanyInfo extends CompanyInfoModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.CompanyInfoImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CompanyInfo, Long> COMPANY_INFO_ID_ACCESSOR =
		new Accessor<CompanyInfo, Long>() {

			@Override
			public Long get(CompanyInfo companyInfo) {
				return companyInfo.getCompanyInfoId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<CompanyInfo> getTypeClass() {
				return CompanyInfo.class;
			}

		};

	@com.liferay.portal.kernel.template.TemplateRestricted
	public String toString();

}
// LIFERAY-SERVICE-BUILDER-HASH:1773553032
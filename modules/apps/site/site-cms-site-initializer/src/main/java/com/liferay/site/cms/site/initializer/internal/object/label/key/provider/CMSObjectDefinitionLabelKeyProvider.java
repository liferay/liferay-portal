/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.object.label.key.provider;

import com.liferay.object.label.key.provider.ObjectDefinitionLabelKeyProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(service = ObjectDefinitionLabelKeyProvider.class)
public class CMSObjectDefinitionLabelKeyProvider
	implements ObjectDefinitionLabelKeyProvider {

	@Override
	public String getObjectDefinitionLabelKey(String externalReferenceCode) {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-17564")) {

			return StringPool.BLANK;
		}

		return CMSObjectDefinitionLabelKey.getLabelKey(externalReferenceCode);
	}

	public enum CMSObjectDefinitionLabelKey {

		BASIC_DOCUMENT("L_BASIC_DOCUMENT", "basic-document"),
		BASIC_WEB_CONTENT("L_BASIC_WEB_CONTENT", "basic-web-content"),
		BLOG("L_BLOG", "blog"),
		EXTERNAL_VIDEO("L_EXTERNAL_VIDEO", "external-video"),
		KNOWLEDGE_BASE("L_KNOWLEDGE_BASE", "knowledge-base");

		public static String getLabelKey(String externalReferenceCode) {
			for (CMSObjectDefinitionLabelKey value : values()) {
				if (StringUtil.equals(
						value._externalReferenceCode, externalReferenceCode)) {

					return value._labelKey;
				}
			}

			return StringPool.BLANK;
		}

		private CMSObjectDefinitionLabelKey(
			String externalReferenceCode, String labelKey) {

			_externalReferenceCode = externalReferenceCode;
			_labelKey = labelKey;
		}

		private final String _externalReferenceCode;
		private final String _labelKey;

	}

}
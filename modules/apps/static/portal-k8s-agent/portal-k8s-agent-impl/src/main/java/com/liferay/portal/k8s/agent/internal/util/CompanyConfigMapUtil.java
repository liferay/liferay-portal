/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.k8s.agent.PortalK8sConfigMapModifier;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class CompanyConfigMapUtil {

	public static void clearConfigMap(
		Company company,
		PortalK8sConfigMapModifier portalK8sConfigMapModifier) {

		portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.clear();

				Map<String, String> labels = configMapModel.labels();

				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					company.getWebId());
				labels.put("lxc.liferay.com/metadataType", "dxp");
			},
			getConfigMapName(company));
	}

	public static String getConfigMapName(Company company) {
		return company.getWebId() + "-lxc-dxp-metadata";
	}

	public static void modifyConfigMap(
		Company company, PortalK8sConfigMapModifier portalK8sConfigMapModifier,
		VirtualHostLocalService virtualHostLocalService) {

		List<String> virtualHostNames = new ArrayList<>();

		for (VirtualHost virtualHost :
				virtualHostLocalService.getVirtualHosts(
					company.getCompanyId())) {

			virtualHostNames.add(virtualHost.getHostname());
		}

		portalK8sConfigMapModifier.modifyConfigMap(
			configMapModel -> {
				Map<String, String> data = configMapModel.data();

				data.put(
					"com.liferay.lxc.dxp.domains",
					StringUtil.merge(virtualHostNames, StringPool.NEW_LINE));
				data.put(
					"com.liferay.lxc.dxp.main.domain",
					company.getVirtualHostname());
				data.put(
					"com.liferay.lxc.dxp.mainDomain",
					company.getVirtualHostname());
				data.put(
					"com.liferay.lxc.dxp.server.protocol",
					_getWebServerProtocol());

				Map<String, String> labels = configMapModel.labels();

				labels.put(
					"dxp.lxc.liferay.com/virtualInstanceId",
					company.getWebId());
				labels.put("lxc.liferay.com/metadataType", "dxp");
			},
			getConfigMapName(company));
	}

	private static String _getWebServerProtocol() {
		String webServerProtocol = PropsValues.WEB_SERVER_PROTOCOL;

		if (Validator.isNull(webServerProtocol)) {
			return Http.HTTP;
		}

		return webServerProtocol;
	}

}
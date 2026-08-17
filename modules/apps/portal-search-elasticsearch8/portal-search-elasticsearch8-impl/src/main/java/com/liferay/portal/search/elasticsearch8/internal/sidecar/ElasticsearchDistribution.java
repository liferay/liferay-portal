/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.sidecar;

import com.liferay.petra.string.StringBundler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bryan Engler
 */
public class ElasticsearchDistribution implements Distribution {

	public static final String VERSION = "8.19.20";

	@Override
	public Distributable getElasticsearchDistributable() {
		return new DistributableImpl(
			StringBundler.concat(
				"https://artifacts.elastic.co/downloads/elasticsearch",
				"/elasticsearch-", VERSION, "-linux-x86_64.tar.gz"),
			_ELASTICSEARCH_CHECKSUM);
	}

	@Override
	public List<Distributable> getPluginDistributables() {
		return Arrays.asList(
			new DistributableImpl(
				_getDownloadURLString("analysis-icu"), _ICU_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-kuromoji"), _KUROMOJI_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-smartcn"), _SMARTCN_CHECKSUM),
			new DistributableImpl(
				_getDownloadURLString("analysis-stempel"), _STEMPEL_CHECKSUM));
	}

	private String _getDownloadURLString(String plugin) {
		return StringBundler.concat(
			"https://artifacts.elastic.co/downloads/elasticsearch-plugins/",
			plugin, "/", plugin, "-", VERSION, ".zip");
	}

	private static final String _ELASTICSEARCH_CHECKSUM =
		"1c416956374360cec621bedcd293b6365e87dc3a911353bc7b17087f977f2426a3f9" +
			"8a7d8c2add14324207e5a44afbe87cd236b977f5fd3051cb2999899b2def";

	private static final String _ICU_CHECKSUM =
		"229ed8d94e75dd38367d1292e5c3db4de57f3a7af9c6b4312770deeab82658a93b6f" +
			"971881f589ea104a7832231972eed2abc41b67f9dcd44a9490218af35c99";

	private static final String _KUROMOJI_CHECKSUM =
		"bdc9a461ff021381597058a16a52e0b5a190ab9fd4b6d25efd4672cb096c676f5362" +
			"4a7959ff3714711c4def423459447b9832f19bb52e15ff4a3cb76d5cfbd9";

	private static final String _SMARTCN_CHECKSUM =
		"8a115d74e0f99789ffec97e184953fe4d53baadbaba5647b3971a5f6839e5819d4b7" +
			"02ae9aeff7d376c74677b1f50baa7aa36d8f73db6eab9ba6483eb998fe79";

	private static final String _STEMPEL_CHECKSUM =
		"5d189a776e1914ab3c8982966f5302908e230b88909f8800f909b2f4311956fa9ae6" +
			"295781810805c28a3e7965e4080712336db97c800af8ac4a7359ec65bd93";

}
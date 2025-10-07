/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.string.StringBundler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bryan Engler
 */
public class ElasticsearchDistribution implements Distribution {

	public static final String VERSION = "8.18.1";

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
		"9cbd5357fbe5fe046fcfeab33a1c035fbc57c05f7a8820ccc2d1f0bed9a4508a8c1e" +
			"97c6e2ee57612e6d30a4161efa227fb4de0e34aa9c22a6716c59f99883f5";

	private static final String _ICU_CHECKSUM =
		"5994b25ddf3e7839fb458ffc116f5fced5973c0e59be05944a435b63e24adc6d3fd4" +
			"c20883868bfb2b6e79f37922f0ee27f76823f4b5b255e8669d10506ea4c7";

	private static final String _KUROMOJI_CHECKSUM =
		"6311ec8c615c50b2260af6f302ad6468e851d7056138494cef58978afd8f6ebccae6" +
			"219eb7e5b8a220cdac16982c5c93e70895e2facb59ac2e4fd65628a5290a";

	private static final String _SMARTCN_CHECKSUM =
		"8030c1b1605cab592db6b07169df8d6c95d0fc8208dc8cfc316080265e97b44036bb" +
			"7fb75af02c0aa7c9ed91fd527d45a7881129ca684b81c270fec062bfe9ee";

	private static final String _STEMPEL_CHECKSUM =
		"8a9b1e785b8c9281e6c5e54234f8a034d335469029c77fdf66af6c22b18bd129c24b" +
			"e5c01ad5642008837fb21f8077fa4239a79bbdad1dd7bf75d6cde1571e22";

}
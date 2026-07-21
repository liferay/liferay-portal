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

	public static final String VERSION = "8.19.19";

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
		"1a87278db01fa925cf3d3368e9cae00de411e20d01039d8bbe611352f20ce822c7e8" +
			"b7877cb3c5c311cfcd99e6362d797fea4648befe405f6ec04467af755490";

	private static final String _ICU_CHECKSUM =
		"b1b410fa0c97052d5b77f7e9adfb669ac169b23bd55716f5ae006b6195e83ca93243" +
			"794d6391781f9e57a97e0b678828e6985a3b3534a34eca99c9f34327c1ed";

	private static final String _KUROMOJI_CHECKSUM =
		"4a72322294276b4673052fcb5194a524b96223d5f96f9cd4eb7eb0a3fb1ba8a7942c" +
			"80f9be0811f4c2cbbcb511cf79cd5d9732fae2b548efc124ebd157347e36";

	private static final String _SMARTCN_CHECKSUM =
		"e1ce09d02b79fea7600bb33f46ca72df61cb525ccffd022f6c74b0ebd6db9b380d2a" +
			"277121fa29a5645879772ff1f771bcf958ee84de4587d4b8fbfcf55425bb";

	private static final String _STEMPEL_CHECKSUM =
		"416f7de7fc3f5c6099036eb24809978f2cf2536483341c7c2c75889f4786dad73609" +
			"23392e3e8dec8f731b3ee3177b75856af9ab91756fa7fd83365ac4f926bb";

}
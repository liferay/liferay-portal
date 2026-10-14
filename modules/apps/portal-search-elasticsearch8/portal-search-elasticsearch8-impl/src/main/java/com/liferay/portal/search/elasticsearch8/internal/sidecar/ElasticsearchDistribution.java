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

	public static final String VERSION = "8.19.23";

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
		"ccec1037814ef8051717d09adf22fde4edd93135284eb84948eb8fd82cb4e95513bb" +
			"8c0780deb555e570fef549968f1d59ebe1ba81683afc6f8fab939ad03e6f";

	private static final String _ICU_CHECKSUM =
		"424d8d77921495782e3586f0fff1948204b0e8a0add863642fb11d0aaabd95c3afa3" +
			"d54aa111f197fcb1110359d0b4dea648286c57c2c5c68203273a63f8fdc4";

	private static final String _KUROMOJI_CHECKSUM =
		"b9faef13520aeab1070e22be81806f04080ddbc4e06a889ba7dc75e4353909356843" +
			"98b115c04cef304e69d292518d355589c5b8fce0a66580c2f23fbd73e613";

	private static final String _SMARTCN_CHECKSUM =
		"415729581f344596152f4e8bc39f72914c52568e34692eb8a7c6c9a3f8b22cdca52e" +
			"d746c3793f43bddf303733e9ac960754bd840fe1c5da0bec1c44107e47cf";

	private static final String _STEMPEL_CHECKSUM =
		"5c7d8060950b0fb209b2303158d394298256fca31b3a70c715e63ea4996a5bde5842" +
			"7fd82e9f8434139971cbba87f48a174ee3f36751092853b25953a3283521";

}
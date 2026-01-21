/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(generateUI = false)
@Meta.OCD(
	id = "com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderDownloadConfiguration"
)
public interface TensorFlowImageAssetAutoTagProviderDownloadConfiguration {

	@Meta.AD(
		deflt = "e3b84c7e240ce8025b30d868f5e840b4bba9761d", required = false
	)
	public String modelDownloadSHA1();

	@Meta.AD(
		deflt = "https://repository-cdn.liferay.com/nexus/service/local/repositories/public/content/com/liferay/org.tensorflow.models.inception/5h/org.tensorflow.models.inception-5h.jar",
		required = false
	)
	public String modelDownloadURL();

	@Meta.AD(
		deflt = "e749c7ce289ad236914657a11b3c198f35ae5f41", required = false
	)
	public String nativeLibraryDownloadSHA1();

	@Meta.AD(
		deflt = "https://repository-cdn.liferay.com/nexus/service/local/repositories/public/content/org/tensorflow/libtensorflow_jni/1.15.0/libtensorflow_jni-1.15.0.jar",
		required = false
	)
	public String nativeLibraryDownloadURL();

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import java.io.File;

import java.net.URL;

/**
 * @author Michael Hashimoto
 */
public interface TestrayAttachmentUploader {

	public File getPreparedFilesBaseDir();

	public TestrayAttachmentRecorder getTestrayAttachmentRecorder();
	
	public URL getTestrayServerLogsURL();
	
	public URL getTestrayServerURL();

	public void prepareFiles();

	public void upload();

}
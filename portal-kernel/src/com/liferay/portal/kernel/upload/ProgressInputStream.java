/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upload;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ProgressTracker;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Minhchau Dang
 */
public class ProgressInputStream extends InputStream {

	public ProgressInputStream(
		PortletRequest portletRequest, InputStream inputStream, long totalSize,
		String progressId) {

		_inputStream = inputStream;
		_totalSize = totalSize;
		_progressId = progressId;

		_portletSession = portletRequest.getPortletSession();

		initProgress();
	}

	@Override
	public int available() throws IOException {
		return _inputStream.available();
	}

	public void clearProgress() {
		_portletSession.removeAttribute(_getPercentAttributeName());
	}

	@Override
	public void close() throws IOException {
		_inputStream.close();
	}

	public long getTotalRead() {
		return _totalRead;
	}

	public void initProgress() {
		ProgressTracker progressTracker = new ProgressTracker(_progressId);

		progressTracker.initialize(_portletSession);
	}

	@Override
	public void mark(int readlimit) {
		_inputStream.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return _inputStream.markSupported();
	}

	@Override
	public int read() throws IOException {
		return _inputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int bytesRead = super.read(b, off, len);

		_updateProgress(bytesRead);

		return bytesRead;
	}

	public void readAll(OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[_DEFAULT_INITIAL_BUFFER_SIZE];

		int len = 0;

		while ((len = read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}

		outputStream.close();
	}

	@Override
	public void reset() throws IOException {
		_inputStream.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		long result = _inputStream.skip(n);

		_updateProgress(result);

		return result;
	}

	private String _getPercentAttributeName() {
		return ProgressTracker.PERCENT + _progressId;
	}

	private void _updateProgress(long bytesRead) {
		if (bytesRead > 0) {
			_totalRead += bytesRead;
		}
		else if (_totalSize > 0) {
			_totalRead = _totalSize;
		}

		int percent = 0;

		if (_totalSize > 0) {
			percent = (int)((_totalRead * 100) / _totalSize);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					_totalRead, "/", _totalSize, "=", percent));
		}

		ProgressTracker progressTracker =
			(ProgressTracker)_portletSession.getAttribute(
				_getPercentAttributeName(), PortletSession.APPLICATION_SCOPE);

		Integer curPercent = null;

		if (progressTracker != null) {
			curPercent = progressTracker.getPercent();
		}

		if ((curPercent == null) || ((percent - curPercent.intValue()) >= 1)) {
			if (progressTracker == null) {
				progressTracker = new ProgressTracker(_progressId);

				progressTracker.initialize(_portletSession);
			}

			progressTracker.setPercent(percent);
		}
	}

	private static final int _DEFAULT_INITIAL_BUFFER_SIZE = 4 * 1024;

	private static final Log _log = LogFactoryUtil.getLog(
		ProgressInputStream.class);

	private final InputStream _inputStream;
	private final PortletSession _portletSession;
	private final String _progressId;
	private long _totalRead;
	private final long _totalSize;

}
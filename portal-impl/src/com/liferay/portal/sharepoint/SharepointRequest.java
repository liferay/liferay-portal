/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StreamUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Farache
 */
public class SharepointRequest {

	public SharepointRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, User user)
		throws SharepointException {

		this(httpServletRequest, httpServletResponse, user, StringPool.BLANK);
	}

	public SharepointRequest(String rootPath) throws SharepointException {
		this(null, null, null, rootPath);
	}

	public void addParam(String key, String value) {
		_params.put(key, new String[] {value});
	}

	public byte[] getBytes() {
		return _bytes;
	}

	public long getCompanyId() {
		return _user.getCompanyId();
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public String getParameterValue(String name) {
		String[] values = _params.get(name);

		if (ArrayUtil.isNotEmpty(values)) {
			return GetterUtil.getString(_params.get(name)[0]);
		}

		return StringPool.BLANK;
	}

	public String getRootPath() {
		return _rootPath;
	}

	public SharepointStorage getSharepointStorage() {
		return _storage;
	}

	public User getUser() {
		return _user;
	}

	public long getUserId() {
		return _user.getUserId();
	}

	public void setBytes(byte[] bytes) {
		_bytes = bytes;
	}

	public void setRootPath(String rootPath) {
		_rootPath = SharepointUtil.replaceBackSlashes(rootPath);
	}

	public void setSharepointStorage(SharepointStorage storage) {
		_storage = storage;
	}

	protected void addParams() throws SharepointException {
		String contentType = _httpServletRequest.getContentType();

		if (!contentType.equals(SharepointUtil.VEERMER_URLENCODED)) {
			return;
		}

		try {
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();

			StreamUtil.transfer(
				_httpServletRequest.getInputStream(),
				unsyncByteArrayOutputStream);

			byte[] bytes = unsyncByteArrayOutputStream.toByteArray();

			UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new InputStreamReader(new ByteArrayInputStream(bytes)));

			String url = unsyncBufferedReader.readLine();

			String[] params = url.split(StringPool.AMPERSAND);

			for (String param : params) {
				String[] kvp = param.split(StringPool.EQUAL);

				String key = HttpComponentsUtil.decodeURL(kvp[0]);

				String value = StringPool.BLANK;

				if (kvp.length > 1) {
					value = HttpComponentsUtil.decodeURL(kvp[1]);
				}

				addParam(key, value);
			}

			setBytes(ArrayUtil.subset(bytes, url.length() + 1, bytes.length));
		}
		catch (Exception exception) {
			throw new SharepointException(exception);
		}
	}

	private SharepointRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, User user, String rootPath)
		throws SharepointException {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_user = user;
		_rootPath = rootPath;

		_params.putAll(httpServletRequest.getParameterMap());

		addParams();
	}

	private byte[] _bytes;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final Map<String, String[]> _params = new HashMap<>();
	private String _rootPath = StringPool.BLANK;
	private SharepointStorage _storage;
	private final User _user;

}
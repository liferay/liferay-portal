/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.web.cache;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;

import java.util.Base64;

import net.oauth.signature.pem.PEMReader;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;

/**
 * @author Brian Wing Shun Chan
 */
public class DSAccessTokenWebCacheItem implements WebCacheItem {

	public static JSONObject get(
		String apiUsername, String environment, String integrationKey,
		String rsaPrivateKey) {

		return (JSONObject)WebCachePoolUtil.get(
			StringBundler.concat(
				DSAccessTokenWebCacheItem.class.getName(), StringPool.POUND,
				apiUsername, StringPool.POUND, environment, StringPool.POUND,
				integrationKey, StringPool.POUND, rsaPrivateKey),
			new DSAccessTokenWebCacheItem(
				apiUsername, environment, integrationKey, rsaPrivateKey));
	}

	public DSAccessTokenWebCacheItem(
		String apiUsername, String environment, String integrationKey,
		String rsaPrivateKey) {

		_apiUsername = apiUsername;
		_environment = environment;
		_integrationKey = integrationKey;

		if (environment.equals("production")) {
			_environmentBaseURI = "account.docusign.com";
		}
		else {
			_environmentBaseURI = "account-d.docusign.com";
		}

		if (rsaPrivateKey == null) {
			_rsaPrivateKeyBytes = new byte[0];
		}
		else {
			String pem = _getPEM(rsaPrivateKey);

			_rsaPrivateKeyBytes = pem.getBytes();
		}
	}

	@Override
	public JSONObject convert(String key) {
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Get DocuSign access token for integration key " +
						_integrationKey);
			}

			Http.Options options = new Http.Options();

			options.setParts(
				HashMapBuilder.put(
					"assertion", _getJWT()
				).put(
					"grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"
				).build());
			options.setLocation(
				"https://" + _environmentBaseURI + "/oauth/token");
			options.setPost(true);

			return JSONFactoryUtil.createJSONObject(
				HttpUtil.URLtoString(options));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	@Override
	public long getRefreshTime() {
		return _REFRESH_TIME;
	}

	private String _encode(byte[] bytes) {
		//com.liferay.portal.kernel.util.Base64.encode(bytes);

		Base64.Encoder encoder = Base64.getUrlEncoder();

		return encoder.encodeToString(bytes);
	}

	private String _getJWT() throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA");

		signature.initSign(_readPrivateKey());

		String headerJSON = JSONUtil.put(
			"alg", "RS256"
		).put(
			"typ", "JWT"
		).toString();

		String bodyJSON = JSONUtil.put(
			"aud", _environmentBaseURI
		).put(
			"exp", _getUnixTime(3600)
		).put(
			"iat", _getUnixTime(0)
		).put(
			"iss", _integrationKey
		).put(
			"scope", "signature"
		).put(
			"sub", _apiUsername
		).toString();

		String token =
			_encode(headerJSON.getBytes()) + "." + _encode(bodyJSON.getBytes());

		signature.update(token.getBytes());

		return StringUtil.removeSubstring(
			token + "." + _encode(signature.sign()), "=");
	}

	private String _getPEM(String rsaPrivateKey) {

		// PEMReader needs the markers and the base64 body on their own lines.
		// Neither base64 nor a marker can contain a backslash, so an escaped
		// line break is unambiguous.

		String pem = StringUtil.replace(
			rsaPrivateKey.trim(), "\\\\n", StringPool.NEW_LINE);

		pem = StringUtil.replace(pem, "\\n", StringPool.NEW_LINE);

		int beginIndex = pem.indexOf(_PEM_MARKER_BEGIN);
		int endIndex = pem.lastIndexOf(_PEM_MARKER_END);

		if ((beginIndex == -1) || (endIndex == -1)) {
			return pem;
		}

		String body = pem.substring(
			beginIndex + _PEM_MARKER_BEGIN.length(), endIndex);

		return StringBundler.concat(
			_PEM_MARKER_BEGIN, StringPool.NEW_LINE, body.trim(),
			StringPool.NEW_LINE, _PEM_MARKER_END);
	}

	private Object _getUnixTime(long offset) {
		Long unixTime = (System.currentTimeMillis() / Time.SECOND) + offset;

		if (_environment.equals("production")) {
			return unixTime;
		}

		return String.valueOf(unixTime);
	}

	private PrivateKey _readPrivateKey() throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PEMReader pemReader = new PEMReader(_rsaPrivateKeyBytes);

		PKCS1EncodedKeySpec pkcs1EncodedKeySpec = new PKCS1EncodedKeySpec(
			pemReader.getDerBytes());

		return keyFactory.generatePrivate(pkcs1EncodedKeySpec.getKeySpec());
	}

	private static final String _PEM_MARKER_BEGIN =
		"-----BEGIN RSA PRIVATE KEY-----";

	private static final String _PEM_MARKER_END =
		"-----END RSA PRIVATE KEY-----";

	private static final long _REFRESH_TIME = Time.MINUTE * 45;

	private static final Log _log = LogFactoryUtil.getLog(
		DSAccessTokenWebCacheItem.class);

	private final String _apiUsername;
	private final String _environment;
	private final String _environmentBaseURI;
	private final String _integrationKey;
	private final byte[] _rsaPrivateKeyBytes;

}
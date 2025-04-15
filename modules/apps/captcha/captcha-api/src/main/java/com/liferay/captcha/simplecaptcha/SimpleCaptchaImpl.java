/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.simplecaptcha;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nl.captcha.backgrounds.BackgroundProducer;
import nl.captcha.gimpy.GimpyRenderer;
import nl.captcha.noise.NoiseProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.TextProducer;
import nl.captcha.text.renderer.WordRenderer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Sanz
 */
@Component(
	property = "captcha.engine.impl=com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl",
	service = Captcha.class
)
public class SimpleCaptchaImpl implements Captcha {

	@Override
	public void check(HttpServletRequest httpServletRequest)
		throws CaptchaException {

		if (!isEnabled(httpServletRequest)) {
			return;
		}

		if (!validateChallenge(httpServletRequest)) {
			throw new CaptchaTextException();
		}

		incrementCounter(httpServletRequest);

		if (_log.isDebugEnabled()) {
			_log.debug("CAPTCHA text is valid");
		}
	}

	@Override
	public void check(PortletRequest portletRequest) throws CaptchaException {
		check(portal.getHttpServletRequest(portletRequest));
	}

	@Override
	public void enforceCaptcha(HttpServletRequest httpServletRequest) {
		if (GetterUtil.getBoolean(PropsUtil.get("captcha.enforce.disabled"))) {
			return;
		}

		HttpSession httpSession = _getHttpSession(httpServletRequest);

		httpSession.setAttribute(
			_getHttpSessionKey(_CAPTCHA_MAX_CHALLENGES, httpServletRequest), 0);
	}

	@Override
	public void enforceCaptcha(PortletRequest portletRequest) {
		enforceCaptcha(portal.getHttpServletRequest(portletRequest));
	}

	@Override
	public String getName() {
		return "SimpleCaptcha";
	}

	@Override
	public String getTaglibPath() {
		return _TAGLIB_PATH;
	}

	@Override
	public boolean isEnabled(HttpServletRequest httpServletRequest) {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();
		HttpSession httpSession = _getHttpSession(httpServletRequest);

		int maxChallenges = 0;

		if (GetterUtil.getBoolean(PropsUtil.get("captcha.enforce.disabled"))) {
			maxChallenges = captchaConfiguration.maxChallenges();
		}
		else {
			maxChallenges = GetterUtil.getInteger(
				httpSession.getAttribute(
					_getHttpSessionKey(
						_CAPTCHA_MAX_CHALLENGES, httpServletRequest)),
				captchaConfiguration.maxChallenges());
		}

		if (maxChallenges == 0) {
			return true;
		}

		if (maxChallenges > 0) {
			Integer count = (Integer)httpSession.getAttribute(
				_getHttpSessionKey(WebKeys.CAPTCHA_COUNT, httpServletRequest));

			if ((count != null) && (count >= maxChallenges)) {
				return false;
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isEnabled(PortletRequest portletRequest) {
		return isEnabled(portal.getHttpServletRequest(portletRequest));
	}

	@Override
	public void serveImage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		HttpSession httpSession = _getHttpSession(httpServletRequest);

		String key = WebKeys.CAPTCHA_TEXT;

		String portletId = ParamUtil.getString(httpServletRequest, "portletId");

		if (Validator.isNotNull(portletId)) {
			key = portal.getPortletNamespace(portletId) + key;
		}

		nl.captcha.Captcha simpleCaptcha = getSimpleCaptcha();

		httpSession.setAttribute(key, simpleCaptcha.getAnswer());

		httpServletResponse.setContentType(ContentTypes.IMAGE_PNG);

		CaptchaServletUtil.writeImage(
			httpServletResponse.getOutputStream(), simpleCaptcha.getImage());
	}

	@Override
	public String serveImage(OutputStream outputStream) throws IOException {
		nl.captcha.Captcha simpleCaptcha = getSimpleCaptcha();

		CaptchaServletUtil.writeImage(outputStream, simpleCaptcha.getImage());

		return simpleCaptcha.getAnswer();
	}

	@Override
	public void serveImage(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		PortletSession portletSession = resourceRequest.getPortletSession();

		nl.captcha.Captcha simpleCaptcha = getSimpleCaptcha();

		String key = WebKeys.CAPTCHA_TEXT;

		String portletId = portal.getPortletId(resourceRequest);

		if (Validator.isNotNull(portletId)) {
			key = portal.getPortletNamespace(portletId) + key;
		}

		portletSession.setAttribute(key, simpleCaptcha.getAnswer());

		resourceResponse.setContentType(ContentTypes.IMAGE_PNG);

		CaptchaServletUtil.writeImage(
			resourceResponse.getPortletOutputStream(),
			simpleCaptcha.getImage());
	}

	protected void activate() {
		initBackgroundProducers();
		initGimpyRenderers();
		initNoiseProducers();
		initTextProducers();
		initWordRenderers();
	}

	protected BackgroundProducer getBackgroundProducer() {
		if (_backgroundProducers.length == 1) {
			return _backgroundProducers[0];
		}

		int pos = RandomUtil.nextInt(_backgroundProducers.length);

		return _backgroundProducers[pos];
	}

	protected GimpyRenderer getGimpyRenderer() {
		if (_gimpyRenderers.length == 1) {
			return _gimpyRenderers[0];
		}

		int pos = RandomUtil.nextInt(_gimpyRenderers.length);

		return _gimpyRenderers[pos];
	}

	protected int getHeight() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaHeight();
	}

	protected NoiseProducer getNoiseProducer() {
		if (_noiseProducers.length == 1) {
			return _noiseProducers[0];
		}

		int pos = RandomUtil.nextInt(_noiseProducers.length);

		return _noiseProducers[pos];
	}

	protected nl.captcha.Captcha getSimpleCaptcha() {
		nl.captcha.Captcha.Builder captchaBuilder =
			new nl.captcha.Captcha.Builder(getWidth(), getHeight());

		captchaBuilder.addText(getTextProducer(), getWordRenderer());
		captchaBuilder.addBackground(getBackgroundProducer());
		captchaBuilder.gimp(getGimpyRenderer());
		captchaBuilder.addNoise(getNoiseProducer());
		captchaBuilder.addBorder();

		return captchaBuilder.build();
	}

	protected TextProducer getTextProducer() {
		if (_textProducers.length == 1) {
			return _textProducers[0];
		}

		int pos = RandomUtil.nextInt(_textProducers.length);

		return _textProducers[pos];
	}

	protected int getWidth() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		return captchaConfiguration.simpleCaptchaWidth();
	}

	protected WordRenderer getWordRenderer() {
		if (_wordRenderers.length == 1) {
			return _wordRenderers[0];
		}

		int pos = RandomUtil.nextInt(_wordRenderers.length);

		return _wordRenderers[pos];
	}

	protected void incrementCounter(HttpServletRequest httpServletRequest) {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		if ((captchaConfiguration.maxChallenges() > 0) &&
			Validator.isNotNull(httpServletRequest.getRemoteUser())) {

			HttpSession httpSession = _getHttpSession(httpServletRequest);

			Integer count = (Integer)httpSession.getAttribute(
				_getHttpSessionKey(WebKeys.CAPTCHA_COUNT, httpServletRequest));

			httpSession.setAttribute(
				_getHttpSessionKey(WebKeys.CAPTCHA_COUNT, httpServletRequest),
				incrementCounter(count));
		}
	}

	protected Integer incrementCounter(Integer count) {
		if (count == null) {
			count = Integer.valueOf(1);
		}
		else {
			count = Integer.valueOf(count.intValue() + 1);
		}

		return count;
	}

	protected void incrementCounter(PortletRequest portletRequest) {
		incrementCounter(portal.getHttpServletRequest(portletRequest));
	}

	protected void initBackgroundProducers() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		String[] backgroundProducerClassNames =
			captchaConfiguration.simpleCaptchaBackgroundProducers();

		_backgroundProducers =
			new BackgroundProducer[backgroundProducerClassNames.length];

		for (int i = 0; i < backgroundProducerClassNames.length; i++) {
			String backgroundProducerClassName =
				backgroundProducerClassNames[i];

			_backgroundProducers[i] = (BackgroundProducer)_getInstance(
				backgroundProducerClassName);
		}
	}

	protected void initGimpyRenderers() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		String[] gimpyRendererClassNames =
			captchaConfiguration.simpleCaptchaGimpyRenderers();

		_gimpyRenderers = new GimpyRenderer[gimpyRendererClassNames.length];

		for (int i = 0; i < gimpyRendererClassNames.length; i++) {
			String gimpyRendererClassName = gimpyRendererClassNames[i];

			_gimpyRenderers[i] = (GimpyRenderer)_getInstance(
				gimpyRendererClassName);
		}
	}

	protected void initNoiseProducers() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		String[] noiseProducerClassNames =
			captchaConfiguration.simpleCaptchaNoiseProducers();

		_noiseProducers = new NoiseProducer[noiseProducerClassNames.length];

		for (int i = 0; i < noiseProducerClassNames.length; i++) {
			String noiseProducerClassName = noiseProducerClassNames[i];

			_noiseProducers[i] = (NoiseProducer)_getInstance(
				noiseProducerClassName);
		}
	}

	protected void initTextProducers() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		String[] textProducerClassNames =
			captchaConfiguration.simpleCaptchaTextProducers();

		_textProducers = new TextProducer[textProducerClassNames.length];

		for (int i = 0; i < textProducerClassNames.length; i++) {
			String textProducerClassName = textProducerClassNames[i];

			_textProducers[i] = (TextProducer)_getInstance(
				textProducerClassName);
		}
	}

	protected void initWordRenderers() {
		CaptchaConfiguration captchaConfiguration =
			captchaProvider.getCaptchaConfiguration();

		String[] wordRendererClassNames =
			captchaConfiguration.simpleCaptchaWordRenderers();

		_wordRenderers = new WordRenderer[wordRendererClassNames.length];

		for (int i = 0; i < wordRendererClassNames.length; i++) {
			String wordRendererClassName = wordRendererClassNames[i];

			_wordRenderers[i] = (WordRenderer)_getInstance(
				wordRendererClassName);
		}
	}

	protected boolean validateChallenge(HttpServletRequest httpServletRequest)
		throws CaptchaException {

		HttpSession httpSession = _getHttpSession(httpServletRequest);

		String httpSessionKey = _getHttpSessionKey(
			WebKeys.CAPTCHA_TEXT, httpServletRequest);

		String captchaText = (String)httpSession.getAttribute(httpSessionKey);

		if (captchaText == null) {
			_log.error(
				"CAPTCHA text is null. User " +
					httpServletRequest.getRemoteUser() +
						" may be trying to circumvent the CAPTCHA.");

			throw new CaptchaTextException();
		}

		boolean valid = captchaText.equals(
			ParamUtil.getString(httpServletRequest, "captchaText"));

		if (valid) {
			httpSession.removeAttribute(httpSessionKey);
		}

		return valid;
	}

	protected boolean validateChallenge(PortletRequest portletRequest)
		throws CaptchaException {

		return validateChallenge(portal.getHttpServletRequest(portletRequest));
	}

	@Reference
	protected CaptchaProvider captchaProvider;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.captcha.impl)(release.schema.version>=1.1.0))"
	)
	protected Release release;

	private HttpSession _getHttpSession(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			portal.getOriginalServletRequest(httpServletRequest);

		return originalHttpServletRequest.getSession();
	}

	private String _getHttpSessionKey(
		String key, HttpServletRequest httpServletRequest) {

		String portletId = portal.getPortletId(httpServletRequest);

		if (Validator.isNotNull(portletId)) {
			return portal.getPortletNamespace(portletId) + key;
		}

		return key;
	}

	private Object _getInstance(String className) {
		className = className.trim();

		Object instance = _instances.get(className);

		if (instance != null) {
			return instance;
		}

		try {
			Class<?> clazz = _loadClass(className);

			instance = clazz.newInstance();

			_instances.put(className, instance);
		}
		catch (Exception exception) {
			_log.error("Unable to load " + className, exception);
		}

		return instance;
	}

	private Class<?> _loadClass(String className) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.loadClass(className);
	}

	private static final String _CAPTCHA_MAX_CHALLENGES =
		SimpleCaptchaImpl.class.getName() + "#CAPTCHA_MAX_CHALLENGES";

	private static final String _TAGLIB_PATH = "/captcha/simplecaptcha.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SimpleCaptchaImpl.class);

	private BackgroundProducer[] _backgroundProducers;
	private GimpyRenderer[] _gimpyRenderers;
	private final Map<String, Object> _instances = new ConcurrentHashMap<>();
	private NoiseProducer[] _noiseProducers;
	private TextProducer[] _textProducers;
	private WordRenderer[] _wordRenderers;

}
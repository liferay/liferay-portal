/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.NullAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

/**
 * @author Hai Yu
 */
@Plugin(
	category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE,
	name = CompanyWebIdConsoleAppender.PLUGIN_NAME, printObject = true
)
public final class CompanyWebIdConsoleAppender extends AbstractAppender {

	public static final String PLUGIN_NAME = "CompanyWebIdConsole";

	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public void append(LogEvent logEvent) {
		if (_appender == null) {
			_appender = _createAppender();
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		if ((companyId == 0) ||
			!ArrayUtil.contains(
				PortalInstancePool.getCompanyIds(), companyId)) {

			_appender.append(logEvent);

			return;
		}

		MutableLogEvent mutableLogEvent = null;

		if (logEvent instanceof MutableLogEvent) {
			mutableLogEvent = (MutableLogEvent)logEvent;

			mutableLogEvent.setContextData(
				new SortedArrayStringMap(logEvent.getContextData()));
		}
		else {
			mutableLogEvent = new MutableLogEvent();

			mutableLogEvent.initFrom(logEvent);
		}

		StringMap contextData = (StringMap)mutableLogEvent.getContextData();

		contextData.putValue(
			"company.webId", PortalInstancePool.getWebId(companyId));

		_appender.append(mutableLogEvent);
	}

	public static class Builder
		extends AbstractAppender.Builder<Builder>
		implements org.apache.logging.log4j.core.util.Builder
			<CompanyWebIdConsoleAppender> {

		@Override
		public CompanyWebIdConsoleAppender build() {
			return new CompanyWebIdConsoleAppender(
				_bufferedIo, _bufferSize, _direct, getFilter(), _follow,
				_immediateFlush, getLayout(), getName(), _target);
		}

		@PluginBuilderAttribute("bufferedIo")
		private boolean _bufferedIo = true;

		@PluginBuilderAttribute("bufferSize")
		private int _bufferSize = Constants.ENCODER_BYTE_BUFFER_SIZE;

		@PluginBuilderAttribute("direct")
		private boolean _direct;

		@PluginBuilderAttribute("follow")
		private boolean _follow;

		@PluginBuilderAttribute("immediateFlush")
		private boolean _immediateFlush = true;

		@PluginBuilderAttribute("target")
		private ConsoleAppender.Target _target =
			ConsoleAppender.Target.SYSTEM_OUT;

	}

	private CompanyWebIdConsoleAppender(
		boolean bufferedIo, int bufferSize, boolean direct, Filter filter,
		boolean follow, boolean immediateFlush,
		Layout<? extends Serializable> layout, String name,
		ConsoleAppender.Target target) {

		super(name, filter, layout, true, null);

		_bufferedIo = bufferedIo;
		_bufferSize = bufferSize;
		_direct = direct;
		_follow = follow;
		_immediateFlush = immediateFlush;
		_target = target;
	}

	private Appender _createAppender() {
		ConsoleAppender.Builder builder = ConsoleAppender.newBuilder();

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		builder.setConfiguration(loggerContext.getConfiguration());

		builder.setDirect(_direct);
		builder.setFollow(_follow);
		builder.setIgnoreExceptions(ignoreExceptions());
		builder.setLayout(getLayout());
		builder.setName(getName());
		builder.setTarget(_target);
		builder.withBufferedIo(_bufferedIo);
		builder.withBufferSize(_bufferSize);
		builder.withImmediateFlush(_immediateFlush);

		Appender appender = builder.build();

		if (appender != null) {
			appender.start();

			return appender;
		}

		return NullAppender.createAppender(getName());
	}

	private Appender _appender;
	private final boolean _bufferedIo;
	private final int _bufferSize;
	private final boolean _direct;
	private final boolean _follow;
	private final boolean _immediateFlush;
	private final ConsoleAppender.Target _target;

}
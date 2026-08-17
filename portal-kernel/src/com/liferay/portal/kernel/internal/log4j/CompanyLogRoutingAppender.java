/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.NullAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.PatternProcessor;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Constants;

/**
 * @author Hai Yu
 */
@Plugin(
	category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE,
	name = CompanyLogRoutingAppender.PLUGIN_NAME, printObject = true
)
public final class CompanyLogRoutingAppender extends AbstractAppender {

	public static final String PLUGIN_NAME = "CompanyLogRouting";

	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public void append(LogEvent logEvent) {
		if (!_COMPANY_LOG_ENABLED) {
			return;
		}

		List<Appender> appenders = _appenders.computeIfAbsent(
			CompanyThreadLocal.getCompanyId(), this::_createAppenders);

		appenders.forEach(appender -> appender.append(logEvent));
	}

	public File getCompanyLogDirectory(long companyId) {
		return new File(
			StringUtil.replace(
				_dirPattern, "@company.id@", String.valueOf(companyId)));
	}

	public static class Builder
		extends AbstractAppender.Builder<Builder>
		implements org.apache.logging.log4j.core.util.Builder
			<CompanyLogRoutingAppender> {

		@Override
		public CompanyLogRoutingAppender build() {
			return new CompanyLogRoutingAppender(
				_advertise, _advertiseUri, _append, _bufferedIo, _bufferSize,
				_companyLogRoutingFilePatterns, _createOnDemand, _dirPattern,
				_fileGroup, _fileName, _fileOwner, _filePermissions,
				getFilter(), _immediateFlush, _locking, getName(),
				_rolloverStrategy, _triggeringPolicy);
		}

		@PluginBuilderAttribute("advertise")
		private boolean _advertise;

		@PluginBuilderAttribute("advertiseUri")
		private String _advertiseUri;

		@PluginBuilderAttribute("append")
		private boolean _append = true;

		@PluginBuilderAttribute("bufferedIo")
		private boolean _bufferedIo = true;

		@PluginBuilderAttribute("bufferSize")
		private int _bufferSize = Constants.ENCODER_BYTE_BUFFER_SIZE;

		@PluginElement("FilePattern")
		@Required
		private CompanyLogRoutingFilePattern[] _companyLogRoutingFilePatterns;

		@PluginBuilderAttribute("createOnDemand")
		private boolean _createOnDemand;

		@PluginBuilderAttribute("dirPattern")
		@Required
		private String _dirPattern;

		@PluginBuilderAttribute("fileGroup")
		private String _fileGroup;

		@PluginBuilderAttribute("fileName")
		private String _fileName;

		@PluginBuilderAttribute("fileOwner")
		private String _fileOwner;

		@PluginBuilderAttribute("filePermissions")
		private String _filePermissions;

		@PluginBuilderAttribute("immediateFlush")
		private boolean _immediateFlush = true;

		@PluginBuilderAttribute("locking")
		private boolean _locking;

		@PluginElement("Strategy")
		private RolloverStrategy _rolloverStrategy;

		@PluginElement("Policy")
		@Required
		private TriggeringPolicy _triggeringPolicy;

	}

	private CompanyLogRoutingAppender(
		boolean advertise, String advertiseUri, boolean append,
		boolean bufferedIo, int bufferSize,
		CompanyLogRoutingFilePattern[] companyLogRoutingFilePatterns,
		boolean createOnDemand, String dirPattern, String fileGroup,
		String fileName, String fileOwner, String filePermissions,
		Filter filter, boolean immediateFlush, boolean locking, String name,
		RolloverStrategy rolloverStrategy, TriggeringPolicy triggeringPolicy) {

		super(name, filter, null, true, null);

		_advertise = advertise;
		_advertiseUri = advertiseUri;
		_append = append;
		_bufferedIo = bufferedIo;
		_bufferSize = bufferSize;
		_companyLogRoutingFilePatterns = companyLogRoutingFilePatterns;
		_createOnDemand = createOnDemand;
		_dirPattern = dirPattern;
		_fileGroup = fileGroup;
		_fileName = fileName;
		_fileOwner = fileOwner;
		_filePermissions = filePermissions;
		_immediateFlush = immediateFlush;
		_locking = locking;
		_rolloverStrategy = rolloverStrategy;
		_triggeringPolicy = triggeringPolicy;
	}

	private Appender _createAppender(
		Layout<?> layout, String filePattern, String name) {

		RollingFileAppender.Builder builder = RollingFileAppender.newBuilder();

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext();

		builder.setConfiguration(loggerContext.getConfiguration());

		builder.setIgnoreExceptions(ignoreExceptions());
		builder.setLayout(layout);
		builder.setName(name);
		builder.withAdvertise(_advertise);
		builder.withAdvertiseUri(_advertiseUri);
		builder.withAppend(_append);
		builder.withBufferedIo(_bufferedIo);
		builder.withBufferSize(_bufferSize);
		builder.withCreateOnDemand(_createOnDemand);
		builder.withFileGroup(_fileGroup);
		builder.withFileName(_fileName);
		builder.withFileOwner(_fileOwner);
		builder.withFilePattern(filePattern);
		builder.withFilePermissions(_filePermissions);
		builder.withImmediateFlush(_immediateFlush);
		builder.withLocking(_locking);
		builder.withPolicy(_triggeringPolicy);

		if (_rolloverStrategy instanceof DirectWriteRolloverStrategy) {
			DirectWriteRolloverStrategy directWriteRolloverStrategy =
				(DirectWriteRolloverStrategy)_rolloverStrategy;

			DirectWriteRolloverStrategy.Builder
				directWriteRolloverStrategyBuilder =
					DirectWriteRolloverStrategy.newBuilder();

			directWriteRolloverStrategyBuilder.withCompressionLevelStr(
				String.valueOf(
					directWriteRolloverStrategy.getCompressionLevel()));
			directWriteRolloverStrategyBuilder.withConfig(
				loggerContext.getConfiguration());

			List<Action> customActions =
				directWriteRolloverStrategy.getCustomActions();

			directWriteRolloverStrategyBuilder.withCustomActions(
				customActions.toArray(new Action[0]));

			directWriteRolloverStrategyBuilder.withMaxFiles(
				String.valueOf(directWriteRolloverStrategy.getMaxFiles()));
			directWriteRolloverStrategyBuilder.withStopCustomActionsOnError(
				directWriteRolloverStrategy.isStopCustomActionsOnError());

			PatternProcessor patternProcessor =
				directWriteRolloverStrategy.getTempCompressedFilePattern();

			if (patternProcessor != null) {
				directWriteRolloverStrategyBuilder.
					withTempCompressedFilePattern(
						patternProcessor.getPattern());
			}

			builder.withStrategy(directWriteRolloverStrategyBuilder.build());
		}
		else {
			builder.withStrategy(_rolloverStrategy);
		}

		Appender appender = builder.build();

		if (appender != null) {
			appender.start();

			return appender;
		}

		return NullAppender.createAppender(name);
	}

	private List<Appender> _createAppenders(long companyId) {
		List<Appender> appenders = new ArrayList<>();

		String appenderName = StringBundler.concat(
			companyId, StringPool.DASH, getName(), StringPool.DASH);

		for (CompanyLogRoutingFilePattern companyLogRoutingFilePattern :
				_companyLogRoutingFilePatterns) {

			Layout<?> layout = companyLogRoutingFilePattern.getLayout();

			Class<?> layoutClass = layout.getClass();

			appenders.add(
				_createAppender(
					layout,
					StringUtil.replace(
						_dirPattern + StringPool.FORWARD_SLASH +
							companyLogRoutingFilePattern.getFileNamePattern(),
						"@company.id@", String.valueOf(companyId)),
					appenderName.concat(layoutClass.getSimpleName())));
		}

		return appenders;
	}

	private static final boolean _COMPANY_LOG_ENABLED = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.COMPANY_LOG_ENABLED));

	private final boolean _advertise;
	private final String _advertiseUri;
	private final boolean _append;
	private final Map<Long, List<Appender>> _appenders =
		new ConcurrentHashMap<>();
	private final boolean _bufferedIo;
	private final int _bufferSize;
	private final CompanyLogRoutingFilePattern[] _companyLogRoutingFilePatterns;
	private final boolean _createOnDemand;
	private final String _dirPattern;
	private final String _fileGroup;
	private final String _fileName;
	private final String _fileOwner;
	private final String _filePermissions;
	private final boolean _immediateFlush;
	private final boolean _locking;
	private final RolloverStrategy _rolloverStrategy;
	private final TriggeringPolicy _triggeringPolicy;

}
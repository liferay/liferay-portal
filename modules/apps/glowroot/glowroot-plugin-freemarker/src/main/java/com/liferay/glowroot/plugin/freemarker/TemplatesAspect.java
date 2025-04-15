/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.plugin.freemarker;

import java.util.Map;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.MessageSupplier;
import org.glowroot.agent.plugin.api.OptionalThreadContext;
import org.glowroot.agent.plugin.api.TimerName;
import org.glowroot.agent.plugin.api.TraceEntry;
import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindParameterArray;
import org.glowroot.agent.plugin.api.weaving.BindThrowable;
import org.glowroot.agent.plugin.api.weaving.BindTraveler;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.OnReturn;
import org.glowroot.agent.plugin.api.weaving.OnThrow;
import org.glowroot.agent.plugin.api.weaving.Pointcut;
import org.glowroot.agent.plugin.api.weaving.Shim;

/**
 * @author Fabian Bouché
 */
public class TemplatesAspect {

	@Pointcut(
		className = "com.liferay.fragment.entry.processor.freemarker.FreeMarkerFragmentEntryProcessor",
		methodName = "processFragmentEntryLinkHTML",
		methodParameterTypes = {
			"com.liferay.fragment.model.FragmentEntryLink", "java.lang.String",
			"com.liferay.fragment.processor.FragmentEntryProcessorContext"
		},
		timerName = "Fragment Entry Link FreeMarker Template"
	)
	public static class FreeMarkerFragmentEntryProcessorAdvice {

		@OnBefore
		public static TraceEntry onBefore(
			OptionalThreadContext optionalThreadContext,
			@BindParameter FragmentEntryLinkShim fragmentEntryLinkShim,
			@BindParameter String html,
			@BindParameter FragmentEntryProcessorContextShim
				fragmentEntryProcessorContextShim) {

			TraceEntry traceEntry = null;

			StringBuilder sb = new StringBuilder();

			sb.append("Fragment Entry Link FreeMarker Template (Company ID ");
			sb.append(fragmentEntryLinkShim.getCompanyId());
			sb.append(", Fragment Entry Link ID ");
			sb.append(fragmentEntryLinkShim.getFragmentEntryLinkId());
			sb.append(", and Group ID ");
			sb.append(fragmentEntryLinkShim.getGroupId());
			sb.append(")");

			if (_INSTRUMENTATION_LEVEL_TRACE.equals(
					TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();

				optionalThreadContext.addTransactionAttribute("HTML", html);
			}
			else if (_INSTRUMENTATION_LEVEL_DEBUG.equals(
						TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();
			}
			else {
				traceEntry = optionalThreadContext.startTraceEntry(
					MessageSupplier.create(sb.toString()), _timerName);
			}

			return traceEntry;
		}

		@OnReturn
		public static void onReturn(@BindTraveler TraceEntry traceEntry) {
			traceEntry.end();
		}

		@OnThrow
		public static void onThrow(
			@BindThrowable Throwable throwable,
			@BindTraveler TraceEntry traceEntry) {

			traceEntry.endWithError(throwable);
		}

		private static final TimerName _timerName = Agent.getTimerName(
			FreeMarkerFragmentEntryProcessorAdvice.class);

	}

	@Pointcut(
		className = "com.liferay.journal.internal.transformer.JournalTransformer",
		methodName = "transform",
		methodParameterTypes = {
			"com.liferay.journal.model.JournalArticle",
			"com.liferay.dynamic.data.mapping.model.DDMTemplate",
			"com.liferay.journal.util.JournalHelper", "java.lang.String",
			"com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry",
			"java.util.List",
			"com.liferay.portal.kernel.portlet.PortletRequestModel", "boolean",
			"java.lang.String", "com.liferay.portal.kernel.theme.ThemeDisplay",
			"java.lang.String"
		},
		timerName = "Journal Article FreeMarker Template"
	)
	public static class JournalTransformerAdvice {

		@OnBefore
		public static TraceEntry onBefore(
			OptionalThreadContext optionalThreadContext,
			@BindParameterArray Object[] parameters) {

			TraceEntry traceEntry = null;

			StringBuilder sb = new StringBuilder();

			sb.append("Journal Article FreeMarker Template (Company ID ");

			ThemeDisplayShim themeDisplayShim = (ThemeDisplayShim)parameters[9];

			sb.append(themeDisplayShim.getCompanyId());

			sb.append(", Site Group ID ");
			sb.append(themeDisplayShim.getSiteGroupId());
			sb.append(", and Template ID ");

			DDMTemplateShim dDMTemplateShim = (DDMTemplateShim)parameters[1];

			sb.append(dDMTemplateShim.getTemplateId());

			sb.append(")");

			if (_INSTRUMENTATION_LEVEL_TRACE.equals(
					TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();

				optionalThreadContext.addTransactionAttribute(
					"Script", dDMTemplateShim.getScript());
			}
			else if (_INSTRUMENTATION_LEVEL_DEBUG.equals(
						TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();
			}
			else {
				traceEntry = optionalThreadContext.startTraceEntry(
					MessageSupplier.create(sb.toString()), _timerName);
			}

			return traceEntry;
		}

		@OnReturn
		public static void onReturn(@BindTraveler TraceEntry traceEntry) {
			traceEntry.end();
		}

		@OnThrow
		public static void onThrow(
			@BindThrowable Throwable throwable,
			@BindTraveler TraceEntry traceEntry) {

			traceEntry.endWithError(throwable);
		}

		private static final TimerName _timerName = Agent.getTimerName(
			JournalTransformerAdvice.class);

	}

	@Pointcut(
		className = "com.liferay.portal.templateparser.Transformer",
		methodName = "transform",
		methodParameterTypes = {
			"com.liferay.portal.kernel.theme.ThemeDisplay", "java.util.Map",
			"java.lang.String", "java.lang.String",
			"com.liferay.portal.kernel.io.unsync.UnsyncStringWriter",
			"jakarta.servlet.http.HttpServletRequest",
			"jakarta.servlet.http.HttpServletResponse"
		},
		timerName = "Transformer FreeMarker Template"
	)
	public static class TransformerAdvice {

		@OnBefore
		public static TraceEntry onBefore(
			OptionalThreadContext optionalThreadContext,
			@BindParameter ThemeDisplayShim themeDisplayShim,
			@BindParameter Map<String, Object> contextObjects,
			@BindParameter String script, @BindParameter String type) {

			TraceEntry traceEntry = null;

			StringBuilder sb = new StringBuilder();

			sb.append("Transformer FreeMarker Template (Company ID ");
			sb.append(themeDisplayShim.getCompanyId());
			sb.append(", Site Group ID ");
			sb.append(themeDisplayShim.getSiteGroupId());
			sb.append(", and Template ID ");
			sb.append(contextObjects.get("template_id"));
			sb.append(")");

			if (_INSTRUMENTATION_LEVEL_TRACE.equals(
					TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();

				optionalThreadContext.addTransactionAttribute("Script", script);
				optionalThreadContext.addTransactionAttribute("Type", type);
			}
			else if (_INSTRUMENTATION_LEVEL_DEBUG.equals(
						TemplatesPluginProperties.instrumentationLevel())) {

				traceEntry = optionalThreadContext.startTransaction(
					"FreeMarker Templates", sb.toString(),
					MessageSupplier.create(sb.toString()), _timerName);

				optionalThreadContext.setTransactionOuter();
			}
			else {
				traceEntry = optionalThreadContext.startTraceEntry(
					MessageSupplier.create(sb.toString()), _timerName);
			}

			return traceEntry;
		}

		@OnReturn
		public static void onReturn(@BindTraveler TraceEntry traceEntry) {
			traceEntry.end();
		}

		@OnThrow
		public static void onThrow(
			@BindThrowable Throwable throwable,
			@BindTraveler TraceEntry traceEntry) {

			traceEntry.endWithError(throwable);
		}

		private static final TimerName _timerName = Agent.getTimerName(
			TransformerAdvice.class);

	}

	@Shim("com.liferay.dynamic.data.mapping.model.DDMTemplate")
	public interface DDMTemplateShim {

		public String getScript();

		public long getTemplateId();

	}

	@Shim("com.liferay.fragment.model.FragmentEntryLink")
	public interface FragmentEntryLinkShim {

		public long getCompanyId();

		public long getFragmentEntryLinkId();

		public long getGroupId();

	}

	@Shim("com.liferay.fragment.processor.FragmentEntryProcessorContext")
	public interface FragmentEntryProcessorContextShim {
	}

	@Shim("com.liferay.journal.model.JournalArticle")
	public interface JournalArticleShim {
	}

	@Shim("com.liferay.portal.kernel.theme.ThemeDisplay")
	public interface ThemeDisplayShim {

		public long getCompanyGroupId();

		public long getCompanyId();

		public long getScopeGroupId();

		public long getSiteGroupId();

	}

	private static final String _INSTRUMENTATION_LEVEL_DEBUG = "DEBUG";

	private static final String _INSTRUMENTATION_LEVEL_TRACE = "TRACE";

}